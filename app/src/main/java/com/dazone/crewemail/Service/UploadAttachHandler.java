package com.dazone.crewemail.Service;

import android.os.Process;
import com.dazone.crewemail.data.AttachData;
import java.util.List;

/**
 * Created by THANHTUNG on 30/12/2015.
 */
public class UploadAttachHandler {
    private List<AttachData>list;
    private AttachData attachData;

    public UploadAttachHandler(List<AttachData>list){
        this.list = list;
    }

    public UploadAttachHandler(AttachData attachData){
        this.attachData = attachData;
    }

    public void executeUploadListData() {
        Thread threadOffline = new Thread() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                if (list != null) {
                    for (AttachData attachData : list) {
                        if (null != attachData) {
                            new UploadFile(attachData).executeUpload();
                        }
                    }
                }
            }
        };
        threadOffline.start();
    }

    public void executeUploadData() {
        Thread threadOffline = new Thread() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                if (attachData != null) {
                        if (null != attachData) {
                            new UploadFile(attachData).executeUpload();
                        }
                }
            }
        };
        threadOffline.start();
    }
}
