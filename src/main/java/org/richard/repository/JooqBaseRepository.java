package org.richard.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;

public abstract class JooqBaseRepository {


    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public JooqBaseRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    public DSLContext getDsl() {
        return dsl;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
