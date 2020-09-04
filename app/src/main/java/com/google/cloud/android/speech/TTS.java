package com.google.cloud.android.speech;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class TTS {

    /*
    호출 전에 onCreate 함수 내에 넣어야 할 코드
        final Context context = this;
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
     */
    /*
    파라미터는 다음과 같다.
    input : 음성 합성할 텍스트 String
    context : 다음 변수를 정의한 뒤 파라미터로 넘길 것
        final Context context = this; (import android.content.Context; 필요)
    voice 값에 따른 음성 정보
    1 : 한국어, 여성 음색
    2 : 한국어, 남성 음색
    3 : 영어, 여성 음색
    4 : 영어, 남성 음색
    5 : 일본어, 남성 음색
    6 : 중국어, 여성 음색
    7 : 중국어, 남성 음색
    8 : 스페인어, 남성 음색
    9 : 스페인어, 여성 음색
    */
    public static void speechSynthesis(String input, Context context, int voice){

        String clientId = "8dunys4x9m";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "6Kgy4EImHbEgIcXaeAGW5pwTWvsu5sZX68gEi54p";//애플리케이션 클라이언트 시크릿값";
        try {
            String text = URLEncoder.encode(input, "UTF-8"); // 13자
            String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            // post request
            String voiceInfo = "";
            switch(voice){
                case 1: voiceInfo = "speaker=mijin&speed=0&text=";
                    break;
                case 2: voiceInfo = "speaker=jinho&speed=0&text=";
                    break;
                case 3: voiceInfo = "speaker=clara&speed=0&text=";
                    break;
                case 4: voiceInfo = "speaker=matt&speed=0&text=";
                    break;
                case 5: voiceInfo = "speaker=shinji&speed=0&text=";
                    break;
                case 6: voiceInfo = "speaker=meimei&speed=0&text=";
                    break;
                case 7: voiceInfo = "speaker=liangliang&speed=0&text=";
                    break;
                case 8: voiceInfo = "speaker=jose&speed=0&text=";
                    break;
                case 9: voiceInfo = "speaker=carmen&speed=0&text=";
                    break;
            }
            String postParams = voiceInfo + text;
            con.setDoOutput(true);
            OutputStream ops = con.getOutputStream(); //
            DataOutputStream wr = new DataOutputStream(ops); //
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];

                // 랜덤한 이름으로 mp3 파일 생성
                String tempname = context.getFilesDir().getPath().toString() + "/" + Long.valueOf(new Date().getTime()).toString();
                File f = new File(tempname + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();

                //여기서 바로 재생하도록 하자.
                String pathToFile = tempname + ".mp3";
                MediaPlayer audioPlay = new MediaPlayer();
                audioPlay.setDataSource(pathToFile);
                audioPlay.prepare();
                audioPlay.start();

            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
