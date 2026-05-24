package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.IndividualAssignment;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link IndividualAssignment} (Индивидуальное задание).
 *
 * <p>Определяет контракт для операций создания, обновления и поиска индивидуальных заданий.
 */
public interface IndividualAssignmentDAO {

    /**
     * Добавляет новое индивидуальное задание в базу данных.
     *
     * @param assignment объект {@link IndividualAssignment}, содержащий данные для сохранения
     * @return сгенерированный уникальный идентификатор новой записи
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Long add(IndividualAssignment assignment);

    /**
     * Обновляет существующее индивидуальное задание в базе данных.
     *
     * @param assignment объект {@link IndividualAssignment} с обновлёнными данными и установленным {@code assignmentId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    void update(IndividualAssignment assignment);

    /**
     * Возвращает список всех активных индивидуальных заданий для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link IndividualAssignment}; пустой список, если задания не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     *
     * @implNote Запрос содержит фильтр {@code WHERE is_active = true}
     */
    List<IndividualAssignment> findByStudentId(Long studentId);
}