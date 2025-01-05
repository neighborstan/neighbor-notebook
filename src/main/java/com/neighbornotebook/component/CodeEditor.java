package com.neighbornotebook.component;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.flowless.VirtualizedScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import lombok.Getter;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Компонент для редактирования кода с подсветкой синтаксиса.
 * Поддерживает различные языки программирования.
 */
public class CodeEditor extends VBox {

    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new",
            "package", "private", "protected", "public", "return",
            "short", "static", "strictfp", "super", "switch",
            "synchronized", "this", "throw", "throws", "transient",
            "try", "void", "volatile", "while", "var"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    @Getter
    private final CodeArea codeArea;
    private final VirtualizedScrollPane<CodeArea> scrollPane;

    public CodeEditor() {
        // Инициализируем CodeArea
        codeArea = new CodeArea();
        codeArea.setWrapText(true);

        // Добавляем базовый CSS класс
        codeArea.getStyleClass().add("code-area");

        // Добавляем нумерацию строк
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        // Настраиваем подсветку синтаксиса
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        // Настраиваем стили
        codeArea.getStylesheets().add(getClass().getResource("/styles/code-editor.css").toExternalForm());

        // Настраиваем контекстное меню
        setupContextMenu();

        // Настраиваем горячие клавиши
        setupKeyBindings();

        // Создаем VirtualizedScrollPane для CodeArea
        scrollPane = new VirtualizedScrollPane<>(codeArea);

        // Настраиваем размеры для корректного скроллинга
        setFillWidth(true);
        setMinHeight(200);
        setPrefHeight(400);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setMinHeight(200);
        scrollPane.setPrefHeight(400);

        // Добавляем ScrollPane в VBox
        getChildren().add(scrollPane);
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem cutItem = new MenuItem("Вырезать");
        cutItem.setOnAction(e -> codeArea.cut());
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));

        MenuItem copyItem = new MenuItem("Копировать");
        copyItem.setOnAction(e -> codeArea.copy());
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));

        MenuItem pasteItem = new MenuItem("Вставить");
        pasteItem.setOnAction(e -> codeArea.paste());
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));

        MenuItem selectAllItem = new MenuItem("Выделить всё");
        selectAllItem.setOnAction(e -> codeArea.selectAll());
        selectAllItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));

        contextMenu.getItems().addAll(cutItem, copyItem, pasteItem, selectAllItem);
        codeArea.setContextMenu(contextMenu);
    }

    private void setupKeyBindings() {
        codeArea.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case V -> codeArea.paste();
                    case C -> codeArea.copy();
                    case X -> codeArea.cut();
                    case A -> codeArea.selectAll();
                    default -> {
                        // Игнорируем остальные сочетания клавиш
                    }
                }
            }
        });
    }

    /**
     * Устанавливает текст в редактор.
     *
     * @param text текст для отображения
     */
    public void setText(String text) {
        codeArea.replaceText(text != null ? text : "");
    }

    /**
     * Возвращает текущий текст из редактора.
     *
     * @return текст из редактора
     */
    public String getText() {
        return codeArea.getText();
    }

    /**
     * Обновляет тему редактора.
     *
     * @param isDark true для темной темы, false для светлой
     */
    public void updateTheme(boolean isDark) {
        codeArea.getStyleClass().removeAll("light", "dark");
        codeArea.getStyleClass().add(isDark ? "dark" : "light");
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass = determineStyleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    /**
     * Определяет стиль для найденного фрагмента текста.
     *
     * @param matcher сопоставитель регулярного выражения
     * @return название CSS класса для стилизации
     */
    private static String determineStyleClass(Matcher matcher) {
        if (matcher.group("KEYWORD") != null) {
            return "keyword";
        }
        if (matcher.group("STRING") != null) {
            return "string";
        }
        if (matcher.group("COMMENT") != null) {
            return "comment";
        }
        return null;
    }
}