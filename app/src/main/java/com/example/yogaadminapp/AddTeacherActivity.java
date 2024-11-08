package com.example.yogaadminapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

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
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        databaseHelper = new DatabaseHelper(this);

        // Xử lý sự kiện khi nhấn nút Add Teacher
        buttonAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    String name = editTextName.getText().toString().trim();
                    String email = editTextEmail.getText().toString().trim();

                    databaseHelper.addTeacher(name, email); // Thêm giáo viên vào cơ sở dữ liệu
                    Toast.makeText(AddTeacherActivity.this, "Teacher added!", Toast.LENGTH_SHORT).show();

                    // Quay lại Activity trước đó
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút Back
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Gọi phương thức quay lại trang trước đó
            }
        });
    }

    // Phương thức kiểm tra đầu vào
    private boolean validateInputs() {
        boolean isValid = true;

        // Kiểm tra nếu tên trống
        if (editTextName.getText().toString().trim().isEmpty()) {
            editTextName.setError("Name is required");
            isValid = false;
        } else {
            editTextName.setError(null); // Xóa lỗi nếu đã có dữ liệu
        }

        // Kiểm tra nếu email trống
        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            isValid = false;
        } else if (!isValidEmail(email)) {
            editTextEmail.setError("Invalid email format");
            isValid = false;
        } else {
            editTextEmail.setError(null); // Xóa lỗi nếu email hợp lệ
        }

        return isValid;
    }

    // Phương thức kiểm tra định dạng email hợp lệ
    private boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }
}
