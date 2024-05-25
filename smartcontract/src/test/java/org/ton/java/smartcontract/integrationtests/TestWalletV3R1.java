package org.ton.java.smartcontract.integrationtests;

import com.iwebpp.crypto.TweetNaclFast;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ton.java.address.Address;
import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.TestFaucet;
import org.ton.java.smartcontract.types.WalletV3Config;
import org.ton.java.smartcontract.utils.MsgUtils;
import org.ton.java.smartcontract.wallet.v3.WalletV3R1;
import org.ton.java.tlb.types.Message;
import org.ton.java.tonlib.types.ExtMessageInfo;
import org.ton.java.utils.Utils;

import java.math.BigInteger;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@RunWith(JUnit4.class)
public class TestWalletV3R1 extends CommonTest {
    @Test
    public void testWalletV3R1() throws InterruptedException {
        TweetNaclFast.Signature.KeyPair keyPair = Utils.generateSignatureKeyPair();

        WalletV3R1 contract = WalletV3R1.builder()
                .keyPair(keyPair)
                .walletId(42)
                .build();

        log.info("pub-key {}", Utils.bytesToHex(contract.getKeyPair().getPublicKey()));
        log.info("prv-key {}", Utils.bytesToHex(contract.getKeyPair().getSecretKey()));


        Message msg = MsgUtils.createExternalMessageWithSignedBody(keyPair, contract.getAddress(),
                contract.getStateInit(),
                CellBuilder.beginCell()
                        .storeUint(42, 32) // subwallet
                        .storeUint(Instant.now().getEpochSecond() + 5 * 60L, 32)  //valid-until
                        .storeUint(0, 32) //seqno
                        .endCell());
        Address address = msg.getInit().getAddress();

        String nonBounceableAddress = address.toString(true, true, false, true);
        String bounceableAddress = address.toString(true, true, true, true);

        String my = "Creating new wallet in workchain " + contract.getWc() + "\n";
        my = my + "Loading private key from file new-wallet.pk" + "\n";
        my = my + "StateInit: " + msg.getInit().toCell().print() + "\n";
        my = my + "new wallet address = " + address.toString(false) + "\n";
        my = my + "(Saving address to file new-wallet.addr)" + "\n";
        my = my + "Non-bounceable address (for init): " + nonBounceableAddress + "\n";
        my = my + "Bounceable address (for later access): " + bounceableAddress + "\n";
        my = my + "signing message: " + msg.getBody().print() + "\n";
        my = my + "External message for initialization is " + msg.toCell().print() + "\n";
        my = my + Utils.bytesToHex(msg.getBody().toBoc()).toUpperCase() + "\n";
        my = my + "(Saved wallet creating query to file new-wallet-query.boc)" + "\n";
        log.info(my);

        // top up new wallet using test-faucet-wallet
        BigInteger balance = TestFaucet.topUpContract(tonlib, Address.of(nonBounceableAddress), Utils.toNano(1));
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));

        // deploy new wallet
        ExtMessageInfo extMessageInfo = tonlib.sendRawMessage(msg.toCell().toBase64());
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForDeployment(20);

        // try to transfer coins from new wallet (back to faucet)
        WalletV3Config config = WalletV3Config.builder()
                .seqno(contract.getSeqno())
                .destination(Address.of(TestFaucet.BOUNCEABLE))
                .amount(Utils.toNano(0.8))
                .comment("testWalletV3R1")
                .build();

        extMessageInfo = contract.send(config);
        assertThat(extMessageInfo.getError().getCode()).isZero();

        contract.waitForBalanceChange(90);

        balance = contract.getBalance();
        log.info("new wallet {} balance: {}", contract.getName(), Utils.formatNanoValue(balance));
        assertThat(balance.longValue()).isLessThan(Utils.toNano(0.2).longValue());
    }
}
