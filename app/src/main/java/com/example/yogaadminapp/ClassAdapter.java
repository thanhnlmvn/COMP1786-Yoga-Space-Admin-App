package com.example.yogaadminapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
            intent.putExtra("CLASS_ID", yogaClass.getId());
            ((ViewClassActivity) context).startActivityForResult(intent, ViewClassActivity.EDIT_CLASS_REQUEST);
        });

        // Handle Detail button
        Button buttonDetail = convertView.findViewById(R.id.buttonDetail);
        buttonDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassDetailActivity.class);
            intent.putExtra("CLASS_ID", yogaClass.getId());
            context.startActivity(intent);
        });

        // Handle Delete button
        Button buttonDeleteClass = convertView.findViewById(R.id.buttonDeleteClass);
        buttonDeleteClass.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseHelper.deleteClass(yogaClass.getId());
                        classList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Class deleted successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return convertView;
    }
}
