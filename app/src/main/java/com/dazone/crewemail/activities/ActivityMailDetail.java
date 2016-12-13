package com.dazone.crewemail.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.dazone.crewemail.R;
import com.dazone.crewemail.fragments.FragmentMailDetail;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;

import java.util.HashMap;
import java.util.List;

/**
 * Created by THANHTUNG on 21/12/2015.
 */
public class ActivityMailDetail extends ToolBarActivity {
    FragmentMailDetail fragmentMailDetail;
    long mailNo;
    String a;
    boolean isRead;
    private boolean isFromNotification;
    public static HashMap<String,String> files =  new HashMap<String, String> ();
    private String mailBoxNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayToolBarBackButton(true);
        setTitle("");
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        Bundle bundleq = getIntent().getExtras();
        if (bundleq != null) {
            try {
                mailNo = bundleq.getLong(StaticsBundle.BUNDLE_MAIL_NO);
            } catch (Exception e) {
                mailNo = 2505;
            }

            try {
                a = bundleq.getString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
            } catch (Exception e) {
                e.printStackTrace();
                a = "";
            }

            try {
                isRead = bundleq.getBoolean(StaticsBundle.PREFS_KEY_ISREAD);
            } catch (Exception e) {
                e.printStackTrace();
                isRead = false;
            }

            isFromNotification = bundleq.getBoolean(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION, false);
            if(isFromNotification) {
                mailBoxNo = bundleq.getString(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, "0");
                new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, Long.parseLong(mailBoxNo));
            }

        }
        if (bundle == null) {
            fragmentMailDetail = FragmentMailDetail.newInstance(mailNo, a, isRead);
            Util.addFragmentToActivity(getSupportFragmentManager(), fragmentMailDetail, mainContainer, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification) {
            Intent intent = new Intent(this, ListEmailActivity.class);
            intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            finish();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    if (isFromNotification) {
                        Intent intent = new Intent(this, ListEmailActivity.class);
                        intent.putExtra(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        finish();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
