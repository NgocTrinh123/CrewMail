package com.dazone.crewemail.data;

import android.text.TextUtils;

import com.dazone.crewemail.utils.Util;
import com.google.gson.annotations.SerializedName;

/**
 * Created by THANHTUNG on 26/01/2016.
 */
public class MailProfileData {
    @SerializedName("UserNo")
    public String UserNo;
    @SerializedName("UserID")
    public String UserId = "";
    @SerializedName("Name_Default")
    public String NameDefault = "";
    @SerializedName("Name_EN")
    public String NameEn = "";
    @SerializedName("Name")
    public String Name = "";
    @SerializedName("MailAddress")
    public String MailAddress = "";
    @SerializedName("CellPhone")
    public String CellPhone = "";
    @SerializedName("AvatarUrl")
    public String Avatar = "";
    @SerializedName("CompanyId")
    public String CompanyId = "";

    public String getFullName()
    {
        String temp = "";
        if(Util.isPhoneLanguageEN())
        {
            if(!TextUtils.isEmpty(NameEn))
            {
                temp = NameEn;
            }
            else
            {
                temp = NameDefault;
            }
        }
        else
        {
            if(!TextUtils.isEmpty(NameDefault))
            {
                temp = NameDefault;
            }
            else
            {
                temp = NameEn;
            }
        }
        return temp;
    }

    public String getUserNo() {
        return UserNo;
    }

    public void setUserNo(String userNo) {
        UserNo = userNo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getNameDefault() {
        return NameDefault;
    }

    public void setNameDefault(String nameDefault) {
        NameDefault = nameDefault;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMailAddress() {
        return MailAddress;
    }

    public void setMailAddress(String mailAddress) {
        MailAddress = mailAddress;
    }

    public String getCellPhone() {
        return CellPhone;
    }

    public void setCellPhone(String cellPhone) {
        CellPhone = cellPhone;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }
}
