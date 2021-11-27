package com.myapplication.informasimasjid.halaman.jadwal_kajian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.adapter.KajianAdapter;
import com.myapplication.informasimasjid.adapter.KeuanganAdapter;
import com.myapplication.informasimasjid.halaman.keuangan.FormKeuangan;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataKajian;
import com.myapplication.informasimasjid.model.DataKeuangan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KajianActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "KajianActivity";

    ImageView back;
    FloatingActionButton buttonAdd;

    private RecyclerView recyclerView;
    private KajianAdapter mAdapter;

    private FirebaseFirestore firestoreDB;
    ListenerRegistration firestoreListener;

    private LinearLayoutManager mManager;

    FirebaseAuth firebaseAuth;

    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kajian);

        buttonAdd = findViewById(R.id.fabAdd);
        back = findViewById(R.id.btnbacktoHome);
        recyclerView = findViewById(R.id.list_kajian);

        // Access a Cloud Firestore instance from your Activity
        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        recyclerView.setHasFixedSize(true);

        sharedPrefManager = new Session(this);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        if(firebaseAuth.getCurrentUser() !=null  && sharedPrefManager.getSes_level().equals("1")){
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(this);
        }

        firestoreListener = firestoreDB.collection("kajian")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }

                        List<DataKajian> kajianList = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            DataKajian data = doc.toObject(DataKajian.class);
                            data.setId(doc.getId());
                            kajianList.add(data);
                        }

                        mAdapter = new KajianAdapter(getApplicationContext(), kajianList );
                        recyclerView.setAdapter(mAdapter);
                    }
                });



        back.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent pageFormKajian = new Intent(this, FormJadwalKajian.class);
                startActivity(pageFormKajian);
                break;
        }
    }


}