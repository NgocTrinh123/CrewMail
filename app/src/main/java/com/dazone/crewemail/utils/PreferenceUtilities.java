package com.dazone.crewemail.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dazone.crewemail.DaZoneApplication;

public class PreferenceUtilities {
    private SharedPreferences mPreferences;

    private final String KEY_CURRENT_SERVICE_DOMAIN = "currentServiceDomain";
    private final String KEY_CURRENT_COMPANY_DOMAIN = "currentCompanyDomain";
    private final String KEY_CURRENT_COMPANY_NO = "currentCompanyNo";
    private final String KEY_CURRENT_MOBILE_SESSION_ID = "currentMobileSessionId";
    private final String KEY_CURRENT_USER_ID = "currentUserID";
    private final String USER_ID = "user_id";
    private final String PASS = "password";
    private final String DOMAIN = "domain";
    private final String KEY_LOGIN = "key_login";

    public PreferenceUtilities() {
        mPreferences = DaZoneApplication.getInstance().getApplicationContext().getSharedPreferences("CrewBoard_Prefs", Context.MODE_PRIVATE);
    }

    public void setCurrentServiceDomain(String domain) {
        mPreferences.edit().putString(KEY_CURRENT_SERVICE_DOMAIN, domain).apply();
    }

    public String getCurrentServiceDomain() {
        return mPreferences.getString(KEY_CURRENT_SERVICE_DOMAIN, "");
    }

    public void setCurrentCompanyDomain(String domain) {
        mPreferences.edit().putString(KEY_CURRENT_COMPANY_DOMAIN, domain).apply();
    }

    public String getCurrentCompanyDomain() {
        return mPreferences.getString(KEY_CURRENT_COMPANY_DOMAIN, "");
    }

    public void setCurrentCompanyNo(int companyNo) {
        mPreferences.edit().putInt(KEY_CURRENT_COMPANY_NO, companyNo).apply();
    }

    public int getCurrentCompanyNo() {
        return mPreferences.getInt(KEY_CURRENT_COMPANY_NO, 0);
    }

    public void setCurrentMobileSessionId(String sessionId) {
        mPreferences.edit().putString(KEY_CURRENT_MOBILE_SESSION_ID, sessionId).apply();
    }

    public String getCurrentMobileSessionId() {
        return mPreferences.getString(KEY_CURRENT_MOBILE_SESSION_ID, "");
    }

    // ----------------------------------------------------------------------------------------------

    public void setCurrentUserID(String userID) {
        mPreferences.edit().putString(KEY_CURRENT_USER_ID, userID).apply();
    }

    public String getCurrentUserID() {
        return mPreferences.getString(KEY_CURRENT_USER_ID, "");
    }

    public void setUserId(String userId) {
        mPreferences.edit().putString(USER_ID, userId).apply();
    }

    public String getUserId() {
        return mPreferences.getString(USER_ID, "");
    }

    public void setPass(String pass) {
        mPreferences.edit().putString(PASS, pass).apply();
    }

    public String getPass() {
        return mPreferences.getString(PASS, "");
    }

    public void setDomain(String domain) {
        mPreferences.edit().putString(DOMAIN, domain).apply();
    }

    public String getDomain() {
        return mPreferences.getString(DOMAIN, "");
    }

    public void setCheckLogin(boolean isLogin) {
        mPreferences.edit().putBoolean(KEY_LOGIN, isLogin);
    }

    public boolean getCheckLogin() {
        return mPreferences.getBoolean(KEY_LOGIN, false);
    }
}