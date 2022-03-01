package com.gsbatra.expensedeck.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SubscriptionDAO {
    @Insert
    void insert(Subscription... subscriptions);

    @Update
    void update(Subscription... subscriptions);

    @Delete
    void delete(Subscription... subscriptions);

    @Query("DELETE FROM subscriptions WHERE rowid = :subscriptionID")
    void delete(int subscriptionID);

    @Query("SELECT * FROM subscriptions WHERE rowid = :subscriptionID")
    Subscription getById(int subscriptionID);

    @Query("Select * FROM subscriptions")
    LiveData<List<Subscription>> getAll();
}
