package ru.alesya0711.laba6.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Модель данных итоговой работы студента.
 *
 * <p>Представляет запись о сдаче студентом итоговой работы
 * по определённому учебному курсу. Содержит информацию о билете, датах,
 * оценках по теории и практике, а также статус активности записи.
 */
public class FinalWork {

    /** Уникальный идентификатор итоговой работы */
    private Long finalId;

    /** Идентификатор курса, по которому сдана работа */
    private Long courseId;

    /** Объект курса (для удобства работы с данными) */
    private Course course;

    /** Идентификатор студента, сдавшего работу */
    private Long studentId;

    /** Объект студента (для удобства работы с данными) */
    private Student student;

    /** Дата проведения экзамена */
    private LocalDate examDate;

    /** Номер экзаменационного билета */
    private String ticketNumber;

    /** Оценка по теоретической части экзамена (может быть {@code null}) */
    private Integer theoryGrade;

    /** Оценка по практической части экзамена (может быть {@code null}) */
    private Integer practiceGrade;

    /** Флаг активности записи: {@code true} — работа активна, {@code false} — мягко удалена */
    private boolean isActive = true;

    /**
     * Создаёт пустой объект итоговой работы.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public FinalWork() {}

    /**
     * Создаёт объект итоговой работы с основными параметрами.
     *
     * @param finalId уникальный идентификатор работы
     * @param courseId идентификатор курса
     * @param studentId идентификатор студента
     * @param examDate дата экзамена
     * @param ticketNumber номер билета
     */
    public FinalWork(Long finalId, Long courseId, Long studentId, LocalDate examDate, String ticketNumber) {
        this.finalId = finalId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.examDate = examDate;
        this.ticketNumber = ticketNumber;
    }

    /**
     * Возвращает уникальный идентификатор итоговой работы.
     *
     * @return ID работы
     */
    public Long getFinalId() {
        return finalId;
    }

    /**
     * Устанавливает уникальный идентификатор итоговой работы.
     *
     * @param finalId новый ID работы
     */
    public void setFinalId(Long finalId) {
        this.finalId = finalId;
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
     * Устанавливает идентификатор курса.
     *
     * @param courseId новый ID курса
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Возвращает объект курса, связанный с работой.
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
     * Возвращает объект студента, связанный с работой.
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
     * Возвращает дату проведения экзамена.
     *
     * @return дата экзамена или {@code null}, если не установлена
     */
    public LocalDate getExamDate() {
        return examDate;
    }

    /**
     * Устанавливает дату проведения экзамена.
     *
     * @param examDate новая дата экзамена
     */
    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    /**
     * Возвращает номер экзаменационного билета.
     *
     * @return номер билета или {@code null}, если не задан
     */
    public String getTicketNumber() {
        return ticketNumber;
    }

    /**
     * Устанавливает номер экзаменационного билета.
     *
     * @param ticketNumber новый номер билета
     */
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    /**
     * Возвращает оценку по теоретической части экзамена.
     *
     * @return оценка по теории или {@code null}, если не выставлена
     */
    public Integer getTheoryGrade() {
        return theoryGrade;
    }

    /**
     * Устанавливает оценку по теоретической части экзамена.
     *
     * @param theoryGrade новая оценка по теории
     */
    public void setTheoryGrade(Integer theoryGrade) {
        this.theoryGrade = theoryGrade;
    }

    /**
     * Возвращает оценку по практической части экзамена.
     *
     * @return оценка по практике или {@code null}, если не выставлена
     */
    public Integer getPracticeGrade() {
        return practiceGrade;
    }

    /**
     * Устанавливает оценку по практической части экзамена.
     *
     * @param practiceGrade новая оценка по практике
     */
    public void setPracticeGrade(Integer practiceGrade) {
        this.practiceGrade = practiceGrade;
    }

    /**
     * Возвращает статус активности записи.
     *
     * @return {@code true}, если работа активна; {@code false} — если мягко удалена
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Устанавливает статус активности записи.
     *
     * @param active {@code true} — активировать запись, {@code false} — пометить как удалённую
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Сравнивает два объекта {@link FinalWork} по уникальному идентификатору.
     *
     * <p>Две работы считаются равными, если их {@code finalId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code finalId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinalWork finalWork = (FinalWork) o;
        return Objects.equals(finalId, finalWork.finalId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора работы.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link FinalWork} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code finalId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(finalId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code FinalWork{finalId=..., ticketNumber='...', theoryGrade=..., practiceGrade=...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "FinalWork{" +
                "finalId=" + finalId +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", theoryGrade=" + theoryGrade +
                ", practiceGrade=" + practiceGrade +
                '}';
    }
}