package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonViewClass = findViewById(R.id.buttonViewClass);
        Button buttonViewTeacher = findViewById(R.id.buttonViewTeacher);
        Button buttonViewCustomer = findViewById(R.id.buttonViewCustomer); // Nút mới cho View Customers

        buttonViewClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewClassActivity.class);
                startActivity(intent);
            }
        });

        buttonViewTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewTeacherActivity.class);
                startActivity(intent);
            }
        });

        buttonViewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewCustomerActivity.class); // Mở ViewCustomerActivity
                startActivity(intent);
            }
        });
    }
}
