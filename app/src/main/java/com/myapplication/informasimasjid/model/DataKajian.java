package com.myapplication.informasimasjid.model;

import java.util.HashMap;
import java.util.Map;

public class DataKajian {

    String id;
    String judul;
    String tema;
    String time;
    String tempat;
    String narasumber;
    String keterangan;
    String hari;
    String link;

    DataKajian(){};

    public DataKajian(String id, String judul, String tema, String time, String tempat, String narasumber, String keterangan, String hari, String link) {
        this.id = id;
        this.judul = judul;
        this.tema = tema;
        this.time = time;
        this.tempat = tempat;
        this.narasumber = narasumber;
        this.keterangan = keterangan;
        this.hari = hari;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTempat() {
        return tempat;
    }

    public void setTempat(String tempat) {
        this.tempat = tempat;
    }

    public String getNarasumber() {
        return narasumber;
    }

    public void setNarasumber(String narasumber) {
        this.narasumber = narasumber;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }


    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("hari", this.hari);
        result.put("judul", this.judul);
        result.put("tema", this.tema);
        result.put("tempat", this.tempat);
        result.put("keterangan", this.keterangan);
        result.put("narasumber", this.narasumber);
        result.put("time", this.time);

        return result;
    }

}
