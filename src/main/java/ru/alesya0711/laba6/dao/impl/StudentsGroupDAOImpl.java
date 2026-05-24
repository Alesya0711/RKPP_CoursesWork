package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.StudentsGroupDAO;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link StudentsGroupDAO} для работы с таблицей связи студентов и учебных групп.
 *
 * <p>Предоставляет методы для получения списка идентификаторов студентов, зачисленных в указанную группу.
 */
public class StudentsGroupDAOImpl implements StudentsGroupDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public StudentsGroupDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Возвращает список уникальных идентификаторов студентов, входящих в указанную учебную группу.
     *
     * @param groupId уникальный идентификатор группы
     * @return список ID студентов; пустой список, если группа пуста или не найдена
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote SQL-запрос загружается из файла ресурсов по ключу {@code sql.students_group.get_ids_by_group}
     */
    @Override
    public List<Long> getStudentIdsByGroupId(Long groupId) {
        List<Long> ids = new ArrayList<>();
        String sql = SqlStatements.get("sql.students_group.get_ids_by_group");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, groupId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getLong("student_id"));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка студентов группы: " + e.getMessage(), e);
        }
        return ids;
    }
}