package org.belichenko.a.onehundredmeterchat;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
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

    public static void getMessage(Map<String, String> filters, Callback<Message> callback) {
        apiInterface.getMessage(filters, callback);
    }

    interface ApiInterface {
        @GET("/hakaton/api.php")
            //void getMessage(Callback<List<Message>> callback);
        void getMessage(@QueryMap Map<String, String> filters, Callback<Message> callback);

//        @GET("https://restcountries.eu/rest/v1/name/{country}")
//        void getCountry(@Path("country") String name, Callback<List<Country>> callback);
    }

//    public static void getCountry(String country, Callback<List<Country>> callback) {
//        apiInterface.getCountry(country, callback);
//    }

}
