package com.example.yogaadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewClassActivity extends AppCompatActivity {

    public static final int ADD_CLASS_REQUEST = 1;
    public static final int EDIT_CLASS_REQUEST = 2;
    private DatabaseHelper databaseHelper;
    private ListView listViewClasses;
    private Button buttonAddClass;
    private AutoCompleteTextView editTextSearchTeacher;
    private EditText editTextSearchDate;
    private Spinner spinnerClassType; // Spinner for class type selection
    private List<YogaClass> allClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);

        databaseHelper = new DatabaseHelper(this);
        listViewClasses = findViewById(R.id.listViewClasses);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        editTextSearchTeacher = findViewById(R.id.editTextSearchTeacher);
        editTextSearchDate = findViewById(R.id.editTextSearchDate);
        spinnerClassType = findViewById(R.id.spinnerClassType);

        // Load all classes from database
        allClasses = databaseHelper.getAllClasses();
        displayFilteredClasses();

        // Set up Add Class button
        buttonAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(ViewClassActivity.this, AddClassActivity.class);
            startActivityForResult(intent, ADD_CLASS_REQUEST);
        });

        // Set up DatePickerDialog for date search
        editTextSearchDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up TextWatcher for teacher name search
        editTextSearchTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClassesByTeacherName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up Spinner for class type selection
        setupClassTypeSpinner();
    }

    // Method to set up class type spinner
    private void setupClassTypeSpinner() {
        List<String> classTypes = databaseHelper.getAllClassTypes();
        classTypes.add(0, "All Types"); // Add option for all types

        ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classTypes);
        classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassType.setAdapter(classTypeAdapter);

        spinnerClassType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                filterClassesByType(selectedType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Method to display DatePickerDialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String selectedDate = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
            editTextSearchDate.setText(selectedDate);
            filterClassesByDate(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Method to filter classes by date
    private void filterClassesByDate(String date) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getDate().equals(date)) {
                filteredClasses.add(yogaClass);
            }
        }
        updateListView(filteredClasses, "No classes found for the selected date.");
    }

    // Method to filter classes by teacher name
    private void filterClassesByTeacherName(String query) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getTeacherName().toLowerCase().contains(query.toLowerCase())) {
                filteredClasses.add(yogaClass);
            }
        }
        updateListView(filteredClasses, "No classes found for the specified teacher name.");
    }

    // Method to filter classes by class type
    private void filterClassesByType(String classType) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (classType.equals("All Types") || yogaClass.getClassType().equals(classType)) {
                filteredClasses.add(yogaClass);
            }
        }
        updateListView(filteredClasses, "No classes found for the selected type.");
    }

    // Method to update ListView and show toast if empty
    private void updateListView(List<YogaClass> filteredClasses, String emptyMessage) {
        ClassAdapter adapter = new ClassAdapter(this, filteredClasses);
        listViewClasses.setAdapter(adapter);

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, emptyMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display all classes
    private void displayFilteredClasses() {
        ClassAdapter adapter = new ClassAdapter(this, allClasses);
        listViewClasses.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_CLASS_REQUEST || requestCode == EDIT_CLASS_REQUEST) && resultCode == RESULT_OK) {
            allClasses = databaseHelper.getAllClasses();
            displayFilteredClasses();
        }
    }
}
