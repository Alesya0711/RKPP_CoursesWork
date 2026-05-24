package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных учебного класса (группы в рамках школы).
 *
 * <p>Представляет информацию об учебном классе: название, описание, привязка к школе
 * и классному руководителю. Используется для организации структуры образовательного
 * учреждения в системе управления учебным процессом.
 */
public class Class {

    /** Уникальный идентификатор учебного класса */
    private Long classId;

    /** Название класса (например, "10А", "11Б") */
    private String className;

    /** Дополнительное описание класса */
    private String description;

    /** Идентификатор школы, к которой относится класс */
    private Long schoolId;

    /** Объект школы (для удобства работы с данными) */
    private School school;

    /** ФИО классного руководителя */
    private String homeroomTeacher;

    /**
     * Создаёт пустой объект учебного класса.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Class() {}

    /**
     * Создаёт объект учебного класса с основными параметрами.
     *
     * @param classId уникальный идентификатор класса
     * @param className название класса
     * @param description описание класса
     * @param schoolId идентификатор школы
     * @param homeroomTeacher ФИО классного руководителя
     */
    public Class(Long classId, String className, String description, Long schoolId, String homeroomTeacher) {
        this.classId = classId;
        this.className = className;
        this.description = description;
        this.schoolId = schoolId;
        this.homeroomTeacher = homeroomTeacher;
    }

    /**
     * Возвращает уникальный идентификатор учебного класса.
     *
     * @return ID класса
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * Устанавливает уникальный идентификатор учебного класса.
     *
     * @param classId новый ID класса
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * Возвращает название учебного класса.
     *
     * @return название класса (например, "10А")
     */
    public String getClassName() {
        return className;
    }

    /**
     * Устанавливает название учебного класса.
     *
     * @param className новое название класса
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Возвращает дополнительное описание класса.
     *
     * @return текст описания или {@code null}, если не задан
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает дополнительное описание класса.
     *
     * @param description текст описания
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает идентификатор школы, к которой относится класс.
     *
     * @return ID школы
     */
    public Long getSchoolId() {
        return schoolId;
    }

    /**
     * Устанавливает идентификатор школы для класса.
     *
     * @param schoolId новый ID школы
     */
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * Возвращает объект школы, связанный с классом.
     *
     * @return объект {@link School} или {@code null}, если не установлен
     */
    public School getSchool() {
        return school;
    }

    /**
     * Устанавливает объект школы для удобства работы с данными.
     *
     * @param school объект {@link School}
     */
    public void setSchool(School school) {
        this.school = school;
    }

    /**
     * Возвращает ФИО классного руководителя.
     *
     * @return ФИО учителя или {@code null}, если не назначен
     */
    public String getHomeroomTeacher() {
        return homeroomTeacher;
    }

    /**
     * Устанавливает ФИО классного руководителя.
     *
     * @param homeroomTeacher ФИО учителя
     */
    public void setHomeroomTeacher(String homeroomTeacher) {
        this.homeroomTeacher = homeroomTeacher;
    }

    /**
     * Сравнивает два объекта {@link Class} по уникальному идентификатору.
     *
     * <p>Два класса считаются равными, если их {@code classId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code classId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(classId, aClass.classId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора класса.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Class} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code classId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(classId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Class{classId=..., className='...', ...}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Class{" +
                "classId=" + classId +
                ", className='" + className + '\'' +
                ", description='" + description + '\'' +
                ", homeroomTeacher='" + homeroomTeacher + '\'' +
                '}';
    }
}