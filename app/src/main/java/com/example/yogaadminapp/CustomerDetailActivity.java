package com.example.yogaadminapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
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

public class CustomerDetailActivity extends AppCompatActivity {

    private TextView textViewCustomerDetails;
    private DatabaseReference customerRef;
    private String firebaseKey;
    private String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        // Set up Back button
        buttonBack.setOnClickListener(v -> finish());

        textViewCustomerDetails = findViewById(R.id.textViewCustomerDetails);

        firebaseKey = getIntent().getStringExtra("FIREBASE_KEY");
        customerEmail = getIntent().getStringExtra("EMAIL");

        if (firebaseKey != null) {
            customerRef = FirebaseDatabase.getInstance().getReference("customers").child(firebaseKey).child("BookedClasses");
            loadBookedClasses();
        } else {
            Toast.makeText(this, "Error: Customer ID not found.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadBookedClasses() {
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> bookedClasses = new ArrayList<>();
                int totalClasses = 0; // Khởi tạo biến đếm

                for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                    String classType = classSnapshot.child("ClassType").getValue(String.class);
                    String date = classSnapshot.child("Date").getValue(String.class);
                    String price = String.valueOf(classSnapshot.child("Price").getValue());
                    String teacherName = classSnapshot.child("TeacherName").getValue(String.class);

                    if (classType != null && date != null && price != null && teacherName != null) {
                        bookedClasses.add("Class Type: " + classType + "\nDate: " + date + "\nPrice: £" + price + "\nTeacher: " + teacherName);
                        totalClasses++; // Tăng biến đếm cho mỗi lớp học
                    } else {
                        Log.e("CustomerDetailActivity", "One of the fields is null for a booked class.");
                    }
                }

                // Kiểm tra và hiển thị thông tin
                if (!bookedClasses.isEmpty()) {
                    String details = "Total Classes Booked: " + totalClasses + "\n\nBooked Classes for " + customerEmail + ":\n\n" + String.join("\n\n", bookedClasses);
                    textViewCustomerDetails.setText(details);
                } else {
                    textViewCustomerDetails.setText("No classes booked for " + customerEmail + ".");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CustomerDetailActivity.this, "Failed to load class details: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", "Error loading data", databaseError.toException());
            }
        });
    }

}
