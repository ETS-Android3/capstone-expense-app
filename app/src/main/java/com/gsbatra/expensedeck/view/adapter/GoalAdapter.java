package com.gsbatra.expensedeck.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gsbatra.expensedeck.EditGoalActivity;
import com.gsbatra.expensedeck.R;
import com.gsbatra.expensedeck.db.Goal;
import com.gsbatra.expensedeck.db.GoalViewModel;
import com.gsbatra.expensedeck.db.Transaction;
import com.gsbatra.expensedeck.db.TransactionViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> implements Filterable {

    private List<Goal> goals;
    private List<Goal> goalsAll;
    private List<Transaction> transactions;
    private OnAmountsDataReceivedListener onAmountsDataReceivedListener;

    public static class GoalViewHolder extends RecyclerView.ViewHolder {
        private final TextView goalName;
        private final TextView goalTag;
        private final TextView goalDate;
        private final TextView goalAmount;
        private final TextView goalAmountTotal;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            goalName = itemView.findViewById(R.id.goalName);
            goalTag = itemView.findViewById(R.id.goalTag);
            goalDate = itemView.findViewById(R.id.goalDate);
            goalAmount = itemView.findViewById(R.id.goalAmount);
            goalAmountTotal = itemView.findViewById(R.id.goal);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        // background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();
            if(charSequence != null && charSequence.toString().length() > 0) {
                List<Goal> filteredGoals = new ArrayList<>();
                for(Goal goal : goalsAll) {
                    if(goal.title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredGoals.add(goal);
                    }
                }

                filterResults.count = filteredGoals.size();
                filterResults.values = filteredGoals;
            } else {
                synchronized (this) {
                    filterResults.values = goalsAll;
                    filterResults.count = goalsAll.size();
                }
            }

            return filterResults;
        }

        // ui thread
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            goals = (List<Goal>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    public GoalAdapter(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
    }

    @NotNull
    @Override
    public GoalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_card_view, parent, false);
        return new GoalViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        // goal attributes
        String goalStart = goals.get(position).startDate;
        String goalEnd = goals.get(position).endDate;
        String type = goals.get(position).type;
        String tag = goals.get(position).tag;
        String title = goals.get(position).title;
        double amount_total = goals.get(position).amountTotal;

        String startDate_cap = goalStart.substring(0, 5);
        String endDate_cap = goalEnd.substring(0, 5);
        holder.goalName.setText(title);
        holder.goalTag.setText(tag);
        holder.goalDate.setText(startDate_cap + " - " + endDate_cap);

        double amount = 0;
        try {
            String date_format = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(date_format, Locale.US);
            Date start = sdf.parse(goalStart);
            Date end = sdf.parse(goalEnd);
            for (Transaction transaction : transactions) {
                Date trans_date = sdf.parse(transaction.when);
                boolean isDateValid = ((trans_date.equals(start) || trans_date.equals(end)) ||
                        (trans_date.after(start) && trans_date.before(end)));
                if (tag.equals(transaction.tag) && isDateValid)
                    amount += transaction.amount;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        format.setCurrency(Currency.getInstance("USD"));
        String amount_str = format.format(amount);
        String amountTotal_str = format.format(amount_total);

        holder.goalAmount.setText(amount_str);
        holder.goalAmountTotal.setText(amountTotal_str);

        if(type.equals("Income")) {
            if(amount < goals.get(position).amountTotal) {
                // red (goal is not being met)
                holder.goalAmountTotal.setTextColor(Color.parseColor("#EB5757"));
            } else {
                // green (goal is being met)
                holder.goalAmountTotal.setTextColor(Color.parseColor("#6FCF97"));
            }
        } else {
            if(amount > goals.get(position).amountTotal) {
                // red (goal is not being met)
                holder.goalAmountTotal.setTextColor(Color.parseColor("#EB5757"));
            } else {
                // green (goal is being met)
                holder.goalAmountTotal.setTextColor(Color.parseColor("#6FCF97"));
            }
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EditGoalActivity.class);
            intent.putExtra("id", goals.get(position).id);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return goals != null ? goals.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        this.goalsAll = goals;
        getAmounts();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public void getAmounts(){
        if(goals == null) {
            onResult(0, 0);
            return;
        }

        double recurring = 0;
        for(Goal goal : goals){
            recurring += goal.amountTotal;
        }

        onResult(recurring, getItemCount());
    }

    private void onResult(double recurring, int size) {
        if(onAmountsDataReceivedListener != null){
            onAmountsDataReceivedListener.onAmountsDataReceived(recurring, size);
        }
    }

    public interface OnAmountsDataReceivedListener {
        void onAmountsDataReceived(double recurring, int size);
    }

    public void setOnAmountsDataReceivedListener(OnAmountsDataReceivedListener listener){
        this.onAmountsDataReceivedListener = listener;
    }
}
