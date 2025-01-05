package com.neighbornotebook;

import com.neighbornotebook.service.ThemeService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * Главный класс приложения.
 * Объединяет Spring Boot и JavaFX, инициализирует основные компоненты.
 */
@Slf4j
@SpringBootApplication
public class NeighborNotebookApplication {

    // Минимальные размеры окна
    private static final double MIN_WIDTH = 800;
    private static final double MIN_HEIGHT = 500;

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
    }

    /**
     * Внутренний класс для инициализации JavaFX приложения.
     * Управляет жизненным циклом GUI-части приложения.
     */
    @Slf4j
    public static class JavaFxApplication extends Application {
        private ConfigurableApplicationContext applicationContext;

        @Override
        public void init() {
            this.applicationContext = new SpringApplicationBuilder(NeighborNotebookApplication.class)
                    .run();
        }

        @Override
        public void start(Stage stage) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                fxmlLoader.setControllerFactory(applicationContext::getBean);
                
                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                
                // Применяем базовую тему
                String defaultTheme = applicationContext.getBean(ThemeService.class).getCurrentTheme();
                String themeFile = "light".equals(defaultTheme) ? "light-theme.css" : "code-editor.css";
                scene.getStylesheets().add(getClass().getResource("/styles/" + themeFile).toExternalForm());
                
                // Инициализируем сервис тем
                ThemeService themeService = applicationContext.getBean(ThemeService.class);
                themeService.setScene(scene);
                
                // Устанавливаем минимальные размеры окна
                stage.setMinWidth(MIN_WIDTH);
                stage.setMinHeight(MIN_HEIGHT);
                
                stage.setTitle("Neighbor Notebook");
                stage.setScene(scene);
                stage.show();
                
                log.info("Приложение успешно запущено");
            } catch (Exception e) {
                log.error("Ошибка при запуске приложения", e);
                Platform.exit();
            }
        }

        @Override
        public void stop() {
            applicationContext.close();
            Platform.exit();
        }
    }
} 