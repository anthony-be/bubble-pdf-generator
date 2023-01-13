package be.cocoding.bubblepdf.download.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class URLStreamDownloader implements DownloaderStrategy {

    @Override
    public void download(String uri, Path dstPath) throws IOException {
        try(InputStream in = new URL(uri).openStream()){
            Files.copy(in, dstPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}
