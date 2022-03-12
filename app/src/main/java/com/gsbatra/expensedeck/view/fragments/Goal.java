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
import com.gsbatra.expensedeck.AddGoalsActivity;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.GoalViewModel;
import com.gsbatra.expensedeck.view.adapter.GoalAdapter;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Goal extends Fragment implements GoalAdapter.OnAmountsDataReceivedListener {

    private View view;

    public Goal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.goal_fragment, container, false);

        // set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recurring_goals_rv);
        GoalAdapter adapter = new GoalAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        GoalViewModel goalViewModel = new ViewModelProvider(this).get(GoalViewModel.class);
        goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), adapter::setGoals);
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();

        // fab
        FloatingActionButton subscription_fab = view.findViewById(R.id.goal_fab);
        subscription_fab.setOnClickListener(view -> startActivity(new Intent(getActivity(), AddGoalsActivity.class)));

        return view;
    }

    @Override
    public void onAmountsDataReceived(double recurring, int size) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String recurring_str = format.format(recurring);

        TextView recurring_tv = view.findViewById(R.id.total_goals_amount_view);
        recurring_tv.setText(recurring_str);
        TextView goals_tv = view.findViewById(R.id.total_goals_card_view);
        goals_tv.setText(String.valueOf(size));
    }
}