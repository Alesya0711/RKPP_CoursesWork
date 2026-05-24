package ru.alesya0711.laba6.view.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.alesya0711.laba6.dao.impl.IndividualAssignmentDAOImpl;
import ru.alesya0711.laba6.dao.impl.StudentDAOImpl;
import ru.alesya0711.laba6.dao.impl.StudentsGroupDAOImpl;
import ru.alesya0711.laba6.model.IndividualAssignment;
import ru.alesya0711.laba6.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


/**
 * Контроллер диалогового окна добавления/редактирования индивидуального задания.
 *
 * <p>Управляет формой создания или изменения задания для студента: выбор студента,
 * ввод названия, даты выдачи, оценки (0-100) и статуса выполнения.
 * Обеспечивает валидацию ввода и фильтрацию некорректных данных.
 */
@Slf4j
public class AssignmentDialogController {

    @FXML private Label dialogTitleLabel;
    @FXML private ComboBox<Student> studentComboBox;
    @FXML private TextField assignmentNameField;
    @FXML private DatePicker assignmentDatePicker;
    @FXML private Spinner<Integer> gradeSpinner;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button saveButton;

    private StudentDAOImpl studentDAO;
    private boolean editMode = false;
    private Long assignmentId;
    private StudentsGroupDAOImpl studentsGroupDAO;
    private Long currentTopicId;

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
        log.debug("Инициализация AssignmentDialogController");

        try {
            studentDAO = new StudentDAOImpl();
            studentsGroupDAO = new StudentsGroupDAOImpl();
            log.debug("DAO объекты успешно созданы");
        } catch (SQLException e) {
            log.error("Ошибка создания DAO объектов", e);
        }

        // Настройка диапазона оценок (0-100)
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        gradeSpinner.setValueFactory(valueFactory);

        // Фильтр ввода для оценки: только цифры
        gradeSpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                return;
            }
            if (!newVal.matches("\\d+")) {
                gradeSpinner.getEditor().setText(oldVal);
                gradeSpinner.getEditor().positionCaret(oldVal.length());
            }
        });

        // Заполнение статусов
        statusComboBox.setItems(FXCollections.observableArrayList(
                "PENDING",      // Не начато
                "IN_PROGRESS",  // В процессе
                "SUBMITTED",    // Сдано
                "GRADED",       // Оценено
                "OVERDUE"       // Просрочено
        ));
        statusComboBox.setValue("PENDING");

        // Ограничение длины названия задания (макс. 100 символов)
        assignmentNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 100) {
                assignmentNameField.setText(oldVal);
            }
        });

        // Блокировка кнопки сохранения при некорректных данных
        saveButton.disableProperty().bind(
                assignmentNameField.textProperty().isEqualTo("")
                        .or(assignmentNameField.textProperty().length().lessThan(3))
                        .or(studentComboBox.valueProperty().isNull())
        );

        log.debug("Интерфейс диалога настроен");
    }

    /**
     * Устанавливает идентификатор текущей темы.
     * @param topicId ID темы
     */
    public void setCurrentTopicId(Long topicId) {
        this.currentTopicId = topicId;
        log.debug("Установлена текущая тема ID: {}", topicId);
    }

    /**
     * Устанавливает режим редактирования.
     * @param edit true для режима редактирования
     * @param id ID редактируемого задания
     */
    public void setEditMode(boolean edit, Long id) {
        this.editMode = edit;
        this.assignmentId = id;

        if (edit) {
            String text = bundle.getString("assignment.dialog.text.edit");
            dialogTitleLabel.setText(text);
            log.debug("Включен режим редактирования для задания ID={}", id);
        } else {
            String text = bundle.getString("assignment.dialog.text.add");
            dialogTitleLabel.setText(text);
            log.debug("Включен режим добавления");
        }
    }

    /**
     * Загружает список студентов из указанной группы в ComboBox.
     * @param groupId ID группы
     */
    public void loadStudents(Long groupId) {
        log.debug("Загрузка студентов для группы ID={}", groupId);
        try {
            List<Student> studentsToLoad;

            if (groupId != null) {
                List<Long> studentIds = studentsGroupDAO.getStudentIdsByGroupId(groupId);
                log.debug("Найдено {} студентов в группе", studentIds.size());

                studentsToLoad = new ArrayList<>();
                for (Long id : studentIds) {
                    Optional<Student> opt = studentDAO.getById(id);
                    opt.ifPresent(studentsToLoad::add);
                }
            } else {
                studentsToLoad = studentDAO.getAll();
                log.debug("Загружены все студенты ({})", studentsToLoad.size());
            }

            studentComboBox.setItems(FXCollections.observableArrayList(studentsToLoad));

            // Настройка отображения ФИО студентов
            setupStudentComboBoxDisplay();

            log.info("Загружено {} студентов в ComboBox", studentsToLoad.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки списка студентов", e);
        }
    }

    /**
     * Настраивает отображение ФИО студентов в выпадающем списке и заголовке ComboBox.
     */
    private void setupStudentComboBoxDisplay() {
        // Фабрика для элементов выпадающего списка
        studentComboBox.setCellFactory(param -> new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    String fio = student.getLastName() + " " + student.getFirstName();
                    if (student.getMiddleName() != null) {
                        fio += " " + student.getMiddleName();
                    }
                    setText(fio);
                }
            }
        });

        // Фабрика для кнопки (выбранного элемента)
        studentComboBox.setButtonCell(new ListCell<Student>() {
            @Override
            protected void updateItem(Student student, boolean empty) {
                super.updateItem(student, empty);
                if (empty || student == null) {
                    setText(null);
                } else {
                    String fio = student.getLastName() + " " + student.getFirstName();
                    if (student.getMiddleName() != null) {
                        fio += " " + student.getMiddleName();
                    }
                    setText(fio);
                }
            }
        });
    }

    /**
     * Заполняет поля формы данными существующего задания.
     * @param assignment объект задания
     * @param student объект студента
     */
    public void fillAssignmentData(IndividualAssignment assignment, Student student) {
        log.debug("Заполнение формы данными задания ID={}", assignment.getAssignmentId());

        if (student != null) {
            studentComboBox.setValue(student);
        }

        if (assignment.getAssignmentName() != null) {
            assignmentNameField.setText(assignment.getAssignmentName());
        }

        if (assignment.getAssignmentDate() != null) {
            assignmentDatePicker.setValue(assignment.getAssignmentDate());
        } else {
            assignmentDatePicker.setValue(LocalDate.now());
        }

        if (assignment.getGrade() != null) {
            gradeSpinner.getValueFactory().setValue(assignment.getGrade());
        } else {
            gradeSpinner.getValueFactory().setValue(0);
        }

        if (assignment.getStatus() != null) {
            statusComboBox.setValue(assignment.getStatus());
        } else {
            statusComboBox.setValue("PENDING");
        }

        log.debug("Форма заполнена данными");
    }

    /**
     * Обрабатывает сохранение индивидуального задания (добавление или обновление).
     */
    @FXML
    private void saveAssignment(ActionEvent event) {
        log.info("Сохранение индивидуального задания");

        // Валидация полей
        if (studentComboBox.getValue() == null) {
            log.warn("Студент не выбран");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.student2"));
            return;
        }

        if (assignmentNameField.getText().trim().isEmpty()) {
            log.warn("Название задания пустое");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.nameAssignment"));
            return;
        }

        if (assignmentDatePicker.getValue() == null) {
            log.warn("Дата выдачи не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.data"));
            return;
        }

        Integer grade = gradeSpinner.getValue();
        if (grade == null) {
            log.warn("Оценка не установлена");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.gradeAssignment"));
            return;
        }

        try {
            if (grade < 0 || grade > 100) {
                log.warn("Некорректный диапазон оценки: {}", grade);
                showAlert(bundle.getString("alert.error"), bundle.getString("alert.gradeDiapozon"));
                return;
            }

            Student student = studentComboBox.getValue();
            String assignmentName = assignmentNameField.getText().trim();
            LocalDate assignmentDate = assignmentDatePicker.getValue();
            String status = statusComboBox.getValue();

            log.debug("Подготовка данных: студент={}, задание={}, дата={}, оценка={}, статус={}",
                    student.getStudentId(), assignmentName, assignmentDate, grade, status);

            IndividualAssignmentDAOImpl assignmentDAO = new IndividualAssignmentDAOImpl();

            if (editMode && assignmentId != null) {
                // Режим редактирования
                log.info("Режим редактирования. ID задания: {}", assignmentId);

                IndividualAssignment assignment = new IndividualAssignment();
                assignment.setAssignmentId(assignmentId);
                assignment.setTopicId(currentTopicId);
                assignment.setStudentId(student.getStudentId());
                assignment.setAssignmentName(assignmentName);
                assignment.setAssignmentDate(assignmentDate);
                assignment.setGrade(grade);
                assignment.setStatus(status);
                assignment.setSubmissionDate(LocalDate.now());

                assignmentDAO.update(assignment);
                log.info("Задание ID={} успешно обновлено", assignmentId);

            } else {
                // Режим добавления
                log.info("Режим добавления нового задания");

                IndividualAssignment assignment = new IndividualAssignment();
                assignment.setTopicId(currentTopicId);
                assignment.setStudentId(student.getStudentId());
                assignment.setAssignmentName(assignmentName);
                assignment.setAssignmentDate(assignmentDate);
                assignment.setGrade(grade);
                assignment.setStatus(status);
                assignment.setSubmissionDate(null);

                Long newId = assignmentDAO.add(assignment);
                log.info("Новое задание успешно добавлено (ID: {})", newId);
            }

            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
            log.debug("Диалоговое окно закрыто");

        } catch (Exception e) {
            log.error("Ошибка БД при сохранении задания", e);
            showAlert(bundle.getString("alert.error.bd"), bundle.getString("alert.error.assigment.dialog") + e.getMessage());
        }
    }

    /**
     * Обрабатывает отмену действия и закрытие окна.
     */
    @FXML
    private void cancel(ActionEvent event) {
        log.debug("Отмена действия, закрытие окна");
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Показывает предупреждающее диалоговое окно.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}