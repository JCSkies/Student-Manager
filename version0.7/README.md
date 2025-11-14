Student Manager
### A JavaFX desktop application for managing student profiles, comments, and role preferences using SQLite.

![Java](https://img.shields.io/badge/Java-21-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-orange)
![SQLite](https://img.shields.io/badge/SQLite-Database-blue)
![Build](https://img.shields.io/badge/Build-Maven-yellow)
![License](https://img.shields.io/badge/License-MIT-green)

---

📌 Functional Specification

1. Home Page

Displays a welcome message.

Provides navigation to all feature pages, including the Define Programming Languages page.

2. Define Programming Languages Page

Input field to add a programming language (required).

Prevents blank submissions through input validation.

Stores languages temporarily (for testing) with plans for database persistence.

Includes a Back button.

3. Student List Page

Displays a sortable list of all students.

Includes an Information button for viewing individual student details.

Includes a Back button.

4. Define Student Page

Provides a form for entering new student information.

Automatically sorts the list after a student is added.

Stores data into the persistent student database (SQLite).

5. Local Persistent Data

Uses a JSON file (early implementation) to store student data.

On startup, loads student data automatically.

Updates the JSON file (or database) on add/delete operations.

Ensures no data is lost after closing the program.

6. Search Keywords Page

Users can enter search keywords.

Results displayed in a table-view styled results page.

Filters students by name, comments, or attributes containing the keyword.

7. Comment Editing

View, add, and delete comments on a student’s profile.

Accessible through the specific student's information page.

Provides an edit mode for modifying student details and comments.

---

🛠 Technical Specification

Language: Java 21

GUI Framework: JavaFX

Build Tool: Maven

JDK Distribution: ZuluFX 21 (verified under Zulu 23)

Database: SQLite

Database Library: JDBC

Main Class: cs151.application.Main

Styling: CSS

Architecture: MVC (Model–View–Controller)

---

UML Diagram
### UML
![UML](CS151v0.7.png)

---

🧰 Tech Stack Overview

Java (Zulu OpenJDK 21+)

JavaFX

SQLite (JDBC)

CSS Styling

Maven Build System

## 📂 Project Structure

```

src/
└── main/
    ├── java/
    │   └── cs151/application/
    │       ├── controller/
    │       │   ├── EditPageController.java
    │       │   ├── DefineLanguagePageController.java
    │       │   ├── DefineStudentPageController.java
    │       │   ├── HomePageController.java
    │       │   ├── SearchPageController.java
    │       │   ├── StudentInfoPageController.java
    │       │   └── StudentsListPageController.java
    │       ├── model/
    │       │   └── Student.java
    │       ├── services/
    │       │   ├── ControllerUtility.java
    │       │   ├── DataAccessor.java
    │       │   ├── DatabaseUtility.java
    │       │   └── ViewUtility.java
    │       ├── view/
    │       │   ├── EditStudentPage.java
    │       │   ├── DefineLanguagePage.java
    │       │   ├── DefineStudentPage.java
    │       │   ├── HomePage.java
    │       │   ├── ListDisplay.java
    │       │   ├── SearchStudentPage.java
    │       │   ├── StudentInfoPage.java
    │       │   └── StudentsListPage.java
    │       ├── Main.java
    │       └── module-info.java
    ├── resources/
    │   └── img/
    │       ├── bg.png
    │       ├── inputBg.png
    │       └── sectionBg.png
    └── style/
        └── homePage.css
```  
---

🎯 Future Enhacements

Add unit tests (JUnit) for controllers and services

Migrate all temporary JSON storage fully into SQLite

Add filtering by multiple attributes (e.g., role + GPA + status)

Implement dark mode / themes

Add export to CSV/Excel

Improve comment threading or timestamps

---
