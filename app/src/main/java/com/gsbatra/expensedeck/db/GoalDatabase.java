package com.gsbatra.expensedeck.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public abstract class GoalDatabase extends RoomDatabase {

    public interface GoalListener {
        void onGoalReturned(Goal goal);
    }

    public abstract GoalDAO goalDAO();
    private static GoalDatabase INSTANCE;

    public static GoalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GoalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GoalDatabase.class, "goal_database")
                            .addCallback(createGoalDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static final Callback createGoalDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static void getGoal(int id, GoalListener listener) {
        new AsyncTask<Integer, Void, Goal>() {
            protected Goal doInBackground(Integer... ids) {
                return INSTANCE.goalDAO().getById(ids[0]);
            }

            protected void onPostExecute(Goal goal) {
                super.onPostExecute(goal);
                listener.onGoalReturned(goal);
            }
        }.execute(id);
    }

    public static void insert(Goal goal) {
        new AsyncTask<Goal, Void, Void> () {
            protected Void doInBackground(Goal... goals) {
                INSTANCE.goalDAO().insert(goals);
                return null;
            }
        }.execute(goal);
    }

    public static void delete(int goalID) {
        new AsyncTask<Integer, Void, Void> () {
            protected Void doInBackground(Integer... ids) {
                INSTANCE.goalDAO().delete(ids[0]);
                return null;
            }
        }.execute(goalID);
    }

    public static void update(Goal goal) {
        new AsyncTask<Goal, Void, Void> () {
            protected Void doInBackground(Goal... goals) {
                INSTANCE.goalDAO().update(goals);
                return null;
            }
        }.execute(goal);
    }
}
