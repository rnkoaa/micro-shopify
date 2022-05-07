package org.richard.repository;

import static org.microshopify.jooq.tables.Image.IMAGE;
import static org.microshopify.jooq.tables.Product.PRODUCT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.frankoak.infra.jooq.ImageRecordUnMapper;
import org.richard.frankoak.infra.jooq.ProductRecordUnMapper;
import org.richard.product.Image;
import org.richard.product.Product;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    private final ProductRecordUnMapper productRecordUnMapper;
    private final ImageRecordUnMapper imageRecordUnMapper;

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.productRecordUnMapper = new ProductRecordUnMapper(objectMapper);
        this.imageRecordUnMapper = new ImageRecordUnMapper();
    }

    @Override
    public Optional<Product> findByHandle(String handle) {
        Product product = getDsl().selectFrom(PRODUCT)
            .where(PRODUCT.HANDLE.eq(handle))
            .fetchOneInto(Product.class);
        return Optional.ofNullable(product);
    }

    @Override
    public boolean update(Product value) {
        return false;
    }

    @Override
    public Product save(Product product) {
        return getDsl().transactionResult(configuration -> {
            try {
                Integer result = getDsl().insertInto(PRODUCT)
                    .set(productRecordUnMapper.unmap(product))
                    .returningResult(PRODUCT.ID)
                    .fetchOneInto(Integer.class);

                final var savedProduct = product.clone()
                    .withId(result);
                // insert all its children with category as
                if (product.images() != null) {
                    List<Image> images = product.images()
                        .stream().map(image -> saveImage(savedProduct, image))
                        .toList();
//                    savedProduct = savedProduct.withImages(images);
                }

                if (result != null) {
                    return savedProduct;
                }
                return product;
            } catch (DataAccessException ex) {
                if (ex.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    System.out.println("Unique Constraint: " + product);
                }
            }
            return product;
        });
    }

    private Image saveImage(Product product, Image image) {
        var toSave = image.withProduct(product);
        Integer result = getDsl()
            .insertInto(IMAGE)
            .set(imageRecordUnMapper.unmap(toSave))
            .returningResult(IMAGE.ID)
            .fetchOneInto(Integer.class);
        return image.withId(result);
    }

    @Override
    public List<Product> save(List<Product> values) {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<Product> findAll() {
        return getDsl()
            .selectFrom(PRODUCT)
            .fetchInto(Product.class);
    }

    @Override
    public Optional<Product> findById(Integer id) {
        Product product = getDsl().selectFrom(PRODUCT)
            .where(PRODUCT.ID.eq(id))
            .fetchOneInto(Product.class);
        return Optional.ofNullable(product);
    }

    @Override
    public boolean delete(Integer id) {
        return getDsl().transactionResult(configuration -> {
            if (!deleteImages(id)) {
                return false;
            }
            int execute = getDsl()
                .delete(PRODUCT)
                .where(PRODUCT.ID.eq(id))
                .execute();
            return execute > 0;
        });
    }

    @Override
    public boolean deleteAll(boolean testing) {
        if (testing) {
            return getDsl().transactionResult(configuration -> {
                if (!deleteAllImages(testing)) {
                    return false;
                }

                return getDsl()
                    .delete(PRODUCT)
                    .execute() > 0;
            });
        }
        return false;
    }

    private boolean deleteImages(Integer id) {
        return getDsl()
            .delete(IMAGE)
            .where(IMAGE.PRODUCT_ID.eq(id))
            .execute() > 0;
    }

    private boolean deleteAllImages(boolean test) {
        if (test) {
            return getDsl()
                .delete(IMAGE)
                .execute() > 0;
        }
        return false;
    }
}
