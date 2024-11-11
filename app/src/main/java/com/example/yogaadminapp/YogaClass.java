package com.example.yogaadminapp;

import java.util.List;

public class YogaClass {
    private int id;  // ID cho SQLite
    private String firebaseId;  // ID cho Firebase
    private String date;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String classType;
    private String teacherName;
    private String description;
    private List<String> bookedUsers; // Danh sách bookedUsers

    // Constructor không tham số (yêu cầu cho Firebase)
    public YogaClass() {
    }

    // Constructor nhận `firebaseId` là String thay vì int `id`
    public YogaClass(String firebaseId, String date, String time, int capacity, int duration, double price, String classType, String teacherName, String description) {
        this.firebaseId = firebaseId;
        this.date = date;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.teacherName = teacherName;
        this.description = description;
    }

    // Getters và Setters cho tất cả các thuộc tính
    public int getId() {
        return id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getBookedUsers() {
        return bookedUsers;
    }

    public void setBookedUsers(List<String> bookedUsers) {
        this.bookedUsers = bookedUsers;
    }
}
