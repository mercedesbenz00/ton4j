package org.ton.java.smartcontract.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.address.Address;
import org.ton.java.cell.Cell;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
public class NftCollectionConfig implements WalletConfig {
    long seqno;
    byte mode;
    long createdAt;
    Address destination;
    BigInteger amount;
    Cell body;
}
