package be.cocoding.bubblepdf.storage;

public interface ReportStore {

    void store(String bucketName, String filename, byte[] reportContent);
}
