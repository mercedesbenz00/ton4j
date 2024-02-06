package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
/**
 * msg_import_imm$011 in_msg:^MsgEnvelope transaction:^Transaction fwd_fee:Grams = InMsg;
 */
public class InMsgImportImm implements InMsg {
    MsgEnvelope inMsg;
    Transaction transaction;
    BigInteger fwdFee;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b011, 3)
                .storeRef(inMsg.toCell())
                .storeRef(transaction.toCell())
                .storeCoins(fwdFee)
                .endCell();
    }

    public static InMsgImportImm deserialize(CellSlice cs) {
        return InMsgImportImm.builder()
                .inMsg(MsgEnvelope.deserialize(CellSlice.beginParse(cs.loadRef())))
                .transaction(Transaction.deserialize(CellSlice.beginParse(cs.loadRef())))
                .fwdFee(cs.loadCoins())
                .build();
    }
}
