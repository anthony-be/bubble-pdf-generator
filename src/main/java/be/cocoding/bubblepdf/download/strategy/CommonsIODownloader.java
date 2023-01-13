package be.cocoding.bubblepdf.download.strategy;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class CommonsIODownloader implements DownloaderStrategy {

    private final int CONNECT_TIMEOUT = 15 * 1000; // 15 sec
    private final int READ_TIMEOUT = 15 * 1000; // 15 sec

    @Override
    public void download(String uri, Path dstPath) throws IOException {
        URL url = new URL(uri);
        FileUtils.copyURLToFile(url, dstPath.toFile(), CONNECT_TIMEOUT, READ_TIMEOUT);
    }
}
