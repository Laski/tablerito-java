package com.example.tablerito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TableritoApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BoardService boards;

    @Test
    void startsWithTheSampleBoard() throws Exception {
        Board board = boards.board();
        assertThat(board.name()).isEqualTo("Curso Cloud-Native");
        assertThat(board.lists()).extracting(CardList::name).startsWith("Por hacer", "Haciendo", "Hecho");
        assertThat(board.lists().get(0).cards()).extracting(Card::title)
            .startsWith("Meter el Tablerito en un contenedor", "Armarle un pipeline");
        assertThat(board.lists().get(1).cards()).extracting(Card::title)
            .startsWith("Encontrar los problemas del Tablerito");
        assertThat(board.lists().get(2).cards()).extracting(Card::title)
            .startsWith("Leer los 12 factores");

        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Curso Cloud-Native")));
    }

    @Test
    void createsACardFromThePage() throws Exception {
        CardList list = boards.createList("From the page");

        mvc.perform(post("/lists/{list}/cards", list.id())
                .param("title", "Try out Tablerito"))
            .andExpect(status().is3xxRedirection());

        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Try out Tablerito")));
    }

    @Test
    void movesACardFromThePage() throws Exception {
        CardList source = boards.createList("Source");
        CardList target = boards.createList("Target");
        Card card = boards.createCard(source.id(), "Move me");

        mvc.perform(post("/cards/{card}/move", card.id()).param("listId", String.valueOf(target.id())))
            .andExpect(status().is3xxRedirection());

        assertThat(source.cards()).isEmpty();
        assertThat(target.cards()).containsExactly(card);
    }

    @Test
    void savesAndReturnsAnAttachment() throws Exception {
        CardList list = boards.createList("With attachments");
        Card card = boards.createCard(list.id(), "Read the contract");
        MockMultipartFile file = new MockMultipartFile("file", "contract.txt", "text/plain", "hello".getBytes());

        mvc.perform(multipart("/cards/{card}/attachments", card.id()).file(file))
            .andExpect(status().is3xxRedirection());

        String name = card.id() + "-contract.txt";
        assertThat(card.attachments()).containsExactly(name);
        mvc.perform(get("/attachments/{name}", name))
            .andExpect(status().isOk())
            .andExpect(content().string("hello"));

        Files.delete(Path.of("attachments", name));
    }

    @Test
    void aMissingCardGives404() throws Exception {
        mvc.perform(post("/cards/{card}/move", 999).param("listId", "1"))
            .andExpect(status().isNotFound());
    }
}
