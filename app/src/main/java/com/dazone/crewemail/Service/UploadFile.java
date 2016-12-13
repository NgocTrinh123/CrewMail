package com.dazone.crewemail.Service;
import com.dazone.crewemail.data.AttachData;

/**
 * Created by THANHTUNG on 30/12/2015.
 */
public class UploadFile {
    AttachData dto;

    public UploadFile(AttachData dto) {
        this.dto = dto;
    }

    public void executeUpload()
    {
        new UploadFileToServer().execute(dto.getPath(), dto.getFileName());
    }
}
