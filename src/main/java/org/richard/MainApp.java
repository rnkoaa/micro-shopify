package org.richard;

import static org.richard.config.ObjectMapperFactory.buildObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.richard.config.HttpClientFactory;
import org.richard.frankoak.ProductDetailDownloader;
import org.richard.frankoak.product.ImageItem;
import org.richard.frankoak.product.ProductDetailResponse;
import org.richard.frankoak.product.ProductItemResponse;

public class MainApp {

    static String jdbcUrl = "jdbc:sqlite:src/main/resources/db/micro-shopify.db";

    static ObjectMapper objectMapper = buildObjectMapper();

    public static void main(String[] args) throws IOException, InterruptedException {
        Set<ProductItemResponse> productItemResponses = readProducts();

        Set<ProductImageInfo> productImageUrls = productItemResponses.stream()
            .map(p -> {
                Set<String> imageUrls = p.images().stream()
                    .map(ImageItem::src)
                    .collect(Collectors.toSet());

                return new ProductImageInfo(p.id(), p.handle(), imageUrls);
            })
            .collect(Collectors.toSet());

        long count = productImageUrls.stream()
            .mapToLong(p -> p.imageUrls().size())
            .sum();

        System.out.println("Total Images: " + count);
        ExecutorService executorService = Executors.newCachedThreadPool();
        ImageFileDownload imageFileDownload = new ImageFileDownload(executorService);
        for (ProductImageInfo productImageUrl : productImageUrls) {
            imageFileDownload.download(productImageUrl);
        }

//        for (ProductItemResponse productItemResponse : productItemResponses) {
////            System.out.println(productItemResponse);
//        }

//        Set<Category> categories = parseMenu();
//        List<Category> seedCategories = new ArrayList<>();
//        flatten(seedCategories, null, categories);
//
//        seedCategories.sort(Comparator.comparing(Category::name));
//

//        String categoryPaths = "category-pages";
//        File file = new File(categoryPaths);
//        File[] files = file.listFiles();
//        if (files != null && files.length > 0) {
//            System.out.printf("Found %d Products\n ", files.length);
//            Set<Product> products = Arrays.stream(files)
//                .filter(f -> f.getName().endsWith(".html"))
//                .flatMap(f -> {
//                    System.out.printf("Processing File %s\n", f.getPath());
//                    try {
//                        return CategoryPageParser.parseShopAllData(f.getPath()).stream();
//                    } catch (IOException e) {
//                        System.out.println("unable to read file");
//                    }
//                    return Stream.empty();
//                })
//                .collect(Collectors.toSet());
//
//            System.out.println("Read " + products.size() + " " + products);
//        }
    }

    public static void downloadProductDetails() {
        ProductDetailDownloader productDetailDownloader = new ProductDetailDownloader(
            HttpClientFactory.buildHttpClient());

        Set<String> allProducts = CategoryMenuParser.readCategoryFiles(objectMapper)
            .stream()
            .flatMap(r -> r.products().stream())
            .map(it -> String.format("%s.json", it.handle()))
            .collect(Collectors.toSet());

        productDetailDownloader.downloadProductDetails(allProducts);
    }

    public static Set<ProductItemResponse> readProducts() {
        record FileInfo(String filepath, Path path, String fileName) {}

        String path = "product-pages/json";
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            return Arrays.stream(files)
                .map(f -> new FileInfo(f.getPath(), f.toPath(), f.getName()))
                .filter(fPath -> fPath.filepath.endsWith(".json"))
                .map(f -> {
                    try {
                        String handle = f.fileName.substring(0, f.fileName.indexOf("."));
                        String fileContent = Files.readString(f.path);
                        var response = objectMapper.readValue(fileContent, ProductDetailResponse.class);
                        return response.product().withHandle(handle);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }

        return Set.of();
    }

    private void getCategories(HttpClient httpClient, List<Category> categories) {
        System.out.println(categories.size());
        IntStream.range(0, categories.size())
            .mapToObj(index -> categories.get(index).withId(index))
            .filter(p -> p.url() != null && !p.url().isEmpty())
            .forEach(it -> {
                try {
                    String response = getPage(httpClient, it.url());
                    writeResponse(it, response);
                } catch (IOException | InterruptedException e) {
                    System.out.println("Exception for request: " + it.url() + ": " + e.getMessage());
                }
            });
    }

    private static void writeResponse(Category it, String response) throws IOException {
        String name = "playwright-output/category-pages/" + it.id() + it.url().replaceAll("/", "-") + ".html";
        File myObj = new File(name);
        if (myObj.createNewFile()) {
            FileWriter myWriter = new FileWriter(name);
            myWriter.write(response);
            myWriter.close();
        }
    }

    private static String getPage(HttpClient httpClient, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://www.frankandoak.com" + url))
            .timeout(Duration.ofMinutes(2))
//            .header("Content-Type", "application/json")
//            .POST(BodyPublishers.ofFile(Paths.get("file.json")))
            .build();
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() <= 299) {
            return response.body();
        }
        return "";
    }
}
