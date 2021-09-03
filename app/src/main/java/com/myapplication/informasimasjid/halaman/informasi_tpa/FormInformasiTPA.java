package com.myapplication.informasimasjid.halaman.informasi_tpa;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.myapplication.informasimasjid.R;

public class FormInformasiTPA extends AppCompatActivity {

    Button btnfiletpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_informasi_t_p);

        btnfiletpa = findViewById(R.id.btnfiletpa);

        btnfiletpa.setBackgroundColor(Color.WHITE);
    }
}