package com.myapplication.informasimasjid.halaman.keuangan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.myapplication.informasimasjid.HomeActivity;
import com.myapplication.informasimasjid.MainActivity;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.adapter.KeuanganAdapter;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataKeuangan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeuanganActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KeuanganActivity";
    ImageView back;

    private RecyclerView recyclerView;
    private KeuanganAdapter mAdapter;

    DecimalFormat kursIdr;
    DecimalFormatSymbols formatRp;

    private FirebaseFirestore firestoreDB;
    ListenerRegistration firestoreListener;

    private LinearLayoutManager mManager;
    TextView textsaldosekarang, linkrekapkeuangan;

    FirebaseAuth firebaseAuth;

    FloatingActionButton buttonAdd;

    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keuangan);

        recyclerView = findViewById(R.id.list_keuangan);
        textsaldosekarang = findViewById(R.id.saldosekarang);
        linkrekapkeuangan = findViewById(R.id.linkrekap);
        buttonAdd = findViewById(R.id.fabAdd);

        kursIdr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        kursIdr.setDecimalFormatSymbols(formatRp);

        // Access a Cloud Firestore instance from your Activity
        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        sharedPrefManager = new Session(this);

        // Reference to a Collection


        recyclerView.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        back = findViewById(R.id.btnbacktoHome);

        loadKeuanganList();

        if(firebaseAuth.getCurrentUser() !=null && sharedPrefManager.getSes_level().equals("2")){
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(this);
        }

        firestoreListener = firestoreDB.collection("keuangan")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }

                        List<DataKeuangan> keuanganList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            DataKeuangan data = doc.toObject(DataKeuangan.class);
                            data.setId(doc.getId());
                            keuanganList.add(data);
                        }

                        mAdapter = new KeuanganAdapter(getApplicationContext(), keuanganList );
                        recyclerView.setAdapter(mAdapter);
                    }
                });


        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        linkrekapkeuangan.setOnClickListener(this);

        //get saldo keuangan
        firestoreDB.collection("saldosekarang").document("now")
                .addSnapshotListener((EventListener<DocumentSnapshot>) (documentSnapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen failed!", e);
                        return;
                    }
                    if(documentSnapshots.get("jumlahsaldo") != null) {
                        String premi = "000" + documentSnapshots.get("jumlahsaldo").toString();
                        System.out.println("hello " + premi);
                        textsaldosekarang.setText(kursIdr.format(Long.valueOf(premi)));
                    }

                });
//        firestoreDB.collection("saldosekarang").document("now").addSnapshotListener(new EventListener<DocumentSnapshot>() (new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    String premi = "000"+doc.get("jumlahsaldo").toString();
//                    System.out.println("hello "+premi);
//                    textsaldosekarang.setText(kursIdr.format(Long.valueOf(premi)));
//                }
//
//            }
//        });

    }

    private void loadKeuanganList() {
        firestoreDB.collection("keuangan").orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DataKeuangan> dataKeuangans = new ArrayList<>();

                            for (DocumentSnapshot doc : task.getResult()) {
                                DataKeuangan dataKeuangan = doc.toObject(DataKeuangan.class);
                                dataKeuangan.setId(doc.getId());
                                dataKeuangans.add(dataKeuangan);
                            }

                            mAdapter = new KeuanganAdapter(getApplicationContext(),dataKeuangans);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent intent = new Intent(this, FormKeuangan.class);
                startActivity(intent);
                break;
            case R.id.linkrekap:
                Intent pagerekap = new Intent(this, RekapKeuangan.class);
                startActivity(pagerekap);
                break;
        }
    }

    private void updateSaldo(String kateg, String uang){

        firestoreDB.collection("saldosekarang").document("now").get().addOnSuccessListener(
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
                        firestoreDB.collection("saldosekarang").document("now").update("jumlahsaldo", saldonow);
                    }
                }
        );
    }
}