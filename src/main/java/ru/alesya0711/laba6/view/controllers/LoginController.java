package ru.alesya0711.laba6.view.controllers;

import lombok.extern.slf4j.Slf4j;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.alesya0711.laba6.dao.impl.TeacherDAOImpl;
import ru.alesya0711.laba6.model.Teacher;
import ru.alesya0711.laba6.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Контроллер окна авторизации преподавателя.
 * Реализует аутентификацию через СУБД PostgreSQL и локализацию интерфейса.
 */
@Slf4j
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private Label languageLabel;
    @FXML private Label titleLabel;

    @FXML
    private ResourceBundle resources;
    ResourceBundle bundle = ResourceBundle.getBundle(
            "ru.alesya0711.laba6.resources.strings",
            Locale.getDefault()
    );

    /**
     * Инициализация контроллера после загрузки FXML.
     * Настраивает фильтры ввода, привязку свойств и язык по умолчанию.
     */
    @FXML
    public void initialize() {
        log.info("Инициализация LoginController");

        initializeLanguageComboBox();

        // Фильтрация ввода логина: только латиница, цифры и подчеркивание
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-Z0-9_]*")) {
                usernameField.setText(newVal.replaceAll("[^a-zA-Z0-9_]", ""));
            }
        });

        // Блокировка кнопки входа при пустых полях или коротком логине
        loginButton.disableProperty().bind(
                usernameField.textProperty().isEqualTo("")
                        .or(usernameField.textProperty().length().lessThan(3))
                        .or(passwordField.textProperty().isEqualTo(""))
        );

        updateUI();
        log.debug("Интерфейс авторизации настроен");
    }

    /**
     * Заполняет ComboBox выбора языка и устанавливает текущую локаль.
     */
    private void initializeLanguageComboBox() {
        log.debug("Заполнение списка языков");
        languageComboBox.getItems().addAll("English", "Русский", "Deutsch");

        Locale current = Locale.getDefault();
        String defaultLang = "English";

        if (current.getLanguage().equals("ru")) {
            defaultLang = "Русский";
        } else if (current.getLanguage().equals("de")) {
            defaultLang = "Deutsch";
        }

        languageComboBox.setValue(defaultLang);
        log.debug("Язык по умолчанию: {}", defaultLang);

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) changeLanguage(newVal);
        });
    }

    /**
     * Меняет локаль приложения и перезагружает ресурсы.
     * @param language название выбранного языка
     */
    private void changeLanguage(String language) {
        log.info("Смена языка интерфейса на: {}", language);
        Locale newLocale;

        switch (language) {
            case "Русский" -> newLocale = new Locale("ru", "RU");
            case "Deutsch" -> newLocale = new Locale("de", "DE");
            default -> newLocale = Locale.ENGLISH;
        }

        Locale.setDefault(newLocale);

        try {
            resources = ResourceBundle.getBundle("ru.alesya0711.laba6.resources.strings", newLocale);
            log.debug("Ресурсы для локали {} загружены успешно", newLocale);
            updateUI();
        } catch (Exception e) {
            log.error("Ошибка загрузки ресурсов для локали {}", newLocale, e);
        }
    }

    /**
     * Обновляет текстовые элементы интерфейса согласно текущим ресурсам.
     */
    private void updateUI() {
        if (resources == null) {
            log.warn("Ресурсы не инициализированы, обновление UI пропущено");
            return;
        }

        try {
            titleLabel.setText(resources.getString("login.title"));
            languageLabel.setText(resources.getString("app.language"));
            loginButton.setText(resources.getString("login.button"));
            usernameField.setPromptText(resources.getString("login.username"));
            passwordField.setPromptText(resources.getString("login.password"));

            if (titleLabel.getScene() != null) {
                Stage stage = (Stage) titleLabel.getScene().getWindow();
                stage.setTitle(resources.getString("app.title"));
            }
            log.debug("Тексты интерфейса обновлены");
        } catch (Exception e) {
            log.error("Ошибка обновления текстов интерфейса", e);
        }
    }

    /**
     * Обработчик нажатия кнопки "Войти".
     * Выполняет аутентификацию через СУБД и переход к главной панели.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            log.warn("Попытка входа с пустым логином");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.login.error"));
            return;
        }

        log.info("Попытка входа пользователя: {}", username);

        try {
            log.debug("Вызов DatabaseConnection.initConnection...");
            DatabaseConnection.initConnection(username, password);
            log.info("Соединение с БД установлено для пользователя {}", username);

            TeacherDAOImpl teacherDAO = new TeacherDAOImpl();
            Optional<Teacher> teacherOpt = teacherDAO.findByUsername(username);

            if (teacherOpt.isPresent()) {
                Teacher teacher = teacherOpt.get();
                log.info("Успешная аутентификация: {} (ID: {})", username, teacher.getTeacherId());

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/teacher-panel.fxml"),
                        resources
                );
                Parent root = loader.load();
                loader.<TeacherPanelController>getController().setCurrentTeacher(teacher);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.setScene(new Scene(root, 1100, 700));
                log.debug("Основное окно отображено");

            } else {
                log.warn("Роль {} существует в БД, но запись в таблице teachers не найдена", username);
                showAlert(bundle.getString("alert.login.vnimanie"), bundle.getString("alert.login.error2"));
                DatabaseConnection.closeConnection();
            }

        } catch (SQLException e) {
            log.error("Ошибка подключения для {}: {}", username, e.getMessage());
            showAlert(bundle.getString("alert.error.bd"), bundle.getString("alert.login.error3")+"\n" + e.getMessage());
            DatabaseConnection.closeConnection();

        } catch (Exception e) {
            log.error("Непредвиденная ошибка при входе пользователя {}", username, e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.login.error4") + e.getMessage());
            DatabaseConnection.closeConnection();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}