package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных посещаемости студента на занятии.
 *
 * <p>Представляет запись о присутствии или отсутствии студента на конкретном уроке.
 * Используется для учёта посещаемости в рамках системы управления учебным процессом.
 */
public class Attendance {

    /** Уникальный идентификатор записи о посещаемости */
    private Long attendanceId;

    /** Идентификатор занятия (для связи с таблицей lessons) */
    private Long lessonId;

    /** Объект занятия (для удобства работы с данными) */
    private Lesson lesson;

    /** Идентификатор студента (для связи с таблицей students) */
    private Long studentId;

    /** Объект студента (для удобства работы с данными) */
    private Student student;

    /** Флаг присутствия: {@code true} — студент присутствовал, {@code false} — отсутствовал */
    private Boolean isPresent;

    /** Дополнительный комментарий к записи (например, причина отсутствия) */
    private String comment;

    /**
     * Создаёт пустой объект посещаемости.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Attendance() {}

    /**
     * Создаёт объект посещаемости с основными параметрами.
     *
     * @param attendanceId уникальный идентификатор записи
     * @param lessonId идентификатор занятия
     * @param studentId идентификатор студента
     * @param isPresent флаг присутствия студента
     */
    public Attendance(Long attendanceId, Long lessonId, Long studentId, Boolean isPresent) {
        this.attendanceId = attendanceId;
        this.lessonId = lessonId;
        this.studentId = studentId;
        this.isPresent = isPresent;
    }

    /**
     * Возвращает уникальный идентификатор записи о посещаемости.
     *
     * @return ID записи
     */
    public Long getAttendanceId() {
        return attendanceId;
    }

    /**
     * Устанавливает уникальный идентификатор записи о посещаемости.
     *
     * @param attendanceId новый ID записи
     */
    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    /**
     * Возвращает идентификатор занятия.
     *
     * @return ID занятия
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Устанавливает идентификатор занятия.
     *
     * @param lessonId новый ID занятия
     */
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    /**
     * Возвращает объект занятия, связанный с записью.
     *
     * @return объект {@link Lesson} или {@code null}, если не установлен
     */
    public Lesson getLesson() {
        return lesson;
    }

    /**
     * Устанавливает объект занятия для удобства работы с данными.
     *
     * @param lesson объект {@link Lesson}
     */
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
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
     * Возвращает статус присутствия студента на занятии.
     *
     * @return {@code true}, если студент присутствовал; {@code false} — если отсутствовал;
     *         {@code null}, если статус не установлен
     */
    public Boolean getIsPresent() {
        return isPresent;
    }

    /**
     * Устанавливает статус присутствия студента на занятии.
     *
     * @param isPresent {@code true} — присутствовал, {@code false} — отсутствовал
     */
    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    /**
     * Возвращает дополнительный комментарий к записи.
     *
     * @return текст комментария или {@code null}, если не задан
     */
    public String getComment() {
        return comment;
    }

    /**
     * Устанавливает дополнительный комментарий к записи.
     *
     * @param comment текст комментария (например, причина отсутствия)
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Сравнивает два объекта {@link Attendance} по уникальному идентификатору.
     *
     * <p>Две записи считаются равными, если их {@code attendanceId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code attendanceId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(attendanceId, that.attendanceId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора записи.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Attendance} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code attendanceId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(attendanceId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Attendance{attendanceId=..., isPresent=...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", isPresent=" + isPresent +
                '}';
    }
}