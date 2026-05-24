package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.StudentDAO;
import ru.alesya0711.laba6.model.Student;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link StudentDAO} для работы с таблицей студентов.
 *
 * <p>Предоставляет методы для получения информации о студентах из базы данных.
 */
public class StudentDAOImpl implements StudentDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public StudentDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Находит студента по уникальному идентификатору.
     *
     * @param id уникальный идентификатор студента
     * @return {@link Optional} с объектом {@link Student}, если студент найден;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.student.get_by_id}
     */
    @Override
    public Optional<Student> getById(Long id) {
        String sql = SqlStatements.get("sql.student.get_by_id");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске студента по ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает список всех студентов из базы данных.
     *
     * @return список объектов {@link Student}; пустой список, если в базе нет студентов
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.student.get_all}
     */
    @Override
    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.student.get_all");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка студентов: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Student}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Student}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getLong("student_id"));
        s.setLastName(rs.getString("last_name"));
        s.setFirstName(rs.getString("first_name"));
        s.setMiddleName(rs.getString("middle_name"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setClassId(rs.getLong("class_id"));
        return s;
    }
}