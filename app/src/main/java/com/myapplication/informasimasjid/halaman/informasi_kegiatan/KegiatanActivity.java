package com.myapplication.informasimasjid.halaman.informasi_kegiatan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.adapter.InformasiAdapter;
import com.myapplication.informasimasjid.adapter.KajianAdapter;
import com.myapplication.informasimasjid.halaman.keuangan.FormKeuangan;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataInformasi;
import com.myapplication.informasimasjid.model.DataKajian;

import java.util.ArrayList;
import java.util.List;

public class KegiatanActivity extends AppCompatActivity implements View.OnClickListener  {
    private static final String TAG = "KegiatanActivity";

    ImageView back;

    private RecyclerView recyclerView;
    private InformasiAdapter mAdapter;

    private FirebaseFirestore firestoreDB;
    ListenerRegistration firestoreListener;

    private LinearLayoutManager mManager;

    FloatingActionButton buttonAdd;
    FirebaseAuth firebaseAuth;

    Session sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kegiatan);

        back = findViewById(R.id.btnbacktoHome);
        recyclerView = findViewById(R.id.list_informasi);
        buttonAdd = findViewById(R.id.fabAdd);

        // Access a Cloud Firestore instance from your Activity
        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView.setHasFixedSize(true);

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mManager);

        sharedPrefManager = new Session(this);

        firestoreListener = firestoreDB.collection("informasi").orderBy("created", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen failed!", e);
                            return;
                        }

                        List<DataInformasi> datainformasi = new ArrayList<>();

                        for (DocumentSnapshot doc : documentSnapshots) {
                            DataInformasi data = doc.toObject(DataInformasi.class);
                            data.setId(doc.getId());
                            datainformasi.add(data);
                        }

                        mAdapter = new InformasiAdapter(getApplicationContext(), datainformasi );
                        recyclerView.setAdapter(mAdapter);
                    }
                });

        if(firebaseAuth.getCurrentUser()!=null && sharedPrefManager.getSes_level().equals("1")){
            buttonAdd.setVisibility(View.VISIBLE);
            buttonAdd.setOnClickListener(this);
        }

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                Intent intent = new Intent(this, FormKegiatan.class);
                startActivity(intent);
                break;
        }
    }
}