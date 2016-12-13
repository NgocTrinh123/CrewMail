package com.dazone.crewemail.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by THANHTUNG on 16/12/2015.
 */
public class AttachData implements Serializable {
    @SerializedName("FileNo")
    private long AttachNo;
    @SerializedName("MailNo")
    private long MailNo;
    @SerializedName("Date")
    private long DateCreate;
    @SerializedName("Name")
    private String FileName;
    @SerializedName("Type")
    private String FileType;
    @SerializedName("Path")
    private String Path;
    @SerializedName("Url")
    private String Url;
    @SerializedName("Size")
    private long FileSize;
    @SerializedName("SizeToString")
    private String SizeToString;

    public AttachData(String fileName, String path, long fileSize, String fileType, long dateCreate) {
        FileName = fileName;
        Path = path;
        FileSize = fileSize;
        DateCreate = dateCreate;
        FileType = fileType;
    }

    public AttachData() {
    }

    public long getAttachNo() {
        return AttachNo;
    }

    public void setAttachNo(long attachNo) {
        AttachNo = attachNo;
    }

    public long getMailNo() {
        return MailNo;
    }

    public void setMailNo(long mailNo) {
        MailNo = mailNo;
    }

    public long getDateCreate() {
        return DateCreate;
    }

    public void setDateCreate(long dateCreate) {
        DateCreate = dateCreate;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileType() {
        return FileType;
    }

    public void setFileType(String fileType) {
        FileType = fileType;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public long getFileSize() {
        return FileSize;
    }

    public void setFileSize(long fileSize) {
        FileSize = fileSize;
    }

    public String getSizeToString() {
        return SizeToString;
    }

    public void setSizeToString(String sizeToString) {
        SizeToString = sizeToString;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    protected AttachData(Parcel in)
    {
        this.AttachNo = in.readLong();
        this.MailNo = in.readLong();
        this.DateCreate = in.readLong();
        this.FileName = in.readString();
        this.FileType = in.readString();
        this.Path = in.readString();
        this.Url = in.readString();
        this.FileSize = in.readLong();
        this.SizeToString = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.AttachNo);
        dest.writeLong(this.MailNo);
        dest.writeLong(this.DateCreate);
        dest.writeString(this.FileName);
        dest.writeString(this.FileType);
        dest.writeString(this.Url);
        dest.writeLong(this.FileSize);
        dest.writeString(this.SizeToString);
        dest.writeString(this.Path);
    }

    public static final Creator<AttachData> CREATOR = new Creator<AttachData>() {
        public AttachData createFromParcel(Parcel source) {
            return new AttachData(source);
        }

        public AttachData[] newArray(int size) {
            return new AttachData[size];
        }
    };*/
}
