package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;
import org.ton.java.cell.CellSlice;

import java.math.BigInteger;

import static java.util.Objects.isNull;

@Builder
@Getter
@Setter
@ToString
/**
 ext_in_msg_info$10
 src:MsgAddressExt
 dest:MsgAddressInt
 import_fee:Grams - default zero
 = CommonMsgInfo;

 import_fee - default BigInteger.ZERO
 */
public class ExternalMessageInfo implements CommonMsgInfo {
    long magic;
    MsgAddressExt srcAddr;
    MsgAddressInt dstAddr;
    BigInteger importFee;

    private String getMagic() {
        return Long.toHexString(magic);
    }

    public Cell toCell() {
        CellBuilder result = CellBuilder.beginCell()
                .storeUint(0b10, 2)
                .storeSlice(CellSlice.beginParse(srcAddr.toCell()))
                .storeSlice(CellSlice.beginParse(dstAddr.toCell()))
                .storeCoins(isNull(importFee) ? BigInteger.ZERO : importFee);
        return result.endCell();
    }

    public static ExternalMessageInfo deserialize(CellSlice cs) {
        long magic = cs.loadUint(2).intValue();
        assert (magic == 0b10) : "ExternalMessage: magic not equal to 0b10, found 0b" + Long.toBinaryString(magic);
        return ExternalMessageInfo.builder()
                .magic(magic)
                .srcAddr(MsgAddressExt.deserialize(cs))
                .dstAddr(MsgAddressInt.deserialize(cs))
                .importFee(cs.loadCoins())
                .build();
    }
}
