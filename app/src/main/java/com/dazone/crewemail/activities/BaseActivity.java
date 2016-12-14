package com.dazone.crewemail.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Util;

/**
 * Created by Sherry on 12/8/15.
 */
public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    protected String mServerSite;
    private boolean mIsExit;
    public static BaseActivity Instance = null;
    public Prefs prefs;

    public static boolean isAppWentToBg = true;

    public static boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        mServerSite = DaZoneApplication.getInstance().getPrefs().getServerSite();
        Instance = this;
        prefs = DaZoneApplication.getInstance().getPrefs();
    }

    @Override
    public void onBackPressed() {

        if (!isTaskRoot()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.finish_activity_show, R.anim.finish_activity_hide);
        } else {
            if (mIsExit) {
                super.onBackPressed();
            } else {
                // press 2 times to exit app feature
                this.mIsExit = true;
                Toast.makeText(this, R.string.quit_confirm, Toast.LENGTH_SHORT).show();
                myHandler.postDelayed(myRunnable, 2000);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        if (!isTaskRoot()) {
            overridePendingTransition(R.anim.finish_activity_show, R.anim.finish_activity_hide);
        }
        super.onDestroy();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isPause = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;

        /*Global.isAppForeground = false;
        unregisterGCMReceiver();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPause = false;
//        if (isAppWentToBg && !(this instanceof LoginActivity)) {
//            isAppWentToBg = false;
//            if (!(this instanceof PinActivity) || ((PinActivity) this).typePIN != 4) {
//                String strPIN = new Prefs().getStringValue(Statics.KEY_PREFERENCES_PIN, "");
//                Intent intent;
//                if (!TextUtils.isEmpty(strPIN)) {
//                    intent = new Intent(this, PinActivity.class);
//                    intent.putExtra(Statics.KEY_INTENT_TYPE_PIN, Statics.TYPE_PIN_CONFIRM);
//                    startActivity(intent);
//                }
//            }
//        }
    }


    /**
     * For press 2 times to exit app
     */
    private Handler myHandler = new Handler();
    private Runnable myRunnable = new Runnable() {
        public void run() {
            mIsExit = false;
        }
    };

    /**
     * end here
     **/

    @Override
    protected void onStop() {
        myHandler.removeCallbacks(myRunnable);
        super.onStop();
        if (isPause) {
            isAppWentToBg = true;
            isPause = false;
        }
    }


    public void showProgressDialog() {
        if (null == mProgressDialog || !mProgressDialog.isShowing()) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(getString(R.string.loading_title));
            mProgressDialog.setMessage(getString(R.string.loading_content));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showNetworkDialog() {
        if (Util.isWifiEnable()) {
            AlertDialogView.normalAlertDialog(this, getString(R.string.app_name), getString(R.string.no_connection_error), getString(R.string.yes), null);
        } else {
            AlertDialogView.normalAlertDialogWithCancel(this, getString(R.string.app_name), getString(R.string.no_wifi_error), getString(R.string.yes), getString(R.string.no), new AlertDialogView.OnAlertDialogViewClickEvent() {
                @Override
                public void onOkClick(DialogInterface alertDialog) {
                    Intent wireLess = new Intent(
                            Settings.ACTION_WIFI_SETTINGS);
                    startActivity(wireLess);
                    alertDialog.dismiss();
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
    }

    public void callActivity(Class cls) {
        Intent newIntent = new Intent(this, cls);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(newIntent);
    }

    public void startNewActivity(Class cls, int count) {
        Intent newIntent = new Intent(this, cls);
        newIntent.putExtra("count_id", count);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (cls == ListEmailActivity.class) {
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        startActivity(newIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void startSingleActivity(Class cls) {
        Intent newIntent = new Intent(this, cls);
        newIntent.putExtra("count_id", 1);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    /*protected int getAppVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;

        }catch (Exception e)
        {
            return 0;
        }
    }

    // for GMC
    public String getGCMregistrationid() {
        if (prefs == null)
            prefs = new Prefs();
        return prefs.getGCMregistrationid();
    }

    public void setGMCid(String token) {
        if (prefs == null)
            prefs = new Prefs();
        prefs.setGCMregistrationid(token);
    }
    public int getappversion() {
        if (prefs == null)
            prefs = new Prefs();
        return prefs.getappversion();
    }

    public void setappversion(int token) {
        if (prefs == null)
            prefs = new Prefs();
        prefs.setappversion(token);
    }

    protected String getRegistrationId(Context context) {
        String registrationId = getGCMregistrationid();
        if (registrationId.isEmpty()) {
            Util.printLogs( "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = getappversion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Util.printLogs("App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerGCMReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Statics.ACTION_RECEIVER_NOTIFICATION);
        registerReceiver(mReceiverNewAssignTask, filter);
    }

    private void unregisterGCMReceiver() {
        unregisterReceiver(mReceiverNewAssignTask);
    }

    private BroadcastReceiver mReceiverNewAssignTask = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Statics.ACTION_RECEIVER_NOTIFICATION)) {
                String gcmDto = intent.getStringExtra(Statics.GCM_DATA_NOTIFICATOON);
            }
        }
    };

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }*/
}
