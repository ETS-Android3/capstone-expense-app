package com.gsbatra.expensedeck.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SubscriptionViewModel extends AndroidViewModel {
    private LiveData<List<Subscription>> subscriptions;

    public SubscriptionViewModel(@NonNull Application application) {
        super(application);
        subscriptions = SubscriptionDatabase.getDatabase(getApplication()).subscriptionDAO().getAll();
    }

    public LiveData<List<Subscription>> getAllSubscriptions() {
        return subscriptions;
    }

//    public LiveData<List<Goal>> getIncomeTransactions() {
//        transactions = TransactionDatabase.getDatabase(getApplication()).transactionDAO().getIncome();
//        return transactions;
//    }
//
//    public LiveData<List<Transaction>> getExpenseTransactions() {
//        transactions = TransactionDatabase.getDatabase(getApplication()).transactionDAO().getExpenses();
//        return transactions;
//    }
}
