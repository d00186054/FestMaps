package com.example.androidrealtimelocation2020.Remote;

import com.example.androidrealtimelocation2020.Model.MyResponse;
import com.example.androidrealtimelocation2020.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAOzDDh38:APA91bGYzI4BBKhubbXWQmQzHUYt_lPDi8nPerRo9ZgHjlsHojV6UL76xp6wwI_p8d0q9liiU-XY6x1pISGgR-Bj14fAlLHYg1B-Gt-eWH32NGTKesTgGND49IzuxPnM4ed8RZN3NTtq"
    })
    @POST("fcm/send")
    Observable<MyResponse> sendFreindRequestToUser(@Body Request body);
}
