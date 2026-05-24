package ru.alesya0711.laba6.util;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DatabaseConnection {
    private static Connection connection;
    private static String dbUrlBase;
    private static String dbName;

    static {
        try (InputStream is = DatabaseConnection.class.getResourceAsStream("/ru/alesya0711/laba6/config.properties")) {
            Properties prop = new Properties();
            if (is != null) prop.load(is);
            dbUrlBase = prop.getProperty("db.url");
            dbName = prop.getProperty("db.name");
            log.debug("Загружены настройки подключения: url={}, db={}", dbUrlBase, dbName);
        } catch (IOException e) {
            log.error("Ошибка загрузки config.properties", e);
        }
    }

    private DatabaseConnection() {}

    public static void initConnection(String user, String password) throws SQLException {
        if (connection != null && !connection.isClosed()) closeConnection();

        String fullUrl = dbUrlBase + dbName;
        log.info("Подключение к {} пользователем {}", fullUrl, user);
        connection = DriverManager.getConnection(fullUrl, user, password);
        log.info("Соединение установлено успешно");
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Соединение не инициализировано. Вызовите initConnection()");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Соединение закрыто");
            } catch (SQLException e) {
                log.error("Ошибка закрытия соединения", e);
            }
        }
    }
}