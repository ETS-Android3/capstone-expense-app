package com.gsbatra.expensedeck.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Transaction.class}, version = 1, exportSchema = false)
public abstract class TransactionDatabase extends RoomDatabase {
    public interface TransactionListener {
        void onTransactionReturned(Transaction transaction);
    }

    public abstract TransactionDAO transactionDAO();
    private static TransactionDatabase INSTANCE;

    public static TransactionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TransactionDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TransactionDatabase.class, "transaction_database")
                            .addCallback(createTransactionDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Note this call back will be run
    private static final RoomDatabase.Callback createTransactionDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static void getTransaction(int id, TransactionListener listener) {
        new AsyncTask<Integer, Void, Transaction>() {
            protected Transaction doInBackground(Integer... ids) {
                return INSTANCE.transactionDAO().getById(ids[0]);
            }

            protected void onPostExecute(Transaction transaction) {
                super.onPostExecute(transaction);
                listener.onTransactionReturned(transaction);
            }
        }.execute(id);
    }

    public static void insert(Transaction transaction) {
        new AsyncTask<Transaction, Void, Void> () {
            protected Void doInBackground(Transaction... transactions) {
                INSTANCE.transactionDAO().insert(transactions);
                return null;
            }
        }.execute(transaction);
    }

    public static void delete(int transactionID) {
        new AsyncTask<Integer, Void, Void> () {
            protected Void doInBackground(Integer... ids) {
                INSTANCE.transactionDAO().delete(ids[0]);
                return null;
            }
        }.execute(transactionID);
    }

    public static void update(Transaction transaction) {
        new AsyncTask<Transaction, Void, Void> () {
            protected Void doInBackground(Transaction... transactions) {
                INSTANCE.transactionDAO().update(transactions);
                return null;
            }
        }.execute(transaction);
    }
}
