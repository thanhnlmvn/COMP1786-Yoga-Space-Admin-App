package com.example.yogaadminapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends ArrayAdapter<Teacher> implements Filterable {

    private Context context;
    private DatabaseHelper databaseHelper;
    private List<Teacher> teacherList;
    private List<Teacher> teacherListFull;

    public TeacherAdapter(Context context, List<Teacher> teacherList) {
        super(context, R.layout.item_teacher, teacherList);
        this.context = context;
        this.teacherList = teacherList;
        this.teacherListFull = new ArrayList<>(teacherList);
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);

        Teacher teacher = teacherList.get(position);

        TextView textViewTeacherName = itemView.findViewById(R.id.textViewTeacherName);
        TextView textViewTeacherEmail = itemView.findViewById(R.id.textViewTeacherEmail);
        Button buttonDeleteTeacher = itemView.findViewById(R.id.buttonDeleteTeacher);
        Button buttonEditTeacher = itemView.findViewById(R.id.buttonEditTeacher);

        textViewTeacherName.setText(teacher.getName());
        textViewTeacherEmail.setText(teacher.getEmail());

        buttonDeleteTeacher.setOnClickListener(v -> showDeleteConfirmationDialog(teacher));
        buttonEditTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTeacherActivity.class);
            intent.putExtra("TEACHER_ID", teacher.getId());
            intent.putExtra("TEACHER_NAME", teacher.getName());
            intent.putExtra("TEACHER_EMAIL", teacher.getEmail());
            ((ViewTeacherActivity) context).startActivityForResult(intent, ViewTeacherActivity.EDIT_TEACHER_REQUEST);
        });

        return itemView;
    }

    private void showDeleteConfirmationDialog(Teacher teacher) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Teacher")
                .setMessage("Are you sure you want to delete " + teacher.getName() + "? This will also delete all related classes, bookings, and customer data.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference classesRef = FirebaseDatabase.getInstance().getReference("classes");
                    DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
                    DatabaseReference customersRef = FirebaseDatabase.getInstance().getReference("customers");

                    // Truy vấn để tìm tất cả các lớp học liên quan đến giáo viên
                    classesRef.orderByChild("teacherName").equalTo(teacher.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot classesSnapshot) {
                            for (DataSnapshot classSnapshot : classesSnapshot.getChildren()) {
                                String classId = classSnapshot.getKey();

                                // Xóa lớp học khỏi Firebase
                                classSnapshot.getRef().removeValue();

                                // Truy vấn và xóa tất cả bookings liên quan đến lớp học này
                                bookingsRef.orderByChild("ClassId").equalTo(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot bookingsSnapshot) {
                                        for (DataSnapshot bookingSnapshot : bookingsSnapshot.getChildren()) {
                                            String email = bookingSnapshot.child("Email").getValue(String.class);
                                            bookingSnapshot.getRef().removeValue(); // Xóa booking

                                            // Xóa thông tin lớp học từ node "customers"
                                            if (email != null) {
                                                String sanitizedEmail = email.replace(".", "_");
                                                customersRef.child(sanitizedEmail).child("BookedClasses").child(classId).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(context, "Failed to delete bookings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            // Sau khi xóa tất cả các lớp học, xóa giáo viên khỏi Firebase
                            databaseHelper.deleteTeacher(teacher.getId());
                            teacherList.remove(teacher);
                            notifyDataSetChanged();

                            Toast.makeText(context, "Teacher and all related data deleted successfully!", Toast.LENGTH_SHORT).show();
                            if (context instanceof ViewTeacherActivity) {
                                ((ViewTeacherActivity) context).checkNoResults();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, "Failed to delete classes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Teacher> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(teacherListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Teacher teacher : teacherListFull) {
                        if (teacher.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(teacher);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                teacherList.clear();
                teacherList.addAll((List<Teacher>) results.values);
                notifyDataSetChanged();
                if (context instanceof ViewTeacherActivity) {
                    ((ViewTeacherActivity) context).checkNoResults();
                }
            }
        };
    }
}
