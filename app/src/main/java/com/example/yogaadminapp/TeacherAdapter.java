package com.example.yogaadminapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

public class TeacherAdapter extends ArrayAdapter<Teacher> {

    private Context context;
    private DatabaseHelper databaseHelper;
    private List<Teacher> teacherList;

    public TeacherAdapter(Context context, List<Teacher> teacherList) {
        super(context, R.layout.item_teacher, teacherList);
        this.context = context;
        this.teacherList = teacherList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);

        Teacher teacher = teacherList.get(position);

        TextView textViewTeacherName = itemView.findViewById(R.id.textViewTeacherName);
        TextView textViewTeacherEmail = itemView.findViewById(R.id.textViewTeacherEmail);
        Button buttonDeleteTeacher = itemView.findViewById(R.id.buttonDeleteTeacher);

        textViewTeacherName.setText(teacher.getName());
        textViewTeacherEmail.setText(teacher.getEmail()); // Hiển thị email

        buttonDeleteTeacher.setOnClickListener(v -> showDeleteConfirmationDialog(teacher));

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
                })
                .setNegativeButton("No", null)
                .show();
    }
}
