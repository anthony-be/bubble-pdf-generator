package be.cocoding.bubblepdf.web.controller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class PdfControllerTest {

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        final int memSize = 20 * 1024 * 1024;

        client = MockMvcWebTestClient.bindToController(new PdfController())
                .configureClient()
                    .baseUrl("/api/pdf")
                    .exchangeStrategies(ExchangeStrategies.builder()
                            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(memSize))
                            .build())
                .build();
    }

    @Test
    void createPdf_WithWrongHttpMethod() {
        client.put()
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectBody().isEmpty();
    }

    @Test
    void createPdf_WithEmptyPayload() {
        client.post()
                .body(BodyInserters.empty())
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody().isEmpty();
    }

    @Test
    void createPdf_WithInvalidJsonPayload() {
        String invalidJson = "{ invalid-key : invalid value with space not wrapped in double quotes }";
        client.post().bodyValue(invalidJson).exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody().isEmpty();
    }

    @Test
    void createPdf() throws IOException {
        String validJson = sampleJson();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().cacheControl(CacheControl.noStore().mustRevalidate().cachePrivate())
//                .expectBody().consumeWith(this::storeInFile)
        ;
    }

    private String sampleJson() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:/sample-request-with-images.json");
        return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

//    private void storeInFile(EntityExchangeResult<byte[]> result) {
//        try {
//            Path tempFile = Files.createTempFile("PdfControllerTest", ".pdf");
//            System.out.println("Test output in file: " + tempFile.toAbsolutePath());
//            Files.write(tempFile, result.getResponseBody());
//        } catch (IOException e) {
//            sneakyThrow(e);
//        }
//    }
}