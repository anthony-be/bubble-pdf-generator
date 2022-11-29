package be.cocoding.bubblepdf.storage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

@Ignore("Manual execution only as test needs Google Cloud authentication configuration")
public class GoogleCloudStoreTest {

    // To authenticate to Google Cloud, use either :
    // * gcloud client on your workstation with your authentication (local development)
    // * Use environment variable GOOGLE_APPLICATION_CREDENTIALS pointing to JSON private key file (hosting)

    private GoogleCloudStore store;

    @Before
    public void setUp() {
        assertNotNull("Credentials environment variable should be defined", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        store = new GoogleCloudStore();
    }

    @Test
    public void store() {


        store.store("test-pdf-32", "anthony-test.pdf", "Hello World !".getBytes(StandardCharsets.UTF_8));
    }
}