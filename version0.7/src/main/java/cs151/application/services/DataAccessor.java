package cs151.application.services;

import cs151.application.model.Student;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class DataAccessor implements AutoCloseable {
    private final Path path = Paths.get("localData", "database.db");
    private final String url = "jdbc:sqlite:" + path;
    private final Connection conn;

    public DataAccessor() throws SQLException {
        this.conn = DriverManager.getConnection(url);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA busy_timeout = 5000");
        }
    }

    public void initDatabase() throws Exception {
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {

            st.execute("""
                        CREATE TABLE IF NOT EXISTS students(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL UNIQUE COLLATE NOCASE,
                            academic_status TEXT,
                            is_employed INTEGER DEFAULT 0 CHECK (is_employed IN (0,1)),
                            job_detail TEXT,
                            prefer_role TEXT
                        )
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS languages(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            lang_name TEXT NOT NULL COLLATE NOCASE UNIQUE
                        )
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS databases(
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            database_name TEXT NOT NULL COLLATE NOCASE UNIQUE
                        )
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS student_language_map(
                            student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                            lang_id INTEGER NOT NULL REFERENCES languages(id) ON DELETE CASCADE,
                            PRIMARY KEY(student_id, lang_id)
                        )
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS student_database_map(
                            student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                            database_id INTEGER NOT NULL REFERENCES databases(id) ON DELETE CASCADE,
                            PRIMARY KEY(student_id, database_id)
                        )
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS comments(
                            student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                            comment TEXT
                        )
                    """);
        }
        conn.commit();
        conn.setAutoCommit(true);
    }

    public void addDatabaseSkill(String name) throws SQLException {
        String sql = """
                INSERT INTO databases(database_name)
                VALUES (?)
                ON CONFLICT(database_name) DO NOTHING
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.executeUpdate();
        }
    }

    public boolean isDatabaseInitialed() throws SQLException {
        String sql = """
                SELECT 1 FROM databases LIMIT 1
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet res = ps.executeQuery()) {
                return res.next();
            }
        }
    }

    public List<String> getDatabaseList() throws SQLException {
        List<String> returnList = new ArrayList<>();
        String sql = """
                SELECT database_name FROM databases
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) returnList.add(res.getString(1));
            }
        }
        return returnList;
    }

    public List<String> getLanguageList() throws SQLException {
        String sqlComment = """
                SELECT lang_name FROM languages
                """;

        List<String> res = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sqlComment)) {
            try (ResultSet set = ps.executeQuery()) {
                while (set.next()) {
                    res.add(set.getString(1));
                }
            }
        }
        return res;
    }

    public Long languagesSize() throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM languages
                """;
        long size;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet report = ps.executeQuery()) {
                size = report.next() ? report.getLong(1) : 0L;
            }
        }
        return size;
    }

    public boolean addLanguage(String langName) throws SQLException {
        String sql = """
                INSERT INTO languages(lang_name)
                VALUES (?)
                ON CONFLICT(lang_name) DO NOTHING
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, langName.trim());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteLanguage(String langName) throws SQLException {
        if (langName == null) return false;
        String sql = """
                DELETE FROM languages WHERE lang_name = ? COLLATE NOCASE
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, langName.trim());
            int deleteReport = ps.executeUpdate();
            if (deleteReport <= 0) return false;
        }
        return true;
    }

    public Long findStudentId(String studentName) throws SQLException {
        String sql = """
                SELECT id FROM students
                WHERE name = ? COLLATE NOCASE
                LIMIT 1
                """;
        Long res = null;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentName.trim());
            try (ResultSet set = ps.executeQuery()) {
                if (set.next()) {
                    res = set.getLong("id");
                }
            }
        }
        return res;
    }

    public Student getStudent(String stdName) throws SQLException {
        if (stdName == null) return null;
        Student res = new Student();
        res.setName("lets do this");
        String sqlComment = """
                SELECT id, name, academic_status, is_employed, job_detail, prefer_role
                FROM students
                WHERE name = ?
                LIMIT 1
                """;
        long id;
        try (PreparedStatement ps = conn.prepareStatement(sqlComment)) {
            ps.setString(1, stdName.trim());
            try (ResultSet set = ps.executeQuery()) {
                if (!set.next()) {
                    return null;
                }
                res.setName(set.getString("name"));
                res.setAcademicStatus(set.getString("academic_status"));
                res.setEmployed(set.getInt("is_employed") != 0);
                res.setJobDetails(set.getString("job_detail"));
                res.setPreferredRole(set.getString("prefer_role"));
                id = set.getInt("id");
            }
        }

        res.addLanguages(getLanguageListByStudent(id));
        res.addDatabases(getDatabaseListByStudent(id));
        res.addComments(getCommentList(id));

        return res;
    }

    public List<String> getStudentNameList() throws SQLException {
        List<String> stdNameList = new ArrayList<>();
        String sql = """
                SELECT name FROM students ORDER BY name
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet allNameSet = ps.executeQuery()) {
                while (allNameSet.next()) {
                    stdNameList.add(allNameSet.getString("name"));
                }
            }
        }
        return stdNameList;
    }

    public boolean deleteStudent(String stdName) throws SQLException {
        String sql = """
                DELETE FROM students WHERE name = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stdName.trim());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<String> getLanguageListByStudent(long id) throws SQLException {
        final String sql = """
                SELECT lang_id FROM student_language_map WHERE student_id = ?
                """;
        List<String> res = new ArrayList<>();
        List<Long> langIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) langIds.add(rs.getLong(1));
            }
        }
        for (Long langId : langIds) {
            String sql2 = """
                    SELECT lang_name FROM languages WHERE id = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setLong(1, langId);
                try (ResultSet set = ps.executeQuery()) {
                    if (set.next()) res.add(set.getString(1));
                }
            }
        }
        return res;
    }

    public List<String> getDatabaseListByStudent(long id) throws SQLException {
        final String sql = """
                SELECT database_id FROM student_database_map WHERE student_id = ?
                """;
        List<String> res = new ArrayList<>();
        List<Long> dbIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) dbIds.add(rs.getLong(1));
            }
        }

        for (Long dbId : dbIds) {
            String sql1 = """
                    SELECT database_name FROM databases WHERE id = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setLong(1, dbId);
                try (ResultSet set = ps.executeQuery()) {
                    if (set.next()) res.add(set.getString(1));
                }
            }
        }
        return res;
    }

    public List<String> getCommentList(long id) throws SQLException {
        List<String> res = new ArrayList<>();
        String sql = """
                SELECT comment FROM comments WHERE student_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet set = ps.executeQuery()) {
                while (set.next()) {
                    res.add(set.getString("comment"));
                }
            }
        }
        return res;
    }

    public boolean isPresent(String name) throws SQLException {
        String sql = """
                SELECT name FROM students WHERE name = ? COLLATE NOCASE
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ResultSet set = ps.executeQuery();
            return set.next();
        }
    }

    public void addStudent(Student std) throws SQLException {
        if (std.getName().isBlank()) return;

        long newId = -1;
        String sql = """
                INSERT INTO students(name, academic_status, is_employed, job_detail, prefer_role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, std.getName());
            ps.setString(2, std.getAcademicStatus());
            ps.setInt(3, (std.isEmployed() ? 1 : 0));
            ps.setString(4, std.getJobDetails());
            ps.setString(5, std.getPreferredRole());
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) newId = gk.getLong(1);
            }
        }
        if (newId != -1 && !std.getProgrammingLanguages().isEmpty()) {
            insertStudentLangMap(newId, std.getProgrammingLanguages());
        }
        if (newId != -1 && !std.getDatabases().isEmpty()) {
            insertStudentDatabaseMap(newId, std.getDatabases());
        }
        if (newId != -1 && !std.getComments().isEmpty()) {
            insertComments(newId, std.getComments());
        }
    }

    private void insertStudentLangMap(Long id, List<String> langList) throws SQLException {
        for (String langName : langList) {
            Long langId = getLanguageId(langName);
            String sql = """
                    INSERT INTO student_language_map(student_id, lang_id)
                    VALUES (?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setLong(2, langId);
                ps.executeUpdate();
            }
        }
    }

    private void insertStudentDatabaseMap(Long id, List<String> databaseList) throws SQLException {
        for (String databaseName : databaseList) {
            Long databaseId = getDatabaseId(databaseName);
            String sql = """
                    INSERT INTO student_database_map(student_id, database_id)
                    VALUES (?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setLong(2, databaseId);
                ps.executeUpdate();
            }
        }
    }

    private void insertComments(Long id, List<String> commentList) throws SQLException {
        for (String comment : commentList) {
            String sql = """
                    INSERT INTO comments(student_id, comment)
                    VALUES (?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, comment);
                ps.executeUpdate();
            }
        }
    }

    public void addComment(String studentName, String comment) throws SQLException {
        Long id = findStudentId(studentName);
        if (id == null) return;
        insertComments(id, List.of(comment));
    }

    public List<String> searchByKeyWords(String keyword) throws SQLException {
        Set<String> searchResult = new TreeSet<>();
        // add all key word related student from students table

        String pat = "%" + keyword + "%";
        String sql = """
                SELECT name FROM students AS s
                WHERE s.name            LIKE ?  COLLATE NOCASE
                   OR s.academic_status LIKE ?  COLLATE NOCASE
                   OR s.job_detail      LIKE ?  COLLATE NOCASE
                   OR s.prefer_role     LIKE ?  COLLATE NOCASE
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, pat);
            ps.setString(i++, pat);
            ps.setString(i++, pat);
            ps.setString(i, pat);
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) searchResult.add(res.getString(1));
            }
        }

        Set<Long> stdIdSet = new TreeSet<>();
        Long langId = getLanguageId(keyword);
        if (langId != null) {
            addStdIdToSetFromLangId(stdIdSet, langId);
        }

        Long dataId = getDatabaseId(keyword);
        if (dataId != null) {
            addStdIdToSetFromDbId(stdIdSet, dataId);
        }

        for (Long stdId : stdIdSet) {
            String sql2 = """
                    SELECT name FROM students WHERE id = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setLong(1, stdId);
                try (ResultSet set = ps.executeQuery()) {
                    if (set.next()) {
                        searchResult.add(set.getString(1));
                    }
                }
            }
        }
        return new ArrayList<>(searchResult);
    }

    public Long getLanguageId(String langName) throws SQLException {
        Long id = null;
        String sql = """
                SELECT id FROM languages WHERE lang_name = ? COLLATE NOCASE
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, langName);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) id = res.getLong(1);
            }
        }
        return id;
    }

    private Long getDatabaseId(String dbName) throws SQLException {
        Long id = null;
        String sql = """
                SELECT id FROM databases WHERE database_name = ? COLLATE NOCASE
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            try (ResultSet res = ps.executeQuery()) {
                if (res.next()) id = res.getLong(1);
            }
        }
        return id;
    }

    public void addStdIdToSetFromLangId(Set<Long> set, Long langId) throws SQLException {
        if (langId == null) return;
        String sql = """
                SELECT student_id FROM student_language_map
                WHERE lang_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, langId);
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) set.add(res.getLong(1));
            }
        }
    }

    private void addStdIdToSetFromDbId(Set<Long> set, Long dbId) throws SQLException {
        if (dbId == null) return;
        String sql = """
                SELECT student_id FROM student_database_map
                WHERE database_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dbId);
            try (ResultSet res = ps.executeQuery()) {
                while (res.next()) set.add(res.getLong(1));
            }
        }
    }

    public void deleteComment(String comment) throws SQLException {
        String sql = """
                DELETE FROM comments WHERE comment = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment);
            ps.executeUpdate();
        }
    }

    public void editStudent(String target, Student sample) throws SQLException {
        long stdId = findStudentId(target);
        String updateStd = """
                UPDATE students SET name = ?, academic_status = ?, is_employed = ?, job_detail = ?, prefer_role = ? WHERE id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(updateStd)) {
            ps.setString(1, sample.getName());
            ps.setString(2, sample.getAcademicStatus());
            ps.setLong(3, sample.isEmployed() ? 1L : 0L);
            ps.setString(4, sample.getJobDetails());
            ps.setString(5, sample.getPreferredRole());
            ps.setLong(6, stdId);
            ps.executeUpdate();
        }

        String deletingLangs = """
                DELETE FROM student_language_map WHERE student_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(deletingLangs)) {
            ps.setLong(1, stdId);
            ps.executeUpdate();
        }

        String deletingDatabaseSkills = """
                DELETE FROM student_database_map WHERE student_id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(deletingDatabaseSkills)) {
            ps.setLong(1, stdId);
            ps.executeUpdate();
        }

        insertStudentLangMap(stdId, sample.getProgrammingLanguages());
        insertStudentDatabaseMap(stdId, sample.getDatabases());
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }
}
