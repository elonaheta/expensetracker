package com.example.expensetracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBHelper;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    TextView tvTotalBalance;
    ListView lvExpenses;
    Button btnGoToAddExpense, btnGoToAddIncome, navWallets, navStats, navMore;
    DBHelper db;
    int userId;

    ArrayList<String> transactionList;
    ArrayList<Integer> transactionIds;
    ArrayList<Integer> transactionTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        lvExpenses = findViewById(R.id.lvExpenses);
        btnGoToAddExpense = findViewById(R.id.btnGoToAddExpense);
        btnGoToAddIncome = findViewById(R.id.btnGoToAddIncome);
        navWallets = findViewById(R.id.navWalletsFromHome);
        navStats = findViewById(R.id.navStatsFromHome);
        navMore = findViewById(R.id.navMore);

        refreshUI();

        btnGoToAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
            Intent i = new Intent(HomeActivity.this, AddExpenseActivity.class);
            i.putExtra("USER_ID", userId);
            startActivity(i);}
        });

        btnGoToAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, AddIncomeActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
            }
        });

        navWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, WalletsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, StatsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, MoreActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        lvExpenses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int transactionId = transactionIds.get(position);
                final int isIncome = transactionTypes.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setMessage("What do you want to do with this transaction?")
                        .setCancelable(false)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i;
                                if (isIncome == 1) {
                                    i = new Intent(HomeActivity.this, AddIncomeActivity.class);
                                } else {
                                    i = new Intent(HomeActivity.this, AddExpenseActivity.class);
                                }
                                i.putExtra("USER_ID", userId);
                                i.putExtra("TRANSACTION_ID", transactionId);
                                i.putExtra("IS_INCOME", isIncome);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isIncome == 1) {
                                    db.deleteIncomeTransaction(transactionId);
                                } else {
                                    db.deleteExpense(transactionId);
                                }
                                refreshUI();
                                Toast.makeText(HomeActivity.this, "Transaction deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Manage Transaction");
                alert.show();

                return true;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        double totalBalance = db.getTotalUserBalance(userId);
        tvTotalBalance.setText("Total Balance: €" + totalBalance);

        transactionList = new ArrayList<>();
        transactionIds = new ArrayList<>();
        transactionTypes = new ArrayList<>();

        transactionList = db.getAllTransactionsList(userId, transactionIds, transactionTypes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactionList);
        lvExpenses.setAdapter(adapter);
    }
}