package be.cocoding.bubblepdf.download.strategy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.MinimalHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class HttpClient5Downloader implements DownloaderStrategy {

    @Override
    public void download(String uri, Path dstPath) throws IOException {
        URL url = new URL(uri);
        MinimalHttpClient httpclient = HttpClients.createMinimal();
        try {
            HttpGet get = new HttpGet(url.toURI());
            Path downloaded = httpclient.execute(get, new FileDownloadResponseHandler(dstPath));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(httpclient);
        }
    }

    static class FileDownloadResponseHandler implements HttpClientResponseHandler<Path> {

        private final Path target;

        public FileDownloadResponseHandler(Path target) {
            this.target = target;
        }

        @Override
        public Path handleResponse(ClassicHttpResponse response) throws IOException {
            InputStream source = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(source, this.target.toFile());
            return this.target;
        }

    }
}
