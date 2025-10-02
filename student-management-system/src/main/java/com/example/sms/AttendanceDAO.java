package com.example.sms;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Attendance operations: mark and view attendance.
 */
public class AttendanceDAO {

    /** Marks attendance for a student on a given date. */
    public boolean markAttendance(int studentId, LocalDate date, String status) {
        String sql = "INSERT INTO attendance (student_id, date, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] markAttendance: " + e.getMessage());
        }
        return false;
    }

    /** Returns attendance strings like "2025-10-02 - Present" for a student. */
    public List<String> getAttendanceByStudent(int studentId) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT date, status FROM attendance WHERE student_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("date");
                    String status = rs.getString("status");
                    result.add(d.toString() + " - " + status);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] getAttendanceByStudent: " + e.getMessage());
        }
        return result;
    }
}
