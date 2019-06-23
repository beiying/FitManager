package com.beiying.demo;

import com.beiying.demo.user.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by beiying on 19/2/27.
 */

public interface WebService {

    @FormUrlEncoded
    @POST("/user/login/with/captcha")
    Call<User> loginWithCaptcha(@Field("phone") String phone, @Field("captcha") String captcha);

    @GET("/user/sync?uid={uid}")
    Call<User> getUser(@Path("uid") int uid);
}
