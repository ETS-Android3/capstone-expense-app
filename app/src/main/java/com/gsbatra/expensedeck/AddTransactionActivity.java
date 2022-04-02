package com.gsbatra.expensedeck;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private int transaction_id;
    final Calendar myCalendar = Calendar.getInstance();
    private final String[] type = new String[] {"Income", "Expense"};
    private final String[] tag = new String[] {"Utilities", "Entertainment", "Healthcare", "Transportation", "Housing",
            "Investing", "Food", "Insurance",  "Other"};
    TextInputEditText whentext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_transaction);

        whentext = findViewById(R.id.et_when);
        DatePickerDialog.OnDateSetListener date1 = (datePicker, yr, mt, dy) -> {
            myCalendar.set(Calendar.YEAR, yr);
            myCalendar.set(Calendar.MONTH, mt);
            myCalendar.set(Calendar.DAY_OF_MONTH, dy);
            updateLabel();
        };
        whentext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddTransactionActivity.this,date1,myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        whentext.setText(dateFormat.format(myCalendar.getTime()));
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
        String when = ((TextInputEditText) findViewById(R.id.et_when)).getText().toString();

        if(title.equals("") || amount.equals("") || type.equals("") || tag.equals("") || when.equals("")){
            Toast.makeText(getApplicationContext(), "Fill out the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((TextInputEditText) findViewById(R.id.et_note)).getText().toString();

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
