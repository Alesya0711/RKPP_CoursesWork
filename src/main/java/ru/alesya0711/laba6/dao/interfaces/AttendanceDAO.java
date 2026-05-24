package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Attendance;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Attendance} (Посещаемость).
 *
 * <p>Определяет контракт для операций создания, обновления и поиска записей
 * о посещаемости занятий.
 */
public interface AttendanceDAO {

    /**
     * Добавляет новую запись о посещаемости в базу данных.
     *
     * @param attendance объект {@link Attendance}, содержащий данные для добавления
     * @return сгенерированный уникальный идентификатор новой записи
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Long add(Attendance attendance);

    /**
     * Обновляет существующую запись о посещаемости в базе данных.
     *
     * @param attendance объект {@link Attendance} с обновлёнными данными и установленным ID
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    void update(Attendance attendance);

    /**
     * Возвращает список всех записей о посещаемости для указанного занятия.
     *
     * @param lessonId уникальный идентификатор занятия
     * @return список объектов {@link Attendance}; пустой список, если записи не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     */
    List<Attendance> findByLessonId(Long lessonId);

    /**
     * Возвращает список всех записей о посещаемости для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link Attendance}; пустой список, если записи не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     */
    List<Attendance> findByStudentId(Long studentId);
}