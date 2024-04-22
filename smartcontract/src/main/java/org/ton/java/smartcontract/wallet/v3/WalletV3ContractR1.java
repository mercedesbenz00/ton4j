package org.ton.java.smartcontract.wallet.v3;

import org.ton.java.cell.CellBuilder;
import org.ton.java.smartcontract.types.WalletCodes;
import org.ton.java.smartcontract.wallet.Options;

import static java.util.Objects.isNull;

public class WalletV3ContractR1 extends WalletV3ContractBase {

    /**
     * @param options Options
     */
    public WalletV3ContractR1(Options options) {
        super(options);
        options.code = CellBuilder.beginCell().fromBoc(WalletCodes.V3R1.getValue()).endCell();
        if (isNull(options.walletId)) {
            options.walletId = 698983191 + options.wc;
        }
    }

    @Override
    public String getName() {
        return "V3R1";
    }
}
