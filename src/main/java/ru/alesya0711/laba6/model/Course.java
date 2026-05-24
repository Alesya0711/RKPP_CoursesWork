package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных учебного курса.
 *
 * <p>Представляет информацию об учебном курсе: название, описание, привязка к преподавателю.
 * Используется для организации учебного процесса и управления расписанием в системе.
 *
 * <p>Основные поля:
 * <ul>
 *   <li>{@code courseId} — уникальный идентификатор курса</li>
 *   <li>{@code teacherId} / {@code teacher} — ссылка на преподавателя</li>
 *   <li>{@code courseName} — название курса</li>
 *   <li>{@code courseDescription} — описание содержания курса</li>
 * </ul>
 *
 * @author Alesya
 * @version 1.0
 * @see Teacher
 */
public class Course {

    /** Уникальный идентификатор учебного курса */
    private Long courseId;

    /** Идентификатор преподавателя, ведущего курс */
    private Long teacherId;

    /** Объект преподавателя (для удобства работы с данными) */
    private Teacher teacher;

    /** Название учебного курса */
    private String courseName;

    /** Описание содержания и целей курса */
    private String courseDescription;

    /**
     * Создаёт пустой объект учебного курса.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Course() {}

    /**
     * Создаёт объект учебного курса с основными параметрами.
     *
     * @param courseId уникальный идентификатор курса
     * @param courseName название курса
     * @param courseDescription описание курса
     */
    public Course(Long courseId, String courseName, String courseDescription) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }

    /**
     * Возвращает уникальный идентификатор учебного курса.
     *
     * @return ID курса
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Устанавливает уникальный идентификатор учебного курса.
     *
     * @param courseId новый ID курса
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Возвращает идентификатор преподавателя, ведущего курс.
     *
     * @return ID преподавателя
     */
    public Long getTeacherId() {
        return teacherId;
    }

    /**
     * Устанавливает идентификатор преподавателя для курса.
     *
     * @param teacherId новый ID преподавателя
     */
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Возвращает объект преподавателя, связанного с курсом.
     *
     * @return объект {@link Teacher} или {@code null}, если не установлен
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * Устанавливает объект преподавателя для удобства работы с данными.
     *
     * @param teacher объект {@link Teacher}
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    /**
     * Возвращает название учебного курса.
     *
     * @return название курса
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Устанавливает название учебного курса.
     *
     * @param courseName новое название курса
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Возвращает описание учебного курса.
     *
     * @return текст описания или {@code null}, если не задан
     */
    public String getCourseDescription() {
        return courseDescription;
    }

    /**
     * Устанавливает описание учебного курса.
     *
     * @param courseDescription текст описания
     */
    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    /**
     * Сравнивает два объекта {@link Course} по уникальному идентификатору.
     *
     * <p>Два курса считаются равными, если их {@code courseId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code courseId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId, course.courseId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора курса.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Course} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code courseId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Course{courseId=..., courseName='...', ...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                '}';
    }
}