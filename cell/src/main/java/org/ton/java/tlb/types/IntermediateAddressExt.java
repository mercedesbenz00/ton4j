package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.Cell;
import org.ton.java.cell.CellBuilder;

import java.math.BigInteger;

/**
 * interm_addr_ext$11 workchain_id:int32 addr_pfx:uint64 = IntermediateAddress;
 */
@Builder
@Getter
@Setter
@ToString
public class IntermediateAddressExt implements IntermediateAddress {
    long workchainId;
    BigInteger addrPfx;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeBit(true)
                .storeBit(true)
                .storeUint(workchainId, 32)
                .storeUint(addrPfx, 64)
                .endCell();
    }
}
