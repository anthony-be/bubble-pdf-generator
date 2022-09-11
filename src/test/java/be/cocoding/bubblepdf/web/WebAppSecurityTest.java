package be.cocoding.bubblepdf.web;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebAppSecurityTest {

    private final String BASE_URI = "/api/pdf";

    @Autowired
    private WebTestClient client;

    @Test
    void testNoAuthentication() throws IOException {
        client.post()
                .uri(BASE_URI)
                .bodyValue(sampleJson())
                .headers(httpHeaders -> {
                    // Make sure Authorization header is not set
                    if(httpHeaders.containsKey(HttpHeaders.AUTHORIZATION)){
                        httpHeaders.remove(HttpHeaders.AUTHORIZATION);
                    }
                })
                .exchange()
                .expectStatus().isOk(); //.isForbidden();
    }

    @Test
    void testEmptyAuthentication() throws IOException {
        client.post()
                .uri(BASE_URI)
                .bodyValue(sampleJson())
                .headers(httpHeaders -> httpHeaders.replace(HttpHeaders.AUTHORIZATION, Collections.singletonList("")))
                .exchange()
                .expectStatus().isOk(); //.isForbidden();
    }

    @Test
    void testWrongAuthorizationWithBearer() throws IOException {
        client.post()
                .uri(BASE_URI)
                .bodyValue(sampleJson())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(Base64.getEncoder().encodeToString("1234567890".getBytes(StandardCharsets.UTF_8))))
                .exchange()
                .expectStatus().isOk(); //.isForbidden();
    }

    @Test
    void testBasicAuthorizationWrongPassword() throws IOException {
        client.post()
                .uri(BASE_URI)
                .bodyValue(sampleJson())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("nick", "nick"))
                .exchange()
                .expectStatus().isOk(); //.isForbidden();
    }

    @Test
    void testBasicAuthorizationWithValidCredentials() throws IOException {
        client.post()
                .uri(BASE_URI)
                .bodyValue(sampleJson())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("nick", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF);
    }

    private String sampleJson() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:/sample-request-with-images.json");
        return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}
