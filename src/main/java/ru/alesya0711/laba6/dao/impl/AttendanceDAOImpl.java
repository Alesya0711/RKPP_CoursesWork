package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.AttendanceDAO;
import ru.alesya0711.laba6.model.Attendance;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link AttendanceDAO} для работы с таблицей посещаемости.
 *
 * <p>Предоставляет методы для выполнения операций CRUD (Create, Read, Update, Delete)
 * над сущностью посещаемости в базе данных.
 *
 * <p>Основные возможности:
 * <ul>
 *   <li>Добавление новой записи о посещаемости</li>
 *   <li>Обновление существующей записи</li>
 *   <li>Поиск записей по идентификатору занятия</li>
 *   <li>Поиск записей по идентификатору студента</li>
 * </ul>
 */
public class AttendanceDAOImpl implements AttendanceDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public AttendanceDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Добавляет новую запись о посещаемости в базу данных.
     *
     * @param attendance объект {@link Attendance} с заполненными полями для сохранения
     * @return сгенерированный уникальный идентификатор новой записи, или {@code null} в случае ошибки
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.attendance.add}
     */
    @Override
    public Long add(Attendance attendance) {
        String sql = SqlStatements.get("sql.procedure.save_attendance");

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.setLong(1, attendance.getLessonId());
            cstmt.setLong(2, attendance.getStudentId());
            cstmt.setBoolean(3, attendance.getIsPresent() != null ? attendance.getIsPresent() : false);

            cstmt.execute();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении посещаемости: " + e.getMessage(), e);
        }
    }

    /**
     * Обновляет существующую запись о посещаемости в базе данных.
     *
     * @param attendance объект {@link Attendance} с обновлёнными данными и установленным {@code attendanceId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.attendance.update}
     */
    @Override
    public void update(Attendance attendance) {
        String sql = SqlStatements.get("sql.attendance.update");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, attendance.getLessonId());
            ps.setLong(2, attendance.getStudentId());
            ps.setBoolean(3, attendance.getIsPresent() != null ? attendance.getIsPresent() : false);
            ps.setString(4, attendance.getComment());
            ps.setLong(5, attendance.getAttendanceId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении записи о посещаемости: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает список всех записей о посещаемости для указанного занятия.
     *
     * @param lessonId уникальный идентификатор занятия
     * @return список объектов {@link Attendance}, соответствующих заданному {@code lessonId};
     *         пустой список, если записи не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.attendance.find_by_lesson}
     */
    @Override
    public List<Attendance> findByLessonId(Long lessonId) {
        List<Attendance> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.attendance.find_by_lesson");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, lessonId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске посещаемости по занятию: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Возвращает список всех записей о посещаемости для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link Attendance}, соответствующих заданному {@code studentId};
     *         пустой список, если записи не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.attendance.find_by_student}
     */
    @Override
    public List<Attendance> findByStudentId(Long studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.attendance.find_by_student");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске посещаемости по студенту: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Attendance}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Attendance}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getLong("attendance_id"));
        a.setLessonId(rs.getLong("lesson_id"));
        a.setStudentId(rs.getLong("student_id"));
        a.setIsPresent(rs.getBoolean("is_present"));
        a.setComment(rs.getString("comment"));
        return a;
    }
}