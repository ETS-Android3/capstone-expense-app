package com.gsbatra.expensedeck.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Subscription.class}, version = 1, exportSchema = false)
public abstract class SubscriptionDatabase extends RoomDatabase {
    public interface SubscriptionListener {
        void onSubscriptionReturned(Subscription subscription);
    }

    public abstract SubscriptionDAO subscriptionDAO();
    private static SubscriptionDatabase INSTANCE;

    public static SubscriptionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SubscriptionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SubscriptionDatabase.class, "subscription_database")
                            .addCallback(createSubscriptionDatabaseCallback)
                            .fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static final Callback createSubscriptionDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static void getSubscription(int id, SubscriptionListener listener) {
        new AsyncTask<Integer, Void, Subscription>() {
            protected Subscription doInBackground(Integer... ids) {
                return INSTANCE.subscriptionDAO().getById(ids[0]);
            }

            protected void onPostExecute(Subscription subscription) {
                super.onPostExecute(subscription);
                listener.onSubscriptionReturned(subscription);
            }
        }.execute(id);
    }

    public static void insert(Subscription subscription) {
        new AsyncTask<Subscription, Void, Void> () {
            protected Void doInBackground(Subscription... subscriptions) {
                INSTANCE.subscriptionDAO().insert(subscriptions);
                return null;
            }
        }.execute(subscription);
    }

    public static void delete(int subscriptionID) {
        new AsyncTask<Integer, Void, Void> () {
            protected Void doInBackground(Integer... ids) {
                INSTANCE.subscriptionDAO().delete(ids[0]);
                return null;
            }
        }.execute(subscriptionID);
    }

    public static void update(Subscription subscription) {
        new AsyncTask<Subscription, Void, Void> () {
            protected Void doInBackground(Subscription... subscriptions) {
                INSTANCE.subscriptionDAO().update(subscriptions);
                return null;
            }
        }.execute(subscription);
    }
}
