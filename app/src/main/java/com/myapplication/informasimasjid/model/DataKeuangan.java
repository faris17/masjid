package com.myapplication.informasimasjid.model;

import java.util.HashMap;
import java.util.Map;

public class DataKeuangan {

    private String id;
    private String harga;
    private String kategori;
    private String keterangan;
    private String tanggal;

    public DataKeuangan(){}

    public DataKeuangan(String id,String harga, String kategori, String keterangan, String tanggal) {
        this.id = id;
        this.harga = harga;
        this.kategori = kategori;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("harga", this.harga);
        result.put("kategori", this.kategori);
        result.put("keterangan", this.keterangan);
        result.put("tanggal", this.tanggal);

        return result;
    }
}
