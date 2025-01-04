package com.neighbornotebook.controller;

import com.neighbornotebook.service.NoteService;
import com.neighbornotebook.config.StorageConfig;
import com.neighbornotebook.exception.GlobalExceptionHandler;
import com.neighbornotebook.model.Note;
import com.neighbornotebook.model.NoteListItem;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Основной контроллер пользовательского интерфейса.
 * Управляет взаимодействием между UI и бизнес-логикой приложения.
 */
@Slf4j
@Component
public class MainController {
    
    private final NoteService noteService;
    private final GlobalExceptionHandler exceptionHandler;
    private final StorageConfig storageConfig;
    
    /** Поле для поиска заметок */
    @FXML
    private TextField searchField;
    
    /** Список заметок */
    @FXML
    private ListView<NoteListItem> noteListView;
    
    /** Поле для ввода заголовка заметки */
    @FXML
    private TextField titleField;
    
    /** Поле для ввода содержимого заметки */
    @FXML
    private TextArea contentArea;
    
    private final ObservableList<NoteListItem> noteItems = FXCollections.observableArrayList();
    private NoteListItem currentNote;
    
    /**
     * Создает новый экземпляр контроллера.
     *
     * @param noteService сервис для работы с заметками
     * @param exceptionHandler обработчик исключений
     * @param storageConfig конфигурация хранилища
     */
    public MainController(NoteService noteService, 
                         GlobalExceptionHandler exceptionHandler,
                         StorageConfig storageConfig) {
        this.noteService = noteService;
        this.exceptionHandler = exceptionHandler;
        this.storageConfig = storageConfig;
    }
    
    /**
     * Инициализирует контроллер.
     * Вызывается автоматически после загрузки FXML.
     */
    @FXML
    public void initialize() {
        log.debug("Инициализация главного контроллера");
        
        noteListView.setItems(noteItems);
        noteListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> loadNote(newValue));
        
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> filterNotes(newValue));
        
        refreshNoteList();
    }
    
    /**
     * Обрабатывает выбор директории хранилища.
     */
    @FXML
    public void handleSelectStorage() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите директорию для хранения заметок");
        directoryChooser.setInitialDirectory(
                storageConfig.getCurrentStoragePath().toFile());
        
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            try {
                storageConfig.updateStoragePath(selectedDirectory.toPath());
                refreshNoteList();
                showInfo("Хранилище обновлено", 
                        "Новый путь: " + selectedDirectory.getAbsolutePath());
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
    }
    
    /**
     * Обрабатывает создание новой заметки.
     */
    @FXML
    public void handleNewNote() {
        currentNote = null;
        clearFields();
    }
    
    /**
     * Обрабатывает удаление текущей заметки.
     */
    @FXML
    public void handleDeleteNote() {
        if (currentNote != null) {
            try {
                noteService.deleteNote(currentNote.getId());
                refreshNoteList();
                clearFields();
                currentNote = null;
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
    }
    
    /**
     * Обрабатывает событие сохранения заметки.
     * Создает новую заметку или обновляет существующую.
     */
    @FXML
    public void handleSaveNote() {
        try {
            String title = titleField.getText();
            String content = contentArea.getText();
            
            if (currentNote == null) {
                log.debug("Попытка сохранения новой заметки с заголовком: {}", title);
                Note note = noteService.createNote(title, content);
                log.info("Заметка успешно сохранена");
            } else {
                log.debug("Обновление заметки: {}", currentNote.getId());
                Note note = noteService.getNoteById(currentNote.getId())
                        .orElseThrow(() -> new IllegalStateException("Заметка не найдена"));
                note.setTitle(title);
                note.setContent(content);
                noteService.updateNote(note);
                log.info("Заметка успешно обновлена");
            }
            
            refreshNoteList();
            clearFields();
            currentNote = null;
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
    
    private void loadNote(NoteListItem item) {
        if (item != null) {
            try {
                Note note = noteService.getNoteById(item.getId())
                        .orElseThrow(() -> new IllegalStateException("Заметка не найдена"));
                
                currentNote = item;
                titleField.setText(note.getTitle());
                contentArea.setText(note.getContent());
            } catch (Exception e) {
                exceptionHandler.handleException(e);
            }
        }
    }
    
    private void filterNotes(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            refreshNoteList();
        } else {
            String search = searchText.toLowerCase();
            List<NoteListItem> filtered = noteItems.stream()
                    .filter(item -> item.getTitle().toLowerCase().contains(search))
                    .collect(Collectors.toList());
            noteItems.setAll(filtered);
        }
    }
    
    private void refreshNoteList() {
        try {
            List<NoteListItem> items = noteService.getAllNotes().stream()
                    .map(NoteListItem::new)
                    .sorted(Comparator.comparing(NoteListItem::getUpdatedAt).reversed())
                    .collect(Collectors.toList());
            noteItems.setAll(items);
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }
    
    private void showInfo(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Очищает поля ввода.
     */
    private void clearFields() {
        titleField.clear();
        contentArea.clear();
        log.debug("Поля ввода очищены");
    }
} 