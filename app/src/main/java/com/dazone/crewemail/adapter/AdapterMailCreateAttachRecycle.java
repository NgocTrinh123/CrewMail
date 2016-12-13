package com.dazone.crewemail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dazone.crewemail.R;
import com.dazone.crewemail.View.ViewHolderMailCreateAttachRecycle;
import com.dazone.crewemail.data.AttachData;

import java.util.List;

/**
 * Created by THANHTUNG on 16/12/2015.
 */
public class AdapterMailCreateAttachRecycle extends RecyclerView.Adapter<ViewHolderMailCreateAttachRecycle> {
    private List<AttachData> listAttach;
    private Context context;
    private View.OnClickListener mOnClickListenerDelete;
    private View.OnClickListener mOnClickListenerDownload;

    public AdapterMailCreateAttachRecycle(Context context, List<AttachData> listAttach) {
        this.listAttach = listAttach;
        this.context = context;
    }

    public void onAttachItemDeleteClick(View.OnClickListener clickListener){
        this.mOnClickListenerDelete = clickListener;
    }

    public void onAttachItemDownloadClick(View.OnClickListener clickListener){
        this.mOnClickListenerDownload = clickListener;
    }

    @Override
    public ViewHolderMailCreateAttachRecycle onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mail_create_item_attach, null);
        ViewHolderMailCreateAttachRecycle viewHolder = new ViewHolderMailCreateAttachRecycle(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderMailCreateAttachRecycle holder, int position) {
        AttachData attachData = listAttach.get(position);
        holder.txtMailCreateAttachFileName.setText( attachData.getFileName());
        holder.txtMailCreateAttachFileSize.setText(" ("+attachData.getFileSize()+")");
        holder.imgMailCreateAttachDelete.setOnClickListener(mOnClickListenerDelete);
        holder.imgMailCreateAttachDownload.setOnClickListener(mOnClickListenerDownload);
    }

    @Override
    public int getItemCount() {
        return (null != listAttach ? listAttach.size() : 0);
    }
}
