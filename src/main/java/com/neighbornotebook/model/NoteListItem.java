package com.neighbornotebook.model;

import lombok.Getter;
import java.time.LocalDateTime;

/**
 * Класс для отображения заметки в списке.
 * Содержит только необходимую информацию для отображения в UI.
 */
@Getter
public class NoteListItem {
    private final String id;
    private final String title;
    private final LocalDateTime updatedAt;
    
    /**
     * Создает новый элемент списка на основе заметки.
     *
     * @param note заметка для отображения
     */
    public NoteListItem(Note note) {
        this.id = note.getId();
        this.title = note.getTitle();
        this.updatedAt = note.getUpdatedAt();
    }
    
    @Override
    public String toString() {
        return title;
    }
} 