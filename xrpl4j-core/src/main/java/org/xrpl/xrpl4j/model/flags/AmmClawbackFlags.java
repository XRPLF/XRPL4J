package org.xrpl.xrpl4j.model.flags;

/*-
 * ========================LICENSE_START=================================
 * xrpl4j :: core
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

import org.xrpl.xrpl4j.model.transactions.AmmClawback;

/**
 * {@link TransactionFlags} for {@link AmmClawback} transactions.
 */
public class AmmClawbackFlags extends TransactionFlags {
  /**
   * Constant {@link AmmDepositFlags} for the {@code tfClawTwoAssets} flag.
   */
  public static final AmmClawbackFlags CLAW_TWO_ASSETS = new AmmClawbackFlags(0x00000001);

  /**
   * Constant {@link AmmDepositFlags} for an unset value for "flags".
   */
  public static final AmmClawbackFlags UNSET = new AmmClawbackFlags(0L);

  private AmmClawbackFlags(long value) {
    super(value);
  }

  private AmmClawbackFlags() {
  }

  /**
   * Construct an empty instance of {@link AmmClawbackFlags}. Transactions with empty flags will
   * not be serialized with a {@code Flags} field.
   *
   * @return An empty {@link AmmClawbackFlags}.
   */
  public static AmmClawbackFlags empty() {
    return new AmmClawbackFlags();
  }

  /**
   * Whether the {@code tfClawTwoAssets} flag is set.
   *
   * @return {@code true} if {@code tfLPToken} is set, otherwise {@code false}.
   */
  public boolean tfClawTwoAssets() {
    return this.isSet(CLAW_TWO_ASSETS);
  }
}
