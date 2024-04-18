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
 msg_export_deq_imm$100 out_msg:^MsgEnvelope
 reimport:^InMsg = OutMsg;
 */
public class OutMsgDeqImm implements OutMsg {
    int magic;
    MsgEnvelope msg;
    InMsg reimport;

    @Override
    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(0b100, 3)
                .storeRef(msg.toCell())
                .storeRef(reimport.toCell())
                .endCell();
    }

    public static OutMsgDeqImm deserialize(CellSlice cs) {
        return OutMsgDeqImm.builder()
                .magic(cs.loadUint(3).intValue())
                .msg(MsgEnvelope.deserialize(CellSlice.beginParse(cs.loadRef())))
                .reimport(InMsg.deserialize(CellSlice.beginParse(cs.loadRef())))
                .build();
    }
}
