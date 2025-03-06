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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.transactions.Address;

import java.time.Duration;
import java.util.Optional;

/**
 * Abstraction for the XRP Ledger environment that the integration tests talk to. Provides access to resources need to
 * interact to the with the ledger.
 */
public interface XrplEnvironment {

  Logger logger = LoggerFactory.getLogger(XrplEnvironment.class);

  /**
   * Gets the XRPL environment to use.
   *
   * <p>
   * Uses local rippled container by default. Set -DuseTestnet to run against reporting mode testnet node Set
   * -DuseClioTestnet to run against Clio testnet node Set -DuseDevnet to run against devnet
   * </p>
   *
   * @return XRPL environment of the correct type.
   */
  static XrplEnvironment getNewConfiguredEnvironment() {
    // Use local rippled container by default.
    // Set -DuseTestnet to run against reporting mode testnet node
    // Set -DuseClioTestnet to run against Clio testnet node
    // Set -DuseDevnet to run against devnet
    boolean isTestnetEnabled = System.getProperty("useTestnet") != null;
    boolean isDevnetEnabled = System.getProperty("useDevnet") != null;
    boolean isClioTestnetEnabled = System.getProperty("useClioTestnet") != null;
    if (isTestnetEnabled) {
      logger.info(
        "System property 'useTestnet' detected; Using Reporting mode only Testnet node for integration testing.");
      return new ReportingTestnetEnvironment();
    } else if (isClioTestnetEnabled) {
      logger.info("System property 'useClioTestnet' detected; Using Clio Testnet node for integration testing.");
      return new ClioTestnetEnvironment();
    } else if (isDevnetEnabled) {
      logger.info("System property 'useDevnet' detected; Using Devnet for integration testing.");
      return new DevnetEnvironment();
    } else {
      logger.info("Neither 'useTestNet', 'useClioTestNet', nor 'useDevnet' System properties detected." +
        " Using local rippled for integration testing.");
      return new LocalRippledEnvironment();
    }
  }

  /**
   * Gets the configured {@link MainnetEnvironment} for integration tests that run against mainnet, such as
   * {@link org.xrpl.xrpl4j.tests.AccountTransactionsIT}.
   *
   * <p>The default environment is a Reporting Mode only environment. Tests can be run against a Clio node
   * by setting the {@code -DuseClioMainnet} system property.</p>
   *
   * @return A {@link MainnetEnvironment}.
   */
  static MainnetEnvironment getConfiguredMainnetEnvironment() {
    boolean isClioEnabled = System.getProperty("useClioMainnet") != null;
    if (isClioEnabled) {
      logger.info(
        "System property 'useClioMainnet' detected; Using Clio mainnet node for integration tests that are run " +
          "against mainnet.");
      return new ClioMainnetEnvironment();
    } else {
      logger.info(
        "System property 'useClioMainnet' was not detected; Using Reporting Mode mainnet node for integration tests " +
          "that are run against mainnet.");
      return new ReportingMainnetEnvironment();
    }
  }

  XrplClient getXrplClient();

  void fundAccount(Address classicAddress);

  /**
   * Accept the next ledger on an ad-hoc basis.
   */
  void acceptLedger();

  /**
   * Helper method to start the Ledger Acceptor on a regular interval.
   *
   * @param acceptIntervalMillis The interval, in milliseconds, between regular calls to the `ledger_accept` method.
   *                             This method is responsible for accepting new transactions into the ledger.
   *
   * @see "https://xrpl.org/docs/references/http-websocket-apis/admin-api-methods/server-control-methods/ledger_accept"
   */
  void startLedgerAcceptor(final Duration acceptIntervalMillis);

  /**
   * Stops the automated Ledger Acceptor, for example to control an integration test more finely.
   */
  void stopLedgerAcceptor();

  /**
   * An optionally present URL for this environment's explorer. Note this value is optional for environments that have
   * no explorer configured, such as a local rippled in a docker container.
   *
   * @return An optionally-present {@link String} containing the root URL to a ledger explorer that can be used to
   *   lookup transaction information in a browser.
   */
  default Optional<String> explorerUrl() {
    return Optional.empty();
  }
}
