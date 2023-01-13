package be.cocoding.bubblepdf.download.strategy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public class URLStreamChannelDownloader implements DownloaderStrategy {
    @Override
    public void download(String uri, Path dstPath) throws IOException {
        try (InputStream in = new URL(uri).openStream();
            FileOutputStream fileOutputStream = new FileOutputStream(dstPath.toFile())) {

            ReadableByteChannel readableByteChannel = Channels.newChannel(in);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
