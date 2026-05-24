package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных учебного заведения (школы).
 *
 * <p>Представляет информацию о школе: название, адрес, ФИО директора.
 * Используется для организации структуры образовательного учреждения
 * в системе управления учебным процессом.
 */
public class School {

    /** Уникальный идентификатор учебного заведения */
    private Long schoolId;

    /** Полное название школы (например, "МБОУ СОШ №1 г. Муром") */
    private String schoolName;

    /** Адрес школы (юридический или фактический) */
    private String address;

    /** ФИО директора учебного заведения */
    private String director;

    /**
     * Создаёт пустой объект школы.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public School() {}

    /**
     * Создаёт объект школы с основными параметрами.
     *
     * @param schoolId уникальный идентификатор школы
     * @param schoolName полное название учебного заведения
     * @param address адрес школы
     * @param director ФИО директора
     */
    public School(Long schoolId, String schoolName, String address, String director) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.address = address;
        this.director = director;
    }

    /**
     * Возвращает уникальный идентификатор школы.
     *
     * @return ID школы
     */
    public Long getSchoolId() {
        return schoolId;
    }

    /**
     * Устанавливает уникальный идентификатор школы.
     *
     * @param schoolId новый ID школы
     */
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * Возвращает полное название учебного заведения.
     *
     * @return название школы или {@code null}, если не задано
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * Устанавливает полное название учебного заведения.
     *
     * @param schoolName новое название школы
     */
    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * Возвращает адрес школы.
     *
     * @return адрес или {@code null}, если не задан
     */
    public String getAddress() {
        return address;
    }

    /**
     * Устанавливает адрес школы.
     *
     * @param address новый адрес учебного заведения
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Возвращает ФИО директора школы.
     *
     * @return ФИО директора или {@code null}, если не назначен
     */
    public String getDirector() {
        return director;
    }

    /**
     * Устанавливает ФИО директора школы.
     *
     * @param director ФИО нового директора
     */
    public void setDirector(String director) {
        this.director = director;
    }

    /**
     * Сравнивает два объекта {@link School} по уникальному идентификатору.
     *
     * <p>Две школы считаются равными, если их {@code schoolId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code schoolId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        School school = (School) o;
        return Objects.equals(schoolId, school.schoolId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора школы.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link School} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code schoolId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(schoolId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code School{schoolId=..., schoolName='...', address='...', director='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "School{" +
                "schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                ", address='" + address + '\'' +
                ", director='" + director + '\'' +
                '}';
    }
}