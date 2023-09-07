package ts.mila.geo_position_background.api;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.Log;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ts.mila.geo_position_background.models.Position;

public class ApiClient {
    HashMap<String, Object> data;
    ExecutorService executor;
    Retrofit retrofit;
    Realm realm;

    String TAG = "[API CLIENT]";

    public ApiClient(Realm realm) {
        this.realm = realm;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void runPost(String apiURL, JSONObject lastPosition, boolean cached) {
        Log.e(TAG, lastPosition.toString());
        if (!apiURL.endsWith("/")) {
            retrofit = new Retrofit.Builder().baseUrl(apiURL + "/").addConverterFactory(GsonConverterFactory.create()).build();
        } else {
            retrofit = new Retrofit.Builder().baseUrl(apiURL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        RetrofitApiClient retrofitAPI = retrofit.create(RetrofitApiClient.class);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm t) {
                RealmResults<Position> positions = t.where(Position.class).findAll();
                JSONObject  jsonObject = new JSONObject();
                ArrayList<JSONObject> list = new ArrayList<>();
                for(Position p: positions){
                    list.add(p.toJsonObject());
                }
                list.add(lastPosition);
                try {
                    jsonObject.put("model",Build.MODEL);
                    jsonObject.put("deviceId",Build.ID);
                    jsonObject.put("version",Build.VERSION.RELEASE);
                    jsonObject.put("positions",list);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                Call<ResponseObject> call = retrofitAPI.postPosition(jsonObject);
                call.enqueue(new retrofit2.Callback<ResponseObject>() {
                    @Override
                    public void onResponse(Call<ResponseObject> call, Response<ResponseObject> response) {
                        if (cached) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm transaction) {
                                    transaction.delete(Position.class);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject> call, Throwable t) {
                        Log.i(TAG, "Error on sending post :: " + t.getMessage());
                        if (cached) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm transaction) {
                                    Position pos = transaction.createObjectFromJson(Position.class, lastPosition);
                                    if (pos != null) {
                                        transaction.insert(pos);
                                        Log.i(TAG, pos.toString() + " saved");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

    }


    public void postData(String apiURL, JSONObject data) {
        if (apiURL != null && !apiURL.trim().equals("") && urlFormed(apiURL)) {
            try {
                URL url = new URL(apiURL);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setRequestProperty("Content-Type", "application/json");
                client.setDoOutput(true);
                OutputStream os = client.getOutputStream();
                data.put("deviceId", Build.ID);
                data.put("deviceModel", Build.MODEL);
                byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

            } catch (IOException | JSONException e) {
                Log.i("[APi CLIENT]", "Error on posting data  :::" + e);
            }
        } else {
            Log.i("[APi CLIENT]", "URL malformed  :::" + apiURL);
        }
        Log.e("[DEVICE ID]", getSystemDetail());
    }

    @SuppressLint("HardwareIds")
    private String getSystemDetail() {
        HashMap<String, String> device = new HashMap<>();
        device.put("brand", Build.BRAND);
        device.put("id", Build.ID);
        device.put("model", Build.MODEL);
        device.put("sdk", Build.BRAND);
        device.put("manufacture", Build.MANUFACTURER);
        device.put("user", Build.USER);
        device.put("type", Build.TYPE);
        device.put("base", String.valueOf(Build.VERSION_CODES.BASE));
        device.put("incremental", Build.VERSION.INCREMENTAL);
        device.put("board", Build.BOARD);
        device.put("host", Build.HOST);
        device.put("fingerprint", Build.BOARD);
        device.put("version", Build.VERSION.RELEASE);
        return device.toString().replaceAll("=", ":");
    }


    public boolean urlFormed(String str) {
        try {
            new URL(str).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}