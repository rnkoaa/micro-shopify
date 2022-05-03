package org.richard.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.richard.Product;

public class ProductRepository extends JooqBaseRepository implements Repository<Product, Integer>,
    HasHandle<Product> {

    public ProductRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(dsl, objectMapper);
    }

    @Override
    public Optional<Product> findByHandle(String handle) {
        return Optional.empty();
    }

    @Override
    public boolean update(Product value) {
        return false;
    }

    @Override
    public Product save(Product value) {
        return null;
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
        return null;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public boolean deleteAll(boolean testing) {
        return false;
    }
}
