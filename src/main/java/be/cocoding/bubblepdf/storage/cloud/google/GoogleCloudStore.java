package be.cocoding.bubblepdf.storage.cloud.google;

import be.cocoding.bubblepdf.storage.ReportStore;
import be.cocoding.bubblepdf.storage.cloud.google.credentials.ApplicationDefaultCredentialsProviderImpl;
import be.cocoding.bubblepdf.storage.cloud.google.credentials.CredentialsProvider;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

@Component
public class GoogleCloudStore implements ReportStore, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStore.class);
    private static final String GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";

    private final CredentialsProvider credentialsProvider;

    public GoogleCloudStore(){
        this(ApplicationDefaultCredentialsProviderImpl.getInstance());
    }

    public GoogleCloudStore(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = requireNonNull(credentialsProvider, "credentialsProvider parameter is required");
    }

    @Override
    public void store(String bucketName, String filename, byte[] reportContent) {
        logger.info("Storing report file {} under bucket {}. Size of report: {} bytes", filename, bucketName, reportContent.length);
        Storage storage = getStorageInstance();
        BlobId blobId = BlobId.of(bucketName, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(MediaType.APPLICATION_PDF_VALUE).build();
        storage.create(blobInfo, reportContent);
        logger.info("Uploaded report: " + filename);
    }

    private Storage getStorageInstance() {
//        Credentials credentials = credentialsProvider.getCredentials();
//        HttpStorageOptions httpStorageOptions = HttpStorageOptions.newBuilder().setCredentials(credentials).build();
//        return httpStorageOptions.getService();
        return StorageServiceFactory.getStorage(credentialsProvider);
    }

    @Override
    public void afterPropertiesSet() {
        String google_application_credentials = System.getenv(GOOGLE_APPLICATION_CREDENTIALS);
        if (google_application_credentials == null) {
            logger.warn("No environment variable {} found. This environment variable is required to authenticate to Google Cloud",
                    GOOGLE_APPLICATION_CREDENTIALS);
        } else {
            logger.info("Environment variable {} found with following value: {}",
                    GOOGLE_APPLICATION_CREDENTIALS, google_application_credentials);
        }
    }
}
