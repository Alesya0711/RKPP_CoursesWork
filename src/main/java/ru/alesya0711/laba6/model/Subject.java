package ru.alesya0711.laba6.model;

import java.util.Objects;

/**
 * Модель данных учебного предмета.
 *
 * <p>Представляет информацию об учебном предмете: уникальный идентификатор и название.
 * Используется для классификации курсов и назначения преподавателей в системе
 * управления учебным процессом.
 */
public class Subject {

    /** Уникальный идентификатор учебного предмета */
    private Long subjectId;

    /** Полное название предмета (например, "Информатика") */
    private String subjectName;

    /**
     * Создаёт пустой объект учебного предмета.
     *
     * <p>Используется по умолчанию при маппинге результатов из базы данных
     * или при создании новой записи через конструктор с параметрами.
     */
    public Subject() {}

    /**
     * Создаёт объект учебного предмета с основными параметрами.
     *
     * @param subjectId уникальный идентификатор предмета
     * @param subjectName полное название предмета
     */
    public Subject(Long subjectId, String subjectName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    /**
     * Возвращает уникальный идентификатор учебного предмета.
     *
     * @return ID предмета
     */
    public Long getSubjectId() {
        return subjectId;
    }

    /**
     * Устанавливает уникальный идентификатор учебного предмета.
     *
     * @param subjectId новый ID предмета
     */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Возвращает полное название учебного предмета.
     *
     * @return название предмета или {@code null}, если не задано
     */
    public String getSubjectName() {
        return subjectName;
    }

    /**
     * Устанавливает полное название учебного предмета.
     *
     * @param subjectName новое название предмета
     */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**
     * Сравнивает два объекта {@link Subject} по уникальному идентификатору.
     *
     * <p>Два предмета считаются равными, если их {@code subjectId} совпадают.
     * Это позволяет использовать объекты в коллекциях и сравнивать их при обновлении данных.
     *
     * @param o объект для сравнения
     * @return {@code true}, если это тот же объект или у них одинаковый {@code subjectId}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(subjectId, subject.subjectId);
    }

    /**
     * Возвращает хэш-код на основе уникального идентификатора предмета.
     *
     * <p>Реализация согласована с методом {@link #equals(Object)} и позволяет
     * использовать объекты {@link Subject} в хэш-коллекциях (например, {@link java.util.HashSet}).
     *
     * @return хэш-код, вычисленный на основе {@code subjectId}
     */
    @Override
    public int hashCode() {
        return Objects.hash(subjectId);
    }

    /**
     * Возвращает строковое представление объекта для отладки и логирования.
     *
     * <p>Формат вывода: {@code Subject{subjectId=..., subjectName='...'}}
     *
     * @return строка с основными полями объекта
     */
    @Override
    public String toString() {
        return "Subject{" +
                "subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }
}