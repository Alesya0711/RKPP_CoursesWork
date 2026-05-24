package ru.alesya0711.laba6.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель данных учебной группы.
 *
 * <p>Представляет информацию об учебной группе: название, описание, привязка к курсу,
 * количество студентов и дату формирования. Используется для организации учебного
 * процесса и управления составом групп в системе.
 */
public class Group {

    /** Уникальный идентификатор учебной группы */
    private Long groupId;

    /** Идентификатор курса, к которому относится группа */
    private Long courseId;

    /** Объект курса (для удобства работы с данными) */
    private Course course;

    /** Название учебной группы (например, "ПИн-123") */
    private String groupName;

    /** Дополнительное описание группы */
    private String description;

    /** Количество студентов в группе */
    private Integer studentCount;

    /** Дата формирования группы */
    private LocalDate formationDate;

    /**
     * Создаёт пустой объект учебной группы.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Group() {}

    /**
     * Создаёт объект учебной группы с основными параметрами.
     *
     * @param groupId уникальный идентификатор группы
     * @param courseId идентификатор курса
     * @param groupName название группы
     * @param description описание группы
     * @param studentCount количество студентов
     * @param formationDate дата формирования
     */
    public Group(Long groupId, Long courseId, String groupName, String description,
                 Integer studentCount, LocalDate formationDate) {
        this.groupId = groupId;
        this.courseId = courseId;
        this.groupName = groupName;
        this.description = description;
        this.studentCount = studentCount;
        this.formationDate = formationDate;
    }

    /**
     * Возвращает уникальный идентификатор учебной группы.
     *
     * @return ID группы
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * Устанавливает уникальный идентификатор учебной группы.
     *
     * @param groupId новый ID группы
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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
     * Устанавливает идентификатор курса для группы.
     *
     * @param courseId новый ID курса
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Возвращает объект курса, связанный с группой.
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
     * Возвращает название учебной группы.
     *
     * @return название группы (например, "ПИн-123")
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Устанавливает название учебной группы.
     *
     * @param groupName новое название группы
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Возвращает дополнительное описание группы.
     *
     * @return текст описания или {@code null}, если не задан
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает дополнительное описание группы.
     *
     * @param description текст описания
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает количество студентов в группе.
     *
     * @return число студентов или {@code null}, если не установлено
     */
    public Integer getStudentCount() {
        return studentCount;
    }

    /**
     * Устанавливает количество студентов в группе.
     *
     * @param studentCount новое количество студентов
     */
    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    /**
     * Возвращает дату формирования группы.
     *
     * @return дата формирования или {@code null}, если не установлена
     */
    public LocalDate getFormationDate() {
        return formationDate;
    }

    /**
     * Устанавливает дату формирования группы.
     *
     * @param formationDate новая дата формирования
     */
    public void setFormationDate(LocalDate formationDate) {
        this.formationDate = formationDate;
    }

    /**
     * Сравнивает два объекта {@link Group} по уникальному идентификатору.
     *
     * <p>Две группы считаются равными, если их {@code groupId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code groupId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(groupId, group.groupId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора группы.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Group} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code groupId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Group{groupId=..., groupName='...', studentCount=...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }
}