package com.example.yogaadminapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClassDetailActivity extends AppCompatActivity {

    private DatabaseReference firebaseDatabaseRef;
    private TextView textViewDate, textViewTime, textViewTeacher, textViewDescription, textViewCapacity, textViewDuration, textViewPrice, textViewClassType;
    private String firebaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        // Initialize Firebase reference
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("classes");

        // Initialize views
        initializeViews();

        // Get firebaseId from Intent and load class details
        firebaseId = getIntent().getStringExtra("FIREBASE_ID");
        if (firebaseId != null && !firebaseId.isEmpty()) {
            loadClassDetails(firebaseId);
        } else {
            Toast.makeText(this, "Error: Class ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up Back button functionality
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        textViewTeacher = findViewById(R.id.textViewTeacher);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewCapacity = findViewById(R.id.textViewCapacity);
        textViewDuration = findViewById(R.id.textViewDuration);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewClassType = findViewById(R.id.textViewClassType);
    }

    private void loadClassDetails(String firebaseId) {
        firebaseDatabaseRef.child(firebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                if (yogaClass != null) {
                    textViewDate.setText("Date: " + yogaClass.getDate());
                    textViewTime.setText("Time: " + yogaClass.getTime());
                    textViewTeacher.setText("Teacher: " + yogaClass.getTeacherName());
                    textViewDescription.setText("Description: " + (yogaClass.getDescription().isEmpty() ? "N/A" : yogaClass.getDescription()));
                    textViewCapacity.setText("Capacity: " + yogaClass.getCapacity());
                    textViewDuration.setText("Duration: " + yogaClass.getDuration() + " mins");
                    textViewPrice.setText("Price: $" + yogaClass.getPrice());
                    textViewClassType.setText("Class Type: " + yogaClass.getClassType());
                } else {
                    Toast.makeText(ClassDetailActivity.this, "Error: Class not found.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ClassDetailActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading data: ", databaseError.toException());
            }
        });
    }
}