package com.dazone.crewemail.customviews;

import android.content.Context;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;

/**
 * Created by THANHTUNG on 26/01/2016.
 */
public class MailProfileLinear extends LinearLayout {
    private TextView txtTitle;
    public TextView txtValue;
    private KeyListener listener;

    public MailProfileLinear(Context context, String title, String value) {
        super(context);
        init(title, value);
    }

    private void init( String title, String value) {
        LayoutInflater li = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.note_profile_item, this, true);
        txtTitle = (TextView) findViewById(R.id.txtProfileTitle);
        txtValue = (TextView) findViewById(R.id.txtProfileValue);
        listener = txtValue.getKeyListener();
        txtTitle.setText(title);
        txtValue.setText(value);

    }

}
