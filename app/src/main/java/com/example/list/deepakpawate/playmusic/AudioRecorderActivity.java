package com.example.list.deepakpawate.playmusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class AudioRecorderActivity extends AppCompatActivity {

    private Button startR1 = null, stopR2 = null, playR3 = null;
    public static final int RequestPermissionCode = 101;
    String filePath = null;
    String fileName = "recordedFile.mp4";
    MediaRecorder audioRecorder;
    MediaPlayer audioPlayer;
    Button buttonVar3;
    private boolean recNotStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        startR1 = (Button) findViewById(R.id.startR);
        stopR2 = (Button) findViewById(R.id.stopR);
        playR3 = (Button) findViewById(R.id.playR);
        buttonVar3 =(Button)  findViewById(R.id.button3) ;
        playR3.setEnabled(false);
        stopR2.setEnabled(false);

        startR1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(" Rec_file", "startR1.setOnClickListener: ");
                Log.e("startR", "onClick: ");
                playR3.setEnabled(false);
                recordAudioFromMic();
            }
        });

        stopR2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(" Rec_file ", "stopR2.setOnClickListener: ");
                Log.e("stopR", "onClick: ");
                stopRecording();
            }
        });

        playR3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(" Rec_file ", "playR3.setOnClickListener: ");
                playRecorded();
            }
        });

        buttonVar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("AudioRecorder", "buttonVar.setOnClickListener: ");
                finish();
            }
        });

    }


    public boolean createRecOutputFile() {
        boolean file;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + "recordedFile.mp4";
        Log.e(" Rec_file", "createRecOutputFile: " + filePath);
        if (filePath != null && !filePath.isEmpty())
            return true;

        return false;
    }

    public void configure() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioRecorder.setOutputFile(filePath);
    }

    public void recordAudioFromMic() {
        Log.e(" Rec_file [1]", "recordAudioFromMic: ");
        if (checkPermission()) {
            Log.e(" Rec_file [2]", "recordAudioFromMic: ");
            if (createRecOutputFile()) {
                Log.e(" Rec_file [3]", "recordAudioFromMic: " + filePath);
                configure();
                Log.e(" Rec_file [4]", "recordAudioFromMic: " + filePath);

                try {
                    audioRecorder.prepare();
                    audioRecorder.start();
                } catch (IllegalStateException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                startR1.setEnabled(false);
                stopR2.setEnabled(true);
                recNotStarted = true;
            }
        }else {
            Log.e("Rec_File", "needPermission: ");
            needPermission();
        }
    }

    public void needPermission() {
        Log.e("Rec_File", "inside needPermission: ");
        ActivityCompat.requestPermissions(AudioRecorderActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("Rec_file", "onRequestPermissionsResult: ");
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AudioRecorderActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AudioRecorderActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        Log.e("Rec_file", "checkPermission: ");
        int recAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writeExt = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return recAudioPermission == PackageManager.PERMISSION_GRANTED &&
                writeExt == PackageManager.PERMISSION_GRANTED;
    }

    public void playRecorded() {
        Log.e("check boolean", "playRecorded: " + MainActivity.player.isPlaying());
        if(MainActivity.player.isPlaying()){
            Log.e("Pausing Main Player", "playRecorded()");
            MainActivity.player.pause();
            MainActivity.play.setImageResource(android.R.drawable.ic_media_play);
        }
        stopR2.setEnabled(false);
        audioPlayer = new MediaPlayer();
        try {
            audioPlayer.setDataSource(filePath);
            audioPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioPlayer.start();
    }

    public void stopRecording() {
        if(!recNotStarted) {
            Toast.makeText(AudioRecorderActivity.this, "Record before stop recording",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        audioRecorder.stop();
        stopR2.setEnabled(false);
        playR3.setEnabled(true);
        startR1.setEnabled(true);
        recNotStarted = false;
     }

}
