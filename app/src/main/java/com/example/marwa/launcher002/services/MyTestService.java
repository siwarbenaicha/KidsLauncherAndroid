package com.example.marwa.launcher002.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.MainActivity;
import com.example.marwa.launcher002.model.SmsData;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MyTestService extends Service {

    private Timer timer = new Timer();


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getScreenState();   //Your code here
            }
        }, 0, 10000);//5 Minutes
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private static final String URL_Activities = "http://"+ WSadressIP.WSIP+"/launcher/MgetScreenState.php";

    private void getScreenState() {

        final Integer[] statee = new Integer[1];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Activities,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);


                            for (int i = 0; i < array.length(); i++) {


                                JSONObject product = array.getJSONObject(i);
                                statee[0] = product.getInt("state");


                            }

                            if(statee[0] == 1){

                                DevicePolicyManager deviceManger = (DevicePolicyManager)getSystemService( Context.DEVICE_POLICY_SERVICE);
                                deviceManger.lockNow();
                            }else if(statee[0] == 0){

                                unlockScreen(); //this function is empty
                            }




                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");

                return params;
            }
        };;

        //adding our stringrequest to queue
        Volley.newRequestQueue(MyTestService.this).add(stringRequest);
    }

    private void unlockScreen() {

    }






}
