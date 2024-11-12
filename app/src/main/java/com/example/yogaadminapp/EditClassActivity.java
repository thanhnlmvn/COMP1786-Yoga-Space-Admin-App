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

public class EditClassActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextCapacity, editTextDuration, editTextPrice, editTextDescription;
    private Spinner spinnerClassType;
    private AutoCompleteTextView autoCompleteTeacherName;
    private DatabaseReference firebaseDatabaseRef;
    private String firebaseId;
    private List<String> teacherNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Initialize Firebase reference
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("classes");

        // Initialize views
        initializeViews();

        // Load teacher names from the database
        loadTeacherNames();

        // Get firebaseId from Intent and load class details
        firebaseId = getIntent().getStringExtra("FIREBASE_ID");
        if (firebaseId != null && !firebaseId.isEmpty()) {
            loadClassDetails(firebaseId);
        } else {
            Toast.makeText(this, "Error: Firebase ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up DatePickerDialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up TimePickerDialog
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        // Handle update button click
        findViewById(R.id.buttonUpdateClass).setOnClickListener(v -> updateClass());

        // Set up Back button functionality
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerClassType = findViewById(R.id.spinnerClassType);
        autoCompleteTeacherName = findViewById(R.id.autoCompleteTeacherName);
    }

    private void loadTeacherNames() {
        teacherNames = new ArrayList<>();
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teacherNames.add(teacher.getName());
                    }
                }
                ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(EditClassActivity.this, android.R.layout.simple_dropdown_item_1line, teacherNames);
                autoCompleteTeacherName.setAdapter(teacherAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditClassActivity.this, "Failed to load teacher names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClassDetails(String firebaseId) {
        firebaseDatabaseRef.child(firebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                if (yogaClass != null) {
                    editTextDate.setText(yogaClass.getDate());
                    editTextTime.setText(yogaClass.getTime());
                    editTextCapacity.setText(String.valueOf(yogaClass.getCapacity()));
                    editTextDuration.setText(String.valueOf(yogaClass.getDuration()));
                    editTextPrice.setText(String.valueOf(yogaClass.getPrice()));
                    editTextDescription.setText(yogaClass.getDescription());
                    autoCompleteTeacherName.setText(yogaClass.getTeacherName());

                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditClassActivity.this, R.array.class_types, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClassType.setAdapter(adapter);

                    int spinnerPosition = adapter.getPosition(yogaClass.getClassType());
                    spinnerClassType.setSelection(spinnerPosition);
                } else {
                    Toast.makeText(EditClassActivity.this, "Class details not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditClassActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClass() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        String capacityStr = editTextCapacity.getText().toString().trim();
        String durationStr = editTextDuration.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String classType = spinnerClassType.getSelectedItem().toString();
        String teacherName = autoCompleteTeacherName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (validateInputs(date, time, capacityStr, durationStr, priceStr, teacherName)) {
            int capacity = Integer.parseInt(capacityStr);
            int duration = Integer.parseInt(durationStr);
            double price = Double.parseDouble(priceStr);

            YogaClass updatedClass = new YogaClass(firebaseId, date, time, capacity, duration, price, classType, teacherName, description);
            firebaseDatabaseRef.child(firebaseId).setValue(updatedClass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditClassActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditClassActivity.this, "Failed to update class: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private boolean validateInputs(String date, String time, String capacityStr, String durationStr, String priceStr, String teacherName) {
        if (date.isEmpty()) {
            editTextDate.setError("Date is required");
            return false;
        }
        if (time.isEmpty()) {
            editTextTime.setError("Time is required");
            return false;
        }
        if (capacityStr.isEmpty()) {
            editTextCapacity.setError("Capacity is required");
            return false;
        }
        if (durationStr.isEmpty()) {
            editTextDuration.setError("Duration is required");
            return false;
        }
        if (priceStr.isEmpty()) {
            editTextPrice.setError("Price is required");
            return false;
        }
        if (teacherName.isEmpty()) {
            autoCompleteTeacherName.setError("Teacher name is required");
            return false;
        } else if (!teacherNames.contains(teacherName)) {
            autoCompleteTeacherName.setError("Please select a valid teacher");
            Toast.makeText(this, "Invalid teacher name. Please select from suggestions.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
