package com.example.expensetracker.database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ExpenseTrackerDB";
    public static final int DB_Version = 1;
    public static final String T_USERS = "users";
    public static final String T_WALLETS = "wallets";
    public static final String T_CATEGORIES = "categories";
    public static final String T_EXPENSES = "expenses";
    public static final String T_INCOME = "income";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL("CREATE TABLE " + T_USERS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, username TEXT, password TEXT, date_created TEXT)");
        db.execSQL("CREATE TABLE " + T_WALLETS + "(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, name TEXT, type TEXT, balance REAL, FOREIGN KEY (user_id) REFERENCES " + T_USERS + " (id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE " + T_CATEGORIES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, name TEXT, icon TEXT, is_default INTEGER, FOREIGN KEY(user_id) REFERENCES " + T_USERS + " (id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE " + T_EXPENSES + "(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, wallet_id INTEGER, category_id INTEGER, amount REAL, description TEXT, date TEXT, currency TEXT, " +
                " FOREIGN KEY (user_id) REFERENCES " + T_USERS + " (id) ON DELETE CASCADE, " +
                " FOREIGN KEY(wallet_id) REFERENCES " + T_WALLETS + "(id) ON DELETE CASCADE, " +
                " FOREIGN KEY(category_id) REFERENCES " + T_CATEGORIES + "(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE " + T_INCOME + "(id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, wallet_id INTEGER, amount REAL, description TEXT, date TEXT, currency TEXT, " +
                " FOREIGN KEY (user_id) REFERENCES " + T_USERS + " (id) ON DELETE CASCADE, " +
                " FOREIGN KEY(wallet_id) REFERENCES " + T_WALLETS + "(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + T_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + T_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + T_WALLETS);
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }

    public boolean registerUser(String email, String username, String password, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("username", username);
        cv.put("password", password);
        cv.put("date_created", date);
        long result = db.insert(T_USERS, null, cv);
        return result != -1;
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + T_USERS + " WHERE email = ? AND password = ?";
        Cursor c = db.rawQuery(query, new String[]{email, password});
        boolean loggedIn = c.getCount() > 0;
        c.close();
        return loggedIn;
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(T_USERS, new String[]{"id"}, "email = ?", new String[]{email}, null, null, null);
        int userId = -1;
        if (c.moveToFirst()) {
            userId = c.getInt(0);
        }
        c.close();
        return userId;
    }

    public void addWallet(int userId, String name, String type, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("name", name);
        cv.put("type", type);
        cv.put("balance", balance);
        db.insert(T_WALLETS, null, cv);
    }

    public ArrayList<String> getWallets(int userId) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name, type, balance FROM " + T_WALLETS + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            list.add(c.getString(0) + "(" + c.getString(1) + ") - Balance: " + c.getDouble(2));
        }
        c.close();
        return list;
    }

    public ArrayList<Integer> getWalletIds(int userId) {
        ArrayList<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM " + T_WALLETS + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            ids.add(c.getInt(0));
        }
        c.close();
        return ids;
    }

    public ArrayList<String> getCategories(int userId) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT icon, name FROM " + T_CATEGORIES + " WHERE user_id = ? OR is_default = 1";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            list.add(c.getString(0) + " " + c.getString(1));
        }
        c.close();
        return list;
    }

    public ArrayList<Integer> getCategoryIds(int userId) {
        ArrayList<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id FROM " + T_CATEGORIES + " WHERE user_id = ? OR is_default = 1";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            ids.add(c.getInt(0));
        }
        c.close();
        return ids;
    }

    private double getWalletBalance(int walletId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double bal = 0;
        Cursor c = db.rawQuery("SELECT balance FROM " + T_WALLETS + " WHERE id = ?", new String[]{String.valueOf(walletId)});
        if (c.moveToFirst()) bal = c.getDouble(0);
        c.close();
        return bal;
    }

    private void updateWalletBalance(int walletId, double newBalance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("balance", newBalance);
        db.update(T_WALLETS, cv, "id = ?", new String[]{String.valueOf(walletId)});
    }

    public void updateWallet(int walletId, String name, String type, double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("type", type);
        cv.put("balance", balance);
        db.update(T_WALLETS, cv, "id = ?", new String[]{String.valueOf(walletId)});
    }

    public void deleteWallet(int walletId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(T_WALLETS, "id = ?", new String[]{String.valueOf(walletId)});
    }

    public double getTotalUserBalance(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        double totalBalance = 0.0;
        Cursor c = db.rawQuery("SELECT SUM(balance) FROM " + T_WALLETS + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            totalBalance = c.getDouble(0);
        }
        c.close();
        return totalBalance;
    }

    public void addCategory(int userId, String name, String icon, int isDefault) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("name", name);
        cv.put("icon", icon);
        cv.put("is_default", isDefault);
        db.insert(T_CATEGORIES, null, cv);
    }

    public void addExpense(int userId, int walletId, int categoryId, double amount, String desc, String date, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("wallet_id", walletId);
        cv.put("category_id", categoryId);
        cv.put("amount", amount);
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("currency", currency);
        db.insert(T_EXPENSES, null, cv);
        double currentBalance = getWalletBalance(walletId);
        updateWalletBalance(walletId, currentBalance - amount);
    }

    public void updateExpense(int expenseId, int walletId, int categoryId, double amount, String desc, String date, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT wallet_id, amount FROM " + T_EXPENSES + " WHERE id = ?", new String[]{String.valueOf(expenseId)});
        if (c.moveToFirst()) {
            int oldWalletId = c.getInt(0);
            double oldAmount = c.getDouble(1);
            double oldWalletBal = getWalletBalance(oldWalletId);
            updateWalletBalance(oldWalletId, oldWalletBal + oldAmount);
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put("wallet_id", walletId);
        cv.put("category_id", categoryId);
        cv.put("amount", amount);
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("currency", currency);
        db.update(T_EXPENSES, cv, "id = ?", new String[]{String.valueOf(expenseId)});
        double newWalletBal = getWalletBalance(walletId);
        updateWalletBalance(walletId, newWalletBal - amount);
    }

    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT wallet_id, amount FROM " + T_EXPENSES + " WHERE id = ?", new String[]{String.valueOf(expenseId)});
        if (c.moveToFirst()) {
            int walletId = c.getInt(0);
            double amount = c.getDouble(1);
            double currentBalance = getWalletBalance(walletId);
            updateWalletBalance(walletId, currentBalance + amount);
        }
        c.close();
        db.delete(T_EXPENSES, "id = ?", new String[]{String.valueOf(expenseId)});
    }

    public void addIncomeTransaction(int userId, int walletId, double amount, String desc, String date, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("wallet_id", walletId);
        cv.put("amount", amount);
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("currency", currency);
        db.insert(T_INCOME, null, cv);
        double currentBalance = getWalletBalance(walletId);
        updateWalletBalance(walletId, currentBalance + amount);
    }

    public void updateIncomeTransaction(int incomeId, int walletId, double amount, String desc, String date, String currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT wallet_id, amount FROM " + T_INCOME + " WHERE id = ?", new String[]{String.valueOf(incomeId)});
        if (c.moveToFirst()) {
            int oldWalletId = c.getInt(0);
            double oldAmount = c.getDouble(1);
            double oldWalletBal = getWalletBalance(oldWalletId);
            updateWalletBalance(oldWalletId, oldWalletBal - oldAmount);
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put("wallet_id", walletId);
        cv.put("amount", amount);
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("currency", currency);
        db.update(T_INCOME, cv, "id = ?", new String[]{String.valueOf(incomeId)});
        double newWalletBal = getWalletBalance(walletId);
        updateWalletBalance(walletId, newWalletBal + amount);
    }

    public void deleteIncomeTransaction(int incomeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT wallet_id, amount FROM " + T_INCOME + " WHERE id = ?", new String[]{String.valueOf(incomeId)});
        if (c.moveToFirst()) {
            int walletId = c.getInt(0);
            double amount = c.getDouble(1);
            double currentBalance = getWalletBalance(walletId);
            updateWalletBalance(walletId, currentBalance - amount);
        }
        c.close();
        db.delete(T_INCOME, "id = ?", new String[]{String.valueOf(incomeId)});
    }

    public ArrayList<String> getAllTransactionsList(int userId, ArrayList<Integer> idList, ArrayList<Integer> typeList) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String expenseQuery = "SELECT e.id, e.amount, e.currency, e.description, e.date, c.icon, c.name, w.name, 0 as type " +
                "FROM " + T_EXPENSES + " e " +
                "JOIN " + T_CATEGORIES + " c ON e.category_id = c.id " +
                "JOIN " + T_WALLETS + " w ON e.wallet_id = w.id " +
                "WHERE e.user_id = ? " +
                "UNION ALL " +
                "SELECT i.id, i.amount, i.currency, i.description, i.date, '' as icon, 'Income' as name, w.name, 1 as type " +
                "FROM " + T_INCOME + " i " +
                "JOIN " + T_WALLETS + " w ON i.wallet_id = w.id " +
                "WHERE i.user_id = ? " +
                "ORDER BY date DESC";
        Cursor c = db.rawQuery(expenseQuery, new String[]{String.valueOf(userId), String.valueOf(userId)});
        while (c.moveToNext()) {
            int transactionId = c.getInt(0);
            double amount = c.getDouble(1);
            String currency = c.getString(2);
            String description = c.getString(3);
            String date = c.getString(4);
            int type = c.getInt(8);
            idList.add(transactionId);
            typeList.add(type);
            String info;
            if (type == 0) {
                info = "[" + date + "] - €" + amount + " " + currency +
                        " | " + c.getString(5) + " " + c.getString(6) +
                        " | " + description + " (" + c.getString(7) + ")";
            } else {
                info = "[" + date + "] + €" + amount + " " + currency +
                        " | 💲 Income | " + description + " (" + c.getString(7) + ")";
            }
            list.add(info);
        }
        c.close();
        return list;
    }

    public String getExpenseDate(int expenseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT date FROM " + T_EXPENSES + " WHERE id = ?", new String[]{String.valueOf(expenseId)});
        String date = "";
        if (c.moveToFirst()) {
            date = c.getString(0);
        }
        c.close();
        if (date == null || date.isEmpty()) {
            date = getCurrentDate();
        }
        return date;
    }

    public String getIncomeDate(int incomeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT date FROM " + T_INCOME + " WHERE id = ?", new String[]{String.valueOf(incomeId)});
        String date = "";
        if (c.moveToFirst()) {
            date = c.getString(0);
        }
        c.close();
        if (date == null || date.isEmpty()) {
            date = getCurrentDate();
        }
        return date;
    }

    public String getCurrentDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public String getTotalSpending(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        double total = 0.0;
        Cursor c = db.rawQuery("SELECT SUM(amount) FROM " + T_EXPENSES + " WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return "Total Spent: €" + total;
    }

    public String getMonthlySpending(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        double total = 0.0;
        Cursor c = db.rawQuery("SELECT SUM(amount) FROM " + T_EXPENSES + " WHERE user_id = ? AND strftime('%Y-%m', date) = strftime('%Y-%m', 'now')", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return "This Month: €" + total;
    }

    public String getWeeklySpending(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        double total = 0.0;
        Cursor c = db.rawQuery("SELECT SUM(amount) FROM " + T_EXPENSES + " WHERE user_id = ? AND date >= date('now', '-7 days')", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return "This Week: €" + total;
    }

    public String getDailySpending(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        double total = 0.0;
        Cursor c = db.rawQuery("SELECT SUM(amount) FROM " + T_EXPENSES + " WHERE user_id = ? AND date = date('now', 'localtime')", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return "Today: €" + total;
    }

    public ArrayList<String> getSpendingByCategory(int userId) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT c.icon, c.name, SUM(e.amount) FROM " + T_EXPENSES + " e " +
                "JOIN " + T_CATEGORIES + " c ON e.category_id = c.id " +
                "WHERE e.user_id = ? GROUP BY e.category_id";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (c.moveToNext()) {
            list.add(c.getString(0) + " " + c.getString(1) + ": €" + c.getDouble(2));
        }
        c.close();
        return list;
    }
}