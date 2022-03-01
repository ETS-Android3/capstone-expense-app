package com.gsbatra.expensedeck;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.gsbatra.expensedeck.db.TransactionDatabase;

import org.jetbrains.annotations.NotNull;

public class EditTransactionActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_delete_transaction);
        this.id = getIntent().getIntExtra("id", -1);
        updateTextViews();

        findViewById(R.id.btn_edit_transaction).setOnClickListener(view -> {
            Intent intent = new Intent(EditTransactionActivity.this, AddTransactionActivity.class);
            intent.putExtra("id", this.id);
            startActivity(intent);
            finish();
        });

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
        TextView type_tv = findViewById(R.id.type);
        TextView tag_tv = findViewById(R.id.tag);
        TextView note_tv = findViewById(R.id.note);
        TextView when_tv = findViewById(R.id.when);

        TransactionDatabase.getTransaction(this.id, transaction -> {
            title_tv.setText(transaction.title);
            amount_tv.setText(String.valueOf(transaction.amount));
            type_tv.setText(transaction.type);
            tag_tv.setText(transaction.tag);
            note_tv.setText(transaction.note);
            when_tv.setText(transaction.when);
        });
    }

    private void deleteTransaction(int id) {
        TransactionDatabase.delete(id);
        finish();
    }

    public static class DisplayDialog extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            int transaction_id = getArguments().getInt("id");
            final String title = getArguments().getString("title");
            builder.setTitle(title)
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete", (dialog, id) -> {
                        ((EditTransactionActivity) getActivity()).deleteTransaction(transaction_id);
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }
}
