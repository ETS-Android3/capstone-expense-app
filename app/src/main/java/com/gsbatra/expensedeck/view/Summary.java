package com.gsbatra.expensedeck.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Summary extends Fragment{
    public Summary(){
    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.summary_fragment, container, false);
        TransactionViewModel transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), this::setTransactions);
        return view;
    }

    public void setTransactions(List<Transaction> transactions) {
        HashMap<String, Integer> map = new HashMap<>();
        for(Transaction transaction : transactions){
            String tag = transaction.tag;
            if(!map.containsKey(tag)){
                map.put(tag, 1);
            } else {
                int count = map.get(tag);
                map.replace(tag, count, ++count);
            }
        }
        createPieChart(map);
    }

    public void createPieChart(HashMap<String, Integer> map){
        PieChart pieChart = view.findViewById(R.id.pieChart);
        Typeface reg_tf = ResourcesCompat.getFont(getContext(), R.font.opensans_regular);
        Typeface semi_tf = ResourcesCompat.getFont(getContext(), R.font.opensans_semibold);

        ArrayList<PieEntry> temp = new ArrayList<>();
        for(String key : map.keySet()) {
            int count = map.get(key);
            temp.add(new PieEntry(count, key));
        }

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.BLACK);

        PieDataSet pieDataSet = new PieDataSet(temp, "Transactions");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(20f);
        pieChart.setEntryLabelTextSize(16f);

        ValueFormatter vf = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + (int)value;
            }
        };

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(vf);
        pieData.setValueTypeface(reg_tf);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Transactions");
        pieChart.setCenterTextTypeface(semi_tf);
        pieChart.setCenterTextSize(24f);
        pieChart.setCenterTextColor(Color.WHITE);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getLegend().setEnabled(false);
        pieChart.animate();
    }
}
