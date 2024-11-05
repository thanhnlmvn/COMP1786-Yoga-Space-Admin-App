package com.example.yogaadminapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditClassActivity extends AppCompatActivity {

    private EditText editTextDate, editTextTime, editTextCapacity, editTextDuration, editTextPrice, editTextDescription;
    private Spinner spinnerClassType;
    private AutoCompleteTextView autoCompleteTeacherName;
    private DatabaseHelper databaseHelper;
    private int classId;
    private List<String> teacherNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Initialize views
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextCapacity = findViewById(R.id.editTextCapacity);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerClassType = findViewById(R.id.spinnerClassType);
        autoCompleteTeacherName = findViewById(R.id.autoCompleteTeacherName);
        Button buttonUpdateClass = findViewById(R.id.buttonUpdateClass);

        databaseHelper = new DatabaseHelper(this);

        // Get teacher names from the database
        teacherNames = getTeacherNamesFromDatabase();
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, teacherNames);
        autoCompleteTeacherName.setAdapter(teacherAdapter);

        // Get classId from Intent
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (classId != -1) {
            loadClassDetails(classId);
        }

        // Set up DatePickerDialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up TimePickerDialog
        editTextTime.setOnClickListener(v -> showTimePickerDialog());

        // Handle update button click
        buttonUpdateClass.setOnClickListener(v -> updateClass());
    }

    private void loadClassDetails(int id) {
        YogaClass yogaClass = databaseHelper.getClassById(id);
        if (yogaClass != null) {
            editTextDate.setText(yogaClass.getDate());
            editTextTime.setText(yogaClass.getTime());
            editTextCapacity.setText(String.valueOf(yogaClass.getCapacity()));
            editTextDuration.setText(String.valueOf(yogaClass.getDuration()));
            editTextPrice.setText(String.valueOf(yogaClass.getPrice()));
            editTextDescription.setText(yogaClass.getDescription());
            autoCompleteTeacherName.setText(yogaClass.getTeacherName()); // Set current teacher name

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.class_types, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClassType.setAdapter(adapter);

            int spinnerPosition = adapter.getPosition(yogaClass.getClassType());
            spinnerClassType.setSelection(spinnerPosition);
        }
    }

    private void updateClass() {
        String date = editTextDate.getText().toString().trim();
        String time = editTextTime.getText().toString().trim();
        int capacity = Integer.parseInt(editTextCapacity.getText().toString().trim());
        int duration = Integer.parseInt(editTextDuration.getText().toString().trim());
        double price = Double.parseDouble(editTextPrice.getText().toString().trim());
        String classType = spinnerClassType.getSelectedItem().toString();
        String teacherName = autoCompleteTeacherName.getText().toString().trim(); // Get teacher name from AutoCompleteTextView
        String description = editTextDescription.getText().toString().trim();

        databaseHelper.updateClass(classId, date, time, capacity, duration, price, classType, teacherName, description);
        Toast.makeText(this, "Class updated successfully", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    private List<String> getTeacherNamesFromDatabase() {
        List<Teacher> teacherList = databaseHelper.getAllTeachers();
        List<String> names = new ArrayList<>();
        for (Teacher teacher : teacherList) {
            names.add(teacher.getName());
        }
        return names;
    }

    // Show DatePickerDialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            calendar.set(year1, month1, dayOfMonth);
            String selectedDate = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime()); // Format the date with the day of the week
            editTextDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    // Show TimePickerDialog
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute1); // Format the time as needed
            editTextTime.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }
}
