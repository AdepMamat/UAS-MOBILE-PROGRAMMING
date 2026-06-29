package com.example.hasilpanenkebun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotal, tvUserName, tvClock;
    private CardView menuTambah, menuRiwayat, menuStatistik, menuLogout;
    private databaseHelper db;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvTotal = findViewById(R.id.tvTotal);
        tvUserName = findViewById(R.id.tvUserName);
        tvClock = findViewById(R.id.tvClock);
        menuTambah = findViewById(R.id.menuTambah);
        menuRiwayat = findViewById(R.id.menuRiwayat);
        menuStatistik = findViewById(R.id.menuStatistik);
        menuLogout = findViewById(R.id.menuLogout);

        db = new databaseHelper(this);

        // Load User Session
        SharedPreferences pref = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        String name = pref.getString("nama", "Adep Mamat Apriliadi");
        tvUserName.setText(name);

        // Real-time Clock
        startClock();

        menuTambah.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, tambahpanenactivity.class));
        });

        menuRiwayat.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        });

        menuStatistik.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, LaporanActivity.class));
        });

        menuLogout.setOnClickListener(v -> {
            // Hapus sesi
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void startClock() {
        runnable = new Runnable() {
            @Override
            public void run() {
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String currentDate = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(new Date());
                tvClock.setText(currentDate + " | " + currentTime);
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void updateCount() {
        Cursor cursor = db.getAllData();
        if (cursor != null) {
            tvTotal.setText(String.valueOf(cursor.getCount()));
            cursor.close();
        }
    }
}