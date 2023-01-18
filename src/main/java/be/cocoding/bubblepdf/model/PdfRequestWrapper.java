package be.cocoding.bubblepdf.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@Jacksonized @Builder
public class PdfRequestWrapper {

    private List<Question> questions;

    private Metadata metadata;

    public boolean hasData(){
        if(CollectionUtils.isEmpty(questions)){
            return false;
        }

        return questions.stream().map(Question::hasData)
                .reduce(false, Boolean::logicalOr);
    }

}
