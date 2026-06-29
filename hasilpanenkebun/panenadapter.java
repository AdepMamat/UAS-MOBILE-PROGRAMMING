package com.example.hasilpanenkebun;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class panenadapter extends RecyclerView.Adapter<panenadapter.ViewHolder> {

    private Context context;
    private ArrayList<modelpanen> listPanen;
    private databaseHelper dbHelper;
    private TextView tvTotal;

    public panenadapter(Context context, ArrayList<modelpanen> listPanen, TextView tvTotal) {
        this.context = context;
        this.listPanen = listPanen;
        this.tvTotal = tvTotal;
        dbHelper = new databaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_panen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        modelpanen model = listPanen.get(position);
        holder.tvNama.setText(model.getNama());
        holder.tvTanggal.setText(model.getTanggal());
        
        // Format Currency
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        long subtotal = (long) Integer.parseInt(model.getJumlah()) * model.getHarga();
        
        String hargaSatuanStr = formatRupiah.format((long) model.getHarga());
        String totalHargaStr = formatRupiah.format(subtotal);
        
        // Menampilkan: 20000 kg x Rp20.000 = Rp400.000.000
        holder.tvJumlah.setText(model.getJumlah() + " " + model.getSatuan() + " x " + hargaSatuanStr + " = " + totalHargaStr);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, editpanenactivity.class);
            intent.putExtra("ID", model.getId());
            intent.putExtra("NAMA", model.getNama());
            intent.putExtra("TANGGAL", model.getTanggal());
            intent.putExtra("JUMLAH", model.getJumlah());
            intent.putExtra("SATUAN", model.getSatuan());
            intent.putExtra("HARGA", model.getHarga());
            intent.putExtra("KET", model.getKeterangan());
            context.startActivity(intent);
        });

        holder.btnHapus.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                dbHelper.deleteData(model.getId());
                listPanen.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, listPanen.size());
                
                // Update total count on dashboard
                if (tvTotal != null) {
                    tvTotal.setText(String.valueOf(listPanen.size()));
                }

                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPanen.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvTanggal, tvJumlah;
        ImageButton btnEdit, btnHapus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJumlah = itemView.findViewById(R.id.tvJumlah);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}