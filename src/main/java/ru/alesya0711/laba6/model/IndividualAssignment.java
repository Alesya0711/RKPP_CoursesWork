package ru.alesya0711.laba6.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель данных индивидуального задания студента.
 *
 * <p>Представляет запись о задании, выданном студенту в рамках определённой темы курса.
 * Содержит информацию о названии, датах выдачи и сдачи, оценке, статусе выполнения,
 * а также флаг активности для поддержки мягкого удаления.
 */
public class IndividualAssignment {

    /** Уникальный идентификатор индивидуального задания */
    private Long assignmentId;

    /** Идентификатор темы курса, к которой относится задание */
    private Long topicId;

    /** Объект темы (для удобства работы с данными) */
    private Topic topic;

    /** Идентификатор студента, которому выдано задание */
    private Long studentId;

    /** Объект студента (для удобства работы с данными) */
    private Student student;

    /** Название индивидуального задания */
    private String assignmentName;

    /** Дата выдачи задания студенту */
    private LocalDate assignmentDate;

    /** Оценка за выполнение задания (диапазон 0-100); может быть {@code null}, если не выставлена */
    private Integer grade;

    /**
     * Статус выполнения задания.
     * Возможные значения:
     * <ul>
     *   <li>{@code PENDING} — задание не начато</li>
     *   <li>{@code IN_PROGRESS} — в процессе выполнения</li>
     *   <li>{@code SUBMITTED} — сдано на проверку</li>
     *   <li>{@code GRADED} — оценено</li>
     *   <li>{@code OVERDUE} — просрочено</li>
     * </ul>
     */
    private String status;

    /** Дата сдачи задания студентом; может быть {@code null}, если задание ещё не сдано */
    private LocalDate submissionDate;

    /** Флаг активности записи: {@code true} — задание активно, {@code false} — мягко удалено */
    private boolean isActive = true;

    /**
     * Создаёт пустой объект индивидуального задания.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public IndividualAssignment() {}

    /**
     * Создаёт объект индивидуального задания с основными параметрами.
     *
     * @param assignmentId уникальный идентификатор задания
     * @param topicId идентификатор темы курса
     * @param studentId идентификатор студента
     * @param assignmentName название задания
     * @param assignmentDate дата выдачи задания
     * @param status статус выполнения задания
     */
    public IndividualAssignment(Long assignmentId, Long topicId, Long studentId,
                                String assignmentName, LocalDate assignmentDate, String status) {
        this.assignmentId = assignmentId;
        this.topicId = topicId;
        this.studentId = studentId;
        this.assignmentName = assignmentName;
        this.assignmentDate = assignmentDate;
        this.status = status;
    }

    /**
     * Возвращает уникальный идентификатор задания.
     *
     * @return ID задания
     */
    public Long getAssignmentId() {
        return assignmentId;
    }

    /**
     * Устанавливает уникальный идентификатор задания.
     *
     * @param assignmentId новый ID задания
     */
    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
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
     * Возвращает объект темы, связанный с заданием.
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
     * Возвращает объект студента, связанного с заданием.
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
     * Возвращает название индивидуального задания.
     *
     * @return название задания
     */
    public String getAssignmentName() {
        return assignmentName;
    }

    /**
     * Устанавливает название индивидуального задания.
     *
     * @param assignmentName новое название задания
     */
    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    /**
     * Возвращает дату выдачи задания.
     *
     * @return дата выдачи или {@code null}, если не установлена
     */
    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    /**
     * Устанавливает дату выдачи задания.
     *
     * @param assignmentDate новая дата выдачи
     */
    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    /**
     * Возвращает оценку за выполнение задания.
     *
     * @return оценка (0-100) или {@code null}, если не выставлена
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * Устанавливает оценку за выполнение задания.
     *
     * @param grade новая оценка (рекомендуемый диапазон 0-100)
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /**
     * Возвращает статус выполнения задания.
     *
     * @return строковый статус: {@code PENDING}, {@code IN_PROGRESS},
     *         {@code SUBMITTED}, {@code GRADED}, {@code OVERDUE} или {@code null}
     */
    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус выполнения задания.
     *
     * @param status новый статус из допустимого набора значений
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Возвращает дату сдачи задания студентом.
     *
     * @return дата сдачи или {@code null}, если задание ещё не сдано
     */
    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Устанавливает дату сдачи задания.
     *
     * @param submissionDate новая дата сдачи
     */
    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * Возвращает статус активности записи.
     *
     * @return {@code true}, если задание активно; {@code false} — если мягко удалено
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Устанавливает статус активности записи.
     *
     * @param active {@code true} — активировать задание, {@code false} — пометить как удалённое
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Сравнивает два объекта {@link IndividualAssignment} по уникальному идентификатору.
     *
     * <p>Два задания считаются равными, если их {@code assignmentId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code assignmentId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualAssignment that = (IndividualAssignment) o;
        return Objects.equals(assignmentId, that.assignmentId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора задания.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link IndividualAssignment} в хэш-коллекциях
     * (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code assignmentId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(assignmentId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code IndividualAssignment{assignmentId=..., assignmentName='...', status='...', grade=...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "IndividualAssignment{" +
                "assignmentId=" + assignmentId +
                ", assignmentName='" + assignmentName + '\'' +
                ", status='" + status + '\'' +
                ", grade=" + grade +
                '}';
    }
}