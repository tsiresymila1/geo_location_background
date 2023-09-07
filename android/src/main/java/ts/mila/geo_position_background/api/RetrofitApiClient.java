package ts.mila.geo_position_background.api;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitApiClient {
    @POST("/")
    Call<ResponseObject> postPosition(@Body() JSONObject json);
}
