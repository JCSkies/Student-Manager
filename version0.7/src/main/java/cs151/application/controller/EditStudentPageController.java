package cs151.application.controller;

import cs151.application.model.Student;
import cs151.application.services.ControllerUtility;
import cs151.application.services.DataAccessor;
import cs151.application.services.Logger;
import cs151.application.view.EditStudentPage;
import cs151.application.view.StudentInfoPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditStudentPageController {
    private final EditStudentPage page;
    private final ControllerUtility tool = new ControllerUtility();
    private final Student targetStudent;
    private final String oldName;
    private final Logger logger = Logger.getInstance();

    public EditStudentPageController(EditStudentPage page, Student targetStudent) {
        this.targetStudent = targetStudent;
        this.page = page;
        this.oldName = targetStudent.getName();
    }

    public List<String> getLangList(){
        List<String> languageList = new ArrayList<>();
        try (DataAccessor da = new DataAccessor()) {
            languageList = da.getLanguageList();
        } catch (Exception e) {
            logger.log(e);
        }
        return languageList;
    }

    public List<String> getDBList(){
        List<String> dbList = new ArrayList<>();
        try (DataAccessor da = new DataAccessor()) {
            dbList = da.getDatabaseList();
        } catch (Exception e) {
            logger.log(e);
        }
        return dbList;
    }

    public void cancelAct() {
        Student updatedStd = new Student();
        try (DataAccessor da = new DataAccessor()) {
            updatedStd = da.getStudent(targetStudent.getName());
        } catch (Exception e) {
            logger.log(e);
        }

        StudentInfoPage newPage = new StudentInfoPage(updatedStd);
        newPage.show();
        page.close();
    }


    public void saveAct(TextField nameInput, ComboBox<String> statusInput, RadioButton employed, TextField jobInput, ComboBox<String> roleInput, List<CheckBox> langCheckBoxes, List<CheckBox> dataCheckBoxes) {
        tool.popAlert(Alert.AlertType.CONFIRMATION, "Are you sure to submit?").showAndWait().ifPresent(responds -> {
            if (responds == ButtonType.OK) {
                String name = nameInput.getText();
                if (isValid(name) && !isDuplicate(name)) {
                    targetStudent.setName(nameInput.getText());
                    targetStudent.setAcademicStatus(statusInput.getValue());
                    targetStudent.setEmployed(employed.selectedProperty().get());
                    targetStudent.setJobDetails(jobInput.getText());
                    targetStudent.setPreferredRole(roleInput.getValue());
                    targetStudent.setProgrammingLanguages(makeListFromCheckBox(langCheckBoxes));
                    targetStudent.setDatabases(makeListFromCheckBox(dataCheckBoxes));
                    storeData();
                    tool.popAlert(Alert.AlertType.INFORMATION, "Change Saved").showAndWait();
                    logger.log("Student: " + name + "'s information has changed");
                }
            }
        });
        cancelAct();
    }

    public void storeData() {
        try (DataAccessor da = new DataAccessor()) {
            da.editStudent(oldName, targetStudent);
        } catch (Exception e) {
            logger.log(e);
        }
    }

    public List<String> makeListFromCheckBox(List<CheckBox> cb) {
        List<String> res = new ArrayList<>();
        for (CheckBox box : cb) {
            if (box.isSelected()) {
                res.add(box.getText());
            }
        }
        return res;
    }

    public void addCommentAct(VBox commentAreaBox, TextArea comTextArea) {
        ControllerUtility tool = new ControllerUtility();
        String comText = comTextArea.getText();
        if (comText.isBlank()) {
            tool.popAlert(Alert.AlertType.ERROR, "Comment can not be blank").showAndWait();
            logger.log("ERROR: fail to add blank comment");
            return;
        }

        String comWithTimeStemp = targetStudent.addComment(comText);
        try (DataAccessor da = new DataAccessor()) {
            da.addComment(targetStudent.getName(), comWithTimeStemp);
            logger.log("New comment added to student " + targetStudent.getName());
        } catch (Exception e) {
            logger.log(e);
        }

        TextArea text = new TextArea("Comment:  " + comWithTimeStemp + "\n");
        text.setEditable(false);
        text.setMaxWidth(483);
        text.setMinWidth(483);
        text.setMaxHeight(80);
        text.getStyleClass().add("showText");

        Button clear = new Button("×");
        clear.getStyleClass().add("clear-btn");
        clear.setFocusTraversable(false);

        StackPane wrapper = new StackPane(text, clear);
        wrapper.setPickOnBounds(false);
        StackPane.setAlignment(clear, Pos.TOP_RIGHT);
        StackPane.setMargin(clear, new Insets(6, 6, 0, 0));

        clear.setOnAction(e -> deleteCommentBtnAct(comWithTimeStemp, wrapper));
        commentAreaBox.getChildren().add(wrapper);

        tool.popAlert(Alert.AlertType.INFORMATION, "Added successfully").showAndWait();
        comTextArea.clear();
    }

    private boolean isValid(String name) {
        if (name.isBlank()) {
            tool.popAlert(Alert.AlertType.ERROR, "Name can not be empty").showAndWait();
            logger.log("ERROR: fail to give a student an empty name");
            return false;
        }
        return true;
    }

    private boolean isDuplicate(String name) {
        if (Objects.equals(name, targetStudent.getName())) return false;
        boolean isDuplicate = false;
        try (DataAccessor da = new DataAccessor()) {
            isDuplicate = da.isPresent(name);
        } catch (Exception e) {
            logger.log(e);
        }
        if (isDuplicate) tool.popAlert(Alert.AlertType.ERROR, "Student already exists").showAndWait();
        logger.log("ERROR: fail to change a student name because the new name is existed");
        return isDuplicate;
    }

    public void deleteCommentBtnAct(String comment, StackPane wrapper) {
        Parent p = wrapper.getParent();          // wrapper is TextArea and X stacking StackPane
        if (p instanceof Pane pane) pane.getChildren().remove(wrapper);

        try (DataAccessor da = new DataAccessor()) {
            da.deleteComment(comment);
            logger.log("1 comment has deleted from student" + targetStudent.getName());
        } catch (Exception e) {
            logger.log(e);
        }
    }
}
