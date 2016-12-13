package com.dazone.crewemail.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ActivityMailDetail;
import com.dazone.crewemail.adapter.TagAdapter;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.MailBoxMenuData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.MenuSortData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.UserDBHelper;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by THANHTUNG on 18/12/2015.
 */
public class MailHelper {

    //1: Image
    //2: Video
    //3: Audio
    //4: DOC
    //5: XLS
    //6: PDF
    //7: PPT
    //8: File
    public static int getTypeFile(AttachData attachData) {
        int type;
        if (attachData != null && !TextUtils.isEmpty(attachData.getFileType())) {
            switch (attachData.getFileType()) {
                case Statics.IMAGE_GIF:
                case Statics.IMAGE_JPEG:
                case Statics.IMAGE_JPG:
                case Statics.IMAGE_PNG:
                    type = 1;
                    break;
                case Statics.VIDEO_MP4:
                    type = 2;
                    break;
                case Statics.AUDIO_MP3:
                case Statics.AUDIO_AMR:
                case Statics.AUDIO_WMA:
                    type = 3;
                    break;
                case Statics.FILE_DOC:
                case Statics.FILE_DOCX:
                    type = 4;
                    break;
                case Statics.FILE_XLS:
                case Statics.FILE_XLSX:
                    type = 5;
                    break;
                case Statics.FILE_PDF:
                    type = 6;
                    break;
                case Statics.FILE_PPT:
                case Statics.FILE_PPTX:
                    type = 7;
                    break;
                default:
                    type = 8;
                    break;
            }
        } else {
            return 8;
        }
        return type;
    }

    //1: Image
    //2: Video
    //3: Audio
    //4: DOC
    //5: XLS
    //6: PDF
    //7: PPT
    //8: zip
    //9: rar
    //10: apk
    //11: default
    public static int getTypeFile(String typeFile) {
        int type = 0;
        switch (typeFile) {
            case Statics.IMAGE_GIF:
            case Statics.IMAGE_JPEG:
            case Statics.IMAGE_JPG:
            case Statics.IMAGE_PNG:
                type = 1;
                break;
            case Statics.VIDEO_MP4:
                type = 2;
                break;
            case Statics.AUDIO_MP3:
            case Statics.AUDIO_AMR:
            case Statics.AUDIO_WMA:
                type = 3;
                break;
            case Statics.FILE_DOC:
            case Statics.FILE_DOCX:
                type = 4;
                break;
            case Statics.FILE_XLS:
            case Statics.FILE_XLSX:
                type = 5;
                break;
            case Statics.FILE_PDF:
                type = 6;
                break;
            case Statics.FILE_PPT:
            case Statics.FILE_PPTX:
                type = 7;
                break;
            case Statics.FILE_ZIP:
                type = 8;
                break;
            case Statics.FILE_RAR:
                type = 9;
                break;
            case Statics.FILE_APK:
                type = 10;
                break;
            default:
                type = 11;
                break;
        }
        return type;
    }

    public static void OpenFile(Context context, AttachData attachment) {
        if (attachment != null && !TextUtils.isEmpty(attachment.getPath())) {
            int Type = getTypeFile(attachment);
            String MimeType;
            File file = new File(attachment.getPath());
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                switch (Type) {
                    case 1:
                        MimeType = Statics.MIME_TYPE_IMAGE;
                        break;
                    case 2:
                        MimeType = Statics.MIME_TYPE_VIDEO;
                        break;
                    case 3:
                        MimeType = Statics.MIME_TYPE_AUDIO;
                        break;
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        MimeType = Statics.MIME_TYPE_APP;
                        break;
                    default:
                        MimeType = Statics.MIME_TYPE_APP;
                        break;
                }
                intent3.setDataAndType(path, MimeType);

                try {
                    context.startActivity(intent3);
                } catch (ActivityNotFoundException e) {
                }
            }
        }
    }

    public static void OpenAllFile(Context context, String uri) {
        if (!TextUtils.isEmpty(uri)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(uri), Statics.MIME_TYPE_APP);
            context.startActivity(intent);
        }
    }

    public static void LoadFile(Fragment fragment) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent intent1 = new Intent();
                intent1.setType(Statics.MIME_TYPE_APP);
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                fragment.startActivityForResult(intent1, Statics.FILE_PICKER_SELECT);
                fragment.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.setType(Statics.MIME_TYPE_APP);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                fragment.startActivityForResult(intent2, Statics.FILE_PICKER_SELECT);
                fragment.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DownloadAttach(Context context, String url) {
        String serviceString = Context.DOWNLOAD_SERVICE;
        final DownloadManager downloadmanager;
        downloadmanager = (DownloadManager) context.getSystemService(serviceString);
        Uri uri = Uri
                .parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final Long reference = downloadmanager.enqueue(request);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor c = downloadmanager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                        }
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //isTask 0: Thu gọn
    // 1: Chi tiết
    public static String getContentAddressMail(List<PersonData> personDataList, int isTask) {
        String mail = "";
        if (isTask == 0) {
            for (PersonData personData : personDataList) {
                if (TextUtils.isEmpty(personData.getFullName())) {
                    mail += personData.getEmail() + "\n";
                } else {
                    mail += personData.getFullName() + " <" + personData.getEmail() + ">\n";
                }
            }
        } else {
            for (int i = 0; i < personDataList.size(); i++) {
                PersonData personData = personDataList.get(0);
                if (TextUtils.isEmpty(personData.getFullName())) {
                    mail = personData.getEmail() + "\n";
                } else {
                    mail = personData.getFullName() + " <" + personData.getEmail() + ">\n";
                }
            }
        }
        mail = mail.substring(0, mail.length() - 1);
        return mail;
    }

    public static void setEmailAddress(List<PersonData> personDataList, TextView txtEmail, int isTask, LinearLayout linearLayout) {
        String emailString = "";
        if (personDataList != null && personDataList.size() > 0) {
            emailString = getContentAddressMail(personDataList, isTask);
            txtEmail.setText(emailString);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public static void setEmailTextViewDetail(PersonData personData, TextView textView) {
        if (personData != null) {
            textView.setText(personData.getFullName() + " <" + personData.getEmail() + ">");
        }
    }

    public static void setSettingWebView(WebView mWebView) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setInitialScale(1);
        mWebView.setFocusable(true);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    public static void displayDownloadFileDialog(final Context context, final String url, final String name) {

        AlertDialogView.normalAlertDialogWithCancel(context, context.getString(R.string.app_name), context.getString(R.string.string_alert_download_mail), context.getString(R.string.string_title_mail_create_ok), context.getString(R.string.string_title_mail_create_cancel), new AlertDialogView.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {
                String mimeType;
                String serviceString = Context.DOWNLOAD_SERVICE;
                String fileType = name.substring(name.lastIndexOf(".")).toLowerCase();
                final DownloadManager downloadmanager;
                downloadmanager = (DownloadManager) context.getSystemService(serviceString);
                Uri uri = Uri
                        .parse(url);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Statics.pathDownload, name);
                //request.setTitle(name);
                int type = getTypeFile(fileType);
                switch (type) {
                    case 1:
                        request.setMimeType(Statics.MIME_TYPE_IMAGE);
                        break;
                    case 2:
                        request.setMimeType(Statics.MIME_TYPE_VIDEO);
                        break;
                    case 3:
                        request.setMimeType(Statics.MIME_TYPE_AUDIO);
                        break;
                    default:
                        try {
                            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
                        } catch (Exception e) {
                            e.printStackTrace();
                            mimeType = Statics.MIME_TYPE_ALL;
                        }
                        if (TextUtils.isEmpty(mimeType)) {
                            request.setMimeType(Statics.MIME_TYPE_ALL);
                        } else {
                            request.setMimeType(mimeType);
                        }
                        break;
                }
                final Long reference = downloadmanager.enqueue(request);

                BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            long downloadId = intent.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(reference);
                            Cursor c = downloadmanager.query(query);
                            if (c.moveToFirst()) {
                                int columnIndex = c
                                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                if (DownloadManager.STATUS_SUCCESSFUL == c
                                        .getInt(columnIndex)) {
                                    String filepath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                                    File file = new File(filepath);
                                    MimeTypeMap map = MimeTypeMap.getSingleton();
                                    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                                    String type = map.getMimeTypeFromExtension(ext);

                                    if (type == null)
                                        type = "*/*";
                                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                                    Uri data = Uri.fromFile(file);
                                    intent2.setDataAndType(data, type);
                                    context.startActivity(intent2);
                                    ActivityMailDetail.files.put(url,filepath);
                                }
                            }
                        }
                    }
                };
                context.registerReceiver(receiver, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }

            @Override
            public void onCancelClick() {

            }
        });
    }

    public static String getUrl(AttachData attachData) {
        String url = DaZoneApplication.getInstance().getPrefs().getServerSite() + Urls.URL_DOWNLOAD_ATTACH + "sessionId=" + new Prefs().getAccessToken() + "&languageCode=" + Locale.getDefault().getLanguage().toUpperCase() +
                "&timeZoneOffset=" + TimeUtils.getTimezoneOffsetInMinutes() + "&fileNo=" + attachData.getAttachNo();
        Util.printLogs("Link tải nè: " + url);
        return url;
    }

    public static String GetPriority(String priority) {
        String mPriority = "x";
        if (!TextUtils.isEmpty(priority)) {
            if (priority.equalsIgnoreCase(Util.getString(R.string.priority_none)))
                mPriority = "x";
            if (priority.equalsIgnoreCase(Util.getString(R.string.priority_high)))
                mPriority = "1";
            if (priority.equalsIgnoreCase(Util.getString(R.string.priority_normal)))
                mPriority = "3";
            if (priority.equalsIgnoreCase(Util.getString(R.string.priority_low)))
                mPriority = "5";
        } else {
            mPriority = "x";
        }
        return mPriority;
    }

    public static String getOriginalMessage(Context context, MailBoxData data) {
        String message = "\n--------- Original Message ---------\n";
        if (data != null) {
            message += Util.getString(R.string.string_title_mail_create_from) + ": " + data.getMailFrom().getFullName() + "<" + data.getMailFrom().getEmail() + ">\n";
            if (data.getListPersonDataTo() != null && data.getListPersonDataTo().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_to) + ": ";
                for (PersonData personData : data.getListPersonDataTo())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }

            if (data.getListPersonDataCc() != null && data.getListPersonDataCc().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_cc) + ": ";
                for (PersonData personData : data.getListPersonDataCc())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }

            if (data.getListPersonDataBcc() != null && data.getListPersonDataBcc().size() > 0) {
                message += Util.getString(R.string.string_title_mail_create_bcc) + ": ";
                for (PersonData personData : data.getListPersonDataBcc())
                    message += personData.getEmail() + ", ";
                message += "\n";
            }
            if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO"))
                message += Util.getString(R.string.string_title_mail_send) + ": " + TimeUtils.displayTimeWithoutOffset(context, data.getDateCreate(), 1) + "\n";
            else
                message += Util.getString(R.string.string_title_mail_send) + ": " + TimeUtils.displayTimeWithoutOffset(context, data.getDateCreate(), 0) + "\n";
            message += Util.getString(R.string.string_title_mail_create_subject) + ": " + data.getSubject() + "\n";
        } else {
            message = "";
        }
        return message;
    }

    public static String getAddType(PersonData personData) {
        if (personData.getType() == 1) {
            return "depart";
        } else if (personData.getType() == 2) {
            return "user";
        } else {
            return "mail";
        }
    }

    public static ArrayList<PersonData> removeMe(ArrayList<PersonData> listTo, ArrayList<AccountData> listUser) {
        ArrayList<PersonData> list = new ArrayList<>();
        try {
            if (listTo != null && listTo.size() > 0) {
                for (PersonData personData : listTo) {
                    boolean isCheck = false;
                    for (AccountData accountData : listUser) {
                        if (accountData.getMailAddress().equalsIgnoreCase(personData.getEmail())) {
                            isCheck = true;
                            break;
                        } else {
                            isCheck = false;
                        }
                    }
                    if (isCheck == false) {
                        list.add(personData);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public static ArrayList<PersonData> removeMe(ArrayList<PersonData> listTo) {
        UserData userData = UserDBHelper.getUser();
        ArrayList<PersonData> list = new ArrayList<>();
        try {
            if (listTo != null && listTo.size() > 0) {
                for (PersonData personData : listTo) {
                    boolean isCheck;
                    isCheck = userData.getmEmail().equalsIgnoreCase(personData.getEmail());
                    if (isCheck == false) {
                        list.add(personData);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z0-9._-]+)+");
        return (emailPattern.matcher(email).matches());
    }

    public static void displaySingleChoice(final Context context, List<MailTagMenuData> itemList, DialogInterface.OnClickListener listener, String title) {
        List<String> list = new ArrayList<>();
        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        for (MailTagMenuData mailTagMenuData : itemList) {
            if (mailTagMenuData.getTagNo() == -1) {
                list.add(mailTagMenuData.getName());
            } else {
                list.add("Tag: " + mailTagMenuData.getName());
            }
        }
        ArrayAdapter<String> adapterspiner = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, list);
        adb.setAdapter(adapterspiner, listener);
        adb.show();
    }

    public static void displaySingleChoiceList(final Activity context, List<MailTagMenuData> itemList, DialogInterface.OnClickListener listener, String title) {

        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        TagAdapter adapter = new TagAdapter(context, R.layout.tag, itemList);
        adb.setAdapter(adapter, listener);
        adb.show();
    }

    public static void displaySingleChoiceListString(final Activity context, List<String> itemList, DialogInterface.OnClickListener listener, String title) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context).setTitle(title);
        ArrayAdapter<String> adapterspiner = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, itemList);
        adb.setAdapter(adapterspiner, listener);
        adb.show();
    }

    /**
     * Convert Json String to LinkedHashMap
     *
     * @param jsonString jsonString
     * @return map menu data
     */
    public static LinkedHashMap<String, Object> convertJsonStringToMap(String jsonString) {
        LinkedHashMap<String, Object> menuMap = new LinkedHashMap<>();
        try {
            Gson gson = new Gson();
            JSONObject jo = new JSONObject(jsonString);
            // tag mail box
            String mailTag = jo.getString("MailTags");
            Type listTagType = new TypeToken<List<MailTagMenuData>>() {
            }.getType();
            List<MailTagMenuData> tagList = gson.fromJson(mailTag, listTagType);
            menuMap.put("MailTags", tagList);
            // other mail box
            JSONObject mailBoxJo = jo.getJSONObject("MailBoxs");
            Iterator<String> keySets = mailBoxJo.keys();
            while (keySets.hasNext()) {
                String key = keySets.next();
                String value = mailBoxJo.getString(key);
                Type listType = new TypeToken<List<MailBoxMenuData>>() {
                }.getType();
                List<MailBoxMenuData> listMailBox = gson.fromJson(value, listType);
                menuMap.put(key, listMailBox);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        return menuMap;
    }

    public static int getColorTag(int ImageNo) {
        switch (ImageNo) {
            case 0:
                return R.drawable.tag_icon_01;
            case 1:
                return R.drawable.tag_icon_02;
            case 2:
                return R.drawable.tag_icon_03;
            case 3:
                return R.drawable.tag_icon_04;
            case 4:
                return R.drawable.tag_icon_05;
            case 5:
                return R.drawable.tag_icon_06;
            case 6:
                return R.drawable.tag_icon_07;
            case 7:
                return R.drawable.tag_icon_08;
            case 8:
                return R.drawable.tag_icon_09;
            case 9:
                return R.drawable.tag_icon_10;
            case 10:
                return R.drawable.tag_icon_11;
            case 11:
                return R.drawable.tag_icon_12;
            case 12:
                return R.drawable.tag_icon_13;
            case 13:
                return R.drawable.tag_icon_14;
            case 14:
                return R.drawable.tag_icon_15;
            case 15:
                return R.drawable.tag_icon_16;
            case 16:
                return R.drawable.tag_icon_17;
            case 17:
                return R.drawable.tag_icon_18;
            case 18:
                return R.drawable.tag_icon_19;
            case 19:
                return R.drawable.tag_icon_20;
            case 20:
                return R.drawable.tag_icon_21;
        }
        return 0;
    }


    //isTask : 1 - get index of Inbox
    //: 0 - get index of OutBox
    public static int getIndexOfMenu(List<MailBoxMenuData> menuList, int isTask) {
        int size = 0;
        if (menuList != null && menuList.size() > 0) {
            size = menuList.size() - 1;
            for (int i = 0; i < menuList.size(); i++) {
                MailBoxMenuData mailBoxMenuData = menuList.get(i);
                if (isTask == 1) {
                    if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_sortbox))) {
                        return i;
                    }
                } else {
                    if (mailBoxMenuData.getName().equalsIgnoreCase(Util.getString(R.string.string_title_menu_outbox))) {
                        return i;
                    }
                }
            }
        }
        return size;
    }

    public static int getSelectPosition(ArrayList<MenuSortData> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            MenuSortData menuSortData = list.get(i);
            if (menuSortData.getIsCheck() == 1 || menuSortData.getIsCheck() == 2) {
                index = i;
            }
        }
        return index;
    }

    public static int CheckEmail(ArrayList<PersonData> list) {
        int temp = 0;
        for (PersonData personData : list) {
            if (personData.getAddrType().equalsIgnoreCase("mail")) {
                String mail = "";
                if (TextUtils.isEmpty(personData.getEmail())) {
                    mail = personData.getmEmail();
                } else {
                    mail = personData.getEmail();
                }
                if (!MailHelper.isValidEmail(mail)) {
                    temp++;
                }
            }
        }
        return temp;
    }
}
