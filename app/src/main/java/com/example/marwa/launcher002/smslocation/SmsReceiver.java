package com.example.marwa.launcher002.smslocation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;



import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {

    protected LocationListener locationListener;
    protected LocationManager locationManager;
    protected Context context;

    private static Location currentlocation;

    public static Criteria criteria;



    private final static String TAG = SmsReceiver.class.getSimpleName();

    public SmsReceiver() {

    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String keyword = PreferenceManager.getDefaultSharedPreferences(context).getString("keyword", "where are you ?");

        if (keyword.length() == 0) {
            //Log.d(TAG, "No keyword available. Exit");
            return;
        }

        else
        {
            Log.v(TAG, "i'm here");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                String provider = locationManager.getBestProvider(criteria, true);
                // Cant get a hold of provider
                if (provider == null) {
                    Log.v(TAG, "Provider is null");
                    // showNoProvider();
                    return;
                } else {
                    Log.v(TAG, "Provider: " + provider);
                }
            }

            LocationListener locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    String latitude = Double.toString(location.getLatitude());
                    String longitude = Double.toString(location.getLongitude());
                    ArrayList<SmsMessage> list = null;
                    try {
                        list = getMessagesWithKeyword(keyword, intent.getExtras());


                        Log.v(TAG, "IN ON LOCATION CHANGE");



                        locationManager.removeUpdates(this);
                        Log.v(TAG, "IN ON LOCATION CHANGE, lat=" + latitude + ", lon=" + longitude);

                        SmsManager smsManager = SmsManager.getDefault();
                        String smsBody = "";
                        smsBody = smsBody + "http://maps.google.com?q=";
                        smsBody= smsBody +latitude;
                        smsBody= smsBody +",";
                        smsBody= smsBody +longitude;
                        Log.v("IN ON LOCATION CHANGE", "testt22222");
                        smsManager.sendTextMessage(list.get(0).getOriginatingAddress(), null, smsBody, null, null);
                        Log.v("IN ON LOCATION CHANGE", "testt"+smsBody);
                    } catch (Exception e) {
                        return;
                    }

                    if (list.size() == 0) {
                        //Log.d(TAG, "No message available. Exit");
                        return;
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }


            };

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);





        }

        if (!Permissions.haveSendSMSAndLocationPermission(context)) {
            try {
                Permissions.setPermissionNotification(context);
            } catch (Exception e) {
                Toast.makeText(context,"SMS and Location permission needed", Toast.LENGTH_SHORT).show();
            }

            return;
        }





    }

    private ArrayList<SmsMessage> getMessagesWithKeyword(String keyword, Bundle bundle) {
        ArrayList<SmsMessage> list = new ArrayList<SmsMessage>();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage sms = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }

                if (sms.getMessageBody().toString().equals(keyword)) {
                    Log.v("alooooooooooooooooooo" ,keyword);
                    //  Log.v("originatingadress" ,   sms.getOriginatingAddress());


                    list.add(sms);
                }

            }
        }
        return list;
    }




}




