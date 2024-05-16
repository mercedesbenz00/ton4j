package org.ton.java.smartcontract.wallet.v3;

import org.ton.java.address.Address;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.types.WalletCodes;
import org.ton.java.smartcontract.wallet.Options;
import org.ton.java.tonlib.Tonlib;
import org.ton.java.tonlib.types.RunResult;
import org.ton.java.tonlib.types.TvmStackEntryNumber;

import static java.util.Objects.isNull;

public class WalletV3ContractR2 extends WalletV3ContractBase {

    /**
     * @param options Options
     */
    public WalletV3ContractR2(Options options) {
        super(options);
        options.code = CellBuilder.beginCell().fromBoc(WalletCodes.V3R2.getValue()).endCell();
        if (isNull(options.walletId)) {
            options.walletId = 698983191 + options.wc;
        }
    }

    @Override
    public String getName() {
        return "V3R2";
    }

    @Override
    public Cell createCodeCell() {
        return CellBuilder.beginCell().
                fromBoc(WalletCodes.V3R2.getValue()).
                endCell();
    }

    public String getPublicKey(Tonlib tonlib) {

        Address myAddress = this.getAddress();
        RunResult result = tonlib.runMethod(myAddress, "get_public_key");

        if (result.getExit_code() != 0) {
            throw new Error("method get_public_key, returned an exit code " + result.getExit_code());
        }

        TvmStackEntryNumber publicKeyNumber = (TvmStackEntryNumber) result.getStack().get(0);
        return publicKeyNumber.getNumber().toString(16);
    }
}
