package cs151.application.view;

import cs151.application.controller.StudentsListPageController;
import cs151.application.services.ViewUtility;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class StudentsListPage extends Stage {
    private final ViewUtility tool = new ViewUtility();
    private final StudentsListPageController controller;

    public StudentsListPage(List<String> stdNames) {
        // set scene
        this.controller = new StudentsListPageController(this);
        Scene pageScene = buildScene(stdNames);
        this.setTitle("Student List");
        this.setScene(pageScene);
    }

    public Scene buildScene(List<String> stdNames) {
        // label
        Label labelText = new Label("   List Of Students   ");
        labelText.getStyleClass().add("subtitle");

        // close button
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> controller.closeBtnAction());
        HBox btnLayout = new HBox(closeBtn);
        btnLayout.getStyleClass().add("buttonLayout");

        // make table view
        TableView<String> studentTable = makeStdTable(stdNames);
        VBox listBox = new VBox(studentTable);
        listBox.getStyleClass().add("sectionLayout");

        // student Pane
        ScrollPane studentListPane = new ScrollPane(listBox);
        studentListPane.setFitToWidth(true);

        VBox contentBox = new VBox(studentListPane);
        contentBox.getStyleClass().add("inputLayout");

        // layout
        VBox pageLayout = new VBox(labelText, contentBox, btnLayout);
        Scene result = new Scene(pageLayout, 800, 700);
        tool.setPageStyle(result);
        return result;
    }

    private TableView<String> makeStdTable(List<String> stdNameList) {
        TableView<String> table = new TableView<>();
        ObservableList<String> data = FXCollections.observableArrayList(stdNameList);
        table.setItems(data);

        // 1) student + counter
        TableColumn<String, Void> idxCol = new TableColumn<>("student Count");
        idxCol.setSortable(false);
        idxCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
                setGraphic(null);
            }
        });
        idxCol.setMinWidth(80);

        // 2) stdName
        TableColumn<String, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue()));
        nameCol.setMinWidth(220);

        // 3) viewBtn
        TableColumn<String, Void> viewCol = new TableColumn<>("Click to view");
        viewCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setOnAction(e -> {
                    int row = getIndex();
                    if (row >= 0 && row < table.getItems().size()) {
                        String stdName = table.getItems().get(row);
                        table.getSelectionModel().select(row);
                        controller.selectAct(stdName);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setText(null);
            }
        });
        viewCol.setMinWidth(90);

        // 4) removeBtn
        TableColumn<String, Void> removeCol = new TableColumn<>("Click to remove");
        removeCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Remove");

            {
                btn.setOnAction(e -> {
                    int row = getIndex();
                    if (row >= 0 && row < table.getItems().size()) {
                        String stdName = table.getItems().get(row);
                        table.getSelectionModel().select(row);
                        controller.deleteAct(stdName);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setText(null);
            }
        });
        removeCol.setMinWidth(90);

        table.getColumns().setAll(idxCol, nameCol, viewCol, removeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setFixedCellSize(40);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return table;
    }
}
