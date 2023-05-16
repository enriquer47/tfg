package com.example.tfg;

import android.content.Context;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {
    private static String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY = "key=AAAAFRHiBLY:APA91bEQ_1HxcflGR-xsdg8uPUB9AAPTgQraOfwabEaBkVwEC-hoJtk78Eoc58Zp-VBNZfzDLIsQpcSa02a7J6p3wzBnJEomYwrEE_4QucunXbdjdzogEXAopFbsin7s6u0Pl7STgQCt";

    public static void pushNotification(Context context, String token, String title, String message){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectNotification = new JSONObject();
            jsonObjectNotification.put("title", title);
            jsonObjectNotification.put("body", message);
            jsonObject.put("to", token);
            jsonObject.put("notification", jsonObjectNotification);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM "+response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("Content-Type","application/json");
                    header.put("Authorization",SERVER_KEY);
                    return header;
                }
            };
            queue.add(jsonObjectRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
