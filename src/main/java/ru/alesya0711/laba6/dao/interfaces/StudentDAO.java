package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Student} (Студент).
 *
 * <p>Определяет методы для поиска студентов по уникальному идентификатору
 * и получения полного списка студентов из базы данных.
 */
public interface StudentDAO {

    /**
     * Находит студента по уникальному идентификатору.
     *
     * @param id идентификатор студента в базе данных
     * @return {@link Optional} с объектом {@link Student}, если студент найден;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Optional<Student> getById(Long id);

    /**
     * Возвращает список всех студентов из базы данных.
     *
     * @return список объектов {@link Student}; пустой список, если студенты не найдены
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    List<Student> getAll();
}