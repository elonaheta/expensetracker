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

public class MoreActivity extends AppCompatActivity {

    Button btnOpenAddCategoryDialog, btnLogout, navHome, navWallets, navStats;
    DBHelper db;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        btnOpenAddCategoryDialog = findViewById(R.id.btnOpenAddCategoryDialog);
        navHome = findViewById(R.id.navHomeFromMore);
        navWallets = findViewById(R.id.navWalletsFromMore);
        navStats = findViewById(R.id.navStatsFromMore);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnOpenAddCategoryDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MoreActivity.this, HomeActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MoreActivity.this, WalletsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MoreActivity.this, StatsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etIcon = new EditText(this);
        etIcon.setHint("Add an icon (e.g. 🛒)");
        layout.addView(etIcon);

        final EditText etName = new EditText(this);
        etName.setHint("Category Name");
        layout.addView(etName);

        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String icon = etIcon.getText().toString().trim();
                String name = etName.getText().toString().trim();

                if (!name.isEmpty() && !icon.isEmpty()) {
                    db.addCategory(userId, name, icon, 0);
                    Toast.makeText(MoreActivity.this, "New Category Added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MoreActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}