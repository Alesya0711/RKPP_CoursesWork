module ru.alesya0711.laba6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires layout;
    requires kernel;
    requires org.slf4j;
    requires static lombok;


    opens ru.alesya0711.laba6 to javafx.fxml;
    opens ru.alesya0711.laba6.view.controllers to javafx.fxml, javafx.base, javafx.controls;
    opens ru.alesya0711.laba6.model to javafx.base, javafx.controls;
    exports ru.alesya0711.laba6;
    exports ru.alesya0711.laba6.util;
    opens ru.alesya0711.laba6.util to javafx.fxml;
}