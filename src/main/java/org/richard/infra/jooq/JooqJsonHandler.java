package org.richard.infra.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.JSON;

public abstract class JooqJsonHandler {

    private final ObjectMapper objectMapper;

    protected JooqJsonHandler(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

    public JSON parseJSON(Object value) {
        return JSON.valueOf(safeJson(value));
    }

    public String safeJson(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public <T> T deserialize(String value, Class<T> clzz) {
        try {
            return objectMapper.readValue(value, clzz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public <T> List<T> deserialize(String value, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
    public <T> Set<T> deserializeStringSet(String value, TypeReference<Set<T>> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            return Set.of();
        }
    }

    public <T, U> Map<T, U> deserializeMap(String value, TypeReference<Map<T, U>> typeReference) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
