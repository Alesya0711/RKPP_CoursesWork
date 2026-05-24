package ru.alesya0711.laba6.dao.impl;

import ru.alesya0711.laba6.dao.interfaces.FinalWorkDAO;
import ru.alesya0711.laba6.model.FinalWork;
import ru.alesya0711.laba6.util.DatabaseConnection;
import ru.alesya0711.laba6.util.SqlStatements;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация интерфейса {@link FinalWorkDAO} для работы с таблицей итоговых работ.
 *
 * <p>Предоставляет методы для выполнения операций CRUD и мягкого удаления
 * над сущностью итоговой работы в базе данных PostgreSQL.
 */
public class FinalWorkDAOImpl implements FinalWorkDAO {

    /** Соединение с базой данных, используемое для выполнения запросов */
    private final Connection connection;

    /**
     * Создаёт экземпляр DAO и устанавливает соединение с базой данных.
     *
     * @throws SQLException если не удалось получить соединение через {@link DatabaseConnection}
     */
    public FinalWorkDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Добавляет новую итоговую работу в базу данных.
     *
     * @param finalWork объект {@link FinalWork} с заполненными полями для сохранения
     * @return сгенерированный уникальный идентификатор новой записи, или {@code null} в случае ошибки
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Автоматически устанавливает статус {@code is_active = true} для новой записи.
     *           SQL-запрос загружается по ключу {@code sql.final_work.add}.
     */
    @Override
    public Long add(FinalWork finalWork) {
        String sql = SqlStatements.get("sql.function.add_final_work");

        try (CallableStatement cstmt = connection.prepareCall(sql)) {
            cstmt.registerOutParameter(1, Types.BIGINT);

            cstmt.setLong(2, finalWork.getCourseId());
            cstmt.setLong(3, finalWork.getStudentId());
            cstmt.setDate(4, finalWork.getExamDate() != null ? Date.valueOf(finalWork.getExamDate()) : null);
            cstmt.setString(5, finalWork.getTicketNumber());

            if (finalWork.getTheoryGrade() != null) cstmt.setInt(6, finalWork.getTheoryGrade());
            else cstmt.setNull(6, Types.INTEGER);

            if (finalWork.getPracticeGrade() != null) cstmt.setInt(7, finalWork.getPracticeGrade());
            else cstmt.setNull(7, Types.INTEGER);

            cstmt.execute();
            Long generatedId = cstmt.getLong(1);
            return generatedId;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении итоговой работы: " + e.getMessage(), e);
        }
    }





    /**
     * Обновляет существующую итоговую работу в базе данных.
     *
     * @param finalWork объект {@link FinalWork} с обновлёнными данными и установленным {@code finalId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Сохраняет текущий статус активности ({@code is_active}).
     *           SQL-запрос загружается по ключу {@code sql.final_work.update}.
     */
    @Override
    public void update(FinalWork finalWork) {
        String sql = SqlStatements.get("sql.final_work.update");

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, finalWork.getCourseId());
            ps.setLong(2, finalWork.getStudentId());
            ps.setDate(3, finalWork.getExamDate() != null ? Date.valueOf(finalWork.getExamDate()) : null);
            ps.setString(4, finalWork.getTicketNumber());
            if (finalWork.getTheoryGrade() != null) ps.setInt(5, finalWork.getTheoryGrade()); else ps.setNull(5, Types.INTEGER);
            if (finalWork.getPracticeGrade() != null) ps.setInt(6, finalWork.getPracticeGrade()); else ps.setNull(6, Types.INTEGER);
            ps.setBoolean(7, finalWork.isActive());
            ps.setLong(8, finalWork.getFinalId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении итоговой работы: " + e.getMessage(), e);
        }
    }

    /**
     * Возвращает список всех <b>активных</b> итоговых работ для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link FinalWork}; пустой список, если активные работы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Запрос содержит фильтр {@code WHERE is_active = true}.
     *           SQL-запрос загружается по ключу {@code sql.final_work.find_by_student}.
     */
    @Override
    public List<FinalWork> findByStudentId(Long studentId) {
        List<FinalWork> list = new ArrayList<>();
        String sql = SqlStatements.get("sql.final_work.find_by_student");

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске итоговых работ студента: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Выполняет мягкое удаление итоговой работы (устанавливает {@code is_active = false}).
     *
     * @param finalId уникальный идентификатор работы для удаления
     * @throws SQLException если произошла ошибка при выполнении SQL-запроса
     *
     * @implNote Физическая запись в БД сохраняется, но помечается как неактивная.
     *           SQL-запрос загружается по ключу {@code sql.final_work.soft_delete}.
     */
    public void softDelete(Long finalId) throws SQLException {
        String sql = SqlStatements.get("sql.final_work.soft_delete");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, finalId);
            ps.executeUpdate();
        }
    }

    /**
     * Преобразует строку результата запроса {@link ResultSet} в объект {@link FinalWork}.
     *
     * @param rs результат запроса, указывающий на текущую строку данных
     * @return заполненный объект {@link FinalWork}
     * @throws SQLException если произошла ошибка при чтении данных из {@link ResultSet}
     */
    private FinalWork mapRow(ResultSet rs) throws SQLException {
        FinalWork f = new FinalWork();
        f.setFinalId(rs.getLong("final_id"));
        f.setCourseId(rs.getLong("course_id"));
        f.setStudentId(rs.getLong("student_id"));
        Date date = rs.getDate("exam_date");
        f.setExamDate(date != null ? date.toLocalDate() : null);
        f.setTicketNumber(rs.getString("ticket_number"));
        f.setTheoryGrade(rs.getObject("theory_grade") != null ? rs.getInt("theory_grade") : null);
        f.setPracticeGrade(rs.getObject("practice_grade") != null ? rs.getInt("practice_grade") : null);
        f.setActive(rs.getBoolean("is_active"));
        return f;
    }
}