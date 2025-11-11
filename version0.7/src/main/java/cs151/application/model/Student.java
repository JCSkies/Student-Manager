package cs151.application.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String academicStatus; // Freshman, Sophomore, etc.
    private boolean employed;
    private String jobDetails;
    private String preferredRole;  // front end/ back end etc.
    private List<String> programmingLanguages; // coding language
    private List<String> databases;             // database skill
    private final List<String> comments;              // prof's comments
    private boolean whitelist = true;
    private boolean blacklist = false;

    public Student() {
        this.programmingLanguages = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.databases = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    public boolean isEmployed() {
        return employed;
    }

    public void setEmployed(boolean employed) {
        this.employed = employed;
    }

    public List<String> getProgrammingLanguages() {
        return programmingLanguages;
    }

    public void setProgrammingLanguages(List<String> programmingLanguages) {
        this.programmingLanguages = programmingLanguages;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public void setPreferredRole(String preferredRole) {
        this.preferredRole = preferredRole;
    }

    public List<String> getComments() {
        return comments;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public void setWhitelist(boolean whitelist) {
        this.whitelist = whitelist;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public void setBlacklist(boolean blacklist) {
        this.blacklist = blacklist;
    }

    public void addLanguages(List<String> lang) {
        programmingLanguages.addAll(lang);
    }

    public void addLanguage(String lang) {
        programmingLanguages.add(lang);
    }

    public void addDatabases(List<String> databasesList) {
        databases.addAll(databasesList);
    }

    public void addDatabase(String databaseName) {
        databases.add(databaseName);
    }

    public void addComments(List<String> c) {
        comments.addAll(c);
    }

    public String addComment(String comment) {
        ZonedDateTime now = ZonedDateTime.now();
        String timeStamp =  now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
        this.comments.add(" <" + timeStamp + ">\n" + comment);
        return " <" + timeStamp + ">\n" + comment;
    }

    public String toString() {
        return "FullName : " + name +
                "\nAcademicStatus : " + academicStatus +
                "\nEmployed : " + employed +
                "\nJobDetails : " + jobDetails +
                "\nProgrammingLanguages : " + programmingLanguages +
                "\nDatabases : " + databases +
                "\nPreferredRole : " + preferredRole +
                "\nWhitelist : " + whitelist +
                "\nBlacklist : " + blacklist +
                "\nComments : " + comments;
    }
}
