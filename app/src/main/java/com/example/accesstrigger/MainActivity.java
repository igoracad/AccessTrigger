package com.example.accesstrigger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.accesstrigger.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'accesstrigger' library on application startup.
    static {
        System.loadLibrary("accesstrigger");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        webRequest(stringFromJNI());
    }

    private void triggerHomeButtonPress() {
        // Trigger the AccessibilityService to perform the home button action
        MyAccessibilityService.clickAppHomeScreen();
    }
    public void webRequest(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Call<Void> call = service.getRequest();

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    Log.i("Get request", "success " + response.code());
                    triggerHomeButtonPress();
                } else {
                    Log.e("Get request", "failure " + response.code());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("sms", "Exception on Post", t);
            }
        });
    }

    /**
     * A native method that is implemented by the 'accesstrigger' native library,
     * which is packaged with this application.
     */
    public native boolean verifyCredentials(String username, String password);

    public native String stringFromJNI();
}