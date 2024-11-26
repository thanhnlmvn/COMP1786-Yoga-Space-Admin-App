package com.example.yogaadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCustomerActivity extends AppCompatActivity {

    private ListView listViewCustomers;
    private AutoCompleteTextView autoCompleteTextViewSearchEmail;
    private DatabaseReference customersRef;
    private List<String> customerEmails;
    private Map<String, String> emailToFirebaseKeyMap; // Map để lưu trữ Firebase Key cho mỗi email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);

        listViewCustomers = findViewById(R.id.listViewCustomers);
        autoCompleteTextViewSearchEmail = findViewById(R.id.autoCompleteTextViewSearchEmail);
        customersRef = FirebaseDatabase.getInstance().getReference("customers");

        customerEmails = new ArrayList<>();
        emailToFirebaseKeyMap = new HashMap<>();

        loadCustomerEmails();

        // Set up TextWatcher for AutoCompleteTextView to implement real-time search
        autoCompleteTextViewSearchEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Do nothing here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Call the search function whenever the text changes
                String query = charSequence.toString().toLowerCase();
                filterCustomers(query);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing here
            }
        });
    }

    private void loadCustomerEmails() {
        customersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customerEmails.clear();
                emailToFirebaseKeyMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.getKey().replace("_com", ".com");
                    customerEmails.add(email);
                    emailToFirebaseKeyMap.put(email, snapshot.getKey());
                }

                // Set adapter for AutoCompleteTextView
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(ViewCustomerActivity.this,
                        android.R.layout.simple_dropdown_item_1line, customerEmails);
                autoCompleteTextViewSearchEmail.setAdapter(emailAdapter);

                // Set adapter for ListView
                CustomerAdapter adapter = new CustomerAdapter(customerEmails);
                listViewCustomers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewCustomerActivity.this, "Failed to load customer emails: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCustomers(String query) {
        List<String> filteredEmails = new ArrayList<>();
        for (String email : customerEmails) {
            if (email.toLowerCase().contains(query)) {
                filteredEmails.add(email);
            }
        }

        // Update the ListView with the filtered emails
        CustomerAdapter adapter = new CustomerAdapter(filteredEmails);
        listViewCustomers.setAdapter(adapter);
    }

    private class CustomerAdapter extends android.widget.BaseAdapter {
        private List<String> emails;

        public CustomerAdapter(List<String> emails) {
            this.emails = emails;
        }

        @Override
        public int getCount() {
            return emails.size();
        }

        @Override
        public Object getItem(int position) {
            return emails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.customer_item, parent, false);
            }

            TextView textViewEmail = convertView.findViewById(R.id.textViewEmail);
            Button buttonDetail = convertView.findViewById(R.id.buttonDetail);

            String email = emails.get(position);
            textViewEmail.setText(email);

            buttonDetail.setOnClickListener(v -> {
                String firebaseKey = emailToFirebaseKeyMap.get(email);
                if (firebaseKey != null) {
                    Intent intent = new Intent(ViewCustomerActivity.this, CustomerDetailActivity.class);
                    intent.putExtra("FIREBASE_KEY", firebaseKey);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewCustomerActivity.this, "Error: Firebase Key not found.", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }
}
