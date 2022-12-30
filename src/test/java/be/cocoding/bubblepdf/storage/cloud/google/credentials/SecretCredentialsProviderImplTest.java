package be.cocoding.bubblepdf.storage.cloud.google.credentials;

import com.google.auth.Credentials;
import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.stub.GrpcSecretManagerServiceStub;
import com.google.cloud.secretmanager.v1.stub.SecretManagerServiceStub;
import com.google.cloud.secretmanager.v1.stub.SecretManagerServiceStubSettings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore("For manual testing of integration with Secret Manager of GCP")
public class SecretCredentialsProviderImplTest {

    private SecretCredentialsProviderImpl provider;

    private SecretManagerServiceStub stub;

    // Adapt these values to match what you want to test
    private final String projectId = "000000000000";
    private final String secretName = "my-secret";

    @Before
    public void setUp() throws Exception {
        provider = new SecretCredentialsProviderImpl(projectId, secretName);
    }

    @Test
    public void getCredentials() {
        Credentials actual = provider.getCredentials();
        assertNotNull(actual);
    }
}