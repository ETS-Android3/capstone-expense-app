package com.gsbatra.expensedeck.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TransactionViewModel extends AndroidViewModel {
    private LiveData<List<Transaction>> transactions;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        transactions = TransactionDatabase.getDatabase(getApplication()).transactionDAO().getAll();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactions;
    }

    public LiveData<List<Transaction>> getIncomeTransactions() {
        transactions = TransactionDatabase.getDatabase(getApplication()).transactionDAO().getIncome();
        return transactions;
    }

    public LiveData<List<Transaction>> getExpenseTransactions() {
        transactions = TransactionDatabase.getDatabase(getApplication()).transactionDAO().getExpenses();
        return transactions;
    }
}
