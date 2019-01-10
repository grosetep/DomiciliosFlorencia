package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by administrator on 10/07/2017.
 */
public class StringOperations {
    private static final String TAG = StringOperations.class.getSimpleName();
    public static String getStringBeforeDelimiter(String original, String delimiter){
        String value = "";

        int index = original.indexOf(delimiter);
        value = original.substring(0,index);
        return value;
    }
    public static String getAmountFormat(String amount){
        int pais = 52;
        NumberFormat format = null;
        if (amount!=null && amount != "") {
            switch (pais) {
                case 173:format = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));break;
                case 146:format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));break;
                case 52:format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));break;
            }

            return format.format(Double.parseDouble(amount));
        }else{
            return null;
        }
    }
    public static String getAmountFormat(String amount,Context ctx){
        int pais = 0;

        String id_country = ApplicationPreferences.getLocalStringPreference(ctx, Constants.id_country);
        if (id_country.isEmpty()) {
            pais = 52;
        }else{pais = Integer.parseInt(id_country);}
        NumberFormat format = null;
        if (amount!=null && amount != "") {
            switch (pais) {
                case 173:format = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));break;
                case 146:format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));break;
                case 52:format = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));break;
            }

            return format.format(Double.parseDouble(amount));
        }else{
            return null;
        }
    }
    public static String getAmountFormatWithNoDecimals(String amount){
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        format.setMaximumFractionDigits(0);
        return format.format(Double.parseDouble(amount));
    }
    public static String getStringWithCashSymbol(String amount){
        return "$ "+amount;
    }
    public static String getPercentageFormat(String number){
        return number.concat(" %");
    }
    public static String getStringWithDe(String s){return "De: " + s + " A Solo";}
    public static String getStringWithA(String s){return "" + s;}
    public static String getDecimalFormat(String amount){
        DecimalFormat df = new DecimalFormat();
        Float number = 0f;
        try {
            number = Float.parseFloat(amount);
            df.setMaximumFractionDigits(2);
        }catch(NumberFormatException e){
            return "0.00";
        }
        return df.format(number);
    }
    public static String getDecimalFormatWithNoDecimals(String amount){
        DecimalFormat df = new DecimalFormat();
        Float number = 0f;
        try {
            number = Float.parseFloat(amount);
            df.setMaximumFractionDigits(0);
        }catch(NumberFormatException e){
            return "0.00";
        }
        return df.format(number);
    }

    public static Date getDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date d = sdf.parse(date);
            return d;
        } catch (ParseException e) {
            return null;
        }
    }
    public static String getDistanceFormat(String distance_string, String pickup_number){
        try {
            float distance = Float.parseFloat(distance_string);
            DecimalFormat df = new DecimalFormat("###");
            if ((distance/1000) < 1.0) {//distancia en metros
                return pickup_number.equals("0") ? "" : df.format(distance) + " mts";//distancia ya viene en metros
            } else {//distancia en km
                double rounded_distance = Math.round((distance / 1000) * 100.0) / 100.0;
                return pickup_number.equals("0") ? "" : rounded_distance + " km";//convertir a km
            }
        } catch (NumberFormatException e) {
            return pickup_number.equals("0") ? "" : distance_string + " km";
        }
    }

}
