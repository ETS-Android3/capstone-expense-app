package com.gsbatra.expensedeck;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
        findViewById(R.id.btn_save_goal).setOnClickListener(this::saveGoal);
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

    private void saveGoal(View v) {
        updateDatabase();
    }

    private void updateDatabase() {
        String title = ((TextInputEditText) findViewById(R.id.et_title)).getText().toString();
        String amount = ((TextInputEditText) findViewById(R.id.et_amount)).getText().toString();
        String amountTotal = ((TextInputEditText) findViewById(R.id.et_amountTotal)).getText().toString();
        AutoCompleteTextView tagTextView = findViewById(R.id.et_transactionTag);
        String tag = tagTextView.getEditableText().toString();

        // check required fields are not null
        if(title.equals("") || amount.equals("") || amountTotal.equals("") || tag.equals("")){
            Toast.makeText(getApplicationContext(), "Fill out the required fields", Toast.LENGTH_SHORT).show();
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

        if(Integer.parseInt(amount) > Integer.parseInt(amountTotal)) {
            new AlertDialog.Builder(AddGoalsActivity.this, AlertDialog.THEME_HOLO_DARK)
                    .setTitle("Notice")
                    .setMessage("The amount entered exceeds the amount total.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish(); // Quit activity
                        }
                    }).show();
        }
        else {
            finish(); // Quit activity
        }
    }
}
