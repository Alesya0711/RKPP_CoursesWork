package ru.alesya0711.laba6.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель данных учебного занятия (урока).
 *
 * <p>Представляет информацию о конкретном занятии в рамках темы курса:
 * порядковый номер, дата проведения и тип (Лекция, Практика, Лабораторная, Контрольная).
 * Используется для формирования расписания и учёта посещаемости.
 */
public class Lesson {

    /** Уникальный идентификатор учебного занятия */
    private Long lessonId;

    /** Идентификатор темы курса, к которой относится занятие */
    private Long topicId;

    /** Объект темы (для удобства работы с данными) */
    private Topic topic;

    /** Порядковый номер занятия в рамках темы (начиная с 1) */
    private Integer lessonNumber;

    /** Дата проведения занятия */
    private LocalDate lessonDate;

    /** Тип занятия: "Лекция", "Практика", "Лабораторная", "Контрольная" */
    private String lessonType;

    /**
     * Создаёт пустой объект учебного занятия.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Lesson() {}

    /**
     * Создаёт объект учебного занятия с основными параметрами.
     *
     * @param lessonId уникальный идентификатор занятия
     * @param topicId идентификатор темы курса
     * @param lessonNumber порядковый номер занятия
     * @param lessonDate дата проведения занятия
     * @param lessonType тип занятия
     */
    public Lesson(Long lessonId, Long topicId, Integer lessonNumber,
                  LocalDate lessonDate, String lessonType) {
        this.lessonId = lessonId;
        this.topicId = topicId;
        this.lessonNumber = lessonNumber;
        this.lessonDate = lessonDate;
        this.lessonType = lessonType;
    }

    /**
     * Возвращает уникальный идентификатор занятия.
     *
     * @return ID занятия
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Устанавливает уникальный идентификатор занятия.
     *
     * @param lessonId новый ID занятия
     */
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    /**
     * Возвращает идентификатор темы курса.
     *
     * @return ID темы
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * Устанавливает идентификатор темы курса.
     *
     * @param topicId новый ID темы
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * Возвращает объект темы, связанный с занятием.
     *
     * @return объект {@link Topic} или {@code null}, если не установлен
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Устанавливает объект темы для удобства работы с данными.
     *
     * @param topic объект {@link Topic}
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Возвращает порядковый номер занятия в теме.
     *
     * @return номер занятия (начиная с 1) или {@code null}, если не установлен
     */
    public Integer getLessonNumber() {
        return lessonNumber;
    }

    /**
     * Устанавливает порядковый номер занятия в теме.
     *
     * @param lessonNumber новый номер занятия
     */
    public void setLessonNumber(Integer lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    /**
     * Возвращает дату проведения занятия.
     *
     * @return дата занятия или {@code null}, если не установлена
     */
    public LocalDate getLessonDate() {
        return lessonDate;
    }

    /**
     * Устанавливает дату проведения занятия.
     *
     * @param lessonDate новая дата занятия
     */
    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    /**
     * Возвращает тип учебного занятия.
     *
     * @return строковое значение типа: "Лекция", "Практика",
     *         "Лабораторная", "Контрольная" или {@code null}
     */
    public String getLessonType() {
        return lessonType;
    }

    /**
     * Устанавливает тип учебного занятия.
     *
     * @param lessonType новый тип занятия из допустимого набора значений
     */
    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }

    /**
     * Сравнивает два объекта {@link Lesson} по уникальному идентификатору.
     *
     * <p>Два занятия считаются равными, если их {@code lessonId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code lessonId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(lessonId, lesson.lessonId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора занятия.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Lesson} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code lessonId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(lessonId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Lesson{lessonId=..., lessonNumber=..., lessonDate=..., lessonType='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Lesson{" +
                "lessonId=" + lessonId +
                ", lessonNumber=" + lessonNumber +
                ", lessonDate=" + lessonDate +
                ", lessonType='" + lessonType + '\'' +
                '}';
    }
}