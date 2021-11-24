package com.myapplication.informasimasjid.library;

import android.content.Context;
import android.content.SharedPreferences;

public class Session{
    private static final String ses_aplikasi       = "aplikasi";
    public static final String Ses_boarding      = "false";
    public static final String Ses_nama      = "nama";
    public static final String Ses_level = "level";

    private static final String SUDAH_LOGIN = "SudahLogin";

    SharedPreferences sesi;
    SharedPreferences.Editor Editor;

    public Session(Context context){
        sesi = context.getSharedPreferences(ses_aplikasi, Context.MODE_PRIVATE);
        Editor = sesi.edit();
    }

    public void saveSPString(String keySP, String value){
        Editor.putString(keySP, value);
        Editor.commit();
    }
    public void saveBoarding(String keyBoarding, boolean value){
        Editor.putBoolean(keyBoarding, value);
        Editor.commit();
    }

    public void HapusSession(){
        Editor.clear();
        Editor.commit();
    }

    public void saveSPInt(String keySP, int value){
        Editor.putInt(keySP, value);
        Editor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        Editor.putBoolean(keySP, value);
        Editor.commit();
    }

    public  String getSes_nama() {
        return sesi.getString(Ses_nama, "");
    }

    public  String getSes_level() {
        return sesi.getString(Ses_level, "");
    }


    public Boolean getSudahLogin(){
        return sesi.getBoolean(SUDAH_LOGIN, false);
    }

    public boolean getSes_boarding(){
        return sesi.getBoolean(Ses_boarding, false);
    }
}

