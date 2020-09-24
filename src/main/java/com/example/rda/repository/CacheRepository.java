package com.example.rda.repository;

import com.google.gson.Gson;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class CacheRepository {
    private final RedisStringReactiveCommands<String, String> connection;
    private final Gson gson;

    public <T> Mono<String> cacheContent(String id, T content) {
        String contentString = gson.toJson(content);
        SetArgs ex = SetArgs.Builder.ex(60);
        return connection.set(id, contentString, ex);
    }


    public <T> Mono<T> getContentFromCache(String key, Class<T> tClass) {
        return connection.get(key)
                .map(cacheContent -> {
                    return gson.fromJson(cacheContent, tClass);
                });
    }
}
