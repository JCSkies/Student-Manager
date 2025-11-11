package cs151.application.view;

import cs151.application.services.ViewUtility;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.util.List;

public class ListDisplay extends Stage {
    ViewUtility tool = new ViewUtility();

    public ListDisplay(List<String> displayStrings, String title) {
        Label titleLabel = new Label(title);

        TextFlow tx = new TextFlow();
        for (String displayString : displayStrings) {
            Text text = new Text(displayString + "   ");
            tx.getChildren().add(text);
        }

        HBox line = new HBox(tx);
        VBox sectionLayout = new VBox(titleLabel, new Separator(), line);
        sectionLayout.getStyleClass().add("sectionLayout");

        VBox pageLayout = new VBox(sectionLayout);

        Scene pageScene = new Scene(pageLayout, 500, 200);
        tool.setPageStyle(pageScene);
        this.setTitle("List");
        this.setScene(pageScene);
    }
}
