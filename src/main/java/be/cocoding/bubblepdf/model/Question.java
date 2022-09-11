package be.cocoding.bubblepdf.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Jacksonized
@Builder
public class Question {

    private String title;

    private List<Element> elements;

    public boolean hasData(){
        return StringUtils.hasLength(title)
                || Optional.ofNullable(elements).orElseGet(Collections::emptyList).stream()
                .map(Element::hasData).reduce(false, Boolean::logicalOr);
    }
}

