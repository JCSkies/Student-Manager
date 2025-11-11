package cs151.application.services;

import javafx.scene.control.Alert;

public class ControllerUtility {
    public ControllerUtility() {
    }

    public Alert popAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.setHeaderText(type.name());
        alert.setTitle("Attention");
        return alert;
    }
}
