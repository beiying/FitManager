package cn.beiying.fit_mvp;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by beiying on 19/3/8.
 */

public class ApiRetrofit {
    public static final String BASE_SERVER_URL = "http://chatbot.api.talkmoment.com";

    private static ApiRetrofit sApiRetrofit;
    private Retrofit mRetrofit;
    private OkHttpClient mHttpClient;
    private ApiServer mApiServer;

    private String TAG = "ApiRetrofit";

    private Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            Response response = chain.proceed(request);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.e(TAG, "----------Request Start----------------");
            Log.e(TAG, "| " + request.toString() + request.headers().toString());
            Log.e(TAG, "| Response:" + content);
            Log.e(TAG, "----------Request End:" + duration + "毫秒----------");
            return response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
        }
    };

    public ApiRetrofit() {
        mHttpClient = new OkHttpClient.Builder()
                .addInterceptor(mInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mHttpClient)
                .build();
        mApiServer = mRetrofit.create(ApiServer.class);
    }

    public static ApiRetrofit getInstance() {
        if (sApiRetrofit == null) {
            synchronized (Object.class) {
                if (sApiRetrofit == null) {
                    sApiRetrofit = new ApiRetrofit();
                }
            }
        }
        return sApiRetrofit;
    }

    public ApiServer getApiService() {
        return mApiServer;
    }

}
