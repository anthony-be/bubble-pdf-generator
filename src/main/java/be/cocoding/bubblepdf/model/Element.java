package be.cocoding.bubblepdf.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextElement.class, name = TextElement.TYPE),
        @JsonSubTypes.Type(value = ImageElement.class, name = ImageElement.TYPE)
})
public interface Element {

    String getType();

    String getValue();

}
