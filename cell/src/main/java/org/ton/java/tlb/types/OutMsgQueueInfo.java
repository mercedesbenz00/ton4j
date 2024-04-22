package org.ton.java.tlb.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.cell.*;

@Builder
@Getter
@Setter
@ToString
/**
 // _ (HashmapAugE 352 EnqueuedMsg uint64) = OutMsgQueue;
 // _ (HashmapE 96 ProcessedUpto) = ProcessedInfo; // key is [ shard:uint64 mc_seqno:uint32 ]
 // _ (HashmapE 320 IhrPendingSince) = IhrPendingInfo;
 */

public class OutMsgQueueInfo {
    TonHashMapAugE outMsgQueue;
    TonHashMapE processedInfo;
    TonHashMapE ihrPendingInfo;

    public Cell toCell() {
        return CellBuilder.beginCell()
                .storeDict(outMsgQueue.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 352).endCell().bits,
                        v -> CellBuilder.beginCell().storeCell(((EnqueuedMsg) v).toCell()),
                        e -> CellBuilder.beginCell().storeUint((Long) e, 64).endCell().bits,
                        (fk, fv) -> CellBuilder.beginCell().storeUint(0, 1))) // todo
                .storeDict(processedInfo.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 96).endCell().bits,
                        v -> CellBuilder.beginCell().storeCell(((ProcessedUpto) v).toCell()).endCell()))
                .storeDict(ihrPendingInfo.serialize(
                        k -> CellBuilder.beginCell().storeUint((Long) k, 320).endCell().bits,
                        v -> CellBuilder.beginCell().storeUint((Long) v, 64).endCell()))
                .endCell();
    }

    public static OutMsgQueueInfo deserialize(CellSlice cs) {
        return OutMsgQueueInfo.builder()
                .outMsgQueue(
                        cs.loadDictAugE(352,
                                k -> k.readInt(352),
                                v -> EnqueuedMsg.deserialize(v),
                                e -> CellSlice.beginParse(e).loadUint(64)
                        ))
                .processedInfo(
                        cs.loadDictE(96,
                                k -> k.readInt(96),
                                v -> ProcessedUpto.deserialize(CellSlice.beginParse(v))
                        ))
                .ihrPendingInfo(
                        cs.loadDictE(320,
                                k -> k.readInt(320),
                                v -> CellSlice.beginParse(v).loadUint(64)
                        ))
                .build();
    }
}
