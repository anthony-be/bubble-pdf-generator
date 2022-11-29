package be.cocoding.bubblepdf.parser;

import be.cocoding.bubblepdf.model.PdfRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static be.cocoding.bubblepdf.model.PdfRequestWrapper.sampleModelWithMetadata;
import static org.junit.jupiter.api.Assertions.*;

class RequestJsonParserTest {

    @Test
    public void parseJson_withNull() {
        assertNull(RequestJsonParser.parseJson(null));
    }

    @Test
    public void parseJson_withEmpty() {
        assertNull(RequestJsonParser.parseJson(""));
    }

    @Test
    public void parseJson_withBlank() {
        assertNull(RequestJsonParser.parseJson("    "));
    }

    @Test
    public void parseJsonWithMetadata() throws IOException {
        String json = sampleJsonWithMetadata();
        PdfRequestWrapper actual = RequestJsonParser.parseJson(json);

        assertNotNull(actual);
        assertEquals(sampleModelWithMetadata(), actual);

    }

    private String sampleJsonWithMetadata() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:/sample-request-with-bucket.json");
        return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }



}