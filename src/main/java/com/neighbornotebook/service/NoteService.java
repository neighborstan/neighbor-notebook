package com.neighbornotebook.service;

import com.neighbornotebook.model.Note;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для работы с заметками.
 * Определяет основные операции для управления заметками.
 */
public interface NoteService {
    
    /**
     * Создает новую заметку.
     *
     * @param title заголовок заметки
     * @param content содержимое заметки
     * @return созданная заметка
     */
    Note createNote(String title, String content);
    
    /**
     * Получает заметку по идентификатору.
     *
     * @param id идентификатор заметки
     * @return заметка, если найдена
     */
    Optional<Note> getNoteById(String id);
    
    /**
     * Получает список всех заметок.
     *
     * @return список всех заметок
     */
    List<Note> getAllNotes();
    
    /**
     * Обновляет существующую заметку.
     *
     * @param note заметка для обновления
     * @return обновленная заметка
     */
    Note updateNote(Note note);
    
    /**
     * Удаляет заметку по идентификатору.
     *
     * @param id идентификатор заметки для удаления
     */
    void deleteNote(String id);
} 