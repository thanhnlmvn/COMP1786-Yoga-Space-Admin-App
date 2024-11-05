package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

public class ViewClassActivity extends AppCompatActivity {

    public static final int ADD_CLASS_REQUEST = 1;
    public static final int EDIT_CLASS_REQUEST = 2; // Define EDIT_CLASS_REQUEST constant
    private DatabaseHelper databaseHelper;
    private ListView listViewClasses;
    private Button buttonAddClass;
    private AutoCompleteTextView editTextSearchTeacher; // Using AutoCompleteTextView for search
    private Button buttonSelectClassType; // Button to select class type
    private List<YogaClass> allClasses; // Store all classes to filter
    private List<String> teacherNames; // List of teacher names
    private List<String> classTypes; // List of class types

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);

        databaseHelper = new DatabaseHelper(this);
        listViewClasses = findViewById(R.id.listViewClasses);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        editTextSearchTeacher = findViewById(R.id.editTextSearchTeacher); // Initialize AutoCompleteTextView
        buttonSelectClassType = findViewById(R.id.buttonSelectClassType); // Initialize button for class type selection

        displayFilteredClasses(); // Display existing classes
        setupTeacherNames(); // Get teacher names and set up AutoCompleteTextView
        setupClassTypes(); // Setup class types from database

        buttonAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(ViewClassActivity.this, AddClassActivity.class);
            startActivityForResult(intent, ADD_CLASS_REQUEST);
        });

        buttonSelectClassType.setOnClickListener(v -> showClassTypeDialog());

        // Set up TextWatcher for search field
        editTextSearchTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClassesByTeacherName(s.toString()); // Filter classes as user types
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTeacherNames() {
        teacherNames = getTeacherNamesFromDatabase(); // Get the list of teacher names from the database
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, teacherNames);
        editTextSearchTeacher.setAdapter(teacherAdapter); // Set suggestions for AutoCompleteTextView
        editTextSearchTeacher.setThreshold(1); // Show suggestions after one character
    }

    private void setupClassTypes() {
        classTypes = databaseHelper.getAllClassTypes(); // Get class types from the database
    }

    private void showClassTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_select_class_type, null);
        builder.setView(dialogView);

        // Get the spinner from the dialog layout
        Spinner spinnerClassType = dialogView.findViewById(R.id.spinnerClassType);
        ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classTypes);
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(classTypeAdapter);

        // Handle selection of class type
        builder.setPositiveButton("OK", (dialog, which) -> {
            String selectedClassType = classTypes.get(spinnerClassType.getSelectedItemPosition());
            filterClassesByClassType(selectedClassType);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show dialog
        builder.create().show();
    }


    private void displayFilteredClasses() {
        allClasses = databaseHelper.getAllClasses(); // Get all classes
        if (allClasses.isEmpty()) {
            Toast.makeText(this, "No classes found.", Toast.LENGTH_SHORT).show();
        }
        ClassAdapter adapter = new ClassAdapter(this, allClasses);
        listViewClasses.setAdapter(adapter);
    }

    private void filterClassesByTeacherName(String query) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getTeacherName().toLowerCase().contains(query.toLowerCase())) {
                filteredClasses.add(yogaClass);
            }
        }

        ClassAdapter adapter = new ClassAdapter(this, filteredClasses);
        listViewClasses.setAdapter(adapter);

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, "No classes found for the specified teacher name.", Toast.LENGTH_SHORT).show();
        }
    }

    private void filterClassesByClassType(String classType) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getClassType().equals(classType)) {
                filteredClasses.add(yogaClass);
            }
        }

        ClassAdapter adapter = new ClassAdapter(this, filteredClasses);
        listViewClasses.setAdapter(adapter);

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, "No classes found for the selected class type.", Toast.LENGTH_SHORT).show();
        }
    }

    private List<String> getTeacherNamesFromDatabase() {
        List<Teacher> teachers = databaseHelper.getAllTeachers();
        List<String> teacherNames = new ArrayList<>();
        for (Teacher teacher : teachers) {
            teacherNames.add(teacher.getName());
        }
        return teacherNames;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CLASS_REQUEST && resultCode == RESULT_OK) {
            displayFilteredClasses(); // Update the class list
        } else if (requestCode == EDIT_CLASS_REQUEST && resultCode == RESULT_OK) {
            displayFilteredClasses(); // Update the list after editing the class
        }
    }
}
