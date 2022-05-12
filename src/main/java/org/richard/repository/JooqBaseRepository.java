package org.richard.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.richard.infra.jooq.JooqJsonHandler;

public abstract class JooqBaseRepository extends JooqJsonHandler {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public JooqBaseRepository(DSLContext dsl, ObjectMapper objectMapper) {
        super(objectMapper);
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
