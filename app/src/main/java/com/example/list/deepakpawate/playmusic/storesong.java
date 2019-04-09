package com.example.list.deepakpawate.playmusic;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Created by dpawate on 3/18/2018.
 */


public class storesong extends SimpleCursorAdapter {
    public storesong(Context context, int layout, Cursor c) {
        super(context, layout, c, new String[]{MediaStore.MediaColumns.DISPLAY_NAME,
                        MediaStore.MediaColumns.TITLE,MediaStore.Audio.AudioColumns.DURATION},
                 new int[]{R.id.title, R.id.duration});
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView duration = (TextView) view.findViewById(R.id.duration);
        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        if (data.contains("aac")) {
            Log.e("Mimetype",data);
        }
        title.setText(cursor.getString(cursor.getColumnIndex(
                MediaStore.MediaColumns.TITLE)));
        String str =cursor.getString(cursor.getColumnIndex(
                MediaStore.MediaColumns.MIME_TYPE));
        Log.e("Mime type",str);
        long durationInMilli = Long.parseLong(cursor.getString(
                cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));

        double durationInMinutes = ((double) durationInMilli / 1000.0) / 60.0;
        durationInMinutes = new BigDecimal(Double.toString(durationInMinutes)).
                setScale(2, BigDecimal.ROUND_UP).doubleValue();
        duration.setText("" + durationInMinutes);
        view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.songinfo, parent, false);
        Log.e("b4","bindView");
        Log.e("b4","#################################");
        bindView(v, context, cursor);
        return v;
    }
}

