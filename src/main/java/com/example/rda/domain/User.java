package com.example.rda.domain;

import com.example.rda.common.ItemsPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private UUID id;
    private String username;
    private ItemsPage<Content> lastContent;
}
