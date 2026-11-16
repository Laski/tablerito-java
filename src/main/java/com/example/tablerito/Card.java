package com.example.tablerito;

import java.util.ArrayList;
import java.util.List;

public record Card(long id, String title, List<String> attachments) {

    public Card(long id, String title) {
        this(id, title, new ArrayList<>());
    }
}
