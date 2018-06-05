package com.example.mbigelli.watsonchat;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by mbigelli on 08/05/17.
 */

public class JSONUtils {
    public static JSONArray remove(JSONArray jsonArray, int index) {

        JSONArray output = new JSONArray();
        int len = jsonArray.length();
        for (int i = 0; i < len; i++)   {
            if (i != index) {
                try {
                    output.put(jsonArray.get(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return output;
    }
}
