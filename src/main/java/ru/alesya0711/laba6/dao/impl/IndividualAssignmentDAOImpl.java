package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.IndividualAssignmentDAO;
import ru.alesya0711.laba6.model.IndividualAssignment;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link IndividualAssignmentDAO} для работы с таблицей индивидуальных заданий.
 *
 * <p>Предоставляет методы для выполнения операций CRUD и мягкого удаления
 * над сущностью индивидуального задания в базе данных.
 */
public class IndividualAssignmentDAOImpl implements IndividualAssignmentDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public IndividualAssignmentDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Добавляет новое индивидуальное задание в базу данных.
     *
     * @param assignment объект {@link IndividualAssignment} с заполненными полями для сохранения
     * @return сгенерированный уникальный идентификатор новой записи, или {@code null} в случае ошибки
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Автоматически устанавливает статус {@code is_active = true} для новой записи.
     *           SQL-запрос загружается по ключу {@code sql.assignment.add}.
     */
    @Override
    public Long add(IndividualAssignment assignment) {
        String sql = SqlStatements.get("sql.assignment.add");

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, assignment.getTopicId());
            ps.setLong(2, assignment.getStudentId());
            ps.setString(3, assignment.getAssignmentName());
            ps.setDate(4, assignment.getAssignmentDate() != null ? Date.valueOf(assignment.getAssignmentDate()) : null);
            if (assignment.getGrade() != null) ps.setInt(5, assignment.getGrade()); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, assignment.getStatus());
            ps.setDate(7, assignment.getSubmissionDate() != null ? Date.valueOf(assignment.getSubmissionDate()) : null);
            ps.setBoolean(8, true);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении индивидуального задания: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Обновляет существующее индивидуальное задание в базе данных.
     *
     * @param assignment объект {@link IndividualAssignment} с обновлёнными данными и установленным {@code assignmentId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Сохраняет текущий статус активности ({@code is_active}).
     *           SQL-запрос загружается по ключу {@code sql.assignment.update}.
     */
    @Override
    public void update(IndividualAssignment assignment) {
        String sql = SqlStatements.get("sql.assignment.update");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, assignment.getTopicId());
            ps.setLong(2, assignment.getStudentId());
            ps.setString(3, assignment.getAssignmentName());
            ps.setDate(4, assignment.getAssignmentDate() != null ? Date.valueOf(assignment.getAssignmentDate()) : null);
            if (assignment.getGrade() != null) ps.setInt(5, assignment.getGrade()); else ps.setNull(5, Types.INTEGER);
            ps.setString(6, assignment.getStatus());
            ps.setDate(7, assignment.getSubmissionDate() != null ? Date.valueOf(assignment.getSubmissionDate()) : null);
            ps.setBoolean(8, assignment.isActive());
            ps.setLong(9, assignment.getAssignmentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении индивидуального задания: " + e.getMessage(), e);
        }
    }

    /**
     * Выполняет мягкое удаление индивидуального задания (устанавливает {@code is_active = false}).
     *
     * @param assignmentId уникальный идентификатор задания для удаления
     * @throws SQLException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Физическая запись в БД сохраняется, но помечается как неактивная.
     *           SQL-запрос загружается по ключу {@code sql.assignment.soft_delete}.
     */
    public void softDelete(Long assignmentId) throws SQLException {
        String sql = SqlStatements.get("sql.assignment.soft_delete");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, assignmentId);
            ps.executeUpdate();
        }
    }

    /**
     * Возвращает список всех <b>активных</b> индивидуальных заданий для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link IndividualAssignment}; пустой список, если активные задания не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Запрос содержит фильтр {@code WHERE is_active = true}.
     *           SQL-запрос загружается по ключу {@code sql.assignment.find_by_student}.
     */
    @Override
    public List<IndividualAssignment> findByStudentId(Long studentId) {
        List<IndividualAssignment> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.assignment.find_by_student");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске заданий студента: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link IndividualAssignment}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link IndividualAssignment}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private IndividualAssignment mapRow(ResultSet rs) throws SQLException {
        IndividualAssignment a = new IndividualAssignment();
        a.setAssignmentId(rs.getLong("assignment_id"));
        a.setTopicId(rs.getLong("topic_id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setAssignmentName(rs.getString("assignment_name"));
        Date date = rs.getDate("assignment_date");
        a.setAssignmentDate(date != null ? date.toLocalDate() : null);
        a.setGrade(rs.getObject("grade") != null ? rs.getInt("grade") : null);
        a.setStatus(rs.getString("status"));
        Date subDate = rs.getDate("submission_date");
        a.setSubmissionDate(subDate != null ? subDate.toLocalDate() : null);
        a.setActive(rs.getBoolean("is_active"));

        return a;
    }
}