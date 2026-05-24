package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.CourseDAO;
import ru.alesya0711.laba6.model.Course;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link CourseDAO} для работы с таблицей учебных курсов.
 *
 * <p>Предоставляет методы для поиска курсов по уникальному идентификатору
 * и по идентификатору преподавателя.
 */
public class CourseDAOImpl implements CourseDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public CourseDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Находит учебный курс по уникальному идентификатору.
     *
     * @param id идентификатор курса в базе данных
     * @return {@link Optional} с объектом {@link Course}, если курс найден;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.course.get_by_id}
     */
    @Override
    public Optional<Course> getById(Long id) {
        String sql = SqlStatements.get("sql.course.get_by_id");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске курса по ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает список всех курсов, принадлежащих указанному преподавателю.
     *
     * @param teacherId уникальный идентификатор преподавателя
     * @return список объектов {@link Course}; пустой список, если курсы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.course.find_by_teacher}
     */
    @Override
    public List<Course> findByTeacherId(Long teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.course.find_by_teacher");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске курсов преподавателя: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Course}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Course}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getLong("course_id"));
        c.setTeacherId(rs.getLong("teacher_id"));
        c.setCourseName(rs.getString("course_name"));
        c.setCourseDescription(rs.getString("course_description"));
        return c;
    }
}