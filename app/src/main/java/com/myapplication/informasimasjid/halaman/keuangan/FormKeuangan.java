package com.myapplication.informasimasjid.halaman.keuangan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapplication.informasimasjid.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FormKeuangan extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    RadioGroup btnKategori;
    RadioButton btnPemasukan, btnPengeluaran;
    Button btnSave;
    ProgressBar progressBarLogin;

    DatePickerDialog picker;

    EditText inputKet, inputUang, inputTgl;

    CollectionReference keuanganRef, saldosekarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_keuangan);

        progressBarLogin = findViewById(R.id.progressbar);
        btnSave = findViewById(R.id.btnsave);
        inputKet = findViewById(R.id.keterangan);
        inputUang = findViewById(R.id.jumlahuang);
        inputTgl = findViewById(R.id.tanggal);

        btnKategori = findViewById(R.id.kategori);
        btnPemasukan = findViewById(R.id.pemasukan);
        btnPengeluaran = findViewById(R.id.pengeluaran);
        back = findViewById(R.id.btnbacktoHome);


        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to a Collection
        keuanganRef = db.collection("keuangan");
        saldosekarang = db.collection("saldosekarang");

        progressBarLogin.setVisibility(View.GONE);

        btnSave.setOnClickListener(this);
        inputTgl.setOnClickListener(this);

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

    }

    //ambil calender
    private void getTanggal(){
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        // date picker dialog
        picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        inputTgl.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    private void saveData(){
        //show progress
        btnSave.setVisibility(View.GONE);
        progressBarLogin.setVisibility(View.VISIBLE);

        //ambil data
        String dketerangan = inputKet.getText().toString().trim();
        String dnominal = inputUang.getText().toString().trim();
        int kateg = btnKategori.getCheckedRadioButtonId();
        String tanggaldaftar = inputTgl.getText().toString().trim();
        String dkategori;
        if(kateg == btnPemasukan.getId()){
            dkategori = "pemasukan";
        }
        else {
            dkategori = "pengeluaran";
        }

        Date date = new Date();
        //This method returns the time in millis
        long timeMilli = date.getTime();

        Map noteDataMap = new HashMap<>();
         noteDataMap.put("kategori", dkategori);
         noteDataMap.put("keterangan", dketerangan );
         noteDataMap.put("nominal", dnominal);
         noteDataMap.put("tanggal", tanggaldaftar);
         noteDataMap.put("time", timeMilli);

        keuanganRef.add(noteDataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                updateSaldo(dkategori, dnominal);
                Toast.makeText(getApplicationContext(), "Berhasil disimpan", Toast.LENGTH_SHORT).show();
                Log.d("pesan", "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Gagal disimpan", Toast.LENGTH_SHORT).show();
                        Log.w("pesan", "Error adding document", e);
                    }});

        btnSave.setVisibility(View.VISIBLE);
        progressBarLogin.setVisibility(View.GONE);
        hapusfield();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tanggal:
                getTanggal();
                break;
            case R.id.btnsave:
                saveData();
                break;
        }
    }

    private void hapusfield(){
        inputTgl.getText().clear();
        inputUang.getText().clear();
        inputKet.getText().clear();
    }

    private void updateSaldo(String kateg, String uang){

        saldosekarang.document("now").get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                         int saldo = (Integer) Integer.parseInt((String) documentSnapshot.get("jumlahsaldo"));
                         int updateSaldo;

                         if(kateg.equals("pemasukan")){
                             updateSaldo  = saldo + Integer.parseInt(uang);
                         } else {
                             updateSaldo  = saldo - Integer.parseInt(uang);
                         }

                         String saldonow = Integer.toString(updateSaldo);
                         saldosekarang.document("now").update("jumlahsaldo", saldonow);
                    }
                }
        );
    }
}