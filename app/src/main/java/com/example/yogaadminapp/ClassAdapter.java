package com.example.yogaadminapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ClassAdapter extends ArrayAdapter<YogaClass> {

    private Context context;
    private List<YogaClass> classList;
    private DatabaseHelper databaseHelper;

    public ClassAdapter(Context context, List<YogaClass> classList) {
        super(context, R.layout.item_class, classList);
        this.context = context;
        this.classList = classList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        }

        YogaClass yogaClass = classList.get(position);

        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        TextView textViewTeacher = convertView.findViewById(R.id.textViewTeacher);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        TextView textViewClassType = convertView.findViewById(R.id.textViewClassType);

        textViewDate.setText("Date: " + yogaClass.getDate());
        textViewTime.setText("Time: " + yogaClass.getTime());
        textViewTeacher.setText("Teacher: " + yogaClass.getTeacherName());
        textViewDescription.setText("Description: " + (yogaClass.getDescription().isEmpty() ? "N/A" : yogaClass.getDescription()));
        textViewPrice.setText("Price: $" + yogaClass.getPrice());
        textViewClassType.setText("Class Type: " + yogaClass.getClassType());

        // Handle Edit button
        Button buttonEditClass = convertView.findViewById(R.id.buttonEditClass);
        buttonEditClass.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditClassActivity.class);
            intent.putExtra("FIREBASE_ID", yogaClass.getFirebaseId());
            ((ViewClassActivity) context).startActivityForResult(intent, ViewClassActivity.EDIT_CLASS_REQUEST);
        });

        // Handle Detail button
        Button buttonDetail = convertView.findViewById(R.id.buttonDetail);
        buttonDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassDetailActivity.class);
            intent.putExtra("FIREBASE_ID", yogaClass.getFirebaseId());
            context.startActivity(intent);
        });

        // Handle Delete button
        Button buttonDeleteClass = convertView.findViewById(R.id.buttonDeleteClass);
        buttonDeleteClass.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String firebaseId = yogaClass.getFirebaseId();

                        // Xóa lớp học khỏi Firebase
                        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(firebaseId);
                        classRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Xóa tất cả các bookings liên quan
                                DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");
                                bookingRef.orderByChild("ClassId").equalTo(firebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                                            String bookingId = bookingSnapshot.getKey(); // ID của booking
                                            String email = bookingSnapshot.child("Email").getValue(String.class); // Lấy email từ booking
                                            bookingSnapshot.getRef().removeValue(); // Xóa booking khỏi Firebase

                                            // Xóa thông tin lớp học khỏi "customers"
                                            if (email != null) {
                                                String sanitizedEmail = email.replace(".", "_"); // Thay đổi email cho đúng định dạng Firebase
                                                DatabaseReference customerClassRef = FirebaseDatabase.getInstance()
                                                        .getReference("customers")
                                                        .child(sanitizedEmail)
                                                        .child("BookedClasses")
                                                        .child(firebaseId);
                                                customerClassRef.removeValue(); // Xóa lớp học khỏi khách hàng
                                            }
                                        }
                                        Toast.makeText(context, "All related bookings and customer data deleted.", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(context, "Failed to delete bookings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Xóa lớp khỏi cơ sở dữ liệu cục bộ
                                databaseHelper.deleteClass(firebaseId);

                                // Xóa lớp khỏi danh sách và cập nhật giao diện
                                classList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Class deleted successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to delete class: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}
