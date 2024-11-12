package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ViewTeacherActivity extends AppCompatActivity {

    public static final int ADD_TEACHER_REQUEST = 1;
    public static final int EDIT_TEACHER_REQUEST = 2;

    private DatabaseReference teachersRef;
    private ListView listViewTeachers;
    private TeacherAdapter adapter;
    private EditText editTextSearch;
    private TextView textViewNoResults;
    private List<Teacher> teacherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);

        teachersRef = FirebaseDatabase.getInstance().getReference("teachers");

        Button buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        listViewTeachers = findViewById(R.id.listViewTeachers);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewNoResults = findViewById(R.id.textViewNoResults);

        teacherList = new ArrayList<>();
        loadTeachersFromFirebase();

        buttonAddTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(ViewTeacherActivity.this, AddTeacherActivity.class);
            startActivityForResult(intent, ADD_TEACHER_REQUEST);
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadTeachersFromFirebase(); // Refresh the teacher list
        }
    }

    private void loadTeachersFromFirebase() {
        teachersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teacherList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    if (teacher != null) {
                        teacherList.add(teacher);
                    }
                }
                displayTeachers(); // Display teachers after loading from Firebase
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewTeacherActivity.this, "Failed to load teachers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTeachers() {
        adapter = new TeacherAdapter(this, teacherList);
        listViewTeachers.setAdapter(adapter);
        checkNoResults(); // Check for results after displaying teachers
    }

    // Check if there are no results
    protected void checkNoResults() {
        if (adapter.getCount() == 0) {
            textViewNoResults.setVisibility(View.VISIBLE);
        } else {
            textViewNoResults.setVisibility(View.GONE);
        }
    }
}
