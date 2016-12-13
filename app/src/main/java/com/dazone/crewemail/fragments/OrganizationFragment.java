package com.dazone.crewemail.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.OrganizationActivity;
import com.dazone.crewemail.customviews.OrganizationView;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.interfaces.OnOrganizationSelectedEvent;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.Util;

import java.util.ArrayList;

/**
 * Created by Dinh Huynh on 12/30/15.
 */
public class OrganizationFragment extends BaseFragment implements View.OnClickListener, OnOrganizationSelectedEvent {

    public static OrganizationFragment fragment;
    public boolean isSearching = false;
    private View mView;
    private ArrayList<PersonData> selectedPersonList;
    private TextView sharedPersonTv;
    private LinearLayout mSharePersonContent;
    private EditText searchEditText;
    private OrganizationView orgView;
    private boolean mIsDisplaySelectedOnly;

    public static OrganizationFragment newInstance(ArrayList<PersonData> selectedPerson, boolean isDisplaySelectedOnly) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("selectedPerson", selectedPerson);
        args.putBoolean("isDisplaySelectedOnly", isDisplaySelectedOnly);
        OrganizationFragment fragment = new OrganizationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        selectedPersonList = getArguments().getParcelableArrayList("selectedPerson");
        mIsDisplaySelectedOnly = getArguments().getBoolean("isDisplaySelectedOnly");
        if (selectedPersonList == null) {
            selectedPersonList = new ArrayList<>();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_organization, container, false);
        sharedPersonTv = (TextView) mView.findViewById(R.id.shared_person_name);
        mSharePersonContent = (LinearLayout) mView.findViewById(R.id.share_list_content);
        searchEditText = (EditText) mView.findViewById(R.id.share_search_edit_text);

        mView.findViewById(R.id.share_company_btn).setOnClickListener(this);
        mView.findViewById(R.id.share_search_btn).setOnClickListener(this);
        mView.findViewById(R.id.share_search_action).setOnClickListener(this);

        initSearchView();

        initOrganizationTree();

        setSelectedPersonName();
        initShareTextViewAction();
        return mView;
    }

    private void initOrganizationTree() {
        orgView = new OrganizationView(getActivity(), selectedPersonList, mIsDisplaySelectedOnly, mSharePersonContent);
        orgView.setOnSelectedEvent(this);
        if (!mIsDisplaySelectedOnly) {
            mView.findViewById(R.id.share_information_wrapper).setVisibility(View.VISIBLE);
//            orgView.displayWholeOrganization(mSharePersonContent);
        } else {
            mView.findViewById(R.id.share_information_wrapper).setVisibility(View.GONE);
//            orgView.displaySelectedList(selectedPersonList, mSharePersonContent);
        }

    }

    private void initShareTextViewAction() {
        sharedPersonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(getActivity(), OrganizationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(StaticsBundle.BUNDLE_LIST_PERSON, selectedPersonList);
                bundle.putBoolean(StaticsBundle.BUNDLE_ORG_DISPLAY_SELECTED_ONLY, true);
                intent.putExtras(bundle);
                startActivityForResult(intent, Statics.ORGANIZATION_DISPLAY_SELECTED_ACTIVITY);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Statics.ORGANIZATION_DISPLAY_SELECTED_ACTIVITY:
                    ArrayList<PersonData> resultList = data.getExtras().getParcelableArrayList(StaticsBundle.BUNDLE_LIST_PERSON);
                    selectedPersonList.clear();
                    if (resultList != null)
                        selectedPersonList.addAll(resultList);
                    mSharePersonContent.removeAllViews();
                    orgView = new OrganizationView(getActivity(), selectedPersonList, mIsDisplaySelectedOnly, mSharePersonContent);
                    orgView.setOnSelectedEvent(this);
                    break;
            }
        }
    }

    private void initSearchView() {
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                    } catch (Exception e) {
                    }
                    handleSearchAction(true);
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        //&& id != R.id.share_save&& id != R.id.imv_share
        if (id != R.id.share_search_action) {
            // this action set for tab only
            mView.findViewById(R.id.share_company_btn).setEnabled(true);
            mView.findViewById(R.id.share_search_btn).setEnabled(true);
            v.setEnabled(false);
            mSharePersonContent.removeAllViews();
        }
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        } catch (Exception e) {
        }
        switch (id) {
            case R.id.share_company_btn:
                isSearching = false;
                mView.findViewById(R.id.share_search_wrapper).setVisibility(View.GONE);
                orgView.displayAsFolder(mSharePersonContent);
                break;
            case R.id.share_search_btn:
                isSearching = true;
                mView.findViewById(R.id.share_search_wrapper).setVisibility(View.VISIBLE);
                handleSearchAction(false);
                break;
            case R.id.share_search_action:
                handleSearchAction(true);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.check_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra(StaticsBundle.BUNDLE_LIST_PERSON, selectedPersonList);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                break;
        }
        return true;
    }

    private void handleSearchAction(boolean isCheckRequired) {
        String query = searchEditText.getText().toString().trim().toLowerCase();
        mSharePersonContent.removeAllViews();
        if (TextUtils.isEmpty(query.trim())) { // validate data if empty or not
            if (isCheckRequired) {
                searchEditText.setError(getString(R.string.search_field_empty));
                searchEditText.requestFocus();
            }
        } else {
            orgView.setDisplayType(1);
            orgView.displayMatchQuery(mSharePersonContent, query);
        }
    }

    private void unCheckParentData(PersonData personData) {
        if (mIsDisplaySelectedOnly) {

            PersonData needRemovePerson = null;
            for (PersonData selectedPerson : selectedPersonList) {
                if (personData.getType() == 2 && selectedPerson.getType() == 1 && selectedPerson.getDepartNo() == personData.getDepartNo()) {
                    needRemovePerson = selectedPerson;
                    break;
                } else if (personData.getType() == 1 && selectedPerson.getType() == 1 && selectedPerson.getDepartNo() == personData.getDepartmentParentNo()) {
                    needRemovePerson = selectedPerson;
                    break;
                }
            }
            if (needRemovePerson != null) {
                selectedPersonList.remove(needRemovePerson);
                if (needRemovePerson.getDepartmentParentNo() > 0) {
                    unCheckParentData(needRemovePerson);
                }
            }

        }
    }

    @Override
    public void onOrganizationCheck(boolean isCheck, PersonData personData) {
        int indexOf = selectedPersonList.indexOf(personData);

        if (indexOf != -1) {
            if (!isCheck) {
                selectedPersonList.remove(indexOf);
                unCheckParentData(personData);
            } else {
                selectedPersonList.get(indexOf).setIsCheck(true);
            }
        } else {
            if (isCheck) {
                selectedPersonList.add(personData);
            }
        }
        if (!mIsDisplaySelectedOnly) {
            setSelectedPersonName();
        }
    }

    private void setSelectedPersonName() {
        String shareString = "";
        for (PersonData selectedPerson : selectedPersonList) {
            if (!TextUtils.isEmpty(shareString)) {
                shareString += "; ";
            }
            shareString += selectedPerson.getFullName();
        }
        if (TextUtils.isEmpty(shareString)) {
            sharedPersonTv.setVisibility(View.GONE);
        } else {
            sharedPersonTv.setVisibility(View.VISIBLE);
            sharedPersonTv.setText(shareString);
        }
    }
}
