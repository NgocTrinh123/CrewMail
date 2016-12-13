package com.dazone.crewemail.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.customviews.MailMenuView;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.fragments.ListEmailFragment;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

public class ListEmailActivity extends ToolBarActivity implements MailMenuView.OnMenuItemClickListener {

    private ListEmailFragment fm;
    private int mMainContainer = 0;
    private MailMenuView mailMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayFloatingButton(true);
        // displayAsFolder mail menu and set onClick listener
        mailMenuView = displayNavigationBar(this);
        HttpRequest.getInstance().CheckAccount(new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                AlertDialogView.normalAlertDialogNotBack(ListEmailActivity.this, Util.getString(R.string.app_name), Util.getString(R.string.string_no_id), Util.getString(R.string.string_title_mail_create_ok),
                        Util.getString(R.string.logout), new AlertDialogView.OnAlertDialogViewClickEvent() {
                            @Override
                            public void onOkClick(DialogInterface alertDialog) {
                                finish();
                            }

                            @Override
                            public void onCancelClick() {
                                HttpRequest.getInstance().Logout(new BaseHTTPCallBack() {
                                    @Override
                                    public void onHTTPSuccess() {
                                        UserData.getInstance().logout();
                                        Intent intent = new Intent(ListEmailActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }

                                    @Override
                                    public void onHTTPFail(ErrorData errorDto) {
                                        Util.showMessageShort(errorDto.getMessage());
                                        UserData.getInstance().logout();
                                        Intent intent = new Intent(ListEmailActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                });

                            }
                        });
            }
        });
    }

    public void reloadMenu(int isTask) {
        mailMenuView.reloadMenu(isTask);
    }

    @Override
    protected void addFragment(Bundle bundle, int mainContainer) {
        if (bundle == null) {
            mMainContainer = mainContainer;

            fm = ListEmailFragment.newInstance(0, "", null, 0);
            Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
        } else {
            int mailBoxNo = Integer.parseInt(bundle.getString(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, "0"));
            fm = ListEmailFragment.newInstance(mailBoxNo, "", null, 0);
            try {
                Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public void onMenuItemClick(Object object) {
        if (mMainContainer != 0) {
            if (object instanceof MailBoxMenuData) {
                MailBoxMenuData menuData = (MailBoxMenuData) object;
                setToolBarTitle(menuData.getName());
                fm = ListEmailFragment.newInstance(menuData.getBoxNo(), menuData.getName(), menuData.getClassName(), EmailBoxStatics.NORMAL_MAIL_BOX);
                Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
            } else if (object instanceof MailTagMenuData) {
                MailTagMenuData menuData = (MailTagMenuData) object;
                setToolBarTitle(menuData.getName());
                fm = ListEmailFragment.newInstance(menuData.getTagNo(), menuData.getName(), null, EmailBoxStatics.TAG_MAIL_BOX);
                Util.replaceFragment(getSupportFragmentManager(), fm, mMainContainer, false);
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        Prefs prefs = new Prefs();
        if (prefs.getBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, false)) {
            prefs.putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, false);
            if (fm != null)
                fm.RefeshData(true);
        }
    }

    public void setFab(boolean task) {
        displayFloatingButton(task);
    }
}
