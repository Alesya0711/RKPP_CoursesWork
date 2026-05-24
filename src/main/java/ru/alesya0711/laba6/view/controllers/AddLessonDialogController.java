package ru.alesya0711.laba6.view.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.alesya0711.laba6.dao.impl.CourseDAOImpl;
import ru.alesya0711.laba6.dao.impl.LessonDAOImpl;
import ru.alesya0711.laba6.dao.impl.TopicDAOImpl;
import ru.alesya0711.laba6.model.Course;
import ru.alesya0711.laba6.model.Lesson;
import ru.alesya0711.laba6.model.Topic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Контроллер диалогового окна добавления учебного занятия.
 *
 * <p>Управляет формой создания нового занятия в расписании: выбор курса, темы,
 * номера занятия, даты и типа (Лекция, Практика, Лабораторная, Контрольная).
 * Обеспечивает валидацию ввода и проверку уникальности номера занятия в рамках темы.
 */
@Slf4j
public class AddLessonDialogController {

    @FXML private ComboBox<Course> lessonCourseCombo;
    @FXML private ComboBox<Topic> lessonTopicCombo;
    @FXML private Spinner<Integer> lessonNumberSpinner;
    @FXML private DatePicker lessonDatePicker;
    @FXML private ComboBox<String> lessonTypeCombo;
    @FXML private Button saveLessonButton;

    private LessonDAOImpl lessonDAO;
    private Long teacherId;

    ResourceBundle bundle = ResourceBundle.getBundle(
            "ru.alesya0711.laba6.resources.strings",
            Locale.getDefault()
    );
    /**
     * Инициализация контроллера после загрузки FXML.
     * Настраивает спиннеры, фильтры ввода и слушатели изменений.
     */
    @FXML
    public void initialize() {
        log.debug("Инициализация AddLessonDialogController");

        try {
            lessonDAO = new LessonDAOImpl();
            log.debug("LessonDAO успешно создан");
        } catch (SQLException e) {
            log.error("Ошибка создания LessonDAO", e);
            showAlert(bundle.getString("alert.error.bd"), bundle.getString("alert.error.bd.message") + e.getMessage());
            saveLessonButton.setDisable(true);
        }

        // Настройка диапазона номеров занятий (1-100)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        lessonNumberSpinner.setValueFactory(valueFactory);

        // Фильтр ввода для номера занятия: только цифры
        lessonNumberSpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            if (!newVal.matches("\\d+")) {
                lessonNumberSpinner.getEditor().setText(oldVal);
                lessonNumberSpinner.getEditor().positionCaret(oldVal.length());
            }
        });

        // Заполнение типов занятий
        lessonTypeCombo.getItems().addAll("Лекция", "Практика", "Лабораторная", "Контрольная");
        lessonTypeCombo.setValue("Лекция");

        lessonDatePicker.setValue(LocalDate.now());

        // Настройка отображения названий курсов и тем
        setupComboBoxDisplay();

        // Слушатель изменения курса для загрузки тем
        lessonCourseCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            if (newCourse != null) {
                log.debug("Выбран курс ID={}: {}", newCourse.getCourseId(), newCourse.getCourseName());
                loadTopicsForCourse(newCourse.getCourseId());
            } else {
                lessonTopicCombo.getItems().clear();
            }
        });

        setupValidation();
        log.debug("Интерфейс диалога настроен");
    }

    /**
     * Настраивает отображение названий в ComboBox для курсов и тем.
     */
    private void setupComboBoxDisplay() {
        // 1. Настройка отображения для ComboBox Курсов
        lessonCourseCombo.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                // Если ячейка пустая или курс null, текст не ставим, иначе ставим название курса
                setText((empty || course == null) ? null : course.getCourseName());
            }
        });

        // Для кнопки (выбранного элемента) используем ту же логику
        lessonCourseCombo.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                setText((empty || course == null) ? null : course.getCourseName());
            }
        });

        // 2. Настройка отображения для ComboBox Тем
        lessonTopicCombo.setCellFactory(param -> new ListCell<Topic>() {
            @Override
            protected void updateItem(Topic topic, boolean empty) {
                super.updateItem(topic, empty);
                // Если ячейка пустая или тема null, текст не ставим, иначе ставим название темы
                setText((empty || topic == null) ? null : topic.getTopicName());
            }
        });

        // Для кнопки (выбранного элемента) используем ту же логику
        lessonTopicCombo.setButtonCell(new ListCell<Topic>() {
            @Override
            protected void updateItem(Topic topic, boolean empty) {
                super.updateItem(topic, empty);
                setText((empty || topic == null) ? null : topic.getTopicName());
            }
        });
    }

    /**
     * Устанавливает начальные данные (ID преподавателя, курса и темы).
     * Используется при открытии диалога из контекста конкретного курса/темы.
     */
    public void setInitialData(Long teacherId, Long courseId, Long topicId) {
        this.teacherId = teacherId;
        log.debug("Установлены начальные данные: teacherId={}, courseId={}, topicId={}", teacherId, courseId, topicId);
        loadInitialData(courseId, topicId);
    }

    /**
     * Загружает курсы преподавателя и выбирает указанные курс и тему.
     */
    private void loadInitialData(Long courseId, Long topicId) {
        log.debug("Загрузка начальных данных для курса ID={} и темы ID={}", courseId, topicId);

        try {
            if (teacherId == null) {
                log.warn("teacherId не установлен");
                return;
            }

            CourseDAOImpl courseDAO = new CourseDAOImpl();
            List<Course> courses = courseDAO.findByTeacherId(teacherId);
            log.info("Загружено {} курсов для преподавателя ID={}", courses.size(), teacherId);

            lessonCourseCombo.getItems().addAll(courses);

            // Поиск и выбор нужного курса
            Course selectedCourse = courses.stream()
                    .filter(c -> c.getCourseId().equals(courseId))
                    .findFirst()
                    .orElse(null);

            if (selectedCourse != null) {
                lessonCourseCombo.setValue(selectedCourse);
                loadTopicsForCourse(courseId);

                // Поиск и выбор нужной темы
                Topic selectedTopic = lessonTopicCombo.getItems().stream()
                        .filter(t -> t.getTopicId().equals(topicId))
                        .findFirst()
                        .orElse(null);

                if (selectedTopic != null) {
                    lessonTopicCombo.setValue(selectedTopic);
                    log.debug("Тема ID={} выбрана автоматически", topicId);
                }
            }

        } catch (Exception e) {
            log.error("Ошибка загрузки начальных данных", e);
        }
    }

    /**
     * Загружает список тем для выбранного курса.
     * @param courseId ID курса
     */
    private void loadTopicsForCourse(Long courseId) {
        log.debug("Загрузка тем для курса ID={}", courseId);
        try {
            TopicDAOImpl topicDAO = new TopicDAOImpl();
            List<Topic> topics = topicDAO.findByCourseId(courseId);
            lessonTopicCombo.getItems().clear();
            lessonTopicCombo.getItems().addAll(topics);
            log.info("Загружено {} тем для курса ID={}", topics.size(), courseId);
        } catch (Exception e) {
            log.error("Ошибка загрузки тем", e);
        }
    }

    /**
     * Настраивает валидацию полей формы в реальном времени.
     */
    private void setupValidation() {
        // Проверка даты (не может быть в прошлом)
        lessonDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isBefore(LocalDate.now())) {
                lessonDatePicker.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
                saveLessonButton.setDisable(true);
                log.warn("Выбрана дата в прошлом: {}", newVal);
            } else {
                lessonDatePicker.setStyle("");
                checkFormValidity();
            }
        });

        // Перепроверка валидности при изменении других полей
        lessonCourseCombo.valueProperty().addListener((obs, oldV, newV) -> checkFormValidity());
        lessonTopicCombo.valueProperty().addListener((obs, oldV, newV) -> checkFormValidity());
        lessonTypeCombo.valueProperty().addListener((obs, oldV, newV) -> checkFormValidity());
    }

    /**
     * Проверяет заполненность всех обязательных полей.
     */
    private void checkFormValidity() {
        boolean isValid = lessonCourseCombo.getValue() != null &&
                lessonTopicCombo.getValue() != null &&
                lessonNumberSpinner.getValue() != null &&
                lessonDatePicker.getValue() != null &&
                lessonTypeCombo.getValue() != null;

        saveLessonButton.setDisable(!isValid);
    }

    /**
     * Обрабатывает сохранение нового занятия.
     */
    @FXML
    private void saveLesson(ActionEvent event) {
        log.info("Сохранение нового занятия");

        // Валидация полей
        if (lessonCourseCombo.getValue() == null) {
            log.warn("Курс не выбран");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.course"));
            return;
        }
        if (lessonTopicCombo.getValue() == null) {
            log.warn("Тема не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.topic"));
            return;
        }
        if (lessonDatePicker.getValue() == null) {
            log.warn("Дата не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.data"));
            return;
        }
        if (lessonTypeCombo.getValue() == null) {
            log.warn("Тип занятия не выбран");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.type"));
            return;
        }

        Long topicId = lessonTopicCombo.getValue().getTopicId();
        Integer lessonNumber = lessonNumberSpinner.getValue();

        log.debug("Подготовка данных: тема ID={}, номер={}, дата={}, тип={}",
                topicId, lessonNumber, lessonDatePicker.getValue(), lessonTypeCombo.getValue());

        // Проверка уникальности номера занятия в теме
        if (!isLessonNumberUnique(topicId, lessonNumber)) {
            log.warn("Занятие с номером {} уже существует в теме ID={}", lessonNumber, topicId);
            showAlert(bundle.getString("alert.error"),
                    bundle.getString("alert.error.lesson_1") + lessonNumber + bundle.getString("alert.error.lesson_2") + "\n" +
                            bundle.getString("alert.error.lesson_3"));
            return;
        }

        try {
            Lesson lesson = new Lesson();
            lesson.setTopicId(topicId);
            lesson.setLessonNumber(lessonNumber);
            lesson.setLessonDate(lessonDatePicker.getValue());
            lesson.setLessonType(lessonTypeCombo.getValue());

            lessonDAO.add(lesson);
            log.info("Занятие успешно добавлено в расписание (ID темы: {}, номер: {})", topicId, lessonNumber);

            showAlert(bundle.getString("alert.yspex"), bundle.getString("alert.addLesson"));

            // Закрытие диалога через кнопку OK (если есть) или прямое закрытие Stage
            Stage stage = (Stage) saveLessonButton.getScene().getWindow();
            stage.close();
            log.debug("Диалоговое окно закрыто");

        } catch (Exception e) {
            log.error("Ошибка БД при сохранении занятия", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.addLesson") + e.getMessage());
        }
    }

    /**
     * Проверяет, свободен ли указанный номер занятия в данной теме.
     * @param topicId ID темы
     * @param lessonNumber номер занятия
     * @return true если номер свободен
     */
    private boolean isLessonNumberUnique(Long topicId, Integer lessonNumber) {
        log.debug("Проверка уникальности номера {} для темы ID={}", lessonNumber, topicId);
        try {
            List<Lesson> existingLessons = lessonDAO.findByTopicId(topicId);
            for (Lesson l : existingLessons) {
                if (l.getLessonNumber().equals(lessonNumber)) {
                    log.debug("Найдено существующее занятие с таким номером");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка проверки уникальности номера занятия", e);
            return false;
        }
        return true;
    }

    /**
     * Обрабатывает отмену действия и закрытие окна.
     */
    @FXML
    private void cancel(ActionEvent event) {
        log.debug("Отмена действия, закрытие окна");
        closeDialog();
    }

    /**
     * Закрывает диалоговое окно.
     */
    private void closeDialog() {
        Stage stage = (Stage) saveLessonButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Показывает информационное или ошибочное диалоговое окно.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Ошибка") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}