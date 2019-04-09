package com.example.list.deepakpawate.playmusic;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import static android.media.AudioManager.STREAM_MUSIC;
import static com.example.list.deepakpawate.playmusic.MainActivity.audioManager;
import static com.example.list.deepakpawate.playmusic.MainActivity.player;


public class VolumeActivity extends AppCompatActivity {
    SeekBar setVolume;
    private CheckBox checkBox;
    private float gvolume;
    private Switch gswitch;
    private Button buttonVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forvolume);
        setVolume = (SeekBar) findViewById(R.id.seekBar2);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        gswitch =(Switch) findViewById(R.id.switch1) ;
        buttonVar =(Button)  findViewById(R.id.button) ;
        setVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                     Log.e("volume bar", "inside volume bar");
                                                     float volumeNum = progress / 100f;
                                                     gvolume = volumeNum;
                                                     Log.e("volume bar", "inside volume bar" + gvolume);
                                                     player.setVolume(volumeNum, volumeNum);
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }

                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {

                                                 }
                                             }
        );
        checkBox.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.checkBox: {
                        if (((CheckBox) v).isChecked()) {
                            Log.e("check volume", "global volume" + gvolume);
                            Log.e("Mute pressed", "change volume");
                            player.setVolume(0, 0);
                        } else {
                            Log.e("Mute unpressed", "change volume");
                            Log.e("unpressed mute", "global volume" + gvolume);
                            player.setVolume((float)0.8, (float)0.8);
                        }
                    }
                }
            }
        });

        gswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true) {
                    Intent intent = new Intent(VolumeActivity.this, AudioRecorderActivity.class);
                    Log.d("gswitch", "gswitch true: ");
                    startActivity(intent);
                    gswitch.setChecked(false);

                }
            }
        });

        buttonVar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("buttonVar", "buttonVar.setOnClickListener: ");
                finish();
            }
        });
    }
}
