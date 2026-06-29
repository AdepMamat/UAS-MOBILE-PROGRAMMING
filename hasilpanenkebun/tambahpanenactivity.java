package com.example.hasilpanenkebun;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class tambahpanenactivity extends AppCompatActivity {

    private EditText etNama, etTanggal, etJumlah, etSatuan, etHarga, etKet;
    private TextView tvTotalPreview;
    private Button btnSimpan;
    private databaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_panen);

        etNama = findViewById(R.id.etNama);
        etTanggal = findViewById(R.id.etTanggal);
        etJumlah = findViewById(R.id.etJumlah);
        etSatuan = findViewById(R.id.etSatuan);
        etHarga = findViewById(R.id.etHarga);
        etKet = findViewById(R.id.etKet);
        tvTotalPreview = findViewById(R.id.tvTotalPreview);
        btnSimpan = findViewById(R.id.btnSimpan);
        dbHelper = new databaseHelper(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Setup Date Picker
        etTanggal.setFocusable(false);
        etTanggal.setOnClickListener(v -> showDatePicker());

        // Live Calculation
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etJumlah.addTextChangedListener(watcher);
        etHarga.addTextChangedListener(watcher);

        btnSimpan.setOnClickListener(v -> {
            String nama = etNama.getText().toString();
            String tanggal = etTanggal.getText().toString();
            String jumlah = etJumlah.getText().toString();
            String satuan = etSatuan.getText().toString();
            String hargaStr = etHarga.getText().toString();
            String ket = etKet.getText().toString();

            if (nama.isEmpty() || tanggal.isEmpty() || jumlah.isEmpty() || satuan.isEmpty() || hargaStr.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int harga = Integer.parseInt(hargaStr);
                    boolean isInserted = dbHelper.insertData(nama, tanggal, jumlah, satuan, harga, ket);
                    if (isInserted) {
                        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Harga harus berupa angka", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateTotal() {
        String qtyStr = etJumlah.getText().toString();
        String priceStr = etHarga.getText().toString();
        
        if (!qtyStr.isEmpty() && !priceStr.isEmpty()) {
            try {
                long qty = Long.parseLong(qtyStr);
                long price = Long.parseLong(priceStr);
                long total = qty * price;
                
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                tvTotalPreview.setText("Estimasi Total: " + formatRupiah.format(total));
            } catch (Exception e) {
                tvTotalPreview.setText("Estimasi Total: Rp 0");
            }
        } else {
            tvTotalPreview.setText("Estimasi Total: Rp 0");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month1 + 1), year1);
                    etTanggal.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }
}