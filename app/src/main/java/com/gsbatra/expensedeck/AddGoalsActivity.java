package com.gsbatra.expensedeck;

import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddGoalsActivity extends AppCompatActivity {
    private int goal_id;
    final Calendar calendar = Calendar.getInstance();
    private final String[] type = new String[] {"Income", "Expense"};
    private final String[] tag = new String[] {"Utilities", "Entertainment", "Healthcare", "Transportation", "Housing",
            "Investing", "Food", "Insurance",  "Other"};
    TextInputEditText startdate;
    TextInputEditText enddate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_goal);

        // start date
        startdate = findViewById(R.id.et_startdate);
        DatePickerDialog.OnDateSetListener date1 = (datePicker, yr, mt, dy) -> {
            calendar.set(Calendar.YEAR, yr);
            calendar.set(Calendar.MONTH, mt);
            calendar.set(Calendar.DAY_OF_MONTH, dy);
            updateStartLabel();
        };
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGoalsActivity.this, date1,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        // end date
        enddate = findViewById(R.id.et_enddate);
        DatePickerDialog.OnDateSetListener date2 = (datePicker, yr, mt, dy) -> {
            calendar.set(Calendar.YEAR, yr);
            calendar.set(Calendar.MONTH, mt);
            calendar.set(Calendar.DAY_OF_MONTH, dy);
            updateEndLabel();
        };
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddGoalsActivity.this, date2,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        // type dropdown
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_type, type);
        AutoCompleteTextView typeDropdown = findViewById(R.id.et_transactionType);
        typeDropdown.setAdapter(typeAdapter);

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

    private void updateStartLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        startdate.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateEndLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        enddate.setText(dateFormat.format(calendar.getTime()));
    }

    private void setEditGoal() {
        GoalDatabase.getGoal(goal_id, goal -> {
            ((TextInputEditText) findViewById(R.id.et_title)).setText(goal.title);
            ((TextInputEditText) findViewById(R.id.et_amountTotal)).setText(String.valueOf(goal.amountTotal));
            ((AutoCompleteTextView) findViewById(R.id.et_transactionType)).setText(goal.type, false);
            ((AutoCompleteTextView) findViewById(R.id.et_transactionTag)).setText(goal.tag, false);
            ((TextInputEditText) findViewById(R.id.et_startdate)).setText(goal.startDate);
            ((TextInputEditText) findViewById(R.id.et_enddate)).setText(goal.endDate);
            ((TextInputEditText) findViewById(R.id.et_note)).setText(goal.note);
        });
    }

    private void saveGoal(View v) {
        updateDatabase();
    }

    private void updateDatabase() {
        String title = ((TextInputEditText) findViewById(R.id.et_title)).getText().toString();
        String amountTotal = ((TextInputEditText) findViewById(R.id.et_amountTotal)).getText().toString();
        AutoCompleteTextView tagTextView = findViewById(R.id.et_transactionTag);
        String tag = tagTextView.getEditableText().toString();
        AutoCompleteTextView typeTextView = findViewById(R.id.et_transactionType);
        String type = typeTextView.getEditableText().toString();
        String startDate = ((TextInputEditText) findViewById(R.id.et_startdate)).getText().toString();
        String endDate = ((TextInputEditText) findViewById(R.id.et_enddate)).getText().toString();

        // check required fields are not null
        if(title.equals("") || amountTotal.equals("") || tag.equals("") || type.equals("") ||
            startDate.equals("") || endDate.equals("")){
            Toast.makeText(getApplicationContext(), "Fill out the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // check if start or end date are invalid
        try {
            String format = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            assert start != null;
            if (start.after(end)) {
                Toast.makeText(getApplicationContext(), "Invalid start or end dates", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Invalid start or end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = ((TextInputEditText) findViewById(R.id.et_note)).getText().toString();

        Goal goal = new Goal(goal_id == -1 ? 0 : goal_id,
                title, Double.parseDouble(amountTotal), type, tag, note, startDate, endDate);

        if (goal_id == -1) {
            GoalDatabase.insert(goal);
        } else {
            GoalDatabase.update(goal);
        }

        finish(); // Quit activity
    }
}
