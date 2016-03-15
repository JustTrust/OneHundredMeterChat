package org.belichenko.a.onehundredmeterchat;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.QueryMap;

public class Retrofit {
    private static final String ENDPOINT = "http://psi.kh.ua";
    private static ApiInterface apiInterface;

    static {
        init();
    }

    private static void init() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        apiInterface = restAdapter.create(ApiInterface.class);
    }


    public static void getMessage(Map<String, String> filters, Callback<List<Message>> callback) {
        apiInterface.getMessage(filters, callback);

    }

    public static void sendMessage(Map<String, String> filters, Callback<List<Message>> callback) {
        apiInterface.sendMessage(filters, callback);
    }

    interface ApiInterface extends Constant {
        @GET("/hakaton/api.php")
        void getMessage(@QueryMap Map<String, String> filters, Callback<List<Message>> callback);

        @POST("/hakaton/api.php")
        void sendMessage(@FieldMap Map<String, String> filters, Callback<List<Message>> callback);
    }
}