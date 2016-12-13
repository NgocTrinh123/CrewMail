package com.dazone.crewemail.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.dto.BelongToDepartmentDTO;
import com.dazone.crewemail.interfaces.OnGetAllOfUser;
import com.dazone.crewemail.webservices.HttpRequest;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by THANHTUNG on 17/12/2015.
 */
public class PersonData implements Serializable, Parcelable {
    @SerializedName("Name")
    private String FullName;
    @SerializedName("Name_EN")
    private String mNameEn;
    @SerializedName("Name_Default")
    private String mNameDefault;
    @SerializedName("Mail")
    private String Email;
    @SerializedName("MailAddress")
    private String mEmail;
    @SerializedName("UserNo")
    private int UserNo = 0;
    @SerializedName("UserID")
    private String UserID;
    @SerializedName("AvatarUrl")
    private String UrlAvatar;
    @SerializedName("addrType")
    private String addrType = ""; //mail: email user input, user: name member from oran, depart: Type Address depart
    private int TypeAddress = 0; //0: to, 1: cc, 2: Type Address Bcc
    private int TypeColor = 2;//0: unknown 1: Known from listUser 2: Known from Oran
    @SerializedName("DepartNo")
    private int DepartNo = 0;
    @SerializedName("DepartName")
    private String DepartName;
    @SerializedName("PositionNo")
    private int PositionNo;
    @SerializedName("PositionName")
    private String PositionName;
    @SerializedName("DutyNo")
    private int DutyNo;
    @SerializedName("DutyName")
    private String DutyName;
    @SerializedName("isLow")
    private boolean isLow;
    @SerializedName("Photo")
    private String mPhoto;
    @SerializedName("Belongs")
    private ArrayList<BelongToDepartmentDTO> Belongs = new ArrayList<>();

    private int mType; // 0 : user not in organization || user in organization  1 : department , 2 : user
    @SerializedName("Enabled")
    private boolean mIsEnabled = true;

    @SerializedName("SortNo") // use for department only (mType = 1)
    private int mSortNo;
    @SerializedName("ParentNo") // use for department only (mType = 1)
    private int mDepartmentParentNo;

    @SerializedName("ChildDepartments")
    private ArrayList<PersonData> mPersonList;

    private boolean isCheck = false;

    public PersonData(String name, String email, String urlAvatar) {
        Email = email;
        FullName = name;
        UrlAvatar = urlAvatar;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "FullName='" + FullName + '\'' +
                ", mNameEn='" + mNameEn + '\'' +
                ", mNameDefault='" + mNameDefault + '\'' +
                ", Email='" + Email + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", UserNo=" + UserNo +
                ", UserID='" + UserID + '\'' +
                ", UrlAvatar='" + UrlAvatar + '\'' +
                ", addrType='" + addrType + '\'' +
                ", TypeAddress=" + TypeAddress +
                ", TypeColor=" + TypeColor +
                ", DepartNo=" + DepartNo +
                ", DepartName='" + DepartName + '\'' +
                ", PositionNo=" + PositionNo +
                ", PositionName='" + PositionName + '\'' +
                ", DutyNo=" + DutyNo +
                ", DutyName='" + DutyName + '\'' +
                ", isLow=" + isLow +
                ", mPhoto='" + mPhoto + '\'' +
                ", Belongs=" + Belongs +
                ", mType=" + mType +
                ", mIsEnabled=" + mIsEnabled +
                ", mSortNo=" + mSortNo +
                ", mDepartmentParentNo=" + mDepartmentParentNo +
                ", mPersonList=" + mPersonList +
                ", isCheck=" + isCheck +
                '}';
    }

    public PersonData(String name, String email) {
        Email = email;
        FullName = name;
    }

    public PersonData() {
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        if (TextUtils.isEmpty(Email))
            return "";
        else
            return Email;
    }

    public ArrayList<BelongToDepartmentDTO> getBelongs() {
        return Belongs;
    }

    public void setBelongs(ArrayList<BelongToDepartmentDTO> belongs) {
        Belongs = belongs;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getmEmail() {
        if (TextUtils.isEmpty(mEmail))
            return "";
        else
            return mEmail;
    }

    public void setmEmail(String email) {
        mEmail = email;
    }

    public String getUrlAvatar() {
        return UrlAvatar;
    }

    public void setUrlAvatar(String urlAvatar) {
        UrlAvatar = urlAvatar;
    }

    public int getTypeAddress() {
        return TypeAddress;
    }

    public void setTypeAddress(int typeAddress) {
        TypeAddress = typeAddress;
    }

    public int getTypeColor() {
        return TypeColor;
    }

    public void setTypeColor(int typeColor) {
        TypeColor = typeColor;
    }


    public int getType() {
        if (DepartNo != 0) {
            if (UserNo == 0) // category
                return 1;
            else
                return 2; // user
        } else {
            return 0; // not in organization
        }
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public String getNameEn() {
        return mNameEn;
    }

    public void setNameEn(String nameEn) {
        mNameEn = nameEn;
    }

    public String getNameDefault() {
        return mNameDefault;
    }

    public void setNameDefault(String nameDefault) {
        mNameDefault = nameDefault;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        mIsEnabled = isEnabled;
    }

    public ArrayList<PersonData> getPersonList() {
        return mPersonList;
    }

    public void setPersonList(ArrayList<PersonData> personList) {
        mPersonList = personList;
    }

    public int getSortNo() {
        return mSortNo;
    }

    public void setSortNo(int sortNo) {
        mSortNo = sortNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public void setUserNo(int userNo) {
        UserNo = userNo;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getDepartNo() {
        return DepartNo;
    }

    public void setDepartNo(int departNo) {
        DepartNo = departNo;
    }

    public String getDepartName() {
        return DepartName;
    }

    public void setDepartName(String departName) {
        DepartName = departName;
    }

    public int getPositionNo() {
        return PositionNo;
    }

    public void setPositionNo(int positionNo) {
        PositionNo = positionNo;
    }

    public String getPositionName() {
        return PositionName;
    }

    public void setPositionName(String positionName) {
        PositionName = positionName;
    }

    public int getDutyNo() {
        return DutyNo;
    }

    public void setDutyNo(int dutyNo) {
        DutyNo = dutyNo;
    }

    public String getDutyName() {
        return DutyName;
    }

    public void setDutyName(String dutyName) {
        DutyName = dutyName;
    }

    public boolean isLow() {
        return isLow;
    }

    public void setLow(boolean low) {
        isLow = low;
    }

    /**
     * mail: email user input, user: name member from oran, depart: Type Address depart
     *
     * @return
     */
    public String getAddrType() {
        if (TextUtils.isEmpty(addrType)) {
            if (this.getType() == 1) {
                return "depart";
            } else if (this.getType() == 2) {
                return "user";
            } else {
                return "mail";
            }
        } else {

            return addrType;
        }
    }

    public int getDepartmentParentNo() {
        return mDepartmentParentNo;
    }

    public void setDepartmentParentNo(int departmentParentNo) {
        mDepartmentParentNo = departmentParentNo;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public void addChild(PersonData person) {
        if (this.mPersonList == null)
            this.mPersonList = new ArrayList<>();
        this.mPersonList.add(person);
    }


    /**
     * get list of all user and department in db if exist ,
     * else call from webservice
     *
     * @param callback
     */
    public static void getDepartmentAndUser(OnGetAllOfUser callback, int task) {
        String serverLink = HttpRequest.getInstance().sRootLink;

        ArrayList<PersonData> result = OrganizationUserDBHelper.getAllOfOrganization(serverLink);
        if (task == 0) {
            HttpRequest.getInstance().getDepartment(callback);

            if (result == null || result.size() == 0) {
                // call api
                HttpRequest.getInstance().getDepartment(callback);
            } else {
                // sort data by order
                Collections.sort(result, new Comparator<PersonData>() {
                    @Override
                    public int compare(PersonData r1, PersonData r2) {
                        if (r1.mSortNo > r2.mSortNo) {
                            return 1;
                        } else if (r1.mSortNo == r2.mSortNo) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
                if (callback != null) {
                    callback.onGetAllOfUserSuccess(result);
                }
            }
        } else {
            HttpRequest.getInstance().getDepartment(callback);
        }
    }

    /**
     * Search User and department
     *
     * @param personList
     * @param query
     * @return result list which contain %query%
     */
    public static ArrayList<PersonData> searchDepartmentUserFromList(ArrayList<PersonData> personList, String query) {
        ArrayList<PersonData> resultList = new ArrayList<>();
        if (personList != null) {
            for (PersonData person : personList) {

                if ((person.FullName != null && person.FullName.toLowerCase().contains(query))
                        || (person.Email != null && person.Email.toLowerCase().contains(query))
                        || (person.mNameEn != null && person.mNameEn.toLowerCase().contains(query))
                        || (person.mNameDefault != null && person.mNameDefault.toLowerCase().contains(query))) {
                    resultList.add(person);
                }
            }
        }
        return resultList;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PersonData) {
            PersonData temp = (PersonData) o;
            if (this.getType() == 2 && temp.getType() == 2) { // user
                return (this.UserNo == temp.UserNo);

            } else if (this.getType() == 1 && temp.getType() == 1) {
                return (this.DepartNo == temp.DepartNo);
            } else {
                if (this.Email != null) {
                    return (this.Email.equals(temp.mEmail) || this.Email.equals(temp.Email));
                }
            }
        }
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public PersonData(PersonData personData) {
        this.FullName = personData.getFullName();
        this.mNameEn = personData.mNameEn;
        this.mNameDefault = personData.mNameDefault;
        this.Email = personData.getEmail();
        this.mEmail = personData.mEmail;
        this.UserNo = personData.UserNo;
        this.UserID = personData.UserID;
        this.UrlAvatar = personData.UrlAvatar;
        this.addrType = personData.addrType;
        this.TypeAddress = personData.TypeAddress;
        this.TypeColor = personData.getTypeColor();
        //this.Belongs = personData.Belongs;

        this.isLow = personData.isLow;
        this.mPhoto = personData.mPhoto;
        this.mType = personData.mType;
        this.mIsEnabled = personData.mIsEnabled;
        this.mSortNo = personData.mSortNo;
        this.mDepartmentParentNo = personData.mDepartmentParentNo;
        this.isCheck = personData.isCheck();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.FullName);
        dest.writeString(this.mNameEn);
        dest.writeString(this.mNameDefault);
        dest.writeString(this.Email);
        dest.writeString(this.mEmail);
        dest.writeInt(this.UserNo);
        dest.writeString(this.UserID);
        dest.writeString(this.UrlAvatar);
        dest.writeString(this.addrType);
        dest.writeInt(this.TypeAddress);
        dest.writeInt(this.TypeColor);
        dest.writeTypedList(this.Belongs);
        dest.writeInt(this.DepartNo);
        dest.writeString(this.DepartName);
        dest.writeInt(this.PositionNo);
        dest.writeString(this.PositionName);
        dest.writeInt(this.DutyNo);
        dest.writeString(this.DutyName);
        dest.writeByte(isLow ? (byte) 1 : (byte) 0);
        dest.writeString(this.mPhoto);
        dest.writeInt(this.mType);
        dest.writeByte(mIsEnabled ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mSortNo);
        dest.writeInt(this.mDepartmentParentNo);
        dest.writeByte(isCheck ? (byte) 1 : (byte) 0);
    }

    protected PersonData(Parcel in) {
        this.FullName = in.readString();
        this.mNameEn = in.readString();
        this.mNameDefault = in.readString();
        this.Email = in.readString();
        this.mEmail = in.readString();
        this.UserNo = in.readInt();
        this.UserID = in.readString();
        this.UrlAvatar = in.readString();
        this.addrType = in.readString();
        this.TypeAddress = in.readInt();
        this.TypeColor = in.readInt();
        this.Belongs = in.createTypedArrayList(BelongToDepartmentDTO.CREATOR);
        this.DepartNo = in.readInt();
        this.DepartName = in.readString();
        this.PositionNo = in.readInt();
        this.PositionName = in.readString();
        this.DutyNo = in.readInt();
        this.DutyName = in.readString();
        this.isLow = in.readByte() != 0;
        this.mPhoto = in.readString();
        this.mType = in.readInt();
        this.mIsEnabled = in.readByte() != 0;
        this.mSortNo = in.readInt();
        this.mDepartmentParentNo = in.readInt();
        this.isCheck = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PersonData> CREATOR = new Parcelable.Creator<PersonData>() {
        public PersonData createFromParcel(Parcel source) {
            return new PersonData(source);
        }

        public PersonData[] newArray(int size) {
            return new PersonData[size];
        }
    };
}
