package com.myapplication.informasimasjid.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.halaman.informasi_kegiatan.FormKegiatan;
import com.myapplication.informasimasjid.halaman.jadwal_kajian.FormJadwalKajian;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataInformasi;
import com.myapplication.informasimasjid.model.DataKajian;

import java.util.ArrayList;
import java.util.List;

public class InformasiAdapter extends RecyclerView.Adapter<InformasiAdapter.MyViewHolder> {

    private Context context;
    private List<DataInformasi> datainformasi;

    private ProgressDialog mDialog;

    //variable untuk firebase storage
    private StorageReference reference;
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    Session sharedPrefManager;

    CollectionReference informasiRef;

    public InformasiAdapter(Context c, List<DataInformasi> p) {
        this.context = c;
        datainformasi = p;

        database = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        informasiRef = database.collection("informasi");

        sharedPrefManager = new Session(c);

    }

    public List<DataInformasi> getListInformasi() {
        return datainformasi;
    }

    public void setLisInformasi(ArrayList<DataInformasi> datainformasi) {
        this.datainformasi = datainformasi;
    }


    @NonNull
    @Override
    public InformasiAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_informasi, viewGroup, false);
        mDialog = new ProgressDialog(context);
        return new InformasiAdapter.MyViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull InformasiAdapter.MyViewHolder holder, int position) {
        String key = getListInformasi().get(position).getId();
        String judul = getListInformasi().get(position).getJudul();
        String deskripsi = getListInformasi().get(position).getDeskripsi();
        String infokontak = getListInformasi().get(position).getInfokontak();
        String tanggal = getListInformasi().get(position).getTanggal();

        holder.vjudul.setText(judul);
        holder.vketerangan.setText(deskripsi);
        holder.vinfokontak.setText(infokontak);
        holder.vtanggal.setText(tanggal);

        if(mAuth.getCurrentUser()==null || sharedPrefManager.getSes_level().equals("2")){
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }

        holder.edit.setOnClickListener(view -> {
            Intent intent = new Intent(context.getApplicationContext(), FormKegiatan.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("EXTRA_EDIT", "edit");
            intent.putExtra("EXTRA_ID", key);
            intent.putExtra("EDIT_JUDUL", judul);
            intent.putExtra("EDIT_TANGGAL", tanggal);
            intent.putExtra("EDIT_DESKRIPSI", deskripsi);
            intent.putExtra("EDIT_KONTAK", infokontak);
            context.startActivity(intent);
        });

        holder.delete.setOnClickListener(view -> {
            informasiRef.document(key).delete()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            //update Saldo setelah berhasil delete
                            datainformasi.remove(position);
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
        return datainformasi.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vjudul, vketerangan, vinfokontak, vtanggal;
        Button edit, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            vjudul = (TextView) itemView.findViewById(R.id.judulinformasi);
            vketerangan = (TextView) itemView.findViewById(R.id.keterangan);
            vinfokontak = itemView.findViewById(R.id.infokontak);
            vtanggal = itemView.findViewById(R.id.tanggal);
            edit = itemView.findViewById(R.id.btnEdit);
            delete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
