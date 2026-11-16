package com.example.tablerito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class BoardServiceTest {

    private BoardService boards;

    @BeforeEach
    void createService() {
        NotificationService notifications = new NotificationService(new NotificationClient("http://localhost:9000/notifications", "api-key"), "development");
        boards = new BoardService(notifications);
        boards.seedSampleBoard();
    }

    @Test
    void movesACardToAnotherList() {
        CardList source = boards.createList("To do");
        CardList target = boards.createList("Done");
        Card card = boards.createCard(source.id(), "Pack the books");

        boards.moveCard(card.id(), target.id());

        assertThat(source.cards()).isEmpty();
        assertThat(target.cards()).containsExactly(card);
    }

    @Test
    void aMissingCardGives404() {
        assertThatThrownBy(() -> boards.findCard(999))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404");
    }

    @Test
    void aMissingListGives404() {
        assertThatThrownBy(() -> boards.createCard(999, "Lost"))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("404");
    }
}
