package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ViewClassActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView listViewClasses;
    private ClassAdapter classAdapter;
    private Button buttonAddClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);

        listViewClasses = findViewById(R.id.listViewClasses);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        databaseHelper = new DatabaseHelper(this);

        buttonAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewClassActivity.this, AddClassActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        displayClasses();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Refresh the list when returning from AddClassActivity
            displayClasses();
        }
    }

    private void displayClasses() {
        List<YogaClass> classList = databaseHelper.getAllClasses();
        if (classList != null && !classList.isEmpty()) {
            classAdapter = new ClassAdapter(this, classList);
            listViewClasses.setAdapter(classAdapter);
        } else {
            Toast.makeText(this, "No classes available. Please add classes.", Toast.LENGTH_SHORT).show();
        }
    }
}
