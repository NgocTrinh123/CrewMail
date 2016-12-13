package com.dazone.crewemail.webservices;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.dto.BelongToDepartmentDTO;
import com.dazone.crewemail.dto.ErrorDto;
import com.dazone.crewemail.dto.MessageDto;
import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.data.MailProfileData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.ReadDateData;
import com.dazone.crewemail.data.ReceiveData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.AccountUserDBHelper;
import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.database.ServerSiteDBHelper;
import com.dazone.crewemail.database.UserDBHelper;
import com.dazone.crewemail.helper.MailHelper;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.BaseHTTPCallBackWithString;
import com.dazone.crewemail.interfaces.OnAutoLoginCallBack;
import com.dazone.crewemail.interfaces.OnCheckDevice;
import com.dazone.crewemail.interfaces.OnGetAllOfUser;
import com.dazone.crewemail.interfaces.OnGetInfoUser;
import com.dazone.crewemail.interfaces.OnGetListOfMailAccount;
import com.dazone.crewemail.interfaces.OnGetReadDate;
import com.dazone.crewemail.interfaces.OnGetStringCallBack;
import com.dazone.crewemail.interfaces.OnMailDetailCallBack;
import com.dazone.crewemail.interfaces.OnMailListCallBack;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private static HttpRequest sInstance;
    public static String sRootLink;
    private static Context sContext;
    public static boolean mEnableTestServer = false;

    public static HttpRequest getInstance() {
        if (null == sInstance) {
            sInstance = new HttpRequest();
        }
        if (mEnableTestServer)
            sRootLink = "http://test1.suziptong.com";
        else
            sRootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();
        sContext = DaZoneApplication.getInstance();
        return sInstance;
    }

    public void signUp(final BaseHTTPCallBackWithString baseHTTPCallBack, final String email) {
        final String url = Urls.URL_SIGN_UP;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("mailAddress", "" + email);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                MessageDto messageDto = gson.fromJson(response, MessageDto.class);

                if (baseHTTPCallBack != null && messageDto != null) {
                    String message = messageDto.getMessage();
                    baseHTTPCallBack.onHTTPSuccess(message);
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPFail(error);
                }
            }
        });
    }

    public void login(String userID, String password, String companyDomain, String server_link, final Prefs prefs, final BaseHTTPCallBack baseHTTPCallBack) {
        final String url = server_link + Urls.URL_GET_LOGIN;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", "" + TimeUtils.getTimezoneOffsetInMinutes());
        params.put("companyDomain", companyDomain);
        params.put("password", password);
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
/*                JSONObject jo = new JSONObject(response);
                String mailDataString = jo.getString("d");*/
                    Gson gson = new Gson();
                    UserData userDto = gson.fromJson(response, UserData.class);
                    if (userDto != null) {
                        if (prefs != null)
                            prefs.putUserData(response, userDto.getSession());
                        UserDBHelper.addUser(userDto);
                        baseHTTPCallBack.onHTTPSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void Logout(final BaseHTTPCallBack baseHTTPCallBack) {
        String url = sRootLink + Urls.URL_DELETE_DEVICE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + DaZoneApplication.getInstance().getPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void checkLogin(final Prefs prefs, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = sRootLink + Urls.URL_CHECK_SESSION;
        if (mEnableTestServer) {
            url = DaZoneApplication.getInstance().getPrefs().getServerSite() + Urls.URL_CHECK_SESSION;
        }
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + DaZoneApplication.getInstance().getPrefs().getAccessToken());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserData userDto = gson.fromJson(response, UserData.class);
                if (userDto != null) {
                    if (prefs != null)
                        prefs.putUserData(response, userDto.getSession());
                }
                UserDBHelper.addUser(userDto);
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPSuccess();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPFail(error);
                }
            }
        });
    }

    public void AutoLogin(String companyDomain, String userID, String server_link, final Prefs prefs,  final OnAutoLoginCallBack callBack) {
        String url = server_link + Urls.URL_AUTO_LOGIN;

        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Util.getTimeOffsetInMinute());
        params.put("companyDomain", companyDomain);
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);

        new WebServiceManager().doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserData userDto = gson.fromJson(response, UserData.class);
                if (userDto != null) {
                    if (prefs != null)
                        prefs.putUserData(response, userDto.getSession());
                    UserDBHelper.addUser(userDto);
                    callBack.OnAutoLoginSuccess(response);
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                callBack.OnAutoLoginFail(error);
            }
        });
    }

    // -----------------------------------------------------------------------------------------------------------------------------------

    /**
     * Insert DEVICE
     */
    public void InsertDevice(String deviceId, final BaseHTTPCallBack baseHTTPCallBack) {
        String url = sRootLink + Urls.URL_INSERT_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + DaZoneApplication.getInstance().getPrefs().getaccesstoken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("deviceID", deviceId);
        params.put("osVersion", android.os.Build.VERSION.RELEASE);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (baseHTTPCallBack != null)
                    baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void getEmailSignUp(final OnGetStringCallBack callBack, String send_string) {
        String url = sRootLink + Urls.URL_GET_EMAIL_SIGN_UP;
        Map<String, String> params = new HashMap<>();
        params.put("TextConvert", send_string);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onGetStringSuccess(response);
            }

            @Override
            public void onFailure(ErrorData error) {

                callBack.onGetStringFail(error);
            }
        });
    }

    /*********************
     * EMAIL PART
     ******************/
    public void getEmailMenuList(final OnMenuListCallBack callback) {
        String url = sRootLink + Urls.URL_GET_EMAIL_MENU_LIST;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetAllOfMailBoxs";
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                /*JSONObject jo = new JSONObject(response);
                String mailDataString = jo.getString("d");*/
                    Prefs prefs = DaZoneApplication.getInstance().getPrefs();
                    prefs.putMenuListData(DaZoneApplication.getInstance().getPrefs().getAccessToken() + "#@#" + response);
                    LinkedHashMap<String, Object> menuMap = MailHelper.convertJsonStringToMap(response);
                    if (callback != null)
                        callback.onMenuListSuccess(menuMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null)
                    callback.onMenuListFail(error);
            }
        });
    }

    /**
     * @param mailBoxNo
     * @param anchorMailNo
     * @param limit
     * @param isDownward
     * @param emailType    0 : normal email , 1: tag email
     * @param searchType
     * @param searchText
     * @param callback
     */
    public void getEmailList(int mailBoxNo, long anchorMailNo, int limit, boolean isDownward, final int emailType, int searchType, String searchText,
                             long quickSearchMode, int sortColum, boolean isAscend, final OnMailListCallBack callback) {
        String url = "";
        if (emailType == EmailBoxStatics.TAG_MAIL_BOX) {
            url = sRootLink + Urls.URL_GET_EMAIL_TAG_LIST;
//            url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetListOfMailsByTag";
        } else {
            url = sRootLink + Urls.URL_GET_EMAIL_LIST;
//            url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetListOfMails";
        }
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        if (emailType == EmailBoxStatics.NORMAL_MAIL_BOX)
            params.put("mailBoxNo", "" + mailBoxNo);
        else
            params.put("mailTagNo", "" + mailBoxNo);
        params.put("anchorMailNo", "" + anchorMailNo);
        params.put("isDownward", "" + isDownward);
        params.put("readCount", "" + limit);
        params.put("searchType", "" + searchType);
        params.put("searchText", searchText);
        params.put("quickSearchMode", "" + quickSearchMode);
        params.put("sortColumn", "" + sortColum);
        params.put("isAscending", "" + isAscend);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    String mailDataString = jo.getString("Mails");
                    Type listType = new TypeToken<List<MailData>>() {
                    }.getType();
                    List<MailData> mailList = new Gson().fromJson(mailDataString, listType);
                    int totalCount = 0;
                    if (emailType == EmailBoxStatics.TAG_MAIL_BOX) {
                        totalCount = jo.getJSONObject("MailTag").getInt("TotalCount");
                    } else {
                        totalCount = jo.getJSONObject("MailBox").getInt("TotalCount");
                    }
                    if (callback != null) {
                        callback.onMailListSuccess(mailList, totalCount);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onMailListFail(new ErrorData(ErrorData.EXCEPTION_ERROR_CODE, sContext.getString(R.string.exception_error)));
                    }
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null) {
                    callback.onMailListFail(error);
                }
            }
        });
    }

    public void getMaillDetail(final OnMailDetailCallBack callBack, long mailNo) {
        String url = sRootLink + Urls.URL_GET_MAIL_DETAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetMailContent";
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("mailNo", String.valueOf(mailNo));
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    List<AttachData> list = new ArrayList<>();
                    Gson gson = new Gson();
                    JSONObject jo = new JSONObject(response);
                    String dataDetailStringObject = (jo.has("mail")) ? jo.getString("mail") : null;
                    JSONArray jay = jo.getJSONArray("files");
                    MailBoxData mailBoxData = gson.fromJson(dataDetailStringObject, MailBoxData.class);
                    for (int i = 0; i < jay.length(); i++) {
                        list.add(gson.fromJson(jay.getJSONObject(i).toString(), AttachData.class));
                    }
                    mailBoxData.setListAttachMent(list);
                    if (callBack != null)
                        callBack.OnMailDetailSuccess(mailBoxData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.OnMaillDetailFail(error);
            }
        });
    }


    /**
     * @param object ArrayList<MailData> || MailNo Long value if want to delete 1 email only
     */
    public void updateEmailReadUnRead(boolean isRead, Object object, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_UPDATE_EMAIL_READ_UNREAD;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/UpdateReadDateOfMails";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("isRead", "" + isRead);
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    /**
     * @param object ArrayList<MailData> || MailNo Long value if want to delete 1 email only
     */
    public void updateEmailImportant(boolean isImportant, Object object, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_UPDATE_EMAIL_IMPORTANT;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/UpdateImportantOfMails";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("isImportant", "" + isImportant);
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void updateTagOfMail(long tagNo, Object object, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_UPDATE_TAG;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/UpdateImportantOfMails";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("tagNo", "" + tagNo);
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    /**
     * this function is use for update list of emailNo only
     *
     * @param params
     * @param object
     * @return
     */
    private JSONObject covertMailList(Map<String, Object> params, Object object) {
        List<Long> needUpdateMailNo = new ArrayList<>();
        if (object instanceof ArrayList<?>) {
            ArrayList<?> selectedEmailList = (ArrayList<?>) object;
            for (Object selectedEmail : selectedEmailList) {
                if (selectedEmail instanceof MailData)
                    needUpdateMailNo.add(((MailData) selectedEmail).getMailNo());
            }
        } else if (object instanceof Long) {
            needUpdateMailNo.add((Long) object);
        }
        params.put("mails", needUpdateMailNo);

        Gson gson = new Gson();
        String js = gson.toJson(params);
        JSONObject jo = null;
        try {
            jo = new JSONObject(js);
        } catch (JSONException e) {
            e.printStackTrace();
            jo = new JSONObject();
        }
        return jo;
    }

    /**
     * @param object ArrayList<MailData> || MailNo Long value if want to delete 1 email only
     */
    public void deleteEmail(Object object, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_DELETE_EMAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/DeleteMails";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void moveEmailToTrash(Object object, boolean isInTrashBox, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_MOVE_EMAIL_TO_TRASH;
        if (isInTrashBox)
            url = sRootLink + Urls.URL_DELETE_EMAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/MoveMailsToTrash";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void moveEmailToBox(Object object, int mailBoxNo, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_MOVE_MAIL_TO_BOX;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/MoveMailsToTrash";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("mailBoxNo", mailBoxNo + "");
        JSONObject jo = covertMailList(params, object);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    /**
     * GET ALL USERS WITH BELONGS
     */
    public void getAllUsersWithBeLongs(final OnGetAllOfUser callBack) {
        String url = sRootLink + Urls.URL_GET_ALL_USER_WITH_BELONGS;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + DaZoneApplication.getInstance().getPrefs().getaccesstoken());
        params.put("languageCode", "" + Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes() + "");

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type typeUserWithBelong = new TypeToken<List<PersonData>>() {
                    }.getType();
                    ArrayList<PersonData> userWithBelongDTOs = new Gson().fromJson(response, typeUserWithBelong);
                    for (PersonData personData : userWithBelongDTOs) {
                        Util.printLogs("aaaaaaaaaaaaaaaaaaa userWithBelongDTO " + personData.toString());
                    }
                    ArrayList<PersonData> department = OrganizationUserDBHelper.getAllOfOrganization(sRootLink);
                    for (PersonData personData : department) {
                        Util.printLogs("aaaaaaaaaaaaaaaaaaa department " + personData.toString());

                    }
                    if (callBack != null) {
                        callBack.onGetAllOfUserSuccess(userWithBelongDTOs);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onGetAllOfUserFail(error);
            }
        });
    }

    public void getAllUser(final OnGetAllOfUser callBack) {
        String url = sRootLink + Urls.URL_GET_ALL_USER_WITH_BELONGS;
        //String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetAllOfUsers";
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<ArrayList<PersonData>>() {
                    }.getType();
                    ArrayList<PersonData> listUser = new Gson().fromJson(response, listType);
                    String serverLink = HttpRequest.getInstance().sRootLink;

                    /*Iterator<PersonData> iter = listUser.iterator();

                    while (iter.hasNext()) {
                        PersonData user = iter.next();
                        if (!OrganizationUserDBHelper.getDepartmentByID(serverLink, user.getDepartNo())) {
                            iter.remove();
                        }
                    }*/

                    ArrayList<PersonData> listTemp = new ArrayList<>();
                    for (PersonData personData : listUser) {
                        for (BelongToDepartmentDTO belongToDepartmentDTO : personData.getBelongs()) {
                            PersonData test = new PersonData(personData);
                            test.setDepartNo(belongToDepartmentDTO.getDepartNo());
                            test.setUserNo(belongToDepartmentDTO.getUserNo());
                            test.setPositionNo(belongToDepartmentDTO.getPositionNo());
                            test.setDutyNo(belongToDepartmentDTO.getDutyNo());
                            test.setDepartName(belongToDepartmentDTO.getDepartName());
                            test.setPositionName(belongToDepartmentDTO.getPositionName());
                            test.setDutyName(belongToDepartmentDTO.getDutyName());
                            for (PersonData department : OrganizationUserDBHelper.getDepartments(serverLink)) {
                                if (belongToDepartmentDTO.getDepartNo() == department.getDepartNo()) {
                                    listTemp.add(test);
                                }
                            }
                        }
                    }

                    if (callBack != null) {
                        callBack.onGetAllOfUserSuccess(listTemp);
                    }
                    int serverSiteId = ServerSiteDBHelper.getServerSiteId(sRootLink);
                    OrganizationUserDBHelper.addTreeUser(listTemp, serverSiteId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onGetAllOfUserFail(error);
            }
        });
    }

    public void getDepartment(final OnGetAllOfUser callBack) {
        String url = sRootLink + Urls.URL_GET_DEPARTMENT;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    //OrganizationUserDBHelper.clearData();
                    Type listType = new TypeToken<ArrayList<PersonData>>() {
                    }.getType();
                    ArrayList<PersonData> listUser = new Gson().fromJson(response, listType);
/*                    for (PersonData personData : listUser) {
                        Util.printLogs("bbbbbbbbbbbbb " + personData.toString());
                    }*/
                    int serverSiteId = ServerSiteDBHelper.getServerSiteId(sRootLink);
                    OrganizationUserDBHelper.addTreeUser(listUser, serverSiteId);
                    getAllUser(callBack);
                    //getAllUsersWithBeLongs(callBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
//                if(callBack !=null)
            }
        });
    }

    private String convertMailAddress(ArrayList<PersonData> list) {
        //JSONArray jsonArray = new JSONArray();
        List<HashMap> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (PersonData personData : list) {
                HashMap<String, String> hashMap = new HashMap<>();
                if (TextUtils.isEmpty(personData.getAddrType())) {
                    hashMap.put("addrType", MailHelper.getAddType(personData));
                } else {
                    hashMap.put("addrType", personData.getAddrType());
                }
                if (personData.getUserNo() == 0) {
                    if (personData.getDepartNo() == 0) {
                        hashMap.put("no", "0");
                    } else {
                        hashMap.put("no", personData.getDepartNo() + "");
                    }
                } else {
                    hashMap.put("no", personData.getUserNo() + "");
                }
                hashMap.put("isLow", personData.isLow() + "");
                hashMap.put("name", personData.getFullName());
                if (TextUtils.isEmpty(personData.getEmail())) {
                    hashMap.put("mail", personData.getmEmail());
                } else {
                    hashMap.put("mail", personData.getEmail());
                }

                list1.add(hashMap);
            }
        } else {
            return "";
        }
        return new Gson().toJson(list1).toString();
    }

    private String convertAttachment(List<AttachData> list) {
        List<HashMap> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (AttachData attachData : list) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("fileName", attachData.getFileName());
                hashMap.put("size", attachData.getFileSize() + "");
                if (attachData.getFileSize() > 10485760) {
                    hashMap.put("isLargeFile", "true");
                } else {
                    hashMap.put("isLargeFile", "false");
                }
                list1.add(hashMap);
            }
        } else {
            return "";
        }
        return new Gson().toJson(list1).toString();
    }

    public void ComposeMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_COMPOSE_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
/*        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String js = gson.toJson(params);*/
        JSONObject jo = null;
        jo = new JSONObject(params);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void ReplyMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_REPLY_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
        params.put("originalMailNo", mailBoxData.getMailNo() + "");
        JSONObject jo = null;
        jo = new JSONObject(params);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void ForwardMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_FORWARD_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
        params.put("originalMailNo", mailBoxData.getMailNo() + "");
        JSONObject jo = null;
        jo = new JSONObject(params);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void removeTempFile(final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_REMOVE_TEMP_FILE;
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void getListOfMailAccountsForServer(final OnGetListOfMailAccount callBack) {
        String url = sRootLink + Urls.URL_GET_LIST_OF_MAIL_ACCOUNT;
        //String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetListOfMailAccountsForServer";
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    Type listType = new TypeToken<List<AccountData>>() {
                    }.getType();
                    ArrayList<AccountData> listAccount = new Gson().fromJson(response, listType);
                    AccountUserDBHelper.addUser(listAccount);
                    if (callBack != null)
                        callBack.OnGetListOfMailAccountSuccess(listAccount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.OnGetListOfMailAccountFail(error);
            }
        });
    }

    public void regGCMID(final BaseHTTPCallBack callBack, String appID, String deviceToken) {
        String url = "";
        Map<String, String> params = new HashMap<>();
        params.put("regId", appID);
        params.put("phoneToken", deviceToken);
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getaccesstoken());
        int appKey = 0;
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                callBack.onHTTPFail(error);
            }
        });
    }

    public void DrafComposeMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_DRAF_COMPOSE_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
/*        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String js = gson.toJson(params);*/
        JSONObject jo = null;
        jo = new JSONObject(params);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void DrafReplyMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_DRAF_REPLY_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
        params.put("originalMailNo", mailBoxData.getMailNo());
/*        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String js = gson.toJson(params);*/
        JSONObject jo = null;
        jo = new JSONObject(params);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void DrafForwardMail(MailBoxData mailBoxData, final BaseHTTPCallBack callBack) {

        String url = sRootLink + Urls.URL_DRAF_FORWARD_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx?ComposeMail";
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromName", mailBoxData.getMailFrom().getFullName());
        params.put("fromAddr", mailBoxData.getMailFrom().getEmail());
        //params.put("fromAddr","test1@suziptong.com");
        params.put("accNo", mailBoxData.getUserNo() + "");
        params.put("title", mailBoxData.getSubject());
        params.put("content", mailBoxData.getContent());
        params.put("priority", mailBoxData.getPriority());
        params.put("isOneByOne", "" + false);
        params.put("to", convertMailAddress(mailBoxData.getListPersonDataTo()));
        params.put("cc", convertMailAddress(mailBoxData.getListPersonDataCc()));
        params.put("bcc", convertMailAddress(mailBoxData.getListPersonDataBcc()));
        params.put("files", convertAttachment(mailBoxData.getListAttachMent()));
        params.put("originalMailNo", mailBoxData.getMailNo());
/*        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String js = gson.toJson(params);*/
        JSONObject jo = null;
        jo = new JSONObject(params);

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    /*public void getFriendItem(final OnGetListFriend callBack, int UserNo, int companyNo) {
        String url = sRootLink + Urls.URL_FRIEND_SERVICE_INFO;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("UserNo", ""+UserNo);
        params.put("CompanyNo", ""+companyNo);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<List<MailProfileData>>() {
                }.getType();
                List<MailProfileData> list = new Gson().fromJson(response, listType);
                if (callBack != null)
                    callBack.onGetListFriendSuccess(list);
            }

            @Override
            public void onFailure(ErrorData error) {

                callBack.onGetListFriendFail(error);
            }
        });
    }*/

    public void getInfoUser(final OnGetInfoUser callBack, int UserNo) {
        String url = sRootLink + Urls.URL_FRIEND_SERVICE_INFO_NEW;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("userNo", UserNo + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<MailProfileData>() {
                }.getType();
                MailProfileData list = new Gson().fromJson(response, listType);
                if (callBack != null)
                    callBack.onGetInfoUserSuccess(list);
            }

            @Override
            public void onFailure(ErrorData error) {

                callBack.onGetInfoUserFail(error);
            }
        });
    }

    public void filterAddressMail(int beforeMailBoxNo, int afterMailBoxNo, String fromAddress, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_FILTER_ADDRESS_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/UpdateImportantOfMails";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("fromAddress", "" + fromAddress);
        params.put("beforeMailBoxNo", "" + beforeMailBoxNo + "");
        params.put("afterMailBoxNo", "" + afterMailBoxNo + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void filterSenderMail(int beforeMailBoxNo, int afterMailBoxNo, String senderName, final BaseHTTPCallBack callBack) {
        String url = sRootLink + Urls.URL_FILTER_SENDER_MAIL;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/UpdateImportantOfMails";

        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("senderName", "" + senderName);
        params.put("beforeMailBoxNo", "" + beforeMailBoxNo + "");
        params.put("afterMailBoxNo", "" + afterMailBoxNo + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                if (callBack != null)
                    callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callBack != null)
                    callBack.onHTTPFail(error);
            }
        });
    }

    public void CheckAccount(final BaseHTTPCallBack callback) {
        String url = sRootLink + Urls.URL_CHECK_ACCOUNT;
//        String url = "http://test1.suziptong.com/UI/MobileMail3/MobileDataService.asmx/GetAllOfMailBoxs";
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (callback != null) {
                        if (response.equalsIgnoreCase("true"))
                            callback.onHTTPSuccess();
                        else
                            callback.onHTTPFail(null);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null)
                    callback.onHTTPFail(error);
            }
        });
    }

    public void getReceivesForMail(long mailNo, final OnGetReadDate callBack) {
        String url = sRootLink + Urls.URL_GET_RECEIVE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("mailNo", mailNo + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    ReceiveData receiveData = new Gson().fromJson(response, ReceiveData.class);
                    if (callBack != null) {
                        callBack.onGetReadDateSuccess(receiveData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {

                if (callBack != null)
                    callBack.onGetReadDateFail(error);
            }
        });
    }

    private JSONObject covertItem(Map<String, Object> params, Object object, long mailNo, boolean task) {
        List<Long> needUpdateLogNo = new ArrayList<>();
        List<Long> needUpdateMailNo = new ArrayList<>();
        if (object instanceof ArrayList<?>) {
            ArrayList<?> selectedEmailList = (ArrayList<?>) object;
            for (Object selectedEmail : selectedEmailList) {
                if (selectedEmail instanceof ReadDateData) {
                    needUpdateLogNo.add(((ReadDateData) selectedEmail).getLogNo());
                    if (task)
                        needUpdateMailNo.add(mailNo);
                }
            }
        } else if (object instanceof Long) {
            needUpdateLogNo.add((Long) object);
        }
        params.put("logNos", needUpdateLogNo);
        if (task)
            params.put("receivedMailNos", needUpdateMailNo);

        Gson gson = new Gson();
        String js = gson.toJson(params);
        JSONObject jo = null;
        try {
            jo = new JSONObject(js);
        } catch (JSONException e) {
            e.printStackTrace();
            jo = new JSONObject();
        }
        return jo;
    }

    public void CancelSent(long mailNo, Object object, final BaseHTTPCallBack callback) {
        String url = sRootLink + Urls.URL_CANCEL_SENT;
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        JSONObject jo = covertItem(params, object, mailNo, true);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (callback != null) {
                        callback.onHTTPSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null)
                    callback.onHTTPFail(error);
            }
        });
    }

    public void CancelReservation(long mailNo, Object object, final BaseHTTPCallBack callback) {
        String url = sRootLink + Urls.URL_CANCEL_RESERCATION;
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        JSONObject jo = covertItem(params, object, mailNo, false);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, jo, new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (callback != null) {
                        callback.onHTTPSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null)
                    callback.onHTTPFail(error);
            }
        });
    }

    public void CancelReservationEntrie(long mailNo, long cmSendNum, final BaseHTTPCallBack callback) {
        String url = sRootLink + Urls.URL_CANCEL_RESERCATION_ENTRIE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", DaZoneApplication.getInstance().getPrefs().getAccessToken());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("mailNo", mailNo + "");
        params.put("cmSendNum", cmSendNum + "");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    if (callback != null) {
                        callback.onHTTPSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorData error) {
                if (callback != null)
                    callback.onHTTPFail(error);
            }
        });
    }
}
