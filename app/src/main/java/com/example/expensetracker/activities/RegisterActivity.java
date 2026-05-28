package com.example.expensetracker.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.*;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etEmail, etPassword;
    Button btnRegister;
    TextView tvGoToLogin;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        db = new DBHelper(this);
        etUsername = findViewById(R.id.etRegisterUsername);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim().toLowerCase();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String currentDate = db.getCurrentDate();
                boolean uRegjistrua = db.registerUser(email, username, password, currentDate);

                if (uRegjistrua) {
                    int newUserId = db.getUserId(email);

                    if (newUserId != -1) {
                        db.addWallet(newUserId, "Main Wallet", "Cash", 0.0);
                        db.addCategory(newUserId, "Food", "🍔", 0);
                        db.addCategory(newUserId, "Transport", "🚗", 0);
                        db.addCategory(newUserId, "Fun", "🎬", 0);
                        db.addCategory(newUserId, "Bills", "💡", 0);

                        Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "This email already exists!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}