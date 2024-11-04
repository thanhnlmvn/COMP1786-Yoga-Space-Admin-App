package com.example.yogaadminapp;

public class YogaClass {
    private int id;
    private String date;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String classType;
    private String teacherName;
    private String description;

    // Constructor
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

    // Getters and setters (optional)
    public int getId() {
        return id;
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
}
