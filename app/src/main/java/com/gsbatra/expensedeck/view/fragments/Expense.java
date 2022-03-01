package com.gsbatra.expensedeck.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.TransactionViewModel;
import com.gsbatra.expensedeck.view.adapter.TransactionAdapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Expense extends Fragment implements TransactionAdapter.OnAmountsDataReceivedListener{
    public Expense(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.expense_fragment, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.expense_transactions_rv);
        TransactionAdapter adapter = new TransactionAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getExpenseTransactions().observe(getViewLifecycleOwner(), adapter::setTransactions);
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();
        return view;
    }

    @Override
    public void onAmountsDataReceived(double balance, double income, double expense, int size) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String expense_str = format.format(expense);

        TextView transactions_tv = view.findViewById(R.id.total_transactions_amount);
        transactions_tv.setText(String.valueOf(size));
        TextView expenses_tv = view.findViewById(R.id.total_expense_amount);
        expenses_tv.setText(expense_str);
    }
}
