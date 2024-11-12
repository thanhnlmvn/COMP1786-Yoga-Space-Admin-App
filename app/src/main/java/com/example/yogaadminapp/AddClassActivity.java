package com.example.yogaadminapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextCapacity, editTextDuration, editTextPrice, editTextDescription;
    private Spinner spinnerClassType;
    private AutoCompleteTextView autoCompleteTeacherName;
    private List<String> teacherNames;

    // Firebase Database reference
    private DatabaseReference firebaseDatabaseRef;
    private DatabaseReference teachersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        // Initialize views
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerClassType = findViewById(R.id.spinnerClassType);
        autoCompleteTeacherName = findViewById(R.id.autoCompleteTeacherName);
        Button buttonAddClass = findViewById(R.id.buttonAddClass);

        // Initialize Firebase reference
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("classes");
        teachersRef = FirebaseDatabase.getInstance().getReference("teachers");

        // Set up Back button
        buttonBack.setOnClickListener(v -> finish());

        // Get teacher names from Firebase
        getTeacherNamesFromFirebase();

        // Set up spinner with class types
        setupClassTypeSpinner();

        // Set up DatePickerDialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up TimePickerDialog
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        // Handle button click
        buttonAddClass.setOnClickListener(v -> addClass());
    }

    private void addClass() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String capacityStr = editTextCapacity.getText().toString().trim();
        String durationStr = editTextDuration.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String classType = spinnerClassType.getSelectedItem().toString();
        String selectedTeacher = autoCompleteTeacherName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (validateInputs(date, time, capacityStr, durationStr, priceStr, selectedTeacher)) {
            int capacity = Integer.parseInt(capacityStr);
            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr);

            // Save to Firebase with auto-generated ID
            String classId = firebaseDatabaseRef.push().getKey();
            if (classId != null) {
                YogaClass newClass = new YogaClass(classId, date, time, capacity, duration, price, classType, selectedTeacher, description);
                firebaseDatabaseRef.child(classId).setValue(newClass)
                        .addOnSuccessListener(aVoid -> Toast.makeText(AddClassActivity.this, "Class added to Firebase successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AddClassActivity.this, "Failed to add class to Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            Toast.makeText(AddClassActivity.this, "Class added successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Notify that the class was added
            finish(); // Return to the previous activity
        }
    }

    private boolean validateInputs(String date, String time, String capacityStr, String durationStr, String priceStr, String selectedTeacher) {
        boolean valid = true;

        if (date.isEmpty()) {
            editTextDate.setError("Date is required");
            valid = false;
        }

        if (time.isEmpty()) {
            editTextTime.setError("Time is required");
            valid = false;
        }

        if (capacityStr.isEmpty()) {
            editTextCapacity.setError("Capacity is required");
            valid = false;
        }

        if (durationStr.isEmpty()) {
            editTextDuration.setError("Duration is required");
            valid = false;
        }

        if (priceStr.isEmpty()) {
            editTextPrice.setError("Price is required");
            valid = false;
        }

        if (selectedTeacher.isEmpty()) {
            autoCompleteTeacherName.setError("Teacher name is required");
            valid = false;
        } else if (!teacherNames.contains(selectedTeacher)) {
            autoCompleteTeacherName.setError("Please select a valid teacher from the list");
            Toast.makeText(AddClassActivity.this, "Invalid teacher name. Please select from the suggestions.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void getTeacherNamesFromFirebase() {
        teacherNames = new ArrayList<>();
        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null && teacher.getName() != null) {
                        teacherNames.add(teacher.getName());
                    }
                }
                setupTeacherNameAutoComplete(); // Set up AutoCompleteTextView with teacher names
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddClassActivity.this, "Failed to load teacher names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTeacherNameAutoComplete() {
        if (!teacherNames.isEmpty()) {
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, teacherNames);
            autoCompleteTeacherName.setAdapter(teacherAdapter);
            autoCompleteTeacherName.setThreshold(1); // Start showing suggestions after one character
        } else {
            Toast.makeText(this, "No teachers available. Please add teachers first.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no teacher is available
        }
    }

    private void setupClassTypeSpinner() {
        ArrayAdapter<CharSequence> classTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_types, android.R.layout.simple_spinner_item);
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(classTypeAdapter);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            String selectedDate = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
            editTextDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            editTextTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }
}
