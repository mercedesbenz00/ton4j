package org.ton.java.smartcontract.unittests;

import com.iwebpp.crypto.TweetNaclFast;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.ton.java.address.Address;
import org.ton.java.smartcontract.types.WalletV3Config;
import org.ton.java.smartcontract.utils.MsgUtils;
import org.ton.java.smartcontract.wallet.v3.WalletV3R2;
import org.ton.java.tlb.types.Message;
import org.ton.java.utils.Utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@RunWith(JUnit4.class)
public class TestWalletsV3 {

    /**
     * >fift -s new-wallet-v3.fif 0 42
     */
    @Test
    public void testNewWalletV3R2() {
        // echo "F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4" | xxd -r -p - > new-wallet.pk
        byte[] secretKey = Utils.hexToSignedBytes("F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4");
        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(secretKey);

        WalletV3R2 contract = WalletV3R2.builder()
                .wc(0)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String codeAsHex = contract.getStateInit().getCode().bitStringToHex();
        String dataAsHex = contract.getStateInit().getData().bitStringToHex();
        String rawAddress = contract.getAddress().toRaw();

        assertThat(codeAsHex).isEqualTo("FF0020DD2082014C97BA218201339CBAB19F71B0ED44D0D31FD31F31D70BFFE304E0A4F2608308D71820D31FD31FD31FF82313BBF263ED44D0D31FD31FD3FFD15132BAF2A15144BAF2A204F901541055F910F2A3F8009320D74A96D307D402FB00E8D101A4C8CB1FCB1FCBFFC9ED54");
        assertThat(dataAsHex).isEqualTo("000000000000002A82A0B2543D06FEC0AAC952E9EC738BE56AB1B6027FC0C1AA817AE14B4D1ED2FB");
        assertThat(rawAddress).isEqualTo("0:2d29bfa071c8c62fa3398b661a842e60f04cb8a915fb3e749ef7c6c41343e16c");

        Message msg = contract.prepareDeployMsg();
        // external message for serialization
        assertThat(msg.toCell().bitStringToHex()).isEqualTo("88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D811874B9A113E0A2328885A19E53A31DA6ADA97D9D03506EAA33E03C541C2098F409E248E570BBD9DE806DCCF1E0727873DFF3A6C969E4824D3D77025D96B040D01200000055FFFFFFFE00000001_");
        // final boc
        assertThat(Utils.bytesToHex(msg.toCell().toBoc(true)).toUpperCase()).isEqualTo("B5EE9C724102030100010F0002DF88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D811874B9A113E0A2328885A19E53A31DA6ADA97D9D03506EAA33E03C541C2098F409E248E570BBD9DE806DCCF1E0727873DFF3A6C969E4824D3D77025D96B040D01200000055FFFFFFFE000000010010200DEFF0020DD2082014C97BA218201339CBAB19F71B0ED44D0D31FD31F31D70BFFE304E0A4F2608308D71820D31FD31FD31FF82313BBF263ED44D0D31FD31FD3FFD15132BAF2A15144BAF2A204F901541055F910F2A3F8009320D74A96D307D402FB00E8D101A4C8CB1FCB1FCBFFC9ED540050000000000000002A82A0B2543D06FEC0AAC952E9EC738BE56AB1B6027FC0C1AA817AE14B4D1ED2FBAD8E6092");
    }

    /**
     * >fift -s new-wallet-v3.fif -1 42
     */
    @Test
    public void testNewWalletV3R2Master() {
        // echo "F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4" | xxd -r -p - > new-wallet.pk
        byte[] secretKey = Utils.hexToSignedBytes("F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4");
        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(secretKey);

        WalletV3R2 contract = WalletV3R2.builder()
                .wc(-1)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        String codeAsHex = contract.getStateInit().getCode().bitStringToHex();
        String dataAsHex = contract.getStateInit().getData().bitStringToHex();
        String rawAddress = contract.getAddress().toRaw();

        assertThat(codeAsHex).isEqualTo("FF0020DD2082014C97BA218201339CBAB19F71B0ED44D0D31FD31F31D70BFFE304E0A4F2608308D71820D31FD31FD31FF82313BBF263ED44D0D31FD31FD3FFD15132BAF2A15144BAF2A204F901541055F910F2A3F8009320D74A96D307D402FB00E8D101A4C8CB1FCB1FCBFFC9ED54");
        assertThat(dataAsHex).isEqualTo("000000000000002A82A0B2543D06FEC0AAC952E9EC738BE56AB1B6027FC0C1AA817AE14B4D1ED2FB");
        assertThat(rawAddress).isEqualTo("-1:2d29bfa071c8c62fa3398b661a842e60f04cb8a915fb3e749ef7c6c41343e16c");

        Message msg = contract.prepareDeployMsg();
        // external message for serialization
        assertThat(msg.toCell().bitStringToHex()).isEqualTo("89FE5A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D811874B9A113E0A2328885A19E53A31DA6ADA97D9D03506EAA33E03C541C2098F409E248E570BBD9DE806DCCF1E0727873DFF3A6C969E4824D3D77025D96B040D01200000055FFFFFFFE00000001_");
        // final boc
        assertThat(Utils.bytesToHex(msg.toCell().toBoc(true)).toUpperCase()).isEqualTo("B5EE9C724102030100010F0002DF89FE5A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D811874B9A113E0A2328885A19E53A31DA6ADA97D9D03506EAA33E03C541C2098F409E248E570BBD9DE806DCCF1E0727873DFF3A6C969E4824D3D77025D96B040D01200000055FFFFFFFE000000010010200DEFF0020DD2082014C97BA218201339CBAB19F71B0ED44D0D31FD31F31D70BFFE304E0A4F2608308D71820D31FD31FD31FF82313BBF263ED44D0D31FD31FD3FFD15132BAF2A15144BAF2A204F901541055F910F2A3F8009320D74A96D307D402FB00E8D101A4C8CB1FCB1FCBFFC9ED540050000000000000002A82A0B2543D06FEC0AAC952E9EC738BE56AB1B6027FC0C1AA817AE14B4D1ED2FB7EB09F49");
    }

    /**
     * >fift -s wallet-v3.fif new-wallet 0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d 42 1 1 -t 1000
     */
    @Test
    public void testCreateTransferMessageWalletV3R2WithBounce() {
        byte[] secretKey = Utils.hexToSignedBytes("F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4");
        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(secretKey);

        WalletV3R2 contract = WalletV3R2.builder()
                .wc(0)
                .keyPair(keyPair)
                .walletId(42)
                .build();

        WalletV3Config config = WalletV3Config.builder()
                .destination(Address.of("0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d"))
                .walletId(42)
                .seqno(1L)
                .amount(Utils.toNano(1))
                .bounce(true)
                .validUntil(1000)
                .build();

        Message msg = contract.prepareExternalMsg(config);
        // external message for serialization
        assertThat(msg.toCell().bitStringToHex()).isEqualTo("88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D801935A67A209E4C1BFCB2E03099F84F721AC738C2C82A8725B61FE1FD0C28FD19FE78154A255F0683A0F11D84A3A98E93D13FA61E8E389E20305380D7A038A88780000015000001F40000000081C_");
        // external message in BoC format
        assertThat(Utils.bytesToHex(msg.toCell().toBoc(true)).toUpperCase()).isEqualTo("B5EE9C724101020100A90001DF88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D801935A67A209E4C1BFCB2E03099F84F721AC738C2C82A8725B61FE1FD0C28FD19FE78154A255F0683A0F11D84A3A98E93D13FA61E8E389E20305380D7A038A88780000015000001F40000000081C010068620012C72A4B1C534C0572E9E3B1C157E9FA79971A2416D7E1BA8F19AC2C4E46F006A1DCD650000000000000000000000000000013335626");
    }

    /**
     * >fift -s wallet-v3.fif new-wallet 0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d 42 1 1 -n -t 1000
     */
    @Test
    public void testCreateTransferMessageWalletV3R2NoBounce() {
        byte[] secretKey = Utils.hexToSignedBytes("F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4");
        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(secretKey);

        WalletV3R2 contract = WalletV3R2.builder()
                .walletId(42)
                .wc(0)
                .keyPair(keyPair)
                .build();

        WalletV3Config config = WalletV3Config.builder()
                .destination(Address.of("0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d"))
                .walletId(42)
                .seqno(1L)
                .amount(Utils.toNano(1))
                .bounce(false)
                .validUntil(1000)
                .build();

        Message msg = contract.prepareExternalMsg(config);
        // external message for serialization
        assertThat(msg.toCell().bitStringToHex()).isEqualTo("88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D802738F80EEA52936E75BBFA1525E8037940497F143C704F0EE6AE8AF656EA225CCBC41511C8DEB37BC17C33258323E6C90DF5F7A19161EAF92CEB2566171C608180000015000001F40000000081C_");
        // external message in BoC format
        assertThat(Utils.bytesToHex(msg.toCell().toBoc(true)).toUpperCase()).isEqualTo("B5EE9C724101020100A90001DF88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D802738F80EEA52936E75BBFA1525E8037940497F143C704F0EE6AE8AF656EA225CCBC41511C8DEB37BC17C33258323E6C90DF5F7A19161EAF92CEB2566171C608180000015000001F40000000081C010068420012C72A4B1C534C0572E9E3B1C157E9FA79971A2416D7E1BA8F19AC2C4E46F006A1DCD650000000000000000000000000000020855187");
    }

    /**
     * >fift -s wallet-v3.fif new-wallet 0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d 42 1 1 -t 1000 -C gift
     */
    @Test
    public void testCreateTransferMessageWalletV3R2WithBounceAndComment() {
        byte[] secretKey = Utils.hexToSignedBytes("F182111193F30D79D517F2339A1BA7C25FDF6C52142F0F2C1D960A1F1D65E1E4");
        TweetNaclFast.Signature.KeyPair keyPair = TweetNaclFast.Signature.keyPair_fromSeed(secretKey);

        WalletV3R2 contract = WalletV3R2.builder()
                .walletId(42)
                .wc(0)
                .keyPair(keyPair)
                .build();

        WalletV3Config config = WalletV3Config.builder()
                .destination(Address.of("0:258e549638a6980ae5d3c76382afd3f4f32e34482dafc3751e3358589c8de00d"))
                .walletId(42)
                .seqno(1L)
                .amount(Utils.toNano(1))
                .bounce(true)
                .validUntil(1000)
                .body(MsgUtils.createTextMessageBody("gift"))
                .build();

        Message msg = contract.prepareExternalMsg(config);
        // external message for serialization
        assertThat(msg.toCell().bitStringToHex()).isEqualTo("88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D80565D22D9452A21AECE10EA776A94032D2C084346971AB06EAE7D464BAC1266D23BAD143B9C79E2D878BD8CD19450AE7550F6BE28D27B988A95D34330CBD1AB8300000015000001F40000000081C_");
        // external message in BoC format
        assertThat(Utils.bytesToHex(msg.toCell().toBoc(true)).toUpperCase()).isEqualTo("B5EE9C724101020100B10001DF88005A537F40E3918C5F467316CC35085CC1E09971522BF67CE93DEF8D882687C2D80565D22D9452A21AECE10EA776A94032D2C084346971AB06EAE7D464BAC1266D23BAD143B9C79E2D878BD8CD19450AE7550F6BE28D27B988A95D34330CBD1AB8300000015000001F40000000081C010078620012C72A4B1C534C0572E9E3B1C157E9FA79971A2416D7E1BA8F19AC2C4E46F006A1DCD650000000000000000000000000000000000000676966741AA6F393");
    }
}
