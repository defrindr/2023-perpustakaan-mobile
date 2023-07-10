package com.example.perpusmini.helpers;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    public Context instance;

    public static int DENDA = 500;

    public Helper(Context instance) {
        this.instance = instance;
    }

    public boolean notEmpty(TextInputLayout target) {
        String value = getValue(target);
        if (!value.isEmpty()) return true;

        target.setErrorEnabled(true);
        target.setError(target.getHint().toString() + " tidak boleh kosong");
        return false;
    }

    public String getValue(TextInputLayout target) {
        return target.getEditText().getText().toString().trim();
    }

    public void toastMessage(String message) {
        Toast.makeText(instance, message, Toast.LENGTH_SHORT).show();
    }

    public long getEpochTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long epoch = calendar.getTime().getTime();

        return epoch;
    }
    public long getEpochTime(String source) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.parse(source).getTime();
    }

}
