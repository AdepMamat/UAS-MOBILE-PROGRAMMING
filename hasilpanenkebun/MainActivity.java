package com.example.hasilpanenkebun;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton btnTambah;
    private TextView tvTotalPanen;
    private databaseHelper dbHelper;
    private ArrayList<modelpanen> listPanen;
    private panenadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        btnTambah = findViewById(R.id.btnTambah);
        tvTotalPanen = findViewById(R.id.tvTotalPanen);
        dbHelper = new databaseHelper(this);
        listPanen = new ArrayList<>();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnTambah.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, tambahpanenactivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        listPanen.clear();
        Cursor cursor = dbHelper.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                modelpanen model = new modelpanen(
                        cursor.getString(0), // id
                        cursor.getString(1), // nama
                        cursor.getString(2), // tanggal
                        cursor.getString(3), // jumlah
                        cursor.getString(4), // satuan
                        cursor.getInt(5),    // harga_jual
                        cursor.getString(6)  // keterangan
                );
                listPanen.add(model);
            } while (cursor.moveToNext());
            cursor.close();
        }

        tvTotalPanen.setText(String.valueOf(listPanen.size()));

        adapter = new panenadapter(this, listPanen, tvTotalPanen);
        recyclerView.setAdapter(adapter);
    }
}