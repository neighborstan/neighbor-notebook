package com.neighbornotebook.service;

import javafx.scene.Scene;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.prefs.Preferences;
import java.net.URL;
import java.util.List;

/**
 * Сервис для управления темами приложения.
 */
@Slf4j
@Service
public class ThemeService {
    
    private static final String THEME_KEY = "app.theme";
    private static final String LIGHT_THEME = "light";
    private static final String DARK_THEME = "dark";
    
    private final Preferences preferences;
    private Scene scene;
    private String currentThemePath;
    
    public ThemeService() {
        this.preferences = Preferences.userNodeForPackage(ThemeService.class);
    }
    
    /**
     * Устанавливает сцену для управления темами.
     *
     * @param scene сцена JavaFX
     */
    public void setScene(Scene scene) {
        this.scene = scene;
        applyTheme(getCurrentTheme());
    }
    
    /**
     * Переключает тему между светлой и темной.
     */
    public void toggleTheme() {
        String currentTheme = getCurrentTheme();
        String newTheme = LIGHT_THEME.equals(currentTheme) ? DARK_THEME : LIGHT_THEME;
        
        preferences.put(THEME_KEY, newTheme);
        applyTheme(newTheme);
        
        log.info("Тема изменена на: {}", newTheme);
    }
    
    /**
     * Возвращает текущую тему.
     *
     * @return название текущей темы
     */
    public String getCurrentTheme() {
        return preferences.get(THEME_KEY, DARK_THEME);
    }
    
    /**
     * Проверяет, является ли текущая тема темной.
     *
     * @return true, если текущая тема темная
     */
    public boolean isDarkTheme() {
        return DARK_THEME.equals(getCurrentTheme());
    }
    
    private void applyTheme(String theme) {
        if (scene != null) {
            String themeFile = LIGHT_THEME.equals(theme) ? "light-theme.css" : "code-editor.css";
            URL themeUrl = getClass().getResource("/styles/" + themeFile);
            
            if (themeUrl != null) {
                String cssPath = themeUrl.toExternalForm();
                
                // Если тема уже применена, сначала удаляем её
                if (currentThemePath != null) {
                    scene.getStylesheets().remove(currentThemePath);
                }
                
                // Очищаем все стили и применяем новую тему
                List<String> stylesheets = scene.getStylesheets();
                stylesheets.clear();
                stylesheets.add(cssPath);
                currentThemePath = cssPath;
                
                log.debug("Применена тема: {}", themeFile);
            } else {
                log.error("Не удалось найти файл темы: {}", themeFile);
            }
        } else {
            log.warn("Сцена не инициализирована");
        }
    }
} 