package com.gsbatra.expensedeck.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

public class Income extends Fragment implements TransactionAdapter.OnAmountsDataReceivedListener{
    public Income(){ }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private View view;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.income_fragment, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.income_transactions_rv);
        adapter = new TransactionAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        TransactionViewModel transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        transactionViewModel.getIncomeTransactions().observe(getViewLifecycleOwner(), adapter::setTransactions);
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();
        return view;
    }

    @Override
    public void onAmountsDataReceived(double balance, double income, double expense, int size, String Suggestion) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String income_str = format.format(income);

        TextView income_tv = view.findViewById(R.id.total_income_amount);
        income_tv.setText(income_str);
        TextView transactions_tv = view.findViewById(R.id.total_transactions_amount);
        transactions_tv.setText(String.valueOf(size));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search income transactions...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
