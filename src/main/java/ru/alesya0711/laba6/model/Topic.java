package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных темы учебного курса.
 *
 * <p>Представляет информацию о теме курса: название, описание, привязка к курсу.
 * Используется для структурирования учебного материала и организации занятий
 * в системе управления учебным процессом.
 */
public class Topic {

    /** Уникальный идентификатор темы курса */
    private Long topicId;

    /** Идентификатор курса, к которому относится тема */
    private Long courseId;

    /** Объект курса (для удобства работы с данными) */
    private Course course;

    /** Название темы курса */
    private String topicName;

    /** Описание содержания и целей темы */
    private String topicDescription;

    /**
     * Создаёт пустой объект темы курса.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Topic() {}

    /**
     * Создаёт объект темы курса с основными параметрами.
     *
     * @param topicId уникальный идентификатор темы
     * @param courseId идентификатор курса
     * @param topicName название темы
     * @param topicDescription описание темы
     */
    public Topic(Long topicId, Long courseId, String topicName, String topicDescription) {
        this.topicId = topicId;
        this.courseId = courseId;
        this.topicName = topicName;
        this.topicDescription = topicDescription;
    }

    /**
     * Возвращает уникальный идентификатор темы.
     *
     * @return ID темы
     */
    public Long getTopicId() {
        return topicId;
    }

    /**
     * Устанавливает уникальный идентификатор темы.
     *
     * @param topicId новый ID темы
     */
    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    /**
     * Возвращает идентификатор курса.
     *
     * @return ID курса
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Устанавливает идентификатор курса для темы.
     *
     * @param courseId новый ID курса
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Возвращает объект курса, связанный с темой.
     *
     * @return объект {@link Course} или {@code null}, если не установлен
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Устанавливает объект курса для удобства работы с данными.
     *
     * @param course объект {@link Course}
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * Возвращает название темы курса.
     *
     * @return название темы или {@code null}, если не задано
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * Устанавливает название темы курса.
     *
     * @param topicName новое название темы
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * Возвращает описание темы курса.
     *
     * @return текст описания или {@code null}, если не задан
     */
    public String getTopicDescription() {
        return topicDescription;
    }

    /**
     * Устанавливает описание темы курса.
     *
     * @param topicDescription текст описания
     */
    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    /**
     * Сравнивает два объекта {@link Topic} по уникальному идентификатору.
     *
     * <p>Две темы считаются равными, если их {@code topicId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code topicId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(topicId, topic.topicId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора темы.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Topic} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code topicId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(topicId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Topic{topicId=..., topicName='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Topic{" +
                "topicId=" + topicId +
                ", topicName='" + topicName + '\'' +
                '}';
    }
}