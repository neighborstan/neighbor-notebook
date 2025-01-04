package com.neighbornotebook.controller;

import com.neighbornotebook.service.NoteService;
import com.neighbornotebook.exception.GlobalExceptionHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * Основной контроллер пользовательского интерфейса.
 * Управляет взаимодействием между UI и бизнес-логикой приложения.
 */
@Slf4j
@Component
public class MainController {
    
    private final NoteService noteService;
    private final GlobalExceptionHandler exceptionHandler;
    
    /** Поле для ввода заголовка заметки */
    @FXML
    private TextField titleField;
    
    /** Поле для ввода содержимого заметки */
    @FXML
    private TextArea contentArea;
    
    /**
     * Создает новый экземпляр контроллера.
     *
     * @param noteService сервис для работы с заметками
     * @param exceptionHandler обработчик исключений
     */
    public MainController(NoteService noteService, GlobalExceptionHandler exceptionHandler) {
        this.noteService = noteService;
        this.exceptionHandler = exceptionHandler;
    }
    
    /**
     * Инициализирует контроллер.
     * Вызывается автоматически после загрузки FXML.
     */
    @FXML
    public void initialize() {
        log.debug("Инициализация главного контроллера");
    }
    
    /**
     * Обрабатывает событие сохранения заметки.
     * Создает новую заметку на основе введенных данных.
     */
    @FXML
    public void handleSaveNote() {
        try {
            String title = titleField.getText();
            String content = contentArea.getText();
            
            log.debug("Попытка сохранения заметки с заголовком: {}", title);
            noteService.createNote(title, content);
            log.info("Заметка успешно сохранена");
            
            clearFields();
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
    
    /**
     * Очищает поля ввода после сохранения заметки.
     */
    private void clearFields() {
        titleField.clear();
        contentArea.clear();
        log.debug("Поля ввода очищены");
    }
} 