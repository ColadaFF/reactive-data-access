package com.example.rda.repository;

import com.example.rda.domain.User;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

@Repository
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class DataRepository {
    private final ContentRepository contentRepository;
    private final PostgresqlConnectionFactory postgresqlConnectionFactory;


    private BiFunction<Row, RowMetadata, Mono<User>> getRowRowMetadataMonoBiFunction() {
        return (row, rowMetadata) -> {
            System.out.println("row = " + row);
            UUID id = row.get("id", UUID.class);
            String username = row.get("username", String.class);
            return contentRepository.getLastContentForUser(id.toString())
                    .map(page -> {
                        return User.builder()
                                .withId(id)
                                .withUsername(username)
                                .withLastContent(page)
                                .build();
                    });
        };
    }

    // Flux<Flux<User>>
    // Flux<User>
    public Flux<User> getUsers() {
        return postgresqlConnectionFactory.create()
                .flatMapMany(connection -> connection
                        .createStatement("SELECT * FROM users")
                        .execute()
                        .flatMap(postgresqlResult -> postgresqlResult
                                .map((row, rowMetadata) -> {
                                    System.out.println("row = " + row);
                                    UUID id = row.get("id", UUID.class);
                                    String username = row.get("username", String.class);
                                    return contentRepository.getLastContentForUser(id.toString())
                                            .map(page -> {
                                                return User.builder()
                                                        .withId(id)
                                                        .withUsername(username)
                                                        .withLastContent(page)
                                                        .build();
                                            });
                                }))
                        .flatMap(userMono -> userMono)
                );

    }


    public Mono<User> getUserById(String id) {
        return postgresqlConnectionFactory
                .create()
                .flatMapMany(postgresqlConnection -> postgresqlConnection
                        .createStatement("SELECT * FROM USERS WHERE id = $1")
                        .bind("$1", UUID.fromString(id))
                        .execute()
                        .flatMap(postgresqlResult ->
                                postgresqlResult.map((row, rowMetadata) -> {
                                    System.out.println("row = " + row);
                                    UUID userId = row.get("id", UUID.class);
                                    String username = row.get("username", String.class);
                                    return contentRepository.getLastContentForUser(userId.toString())
                                            .map(page -> {
                                                return User.builder()
                                                        .withId(userId)
                                                        .withUsername(username)
                                                        .withLastContent(page)
                                                        .build();
                                            });
                                })
                        )
                )
                .single()
                .flatMap(Function.identity());
    }
}
