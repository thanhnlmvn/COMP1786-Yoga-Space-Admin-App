package com.example.yogaadminapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "YogaAdmin.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TEACHERS = "teachers";
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";

    // Columns for classes table
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_CAPACITY = "capacity";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CLASS_TYPE = "class_type";
    private static final String COLUMN_TEACHER_NAME = "teacher_name";
    private static final String COLUMN_DESCRIPTION = "description";

    // Firebase Database reference
    private final DatabaseReference firebaseDatabaseRef;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TEACHERS_TABLE = "CREATE TABLE " + TABLE_TEACHERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT)";
        db.execSQL(CREATE_TEACHERS_TABLE);

        String CREATE_CLASSES_TABLE = "CREATE TABLE " + TABLE_CLASSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DATE + " TEXT," +
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    public void addTeacher(String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        long id = db.insert(TABLE_TEACHERS, null, values);
        db.close();

        if (id != -1) {
            firebaseDatabaseRef.child("teachers").child(String.valueOf(id)).setValue(new Teacher((int) id, name, email));
        }
    }

    public void addClass(String date, String time, int capacity, int duration, double price, String classType, String teacherName, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_CLASS_TYPE, classType);
        values.put(COLUMN_TEACHER_NAME, teacherName);
        values.put(COLUMN_DESCRIPTION, description);
        long id = db.insert(TABLE_CLASSES, null, values);
        db.close();

        if (id != -1) {
            firebaseDatabaseRef.child("classes").child(String.valueOf(id)).setValue(new YogaClass((int) id, date, time, capacity, duration, price, classType, teacherName, description));
        }
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> teacherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_TEACHERS, null);
            if (cursor.moveToFirst()) {
                do {
                    Teacher teacher = new Teacher();
                    teacher.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    teacher.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                    teacher.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                    teacherList.add(teacher);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return teacherList;
    }

    public List<String> getAllTeacherNames() {
        List<String> teacherNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_TEACHERS, null);
            if (cursor.moveToFirst()) {
                do {
                    teacherNames.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return teacherNames;
    }

    public void updateTeacher(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.update(TABLE_TEACHERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        firebaseDatabaseRef.child("teachers").child(String.valueOf(id)).setValue(new Teacher(id, name, email));
    }

    public void deleteTeacher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEACHERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        firebaseDatabaseRef.child("teachers").child(String.valueOf(id)).removeValue();
    }

    public YogaClass getClassById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLASSES, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);

        YogaClass yogaClass = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                yogaClass = new YogaClass(
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
            }
            cursor.close();
        }
        db.close();
        return yogaClass;
    }

    public List<YogaClass> getAllClasses() {
        List<YogaClass> classList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CLASSES, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
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
                    classList.add(yogaClass);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return classList;
    }

    public List<String> getAllClassTypes() {
        List<String> classTypes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_CLASS_TYPE + " FROM " + TABLE_CLASSES, null);
            if (cursor.moveToFirst()) {
                do {
                    classTypes.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return classTypes;
    }

    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLASSES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        firebaseDatabaseRef.child("classes").child(String.valueOf(id)).removeValue();
    }

    public void updateClass(int id, String date, String time, int capacity, int duration, double price, String classType, String teacherName, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_CAPACITY, capacity);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_CLASS_TYPE, classType);
        values.put(COLUMN_TEACHER_NAME, teacherName);
        values.put(COLUMN_DESCRIPTION, description);
        db.update(TABLE_CLASSES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();

        firebaseDatabaseRef.child("classes").child(String.valueOf(id)).setValue(new YogaClass(id, date, time, capacity, duration, price, classType, teacherName, description));
    }

    public void syncTeachersToFirebase() {
        List<Teacher> teacherList = getAllTeachers();
        for (Teacher teacher : teacherList) {
            firebaseDatabaseRef.child("teachers").child(String.valueOf(teacher.getId())).setValue(teacher);
        }
    }

    public void syncClassesToFirebase() {
        List<YogaClass> classList = getAllClasses();
        for (YogaClass yogaClass : classList) {
            firebaseDatabaseRef.child("classes").child(String.valueOf(yogaClass.getId())).setValue(yogaClass);
        }
    }
}
