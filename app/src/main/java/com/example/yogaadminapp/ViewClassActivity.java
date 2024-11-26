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
        firebaseDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allClasses.clear(); // Clear list to prevent duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                    if (yogaClass != null) {
                        yogaClass.setFirebaseId(snapshot.getKey()); // Set Firebase ID

                        // Check and assign bookedUsers if they exist
                        List<String> bookedUsers = new ArrayList<>();
                        DataSnapshot bookedUsersSnapshot = snapshot.child("BookedUsers");
                        for (DataSnapshot userSnapshot : bookedUsersSnapshot.getChildren()) {
                            bookedUsers.add(userSnapshot.getValue(String.class));
                        }
                        yogaClass.setBookedUsers(bookedUsers);

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

                editTextSearchTeacher.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Gọi filterClassesByTeacherName với giá trị hiện tại trong ô tìm kiếm
                        filterClassesByTeacherName(s.toString().trim());
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

    private void setupClassTypeSpinner() {
        firebaseDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> classTypes = new ArrayList<>();
                classTypes.add("All Types");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    YogaClass yogaClass = snapshot.getValue(YogaClass.class);
                    if (yogaClass != null && !classTypes.contains(yogaClass.getClassType())) {
                        classTypes.add(yogaClass.getClassType());
                    }
                }
                ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(ViewClassActivity.this, android.R.layout.simple_spinner_item, classTypes);
                classTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClassType.setAdapter(classTypeAdapter);

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

    private void filterClassesByTeacherName(String teacherName) {
        if (teacherName.isEmpty()) {
            // Nếu không nhập tên giáo viên, hiển thị tất cả các lớp
            updateListView(allClasses, "No classes available.");
            return;
        }

        // Lọc danh sách các lớp dựa trên tên giáo viên
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getTeacherName().equalsIgnoreCase(teacherName)) {
                filteredClasses.add(yogaClass);
            }
        }

        // Cập nhật danh sách hiển thị
        updateListView(filteredClasses, "No classes found for the specified teacher.");
    }

    private void filterClassesByType(String classType) {
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (classType.equals("All Types") || yogaClass.getClassType().equals(classType)) {
                filteredClasses.add(yogaClass);
            }
        }
        updateListView(filteredClasses, "No classes found for the selected type.");
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // Xử lý khi chọn ngày
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
                    editTextSearchDate.setText(selectedDate);
                    filterClassesByDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Xử lý khi người dùng nhấn "Cancel"
        datePickerDialog.setOnCancelListener(dialog -> {
            editTextSearchDate.setText(""); // Xóa nội dung trong ô tìm kiếm
            updateListView(allClasses, "No classes available."); // Hiển thị tất cả các lớp
        });

        datePickerDialog.show();
    }

    private void filterClassesByDate(String date) {
        if (date.isEmpty()) {
            // Nếu không nhập ngày, hiển thị tất cả các lớp
            updateListView(allClasses, "No classes available.");
            return;
        }

        // Lọc danh sách các lớp dựa trên ngày
        List<YogaClass> filteredClasses = new ArrayList<>();
        for (YogaClass yogaClass : allClasses) {
            if (yogaClass.getDate().equals(date)) {
                filteredClasses.add(yogaClass);
            }
        }

        // Cập nhật danh sách hiển thị
        updateListView(filteredClasses, "No classes found for the selected date.");
    }

    private void updateListView(List<YogaClass> filteredClasses, String emptyMessage) {
        adapter = new ClassAdapter(this, filteredClasses);
        listViewClasses.setAdapter(adapter);

        if (filteredClasses.isEmpty()) {
            Toast.makeText(this, emptyMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayFilteredClasses() {
        adapter = new ClassAdapter(this, allClasses);
        listViewClasses.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_CLASS_REQUEST || requestCode == EDIT_CLASS_REQUEST) && resultCode == RESULT_OK) {
            loadClassesFromFirebase(); // Reload data when a class is added/edited
        }
    }
}
