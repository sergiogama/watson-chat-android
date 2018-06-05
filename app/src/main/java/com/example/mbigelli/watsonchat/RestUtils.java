package com.example.mbigelli.watsonchat;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mbigelli on 05/05/17.
 */

public class RestUtils {

    public static String authString(String username, String password) {
        return "Basic " + Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP);
    }

    public static Map<String, String> authHeaders(String username, String password) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authString(username, password));
        return headers;
    }

    public static Uri uriWithParams(String uriString, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(uriString).buildUpon();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    public static String readStream(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }




}
