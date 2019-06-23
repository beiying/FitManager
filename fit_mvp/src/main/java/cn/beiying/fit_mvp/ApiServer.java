package cn.beiying.fit_mvp;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by beiying on 19/3/8.
 */

public interface ApiServer {
    @POST("/user/app/login/with/password")
    Observable<String> LoginByRx(@Field("username") String username, @Field("password") String password);

    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
