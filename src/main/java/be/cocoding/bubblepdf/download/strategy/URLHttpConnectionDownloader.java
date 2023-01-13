package be.cocoding.bubblepdf.download.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class URLHttpConnectionDownloader implements DownloaderStrategy {

    @Override
    public void download(String uri, Path dstPath) throws IOException {
        URL url = new URL(uri);
        URLConnection connection = url.openConnection();
        connection.connect();
        try (InputStream inputStream = connection.getInputStream()) {
            Files.copy(inputStream, dstPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
