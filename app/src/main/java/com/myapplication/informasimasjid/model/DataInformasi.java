package com.myapplication.informasimasjid.model;

import java.util.HashMap;
import java.util.Map;

public class DataInformasi {

    String id;
    String judul;
    String infokontak;
    String tanggal;
    String deskripsi;
    Long created;

    DataInformasi(){}

    public DataInformasi(String id, String judul, String infokontak, String tanggal, String deskripsi, Long created) {
        this.id = id;
        this.judul = judul;
        this.infokontak = infokontak;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.created = created;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getInfokontak() {
        return infokontak;
    }

    public void setInfokontak(String infokontak) {
        this.infokontak = infokontak;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("judul", this.judul);
        result.put("infokontak", this.infokontak);
        result.put("tanggal", this.tanggal);
        result.put("deskripsi", this.deskripsi);
        result.put("created", this.created);

        return result;
    }
}
