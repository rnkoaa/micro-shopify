package org.richard.repository;

import static org.microshopify.jooq.tables.CategoryProduct.CATEGORY_PRODUCT;

import java.util.List;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.product.Category;
import org.richard.product.Product;

public class CategoryProductRepository {

    final DSLContext dslContext;
    final ProductRepository productRepository;
    final CategoryRepository categoryRepository;

    public CategoryProductRepository(
        DSLContext dslContext,
        ProductRepository productRepository,
        CategoryRepository categoryRepository) {
        this.dslContext = dslContext;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Category save(Category category, List<Product> products) {
        return dslContext.transactionResult(configuration -> {
            var savedCategory = categoryRepository.save(category);
            List<Product> savedProducts = productRepository.save(products);

            List<Integer> savedProductIds = savedProducts.stream()
                .map(product ->
                    dslContext.transactionResult(trx -> {
                        try {
                            return trx.dsl().insertInto(CATEGORY_PRODUCT)
                                .values(savedCategory.id(), product.id())
                                .execute();
                        } catch (DataAccessException ex) {
                            System.out.println("Got exception: " + ex.getMessage());
                            if (ex.getMessage().contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                                return 1;
                            }
                            return 0;
                        }
                    })
                ).toList();
            if (savedProductIds.size() != savedProducts.size()) {
                throw new RuntimeException("not all products were saved");
            }
            return savedCategory.withProducts(savedProducts);
        });
    }

    public boolean delete(Integer categoryId, Integer productId) {
        return dslContext.transactionResult(configuration -> {
            productRepository.delete(productId);
            categoryRepository.delete(categoryId);
            dslContext.delete(CATEGORY_PRODUCT)
                .where(CATEGORY_PRODUCT.CATEGORY_ID.eq(categoryId))
                .and(CATEGORY_PRODUCT.CATEGORY_ID.eq(productId))
                .execute();
            return true;
        });
    }

    public boolean delete(boolean testing) {
        if (testing) {
            return dslContext.transactionResult(configuration -> {
                productRepository.deleteAll(true);
                categoryRepository.deleteAll(true);
                dslContext.delete(CATEGORY_PRODUCT)
                    .execute();
                return true;
            });
        }
        return false;
    }
}
