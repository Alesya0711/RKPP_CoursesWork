package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Lesson;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Lesson} (Учебное занятие).
 * 
 * <p>Определяет контракт для операций добавления новых занятий и поиска 
 * занятий по идентификатору темы.
 */
public interface LessonDAO {
    
    /**
     * Добавляет новое учебное занятие в базу данных.
     * 
     * @param lesson объект {@link Lesson} с заполненными полями для сохранения
     * @return сгенерированный уникальный идентификатор нового занятия
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Long add(Lesson lesson);

    /**
     * Возвращает список всех занятий, принадлежащих указанной теме курса.
     * 
     * @param topicId уникальный идентификатор темы
     * @return список объектов {@link Lesson}; пустой список, если занятия не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    List<Lesson> findByTopicId(Long topicId);
}