package com.dazone.crewemail.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.Service.UploadFileToServer;
import com.dazone.crewemail.activities.ListEmailActivity;
import com.dazone.crewemail.activities.OrganizationActivity;
import com.dazone.crewemail.adapter.AdapterMailCreateAttachLinear;
import com.dazone.crewemail.adapter.AdapterMailCreateFromSpiner;
import com.dazone.crewemail.customviews.AlertDialogView;
import com.dazone.crewemail.customviews.PersonCompleteView;
import com.dazone.crewemail.data.AccountData;
import com.dazone.crewemail.data.AttachData;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailBoxData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.helper.MailHelper;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnGetAllOfUser;
import com.dazone.crewemail.interfaces.OnGetListOfMailAccount;
import com.dazone.crewemail.interfaces.OnMailDetailCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by THANHTUNG on 21/12/2015.
 */
public class FragmentMailCreate extends BaseFragment implements TokenCompleteTextView.TokenListener, OnGetAllOfUser, BaseHTTPCallBack, OnGetListOfMailAccount, View.OnClickListener, OnMailDetailCallBack {
    private ImageButton imgDropDown, imgAddAttach;
    private LinearLayout linearMainBccAndCc, linearAttach;
    private EditText edtMailCreateSubject, edtMailCreateContent;
    private TextView txtMailCreateOriginalMessage;
    private PersonCompleteView edtMailCreateTo, edtMailCreateBcc, edtMailCreateCc;
    private Spinner spnMailFrom;
    private CheckBox chkMailCreateQuote;
    private WebView webMailCreateContent;
    private ArrayList<PersonData> people = new ArrayList<>();
    private ArrayAdapter<PersonData> adapter;
    private MailBoxData mailBoxData = new MailBoxData();
    private MailBoxData dataCreate;
    private View v;
    private AdapterMailCreateFromSpiner adapterMailCreateFromSpiner;
    private ArrayList<AccountData> listUser = new ArrayList<>();
    private int task = 4;
    private boolean isSend = true;
    private int isUpload = 0;
    private int isFail = 0;
    private String className;
    private long MailNo;
    private ArrayList<AttachData> tp = new ArrayList<>();

    public static FragmentMailCreate newInstance(int task, MailBoxData data, String className, long MailNo) {
        Bundle args = new Bundle();
        args.putInt(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK, task);
        args.putSerializable(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL, data);
        args.putString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME, className);
        args.putLong(StaticsBundle.BUNDLE_MAIL_NO, MailNo);
        FragmentMailCreate fragment = new FragmentMailCreate();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Fresco.initialize(getActivity());
        PersonData.getDepartmentAndUser(this, 1);
        AccountData.getAllAccount(this);
        HttpRequest.getInstance().removeTempFile(null);
        Bundle bundle = getArguments();
        if (bundle != null) {
            task = (bundle.getInt(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK));
            dataCreate = (MailBoxData) bundle.getSerializable(StaticsBundle.BUNDLE_MAIL_CREATE_FROM_DETAIL);
            className = bundle.getString(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME);
            MailNo = bundle.getLong(StaticsBundle.BUNDLE_MAIL_NO);
            if (dataCreate != null) {
                setUpCreate(dataCreate, className);
            } else {
                setUpDraft(MailNo);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mail_create, container, false);
        initControl(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initAdapter();
        initClick();
        if (task != 3)
            BindData(mailBoxData);
    }

    private void initControl(View v) {
        imgDropDown = (ImageButton) v.findViewById(R.id.imgMailCreateImageDropDown);
        imgAddAttach = (ImageButton) v.findViewById(R.id.imgMailCreateAttach);
        linearMainBccAndCc = (LinearLayout) v.findViewById(R.id.linear_main_bcc_cc);
        linearAttach = (LinearLayout) v.findViewById(R.id.linear_attach);
        edtMailCreateTo =
                (PersonCompleteView) v.findViewById(R.id.edtMailCreateTo);
        edtMailCreateTo.setType(0);
        edtMailCreateCc =
                (PersonCompleteView) v.findViewById(R.id.edtMailCreateCc);
        edtMailCreateCc.setType(1);
        edtMailCreateBcc =
                (PersonCompleteView) v.findViewById(R.id.edtMailCreateBcc);
        edtMailCreateBcc.setType(2);
        edtMailCreateSubject = (EditText) v.findViewById(R.id.edtMailCreateSubject);
        edtMailCreateContent = (EditText) v.findViewById(R.id.edtMailCreateContent);
        spnMailFrom = (Spinner) v.findViewById(R.id.spnMailCreateAccountFrom);
        chkMailCreateQuote = (CheckBox) v.findViewById(R.id.chkMailCreateCheck);
        webMailCreateContent = (WebView) v.findViewById(R.id.webMailCreateContent);
        txtMailCreateOriginalMessage = (TextView) v.findViewById(R.id.txtMailCreateOriginalMessage);
        v.findViewById(R.id.imgMailCreateOrgTo).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateOrgCc).setOnClickListener(this);
        v.findViewById(R.id.imgMailCreateOrgBcc).setOnClickListener(this);
    }

    private void initAdapter() {
        setAdapter(edtMailCreateTo);
        setAdapter(edtMailCreateBcc);
        setAdapter(edtMailCreateCc);
        adapterMailCreateFromSpiner = new AdapterMailCreateFromSpiner(getActivity(), R.layout.fragment_mail_create_item_spiner_address_to, listUser);
        spnMailFrom.setAdapter(adapterMailCreateFromSpiner);
    }

    private void setAdapter(TokenCompleteTextView tokenCompleteTextView) {
        adapter = new FilteredArrayAdapter<PersonData>(getActivity(), R.layout.person_layout, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                PersonData p = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(p.getFullName());
                ((TextView) convertView.findViewById(R.id.email)).setText(p.getEmail());
                return convertView;
            }

            @Override
            protected boolean keepObject(PersonData person, String mask) {
                mask = mask.toLowerCase();
                return person.getFullName().toLowerCase().contains(mask) || person.getFullName().toLowerCase().contains(mask);
            }
        };
        tokenCompleteTextView.setAdapter(adapter);
        tokenCompleteTextView.setTokenListener(this);
        tokenCompleteTextView.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
    }

    private void initClick() {
        imgDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearMainBccAndCc.getVisibility() == View.VISIBLE) {
                    imgDropDown.setImageResource(R.drawable.dropdow_arr_ic);
                    linearMainBccAndCc.setVisibility(View.GONE);
                } else {
                    imgDropDown.setImageResource(R.drawable.dropdow_arr_ic_02);
                    linearMainBccAndCc.setVisibility(View.VISIBLE);
                }
            }
        });

        imgAddAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MailHelper.LoadFile(FragmentMailCreate.this);

                Intent i = new Intent(getActivity(), FilePickerActivity.class);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                startActivityForResult(i, Statics.FILE_PICKER_SELECT);
                //AlertDialogView.normalAlertDialog(getActivity(), getString(R.string.app_name), getString(R.string.string_alert_update), null);
            }
        });

        spnMailFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PersonData personData = new PersonData();
                personData.setFullName(listUser.get(position).getName());
                personData.setEmail(listUser.get(position).getMailAddress());
                mailBoxData.setMailFrom(personData);
                mailBoxData.setUserNo(listUser.get(position).getAccountNo());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        chkMailCreateQuote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    webMailCreateContent.setVisibility(View.VISIBLE);
                    txtMailCreateOriginalMessage.setVisibility(View.VISIBLE);
                } else {
                    webMailCreateContent.setVisibility(View.GONE);
                    txtMailCreateOriginalMessage.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setUpCreate(MailBoxData data, String className) {
        ArrayList<PersonData> temp = new ArrayList<>();
        ArrayList<PersonData> temp2 = new ArrayList<>();
        ArrayList<PersonData> temp3 = new ArrayList<>();
        mailBoxData.setMailNo(data.getMailNo());
        mailBoxData.setContentReply(data.getContentReply());
        if (task == 0) {
            mailBoxData.setSubject("FWD: " + data.getSubject());
            if (data.getListAttachMent() != null) {
                mailBoxData.setListAttachMent(data.getListAttachMent());
                tp.addAll(data.getListAttachMent());
            }
        } else {
            if (EmailBoxStatics.MAIL_CLASS_OUT_BOX.equals(className)) {
                // out box
                mailBoxData.setListPersonDataTo(data.getListPersonDataTo());
                mailBoxData.setSubject(data.getSubject());
                mailBoxData.setListPersonDataCc(data.getListPersonDataCc());
                mailBoxData.setListPersonDataBcc(data.getListPersonDataBcc());
            } else {
                // other mail box
                temp.add(data.getMailFrom());
                mailBoxData.setSubject("RE: " + data.getSubject());
                mailBoxData.setListPersonDataTo(temp);
            }
            if (task == 2) {
                temp.addAll(MailHelper.removeMe(data.getListPersonDataTo()));
                mailBoxData.setListPersonDataTo(temp);
                temp2.addAll(MailHelper.removeMe(data.getListPersonDataCc()));
                mailBoxData.setListPersonDataCc(temp2);
                temp3.addAll(MailHelper.removeMe(data.getListPersonDataBcc()));
                mailBoxData.setListPersonDataBcc(temp3);
            }
        }
    }

    private void setUpDraft(long mailNo) {
        if (task == 3) {
            HttpRequest.getInstance().getMaillDetail(this, mailNo);
        }
    }

    public void GetFile(Uri uri) {
        String Path = Util.getPathFromURI(uri, getActivity());
        String fileName = Util.getFileName(uri, getActivity());
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        long fileSize = Util.getFileSize(Path);
        AttachData attachData = new AttachData(fileName, Path, fileSize, fileType, System.currentTimeMillis());
        mailBoxData.getListAttachMent().add(attachData);
        final AdapterMailCreateAttachLinear itemView = new AdapterMailCreateAttachLinear(getActivity(), attachData);
        itemView.setTag(attachData);
        itemView.getImageButtonDelete().setVisibility(View.VISIBLE);
        itemView.getImageButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailBoxData.getListAttachMent().remove(itemView.getTag());
                linearAttach.removeView(itemView);
                isUpload--;
                Util.printLogs("Còn mấy file: " + mailBoxData.getListAttachMent().size());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MailHelper.OpenFile(getActivity(), (AttachData) itemView.getTag());
            }
        });
        linearAttach.addView(itemView);
        new Upload().execute(attachData.getPath(), attachData.getFileName());
    }

    public void handleSelectedOrganizationResult(int type, ArrayList<PersonData> resultList) {
        if (type == Statics.ORGANIZATION_TO_ACTIVITY) {
            edtMailCreateTo.clear();
        }
        if (type == Statics.ORGANIZATION_CC_ACTIVITY) edtMailCreateCc.clear();
        if (type == Statics.ORGANIZATION_BCC_ACTIVITY) edtMailCreateBcc.clear();
        if (resultList != null && resultList.size() > 0) {
            Set<PersonData> set = new LinkedHashSet<>(resultList);
            ArrayList<PersonData> uniqueList = new ArrayList<>(set);
//
            for (PersonData personData : uniqueList) {
/*                // in organization tree -> blue
                personData.setTypeColor(2);*/

                switch (type) {
                    case Statics.ORGANIZATION_TO_ACTIVITY:
                        personData.setTypeAddress(0);
                        edtMailCreateTo.addObject(personData);
                        break;
                    case Statics.ORGANIZATION_CC_ACTIVITY:
                        personData.setTypeAddress(1);
                        edtMailCreateCc.addObject(personData);
                        break;
                    case Statics.ORGANIZATION_BCC_ACTIVITY:
                        personData.setTypeAddress(2);
                        edtMailCreateBcc.addObject(personData);
                        break;
                }

            }
            if (resultList.size() == 1) {
                edtMailCreateTo.allowCollapse(false);
            } else {
                edtMailCreateTo.allowCollapse(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void BindData(MailBoxData data) {
        if (data != null && data.getMailNo() > 0) {
            edtMailCreateContent.requestFocus();
            txtMailCreateOriginalMessage.setVisibility(View.VISIBLE);
            txtMailCreateOriginalMessage.setText(MailHelper.getOriginalMessage(getActivity(), dataCreate));
            if (task == 0 || task == 3) {
                chkMailCreateQuote.setVisibility(View.GONE);
            } else {
                chkMailCreateQuote.setVisibility(View.VISIBLE);
            }
            webMailCreateContent.setVisibility(View.VISIBLE);
            edtMailCreateSubject.setText(data.getSubject());
            webMailCreateContent.loadData(data.getContentReply(), "text/html; charset=utf-8", null);
            handleSelectedOrganizationResult(Statics.ORGANIZATION_TO_ACTIVITY, data.getListPersonDataTo());
            handleSelectedOrganizationResult(Statics.ORGANIZATION_CC_ACTIVITY, data.getListPersonDataCc());
            handleSelectedOrganizationResult(Statics.ORGANIZATION_BCC_ACTIVITY, data.getListPersonDataBcc());
            if (task == 3 || task == 0) {
                if (task == 3)
                    edtMailCreateContent.setText(Html.fromHtml(mailBoxData.getContent()));
                if (task == 0)
                    //imgAddAttach.setVisibility(View.GONE);
                    if (mailBoxData.getListAttachMent().size() > 0) {
                        linearAttach.setVisibility(View.VISIBLE);
                        for (final AttachData attachData : mailBoxData.getListAttachMent()) {
                            final AdapterMailCreateAttachLinear itemView = new AdapterMailCreateAttachLinear(getActivity(), attachData);
                            itemView.setTag(attachData);
                            itemView.getImageButtonDownLoad().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            linearAttach.addView(itemView);
                        }
                    } else {
                        linearAttach.setVisibility(View.GONE);
                    }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mail_create_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (checkDraft(1) == true) {
                    DrafMail();
                } else {
                    AlertDialogView.normalAlertDialogWithCancel(getActivity(), getString(R.string.app_name), getString(R.string.string_email_cancel), getString(R.string.yes), getString(R.string.no), new AlertDialogView.OnAlertDialogViewClickEvent() {
                        @Override
                        public void onOkClick(DialogInterface alertDialog) {
                            getActivity().finish();
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                }
                break;
            case R.id.menuMailCreateSend:
                if (isSend) {
                    if (mailBoxData != null) {
                        String subject = edtMailCreateSubject.getText().toString();
                        String content = edtMailCreateContent.getText().toString();
                        if (TextUtils.isEmpty(subject)) {
                            AlertDialogView.normalAlertDialog(getActivity(), getString(R.string.app_name), getString(R.string.string_lost_subjet), getString(R.string.string_title_mail_create_ok), null);
                            isSend = true;
                        } else if (TextUtils.isEmpty(content)) {
                            AlertDialogView.normalAlertDialog(getActivity(), getString(R.string.app_name), getString(R.string.string_lost_content), getString(R.string.string_title_mail_create_ok), null);
                            isSend = true;
                        } else {
                            if (!checkDraft(0)) {
                                AlertDialogView.normalAlertDialog(getActivity(), getString(R.string.app_name), getString(R.string.string_title_mail_create_error), getString(R.string.string_title_mail_create_ok), null);
                                isSend = true;
                            } else {
                                ArrayList<PersonData> list = new ArrayList<>();
                                list.addAll(edtMailCreateBcc.getObjects());
                                list.addAll(edtMailCreateCc.getObjects());
                                list.addAll(edtMailCreateTo.getObjects());
                                int temp = MailHelper.CheckEmail(list);
                                if (temp == 0) {
                                    SendMail(content, subject, 0);
                                } else {
                                    Util.showMessage(getString(R.string.string_email_error_new));
                                }
                            }
                        }
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case Statics.FILE_PICKER_SELECT:
                        if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                            // For JellyBean and above
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                ClipData clip = data.getClipData();

                                if (clip != null) {
                                    for (int i = 0; i < clip.getItemCount(); i++) {
                                        Uri uri = clip.getItemAt(i).getUri();
                                        GetFile(uri);
                                        // Do something with the URI
                                    }
                                }
                                // For Ice Cream Sandwich
                            } else {
                                ArrayList<String> paths = data.getStringArrayListExtra
                                        (FilePickerActivity.EXTRA_PATHS);

                                if (paths != null) {
                                    for (String path : paths) {
                                        Uri uri = Uri.parse(path);
                                        // Do something with the URI
                                        GetFile(uri);
                                    }
                                }
                            }

                        } else {
                            Uri uri = data.getData();
                            GetFile(uri);
                            // Do something with the URI
                        }
                        break;
                    case Statics.ORGANIZATION_TO_ACTIVITY:
                    case Statics.ORGANIZATION_CC_ACTIVITY:
                    case Statics.ORGANIZATION_BCC_ACTIVITY:
                        ArrayList<PersonData> resultList = data.getExtras().getParcelableArrayList(StaticsBundle.BUNDLE_LIST_PERSON);
                        handleSelectedOrganizationResult(requestCode, resultList);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onTokenAdded(Object token) {
        if (edtMailCreateTo.getObjects() != null && edtMailCreateTo.getObjects().size() > 1) {
            edtMailCreateTo.allowCollapse(true);
        }
    }

    @Override
    public void onTokenRemoved(Object token) {

    }

    @Override
    public void onClick(View v) {
        ArrayList<PersonData> selectedList = new ArrayList<>();
        switch (v.getId()) {
            case R.id.imgMailCreateOrgTo:
                selectedList.addAll(edtMailCreateTo.getObjects());
                startOrganizationActivity(selectedList, Statics.ORGANIZATION_TO_ACTIVITY);
                break;
            case R.id.imgMailCreateOrgCc:
                selectedList.addAll(edtMailCreateCc.getObjects());
                startOrganizationActivity(selectedList, Statics.ORGANIZATION_CC_ACTIVITY);
                break;

            case R.id.imgMailCreateOrgBcc:
                selectedList.addAll(edtMailCreateBcc.getObjects());
                startOrganizationActivity(selectedList, Statics.ORGANIZATION_BCC_ACTIVITY);
                break;
        }
    }


    private void startOrganizationActivity(ArrayList<PersonData> selectedList, int type) {
        Intent i = new Intent(getActivity(), OrganizationActivity.class);
        i.putParcelableArrayListExtra(StaticsBundle.BUNDLE_LIST_PERSON, selectedList);
        startActivityForResult(i, type);
    }

    private class Upload extends UploadFileToServer {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!TextUtils.isEmpty(result)) {
                if (!result.contains("fail")) {
                    isUpload++;
                } else {
                    isFail++;
                }
            } else {
            }
        }
    }

    //isTask: 0-Compose 1-DrafCompose
    public void SendMail(String content, String subject, int isTask) {
        isSend = false;
        String dataContent = "";
        dataContent = content;
        //}
        mailBoxData.setListPersonDataBcc(new ArrayList<PersonData>());
        mailBoxData.setListPersonDataCc(new ArrayList<PersonData>());
        mailBoxData.setListPersonDataTo(new ArrayList<PersonData>());
        mailBoxData.getListPersonDataBcc().addAll(edtMailCreateBcc.getObjects());
        mailBoxData.getListPersonDataTo().addAll(edtMailCreateTo.getObjects());
        mailBoxData.getListPersonDataCc().addAll(edtMailCreateCc.getObjects());
        mailBoxData.setContent(dataContent);
        mailBoxData.setSubject(subject);
        mailBoxData.setPriority("3");
        if (task == 0)
            if (dataContent != null) {
                if (tp != null && tp.size() > 0)
                    for (AttachData attachData : tp)
                        mailBoxData.getListAttachMent().remove(attachData);
            }
        if ((isUpload + isFail) == mailBoxData.getListAttachMent().size()) {
            if (isTask == 0) {
                if (task == 4 || task == 3) {
                    MailNo = mailBoxData.getMailNo();
                    mailBoxData.setMailNo(0);
                    HttpRequest.getInstance().ComposeMail(mailBoxData, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            HttpRequest.getInstance().deleteEmail(MailNo, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    if (isVisible()) {
                                        Intent intent = new Intent(getActivity(), ListEmailActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    if (isVisible()) {
                                        Util.showMessageShort(errorDto.getMessage());
                                        getActivity().finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onHTTPFail(ErrorData errorDto) {
                            if (isVisible()) {
                                Util.showMessageShort(errorDto.getMessage());
                                //getActivity().finish();
                            }
                        }
                    });
                } else if (task == 0) {
                    HttpRequest.getInstance().ForwardMail(mailBoxData, this);
                } else if (task == 1 || task == 2) {
                    HttpRequest.getInstance().ReplyMail(mailBoxData, this);
                }
            } else if (isTask == 1) {
                HttpRequest.getInstance().DrafComposeMail(mailBoxData, FragmentMailCreate.this);
            } else if (isTask == 2) {
                HttpRequest.getInstance().DrafReplyMail(mailBoxData, FragmentMailCreate.this);
            } else {
                HttpRequest.getInstance().DrafForwardMail(mailBoxData, FragmentMailCreate.this);
            }
        } else {
            String notice = String.format(getString(R.string.string_alert_send_mail_fail), (mailBoxData.getListAttachMent().size() - isUpload));
            Util.showMessage(notice);
            isSend = true;
        }
    }

    //task : 0 - Create, reply, forward
    //1 - draft
    public boolean checkDraft(int task) {
        String subject = edtMailCreateSubject.getText().toString();
        String content = edtMailCreateContent.getText().toString();
        if (task == 0) {
            return !(edtMailCreateTo.getObjects().size() == 0 && edtMailCreateBcc.getObjects().size() == 0 && edtMailCreateCc.getObjects().size() == 0);
        } else {
            return !(edtMailCreateTo.getObjects().size() == 0 && edtMailCreateBcc.getObjects().size() == 0 && edtMailCreateCc.getObjects().size() == 0
                    && TextUtils.isEmpty(subject) && TextUtils.isEmpty(content));
        }
    }

    public void DrafMail() {
        final List<String> itemList = new ArrayList<>();
        itemList.add(getString(R.string.string_save_draft));
        if (task == 3)
            itemList.add(getString(R.string.string_delete_draft));
        else {
            itemList.add(getString(R.string.string_discard_draft));
        }
        MailHelper.displaySingleChoiceListString(getActivity(), itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (itemList.get(which).equalsIgnoreCase(getString(R.string.string_save_draft))) {
                    String subject = edtMailCreateSubject.getText().toString();
                    String content = edtMailCreateContent.getText().toString();
                    if (task == 4) {
                        SendMail(content, subject, 1);
                    } else if (task == 0) {
                        SendMail(content, subject, 3);
                    } else if (task == 1 || task == 2) {
                        SendMail(content, subject, 2);
                    } else if (task == 3) {
                        getActivity().finish();
                    }
                } else {
                    if (task == 3)
                        HttpRequest.getInstance().moveEmailToTrash(mailBoxData.getMailNo(), false, new BaseHTTPCallBack() {
                            @Override
                            public void onHTTPSuccess() {
                                new Prefs().putBooleanValue(StaticsBundle.PREFS_KEY_RELOAD_LIST, true);
                                if (isVisible()) {
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onHTTPFail(ErrorData errorDto) {
                                Util.showMessageShort(getString(R.string.string_update_fail));
                            }
                        });
                    else {
                        getActivity().finish();
                    }
                }
            }
        }, getString(R.string.app_name));
    }

    @Override
    public void OnMailDetailSuccess(MailBoxData mailBoxData) {
        if (isVisible()) {
            if (mailBoxData != null)
                this.mailBoxData = mailBoxData;
            BindData(mailBoxData);
        }
    }

    @Override
    public void OnMaillDetailFail(ErrorData errorData) {
        if (isVisible()) {
            Util.showMessageShort(errorData.getMessage());
        }
    }

    @Override
    public void onGetAllOfUserSuccess(ArrayList<PersonData> list) {
        ArrayList<PersonData> test = new ArrayList<>();
        if (list != null) {
            for (PersonData personData : list) {
                if (personData.getFullName().equalsIgnoreCase(UserData.getUserInformation().getFullName())) {

                } else {
                    people.add(personData);
                }
            }
        }
    }

    @Override
    public void onGetAllOfUserFail(ErrorData errorData) {
    }

    @Override
    public void onHTTPSuccess() {
        Util.showMessage(getString(R.string.string_alert_send_mail_success));
        if (isVisible()) {
            long temp = new Prefs().getLongValue(StaticsBundle.PREFS_KEY_COMPOSE, 0);
            if (temp != 0) {
                new Prefs().putLongValue(Statics.SAVE_BOX_NO_PREF, temp);
            }

            Intent intent = new Intent(getActivity(), ListEmailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
        Util.showMessage(getString(R.string.string_alert_send_mail_fail));
        isSend = true;
    }

    @Override
    public void OnGetListOfMailAccountSuccess(ArrayList<AccountData> accountData) {
        if (accountData != null) {
            listUser.addAll(accountData);
            if (adapterMailCreateFromSpiner != null)
                adapterMailCreateFromSpiner.notifyDataSetChanged();
        }
    }

    @Override
    public void OnGetListOfMailAccountFail(ErrorData errorData) {

    }
}
