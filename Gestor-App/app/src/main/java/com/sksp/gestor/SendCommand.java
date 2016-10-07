package com.sksp.gestor;

/**
 * Created by root on 23/9/16.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class SendCommand extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> control = new ArrayList<String>();
    List<String> moniter = new ArrayList<String>();
    List<String> modify = new ArrayList<String>();
    LinearLayout lloverlay,llinput2,llout;
    EditText et2in1,et2in2;
    Button Submit;
    ArrayList<String> selected ;
    ArrayList<String> name = new ArrayList<String>(),reply=new ArrayList<String>();
    ArrayList<ListOutput> CustomAdapter = new ArrayList<ListOutput>();
    ListView lvout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commandsouter);

        lloverlay = (LinearLayout) findViewById(R.id.lloverlay);
        llinput2 = (LinearLayout)findViewById(R.id.ll2input);
        llout = (LinearLayout)findViewById(R.id.lloutput);
        et2in1 = (EditText) findViewById(R.id.et2input1);
        et2in2 = (EditText) findViewById(R.id.et2input2);
        Submit = (Button) findViewById(R.id.btn2submit);
        lvout = (ListView) findViewById(R.id.lvOutput);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView)findViewById(R.id.tvoutHead)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llout.setVisibility(View.GONE);
            }
        });
       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.two_line_list_item, android.R.id.text1, name,reply);
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);


        // preparing list data
        prepareListData();

        Intent i = getIntent();
        selected = i.getStringArrayListExtra("selected");

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /*/ Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        */// Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
               /* Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),
                                    Toast.LENGTH_SHORT)
                        .show(); */
                selectprefix(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                //getInput(1,"sa","As");
                return false;
            }
        });
        expListView.expandGroup(1);
    }
    public void getInput(int mode ,String prefix,String... b){
        lloverlay.setVisibility(View.VISIBLE);
        llinput2.setVisibility(View.VISIBLE);
        et2in1.setHint(b[0]);
        if (mode == 1){
            et2in2.setVisibility(View.GONE);
        }else{
            et2in2.setVisibility(View.VISIBLE);
            et2in2.setHint(b[1]);
        }
        final String cmd = prefix;
        final int m = mode;
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = cmd + et2in1.getText().toString();
                if (m == 2)
                    result += "::" + et2in2.getText().toString();
                //Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
                et2in1.clearComposingText();

                lloverlay.setVisibility(View.GONE);
                llinput2.setVisibility(View.GONE);
                et2in1.setText("");
                et2in2.setText("");
               // Toast.makeText(getBaseContext(),result,Toast.LENGTH_SHORT).show();
                new BackTask().execute(result);
                return;
            }
        });
    }
    private void clearArrayList(){
        CustomAdapter.clear();
        name.clear();
        reply.clear();
    }
    private  void setAdapter(){
        Resources res1 = getResources();
        CustomAdapterOutput adapter = new CustomAdapterOutput(SendCommand.this, CustomAdapter, res1);
        adapter.notifyDataSetChanged();
        lvout.setAdapter(adapter);
    }
    private void setListData() {
        for (int i = 0; i < name.size(); i++) {
            final ListOutput sched = new ListOutput();
            Log.e("value : ",name.get(i));
            sched.setName(name.get(i));
            sched.setReply(reply.get(i));
            // CustomListViewValuesArr.
            CustomAdapter.add(sched);
        }
    }
    public void processjson(String result){
        try {

            JSONObject mainjsonObj = new JSONObject(result);
            //JSONArray jsonar = mainjsonObj.names();
            JSONArray jsonar = mainjsonObj.getJSONArray("Ip");
            JSONArray jsonar1 = mainjsonObj.getJSONArray("Reply");


            // looping through All Contacts
            for (int i = 0; i < jsonar.length(); i++) {
                String s = jsonar.opt(i).toString();
                String s1 = jsonar1.opt(i).toString();
                //JSONObject c = jsonar.getJSONObject(i);
                //JSONObject c1 = jsonar1.getJSONObject(i);
                byte[] data = Base64.decode(s1, Base64.DEFAULT);
                String text = new String(data, "UTF-8");
                name.add(s);
                reply.add(text);

                //name.add(c.getString("ip"));
                //reply.add(c1.getString("reply"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onItemClick(int mPosition)
    {
        // SHOW ALERT
        //Toast.makeText(CustomListView, "" + tempValues.getSysName() + " "+tempValues.getImage() +" Url:"+tempValues.getIp(), Toast.LENGTH_LONG).show();
        View v = lvout.getChildAt(mPosition - lvout.getFirstVisiblePosition());
        llout.setVisibility(View.GONE);
    }
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Control");
        listDataHeader.add("Moniter");
        listDataHeader.add("Modify");

        // Adding child data

        control.add("Shutdown");
        control.add("Restart");
        control.add("Log off");
        control.add("Scan");
        control.add("ChkDsk");
        control.add("Driver Status");

        moniter.add("Netstat");
        moniter.add("Driver Query");
        moniter.add("System Info");
        moniter.add("Label");
        moniter.add("show ipConfiguration");


        modify.add("Enable Firewall");
        modify.add("Disable Firewall");
        modify.add("Set Proxy");
        modify.add("Reset Proxy");
        modify.add("Add User");
        modify.add("Delete User");
        modify.add("Change Password");

        listDataChild.put(listDataHeader.get(0), control); // Header, Child data
        listDataChild.put(listDataHeader.get(1), moniter);
        listDataChild.put(listDataHeader.get(2), modify);
    }
    public void selectprefix(String name){
        String cmd="";
        if (name.equals(control.get(0))){//shutdown
            cmd = "N1::";
            sendtoBT(cmd);
            return;
        }if (name.equals(control.get(1))){//restart
            cmd = "N3::";
            sendtoBT(cmd);
            return;
        }if (name.equals(control.get(2))){//logoff
            cmd = "N4::";
            sendtoBT(cmd);
            return;
        }if (name.equals(control.get(3))){//scan
            cmd = "Y11::";
            sendtoBT(cmd);
            return;
        }if (name.equals(control.get(4))){//checkdisk
            cmd = "Y9::";
            sendtoBT(cmd);
            return;
        }if (name.equals(control.get(5))){//driverstatus
            cmd = "Y6::";
            sendtoBT(cmd);
            return;
        }

        if (name.equals(moniter.get(0))){//netstat
            cmd = "Y8::";
            sendtoBT(cmd);
            return;
        }if (name.equals(moniter.get(1))){//driver query
            cmd = "Y12::";
            sendtoBT(cmd);
            return;
        }if (name.equals(moniter.get(2))){//system info
            cmd = "Y13::";
            sendtoBT(cmd);
            return;
        }if (name.equals(moniter.get(3))){//label
            cmd = "Y14::";
            sendtoBT(cmd);
            return;
        }if (name.equals(moniter.get(4))){//ipconfig
            cmd = "Y7::";
            sendtoBT(cmd);
            return;
        }

        if (name.equals(modify.get(0))){//Fenable
            cmd = "Y2::";
            sendtoBT(cmd);
            return;
        }if (name.equals(modify.get(1))){//Fdisable
            cmd = "Y3::";
            sendtoBT(cmd);
            return;
        }if (name.equals(modify.get(2))){//set Proxy
            cmd = "Y0::"; //get 1 data
            getInput(1,cmd,new String[]{"Proxy Server Ip"});
            return;
        }if (name.equals(modify.get(3))){//disable Proxy
            cmd = "Y1::";
            sendtoBT(cmd);
            return;
        }if (name.equals(modify.get(4))){//add user
            cmd = "Y4::"; //get 2 username and password
            getInput(2,cmd,new String[]{"Username","Password"});
            return;
        }if (name.equals(modify.get(5))){//delete user
            cmd = "Y5::"; // delete user
            getInput(1,cmd,new String[]{"Username"});
            return;
        }if (name.equals(modify.get(6))){//change password
            cmd = "Y16::";//get 2 input
            getInput(2,cmd,new String[]{"Username", "New Password"});
            return;
        }
    }
    private void sendtoBT(String cmd){
        new BackTask().execute(cmd);
    }
    private void handleOutput(String s){

    }
    private class BackTask extends AsyncTask<String, String, String> {

        private String result = null;

        @Override
        protected String doInBackground(String... params) {


            try {
                JSONArray ja = new JSONArray(selected);
                JSONObject obj = new JSONObject();
                obj.put("ip",ja);
                obj.put("cmd",params[0]);

                String data1 = obj.toString();

                String data =  URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(data1, "UTF-8");

                Log.d("data",data);

                BufferedReader reader = null;
                try {
                    URL url = new URL("http://10.42.0.1/sendCmd/");
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
                    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                    writer.write(data);
                    writer.flush();
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

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
           // Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
            clearArrayList();
            processjson(result);
            LinearLayout l = (LinearLayout) findViewById(R.id.lloutput);
            l.setVisibility(View.VISIBLE);
            setListData();
            setAdapter();

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