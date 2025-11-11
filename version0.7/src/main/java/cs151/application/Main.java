package cs151.application;

import cs151.application.services.DatabaseUtility;
import cs151.application.view.HomePage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {
    private final Path path = Paths.get("localData", "database.db");

    @Override
    public void start(Stage primaryStage) throws IOException {
        Files.createDirectories(path.getParent());
        if (!Files.exists(path)) {
            DatabaseUtility initializer = new DatabaseUtility(path);
            initializer.initDatabase();
            initializer.initDatabaseSkillList();
            initializer.initDefaultLanguages();
            initializer.initSampleStudents();
        }
        HomePage homePage = new HomePage();
        homePage.show();
    }
}
