package com.dazone.crewemail.activities;

import android.os.Bundle;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.fragments.SearchFragment;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;

/**
 * Created by THANHTUNG on 12/01/2016.
 */
public class SearchActivity extends ToolBarActivity {
    private SearchFragment fm;
    private int mMainContainer = 0;
    private int mMailBoxNo;
    private int mMailType; // 0 normal mail box , 1 : tag mail box
    private String mMailBoxClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolBarBackButton();
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        Bundle bundleq = getIntent().getExtras();
        if (bundleq != null) {
            mMailBoxNo = bundleq.getInt("mailBoxNo");
            mMailType = bundleq.getInt("mailType");
            mMailBoxClassName = bundleq.getString("emailBoxClassName");
        }
        if (bundle == null) {
            mMainContainer = mainContainer;
            fm = SearchFragment.newInstance(mMailBoxNo, mMailBoxClassName, mMailType);
            Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (fm != null) {
            if (!fm.releaseAllSelectedItem()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        DaZoneApplication.getInstance().cancelPendingRequests(Urls.URL_GET_EMAIL_LIST);
        super.onDestroy();

    }

}
