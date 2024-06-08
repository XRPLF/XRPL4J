package org.xrpl.xrpl4j.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.Finality;
import org.xrpl.xrpl4j.model.client.FinalityStatus;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.ledger.OracleLedgerEntryParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.ledger.OracleObject;
import org.xrpl.xrpl4j.model.transactions.AssetPrice;
import org.xrpl.xrpl4j.model.transactions.OracleDelete;
import org.xrpl.xrpl4j.model.transactions.OracleDocumentId;
import org.xrpl.xrpl4j.model.transactions.OracleProvider;
import org.xrpl.xrpl4j.model.transactions.OracleSet;
import org.xrpl.xrpl4j.model.transactions.OracleUri;
import org.xrpl.xrpl4j.model.transactions.PriceData;
import org.xrpl.xrpl4j.model.transactions.PriceDataWrapper;

import java.time.Instant;
import java.util.Optional;

public class PriceOracleIT extends AbstractIT {

  String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');

  @Test
  void createAndUpdateAndDeleteOracle() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair sourceKeyPair = createRandomAccountEd25519();

    AccountInfoResult accountInfo = this.scanForResult(
      () -> this.getValidatedAccountInfo(sourceKeyPair.publicKey().deriveAddress())
    );

    FeeResult feeResult = xrplClient.fee();
    OracleProvider provider = OracleProvider.of(BaseEncoding.base16().encode("DIA".getBytes()));
    OracleUri uri = OracleUri.of(BaseEncoding.base16().encode("http://example.com".getBytes()));
    UnsignedInteger lastUpdateTime = unixTimestamp();
    String assetClass = BaseEncoding.base16().encode("currency".getBytes());
    PriceDataWrapper priceData1 = PriceDataWrapper.of(
      PriceData.builder()
        .baseAsset("XRP")
        .quoteAsset(xrpl4jCoin)
        .assetPrice(AssetPrice.of(UnsignedLong.ONE))
        .scale(UnsignedInteger.valueOf(10))
        .build()
    );
    PriceDataWrapper priceData2 = PriceDataWrapper.of(
      PriceData.builder()
        .baseAsset("XRP")
        .quoteAsset("EUR")
        .assetPrice(AssetPrice.of(UnsignedLong.ONE))
        .scale(UnsignedInteger.valueOf(10))
        .build()
    );
    OracleSet oracleSet = OracleSet.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
      .sequence(accountInfo.accountData().sequence())
      .signingPublicKey(sourceKeyPair.publicKey())
      .lastLedgerSequence(accountInfo.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue())
      .oracleDocumentId(OracleDocumentId.of(UnsignedInteger.ONE))
      .provider(provider)
      .uri(uri)
      .lastUpdateTime(lastUpdateTime)
      .assetClass(assetClass)
      .addPriceDataSeries(priceData1, priceData2)
      .build();

    SingleSignedTransaction<OracleSet> signedOracleSet = signatureService.sign(sourceKeyPair.privateKey(), oracleSet);
    SubmitResult<OracleSet> oracleSetSubmitResult = xrplClient.submit(signedOracleSet);
    assertThat(oracleSetSubmitResult.engineResult()).isEqualTo("tesSUCCESS");

    Finality finality = scanForFinality(
      signedOracleSet.hash(),
      accountInfo.ledgerIndexSafe(),
      oracleSet.lastLedgerSequence().get(),
      oracleSet.sequence(),
      sourceKeyPair.publicKey().deriveAddress()
    );
    assertThat(finality.finalityStatus()).isEqualTo(FinalityStatus.VALIDATED_SUCCESS);

    LedgerEntryResult<OracleObject> ledgerEntry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.oracle(
        OracleLedgerEntryParams.builder()
          .oracleDocumentId(oracleSet.oracleDocumentId())
          .account(sourceKeyPair.publicKey().deriveAddress())
          .build(),
        LedgerSpecifier.VALIDATED
      )
    );
    OracleObject oracleObject = ledgerEntry.node();
    assertThat(oracleObject.owner()).isEqualTo(sourceKeyPair.publicKey().deriveAddress());
    assertThat(oracleObject.provider()).isEqualTo(provider);
    assertThat(oracleObject.assetClass()).isEqualTo(assetClass);
    assertThat(oracleObject.lastUpdateTime()).isEqualTo(lastUpdateTime);
    assertThat(oracleObject.uri()).isNotEmpty().get().isEqualTo(uri);
    assertThat(oracleObject.priceDataSeries()).containsExactlyInAnyOrder(priceData1, priceData2);

    UnsignedInteger lastUpdateTime2 = unixTimestamp();
    PriceDataWrapper newPriceData = PriceDataWrapper.of(
      PriceData.builder()
        .baseAsset("XRP")
        .quoteAsset("USD")
        .assetPrice(AssetPrice.of(UnsignedLong.ONE))
        .scale(UnsignedInteger.valueOf(10))
        .build()
    );
    PriceDataWrapper updatedPriceData = PriceDataWrapper.of(
      PriceData.builder().from(priceData2.priceData())
        .assetPrice(AssetPrice.of(UnsignedLong.valueOf(1000)))
        .build()
    );
    OracleSet oracleUpdate = OracleSet.builder().from(oracleSet)
      .lastLedgerSequence(ledgerEntry.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue())
      .lastUpdateTime(lastUpdateTime2)
      .sequence(oracleSet.sequence().plus(UnsignedInteger.ONE))
      .priceDataSeries(Lists.newArrayList(
        // New asset pair should get added
        newPriceData,
        // Same asset pair without assetPrice should delete
        PriceDataWrapper.of(
          PriceData.builder().from(priceData1.priceData())
            .scale(Optional.empty())
            .assetPrice(Optional.empty())
            .build()
        ),
        // Updating assetPrice should update an existing price data entry.
        updatedPriceData
      ))
      .build();

    SingleSignedTransaction<OracleSet> signedOracleUpdate = signatureService.sign(sourceKeyPair.privateKey(), oracleUpdate);
    SubmitResult<OracleSet> oracleUpdateSubmitResult = xrplClient.submit(signedOracleUpdate);
    assertThat(oracleUpdateSubmitResult.engineResult()).isEqualTo("tesSUCCESS");

    Finality updateFinality = scanForFinality(
      signedOracleUpdate.hash(),
      accountInfo.ledgerIndexSafe(),
      oracleUpdate.lastLedgerSequence().get(),
      oracleUpdate.sequence(),
      sourceKeyPair.publicKey().deriveAddress()
    );
    assertThat(updateFinality.finalityStatus()).isEqualTo(FinalityStatus.VALIDATED_SUCCESS);

    ledgerEntry = xrplClient.ledgerEntry(
      LedgerEntryRequestParams.oracle(
        OracleLedgerEntryParams.builder()
          .oracleDocumentId(oracleSet.oracleDocumentId())
          .account(sourceKeyPair.publicKey().deriveAddress())
          .build(),
        LedgerSpecifier.VALIDATED
      )
    );
    oracleObject = ledgerEntry.node();
    assertThat(oracleObject.owner()).isEqualTo(sourceKeyPair.publicKey().deriveAddress());
    assertThat(oracleObject.provider()).isEqualTo(provider);
    assertThat(oracleObject.assetClass()).isEqualTo(assetClass);
    assertThat(oracleObject.lastUpdateTime()).isEqualTo(lastUpdateTime2);
    assertThat(oracleObject.uri()).isNotEmpty().get().isEqualTo(uri);
    assertThat(oracleObject.priceDataSeries()).containsExactlyInAnyOrder(newPriceData, updatedPriceData);

    OracleDelete oracleDelete = OracleDelete.builder()
      .account(sourceKeyPair.publicKey().deriveAddress())
      .fee(oracleSet.fee())
      .sequence(oracleUpdate.sequence().plus(UnsignedInteger.ONE))
      .lastLedgerSequence(ledgerEntry.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue())
      .signingPublicKey(sourceKeyPair.publicKey())
      .oracleDocumentId(oracleSet.oracleDocumentId())
      .build();
    SingleSignedTransaction<OracleDelete> signedOracleDelete = signatureService.sign(sourceKeyPair.privateKey(), oracleDelete);
    SubmitResult<OracleDelete> oracleDeleteSubmitResult = xrplClient.submit(signedOracleDelete);
    assertThat(oracleDeleteSubmitResult.engineResult()).isEqualTo("tesSUCCESS");

    Finality deleteFinality = scanForFinality(
      signedOracleDelete.hash(),
      accountInfo.ledgerIndexSafe(),
      oracleDelete.lastLedgerSequence().get(),
      oracleDelete.sequence(),
      sourceKeyPair.publicKey().deriveAddress()
    );
    assertThat(deleteFinality.finalityStatus()).isEqualTo(FinalityStatus.VALIDATED_SUCCESS);

    xrplClient.ledgerEntry(
      LedgerEntryRequestParams.oracle(
        OracleLedgerEntryParams.builder()
          .oracleDocumentId(oracleSet.oracleDocumentId())
          .account(sourceKeyPair.publicKey().deriveAddress())
          .build(),
        LedgerSpecifier.VALIDATED
      )
    );
  }

  private static UnsignedInteger unixTimestamp() {
    return UnsignedInteger.valueOf(System.currentTimeMillis() / 1000L);
  }
}
