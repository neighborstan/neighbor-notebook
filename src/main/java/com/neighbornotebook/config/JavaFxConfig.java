package com.neighbornotebook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import javafx.fxml.FXMLLoader;

/**
 * Конфигурация JavaFX компонентов.
 * Настраивает базовые компоненты для работы с JavaFX в Spring-контексте.
 */
@Configuration
public class JavaFxConfig {

    /**
     * Создает экземпляр FXMLLoader для загрузки FXML файлов.
     *
     * @return настроенный загрузчик FXML
     */
    @Bean
    public FXMLLoader fxmlLoader() {
        return new FXMLLoader();
    }
} 