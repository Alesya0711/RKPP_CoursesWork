package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Group;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Group} (Учебная группа).
 *
 * <p>Определяет контракт для операций поиска групп по уникальному идентификатору
 * и по идентификатору учебного курса.
 */
public interface GroupDAO {

    /**
     * Находит учебную группу по уникальному идентификатору.
     *
     * @param id идентификатор группы в базе данных
     * @return {@link Optional} с объектом {@link Group}, если группа найдена;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Optional<Group> getById(Long id);

    /**
     * Возвращает список всех учебных групп, принадлежащих указанному курсу.
     *
     * @param courseId уникальный идентификатор учебного курса
     * @return список объектов {@link Group}; пустой список, если группы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    List<Group> findByCourseId(Long courseId);
}