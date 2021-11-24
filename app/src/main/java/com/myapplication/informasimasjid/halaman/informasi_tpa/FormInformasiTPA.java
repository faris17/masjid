package com.myapplication.informasimasjid.halaman.informasi_tpa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapplication.informasimasjid.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FormInformasiTPA extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TPA_form";

    Button btnfiletpa, btnSave;
    EditText inputPengumuman, inputWaktuPendaftaran, inputWaktuPenutupan, inputlinkPendaftaran, inputNomorHP;
    ProgressBar progressBar;
    ImageView imageView, back;

    DatePickerDialog picker;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference

    FirebaseStorage storage;
    StorageReference storageReference;

    private FirebaseFirestore db;

    CollectionReference tpaRef;

    private String id;
    String update = "Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_informasi_t_p);

        btnfiletpa = findViewById(R.id.btnfiletpa);
        inputPengumuman = findViewById(R.id.pengumuman);
        inputWaktuPendaftaran = findViewById(R.id.waktupendaftaran);
        inputWaktuPenutupan = findViewById(R.id.waktupenutupan);
        inputlinkPendaftaran = findViewById(R.id.linkpendaftaran);
        inputNomorHP = findViewById(R.id.contact);
        imageView = findViewById(R.id.imgView);
        back = findViewById(R.id.btnback);

        btnfiletpa.setBackgroundColor(Color.WHITE);
        btnSave = findViewById(R.id.btnsave);
        progressBar = findViewById(R.id.progressbar);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Reference to a Collection
        tpaRef = db.collection("tpa");

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSave.setOnClickListener(this);
        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        //get child tpa
        db.collection("tpa")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }
                        int i=1;
                        for (DocumentSnapshot doc : documentSnapshots) {
                            id= doc.getId();
                            getImage(getApplicationContext(), "images/"+id+".jpg", imageView);
                            String pengumuman = String.valueOf(doc.get("pengumuman"));
                            String jadwalpendaftaran = String.valueOf(doc.get("jadwalpendaftaran"));
                            String jadwalpenutupan = String.valueOf(doc.get("jadwalpenutupan"));
                            String link = (String) doc.get("linkpendaftaran");
                            String contact = String.valueOf(doc.get("contact"));


                            inputPengumuman.setText(pengumuman);
                            inputWaktuPendaftaran.setText(jadwalpendaftaran);
                            inputWaktuPenutupan.setText(jadwalpenutupan);
                            inputlinkPendaftaran.setText(link);
                            inputNomorHP.setText(contact);
                            btnSave.setText(update);
                        }

                    }
                });

        btnfiletpa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Select File", Toast.LENGTH_SHORT).show();
                SelectImage();
            }
        });

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
                        inputWaktuPendaftaran.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    private void saveData(){
        //show progress
        btnSave.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        //ambil data
        String dpengumuman = inputPengumuman.getText().toString().trim();
        String dwaktupendaftaran = inputWaktuPendaftaran.getText().toString().trim();
        String dwaktupenutupan = inputWaktuPenutupan.getText().toString().trim();;
        String dnomorhp = inputNomorHP.getText().toString().trim();
        String dlink = inputlinkPendaftaran.getText().toString().trim();

        Map noteDataMap = new HashMap<>();
        noteDataMap.put("pengumuman", dpengumuman);
        noteDataMap.put("jadwalpendaftaran", dwaktupendaftaran );
        noteDataMap.put("jadwalpenutupan", dwaktupenutupan);
        noteDataMap.put("linkpendaftaran", dlink);
        noteDataMap.put("contact", dnomorhp);

        if(id != null){
            //show progress
            btnSave.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            tpaRef.document(id).update(noteDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {
                    uploadImage(id);
                    Toast.makeText(getApplicationContext(), "Berhasil update", Toast.LENGTH_SHORT).show();

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w("pesan", "Error adding document", e);
                        }});
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setText(update);

        }
        else {
            tpaRef.add(noteDataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    uploadImage(documentReference.getId());
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
            progressBar.setVisibility(View.GONE);

        }
        hapusfield();
    }

    private void hapusfield(){
        inputPengumuman.getText().clear();
        inputWaktuPendaftaran.getText().clear();
        inputWaktuPenutupan.getText().clear();
        inputlinkPendaftaran.getText().clear();
        inputNomorHP.getText().clear();
        imageView.setImageBitmap(null);
    }

    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage(String id) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/" + id +".jpg");

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload Done", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getImage(Context context, String data, ImageView imageView) {
        storageReference.child(data).getDownloadUrl().addOnSuccessListener(uri -> {

            // Got the download URL for 'images/brosur.png'
            Glide.with(context)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            System.out.println("terjadi error");
        });
    }


}