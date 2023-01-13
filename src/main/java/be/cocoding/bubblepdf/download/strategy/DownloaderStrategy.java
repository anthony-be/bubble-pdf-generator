package be.cocoding.bubblepdf.download.strategy;

import java.io.IOException;
import java.nio.file.Path;

public interface DownloaderStrategy {

    void download(String uri, Path dstPath) throws IOException ;

}
