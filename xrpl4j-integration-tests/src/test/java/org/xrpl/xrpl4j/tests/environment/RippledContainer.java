package org.xrpl.xrpl4j.tests.environment;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: integration-tests
 * %%
 * Copyright (C) 2020 - 2022 XRPL Foundation and its contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import static org.slf4j.LoggerFactory.getLogger;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.google.common.base.Preconditions;
import okhttp3.HttpUrl;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.ImagePullPolicy;
import org.testcontainers.images.PullPolicy;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplAdminClient;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.model.client.admin.AcceptLedgerResult;
import org.xrpl.xrpl4j.model.client.serverinfo.ReportingModeServerInfo;
import org.xrpl.xrpl4j.model.client.serverinfo.RippledServerInfo;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Service to start rippled inside docker using testcontainers.
 */
public class RippledContainer {

  // Seed for the Master/Root wallet in the rippled docker container.
  public static final String MASTER_WALLET_SEED = "snoPBrXtMeMyMHUVTgbuqAfg1SUTb";
  private static final Logger LOGGER = getLogger(RippledContainer.class);
  private static ScheduledExecutorService ledgerAcceptor = null;

  /**
   * Advances the ledger by one on each call.
   *
   * @see "https://xrpl.org/docs/references/http-websocket-apis/admin-api-methods/server-control-methods/ledger_accept"
   */
  private static final Consumer<RippledContainer> LEDGER_ACCEPTOR = (rippledContainer) -> {
    try {
      AcceptLedgerResult status = rippledContainer.getXrplAdminClient().acceptLedger();
      LOGGER.info("LEDGER_ACCEPTOR: Accepted ledger status: {}", status);
    } catch (RuntimeException | JsonRpcClientErrorException e) {
      LOGGER.warn("Ledger accept failed", e);
    }
  };

  private final GenericContainer<?> rippledContainer;
  private XrplAdminClient xrplAdminClient;
  private boolean started;

  /**
   * No-args constructor.
   */
  public RippledContainer() {
    try (GenericContainer<?> container = new GenericContainer<>("rippleci/rippled:2.2.0")) {
      this.rippledContainer = container.withCreateContainerCmdModifier((Consumer<CreateContainerCmd>) (cmd) ->
          cmd.withEntrypoint("/opt/ripple/bin/rippled"))
        .withCommand("-a --start --conf /config/rippled.cfg")
        .withExposedPorts(5005)
        .withImagePullPolicy(PullPolicy.alwaysPull())
        .withClasspathResourceMapping("rippled",
          "/config",
          BindMode.READ_ONLY)
        .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Application starting.*"));
    }

    ledgerAcceptor = Executors.newScheduledThreadPool(1);
  }

  /**
   * Get the {@link KeyPair} of the master account.
   *
   * @return The {@link KeyPair} of the master account.
   */
  public static KeyPair getMasterKeyPair() {
    return Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(MASTER_WALLET_SEED)).deriveKeyPair();
  }

  /**
   * Starts container with default interval (1s) for closing ledgers.
   */
  public RippledContainer start() {
    return this.start(Duration.ofMillis(10));
  }

  /**
   * Start contain with given interval for closing ledgers.
   *
   * @param acceptIntervalMillis The number of milliseconds before each accept call to close the ledger.
   *
   * @return A {@link RippledContainer}.
   */
  public RippledContainer start(final Duration acceptIntervalMillis) {
    Objects.requireNonNull(ledgerAcceptor);

    if (started) {
      throw new IllegalStateException("container already started");
    }
    started = true;
    rippledContainer.start();

    // Re-used the same client for all admin requests
    xrplAdminClient = new XrplAdminClient(this.getBaseUri());

    this.startLedgerAcceptor(acceptIntervalMillis);

    return this;
  }

  /**
   * Waits until the system time in the docker container matches the host's time.
   */
  private void waitForLedgerTimeToSync() {
    Awaitility.await()
      .pollDelay(1, TimeUnit.SECONDS)
      .atMost(10, TimeUnit.SECONDS)
      .until(() -> Duration.between(getLedgerTime().toInstant(), Instant.now()).abs().getSeconds() < 1);
  }

  private ZonedDateTime getLedgerTime() {
    try {
      return getXrplClient().serverInformation().info()
        .map(
          RippledServerInfo::time,
          clioServerInfo -> ZonedDateTime.now(),
          ReportingModeServerInfo::time
        );
    } catch (JsonRpcClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Shutdown all TestContainers.
   */
  public void shutdown() {
    assertContainerStarted();
    ledgerAcceptor.shutdownNow();
    rippledContainer.stop();
    started = false;
  }

  private void assertContainerStarted() {
    if (!started) {
      throw new IllegalStateException("container not started");
    }
  }

  /**
   * Provides an instance of an {@link XrplAdminClient} that will connect to the rippled container.
   *
   * @return A {@link XrplAdminClient}.
   */
  public XrplAdminClient getXrplAdminClient() {
    return this.xrplAdminClient;
  }

  /**
   * Provides an instance of an {@link XrplClient} that will connect to the rippled container.
   *
   * @return A {@link XrplAdminClient}.
   */
  public XrplClient getXrplClient() {
    return new XrplClient(this.getBaseUri());
  }

  private static HttpUrl getBaseUri(GenericContainer<?> rippledContainer) {
    return HttpUrl.parse("http://" + rippledContainer.getHost() + ":" + rippledContainer.getMappedPort(5005) + "/");
  }

  public HttpUrl getBaseUri() {
    assertContainerStarted();
    return getBaseUri(rippledContainer);
  }

  /**
   * Accept the next ledger on an ad-hoc basis.
   */
  public void acceptLedger() {
    assertContainerStarted(); // <-- This shouldn't work if the rippled container has not yet started (or is shutdown)
    LEDGER_ACCEPTOR.accept(this);
  }

  /**
   * Helper method to start the Ledger Acceptor on a regular interval.
   *
   * @param acceptIntervalMillis The interval, in milliseconds, between regular calls to the `ledger_accept` method.
   *                             This method is responsible for accepting new transactions into the ledger.
   *
   * @see "https://xrpl.org/docs/references/http-websocket-apis/admin-api-methods/server-control-methods/ledger_accept"
   */
  public void startLedgerAcceptor(final Duration acceptIntervalMillis) {
    Objects.requireNonNull(acceptIntervalMillis, "acceptIntervalMillis must not be null");
    Preconditions.checkArgument(acceptIntervalMillis.toMillis() > 0, "acceptIntervalMillis must be greater than 0");

    // rippled is run in standalone mode which means that ledgers won't automatically close. You have to manually
    // advance the ledger using the "ledger_accept" method on the admin API. To mimic the behavior of a networked
    // rippled, run a scheduled task to trigger the "ledger_accept" method.
    ledgerAcceptor = Executors.newScheduledThreadPool(1);
    ledgerAcceptor.scheduleAtFixedRate(() -> LEDGER_ACCEPTOR.accept(this),
      acceptIntervalMillis.toMillis(),
      acceptIntervalMillis.toMillis(),
      TimeUnit.MILLISECONDS
    );

    waitForLedgerTimeToSync();
  }

  /**
   * Stops the automated Ledger Acceptor, for example to control an integration test more finely.
   */
  @SuppressWarnings({"all"})
  public void stopLedgerAcceptor() {
    try {
      ledgerAcceptor.shutdown();
      ledgerAcceptor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException("Unable to stop ledger acceptor", e);
    }
  }
}
