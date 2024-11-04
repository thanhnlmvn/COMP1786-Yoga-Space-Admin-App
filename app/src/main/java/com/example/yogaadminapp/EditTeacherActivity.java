package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditTeacherActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private Button buttonUpdateTeacher;
    private int teacherId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonUpdateTeacher = findViewById(R.id.buttonUpdateTeacher);

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        teacherId = intent.getIntExtra("TEACHER_ID", -1);
        String name = intent.getStringExtra("TEACHER_NAME");
        String email = intent.getStringExtra("TEACHER_EMAIL");

        if (teacherId != -1) {
            editTextName.setText(name);
            editTextEmail.setText(email);
        }

        buttonUpdateTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = editTextName.getText().toString().trim();
                String updatedEmail = editTextEmail.getText().toString().trim();

                if (!updatedName.isEmpty() && !updatedEmail.isEmpty()) {
                    databaseHelper.updateTeacher(teacherId, updatedName, updatedEmail);
                    Toast.makeText(EditTeacherActivity.this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set result to OK
                    finish();
                } else {
                    Toast.makeText(EditTeacherActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
