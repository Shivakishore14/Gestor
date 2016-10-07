package com.sksp.gestor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Login extends AppCompatActivity {
    Button login;
    EditText uname, pass;
    TextView notification;
    String username, password;
    Boolean a;
    ProgressDialog progress ;
    CookieManager cookieManager = new CookieManager();
    SharedPreferences pref ,sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieHandler.setDefault(cookieManager);
        pref = getSharedPreferences("session", MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.login);
        uname = (EditText)findViewById(R.id.etuname);
        pass = (EditText)findViewById(R.id.etpass);
        login = (Button)findViewById(R.id.btlogin);
        notification = (TextView) findViewById(R.id.tvLoginNotification);
        sp  = getApplicationContext().getSharedPreferences("settingsInfo", Context.MODE_PRIVATE);
        progress = new ProgressDialog(this);
        progress.setMessage("Loging in...");

// To dismiss the dialog


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor("#00796B"));
        }
        if (sp.getString("username",null) == null){
           // Intent i =new Intent(Login.this, Settings.class);
            //startActivity(i);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = uname.getText().toString();
                password = pass.getText().toString();
                String suname = sp.getString("username",null);
                String spass = sp.getString("password",null);
                if (username.length() != 0 ) {
                   // if (suname.equals(username)&&spass.equals(password)) {
                        //Intent i = new Intent(Login.this, Settings.class);
                        //startActivity(i);
                    //} else {
                        try {
                            BackTask st = new BackTask();
                            st.execute();
                            notification.setText("Logging in as :" + username);
                        } catch (Exception e) {
                            e.printStackTrace();
                      //  }
                    }
                }else{
                    notification.setText("Enter valid username");
                }

            }
        });
    }

    private class BackTask extends AsyncTask<String, String, String> {

        private String result = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                String data = URLEncoder.encode("username", "UTF-8") + "=" +
                        URLEncoder.encode(username, "UTF-8") +"&"+ URLEncoder.encode("password", "UTF-8") + "=" +
                        URLEncoder.encode(password, "UTF-8");
                BufferedReader reader = null;
                try {
                        URL url = new URL("http://10.42.0.1/login/");
                    URLConnection con = url.openConnection();
                    con.setDoOutput(true);

                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(data);
                    writer.flush();
                   /* con.setRequestProperty();
                    //
                     final String COOKIES_HEADER = "Set-Cookie";
                     java.net.CookieManager msCookieManager = new java.net.CookieManager();

                    Map<String, List<String>> headerFields = con.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                    String t= "";
                    if(cookiesHeader != null)
                    {
                        for (String cookie : cookiesHeader)
                        {
                            t = t + cookie + "::";
                            msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
                        }
                    }
                    Log.e("cookie",t);
                    editor.putString("cookie",t);
                    editor.commit();
                   //
                    //load back the
                    if(msCookieManager.getCookieStore().getCookies().size() > 0)
                    {
                        //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                        //con.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    }
                    //load the
                    *///getting response back
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder s = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        s.append(line + "\n");
                    }
                    result = s.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
            progress.dismiss();
            //notification.setText(result);

            if(result!=null){
                //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                if (!result.equals("Not loggedin"));
                    Intent i = new Intent(Login.this,ShowSys.class);
                // i.putExtra("json",result);
                 startActivity(i);
            }
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // progress. For example updating ProgessDialog
        }
    }

}

