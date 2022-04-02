package com.gsbatra.expensedeck.view.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.SignInActivity;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionViewModel;
import com.gsbatra.expensedeck.view.adapter.SummaryAdapter;
import com.gsbatra.expensedeck.view.adapter.TransactionAdapter;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Summary extends Fragment implements TransactionAdapter.OnAmountsDataReceivedListener {
    //
    Calendar rightNow = Calendar.getInstance();
    ExpandableListView expandableListView;
    List<String> listGroup;
    HashMap<String,List<String>> listItem;
    SummaryAdapter adapter;

    public static HashMap<String, Double> EmapMTD = new HashMap<>();
    public static HashMap<String, Double> EmapYTD = new HashMap<>();

    public static HashMap<String, Double> ImapMTD = new HashMap<>();
    public static HashMap<String, Double> ImapYTD = new HashMap<>();

    public static double monthtotalincome;
    public static double monthtotalexpenses;

    public static double yeartotalincome;
    public static double yeartotalexpenses;

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
        //
        expandableListView = view.findViewById(R.id.expandable_listview);
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new SummaryAdapter(getActivity(),listGroup,listItem);
        expandableListView.setAdapter(adapter);
        initListData();

        TextView monthlydate_tv = view.findViewById(R.id.MonthlyDate);
        monthlydate_tv.setText(caldate);

        TransactionAdapter adapter = new TransactionAdapter(getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        adapter.setOnAmountsDataReceivedListener(this);
        adapter.getAmounts();
        //
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
        }

        listItem.put(listGroup.get(0),list1);
        listItem.put(listGroup.get(1),list2);
        adapter.notifyDataSetChanged();
    }

    public void setTransactions(List<Transaction> transactions) {
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

            String mo = whn.substring(0,2); //start position 0, length of two.  gets MM of the transaction
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

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(getActivity(), "Please Sign In", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_nosearch, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signout){
            displayDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDialog() {
        DisplayDialog signOutDialog = new DisplayDialog();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        String name = "Sign out";
        if(signInAccount != null)
            name = signInAccount.getDisplayName();
        Bundle args = new Bundle();
        args.putString("name", name);
        signOutDialog.setArguments(args);
        signOutDialog.show(getParentFragmentManager(), "signOutDialog");
    }

    public static class DisplayDialog extends DialogFragment {
        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()), android.app.AlertDialog.THEME_HOLO_DARK);
            final String name = getArguments().getString("name");
            builder.setTitle(name)
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignIn.getClient(
                                getContext(),
                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        ).signOut();
                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> {});
            return builder.create();
        }
    }
}
