package com.example.tablerito;

import java.util.ArrayList;
import java.util.List;

public record CardList(long id, String name, List<Card> cards) {

    public CardList(long id, String name) {
        this(id, name, new ArrayList<>());
    }
}
