package com.gsbatra.expensedeck;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gsbatra.expensedeck.db.Subscription;
import com.gsbatra.expensedeck.db.SubscriptionDatabase;

public class AddSubscriptionActivity extends AppCompatActivity {
    private int subscription_id;
    private final String[] tag = new String[] {"Utilities", "Entertainment", "Healthcare", "Transportation", "Housing",
            "Investing", "Food", "Insurance",  "Other"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_subscription);

        // tag dropdown
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.dropdown_type, tag);
        AutoCompleteTextView tagDropdown = findViewById(R.id.et_transactionTag);
        tagDropdown.setAdapter(tagAdapter);

        subscription_id = getIntent().getIntExtra("id", -1);
        if(subscription_id != -1) {
            setEditSubscription();
        }

        // btn listener for adding a subscription
        findViewById(R.id.btn_save_subscription).setOnClickListener(this::saveSubscription);
    }

    private void setEditSubscription() {
        SubscriptionDatabase.getSubscription(subscription_id, subscription -> {
            ((TextInputEditText) findViewById(R.id.et_title)).setText(subscription.title);
            ((TextInputEditText) findViewById(R.id.et_amount)).setText(String.valueOf(subscription.amount));
            ((AutoCompleteTextView) findViewById(R.id.et_transactionTag)).setText(subscription.tag, false);
            ((TextInputEditText) findViewById(R.id.et_note)).setText(subscription.note);
        });
    }

    private void saveSubscription(View v){
        updateDatabase();
    }

    private void updateDatabase() {
        String title = ((TextInputEditText) findViewById(R.id.et_title)).getText().toString();
        String amount = ((TextInputEditText) findViewById(R.id.et_amount)).getText().toString();
        AutoCompleteTextView tagTextView = findViewById(R.id.et_transactionTag);
        String tag = tagTextView.getEditableText().toString();

        // check required fields are not null
        if(title.equals("") || amount.equals("") || tag.equals("")){
            Toast.makeText(getApplicationContext(), "Fill out the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((TextInputEditText) findViewById(R.id.et_note)).getText().toString();

        Subscription subscription = new Subscription(subscription_id == -1 ? 0 : subscription_id,
                title, Double.parseDouble(amount), tag, note);

        if (subscription_id == -1) {
            SubscriptionDatabase.insert(subscription);
        } else {
            SubscriptionDatabase.update(subscription);
        }

        finish(); // Quit activity
    }
}
