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

public class WalletsActivity extends AppCompatActivity {

    TextView tvTotalBalance;
    ListView lvWallets;
    Button btnOpenAddWalletDialog, navHome, navStats, navMore;
    DBHelper db;
    int userId;
    ArrayList<String> walletList;
    ArrayList<Integer> walletIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wallets);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);

        tvTotalBalance = findViewById(R.id.tvWalletTotalBalance);
        lvWallets = findViewById(R.id.lvWallets);
        btnOpenAddWalletDialog = findViewById(R.id.btnOpenAddWalletDialog);
        navHome = findViewById(R.id.navHome);
        navStats = findViewById(R.id.navStats);
        navMore = findViewById(R.id.navMore);

        refreshUI();

        navMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WalletsActivity.this, MoreActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        btnOpenAddWalletDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWalletDialog(-1, "", "", 0.0);
            }
        });

        lvWallets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int walletId = walletIds.get(position);
                String walletInfo = walletList.get(position);

                String walletName = walletInfo.split("\\(")[0].trim();
                String walletType = walletInfo.substring(walletInfo.indexOf("(") + 1, walletInfo.indexOf(")")).trim();
                double walletBalance = Double.parseDouble(walletInfo.substring(walletInfo.lastIndexOf(": ") + 2).trim());

                AlertDialog.Builder builder = new AlertDialog.Builder(WalletsActivity.this);
                builder.setMessage("What do you want to do with this wallet?")
                        .setCancelable(false)
                        .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showWalletDialog(walletId, walletName, walletType, walletBalance);
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteWallet(walletId);
                                Toast.makeText(WalletsActivity.this, "Wallet deleted!", Toast.LENGTH_SHORT).show();
                                refreshUI();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.setTitle("Manage Wallet");
                alert.show();
                return true;
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WalletsActivity.this, HomeActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });

        navStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WalletsActivity.this, StatsActivity.class);
                i.putExtra("USER_ID", userId);
                startActivity(i);
                finish();
            }
        });
    }

    private void refreshUI() {
        double totalBalance = db.getTotalUserBalance(userId);
        tvTotalBalance.setText("Total Balance: €" + totalBalance);

        walletList = db.getWallets(userId);
        walletIds = db.getWalletIds(userId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, walletList);
        lvWallets.setAdapter(adapter);
    }

    private void showWalletDialog(final int walletId, String existingName, String existingType, double existingBalance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etName = new EditText(this);
        etName.setHint("Wallet Name");
        if (walletId != -1) {
            etName.setText(existingName);
        }
        layout.addView(etName);

        final EditText etType = new EditText(this);
        etType.setHint("Type (e.g. Cash, Card)");
        if (walletId != -1) {
            etType.setText(existingType);
        }
        layout.addView(etType);

        final EditText etBalance = new EditText(this);
        etBalance.setHint("Balance");
        etBalance.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (walletId != -1) {
            etBalance.setText(String.valueOf(existingBalance));
        }
        layout.addView(etBalance);

        builder.setView(layout);

        if (walletId == -1) {
            builder.setTitle("Add New Wallet");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = etName.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    String balanceStr = etBalance.getText().toString().trim();

                    if (!name.isEmpty() && !type.isEmpty() && !balanceStr.isEmpty()) {
                        db.addWallet(userId, name, type, Double.parseDouble(balanceStr));
                        refreshUI();
                        Toast.makeText(WalletsActivity.this, "Wallet Added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WalletsActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            builder.setTitle("Edit Wallet");
            builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = etName.getText().toString().trim();
                    String type = etType.getText().toString().trim();
                    String balanceStr = etBalance.getText().toString().trim();

                    if (!name.isEmpty() && !type.isEmpty() && !balanceStr.isEmpty()) {
                        db.updateWallet(walletId, name, type, Double.parseDouble(balanceStr));
                        refreshUI();
                        Toast.makeText(WalletsActivity.this, "Wallet Edited!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WalletsActivity.this, "Fill in all fields!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        builder.setNegativeButton("Cancel", null);

        AlertDialog alert = builder.create();
        alert.show();
    }
}