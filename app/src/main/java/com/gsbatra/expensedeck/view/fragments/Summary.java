package com.gsbatra.expensedeck.view.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionViewModel;
import com.gsbatra.expensedeck.view.adapter.SummaryAdapter;
import com.gsbatra.expensedeck.view.adapter.TransactionAdapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class Summary extends Fragment implements TransactionAdapter.OnAmountsDataReceivedListener {

    Calendar rightNow = Calendar.getInstance();
    ExpandableListView expandableListView;
    List<String> listGroup;
    HashMap<String,List<String>> listItem;
    SummaryAdapter adapter;

    public static HashMap<String, Double> EmapMTD = new HashMap<String, Double>();
    public static HashMap<String, Double> EmapYTD = new HashMap<String, Double>();

    public static HashMap<String, Double> ImapMTD = new HashMap<String, Double>();
    public static HashMap<String, Double> ImapYTD = new HashMap<String, Double>();

    public static double monthtotalincome;
    public static double monthtotalexpenses;

    public static double yeartotalincome;
    public static double yeartotalexpenses;

    private static final ArrayList<Entry> chartValues = new ArrayList<>();
    private static double yAxisMax;
    private static double yAxisMin;
    private static Integer xAxisMax;
    private static Integer xAxisMin;

    public Summary(){
        EmapMTD.put("Utilities",0.0);
        EmapMTD.put("Entertainment",0.0);
        EmapMTD.put("Healthcare",0.0);
        EmapMTD.put("Transportation",0.0);
        EmapMTD.put("Housing",0.0);
        EmapMTD.put("Investing",0.0);

        EmapYTD.put("Utilities",0.0);
        EmapYTD.put("Entertainment",0.0);
        EmapYTD.put("Healthcare",0.0);
        EmapYTD.put("Transportation",0.0);
        EmapYTD.put("Housing",0.0);
        EmapYTD.put("Investing",0.0);

        ImapMTD.put("Utilities",0.0);
        ImapMTD.put("Entertainment",0.0);
        ImapMTD.put("Healthcare",0.0);
        ImapMTD.put("Transportation",0.0);
        ImapMTD.put("Housing",0.0);
        ImapMTD.put("Investing",0.0);

        ImapYTD.put("Utilities",0.0);
        ImapYTD.put("Entertainment",0.0);
        ImapYTD.put("Healthcare",0.0);
        ImapYTD.put("Transportation",0.0);
        ImapYTD.put("Housing",0.0);
        ImapYTD.put("Investing",0.0);

        monthtotalexpenses = 0.0;
        monthtotalincome = 0.0;
        yeartotalincome = 0.0;
        yeartotalexpenses = 0.0;

    }

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int year = rightNow.get(Calendar.YEAR);
        String month = rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String caldate = month + " " + year;

        view = inflater.inflate(R.layout.summary_fragment, container, false);
        TransactionViewModel transactionViewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), this::setTransactions);

        expandableListView = view.findViewById(R.id.expandable_listview);
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new SummaryAdapter(getActivity(),listGroup,listItem);
        expandableListView.setAdapter(adapter);
        initListData();

        TextView monthlydate_tv = view.findViewById(R.id.MonthlyDate);
        monthlydate_tv.setText(caldate);
        RadioButton radio1 = view.findViewById(R.id.activity_main_monthly);
        radio1.setChecked(true);

        TransactionAdapter adapter = new TransactionAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();

        return view;
    }

    private void initListData() { //
        listGroup.add(getString(R.string.expenseslist1));
        listGroup.add(getString(R.string.incomelist1));

        String[] array;

        List<String> list1 = new ArrayList<>();
        array = getResources().getStringArray(R.array.expenseslist1);
        for (String item : array) {
            list1.add(item);
        }

        List<String> list2 = new ArrayList<>();
        array = getResources().getStringArray(R.array.incomelist1);
        for (String item : array) {
            list2.add(item);
            //hard-coded for now.  show income/expense for each individual item
        }

        listItem.put(listGroup.get(0),list1);
        listItem.put(listGroup.get(1),list2);
        adapter.notifyDataSetChanged();
    }

    public void setTransactions(List<Transaction> transactions) {
        // Update the chart
        LineChart lineChart = view.findViewById(R.id.linechart);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        chartValues.clear();
        createLineChart(transactions);
        getValues(transactions);
        setAxes();
        setLineChartData();

        String mFormat="MM";
        SimpleDateFormat dateFormat=new SimpleDateFormat(mFormat, Locale.US);
        String currentmonth = dateFormat.format(rightNow.getTime());

        String yFormat="yy";
        SimpleDateFormat dateFormat1=new SimpleDateFormat(yFormat, Locale.US);
        String currentyear = dateFormat1.format(rightNow.getTime());

        HashMap<String, Integer> map = new HashMap<>();

        for(Transaction transaction : transactions){
            String tag = transaction.tag;
            String type = transaction.type;
            Double amt = transaction.amount;
            String whn = transaction.when;

            String mo = whn.substring(0,2);
            String yr = whn.substring(Math.max(whn.length() - 2, 0)); //year

            if(!map.containsKey(tag)){ //
                map.put(tag, 1);

                if (yr.equals(currentyear)) {
                    if (type.equals("Expense")) {
                        //amt = amt * -1;
                        yeartotalexpenses += amt;
                        EmapYTD.put(tag, amt);
                    }
                    else {
                        yeartotalincome += amt;
                        ImapYTD.put(tag, amt);
                    }

                    if (mo.equals(currentmonth)) {
                        if (type.equals("Expense")) {
                            monthtotalexpenses += amt;
                            EmapMTD.put(tag, amt);
                        }
                        else {
                            monthtotalincome += amt;
                            ImapMTD.put(tag, amt);
                        }
                    }
                }

            } else { //there already exists a transaction w the same tag
                int count = map.get(tag);
                map.replace(tag, count, ++count);
                //

                if (yr.equals(currentyear)) {
                    if (type.equals("Expense")) {
                        //if one of type expense doesnt exist
                        yeartotalexpenses += amt;
                        if(!EmapYTD.containsKey(tag)) {
                            //amt = amt * -1;
                            EmapYTD.put(tag, amt);
                        } else {
                            //if one of type expense exists
                            //amt = amt * -1;
                            double y = EmapYTD.get(tag);
                            EmapYTD.replace(tag, y, y + amt);
                        }
                    }
                    else {
                        yeartotalincome += amt;
                        if(!ImapYTD.containsKey(tag)) {
                            ImapYTD.put(tag, amt);
                        } else {
                            //if one of type income exists
                            double x = ImapYTD.get(tag);
                            ImapYTD.replace(tag, x, x + amt);
                        }
                    }

                    if (mo.equals(currentmonth)) {
                        if (type.equals("Expense")) {
                            //if one of type expense doesnt exist
                            monthtotalexpenses += amt;
                            if(!EmapMTD.containsKey(tag)) {
                                EmapMTD.put(tag, amt);
                            } else {
                                //if one of type expense exists
                                double y = EmapMTD.get(tag);
                                EmapMTD.replace(tag, y, y + amt);
                            }
                        }
                        else {
                            monthtotalincome += amt;
                            if(!ImapMTD.containsKey(tag)) {
                                ImapMTD.put(tag, amt);
                            } else {
                                //if one of type income exists
                                double x = ImapMTD.get(tag);
                                ImapMTD.replace(tag, x, x + amt);
                            }
                        }
                    }
                }

            }


        }
        createPieChart(map);

        double balance_mtd = 0;

        for (String name: ImapMTD.keySet()) {
            String key = name.toString();
            Double value = ImapMTD.get(name);
            balance_mtd += value;
        }

        for (String name: EmapMTD.keySet()) {
            String key = name.toString();
            Double value = EmapMTD.get(name);
            balance_mtd -= value;
        }

        double balance_ytd = 0;

        for (String name: ImapYTD.keySet()) {
            String key = name.toString();
            Double value = ImapYTD.get(name);
            balance_ytd += value;
        }

        for (String name: EmapYTD.keySet()) {
            String key = name.toString();
            Double value = EmapYTD.get(name);
            balance_ytd -= value;
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));

        TextView balanceMTD_tv = view.findViewById(R.id.list_child_MTD_balance);
        balanceMTD_tv.setText(String.valueOf(format.format(balance_mtd)));
        if (balance_mtd < 0) {
            balanceMTD_tv.setTextColor(Color.RED); //if its a negative number
            balanceMTD_tv.setText("(" + String.valueOf(format.format(balance_mtd * -1.0)) + ")");
        }
        TextView balanceYTD_tv = view.findViewById(R.id.list_child_YTD_balance);
        balanceYTD_tv.setText(String.valueOf(format.format(balance_ytd)));
        if (balance_ytd < 0) {
            balanceYTD_tv.setTextColor(Color.RED); //if its a negative number
            balanceYTD_tv.setText("(" + String.valueOf(format.format(balance_ytd * -1.0)) + ")");
        }

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
        pieChart.setEntryLabelTextSize(14f);

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

    public void createLineChart(List<Transaction> transactions) {
        LineChart lineChart = view.findViewById(R.id.linechart);
        RadioButton radio1 = view.findViewById(R.id.activity_main_monthly);
        RadioButton radio2 = view.findViewById(R.id.activity_main_yearly);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setGridColor(Color.WHITE);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);
        leftAxis.setTextSize(14f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setGridColor(Color.WHITE);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.setDescription(null);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraBottomOffset(8f);
        lineChart.setBackgroundColor(Color.BLACK);

        radio1.setOnClickListener(v -> {
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            chartValues.clear();
            getValues(transactions);
            setAxes();
            setLineChartData();});

        radio2.setOnClickListener(v -> {
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            chartValues.clear();
            getValues(transactions);
            setAxes();
            setLineChartData();});
    }

    @SuppressLint("NonConstantResourceId")
    public void getValues(List<Transaction> transactions){
        RadioGroup radioGroup = view.findViewById(R.id.activity_main_timeinterval);
        TextView xAxisLabel = view.findViewById(R.id.xaxis_label);
        xAxisMax = Integer.MIN_VALUE;
        xAxisMin = Integer.MAX_VALUE;
        yAxisMax = Integer.MIN_VALUE;
        yAxisMin = Integer.MAX_VALUE;

        //Get RadioGroup value(monthly or yearly), set chart title, and set xAxis label
        String frequency;
        if (radioGroup.getCheckedRadioButtonId() == R.id.activity_main_monthly) {
            frequency = "monthly";
            xAxisLabel.setText(getResources().getString(R.string.day));
        }
        else {
            frequency = "yearly";
            xAxisLabel.setText(getResources().getString(R.string.month));
        }

        //Get current month and year
        String mFormat="MM";
        SimpleDateFormat dateFormat=new SimpleDateFormat(mFormat, Locale.US);
        String currentmonth = dateFormat.format(rightNow.getTime());

        String yFormat="yy";
        SimpleDateFormat dateFormat1=new SimpleDateFormat(yFormat, Locale.US);
        String currentyear = dateFormat1.format(rightNow.getTime());

        //sort transactions ascending by day if monthly, or by month if yearly
        if (frequency.equals("monthly"))
            transactions.sort(Comparator.comparing(Transaction::getDay));
        else //yearly
            transactions.sort(Comparator.comparing(Transaction::getMonth));

        //totalBalance stores the date(day or month) and total balance for that date for each transaction
        HashMap<Integer, Double> totalBalance = new HashMap<>();
        double balance = 0.0;

        for (Transaction t: transactions) {
            String type = t.type;
            double amt = t.amount;
            String whn = t.when;

            String mo = whn.substring(0,2);
            String yr = whn.substring(Math.max(whn.length() - 2, 0));

            switch(frequency) {
                case "monthly":
                    if (yr.equals(currentyear) && mo.equals(currentmonth)) {
                        if (type.equals("Income")) {
                            balance += amt;
                            if (totalBalance.containsKey(t.getDay()))
                                totalBalance.replace(t.getDay(), balance);
                            else
                                totalBalance.put(t.getDay(), balance);
                        } else if (type.equals("Expense")) {
                            balance -= amt;
                            if (totalBalance.containsKey(t.getDay()))
                                totalBalance.replace(t.getDay(), balance);
                            else
                                totalBalance.put(t.getDay(), balance);
                        }
                    }
                    break;
                case "yearly":
                    if (yr.equals(currentyear)) {
                        if (type.equals("Income")) {
                            balance += amt;
                            if (totalBalance.containsKey(t.getMonth()))
                                totalBalance.replace(t.getMonth(), balance);
                            else
                                totalBalance.put(t.getMonth(), balance);
                        } else if (type.equals("Expense")) {
                            balance -= amt;
                            if (totalBalance.containsKey(t.getMonth()))
                                totalBalance.replace(t.getMonth(), balance);
                            else
                                totalBalance.put(t.getMonth(), balance);
                        }
                    }
                    break;
            }
        }

        //load balances into Values List for graph
        for (Map.Entry<Integer, Double> entry: totalBalance.entrySet()){
            Integer date = entry.getKey();
            double bal = entry.getValue();

            chartValues.add(new Entry(date, (float)bal));

            if (bal > yAxisMax) {
                yAxisMax = bal;}
            if (bal < yAxisMin) {
                yAxisMin = bal;}
            if (date > xAxisMax) {
                xAxisMax = date;}
            if (date < xAxisMin) {xAxisMin = date;}
        }

        //sort final chart values by date
        chartValues.sort(Comparator.comparingInt(o -> (int) o.getX()));
    }

    public void setAxes() {
        LineChart lineChart = view.findViewById(R.id.linechart);
        YAxis leftAxis = lineChart.getAxisLeft();
        XAxis xAxis = lineChart.getXAxis();

        leftAxis.setAxisMaximum((float)(yAxisMax + (yAxisMax / 20)));
        leftAxis.setAxisMinimum((float)(yAxisMin - (yAxisMin / 20)));
        double buffer = ((xAxisMax - xAxisMin) / 10.0);
        xAxis.setAxisMaximum((float)(xAxisMax + buffer));
        xAxis.setAxisMinimum((float)(xAxisMin - buffer));
    }

    private void setLineChartData() {
        LineChart lineChart = view.findViewById(R.id.linechart);
        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(chartValues);
            set1.notifyDataSetChanged();
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(chartValues, "Balance");
            set1.setDrawIcons(false);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.WHITE);
            set1.setLineWidth(2f);
            set1.setCircleRadius(6f);
            set1.setDrawCircleHole(false);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            data.setValueTextSize(20f);
            data.setValueTextColor(Color.WHITE);
            lineChart.setData(data);
        }
    }

    @Override
    public void onAmountsDataReceived(double balance, double income, double expense, int size) {
        /*NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String balanceMTD = format.format(balance);//
        String balanceYTD = format.format(balance);//

        TextView balanceMTD_tv = view.findViewById(R.id.list_child_MTD_balance);
        balanceMTD_tv.setText(balanceMTD);
        TextView balanceYTD_tv = view.findViewById(R.id.list_child_YTD_balance);
        balanceYTD_tv.setText(balanceYTD);*/
    }
}
