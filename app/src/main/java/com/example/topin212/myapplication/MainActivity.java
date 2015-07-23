package com.example.topin212.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
//import android.preference.PreferenceFragment;
//import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    public TextView status;
    private TextView buffSize;
    private EditText changeBuff;

    private URL uRL;
    private URL FIle;

    public String testLogin = "root";
    //public String testPassword = "zaqwsx";
    private SharedPreferences appPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView)findViewById(R.id.statusView);
        buffSize = (TextView)findViewById(R.id.buffSizeText);
        changeBuff = (EditText)findViewById(R.id.editBuffSize);



        appPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        GetTHeValue();
      //  PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        switch(item.getItemId()) {
            case R.id.action_Log_in:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

            //noinspection SimplifiableIfStatement
        }
    }

    public void GetTHeValue(){
        String KEY_VALUE = "example_txt_1";
        try{
            testLogin = (appPreferences.getString(KEY_VALUE, "Default"));
            status.setText(testLogin);
        }catch(NullPointerException noe){
            noe.printStackTrace();
        }
        status.setText(testLogin);
        String tempString = "https://mac-accounts-prokopenko.c9.io/BUFFEROVEC/bf.php?&login="+testLogin+"&lk="+"&loginfo="+0;
        try{
            FIle = new URL(tempString);
        }catch(MalformedURLException moe)
        {
            moe.printStackTrace();
        }
        if(!buffSize.getText().toString().equals("Sync to get the buff")) {
            new UpdateValues().execute(FIle);
            status.setText("Wait for the value to refresh!");
        }
    }


    public void buttonGetBuffSize(View view) throws IOException, InterruptedException {
        //Read the preferences to get the login and encrypt in base 64
        GetTHeValue();
    }

    public void buttonPlus(View view) throws IOException {
        double source = Double.parseDouble(buffSize.getText().toString()), add = Double.parseDouble(changeBuff.getText().toString());
        double result = source+add;
        changeBuff.setText(""+result);
        buttonSubmitChanges();
    }
    public void buttonMinus(View view) throws IOException {
        double source = Double.parseDouble(buffSize.getText().toString()), add = Double.parseDouble(changeBuff.getText().toString());
        double result = source-add;
        changeBuff.setText(""+result);
        buttonSubmitChanges();
    }


    public void buttonSubmitChanges() throws IOException
    {
        String tempStr = changeBuff.getText().toString();
        //int tempInt = Integer.parseInt()
        if(!tempStr.equals("")) {
            buffSize.setText(tempStr);
            //status.setText("Submitting changes...");
            try {
                //uRL = new URL("http://veryvery.esy.es/bf/rr.php?key=" + tempStr);
                uRL = new URL("https://mac-accounts-prokopenko.c9.io/BUFFEROVEC/bf.php?key="+tempStr);
                Log.d("URL:", uRL.toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection httpURLConnection = (HttpURLConnection) uRL.openConnection();


                            try {
                                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                                readStream(in);
                            } finally {
                                httpURLConnection.disconnect();
                            }


                        } catch (MalformedURLException mlu) {
                            Log.d("MFUE", "this is bad :(");
                            mlu.printStackTrace();
                        } catch (IOException ioe) {
                            Log.d("IOE", "Nothing wrong here.");
                            ioe.printStackTrace();
                        }
                    }

                    private String readStream(InputStream is) {
                        try {
                            ByteArrayOutputStream bo = new ByteArrayOutputStream();
                            int i = is.read();
                            while (i != -1) {
                                bo.write(i);
                                i = is.read();
                            }
                            return bo.toString();
                        } catch (IOException e) {
                            return "";
                        }
                    }
                }).start();


            } catch (IOException ioe){
                ioe.printStackTrace();
            }

        }
    }


    class UpdateValues extends AsyncTask<URL, Void, String> {

        protected void onPreExecute(){
            status.setText("Getting file conents...");
        }
        //method for connecting us to the server with formed URL already
        @Override
        protected String doInBackground(URL... params) {
            String resultString = "";
            try {
                //FIle = new URL("https://mac-accounts-prokopenko.c9.io/BUFFEROVEC/bf.php?&login=petro&lk=VSTAVISH SAM&loginfo=0");
                BufferedReader br = new BufferedReader(new InputStreamReader(FIle.openStream()));
                String temporary;
                while ((temporary = br.readLine()) != null) {
                    resultString += temporary;
                }
            } catch (MalformedURLException moe) {
                Log.d("During get", "Moe caught");
                moe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return resultString;
        }

        @Override
        public void onPostExecute(String result)
        {
            status.setText("Done.");
            buffSize.setText(result);
        }
    }
}