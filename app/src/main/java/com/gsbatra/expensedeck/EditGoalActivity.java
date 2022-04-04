package com.gsbatra.expensedeck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gsbatra.expensedeck.db.GoalDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EditGoalActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_delete_goal);
        this.id = getIntent().getIntExtra("id", -1);

        // set details of selected goal to text views
        updateTextViews();

        // launch add goal activity if edit is clicked
        findViewById(R.id.btn_edit_transaction).setOnClickListener(view -> {
            Intent intent = new Intent(EditGoalActivity.this, AddGoalsActivity.class);
            intent.putExtra("id", this.id);
            startActivity(intent);
            finish();
        });

        // delete button is clicked
        findViewById(R.id.btn_delete_transaction).setOnClickListener(view -> {
            displayDialog();
        });
    }

    private void displayDialog() {
        DisplayDialog deleteDialog = new DisplayDialog();
        Bundle args = new Bundle();
        args.putInt("id", this.id);
        args.putString("title", ((TextView) findViewById(R.id.title)).getText().toString());
        deleteDialog.setArguments(args);
        deleteDialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    private void updateTextViews() {
        TextView title_tv = findViewById(R.id.title);
        TextView amount_tv = findViewById(R.id.amount_total);
        TextView type_tv = findViewById(R.id.type);
        TextView tag_tv = findViewById(R.id.tag);
        TextView startdate_tv = findViewById(R.id.startdate);
        TextView enddate_tv = findViewById(R.id.enddate);
        TextView note_tv = findViewById(R.id.note);

        GoalDatabase.getGoal(this.id, goal -> {
            title_tv.setText(goal.title);
            amount_tv.setText(String.valueOf(goal.amountTotal));
            type_tv.setText(goal.type);
            tag_tv.setText(goal.tag);
            startdate_tv.setText(goal.startDate);
            enddate_tv.setText(goal.endDate);
            note_tv.setText(goal.note);
        });
    }

    private void deleteGoal(int id) {
        GoalDatabase.delete(id);
        finish();
    }

    public static class DisplayDialog extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            int goal_id = getArguments().getInt("id");
            final String title = getArguments().getString("title");
            builder.setTitle(title)
                    .setMessage("Are you sure you want to delete this goal?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        ((EditGoalActivity) getActivity()).deleteGoal(goal_id);
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }
}
