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
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
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

    public void deleteTeacher(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TEACHERS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateTeacher(int id, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_EMAIL, email);
        db.update(TABLE_TEACHERS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

}
