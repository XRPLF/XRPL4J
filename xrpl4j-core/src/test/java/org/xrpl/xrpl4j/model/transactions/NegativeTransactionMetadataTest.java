package org.xrpl.xrpl4j.model.transactions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.model.client.ledger.LedgerResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.jackson.ObjectMapperFactory;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaAccountRootObject;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaLedgerEntryType;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaLedgerObject;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaNfTokenOfferObject;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaOfferObject;
import org.xrpl.xrpl4j.model.transactions.metadata.MetaRippleStateObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * An IT that loads data from various files containing XRPL mainnet ledger data, and verifies that all data can be
 * deserialized, in particular negative XRP and IOU amounts.
 *
 * @see "https://github.com/XRPLF/xrpl4j/issues/527"
 */
class NegativeTransactionMetadataTest {

  Logger logger = LoggerFactory.getLogger(NegativeTransactionMetadataTest.class);

  ObjectMapper objectMapper = ObjectMapperFactory.create();

  /**
   * This test validates that the ledger specified in Github issue #527 actually deserializes properly, despite having
   * negative XRP values.
   *
   * @see "https://github.com/XRPLF/xrpl4j/issues/527"
   */
  @Test
  void deserializeLedger354575() throws IOException {

    File jsonFile = new File(
      "src/test/resources/negative-balance-ledgers/ledger-354575-metadata.json"
    );

    TransactionMetadata metaData = objectMapper.readValue(jsonFile, TransactionMetadata.class);

    // Send this through the general test mechanism....
    this.handleTransactionMetadata(metaData);

    // Also check that the decimal deserializes properly.
    metaData.affectedNodes().get(0).handle(
      (createdNode) -> {
        throw new RuntimeException("Should not be called");
      },
      (modifiedNode) -> {
        assertThat(modifiedNode.previousFields().isPresent()).isTrue();
        modifiedNode.previousFields()
          .map($ -> (MetaAccountRootObject) $)
          .ifPresent(metaAccountRootObject -> {
            assertThat(metaAccountRootObject.balance().isPresent()).isTrue();
            metaAccountRootObject.balance().ifPresent(balance -> {
              assertThat(balance.isNegative()).isTrue();
              assertThat(balance.toXrp()).isEqualTo(new BigDecimal("-2298.00005"));
            });
          });
      },
      (deletedNode) -> {
        throw new RuntimeException("Should not be called");
      }
    );
  }

  /**
   * This test validates that the ledger 87704323 and all of its transactions and metadata are handled correctly, even
   * in the presence of negative XRP or IOU amounts.
   */
  @ParameterizedTest
  @ValueSource(strings = {
    "ledger-result-309936.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/473
    "ledger-result-309937.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/473
    "ledger-result-329212.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-340231.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-346610.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-350621.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-353674.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-354496.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-354575.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-354579.json", // <-- See https://github.com/XRPLF/xrpl4j/issues/474
    "ledger-result-87704323.json",
    "ledger-result-90150378.json",
    "ledger-result-90156059.json"
  })
  void deserializeLedgerResultWithNegativeAmounts(String ledgerResultFileName) throws IOException {
    Objects.requireNonNull(ledgerResultFileName);

    File jsonFile = new File(
      "src/test/resources/negative-balance-ledgers/" + ledgerResultFileName
    );

    LedgerResult ledgerResult = objectMapper.readValue(jsonFile, LedgerResult.class);

    ledgerResult.ledger().transactions().forEach(transactionResult -> {
      assertThat(transactionResult.metadata().isPresent()).isTrue();
      transactionResult.metadata().ifPresent(this::handleTransactionMetadata);
    });
  }

  /**
   * This test validates that the ledger 94084608 and all of its transactions and metadata are handled correctly, even
   * in the presence of a `UnlModify` transaction that has an empty `Account`.
   */
  @ParameterizedTest
  @ValueSource(strings = {
    "ledger-result-94084608.json" // <-- See https://github.com/XRPLF/xrpl4j/issues/590
  })
  void deserializeLedgerResultWithSpecialObjects(String ledgerResultFileName) throws IOException {
    Objects.requireNonNull(ledgerResultFileName);

    File jsonFile = new File(
      "src/test/resources/special-object-ledgers/" + ledgerResultFileName
    );

    LedgerResult ledgerResult = objectMapper.readValue(jsonFile, LedgerResult.class);

    ledgerResult.ledger().transactions().forEach(transactionResult -> {
      assertThat(transactionResult.metadata().isPresent()).isTrue();
      transactionResult.metadata().ifPresent(this::handleTransactionMetadata);
    });
  }

  /**
   * This test validates that the ledger 87704323 and all of its transactions and metadata are handled correctly, even
   * in the presence of negative XRP or IOU amounts.
   */
  @Test
  void deserializeOfferCreateWithNegativeValues() throws IOException {
    File jsonFile = new File(
      "src/test/resources/negative-balance-ledgers/txresult-offercreate-withnegatives.json"
    );

    TransactionResult<OfferCreate> transactionResult = objectMapper.readValue(
      jsonFile, objectMapper.getTypeFactory().constructParametricType(TransactionResult.class, OfferCreate.class)
    );

    assertThat(transactionResult.metadata().isPresent()).isTrue();
    transactionResult.metadata().ifPresent(this::handleTransactionMetadata);
  }

  private void handleTransactionMetadata(final TransactionMetadata transactionMetadata) {
    Objects.requireNonNull(transactionMetadata);

    transactionMetadata.affectedNodes().forEach(affectedNode -> {
      affectedNode.handle(
        (createdNode) -> {
          final MetaLedgerEntryType ledgerEntryType = createdNode.ledgerEntryType();
          if (ledgerEntryType.equals(MetaLedgerEntryType.OFFER)) {
            handleMetaLedgerObject((MetaOfferObject) createdNode.newFields());
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.ACCOUNT_ROOT)) {
            handleMetaLedgerObject((MetaAccountRootObject) createdNode.newFields());
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.RIPPLE_STATE)) {
            handleMetaLedgerObject((MetaRippleStateObject) createdNode.newFields());
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.DIRECTORY_NODE)) {
            logger.warn("Ignoring CreatedNode ledger entry type {}", ledgerEntryType);
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.NEGATIVE_UNL)) {
            logger.warn(
              "Ignoring DeletedNode ledger entry type {}. See https://github.com/XRPLF/xrpl4j/issues/16",
              ledgerEntryType);
          } else {
            throw new RuntimeException("Unhandled CreatedNode ledger entry type: " + ledgerEntryType);
          }
        },
        (modifiedNode) -> {
          modifiedNode.previousFields()
            .ifPresent(metaLedgerObject -> {
              final MetaLedgerEntryType ledgerEntryType = modifiedNode.ledgerEntryType();
              if (ledgerEntryType.equals(MetaLedgerEntryType.OFFER)) {
                handleMetaLedgerObject((MetaOfferObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.ACCOUNT_ROOT)) {
                handleMetaLedgerObject((MetaAccountRootObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.RIPPLE_STATE)) {
                handleMetaLedgerObject((MetaRippleStateObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.DIRECTORY_NODE)) {
                logger.warn("Ignoring ModifiedNode ledger entry type {}", ledgerEntryType);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.NEGATIVE_UNL)) {
                logger.warn(
                  "Ignoring DeletedNode ledger entry type {}. See https://github.com/XRPLF/xrpl4j/issues/16",
                  ledgerEntryType);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.AMM)) {
                logger.warn(
                  "Ignoring DeletedNode ledger entry type {}. See https://github.com/XRPLF/xrpl4j/issues/591",
                  ledgerEntryType);
              } else {
                throw new RuntimeException(
                  "Unhandled ModifiedNode PreviousFields ledger entry type: " + ledgerEntryType);
              }
            });

          modifiedNode.finalFields()
            .ifPresent(metaLedgerObject -> {
              final MetaLedgerEntryType ledgerEntryType = modifiedNode.ledgerEntryType();
              if (ledgerEntryType.equals(MetaLedgerEntryType.OFFER)) {
                handleMetaLedgerObject((MetaOfferObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.ACCOUNT_ROOT)) {
                handleMetaLedgerObject((MetaAccountRootObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.RIPPLE_STATE)) {
                handleMetaLedgerObject((MetaRippleStateObject) metaLedgerObject);
              } else if (
                ledgerEntryType.equals(MetaLedgerEntryType.DIRECTORY_NODE)) {
                logger.warn("Ignoring ModifiedNode ledger entry type {}", ledgerEntryType);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.AMM)) {
                logger.warn(
                  "Ignoring ModifiedNode ledger entry type {}. See See https://github.com/XRPLF/xrpl4j/issues/591",
                  ledgerEntryType);
              } else {
                throw new RuntimeException("Unhandled ModifiedNode FinalFields ledger entry type: " + ledgerEntryType);
              }
            });
        },
        (deletedNode) -> {
          deletedNode.previousFields()
            .ifPresent(metaLedgerObject -> {
              final MetaLedgerEntryType ledgerEntryType = deletedNode.ledgerEntryType();
              if (ledgerEntryType.equals(MetaLedgerEntryType.OFFER)) {
                handleMetaLedgerObject((MetaOfferObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.ACCOUNT_ROOT)) {
                handleMetaLedgerObject((MetaAccountRootObject) metaLedgerObject);
              } else if (ledgerEntryType.equals(MetaLedgerEntryType.RIPPLE_STATE)) {
                handleMetaLedgerObject((MetaRippleStateObject) metaLedgerObject);
              } else {
                throw new RuntimeException(
                  "Unhandled DeletedNode PreviousFields ledger entry type: " + ledgerEntryType);
              }
            });

          final MetaLedgerObject finalFields = deletedNode.finalFields();
          final MetaLedgerEntryType ledgerEntryType = deletedNode.ledgerEntryType();
          if (ledgerEntryType.equals(MetaLedgerEntryType.OFFER)) {
            handleMetaLedgerObject((MetaOfferObject) finalFields);
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.ACCOUNT_ROOT)) {
            handleMetaLedgerObject((MetaAccountRootObject) finalFields);
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.RIPPLE_STATE)) {
            handleMetaLedgerObject((MetaRippleStateObject) finalFields);
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.TICKET)) {
            logger.info(
              "Ignoring ledger entry type {} because it has no currency values for negative checking", ledgerEntryType
            );
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.DIRECTORY_NODE)) {
            logger.warn("Ignoring DeletedNode ledger entry type {}", ledgerEntryType);
          } else if (ledgerEntryType.equals(MetaLedgerEntryType.NFTOKEN_OFFER)) {
            handleMetaLedgerObject((MetaNfTokenOfferObject) finalFields);
          } else {
            throw new RuntimeException("Unhandled DeletedNode FinalFields ledger entry type: " + ledgerEntryType);
          }
        }
      );
    });
  }

  private void handleMetaLedgerObject(MetaRippleStateObject metaRippleStateObject) {
    // Sometimes, there is no balance in a MetaRippleStateObject, so we don't assert on one being present.
    metaRippleStateObject.balance().ifPresent(balance -> {
      if (balance.value().startsWith("-")) {
        assertThat(balance.isNegative()).isTrue();
      } else {
        assertThat(balance.isNegative()).isFalse();
      }
    });

    metaRippleStateObject.lowLimit().ifPresent(lowLimit -> {
      if (lowLimit.value().startsWith("-")) {
        assertThat(lowLimit.isNegative()).isTrue();
      } else {
        assertThat(lowLimit.isNegative()).isFalse();
      }
    });

    metaRippleStateObject.highLimit().ifPresent(highLimit -> {
      if (highLimit.value().startsWith("-")) {
        assertThat(highLimit.isNegative()).isTrue();
      } else {
        assertThat(highLimit.isNegative()).isFalse();
      }
    });
  }

  private void handleMetaLedgerObject(MetaAccountRootObject metaAccountRootObject) {
    // Sometimes, there is no balance in a MetaRippleStateObject, so we don't assert on one being present.

    metaAccountRootObject.balance().ifPresent(balance -> {
      if (balance.toXrp().signum() < 0) {
        assertThat(balance.isNegative()).isTrue();
      } else {
        assertThat(balance.isNegative()).isFalse();
      }
    });
  }

  private void handleMetaLedgerObject(MetaOfferObject metaOfferObject) {
    assertThat(metaOfferObject.takerPays().isPresent()).isTrue();
    assertThat(metaOfferObject.takerGets().isPresent()).isTrue();

    metaOfferObject.takerPays().ifPresent(takerPays -> {
      takerPays.handle(
        xrpCurrencyAmount -> {
          if (xrpCurrencyAmount.toXrp().signum() < 0) {
            assertThat(xrpCurrencyAmount.isNegative()).isTrue();
          } else {
            assertThat(xrpCurrencyAmount.isNegative()).isFalse();
          }
        },
        issuedCurrencyAmount -> {
          if (issuedCurrencyAmount.value().startsWith("-")) {
            assertThat(issuedCurrencyAmount.isNegative()).isTrue();
          } else {
            assertThat(issuedCurrencyAmount.isNegative()).isFalse();
          }
        }
      );
    });
  }

  private void handleMetaLedgerObject(MetaNfTokenOfferObject metaNfTokenOfferObject) {
    assertThat(metaNfTokenOfferObject.amount().isPresent()).isTrue();

    metaNfTokenOfferObject.amount()
      .ifPresent($ -> $.handle(
        xrpCurrencyAmount -> assertThat(xrpCurrencyAmount.isNegative()).isEqualTo(
          xrpCurrencyAmount.toXrp().signum() < 0),
        issuedCurrencyAmount -> assertThat(issuedCurrencyAmount.isNegative()).isEqualTo(
          issuedCurrencyAmount.value().startsWith("-"))
      ));
  }
}
