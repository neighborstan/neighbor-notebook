package com.neighbornotebook.exception;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Глобальный обработчик исключений приложения.
 * Отображает пользователю информацию об ошибках в графическом интерфейсе.
 */
@Slf4j
@Component
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение и отображает сообщение об ошибке пользователю.
     *
     * @param exception исключение для обработки
     */
    public void handleException(Throwable exception) {
        log.error("Произошла ошибка: {}", exception.getMessage(), exception);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            
            if (exception instanceof NoteNotFoundException) {
                alert.setContentText("Заметка не найдена: " + exception.getMessage());
            } else if (exception instanceof StorageException) {
                alert.setContentText("Ошибка хранилища: " + exception.getMessage());
            } else {
                alert.setContentText("Произошла непредвиденная ошибка: " + exception.getMessage());
            }
            
            alert.showAndWait();
        });
    }
} 