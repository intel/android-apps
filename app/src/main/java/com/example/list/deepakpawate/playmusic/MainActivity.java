package com.example.list.deepakpawate.playmusic;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.audiofx.Equalizer;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ListActivity;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import java.io.IOException;
import java.util.List;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    private static final int FREQUENCY = 500;
    private static final int    PEMISSION_NUMBER = 2;
    private static final int STEP_VALUE = 4000;
    private TextView selectedfile = null;
    private SeekBar seekBar = null;
    public static MediaPlayer player = null;
    public static String durationMin;
    public static Equalizer eq_var = null;
    public static AudioManager audioManager;
    private ImageButton prev = null;
    public static ImageButton play = null;
    private ImageButton next = null;
    private ImageButton volume = null;

    private storesong adapter = null;
    private ListView lv = null;
    private ListView m_listView = null;
    private int m_position;
    private int number_of_list_items;

    private boolean isStarted = true;
    private String currentFile = "";
    private boolean isMovingSeekBar = false;
    private final Handler handler = new Handler();


    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            setSongProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Very imp this is the first thing to do
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PEMISSION_NUMBER);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PEMISSION_NUMBER);
            }
        } else {
            // Start creating the user interface
            Log.e("b4", "createplayer" + currentFile);
            createplayer();
        }

    }

    public void createplayer() {
        selectedfile = (TextView) findViewById(R.id.selecteditem);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        prev = (ImageButton) findViewById(R.id.previous);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);
        volume = (ImageButton) findViewById(R.id.volume);
        player = new MediaPlayer();
        player.setOnCompletionListener(onCompletion);
        player.setOnErrorListener(onError);
        seekBar.setOnSeekBarChangeListener(seekBarChanged);

          Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.IS_MUSIC, null, null);

        if (null != cursor) {
            cursor.moveToFirst();
            adapter = new storesong(this, R.layout.songinfo, cursor);
            setListAdapter(adapter);

            number_of_list_items = getListAdapter().getCount();
            Log.e("number of list items", "number_of_list_items =" + number_of_list_items);
            prev.setOnClickListener(OnButtonClick);
            play.setOnClickListener(OnButtonClick);
            next.setOnClickListener(OnButtonClick);
            volume.setOnClickListener(OnButtonClick);
            Log.e("setOnClickListener", "b4 button press" + currentFile);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PEMISSION_NUMBER: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)) {
                        createplayer();
                        Log.e("createplayer called", "createplayer called");
                    }
                } else {
                    Log.e("Permission required", "Permission required");
                }
                return;
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //View inflatedView = getLayoutInflater().inflate(R.layout.songinfo, null);
        m_position = position;
        Log.e("song position", "song position" + m_position + "v.getTag =" + v.getTag());
        currentFile = (String) v.getTag();
        TextView duration =  v.findViewById(R.id.duration);
        durationMin = (String)duration.getText();
        m_listView = l;
        startPlay(currentFile);
    }

    private String getViewTag(int position) {
        View view = m_listView.getAdapter().getView(position, null, m_listView);
        String songString = (String) view.getTag();

        return songString;
    }

    private void startPlay(String file) {
        Log.e("Selected: ", file);

        //Use this selectedfile.setText(file) to get the song name
        //selectedfile.setText(file + " T = " + durationMin);
        selectedfile.setText(file);
        seekBar.setProgress(0);
        player.stop();
        player.reset();

        try {
            player.setDataSource(file);
            player.prepare();
            player.start();
            Log.e("Session ID", "startPlay: " + player.getAudioSessionId());
            Log.e("set Eq", "enabled Equilizer");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar.setMax(player.getDuration());
        play.setImageResource(android.R.drawable.ic_media_pause);
        setSongProgress();
        isStarted = true;
    }

    private void stopPlay() {
        player.stop();
        player.reset();
        play.setImageResource(android.R.drawable.ic_media_play);
        handler.removeCallbacks(updateSeekBarRunnable);
        seekBar.setProgress(0);
        isStarted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable);
        player.stop();
        player.reset();
        player.release();
        player = null;
    }

    private void setSongProgress() {
        handler.removeCallbacks(updateSeekBarRunnable);
        seekBar.setProgress(player.getCurrentPosition());
        handler.postDelayed(updateSeekBarRunnable, FREQUENCY);
    }

    private void printCompletionSong() {
        //String CurrentSong = getViewTag(m_position);
        Log.e("########","##################");
        Log.e("########","##################" + m_position);
    }

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            String nextSong ;
            ++m_position;
            Log.e("OnCompletionListener", "onCompletion: " + m_position + number_of_list_items);
            if(m_position == number_of_list_items) {
                m_position = 0;
                nextSong = getViewTag(m_position);
            } else {
                nextSong = getViewTag(m_position);
            }
            Log.e("OnCompletionListener","onCompletion" + nextSong);
            Log.e("song number","number =" + m_position);
            startPlay(nextSong);
        }
    };

    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChanged =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isMovingSeekBar) {
                        player.seekTo(progress);
                        Log.e("OnSeekBarChangeListener", "song progressing OnProgressChanged");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    isMovingSeekBar = false;
                }
            };

    private boolean checkFileEmpty() {
        if(currentFile.isEmpty()) {
            Log.e("File is null: ", currentFile);
            Toast.makeText(MainActivity.this,"Select song to start Play if NO songs then Add few",Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    private View.OnClickListener OnButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.play: {
                    if(checkFileEmpty()) {
                        return;
                    }
                    Log.e("Play Button1", "Pressed" + currentFile);
                    Log.e("Play button pressed", "which soong number =" + m_position);
                    if (player.isPlaying()) {
                        handler.removeCallbacks(updateSeekBarRunnable);
                        player.pause();
                        play.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        if (isStarted) {
                            player.start();
                            play.setImageResource(android.R.drawable.ic_media_pause);
                            setSongProgress();
                        } else {
                            startPlay(currentFile);
                        }
                    }
                    break;
                }

                case R.id.next: {
                    Log.e("Next Button", "Pressed");
                    if(checkFileEmpty()) {
                        return;
                    }
                    player.pause();
                    player.seekTo(player.getDuration());

                    ++m_position;
                    Log.e("Next Button", "Position onf last song" + m_position);
                    Log.e("Next Button", "No of songs" + number_of_list_items);
                    if(m_position == number_of_list_items) {
                        m_position = 0;
                    }
                    String nextSong = getViewTag(m_position);
                    Log.e("nextSong", "song position" + nextSong);
                    startPlay(nextSong);
                    break;
                }

                case R.id.previous: {
                    Log.e("Previous Button", "Pressed");
                    if(checkFileEmpty()) {
                        return;
                    }
                    player.pause();
                    player.seekTo(player.getCurrentPosition());
                     --m_position;
                    String nextSong = getViewTag(m_position);
                    Log.e("nextSong", "song position" + nextSong);
                    startPlay(nextSong);
                    break;
                }

                case R.id.volume: {
                    Log.e("volume pressed", "change volume");
                    Intent intent = new Intent(MainActivity.this, VolumeActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    };


}
