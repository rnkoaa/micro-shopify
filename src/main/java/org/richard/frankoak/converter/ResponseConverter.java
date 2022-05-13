package org.richard.frankoak.converter;

public interface ResponseConverter<T, U> {

    T convert(U value);
}
