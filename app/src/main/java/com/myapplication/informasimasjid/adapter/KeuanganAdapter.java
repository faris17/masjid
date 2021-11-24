package com.myapplication.informasimasjid.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapplication.informasimasjid.R;
import com.myapplication.informasimasjid.halaman.keuangan.KeuanganActivity;
import com.myapplication.informasimasjid.library.Session;
import com.myapplication.informasimasjid.model.DataKeuangan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class KeuanganAdapter extends RecyclerView.Adapter<KeuanganAdapter.MyViewHolder> {

    private Context context;
    private List<DataKeuangan> datakeuangans;

    private ProgressDialog mDialog;

    //variable untuk firebase storage
    FirebaseFirestore database;
    FirebaseAuth mAuth;

    DecimalFormat kursIdr;
    DecimalFormatSymbols formatRp;

    CollectionReference keuanganRef, saldosekarang;

    Session sharedPrefManager;

    public KeuanganAdapter(Context c, List<DataKeuangan> p) {
        this.context = c;
        datakeuangans = p;

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        keuanganRef = database.collection("keuangan");
        saldosekarang = database.collection("saldosekarang");

        sharedPrefManager = new Session(c);
    }

    public List<DataKeuangan> getListKeuangan() {
        return datakeuangans;
    }

    public void setListKeuangan(ArrayList<DataKeuangan> datakeuangans) {
        this.datakeuangans = datakeuangans;
    }


    @NonNull
    @Override
    public KeuanganAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_keuangan, viewGroup, false);
        mDialog = new ProgressDialog(context);
        return new KeuanganAdapter.MyViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(@NonNull KeuanganAdapter.MyViewHolder holder, final int position) {

        kursIdr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp ");
        formatRp.setMonetaryDecimalSeparator(',');
        kursIdr.setDecimalFormatSymbols(formatRp);
        String uang = getListKeuangan().get(position).getNominal();
        String premi = "000"+uang;
        String kategori = getListKeuangan().get(position).getKategori();
        if(kategori.equals("pemasukan")){
            holder.nama.setText(getListKeuangan().get(position).getKeterangan());
        }
        else {
            holder.nama.setText(getListKeuangan().get(position).getKeterangan());
            holder.uang.setTextColor(Color.RED);
        }

        holder.tanggal.setText(getListKeuangan().get(position).getTanggal());
        holder.uang.setText(kursIdr.format(Long.valueOf(premi)));

        String key = getListKeuangan().get(position).getId();

        if(mAuth.getCurrentUser()==null || sharedPrefManager.getSes_level().equals("1")){
            holder.del.setVisibility(View.GONE);
        }
        holder.del.setOnClickListener(view -> {
            keuanganRef.document(key).delete()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            //update Saldo setelah berhasil delete
                            saldosekarang.document("now").get().addOnSuccessListener(
                                    new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            int saldo = (Integer) Integer.parseInt((String) documentSnapshot.get("jumlahsaldo"));
                                            int updateSaldo;

                                            if(kategori.equals("pemasukan")){
                                                updateSaldo  = saldo - Integer.parseInt(uang);
                                            } else {
                                                updateSaldo  = saldo + Integer.parseInt(uang);
                                            }

                                            String saldonow = Integer.toString(updateSaldo);
                                            saldosekarang.document("now").update("jumlahsaldo", saldonow);
                                        }
                                    }
                            );
                            Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Data delete failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return datakeuangans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama, tanggal, uang;
        ImageView del;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = (TextView) itemView.findViewById(R.id.keterangankeuangan);
            tanggal = (TextView) itemView.findViewById(R.id.tanggal);
            uang = itemView.findViewById(R.id.jmluang);
            del = itemView.findViewById(R.id.delete);

        }
    }

}
