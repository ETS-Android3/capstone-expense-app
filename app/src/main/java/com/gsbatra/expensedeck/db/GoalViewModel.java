package com.gsbatra.expensedeck.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GoalViewModel extends AndroidViewModel {
    private LiveData<List<Goal>> goals;

    public GoalViewModel(@NonNull Application application) {
        super(application);
        goals = GoalDatabase.getDatabase(getApplication()).goalDAO().getAll();
    }

    public LiveData<List<Goal>> getAllGoals() {
        return goals;
    }
}
