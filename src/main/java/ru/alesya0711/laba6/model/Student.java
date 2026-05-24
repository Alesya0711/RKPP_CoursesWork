package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных студента.
 *
 * <p>Представляет информацию о студенте: ФИО, контактные данные, привязка к классу
 * и фотография. Используется для отображения данных в интерфейсе, учёта успеваемости
 * и посещаемости в системе управления учебным процессом.
 */
public class Student {

    /** Уникальный идентификатор студента */
    private Long studentId;

    /** Фамилия студента */
    private String lastName;

    /** Имя студента */
    private String firstName;

    /** Отчество студента (может быть {@code null}) */
    private String middleName;

    /** Номер телефона для связи со студентом */
    private String phone;

    /** Адрес электронной почты студента */
    private String email;

    /** Фотография студента в виде массива байтов (может быть {@code null}) */
    private byte[] photo;

    /** Идентификатор учебного класса, к которому относится студент */
    private Long classId;

    /** Объект класса (для удобства работы с данными) */
    private Class classInfo;

    /**
     * Создаёт пустой объект студента.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Student() {}

    /**
     * Создаёт объект студента с основными параметрами.
     *
     * @param studentId уникальный идентификатор студента
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество (может быть {@code null})
     * @param phone номер телефона
     * @param email адрес электронной почты
     */
    public Student(Long studentId, String lastName, String firstName,
                   String middleName, String phone, String email) {
        this.studentId = studentId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Возвращает уникальный идентификатор студента.
     *
     * @return ID студента
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Устанавливает уникальный идентификатор студента.
     *
     * @param studentId новый ID студента
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * Возвращает фамилию студента.
     *
     * @return фамилия или {@code null}, если не задана
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию студента.
     *
     * @param lastName новая фамилия
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает имя студента.
     *
     * @return имя или {@code null}, если не задано
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя студента.
     *
     * @param firstName новое имя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает отчество студента.
     *
     * @return отчество или {@code null}, если не задано
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Устанавливает отчество студента.
     *
     * @param middleName новое отчество
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Возвращает номер телефона студента.
     *
     * @return номер телефона или {@code null}, если не задан
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Устанавливает номер телефона студента.
     *
     * @param phone новый номер телефона
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Возвращает адрес электронной почты студента.
     *
     * @return email или {@code null}, если не задан
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает адрес электронной почты студента.
     *
     * @param email новый email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает фотографию студента.
     *
     * @return массив байтов с изображением или {@code null}, если фото не загружено
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Устанавливает фотографию студента.
     *
     * @param photo массив байтов с изображением
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
     * Возвращает идентификатор учебного класса.
     *
     * @return ID класса или {@code null}, если студент не привязан к классу
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * Устанавливает идентификатор учебного класса для студента.
     *
     * @param classId новый ID класса
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * Возвращает объект класса, связанный со студентом.
     *
     * @return объект {@link Class} или {@code null}, если не установлен
     */
    public Class getClassInfo() {
        return classInfo;
    }

    /**
     * Устанавливает объект класса для удобства работы с данными.
     *
     * @param classInfo объект {@link Class}
     */
    public void setClassInfo(Class classInfo) {
        this.classInfo = classInfo;
    }

    /**
     * Сравнивает два объекта {@link Student} по уникальному идентификатору.
     *
     * <p>Два студента считаются равными, если их {@code studentId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code studentId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора студента.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Student} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code studentId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Student{studentId=..., lastName='...', firstName='...', email='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}