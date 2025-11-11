package cs151.application.controller;

import cs151.application.model.Student;
import cs151.application.services.DataAccessor;
import cs151.application.services.Logger;
import cs151.application.view.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class StudentInfoPageController {
    private final StudentInfoPage page;
    private final Student std;
    private final Logger logger = Logger.getInstance();

    public StudentInfoPageController(StudentInfoPage page, Student std) {
        this.page = page;
        this.std = std;
    }

    public void langBtnAct() {
        Stage showPage = new ListDisplay(std.getProgrammingLanguages(), "    Student's Programming Languages:");
        showPage.show();
    }

    public void dbBtnAct() {
        Stage showPage = new ListDisplay(std.getDatabases(), "    Student's database skills: ");
        showPage.show();
    }

    public void editStudentBtnAct() {
        EditStudentPage editStudentPage = new EditStudentPage(std);
        editStudentPage.show();
        page.close();
    }

    public void closeBtnAct() {
        List<String> stdNameList = new ArrayList<>();
        try (DataAccessor da = new DataAccessor()) {
            stdNameList = da.getStudentNameList();
        } catch (Exception e) {
            logger.log(e);
        }
        StudentsListPage listPage = new StudentsListPage(stdNameList);
        listPage.show();
        page.close();
    }
}
