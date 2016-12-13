package com.dazone.crewemail.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.setting.GeneralSettingActivity;
import com.dazone.crewemail.activities.setting.MyProfileActivity;
import com.dazone.crewemail.activities.setting.NotificationSettingActivity;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailProfileData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetInfoUser;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MailProfile extends BaseActivity implements View.OnClickListener {
    private ImageView imgAvatar;
    //private LinearLayout linearProfile;
    private MailProfileData item;
    private UserData userData;
    private ProgressDialog pdia;
    private ImageView btn_back;
//    private TextView tvGeneral;
//    private TextView tv_profile;
//    private TextView tvNotification;
//    private TextView tvLogout;
    private LinearLayout ln_general;
    private LinearLayout ln_notify;
    private LinearLayout ln_logout;
    private LinearLayout ln_profile;
    public ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_profile);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        pdia = new ProgressDialog(this);
        pdia.setMessage(Util.getString(R.string.loading_title));
        pdia.show();
        setTitle("");
        Fresco.initialize(this);
        userData = UserData.getUserInformation();
        initControl();
        getDataFromServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initControl() {
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        ln_general = (LinearLayout) findViewById(R.id.ln_general);
        ln_notify = (LinearLayout) findViewById(R.id.ln_notify);
        ln_profile = (LinearLayout) findViewById(R.id.ln_profile);
        ln_logout = (LinearLayout) findViewById(R.id.ln_logout);

        ln_notify.setOnClickListener(this);
        ln_profile.setOnClickListener(this);
        ln_logout.setOnClickListener(this);
        ln_general.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    private void getDataFromServer() {
        HttpRequest.getInstance().getInfoUser(new OnGetInfoUser() {
            @Override
            public void onGetInfoUserSuccess(MailProfileData commentList) {
                if (commentList != null) {
                    item = commentList;
                    bindingData();
                }
                pdia.dismiss();
            }

            @Override
            public void onGetInfoUserFail(ErrorData errorDto) {
                pdia.dismiss();
                setDataTemp();
            }

        }, userData.getId());
    }

    private void bindingData() {
        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + item.getAvatar());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);
        imageLoader.displayImage(String.valueOf(imageUri),imgAvatar);
//        imgAvatar.setImageURI(imageUri);
    }

    public void setDataTemp() {
        String rootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Uri imageUri = Uri.parse(rootLink + userData.getAvatar());
        imgAvatar.setImageURI(imageUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.ln_profile:
                Intent intent3 = new Intent(MailProfile.this, MyProfileActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_general:
                Intent intent = new Intent(MailProfile.this, GeneralSettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_notify:
                Intent intent2 = new Intent(MailProfile.this, NotificationSettingActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ln_logout:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        AlertDialogView.normalAlertDialogWithCancel(this, getString(R.string.app_name), getString(R.string.dialog_are_you_sure), getString(R.string.dialog_logout), getString(R.string.dialog_cancel), new AlertDialogView.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                HttpRequest.getInstance().Logout(new BaseHTTPCallBack() {
                    @Override
                    public void onHTTPSuccess() {
                        Util.printLogs("SUCCESS.");
                        UserData.getInstance().logout();
                        Intent intent = new Intent(MailProfile.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }

                    @Override
                    public void onHTTPFail(ErrorData errorDto) {
                        Util.printLogs("FAIL.");
                    }
                });
            }

            @Override
            public void onCancelClick() {

            }
        });
    }
}
