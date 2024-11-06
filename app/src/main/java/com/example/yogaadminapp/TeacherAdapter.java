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
import java.util.ArrayList;
import java.util.List;


public class TeacherAdapter extends ArrayAdapter<Teacher> implements Filterable {

    private Context context;
    private DatabaseHelper databaseHelper;
    private List<Teacher> teacherList;
    private List<Teacher> teacherListFull; // Full list for filtering

    public TeacherAdapter(Context context, List<Teacher> teacherList) {
        super(context, R.layout.item_teacher, teacherList);
        this.context = context;
        this.teacherList = teacherList;
        this.teacherListFull = new ArrayList<>(teacherList); // Copy of the original list
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
                .setMessage("Are you sure you want to delete " + teacher.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseHelper.deleteTeacher(teacher.getId());
                    Toast.makeText(context, "Teacher deleted!", Toast.LENGTH_SHORT).show();
                    teacherList.remove(teacher);
                    notifyDataSetChanged();
                    if (context instanceof ViewTeacherActivity) {
                        ((ViewTeacherActivity) context).checkNoResults(); // Notify activity to check results
                    }
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
                    filteredList.addAll(teacherListFull); // No filter, return full list
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
                    ((ViewTeacherActivity) context).checkNoResults(); // Notify activity to check results
                }
            }
        };
    }
}
