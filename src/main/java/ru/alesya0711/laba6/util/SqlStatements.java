package ru.alesya0711.laba6.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Утилитный класс для централизованного управления SQL-запросами.
 */
public class SqlStatements {

    /** Хранилище загруженных пар "ключ-значение" из файла statements.properties */
    private static final Properties statements = new Properties();

    /** Флаг, указывающий, была ли уже выполнена загрузка файла ресурсов */
    private static boolean loaded = false;

    /**
     * Приватный конструктор запрещает создание экземпляров класса.
     *
     * <p>Класс предназначен только для статического использования через
     * метод {@link #get(String)}.
     */
    private SqlStatements() {}

    /**
     * Загружает файл {@code statements.properties} из ресурсов приложения,
     * если это ещё не было сделано.
     *
     * @throws RuntimeException если файл не найден в ресурсах или произошла
     *         ошибка при чтении (например, неверная кодировка или повреждённый файл)
     *
     * @implNote Файл ищется по пути {@code /ru/alesya0711/laba6/statements.properties}
     *           относительно корня classpath.
     */
    private static void load() {
        if (loaded) return;
        try (InputStream in = SqlStatements.class.getResourceAsStream(
                "/ru/alesya0711/laba6/statements.properties")) {
            if (in != null) {
                statements.load(in);
                loaded = true;
            } else {
                throw new IOException("Не найден файл statements.properties в ресурсах приложения");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки SQL-запросов: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает текст SQL-запроса по указанному ключу.
     *
     * <p>Метод автоматически загружает файл ресурсов при первом вызове
     * (ленивая инициализация) и кэширует содержимое для последующих обращений.
     *
     * @param key ключ запроса в формате {@code sql.<entity>.<action>}
     *            (например, {@code sql.student.get_by_id}, {@code sql.course.find_by_teacher})
     * @return текст SQL-запроса с параметрами {@code ?} для использования в
     *         {@link java.sql.PreparedStatement}
     * @throws IllegalArgumentException если запрос с указанным ключом не найден
     *         в файле {@code statements.properties}
     * @throws RuntimeException если произошла ошибка при загрузке файла ресурсов
     *
     * @implNote Возвращаемая строка не модифицируется — все подстановки параметров
     *           должны выполняться через методы {@code PreparedStatement.setXXX()}.
     */
    public static String get(String key) {
        load();
        String sql = statements.getProperty(key);
        if (sql == null) {
            throw new IllegalArgumentException("SQL-запрос не найден для ключа: " + key);
        }
        return sql;
    }
}