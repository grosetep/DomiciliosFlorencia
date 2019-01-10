package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DatePickerFragment  extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = DatePickerFragment.class.getSimpleName();
        OnDateSelectedListener mCallback;

// Container Activity must implement this interface
public interface OnDateSelectedListener {
    void onDateSelected(int year, int month, int day);
}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
        //Get yesterday's date
        Calendar calendar = Calendar.getInstance();
        picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        // Create a new instance of DatePickerDialog and return it
        return picker;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            Activity   a;
            if (context instanceof Activity){
                a=(Activity) context;
            }

            mCallback = (OnDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnDateSelectedListener");
        }
    }

    @Override
    public void onDateSet(DatePicker view, int ano, int mes, int dia) {
        mCallback.onDateSelected(ano, mes, dia);
    }
}
