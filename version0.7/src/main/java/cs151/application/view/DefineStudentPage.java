package cs151.application.view;

import cs151.application.controller.DefineStudentPageController;
import cs151.application.services.ViewUtility;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefineStudentPage extends Stage {
    private final DefineStudentPageController controller = new DefineStudentPageController(this);
    private final ViewUtility tool = new ViewUtility();

    // for language check box
    private List<String> languageList;
    private final List<CheckBox> langCheckBoxes = new ArrayList<>();

    // for database check box
    private List<String> dataList = new ArrayList<>();
    private final List<CheckBox> dataCheckBoxes = new ArrayList<>();

    // role drop down
    private final List<String> roleList = new ArrayList<>(Arrays.asList("Backend", "Frontend", "QA", "Security", "Database", "UI/UX", "DevOps", "Network", "IT support", "Other"));

    public DefineStudentPage() {
        languageList = controller.getLangList();
        dataList = controller.getDBList();
        Scene pageScene = buildScene();
        this.setScene(pageScene);
        this.show();
    }

    public Scene buildScene() {
        Label title = new Label("Define a student");
        title.getStyleClass().add("subtitle");
        // name line
        Label name = new Label("Name: ");
        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter student name");
        HBox nameLine = new HBox(name, nameInput);                   // line 1

        // academic status line
        Label academicStatus = new Label("Academic Status: ");
        ComboBox<String> statusInput = new ComboBox<>();
        statusInput.getItems().addAll("Freshman", "sophomore", "Junior", "Senior", "Graduate");
        statusInput.setPromptText("Choose academic status");
        statusInput.setVisibleRowCount(5);
        HBox academicStatusLine = new HBox(academicStatus, statusInput);      // line 2

        // employment info line
        Label employedText = new Label("Is student employed? ");
        RadioButton employed = new RadioButton("Employed");
        RadioButton notEmployed = new RadioButton("Not Employed");
        ToggleGroup isEmployed = new ToggleGroup();
        employed.setToggleGroup(isEmployed);
        notEmployed.setToggleGroup(isEmployed);
        notEmployed.setSelected(true);
        HBox employLine = new HBox(employedText, employed, notEmployed); // line 3

        // job line
        Label job = new Label("Job Detail: ");
        TextField jobInput = new TextField();
        jobInput.setPromptText("Enter student's current position");
        HBox jobLine = new HBox(job, jobInput);
        jobLine.disableProperty().bind(notEmployed.selectedProperty());   // line 4

        // role line
        Label role = new Label("Preferred Role: ");
        ComboBox<String> roleInput = new ComboBox<>();
        roleInput.getItems().addAll(roleList);
        roleInput.setPromptText("Enter the role the student preferred");
        HBox roleLine = new HBox(role, roleInput);   // line 5

        // language select  line 6
        VBox langSelectArea = buildSelectArea(languageList, "Select skilled programming languages: ", langCheckBoxes);

        // database select box  line 7
        VBox dataSelectArea = buildSelectArea(dataList, "Select skilled database: ", dataCheckBoxes);

        // comment area line 8
        Label commentLabel = new Label("Write comment:");
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write comment for the student");
        commentArea.setWrapText(true);
        commentArea.setPrefHeight(200);
        commentArea.setMaxWidth(400);
        VBox commentBox = new VBox(commentLabel, commentArea);

        // layout the form area
        VBox form = new VBox(nameLine, academicStatusLine, employLine, jobLine, roleLine, langSelectArea, dataSelectArea, commentBox);
        form.setSpacing(10);
        // buttons area
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> controller.saveAct(nameInput, statusInput, employed, jobInput, roleInput, langCheckBoxes, dataCheckBoxes, commentArea));
        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> controller.clearAct());
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> controller.cancelAct());
        HBox btnLayout = new HBox(saveBtn, clearBtn, cancelBtn);
        btnLayout.getStyleClass().add("buttonLayout");
        btnLayout.setAlignment(Pos.CENTER);

        VBox contentLayout = new VBox(form, btnLayout);
        contentLayout.getStyleClass().add("sectionLayout");

        VBox pageLayout = new VBox(title, contentLayout);

        Scene pageScene = new Scene(pageLayout, 700, 800);
        tool.setPageStyle(pageScene);
        return pageScene;
    }

    private VBox buildSelectArea(List<String> choice, String title, List<CheckBox> boxList) {
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
        }

        return selectArea;
    }
}
