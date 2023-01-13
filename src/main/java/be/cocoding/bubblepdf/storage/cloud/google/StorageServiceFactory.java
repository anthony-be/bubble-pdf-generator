package be.cocoding.bubblepdf.storage.cloud.google;

import be.cocoding.bubblepdf.storage.cloud.google.credentials.ApplicationDefaultCredentialsProviderImpl;
import be.cocoding.bubblepdf.storage.cloud.google.credentials.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.storage.HttpStorageOptions;
import com.google.cloud.storage.Storage;

public class StorageServiceFactory {

    public static Storage getStorage() {
        return getStorage(ApplicationDefaultCredentialsProviderImpl.getInstance());
    }

    public static Storage getStorage(CredentialsProvider credentialsProvider) {
        Credentials credentials = credentialsProvider.getCredentials();
        HttpStorageOptions httpStorageOptions = HttpStorageOptions.newBuilder().setCredentials(credentials).build();
        return httpStorageOptions.getService();
    }

}
