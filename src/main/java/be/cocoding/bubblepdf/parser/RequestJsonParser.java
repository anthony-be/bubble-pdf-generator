package be.cocoding.bubblepdf.parser;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

public class RequestJsonParser {

    public static PdfRequestWrapper parseJson(final String json){
        if(!StringUtils.hasText(json)){
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, PdfRequestWrapper.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse json value", e);
        }
    }

}
