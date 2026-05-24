package ru.alesya0711.laba6.view.controllers;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.alesya0711.laba6.dao.impl.*;
import ru.alesya0711.laba6.model.*;
import java.io.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;
import javafx.util.StringConverter;
import ru.alesya0711.laba6.util.DatabaseConnection;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import lombok.extern.slf4j.Slf4j;

/**
 * Контроллер главной панели преподавателя.
 *
 * <p>Управляет основным интерфейсом приложения после успешной авторизации.
 * Предоставляет четыре вкладки для работы с учебным процессом:
 * <ul>
 *   <li><b>Мой профиль</b> — отображение и редактирование данных преподавателя,
 *       управление курсами, группами и студентами</li>
 *   <li><b>Успеваемость</b> — управление индивидуальными заданиями и итоговыми работами,
 *       выставление оценок, фильтрация по курсу/группе/теме</li>
 *   <li><b>Посещаемость</b> — учёт присутствия студентов на занятиях,
 *       каскадная загрузка курсов → групп → тем → занятий</li>
 *   <li><b>Отчеты</b> — генерация комплексных отчётов по успеваемости и посещаемости
 *       за выбранный период с экспортом в PDF</li>
 * </ul>
 *
 * <p>Реализует паттерн MVC: получает данные от DAO-слоя, обрабатывает события
 * пользовательского интерфейса и обновляет отображение в JavaFX-компонентах.
 */
@Slf4j
public class TeacherPanelController {

    // ================= ЭЛЕМЕНТЫ FXML =================
    @FXML private Button logoutButton;

    // Вкладка "Мой профиль"
    @FXML private TextField teacherFullNameField;
    @FXML private TextField teacherEmailField;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, Long> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;

    @FXML private TableView<Group> groupsTable;
    @FXML private TableColumn<Group, Long> groupIdColumn;
    @FXML private TableColumn<Group, String> groupNameColumn;

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Long> studentIdColumn;
    @FXML private TableColumn<Student, String> studentFullNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;

    // Вкладка "Успеваемость"
    @FXML private ComboBox<String> courseComboBox;
    @FXML private ComboBox<String> groupComboBox;
    @FXML private ComboBox<String> topicComboBox;
    @FXML private TableView<AssignmentViewModel> assignmentsTable;
    @FXML private TableView<FinalWorkViewModel> finalWorksTable;
    @FXML private TableColumn<AssignmentViewModel, String> assignStudentColumn;
    @FXML private TableColumn<AssignmentViewModel, String> assignTaskColumn;
    @FXML private TableColumn<AssignmentViewModel, String> assignGradeColumn;
    @FXML private TableColumn<AssignmentViewModel, String> assignStatusColumn;
    @FXML private TableColumn<FinalWorkViewModel, String> finalStudentColumn;
    @FXML private TableColumn<FinalWorkViewModel, String> finalTicketColumn;
    @FXML private TableColumn<FinalWorkViewModel, String> finalTheoryGradeColumn;
    @FXML private TableColumn<FinalWorkViewModel, String> finalPracticeGradeColumn;
    @FXML private TableColumn<FinalWorkViewModel, String> finalAverageColumn;
    private IndividualAssignmentDAOImpl assignmentDAO;
    private FinalWorkDAOImpl finalWorkDAO;
    private TopicDAOImpl topicDAO;

    // Вкладка "Посещаемость"
    @FXML private ComboBox<String> attendanceCourseCombo;
    @FXML private ComboBox<String> attendanceGroupCombo;
    @FXML private ComboBox<String> attendanceTopicCombo;
    @FXML private ComboBox<String> attendanceLessonCombo;

    @FXML private TableView<AttendanceRecord> attendanceMarkTable;
    @FXML private TableColumn<AttendanceRecord, Long> attendMarkIdColumn;
    @FXML private TableColumn<AttendanceRecord, String> attendMarkStudentColumn;
    @FXML private TableColumn<AttendanceRecord, String> attendMarkTopicColumn;
    @FXML private TableColumn<AttendanceRecord, String> attendMarkTypeColumn;
    @FXML private TableColumn<AttendanceRecord, Boolean> attendMarkPresentColumn;
    @FXML private Button saveAttendanceButton;

    // Вкладка "Отчеты"
    @FXML private ComboBox<String> reportCourseCombo;
    @FXML private ComboBox<String> reportGroupCombo;
    @FXML private DatePicker reportStartDate;
    @FXML private DatePicker reportEndDate;
    @FXML private CheckBox includeGradesCheck;
    @FXML private CheckBox includeAttendanceCheck;
    @FXML private Button generateComplexReportButton;
    @FXML private TextArea complexReportArea;


    // ================= DAO И ДАННЫЕ =================
    private Teacher currentTeacher;
    private StudentDAOImpl studentDAO;
    private CourseDAOImpl courseDAO;
    private GroupDAOImpl groupDAO;
    private StudentsGroupDAOImpl studentsGroupDAO;
    private LessonDAOImpl lessonDAO;

    private Long currentTopicId;
    private Long currentCourseIdForFinalWork;

    private Map<String, Long> courseIdMap = new HashMap<>();
    private Map<String, Long> groupIdMap = new HashMap<>();
    private Map<String, Long> topicIdMap = new HashMap<>();
    private Map<String, Long> lessonIdMap = new HashMap<>();
    ResourceBundle bundle = ResourceBundle.getBundle(
            "ru.alesya0711.laba6.resources.strings",
            Locale.getDefault()
    );

    /**
     * Инициализирует контроллер после загрузки FXML.
     *
     * <p>Выполняет следующие действия:
     * <ol>
     *   <li>Создаёт экземпляры всех необходимых DAO для работы с БД</li>
     *   <li>Настраивает таблицы и колонки на вкладке "Мой профиль"</li>
     *   <li>Инициализирует вкладки "Успеваемость", "Посещаемость", "Отчеты"</li>
     *   <li>Загружает данные для каскадных ComboBox посещаемости</li>
     *   <li>Настраивает валидацию полей ввода</li>
     * </ol>
     *
     * @implNote Вызывается автоматически механизмом JavaFX FXMLLoader
     */
    @FXML
    public void initialize() {
        log.info("Инициализация панели преподавателя");
        try {
            log.debug("Создание DAO объектов...");
            studentDAO = new StudentDAOImpl();
            courseDAO = new CourseDAOImpl();
            groupDAO = new GroupDAOImpl();
            topicDAO = new TopicDAOImpl();
            assignmentDAO = new IndividualAssignmentDAOImpl();
            finalWorkDAO = new FinalWorkDAOImpl();
            studentsGroupDAO = new StudentsGroupDAOImpl();
            lessonDAO = new LessonDAOImpl();
            log.info("Все DAO объекты успешно созданы");
        } catch (SQLException e) {
            log.error("Ошибка инициализации DAO объектов", e);
            showAlert(bundle.getString("alert.error.bd"), bundle.getString("alert.error.bd.message") + e.getMessage());
        }

        setupProfileTables();
        setupPerformanceTab();
        setupAttendanceTab();
        loadAttendanceComboBoxData();
        setupReportsTab();
        setupValidation();

        log.info("Панель преподавателя инициализирована");
    }

    //=================== Мой профиль =====================

    /**
     * Устанавливает текущего авторизованного преподавателя и загружает его данные.
     *
     * @param teacher объект {@link Teacher} после успешной аутентификации
     * @implNote Метод также запускает загрузку курсов для всех вкладок интерфейса
     */
    public void setCurrentTeacher(Teacher teacher) {
        log.info("Установка текущего преподавателя: ID={}",
                teacher != null ? teacher.getTeacherId() : "null");

        this.currentTeacher = teacher;
        if (teacher != null) {
            String fullName = teacher.getLastName() + " " + teacher.getFirstName();
            if (teacher.getMiddleName() != null) {
                fullName += " " + teacher.getMiddleName();
            }
            log.debug("ФИО преподавателя: {}", fullName);

            teacherFullNameField.setText(fullName);
            teacherEmailField.setText(teacher.getEmail());

            log.info("Загрузка данных для преподавателя {}", teacher.getTeacherId());
            loadCoursesForTeacher(teacher.getTeacherId());
            loadCoursesForPerformanceTab();
            loadCoursesForAttendance();
            loadCoursesForReports();
        }
    }

    /**
     * Возвращает идентификатор выбранного курса из комбобокса успеваемости.
     *
     * @return ID курса или {@code null}, если курс не выбран
     * @implNote Использует карту {@link #courseIdMap} для преобразования названия в ID
     */
    private Long getSelectedCourseId() {
        String selected = courseComboBox.getSelectionModel().getSelectedItem();
        return courseIdMap.get(selected);
    }

    /**
     * Возвращает идентификатор выбранной группы из комбобокса успеваемости.
     *
     * @return ID группы или {@code null}, если группа не выбрана
     * @implNote Использует карту {@link #groupIdMap} для преобразования названия в ID
     */
    private Long getSelectedGroupId() {
        String selected = groupComboBox.getSelectionModel().getSelectedItem();
        return groupIdMap.get(selected);
    }

    /**
     * Возвращает идентификатор выбранной темы из комбобокса успеваемости.
     *
     * @return ID темы или {@code null}, если тема не выбрана
     * @implNote Использует карту {@link #topicIdMap} для преобразования названия в ID
     */
    private Long getSelectedTopicId() {
        String selected = topicComboBox.getSelectionModel().getSelectedItem();
        return topicIdMap.get(selected);
    }

    /**
     * Настраивает таблицы и обработчики событий на вкладке "Мой профиль".
     *
     * <p>Выполняет:
     * <ul>
     *   <li>Привязку колонок таблиц к свойствам моделей через {@link PropertyValueFactory}</li>
     *   <li>Кастомное отображение порядковых номеров в колонках ID</li>
     *   <li>Формирование полного ФИО студента из отдельных полей</li>
     *   <li>Каскадную загрузку: курс → группы → студенты</li>
     * </ul>
     *
     * @implNote Слушатели выбора в таблицах автоматически загружают связанные данные
     */
    private void setupProfileTables() {
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        courseIdColumn.setCellFactory(column -> new TableCell<Course, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });

        groupIdColumn.setCellValueFactory(new PropertyValueFactory<>("groupId"));
        groupNameColumn.setCellValueFactory(new PropertyValueFactory<>("groupName"));

        groupIdColumn.setCellFactory(column -> new TableCell<Group, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });

        studentIdColumn.setCellFactory(column -> new TableCell<Student, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });

        studentFullNameColumn.setCellValueFactory(cellData -> {
            Student s = cellData.getValue();
            String fullName = s.getLastName() + " " + s.getFirstName();
            if (s.getMiddleName() != null && !s.getMiddleName().isEmpty()) {
                fullName += " " + s.getMiddleName();
            }
            return new SimpleStringProperty(fullName);
        });

        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        coursesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            if (newCourse != null) {
                loadGroupsForCourse(newCourse.getCourseId());
            }
        });

        groupsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldGroup, newGroup) -> {
            if (newGroup != null) {
                loadStudentsForGroup(newGroup.getGroupId());
            }
        });

        if (currentTeacher != null) {
            loadCoursesForTeacher(currentTeacher.getTeacherId());
        }
    }

    /**
     * Загружает список курсов текущего преподавателя в таблицу.
     *
     * @param teacherId идентификатор преподавателя
     * @implNote Использует {@link CourseDAOImpl#findByTeacherId(Long)}
     */
    private void loadCoursesForTeacher(Long teacherId) {
        log.debug("Загрузка курсов для преподавателя ID={}", teacherId);
        try {
            List<Course> courses = courseDAO.findByTeacherId(teacherId);
            log.info("Загружено {} курсов", courses.size());
            coursesTable.setItems(FXCollections.observableArrayList(courses));
        } catch (Exception e) {
            log.error("Ошибка загрузки курсов", e);
        }
    }

    /**
     * Загружает группы выбранного курса в таблицу.
     *
     * @param courseId идентификатор курса
     * @implNote Также очищает таблицу студентов при смене курса
     */
    private void loadGroupsForCourse(Long courseId) {
        log.debug("Загрузка групп для курса ID={}", courseId);
        try {
            List<Group> groups = groupDAO.findByCourseId(courseId);
            log.info("Загружено {} групп", groups.size());
            groupsTable.setItems(FXCollections.observableArrayList(groups));
            studentsTable.setItems(FXCollections.observableArrayList());
        } catch (Exception e) {
            log.error("Ошибка загрузки групп", e);
        }
    }

    /**
     * Загружает студентов выбранной группы в таблицу.
     *
     * @param groupId идентификатор группы
     * @implNote Использует {@link StudentsGroupDAOImpl} для получения связи многие-ко-многим
     */
    private void loadStudentsForGroup(Long groupId) {
        log.debug("Загрузка студентов для группы ID={}", groupId);
        try {
            List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            log.debug("Найдено {} идентификаторов студентов", studentIds.size());

            ObservableList<Student> students = FXCollections.observableArrayList();
            for (Long studentId : studentIds) {
                Optional<Student> studentOpt = studentDAO.getById(studentId);
                studentOpt.ifPresent(students::add);
            }

            log.info("Загружено {} студентов", students.size());
            studentsTable.setItems(students);
        } catch (Exception e) {
            log.error("Ошибка загрузки студентов", e);
        }
    }

    // ================= УСПЕВАЕМОСТЬ =================

    /**
     * Настраивает вкладки и обработчики событий на вкладке "Успеваемость".
     *
     * <p>Выполняет:
     * <ul>
     *   <li>Привязку колонок таблиц к свойствам {@link AssignmentViewModel} и {@link FinalWorkViewModel}</li>
     *   <li>Каскадные слушатели для комбобоксов: курс → группы/темы → таблицы</li>
     *   <li>Автоматическое обновление таблиц при изменении фильтров</li>
     * </ul>
     */
    private void setupPerformanceTab() {
        assignStudentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        assignTaskColumn.setCellValueFactory(new PropertyValueFactory<>("assignmentName"));
        assignGradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        assignStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        finalStudentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        finalTicketColumn.setCellValueFactory(new PropertyValueFactory<>("ticketNumber"));
        finalTheoryGradeColumn.setCellValueFactory(new PropertyValueFactory<>("theoryGrade"));
        finalPracticeGradeColumn.setCellValueFactory(new PropertyValueFactory<>("practiceGrade"));
        finalAverageColumn.setCellValueFactory(new PropertyValueFactory<>("averageGrade"));

        courseComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Long courseId = getSelectedCourseId();
                if (courseId != null) {
                    loadGroupsForCourseP(courseId);
                    loadTopicsForCourse(courseId);
                    clearPerformanceTables();
                }
            } else {
                groupComboBox.setItems(FXCollections.observableArrayList());
                topicComboBox.setItems(FXCollections.observableArrayList());
            }
        });

        groupComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePerformanceTables();
        });

        topicComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updatePerformanceTables();
        });
    }

    /**
     * Загружает курсы преподавателя в комбобокс вкладки "Успеваемость".
     *
     * @implNote Заполняет карту {@link #courseIdMap} для быстрого преобразования названия в ID
     */
    private void loadCoursesForPerformanceTab() {
        log.debug("Загрузка курсов для вкладки успеваемости");
        try {
            List<Course> courses = courseDAO.findByTeacherId(currentTeacher.getTeacherId());
            log.info("Загружено {} курсов для успеваемости", courses.size());

            ObservableList<String> courseItems = FXCollections.observableArrayList();
            courseIdMap.clear();

            for (Course c : courses) {
                String courseName = c.getCourseName();
                courseItems.add(courseName);
                courseIdMap.put(courseName, c.getCourseId());
            }

            courseComboBox.setItems(courseItems);
        } catch (Exception e) {
            log.error("Ошибка загрузки курсов для успеваемости", e);
        }
    }

    /**
     * Загружает группы выбранного курса в комбобокс успеваемости.
     *
     * @param courseId идентификатор курса
     * @implNote Использует {@link Platform#runLater(Runnable)} для безопасного обновления UI
     */
    private void loadGroupsForCourseP(Long courseId) {
        log.debug("Загрузка групп для курса ID={} (успеваемость)", courseId);
        try {
            List<Group> groups = groupDAO.findByCourseId(courseId);
            log.info("Загружено {} групп", groups.size());

            ObservableList<String> groupItems = FXCollections.observableArrayList();
            groupIdMap.clear();

            for (Group g : groups) {
                String groupName = g.getGroupName();
                groupItems.add(groupName);
                groupIdMap.put(groupName, g.getGroupId());
            }

            Platform.runLater(() -> {
                groupComboBox.getItems().clear();
                groupComboBox.setItems(groupItems);
                groupComboBox.getSelectionModel().clearSelection();
                groupComboBox.setDisable(groupItems.isEmpty());
                log.debug("ComboBox групп обновлен (disabled={})", groupComboBox.isDisable());
            });
        } catch (Exception e) {
            log.error("Ошибка загрузки групп", e);
        }
    }

    /**
     * Загружает темы выбранного курса в комбобокс успеваемости.
     *
     * @param courseId идентификатор курса
     * @implNote Использует {@link Platform#runLater(Runnable)} для безопасного обновления UI
     */
    private void loadTopicsForCourse(Long courseId) {
        log.debug("Загрузка тем для курса ID={}", courseId);
        try {
            List<Topic> topics = topicDAO.findByCourseId(courseId);
            log.info("Загружено {} тем", topics.size());

            ObservableList<String> topicItems = FXCollections.observableArrayList();
            topicIdMap.clear();

            for (Topic t : topics) {
                String topicName = t.getTopicName();
                topicItems.add(topicName);
                topicIdMap.put(topicName, t.getTopicId());
            }

            Platform.runLater(() -> {
                topicComboBox.getItems().clear();
                topicComboBox.setItems(topicItems);
                topicComboBox.getSelectionModel().clearSelection();
                topicComboBox.setDisable(topicItems.isEmpty());
                log.debug("ComboBox тем обновлен (disabled={})", topicComboBox.isDisable());
            });
        } catch (Exception e) {
            log.error("Ошибка загрузки тем", e);
        }
    }

    /**
     * Обновляет таблицы индивидуальных заданий и итоговых работ при изменении фильтров.
     *
     * @implNote Сохраняет текущие выбранные значения комбобоксов для восстановления после обновления
     */
    private void updatePerformanceTables() {
        String selectedCourse = courseComboBox.getValue();
        String selectedGroup = groupComboBox.getValue();
        String selectedTopic = topicComboBox.getValue();

        Long courseId = getSelectedCourseId();
        Long groupId = getSelectedGroupId();
        Long topicId = getSelectedTopicId();

        if (courseId != null && groupId != null && topicId != null) {
            loadAssignmentsForGroupAndTopic(groupId, topicId);
            loadFinalWorksForGroup(groupId, courseId);
        }

        Platform.runLater(() -> {
            if (selectedCourse != null) courseComboBox.setValue(selectedCourse);
            if (selectedGroup != null) groupComboBox.setValue(selectedGroup);
            if (selectedTopic != null) topicComboBox.setValue(selectedTopic);
        });
    }

    /**
     * Загружает индивидуальные задания для выбранной группы и темы.
     *
     * @param groupId идентификатор группы
     * @param topicId идентификатор темы
     * @implNote Фильтрует задания по теме и преобразует в {@link AssignmentViewModel} для отображения
     */
    private void loadAssignmentsForGroupAndTopic(Long groupId, Long topicId) {
        log.debug("Загрузка индивидуальных заданий для группы ID={} и темы ID={}", groupId, topicId);
        this.currentTopicId = topicId;
        assignmentsTable.setItems(FXCollections.observableArrayList());
        try {
            List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            ObservableList<AssignmentViewModel> assignments = FXCollections.observableArrayList();
            int count = 0;

            for (Long studentId : studentIds) {
                Optional<Student> studentOpt = studentDAO.getById(studentId);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    List<IndividualAssignment> studentAssignments = assignmentDAO.findByStudentId(studentId);

                    for (IndividualAssignment assignment : studentAssignments) {
                        if (assignment.getTopicId().equals(topicId)) {
                            assignments.add(new AssignmentViewModel(
                                    student.getLastName() + " " + student.getFirstName(),
                                    assignment.getAssignmentName(),
                                    assignment.getGrade() != null ? assignment.getGrade().toString() : "Не сдано",
                                    assignment.getStatus()
                            ));
                            count++;
                        }
                    }
                }
            }
            log.info("Загружено {} индивидуальных заданий", count);
            assignmentsTable.setItems(assignments);
        } catch (Exception e) {
            log.error("Ошибка загрузки индивидуальных заданий", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Добавить задание" на вкладке успеваемости.
     *
     * @param event событие нажатия кнопки
     * @implNote Открывает модальное диалоговое окно {@link AssignmentDialogController}
     */
    @FXML
    private void addAssignment(ActionEvent event) {
        log.info("Добавление индивидуального задания");

        if (currentTopicId == null) {
            log.warn("Тема не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.topic"));
            return;
        }

        Long groupId = getSelectedGroupId();
        if (groupId == null) {
            log.warn("Группа не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.group"));
            return;
        }

        log.debug("Открытие диалога добавления задания (topicId={}, groupId={})", currentTopicId, groupId);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/assignment-dialog.fxml"), bundle);
            VBox dialogRoot = loader.load();

            AssignmentDialogController controller = loader.getController();
            controller.setEditMode(false, null);
            controller.setCurrentTopicId(currentTopicId);
            controller.loadStudents(groupId);
            log.debug("Контроллер диалога инициализирован");

            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(logoutButton.getScene().getWindow());
            dialogStage.setTitle(bundle.getString("assignment.dialog.title.add"));
            dialogStage.setScene(new Scene(dialogRoot));

            dialogStage.showAndWait();

            updatePerformanceTables();
            setAuditTeacherId(null);
            log.info("Задание добавлено пользователем");

        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML диалога", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.dialog") + e.getMessage());
        } finally {
            setAuditTeacherId(null);
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Редактировать задание".
     *
     * @param event событие нажатия кнопки
     * @implNote Находит данные задания в БД и передаёт их в {@link AssignmentDialogController}
     */
    @FXML
    private void editAssignment(ActionEvent event) {
        log.info("Редактирование индивидуального задания");

        AssignmentViewModel selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Задание для редактирования не выбрано");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.assigment") );
            return;
        }

        log.debug("Выбрано задание для редактирования: student={}, task={}",
                selected.getStudentName(), selected.getAssignmentName());

        Long groupId = getSelectedGroupId();
        if (groupId == null) {
            log.warn("Группа не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.group2") );
            return;
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "ru.alesya0711.laba6.resources.strings",
                    Locale.getDefault()
            );

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/assignment-dialog.fxml"), bundle);
            VBox dialogRoot = loader.load();

            AssignmentDialogController controller = loader.getController();

            try {
                IndividualAssignmentDAOImpl assignmentDAO = new IndividualAssignmentDAOImpl();
                StudentDAOImpl studentDAO = new StudentDAOImpl();

                Student foundStudent = null;
                IndividualAssignment foundAssignment = null;

                String[] selectedNameParts = selected.getStudentName().split("\\s+");
                String selectedLastName = selectedNameParts.length > 0 ? selectedNameParts[0] : "";
                String selectedFirstName = selectedNameParts.length > 1 ? selectedNameParts[1] : "";

                List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);

                for (Long studentId : studentIds) {
                    Optional<Student> studentOpt = studentDAO.getById(studentId);
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        if (student.getLastName().equals(selectedLastName) &&
                                student.getFirstName().equals(selectedFirstName)) {
                            foundStudent = student;
                            List<IndividualAssignment> assignments = assignmentDAO.findByStudentId(studentId);
                            for (IndividualAssignment assignment : assignments) {
                                if (assignment.getAssignmentName().equals(selected.getAssignmentName())) {
                                    foundAssignment = assignment;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                if (foundAssignment != null && foundStudent != null) {
                    controller.setEditMode(true, foundAssignment.getAssignmentId());
                    controller.setCurrentTopicId(currentTopicId);
                    controller.fillAssignmentData(foundAssignment, foundStudent);
                    log.debug("Данные задания найдены и переданы в контроллер");
                } else {
                    log.error("Не удалось найти данные задания в БД");
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.bd2") );
                    return;
                }

            } catch (SQLException e) {
                log.error("Ошибка БД при поиске задания", e);
                showAlert(bundle.getString("alert.error.bd3") , bundle.getString("alert.error.bd4")  + e.getMessage());
                return;
            }

            controller.loadStudents(groupId);
            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(logoutButton.getScene().getWindow());
            dialogStage.setTitle(bundle.getString("assignment.dialog.title.edit"));
            dialogStage.setScene(new Scene(dialogRoot));

            dialogStage.showAndWait();

            setAuditTeacherId(null);
            updatePerformanceTables();
            log.info("Задание обновлено");

        } catch (IOException e) {
            log.error("Ошибка при редактировании задания", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.dialog")  + e.getMessage());
        } finally {
            setAuditTeacherId(null);
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Выставить оценку" для индивидуального задания.
     *
     * @param event событие нажатия кнопки
     * @implNote Использует {@link TextInputDialog} для ввода оценки и валидирует диапазон 0-100
     */
    @FXML
    private void gradeAssignment(ActionEvent event) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ru.alesya0711.laba6.resources.strings",
                Locale.getDefault()
        );
        log.info("Выставление оценки за индивидуальное задание");

        AssignmentViewModel selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Задание для оценки не выбрано");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.assigment.grade"));
            return;
        }

        TextInputDialog gradeDialog = new TextInputDialog(selected.getGrade());
        gradeDialog.setTitle(bundle.getString("assignment.grade.title"));
        gradeDialog.setHeaderText(bundle.getString("assignment.grade.text2") + " " + selected.getStudentName());
        gradeDialog.setContentText(bundle.getString("assignment.grade.text3"));

        Optional<String> result = gradeDialog.showAndWait();

        if (result.isPresent()) {
            String gradeInput = result.get().trim();
            log.debug("Пользователь ввёл оценку: '{}'", gradeInput);

            try {
                int gradeValue = Integer.parseInt(gradeInput);

                if (gradeValue < 0 || gradeValue > 100) {
                    log.warn("Некорректный диапазон оценки: {}", gradeValue);
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.grade"));
                    return;
                }

                log.info("Попытка сохранить оценку {} для задания '{}'", gradeValue, selected.getAssignmentName());

                setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);
                StudentDAOImpl studentDAO = new StudentDAOImpl();
                IndividualAssignmentDAOImpl assignmentDAO = new IndividualAssignmentDAOImpl();

                Long studentId = findStudentIdByName(selected.getStudentName());
                if (studentId == null) {
                    log.error("Студент {} не найден в БД", selected.getStudentName());
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.student"));
                    return;
                }

                List<IndividualAssignment> assignments = assignmentDAO.findByStudentId(studentId);
                IndividualAssignment foundAssignment = null;

                for (IndividualAssignment a : assignments) {
                    if (a.getAssignmentName().equals(selected.getAssignmentName())) {
                        foundAssignment = a;
                        break;
                    }
                }

                if (foundAssignment != null) {
                    foundAssignment.setGrade(gradeValue);
                    foundAssignment.setStatus(gradeValue >= 50 ? "GRADED" : "SUBMITTED");

                    assignmentDAO.update(foundAssignment);
                    setAuditTeacherId(null);
                    updatePerformanceTables();

                    log.info("Оценка {} успешно выставлена за задание '{}' (статус изменён на {})",
                            gradeValue, selected.getAssignmentName(), foundAssignment.getStatus());
                    showAlert(bundle.getString("alert.yspex"), bundle.getString("alert.grade") + gradeValue + " !");
                } else {
                    log.error("Задание '{}' не найдено в БД для студента ID={}",
                            selected.getAssignmentName(), studentId);
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.assigment"));
                }

            } catch (NumberFormatException e) {
                log.warn("Неверный формат ввода оценки: '{}'", gradeInput);
                showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.number"));
            } catch (SQLException e) {
                log.error("Ошибка БД при сохранении оценки", e);
                showAlert("Ошибка БД", bundle.getString("alert.error.grade.save") + e.getMessage());
            } finally {
                setAuditTeacherId(null);
            }
        } else {
            log.debug("Выставление оценки отменено пользователем");
        }
    }

    /**
     * Находит идентификатор студента по ФИО.
     *
     * @param studentName полное ФИО студента
     * @return ID студента или {@code null}, если не найден
     * @implNote Выполняет линейный поиск по всем студентам (может быть оптимизировано)
     */
    private Long findStudentIdByName(String studentName) {
        try {
            StudentDAOImpl studentDAO = new StudentDAOImpl();
            String[] nameParts = studentName.split("\\s+");
            String lastName = nameParts.length > 0 ? nameParts[0] : "";
            String firstName = nameParts.length > 1 ? nameParts[1] : "";

            List<Student> allStudents = studentDAO.getAll();
            for (Student s : allStudents) {
                if (s.getLastName().equals(lastName) && s.getFirstName().equals(firstName)) {
                    return s.getStudentId();
                }
            }
        } catch (Exception e) {
            log.error("Ошибка поиска ID студента по имени", e);
        }
        return null;
    }

    /**
     * Обрабатывает нажатие кнопки "Удалить задание".
     *
     * @param event событие нажатия кнопки
     * @implNote Использует мягкое удаление через {@link IndividualAssignmentDAOImpl#softDelete(Long)}
     */
    @FXML
    private void deleteAssignment(ActionEvent event) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ru.alesya0711.laba6.resources.strings",
                Locale.getDefault()
        );
        log.info("Удаление индивидуального задания");

        AssignmentViewModel selected = assignmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Задание для удаления не выбрано");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.assigment.delete"));
            return;
        }

        log.debug("Выбрано задание для удаления: student={}, task={}",
                selected.getStudentName(), selected.getAssignmentName());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("assignment.delete.title"));
        alert.setHeaderText(bundle.getString("assignment.delete.text1"));
        alert.setContentText(bundle.getString("assignment.delete.text2"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.info("Пользователь подтвердил удаление");
            try {
                IndividualAssignmentDAOImpl assignmentDAO = new IndividualAssignmentDAOImpl();
                StudentDAOImpl studentDAO = new StudentDAOImpl();

                Long groupId = getSelectedGroupId();
                if (groupId == null) {
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.group2"));
                    return;
                }

                Long assignmentIdToDelete = null;

                String[] nameParts = selected.getStudentName().split("\\s+");
                String lastName = nameParts.length > 0 ? nameParts[0] : "";
                String firstName = nameParts.length > 1 ? nameParts[1] : "";

                List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);

                for (Long studentId : studentIds) {
                    Optional<Student> studentOpt = studentDAO.getById(studentId);
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();

                        if (student.getLastName().equals(lastName) &&
                                student.getFirstName().equals(firstName)) {

                            List<IndividualAssignment> assignments = assignmentDAO.findByStudentId(studentId);

                            for (IndividualAssignment assignment : assignments) {
                                if (assignment.getAssignmentName().equals(selected.getAssignmentName())) {
                                    assignmentIdToDelete = assignment.getAssignmentId();
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                if (assignmentIdToDelete == null) {
                    log.error("Не удалось найти задание в базе данных");
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.bd2"));
                    return;
                }
                setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

                assignmentDAO.softDelete(assignmentIdToDelete);
                setAuditTeacherId(null);

                log.info("Задание успешно удалено из БД");
                updatePerformanceTables();
                showAlert(bundle.getString("alert.yspex"), bundle.getString("alert.assigment.yspex"));

            } catch (SQLException e) {
                log.error("Ошибка удаления задания из БД", e);
                showAlert(bundle.getString("alert.error.bd3"), bundle.getString("alert.assigment.delete.error") + e.getMessage());
            }
        } else {
            log.debug("Удаление отменено пользователем");
        }
    }

    /**
     * Загружает итоговые работы для выбранной группы и курса.
     *
     * @param groupId идентификатор группы
     * @param courseId идентификатор курса
     * @implNote Фильтрует только активные работы и рассчитывает средний балл
     */
    private void loadFinalWorksForGroup(Long groupId, Long courseId) {
        log.debug("Загрузка итоговых работ для группы ID={} и курса ID={}", groupId, courseId);
        try {
            List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            ObservableList<FinalWorkViewModel> finalWorks = FXCollections.observableArrayList();
            int count = 0;

            for (Long studentId : studentIds) {
                Optional<Student> studentOpt = studentDAO.getById(studentId);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    List<FinalWork> studentFinalWorks = finalWorkDAO.findByStudentId(studentId);

                    for (FinalWork fw : studentFinalWorks) {
                        if (fw.getCourseId().equals(courseId)) {
                            Double average = null;
                            if (fw.getTheoryGrade() != null && fw.getPracticeGrade() != null) {
                                average = (fw.getTheoryGrade() + fw.getPracticeGrade()) / 2.0;
                            }

                            finalWorks.add(new FinalWorkViewModel(
                                    student.getLastName() + " " + student.getFirstName(),
                                    fw.getTicketNumber(),
                                    fw.getTheoryGrade() != null ? fw.getTheoryGrade().toString() : "-",
                                    fw.getPracticeGrade() != null ? fw.getPracticeGrade().toString() : "-",
                                    average != null ? String.format("%.1f", average) : "-"
                            ));
                            count++;
                        }
                    }
                }
            }
            log.info("Загружено {} итоговых работ", count);
            finalWorksTable.setItems(finalWorks);
        } catch (Exception e) {
            log.error("Ошибка загрузки итоговых работ", e);
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Добавить итоговую работу".
     *
     * @param event событие нажатия кнопки
     * @implNote Открывает модальное диалоговое окно {@link FinalWorkDialogController}
     */
    @FXML
    private void addFinalWork(ActionEvent event) {

        log.info("Добавление итоговой работы");

        Long courseId = getSelectedCourseId();
        Long groupId = getSelectedGroupId();

        if (courseId == null || groupId == null) {
            log.warn("Курс или группа не выбраны (courseId={}, groupId={})", courseId, groupId);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.groupandcourse"));
            return;
        }

        log.debug("Открытие диалога добавления итоговой работы (courseId={}, groupId={})", courseId, groupId);
        currentCourseIdForFinalWork = courseId;

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "ru.alesya0711.laba6.resources.strings",
                    Locale.getDefault()
            );

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/final-work-dialog.fxml"), bundle);
            VBox dialogRoot = loader.load();

            FinalWorkDialogController controller = loader.getController();
            controller.setEditMode(false, null);
            controller.setCurrentCourseId(courseId);
            controller.loadStudents(groupId);
            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(logoutButton.getScene().getWindow());
            dialogStage.setTitle(bundle.getString("assignment.dialog.final.add"));
            dialogStage.setScene(new Scene(dialogRoot));

            dialogStage.showAndWait();

            setAuditTeacherId(null);
            updatePerformanceTables();
            log.info("Итоговая работа добавлена");

        } catch (IOException e) {
            log.error("Ошибка при добавлении итоговой работы", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.dialog") + e.getMessage());
        } finally {
            setAuditTeacherId(null);
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Редактировать итоговую работу".
     *
     * @param event событие нажатия кнопки
     * @implNote Находит данные работы в БД и передаёт их в {@link FinalWorkDialogController}
     */
    @FXML
    private void editFinalWork(ActionEvent event) {
        log.info("Редактирование итоговой работы");

        FinalWorkViewModel selected = finalWorksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Итоговая работа для редактирования не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.final.error.edit"));
            return;
        }

        log.debug("Выбрана работа для редактирования: student={}, ticket={}",
                selected.getStudentName(), selected.getTicketNumber());

        Long courseId = getSelectedCourseId();
        Long groupId = getSelectedGroupId();

        if (courseId == null || groupId == null) {
            log.warn("Курс или группа не выбраны (courseId={}, groupId={})", courseId, groupId);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.groupandcourse2"));
            return;
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "ru.alesya0711.laba6.resources.strings",
                    Locale.getDefault()
            );
            FinalWorkDAOImpl finalWorkDAO = new FinalWorkDAOImpl();
            StudentDAOImpl studentDAO = new StudentDAOImpl();

            Student foundStudent = findStudentByName(selected.getStudentName(), groupId);
            if (foundStudent == null) {
                log.error("Студент {} не найден в группе ID={}", selected.getStudentName(), groupId);
                showAlert(bundle.getString("alert.error"), bundle.getString("alert.student"));
                return;
            }

            List<FinalWork> existingWorks = finalWorkDAO.findByStudentId(foundStudent.getStudentId());
            FinalWork foundFinalWork = null;

            for (FinalWork fw : existingWorks) {
                if (fw.getCourseId().equals(courseId)) {
                    foundFinalWork = fw;
                    break;
                }
            }

            if (foundFinalWork == null) {
                log.error("Итоговая работа не найдена в БД для студента ID={} и курса ID={}",
                        foundStudent.getStudentId(), courseId);
                showAlert(bundle.getString("alert.error"), bundle.getString("alert.final.error.bd"));
                return;
            }
            log.debug("Найдена работа: ID={}, билет={}", foundFinalWork.getFinalId(), foundFinalWork.getTicketNumber());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/final-work-dialog.fxml"), bundle);
            VBox dialogRoot = loader.load();

            FinalWorkDialogController controller = loader.getController();
            controller.setEditMode(true, foundFinalWork.getFinalId());
            controller.setCurrentCourseId(courseId);
            controller.loadStudents(groupId);
            controller.fillFinalWorkData(foundFinalWork, foundStudent);
            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(logoutButton.getScene().getWindow());
            dialogStage.setTitle(bundle.getString("assignment.dialog.final.edit"));
            dialogStage.setScene(new Scene(dialogRoot));

            dialogStage.showAndWait();

            setAuditTeacherId(null);
            updatePerformanceTables();
            log.info("Итоговая работа успешно обновлена");

        } catch (IOException e) {
            log.error("Ошибка при загрузке FXML диалога", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.dialog") + e.getMessage());
        } catch (SQLException e) {
            log.error("Ошибка БД при загрузке данных итоговой работы", e);
            showAlert(bundle.getString("alert.error.bd3"), bundle.getString("alert.error.bd4") + e.getMessage());
        } finally {
            setAuditTeacherId(null);
        }
    }

    /**
     * Находит объект студента по ФИО в указанной группе.
     *
     * @param studentName полное ФИО студента
     * @param groupId идентификатор группы для поиска
     * @return объект {@link Student} или {@code null}, если не найден
     */
    private Student findStudentByName(String studentName, Long groupId) {
        try {
            StudentDAOImpl studentDAO = new StudentDAOImpl();
            String[] nameParts = studentName.split("\\s+");
            String lastName = nameParts.length > 0 ? nameParts[0] : "";
            String firstName = nameParts.length > 1 ? nameParts[1] : "";

            List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            for (Long studentId : studentIds) {
                Optional<Student> studentOpt = studentDAO.getById(studentId);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    if (student.getLastName().equals(lastName) &&
                            student.getFirstName().equals(firstName)) {
                        return student;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка поиска студента по имени", e);
        }
        return null;
    }

    /**
     * Обрабатывает нажатие кнопки "Удалить итоговую работу".
     *
     * @param event событие нажатия кнопки
     * @implNote Использует мягкое удаление через {@link FinalWorkDAOImpl#softDelete(Long)}
     */
    @FXML
    private void deleteFinalWork(ActionEvent event) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ru.alesya0711.laba6.resources.strings",
                Locale.getDefault()
        );
        log.info("Удаление итоговой работы");

        FinalWorkViewModel selected = finalWorksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            log.warn("Итоговая работа для удаления не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.final.delete"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("assignment.dialog.final.delete.title"));
        alert.setHeaderText(bundle.getString("assignment.dialog.final.delete.text1"));
        alert.setContentText(bundle.getString("assignment.dialog.final.delete.text2")
                + selected.getStudentName() + "?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.info("Пользователь подтвердил удаление итоговой работы");

            try {
                FinalWorkDAOImpl finalWorkDAO = new FinalWorkDAOImpl();
                StudentDAOImpl studentDAO = new StudentDAOImpl();

                Long courseId = getSelectedCourseId();
                Long groupId = getSelectedGroupId();

                if (courseId == null || groupId == null) {
                    log.warn("Курс или группа не выбраны");
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.groupandcourse2"));
                    return;
                }

                Long studentIdFound = null;
                String[] nameParts = selected.getStudentName().split("\\s+");
                String lastName = nameParts.length > 0 ? nameParts[0] : "";
                String firstName = nameParts.length > 1 ? nameParts[1] : "";

                List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
                for (Long sid : studentIds) {
                    Optional<Student> opt = studentDAO.getById(sid);
                    if (opt.isPresent()) {
                        Student s = opt.get();
                        if (s.getLastName().equals(lastName) && s.getFirstName().equals(firstName)) {
                            studentIdFound = sid;
                            log.debug("Найден ID студента: {}", studentIdFound);
                            break;
                        }
                    }
                }

                if (studentIdFound == null) {
                    log.error("Студент {} не найден в выбранной группе ID={}", selected.getStudentName(), groupId);
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.student"));
                    return;
                }

                Long finalWorkIdToDelete = null;
                List<FinalWork> works = finalWorkDAO.findByStudentId(studentIdFound);
                for (FinalWork fw : works) {
                    if (fw.getCourseId().equals(courseId)) {
                        finalWorkIdToDelete = fw.getFinalId();
                        log.debug("Найден ID итоговой работы для удаления: {}", finalWorkIdToDelete);
                        break;
                    }
                }

                if (finalWorkIdToDelete == null) {
                    log.error("Итоговая работа не найдена в БД для студента ID={} и курса ID={}",
                            studentIdFound, courseId);
                    showAlert(bundle.getString("alert.error"), bundle.getString("alert.final.error.bd"));
                    return;
                }

                setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);
                finalWorkDAO.softDelete(finalWorkIdToDelete);
                setAuditTeacherId(null);

                log.info("Итоговая работа ID={} успешно удалена (soft delete)", finalWorkIdToDelete);
                updatePerformanceTables();
                showAlert(bundle.getString("alert.yspex"), bundle.getString("alert.final.yspex"));

            } catch (SQLException e) {
                log.error("Ошибка БД при удалении итоговой работы", e);
                showAlert(bundle.getString("alert.error.bd3"), bundle.getString("alert.final.error.bd2") + e.getMessage());
            }
        } else {
            log.debug("Удаление итоговой работы отменено пользователем");
        }
    }

    /**
     * Очищает таблицы и комбобоксы вкладки "Успеваемость".
     */
    private void clearPerformanceTables() {
        groupComboBox.setItems(FXCollections.observableArrayList());
        topicComboBox.setItems(FXCollections.observableArrayList());
        assignmentsTable.setItems(FXCollections.observableArrayList());
        finalWorksTable.setItems(FXCollections.observableArrayList());
    }

    /**
     * Модель для отображения индивидуальных заданий в таблице.
     *
     * <p>Используется как ViewModel для отделения логики отображения от бизнес-логики.
     * Содержит только данные, необходимые для отображения в таблице успеваемости.
     */
    public class AssignmentViewModel {
        private final String studentName;
        private final String assignmentName;
        private final String grade;
        private final String status;

        /**
         * Создаёт модель представления для индивидуального задания.
         *
         * @param studentName ФИО студента
         * @param assignmentName название задания
         * @param grade оценка (строковое представление)
         * @param status статус выполнения
         */
        public AssignmentViewModel(String studentName, String assignmentName, String grade, String status) {
            this.studentName = studentName;
            this.assignmentName = assignmentName;
            this.grade = grade;
            this.status = status;
        }

        public String getStudentName() { return studentName; }
        public String getAssignmentName() { return assignmentName; }
        public String getGrade() { return grade; }
        public String getStatus() { return status; }
    }

    /**
     * Модель для отображения итоговых работ в таблице.
     */
    public class FinalWorkViewModel {
        private final String studentName;
        private final String ticketNumber;
        private final String theoryGrade;
        private final String practiceGrade;
        private final String averageGrade;

        /**
         * Создаёт модель представления для итоговой работы.
         *
         * @param studentName ФИО студента
         * @param ticketNumber номер билета
         * @param theoryGrade оценка по теории
         * @param practiceGrade оценка по практике
         * @param averageGrade средний балл (рассчитанный)
         */
        public FinalWorkViewModel(String studentName, String ticketNumber, String theoryGrade,
                                  String practiceGrade, String averageGrade) {
            this.studentName = studentName;
            this.ticketNumber = ticketNumber;
            this.theoryGrade = theoryGrade;
            this.practiceGrade = practiceGrade;
            this.averageGrade = averageGrade;
        }

        public String getStudentName() { return studentName; }
        public String getTicketNumber() {return ticketNumber;}
        public String getTheoryGrade() {return theoryGrade;}
        public String getPracticeGrade() {return practiceGrade;}
        public String getAverageGrade() {return averageGrade;}
    }

    //===================== посещаемость================

    /**
     * Загружает данные для каскадных комбобоксов вкладки "Посещаемость".
     *
     * @implNote Запускает загрузку курсов текущего преподавателя
     */
    private void loadAttendanceComboBoxData() {
        if (currentTeacher != null) {
            loadCoursesForAttendance();
        }
    }

    /**
     * Внутренний класс для хранения состояния посещаемости студента на занятии.
     */
    private class AttendanceRecord {
        private final Student student;
        private final BooleanProperty present;
        private final String topicName;
        private final String lessonType;

        /**
         * Создаёт запись о посещаемости.
         *
         * @param student объект студента
         * @param isPresent флаг присутствия
         * @param topic название темы занятия
         * @param type тип занятия (Лекция, Практика и т.д.)
         */
        public AttendanceRecord(Student student, boolean isPresent, String topic, String type) {
            this.student = student;
            this.present = new SimpleBooleanProperty(isPresent);
            this.topicName = topic != null ? topic : "";
            this.lessonType = type != null ? type : "";
        }

        public Student getStudent() { return student; }
        public BooleanProperty presentProperty() { return present; }
        public boolean isPresent() { return present.get(); }
        public void setPresent(boolean value) { present.set(value); }
        public String getTopicName() { return topicName; }
        public String getLessonType() { return lessonType; }
    }

    private ObservableList<AttendanceRecord> attendanceRecords = FXCollections.observableArrayList();

    /**
     * Настраивает таблицу и обработчики событий на вкладке "Посещаемость".
     *
     * <p>Выполняет:
     * <ul>
     *   <li>Привязку колонок таблицы к свойствам {@link AttendanceRecord}</li>
     *   <li>Настройку чекбокса для отметки присутствия с обработчиком изменения</li>
     *   <li>Каскадные слушатели для комбобоксов: курс → группы/темы → занятия → таблица</li>
     * </ul>
     */
    private void setupAttendanceTab() {
        attendMarkIdColumn.setCellValueFactory(cellData -> {
            AttendanceRecord record = cellData.getValue();
            return new SimpleObjectProperty<>(record.getStudent().getStudentId());
        });

        attendMarkStudentColumn.setCellValueFactory(cellData -> {
            AttendanceRecord record = cellData.getValue();
            Student student = record.getStudent();
            String fio = student.getLastName() + " " + student.getFirstName();
            if (student.getMiddleName() != null) fio += " " + student.getMiddleName();
            return new SimpleStringProperty(fio);
        });

        attendMarkTopicColumn.setCellValueFactory(cellData -> {
            AttendanceRecord record = cellData.getValue();
            return new SimpleStringProperty(record.getTopicName());
        });

        attendMarkTypeColumn.setCellValueFactory(cellData -> {
            AttendanceRecord record = cellData.getValue();
            return new SimpleStringProperty(record.getLessonType());
        });

        attendMarkPresentColumn.setCellValueFactory(cellData -> {
            AttendanceRecord record = cellData.getValue();
            return record.presentProperty();
        });

        attendMarkPresentColumn.setCellFactory(column -> new TableCell<AttendanceRecord, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    AttendanceRecord record = (AttendanceRecord) getTableRow().getItem();
                    if (record != null) {
                        record.setPresent(checkBox.isSelected());
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item != null && item);
                    setGraphic(checkBox);
                }
            }
        });

        attendanceCourseCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Long courseId = courseIdMap.get(newVal);
                if (courseId != null) {
                    attendanceGroupCombo.getItems().clear();
                    attendanceTopicCombo.getItems().clear();
                    attendanceLessonCombo.getItems().clear();
                    attendanceMarkTable.getItems().clear();
                    attendanceGroupCombo.setValue(null);
                    attendanceTopicCombo.setValue(null);

                    loadGroupsForAttendance(courseId);
                    loadTopicsForAttendance(courseId);
                }
            }
        });

        attendanceGroupCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {});

        attendanceTopicCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Long topicId = topicIdMap.get(newVal);
                if (topicId != null) {
                    lessonIdMap.clear();
                    attendanceLessonCombo.getItems().clear();
                    attendanceLessonCombo.setValue(null);
                    attendanceMarkTable.getItems().clear();

                    loadLessonsForAttendance(topicId);

                    attendMarkTopicColumn.setVisible(false);
                    attendMarkTopicColumn.setVisible(true);
                }
            }
        });

        attendanceLessonCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String selectedGroup = attendanceGroupCombo.getValue();
                Long groupId = groupIdMap.get(selectedGroup);

                if (groupId != null) {
                    loadStudentsForAttendanceTable(groupId);
                }

                attendMarkTypeColumn.setVisible(false);
                attendMarkTypeColumn.setVisible(true);
                attendanceMarkTable.refresh();
            }
        });

        if (currentTeacher != null) {
            loadCoursesForAttendance();
        }
    }

    /**
     * Загружает курсы преподавателя в комбобокс посещаемости.
     *
     * @implNote Использует {@link CourseDAOImpl#findByTeacherId(Long)}
     */
    private void loadCoursesForAttendance() {
        log.debug("Загрузка курсов для вкладки посещаемости");
        try {
            if (currentTeacher == null) {
                return;
            }

            List<Course> courses = courseDAO.findByTeacherId(currentTeacher.getTeacherId());
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Course c : courses) {
                items.add(c.getCourseName());
            }
            attendanceCourseCombo.setItems(items);
            log.info("Загружено {} курсов для посещаемости", items.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки курсов для посещаемости", e);
        }
    }

    /**
     * Загружает группы выбранного курса в комбобокс посещаемости.
     *
     * @param courseId идентификатор курса
     * @implNote Очищает и заполняет карту {@link #groupIdMap}
     */
    private void loadGroupsForAttendance(Long courseId) {
        log.debug("Загрузка групп для посещаемости, курс ID={}", courseId);
        try {
            List<Group> groups = groupDAO.findByCourseId(courseId);
            ObservableList<String> items = FXCollections.observableArrayList();
            groupIdMap.clear();

            for (Group g : groups) {
                items.add(g.getGroupName());
                groupIdMap.put(g.getGroupName(), g.getGroupId());
            }

            attendanceGroupCombo.setItems(items);
            attendanceGroupCombo.setDisable(items.isEmpty());
            log.info("Загружено {} групп для посещаемости", items.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки групп для посещаемости", e);
        }
    }

    /**
     * Загружает темы выбранного курса в комбобокс посещаемости.
     *
     * @param courseId идентификатор курса
     * @implNote Очищает и заполняет карту {@link #topicIdMap}
     */
    private void loadTopicsForAttendance(Long courseId) {
        log.debug("Загрузка тем для посещаемости, курс ID={}", courseId);
        try {
            List<Topic> topics = topicDAO.findByCourseId(courseId);

            ObservableList<String> items = FXCollections.observableArrayList();
            topicIdMap.clear();

            for (Topic t : topics) {
                items.add(t.getTopicName());
                topicIdMap.put(t.getTopicName(), t.getTopicId());
            }

            attendanceTopicCombo.setItems(items);
            attendanceTopicCombo.setDisable(items.isEmpty());
            log.info("Загружено {} тем для посещаемости", items.size());
        } catch (Exception e) {
            log.error("Ошибка загрузки тем для посещаемости", e);
        }
    }

    /**
     * Загружает занятия выбранной темы в комбобокс посещаемости.
     *
     * @param topicId идентификатор темы
     * @implNote Форматирует строку занятия как "№. Тип (Дата)" для отображения
     */
    private void loadLessonsForAttendance(Long topicId) {
        log.debug("Загрузка занятий для посещаемости, тема ID={}", topicId);
        try {
            List<Lesson> lessons = lessonDAO.findByTopicId(topicId);

            ObservableList<String> items = FXCollections.observableArrayList();
            lessonIdMap.clear();

            for (Lesson l : lessons) {
                String lessonStr = l.getLessonNumber() + ". " + l.getLessonType() + " (" + l.getLessonDate() + ")";
                items.add(lessonStr);
                lessonIdMap.put(lessonStr, l.getLessonId());
            }

            attendanceLessonCombo.setItems(items);
            attendanceLessonCombo.setDisable(items.isEmpty());
            log.info("Загружено {} занятий для посещаемости", items.size());
        } catch (Exception e) {
            log.error("Ошибка загрузки занятий для посещаемости", e);
        }
    }

    /**
     * Загружает студентов выбранной группы в таблицу посещаемости.
     *
     * @param groupId идентификатор группы
     * @implNote Загружает существующие записи о посещаемости для предзаполнения чекбоксов
     */
    private void loadStudentsForAttendanceTable(Long groupId) {
        log.debug("Загрузка студентов для таблицы посещаемости, группа ID={}", groupId);
        try {
            List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            attendanceRecords.clear();

            String selectedLesson = attendanceLessonCombo.getValue();
            Long lessonId = lessonIdMap.get(selectedLesson);

            String topicName = attendanceTopicCombo.getValue();
            String lessonType = "";
            if (selectedLesson != null && selectedLesson.contains(". ") && selectedLesson.contains(" (")) {
                lessonType = selectedLesson.split("\\. ", 2)[1].split(" \\(", 2)[0];
            }

            Map<Long, Boolean> existingAttendance = new HashMap<>();
            if (lessonId != null) {
                try {
                    AttendanceDAOImpl attendanceDAO = new AttendanceDAOImpl();
                    List<Attendance> attendances = attendanceDAO.findByLessonId(lessonId);
                    for (Attendance a : attendances) {
                        existingAttendance.put(a.getStudentId(), a.getIsPresent() != null && a.getIsPresent());
                    }
                    log.debug("Загружено {} существующих записей посещаемости", attendances.size());
                } catch (SQLException e) {
                    log.error("Ошибка загрузки существующей посещаемости", e);
                }
            }

            for (Long id : studentIds) {
                Optional<Student> opt = studentDAO.getById(id);
                if (opt.isPresent()) {
                    Student student = opt.get();
                    boolean isPresent = existingAttendance.getOrDefault(id, false);
                    attendanceRecords.add(new AttendanceRecord(student, isPresent, topicName, lessonType));
                }
            }

            attendanceMarkTable.setItems(attendanceRecords);
            log.info("Таблица посещаемости заполнена: {} записей", attendanceRecords.size());
        } catch (Exception e) {
            log.error("Ошибка заполнения таблицы посещаемости", e);
        }
    }

    /**
     * Настраивает валидацию полей ввода во всём приложении.
     *
     * <p>Выполняет:
     * <ul>
     *   <li>Фильтрацию email: только допустимые символы</li>
     *   <li>Блокировку кнопки сохранения посещаемости при не выбранном занятии</li>
     *   <li>Валидацию дат отчётов: начало не позже конца</li>
     *   <li>Блокировку кнопки генерации отчёта при некорректных датах</li>
     * </ul>
     */
    private void setupValidation() {
        teacherEmailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("[A-Za-z0-9@._-]*")) {
                teacherEmailField.setText(newVal.replaceAll("[^A-Za-z0-9@._-]", ""));
            }
        });

        saveAttendanceButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                attendanceLessonCombo.getValue() == null || attendanceLessonCombo.getValue().isEmpty(),
                        attendanceLessonCombo.valueProperty()
                )
        );

        reportStartDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && reportEndDate.getValue() != null && newVal.isAfter(reportEndDate.getValue())) {
                reportStartDate.setStyle("-fx-border-color: red;");
                showAlert(bundle.getString("alert.error"), bundle.getString("alert.report.error3"));
            } else {
                reportStartDate.setStyle("");
            }
        });

        reportEndDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && reportStartDate.getValue() != null) {
                if (newVal.isBefore(reportStartDate.getValue())) {
                    reportEndDate.setStyle("-fx-border-color: red;");
                    showAlert(bundle.getString("alert.error"), "Дата окончания не может быть раньше даты начала!");
                } else {
                    reportEndDate.setStyle("");
                }
            }
        });

        generateComplexReportButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> {
                    LocalDate start = reportStartDate.getValue();
                    LocalDate end = reportEndDate.getValue();
                    return start == null || end == null || start.isAfter(end);
                }, reportStartDate.valueProperty(), reportEndDate.valueProperty())
        );
    }

    /**
     * Обрабатывает нажатие кнопки "Выход" и возвращает пользователя на экран авторизации.
     *
     * @param event событие нажатия кнопки
     * @implNote Показывает диалог подтверждения, очищает сессию и загружает окно входа
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        log.info("Выход из системы");
        log.info("Пользователь выходит из системы");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(bundle.getString("logout.title"));
        confirm.setHeaderText(null);
        confirm.setContentText(bundle.getString("logout.message"));

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            log.debug("Выход отменен пользователем");
            return;
        }

        log.info("Пользователь подтвердил выход");

        try {
            log.debug("Очистка данных сессии...");
            currentTeacher = null;
            courseIdMap.clear();
            groupIdMap.clear();
            topicIdMap.clear();
            lessonIdMap.clear();
            attendanceRecords.clear();
            log.debug("Сессия очищена");

            ResourceBundle bundle = ResourceBundle.getBundle("ru.alesya0711.laba6.resources.strings");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/login.fxml"),
                    bundle
            );
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(bundle.getString("login.title"));
            stage.setResizable(false);
            stage.centerOnScreen();

            log.info("Закрытие соединения с БД");
            DatabaseConnection.closeConnection();
            log.info("Выход выполнен успешно, возвращение на экран входа");

        } catch (java.util.MissingResourceException e) {
            log.error("Не найден файл ресурсов", e);
            showAlert(bundle.getString("logout.error"), bundle.getString("logout.error.message"));
        } catch (IOException e) {
            log.error("Ошибка загрузки окна входа", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("logout.error.message2") + e.getMessage());
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Сохранить профиль" и обновляет данные преподавателя в БД.
     *
     * @param event событие нажатия кнопки
     * @implNote Разбирает ФИО на части и валидирует минимальную длину
     */
    @FXML
    private void saveProfile(ActionEvent event) {
        log.info("Сохранение профиля преподавателя");

        if (teacherFullNameField.getText().trim().isEmpty()) {
            log.warn("Поле ФИО пустое");
            showAlert(bundle.getString("profil.error"), bundle.getString("profil.error.message"));
            teacherFullNameField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return;
        }

        teacherFullNameField.setStyle("");

        try {
            String fullName = teacherFullNameField.getText().trim();
            String[] parts = fullName.split("\\s+");

            if (parts.length < 2) {
                log.warn("Введено меньше 2 частей имени: {}", parts.length);
                showAlert(bundle.getString("alert.error"), bundle.getString("profil.error.message2"));
                return;
            }

            String lastName = parts[0];
            String firstName = parts[1];
            String middleName = parts.length > 2 ? parts[2] : "";

            log.debug("Разобранное ФИО: фамилия={}, имя={}, отчество={}", lastName, firstName, middleName);

            if (currentTeacher != null) {
                currentTeacher.setLastName(lastName);
                currentTeacher.setFirstName(firstName);
                currentTeacher.setMiddleName(middleName);
                currentTeacher.setEmail(teacherEmailField.getText().trim());

                TeacherDAOImpl teacherDAO = new TeacherDAOImpl();
                teacherDAO.update(currentTeacher);

                log.info("Профиль преподавателя {} успешно обновлён", currentTeacher.getTeacherId());
                showAlert(bundle.getString("alert.yspex"), bundle.getString("profil.yspex.message"));
            }

        } catch (Exception e) {
            log.error("Ошибка сохранения профиля в БД", e);
            showAlert(bundle.getString("alert.error.bd3"), bundle.getString("profil.error.bd") + e.getMessage());
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Сохранить посещаемость" и записывает данные в БД.
     *
     * @param event событие нажатия кнопки
     * @implNote Обновляет существующие записи или создаёт новые, показывает статистику
     */
    @FXML
    private void saveAttendance(ActionEvent event) {
        log.info("Сохранение посещаемости");

        if (attendanceLessonCombo.getValue() == null) {
            log.warn("Занятие не выбрано");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.lesson"));
            return;
        }

        Long lessonId = lessonIdMap.get(attendanceLessonCombo.getValue());
        if (lessonId == null) {
            log.error("Не удалось определить ID занятия");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.error.lesson2"));
            return;
        }

        log.debug("Сохранение посещаемости для занятия ID={}", lessonId);
        log.debug("Всего записей для сохранения: {}", attendanceRecords.size());

        try {
            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);
            AttendanceDAOImpl attendanceDAO = new AttendanceDAOImpl();
            int savedCount = 0;
            int presentCount = 0;
            int absentCount = 0;

            for (AttendanceRecord record : attendanceRecords) {
                boolean isPresent = record.isPresent();
                Student student = record.getStudent();

                List<Attendance> existing = attendanceDAO.findByStudentId(student.getStudentId());
                boolean found = false;

                for (Attendance a : existing) {
                    if (a.getLessonId().equals(lessonId)) {
                        a.setIsPresent(isPresent);
                        attendanceDAO.update(a);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Attendance attendance = new Attendance();
                    attendance.setLessonId(lessonId);
                    attendance.setStudentId(student.getStudentId());
                    attendance.setIsPresent(isPresent);
                    attendance.setComment("");
                    attendanceDAO.add(attendance);
                }

                savedCount++;
                if (isPresent) {
                    presentCount++;
                } else {
                    absentCount++;
                }
            }
            setAuditTeacherId(null);

            log.info("Посещаемость сохранена: всего={}, присутствовали={}, отсутствовали={}",
                    savedCount, presentCount, absentCount);

            showAlert(bundle.getString("alert.yspex"), String.format(
                    bundle.getString("alert.attendanse1")+"\n"+ bundle.getString("alert.attendanse2")+ "%d\n"+bundle.getString("alert.attendanse3")
                            + "%d\n" + bundle.getString("alert.attendanse4")+ "%d",
                    savedCount, presentCount, absentCount));

        } catch (SQLException e) {
            log.error("Ошибка сохранения посещаемости в БД", e);
            showAlert(bundle.getString("alert.alert.error.bd3"), bundle.getString("alert.attendanse.error") + e.getMessage());
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Добавить занятие" из вкладки посещаемости.
     *
     * @param event событие нажатия кнопки
     * @implNote Открывает {@link AddLessonDialogController} с предустановленными курсом и темой
     */
    @FXML
    private void addNewLessonFromAttendance(ActionEvent event) {
        log.info("Добавление нового занятия");

        Long courseId = courseIdMap.get(attendanceCourseCombo.getValue());
        Long topicId = topicIdMap.get(attendanceTopicCombo.getValue());

        if (courseId == null || topicId == null) {
            log.warn("Курс или тема не выбраны");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.lesson.courseandtopic"));
            return;
        }

        log.debug("Открытие диалога добавления занятия (courseId={}, topicId={})", courseId, topicId);

        try {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "ru.alesya0711.laba6.resources.strings",
                    Locale.getDefault()
            );

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dialogs/add-lesson-dialog.fxml"), bundle);
            VBox dialogRoot = loader.load();

            AddLessonDialogController controller = loader.getController();
            controller.setInitialData(currentTeacher.getTeacherId(), courseId, topicId);
            setAuditTeacherId(currentTeacher != null ? currentTeacher.getTeacherId() : null);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(logoutButton.getScene().getWindow());
            dialogStage.setTitle(bundle.getString("attendanse.addLesson"));
            dialogStage.setScene(new Scene(dialogRoot));

            dialogStage.showAndWait();

            setAuditTeacherId(null);
            log.info("Диалог закрыт, обновление списка занятий");

            loadLessonsForAttendance(topicId);

        } catch (IOException e) {
            log.error("Ошибка при открытии диалога", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.dialog") + e.getMessage());
        } finally {
            setAuditTeacherId(null);
        }
    }

    // ВКЛАДКА "ОТЧЕТЫ"

    /**
     * Пытается распарсить дату из текстового поля DatePicker.
     *
     * @param datePicker поле ввода даты
     * @return объект {@link LocalDate} или {@code null}, если парсинг не удался
     * @implNote Пробует форматы: {@code dd.MM.yyyy}, {@code dd/MM/yyyy}, {@code ISO_LOCAL_DATE}
     */
    private LocalDate parseDateFromTextField(DatePicker datePicker) {
        String text = datePicker.getEditor().getText();
        if (text == null || text.isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatter3 = DateTimeFormatter.ISO_LOCAL_DATE;

            try {
                return LocalDate.parse(text, formatter1);
            } catch (Exception e1) {
                try {
                    return LocalDate.parse(text, formatter2);
                } catch (Exception e2) {
                    return LocalDate.parse(text, formatter3);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Генерирует комплексный отчёт по успеваемости и посещаемости за выбранный период.
     *
     * @param event событие нажатия кнопки генерации
     * @implNote Формирует текстовый отчёт с псевдографикой и отображает в {@link TextArea}
     */
    @FXML
    private void generateComplexReport(ActionEvent event) {
        log.info("Генерация комплексного отчета");

        LocalDate startDate = reportStartDate.getValue();
        LocalDate endDate = reportEndDate.getValue();

        if (startDate == null) {
            startDate = parseDateFromTextField(reportStartDate);
        }
        if (endDate == null) {
            endDate = parseDateFromTextField(reportEndDate);
        }

        if (startDate == null || endDate == null) {
            log.warn("Период отчета не выбран (start={}, end={})", startDate, endDate);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.report.error1")+"\n"+bundle.getString("alert.report.error2"));
            return;
        }

        if (startDate.isAfter(endDate)) {
            log.warn("Некорректный период: начало {} позже конца {}", startDate, endDate);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.report.error3"));
            return;
        }

        String courseName = reportCourseCombo.getValue();
        Long courseId = courseIdMap.get(courseName);
        String groupName = reportGroupCombo.getValue();
        Long groupId = groupName != null ? groupIdMap.get(groupName) : null;

        if (courseId == null) {
            log.warn("Курс не выбран");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.report.error4"));
            return;
        }

        log.info("Генерация отчета: курс={} (ID={}), группа={} (ID={}), период={}-{}",
                courseName, courseId, groupName, groupId, startDate, endDate);
        log.debug("Опции отчета: успеваемость={}, посещаемость={}",
                includeGradesCheck.isSelected(), includeAttendanceCheck.isSelected());

        try {
            StringBuilder report = new StringBuilder();
            report.append("═══════════════════════════════════════════════════\n");
            report.append("           КОМПЛЕКСНЫЙ ОТЧЕТ ПО УЧЕБНОМУ КУРСУ\n");
            report.append("═══════════════════════════════════════════════════\n\n");

            // Заголовок
            CourseDAOImpl cDAO = new CourseDAOImpl();
            Optional<Course> courseOpt = cDAO.getById(courseId);
            if (courseOpt.isPresent()) report.append("Курс: ").append(courseOpt.get().getCourseName()).append("\n");

            if (groupId != null) {
                GroupDAOImpl gDAO = new GroupDAOImpl();
                Optional<Group> groupOpt = gDAO.getById(groupId);
                if (groupOpt.isPresent()) report.append("Группа: ").append(groupOpt.get().getGroupName()).append("\n");
            } else {
                report.append("Группа: Все группы\n");
            }
            report.append("Период: ").append(startDate).append(" — ").append(endDate).append("\n");
            report.append("Дата формирования: ").append(LocalDate.now()).append("\n\n");

            List<Long> studentIds = new ArrayList<>();
            if (groupId != null) {
                studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
            }

            StudentDAOImpl sDAO = new StudentDAOImpl();
            TopicDAOImpl tDAO = new TopicDAOImpl();
            List<Topic> topics = tDAO.findByCourseId(courseId);

            // 1. УСПЕВАЕМОСТЬ
            if (includeGradesCheck.isSelected()) {
                report.append("┌────────────────────────────────────────────┐\n");
                report.append("│           1. УСПЕВАЕМОСТЬ СТУДЕНТОВ        │\n");
                report.append("└────────────────────────────────────────────┘\n\n");

                // 1.1 Индивидуальные задания
                report.append("1.1. ИНДИВИДУАЛЬНЫЕ ЗАДАНИЯ:\n");

                List<String[]> rows = new ArrayList<>();
                rows.add(new String[]{"Студент", "Задание", "Оценка", "Статус"});
                report.append("────────────────────────────────────────────────────────\n");

                IndividualAssignmentDAOImpl aDAO = new IndividualAssignmentDAOImpl();
                int totalAssign = 0, gradedAssign = 0;
                double sumGrade = 0;

                for (Long sid : studentIds) {
                    Optional<Student> sOpt = sDAO.getById(sid);
                    if (sOpt.isPresent()) {
                        Student stu = sOpt.get();
                        String stuName = stu.getLastName() + " " + stu.getFirstName();
                        List<IndividualAssignment> assigns = aDAO.findByStudentId(sid);

                        for (IndividualAssignment a : assigns) {
                            boolean inCourse = topics.stream().anyMatch(t -> t.getTopicId().equals(a.getTopicId()));
                            boolean inPeriod = a.getAssignmentDate() != null &&
                                    !a.getAssignmentDate().isBefore(startDate) &&
                                    !a.getAssignmentDate().isAfter(endDate);

                            if (inCourse && inPeriod) {
                                String gradeStr = a.getGrade() != null ? a.getGrade().toString() : "-";
                                String statusStr = a.getStatus() != null ? a.getStatus() : "PENDING";

                                rows.add(new String[]{
                                        truncate(stuName, 28),
                                        truncate(a.getAssignmentName(), 23),
                                        gradeStr,
                                        statusStr
                                });

                                totalAssign++;
                                if (a.getGrade() != null) {
                                    gradedAssign++;
                                    sumGrade += a.getGrade();
                                }
                            }
                        }
                    }
                }

                int[] colWidths = new int[4];
                for (String[] row : rows) {
                    for (int i = 0; i < 4; i++) {
                        colWidths[i] = Math.max(colWidths[i], row[i].length());
                    }
                }

                colWidths[0] += 2;
                colWidths[1] += 2;
                colWidths[2] += 2;

                report.append(String.format("%-" + colWidths[0] + "s %-" + colWidths[1] + "s %-" + colWidths[2] + "s %s\n",
                        rows.get(0)[0], rows.get(0)[1], rows.get(0)[2], rows.get(0)[3]));
                report.append("────────────────────────────────────────────────────────\n");

                for (int i = 1; i < rows.size(); i++) {
                    String[] row = rows.get(i);
                    report.append(String.format("%-" + colWidths[0] + "s %-" + colWidths[1] + "s %-" + colWidths[2] + "s %s\n",
                            row[0], row[1], row[2], row[3]));
                }

                report.append("────────────────────────────────────────────────────────\n");
                if (totalAssign > 0) {
                    report.append(String.format("Всего заданий: %d | Сдано: %d | Средний балл: %.2f\n\n",
                            totalAssign, gradedAssign, gradedAssign > 0 ? sumGrade / gradedAssign : 0));
                } else {
                    report.append("Нет данных за указанный период\n\n");
                }

                // 1.2 Итоговые работы
                report.append("1.2. ИТОГОВЫЕ РАБОТЫ:\n");
                report.append("─────────────────────────────────────────────────────────────────────\n");
                report.append(String.format("%-30s %-10s %-8s %-10s %-8s\n", "Студент", "Билет", "Теория", "Практика", "Средний"));
                report.append("─────────────────────────────────────────────────────────────────────\n");

                FinalWorkDAOImpl fwDAO = new FinalWorkDAOImpl();
                int totalFW = 0;
                double sumFW = 0;

                for (Long sid : studentIds) {
                    Optional<Student> sOpt = sDAO.getById(sid);
                    if (sOpt.isPresent()) {
                        Student stu = sOpt.get();
                        String stuName = stu.getLastName() + " " + stu.getFirstName();
                        List<FinalWork> fws = fwDAO.findByStudentId(sid);

                        for (FinalWork fw : fws) {
                            boolean inCourse = fw.getCourseId().equals(courseId);
                            boolean inPeriod = fw.getExamDate() != null &&
                                    !fw.getExamDate().isBefore(startDate) &&
                                    !fw.getExamDate().isAfter(endDate);

                            if (inCourse && inPeriod) {
                                Double avg = (fw.getTheoryGrade() != null && fw.getPracticeGrade() != null) ?
                                        (fw.getTheoryGrade() + fw.getPracticeGrade()) / 2.0 : null;
                                report.append(String.format("%-30s %-10s %-8s %-10s %-8s\n",
                                        truncate(stuName, 28),
                                        fw.getTicketNumber() != null ? fw.getTicketNumber() : "-",
                                        fw.getTheoryGrade() != null ? fw.getTheoryGrade().toString() : "-",
                                        fw.getPracticeGrade() != null ? fw.getPracticeGrade().toString() : "-",
                                        avg != null ? String.format("%.1f", avg) : "-"));
                                totalFW++;
                                if (avg != null) sumFW += avg;
                            }
                        }
                    }
                }
                report.append("─────────────────────────────────────────────────────────────────────\n");
                if (totalFW > 0) {
                    report.append(String.format("Всего работ: %d | Средний балл: %.2f\n\n", totalFW, sumFW / totalFW));
                } else {
                    report.append("Нет данных за указанный период\n\n");
                }
            }

            // 2. ПОСЕЩАЕМОСТЬ
            if (includeAttendanceCheck.isSelected()) {
                report.append("┌─────────────────────────────────────────────┐\n");
                report.append("│           2. ПОСЕЩАЕМОСТЬ ЗАНЯТИЙ           │\n");
                report.append("└─────────────────────────────────────────────┘\n\n");

                AttendanceDAOImpl attDAO = new AttendanceDAOImpl();
                LessonDAOImpl lDAO = new LessonDAOImpl();

                report.append(" СТАТИСТИКА ПО ЗАНЯТИЯМ:\n");
                report.append("─────────────────────────────────────────────────────────────────────\n");
                report.append(String.format("%-25s %-15s %-10s %-10s %-10s\n", "Дата", "Тип", "Всего", "Присут.", "%"));
                report.append("─────────────────────────────────────────────────────────────────────\n");

                int totalLessons = 0, totalPresent = 0, totalExpected = 0;

                for (Topic topic : topics) {
                    List<Lesson> lessons = lDAO.findByTopicId(topic.getTopicId());
                    for (Lesson lesson : lessons) {
                        boolean inPeriod = lesson.getLessonDate() != null &&
                                !lesson.getLessonDate().isBefore(startDate) &&
                                !lesson.getLessonDate().isAfter(endDate);

                        if (inPeriod) {
                            List<Attendance> atts = attDAO.findByLessonId(lesson.getLessonId());
                            int present = (int) atts.stream().filter(a -> Boolean.TRUE.equals(a.getIsPresent())).count();
                            int expected = groupId != null ? studentIds.size() : atts.size();
                            double pct = expected > 0 ? (present * 100.0 / expected) : 0;

                            report.append(String.format("%-25s %-15s %-10d %-10d %-10.1f\n",
                                    lesson.getLessonDate(), lesson.getLessonType(), expected, present, pct));

                            totalLessons++; totalPresent += present; totalExpected += expected;
                        }
                    }
                }
                report.append("─────────────────────────────────────────────────────────────────────\n");
                if (totalLessons > 0) {
                    report.append(String.format("Всего занятий: %d | Общая посещаемость: %.1f%%\n\n",
                            totalLessons, totalExpected > 0 ? (totalPresent * 100.0 / totalExpected) : 0));
                } else {
                    report.append("Нет данных за указанный период\n\n");
                }
            }

            report.append("═══════════════════════════════════════════════════\n");
            report.append("                    КОНЕЦ ОТЧЕТА\n");
            report.append("═══════════════════════════════════════════════════\n");

            complexReportArea.setText(report.toString());
            log.info("Отчет успешно сгенерирован");
            showAlert(bundle.getString("alert.yspex"), bundle.getString("alert.report.yspex"));

        } catch (Exception e) {
            log.error("Ошибка генерации отчета", e);
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.report.error5") + e.getMessage());
        }
    }

    /**
     * Обрезает строку до указанной длины с добавлением многоточия.
     *
     * @param text исходная строка
     * @param maxLength максимальная длина результата
     * @return обрезанная строка или исходная, если длина допустима
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Экспортирует сгенерированный отчёт в файл PDF.
     *
     * @param event событие нажатия кнопки экспорта
     * @implNote Использует библиотеку iText 7, заменяет псевдографику на простые символы
     */
    @FXML
    private void exportToPdf(ActionEvent event) {
        log.info("Экспорт отчета в PDF");

        if (complexReportArea.getText().isEmpty()) {
            log.warn("Попытка экспорта пустого отчета");
            showAlert(bundle.getString("alert.error"), bundle.getString("report.error2"));
            return;
        }

        log.debug("Открытие FileChooser для сохранения PDF");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("report.save"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Документы PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("otchet_" + LocalDate.now() + ".pdf");

        Stage stage = (Stage) complexReportArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            log.info("Экспорт отчета в файл: {}", file.getAbsolutePath());
            try {
                com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(file);
                com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDocument);

                String fontPath = System.getenv("WINDIR") + "\\Fonts\\arial.ttf";

                com.itextpdf.kernel.font.PdfFont font = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                        fontPath,
                        "Identity-H"
                );

                document.setFont(font);
                document.setFontSize(10);

                String[] lines = complexReportArea.getText().split("\n");

                for (String line : lines) {
                    if (line.trim().isEmpty()) {
                        document.add(new com.itextpdf.layout.element.Paragraph(""));
                        continue;
                    }

                    String cleanLine = line.replace("═", "=").replace("─", "-").replace("│", "|")
                            .replace("┌", "+").replace("└", "+").replace("┘", "+").replace("┐", "+");

                    com.itextpdf.layout.element.Paragraph paragraph = new com.itextpdf.layout.element.Paragraph(cleanLine);
                    paragraph.setFixedLeading(12);
                    document.add(paragraph);
                }

                document.close();
                log.info("Отчет успешно сохранен в PDF");
                showAlert(bundle.getString("alert.yspex"), bundle.getString("report.yspex")+"\n" + file.getAbsolutePath());

            } catch (Exception e) {
                log.error("Ошибка создания PDF файла", e);
                showAlert(bundle.getString("alert.error"), bundle.getString("report.error") + e.getMessage());
            }
        } else {
            log.debug("Сохранение PDF отменено пользователем");
        }
    }

    /**
     * Показывает информационное диалоговое окно.
     *
     * @param title заголовок окна
     * @param message текст сообщения
     * @implNote Использует тип алерта {@link Alert.AlertType#INFORMATION}
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Загружает курсы преподавателя в комбобокс вкладки "Отчеты".
     *
     * @implNote Использует {@link CourseDAOImpl#findByTeacherId(Long)}
     */
    private void loadCoursesForReports() {
        log.debug("Загрузка курсов для вкладки отчетов");
        try {
            if (currentTeacher == null) return;

            List<Course> courses = courseDAO.findByTeacherId(currentTeacher.getTeacherId());
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Course c : courses) {
                items.add(c.getCourseName());
            }

            reportCourseCombo.setItems(items);
            log.info("Загружено {} курсов для отчетов", items.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки курсов для отчетов", e);
        }
    }

    /**
     * Загружает группы выбранного курса в комбобокс отчетов.
     *
     * @param courseId идентификатор курса
     * @implNote Очищает и заполняет карту {@link #groupIdMap}
     */
    private void loadGroupsForReportCourse(Long courseId) {
        log.debug("Загрузка групп для отчета, курс ID={}", courseId);
        try {
            List<Group> groups = groupDAO.findByCourseId(courseId);
            ObservableList<String> items = FXCollections.observableArrayList();

            groupIdMap.clear();

            for (Group g : groups) {
                String groupName = g.getGroupName();
                items.add(groupName);
                groupIdMap.put(groupName, g.getGroupId());
            }

            reportGroupCombo.setItems(items);
            reportGroupCombo.setDisable(items.isEmpty());
            log.info("Загружено {} групп для отчета", items.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки групп для отчета", e);
        }
    }

    /**
     * Настраивает валидацию и слушатели для вкладки "Отчеты".
     */
    private void setupReportsTab() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty())
                        ? LocalDate.parse(string, dateFormatter)
                        : null;
            }
        };

        reportStartDate.setConverter(converter);
        reportEndDate.setConverter(converter);

        reportCourseCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                Long courseId = courseIdMap.get(newVal);
                if (courseId != null) {
                    loadGroupsForReportCourse(courseId);
                }
            } else {
                reportGroupCombo.setItems(FXCollections.observableArrayList());
            }
        });

        if (currentTeacher != null) {
            loadCoursesForReports();
        }
    }

    /**
     * Устанавливает или сбрасывает контекст аудита для текущего преподавателя.
     *
     * @param teacherId идентификатор преподавателя или {@code null} для сброса
     * @implNote Выполняет SQL-команды {@code SET/RESET app.current_teacher_id}
     *           для отслеживания действий в базе данных
     */
    private void setAuditTeacherId(Long teacherId) {
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            if (teacherId != null) {
                stmt.execute("SET app.current_teacher_id = " + teacherId);
                log.debug("Контекст аудита установлен для teacher_id={}", teacherId);
            } else {
                stmt.execute("RESET app.current_teacher_id");
                log.debug("Контекст аудита сброшен");
            }
        } catch (SQLException e) {
            log.error("Не удалось установить контекст аудита", e);
        }
    }
}