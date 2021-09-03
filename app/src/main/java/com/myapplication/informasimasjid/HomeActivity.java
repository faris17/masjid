package com.myapplication.informasimasjid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapplication.informasimasjid.halaman.keuangan.KeuanganActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseUser user ;
    TextView textName;

    LinearLayout halkeuangan, halkajian, haltpa, halinformasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address etc
            String name = user.getDisplayName();
            String email = user.getEmail();

            textName = findViewById(R.id.dashboard_name);
            halkeuangan = findViewById(R.id.keuangan);
            halkajian = findViewById(R.id.kajian);
            haltpa = findViewById(R.id.informasitpa);
            halinformasi = findViewById(R.id.informasilainnya);


            textName.setText(name);

            halkeuangan.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keuangan:
                Intent intent = new Intent(this, KeuanganActivity.class);
                startActivity(intent);
                break;


        }
    }
}