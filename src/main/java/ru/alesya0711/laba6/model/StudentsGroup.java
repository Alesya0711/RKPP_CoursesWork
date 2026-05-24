package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель связи студента с учебной группой (отношение многие-ко-многим).
 *
 * <p>Представляет запись в промежуточной таблице {@code students_groups},
 * которая связывает студентов с учебными группами. Один студент может входить
 * в несколько групп, и одна группа может содержать несколько студентов.
 */
public class StudentsGroup {

    /** Идентификатор студента (внешний ключ на таблицу students) */
    private Long studentId;

    /** Объект студента (для удобства работы с данными) */
    private Student student;

    /** Идентификатор учебной группы (внешний ключ на таблицу groups) */
    private Long groupId;

    /** Объект учебной группы (для удобства работы с данными) */
    private Group group;

    /**
     * Создаёт пустой объект связи студента с группой.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public StudentsGroup() {}

    /**
     * Создаёт объект связи с указанными идентификаторами.
     *
     * @param studentId идентификатор студента
     * @param groupId идентификатор учебной группы
     */
    public StudentsGroup(Long studentId, Long groupId) {
        this.studentId = studentId;
        this.groupId = groupId;
    }

    /**
     * Возвращает идентификатор студента.
     *
     * @return ID студента
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Устанавливает идентификатор студента.
     *
     * @param studentId новый ID студента
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * Возвращает объект студента, связанный с записью.
     *
     * @return объект {@link Student} или {@code null}, если не установлен
     */
    public Student getStudent() {
        return student;
    }

    /**
     * Устанавливает объект студента для удобства работы с данными.
     *
     * @param student объект {@link Student}
     */
    public void setStudent(Student student) {
        this.student = student;
    }

    /**
     * Возвращает идентификатор учебной группы.
     *
     * @return ID группы
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * Устанавливает идентификатор учебной группы.
     *
     * @param groupId новый ID группы
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Возвращает объект учебной группы, связанный с записью.
     *
     * @return объект {@link Group} или {@code null}, если не установлен
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Устанавливает объект учебной группы для удобства работы с данными.
     *
     * @param group объект {@link Group}
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * Сравнивает два объекта {@link StudentsGroup} по составному ключу.
     *
     * <p>Две записи считаются равными, если совпадают оба идентификатора:
     * {@code studentId} и {@code groupId}. Это позволяет использовать объекты
     * в коллекциях и предотвращать дублирование связей.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковые
     *         {@code studentId} и {@code groupId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentsGroup that = (StudentsGroup) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(groupId, that.groupId);
    }

    /**
     * Возвращает хэш-код на основе составного ключа (студент + группа).
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link StudentsGroup} в хэш-коллекциях
     * (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code studentId} и {@code groupId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentId, groupId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code StudentsGroup{studentId=..., groupId=...}}
     *
     * @return строка с идентификаторами студента и группы
     */
    @Override
    public String toString() {
        return "StudentsGroup{" +
                "studentId=" + studentId +
                ", groupId=" + groupId +
                '}';
    }
}