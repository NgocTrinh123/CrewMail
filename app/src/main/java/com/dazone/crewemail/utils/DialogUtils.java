package com.dazone.crewemail.utils;

import android.content.Context;

import com.dazone.crewemail.dialog.MessageDialog;

/**
 * Created by tunglam on 12/14/16.
 */

public class DialogUtils {
    public static void showDialogWithMessageButton(Context context, String message, String positiveButton, String nagetiveButton, MessageDialog.OnCloseDialog onCloseDialog) {
        MessageDialog messageDialog = new MessageDialog(context);
        messageDialog.setMessage(message)
                .setPositiveButton(positiveButton)
                .setNegativeButton(nagetiveButton)
                .setOnCloseDialogListener(onCloseDialog)
                .show();
    }

}
