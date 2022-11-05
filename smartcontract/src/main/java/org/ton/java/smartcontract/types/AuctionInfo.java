package org.ton.java.smartcontract.types;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.ton.java.address.Address;

import java.math.BigInteger;

@Builder
@Getter
@ToString
public class AuctionInfo {
    Address maxBidAddress;
    BigInteger maxBidAmount;
    long auctionEndTime;
}
