package org.richard.repository;

import static org.microshopify.jooq.tables.Product.PRODUCT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.richard.frankoak.infra.jooq.ProductRecordUnMapper;
import org.richard.product.Product;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    private final ProductRecordUnMapper productRecordUnMapper;

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
        this.productRecordUnMapper = new ProductRecordUnMapper(objectMapper);
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

                // insert all its children with category as

                if (result != null) {
                    return product.withId(result);
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
        int execute = getDsl()
            .delete(PRODUCT)
            .where(PRODUCT.ID.eq(id))
            .execute();
        return execute > 0;
    }

    @Override
    public boolean deleteAll(boolean testing) {
        if (testing) {
            getDsl()
                .delete(PRODUCT)
                .execute();
            return true;
        }
        return false;
    }
}
