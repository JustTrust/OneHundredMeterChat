package org.belichenko.a.onehundredmeterchat;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

public class Retrofit {
    private static final String ENDPOINT = "http://psi.kh.ua";
    private static GetInterface getInterface;
    private static PostInterface postInterface;

    static {
        init();
    }

    private static void init() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        RestAdapter postAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();
        getInterface = restAdapter.create(GetInterface.class);
        postInterface = postAdapter.create(PostInterface.class);
    }


    public static void getMessage(Map<String, String> filters, Callback<List<Message>> callback) {
        getInterface.getMessage(filters, callback);

    }

    public static void sendMessage(Map<String, String> filters, Callback<Void> callback) {
        postInterface.sendMessage(filters, callback);
    }


    interface GetInterface {
        @GET("/hakaton/api.php")
        void getMessage(@QueryMap Map<String, String> filters, Callback<List<Message>> callback);

    }
    interface PostInterface {
        @FormUrlEncoded
        @POST("/hakaton/api.php")
        void sendMessage(@FieldMap Map<String, String> filters, Callback<Void> callback);
    }
}