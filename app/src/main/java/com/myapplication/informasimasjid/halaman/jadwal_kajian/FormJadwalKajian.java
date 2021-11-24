package com.myapplication.informasimasjid.halaman.jadwal_kajian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myapplication.informasimasjid.MainActivity;
import com.myapplication.informasimasjid.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FormJadwalKajian extends AppCompatActivity implements View.OnClickListener {
    ImageView back;

    Spinner day;
    EditText judul, tema, naras, ket, tempat, link;
    private TextView tvTimeResult;
    private Button simpan, update, btTimePicker, selectFile;

    ImageView imageView;

    ProgressBar progressBarLogin;

    CollectionReference kajianRef;

    private TimePickerDialog timePickerDialog;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageReference;

    String idkey = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_jadwal_kajian);

        back =  findViewById(R.id.btnbacktoHome);
        day = findViewById(R.id.chooseDay);
        judul = (EditText) findViewById(R.id.namakegiatan);
        tema = (EditText) findViewById(R.id.temakegiatan);
        naras = (EditText) findViewById(R.id.narasumber);
        tempat = (EditText) findViewById(R.id.tempatkegiatan);
        ket = (EditText) findViewById(R.id.keterangan);
        progressBarLogin = findViewById(R.id.progressbar);
        link = findViewById(R.id.linkyoutube);

        selectFile = findViewById(R.id.chooseFile);
        imageView = findViewById(R.id.imgView);

        progressBarLogin.setVisibility(View.GONE);

        tvTimeResult = (TextView) findViewById(R.id.tv_timeresult);
        btTimePicker = (Button) findViewById(R.id.bt_showtimepicker);

        btTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        // on pressing btnSelect SelectImage() is called
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Select File", Toast.LENGTH_SHORT).show();
                SelectImage();
            }
        });

        simpan = findViewById(R.id.btnsave);
        update = findViewById(R.id.btnEdit);
        update.setVisibility(View.GONE);

        // Access a Cloud Firestore instance from your Activity
         db = FirebaseFirestore.getInstance();

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        kajianRef = db.collection("kajian");

        //menangani bila ada proses klik Edit
        String edit = getIntent().getStringExtra("EXTRA_EDIT");
        if(edit !=null){
            idkey = getIntent().getStringExtra("EXTRA_ID");
            String editjudul= getIntent().getStringExtra("EDIT_JUDUL");
            String edittema= getIntent().getStringExtra("EDIT_TEMA");
            String edittempat= getIntent().getStringExtra("EDIT_TEMPAT");
            String editwaktu= getIntent().getStringExtra("EDIT_TIME");
            String editday= getIntent().getStringExtra("EDIT_DAY");
            String editdeskripsi= getIntent().getStringExtra("EDIT_DESKRIPSI");
            String editnarasumber= getIntent().getStringExtra("EDIT_NARASUMBER");
            String editlink= getIntent().getStringExtra("EDIT_LINK");

            update.setVisibility(View.VISIBLE);
            simpan.setVisibility(View.GONE);

            judul.setText(editjudul);
            tema.setText(edittema);
            tempat.setText(edittempat);
            tvTimeResult.setText(editwaktu);
            ket.setText(editdeskripsi);
            naras.setText(editnarasumber);
            link.setText(editlink);

            if(editday.equals("Senin")) day.setSelection(0);
            else if(editday.equals( "Selasa")) day.setSelection(1);
            else if(editday.equals("Rabu")) day.setSelection(2);
            else if(editday.equals("Kamis")) day.setSelection(3);
            else if(editday.equals("Jumat")) day.setSelection(4);
            else if(editday.equals("Sabtu")) day.setSelection(5);
            else  day.setSelection(6);

            getImage("images/"+idkey, imageView);

            update.setOnClickListener(this);
        }
        else {
            simpan.setOnClickListener(this);
        }



        back.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnsave:
                saveData();
                break;
            case R.id.btnEdit:
                saveData();
                break;
        }
    }

    private void saveData(){

        //ambil data
        String dday = String.valueOf(day.getSelectedItem());
        String djudul = judul.getText().toString().trim();
        String dtema = tema.getText().toString().trim();
        String dnaras = naras.getText().toString().trim();
        String dket = ket.getText().toString().trim();
        String dtime = String.valueOf(tvTimeResult.getText());
        String dtempat = tempat.getText().toString().trim();
        String dlink = link.getText().toString().trim();


        Map noteDataMap = new HashMap<>();
        noteDataMap.put("hari", dday);
        noteDataMap.put("judul", djudul );
        noteDataMap.put("tema", dtema);
        noteDataMap.put("narasumber", dnaras);
        noteDataMap.put("keterangan", dket);
        noteDataMap.put("time", dtime);
        noteDataMap.put("tempat", dtempat);
        noteDataMap.put("link", dlink);

        if(idkey != null){
            //show progress
            update.setVisibility(View.GONE);
            progressBarLogin.setVisibility(View.VISIBLE);

            kajianRef.document(idkey).update(noteDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {
                    uploadImage(idkey);
                    Toast.makeText(getApplicationContext(), "Berhasil update", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w("pesan", "Error adding document", e);
                        }});
            update.setVisibility(View.VISIBLE);
        }
        else {
            //show progress
            simpan.setVisibility(View.GONE);
            progressBarLogin.setVisibility(View.VISIBLE);

            kajianRef.add(noteDataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
            simpan.setVisibility(View.VISIBLE);
            hapusfield();
        }

        progressBarLogin.setVisibility(View.GONE);

    }

    private void hapusfield(){
        day.setAdapter(null);
        judul.getText().clear();
        tema.getText().clear();
        naras.getText().clear();
        ket.getText().clear();
        tempat. getText().clear();
        tvTimeResult.setText(null);
        imageView.setImageBitmap(null);
        link.getText().clear();
    }

    private void showTimeDialog() {

        /**
         * Calendar untuk mendapatkan waktu saat ini
         */
        Calendar calendar = Calendar.getInstance();

        /**
         * Initialize TimePicker Dialog
         */
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                /**
                 * Method ini dipanggil saat kita selesai memilih waktu di DatePicker
                 */
                tvTimeResult.setText(hourOfDay+":"+minute);
            }
        },
                /**
                 * Tampilkan jam saat ini ketika TimePicker pertama kali dibuka
                 */
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),

                /**
                 * Cek apakah format waktu menggunakan 24-hour format
                 */
                DateFormat.is24HourFormat(this));

        timePickerDialog.show();
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

    public void getImage(String data, final ImageView imgView){
        storageReference.child(data).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess( Uri uri) {

                // Got the download URL for 'users/me/profile.png'
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(imgView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

}