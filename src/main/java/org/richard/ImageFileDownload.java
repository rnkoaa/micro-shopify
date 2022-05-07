package org.richard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ImageFileDownload {

    private final ExecutorService executorService;

    public ImageFileDownload(ExecutorService executorService) {this.executorService = executorService;}

    boolean download(ProductImageInfo productImageInfo) {
        String baseFilePath = "product-pages/images";
        File baseDir = new File(baseFilePath);
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            return false;
        }

        String productImageBaseDir = baseDir + "/" + productImageInfo.handle();
        File productBaseDir = new File(productImageBaseDir);
        if (!productBaseDir.exists() && !productBaseDir.mkdirs()) {
            return false;
        }

        productImageInfo.imageUrls()
            .stream()
            .collect(Collectors.toMap(this::getFileName, it -> it))
            .forEach((filename, url) -> {
                String fullFilePath = productImageBaseDir + "/" + filename;
                executorService.submit(() -> processDownload(fullFilePath, url));
            });

        return true;
    }

    private void processDownload(String fullFilePath, String imageUrl) {
        File file = new File(fullFilePath);
        if (!file.exists()) {
            try {
                System.out.println("Downloading File From: " + imageUrl);
                URL url = new URL(imageUrl);
                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(fullFilePath);
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

            } catch (Exception e) {
                System.out.println("Exception downloading Image Url [" + imageUrl + "], Message: " + e.getMessage());
            }
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else{
            System.out.println("File " + fullFilePath + " already exists");
        }

    }

    private String getFileName(String url) {
        if (url.contains("?")) {
            return url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        }
        return url.substring(url.lastIndexOf("/"));
    }
}
