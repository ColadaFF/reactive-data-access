package com.example.rda.common;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(setterPrefix = "with")
public class ItemsPage<T> {
    List<T> items;
    String lastItemToken;
}
