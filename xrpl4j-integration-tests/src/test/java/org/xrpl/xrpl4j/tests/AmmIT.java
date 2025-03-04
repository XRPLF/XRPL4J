package org.xrpl.xrpl4j.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountLinesRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountLinesResult;
import org.xrpl.xrpl4j.model.client.accounts.TrustLine;
import org.xrpl.xrpl4j.model.client.amm.AmmInfoAuctionSlot;
import org.xrpl.xrpl4j.model.client.amm.AmmInfoAuthAccount;
import org.xrpl.xrpl4j.model.client.amm.AmmInfoRequestParams;
import org.xrpl.xrpl4j.model.client.amm.AmmInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.common.TimeUtils;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.fees.FeeUtils;
import org.xrpl.xrpl4j.model.client.ledger.AmmLedgerEntryParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryRequestParams;
import org.xrpl.xrpl4j.model.client.ledger.LedgerEntryResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.flags.AmmClawbackFlags;
import org.xrpl.xrpl4j.model.flags.AmmDepositFlags;
import org.xrpl.xrpl4j.model.flags.AmmWithdrawFlags;
import org.xrpl.xrpl4j.model.flags.TrustSetFlags;
import org.xrpl.xrpl4j.model.ledger.AmmObject;
import org.xrpl.xrpl4j.model.ledger.AuctionSlot;
import org.xrpl.xrpl4j.model.ledger.AuthAccount;
import org.xrpl.xrpl4j.model.ledger.AuthAccountWrapper;
import org.xrpl.xrpl4j.model.ledger.Issue;
import org.xrpl.xrpl4j.model.ledger.LedgerObject;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.AccountSet.AccountSetFlag;
import org.xrpl.xrpl4j.model.transactions.AmmBid;
import org.xrpl.xrpl4j.model.transactions.AmmClawback;
import org.xrpl.xrpl4j.model.transactions.AmmCreate;
import org.xrpl.xrpl4j.model.transactions.AmmDeposit;
import org.xrpl.xrpl4j.model.transactions.AmmVote;
import org.xrpl.xrpl4j.model.transactions.AmmWithdraw;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.TradingFee;
import org.xrpl.xrpl4j.model.transactions.TransactionResultCodes;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@DisabledIf(value = "shouldNotRun", disabledReason = "AmmIT only runs on local rippled node or devnet.")
public class AmmIT extends AbstractIT {

  static boolean shouldNotRun() {
    return System.getProperty("useTestnet") != null ||
        System.getProperty("useClioTestnet") != null;
  }

  String xrpl4jCoin = Strings.padEnd(BaseEncoding.base16().encode("xrpl4jCoin".getBytes()), 40, '0');
  String usd = "USD";

  @Test
  void depositAndVoteOnTradingFee() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();
    AmmInfoResult amm = createAmm(issuerKeyPair, feeResult);
    KeyPair traderKeyPair = createRandomAccountEd25519();

    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    AccountInfoResult traderAccountAfterDeposit = depositXrp(
        issuerKeyPair,
        traderKeyPair,
        traderAccount,
        amm,
        signatureService,
        feeResult
    );

    TradingFee newTradingFee = TradingFee.ofPercent(BigDecimal.valueOf(0.24));
    AmmVote ammVote = AmmVote.builder()
        .account(traderAccount.accountData().account())
        .sequence(traderAccountAfterDeposit.accountData().sequence())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .lastLedgerSequence(traderAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4000)).unsignedIntegerValue())
        .signingPublicKey(traderKeyPair.publicKey())
        .asset2(
            Issue.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .build()
        )
        .asset(Issue.XRP)
        .tradingFee(newTradingFee)
        .build();

    SingleSignedTransaction<AmmVote> signedVote = signatureService.sign(traderKeyPair.privateKey(), ammVote);

    SubmitResult<AmmVote> voteSubmitResult = xrplClient.submit(signedVote);
    assertThat(voteSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    scanForFinality(
        signedVote.hash(),
        traderAccount.ledgerIndexSafe(),
        ammVote.lastLedgerSequence().get(),
        ammVote.sequence(),
        traderKeyPair.publicKey().deriveAddress()
    );

    BigDecimal issuerLpTokenBalance = new BigDecimal(xrplClient.accountLines(
            AccountLinesRequestParams.builder()
                .account(issuerKeyPair.publicKey().deriveAddress())
                .peer(amm.amm().account())
                .ledgerSpecifier(LedgerSpecifier.CURRENT)
                .build()
        ).lines().stream()
        .filter(trustLine -> trustLine.currency().equals(amm.amm().lpToken().currency()))
        .findFirst()
        .orElseThrow(RuntimeException::new)
        .balance());

    BigDecimal traderLpTokenBalance = new BigDecimal(xrplClient.accountLines(
            AccountLinesRequestParams.builder()
                .account(traderKeyPair.publicKey().deriveAddress())
                .peer(amm.amm().account())
                .ledgerSpecifier(LedgerSpecifier.CURRENT)
                .build()
        ).lines().stream()
        .filter(trustLine -> trustLine.currency().equals(amm.amm().lpToken().currency()))
        .findFirst()
        .orElseThrow(RuntimeException::new)
        .balance());

    // Expected trading fee is the weighted average of each vote, where the weight is number of LP tokens held
    // by each voter
    TradingFee expectedTradingFee = TradingFee.ofPercent(
        issuerLpTokenBalance.multiply(amm.amm().tradingFee().bigDecimalValue()).add(
                traderLpTokenBalance.multiply(newTradingFee.bigDecimalValue())
            ).divide(issuerLpTokenBalance.add(traderLpTokenBalance), RoundingMode.HALF_UP)
            .setScale(3, RoundingMode.HALF_UP)
    );

    AmmInfoResult ammAfterVote = getAmmInfo(issuerKeyPair);
    assertThat(ammAfterVote.amm().tradingFee()).isEqualTo(expectedTradingFee);
  }

  @Test
  void depositAndBid() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();
    AmmInfoResult amm = createAmm(issuerKeyPair, feeResult);
    KeyPair traderKeyPair = createRandomAccountEd25519();
    KeyPair authAccount1 = createRandomAccountEd25519();

    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    AccountInfoResult traderAccountAfterDeposit = depositXrp(
        issuerKeyPair,
        traderKeyPair,
        traderAccount,
        amm,
        signatureService,
        feeResult
    );

    AmmBid bid = AmmBid.builder()
        .account(traderAccount.accountData().account())
        .sequence(traderAccountAfterDeposit.accountData().sequence())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .lastLedgerSequence(traderAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4000)).unsignedIntegerValue())
        .signingPublicKey(traderKeyPair.publicKey())
        .asset2(
            Issue.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .build()
        )
        .asset(Issue.XRP)
        .addAuthAccounts(
            AuthAccountWrapper.of(AuthAccount.of(authAccount1.publicKey().deriveAddress()))
        )
        .bidMin(
            IssuedCurrencyAmount.builder()
                .from(amm.amm().lpToken())
                .value("100")
                .build()
        )
        .build();

    SingleSignedTransaction<AmmBid> signedBid = signatureService.sign(traderKeyPair.privateKey(), bid);

    SubmitResult<AmmBid> voteSubmitResult = xrplClient.submit(signedBid);
    assertThat(voteSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    scanForFinality(
        signedBid.hash(),
        traderAccount.ledgerIndexSafe(),
        bid.lastLedgerSequence().get(),
        bid.sequence(),
        traderKeyPair.publicKey().deriveAddress()
    );

    AmmInfoResult ammAfterBid = getAmmInfo(issuerKeyPair);

    assertThat(ammAfterBid.amm().auctionSlot()).isNotEmpty();
    AmmInfoAuctionSlot auctionSlot = ammAfterBid.amm().auctionSlot().get();
    assertThat(auctionSlot.account()).isEqualTo(traderAccount.accountData().account());
    assertThat(auctionSlot.authAccounts()).asList().extracting("account")
        .containsExactly(authAccount1.publicKey().deriveAddress());
  }

  @Test
  void depositAndWithdraw() throws JsonRpcClientErrorException, JsonProcessingException {
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();
    AmmInfoResult amm = createAmm(issuerKeyPair, feeResult);
    KeyPair traderKeyPair = createRandomAccountEd25519();

    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    AccountInfoResult traderAccountAfterDeposit = depositXrp(
        issuerKeyPair,
        traderKeyPair,
        traderAccount,
        amm,
        signatureService,
        feeResult
    );

    AmmInfoResult ammInfoAfterDeposit = getAmmInfo(issuerKeyPair);
    AmmWithdraw withdraw = AmmWithdraw.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccountAfterDeposit.accountData().sequence())
        .lastLedgerSequence(
            traderAccountAfterDeposit.ledgerCurrentIndexSafe()
                .plus(UnsignedInteger.valueOf(4000))
                .unsignedIntegerValue()
        )
        .signingPublicKey(traderKeyPair.publicKey())
        .asset2(
            Issue.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .build()
        )
        .asset(Issue.XRP)
        .amount(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(9)))
        .flags(AmmWithdrawFlags.SINGLE_ASSET)
        .build();

    SingleSignedTransaction<AmmWithdraw> signedWithdraw = signatureService.sign(traderKeyPair.privateKey(), withdraw);

    SubmitResult<AmmWithdraw> voteSubmitResult = xrplClient.submit(signedWithdraw);
    assertThat(voteSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    scanForFinality(
        signedWithdraw.hash(),
        traderAccount.ledgerIndexSafe(),
        withdraw.lastLedgerSequence().get(),
        withdraw.sequence(),
        traderKeyPair.publicKey().deriveAddress()
    );

    AmmInfoResult ammAfterWithdraw = getAmmInfo(issuerKeyPair);
    assertThat(ammAfterWithdraw.amm().amount()).isInstanceOf(XrpCurrencyAmount.class)
        .isEqualTo(((XrpCurrencyAmount) ammInfoAfterDeposit.amm().amount())
            .minus((XrpCurrencyAmount) withdraw.amount().get()));

    AccountInfoResult traderAccountAfterWithdraw = xrplClient.accountInfo(
        AccountInfoRequestParams.of(traderKeyPair.publicKey().deriveAddress())
    );

    assertThat(traderAccountAfterWithdraw.accountData().balance()).isEqualTo(
        traderAccountAfterDeposit.accountData().balance()
            .minus(withdraw.fee())
            .plus((XrpCurrencyAmount) withdraw.amount().get())
    );
  }

  @Test
  void depositAndClawbackTwoAssets() throws JsonRpcClientErrorException, JsonProcessingException {
    // create issuer and trader wallets
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair traderKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();

    AccountInfoResult issuerAccount = scanForResult(
        () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );
    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    // enable rippling
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence(), feeResult, AccountSetFlag.DEFAULT_RIPPLE);

    // enable Allow Trustline Clawback
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence().plus(UnsignedInteger.ONE),
        feeResult, AccountSetFlag.ALLOW_TRUSTLINE_CLAWBACK);

    // create Trustline from issuer to trader for currency USD and define two currencies
    TrustSet trustSet = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence())
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline =
        signatureService.sign(traderKeyPair.privateKey(), trustSet);
    SubmitResult<TrustSet> trustlineSubmitResult = xrplClient.submit(signedTrustline);
    assertThat(trustlineSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create Trustline from issuer to trader for xrpl4j coin
    TrustSet trustSet2 = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.ONE))
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(
            IssuedCurrencyAmount.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .value("1000000")
                .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline2 =
        signatureService.sign(traderKeyPair.privateKey(), trustSet2);
    SubmitResult<TrustSet> trustlineSubmitResult2 = xrplClient.submit(signedTrustline2);
    assertThat(trustlineSubmitResult2.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of xrpl4jcoin to trader wallet from issuer
    Payment paymentXrpl4jCoin = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(xrpl4jCoin)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("500")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentXrpl4jCoin =
        signatureService.sign(issuerKeyPair.privateKey(), paymentXrpl4jCoin);
    SubmitResult<Payment> paymentXrpl4jCoinSubmitResult = xrplClient.submit(signedPaymentXrpl4jCoin);
    assertThat(paymentXrpl4jCoinSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of USD to trader wallet from issuer
    Payment paymentUsd = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentUsd = signatureService.sign(issuerKeyPair.privateKey(), paymentUsd);
    SubmitResult<Payment> paymentUsdSubmitResult = xrplClient.submit(signedPaymentUsd);
    assertThat(paymentUsdSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMM from issuer account
    XrpCurrencyAmount reserveAmount = xrplClient.serverInformation().info()
        .map(
            rippled -> rippled.closedLedger().orElse(rippled.validatedLedger().get()).reserveIncXrp(),
            clio -> clio.validatedLedger().get().reserveIncXrp(),
            reporting -> reporting.closedLedger().orElse(reporting.validatedLedger().get()).reserveIncXrp()
        );
    AmmCreate ammCreate = AmmCreate.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .fee(reserveAmount)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(xrpl4jCoin)
                .value("100")
                .build()
        )
        .amount2(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("250")
                .build()
        )
        .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
        .lastLedgerSequence(issuerAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(100)).unsignedIntegerValue())
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmCreate> signedCreate = signatureService.sign(traderKeyPair.privateKey(), ammCreate);
    SubmitResult<AmmCreate> submitResult = xrplClient.submit(signedCreate);
    assertThat(submitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create variables for issued currencies
    Issue testCurrencyIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(usd)
        .build();
    Issue xrpl4jCoinIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .build();

    // deposit assets into AMM pool
    AmmDeposit ammDeposit = AmmDeposit.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("10")
                .build()
        )
        .flags(AmmDepositFlags.SINGLE_ASSET)
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmDeposit> signedAmmDeposit =
        signatureService.sign(traderKeyPair.privateKey(), ammDeposit);
    SubmitResult<AmmDeposit> ammDepositResult = xrplClient.submit(signedAmmDeposit);
    assertThat(ammDepositResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMMClawback
    AmmClawback ammClawback = AmmClawback.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .holder(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(20))
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(4)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .flags(AmmClawbackFlags.CLAW_TWO_ASSETS)
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmClawback> signedAmmClawback =
        signatureService.sign(issuerKeyPair.privateKey(), ammClawback);
    SubmitResult<AmmClawback> ammClawbackResult = xrplClient.submit(signedAmmClawback);
    assertThat(ammClawbackResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
  }

  @Test
  void depositAndClawbackWithAmount() throws JsonRpcClientErrorException, JsonProcessingException {
    // create issuer and trader wallets
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair traderKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();

    AccountInfoResult issuerAccount = scanForResult(
        () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );
    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    // enable rippling
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence(), feeResult, AccountSetFlag.DEFAULT_RIPPLE);

    // enable Allow Trustline Clawback
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence().plus(UnsignedInteger.ONE),
        feeResult, AccountSetFlag.ALLOW_TRUSTLINE_CLAWBACK);

    // create Trustline from issuer to trader for currency USD and define two currencies
    TrustSet trustSet = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence())
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline =
        signatureService.sign(traderKeyPair.privateKey(), trustSet);
    SubmitResult<TrustSet> trustlineSubmitResult = xrplClient.submit(signedTrustline);
    assertThat(trustlineSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create Trustline from issuer to trader for xrpl4jcoin
    TrustSet trustSet2 = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.ONE))
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(
            IssuedCurrencyAmount.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .value("1000000")
                .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline2 =
        signatureService.sign(traderKeyPair.privateKey(), trustSet2);
    SubmitResult<TrustSet> trustlineSubmitResult2 = xrplClient.submit(signedTrustline2);
    assertThat(trustlineSubmitResult2.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of xrpl4jcoin to trader wallet from issuer
    Payment paymentXrpl4jCoin = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(xrpl4jCoin)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("500")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentXrpl4jCoin =
        signatureService.sign(issuerKeyPair.privateKey(), paymentXrpl4jCoin);
    SubmitResult<Payment> paymentXrpl4jCoinSubmitResult = xrplClient.submit(signedPaymentXrpl4jCoin);
    assertThat(paymentXrpl4jCoinSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of USD to trader wallet from issuer
    Payment paymentUsd = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentUsd = signatureService.sign(issuerKeyPair.privateKey(), paymentUsd);
    SubmitResult<Payment> paymentUsdSubmitResult = xrplClient.submit(signedPaymentUsd);
    assertThat(paymentUsdSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMM from issuer account
    XrpCurrencyAmount reserveAmount = xrplClient.serverInformation().info()
        .map(
            rippled -> rippled.closedLedger().orElse(rippled.validatedLedger().get()).reserveIncXrp(),
            clio -> clio.validatedLedger().get().reserveIncXrp(),
            reporting -> reporting.closedLedger().orElse(reporting.validatedLedger().get()).reserveIncXrp()
        );
    AmmCreate ammCreate = AmmCreate.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .fee(reserveAmount)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(xrpl4jCoin)
                .value("100")
                .build()
        )
        .amount2(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("250")
                .build()
        )
        .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
        .lastLedgerSequence(issuerAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(100)).unsignedIntegerValue())
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmCreate> signedCreate = signatureService.sign(traderKeyPair.privateKey(), ammCreate);
    SubmitResult<AmmCreate> submitResult = xrplClient.submit(signedCreate);
    assertThat(submitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create variables for issued currencies
    Issue testCurrencyIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(usd)
        .build();
    Issue xrpl4jCoinIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .build();

    // deposit assets into AMM pool
    AmmDeposit ammDeposit = AmmDeposit.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("10")
                .build()
        )
        .flags(AmmDepositFlags.SINGLE_ASSET)
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmDeposit> signedAmmDeposit =
        signatureService.sign(traderKeyPair.privateKey(), ammDeposit);
    SubmitResult<AmmDeposit> ammDepositResult = xrplClient.submit(signedAmmDeposit);
    assertThat(ammDepositResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMMClawback
    AmmClawback ammClawback = AmmClawback.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .holder(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(20))
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(4)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("5")
                .build()
        )
        .flags(AmmClawbackFlags.UNSET)
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmClawback> signedAmmClawback =
        signatureService.sign(issuerKeyPair.privateKey(), ammClawback);
    SubmitResult<AmmClawback> ammClawbackResult = xrplClient.submit(signedAmmClawback);
    assertThat(ammClawbackResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
  }

  @Test
  void depositAndClawbackSingleAsset() throws JsonRpcClientErrorException, JsonProcessingException {
    // create issuer and trader wallets
    KeyPair issuerKeyPair = createRandomAccountEd25519();
    KeyPair traderKeyPair = createRandomAccountEd25519();
    FeeResult feeResult = xrplClient.fee();

    AccountInfoResult issuerAccount = scanForResult(
        () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );
    AccountInfoResult traderAccount = scanForResult(
        () -> this.getValidatedAccountInfo(traderKeyPair.publicKey().deriveAddress())
    );

    // enable rippling
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence(), feeResult, AccountSetFlag.DEFAULT_RIPPLE);

    // enable Allow Trustline Clawback
    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence().plus(UnsignedInteger.ONE),
        feeResult, AccountSetFlag.ALLOW_TRUSTLINE_CLAWBACK);

    // create Trustline from issuer to trader for currency USD
    TrustSet trustSet = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence())
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline =
        signatureService.sign(traderKeyPair.privateKey(), trustSet);
    SubmitResult<TrustSet> trustlineSubmitResult = xrplClient.submit(signedTrustline);
    assertThat(trustlineSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create Trustline from issuer to trader for xrpl4jcoin
    TrustSet trustSet2 = TrustSet.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.ONE))
        .signingPublicKey(traderKeyPair.publicKey())
        .limitAmount(
            IssuedCurrencyAmount.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .value("1000000")
                .build()
        )
        .flags(TrustSetFlags.builder().tfClearNoRipple().build())
        .build();

    SingleSignedTransaction<TrustSet> signedTrustline2 =
        signatureService.sign(traderKeyPair.privateKey(), trustSet2);
    SubmitResult<TrustSet> trustlineSubmitResult2 = xrplClient.submit(signedTrustline2);
    assertThat(trustlineSubmitResult2.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of xrpl4jCoin to trader wallet from issuer
    Payment paymentXrpl4jCoin = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(xrpl4jCoin)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("500")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentXrpl4jCoin =
        signatureService.sign(issuerKeyPair.privateKey(), paymentXrpl4jCoin);
    SubmitResult<Payment> paymentXrpl4jCoinSubmitResult = xrplClient.submit(signedPaymentXrpl4jCoin);
    assertThat(paymentXrpl4jCoinSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // send payment of USD to trader wallet from issuer
    Payment paymentUsd = Payment.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .destination(traderKeyPair.publicKey().deriveAddress())
        .amount(IssuedCurrencyAmount.builder()
            .currency(usd)
            .issuer(issuerKeyPair.publicKey().deriveAddress())
            .value("1000")
            .build())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<Payment> signedPaymentUsd = signatureService.sign(issuerKeyPair.privateKey(), paymentUsd);
    SubmitResult<Payment> paymentUsdSubmitResult = xrplClient.submit(signedPaymentUsd);
    assertThat(paymentUsdSubmitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMM from issuer account
    XrpCurrencyAmount reserveAmount = xrplClient.serverInformation().info()
        .map(
            rippled -> rippled.closedLedger().orElse(rippled.validatedLedger().get()).reserveIncXrp(),
            clio -> clio.validatedLedger().get().reserveIncXrp(),
            reporting -> reporting.closedLedger().orElse(reporting.validatedLedger().get()).reserveIncXrp()
        );
    AmmCreate ammCreate = AmmCreate.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(2)))
        .fee(reserveAmount)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(xrpl4jCoin)
                .value("100")
                .build()
        )
        .amount2(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("250")
                .build()
        )
        .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
        .lastLedgerSequence(issuerAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(100)).unsignedIntegerValue())
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmCreate> signedCreate = signatureService.sign(traderKeyPair.privateKey(), ammCreate);
    SubmitResult<AmmCreate> submitResult = xrplClient.submit(signedCreate);
    assertThat(submitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create variables for issued currencies
    Issue testCurrencyIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(usd)
        .build();
    Issue xrpl4jCoinIssue = Issue.builder()
        .issuer(issuerKeyPair.publicKey().deriveAddress())
        .currency(xrpl4jCoin)
        .build();

    // deposit assets into AMM pool
    AmmDeposit ammDeposit = AmmDeposit.builder()
        .account(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(10))
        .sequence(traderAccount.accountData().sequence().plus(UnsignedInteger.valueOf(3)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(usd)
                .value("10")
                .build()
        )
        .flags(AmmDepositFlags.SINGLE_ASSET)
        .signingPublicKey(traderKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmDeposit> signedAmmDeposit =
        signatureService.sign(traderKeyPair.privateKey(), ammDeposit);
    SubmitResult<AmmDeposit> ammDepositResult = xrplClient.submit(signedAmmDeposit);
    assertThat(ammDepositResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    // create AMMClawback
    AmmClawback ammClawback = AmmClawback.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .holder(traderKeyPair.publicKey().deriveAddress())
        .fee(XrpCurrencyAmount.ofDrops(20))
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.valueOf(4)))
        .asset(testCurrencyIssue)
        .asset2(xrpl4jCoinIssue)
        .signingPublicKey(issuerKeyPair.publicKey())
        .flags(AmmClawbackFlags.UNSET)
        .build();

    SingleSignedTransaction<AmmClawback> signedAmmClawback =
        signatureService.sign(issuerKeyPair.privateKey(), ammClawback);
    SubmitResult<AmmClawback> ammClawbackResult = xrplClient.submit(signedAmmClawback);
    assertThat(ammClawbackResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);
  }

  private AccountInfoResult depositXrp(
      KeyPair issuerKeyPair,
      KeyPair traderKeyPair,
      AccountInfoResult traderAccount,
      AmmInfoResult amm,
      SignatureService<PrivateKey> signatureService,
      FeeResult feeResult
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    XrpCurrencyAmount depositAmount = XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(10));
    AmmDeposit deposit = AmmDeposit.builder()
        .account(traderAccount.accountData().account())
        .asset2(
            Issue.builder()
                .currency(xrpl4jCoin)
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .build()
        )
        .asset(Issue.XRP)
        .flags(AmmDepositFlags.SINGLE_ASSET)
        .amount(depositAmount)
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .sequence(traderAccount.accountData().sequence())
        .signingPublicKey(traderKeyPair.publicKey())
        .lastLedgerSequence(traderAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4000)).unsignedIntegerValue())
        .build();

    SingleSignedTransaction<AmmDeposit> signedDeposit = signatureService.sign(traderKeyPair.privateKey(), deposit);
    SubmitResult<AmmDeposit> submitResult = xrplClient.submit(signedDeposit);
    assertThat(submitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    scanForFinality(
        signedDeposit.hash(),
        traderAccount.ledgerIndexSafe(),
        deposit.lastLedgerSequence().get(),
        deposit.sequence(),
        traderKeyPair.publicKey().deriveAddress()
    );

    AccountInfoResult traderAccountAfterDeposit = xrplClient.accountInfo(
        AccountInfoRequestParams.of(traderAccount.accountData().account())
    );

    assertThat(traderAccountAfterDeposit.accountData().balance())
        .isEqualTo(traderAccount.accountData().balance().minus(deposit.fee()).minus(depositAmount));

    AccountLinesResult traderLines = xrplClient.accountLines(
        AccountLinesRequestParams.builder()
            .account(traderAccount.accountData().account())
            .peer(amm.amm().account())
            .ledgerSpecifier(LedgerSpecifier.CURRENT)
            .build()
    );

    assertThat(traderLines.lines()).asList().hasSize(1);
    TrustLine lpLine = traderLines.lines().get(0);
    assertThat(lpLine.currency()).isEqualTo(amm.amm().lpToken().currency());
    assertThat(new BigDecimal(lpLine.balance())).isGreaterThan(BigDecimal.ZERO);

    return traderAccountAfterDeposit;
  }

  private AmmInfoResult createAmm(
      KeyPair issuerKeyPair,
      FeeResult feeResult
  ) throws JsonRpcClientErrorException, JsonProcessingException {
    AccountInfoResult issuerAccount = scanForResult(
        () -> this.getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress())
    );

    enableFlag(issuerKeyPair, issuerAccount.accountData().sequence(), feeResult, AccountSetFlag.DEFAULT_RIPPLE);

    XrpCurrencyAmount reserveAmount = xrplClient.serverInformation().info()
        .map(
            rippled -> rippled.closedLedger().orElse(rippled.validatedLedger().get()).reserveIncXrp(),
            clio -> clio.validatedLedger().get().reserveIncXrp(),
            reporting -> reporting.closedLedger().orElse(reporting.validatedLedger().get()).reserveIncXrp()
        );
    AmmCreate ammCreate = AmmCreate.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .sequence(issuerAccount.accountData().sequence().plus(UnsignedInteger.ONE))
        .fee(reserveAmount)
        .amount(
            IssuedCurrencyAmount.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(xrpl4jCoin)
                .value("2.5")
                .build()
        )
        .amount2(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(10)))
        .tradingFee(TradingFee.ofPercent(BigDecimal.ONE))
        .lastLedgerSequence(issuerAccount.ledgerIndexSafe().plus(UnsignedInteger.valueOf(4000)).unsignedIntegerValue())
        .signingPublicKey(issuerKeyPair.publicKey())
        .build();

    SingleSignedTransaction<AmmCreate> signedCreate = signatureService.sign(issuerKeyPair.privateKey(), ammCreate);
    SubmitResult<AmmCreate> submitResult = xrplClient.submit(signedCreate);
    assertThat(submitResult.engineResult()).isEqualTo(TransactionResultCodes.TES_SUCCESS);

    scanForFinality(
        signedCreate.hash(),
        issuerAccount.ledgerIndexSafe(),
        ammCreate.lastLedgerSequence().get(),
        ammCreate.sequence(),
        issuerKeyPair.publicKey().deriveAddress()
    );

    return getAmmInfo(issuerKeyPair);
  }

  private AmmInfoResult getAmmInfo(KeyPair issuerKeyPair) throws JsonRpcClientErrorException {
    AmmInfoResult ammInfoResult = xrplClient.ammInfo(
        AmmInfoRequestParams.from(
            Issue.XRP,
            Issue.builder()
                .issuer(issuerKeyPair.publicKey().deriveAddress())
                .currency(xrpl4jCoin)
                .build()
        ));

    AccountInfoResult ammAccountInfo = xrplClient.accountInfo(
        AccountInfoRequestParams.of(ammInfoResult.amm().account())
    );

    assertThat(ammAccountInfo.accountData().ammId()).isNotEmpty();

    AmmInfoResult ammInfoByAccount = xrplClient.ammInfo(
        AmmInfoRequestParams.from(ammAccountInfo.accountData().account())
    );

    assertThat(ammInfoByAccount.amm()).isEqualTo(ammInfoResult.amm());

    LedgerEntryResult<AmmObject> ammObject = xrplClient.ledgerEntry(
        LedgerEntryRequestParams.amm(
            AmmLedgerEntryParams.builder()
                .asset(Issue.XRP)
                .asset2(
                    Issue.builder()
                        .issuer(issuerKeyPair.publicKey().deriveAddress())
                        .currency(xrpl4jCoin)
                        .build()
                )
                .build(),
            LedgerSpecifier.VALIDATED
        )
    );

    assertThat(ammObject.node().account()).isEqualTo(ammInfoByAccount.amm().account());
    assertThat(ammObject.node().asset()).isEqualTo(Issue.XRP);
    assertThat(ammObject.node().asset2()).isEqualTo(
        Issue.builder()
            .issuer(((IssuedCurrencyAmount) ammInfoByAccount.amm().amount2()).issuer())
            .currency(((IssuedCurrencyAmount) ammInfoByAccount.amm().amount2()).currency())
            .build()
    );
    assertThat(
        (ammObject.node().auctionSlot().isPresent() && ammInfoByAccount.amm().auctionSlot().isPresent()) ||
            (!ammObject.node().auctionSlot().isPresent() && !ammInfoByAccount.amm().auctionSlot().isPresent())
    ).isTrue();
    if (ammObject.node().auctionSlot().isPresent()) {
      AuctionSlot entryAuctionSlot = ammObject.node().auctionSlot().get();
      AmmInfoAuctionSlot infoAuctionSlot = ammInfoByAccount.amm().auctionSlot().get();
      assertThat(entryAuctionSlot.account()).isEqualTo(infoAuctionSlot.account());
      assertThat(entryAuctionSlot.authAccountsAddresses()).isEqualTo(infoAuctionSlot.authAccounts().stream().map(
          AmmInfoAuthAccount::account).collect(Collectors.toList()));
      assertThat(entryAuctionSlot.price()).isEqualTo(infoAuctionSlot.price());
      assertThat(TimeUtils.xrplTimeToZonedDateTime(UnsignedLong.valueOf(entryAuctionSlot.expiration().longValue())))
          .isEqualTo(infoAuctionSlot.expiration());
      assertThat(entryAuctionSlot.discountedFee()).isEqualTo(infoAuctionSlot.discountedFee());
    }

    assertThat(ammObject.node().lpTokenBalance()).isEqualTo(ammInfoByAccount.amm().lpToken());
    assertThat(ammObject.node().tradingFee()).isEqualTo(ammInfoByAccount.amm().tradingFee());

    assertThat(ammObject.node().voteSlots().size()).isEqualTo(ammInfoByAccount.amm().voteSlots().size());

    LedgerEntryResult<AmmObject> entryByIndex = xrplClient.ledgerEntry(
        LedgerEntryRequestParams.index(ammObject.index(), AmmObject.class, LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(ammObject.node());

    LedgerEntryResult<LedgerObject> entryByIndexUnTyped = xrplClient.ledgerEntry(
        LedgerEntryRequestParams.index(ammObject.index(), LedgerSpecifier.VALIDATED)
    );

    assertThat(entryByIndex.node()).isEqualTo(entryByIndexUnTyped.node());

    return ammInfoResult;
  }

  private void enableFlag(KeyPair issuerKeyPair, UnsignedInteger sequence, FeeResult feeResult,
                          AccountSetFlag accountSetFlag)
      throws JsonRpcClientErrorException, JsonProcessingException {
    AccountSet accountSet = AccountSet.builder()
        .account(issuerKeyPair.publicKey().deriveAddress())
        .fee(FeeUtils.computeNetworkFees(feeResult).recommendedFee())
        .signingPublicKey(issuerKeyPair.publicKey())
        .sequence(sequence)
        .setFlag(accountSetFlag)
        .build();

    SingleSignedTransaction<AccountSet> signed = signatureService.sign(issuerKeyPair.privateKey(), accountSet);
    SubmitResult<AccountSet> setResult = xrplClient.submit(signed);
    assertThat(setResult.engineResult()).isEqualTo("tesSUCCESS");
    logger.info(
        "AccountSet transaction successful: https://testnet.xrpl.org/transactions/{}",
        setResult.transactionResult().hash()
    );

    scanForResult(
        () -> getValidatedAccountInfo(issuerKeyPair.publicKey().deriveAddress()),
        info -> info.accountData().flags().lsfDefaultRipple()
    );
  }
}
