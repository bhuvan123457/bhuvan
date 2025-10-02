package com.example.sms;

/**
 * Model class representing a student.
 */
public class Student {
    private int studentId;
    private String name;
    private int age;
    private String course;
    private double marks;

    public Student() { }

    public Student(int studentId, String name, int age, String course, double marks) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.course = course;
        this.marks = marks;
    }

    public Student(String name, int age, String course, double marks) {
        this(0, name, age, course, marks);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s | age=%d | course=%s | marks=%.2f",
                studentId, name, age, course, marks);
    }
}
