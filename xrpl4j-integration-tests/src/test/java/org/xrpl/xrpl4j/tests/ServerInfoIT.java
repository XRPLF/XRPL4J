package org.xrpl.xrpl4j.tests;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: integration-tests
 * %%
 * Copyright (C) 2020 - 2023 XRPL Foundation and its contributors
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

import okhttp3.HttpUrl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.model.client.serverinfo.ServerInfo;

import java.util.concurrent.TimeUnit;

/**
 * Integration test for different types of ServerInfo values.
 */
public class ServerInfoIT {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * This test was written to run a long-running test on different Server Info models. Server Info is an implementation
   * detail and different servers could respond differently. This test essentially verifies the correctness of these
   * models. In the future, if the response structure changes, this test will alarm us to take action but has to be run
   * manually everytime since it is disabled for CI.
   *
   * @throws JsonRpcClientErrorException If {@code jsonRpcClient} throws an error.
   * @throws InterruptedException        If {@link Thread} is interrupted.
   */
  @Timeout(value = 3, unit = TimeUnit.HOURS)
  @Test
  @Disabled
  public void testServerInfoAcrossAllTypes() throws JsonRpcClientErrorException, InterruptedException {
    XrplClient rippledClient = getXrplClient(HttpUrl.parse("https://s1.cbdc-sandbox.rippletest.net:51234"));
    XrplClient reportingClient = getXrplClient(HttpUrl.parse("https://s2-reporting.ripple.com:51234"));
    XrplClient clioClient = getXrplClient(HttpUrl.parse("https://s2-clio.ripple.com:51234"));

    ServerInfo info;

    while (true) {
      info = rippledClient.serverInformation().info();
      logger.info("Rippled info was mapped correctly. " + getType(info));
      info = reportingClient.serverInformation().info();
      logger.info("Reporting mode info was mapped correctly. " + getType(info));
      info = clioClient.serverInformation().info();
      logger.info("Clio info was mapped correctly. " + getType(info));
      Thread.sleep(2000);
    }
  }

  private String getType(ServerInfo info) {
    return info.map(
      rippled -> "rippled",
      clio -> "clio",
      reporting -> "reporting"
    );
  }

  private XrplClient getXrplClient(HttpUrl serverUrl) {
    return new XrplClient(serverUrl);
  }
}
