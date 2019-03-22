package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;


import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderPurchaseItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ElementChanged;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PickupAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by administrator on 10/07/2017.
 */
public class GeneralFunctions {
    public static ArrayList<PublicationCardViewModel> FilterPublications(ArrayList<PublicationCardViewModel> publications, ArrayList<PublicationCardViewModel> new_publications){
        Iterator<PublicationCardViewModel> iter = new_publications.iterator();
        while (iter.hasNext()){
            PublicationCardViewModel p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_publications;
    }
    public static ArrayList<MerchantItem> FilterMerchants(ArrayList<MerchantItem> publications, ArrayList<MerchantItem> new_publications){
        Iterator<MerchantItem> iter = new_publications.iterator();
        while (iter.hasNext()){
            MerchantItem p = iter.next();
            if (publications.contains(p)){ iter.remove();}
        }
        return new_publications;
    }
    public static UserItem getCurrentUser(Context context){
        Gson gson = new Gson();
        UserItem user = null;
        String json_user = ApplicationPreferences.getLocalStringPreference(context, Constants.user_object);
        if (json_user!=null || !json_user.isEmpty()){
            user = gson.fromJson(json_user,UserItem.class);
        }
        return user;
    }
    public static String getTokenUser(Context context){
        Gson gson = new Gson();

        String json_token = ApplicationPreferences.getLocalStringPreference(context, Constants.firebase_token);

        return json_token;
    }
    public static ConfigItem getConfiguration(Context context){
        Gson gson = new Gson();
        ConfigItem config = null;
        String json_config = ApplicationPreferences.getLocalStringPreference(context, Constants.configuration_object);
        if (json_config!=null || !json_config.isEmpty()){
            config = gson.fromJson(json_config,ConfigItem.class);
        }
        return config;
    }
    public static List<OrderItem> Filter(ArrayList<OrderItem> elements, List<OrderItem> new_elements){
        Iterator<OrderItem> iter = new_elements.iterator();
        while (iter.hasNext()){
            OrderItem p = iter.next();
            for (int i = 0;i<elements.size();i++) {
                if (elements.get(i).getIdOrder().equals(p.getIdOrder())) {
                    //si cambio una orden de estatus dejar el mas actualizado y borrar el viejo
                    elements.remove(i);
                    //iter.remove();EN LUGAR DE ELIMINAR EL NUEVO AHORA SE ELIMINA EL VIEJO Y SE CONSERVA EN NUEVO
                }
            }
        }
        return new_elements;
    }
    public static List<OrderPurchaseItem> FilterPurchases(ArrayList<OrderPurchaseItem> elements, List<OrderPurchaseItem> new_elements){
        Iterator<OrderPurchaseItem> iter = new_elements.iterator();
        while (iter.hasNext()){
            OrderPurchaseItem p = iter.next();
            for (int i = 0;i<elements.size();i++) {
                if (elements.get(i).getIdOrder().equals(p.getIdOrder())) {
                    //si cambio una orden de estatus dejar el mas actualizado y borrar el viejo
                    elements.remove(i);
                    //iter.remove();EN LUGAR DE ELIMINAR EL NUEVO AHORA SE ELIMINA EL VIEJO Y SE CONSERVA EN NUEVO
                }
            }
        }
        return new_elements;
    }
    public static Calendar getCalendarFromTime(String time){
        String[] selected_time = time.split(":");
        int hour = Integer.parseInt(selected_time[0]);
        int minute = Integer.parseInt(selected_time[1]);
        Calendar time_selected = Calendar.getInstance();
        time_selected.set(Calendar.HOUR_OF_DAY,hour);
        time_selected.set(Calendar.MINUTE, minute);
        return time_selected;
    }
    public static ElementChanged somethingHasChanged(ArrayList<PickupAddress> list,PickupAddress point){
        //return index element, new ?, changed
        ElementChanged element = new ElementChanged();
        if (list==null || point==null ) {//valores no validos
            element.setChanged(false);
            element.setIndexElement(-1);
            element.setIsNew(false);
            return element;
        }
        if (list!=null && list.size()==0 && point!=null){//escenario inicial
            element.setChanged(false);
            element.setIndexElement(-1);
            element.setIsNew(true);
            return element;
        }else {//ya existe alguna direccion
            element.setIsNew(true);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPickupPointNumber() == point.getPickupPointNumber()) {
                    element.setIsNew(false);//elemento existe
                    element.setIndexElement(i);
                    if (list.get(i).getGooglePlace()!=null) {
                        if (list.get(i).getGooglePlace().getLatitude().compareTo(point.getGooglePlace().getLatitude()) == 0 &&
                                list.get(i).getGooglePlace().getLongitude().compareTo(point.getGooglePlace().getLongitude()) == 0 &&
                                list.get(i).getNameContact().compareTo(point.getNameContact()) == 0 &&
                                list.get(i).getPhoneContact().compareTo(point.getPhoneContact()) == 0 &&
                                list.get(i).getDateShipping().compareTo(point.getDateShipping()) == 0 &&
                                list.get(i).getStartHour().compareTo(point.getStartHour()) == 0 &&
                                list.get(i).getEndHour().compareTo(point.getEndHour()) == 0 &&
                                list.get(i).getComment().compareTo(point.getComment()) == 0) {
                            element.setChanged(false);//no hay modificaciones

                        } else {
                            element.setChanged(true);/*usuario actualizo algun dato*/
                        }
                    }
                    break;//elemento evaluado, terminar proceso
                }


            }
        }
        return element;
    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
    public static  String getDistanceFormat(String distance_string, String pickup_number){
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

    public static  ArrayList<PickupAddress> cleanDirections( ArrayList<PickupAddress> destinations){
        if (destinations!=null) {
            for (PickupAddress a : destinations) {
                if (a.isAddedByCircularTour()) {
                    Log.d("cleanDirections","Se eliminó: " + a.getPickupPointNumber());
                    destinations.remove(a);

                }
            }
        }
        return destinations;
    }
    public static  Drawable getTintedDrawable(Drawable clone,@ColorRes int colorResId,Context ctx) {

        int color = ContextCompat.getColor(ctx, colorResId);
        clone.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return clone;
    }
    public static String getTitleByServiceSelected(String type_service, Context ctx){
        String service_name = "";
        switch (type_service) {
            case Constants.service_supers:
                service_name = ctx.getString(R.string.title_super);
                break;
            case Constants.service_restaurants:
                service_name = ctx.getString(R.string.title_restaurants);
                break;
            case Constants.service_custom:
                service_name = ctx.getString(R.string.title_anything);
                break;
            case Constants.service_mototaxi:
                service_name = ctx.getString(R.string.title_mototaxi);
                break;
            case Constants.service_drugstore:
                service_name = ctx.getString(R.string.title_farmacy);
                break;
        }
        return service_name;
    }
    public static String getTitleByServiceSelectedOnMoreOption(String type_service, Context ctx){
        String service_name = "";
        switch (type_service) {
            case Constants.service_supers:
                service_name = ctx.getString(R.string.title_super_singular);
                break;
            case Constants.service_restaurants:
                service_name = ctx.getString(R.string.title_restaurants_singular);
                break;
            case Constants.service_custom:
                service_name = ctx.getString(R.string.title_anything);
                break;
            case Constants.service_mototaxi:
                service_name = ctx.getString(R.string.title_mototaxi);
                break;
            case Constants.service_drugstore:
                service_name = ctx.getString(R.string.title_farmacy_singular);
                break;
        }
        return service_name;
    }
}
