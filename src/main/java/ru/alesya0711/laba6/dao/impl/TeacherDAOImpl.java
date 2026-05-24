package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.TeacherDAO;
import ru.alesya0711.laba6.model.Teacher;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.Optional;

/**
 * Реализация интерфейса {@link TeacherDAO} для работы с таблицей преподавателей.
 *
 * <p>Предоставляет методы для аутентификации преподавателя при входе в систему
 * и обновления данных его профиля.
 */
public class TeacherDAOImpl implements TeacherDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public TeacherDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Выполняет аутентификацию преподавателя по логину и паролю.
     *
     * @param username логин (имя пользователя) преподавателя
     * @param password пароль преподавателя
     * @return {@link Optional} с объектом {@link Teacher}, если учётные данные верны;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается по ключу {@code sql.teacher.authenticate}.
     *           Сравнение пароля выполняется на уровне базы данных.
     */
    public Optional<Teacher> authenticate(String username, String password) {
        String sql = SqlStatements.get("sql.teacher.authenticate");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при авторизации: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Обновляет данные профиля преподавателя в базе данных.
     *
     * @param teacher объект {@link Teacher} с обновлёнными полями и установленным {@code teacherId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается по ключу {@code sql.teacher.update}.
     *           Метод возвращает количество обновлённых строк для отладки.
     */
    @Override
    public void update(Teacher teacher) {
        String sql = SqlStatements.get("sql.teacher.update");

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, teacher.getLastName());
            ps.setString(2, teacher.getFirstName());
            ps.setString(3, teacher.getMiddleName());
            ps.setString(4, teacher.getEmail());
            ps.setLong(5, teacher.getSubjectId());
            ps.setBytes(6, teacher.getPhoto());
            ps.setLong(7, teacher.getTeacherId());

            int rowsAffected = ps.executeUpdate();
            System.out.println("Обновлено строк в БД: " + rowsAffected);

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении преподавателя: " + e.getMessage(), e);
        }
    }

    public Optional<Teacher> findByUsername(String username) {
        String sql = "SELECT * FROM teachers WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link Teacher}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link Teacher}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getLong("teacher_id"));
        t.setLastName(rs.getString("last_name"));
        t.setFirstName(rs.getString("first_name"));
        t.setMiddleName(rs.getString("middle_name"));
        t.setEmail(rs.getString("email"));
        t.setSubjectId(rs.getLong("subject_id"));
        t.setPhoto(rs.getBytes("photo"));
        return t;
    }
}