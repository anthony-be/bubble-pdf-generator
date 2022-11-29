package be.cocoding.bubblepdf.web.controller;

import be.cocoding.bubblepdf.storage.ReportStore;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class PdfControllerTest {

    private WebTestClient client;

    private ReportStore mockReportStore;

    @BeforeEach
    void setUp() {
        final int memSize = 20 * 1024 * 1024;
        mockReportStore = Mockito.mock(ReportStore.class);
        PdfController pdfController = new PdfController(mockReportStore);

        client = MockMvcWebTestClient.bindToController(pdfController)
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
        String validJson = sampleJsonWithImage();
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

    @Test
    void createPdfWithBucketAndPdfFileId() throws IOException {
        Mockito.doNothing().when(mockReportStore).store(eq("bubble-kindred-tales-test"), eq("gdcerfthb34dr78"), any());

        String validJson = sampleJsonWithBucket();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NO_CONTENT);

        verify(mockReportStore).store(eq("bubble-kindred-tales-test"), eq("gdcerfthb34dr78"), any());
    }

    @Test
    void createPdfWithNoBucket() throws IOException {

        String validJson = sampleJsonWithNoBucket();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void createPdfWithEmptyBucket() throws IOException {

        String validJson = sampleJsonWithEmptyBucket();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void createPdfWithBlankBucket() throws IOException {

        String validJson = sampleJsonWithBlankBucket();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void createPdfWithNoFileId() throws IOException {

        String validJson = sampleJsonWithNoFileId();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void createPdfWithEmptyFileId() throws IOException {

        String validJson = sampleJsonWithEmptyFileId();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void createPdfWithBlankFileId() throws IOException {

        String validJson = sampleJsonWithBlankFileId();
        client.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK);

    }

    private String sampleJsonWithImage() throws IOException {
        return getSampleJson("sample-request-with-images.json");
    }

    private String sampleJsonWithBucket() throws IOException {
        return getSampleJson("sample-request-with-bucket.json");
    }

    private String sampleJsonWithNoBucket() throws IOException {
        return getSampleJson("sample-request-with-metadata-no-bucket.json");
    }

    private String sampleJsonWithEmptyBucket() throws IOException {
        return getSampleJson("sample-request-with-metadata-empty-bucket.json");
    }

    private String sampleJsonWithBlankBucket() throws IOException {
        return getSampleJson("sample-request-with-metadata-blank-bucket.json");
    }

    private String sampleJsonWithNoFileId() throws IOException {
        return getSampleJson("sample-request-with-metadata-no-fileid.json");
    }

    private String sampleJsonWithEmptyFileId() throws IOException {
        return getSampleJson("sample-request-with-metadata-empty-fileid.json");
    }

    private String sampleJsonWithBlankFileId() throws IOException {
        return getSampleJson("sample-request-with-metadata-blank-fileid.json");
    }

    private String getSampleJson(String sampleFileName) throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:/" + sampleFileName);
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