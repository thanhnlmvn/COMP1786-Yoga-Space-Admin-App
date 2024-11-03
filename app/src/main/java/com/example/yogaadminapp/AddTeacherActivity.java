package com.example.yogaadminapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTeacherActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        databaseHelper = new DatabaseHelper(this);

        buttonAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty()) {
                    databaseHelper.addTeacher(name, email);
                    Toast.makeText(AddTeacherActivity.this, "Teacher added!", Toast.LENGTH_SHORT).show();

                    // Trả về kết quả cho ViewTeacherActivity
                    setResult(RESULT_OK);
                    finish(); // Trở về Activity trước đó
                } else {
                    Toast.makeText(AddTeacherActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
