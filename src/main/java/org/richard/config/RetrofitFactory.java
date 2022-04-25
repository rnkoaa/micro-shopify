package org.richard.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import okhttp3.OkHttpClient;
import org.richard.frankoak.FrankOakClient;
import org.richard.frankoak.FrankOakConfig;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitFactory {

//    @Singleton
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(new RequestBodyLoggingInterceptor());
        return httpClient.build();
    }

//    @Singleton
    public Retrofit retrofit(FrankOakConfig config, ObjectMapper objectMapper, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
            .baseUrl(config.baseApi())
            .client(okHttpClient)
            .addCallAdapterFactory(ReactorCallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build();
    }

//    @Singleton
    public FrankOakClient frankOakClient(Retrofit retrofit) {
        return retrofit.create(FrankOakClient.class);
    }
}
