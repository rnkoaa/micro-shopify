package org.richard.service;

import java.util.Optional;
import org.richard.product.Product;
import org.richard.repository.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveOrUpdate(Product product) {
        Optional<Product> productByHandle = productRepository.findByHandle(product.link());
        return productByHandle.map(foundProduct -> {
           var toSave = foundProduct.mergeWith(product);
           // https://stackoverflow.com/questions/66742834/update-a-column-when-value-is-not-null-jooq
            return toSave;
        }).orElseGet(() -> productRepository.save(product));

    }
}
