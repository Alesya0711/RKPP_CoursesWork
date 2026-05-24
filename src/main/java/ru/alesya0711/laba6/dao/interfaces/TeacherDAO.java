package ru.alesya0711.laba6.dao.interfaces;

import ru.alesya0711.laba6.model.Teacher;
import java.util.Optional;

/**
 * Интерфейс доступа к данным для сущности {@link Teacher} (Преподаватель).
 *
 * <p>Определяет контракт для операций обновления данных профиля преподавателя
 * и аутентификации при входе в систему.
 */
public interface TeacherDAO {

    /**
     * Обновляет данные профиля преподавателя в базе данных.
     *
     * @param teacher объект {@link Teacher} с обновлёнными полями и установленным {@code teacherId}
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    void update(Teacher teacher);

    /**
     * Выполняет аутентификацию преподавателя по логину и паролю.
     *
     * @param username логин (имя пользователя) преподавателя
     * @param password пароль преподавателя
     * @return {@link Optional} с объектом {@link Teacher}, если учётные данные верны;
     *         пустой {@link Optional} в противном случае
     * @throws RuntimeException если произошла ошибка при выполнении SQL-запроса
     */
    Optional<Teacher> authenticate(String username, String password);
}