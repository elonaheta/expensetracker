package com.example.expensetracker.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.expensetracker.database.DBHelper;
import java.util.ArrayList;
import com.example.expensetracker.R;

public class AddIncomeActivity extends AppCompatActivity {

    EditText etAmount, etDescription, etCurrency;
    Spinner spWallets;
    Button btnSaveIncome, btnCancel;
    DBHelper db;
    int userId;
    int transactionId;

    ArrayList<String> walletNames;
    ArrayList<Integer> walletIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_income);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etCurrency = findViewById(R.id.etCurrency);
        spWallets = findViewById(R.id.spWallets);
        btnSaveIncome = findViewById(R.id.btnSaveIncome);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setupWalletSpinner();

        btnSaveIncome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String amountStr = etAmount.getText().toString().trim();
                String desc = etDescription.getText().toString().trim();
                String currency = etCurrency.getText().toString().trim();

                if (amountStr.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(AddIncomeActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currency.isEmpty()) {
                    currency = "EUR";
                }

                double amount = Double.parseDouble(amountStr);
                int selectedWalletId = walletIds.get(spWallets.getSelectedItemPosition());

                if (transactionId != -1) {
                    String transactionDate = db.getIncomeDate(transactionId);
                    db.updateIncomeTransaction(transactionId, selectedWalletId, amount, desc, transactionDate, currency);
                    Toast.makeText(AddIncomeActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    String currentDate = db.getCurrentDate();
                    db.addIncomeTransaction(userId, selectedWalletId, amount, desc, currentDate, currency);
                    Toast.makeText(AddIncomeActivity.this, "Income Added", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    private void setupWalletSpinner() {
        walletNames = db.getWallets(userId);
        walletIds = db.getWalletIds(userId);

        ArrayAdapter<String> wAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletNames);
        wAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWallets.setAdapter(wAdapter);
    }
}