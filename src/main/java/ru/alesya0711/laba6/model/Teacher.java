package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных преподавателя.
 *
 * <p>Представляет информацию о преподавателе: учётные данные для входа,
 * персональные данные, привязка к предмету и фотография. Используется для
 * аутентификации в системе, отображения профиля и управления учебными курсами.
 */
public class Teacher {

    /** Уникальный идентификатор преподавателя */
    private Long teacherId;

    /** Логин (имя пользователя) для авторизации в системе */
    private String username;

    /** Хэш пароля для безопасной аутентификации */
    private String passwordHash;

    /** Фамилия преподавателя */
    private String lastName;

    /** Имя преподавателя */
    private String firstName;

    /** Отчество преподавателя (может быть {@code null}) */
    private String middleName;

    /** Адрес электронной почты преподавателя */
    private String email;

    /** Идентификатор преподаваемого предмета */
    private Long subjectId;

    /** Объект предмета (для удобства работы с данными) */
    private Subject subject;

    /** Фотография преподавателя в виде массива байтов (может быть {@code null}) */
    private byte[] photo;

    /**
     * Создаёт пустой объект преподавателя.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Teacher() {}

    /**
     * Создаёт объект преподавателя с основными параметрами.
     *
     * @param teacherId уникальный идентификатор преподавателя
     * @param lastName фамилия
     * @param firstName имя
     * @param middleName отчество (может быть {@code null})
     * @param email адрес электронной почты
     */
    public Teacher(Long teacherId, String lastName, String firstName,
                   String middleName, String email) {
        this.teacherId = teacherId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.email = email;
    }

    /**
     * Возвращает уникальный идентификатор преподавателя.
     *
     * @return ID преподавателя
     */
    public Long getTeacherId() {
        return teacherId;
    }

    /**
     * Устанавливает уникальный идентификатор преподавателя.
     *
     * @param teacherId новый ID преподавателя
     */
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Возвращает логин преподавателя для авторизации.
     *
     * @return имя пользователя или {@code null}, если не задано
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает логин преподавателя для авторизации.
     *
     * @param username новое имя пользователя
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Возвращает хэш пароля преподавателя.
     *
     * @return хэш пароля или {@code null}, если не задан
     * @implNote Пароль хранится в хэшированном виде для безопасности
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Устанавливает хэш пароля преподавателя.
     *
     * @param passwordHash новый хэш пароля
     * @implNote Следует передавать уже хэшированное значение
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Возвращает фамилию преподавателя.
     *
     * @return фамилия или {@code null}, если не задана
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Устанавливает фамилию преподавателя.
     *
     * @param lastName новая фамилия
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Возвращает имя преподавателя.
     *
     * @return имя или {@code null}, если не задано
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Устанавливает имя преподавателя.
     *
     * @param firstName новое имя
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Возвращает отчество преподавателя.
     *
     * @return отчество или {@code null}, если не задано
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Устанавливает отчество преподавателя.
     *
     * @param middleName новое отчество
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * Возвращает адрес электронной почты преподавателя.
     *
     * @return email или {@code null}, если не задан
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает адрес электронной почты преподавателя.
     *
     * @param email новый email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает идентификатор преподаваемого предмета.
     *
     * @return ID предмета или {@code null}, если предмет не назначен
     */
    public Long getSubjectId() {
        return subjectId;
    }

    /**
     * Устанавливает идентификатор преподаваемого предмета.
     *
     * @param subjectId новый ID предмета
     */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Возвращает объект предмета, связанного с преподавателем.
     *
     * @return объект {@link Subject} или {@code null}, если не установлен
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Устанавливает объект предмета для удобства работы с данными.
     *
     * @param subject объект {@link Subject}
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Возвращает фотографию преподавателя.
     *
     * @return массив байтов с изображением или {@code null}, если фото не загружено
     */
    public byte[] getPhoto() {
        return photo;
    }

    /**
     * Устанавливает фотографию преподавателя.
     *
     * @param photo массив байтов с изображением
     */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
     * Сравнивает два объекта {@link Teacher} по уникальному идентификатору.
     *
     * <p>Два преподавателя считаются равными, если их {@code teacherId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code teacherId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(teacherId, teacher.teacherId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора преподавателя.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Teacher} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code teacherId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(teacherId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Teacher{teacherId=..., lastName='...', firstName='...', email='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId=" + teacherId +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}