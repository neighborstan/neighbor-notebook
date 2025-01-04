package com.neighbornotebook;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Главный класс приложения.
 * Объединяет Spring Boot и JavaFX, инициализирует основные компоненты.
 */
@SpringBootApplication
public class NeighborNotebookApplication {

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

        @Value("${app.window.title}")
        private String windowTitle;

        @Value("${app.window.width}")
        private int windowWidth;

        @Value("${app.window.height}")
        private int windowHeight;

        @Override
        public void init() {
            log.info("Инициализация Spring контекста");
            applicationContext = new SpringApplicationBuilder(NeighborNotebookApplication.class)
                    .headless(false)
                    .run();
            log.info("Spring контекст успешно инициализирован");
        }

        @Override
        public void start(Stage stage) throws Exception {
            log.debug("Загрузка главного окна приложения");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, windowWidth, windowHeight);
            
            stage.setTitle(windowTitle);
            stage.setScene(scene);
            stage.show();
            log.info("Приложение успешно запущено");
        }

        @Override
        public void stop() {
            log.info("Завершение работы приложения");
            applicationContext.close();
            Platform.exit();
            log.info("Приложение успешно завершено");
        }
    }
} 