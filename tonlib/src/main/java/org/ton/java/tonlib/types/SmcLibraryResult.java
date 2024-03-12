package org.ton.java.tonlib.types;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
public class SmcLibraryResult implements Serializable {
    @SerializedName("@type")
    final String type = "smc.libraryResult";
    List<SmcLibraryEntry> result;
}

