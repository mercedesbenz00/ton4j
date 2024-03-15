package org.ton.java.liteclient.api.block;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * OutMsgDescr — The description of all messages “exported” or “generated”
 * by the block (i.e., either messages generated by a transaction
 * included in the block, or transit messages with destination not belonging
 * to the current shardchain, forwarded from InMsgDescr).
 */
@Builder
@ToString
@Getter
public class OutMsgDescr implements Serializable {
    List<Leaf> leaf;
}
