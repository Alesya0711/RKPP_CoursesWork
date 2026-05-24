package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Course;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Course} (Учебный курс).
 *
 * <p>Определяет контракт для операций поиска курсов по уникальному идентификатору
 * и по идентификатору преподавателя.
 */
public interface CourseDAO {

    /**
     * Находит учебный курс по уникальному идентификатору.
     *
     * @param id идентификатор курса в базе данных
     * @return {@link Optional} с объектом {@link Course}, если курс найден;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Optional<Course> getById(Long id);

    /**
     * Возвращает список всех курсов, принадлежащих указанному преподавателю.
     *
     * @param teacherId уникальный идентификатор преподавателя
     * @return список объектов {@link Course}; пустой список, если курсы не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    List<Course> findByTeacherId(Long teacherId);
}