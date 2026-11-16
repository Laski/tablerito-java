package com.example.tablerito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AttachmentService {

    public String save(long cardId, MultipartFile file) throws IOException {
        String name = cardId + "-" + Path.of(file.getOriginalFilename()).getFileName();
        Path target = Path.of("attachments", name);
        Files.createDirectories(target.getParent());
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return name;
    }

    public Path find(String name) {
        Path folder = Path.of("attachments");
        Path file = folder.resolve(name).normalize();
        if (!file.startsWith(folder) || !Files.exists(file)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment " + name + " not found");
        }
        return file;
    }
}
