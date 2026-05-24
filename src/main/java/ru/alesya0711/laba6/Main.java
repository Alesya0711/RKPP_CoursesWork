package ru.alesya0711.laba6;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale currentLocale = Locale.getDefault();
        Locale.setDefault(currentLocale);

        ResourceBundle bundle = ResourceBundle.getBundle(
                "ru.alesya0711.laba6.resources.strings",
                currentLocale
        );
        log.info("Приложение запущено");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml"),
                bundle
        );

        Parent root = loader.load();

        String appTitle = bundle.getString("app.title");
        primaryStage.setTitle(appTitle);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}