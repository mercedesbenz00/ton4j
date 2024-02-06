package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

@Builder
@Getter
@Setter
@ToString
/**
 * trans_split_install$0101
 *   split_info:SplitMergeInfo
 *   prepare_transaction:^Transaction
 *   installed:Bool = TransactionDescr;
 */
public class TransactionDescriptionSplitInstall {
    int magic;
    SplitMergeInfo splitInfo;
    Transaction prepareTransaction;
    boolean installed;

    private String getMagic() {
        return Long.toBinaryString(magic);
    }

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b0101, 4)
                .storeCell(splitInfo.toCell())
                .storeRef(prepareTransaction.toCell())
                .storeBit(installed)
                .endCell();
    }

    public static TransactionDescriptionSplitInstall deserialize(CellSlice cs) {
        long magic = cs.loadUint(4).intValue();
        assert (magic == 0b0101) : "TransactionDescriptionSplitInstall: magic not equal to 0b0101, found 0x" + Long.toHexString(magic);

        return TransactionDescriptionSplitInstall.builder()
                .magic(0b0101)
                .splitInfo(SplitMergeInfo.deserialize(cs))
                .prepareTransaction(Transaction.deserialize(CellSlice.beginParse(cs.loadRef())))
                .installed(cs.loadBit())
                .build();
    }
}
