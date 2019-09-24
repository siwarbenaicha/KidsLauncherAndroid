package com.example.marwa.launcher002;


import android.Manifest;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.graphics.PixelFormat;

import android.graphics.drawable.Drawable;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.view.Gravity;
import android.view.KeyEvent;



import android.view.Window;
import android.view.WindowManager;



import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.Singleton.ConnectivityHelper;
import com.example.marwa.launcher002.model.AppDetail;
import com.example.marwa.launcher002.services.CallsHistory;
import com.example.marwa.launcher002.services.ContactSync;
import com.example.marwa.launcher002.services.KeppWifiStatus;
import com.example.marwa.launcher002.services.MyTestService;

import com.example.marwa.launcher002.services.NetworkAvService;
import com.example.marwa.launcher002.services.SmsHistory;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Window window;

    private WindowManager wmanager;

    TextView d;

    /////////////////////////////
    private Button lock, disable, enable;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    /////////////////////////////Marwa Week3
    //private static final String URL_ActivitiesB = "http://"+ WSadressIP.WSIP+"/launcher/MgetInstalledApps.php";
   private static final String URL_ActivitiesB = "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/apps/getapps";
    private List<String> appsWS ;

    private PackageManager manager;
    private List<AppDetail> apps;
    ////////////////////////////// Siwar W3
    private static final int REQUEST_LOCATION = 1;
    Button button;
    TextView textView;
    LocationManager locationManager;
    String lattitude,longitude;
    ///////////////////////// Marwa W5
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(View.STATUS_BAR_HIDDEN);



        //////////////////////////////////////////////
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////// DISABLE SOFT KEYS
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();

        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        localLayoutParams.gravity = Gravity.BOTTOM;

        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);

        localLayoutParams.format = PixelFormat.TRANSPARENT;



        //Disable Title bar and Notification Permanently

        wmanager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParamsTop = new WindowManager.LayoutParams();

        localLayoutParamsTop.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        localLayoutParamsTop.gravity = Gravity.TOP;

        localLayoutParamsTop.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // enable the notification to receive touch events

                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar

                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

                // set the size of statusbar blocker

        localLayoutParamsTop.width = WindowManager.LayoutParams.MATCH_PARENT;

        localLayoutParamsTop.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);

        localLayoutParamsTop.format = PixelFormat.TRANSPARENT;


       ///////////////////////////////////////////// SET DAY : design section
        d=findViewById(R.id.date);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        d.setText("Today,"+formattedDate);
        /////////////////////////////////////////// ENABLE ADMIN SETTINGS:in case of future problem put this function in button and hide it when is enabeled
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        startActivityForResult(intent, RESULT_ENABLE);
        //////////////////////////////////////// BUTTON: extra

        lock = (Button) findViewById(R.id.lock);

         lock.setOnClickListener(
             new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                    Intent i = new Intent(MainActivity.this, MyBrowser.class);
                    startActivity(i);
/*if(haveNetworkConnection()){
    Log.v("''''''''''","__________________ internet connecting");
}*/
                   /*  if (ConnectivityHelper.isConnectedToNetwork(getApplicationContext())) {
                         Log.v("''''''''''","__________________ internet connecting");
                     } else {
                         Log.v("''''''''''","%%%%%%%%%%%%%%%%%%%%%%%%% no interneeeeeeeeeeet");
                     }*/

                 }
              }
         );
        //_________________________ webservice running in background ———————————————\\
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(10000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                               /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    buildAlertMessageNoGps();

                                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    getLocation();
                                    UpdateLocationDB();
                                }
*/
                                listOfInstalledApps(); // webservice running in background
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
        ////////////////////////////////Siwar W3 //////////////////////////////////

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);



        /////////////////////////////////// Calling Services  //////////////////////////////////

        //_________________________________ Contacts updates _________________________________\\
        Intent service = new Intent(this, ContactSync.class);
        startService(service);
        //_________________________________ lock screen _________________________________\\
        Intent servicex = new Intent(this, MyTestService.class);
        startService(servicex);
        //_________________________________ Calls Log _________________________________\\
        Intent background = new Intent(this, CallsHistory.class);
        startService(background);
        //_________________________________ SMS Log _________________________________\\
       Intent ServiceSms = new Intent(this, SmsHistory.class);
        startService(ServiceSms);
        //_________________________________ open/close wifi _________________________________\\

        Intent ServiceWifi = new Intent(this, KeppWifiStatus.class);
        startService(ServiceWifi);
        //_________________________________ Network Status _________________________________\\

        Intent NetworkService = new Intent(this, NetworkAvService.class);
       startService(NetworkService);

        ///////////////////////////////////////////////////////////// Thread Location
        Thread t2 = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(150000);  //1000ms = 1 sec
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    buildAlertMessageNoGps();

                                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    getLocation();
                                    UpdateLocationDB();
                                }


                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t2.start();


    }



    ///////// LAUNCHER SETTINGS: disable les buttons lkol + kid can't leave this mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    private final List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }
    ///////// LAUNCHER SETTINGS: END



    //Marwa Week 3 (apps install/uninstall)



    ///////////////////////////////////////////////////////////////My Web Service to install / Uninstall Apps

    private void listOfInstalledApps() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        appsWS = new ArrayList<String>(); // very important !
     //StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ActivitiesB,
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ActivitiesB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);

                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject product = array.getJSONObject(i);

                                //adding the product to product list
                                appsWS.add(


                                      //  product.getString("package")
                                        product.getString("packg")
                                );
                                // Log.v("boucle Three", "hhhhhhhhhhhhhhhhhhhh"+product.getString("packg"));
                            }


                            //all other functions must to be houniii mouch outside
                            loadApps();
                            loadListView();
                            addClickListener();




                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
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

                //without params
                return params;
            }
        };;


        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }
    ///////////////////////////// to get apps (name, packge w icon) and set them in ArrayList "apps"

    private void loadApps() throws PackageManager.NameNotFoundException {
        manager = getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);





        for (String x : appsWS) {
            Drawable icon = getPackageManager().getApplicationIcon(x);

            final PackageManager pm = getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo( x, 0);
            } catch (final PackageManager.NameNotFoundException e) {
                ai = null;
            }
            final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

            AppDetail app = new AppDetail();
            app.setLabel(applicationName);
            app.setName(x);
            app.setIcon(icon);
            apps.add(app);

        }

    }


    ///////////////////////////// SHOW ListView
    private ListView list;
    private void loadListView(){
        list = (ListView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(this,
                R.layout.list_item,
                apps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(apps.get(position).getIcon());

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(apps.get(position).getLabel());

                TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
                appName.setText(apps.get(position).getName());

                return convertView;
            }
        };

        list.setAdapter(adapter);
    }


    // Click on ListView Item event : open the app!
    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Intent i = manager.getLaunchIntentForPackage(apps.get(pos).getName().toString());
                MainActivity.this.startActivity(i);
            }
        });
    }

/////////////////////////////////////////// Siwar W3


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {


            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latti = location.getLatitude();
                    double longi = location.getLongitude();
                    lattitude = String.valueOf(latti);
                    longitude = String.valueOf(longi);



                    Log.v(TAG, "IN ON LOCATION CHANGE, lat=" + lattitude + ", lon=" + longitude);
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



            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void  UpdateLocationDB(){


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

         // final String   URL = "http://"+ WSadressIP.WSIP+"/launcher/gps.php";



            final String   URL = "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/targets/3";

         //   StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
             StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.contains("success")) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"failed to add",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("Content-Type","application/x-www-form-urlencoded");

                    //params.put("id_target", "3");
                     params.put("latt",lattitude );


                   params.put("lng",longitude);



                    return params;
                }
            };

          queue.add(stringRequest);
    }

////////////////////////// Marwa and Siwar W6
private boolean haveNetworkConnection() {
    boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;

    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
    for (NetworkInfo ni : netInfo) {
        if ("WIFI".equals(ni.getTypeName()))
            if (ni.isConnected())
                haveConnectedWifi = true;
        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            if (ni.isConnected())
                haveConnectedMobile = true;
    }
    return haveConnectedWifi || haveConnectedMobile;
}


}


