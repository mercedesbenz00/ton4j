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
 * tr_phase_compute_vm$1 success:Bool msg_state_used:Bool
 *   account_activated:Bool gas_fees:Grams
 *   ^[ gas_used:(VarUInteger 7)
 *   gas_limit:(VarUInteger 7) gas_credit:(Maybe (VarUInteger 3))
 *   mode:int8 exit_code:int32 exit_arg:(Maybe int32)
 *   vm_steps:uint32
 *   vm_init_state_hash:bits256 vm_final_state_hash:bits256 ]
 *   = TrComputePhase;
 */
public class ComputePhaseVM implements ComputePhase {
    int magic;
    boolean success;
    boolean msgStateUsed;
    boolean accountActivated;
    BigInteger gasFees;
    ComputePhaseVMDetails details;

    private String getMagic() {
        return Integer.toHexString(magic);
    }

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeBit(true)
                .storeBit(success)
                .storeBit(msgStateUsed)
                .storeBit(accountActivated)
                .storeCoins(gasFees)
                .storeSlice(CellSlice.beginParse(((ComputePhaseVMDetails) details).toCell()))
                .endCell();
    }

    public static ComputePhase deserialize(CellSlice cs) {
        return ComputePhaseVM.builder()
                .magic(1)
                .success(cs.loadBit())
                .msgStateUsed(cs.loadBit())
                .accountActivated(cs.loadBit())
                .gasFees(cs.loadCoins())
                .details(ComputePhaseVMDetails.deserialize(CellSlice.beginParse(cs.loadRef())))
                .build();
    }
}
