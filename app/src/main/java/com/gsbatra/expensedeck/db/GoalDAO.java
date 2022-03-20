package com.gsbatra.expensedeck.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDAO {
    @Insert
    void insert(Goal... goals);

    @Update
    void update(Goal... goals);

    @Delete
    void delete(Goal... goals);

    @Query("DELETE FROM goals WHERE rowid = :goalID")
    void delete(int goalID);

    @Query("SELECT * FROM goals WHERE rowid = :goalID")
    Goal getById(int goalID);

    @Query("Select * FROM goals")
    LiveData<List<Goal>> getAll();
}
