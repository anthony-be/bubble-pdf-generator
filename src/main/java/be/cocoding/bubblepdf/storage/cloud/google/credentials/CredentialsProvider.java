package be.cocoding.bubblepdf.storage.cloud.google.credentials;

import com.google.auth.Credentials;

@FunctionalInterface
public interface CredentialsProvider {

    /**
     * Returns the {@link Credentials} to be used for authentication on a GCP service
     *
     * @return a {@link Credentials} instance
     */
    Credentials getCredentials();
}
