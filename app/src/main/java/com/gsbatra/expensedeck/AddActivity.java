package com.gsbatra.expensedeck;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionDatabase;

public class AddActivity extends AppCompatActivity {
    private int transaction_id;
    private final String[] type = new String[] {"Income", "Expense"};
    private final String[] tag = new String[] {"Utilities", "Entertainment", "Healthcare", "Transportation", "Housing",
            "Investing", "Food", "Insurance",  "Other"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_transaction);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_type, type);
        AutoCompleteTextView typeDropdown = findViewById(R.id.et_transactionType);
        typeDropdown.setAdapter(typeAdapter);

        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.dropdown_type, tag);
        AutoCompleteTextView tagDropdown = findViewById(R.id.et_transactionTag);
        tagDropdown.setAdapter(tagAdapter);

        transaction_id = getIntent().getIntExtra("id", -1);
        if(transaction_id != -1) {
            setEditTransaction();
        }
        findViewById(R.id.btn_save_transaction).setOnClickListener(this::saveTransaction);
    }

    private void setEditTransaction() {
        TransactionDatabase.getTransaction(transaction_id, transaction -> {
            ((TextInputEditText) findViewById(R.id.et_title)).setText(transaction.title);
            ((TextInputEditText) findViewById(R.id.et_amount)).setText(String.valueOf(transaction.amount));
            ((AutoCompleteTextView) findViewById(R.id.et_transactionType)).setText(transaction.type, false);
            ((AutoCompleteTextView) findViewById(R.id.et_transactionTag)).setText(transaction.tag, false);
            ((TextInputEditText) findViewById(R.id.et_note)).setText(transaction.note);
            ((TextInputEditText) findViewById(R.id.et_when)).setText(transaction.when);
        });
    }

    private void saveTransaction(View v){
        updateDatabase();
    }

    private void updateDatabase() {
        String title = ((TextInputEditText) findViewById(R.id.et_title)).getText().toString();
        String amount = ((TextInputEditText) findViewById(R.id.et_amount)).getText().toString();
        AutoCompleteTextView typeTextView = findViewById(R.id.et_transactionType);
        String type = typeTextView.getEditableText().toString();
        AutoCompleteTextView tagTextView = findViewById(R.id.et_transactionTag);
        String tag = tagTextView.getEditableText().toString();

        if(title.equals("") || amount.equals("") || type.equals("") || tag.equals("")){
            Toast.makeText(getApplicationContext(), "Title, Amount, Type, and Tag are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((TextInputEditText) findViewById(R.id.et_note)).getText().toString();
        String when = ((TextInputEditText) findViewById(R.id.et_when)).getText().toString();

        Transaction transaction = new Transaction(transaction_id == -1 ? 0 : transaction_id,
                title, Double.parseDouble(amount), type, tag, note, when);

        if (transaction_id == -1) {
            TransactionDatabase.insert(transaction);
        } else {
            TransactionDatabase.update(transaction);
        }

        finish(); // Quit activity
    }
}
