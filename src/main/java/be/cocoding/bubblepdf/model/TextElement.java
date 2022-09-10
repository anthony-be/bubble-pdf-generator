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
public class TextElement implements Element {

    public static final String TYPE = "text";

    @JsonProperty("value")
    private String textValue;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getValue() {
        return textValue;
    }
}
