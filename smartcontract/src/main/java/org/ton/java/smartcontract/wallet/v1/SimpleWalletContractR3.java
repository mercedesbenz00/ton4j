package org.ton.java.smartcontract.wallet.v1;

import org.ton.java.address.Address;
import org.ton.java.cell.Cell;
import org.ton.java.smartcontract.types.ExternalMessage;
import org.ton.java.smartcontract.wallet.Options;
import org.ton.java.smartcontract.wallet.WalletContract;
import org.ton.java.tonlib.Tonlib;
import org.ton.java.tonlib.types.RunResult;
import org.ton.java.tonlib.types.TvmStackEntryNumber;
import org.ton.java.utils.Utils;

import java.math.BigInteger;

import static java.util.Objects.isNull;

public class SimpleWalletContractR3 implements WalletContract {

    public static final String V1_R3_CODE_HEX = "B5EE9C7241010101005F0000BAFF0020DD2082014C97BA218201339CBAB19C71B0ED44D0D31FD70BFFE304E0A4F260810200D71820D70B1FED44D0D31FD3FFD15112BAF2A122F901541044F910F2A2F80001D31F3120D74A96D307D402FB00DED1A4C8CB1FCBFFC9ED54B5B86E42";
    Options options;
    Address address;

    /**
     * @param options Options
     */
    public SimpleWalletContractR3(Options options) {
        this.options = options;
        options.code = Cell.fromBoc(V1_R3_CODE_HEX);
    }

    @Override
    public String getName() {
        return "simpleR3";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public Address getAddress() {
        if (address == null) {
            return (createStateInit()).address;
        }
        return address;
    }

    /**
     * Get current seqno
     *
     * @return long
     */
    public long getSeqno(Tonlib tonlib) {

        Address myAddress = getAddress();

        int i = 0;
        TvmStackEntryNumber seqno = null;
        do {
            try {
                RunResult result = tonlib.runMethod(myAddress, "seqno");
                seqno = (TvmStackEntryNumber) result.getStackEntry().get(0);

            } catch (Exception e) {
                //todo
            }
            if (++i > 10) {
                throw new Error("cannot get seqno from contract " + myAddress.toString(true, true, true));
            }
            try {
                if (isNull(seqno)) {
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                //todo
            }


        } while (isNull(seqno));
        return seqno.getNumber().longValue();
    }

    public boolean sendTonCoins(Tonlib tonlib, byte[] secretKey, Address destinationAddress, BigInteger amount) {
        try {
            long seqno = getSeqno(tonlib);
            ExternalMessage msg = createTransferMessage(secretKey, destinationAddress, amount, seqno);
            tonlib.sendRawMessage(Utils.bytesToBase64(msg.message.toBoc(false)));
            return true;
        } catch (Throwable e) {
            System.err.println("Error sending TonCoins to " + destinationAddress.toString() + ". " + e.getMessage());
            return false;
        }
    }
}
