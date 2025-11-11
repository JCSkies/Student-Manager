package cs151.application.controller;

import cs151.application.services.DataAccessor;
import cs151.application.services.Logger;
import cs151.application.view.DefineLanguagePage;
import cs151.application.view.DefineStudentPage;
import cs151.application.view.SearchStudentPage;
import cs151.application.view.StudentsListPage;

import java.util.ArrayList;
import java.util.List;

public class HomePageController {
    private final Logger logger = Logger.getInstance();

    public HomePageController() {
    }

    public void defineBtnAct() {
        DefineLanguagePage definePage = new DefineLanguagePage();
        definePage.show();
    }

    public void displayBtnAct() {
        List<String> studentList = new ArrayList<>();
        try (DataAccessor da = new DataAccessor()) {
            studentList = da.getStudentNameList();
        } catch (Exception e) {
            logger.log(e);
        }
        StudentsListPage showPage = new StudentsListPage(studentList);
        showPage.show();
    }

    public void defineStudentAct() {
        DefineStudentPage showPage = new DefineStudentPage();
        showPage.show();
    }

    public void searchBtnAct() {
        SearchStudentPage newPage = new SearchStudentPage();
        newPage.show();
    }
}
