package com.example.marwa.launcher002.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.Singleton.ConnectivityHelper;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkAvService extends Service {
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
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                //getNetworkState();
               if(isOnline() ) {
                  // UpdateDB("1");
                   Log.v("''''''''''","__________________ internet connecting");
               }else{
                 //  UpdateDB("0");
                   Log.v("''''''''''","%%%%%%%%%%%%%%%%%%%%%%%%% no interneeeeeeeeeeet");
               }


            }
        }, 0, 10000);//5 Minutes
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }



    public static boolean isNetworkOnline3() {
        boolean isOnline = false;
        try {
            URL url = new URL("https://www.google.com"); // or your server address
            // URL url = new URL("http://www.baidu.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Connection", "close");
            conn.setConnectTimeout(3000);
            isOnline = conn.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOnline;
    }
    public static boolean isNetworkOnline4() {
        boolean isOnline = false;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            // socket.connect(new InetSocketAddress("114.114.114.114", 53), 3000);
            isOnline = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isOnline;
    }
    public static boolean isNetworkOnline2() {
        boolean isOnline = false;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("ping -c 1 8.8.8.8");
            int waitFor = p.waitFor();
            isOnline = waitFor == 0;    // only when the waitFor value is zero, the network is online indeed

            // BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            // String str;
            // while ((str = br.readLine()) != null) {
            //     System.out.println(str);     // you can get the ping detail info from Process.getInputStream()
            // }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isOnline;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
   public boolean hello(){
        boolean isOnline = false;
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;

        for (Network network : connMgr.getAllNetworks()) {
            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }

        }
        if(isWifiConn){
            if(isNetworkOnline4()) {

                isOnline = isNetworkOnline4();
            }

        }
        return isOnline;
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public void UpdateDB(final String status){





        try {
            Log.d("onrecieve", "on recieveeeeeeeeee");
            final String   URL = "http://"+ WSadressIP.WSIP+"/launcher/MUpdateNetwork.php";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("success")) {
                        //
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("havenetwork", status);
                    params.put("_id_target", "3");
                    return params;
                }
            };
            Volley.newRequestQueue(NetworkAvService.this).add(stringRequest);

            Log.d("mytag", "OK IM HERE EVERYONE"); // THIS WORKS


        } catch (Exception e){
            e.printStackTrace();
        }

    }


}
