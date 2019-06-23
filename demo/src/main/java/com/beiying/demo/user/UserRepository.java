package com.beiying.demo.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.beiying.demo.WebService;

import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 因为会从应用的其余部分中提取数据源, 因此可以为视图模型提供从几个不同的数据获取实现获得的数据
 * 起到了ViewModel与Data的解耦
 * Created by beiying on 19/2/27.
 */

@Singleton
public class UserRepository {
    private WebService webService;
    private UserCache userCache;

    public LiveData<User> getUser(int userId) {
        final MutableLiveData<User> data = new MutableLiveData<>();
        webService.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        return data;
    };
}
