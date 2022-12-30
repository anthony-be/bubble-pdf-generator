package be.cocoding.bubblepdf.storage.cloud.google.credentials;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;

/**
 * Implementation of {@link CredentialsProvider} that uses Application Default credentials.
 * <p/>
 * This provider uses {@link Credentials} instance returned by {@link GoogleCredentials#getApplicationDefault()}
 *
 * @see GoogleCredentials#getApplicationDefault()
 */
public class ApplicationDefaultCredentialsProviderImpl implements CredentialsProvider {

    private static final ApplicationDefaultCredentialsProviderImpl instance = new ApplicationDefaultCredentialsProviderImpl();

    private ApplicationDefaultCredentialsProviderImpl(){ }

    public static ApplicationDefaultCredentialsProviderImpl getInstance(){
        return instance;
    }

    @Override
    public Credentials getCredentials() {
        try {
            return GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
