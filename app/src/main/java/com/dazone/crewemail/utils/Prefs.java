package com.dazone.crewemail.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dazone.crewemail.DaZoneApplication;

public class Prefs {
    private SharedPreferences prefs;
    private final String SHARED_PREFERENCES_NAME = "crew_mail_pref";
    private final String ACCESS_TOKEN = "accesstoken";
    private final String USER_JSON_INFO = "user_json";
    private final String SERVER_SITE = "serversite";
    private final String ACCESSTOKEN = "accesstoken";
    private final String MAIL_MENU_LIST = "mail_menu_list";
    private static final String PREF_FLAG_GMC_ID = "flag_gmc_id";

    public Prefs() {
        prefs = DaZoneApplication.getInstance().getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void putBooleanValue(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void clearLogin() {
        prefs.edit().remove(ACCESS_TOKEN).apply();
    }

    public String getAccessToken() {
        return prefs.getString(ACCESS_TOKEN, "");
    }

    public String getServerSite() {
        return prefs.getString(SERVER_SITE, "");
    }

    public void putServerSite(String serverSite) {
        prefs.edit().putString(SERVER_SITE, serverSite).apply();
    }

    public void putUserData(String userDataJson, String accessToken) {
        prefs.edit().putString(ACCESS_TOKEN, accessToken).apply();
        prefs.edit().putString(USER_JSON_INFO, userDataJson).apply();
    }

    public String getUserJson() {
        return prefs.getString(USER_JSON_INFO, "");
    }

    public void removeUserData() {
        prefs.edit().remove(ACCESS_TOKEN).apply();
        prefs.edit().remove(USER_JSON_INFO).apply();
    }

    public void putMenuListData(String menuListJson) {
        prefs.edit().putString(MAIL_MENU_LIST, menuListJson).apply();
    }

    public void removeMenuData() {
        prefs.edit().remove(MAIL_MENU_LIST).apply();
    }

    public void removeSetting() {
        prefs.edit().remove(Statics.KEY_PREFERENCES_PIN).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_NEW_MAIL).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_SOUND).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_VIBRATE).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_TIME).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME).apply();
        prefs.edit().remove(Statics.KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME).apply();
    }

    public String getMenuListData() {
        return prefs.getString(MAIL_MENU_LIST, null);
    }

    public String getStringValue(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void putStringValue(String KEY, String value) {
        prefs.edit().putString(KEY, value).apply();
    }

    public String getaccesstoken() {
        return getStringValue(ACCESSTOKEN, "");
    }

    public String getGCMregistrationid() {
        return getStringValue(PREF_FLAG_GMC_ID, "");
    }

    public void setGCMregistrationid(String value) {
        putStringValue(PREF_FLAG_GMC_ID, value);
    }

    public void putLongValue(String KEY, long value) {
        prefs.edit().putLong(KEY, value).apply();
    }

    public long getLongValue(String KEY, long defvalue) {
        return prefs.getLong(KEY, defvalue);
    }
}
