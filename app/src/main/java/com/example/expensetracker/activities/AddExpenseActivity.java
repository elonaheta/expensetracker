package com.example.expensetracker.activities;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.example.expensetracker.database.DBHelper;
import java.util.ArrayList;
import com.example.expensetracker.R;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etAmount, etDescription, etCurrency;
    Spinner spWallets, spCategories;
    Button btnSaveExpense, btnCancel;
    DBHelper db;
    int userId;
    int transactionId;

    ArrayList<String> walletNames;
    ArrayList<Integer> walletIds;
    ArrayList<String> categoryNames;
    ArrayList<Integer> categoryIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_expense);

        db = new DBHelper(this);
        userId = getIntent().getIntExtra("USER_ID", -1);
        transactionId = getIntent().getIntExtra("TRANSACTION_ID", -1);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        etCurrency = findViewById(R.id.etCurrency);
        spWallets = findViewById(R.id.spWallets);
        spCategories = findViewById(R.id.spCategories);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnCancel = findViewById(R.id.btnCancel);

        setupSpinners();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSaveExpense.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String amountStr = etAmount.getText().toString().trim();
                String desc = etDescription.getText().toString().trim();
                String currency = etCurrency.getText().toString().trim();

                if (amountStr.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(AddExpenseActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currency.isEmpty()) {
                    currency = "EUR";
                }

                double amount = Double.parseDouble(amountStr);
                int selectedWalletId = walletIds.get(spWallets.getSelectedItemPosition());
                int selectedCategoryId = categoryIds.get(spCategories.getSelectedItemPosition());

                if (transactionId != -1) {
                    String transactionDate = db.getExpenseDate(transactionId);
                    db.updateExpense(transactionId, selectedWalletId, selectedCategoryId, amount, desc, transactionDate, currency);
                    Toast.makeText(AddExpenseActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    String currentDate = db.getCurrentDate();
                    db.addExpense(userId, selectedWalletId, selectedCategoryId, amount, desc, currentDate, currency);
                    Toast.makeText(AddExpenseActivity.this, "Expense Added!", Toast.LENGTH_SHORT).show();
                }

                finish();
            }
        });
    }

    private void setupSpinners() {
        walletNames = db.getWallets(userId);
        walletIds = db.getWalletIds(userId);

        categoryNames = db.getCategories(userId);
        categoryIds = db.getCategoryIds(userId);

        ArrayAdapter<String> wAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletNames);
        wAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWallets.setAdapter(wAdapter);

        ArrayAdapter<String> cAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        cAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(cAdapter);
    }
}