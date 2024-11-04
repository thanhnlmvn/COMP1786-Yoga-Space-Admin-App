package com.example.yogaadminapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ClassDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView textViewDate, textViewTime, textViewTeacher, textViewDescription, textViewCapacity, textViewDuration, textViewPrice, textViewClassType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        // Initialize views
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        textViewTeacher = findViewById(R.id.textViewTeacher);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewCapacity = findViewById(R.id.textViewCapacity);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewClassType = findViewById(R.id.textViewClassType);

        databaseHelper = new DatabaseHelper(this);

        // Get CLASS_ID from Intent
        int classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (classId != -1) {
            YogaClass yogaClass = databaseHelper.getClassById(classId);
            if (yogaClass != null) {
                // Populate the TextViews with data
                textViewDate.setText("Date: " + yogaClass.getDate());
                textViewTime.setText("Time: " + yogaClass.getTime());
                textViewTeacher.setText("Teacher: " + yogaClass.getTeacherName());
                textViewDescription.setText("Description: " + (yogaClass.getDescription().isEmpty() ? "N/A" : yogaClass.getDescription()));
                textViewCapacity.setText("Capacity: " + yogaClass.getCapacity());
                textViewDuration.setText("Duration: " + yogaClass.getDuration() + " mins");
                textViewPrice.setText("Price: $" + yogaClass.getPrice());
                textViewClassType.setText("Class Type: " + yogaClass.getClassType());
            } else {
                finish(); // Close the activity if class not found
            }
        } else {
            finish(); // Close the activity if no CLASS_ID was passed
        }
    }
}
