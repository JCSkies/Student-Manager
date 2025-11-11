package cs151.application.view;

import cs151.application.controller.EditStudentPageController;
import cs151.application.model.Student;
import cs151.application.services.DataAccessor;
import cs151.application.services.ViewUtility;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditStudentPage extends Stage {
    private Student editingStudent;   // target student
    private EditStudentPageController controller;

    // for language check box
    private List<String> languageList;
    private final List<CheckBox> langCheckBoxes = new ArrayList<>();

    // for database check box
    private List<String> dataList = new ArrayList<>();
    private final List<CheckBox> dataCheckBoxes = new ArrayList<>();
    private final List<String> roleList = new ArrayList<>(Arrays.asList("Backend", "Frontend", "QA", "Security", "Database", "UI/UX", "DevOps", "Network", "IT support", "Other"));

    public EditStudentPage(Student std) {
        this.editingStudent = std;
        controller = new EditStudentPageController(this, std);
        languageList = controller.getLangList();
        dataList = controller.getDBList();

        Scene sc = buildScene();
        this.setScene(sc);
        this.show();
    }

    public Scene buildScene() {
        Label title = new Label("Edit student's Information");
        title.getStyleClass().add("subtitle");
        // name line
        Label name = new Label("Name: ");
        TextField nameInput = new TextField(editingStudent.getName());
        HBox nameLine = new HBox(name, nameInput);                   // line 1

        // academic status line
        Label academicStatus = new Label("Academic Status: ");
        ComboBox<String> statusInput = new ComboBox<>();
        statusInput.getItems().addAll("Freshman", "sophomore", "Junior", "Senior", "Graduate");
        statusInput.getSelectionModel().select(editingStudent.getAcademicStatus());
        statusInput.setVisibleRowCount(5);
        HBox academicStatusLine = new HBox(academicStatus, statusInput);      // line 2

        // employment info line
        Label employedText = new Label("Is student employed? ");
        RadioButton employed = new RadioButton("Employed");
        RadioButton notEmployed = new RadioButton("Not Employed");
        ToggleGroup isEmployed = new ToggleGroup();
        employed.setToggleGroup(isEmployed);
        notEmployed.setToggleGroup(isEmployed);
        if (editingStudent.isEmployed()) {
            employed.setSelected(true);
        } else {
            notEmployed.setSelected(true);
        }
        HBox employLine = new HBox(employedText, employed, notEmployed); // line 3

        // job line
        Label job = new Label("Job Detail: ");
        TextField jobInput = new TextField(editingStudent.getJobDetails());
        HBox jobLine = new HBox(job, jobInput);
        jobLine.disableProperty().bind(notEmployed.selectedProperty());   // line 4
        notEmployed.setOnAction(e -> {
            if (notEmployed.isSelected()) jobInput.clear();
        });

        // role line
        Label role = new Label("Preferred Role: ");
        ComboBox<String> roleInput = new ComboBox<>();
        roleInput.getItems().addAll(roleList);
        roleInput.getSelectionModel().select(editingStudent.getPreferredRole());
        HBox roleLine = new HBox(role, roleInput);   // line 5

        // language select  line 6
        VBox langSelectArea = buildSelectArea(languageList, "Select skilled programming languages: ", langCheckBoxes, editingStudent.getProgrammingLanguages());

        // database select box  line 7
        VBox dataSelectArea = buildSelectArea(dataList, "Select skilled database: ", dataCheckBoxes, editingStudent.getDatabases());

        // comment area line 8
        VBox commentAreaBox = new VBox();
        commentAreaBox.setSpacing(5);
        commentAreaBox.setAlignment(Pos.CENTER);
        Label commentTitle = new Label("Comments: ");

        for (String comment : editingStudent.getComments()) {
            TextArea text = new TextArea("Comment:  " + comment + "\n");
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

            clear.setOnAction(e -> controller.deleteCommentBtnAct(comment, wrapper));
            commentAreaBox.getChildren().add(wrapper);
        }

        ScrollPane commentArea = new ScrollPane(commentAreaBox);
        commentArea.getStyleClass().add("showText");
        VBox commentBox = new VBox(commentTitle, commentArea);
        commentArea.setMaxWidth(500);
        commentArea.setMinWidth(500);
        commentArea.setMaxHeight(280);
        commentArea.setMinHeight(280);

        // new comment text file
        TextArea addCommentTextArea = new TextArea();
        addCommentTextArea.setMaxWidth(500);
        addCommentTextArea.setMinWidth(500);
        addCommentTextArea.setMaxHeight(80);
        addCommentTextArea.setMinHeight(80);
        addCommentTextArea.setPromptText("Add new comment...");

        Button addCommentBtn = new Button("Add New Comment");
        addCommentBtn.setOnAction(e -> controller.addCommentAct(commentAreaBox, addCommentTextArea));

        // layout the form area
        VBox form = new VBox(nameLine, academicStatusLine, employLine, jobLine, roleLine, langSelectArea, dataSelectArea, commentBox);
        form.setSpacing(10);

        // buttons area
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> controller.saveAct(nameInput, statusInput, employed, jobInput, roleInput, langCheckBoxes, dataCheckBoxes));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> controller.cancelAct());
        HBox btnLayout = new HBox(saveBtn, cancelBtn);
        btnLayout.getStyleClass().add("buttonLayout");
        btnLayout.setAlignment(Pos.CENTER);


        VBox addCommentArea = new VBox(addCommentTextArea, addCommentBtn);
        VBox contentLayout = new VBox(form, addCommentArea, btnLayout);
        contentLayout.getStyleClass().add("sectionLayout");

        VBox pageLayout = new VBox(title, contentLayout);

        Scene pageScene = new Scene(pageLayout, 900, 1000);
        ViewUtility tool = new ViewUtility();
        tool.setPageStyle(pageScene);
        return pageScene;
    }

    private VBox buildSelectArea(List<String> choice, String title, List<CheckBox> boxList, List<String> preSelectList) {
        Label titleText = new Label(title);
        FlowPane checkArea = new FlowPane();
        checkArea.setHgap(5);
        checkArea.setVgap(5);
        VBox selectArea = new VBox(titleText, checkArea);
        checkArea.prefWrapLengthProperty().bind(selectArea.widthProperty());

        for (String lang : choice) {
            CheckBox cb = new CheckBox(lang);
            boxList.add(cb);
            checkArea.getChildren().add(cb);
            if (preSelectList.contains(lang)) {
                cb.setSelected(true);
            }
        }
        return selectArea;
    }
}
