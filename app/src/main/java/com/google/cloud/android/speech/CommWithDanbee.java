package com.google.cloud.android.speech;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommWithDanbee extends AppCompatActivity {


    String chatbot_id;
    String url;

    String res;


    CommWithDanbee(){
        chatbot_id = "da4d0be1-b109-498f-abf5-fb17d94edc93";
        url = "https://danbee.ai/chatflow/engine.do";

    }


    String SendSpeech(String input_sentence){

        JSONObject input = new JSONObject();

        String resres = null;

        try {
            input.put("chatbot_id", chatbot_id);
            input.put("input_sentence", input_sentence);
        }
        catch(Exception e){
            Log.d("debugger", "bye");
        }
        if (input.length() > 0) {
            try {
                resres = new CommWithDanbee.SendJsonDataToServer().execute(String.valueOf(input)).get();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return resres;
    }



    class SendJsonDataToServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL("https://danbee.ai/chatflow/engine.do");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
// json data
                writer.close();
                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }



                JsonResponse = buffer.toString();

                JSONObject json = null;
                try {
                    json = new JSONObject(JsonResponse);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
//response data
                String mes = null;
                try {
                    Log.d("debugger", json.toString());
                    mes = ((JSONObject) ((JSONArray) ((JSONObject) ((JSONObject) json.get("responseSet")).get("result")).get("result")).get(0)).get("message").toString();
                }catch (Exception e) {
                    e.printStackTrace();
                }

                return mes;

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("taggggggg", "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            return;
        }

    }
}
