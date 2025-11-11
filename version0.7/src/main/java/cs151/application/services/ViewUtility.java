package cs151.application.services;

import javafx.scene.Scene;
import java.util.Objects;

public class ViewUtility {
    public ViewUtility(){}

    public void setPageStyle(Scene scene) {
        String stylePath = "/style/homePage.css";
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(stylePath)).toExternalForm());
    }
}
