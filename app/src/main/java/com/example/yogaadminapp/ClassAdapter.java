package com.example.yogaadminapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class ClassAdapter extends ArrayAdapter<YogaClass> {

    private Context context;
    private List<YogaClass> classList;

    public ClassAdapter(@NonNull Context context, @NonNull List<YogaClass> classList) {
        super(context, R.layout.item_class, classList);
        this.context = context;
        this.classList = classList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_class, parent, false);
        }

        YogaClass yogaClass = classList.get(position);

        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        TextView textViewTime = convertView.findViewById(R.id.textViewTime);
        TextView textViewTeacher = convertView.findViewById(R.id.textViewTeacher);
        TextView textViewDescription = convertView.findViewById(R.id.textViewDescription);
        Button buttonDetail = convertView.findViewById(R.id.buttonDetail);

        textViewDate.setText("Date: " + yogaClass.getDate());
        textViewTime.setText("Time: " + yogaClass.getTime());
        textViewTeacher.setText("Teacher: " + yogaClass.getTeacherName());
        textViewDescription.setText("Description: " + (yogaClass.getDescription().isEmpty() ? "N/A" : yogaClass.getDescription()));

        buttonDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, ClassDetailActivity.class);
            intent.putExtra("CLASS_ID", yogaClass.getId());
            context.startActivity(intent);
        });

        return convertView;
    }
}
