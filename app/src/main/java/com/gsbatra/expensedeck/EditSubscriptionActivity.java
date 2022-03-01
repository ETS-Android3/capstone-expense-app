package com.gsbatra.expensedeck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gsbatra.expensedeck.db.SubscriptionDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EditSubscriptionActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_delete_subscription);
        this.id = getIntent().getIntExtra("id", -1);

        // set details of selected subscription to text views
        updateTextViews();

        // launch add subscription activity if edit is clicked
        findViewById(R.id.btn_edit_transaction).setOnClickListener(view -> {
            Intent intent = new Intent(EditSubscriptionActivity.this, AddSubscriptionActivity.class);
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
        TextView amount_tv = findViewById(R.id.amount);
        TextView tag_tv = findViewById(R.id.tag);
        TextView note_tv = findViewById(R.id.note);

        SubscriptionDatabase.getSubscription(this.id, subscription -> {
            title_tv.setText(subscription.title);
            amount_tv.setText(String.valueOf(subscription.amount));
            tag_tv.setText(subscription.tag);
            note_tv.setText(subscription.note);
        });
    }

    private void deleteSubscription(int id) {
        SubscriptionDatabase.delete(id);
        finish();
    }

    public static class DisplayDialog extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            int subscription_id = getArguments().getInt("id");
            final String title = getArguments().getString("title");
            builder.setTitle(title)
                    .setMessage("Are you sure you want to delete this subscription?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        ((EditSubscriptionActivity) getActivity()).deleteSubscription(subscription_id);
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }
}
