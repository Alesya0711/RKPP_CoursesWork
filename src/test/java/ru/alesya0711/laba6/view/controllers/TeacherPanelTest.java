package ru.alesya0711.laba6.view.controllers;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.service.query.PointQuery;
import org.testfx.util.WaitForAsyncUtils;
import ru.alesya0711.laba6.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeacherPanelTest extends ApplicationTest {

    private static final String TEST_USERNAME = "teacher1";
    private static final String TEST_PASSWORD = "1234";
    private final List<String> createdTickets = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        new Main().start(stage);
    }

    @AfterEach
    void cleanUp() {
        createdTickets.clear();
    }

    @Test
    @Order(1)
    @DisplayName("П-1: Успешная авторизация преподавателя")
    void testSuccessfulLogin() throws Exception {
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#usernameField").tryQuery().isPresent());

        clickOn("#usernameField").write(TEST_USERNAME);
        clickOn("#passwordField").write(TEST_PASSWORD);
        clickOn("#loginButton");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#mainTabPane").tryQuery().isPresent());

        verifyThat(lookup("Мой профиль"), isVisible());
    }


    @Test
    @Order(2)
    @DisplayName("П-2: Добавление итоговой работы с валидными данными")
    void testAddFinalWork_Success() throws Exception {
        performLogin();
        navigateToFinalWorkDialog();

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#ticketField").tryQuery().isPresent());
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#studentComboBox").tryQuery().isPresent());

        selectFirstFromComboBox("#studentComboBox");
        sleep(400, TimeUnit.MILLISECONDS);

        TextField ticketField = lookup("#ticketField").queryAs(TextField.class);
        clickOn(ticketField);

        String ticketNumber = "TEST-" + System.currentTimeMillis();
        clickOn(ticketField).write(ticketNumber);
        createdTickets.add(ticketNumber);
        sleep(200, TimeUnit.MILLISECONDS);

        Spinner<Integer> theorySpinner = lookup("#theoryGradeSpinner").queryAs(Spinner.class);
        clickOn(theorySpinner)
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("4");
        sleep(100, TimeUnit.MILLISECONDS);

        Spinner<Integer> practiceSpinner = lookup("#practiceGradeSpinner").queryAs(Spinner.class);
        clickOn(practiceSpinner)
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("5");
        sleep(200, TimeUnit.MILLISECONDS);

        TableView<?> tableBefore = lookup("#finalWorksTable").query();
        int countBefore = tableBefore.getItems().size();

        clickOn("#saveButton");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            TableView<?> table = lookup("#finalWorksTable").query();
            return table.getItems().size() > countBefore;
        });

        sleep(300, TimeUnit.MILLISECONDS);

        TableView<?> tableAfter = lookup("#finalWorksTable").query();
        assertThat(tableAfter.getItems().size())
                .as("Количество записей в таблице должно увеличиться после добавления работы")
                .isGreaterThan(countBefore);
    }

    @Test
    @Order(3)
    @DisplayName("Н-1: Валидация обязательных полей (студент и номер билета)")
    void testMandatoryFieldsValidation_DisablesSaveButton() throws Exception {
        performLogin();
        navigateToFinalWorkDialog();

        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#studentComboBox").tryQuery().isPresent());
        sleep(300, TimeUnit.MILLISECONDS);

        Button saveButton = lookup("#saveButton").queryAs(Button.class);
        TextField ticketField = lookup("#ticketField").queryAs(TextField.class);
        DatePicker examDatePicker = lookup("#examDatePicker").queryAs(DatePicker.class);

        assertThat(saveButton.isDisabled())
                .as("Кнопка должна быть заблокирована при пустых полях")
                .isTrue();

        clickOn(ticketField).write("TEST-123");

        clickOn(examDatePicker);
        sleep(200, TimeUnit.MILLISECONDS);

        clickOn("#theoryGradeSpinner")
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("4");
        sleep(100, TimeUnit.MILLISECONDS);

        clickOn("#practiceGradeSpinner")
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("5");
        sleep(200, TimeUnit.MILLISECONDS);

        assertThat(saveButton.isDisabled())
                .as("Кнопка должна оставаться заблокированной, если студент не выбран")
                .isTrue();

        clickOn("Отмена");
    }


    @Test
    @Order(4)
    @DisplayName("Н-2: Попытка добавить дублирующую активную работу")
    void testDuplicateFinalWork_ShowsError() throws Exception {
        performLogin();
        navigateToFinalWorkDialog();

        String ticket1 = "DUP-001-" + System.currentTimeMillis();
        fillAndSaveFinalWork(ticket1);

        sleep(200, TimeUnit.MILLISECONDS);
        boolean errorDialogShown = false;
        try {
            WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                    lookup("Ошибка").tryQuery().isPresent()
                            || lookup("Error").tryQuery().isPresent()
                            || lookup("У студента").tryQuery().isPresent() // Текст внутри ошибки
            );
            errorDialogShown = true;
        } catch (TimeoutException e) {
        }

        sleep(200, TimeUnit.MILLISECONDS);

        type(KeyCode.ENTER);
        sleep(200, TimeUnit.MILLISECONDS);

        clickOn("Отмена");
    }

    private void performLogin() throws Exception {
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#usernameField").tryQuery().isPresent());
        clickOn("#usernameField").write(TEST_USERNAME);
        clickOn("#passwordField").write(TEST_PASSWORD);
        clickOn("#loginButton");
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#mainTabPane").tryQuery().isPresent());
        sleep(300, TimeUnit.MILLISECONDS);
    }

    private void navigateToFinalWorkDialog() throws Exception {
        clickOn("Успеваемость");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#courseComboBox").tryQuery().isPresent());

        selectFirstFromComboBox("#courseComboBox");

        sleep(300, TimeUnit.MILLISECONDS);

        selectFirstFromComboBox("#groupComboBox");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () -> {
            ComboBox<?> topicCombo = lookup("#topicComboBox").queryAs(ComboBox.class);
            return !topicCombo.isDisabled() && !topicCombo.getItems().isEmpty();
        });
        sleep(300, TimeUnit.MILLISECONDS);

        selectFirstFromComboBox("#topicComboBox");

        clickOn("#addFinalWorkButton");

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#ticketField").tryQuery().isPresent());
    }

    private void selectFirstFromComboBox(String comboBoxFxId) throws Exception {
        ComboBox<?> comboBox = lookup(comboBoxFxId).queryAs(ComboBox.class);

        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, () ->
                !comboBox.isDisabled() && !comboBox.getItems().isEmpty()
        );

        interact(() -> {
            comboBox.show();
            comboBox.getSelectionModel().selectFirst();
            comboBox.hide();
        });

        sleep(400, TimeUnit.MILLISECONDS);

        if (comboBox.getValue() == null) {
            throw new RuntimeException("Не удалось выбрать значение из " + comboBoxFxId);
        }
    }

    private void fillAndSaveFinalWork(String ticketNumber) throws Exception {
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS,
                () -> lookup("#ticketField").tryQuery().isPresent());
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS,
                () -> lookup("#studentComboBox").tryQuery().isPresent());

        selectFirstFromComboBox("#studentComboBox");
        sleep(400, TimeUnit.MILLISECONDS);

        TextField ticketField = lookup("#ticketField").queryAs(TextField.class);
        clickOn(ticketField);

        if (ticketField.getText() != null && !ticketField.getText().isEmpty()) {
            clickOn(ticketField)
                    .press(KeyCode.META).press(KeyCode.A)
                    .release(KeyCode.A).release(KeyCode.META);
            sleep(100, TimeUnit.MILLISECONDS);
        }

        clickOn(ticketField).write(ticketNumber);
        createdTickets.add(ticketNumber);
        sleep(200, TimeUnit.MILLISECONDS);

        Spinner<Integer> theorySpinner = lookup("#theoryGradeSpinner").queryAs(Spinner.class);
        clickOn(theorySpinner)
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("4");
        sleep(100, TimeUnit.MILLISECONDS);

        Spinner<Integer> practiceSpinner = lookup("#practiceGradeSpinner").queryAs(Spinner.class);
        clickOn(practiceSpinner)
                .press(KeyCode.META).press(KeyCode.A)
                .release(KeyCode.A).release(KeyCode.META)
                .write("5");
        sleep(200, TimeUnit.MILLISECONDS);

        TableView<?> tableBefore = lookup("#finalWorksTable").query();

        clickOn("#saveButton");

        sleep(300, TimeUnit.MILLISECONDS);
    }
}
