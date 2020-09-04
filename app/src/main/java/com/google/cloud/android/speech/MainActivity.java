/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private SpeechService mSpeechService;

    private VoiceRecorder mVoiceRecorder;
    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        @Override
        public void onVoiceStart() {
            showStatus(true);
            if (mSpeechService != null) {
                mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (mSpeechService != null) {
                mSpeechService.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            showStatus(false);
            if (mSpeechService != null) {
                mSpeechService.finishRecognizing();
            }
        }

    };

    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    // View references
    private TextView mStatus;
    private TextView mText;
    private ResultAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private int voice_set_num;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mStatus.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mStatus = (TextView) findViewById(R.id.status);
        mText = (TextView) findViewById(R.id.text);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> results = savedInstanceState == null ? null :
                savedInstanceState.getStringArrayList(STATE_RESULTS);

        mAdapter = new ResultAdapter(results);

        voice_set_num = 1;

        // TTS here

        final Context context = this;
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



//        TTS.speechSynthesis("이 어플은 청각장애인 대화보조 어플입니다. 핸드폰에 하고싶은 말을 해주시면 감사하겠습니다.", context, voice_set_num);



        final View backgroundView = (View)findViewById(R.id.recycler_view);


        Button chatmsg = (Button)findViewById(R.id.call_answer);
        final EditText samedittext = (EditText)findViewById(R.id.sample_EditText);

        chatmsg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> hh = mAdapter.getResults();
                String answer_rec = "입력하세요";
                String emot = "";
                String ques = "";

                try {
                    Thread.sleep(1000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                try {
                    ques = hh.get(0);
                }catch(Exception e){
                    ques = "";
                }


                CommWithDanbee test = new CommWithDanbee();
                answer_rec = test.SendSpeech(ques);
                test = null;


                CommWithAdams ttest = new CommWithAdams();
                emot = ttest.SendEmotion(ques);
                ttest = null;
                Float score = new Float(0);
                String emotion_res = null;

                try {
                    JSONArray emot_tmp = new JSONArray(emot);
                    score = Float.parseFloat(emot_tmp.get(0).toString());
                    emotion_res = emot_tmp.get(1).toString();
                }
                catch(Exception e){
                    e.printStackTrace();
                }


                if (emot != null && score >= 0.75) {
                    Log.d("emot null", "www");

                    if (emotion_res.equals("기쁨")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    } else if (emotion_res.equals("신뢰")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    } else if (emotion_res.equals("공포")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                    } else if (emotion_res.equals("기대")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    } else if (emotion_res.equals("놀라움")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                    } else if (emotion_res.equals("슬픔")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                    } else if (emotion_res.equals("혐오")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                    } else if (emotion_res.equals("분노")) {
                        Toast.makeText(getApplicationContext(), emotion_res + score.toString(), Toast.LENGTH_LONG).show();
                        backgroundView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                }
                else if (emot != null &&score < 0.75){
                    backgroundView.setBackgroundColor(getResources().getColor(android.R.color.white));
                }


                samedittext.setText(answer_rec);
                mRecyclerView.setAdapter(mAdapter);

            }
        }) ;


        Button speachmsg = (Button)findViewById(R.id.call_tts);

        speachmsg.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String what_to_say = samedittext.getText().toString();

                mAdapter.addResult(what_to_say);

                mRecyclerView.setAdapter(mAdapter);

                TTS.speechSynthesis(what_to_say, context, voice_set_num);
            }
        });


        Button voiceSettingButton = (Button)findViewById(R.id.voice_setting);

        voiceSettingButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if (voice_set_num == 2) {
                    voice_set_num = 1;
                    Toast.makeText(getApplicationContext(), "여자 목소리로 설정되었습니다.", Toast.LENGTH_LONG).show();
                }
                else{
                    voice_set_num = 2;
                    Toast.makeText(getApplicationContext(), "남자 목소리로 설정되었습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        // Prepare Cloud Speech API
        bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

        // Start listening to voices
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecorder();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            showPermissionMessageDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();

        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;

        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            //outState.putStringArrayList(STATE_RESULTS, mAdapter.getResults());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
            }
        });
    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (mText != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    mText.setText(null);
                                    mAdapter.addResult(text);
                                    mRecyclerView.smoothScrollToPosition(0);
                                } else {
                                    mText.setText(text);
                                }
                            }
                        });
                    }
                }
            };

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_result, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
        }

    }

    private static class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final ArrayList<String> mResults = new ArrayList<>();

        ResultAdapter(ArrayList<String> results) {
            if (results != null) {
                mResults.addAll(results);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        void addResult(String result) {
            mResults.add(0, result);
            notifyItemInserted(0);
        }

        public ArrayList<String> getResults() {
            return mResults;
        }
    }

}
