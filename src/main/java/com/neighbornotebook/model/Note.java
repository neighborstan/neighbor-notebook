package com.neighbornotebook.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Модель для хранения заметок.
 * Содержит основную информацию о заметке, включая содержимое и метаданные.
 */
@Data
public class Note {
    /** Уникальный идентификатор заметки */
    private String id;
    
    /** Заголовок заметки */
    private String title;
    
    /** Содержимое заметки */
    private String content;
    
    /** Дата и время создания заметки */
    private LocalDateTime createdAt;
    
    /** Дата и время последнего обновления заметки */
    private LocalDateTime updatedAt;
    
    /** Формат заметки (markdown, plain, code) */
    private String format;
    
    /** Язык программирования для сниппетов кода */
    private String language;
} 