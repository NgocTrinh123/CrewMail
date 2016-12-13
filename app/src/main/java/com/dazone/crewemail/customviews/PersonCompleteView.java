package com.dazone.crewemail.customviews;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.helper.MailHelper;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by THANHTUNG on 17/12/2015.
 */
public class PersonCompleteView extends TokenCompleteTextView<PersonData> {
    public int Type;
    public PersonCompleteView(Context context) {
        super(context);
        allowDuplicates(false);
        allowCollapse(true);
        setThreshold(1);
    }

    public PersonCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        allowDuplicates(false);
        allowCollapse(true);
        setThreshold(1);
    }

    public PersonCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        allowDuplicates(false);
        allowCollapse(true);
        setThreshold(1);
    }

    @Override
    protected View getViewForObject(PersonData object) {
        String email = "";
        String data = "";
        if(TextUtils.isEmpty(object.getEmail()))
        {
            email = object.getmEmail();
        }else
        {
            email = object.getEmail();
        }

        object.setTypeAddress(Type);

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.fragment_mail_create_person, (ViewGroup)PersonCompleteView.this.getParent(), false);
        LinearLayout tokenMailCreateItem = (LinearLayout)view.findViewById(R.id.tokenMailCreateItem);
        TextView txtContent = (TextView)view.findViewById(R.id.txtMailCreatePersonNameOrMail);

        if (object.getTypeColor() == 0) {
            tokenMailCreateItem.setBackgroundResource(R.drawable.color_background_mail_unknown);
            data = email;
        } else {
            if (object.getTypeColor() == 1)
            {
                tokenMailCreateItem.setBackgroundResource(R.drawable.color_background_mail_to);
                if(TextUtils.isEmpty(email))
                {
                    data = object.getFullName();
                }else
                {
                    data = object.getFullName()+"<"+email+">";
                }
            }
            else if (TextUtils.isEmpty(object.getFullName())) {
                tokenMailCreateItem.setBackgroundResource(R.color.transparent);
                txtContent.setVisibility(GONE);
            } else {
                data = object.getFullName();
                tokenMailCreateItem.setBackgroundResource(R.drawable.color_background_mail_bcc_cc);
            }
        }
        txtContent.setText(data);
        return view;
    }

    @Override
    protected PersonData defaultObject(String completionText) {
        PersonData personData;
        personData =  new PersonData(completionText, completionText.replace(" ", ""));
        personData.setTypeColor(0);
        int index = completionText.indexOf('@');
        if(MailHelper.isValidEmail(completionText))
        {
            if(index==-1)
            {
                personData =  new PersonData(completionText, completionText.replace(" ", "") + "@dazone.co.kr");
                personData.setTypeColor(0);
            }else
            {
                personData = new PersonData(completionText.substring(0, index), completionText);
                personData.setTypeColor(1);
            }
        }else
        {
            personData =  new PersonData(completionText, completionText.replace(" ", ""));
            personData.setTypeColor(0);
        }
        return personData;
    }

    public void setType(int Type) {
        this.Type = Type;
    }
}
