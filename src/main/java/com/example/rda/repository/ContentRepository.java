package com.example.rda.repository;

import com.example.rda.common.ItemsPage;
import com.example.rda.domain.Content;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public class ContentRepository {

    private final MongoCollection<Document> contentCollection;

    @Autowired
    public ContentRepository(@Qualifier("content-collection") MongoCollection<Document> contentCollection) {
        this.contentCollection = contentCollection;
    }

    public Mono<ItemsPage<Content>> getLastContentForUser(String userId) {
        FindPublisher<Document> user = contentCollection.find(Filters.eq("user", userId))
                .limit(20);

        return Flux.from(user)
                .map(document -> {
                    return Content.builder()
                            .withContentId(document.getString("contentId"))
                            .withId(document.getObjectId("_id").toString())
                            .withText(document.getString("text"))
                            .withUser(document.getString("user"))
                            .build();
                })
                .buffer()
                .single()
                .map(contents -> {
                    String lastToken = Optional.ofNullable(contents)
                            .filter(list -> !list.isEmpty())
                            .map(list -> {
                                int size = list.size();
                                return list.get(size - 1);
                            })
                            .map(Content::getId)
                            .orElse(null);

                    return ItemsPage.<Content>builder()
                            .withItems(contents)
                            .withLastItemToken(lastToken)
                            .build();
                });
    }
}
