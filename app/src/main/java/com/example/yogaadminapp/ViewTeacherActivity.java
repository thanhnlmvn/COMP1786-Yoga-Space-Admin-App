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
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ViewTeacherActivity extends AppCompatActivity {

    public static final int ADD_TEACHER_REQUEST = 1;

    public static final int EDIT_TEACHER_REQUEST = 2;


    private DatabaseHelper databaseHelper;
    private ListView listViewTeachers;
    private TeacherAdapter adapter;
    private EditText editTextSearch;
    private TextView textViewNoResults; // TextView for no results message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher);

        Button buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        listViewTeachers = findViewById(R.id.listViewTeachers);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewNoResults = findViewById(R.id.textViewNoResults);

        databaseHelper = new DatabaseHelper(this);
        displayTeachers();

        buttonAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTeacherActivity.this, AddTeacherActivity.class);
                startActivityForResult(intent, ADD_TEACHER_REQUEST);
            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            displayTeachers(); // Refresh the teacher list
        }
    }

    private void displayTeachers() {
        List<Teacher> teacherList = databaseHelper.getAllTeachers();
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
