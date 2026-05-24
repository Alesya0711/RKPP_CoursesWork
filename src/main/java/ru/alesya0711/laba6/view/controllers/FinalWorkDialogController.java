package ru.alesya0711.laba6.view.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ru.alesya0711.laba6.dao.impl.FinalWorkDAOImpl;
import ru.alesya0711.laba6.dao.impl.StudentDAOImpl;
import ru.alesya0711.laba6.dao.impl.StudentsGroupDAOImpl;
import ru.alesya0711.laba6.model.FinalWork;
import ru.alesya0711.laba6.model.Student;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Контроллер диалогового окна добавления/редактирования итоговой работы.
 *
 * <p>Управляет формой создания или изменения записи об итоговой работе (экзамене):
 * выбор студента, ввод номера билета, даты экзамена, оценок по теории и практике.
 * Обеспечивает валидацию ввода и проверку на дублирование активных работ.
 */
@Slf4j
public class FinalWorkDialogController {

    @FXML private Label dialogTitleLabel;
    @FXML private ComboBox<Student> studentComboBox;
    @FXML private TextField ticketField;
    @FXML private DatePicker examDatePicker;
    @FXML private Spinner<Integer> theoryGradeSpinner;
    @FXML private Spinner<Integer> practiceGradeSpinner;
    @FXML private Label averageGradeLabel;
    @FXML private Button saveButton;

    private StudentDAOImpl studentDAO;
    private StudentsGroupDAOImpl studentsGroupDAO;
    private boolean editMode = false;
    private Long finalWorkId;
    private Long currentCourseId;

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
        log.debug("Инициализация FinalWorkDialogController");

        try {
            studentDAO = new StudentDAOImpl();
            studentsGroupDAO = new StudentsGroupDAOImpl();
            log.debug("DAO объекты успешно созданы");
        } catch (SQLException e) {
            log.error("Ошибка создания DAO объектов", e);
        }

        // Настройка диапазонов оценок (2-5)
        SpinnerValueFactory<Integer> theoryFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 0);
        theoryGradeSpinner.setValueFactory(theoryFactory);

        SpinnerValueFactory<Integer> practiceFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 5, 0);
        practiceGradeSpinner.setValueFactory(practiceFactory);

        examDatePicker.setValue(LocalDate.now());

        // Фильтр ввода для номера билета: кириллица, латиница, цифры, дефис
        ticketField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (!newVal.matches("[а-яА-ЯёЁa-zA-Z0-9-]*")) {
                    ticketField.setText(oldVal);
                    ticketField.positionCaret(oldVal.length());
                }
            }
            validateForm();
        });

        // Фильтр ввода для оценок: только цифры
        theoryGradeSpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            if (!newVal.matches("\\d+")) {
                theoryGradeSpinner.getEditor().setText(oldVal);
                theoryGradeSpinner.getEditor().positionCaret(oldVal.length());
            }
        });

        practiceGradeSpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) return;
            if (!newVal.matches("\\d+")) {
                practiceGradeSpinner.getEditor().setText(oldVal);
                practiceGradeSpinner.getEditor().positionCaret(oldVal.length());
            }
        });

        // Автоматический пересчет среднего балла
        theoryGradeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateAverage();
            validateForm();
        });

        practiceGradeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            calculateAverage();
            validateForm();
        });

        // Настройка отображения ФИО студентов в ComboBox
        setupStudentComboBoxDisplay();

        validateForm();
        log.debug("Интерфейс диалога настроен");
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
                    String fullName = student.getLastName() + " " + student.getFirstName();
                    if (student.getMiddleName() != null) {
                        fullName += " " + student.getMiddleName();
                    }
                    setText(fullName);
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
                    String fullName = student.getLastName() + " " + student.getFirstName();
                    if (student.getMiddleName() != null) {
                        fullName += " " + student.getMiddleName();
                    }
                    setText(fullName);
                }
            }
        });
    }

    /**
     * Устанавливает идентификатор текущего курса.
     * @param courseId ID курса
     */
    public void setCurrentCourseId(Long courseId) {
        this.currentCourseId = courseId;
        log.debug("Установлен текущий курс ID: {}", courseId);
    }

    /**
     * Обрабатывает сохранение итоговой работы (добавление или обновление).
     */
    @FXML
    private void saveFinalWork(ActionEvent event) {
        log.info("Сохранение итоговой работы ");

        // Валидация полей
        if (studentComboBox.getValue() == null) {
            log.warn("Студент не выбран");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.student2"));
            return;
        }

        if (ticketField.getText().trim().length() < 3) {
            log.warn("Некорректный номер билета: {}", ticketField.getText());
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.fw.ticket"));
            return;
        }

        if (examDatePicker.getValue() == null) {
            log.warn("Дата экзамена не выбрана");
            showAlert(bundle.getString("alert.error"), bundle.getString("alert.fw.data"));
            return;
        }

        Student student = studentComboBox.getValue();
        log.debug("Выбран студент: ID: {}", student.getStudentId());

        // Проверка на дублирование активной работы (только при добавлении)
        if (!editMode && currentCourseId != null) {
            if (hasActiveFinalWork(student.getStudentId(), currentCourseId)) {
                log.warn("Попытка добавить вторую активную работу для студента ID={} по курсу ID={}",
                        student.getStudentId(), currentCourseId);
                showAlert(bundle.getString("alert.error"),
                        bundle.getString("alert.fw.error1") + student.getLastName() +bundle.getString("alert.fw.error2") + "\n" +
                                bundle.getString("alert.fw.error3"));
                return;
            }
        }

        try {
            FinalWorkDAOImpl finalWorkDAO = new FinalWorkDAOImpl();

            Integer theoryGrade = theoryGradeSpinner.getValue();
            Integer practiceGrade = practiceGradeSpinner.getValue();

            if (editMode && finalWorkId != null) {
                log.info("Режим редактирования. ID работы: {}", finalWorkId);

                FinalWork finalWork = new FinalWork();
                finalWork.setFinalId(finalWorkId);
                finalWork.setCourseId(currentCourseId);
                finalWork.setStudentId(student.getStudentId());
                finalWork.setExamDate(examDatePicker.getValue());
                finalWork.setTicketNumber(ticketField.getText().trim());
                finalWork.setTheoryGrade(theoryGrade);
                finalWork.setPracticeGrade(practiceGrade);

                finalWorkDAO.update(finalWork);
                log.info("Итоговая работа ID={} успешно обновлена", finalWorkId);

            } else {
                // Режим добавления
                log.info("Режим добавления новой работы");

                FinalWork finalWork = new FinalWork();
                finalWork.setCourseId(currentCourseId);
                finalWork.setStudentId(student.getStudentId());
                finalWork.setExamDate(examDatePicker.getValue());
                finalWork.setTicketNumber(ticketField.getText().trim());
                finalWork.setTheoryGrade(theoryGrade);
                finalWork.setPracticeGrade(practiceGrade);

                finalWorkDAO.add(finalWork);
                log.info("Новая итоговая работа успешно добавлена");
            }

            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
            log.debug("Диалоговое окно закрыто");

        } catch (SQLException e) {
            log.error("Ошибка БД при сохранении итоговой работы", e);
            showAlert(bundle.getString("alert.error.bd"), bundle.getString("alert.fw.error.save") + e.getMessage());
        }
    }

    /**
     * Проверяет наличие активной итоговой работы у студента по указанному курсу.
     * @param studentId ID студента
     * @param courseId ID курса
     * @return true если активная работа найдена
     */
    public boolean hasActiveFinalWork(Long studentId, Long courseId) {
        log.debug("Проверка наличия активной работы для студента ID={} по курсу ID={}", studentId, courseId);
        try {
            FinalWorkDAOImpl finalWorkDAO = new FinalWorkDAOImpl();
            List<FinalWork> activeWorks = finalWorkDAO.findByStudentId(studentId);

            for (FinalWork fw : activeWorks) {
                if (fw.getCourseId().equals(courseId)) {
                    log.debug("Найдена активная работа ID={}", fw.getFinalId());
                    return true;
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка проверки активных работ", e);
        }
        return false;
    }

    /**
     * Рассчитывает и отображает средний балл.
     */
    private void calculateAverage() {
        Integer theory = theoryGradeSpinner.getValue();
        Integer practice = practiceGradeSpinner.getValue();

        if (theory != null && practice != null && theory > 0 && practice > 0) {
            double average = (theory + practice) / 2.0;
            averageGradeLabel.setText(String.format("%.1f", average));
        } else {
            averageGradeLabel.setText("-");
        }
    }

    /**
     * Валидирует форму и управляет состоянием кнопки сохранения.
     */
    private void validateForm() {
        boolean isValid = studentComboBox.getValue() != null &&
                ticketField.getText().trim().length() >= 3 &&
                examDatePicker.getValue() != null &&
                theoryGradeSpinner.getValue() != null &&
                theoryGradeSpinner.getValue() > 0 &&
                practiceGradeSpinner.getValue() != null &&
                practiceGradeSpinner.getValue() > 0;

        saveButton.setDisable(!isValid);
    }

    /**
     * Устанавливает режим редактирования.
     * @param edit true для режима редактирования
     * @param id ID редактируемой работы
     */
    public void setEditMode(boolean edit, Long id) {
        ResourceBundle bundle = ResourceBundle.getBundle(
                "ru.alesya0711.laba6.resources.strings",
                Locale.getDefault()
        );
        this.editMode = edit;
        this.finalWorkId = id;
        if (edit) {
            dialogTitleLabel.setText(bundle.getString("assignment.dialog.final.edit"));
            log.debug("Включен режим редактирования для работы ID={}", id);
        } else {
            dialogTitleLabel.setText(bundle.getString("assignment.dialog.final.add"));
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
            log.info("Загружено {} студентов в ComboBox", studentsToLoad.size());

        } catch (Exception e) {
            log.error("Ошибка загрузки списка студентов", e);
        }
    }

    /**
     * Заполняет поля формы данными существующей итоговой работы.
     * @param finalWork объект работы
     * @param student объект студента
     */
    public void fillFinalWorkData(FinalWork finalWork, Student student) {
        log.debug("Заполнение формы данными работы ID={}", finalWork.getFinalId());

        if (student != null) {
            studentComboBox.setValue(student);
        }

        if (finalWork.getTicketNumber() != null) {
            ticketField.setText(finalWork.getTicketNumber());
        }

        if (finalWork.getExamDate() != null) {
            examDatePicker.setValue(finalWork.getExamDate());
        } else {
            examDatePicker.setValue(LocalDate.now());
        }

        if (finalWork.getTheoryGrade() != null) {
            theoryGradeSpinner.getValueFactory().setValue(finalWork.getTheoryGrade());
        } else {
            theoryGradeSpinner.getValueFactory().setValue(0);
        }

        if (finalWork.getPracticeGrade() != null) {
            practiceGradeSpinner.getValueFactory().setValue(finalWork.getPracticeGrade());
        } else {
            practiceGradeSpinner.getValueFactory().setValue(0);
        }

        calculateAverage();
        log.debug("Форма заполнена данными");
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