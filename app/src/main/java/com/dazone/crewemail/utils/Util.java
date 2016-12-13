package com.dazone.crewemail.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dazone.crewemail.DaZoneApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Util {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) DaZoneApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static boolean isWifiEnable() {
        WifiManager wifi = (WifiManager) DaZoneApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) DaZoneApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isWifiEnabled() && mWifi.isConnected();
    }

    public static void printLogs(String logs) {
        if (Statics.ENABLE_DEBUG) {
            int maxLogSize = 5000;
            if (logs.length() > maxLogSize) {
                for (int i = 0; i <= logs.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > logs.length() ? logs.length() : end;
                    Log.d(Statics.LOG_TAG, logs.substring(start, end));
                }
            } else {
                Log.d(Statics.LOG_TAG, logs);
            }
        }

    }

    public static String getString(int stringID) {
        return DaZoneApplication.getInstance().getApplicationContext().getResources().getString(stringID);
    }

    public static Resources getResources() {
        return DaZoneApplication.getInstance().getApplicationContext().getResources();
    }

    public static boolean checkStringValue(String... params) {
        for (String param : params) {
            if (param != null) {
                if (TextUtils.isEmpty(param.trim())) {
                    return false;
                }
                if (param.contains("\n") && TextUtils.isEmpty(param.replace("\n", ""))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public static int getDimenInPx(int id) {
        return (int) DaZoneApplication.getInstance().getApplicationContext().getResources().getDimension(id);
    }

    public static String getUniqueDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
       /*
        * getDeviceId() function Returns the unique device ID.
        * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
        */
        String deviceId = telephonyManager.getDeviceId();


       /*
        * getSubscriberId() function Returns the unique subscriber ID,
        * for example, the IMSI for a GSM phone.
        */
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = telephonyManager.getSubscriberId();
        }

        /*
        * Settings.Secure.ANDROID_ID returns the unique DeviceID
        * Works for Android 2.2 and above
        */
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        /*
        * returns the MacAddress
        */
        if (TextUtils.isEmpty(deviceId)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            deviceId = wInfo.getMacAddress();
        }

        return deviceId;

    }

    public static String parseMili2Date(long milisec, String pattern) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(milisec);
        return simpleDateFormat.format(date);
    }

    public static String getString(String note) {
        if (TextUtils.isEmpty(note)) {
            return "";
        } else {
            return note.replace("\n", " ");
        }
    }

    public static String getPathFromURI(Uri contentURI, Context context) {
        String result;
        try {
            Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;
    }

    //Get filename from URI
    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static long getFileSize(String realPath) {
        long sizeOfInputStram = 0;
        try {
            InputStream is = new FileInputStream(realPath);
            sizeOfInputStram = is.available();
        } catch (Exception e) {
            sizeOfInputStram = 0;
        }
        return sizeOfInputStram;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameLayout, fragment);

        if (isSaveStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment, int frameLayout, boolean isSaveStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameLayout, fragment);

        if (isSaveStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public static void showMessage(String message) {
        Toast.makeText(DaZoneApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public static void showMessageShort(String message) {
        Toast.makeText(DaZoneApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isPhoneLanguageEN() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("EN");
    }

    public static String getPhoneLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static long getTimeOffsetInMinute() {
        return TimeUnit.MINUTES.convert(getTimeOffsetInMilis(), TimeUnit.MILLISECONDS);
    }

    public static long getTimeOffsetInMilis() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();

        return mTimeZone.getRawOffset();
    }

    public static String getLanguageCode() {
        Context context = DaZoneApplication.getInstance().getBaseContext();
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        switch (language) {
            case "ko":
                return "KO";
            case "vi":
                return "VN";
            default:
                return "EN";
        }
    }

    public static int getTimeZoneOffset() {
        return TimeZone.getDefault().getRawOffset() / 1000 / 60;
    }
}
