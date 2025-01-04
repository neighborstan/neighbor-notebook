package com.neighbornotebook.exception;

/**
 * Исключение, выбрасываемое при ошибках работы с хранилищем заметок.
 */
public class StorageException extends RuntimeException {
    
    public StorageException(String message) {
        super(message);
    }
    
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
} 