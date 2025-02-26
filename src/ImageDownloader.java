import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.concurrent.*;

public class ImageDownloader {
    private final ScheduledExecutorService executorService;

    public ImageDownloader() {
        executorService = Executors.newScheduledThreadPool(1);
    }

    public void setWindowIcon(String miiStudioText) {
        executorService.execute(() -> {
            try {
                // Check if the image is cached
                File cachedImage = getCachedImage(miiStudioText);
                if (cachedImage != null) {
                    // Apply the cached image
                    Main.window.setMiiIcon(cachedImage);
                    return;
                }

                // Download and apply the image
                cachedImage = downloadAndCacheImage(miiStudioText);
                Main.window.setMiiIcon(cachedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private File getCachedImage(String miiStudioText) throws IOException {
        Path cachePath = Paths.get(Main.CACHE_PATH);
        if (!Files.exists(cachePath)) {
            Files.createDirectories(cachePath);
        }

        String fileName = miiStudioText + ".png";
        File cacheFile = cachePath.resolve(fileName).toFile();

        return cacheFile.exists() ? cacheFile : null;
    }

    private File downloadAndCacheImage(String miiStudioText) throws IOException {
        String imageUrl = "https://studio.mii.nintendo.com/miis/image.png?data=" + miiStudioText + "&width=512&type=face";

        Path cachePath = Paths.get(Main.CACHE_PATH);
        if (!Files.exists(cachePath)) {
            Files.createDirectories(cachePath);
        }

        String fileName = miiStudioText + ".png";
        File cacheFile = cachePath.resolve(fileName).toFile();

        System.out.println("Downloading image...");
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(cacheFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("Image saved to cache.");
        return cacheFile;
    }
}