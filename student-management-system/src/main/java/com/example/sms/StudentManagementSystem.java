package com.example.sms;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based Student Management System using JDBC and MySQL.
 */
public class StudentManagementSystem {

    private final Scanner scanner = new Scanner(System.in);
    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final LoginDAO loginDAO = new LoginDAO();

    public static void main(String[] args) {
        new StudentManagementSystem().run();
    }

    private void run() {
        System.out.println("=== Student Management System ===");
        System.out.println("Login is required to continue.\n");

        if (!login()) {
            System.out.println("Exiting.");
            return;
        }

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewStudents();
                case 3 -> updateStudent();
                case 4 -> deleteStudent();
                case 5 -> markAttendance();
                case 6 -> viewAttendance();
                case 7 -> generateReport();
                case 8 -> exportStudentsCsv();
                case 9 -> exit = true;
                default -> System.out.println("Invalid option. Try again.");
            }
        }

        System.out.println("Goodbye!");
    }

    private boolean login() {
        int attempts = 3;
        while (attempts-- > 0) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            boolean ok = loginDAO.authenticate(username, password);
            if (ok) {
                System.out.println("Login successful.\n");
                return true;
            } else {
                System.out.println("Invalid credentials. Attempts left: " + attempts);
            }
        }
        return false;
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Update Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Mark Attendance");
        System.out.println("6. View Attendance (by Student)");
        System.out.println("7. Generate Report (All Students with Marks)");
        System.out.println("8. Export Students to CSV");
        System.out.println("9. Exit");
    }

    private void addStudent() {
        System.out.println("\n-- Add Student --");
        String name = readString("Name: ");
        int age = readInt("Age: ");
        String course = readString("Course: ");
        double marks = readDouble("Marks: ");

        Student s = new Student(name, age, course, marks);
        if (studentDAO.addStudent(s)) {
            System.out.println("Added: " + s);
        } else {
            System.out.println("Failed to add student.");
        }
    }

    private void viewStudents() {
        System.out.println("\n-- Students --");
        List<Student> list = studentDAO.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-6s %-20s %-5s %-15s %-7s%n", "ID", "Name", "Age", "Course", "Marks");
        System.out.println("--------------------------------------------------------------");
        for (Student s : list) {
            System.out.printf("%-6d %-20s %-5d %-15s %-7.2f%n",
                    s.getStudentId(), s.getName(), s.getAge(), s.getCourse(), s.getMarks());
        }
    }

    private void updateStudent() {
        System.out.println("\n-- Update Student --");
        int id = readInt("Student ID: ");
        Student existing = studentDAO.getStudentById(id);
        if (existing == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("Editing: " + existing);

        String name = readOptionalString("Name [" + existing.getName() + "]: ");
        Integer age = readOptionalInt("Age [" + existing.getAge() + "]: ");
        String course = readOptionalString("Course [" + existing.getCourse() + "]: ");
        Double marks = readOptionalDouble("Marks [" + existing.getMarks() + "]: ");

        if (name != null && !name.isBlank()) existing.setName(name);
        if (age != null) existing.setAge(age);
        if (course != null && !course.isBlank()) existing.setCourse(course);
        if (marks != null) existing.setMarks(marks);

        if (studentDAO.updateStudent(existing)) {
            System.out.println("Updated: " + existing);
        } else {
            System.out.println("Failed to update student.");
        }
    }

    private void deleteStudent() {
        System.out.println("\n-- Delete Student --");
        int id = readInt("Student ID: ");
        String confirm = readString("Type 'YES' to confirm delete: ");
        if (!"YES".equalsIgnoreCase(confirm)) {
            System.out.println("Cancelled.");
            return;
        }
        if (studentDAO.deleteStudent(id)) {
            System.out.println("Deleted student #" + id);
        } else {
            System.out.println("Failed to delete student or not found.");
        }
    }

    private void markAttendance() {
        System.out.println("\n-- Mark Attendance --");
        int id = readInt("Student ID: ");
        LocalDate date = readOptionalDate("Date [today, yyyy-MM-dd]: ");
        if (date == null) {
            date = LocalDate.now();
        }
        String status = readString("Status (P/A): ");
        status = (status.equalsIgnoreCase("P")) ? "Present" : "Absent";

        if (attendanceDAO.markAttendance(id, date, status)) {
            System.out.println("Attendance saved.");
        } else {
            System.out.println("Failed to mark attendance.");
        }
    }

    private void viewAttendance() {
        System.out.println("\n-- View Attendance --");
        int id = readInt("Student ID: ");
        List<String> list = attendanceDAO.getAttendanceByStudent(id);
        if (list.isEmpty()) {
            System.out.println("No attendance records.");
            return;
        }
        System.out.println("Date - Status");
        for (String line : list) {
            System.out.println(line);
        }
    }

    private void generateReport() {
        System.out.println("\n-- Student Report --");
        viewStudents();
    }

    private void exportStudentsCsv() {
        System.out.println("\n-- Export Students to CSV --");
        List<Student> list = studentDAO.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No students to export.");
            return;
        }
        String fileName = "students_report.csv";
        try (FileWriter out = new FileWriter(fileName);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                     .withHeader("Student ID", "Name", "Age", "Course", "Marks"))) {
            for (Student s : list) {
                printer.printRecord(s.getStudentId(), s.getName(), s.getAge(), s.getCourse(), s.getMarks());
            }
            System.out.println("Exported to " + fileName);
        } catch (IOException e) {
            System.out.println("Failed to export: " + e.getMessage());
        }
    }

    // ---- Input helpers ----
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
        
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                String s = readString(prompt);
                return Integer.parseInt(s);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                String s = readString(prompt);
                return Double.parseDouble(s);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readOptionalString(String prompt) {
        System.out.print(prompt);
        String s = scanner.nextLine();
        return s.isBlank() ? null : s.trim();
    }

    private Integer readOptionalInt(String prompt) {
        String s = readOptionalString(prompt);
        if (s == null) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }

    private Double readOptionalDouble(String prompt) {
        String s = readOptionalString(prompt);
        if (s == null) return null;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }

    private LocalDate readOptionalDate(String prompt) {
        String s = readOptionalString(prompt);
        if (s == null) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Expected yyyy-MM-dd. Using today.");
            return LocalDate.now();
        }
    }
}
