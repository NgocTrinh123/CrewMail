package com.dazone.crewemail.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ExternalFunctionActivity extends BaseActivity {

    protected int activityNumber = 1;
    protected RelativeLayout external_home;

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url, abc);
            return true;
        }
    }

    protected WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_function);
        init();
    }

    protected void init() {
        getBundleValue();
        external_home = (RelativeLayout) findViewById(R.id.external_home);
        webview = (WebView) findViewById(R.id.external_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        setupCookies();
        openURL();
    }

    protected void setupCookies() {
        UserData user = UserData.getUserInformation();

        CookieManager cookieManager = CookieManager.getInstance();
        String site = mServerSite.replace(":8080", "");
        String cookieString = "skey0=" + user.getSession() + ";skey1=fhgdfgdfg;skey2=" + Locale.getDefault().getLanguage().toUpperCase() + ";skey3=" + user.getCompanyNo();
        Util.printLogs(cookieString);
        String[] cookies = cookieString.split(";");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 10);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
            cookieManager.setAcceptCookie(true);
            for (String cookie : cookies) {
                cookieManager.setCookie(site, cookie.trim() + "; Expires=" + TimeUtils.getFormattedTime(calendar.getTimeInMillis(), Statics.FORMAT_DATE_SESSION_EXPIRE) + " GMT");
            }
            cookieManager.flush();
        } else {
            final CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            cookieManager.removeAllCookie();
            cookieManager.setAcceptCookie(true);
            for (String cookie : cookies) {
                cookieManager.setCookie(site, cookie.trim() + "; Expires=" + TimeUtils.getFormattedTime(calendar.getTimeInMillis(), Statics.FORMAT_DATE_SESSION_EXPIRE) + " GMT");
            }
            cookieSyncManager.sync();
        }
        abc.put("Cookie", cookieString);

    }

    protected void getBundleValue() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getInt("count_id") != 0) {
            activityNumber = bundle.getInt("count_id");
        }
    }

    protected Map<String, String> abc = new HashMap<String, String>();

    protected void openURL() {
        switch (activityNumber) {
            case 1:
                webview.loadUrl(mServerSite + "/UI/mobilenotice", abc);
                break;
            case 2:
                webview.loadUrl(mServerSite + "/UI/MobileBoard", abc);
                break;
            case 3:
                webview.loadUrl(mServerSite + "/UI/MobileSchedule", abc);
                break;
            default:
                webview.loadUrl(mServerSite + "/UI/mobilenotice", abc);
                break;
        }
        webview.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
