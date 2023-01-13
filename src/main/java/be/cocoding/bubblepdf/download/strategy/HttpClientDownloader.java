package be.cocoding.bubblepdf.download.strategy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public class HttpClientDownloader implements DownloaderStrategy {
    @Override
    public void download(String uri, Path dstPath) throws IOException {
        URL url = new URL(uri);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy()) // adds HTTP REDIRECT support
                .build();
        try {
            HttpGet get = new HttpGet(url.toURI());
            Path downloaded = httpclient.execute(get, new FileDownloadResponseHandler(dstPath));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            IOUtils.closeQuietly(httpclient);
        }
    }

    static class FileDownloadResponseHandler implements ResponseHandler<Path> {

        private final Path target;

        public FileDownloadResponseHandler(Path target) {
            this.target = target;
        }

        @Override
        public Path handleResponse(HttpResponse response) throws IOException {
            InputStream source = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(source, this.target.toFile());
            return this.target;
        }

    }
}
