package com.example.android.sportsfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar pb_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web);
        //get intent with the url from SportActivity
        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");
        pb_loading = findViewById(R.id.loading);
        pb_loading.setMax(100);
        webView = findViewById(R.id.webview);
        webView.loadUrl(url);
        //enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClientHelper());


    }

    /*
      Source
      https://developer.android.com/training/implementing-navigation/ancestral.html
      Method for up navigation between this activity and the parent activity
       */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //subclass used to keep the urls inside the app.
    private class MyWebViewClient extends WebViewClient {

        /*
          When the page is started make the progress bar visible and set the
          status to 0
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pb_loading.setVisibility(View.VISIBLE);
            pb_loading.setProgress(0);
        }

        /*
        replace url to keep browsing inside the app
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            return true;
        }

        /*
        when the app is done loading the web page makes the progress bar gone
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pb_loading.setVisibility(View.GONE);
            pb_loading.setProgress(100);
        }
    }

    /*
    update the progress bar in real time when the page is loaded
     */
    private class WebChromeClientHelper extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            pb_loading.setProgress(progress);
        }
    }


}

