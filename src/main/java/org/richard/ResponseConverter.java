package org.richard;

public interface ResponseConverter<T, U> {

    T convert(U value);
}
