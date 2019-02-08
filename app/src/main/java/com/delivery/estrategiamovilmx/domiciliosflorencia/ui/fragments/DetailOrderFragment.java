package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.FactorItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.HelpText;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int VEHICLE = 1;
    private static final int WEIGTH = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = DatePickerFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;


    private EditText text_content;
    private EditText text_vehicle;
    private EditText text_weighing;
    private ImageView button_info;
    private ImageView button_info_circular;

    private CheckBox checkbox_circular_tour;

    //rates info fields: inicializar elementos y llenar con la info de la ocnfiguracion cargada en json string
    private TextView text_base_rate;
    private TextView text_base_distance;
    private ImageView image_info;

    // TODO: Rename and change types and number of parameters
    public static DetailOrderFragment newInstance() {
        DetailOrderFragment fragment = new DetailOrderFragment();

        return fragment;
    }

    public DetailOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_order, container, false);
        init(v);
        //setting rates values
        Gson gson = new Gson();
        String rates_string_json = ApplicationPreferences.getLocalStringPreference(getContext(),Constants.initial_rate_object);
        String id_country = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.id_country);
        if (!rates_string_json.isEmpty()){
            FactorItem factor = gson.fromJson(rates_string_json, FactorItem.class);
            //se muestra por defecto el precio para moto, pero en la pantalla de detalle debera mostrar por el tipo de vehiculo seleccionado
            if (factor!=null){
                text_base_distance.setText(getString(R.string.prompt_case_distance,Float.parseFloat(factor.getBaseDistanceMts())/1000));
                text_base_rate.setText(StringOperations.getAmountFormat(factor.getBaseRate(),id_country));
            }
        }
        return v;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public EditText getText_content() {
        return text_content;
    }

    public EditText getText_vehicle() {
        return text_vehicle;
    }

    public EditText getText_weighing() {
        return text_weighing;
    }

    public CheckBox getCheckbox_circular_tour() {
        return checkbox_circular_tour;
    }

    private void init(View v){
        Gson gson = new Gson();
        String list_texts_json = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.help_texts_list);
        HelpText[] help_texts_Array = gson.fromJson(list_texts_json, HelpText[].class);


        final String text_rates = help_texts_Array[0]!=null?help_texts_Array[0].getHtml():"";//la lista esta ordenada. indice 0 indica el elemento ayuda 1.
        final String text_weigth = help_texts_Array[1]!=null?help_texts_Array[1].getHtml():"";//la lista esta ordenada. indice 0 indica el elemento ayuda 2.
        final String text_circular = help_texts_Array[2]!=null?help_texts_Array[2].getHtml():"";//la lista esta ordenada. indice 0 indica el elemento ayuda 3.

        text_content = (EditText)v.findViewById(R.id.text_content);
        text_vehicle = (EditText) v.findViewById(R.id.text_vehicle);
        text_weighing = (EditText) v.findViewById(R.id.text_weighing);
        button_info = (ImageView) v.findViewById(R.id.button_info);
        button_info = v.findViewById(R.id.button_info);
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_PopupInfo(getContext(),text_weigth);
            }
        });
        button_info_circular = v.findViewById(R.id.button_info_circular);
        button_info_circular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_PopupInfo(getContext(),text_circular);
            }
        });

        text_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime_Popup(VEHICLE);
            }
        });
        text_weighing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime_Popup(WEIGTH);
            }
        });
        checkbox_circular_tour = (CheckBox) v.findViewById(R.id.checkbox_circular_tour);
        //rate fields
        text_base_rate = v.findViewById(R.id.text_base_rate);
        text_base_distance = v.findViewById(R.id.text_base_distance);

        image_info = v.findViewById(R.id.image_info);
        image_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_PopupInfo(getContext(),text_rates);
            }
        });
    }
    private void selectTime_Popup(int type_popup) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_select_time, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setTitle(type_popup==VEHICLE? R.string.prompt_select_vehicle:R.string.prompt_select_weigth);
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

        ArrayList<String> values = type_popup==VEHICLE?getVehicles():getWeigths();
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
        TimesAdapter mAdapter = new TimesAdapter(getActivity(),values,alertDialogAndroid,type_popup,text_vehicle,text_weighing);
        recyclerview_times.setAdapter(mAdapter);
    }
    private void show_PopupInfo(Context context,String html_text) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        final View content = layoutInflaterAndroid.inflate(R.layout.show_generic_info_layout, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = mView.findViewById(R.id.content);
        fields.addView(content);
        android.support.v7.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.support.v7.app.AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);
        //getting reference to text field and setting html text
        final ProgressBar pbLoading_update = (ProgressBar) mView.findViewById(R.id.pbLoading_update);
        final TextView layout_text = (TextView) mView.findViewById(R.id.layout_text);
        final TextView layout_text_error = (TextView) mView.findViewById(R.id.layout_text_error);
        //setting text
        layout_text.setText(GeneralFunctions.fromHtml(html_text));
        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(getResources().getString(R.string.title_send_something_tip));
        (mView.findViewById(R.id.text_title)).setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));//primary
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(ContextCompat.getColor(context, R.color.white_all));
        AppCompatButton button = mView.findViewById(R.id.button_ok);
        final android.support.v7.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_text_error.setVisibility(View.GONE);
                alertDialogAndroid.dismiss();
            }
        });

    }

    private ArrayList<String> getVehicles(){
        String vehicles[] = getResources().getStringArray(R.array.vehicles);
        return new ArrayList<String>(Arrays.asList(vehicles));
    }
    private ArrayList<String> getWeigths(){
        //get current vehicle selected
        int resource_asigned = 0;

        String vehicle_selected = text_vehicle.getText().toString();

        if(vehicle_selected.toLowerCase().contains(Constants.bike)){
            resource_asigned = R.array.weigth_bike;
        }else if (vehicle_selected.toLowerCase().contains(Constants.car)){
            resource_asigned = R.array.weigth_car;
        }else if(vehicle_selected.toLowerCase().contains(Constants.moto)){
            resource_asigned = R.array.weigth_moto;
        }else if(vehicle_selected.toLowerCase().contains(Constants.truck)){
            resource_asigned = R.array.weigth_trunk;
        }else{
            return new ArrayList<String>();
        }

        String weigths[] = getResources().getStringArray(resource_asigned);
        return new ArrayList<String>(Arrays.asList(weigths));
    }

    /***********************************************Adapter*****************************************************/
    class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.TimeViewHolder> {
        private final LayoutInflater mInflater;
        private final ArrayList<String> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        private Activity activityContext;
        private EditText text_vehicle;
        private EditText text_weigth;
        private int type_popup;
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

        public TimesAdapter(Activity context, ArrayList<String> models,AlertDialog alert, int type_popup_param, EditText text_vehicle, EditText text_weigth) {
            activityContext = context;
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(context);
            mModels = new ArrayList<>(models);
            type_popup = type_popup_param;
            this.text_vehicle = text_vehicle;
            this.text_weigth = text_weigth;
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type_popup == DetailOrderFragment.VEHICLE) {
                        //si existe hora fin seleccionada, validar si es posterior a la hora inicio,
                        // sino entonces establecer hora final igual a l ahora de inicio mas 30 minutos
                        text_vehicle.setText(model);
                        text_weighing.setText("");
                    } else if(type_popup == DetailOrderFragment.WEIGTH) {

                        text_weighing.setText(model);
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

}
