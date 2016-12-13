package com.dazone.crewemail.interfaces;


import android.support.v4.app.Fragment;

/**
 * Created by Sherry on 10/19/15.
 */
public interface OnFragmentEventListener {
    void onFragmentReplace(Fragment currentFm, int toId, boolean isAddBackStack, Object... param);
    void onFragmentStartActivity(Fragment currentFm, int toId);
}
