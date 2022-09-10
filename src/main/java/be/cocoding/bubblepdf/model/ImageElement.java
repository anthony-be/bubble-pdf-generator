package be.cocoding.bubblepdf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@AllArgsConstructor
@Jacksonized
@Builder
public class ImageElement implements Element {

    public static final String TYPE = "image";

    @JsonProperty("value")
    private String base64ImageContent;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getValue() {
        return base64ImageContent;
    }
}
