package com.example.hasilpanenkebun;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNamaLengkap, etUsername, etPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private databaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNamaLengkap = findViewById(R.id.etNamaLengkap);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        db = new databaseHelper(this);

        btnRegister.setOnClickListener(v -> {
            String nama = etNamaLengkap.getText().toString();
            String user = etUsername.getText().toString();
            String pass = etPassword.getText().toString();

            if (nama.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                if (db.registerUser(user, pass, nama)) {
                    Toast.makeText(this, "Pendaftaran Berhasil! Silakan Login", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal Daftar! Username mungkin sudah ada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}