package com.example.yogaadminapp;

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

    // Constructor không tham số (yêu cầu cho Firebase)
    public YogaClass() {
    }

    // Constructor nhận int id (dành cho SQLite)
    public YogaClass(int id, String date, String time, int capacity, int duration, double price, String classType, String teacherName, String description) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.classType = classType;
        this.teacherName = teacherName;
        this.description = description;
    }

    // Constructor nhận String firebaseId (dành cho Firebase)
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

    // Getters và setters
    public int getId() {
        return id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getDuration() {
        return duration;
    }

    public double getPrice() {
        return price;
    }

    public String getClassType() {
        return classType;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getDescription() {
        return description;
    }

    // Optional: Setters nếu bạn muốn cập nhật các thuộc tính
    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
