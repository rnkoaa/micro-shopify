package org.richard;

import java.util.Optional;

public interface HasHandle<T>{

    Optional<T> findByHandle(String handle);

}
