package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.LessonDAO;
import ru.alesya0711.laba6.model.Lesson;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link LessonDAO} для работы с таблицей учебных занятий.
 *
 * <p>Предоставляет методы для добавления новых занятий и поиска занятий по идентификатору темы.
 */
public class LessonDAOImpl implements LessonDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public LessonDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Добавляет новое учебное занятие в базу данных.
     *
     * @param lesson объект {@link Lesson} с заполненными полями для сохранения
     * @return сгенерированный уникальный идентификатор нового занятия, или {@code null} в случае ошибки
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.lesson.add}
     */
    @Override
    public Long add(Lesson lesson) {
        String sql = SqlStatements.get("sql.function.add_lesson");

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.BIGINT);

            cstmt.setLong(2, lesson.getTopicId());
            cstmt.setInt(3, lesson.getLessonNumber());
            cstmt.setDate(4, Date.valueOf(lesson.getLessonDate()));
            cstmt.setString(5, lesson.getLessonType());

            cstmt.execute();
            Long generatedId = cstmt.getLong(1);
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении занятия: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает список всех занятий, принадлежащих указанной теме курса.
     *
     * @param topicId уникальный идентификатор темы
     * @return список объектов {@link Lesson}; пустой список, если занятия не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.lesson.find_by_topic}
     */
    @Override
    public List<Lesson> findByTopicId(Long topicId) {
        List<Lesson> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.lesson.find_by_topic");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, topicId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске занятий по теме: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Lesson}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Lesson}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Lesson mapRow(ResultSet rs) throws SQLException {
        Lesson l = new Lesson();
        l.setLessonId(rs.getLong("lesson_id"));
        l.setTopicId(rs.getLong("topic_id"));
        l.setLessonNumber(rs.getInt("lesson_number"));
        l.setLessonDate(rs.getDate("lesson_date").toLocalDate());
        l.setLessonType(rs.getString("lesson_type"));
        return l;
    }
}