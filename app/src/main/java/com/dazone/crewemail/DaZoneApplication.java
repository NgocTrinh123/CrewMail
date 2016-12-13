package com.dazone.crewemail;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.dazone.crewemail.customviews.FontSizes;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DaZoneApplication extends Application {
    private static final String TAG = "DaZoneApplication";
    private static DaZoneApplication sInstance;
    private RequestQueue mRequestQueue;
    private static Prefs mPrefs;
    private static PreferenceUtilities preferenceUtilities;
    private static boolean mMessageViewFixedWidthFont = false;
    private static boolean mAutofitWidth;
    private static final FontSizes fontSizes = new FontSizes();
    public ImageLoader imageLoader = ImageLoader.getInstance();

    private static boolean activityRunning;

    public static boolean isActivityRunning() {
        return activityRunning;
    }

    public static void activityResumed() {
        activityRunning = true;
    }

    public static void activityPaused() {
        activityRunning = false;
    }

    @Override
    public void onCreate() {
//        Fresco.initialize(this);
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        sInstance = this;
        Fresco.initialize(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();
        imageLoader.init(config);

    }

    public DaZoneApplication() {
        super();

    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized DaZoneApplication getInstance() {
        return sInstance;
    }

    public synchronized Prefs getPrefs() {
        if (mPrefs == null) {
            mPrefs = new Prefs();
        }
        return mPrefs;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(Statics.REQUEST_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(Statics.REQUEST_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void clearCache() {
        getRequestQueue().getCache().clear();
    }

    public static boolean messageViewFixedWidthFont() {
        return mMessageViewFixedWidthFont;
    }

    public static void setMessageViewFixedWidthFont(boolean fixed) {
        mMessageViewFixedWidthFont = fixed;
    }

    public static boolean autofitWidth() {
        return mAutofitWidth;
    }

    public static void setAutofitWidth(boolean autofitWidth) {
        mAutofitWidth = autofitWidth;
    }

    public static FontSizes getFontSizes() {
        return fontSizes;
    }

    public synchronized PreferenceUtilities getPreferenceUtilities() {
        if (preferenceUtilities == null) {
            preferenceUtilities = new PreferenceUtilities();
        }

        return preferenceUtilities;
    }
}