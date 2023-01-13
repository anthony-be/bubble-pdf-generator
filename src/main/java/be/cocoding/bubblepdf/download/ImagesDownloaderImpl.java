package be.cocoding.bubblepdf.download;

import be.cocoding.bubblepdf.download.strategy.DownloaderFactory;
import be.cocoding.bubblepdf.download.strategy.DownloaderStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ImagesDownloaderImpl implements ImagesDownloader {

    private final String downloadStrategy;

    public ImagesDownloaderImpl(){
        this(DownloaderFactory.Strategy.URL_STREAM_CHANNEL.name());
    }
    public ImagesDownloaderImpl(String downloadStrategy){
        this.downloadStrategy = requireNonNull(downloadStrategy);
    }

    @Override
    public Path download(String imageUri) {
        DownloaderStrategy downloaderStrategy = getDownloaderStrategy(imageUri);
        Path dstPath;
        try {
            dstPath = Files.createTempFile("ImageDownload_", ".tmp");
            dstPath.toFile().deleteOnExit();
            downloaderStrategy.download(imageUri, dstPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download image", e);
        }
        return dstPath;
    }

    @Override
    public Map<String, Path> download(Collection<String> imageUris) {
        Map<String, Path> downloadedImages = new HashMap<>(imageUris.size());
        for(String imageUri : imageUris){
            Path path = download(imageUri);
            downloadedImages.put(imageUri, path);
        }
        return downloadedImages;
    }


    private DownloaderStrategy getDownloaderStrategy(String imageUri){
        return DownloaderFactory.getDownloader(imageUri, downloadStrategy);
    }
}
