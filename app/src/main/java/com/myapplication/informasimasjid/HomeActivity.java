package com.myapplication.informasimasjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapplication.informasimasjid.halaman.informasi_kegiatan.KegiatanActivity;
import com.myapplication.informasimasjid.halaman.informasi_tpa.TpaActivity;
import com.myapplication.informasimasjid.halaman.jadwal_kajian.KajianActivity;
import com.myapplication.informasimasjid.halaman.keuangan.KeuanganActivity;
import com.myapplication.informasimasjid.halaman.masjid.ProfileMasjidActivity;
import com.myapplication.informasimasjid.library.Session;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "homeActivity";

    FirebaseUser user ;
    TextView textName;

        ImageView signout, login, linkmasjid;

    LinearLayout halkeuangan, halkajian, haltpa, halinformasi;

    //variable untuk fiebase auth
    private FirebaseAuth mAuth;

    private FirebaseFirestore firestoreDB;

    Session sharedPrefManager;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //inisialisasi authentikasi
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        signout = findViewById(R.id.btnSignOut);

        textName = findViewById(R.id.dashboard_name);
        halkeuangan = findViewById(R.id.keuangan);
        halkajian = findViewById(R.id.kajian);
        haltpa = findViewById(R.id.informasitpa);
        halinformasi = findViewById(R.id.informasilainnya);
        linkmasjid = findViewById(R.id.profilemasjid);

        // Access a Cloud Firestore instance from your Activity
        firestoreDB = FirebaseFirestore.getInstance();

        sharedPrefManager = new Session(this);
        if (mAuth.getCurrentUser() != null) {

            //GET LEVEL FROM child users
            //get saldo keuangan
            DocumentReference docRef = firestoreDB.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            sharedPrefManager.saveLevel(Session.Ses_level, (String) document.get("level"));
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


            // Name, email address etc
            String name = user.getDisplayName();
            String email = user.getEmail();
            signout.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_sign_out));

            textName.setText(name);
            signout.setOnClickListener(this);
        } else {

            signout.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_login));
            //login
            signout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        halkeuangan.setOnClickListener(this);
        halkajian.setOnClickListener(this);
        haltpa.setOnClickListener(this);
        halinformasi.setOnClickListener(this);
        linkmasjid.setOnClickListener(this);

        //setting pusher
        PusherOptions options = new PusherOptions();
        options.setCluster("mt1");
        Pusher pusher = new Pusher("2ab2c1aaf4c61cc5c990", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting! " +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e
                );
            }
        }, ConnectionState.ALL);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                //NOTIFICATION
                String data = event.getData();
                String title="";
                String pesan="";
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    title= jsonObject.getString("title");
                    pesan= jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mBuilder = new NotificationCompat.Builder(HomeActivity.this);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle(title)
                        .setContentText(pesan)
                        .setAutoCancel(false)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                mNotificationManager = (NotificationManager) HomeActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(getApplicationContext(), KegiatanActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                        notificationIntent, 0);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    assert mNotificationManager != null;
                    mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
                mBuilder.setContentIntent(intent);
                assert mNotificationManager != null;
                mNotificationManager.notify(0 /* Request Code */, mBuilder.build());

                //END NOTIF
            }
        });

    }
    //pusher end

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keuangan:
                Intent intent = new Intent(this, KeuanganActivity.class);
                startActivity(intent);
                break;
            case R.id.kajian:
                Intent pagekajian = new Intent(this, KajianActivity.class);
                startActivity(pagekajian);
                break;
            case R.id.informasitpa:
                Intent pagetpa = new Intent(this, TpaActivity.class);
                startActivity(pagetpa);
                break;
            case R.id.informasilainnya:
                Intent pageinformasi = new Intent(this, KegiatanActivity.class);
                startActivity(pageinformasi);
                break;
            case R.id.btnSignOut:
                onBackPressed();
                break;
            case R.id.profilemasjid:
                Intent pagemasjid = new Intent(this, ProfileMasjidActivity.class);
                startActivity(pagemasjid);
                break;
        }
    }

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Yakin Ingin Logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(login);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}