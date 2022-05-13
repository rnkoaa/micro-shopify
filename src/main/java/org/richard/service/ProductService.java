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
            var updated = productRepository.update(toSave);
            return (updated) ? toSave : product;
        }).orElseGet(() -> productRepository.save(product));

    }
}
