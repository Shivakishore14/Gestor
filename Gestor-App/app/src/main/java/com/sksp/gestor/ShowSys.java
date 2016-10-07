package com.sksp.gestor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by root on 23/9/16.
 */
public class ShowSys extends AppCompatActivity {

    ListView list;
    CustomAdapter adapter;
    public  ShowSys CustomListView = null;
    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();
    static ArrayList<String> ip  = new ArrayList<String>(),name  = new ArrayList<String>(),selected = new ArrayList<String>();
    static ArrayList<Boolean> flag  = new ArrayList<Boolean>();
    SharedPreferences pref ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.showsys);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CustomListView = this;

        new BackTask().execute();
        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
       // setListData();

        Resources res =getResources();
        list= ( ListView )findViewById( R.id.listView );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        //adapter=new CustomAdapter( CustomListView, CustomListViewValuesArr,res );
        //list.setAdapter( adapter );
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabToCmd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected.size() == 0) {
                    Snackbar.make(view, "Select a system", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    return;
                }
                Intent i =  new Intent(getApplicationContext(),SendCommand.class);
                i.putStringArrayListExtra("selected",selected);
                startActivity(i);
            }
        });
    }


    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        ListModel tempValues = ( ListModel ) CustomListViewValuesArr.get(mPosition);
        // SHOW ALERT
        //Toast.makeText(CustomListView, "" + tempValues.getSysName() + " "+tempValues.getImage() +" Url:"+tempValues.getIp(), Toast.LENGTH_LONG).show();
        View v = list.getChildAt(mPosition - list.getFirstVisiblePosition());
        if (!flag.get(mPosition)) {
            selected.add(tempValues.getIp());
            updateView(mPosition,1);
        }else {
            selected.remove(tempValues.getIp());
            updateView(mPosition,0);
        }
    }
    public void updateView(int index, int set){
        flag.set(index, !flag.get(index));
       // Toast.makeText(CustomListView, String.valueOf(set), Toast.LENGTH_LONG).show();
        View v = list.getChildAt(index - list.getFirstVisiblePosition());
        if (v == null)
            return;
        TableLayout tl =(TableLayout) v.findViewById(R.id.tabLayout);

        int c = Color.parseColor("#778899");
        if (set ==1 )
            tl.setBackgroundColor(c);
        else
            tl.setBackgroundColor(0xFFFFFFFF);

    }

    private void setListData() {

        CustomListViewValuesArr.clear();
        for (int i = 0; i < name.size(); i++) {
            final ListModel sched = new ListModel();
            Log.e("value : ",name.get(i));
            sched.setSysName(name.get(i));
            sched.setImage("image" + i);
            sched.setIp(ip.get(i));
            // CustomListViewValuesArr.
            CustomListViewValuesArr.add(sched);
        }
    }
    public void processjson(String result){
        try {

            JSONObject mainjsonObj = new JSONObject(result);
            //JSONArray jsonar = mainjsonObj.names();
            JSONArray jsonar = mainjsonObj.getJSONArray("array");
            ip.clear();
            name.clear();
            selected.clear();
            flag.clear();
            // looping through All Contacts
            for (int i = 0; i < jsonar.length(); i++) {
                JSONObject c = jsonar.getJSONObject(i);
                ip.add(c.getString("ip"));
                name.add(c.getString("name"));
                flag.add(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class BackTask extends AsyncTask<String, String, String> {

        private String result = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://10.42.0.1/getSysMob/");
                    URLConnection con = url.openConnection();
                    con.setDoOutput(true);
                    //////

                    /*  msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    if(msCookieManager.getCookieStore().getCookies().size() > 0)
                    {
                        Log.e("eafdasfadsf","working");
                        //While joining the Cookies, use ',' or ';' as needed. Most of the server are using ';'
                        con.setRequestProperty("Cookie", TextUtils.join(";", msCookieManager.getCookieStore().getCookies()));
                    }

                    ///////
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(data);
                    writer.flush();
                   *///getting response back
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder s = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        s.append(line + "\n");
                    }
                    result = s.toString();
                    Log.e("info", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.d("Response BackTask : ", "> " + result);

            if (result != null) {
                processjson(result);
            } else {
                Log.e("json", "not available");
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(),result,Toast.LENGTH_SHORT).show();
            CustomListViewValuesArr.clear();

            setListData();
            Resources res1 = getResources();
            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res1);
            adapter.notifyDataSetChanged();
            list.setAdapter(adapter);
            //afterimport();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // progress. For example updating ProgessDialog
        }
    }
}
