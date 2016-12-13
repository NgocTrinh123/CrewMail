package com.dazone.crewemail.activities;

import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.interfaces.OnGetStringCallBack;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;


/**
 * Created by david on 11/19/15.
 */
public class SignUpWebviewActivity extends ExternalFunctionActivity implements OnGetStringCallBack {
    @Override
    protected void init() {
        getBundleValue();
        external_home = (RelativeLayout)findViewById(R.id.external_home);
        webview=(WebView)findViewById(R.id.external_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
//        if(activityNumber==1) {
            HttpRequest.getInstance().getEmailSignUp(this, Util.getUniqueDeviceId(this));
//        }
    }
    String url = "http://www.bizsw.co.kr:8080/UI/Center/?regkey=";
    protected void openURL(String url) {
        webview.loadUrl(url,abc);
    }

    @Override
    public void onGetStringSuccess(String... strings) {
        openURL(url+strings[0]);
    }

    @Override
    public void onGetStringFail(ErrorData errorDto) {
        Toast.makeText(getApplicationContext(),errorDto.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
