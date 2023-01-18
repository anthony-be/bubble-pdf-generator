package be.cocoding.test.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileUtils {

    public static File downloadToTempFile(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        InputStream inputStream = url.openStream();
        File tempFile = createTempFileWithDeleteOnExist("Test_downloaded_", ".tmp");
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return tempFile;
    }

    public static File createTempFileWithDeleteOnExist(String prefix, String suffix) throws IOException {
        File tempFile = Files.createTempFile(prefix, suffix).toFile();
        tempFile.deleteOnExit();
        return tempFile;
    }

    private FileUtils(){}
}
