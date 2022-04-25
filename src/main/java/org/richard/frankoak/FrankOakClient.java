package org.richard.frankoak;

import org.richard.frankoak.product.ProductDetailResponse;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FrankOakClient {

    @GET("/{path}")
    ProductDetailResponse get(@Path("path") String path);
}
