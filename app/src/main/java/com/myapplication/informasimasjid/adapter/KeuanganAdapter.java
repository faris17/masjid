package com.myapplication.informasimasjid.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapplication.informasimasjid.R;
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

    public KeuanganAdapter(Context c, List<DataKeuangan> p) {
        this.context = c;
        datakeuangans = p;

        database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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
        String premi = "000"+getListKeuangan().get(position).getHarga();
        String kategori = getListKeuangan().get(position).getKategori();
        if(kategori.equals("pemasukan")){
            holder.nama.setText(getListKeuangan().get(position).getKeterangan());
        }
        else {
            holder.nama.setText(getListKeuangan().get(position).getKeterangan());
            holder.keluar.setCardBackgroundColor(Color.RED);
            holder.nama.setTextColor(Color.WHITE);
            holder.tanggal.setTextColor(Color.WHITE);
            holder.uang.setTextColor(Color.WHITE);
        }

        holder.tanggal.setText(getListKeuangan().get(position).getTanggal());
        holder.uang.setText(kursIdr.format(Long.valueOf(premi)));

    }

    @Override
    public int getItemCount() {
        return datakeuangans.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nama, tanggal, uang;
        CardView keluar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nama = (TextView) itemView.findViewById(R.id.keterangankeuangan);
            tanggal = (TextView) itemView.findViewById(R.id.tanggal);
            uang = itemView.findViewById(R.id.jmluangsedekah);
            keluar = itemView.findViewById(R.id.card_keuangan);

        }
    }

}
