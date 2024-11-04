package com.example.yogaadminapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaAdmin.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TEACHERS = "teachers";
    private static final String TABLE_CLASSES = "classes"; // Added constant for classes table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";

    // Columns for classes table
    private static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CLASS_TYPE = "class_type";
    private static final String COLUMN_TEACHER_NAME = "teacher_name";
    private static final String COLUMN_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEACHERS_TABLE = "CREATE TABLE " + TABLE_TEACHERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT)";
        db.execSQL(CREATE_TEACHERS_TABLE);

        // Create table for classes
        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DAY_OF_WEEK + " TEXT," +
                COLUMN_DATE + " TEXT," + // Include date column
                COLUMN_TIME + " TEXT," +
                COLUMN_CAPACITY + " INTEGER," +
                COLUMN_DURATION + " INTEGER," +
                COLUMN_PRICE + " REAL," +
                COLUMN_CLASS_TYPE + " TEXT," +
                COLUMN_TEACHER_NAME + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(CREATE_CLASSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES); // Drop classes table if exists
        onCreate(db);
    }
    public void addTeacher(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_TEACHERS, null, values);
        db.close();
    }
    public void addClass(String date, String time, int capacity, int duration, double price, String classType, String teacherName, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date); // Update column name if needed
        values.put("time", time);
        values.put("capacity", capacity);
        values.put("duration", duration);
        values.put("price", price);
        values.put("class_type", classType);
        values.put("teacher_name", teacherName);
        values.put("description", description);
        db.insert("classes", null, values);
        db.close();
    }
    public List<Teacher> getAllTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TEACHERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Teacher teacher = new Teacher();
                teacher.setId(cursor.getInt(0));
                teacher.setName(cursor.getString(1));
                teacher.setEmail(cursor.getString(2));
                teacherList.add(teacher);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return teacherList;
    }
    public void updateTeacher(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.update(TABLE_TEACHERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void deleteTeacher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEACHERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public YogaClass getClassById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                YogaClass yogaClass = new YogaClass(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                );
                return yogaClass;
            } catch (IllegalArgumentException e) {
                // Log or handle the exception if a column is not found
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return null;
    }
    public List<YogaClass> getAllClasses() {
        List<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("classes", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                YogaClass yogaClass = new YogaClass(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("capacity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("class_type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("teacher_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                classList.add(yogaClass);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return classList;
    }
}
