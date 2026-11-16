package com.example.tablerito;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;

@Service
public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);

    private final Board board = new Board("Curso Cloud-Native");
    private final AtomicLong ids = new AtomicLong();
    private final NotificationService notifications;

    public BoardService(NotificationService notifications) {
        this.notifications = notifications;
    }

    @PostConstruct
    void seedSampleBoard() {
        CardList toDo = createList("Por hacer");
        CardList doing = createList("Haciendo");
        CardList done = createList("Hecho");
        createCard(done.id(), "Leer los 12 factores");
        createCard(doing.id(), "Encontrar los problemas del Tablerito");
        createCard(toDo.id(), "Meter el Tablerito en un contenedor");
        createCard(toDo.id(), "Armarle un pipeline");
        log.info("Sample board loaded");
    }

    public Board board() {
        return board;
    }

    public CardList createList(String name) {
        CardList list = new CardList(ids.incrementAndGet(), name);
        board.lists().add(list);
        return list;
    }

    public Card createCard(long listId, String title) {
        Card card = new Card(ids.incrementAndGet(), title);
        findList(listId).cards().add(card);
        return card;
    }

    public void moveCard(long cardId, long targetListId) {
        CardList target = findList(targetListId);
        Card card = findCard(cardId);
        for (CardList list : board.lists()) {
            list.cards().remove(card);
        }
        target.cards().add(card);
        notifications.notify(new Notification("La tarjeta \"" + card.title() + "\" pasó a " + target.name()));
    }

    public Card findCard(long cardId) {
        for (CardList list : board.lists()) {
            for (Card card : list.cards()) {
                if (card.id() == cardId) {
                    return card;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card " + cardId + " not found");
    }

    private CardList findList(long listId) {
        for (CardList list : board.lists()) {
            if (list.id() == listId) {
                return list;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "List " + listId + " not found");
    }
}
