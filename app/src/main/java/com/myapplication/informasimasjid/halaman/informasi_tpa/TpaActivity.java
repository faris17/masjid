
package com.myapplication.informasimasjid.halaman.informasi_tpa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.adapter.KeuanganAdapter;
import com.myapplication.informasimasjid.halaman.jadwal_kajian.FormJadwalKajian;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataKeuangan;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TpaActivity extends AppCompatActivity {
    private static final String TAG = "TPA_activity";
    ImageView back, foto;
    Button share, edit, tambah;
    TextView vpengumumantpa, vjadwalpendaftaran, vjadwalpenutupan, vlink, vcontact;

    private FirebaseFirestore firestoreDB;
    private StorageReference reference;

    FirebaseAuth firebaseAuth;

    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpa);

        back = findViewById(R.id.btnbacktoHome);

        vpengumumantpa = findViewById(R.id.pengumumantpa);
        vjadwalpendaftaran = findViewById(R.id.jadwalpendaftaran);
        vjadwalpenutupan = findViewById(R.id.jadwalpenutupan);
        vlink = findViewById(R.id.linkpendaftaran);
        vcontact = findViewById(R.id.nomorcontact);
        edit = findViewById(R.id.btnEdit);
        tambah = findViewById(R.id.btnNambah);
        foto = findViewById(R.id.brosurtpa);

        share = findViewById(R.id.btnShare);

        // Access a Cloud Firestore instance from your Activity
        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        sharedPrefManager = new Session(this);

        reference = FirebaseStorage.getInstance().getReference();

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        firestoreDB.collection("tpa")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }
                        int i=1;
                        for (DocumentSnapshot doc : documentSnapshots) {
                            getImage(getApplicationContext(), "images/"+doc.getId()+".jpg", foto);
                            String pengumuman = String.valueOf(doc.get("pengumuman"));
                            String jadwalpendaftaran = String.valueOf(doc.get("jadwalpendaftaran"));
                            String jadwalpenutupan = String.valueOf(doc.get("jadwalpenutupan"));
                            String link = "<a href="+String.valueOf(doc.get("linkpendaftaran"))+">"+String.valueOf(doc.get("linkpendaftaran"))+"</a>";
                            String contact = String.valueOf(doc.get("contact"));

                            if(sharedPrefManager.getSes_level().equals("1")){
                                if(doc.exists()) {
                                    System.out.println("heloo");
                                    edit.setVisibility(View.VISIBLE);
                                    tambah.setVisibility(View.GONE);
                                } else {
                                    edit.setVisibility(View.GONE);
                                    tambah.setVisibility(View.VISIBLE);
                                }
                            }

                            vpengumumantpa.setText(pengumuman);
                            vjadwalpendaftaran.setText(jadwalpendaftaran);
                            vjadwalpenutupan.setText(jadwalpenutupan);
                            vlink.setText(link);
                            vcontact.setText(contact);

                            vcontact.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contact));
                                    startActivity(intent);
                                }
                            });

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                ((TextView) findViewById(R.id.linkpendaftaran)).setMovementMethod(LinkMovementMethod.getInstance());
                                ((TextView) findViewById(R.id.linkpendaftaran)).setText(Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY));
                            }
                            else {
                                ((TextView) findViewById(R.id.linkpendaftaran)).setMovementMethod(LinkMovementMethod.getInstance());
                                ((TextView) findViewById(R.id.linkpendaftaran)).setText(Html.fromHtml(link));
                            }
                        }

                    }
                });
        //get pengumuman
//        firestoreDB.collection("tpa").document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    String pengumuman = String.valueOf(doc.get("pengumuman"));
//                    String jadwalpendaftaran = String.valueOf(doc.get("jadwalpendaftaran"));
//                    String jadwalpenutupan = String.valueOf(doc.get("jadwalpenutupan"));
//                    String link = "<a href="+String.valueOf(doc.get("linkpendaftaran"))+">"+String.valueOf(doc.get("linkpendaftaran"))+"</a>";
//                    String contact = String.valueOf(doc.get("contact"));
//
//                    if(doc.exists()) {
//                        System.out.println("heloo");
//                        edit.setVisibility(View.VISIBLE);
//                        tambah.setVisibility(View.GONE);
//                    } else {
//                        edit.setVisibility(View.GONE);
//                        tambah.setVisibility(View.VISIBLE);
//                    }
//
//                    vpengumumantpa.setText(pengumuman);
//                    vjadwalpendaftaran.setText(jadwalpendaftaran);
//                    vjadwalpenutupan.setText(jadwalpenutupan);
//                    vlink.setText(link);
//                    vcontact.setText(contact);
//
//                    vcontact.setOnClickListener(new View.OnClickListener(){
//                        public void onClick(View v){
//                            Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contact));
//                            startActivity(intent);
//                        }
//                    });
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        ((TextView) findViewById(R.id.linkpendaftaran)).setMovementMethod(LinkMovementMethod.getInstance());
//                        ((TextView) findViewById(R.id.linkpendaftaran)).setText(Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY));
//                    }
//                    else {
//                        ((TextView) findViewById(R.id.linkpendaftaran)).setMovementMethod(LinkMovementMethod.getInstance());
//                        ((TextView) findViewById(R.id.linkpendaftaran)).setText(Html.fromHtml(link));
//                    }
//                } else {
//                    edit.setVisibility(View.GONE);
//                    tambah.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });
//
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse("android.resource://com.android.test/*");
                try {
                    InputStream stream = getContentResolver().openInputStream(screenshotUri);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
        });
//
        if(sharedPrefManager.getSes_level().equals("1")){
            tambah.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent pageTPA = new Intent(getApplicationContext(), FormInformasiTPA.class);
                    startActivity(pageTPA);
                }
            });

            edit.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent pageTPA = new Intent(getApplicationContext(), FormInformasiTPA.class);
                    startActivity(pageTPA);
                }
            });
        }
    }

    public void getImage(Context context, String data, ImageView foto) {
        reference.child(data).getDownloadUrl().addOnSuccessListener(uri -> {

            // Got the download URL for 'images/brosur.png'
            Glide.with(context)
                    .load(uri)
                    .into(foto);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            System.out.println("terjadi error");
        });
    }
}