package be.cocoding.bubblepdf.storage.cloud.google.credentials;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Implementation of {@link CredentialsProvider} that retrieve the credentials key from a Secret stored in
 * Google Cloud Platform Secret Manager.
 * <p/>
 * The latest version of the secret is used to get the actual value of the secret.
 * <p/>
 * In order to retrieve the secret, two information must be specified to the provider :
 * <ul>
 *     <li>projectId : The identifier number of the project in which the secret is stored</li>
 *     <li>secretName : The name of the secret to use</li>
 * </ul>
 * <p/>
 * By default, the provider uses default {@link SecretManagerServiceSettings} settings for the creation of {@link SecretManagerServiceClient}.<br/>
 * In case you need to fine tune these settings, you can create a settings instance yourself and pass it with the correct
 * provider constructor.
 *
 * @see SecretManagerServiceClient
 * @see SecretManagerServiceClient#accessSecretVersion(SecretVersionName)
 * @see SecretManagerServiceSettings
 * @see SecretManagerServiceSettings#newBuilder()
 * @see GoogleCredentials
 */
public class SecretCredentialsProviderImpl implements CredentialsProvider {

    private static final Logger logger = LoggerFactory.getLogger(SecretCredentialsProviderImpl.class);
    private static final String LATEST_VERSION = "latest";

    private final String projectId;
    private final String secretName;

    private final SecretManagerServiceSettings settings;

    @SneakyThrows(IOException.class)
    public SecretCredentialsProviderImpl(String projectId, String secretName) {
        this(projectId, secretName, SecretManagerServiceSettings.newBuilder().build());
    }

    public SecretCredentialsProviderImpl(String projectId, String secretName, SecretManagerServiceSettings settings) {
        this.projectId = requireNonNull(projectId, "projectId parameter is required");
        this.secretName = requireNonNull(secretName, "secretName parameter is required");
        this.settings = requireNonNull(settings, "settings parameter is required");
    }

    @Override
    public Credentials getCredentials() {
        logger.info("Retrieving Credentials from GCP Secret Manager. ProjectID: {} - Secret Name: {}", projectId, secretName);
        Credentials secretCredentials;
        try(SecretManagerServiceClient client = SecretManagerServiceClient.create(settings)){
            SecretVersionName svn = SecretVersionName.of(projectId, secretName, LATEST_VERSION);
            AccessSecretVersionResponse accessSecretVersionResponse = client.accessSecretVersion(svn);
            ByteString data = accessSecretVersionResponse.getPayload().getData();
            secretCredentials = GoogleCredentials.fromStream(new ByteArrayInputStream(data.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to get Secret value", e);
        }
        return secretCredentials;
    }

}
