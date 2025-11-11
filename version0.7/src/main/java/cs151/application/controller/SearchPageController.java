package cs151.application.controller;

import cs151.application.services.ControllerUtility;
import cs151.application.services.DataAccessor;
import cs151.application.services.Logger;
import cs151.application.view.SearchStudentPage;
import cs151.application.view.StudentsListPage;

import java.util.ArrayList;
import java.util.List;

public class SearchPageController {
    private final SearchStudentPage page;
    private final Logger logger = Logger.getInstance();

    public SearchPageController(SearchStudentPage page) {
        this.page = page;
    }

    public void searchAct(String keyword) {
        List<String> nameList = new ArrayList<>();
        try (DataAccessor da = new DataAccessor()) {
            nameList = da.searchByKeyWords(keyword);
            logger.log("SEARCHING: '" + keyword + "'");
        } catch (Exception e) {
            logger.log(e);
        }
        StudentsListPage newPage = new StudentsListPage(nameList);
        newPage.show();
        page.close();
    }

    public void cancelAct() {
        page.close();
    }
}
