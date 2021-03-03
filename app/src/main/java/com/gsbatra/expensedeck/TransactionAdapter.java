package com.gsbatra.expensedeck;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    static class Transaction {
        String name;
        String category;
        String amount;

        Transaction (String name, String category, String amount) {
            this.name = name;
            this.category = category;
            this.amount = amount;
        }
    }

    private List<Transaction> data;

    public TransactionAdapter(List<Transaction> data){
        this.data = data;
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card_view, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, int position) {
        holder.transactionName.setText(data.get(position).name);
        holder.transactionCategory.setText(data.get(position).category);
        holder.transactionAmount.setText(data.get(position).amount);
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView transactionCard;
        private TextView transactionName;
        private TextView transactionCategory;
        private TextView transactionAmount;

        public ViewHolder(View view) {
            super(view);
            transactionCard = view.findViewById(R.id.transactionCardView);
            transactionName = view.findViewById(R.id.transactionName);
            transactionCategory = view.findViewById(R.id.transactionCategory);
            transactionAmount = view.findViewById(R.id.transactionAmount);
        }
    }
}
