package com.example.danie.geolocalizacion;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import java.util.Locale;

public class ServicioGeocoder extends IntentService {

    protected ResultReceiver receiver;

    public final class Constant {
        public static final int SUCCES_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    }

    public ServicioGeocoder(){
        super("SercicioGeocoder");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        //Log.v(TAG, Thread.currentThread().getName());
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if(intent == null) {
            return;
        }
        String errorMessage = "";

        Location location = intent.getParcelableExtra(Constant.LOCATION_DATA_EXTRA);
        receiver = intent.getParcelableExtra(Constant.RECEIVER);

        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getAltitude(),
                    10);
        } catch (IOException ioException) {
            errorMessage = "servicio no disponible";

        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "geolocalización no válida";

        }

        if(addresses == null || addresses.size() == 0){
            if (errorMessage.isEmpty()){
                errorMessage = "no hay dirección";

            }
            deliverResultToReceiver(Constant.FAILURE_RESULT, errorMessage);
        } else{
            Address address = addresses.get(0);
            String resultado = "";
            for (Address address1: addresses){
                resultado = "";
                for (int i = 0; i <= address1.getMaxAddressLineIndex(); i++){
                    resultado+="\n" + address1.getAddressLine(i);
                }

            }
            resultado = "";
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                resultado += "\n" + address.getAddressLine(i);
            }

            deliverResultToReceiver(Constant.SUCCES_RESULT, resultado);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constant.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }
}
