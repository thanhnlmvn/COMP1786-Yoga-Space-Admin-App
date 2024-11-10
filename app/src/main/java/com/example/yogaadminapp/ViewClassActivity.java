package com.example.yogaadminapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class ViewClassActivity extends AppCompatActivity {

    public static final int ADD_CLASS_REQUEST = 1;
    public static final int EDIT_CLASS_REQUEST = 2;
    private DatabaseReference firebaseDatabaseRef;
    private ListView listViewClasses;
    private Button buttonAddClass;
    private AutoCompleteTextView editTextSearchTeacher;
    private EditText editTextSearchDate;
    private Spinner spinnerClassType;
    private List<YogaClass> allClasses;
    private ClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);

        // Initialize Firebase reference
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("classes");

        listViewClasses = findViewById(R.id.listViewClasses);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        editTextSearchTeacher = findViewById(R.id.editTextSearchTeacher);
        editTextSearchDate = findViewById(R.id.editTextSearchDate);
        spinnerClassType = findViewById(R.id.spinnerClassType);

        // Initialize empty list and adapter
        allClasses = new ArrayList<>();
        adapter = new ClassAdapter(this, allClasses);
        listViewClasses.setAdapter(adapter);

        // Load all classes from Firebase
        loadClassesFromFirebase();

        // Set up Add Class button
        buttonAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(ViewClassActivity.this, AddClassActivity.class);
            startActivityForResult(intent, ADD_CLASS_REQUEST);
        });

        // Set up DatePickerDialog for date search
        editTextSearchDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up teacher name suggestions and filtering
        setupTeacherNameSuggestions();

        // Set up class type spinner and filtering
        setupClassTypeSpinner();
    }

    private void loadClassesFromFirebase() {
        firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allClasses.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                    if (yogaClass != null) {
                        allClasses.add(yogaClass);
                    }
                }
                displayFilteredClasses(); // Display data after loading from Firebase
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewClassActivity.this, "Failed to load classes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading data: ", databaseError.toException());
            }
        });
    }

    // Method to set up teacher name suggestions and add TextWatcher for filtering
    private void setupTeacherNameSuggestions() {
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        teachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> teacherNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teacherNames.add(teacher.getName());
                    }
                }
                ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(ViewClassActivity.this, android.R.layout.simple_dropdown_item_1line, teacherNames);
                editTextSearchTeacher.setAdapter(teacherAdapter);
                editTextSearchTeacher.setThreshold(1);

                // Add TextWatcher to filter classes by teacher name
                editTextSearchTeacher.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        filterClassesByTeacherName(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewClassActivity.this, "Failed to load teacher names: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to set up class type spinner and add OnItemSelectedListener for filtering
    private void setupClassTypeSpinner() {
        firebaseDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> classTypes = new ArrayList<>();
                classTypes.add("All Types"); // Default option
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                    if (yogaClass != null && !classTypes.contains(yogaClass.getClassType())) {
                        classTypes.add(yogaClass.getClassType());
                    }
                }
                ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(ViewClassActivity.this, android.R.layout.simple_spinner_item, classTypes);
                classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClassType.setAdapter(classTypeAdapter);

                // Add OnItemSelectedListener to filter classes by type
                spinnerClassType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        filterClassesByType(parent.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewClassActivity.this, "Failed to load class types: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to filter classes by teacher name
    private void filterClassesByTeacherName(String teacherName) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getTeacherName().equalsIgnoreCase(teacherName)) {
                filteredClasses.add(yogaClass);
            }
        }
        updateListView(filteredClasses, "No classes found for the specified teacher.");
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

    // Method to update ListView and show toast if empty
    private void updateListView(List<YogaClass> filteredClasses, String emptyMessage) {
        adapter = new ClassAdapter(this, filteredClasses);
        listViewClasses.setAdapter(adapter);

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, emptyMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to display all classes
    private void displayFilteredClasses() {
        adapter = new ClassAdapter(this, allClasses);
        listViewClasses.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_CLASS_REQUEST || requestCode == EDIT_CLASS_REQUEST) && resultCode == RESULT_OK) {
            loadClassesFromFirebase();
        }
    }
}
