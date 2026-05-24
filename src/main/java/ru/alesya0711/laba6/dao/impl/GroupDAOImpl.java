package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.GroupDAO;
import ru.alesya0711.laba6.model.Group;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link GroupDAO} для работы с таблицей учебных групп.
 *
 * <p>Предоставляет методы для поиска групп по уникальному идентификатору
 * и по идентификатору учебного курса.
 *
 * @author Alesya
 * @version 1.0
 * @see GroupDAO
 * @see Group
 * @see SqlStatements
 */
public class GroupDAOImpl implements GroupDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public GroupDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Находит учебную группу по уникальному идентификатору.
     *
     * @param id идентификатор группы в базе данных
     * @return {@link Optional} с объектом {@link Group}, если группа найдена;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.group.get_by_id}
     */
    @Override
    public Optional<Group> getById(Long id) {
        String sql = SqlStatements.get("sql.group.get_by_id");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске группы по ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает список всех учебных групп, принадлежащих указанному курсу.
     *
     * @param courseId уникальный идентификатор учебного курса
     * @return список объектов {@link Group}; пустой список, если группы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.group.find_by_course}
     */
    @Override
    public List<Group> findByCourseId(Long courseId) {
        List<Group> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.group.find_by_course");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске групп курса: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Group}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Group}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Group mapRow(ResultSet rs) throws SQLException {
        Group g = new Group();
        g.setGroupId(rs.getLong("group_id"));
        g.setCourseId(rs.getLong("course_id"));
        g.setGroupName(rs.getString("group_name"));
        g.setDescription(rs.getString("description"));
        g.setStudentCount(rs.getInt("student_count"));
        Date date = rs.getDate("formation_date");
        g.setFormationDate(date != null ? date.toLocalDate() : null);
        return g;
    }
}