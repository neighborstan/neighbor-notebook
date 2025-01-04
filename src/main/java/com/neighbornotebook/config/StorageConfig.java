package com.neighbornotebook.config;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Конфигурация хранилища заметок.
 * Управляет путем к директории хранения заметок.
 */
@Slf4j
@Component
public class StorageConfig {
    private Path storagePath;
    
    /**
     * Создает новый экземпляр конфигурации хранилища.
     * По умолчанию использует директорию в домашней папке пользователя.
     */
    public StorageConfig() {
        this.storagePath = getDefaultStoragePath();
        log.info("Инициализировано хранилище заметок: {}", storagePath);
    }
    
    /**
     * Возвращает текущий путь к хранилищу.
     *
     * @return путь к директории хранения заметок
     */
    public Path getCurrentStoragePath() {
        return storagePath;
    }
    
    /**
     * Обновляет путь к хранилищу.
     *
     * @param newPath новый путь к директории хранения заметок
     */
    public void updateStoragePath(Path newPath) {
        this.storagePath = newPath;
        log.info("Путь к хранилищу обновлен: {}", newPath);
    }
    
    private Path getDefaultStoragePath() {
        return Paths.get(System.getProperty("user.home"), "neighbor-notebook", "notes");
    }
} 