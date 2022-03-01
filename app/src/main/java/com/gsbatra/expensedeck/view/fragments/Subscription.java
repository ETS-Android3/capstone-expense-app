package com.gsbatra.expensedeck.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gsbatra.expensedeck.AddSubscriptionActivity;
import com.gsbatra.expensedeck.AddTransactionActivity;
import com.gsbatra.expensedeck.MainActivity;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.SubscriptionViewModel;
import com.gsbatra.expensedeck.view.adapter.SubscriptionAdapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Subscription extends Fragment implements SubscriptionAdapter.OnAmountsDataReceivedListener {

    public Subscription() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.subscription_fragment, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recurring_subscriptions_rv);
        SubscriptionAdapter adapter = new SubscriptionAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        SubscriptionViewModel subscriptionViewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);
        subscriptionViewModel.getAllSubscriptions().observe(getViewLifecycleOwner(), adapter::setSubscriptions);
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();

        // fab
        FloatingActionButton subscription_fab = view.findViewById(R.id.subscription_fab);
        subscription_fab.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddSubscriptionActivity.class)));

        return view;
    }

    @Override
    public void onAmountsDataReceived(double recurring, int size) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String recurring_str = format.format(recurring);

        TextView recurring_tv = view.findViewById(R.id.total_recurring_amount);
        recurring_tv.setText(recurring_str);
        TextView subscriptions_tv = view.findViewById(R.id.total_subscriptions_amount);
        subscriptions_tv.setText(String.valueOf(size));
    }
}