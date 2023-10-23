package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;
import org.ton.java.tlb.loader.Tlb;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
/**
 * interm_addr_regular$0 use_dest_bits:(#<= 96) = IntermediateAddress;
 * interm_addr_simple$10 workchain_id:int8 addr_pfx:uint64 = IntermediateAddress;
 * interm_addr_ext$11 workchain_id:int32 addr_pfx:uint64 = IntermediateAddress;
 *
 * msg_envelope#4
 *   cur_addr:IntermediateAddress
 *   next_addr:IntermediateAddress
 *   fwd_fee_remaining:Grams
 *   msg:^(Message Any) = MsgEnvelope;
 */
public class MsgEnvelope {
    int magic;
    IntermediateAddress currAddr;
    IntermediateAddress nextAddr;
    BigInteger fwdFeeRemaining;
    Message msg;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeUint(4, 4) //magic
                .storeSlice(CellSlice.beginParse(Tlb.save(IntermediateAddress.class, currAddr)))
                .storeSlice(CellSlice.beginParse(Tlb.save(IntermediateAddress.class, nextAddr)))
                .storeCoins(fwdFeeRemaining)
                .storeRef(Tlb.save(Message.class, msg))
                .endCell();
    }
}
