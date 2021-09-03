package com.myapplication.informasimasjid.library;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.myapplication.informasimasjid.R;

import java.util.Calendar;

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText viewTanggal;
    TextView textView;
    Button textView2;
    View content;
    String name;

    @SuppressLint("ValidFragment")
    public SelectDateFragment(View content, String name) {
        this.content=content;
        this.name=name;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,yy,mm,dd);
        return datepickerdialog;
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    public void populateSetDate(int year, int month, int day) {
        if(!name.equals("")){
            textView = content.findViewById(R.id.tanggal);
            textView2 = content.findViewById(R.id.tanggal);
            textView.setText(day + "/" + month + "/" + year);
            textView2.setEnabled(true);
        }else {
            viewTanggal = content.findViewById(R.id.tanggal);
            viewTanggal.setText(day + "/" + month + "/" + year);
        }
    }
}
