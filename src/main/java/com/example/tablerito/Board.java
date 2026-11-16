package com.example.tablerito;

import java.util.ArrayList;
import java.util.List;

public record Board(String name, List<CardList> lists) {

    public Board(String name) {
        this(name, new ArrayList<>());
    }
}
