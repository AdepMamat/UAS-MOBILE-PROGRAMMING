package com.example.hasilpanenkebun;

public class modelpanen {

    String id;
    String nama;
    String tanggal;
    String jumlah;
    String satuan;
    int harga;
    String keterangan;

    public modelpanen(String id,
                      String nama,
                      String tanggal,
                      String jumlah,
                      String satuan,
                      int harga,
                      String keterangan) {

        this.id = id;
        this.nama = nama;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.harga = harga;
        this.keterangan = keterangan;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public int getHarga() {
        return harga;
    }

    public String getKeterangan() {
        return keterangan;
    }
}