package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ViewTeacherActivity extends AppCompatActivity {

    private static final int ADD_TEACHER_REQUEST = 1; // Request code for Intent
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
                startActivityForResult(intent, ADD_TEACHER_REQUEST); // Start AddTeacherActivity
            }
        });

        // Display the list of teachers
        displayTeachers();
    }

    // Called back when returning from AddTeacherActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TEACHER_REQUEST && resultCode == RESULT_OK) {
            displayTeachers(); // Refresh the teacher list
        }
    }

    private void displayTeachers() {
        List<Teacher> teacherList = databaseHelper.getAllTeachers();
        TeacherAdapter adapter = new TeacherAdapter(this, teacherList);
        listViewTeachers.setAdapter(adapter);
    }
}
