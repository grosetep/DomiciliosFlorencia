package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ManageLocationsActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.NewOrderActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.DestinationAddressAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickupPointFragment extends Fragment {
    private static final String TAG = PickupPointFragment.class.getSimpleName();
    private ArrayList<String> start_hours=new ArrayList();
    private ArrayList<String> end_hours=new ArrayList();
    //private ArrayList<ShippingAddress> locations=new ArrayList();
    private FrameLayout container_loading;
    //private RecyclerView recyclerview_locations;
    private CardView card_view_address;
    private ImageView image_location;
    private EditText text_shipping_address;
    private EditText text_detail;
    private LinearLayout layout_cliclable_1;

    private ShippingAddress addressSelected;
    private LinearLayout container_list;
    private DestinationAddressAdapter mAdapter;

    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;


    private ImageView button_select_map;
    private ImageView button_done_name;

    //private NewShippingAddressAdapter mAdapter;
    private int address_position_selected = -1;

    public static final int START_TIME = 1;
    public static final int END_TIME = 2;
    private int type_address = 0;
    private TextView text_more_locations;
    /*private EditText text_location;
    private EditText text_location_ltd;
    private EditText text_cp;
    private EditText text_location_lng;
    private EditText text_place_id;*/
    private EditText text_name_profile;
    private EditText text_phone_profile;
    private TextView text_date;
    private EditText text_time_from;
    private EditText text_time_to;
    private EditText text_comment;
    private TextView layout_add_destiny;
    private TextView layout_del_destiny;


    private final int PLACE_PICKER_REQUEST = 1;
    private final int CONTACT_PICKER_REQUEST = 2;
    public final static int NEW_ADDRESS = 3;
    public final int GET_ALL_ADDRESSES = 4;
    public final String TYPE_QUERY = "frequent";

    private int pickup_point_number = 0;
    private NewOrderActivity parent_activity;



    /*public String getText_place_id() {
        return text_place_id.getText().toString();
    }*/

    public int getPickup_point_number() {
        return pickup_point_number;
    }

    public void setPickup_point_number(int pickup_point_number) {
        this.pickup_point_number = pickup_point_number;
    }

    public int getType_address() {
        return type_address;
    }
    public void setType_address(int type_address) {
        this.type_address = type_address;
    }

    public void setActivityContext(NewOrderActivity activity){
        this.parent_activity = activity;
    }

    public NewOrderActivity getParent_activity() {
        return parent_activity;
    }

    public static PickupPointFragment create(int type_address, int point_number,NewOrderActivity activity) {
        //Log.d(TAG, "create " + point_number);
        PickupPointFragment fragment = new PickupPointFragment();
        fragment.setActivityContext(activity);
        Bundle args = new Bundle();
        args.putInt(Constants.type_address,type_address);
        args.putInt(Constants.number_address,point_number);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String message = savedInstanceState.getString("message");
            ShowConfirmations.showConfirmationMessage("Valor del mensaje",getActivity());
        }
        if (getArguments() != null) {
            setType_address(getArguments().getInt(Constants.type_address));
            setPickup_point_number(getArguments().getInt(Constants.number_address));
        }
        //Log.d(TAG, "onCreate is called: " + getPickup_point_number());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Log.d(TAG, "onCreateView is called: "+getPickup_point_number());
        View v =  inflater.inflate(R.layout.fragment_pickup_point, container, false);
        //Log.d(TAG,"Se crea destino: " +this.getPickup_point_number());
        init(v);
        start_hours.clear();
        end_hours.clear();
        //si es direccion de destino entonces mostrar opcion agregar destino
        if (getType_address() == Constants.DESTINATION_ADDRESS) {
            layout_add_destiny.setVisibility(View.VISIBLE);
            text_shipping_address.setText(getString(R.string.text_prompt_address_destiny));
        }else{// es el origen
            text_shipping_address.setText(getString(R.string.text_prompt_address_origin));
        }
        //mostrar opcion eliminar destino a todos los elementos, para el destino 1 se muestra la opcion de eliminar solo si hay mas de un destino
        if (parent_activity!=null) {
            if (getPickup_point_number() >= 1 && parent_activity.getTotalDestinations() >= 2) {
                layout_del_destiny.setVisibility(View.VISIBLE);
            }
        }

        //if (Connectivity.isNetworkAvailable(getContext())) {
            //locations.clear();
            //locations.add(null);
            //onSuccess();
            //initProcess(true);
            //getShippingAddresses(TYPE_QUERY);
        /*}else{
            showNoConnectionLayout();
        }*/
        return v;
    }


    private void initProcess(boolean flag){
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        container_list.setVisibility(flag?View.GONE:View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }

    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        container_list.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    private void init(View v){
        container_list = (LinearLayout) v.findViewById(R.id.container_list);
        card_view_address = v.findViewById(R.id.card_view_address);
        card_view_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                startActivityForResult(i,GET_ALL_ADDRESSES);
            }
        });
        image_location = v.findViewById(R.id.image_location);
        Drawable clone = ContextCompat.getDrawable(getActivity(), R.drawable.ic_location_on).getConstantState().newDrawable().mutate();
        image_location.setImageDrawable(GeneralFunctions.getTintedDrawable(clone, R.color.colorAccent,getActivity()));
        text_shipping_address = v.findViewById(R.id.text_shipping_address);
        text_detail = v.findViewById(R.id.text_detail);
        //text_more_locations = v.findViewById(R.id.text_more_locations);
        /*text_more_locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                startActivityForResult(i,GET_ALL_ADDRESSES);
            }
        });*/
        /*text_location = (EditText) v.findViewById(R.id.text_location);
        text_location_ltd = (EditText) v.findViewById(R.id.text_location_ltd);
        text_location_lng = (EditText) v.findViewById(R.id.text_location_lng);
        text_cp = (EditText) v.findViewById(R.id.text_cp);
        text_place_id = (EditText) v.findViewById(R.id.text_place_id);*/
        text_name_profile = (EditText) v.findViewById(R.id.text_name_profile);
        text_phone_profile = (EditText) v.findViewById(R.id.text_phone_profile);
        text_comment = (EditText) v.findViewById(R.id.text_comment);
        container_loading = (FrameLayout) v.findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);

        layout_add_destiny = (TextView) v.findViewById(R.id.layout_add_destiny);
        layout_add_destiny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParent_activity().addDestination();
            }
        });
        layout_del_destiny = (TextView) v.findViewById(R.id.layout_del_destiny);
        layout_del_destiny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParent_activity().delDestination(getPickup_point_number());
            }
        });

        //button_select_map = (ImageView) v.findViewById(R.id.button_select_map);
        button_done_name = (ImageView) v.findViewById(R.id.button_done_name);
        //button_select_map.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
                /*text_location.setError(null);
                Snackbar snackbar = Snackbar.make(v, getResources().getString(R.string.prompt_loading_map), Snackbar.LENGTH_LONG);
                snackbar.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    ShowConfirmations.showConfirmationMessage(getString(R.string.no_connection), getActivity());
                }*/
                //Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                //startActivityForResult(i,GET_ALL_ADDRESSES);
        //    }
        //});
        button_done_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact(v);
            }
        });
        text_date = (EditText) v.findViewById(R.id.text_date);
        text_date.setOnClickListener(
                 new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         DialogFragment picker = new DatePickerFragment();
                         picker.show(getFragmentManager(), "date_"+getPickup_point_number());
                     }
                 }
         );
        text_time_from = (EditText) v.findViewById(R.id.text_time_from);
        text_time_to = (EditText) v.findViewById(R.id.text_time_to);

        text_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime_Popup(PickupPointFragment.START_TIME, text_time_from, text_time_to);
            }
        });
        text_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime_Popup(PickupPointFragment.END_TIME, text_time_from, text_time_to);
            }
        });
        /*recyclerview_locations = (RecyclerView) v.findViewById(R.id.recyclerview_locations);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview_locations.setHasFixedSize(true);*/

        text_shipping_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                startActivityForResult(i,GET_ALL_ADDRESSES);
            }
        });
        text_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                startActivityForResult(i,GET_ALL_ADDRESSES);
            }
        });
        layout_cliclable_1 = v.findViewById(R.id.layout_cliclable_1);
        layout_cliclable_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),ManageLocationsActivity.class);
                startActivityForResult(i,GET_ALL_ADDRESSES);
            }
        });
    }


    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_REQUEST);
    }

   /* public void resetElementsNotSelected(){
        for(ShippingAddress loc:locations) {if (loc !=null ) {loc.setSelected(false);}}
        mAdapter.notifyDataSetChanged();
    }*/
    /*private void getShippingAddresses(String type_query) {
        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        RestServiceWrapper.getLocationsByUser(user != null ? user.getIdUser() : "0", type_query, new Callback<GetShippingAddressResponse>() {
            @Override
            public void onResponse(Call<GetShippingAddressResponse> call, retrofit2.Response<GetShippingAddressResponse> response) {
                Log.d(TAG, "Respuesta ShippingAddress: " + response);
                if (response != null && response.isSuccessful()) {
                    GetShippingAddressResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {

                        if (products_response.getResult().size() > 0) {
                            locations.addAll(products_response.getResult());
                        }

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                    } else {
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                    }

                    onSuccess();
                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                }
            }

            @Override
            public void onFailure(Call<GetShippingAddressResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {

                }
            }
        });
    }*/
    /*private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }*/
    /*private void setupAdapter(){
        // Set the adapter
        mAdapter = new DestinationAddressAdapter(getContext(),this,locations);
        recyclerview_locations.setAdapter(mAdapter);
        recyclerview_locations.invalidate();
        if (recyclerview_locations.getLayoutManager()==null){
            recyclerview_locations.setLayoutManager(llm);
        }
    }*/
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            text_name_profile.setText(name);
            text_phone_profile.setText(phoneNo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*data*/
    public String getLatitude(){
        return getAddressSelected()!=null?getAddressSelected().getLatitude().toString():"";
    }
    public String getLongitude(){
        return getAddressSelected()!=null ? getAddressSelected().getLongitude().toString():"";
    }

    public String getNameContact(){return text_name_profile.getText().toString();}
    public String getPhoneContact(){return text_phone_profile.getText().toString();}
    public String getDate(){return text_date.getText().toString();}
    public String getTimeFrom(){return text_time_from.getText().toString();}
    public String getTimeTo(){return text_time_to.getText().toString();}
    public String getComment(){return text_comment.getText().toString();}

    public ShippingAddress getAddressSelected() {
        return addressSelected;
    }

    public void setAddressSelected(ShippingAddress addressSelected) {
        this.addressSelected = addressSelected;
    }

    /*public ShippingAddress getShippingAddressSelected(){
        return getAddress_position_selected()!=-1?locations.get(getAddress_position_selected()):null;
    }*/

    public void setDateSelected(String date){
        if (text_date!=null){
            text_date.setText(date);
        }
    }
/* funciones para mostrar horas*/
    private void selectTime_Popup(int time_start_end,final EditText text_time_from,final EditText text_time_to) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_select_time, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setTitle(getString(R.string.title_alert_dialog_times,getString(time_start_end==START_TIME? R.string.prompt_from:R.string.prompt_to)));
        alertDialogBuilderUserInput.setView(mView);
        //inicializa lista
        final RecyclerView recyclerview_times = (RecyclerView) mView.findViewById(R.id.recyclerview_times);
        recyclerview_times.setItemAnimator(new DefaultItemAnimator());
        recyclerview_times.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL) );
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recyclerview_times.getLayoutManager()==null){ recyclerview_times.setLayoutManager(llm);}
        //agrega contenido
        // Defined Array values to show in ListView
        Calendar initial_time = getStartTime(time_start_end,getType_address());
        ArrayList<String> values = getAvailableHours(time_start_end, initial_time);
        //create alert dialog content and show

        alertDialogBuilderUserInput
        .setNegativeButton(getContext().getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();

        //create adapter and passing by parameter alert dialog
        TimesAdapter mAdapter = new TimesAdapter(getActivity(),values,alertDialogAndroid,time_start_end,text_time_from,text_time_to);
        recyclerview_times.setAdapter(mAdapter);
    }
    private Calendar getStartTime(int time_start_end,int type_address){
        Calendar start_time_work_day = Calendar.getInstance();
        int rigth_now_hour = start_time_work_day.get(Calendar.HOUR_OF_DAY);

        String date_user[] = text_date.getText().toString().split("-");
        Calendar user_selected_date = Calendar.getInstance();

        if (date_user!=null && date_user.length==3) {
            user_selected_date.set(Calendar.MONTH, Integer.parseInt(date_user[1]) - 1);//meses de 0-11
            user_selected_date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_user[0]));
        }

        if ( rigth_now_hour<=Constants.end_hour_work_day) {//aun esta dentro del horario de servicio

            if (time_start_end == PickupPointFragment.START_TIME) {

                if (rigth_now_hour < Constants.start_hour_work_day || user_selected_date.compareTo(start_time_work_day)>0) {//si es muy temprano, se pone la hora default de inicio de actividades, o la fecha es futura
                    start_time_work_day.set(Calendar.HOUR_OF_DAY, Constants.start_hour_work_day);
                    start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
                } else {//sino entonces se calcula la hora de inicio asi como el minuto ya que es alguna hora del dia
                    int rigth_now_minute = start_time_work_day.get(Calendar.MINUTE);

                    if (rigth_now_minute <= Constants.minute_10) {
                        start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
                    } else if (rigth_now_minute > Constants.minute_10 && rigth_now_minute <= Constants.minute_45) {
                        start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                        start_time_work_day.set(Calendar.MINUTE, Constants.minute_0);
                    } else if (rigth_now_minute > Constants.minute_45) {//se asigna la siguinte hora en punto
                        start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                        start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
                    } else {
                        start_time_work_day.set(Calendar.MINUTE, Constants.minute_0);
                        start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                    }
                }
            }else if(time_start_end == PickupPointFragment.END_TIME){
            //verificar si hay hora seleccionada inicio,
            // si existe asignar esa hora de inicio
                Log.d(TAG,"1..." );
                if (!text_time_from.getText().toString().isEmpty() && !text_time_from.getText().toString().equals(Constants.now) ){Log.d(TAG,"2..." );
                    //Log.d(TAG,"haY HORA inicio seleccionada......"+text_time_from.getText().toString() );
                    Calendar start_time_selected = GeneralFunctions.getCalendarFromTime(text_time_from.getText().toString());
                    start_time_work_day = start_time_selected;
                    start_time_work_day.add(Calendar.MINUTE,Constants.minute_30);
                }else{//NO EXISTE hora inicio seleccionada
                    Log.d(TAG,"3..." );
                    if (rigth_now_hour < Constants.start_hour_work_day) {//si es muy temprano, se pone la hora default de inicio de actividades
                        Log.d(TAG,"4..." );
                        start_time_work_day.set(Calendar.HOUR_OF_DAY, Constants.start_hour_work_day);
                        start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);

                    }else {Log.d(TAG,"5..." );
                        int rigth_now_minute = start_time_work_day.get(Calendar.MINUTE);

                        if (rigth_now_minute <= Constants.minute_10) {
                            start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
                        } else if (rigth_now_minute > Constants.minute_10 && rigth_now_minute <= Constants.minute_45) {
                            start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                            start_time_work_day.set(Calendar.MINUTE, Constants.minute_0);
                        } else if (rigth_now_minute > Constants.minute_45) {//se asigna la siguinte hora en punto
                            start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                            start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
                        } else {
                            start_time_work_day.set(Calendar.MINUTE, Constants.minute_0);
                            start_time_work_day.add(Calendar.HOUR_OF_DAY, Constants.uno);
                        }
                    }
                }


            }
        }else{//Fuera de horario de servicio, mostrar horas iniciales del siguiente dia
            start_time_work_day.set(Calendar.HOUR_OF_DAY, Constants.start_hour_work_day);
            start_time_work_day.set(Calendar.MINUTE, Constants.minute_30);
        }

        return start_time_work_day;
    }
    private ArrayList getAvailableHours(int time_start_end, Calendar initial_time){
        ArrayList<String> hours = new ArrayList();
        Calendar right_now = Calendar.getInstance();

        //Establecer horario maximo de servicio
        Calendar hour_max = Calendar.getInstance();
        hour_max.set(right_now.get(Calendar.YEAR),right_now.get(Calendar.MONTH),right_now.get(Calendar.DAY_OF_MONTH),Constants.end_hour_work_day,Constants.minute_30,Constants.minute_0);
        //Variables para calculo de cada medias horas
        Calendar time_iteration = initial_time;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

        //crear primer elemento "Ahora" y "Sin prisa" dependiendo del campo

        switch (time_start_end){
            case PickupPointFragment.START_TIME:hours.add(Constants.now);break;
            case PickupPointFragment.END_TIME:hours.add(Constants.no_hurry);break;
        }

        //Generar lista de medias horas a partir de la hora calculada de inicio
        while(time_iteration.compareTo(hour_max)<=0){//generar horario mientrs este dentro del horario de servicio
            String hora =dateFormat.format(time_iteration.getTime());
            hours.add(hora);
            time_iteration.add(Calendar.MINUTE,Constants.minute_30);//sumar media hora cada vez
        }
        return hours;
    }

    /*public int getAddress_position_selected() {
        return address_position_selected;
    }*/

    /*public void resetElement(int position){
        if (position!=-1) {
            ShippingAddress old_location = locations.get(position);

            if (old_location != null) {
                old_location.setSelected(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }*/
    /*public void setShippingAddressSelected(int position){
        address_position_selected = position;
        locations.get(position).setSelected(true);
        mAdapter.notifyItemChanged(position);
    }*/
    /*public void startAddShippingAddress(){
        Intent i = new Intent(getContext(),AddShippingAddressActivity.class);
        startActivityForResult(i, NEW_ADDRESS);
    }*/
    public void startManageShippingAddresses(){
        Intent i = new Intent(getContext(),ManageLocationsActivity.class);
        startActivityForResult(i,GET_ALL_ADDRESSES);
    }
    /*@Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        supermarkets.onRestoreInstanceState(savedInstanceState);

        title = savedInstanceState.getString(TITLE);
        isHidden = savedInstanceState.getBoolean(IS_HIDDEN);
        rating = savedInstanceState.getDouble(RATING);
        year = savedInstanceState.getInt(YEAR);
    }*/
    /***********************************************Adapter*****************************************************/
    class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.TimeViewHolder> {
        private final LayoutInflater mInflater;
        private final ArrayList<String> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;
        private EditText from;
        private EditText to;
        private int time_start_end;
        private AlertDialog alertDialog;
        public class TimeViewHolder extends RecyclerView.ViewHolder {

            private final TextView text_label;

            public TimeViewHolder(View itemView) {
                super(itemView);
                text_label = (TextView) itemView.findViewById(R.id.text_label);
            }

            public void bind(String model) {
                text_label.setText(model);
            }


        }

        public TimesAdapter(Activity context, ArrayList<String> models,AlertDialog alert, int start_end, EditText text_from, EditText text_to) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
            time_start_end = start_end;
            from = text_from;
            to = text_to;
            alertDialog = alert;
        }

        @Override
        public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.item_text_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new TimeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final TimeViewHolder holder, int position) {
            final String model = mModels.get(position);
            holder.bind(model);
            final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (time_start_end == PickupPointFragment.START_TIME) {
                        //si existe hora fin seleccionada, validar si es posterior a la hora inicio,
                        // sino entonces establecer hora final igual a l ahora de inicio mas 30 minutos
                        from.setText(model);
                        if (!to.getText().toString().isEmpty() && !to.getText().toString().equals(Constants.no_hurry)) {
                            Calendar time_from = GeneralFunctions.getCalendarFromTime(model);
                            Calendar time_to = Calendar.getInstance();
                            if (!model.equals(Constants.now)) {
                                time_to = GeneralFunctions.getCalendarFromTime(to.getText().toString());
                            }

                            if (time_from.compareTo(time_to) >= 0) {
                                time_to = time_from;
                                time_to.add(Calendar.MINUTE, Constants.minute_30);
                                String hora = dateFormat.format(time_to.getTime());
                                to.setText(hora);
                            }

                        }
                    } else if(time_start_end == PickupPointFragment.END_TIME) {
                        Log.d(TAG, "PickupPointFragment.END_TIME..."+model);
                        to.setText(model);
                    }

                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }


                }
            });
        }
        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    /*Place place = PlacePicker.getPlace(getActivity(), data);
                    String postal_code = "";
                    if (place != null && !place.getAddress().equals("")) {
                        //Log.d(TAG, place.getAddress() + " -> " + place.getLatLng() );

                        text_location.setError(null);
                        text_location.setText(place.getAddress());

                        LatLng latlng = place.getLatLng();
                        text_location_ltd.setText(latlng.latitude+"");
                        text_location_lng.setText(latlng.longitude+"");
                        text_place_id.setText(place.getId());
                        String POSTAL_CODE_REGEX = "\\d{5}";
                        Pattern p = Pattern.compile(POSTAL_CODE_REGEX);
                        Matcher m = p.matcher(place.getAddress());

                        if(m.find()) {
                            postal_code =  m.group(0);
                        }
                        text_cp.setText(postal_code);

                        //ejecuta nuevo ajax para recuperar el detalle del sitio

                    }*/
                }
                break;
            case CONTACT_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    contactPicked(data);
                }
                break;
            case GET_ALL_ADDRESSES://direccion que se seleccionó de la lista general
                if (resultCode == Activity.RESULT_OK) {
                    ShippingAddress ship = (ShippingAddress) data.getSerializableExtra(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS);
                    //reset all elements selected
                    /*if (getAddress_position_selected() != -1) {//ya hay uno seleccionado
                        resetElement(getAddress_position_selected());
                    }*/
                    //Reset de todos los elementos existentes a..no seleccionados
                    //resetElementsNotSelected();

                    //validar si se agrega o solo se selecciona de la lista que ya existe
                    //int index = getIndexLocation(ship);
                    //if (index!=-1){//ya existe la direccion en la lista
                        //seleccionar
                        //se actualizan los datos nuevamente ya que pudo ser editada
                    /*    locations.remove(index);//elimina la anterior
                        locations.add(index,ship);//agrega la nueva en la posicion que estaba
                        setShippingAddressSelected(index);
                        recyclerview_locations.scrollToPosition(index);
                    }else{
                        mAdapter.addItem(0, ship);
                        setShippingAddressSelected(0);
                        recyclerview_locations.scrollToPosition(0);
                    }*/

                    /*locations.clear();
                    locations.add(ship);
                    setShippingAddressSelected(0);
                    recyclerview_locations.setVisibility(View.VISIBLE);*/
                    setAddressSelected(ship);
                    setAddressValues(ship);



                }/*else if(resultCode == Activity.RESULT_CANCELED){
                    //validar si es necesario refrescar la lista inicial en caso de que se hayan hecho updates en las direcciones
                    Log.d(TAG,"RESULT_CANCELED");
                    if (ManageLocationsActivity.has_address_updated == 1){
                        Log.d(TAG,"RELOAD!!!!");
                        ManageLocationsActivity.has_address_updated = 0;
                    }else{
                        Log.d(TAG,"no RELOAD!!!!  "+ManageLocationsActivity.has_address_updated);
                    }
                }*/
                break;
            case NEW_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {//NEW_SHIPPING_ADDRESS
                   /* ShippingAddress ship = (ShippingAddress) data.getSerializableExtra(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS);
                    //reset all elements selected
                    if (getAddress_position_selected() != -1) {//ya hay uno seleccionado
                        resetElement(getAddress_position_selected());
                    }
                    //Reset de todos los elementos existentes a..no seleccionados
                    resetElementsNotSelected();
                    //create element on list
                    ship.setIsNew(String.valueOf(true));
                    mAdapter.addItem(0, ship);
                    setShippingAddressSelected(0);
                    recyclerview_locations.scrollToPosition(0);*/
                }
                break;
        }
    }

    private void setAddressValues(ShippingAddress model){
        if (model!=null){
            Drawable clone = ContextCompat.getDrawable(getActivity(), R.drawable.ic_location_on).getConstantState().newDrawable().mutate();
            image_location.setImageDrawable(GeneralFunctions.getTintedDrawable(clone, R.color.colorAccent,getActivity()));
            String show_address  = model.getGooglePlace().length()> Constants.address_max_length?model.getGooglePlace().substring(0,Constants.address_max_length)+"...,":model.getGooglePlace();
            text_shipping_address.setText(show_address);
            text_detail.setVisibility(View.VISIBLE);
            text_detail.setText((model.getNum_int() != null && !model.getNum_int().isEmpty() ? getString(R.string.prompt_interior)+model.getNum_int()+", " : "").concat(model.getReference() != null && !model.getReference().isEmpty() ? model.getReference():""));
        }
    }

    /*private int getIndexLocation(ShippingAddress ship){
        int index = -1;
        if (ship.getIsNew().equals(String.valueOf(true))) return index;

        if (locations.size()>0){//busca en la lista
            for (int i=0;i<locations.size();i++){
                if (locations.get(i)!=null) {
                    if (locations.get(i).getId_location().equals(ship.getId_location())) {//ya existe
                        return i;
                    }
                }
            }
        }
        return index;
    }*/
}
