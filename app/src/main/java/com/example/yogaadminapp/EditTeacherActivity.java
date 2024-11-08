package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditTeacherActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private int teacherId;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher);

        // Khởi tạo các view
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonUpdateTeacher = findViewById(R.id.buttonUpdateTeacher);
        ImageButton buttonBack = findViewById(R.id.buttonBack); // Thêm nút Back nếu có

        // Khởi tạo database helper
        databaseHelper = new DatabaseHelper(this);

        // Lấy thông tin giáo viên từ Intent
        Intent intent = getIntent();
        teacherId = intent.getIntExtra("TEACHER_ID", -1);
        String name = intent.getStringExtra("TEACHER_NAME");
        String email = intent.getStringExtra("TEACHER_EMAIL");

        // Hiển thị thông tin giáo viên nếu ID hợp lệ
        if (teacherId != -1) {
            editTextName.setText(name);
            editTextEmail.setText(email);
        } else {
            Toast.makeText(this, "Error: Teacher ID not found.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity nếu ID không hợp lệ
        }

        // Xử lý sự kiện nút cập nhật giáo viên
        buttonUpdateTeacher.setOnClickListener(v -> updateTeacherInfo());

        // Xử lý sự kiện nút Back
        buttonBack.setOnClickListener(v -> finish());
    }

    private void updateTeacherInfo() {
        String updatedName = editTextName.getText().toString().trim();
        String updatedEmail = editTextEmail.getText().toString().trim();

        if (validateInputs(updatedName, updatedEmail)) {
            databaseHelper.updateTeacher(teacherId, updatedName, updatedEmail);
            Toast.makeText(this, "Teacher updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Đặt kết quả là OK
            finish(); // Đóng Activity và quay lại màn hình trước đó
        }
    }

    private boolean validateInputs(String name, String email) {
        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            return false;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            return false;
        }
        return true;
    }
}
