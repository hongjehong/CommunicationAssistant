package com.google.cloud.android.speech;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommWithAdams  extends AppCompatActivity {



    String key;
    String url_string;

    int res;


    CommWithAdams(){
        key = "652455078463633905";
        url_string = "http://api.adams.ai/datamixiApi/omAnalysis";

    }


    String SendEmotion(String input_sentence){

        String res = null;
        try {
            res = new RestAPITask(url_string, key, input_sentence).execute().get();
        }catch(Exception e){
            e.printStackTrace();
        }

        return res;
    }



    // Rest API calling task
    public class RestAPITask extends AsyncTask<String, Void, String> {
        // Variable to store url
        protected String mURL;
        protected String key;
        protected String query;

        // Constructor
        public RestAPITask(String url, String t_key, String t_query) {
            mURL = url;
            key = t_key;
            query = t_query;
        }

        // Background work
        @Override
        protected String doInBackground(String... params) {
            String rere = null;
            try {

                mURL = mURL + "?key=" + key + "&query=" + query + "&type=1";

                Log.d("ull", mURL);
                URL url = new URL(mURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStream is = conn.getInputStream();

                // Get the stream
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                Log.d("ullasdasd", "hihi");
                // Set the result

                JSONObject json = null;



                json = new JSONObject(builder.toString());

                rere = ((((JSONArray)((JSONObject)json.get("return_object")).get("Result")).get(0))).toString();


                Log.e("REST_API", rere);


            }
            catch (Exception e) {
                // Error calling the rest api
                Log.e("REST_API", "GET method failed: " + e.getMessage());
                e.printStackTrace();
            }
            return rere;
        }
    }

}
