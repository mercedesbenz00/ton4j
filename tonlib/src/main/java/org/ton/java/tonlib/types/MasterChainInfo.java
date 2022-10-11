package org.ton.java.tonlib.types;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class MasterChainInfo {

    @SerializedName("@type")
    final String type = "blocks.masterchainInfo";
    BlockIdExt last;
    String state_root_hash;
    BlockIdExt init;
}

