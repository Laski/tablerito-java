package com.example.tablerito;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class TableritoController {

    private final BoardService boards;
    private final AttachmentService attachments;

    public TableritoController(BoardService boards, AttachmentService attachments) {
        this.boards = boards;
        this.attachments = attachments;
    }

    @GetMapping("/")
    String board(Model model) {
        model.addAttribute("board", boards.board());
        return "board";
    }

    @PostMapping("/lists/{listId}/cards")
    String createCard(@PathVariable long listId, @RequestParam String title) {
        boards.createCard(listId, title);
        return "redirect:/";
    }

    @PostMapping("/cards/{cardId}/move")
    String moveCard(@PathVariable long cardId, @RequestParam long listId) {
        boards.moveCard(cardId, listId);
        return "redirect:/";
    }

    @PostMapping("/cards/{cardId}/attachments")
    String attach(@PathVariable long cardId, @RequestParam MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            Card card = boards.findCard(cardId);
            card.attachments().add(attachments.save(cardId, file));
        }
        return "redirect:/";
    }

    @GetMapping("/attachments/{name}")
    ResponseEntity<Resource> download(@PathVariable String name) {
        ContentDisposition disposition = ContentDisposition.attachment()
            .filename(name, StandardCharsets.UTF_8)
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
            .body(new FileSystemResource(attachments.find(name)));
    }
}
