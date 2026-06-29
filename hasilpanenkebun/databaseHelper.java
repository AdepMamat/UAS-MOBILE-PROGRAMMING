package com.example.hasilpanenkebun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseHelper extends SQLiteOpenHelper {

    // 1) Nama file database memuat NIM: db_[objek]_[NIM].db
    public static final String DB_NAME = "db_hasil_panen_kebun_231011400837.db";
    public static final int DB_VERSION = 3;

    public databaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table Panen
        db.execSQL(
                "CREATE TABLE panen(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "nama_tanaman TEXT," +
                        "tanggal_panen TEXT," +
                        "jumlah_panen TEXT," +
                        "satuan TEXT," +
                        "harga_jual INTEGER," +
                        "keterangan TEXT)"
        );

        // Table Users
        db.execSQL(
                "CREATE TABLE users(" +
                        "username TEXT PRIMARY KEY," +
                        "password TEXT," +
                        "nama_lengkap TEXT)"
        );

        // Memasukkan 8 data awal otomatis (Seeding)
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // --- DATA USERS (Akun Default untuk Login) ---
        ContentValues userCv = new ContentValues();
        userCv.put("username", "admin");
        userCv.put("password", "admin");
        userCv.put("nama_lengkap", "Adep Mamat Apriliadi");
        db.insert("users", null, userCv);

        // --- 8 DATA PANEN (3) ---
        ContentValues cv = new ContentValues();

        // 1. Data memuat Nama & NIM (2)
        cv.put("nama_tanaman", "Adep Mamat Apriliadi - 231011400837");
        cv.put("tanggal_panen", "20/05/2024");
        cv.put("jumlah_panen", "100");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 50000);
        cv.put("keterangan", "Data Identitas Mahasiswa");
        db.insert("panen", null, cv);
        cv.clear();

        // 2. Data Padi
        cv.put("nama_tanaman", "Padi Gogo");
        cv.put("tanggal_panen", "15/05/2024");
        cv.put("jumlah_panen", "500");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 12000);
        cv.put("keterangan", "Panen Sawah Utara");
        db.insert("panen", null, cv);
        cv.clear();

        // 3. Data Jagung
        cv.put("nama_tanaman", "Jagung Manis");
        cv.put("tanggal_panen", "10/05/2024");
        cv.put("jumlah_panen", "200");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 8000);
        cv.put("keterangan", "Kualitas Super");
        db.insert("panen", null, cv);
        cv.clear();

        // 4. Data Tomat
        cv.put("nama_tanaman", "Tomat Cherry");
        cv.put("tanggal_panen", "18/05/2024");
        cv.put("jumlah_panen", "50");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 15000);
        cv.put("keterangan", "Segar dari Kebun");
        db.insert("panen", null, cv);
        cv.clear();

        // 5. Data Cabai
        cv.put("nama_tanaman", "Cabai Rawit");
        cv.put("tanggal_panen", "19/05/2024");
        cv.put("jumlah_panen", "30");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 45000);
        cv.put("keterangan", "Harga sedang naik");
        db.insert("panen", null, cv);
        cv.clear();

        // 6. Data Mangga
        cv.put("nama_tanaman", "Mangga Arumanis");
        cv.put("tanggal_panen", "05/05/2024");
        cv.put("jumlah_panen", "150");
        cv.put("satuan", "Kg");
        cv.put("harga_jual", 20000);
        cv.put("keterangan", "Musim panen raya");
        db.insert("panen", null, cv);
        cv.clear();

        // 7. Data Durian
        cv.put("nama_tanaman", "Durian Montong");
        cv.put("tanggal_panen", "01/05/2024");
        cv.put("jumlah_panen", "20");
        cv.put("satuan", "Buah");
        cv.put("harga_jual", 100000);
        cv.put("keterangan", "Lahan Belakang");
        db.insert("panen", null, cv);
        cv.clear();

        // 8. Data Kelapa
        cv.put("nama_tanaman", "Kelapa Muda");
        cv.put("tanggal_panen", "25/04/2024");
        cv.put("jumlah_panen", "100");
        cv.put("satuan", "Buah");
        cv.put("harga_jual", 15000);
        cv.put("keterangan", "Pesanan Kios");
        db.insert("panen", null, cv);
        cv.clear();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS users(" +
                            "username TEXT PRIMARY KEY," +
                            "password TEXT," +
                            "nama_lengkap TEXT)"
            );
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE panen ADD COLUMN harga_jual INTEGER DEFAULT 0");
        }
    }

    // --- USER METHODS ---

    public boolean registerUser(String username, String password, String nama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("nama_lengkap", nama);
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public String getFullName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nama_lengkap FROM users WHERE username=?", new String[]{username});
        String name = "User";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    // --- PANEN METHODS ---

    public boolean insertData(String nama, String tanggal, String jumlah, String satuan, int harga, String ket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nama_tanaman", nama);
        cv.put("tanggal_panen", tanggal);
        cv.put("jumlah_panen", jumlah);
        cv.put("satuan", satuan);
        cv.put("harga_jual", harga);
        cv.put("keterangan", ket);
        long result = db.insert("panen", null, cv);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM panen ORDER BY id DESC", null);
    }

    public boolean updateData(String id, String nama, String tanggal, String jumlah, String satuan, int harga, String ket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nama_tanaman", nama);
        cv.put("tanggal_panen", tanggal);
        cv.put("jumlah_panen", jumlah);
        cv.put("satuan", satuan);
        cv.put("harga_jual", harga);
        cv.put("keterangan", ket);
        db.update("panen", cv, "id=?", new String[]{id});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("panen", "id=?", new String[]{id});
    }

    public long getTotalPendapatan() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(CAST(jumlah_panen AS INTEGER) * harga_jual) FROM panen", null);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }
}