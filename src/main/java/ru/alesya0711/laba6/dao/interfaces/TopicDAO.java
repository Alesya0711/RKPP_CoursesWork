package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Topic;
import java.util.List;

/**
 * Интерфейс доступа к данным для сущности {@link Topic} (Тема курса).
 *
 * <p>Определяет контракт для поиска учебных тем, принадлежащих указанному курсу.
 */
public interface TopicDAO {

    /**
     * Возвращает список всех тем, относящихся к указанному учебному курсу.
     *
     * @param courseId уникальный идентификатор учебного курса
     * @return список объектов {@link Topic}; пустой список, если темы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    List<Topic> findByCourseId(Long courseId);
}