package com.example.marwa.launcher002;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.marwa.launcher002.services.MyTestService;
import com.example.marwa.launcher002.utils.WSadressIP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBrowser extends AppCompatActivity {
  EditText txtUrl;
    ImageView GoToUrl;
    WebView MyWebView;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(View.STATUS_BAR_HIDDEN);

        setContentView(R.layout.activity_my_browser);
        txtUrl = (EditText) findViewById(R.id.editText);
        GoToUrl = (ImageView) findViewById(R.id.imageView4);
        MyWebView = (WebView) findViewById(R.id.webView1);
        MyWebView.setWebViewClient(new WebViewClient());

       GoToUrl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!txtUrl.getText().equals("")){
                            Log.v("lllllllllllllll",txtUrl.getText().toString());
                            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);



                            Pattern p = Pattern.compile(URL_REGEX);
                            Matcher m = p.matcher(txtUrl.getText().toString());//replace with string to compare
                            if(m.find()) {
                                System.out.println("String contains URL");
                                CheckSafetyDB(txtUrl.getText().toString());

                            }else{
                                System.out.println("no result found");
                            }

                        }
                    }
                }
        );
    }
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

    private static final String URL_Activities = "http://"+ WSadressIP.WSIP+"/launcher/MgetWebSafety.php";
     //final String   URL_Activities =  "http://"+ WSadressIP.WSIPChoko+"/kidslanch_serv/web/index.php/blockedwebs";
    final boolean[] isSafe = {false};
    private void CheckSafetyDB(final String typedWeb) {

        final Integer[] statee = new Integer[1];

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Activities,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray array = new JSONArray(response);


                        for (int i = 0; i < array.length(); i++) {


                                JSONObject product = array.getJSONObject(i);
                                statee[0] = product.getInt("count(*)");
System.out.println("oooooooooooooo  "+statee[0]);

                         }

                            if(statee[0] >= 1){

                                Toast.makeText(MyBrowser.this,"Forbidden Access",Toast.LENGTH_SHORT).show();
                            }else if(statee[0] == 0){

                                MyWebView.getSettings().setJavaScriptEnabled(true);
                                MyWebView.loadUrl("https://"+txtUrl.getText().toString());
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
                params.put("weblink", typedWeb);
                params.put("target", "3");
                return params;
            }
        };

        //adding our stringrequest to queue
        Volley.newRequestQueue(MyBrowser.this).add(stringRequest);

    }

}
