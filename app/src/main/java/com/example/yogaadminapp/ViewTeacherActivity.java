package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ViewTeacherActivity extends AppCompatActivity {

    public static final int ADD_TEACHER_REQUEST = 1;
    public static final int EDIT_TEACHER_REQUEST = 2; // Change this line to public
    private DatabaseHelper databaseHelper;
    private ListView listViewTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);

        Button buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        listViewTeachers = findViewById(R.id.listViewTeachers);
        databaseHelper = new DatabaseHelper(this);

        buttonAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTeacherActivity.this, AddTeacherActivity.class);
                startActivityForResult(intent, ADD_TEACHER_REQUEST);
            }
        });

        displayTeachers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            displayTeachers(); // Refresh the teacher list
        }
    }

    private void displayTeachers() {
        List<Teacher> teacherList = databaseHelper.getAllTeachers();
        TeacherAdapter adapter = new TeacherAdapter(this, teacherList);
        listViewTeachers.setAdapter(adapter);
    }
}
