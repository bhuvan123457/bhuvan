package com.example.sms;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CRUD operations on students.
 */
public class StudentDAO {

    /** Adds a new student; sets generated id back on the object. */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (name, age, course, marks) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getCourse());
            ps.setDouble(4, student.getMarks());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] addStudent: " + e.getMessage());
        }
        return false;
    }

    /** Returns all students. */
    public List<Student> getAllStudents() {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT student_id, name, age, course, marks FROM students ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] getAllStudents: " + e.getMessage());
        }
        return result;
    }

    /** Returns a student by id, or null if not found. */
    public Student getStudentById(int studentId) {
        String sql = "SELECT student_id, name, age, course, marks FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] getStudentById: " + e.getMessage());
        }
        return null;
    }

    /** Updates name/age/course/marks for existing student. */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, age = ?, course = ?, marks = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getCourse());
            ps.setDouble(4, student.getMarks());
            ps.setInt(5, student.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] updateStudent: " + e.getMessage());
        }
        return false;
    }

    /** Deletes student by id. */
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] deleteStudent: " + e.getMessage());
        }
        return false;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setAge(rs.getInt("age"));
        s.setCourse(rs.getString("course"));
        s.setMarks(rs.getDouble("marks"));
        return s;
    }
}
