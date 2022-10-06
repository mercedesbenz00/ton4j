package org.ton.java.tonlib.queries;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class SendRawMessageQuery {
    @SerializedName(value = "@type")
    final String type = "raw.sendMessage";
    String body;
}
