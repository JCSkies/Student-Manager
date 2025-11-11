package cs151.application.view;

import cs151.application.controller.SearchPageController;
import cs151.application.services.ViewUtility;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SearchStudentPage extends Stage {
    private final SearchPageController controller;

    public SearchStudentPage() {
        this.controller = new SearchPageController(this);
        Scene pageScene = buildScene();
        this.setScene(pageScene);
        this.show();
    }

    private Scene buildScene() {
        Label title = new Label("Search for a student");
        TextField input = new TextField();
        input.setPromptText("Enter the student name: ");

        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> controller.searchAct(input.getText()));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> controller.cancelAct());

        HBox btnLayout = new HBox(searchBtn, cancelBtn);
        btnLayout.getStyleClass().add("buttonLayout");

        VBox pageLayout = new VBox(title, input, btnLayout);
        pageLayout.getStyleClass().add("sectionLayout");

        Scene scene = new Scene(pageLayout, 400, 200);
        ViewUtility tool = new ViewUtility();
        tool.setPageStyle(scene);
        return scene;
    }
}
