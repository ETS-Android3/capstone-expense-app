package com.gsbatra.expensedeck.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TransactionDAO {
    @Insert
    void insert(Transaction... transactions);

    @Update
    void update(Transaction... transactions);

    @Delete
    void delete(Transaction... user);

    @Query("DELETE FROM transactions WHERE rowid = :transactionID")
    void delete(int transactionID);

    @Query("SELECT * FROM transactions WHERE rowid = :transactionID")
    Transaction getById(int transactionID);

    @Query("Select * FROM transactions")
    LiveData<List<Transaction>> getAll();

    @Query("Select * FROM transactions WHERE type = 'Expense'")
    LiveData<List<Transaction>> getExpenses();

    @Query("Select * FROM transactions WHERE type = 'Income'")
    LiveData<List<Transaction>> getIncome();
}
