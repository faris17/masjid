package com.myapplication.informasimasjid.model;

import java.util.HashMap;
import java.util.Map;

public class DataKeuangan {

    private String id;
    private String nominal;
    private String kategori;
    private String keterangan;
    private String tanggal;
    private Long time;

    public DataKeuangan(){}

    public DataKeuangan(String id,String nominal, String kategori, String keterangan, String tanggal, Long time) {
        this.id = id;
        this.nominal = nominal;
        this.kategori = kategori;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.time = time;
    }

    public Number getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
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
        result.put("nominal", this.nominal);
        result.put("kategori", this.kategori);
        result.put("keterangan", this.keterangan);
        result.put("tanggal", this.tanggal);

        return result;
    }
}
