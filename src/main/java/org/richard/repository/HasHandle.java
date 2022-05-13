package org.richard.repository;

import java.util.Optional;

public interface HasHandle<T>{

    Optional<T> findByHandle(String handle);

}
