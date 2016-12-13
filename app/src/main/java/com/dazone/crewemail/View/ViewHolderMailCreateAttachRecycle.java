package com.dazone.crewemail.View;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dazone.crewemail.R;

/**
 * Created by THANHTUNG on 16/12/2015.
 */
public class ViewHolderMailCreateAttachRecycle extends RecyclerView.ViewHolder {
    public ImageButton imgMailCreateAttachDownload, imgMailCreateAttachDelete;
    public TextView txtMailCreateAttachFileName, txtMailCreateAttachFileSize;
    public ViewHolderMailCreateAttachRecycle(View itemView) {
        super(itemView);
        this.imgMailCreateAttachDownload = (ImageButton) itemView.findViewById(R.id.imgMailCreateItemAttachDownload);
        this.imgMailCreateAttachDelete = (ImageButton) itemView.findViewById(R.id.imgMailCreateItemAttachDelete);
        this.txtMailCreateAttachFileName = (TextView) itemView.findViewById(R.id.txtMailCreateItemAttachFileName);
        this.txtMailCreateAttachFileSize = (TextView) itemView.findViewById(R.id.txtMailCreateItemAttachFileSize);
    }
}
