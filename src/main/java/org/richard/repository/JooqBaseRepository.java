package org.richard.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.jooq.DSLContext;
import org.richard.infra.jooq.JooqJsonHandler;
import org.richard.utils.Strings;

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

    public boolean mapToBoolean(Integer value) {
        return value != null && value > 0;
    }

    public Instant mapToInstant(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return Instant.now();
        }
        return Instant.parse(value);
    }

    public int safeInt(Integer value){
        return value == null || value <= 0 ? 0 : value;
    }
}
