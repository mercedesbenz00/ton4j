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
 msg_export_deq$1100 out_msg:^MsgEnvelope
 import_block_lt:uint63 = OutMsg;
 */
public class OutMsgDeq implements OutMsg {
    int magic;
    MsgEnvelope outMsg;
    BigInteger importBlockLt;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b1100, 4)
                .storeRef(outMsg.toCell())
                .storeUint(importBlockLt, 63)
                .endCell();
    }

    public static OutMsgDeq deserialize(CellSlice cs) {
        return OutMsgDeq.builder()
                .magic(cs.loadUint(4).intValue())
                .outMsg(MsgEnvelope.deserialize(CellSlice.beginParse(cs.loadRef())))
                .importBlockLt(cs.loadUint(63))
                .build();
    }
}
