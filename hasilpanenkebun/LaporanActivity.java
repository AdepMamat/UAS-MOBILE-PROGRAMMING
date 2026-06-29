package com.example.hasilpanenkebun;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LaporanActivity extends AppCompatActivity {

    private TextView tvTotalPendapatan, tvTopCrop;
    private View itemHarian, itemMingguan, itemBulanan;
    private BarChart barChart;
    private PieChart pieChart;
    private databaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        tvTotalPendapatan = findViewById(R.id.tvTotalPendapatan);
        tvTopCrop = findViewById(R.id.tvTopCrop);
        itemHarian = findViewById(R.id.itemHarian);
        itemMingguan = findViewById(R.id.itemMingguan);
        itemBulanan = findViewById(R.id.itemBulanan);
        
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        
        db = new databaseHelper(this);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        setupRekapItems();
        loadLaporan();
    }

    private void setupRekapItems() {
        ((TextView) itemHarian.findViewById(R.id.tvLabel)).setText("Pendapatan Hari Ini");
        ((ImageView) itemHarian.findViewById(R.id.ivIcon)).setImageResource(android.R.drawable.ic_menu_today);

        ((TextView) itemMingguan.findViewById(R.id.tvLabel)).setText("7 Hari Terakhir");
        ((ImageView) itemMingguan.findViewById(R.id.ivIcon)).setImageResource(android.R.drawable.ic_menu_myplaces);

        ((TextView) itemBulanan.findViewById(R.id.tvLabel)).setText("Bulan Ini");
        ((ImageView) itemBulanan.findViewById(R.id.ivIcon)).setImageResource(android.R.drawable.ic_menu_month);
    }

    private void loadLaporan() {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        tvTotalPendapatan.setText(formatRupiah.format(db.getTotalPendapatan()));

        long harian = 0;
        long mingguan = 0;
        long bulanan = 0;
        
        HashMap<String, Float> monthlyData = new HashMap<>();
        HashMap<String, Float> cropData = new HashMap<>();

        Cursor cursor = db.getAllData();
        if (cursor != null && cursor.moveToFirst()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat monthSdf = new SimpleDateFormat("MMM", Locale.getDefault());
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            
            do {
                try {
                    String cropName = cursor.getString(1);
                    String dateStr = cursor.getString(2);
                    float jumlah = Float.parseFloat(cursor.getString(3));
                    int harga = cursor.getInt(5);
                    long subtotal = (long) jumlah * harga;

                    Date harvestDate = sdf.parse(dateStr);
                    
                    // Rekap Waktu
                    if (isSameDay(harvestDate, today)) harian += subtotal;
                    
                    cal.setTime(today);
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    if (harvestDate.after(cal.getTime())) mingguan += subtotal;

                    if (isSameMonth(harvestDate, today)) bulanan += subtotal;

                    // Bar Chart Data (Monthly)
                    String month = monthSdf.format(harvestDate);
                    Float currentMonthVal = monthlyData.get(month);
                    monthlyData.put(month, (currentMonthVal == null ? 0f : currentMonthVal) + jumlah);

                    // Pie Chart Data (Crop Distribution)
                    Float currentCropVal = cropData.get(cropName);
                    cropData.put(cropName, (currentCropVal == null ? 0f : currentCropVal) + jumlah);

                } catch (Exception e) { e.printStackTrace(); }
            } while (cursor.moveToNext());
            cursor.close();
        }

        ((TextView) itemHarian.findViewById(R.id.tvValue)).setText(formatRupiah.format(harian));
        ((TextView) itemMingguan.findViewById(R.id.tvValue)).setText(formatRupiah.format(mingguan));
        ((TextView) itemBulanan.findViewById(R.id.tvValue)).setText(formatRupiah.format(bulanan));

        // Find Top Crop
        String topCropName = "-";
        float maxYield = 0;
        for (Map.Entry<String, Float> entry : cropData.entrySet()) {
            if (entry.getValue() > maxYield) {
                maxYield = entry.getValue();
                topCropName = entry.getKey();
            }
        }
        tvTopCrop.setText("Tanaman Terbanyak: " + topCropName + " (" + maxYield + " kg)");

        displayBarChart(monthlyData);
        displayPieChart(cropData);
    }

    private void displayBarChart(HashMap<String, Float> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            labels.add(entry.getKey());
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Hasil Panen");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void displayPieChart(HashMap<String, Float> data) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Distribusi Tanaman");
        pieChart.animateXY(1000, 1000);
        pieChart.invalidate();
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameMonth(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }
}