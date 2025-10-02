package com.example.sms;

import java.sql.*;

/**
 * Handles authentication against the users table.
 */
public class LoginDAO {
    /** Returns true if the provided username/password match a user record. */
    public boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] authenticate: " + e.getMessage());
        }
        return false;
    }
}
