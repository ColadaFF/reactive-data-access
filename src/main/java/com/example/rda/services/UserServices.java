package com.example.rda.services;

import com.example.rda.domain.User;
import com.example.rda.repository.CacheRepository;
import com.example.rda.repository.DataRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserServices {
    private final CacheRepository cacheRepository;
    private final DataRepository dataRepository;


    public Mono<User> getUser(String userId) {
        Mono<User> userById = dataRepository.getUserById(userId)
                .doOnNext(subscription -> {
                    System.out.println("Got from DB");
                })
                .flatMap(user -> cacheRepository
                        .cacheContent(userId, user)
                        .map(ignored -> user)
                );
        return cacheRepository.getContentFromCache(userId, User.class)
                .doOnNext(subscription -> {
                    System.out.println("Got from Cache");
                })
                .switchIfEmpty(userById);

    }

}
