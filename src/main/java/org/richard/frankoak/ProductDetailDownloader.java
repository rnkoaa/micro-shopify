package org.richard.frankoak;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductDetailDownloader {

    private final HttpClient httpClient;

    public ProductDetailDownloader(HttpClient httpClient) {this.httpClient = httpClient;}

    public record ProductInfo(String handle, String fullUrl, String filePath, String content) {

        public ProductInfo(String handle, String fullUrl, String filePath) {
            this(handle, fullUrl, filePath, "");
        }

        public ProductInfo withContent(String content) {
            return new ProductInfo(handle, fullUrl, filePath, content);
        }
    }

    public Set<ProductInfo> downloadProductDetails(Set<String> productHandles) {
        String baseUrl = "https://www.frankandoak.com/products/%s";
        String filePathBase = "product-pages/json/%s";
        return productHandles.stream()
            .map(handle -> new ProductInfo(handle, String.format(baseUrl, handle), String.format(filePathBase, handle)))
            .map(productInfo -> {
                try {
                    String response = getPage(httpClient, productInfo.fullUrl);
                    return productInfo.withContent(response);
                } catch (IOException | InterruptedException e) {
//                    throw new RuntimeException(e);
                    return productInfo;
                }
            })
            .peek(productInfo -> {
                try {
                    writeToFile(productInfo);
                } catch (IOException e) {
//                    throw new RuntimeException(e);
                    System.out.printf("error writing %s to file, message: [%s]\n", productInfo.handle, e.getMessage());
                }
            })
            .collect(Collectors.toSet());

    }

    private void writeToFile(ProductInfo productInfo) throws IOException {
        File myObj = new File(productInfo.filePath);
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(productInfo.filePath);
            myWriter.write(productInfo.content);
            myWriter.close();
        }
    }

    private static String getPage(HttpClient httpClient, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMinutes(2))
            .header("Content-Type", "application/json")
            .build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() <= 299) {
            return response.body();
        }
        return "";
    }
}
