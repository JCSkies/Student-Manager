package cs151.application.view;

import cs151.application.controller.HomePageController;
import cs151.application.services.ViewUtility;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomePage extends Stage {
    HomePageController control = new HomePageController();
    ViewUtility tool = new ViewUtility();


    public HomePage() {
        // create button and button action
        Button definePageBtn = new Button("Define programming languages");
        definePageBtn.setOnAction(e -> control.defineBtnAct());

        // button for define a student
        Button defineStudentBtn = new Button("Define student");
        defineStudentBtn.setOnAction(e -> control.defineStudentAct());

        // create button for display students
        Button displayBtn = new Button("Show all students");
        displayBtn.setOnAction(e -> control.displayBtnAct());

        // Search Student
        Button searchStudent = new Button("Search Student");
        searchStudent.setOnAction(e -> control.searchBtnAct());

        // create welcome label
        Label welcomeText = new Label(" Welcome Back ");
        welcomeText.getStyleClass().add("title");

        // set page layout
        VBox btnLayout = new VBox(definePageBtn, defineStudentBtn, displayBtn, searchStudent);
        btnLayout.getStyleClass().add("buttonLayout");
        btnLayout.setAlignment(Pos.CENTER);

        VBox pageLayout = new VBox(welcomeText, btnLayout);
        pageLayout.setSpacing(30);

        // set scene
        BorderPane root = new BorderPane(pageLayout);
        Scene pageScene = new Scene(root, 800, 600);
        tool.setPageStyle(pageScene);

        this.setTitle("Home Page");
        this.setScene(pageScene);
    }

}
