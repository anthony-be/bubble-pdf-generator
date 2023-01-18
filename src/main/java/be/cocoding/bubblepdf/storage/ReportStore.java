package be.cocoding.bubblepdf.storage;

import java.io.File;

public interface ReportStore {

    void store(String bucketName, String filename, byte[] reportContent);
    void store(String bucketName, String filename, File reportFile);
}
