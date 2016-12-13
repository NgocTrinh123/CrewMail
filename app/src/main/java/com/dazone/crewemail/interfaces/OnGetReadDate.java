package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.ReadDateData;
import com.dazone.crewemail.data.ReceiveData;

import java.util.ArrayList;

/**
 * Created by THANHTUNG on 02/02/2016.
 */
public interface OnGetReadDate {
    void onGetReadDateSuccess(ReceiveData receiveData);
    void onGetReadDateFail (ErrorData errorData);
}
