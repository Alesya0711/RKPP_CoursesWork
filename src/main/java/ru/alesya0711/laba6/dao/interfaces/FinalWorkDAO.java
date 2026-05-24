package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.FinalWork;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link FinalWork} (Итоговая работа).
 *
 * <p>Определяет контракт для операций создания, обновления и поиска записей
 * об итоговых работах студентов.
 */
public interface FinalWorkDAO {

    /**
     * Добавляет новую итоговую работу в базу данных.
     *
     * @param finalWork объект {@link FinalWork}, содержащий данные для сохранения
     * @return сгенерированный уникальный идентификатор новой записи
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Long add(FinalWork finalWork);

    /**
     * Обновляет существующую итоговую работу в базе данных.
     *
     * @param finalWork объект {@link FinalWork} с обновлёнными данными и установленным {@code finalId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    void update(FinalWork finalWork);

    /**
     * Возвращает список всех активных итоговых работ для указанного студента.
     *
     * @param studentId уникальный идентификатор студента
     * @return список объектов {@link FinalWork}; пустой список, если активные работы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении запроса
     *
     * @implNote Запрос содержит фильтр {@code WHERE is_active = true}
     */
    List<FinalWork> findByStudentId(Long studentId);
}