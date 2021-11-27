package com.myapplication.informasimasjid.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.halaman.jadwal_kajian.FormJadwalKajian;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataKajian;
import com.myapplication.informasimasjid.model.DataKeuangan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;


public class KajianAdapter extends RecyclerView.Adapter<KajianAdapter.MyViewHolder> {

    private Context context;
    private List<DataKajian> datakajians;

    private ProgressDialog mDialog;

    //variable untuk firebase storage
    private StorageReference reference;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    CollectionReference kajianRef;

    Session sharedPrefManager;

    public KajianAdapter(Context c, List<DataKajian> p) {
        this.context = c;
        datakajians = p;

        database = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        kajianRef = database.collection("kajian");

        sharedPrefManager = new Session(c);
    }

    public List<DataKajian> getListKajian() {
        return datakajians;
    }

    public void setListKajian(ArrayList<DataKajian> datakajians) {
        this.datakajians = datakajians;
    }

    @NonNull
    @Override
    public KajianAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kajian, viewGroup, false);
        mDialog = new ProgressDialog(context);
        return new KajianAdapter.MyViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull KajianAdapter.MyViewHolder holder, final int position) {

        String key = getListKajian().get(position).getId();
        String namakegiatan = getListKajian().get(position).getJudul();
        String temakegiatan = getListKajian().get(position).getTema();
        String tempatkegiatan = getListKajian().get(position).getTempat();
        String time = getListKajian().get(position).getTime();
        String hari = getListKajian().get(position).getHari();
        String penceramah = getListKajian().get(position).getNarasumber();
        String keterangan = getListKajian().get(position).getKeterangan();
        String linkyoutube = getListKajian().get(position).getLink();

            getImage("images/"+getListKajian().get(position).getId(), holder.brosur);

        holder.vagenda.setText(namakegiatan);
        holder.vtema.setText(temakegiatan);
        holder.tempat.setText(tempatkegiatan);
        holder.hari.setText(hari);
        holder.jam.setText(time);
        holder.penceramah.setText(penceramah);
        holder.deskripsi.setText(keterangan);
        holder.link.setText(linkyoutube);

        holder.link.setOnClickListener(view ->{
            Uri uri = Uri.parse(linkyoutube);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if(mAuth.getCurrentUser()==null || sharedPrefManager.getSes_level().equals("2")){
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(context.getApplicationContext(), FormJadwalKajian.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXTRA_EDIT", "edit");
            intent.putExtra("EXTRA_ID", key);
            intent.putExtra("EDIT_JUDUL", namakegiatan);
            intent.putExtra("EDIT_TEMA", temakegiatan );
            intent.putExtra("EDIT_DAY", hari);
            intent.putExtra("EDIT_TIME", time);
            intent.putExtra("EDIT_DESKRIPSI", keterangan);
            intent.putExtra("EDIT_TEMPAT", tempatkegiatan);
            intent.putExtra("EDIT_NARASUMBER", penceramah);
            context.startActivity(intent);
        });

        holder.delete.setOnClickListener(view -> {
            kajianRef.document(key).delete()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            //update Saldo setelah berhasil delete
                            reference.child("images/" + key).delete();
                            datakajians.remove(position);
                            Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Data deleted failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });




    }

    @Override
    public int getItemCount() {
        return datakajians.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vagenda, vtema, tempat, jam, penceramah, deskripsi, hari, link;
        ImageView brosur;
        Button edit, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            vagenda = (TextView) itemView.findViewById(R.id.agenda);
            tempat = (TextView) itemView.findViewById(R.id.tempat);
            jam = itemView.findViewById(R.id.jam);
            penceramah = itemView.findViewById(R.id.penceramah);
            deskripsi = itemView.findViewById(R.id.deskripsi);
            brosur = itemView.findViewById(R.id.brosur);
            vtema = (TextView) itemView.findViewById(R.id.temakegiatan);
            hari = itemView.findViewById(R.id.harikegiatan);
            link = itemView.findViewById(R.id.linkyoutube);

            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);


        }
    }

    public void getImage(String data, final ImageView foto) {
        reference.child(data).getDownloadUrl().addOnSuccessListener(uri -> {

            // Got the download URL for 'images/brosur.png'
            Glide.with(context)
                    .load(uri)
                    .into(foto);
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

}