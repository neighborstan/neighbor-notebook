package com.neighbornotebook.service.impl;

import com.neighbornotebook.model.Note;
import com.neighbornotebook.service.NoteService;
import com.neighbornotebook.exception.NoteNotFoundException;
import com.neighbornotebook.exception.StorageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Реализация сервиса заметок с хранением в файловой системе.
 */
@Slf4j
@Service
public class FileSystemNoteService implements NoteService {
    
    private final Path storagePath;
    private final ObjectMapper objectMapper;

    public FileSystemNoteService(
            @Value("${app.storage.path}") String storagePath,
            ObjectMapper objectMapper) {
        this.storagePath = Paths.get(storagePath);
        this.objectMapper = objectMapper;
        initializeStorage();
    }

    private void initializeStorage() {
        try {
            Files.createDirectories(storagePath);
            log.info("Инициализировано хранилище заметок по пути: {}", storagePath);
        } catch (IOException e) {
            String message = "Не удалось инициализировать хранилище заметок";
            log.error("{}: {}", message, e.getMessage());
            throw new StorageException(message, e);
        }
    }

    @Override
    public Note createNote(String title, String content) {
        log.debug("Создание новой заметки с заголовком: {}", title);
        
        Note note = new Note();
        note.setId(UUID.randomUUID().toString());
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        note.setFormat("plain");

        saveNote(note);
        log.info("Создана новая заметка с ID: {}", note.getId());
        return note;
    }

    @Override
    public Optional<Note> getNoteById(String id) {
        log.debug("Получение заметки по ID: {}", id);
        Path notePath = storagePath.resolve(id + ".json");
        
        try {
            if (Files.exists(notePath)) {
                Note note = objectMapper.readValue(Files.readAllBytes(notePath), Note.class);
                log.debug("Заметка найдена: {}", id);
                return Optional.of(note);
            }
        } catch (IOException e) {
            String message = String.format("Ошибка при чтении заметки %s", id);
            log.error("{}: {}", message, e.getMessage());
            throw new StorageException(message, e);
        }
        
        log.debug("Заметка не найдена: {}", id);
        return Optional.empty();
    }

    @Override
    public List<Note> getAllNotes() {
        log.debug("Получение списка всех заметок");
        try {
            return Files.list(storagePath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(this::readNoteFromFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            String message = "Ошибка при получении списка заметок";
            log.error("{}: {}", message, e.getMessage());
            throw new StorageException(message, e);
        }
    }

    @Override
    public Note updateNote(Note note) {
        log.debug("Обновление заметки: {}", note.getId());
        
        // Проверяем существование заметки
        if (!Files.exists(storagePath.resolve(note.getId() + ".json"))) {
            throw new NoteNotFoundException(note.getId());
        }
        
        note.setUpdatedAt(LocalDateTime.now());
        saveNote(note);
        log.info("Заметка обновлена: {}", note.getId());
        return note;
    }

    @Override
    public void deleteNote(String id) {
        log.debug("Удаление заметки: {}", id);
        try {
            Path notePath = storagePath.resolve(id + ".json");
            if (!Files.deleteIfExists(notePath)) {
                throw new NoteNotFoundException(id);
            }
            log.info("Заметка удалена: {}", id);
        } catch (IOException e) {
            String message = String.format("Ошибка при удалении заметки %s", id);
            log.error("{}: {}", message, e.getMessage());
            throw new StorageException(message, e);
        }
    }

    private void saveNote(Note note) {
        try {
            Path notePath = storagePath.resolve(note.getId() + ".json");
            String noteJson = objectMapper.writeValueAsString(note);
            Files.write(notePath, noteJson.getBytes());
            log.debug("Заметка сохранена в файл: {}", notePath);
        } catch (IOException e) {
            String message = String.format("Ошибка при сохранении заметки %s", note.getId());
            log.error("{}: {}", message, e.getMessage());
            throw new StorageException(message, e);
        }
    }

    private Optional<Note> readNoteFromFile(Path path) {
        try {
            return Optional.of(objectMapper.readValue(Files.readAllBytes(path), Note.class));
        } catch (IOException e) {
            String message = String.format("Ошибка при чтении заметки из файла %s", path);
            log.error("{}: {}", message, e.getMessage());
            return Optional.empty();
        }
    }
} 