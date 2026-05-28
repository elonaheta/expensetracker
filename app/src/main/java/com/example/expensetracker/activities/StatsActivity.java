package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBHelper;
import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    TextView tvToday, tvWeek, tvMonth, tvTotal;
    ListView lvCategoryStats;
    Button navHome, navWallets, navMore;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        tvToday = findViewById(R.id.tvStatToday);
        tvWeek = findViewById(R.id.tvStatWeek);
        tvMonth = findViewById(R.id.tvStatMonth);
        tvTotal = findViewById(R.id.tvStatTotal);
        lvCategoryStats = findViewById(R.id.lvCategoryStats);
        navHome = findViewById(R.id.navHomeFromStats);
        navWallets = findViewById(R.id.navWalletsFromStats);
        navMore = findViewById(R.id.navMore);

        displayStats();
        navMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, MoreActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, HomeActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StatsActivity.this, WalletsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });
    }

    private void displayStats() {
        tvToday.setText(db.getDailySpending(userId));
        tvWeek.setText(db.getWeeklySpending(userId));
        tvMonth.setText(db.getMonthlySpending(userId));
        tvTotal.setText(db.getTotalSpending(userId));

        ArrayList<String> categoryData = db.getSpendingByCategory(userId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryData);
        lvCategoryStats.setAdapter(adapter);
    }
}