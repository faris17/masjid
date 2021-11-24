package com.myapplication.informasimasjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registeractivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailRegister, passwordRegister;
    Button btnRegister;
    TextView linkLogin;
    ProgressBar progressBarRegister;

    FirebaseAuth firebaseAuth;

    CollectionReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);

        firebaseAuth = FirebaseAuth.getInstance();

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Reference to a Collection
        profileRef = db.collection("keuangan");

        emailRegister = findViewById(R.id.email);
        passwordRegister = findViewById(R.id.password);
        btnRegister = findViewById(R.id.register);
        linkLogin = findViewById(R.id.login);
        progressBarRegister = findViewById(R.id.progressbar);

        btnRegister.setOnClickListener(this);
        linkLogin.setOnClickListener(this);

        progressBarRegister.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.register:
                progressBarRegister.setVisibility(View.VISIBLE);
                btnRegister.setVisibility(View.GONE);

                String mEmail = emailRegister.getText().toString().trim();
                String mPass = passwordRegister.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    emailRegister.setError("Required field...");
                    return;
                }

                if (TextUtils.isEmpty(mPass)) {
                    passwordRegister.setError("Required Field..");
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(mEmail, mPass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
//                                    createFirebaseUserProfile(task.getResult().getUser());
                                } else {
                                    Toast.makeText(getApplicationContext(), "Format Salah", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                break;

        }
    }



}