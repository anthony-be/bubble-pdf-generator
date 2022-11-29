package be.cocoding.bubblepdf.storage;

import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class GoogleCloudStore implements ReportStore, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStore.class);
    private static final String GOOGLE_APPLICATION_CREDENTIALS = "GOOGLE_APPLICATION_CREDENTIALS";

    public GoogleCloudStore() {
        logger.debug("Instantiating class...");
    }

    @Override
    public void store(String bucketName, String filename, byte[] reportContent) {
        logger.info("Storing report file {} under bucket {}. Size of report: {} bytes", filename, bucketName, reportContent.length);

        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(bucketName, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(MediaType.APPLICATION_PDF_VALUE).build();
        storage.create(blobInfo, reportContent);
        logger.info("Uploaded report: " + filename);
    }

    @Override
    public void afterPropertiesSet() {
        String google_application_credentials = System.getenv(GOOGLE_APPLICATION_CREDENTIALS);
        if(google_application_credentials == null){
            logger.warn("No environment variable {} found. This environment variable is required to authenticate to Google Cloud",
                    GOOGLE_APPLICATION_CREDENTIALS);
        }else {
            logger.info("Environment variable {} found with following value: {}",
                    GOOGLE_APPLICATION_CREDENTIALS, google_application_credentials);
        }
    }
}
