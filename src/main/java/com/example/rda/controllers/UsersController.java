package com.example.rda.controllers;

import com.example.rda.domain.User;
import com.example.rda.repository.DataRepository;
import com.example.rda.services.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class UsersController {
    private final DataRepository dataRepository;
    private final UserServices userServices;


    @GetMapping
    public Flux<User> getUsers() {
        return dataRepository.getUsers();
    }


    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable("id") String id) {
        return userServices.getUser(id);
    }
}
