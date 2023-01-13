package be.cocoding.bubblepdf.download;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public interface ImagesDownloader {

    Path download(String imageUri);

    Map<String, Path> download(Collection<String> imageUris);
}
