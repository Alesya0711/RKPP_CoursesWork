package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.TopicDAO;
import ru.alesya0711.laba6.model.Topic;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link TopicDAO} для работы с таблицей тем курсов.
 *
 * <p>Предоставляет метод для поиска тем, принадлежащих указанному учебному курсу.
 */
public class TopicDAOImpl implements TopicDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public TopicDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Возвращает список всех тем, относящихся к указанному учебному курсу.
     *
     * @param courseId уникальный идентификатор курса
     * @return список объектов {@link Topic}; пустой список, если темы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.topic.find_by_course}
     */
    @Override
    public List<Topic> findByCourseId(Long courseId) {
        List<Topic> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.topic.find_by_course");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске тем курса: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Topic}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Topic}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Topic mapRow(ResultSet rs) throws SQLException {
        Topic t = new Topic();
        t.setTopicId(rs.getLong("topic_id"));
        t.setCourseId(rs.getLong("course_id"));
        t.setTopicName(rs.getString("topic_name"));
        t.setTopicDescription(rs.getString("topic_description"));
        return t;
    }
}