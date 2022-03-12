package com.gsbatra.expensedeck;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.gsbatra.expensedeck.db.Goal;
import com.gsbatra.expensedeck.db.GoalDatabase;

public class AddGoalsActivity extends AppCompatActivity {
    private int goal_id;
    private final String[] tag = new String[] {"Utilities", "Entertainment", "Healthcare", "Transportation", "Housing",
            "Investing", "Food", "Insurance",  "Other"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_goal);

        // tag dropdown
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.dropdown_type, tag);
        AutoCompleteTextView tagDropdown = findViewById(R.id.et_transactionTag);
        tagDropdown.setAdapter(tagAdapter);

        goal_id = getIntent().getIntExtra("id", -1);
        if(goal_id != -1) {
            setEditGoal();
        }

        // btn listener for adding a goal
        findViewById(R.id.goal_fab).setOnClickListener(this::saveGoal);
    }

    private void setEditGoal() {
        GoalDatabase.getGoal(goal_id, goal -> {
            ((TextInputEditText) findViewById(R.id.et_title)).setText(goal.title);
            ((TextInputEditText) findViewById(R.id.et_amount)).setText(String.valueOf(goal.amount));
            ((TextInputEditText) findViewById(R.id.et_amountTotal)).setText(String.valueOf(goal.amountTotal));
            ((AutoCompleteTextView) findViewById(R.id.et_transactionTag)).setText(goal.tag, false);
            ((TextInputEditText) findViewById(R.id.et_note)).setText(goal.note);
        });
    }

    private void saveGoal(View v){
        updateDatabase();
    }

    private void updateDatabase() {
        String title = ((TextInputEditText) findViewById(R.id.et_title)).getText().toString();
        String amount = ((TextInputEditText) findViewById(R.id.et_amount)).getText().toString();
        String amountTotal = ((TextInputEditText) findViewById(R.id.et_amountTotal)).getText().toString();
        AutoCompleteTextView tagTextView = findViewById(R.id.et_transactionTag);
        String tag = tagTextView.getEditableText().toString();

        // check required fields are not null
        if(title.equals("") || amount.equals("") || tag.equals("")){
            Toast.makeText(getApplicationContext(), "Title, Amount, Total Amount, Tag are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((TextInputEditText) findViewById(R.id.et_note)).getText().toString();

        com.gsbatra.expensedeck.db.Goal goal = new Goal(goal_id == -1 ? 0 : goal_id,
                title, Double.parseDouble(amount), Double.parseDouble(amountTotal), tag, note);

        if (goal_id == -1) {
            GoalDatabase.insert(goal);
        } else {
            GoalDatabase.update(goal);
        }

        finish(); // Quit activity
    }
}