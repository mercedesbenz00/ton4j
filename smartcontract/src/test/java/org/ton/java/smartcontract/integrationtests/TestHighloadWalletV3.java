package org.ton.java.smartcontract.integrationtests;

import com.iwebpp.crypto.TweetNaclFast;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ton.java.address.Address;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.TestFaucet;
import org.ton.java.smartcontract.highload.HighloadWalletV3;
import org.ton.java.smartcontract.types.Destination;
import org.ton.java.smartcontract.types.HighloadQueryId;
import org.ton.java.smartcontract.types.HighloadV3Config;
import org.ton.java.smartcontract.types.HighloadV3InternalMessageBody;
import org.ton.java.tlb.types.*;
import org.ton.java.tonlib.Tonlib;
import org.ton.java.tonlib.types.ExtMessageInfo;
import org.ton.java.tonlib.types.VerbosityLevel;
import org.ton.java.utils.Utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@RunWith(JUnit4.class)
public class TestHighloadWalletV3 extends CommonTest {

    @Test
    public void testBulkTransferSimplified_2() throws InterruptedException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();
        String rawAddress = contract.getAddress().toRaw();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", rawAddress);
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(0.1));
        Utils.sleep(30, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
//                .createdAt(createdAt) // default = now - 60 seconds
                //.timeOut(60) //default timeout = 5 minutes
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(45);

        config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .body(
                        contract.createBulkTransfer(List.of(
                                        Destination.builder()
                                                .address("0QAs9VlT6S776tq3unJcP5Ogsj-ELLunLXuOb1EKcOQi4-QO")
                                                .amount(Utils.toNano(0.022))
                                                .body(CellBuilder.beginCell()
                                                        .storeUint(0, 32)
                                                        .storeString("test-comment-1")
                                                        .endCell()
                                                ).build(),
                                        Destination.builder()
                                                .address("EQAyjRKDnEpTBNfRHqYdnzGEQjdY4KG3gxgqiG3DpDY46u8G")
                                                .amount(Utils.toNano(0.033))
                                                .comment("test-comment-2")
                                                .build()
                                ),
                                BigInteger.valueOf(HighloadQueryId.fromSeqno(1).getQueryId()))
                )
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent 2 messages");
    }


    /**
     * Sends 1000 messages with values without comment/memo field
     */
    @Test
    public void testBulkTransferSimplified_1000() throws InterruptedException, NoSuchAlgorithmException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();
        String rawAddress = contract.getAddress().toRaw();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", rawAddress);
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(12));
        Utils.sleep(30, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(45);

        config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .body(contract.createBulkTransfer(
                        createDummyDestinations(1000),
                        BigInteger.valueOf(HighloadQueryId.fromSeqno(1).getQueryId())))
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent 1000 messages");
    }


    /**
     * Sends 1000 messages with jetton values with comment
     */
    @Test
    public void testBulkJettonTransferSimplified_1000() throws InterruptedException, NoSuchAlgorithmException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();
        String rawAddress = contract.getAddress().toRaw();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", rawAddress);
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(12));
        Utils.sleep(30, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(45);

        config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .body(contract.createBulkTransfer(
                        createDummyDestinations(300),
                        BigInteger.valueOf(HighloadQueryId.fromSeqno(1).getQueryId())))
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent 1000 messages");
    }

    @Test
    public void testSinglePayloadTransfer() throws InterruptedException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();
        String rawAddress = contract.getAddress().toRaw();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", rawAddress);
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet        
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(0.1));
        Utils.sleep(30, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        Address destAddress = Address.of("EQAyjRKDnEpTBNfRHqYdnzGEQjdY4KG3gxgqiG3DpDY46u8G");

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
//                .createdAt(createdAt) // default = now - 60 seconds
                //.timeOut(60) //default timeout = 5 minutes
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(45);

        config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .body(contract.createSingleTransfer(destAddress, 0.02, true,
                        null,
//                        MsgUtils.createTextMessageBody("ton4j")
                        CellBuilder.beginCell().endCell()
                ))
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent single message");
    }

    @Test
    public void testTransfer_3_DifferentRecipients() throws InterruptedException, NoSuchAlgorithmException {
        tonlib = Tonlib.builder()
                .testnet(true)
                .ignoreCache(false)
                .verbosityLevel(VerbosityLevel.DEBUG)
                .build();

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();
        String rawAddress = contract.getAddress().toRaw();


        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", rawAddress);
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(0.1));
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(30);

        int numberOfRecipients = 3;
        BigInteger amountToSendTotal = Utils.toNano(0.01 * numberOfRecipients);

        Cell nMessages = createDummyRecipients(numberOfRecipients, contract, null);

        Cell extMsgWith3Mgs = contract.createBulkTransfer(amountToSendTotal, nMessages);

        config = HighloadV3Config.builder()
                .walletId(42)
                .body(extMsgWith3Mgs)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent {} messages", numberOfRecipients);
    }

    @Test
    public void testTransfer_200_CustomCell() throws InterruptedException, NoSuchAlgorithmException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", contract.getAddress().toString(false));
        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(3));
        Utils.sleep(10, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

//        long createdAt = Instant.now().getEpochSecond() - 60 * 5;

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(30);

        int numberOfRecipients = 200;
        BigInteger amountToSendTotal = Utils.toNano(0.01 * numberOfRecipients);

        Cell nMessages = createDummyRecipients(numberOfRecipients, contract, null);

        Cell extMsgWith200Mgs = contract.createBulkTransfer(amountToSendTotal, nMessages);

        config = HighloadV3Config.builder()
                .walletId(42)
                .body(extMsgWith200Mgs)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent {} messages", numberOfRecipients);
    }

    @Test
    public void testTransfer_1000_CustomCell() throws InterruptedException, NoSuchAlgorithmException {

        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        HighloadWalletV3 contract = HighloadWalletV3.builder()
                .tonlib(tonlib)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String nonBounceableAddress = contract.getAddress().toNonBounceable();
        String bounceableAddress = contract.getAddress().toBounceable();

        log.info("non-bounceable address {}", nonBounceableAddress);
        log.info("    bounceable address {}", bounceableAddress);
        log.info("           raw address {}", contract.getAddress().toString(false));

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(12));
        Utils.sleep(10, "topping up...");
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        HighloadV3Config config = HighloadV3Config.builder()
                .walletId(42)
                .queryId(HighloadQueryId.fromSeqno(0).getQueryId())
                .build();

        ExtMessageInfo extMessageInfo = contract.deploy(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(30);

        Cell nMessages1 = createDummyRecipients(250, contract, null);
        Cell nMessages2 = createDummyRecipients(250, contract, nMessages1);
        Cell nMessages3 = createDummyRecipients(250, contract, nMessages2);
        Cell nMessages4 = createDummyRecipients(250, contract, nMessages3);
        Cell extMsgWith400Mgs = contract.createBulkTransfer(Utils.toNano(11), nMessages4);

        config = HighloadV3Config.builder()
                .walletId(42)
                .body(extMsgWith400Mgs)
                .queryId(HighloadQueryId.fromSeqno(1).getQueryId())
                .build();

        extMessageInfo = contract.sendTonCoins(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();
        log.info("sent {} messages", 1000);
    }

    @Test
    public void testHighloadQueryId() {
        HighloadQueryId qid = new HighloadQueryId();
        assertThat(qid.getQueryId()).isEqualTo(0);
        qid = qid.getNext();
        assertThat(qid.getQueryId()).isEqualTo(1);
        for (int i = 0; i < 1022; i++) {
            qid = qid.getNext();
        }
        assertThat(qid.getQueryId()).isEqualTo(1024);
        assertThat(qid.toSeqno()).isEqualTo(1023);

        qid = HighloadQueryId.fromShiftAndBitNumber(8191, 1020);
        assertThat(qid.hasNext()).isTrue();
        qid = qid.getNext();
        assertThat(qid.hasNext()).isFalse();

        int nqid = qid.getQueryId();
        qid = HighloadQueryId.fromSeqno(qid.toSeqno());
        assertThat(nqid).isEqualTo(qid.getQueryId());
        qid = HighloadQueryId.fromQueryId(qid.getQueryId());
        assertThat(nqid).isEqualTo(qid.getQueryId());
    }

    Cell createDummyRecipients(int numRecipients, HighloadWalletV3 contract, Cell body) throws NoSuchAlgorithmException {
        List<OutAction> outActions = new ArrayList<>();
        for (int i = 0; i < numRecipients; i++) {
            Address destinationAddress = Address.of("0:" + Utils.bytesToHex(MessageDigest.getInstance("SHA-256").digest(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))));
            log.info("dest {} is {}", i, destinationAddress.toBounceable());
            OutAction outAction = ActionSendMsg.builder()
                    .mode((byte) 3)
                    .outMsg(MessageRelaxed.builder()
                            .info(InternalMessageInfoRelaxed.builder()
                                    .bounce(false) // warning, for tests only
                                    .dstAddr(MsgAddressIntStd.builder()
                                            .workchainId(destinationAddress.wc)
                                            .address(destinationAddress.toBigInteger())
                                            .build())
                                    .value(CurrencyCollection.builder()
                                            .coins(Utils.toNano(0.01))
                                            .build())
                                    .build())
                            .build())
                    .build();
            outActions.add(outAction);
        }

        if (nonNull(body)) { // one of those N messages can contain internal message with other X recipients
            OutAction outAction = ActionSendMsg.builder()
                    .mode(3)
                    .outMsg(MessageRelaxed.builder()
                            .info(InternalMessageInfoRelaxed.builder()
                                    .dstAddr(MsgAddressIntStd.builder()
                                            .workchainId(contract.getAddress().wc)
                                            .address(contract.getAddress().toBigInteger())
                                            .build())
                                    .value(CurrencyCollection.builder()
                                            .coins(Utils.toNano(0.01))
                                            .build())
                                    .build())
                            .body(body) // added other X messages
                            .build())
                    .build();
            outActions.add(outAction);
        }

        return HighloadV3InternalMessageBody.builder()
                .queryId(BigInteger.ZERO)
                .actions(OutList.builder()
                        .actions(outActions)
                        .build())
                .build().toCell();
    }

    List<Destination> createDummyDestinations(int count) throws NoSuchAlgorithmException {
        List<Destination> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String dstDummyAddress = "0:" + Utils.bytesToHex(MessageDigest.getInstance("SHA-256").digest(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)));

            result.add(Destination.builder()
                    .bounce(false)
                    .address(dstDummyAddress)
                    .amount(Utils.toNano(0.01))
                    .comment("comment-" + i)
                    .build());
        }
        return result;
    }
}
