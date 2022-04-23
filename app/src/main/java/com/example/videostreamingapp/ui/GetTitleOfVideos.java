package com.example.videostreamingapp.ui;

import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.net.URL;

public class GetTitleOfVideos {
    public static String getTitleQuietly(String youtubeUrl) {
        try {
            if (youtubeUrl != null) {
                URL embededURL = new URL("http://www.youtube.com/oembed?url=" +
                        youtubeUrl + "&format=json"
                );
                Log.d("chchch0",youtubeUrl);
               // FileInputStream fis = new FileInputStream(String.valueOf(embededURL));
                String StringFromInputStream = String.valueOf(IOUtils.readInputStreamFully(embededURL.openStream()));
                return new JSONObject(StringFromInputStream).getString("title");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }
}
