package org.ton.java.smartcontract.wallet;

import org.ton.java.cell.Cell;
import org.ton.java.address.Address;
import org.ton.java.smartcontract.types.Config;

import java.math.BigInteger;

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
    public Config config;
}
