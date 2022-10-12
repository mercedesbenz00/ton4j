package org.ton.java.smartcontract.wallet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.java.address.Address;
import org.ton.java.cell.Cell;
import org.ton.java.smartcontract.types.LockupConfig;
import org.ton.java.smartcontract.wallet.v4.SubscriptionInfo;

import java.math.BigInteger;

@Builder
@Getter
@Setter
@ToString
public class Options {
    public byte[] secretKey;
    public byte[] publicKey;
    public long wc;
    public Address address;
    public BigInteger amount;
    public Cell code;
    public long seqno;
    public Object payload;
    public int sendMode;
    public Cell stateInit;
    public Long walletId;
    public LockupConfig lockupConfig;
    public SubscriptionInfo subscriptionConfig;
}
