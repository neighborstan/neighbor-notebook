package com.neighbornotebook.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к несуществующей заметке.
 */
public class NoteNotFoundException extends RuntimeException {
    
    public NoteNotFoundException(String id) {
        super(String.format("Заметка с ID '%s' не найдена", id));
    }
} 