package com.myapplication.informasimasjid.halaman.informasi_kegiatan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.myapplication.informasimasjid.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class FormKegiatan extends AppCompatActivity implements View.OnClickListener{
    EditText kegiatan, ket, tgl, nohp;
    Button save;
    ImageView back;

    FirebaseFirestore db;
    CollectionReference informasiRef;

    DatePickerDialog picker;

    String idkey = null;
    String btnTextUpdate = "Update";
    String btnTextSave = "Simpan";

    //This method returns the time in millis
    long timeMilli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_kegiatan);

        kegiatan = findViewById(R.id.namakegiatan);
        ket = findViewById(R.id.keterangan);
        tgl = findViewById(R.id.tanggal);
        nohp = findViewById(R.id.nomorhp);

        save = findViewById(R.id.btnsave);
        back = findViewById(R.id.btnback);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        informasiRef = db.collection("informasi");

        Date date = new Date();
        //This method returns the time in millis
        timeMilli = date.getTime();

        //menangani bila ada proses klik Edit
        String edit = getIntent().getStringExtra("EXTRA_EDIT");
        if(edit !=null){
            idkey = getIntent().getStringExtra("EXTRA_ID");
            String editjudul= getIntent().getStringExtra("EDIT_JUDUL");
            String editdeskripsi= getIntent().getStringExtra("EDIT_DESKRIPSI");
            String edittanggal = getIntent().getStringExtra("EDIT_TANGGAL");
            String editnomorhp = getIntent().getStringExtra("EDIT_KONTAK");

            kegiatan.setText(editjudul);
            ket.setText(editdeskripsi);
            tgl.setText(edittanggal);
            nohp.setText(editnomorhp);
            save.setText(btnTextUpdate);
        }
        else {
            save.setText(btnTextSave);

        }

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnsave:
                saveData();
                break;
        }
    }

    private void saveData(){

        //ambil data
        String dkegiatan = kegiatan.getText().toString().trim();
        String dket = ket.getText().toString().trim();
        String dtgl = tgl.getText().toString().trim();
        String dnomor = nohp.getText().toString().trim();


        Map noteDataMap = new HashMap<>();
        noteDataMap.put("judul", dkegiatan );
        noteDataMap.put("deskripsi", dket);
        noteDataMap.put("infokontak", dnomor);
        noteDataMap.put("tanggal", dtgl);

        if(idkey != null){
            //show progress
            informasiRef.document(idkey).update(noteDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("nama", dkegiatan);
                    params.put("message", dket.substring(0,30));

                    client.post("http://192.168.1.10/informasimasjid/send.php", params, new AsyncHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                    Toast.makeText(getApplicationContext(), "Berhasil update", Toast.LENGTH_SHORT).show();

                }

            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w("pesan", "Error adding document", e);
                        }});
        }
        else {


            //show progress
            save.setVisibility(View.GONE);
            noteDataMap.put("created", timeMilli);

            informasiRef.add(noteDataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
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

            hapusfield();
        }

    }

    private void hapusfield(){
        kegiatan.getText().clear();
        ket.getText().clear();
        nohp. getText().clear();
        tgl.getText().clear();
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
                        tgl.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }
}