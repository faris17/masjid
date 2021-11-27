package com.myapplication.informasimasjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText emailLogin,passwordLogin;
    Button Login;
    ProgressBar progressBarLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.email);
        passwordLogin = findViewById(R.id.password);
        Login = findViewById(R.id.login);
        progressBarLogin = findViewById(R.id.progressbar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        if(firebaseAuth.getCurrentUser()!=null){
            startActivity(new Intent(this, HomeActivity.class));
        }

        Login.setOnClickListener(this);

        progressBarLogin.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.login:
                progressBarLogin.setVisibility(View.VISIBLE);
                Login.setVisibility(View.GONE);

                String mEmail = emailLogin.getText().toString().trim();
                String mPass = passwordLogin.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail)){
                    emailLogin.setError("Required field...");
                    return ;
                }

                if(TextUtils.isEmpty(mPass)){
                    passwordLogin.setError("Required Field..");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                            progressBarLogin.setVisibility(View.GONE);
                            Login.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
        }
    }
}