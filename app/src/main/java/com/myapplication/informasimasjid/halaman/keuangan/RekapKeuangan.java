package com.myapplication.informasimasjid.halaman.keuangan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.myapplication.informasimasjid.library.Session;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RekapKeuangan extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Rekapan";

    ImageView back, imageView, imgrekap;
    LinearLayout formkeuangan;
    TextView imagetitle;

    Button btnfile, btnSave;
    EditText titlerekapan;

    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference

    FirebaseStorage storage;
    StorageReference storageReference;

    private FirebaseFirestore db;

    CollectionReference rekapan;

    private String id;
    String update = "Update";

    FirebaseAuth firebaseAuth;
    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekap_keuangan);

        titlerekapan = findViewById(R.id.judulrekapan);
        btnfile = findViewById(R.id.btnfilerekap);
        btnSave = findViewById(R.id.btnsave);
        back = findViewById(R.id.btnback);
        imageView = findViewById(R.id.imgView);
        formkeuangan = findViewById(R.id.forkeuangan);
        imagetitle = findViewById(R.id.imgtitle);
        imgrekap = findViewById(R.id.imagerekap);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Reference to a Collection
        rekapan = db.collection("rekapankeuangan");

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPrefManager = new Session(this);

        if(firebaseAuth.getCurrentUser() ==null || sharedPrefManager.getSes_level().equals("1")){
            formkeuangan.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(this);
        btnfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Select File", Toast.LENGTH_SHORT).show();
                SelectImage();
            }
        });
        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        getDataFirestore();

        hapusfield();
    }

    public void getDataFirestore(){
        //get child rekapan
        DocumentReference docRef = rekapan.document("now");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        getImage(getApplicationContext(), "images/rekapanuang.jpg", imgrekap);
                        imagetitle.setText(document.get("titlerekapan").toString());
                    }
                    else {
                        Log.d(TAG, "DocumentSnapshot gagal ");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "ERROR al Realizar la validacion de Correo"+ task.getException(), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsave:
                saveData();
                break;
        }
    }

    private void saveData(){
        //show progress

        //ambil data
        String dtitle = titlerekapan.getText().toString().trim();

        Map noteDataMap = new HashMap<>();
        noteDataMap.put("titlerekapan", dtitle);
            rekapan.document("now").update(noteDataMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    uploadImage();
                    Toast.makeText(getApplicationContext(), "Berhasil disimpan", Toast.LENGTH_SHORT).show();

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

    private void hapusfield(){
        titlerekapan.getText().clear();
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
    private void uploadImage() {
        if (filePath != null) {
            // Defining the child of storageReference
            StorageReference ref = storageReference.child("images/rekapanuang.jpg");

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    getDataFirestore();
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