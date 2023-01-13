package be.cocoding.bubblepdf.download.strategy;

public class DownloaderFactory {

    public enum Strategy {
        URL_STREAM,
        URL_STREAM_CHANNEL,
        URL_HTTP_CONNECTION,
        HTTP_CLIENT,
        HTTP_CLIENT_5,
        COMMONS_IO,
        GOOGLE_CLOUD_STORAGE
    }

    public static DownloaderStrategy getDownloader(String imageUri, String strategyName) {
        DownloaderStrategy dl;
        if (GCPStorageDownloader.isGsutilUri(imageUri)) {
            // Uri is a Google Cloud Storage
            dl = GCPStorageDownloader.getInstance();
        } else {
            Strategy strategy;
            try {
                strategy = Strategy.valueOf(strategyName);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("No defined strategy found with name: " + strategyName);
            }

            switch (strategy) {
                case GOOGLE_CLOUD_STORAGE:
                    dl = GCPStorageDownloader.getInstance();
                    break;
                case URL_STREAM_CHANNEL:
                    dl = new URLStreamChannelDownloader();
                    break;
                case URL_HTTP_CONNECTION:
                    dl = new URLHttpConnectionDownloader();
                    break;
                case COMMONS_IO:
                    dl = new CommonsIODownloader();
                    break;
                case HTTP_CLIENT:
                    dl = new HttpClientDownloader();
                    break;
                case HTTP_CLIENT_5:
                    dl = new HttpClient5Downloader();
                    break;
                case URL_STREAM:
                default:
                    dl = new URLStreamDownloader();
            }
        }
        return dl;
    }
}
