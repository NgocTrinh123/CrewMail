package com.dazone.crewemail.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.View.SoftKeyboardDetectorView;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.database.ServerSiteDBHelper;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnAutoLoginCallBack;
import com.dazone.crewemail.interfaces.OnCheckDevice;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends BaseActivity implements OnCheckDevice, BaseHTTPCallBack {

    private Prefs prefs;
    private String server_site;
    private Context context;
    boolean firstLogin = true;

    private EditText etUserName;
    private EditText etPassword;
    private EditText etServer;
    private RelativeLayout btnSignUp;
    private TextView tvLogo;
    private RelativeLayout include_logo;
    private ImageView img_login_logo;
    private TextView tv_login_logo_text;
    private RelativeLayout login_btn_login;
    private LinearLayout ll_login_sign_up;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private String regID;
    private String msg = "";
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.app_base_color));
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(mAccountReceiver, intentFilter);

        Intent intent = new Intent();
        intent.setAction("com.dazone.crewcloud.account.get");
        intent.putExtra("senderPackageName", this.getPackageName());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);

        RelativeLayout logo = (RelativeLayout) findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);

        final SoftKeyboardDetectorView softKeyboardDetectorView = new SoftKeyboardDetectorView(this);
        addContentView(softKeyboardDetectorView, new FrameLayout.LayoutParams(-1, -1));

        softKeyboardDetectorView.setOnShownKeyboard(new SoftKeyboardDetectorView.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                img_login_logo.setVisibility(View.GONE);
                //ll_login_sign_up.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_login_logo_text.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                tv_login_logo_text.setLayoutParams(params);
            }
        });

        softKeyboardDetectorView.setOnHiddenKeyboard(new SoftKeyboardDetectorView.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                img_login_logo.setVisibility(View.VISIBLE);
                //ll_login_sign_up.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_login_logo_text.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                tv_login_logo_text.setLayoutParams(params);
            }
        });

        prefs = DaZoneApplication.getInstance().getPrefs();
        server_site = DaZoneApplication.getInstance().getPrefs().getServerSite();

        firstChecking();
    }

    @Override
    protected void onDestroy() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();

        unregisterReceiver(mAccountReceiver);
    }

    public void firstChecking() {
//        if (firstLogin) {
        if (Util.isNetworkAvailable()) {
            doLogin();
        } else {
            showNetworkDialog();
        }
//        }
    }

    @Override
    public void onDeviceSuccess() {
//        doLogin();
    }

    @Override
    public void onHTTPSuccess() {
        if (!TextUtils.isEmpty(server_site)) {
            server_site.replace("http://", "");
            prefs.putServerSite(server_site);
            ServerSiteDBHelper.addServerSite(server_site);
        }

        createGMC();
        loginSuccess();
    }

    @Override
    public void onHTTPFail(ErrorData errorData) {
        if (firstLogin) {
            dismissProgressDialog();
            firstLogin = false;
            findViewById(R.id.logo).setVisibility(View.GONE);
            init();
        } else {
            dismissProgressDialog();
            String error_msg = errorData.getMessage();
            if (TextUtils.isEmpty(error_msg)) {
                error_msg = getString(R.string.connection_falsed);
            }
            AlertDialogView.normalAlertDialog(this, getString(R.string.app_name), error_msg, getString(R.string.string_title_mail_create_ok), null);
        }
    }

    private void loginSuccess() {
        dismissProgressDialog();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HttpRequest.getInstance().getDepartment(null);
                Intent i = new Intent(LoginActivity.this, ListEmailActivity.class);
                startActivity(i);
                finish();
            }
        }, 500);
    }

    private void doLogin() {

        if (Util.checkStringValue(prefs.getAccessToken()) && !prefs.getBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false)) {
            HttpRequest.getInstance().checkLogin(prefs, this);
        } else {
            prefs.putBooleanValue(Statics.PREFS_KEY_SESSION_ERROR, false);
            findViewById(R.id.logo).setVisibility(View.GONE);
            firstLogin = false;
            init();
        }
    }

    private void init() {
        img_login_logo = (ImageView) findViewById(R.id.img_login_logo);
        tv_login_logo_text = (TextView) findViewById(R.id.tv_login_logo_text);
        etUserName = (EditText) findViewById(R.id.login_edt_username);
        etPassword = (EditText) findViewById(R.id.login_edt_password);
        etServer = (EditText) findViewById(R.id.login_edt_server);
        ll_login_sign_up = (LinearLayout) findViewById(R.id.ll_login_sign_up);

        etUserName.setPrivateImeOptions("defaultInputmode=english;");
        etServer.setPrivateImeOptions("defaultInputmode=english;");

        Button btnLogin = (Button) findViewById(R.id.login_btn_login);
        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etUserName.setText(result);
                    etUserName.setSelection(result.length());
                }
            }
        });

        etPassword = (EditText) findViewById(R.id.login_edt_password);
        etServer = (EditText) findViewById(R.id.login_edt_server);

        etPassword.setText(new PreferenceUtilities().getPass());
        etServer.setText(new PreferenceUtilities().getDomain());
        etUserName.setText(new PreferenceUtilities().getUserId());
        etServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etServer.setText(result);
                    etServer.setSelection(result.length());
                }
            }
        });

        //scl_login = (ScrollView) findViewById(R.id.scl_login);
        initForSignup();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUserName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                server_site = etServer.getText().toString().trim();
                if (TextUtils.isEmpty(checkStringValue(server_site, username, password))) {
                    server_site = getServerSite(server_site);
                    String temp_server_site = server_site;
                    String company_domain = server_site;

                    if (company_domain.startsWith("http")) {
                        company_domain.replace("http://", "");
                    } else {
                        server_site = "http://" + server_site;
                        temp_server_site = "http://" + temp_server_site;
                    }
                    if (temp_server_site.contains(".bizsw.co.kr")) {
                        temp_server_site = "http://www.bizsw.co.kr:8080";
                    } else {
                        if (temp_server_site.contains("crewcloud.net")) {
                            temp_server_site = "http://www.crewcloud.net";
                        }
                    }
                    showProgressDialog();
                    Urls.URL_DEFAULT_API = temp_server_site;
                    HttpRequest.getInstance().login(username, password, company_domain, temp_server_site, prefs, LoginActivity.this);
                } else {
                    AlertDialogView.normalAlertDialog(LoginActivity.this, getString(R.string.app_name), checkStringValue(server_site, username, password), getString(R.string.string_title_mail_create_ok), null);
                }
            }
        });
    }

    private void initForSignup() {
        RelativeLayout btnSignUp = (RelativeLayout) findViewById(R.id.login_btn_sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callActivity(SignUpActivity.class);
            }
        });
    }

    private String checkStringValue(String server_site, String username, String password) {
        String result = "";
        if (TextUtils.isEmpty(server_site)) {
            result += getString(R.string.string_server_site);
        }

        if (TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_username);
            } else {
                result += ", " + getString(R.string.login_username);
            }
        }
        if (TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.password);
            } else {
                result += ", " + getString(R.string.password);
            }
        }
        if (TextUtils.isEmpty(result)) {
            return result;
        } else {
            return String.format(getString(R.string.login_empty_input), result);
        }
    }

    private String getServerSite(String server_site) {
        String[] domains = server_site.split("[.]");
        if (server_site.contains(".bizsw.co.kr") && !server_site.contains("8080")) {
            return server_site.replace(".bizsw.co.kr", ".bizsw.co.kr:8080");
        }

        if (domains.length == 1) {
            return domains[0] + ".crewcloud.net";
        } else {
            return server_site;
        }
    }

    // ----------------------------------------------------------------------------------------------

    public static String BROADCAST_ACTION = "com.dazone.crewcloud.account.receive";
    private boolean isAutoLoginShow = false;
    private boolean mFirstLogin = true;

    private BroadcastReceiver mAccountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receiverPackageName = intent.getExtras().getString("receiverPackageName");
            if (LoginActivity.this.getPackageName().equals(receiverPackageName)) {
                //String senderPackageName = intent.getExtras().getString("senderPackageName");
                String companyID = intent.getExtras().getString("companyID");
                String userID = intent.getExtras().getString("userID");
                if (!TextUtils.isEmpty(companyID) && !TextUtils.isEmpty(userID) && !isAutoLoginShow) {
                    isAutoLoginShow = true;
                    showPopupAutoLogin(companyID, userID);
                }
            }
        }
    };

    private void showPopupAutoLogin(final String companyID, final String userID) {
        String alert1 = Util.getString(R.string.auto_login_company_ID) + companyID;
        String alert2 = Util.getString(R.string.auto_login_user_ID) + userID;
        String alert3 = Util.getString(R.string.auto_login_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(Util.getString(R.string.auto_login_title))
                .setMessage(alert1 + "\n" + alert2 + "\n\n" + alert3)
                .setPositiveButton(Util.getString(R.string.auto_login_button_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        autoLogin(companyID, userID);
                    }
                })
                .setNegativeButton(Util.getString(R.string.auto_login_button_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);

        if (textView != null) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
    }

    public void autoLogin(String companyID, final String userID) {
        server_site = companyID;

        server_site = getServerSite(server_site);
        String company_domain = server_site;

        if (!company_domain.startsWith("http")) {
            server_site = "http://" + server_site;
        }

        String temp_server_site = server_site;

        if (temp_server_site.contains(".bizsw.co.kr")) {
            temp_server_site = "http://www.bizsw.co.kr:8080";
        } else {
            if (temp_server_site.contains("crewcloud")) {
                temp_server_site = "http://www.crewcloud.net";
            }
        }

        showProgressDialog();

        Prefs prefs = DaZoneApplication.getInstance().getPrefs();
        prefs.putServerSite(server_site); // group ID

        HttpRequest.getInstance().AutoLogin(company_domain, userID, temp_server_site, prefs, new OnAutoLoginCallBack() {
            @Override
            public void OnAutoLoginSuccess(String response) {
                createGMC();
                loginSuccess();
            }

            @Override
            public void OnAutoLoginFail(ErrorData dto) {
                if (mFirstLogin) {
                    dismissProgressDialog();

                    mFirstLogin = false;
                    include_logo.setVisibility(View.GONE);
                    init();
                } else {
                    dismissProgressDialog();
                    String error_msg = dto.getMessage();

                    if (TextUtils.isEmpty(error_msg)) {
                        error_msg = getString(R.string.connection_falsed);
                    }

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(error_msg).setPositiveButton(getString(R.string.cast_tracks_chooser_dialog_ok), null);
                    builder.create().show();
                }
            }
        });
    }

    // ----------------------------------------------------------------------------------------------

    private class WebClientAsyncTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<LoginActivity> mWeakActivity;
        private ProgressDialog mProgressDialog = null;

        private WebClientAsyncTask(LoginActivity activity) {
            mWeakActivity = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            LoginActivity activity = mWeakActivity.get();

            if (activity != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mProgressDialog = new ProgressDialog(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    mProgressDialog = new ProgressDialog(activity);
                }
//                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setMessage(activity.getString(R.string.wating_app_download));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL apkUrl = new URL(Urls.DOWNLOAD_LINK + "CrewMail.apk");
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/CrewMail.apk";
                fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[4096];
                int readCount;

                while (true) {
                    readCount = bufferedInputStream.read(buffer);
                    if (readCount == -1) {
                        break;
                    }

                    fileOutputStream.write(buffer, 0, readCount);
                    fileOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            LoginActivity activity = mWeakActivity.get();

            if (activity != null) {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/CrewMail.apk";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void createGMC() {
        context = getApplicationContext();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regID = new Prefs().getGCMregistrationid();
            if (regID.isEmpty()) {
                registerInBackground();
            } else {
                HttpRequest.getInstance().InsertDevice(regID, new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {

                    }

                    @Override
                    public void onHTTPFail(ErrorData errorDto) {

                    }

                });
            }
        } else {
            dismissProgressDialog();
            callActivity(ListEmailActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Util.printLogs("This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new register().execute("");
    }

    public class register extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regID = gcm.register(Statics.GOOGLE_SENDER_ID_MAIL);
                msg = "Device registered, registration ID=" + regID;
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            new Prefs().setGCMregistrationid(regID);
            HttpRequest.getInstance().InsertDevice(regID, new BaseHTTPCallBack() {
                @Override
                public void onHTTPSuccess() {

                }

                @Override
                public void onHTTPFail(ErrorData errorDto) {

                }
            });

        }

    }
}
