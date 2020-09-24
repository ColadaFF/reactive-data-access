package com.example.rda.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.reactive.RedisStringReactiveCommands;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public PostgresqlConnectionFactory postgresConnectionFactory() {
        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host("3.235.195.21")
                .port(5432)  // optional, defaults to 5432
                .username("postgres")
                .password("password")
                .database("postgres")
                .build());
    }


    @Bean
    @Qualifier("database")
    public MongoDatabase mongoClientProvider() {
        ConnectionString connectionString = new ConnectionString("mongodb://3.235.195.21:27017");
        MongoClient mongoClient = MongoClients.create(connectionString);
        return mongoClient.getDatabase("tweets");
    }


    @Bean
    @Qualifier("content-collection")
    public MongoCollection<Document> contentCollectionProvider(
            @Qualifier("database") MongoDatabase mongoDatabase
    ) {
        return mongoDatabase.getCollection("content");
    }


    @Bean
    public RedisStringReactiveCommands<String, String> redisClientProvider() {
        RedisClient redisClient = RedisClient.create("redis://3.235.195.21");
        return redisClient
                .connect()
                .reactive();
    }
}
