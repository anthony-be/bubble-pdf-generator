package be.cocoding.bubblepdf.download.strategy;

import be.cocoding.bubblepdf.storage.cloud.google.StorageServiceFactory;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;

import java.nio.file.Path;

public class GCPStorageDownloader implements DownloaderStrategy {

    private static final GCPStorageDownloader instance = new GCPStorageDownloader();

    public static GCPStorageDownloader getInstance(){
        return instance;
    }

    @Override
    public void download(String uri, Path dstPath) {

        BlobId blobId = BlobId.fromGsUtilUri(uri);
        Storage storage = StorageServiceFactory.getStorage();
        storage.downloadTo(blobId, dstPath);
    }

    public static boolean isGsutilUri(String uri){
        boolean isGsUri;
        try {
            BlobId.fromGsUtilUri(uri);
            isGsUri = true;
        } catch (IllegalArgumentException e) {
            isGsUri = false;
        }
        return isGsUri;
    }

    private GCPStorageDownloader(){

    }
}
