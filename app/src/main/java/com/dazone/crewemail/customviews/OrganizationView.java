package com.dazone.crewemail.customviews;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.data.UserData;
import com.dazone.crewemail.database.OrganizationUserDBHelper;
import com.dazone.crewemail.fragments.OrganizationFragment;
import com.dazone.crewemail.interfaces.OnGetAllOfUser;
import com.dazone.crewemail.interfaces.OnOrganizationSelectedEvent;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sherry on 12/31/15.
 */
public class OrganizationView {

    private ArrayList<PersonData> mPersonList = new ArrayList<>();
    private ArrayList<PersonData> mSelectedPersonList;
    private Context mContext;
    private int displayType = 0; // 0 folder structure , 1
    private OnOrganizationSelectedEvent mSelectedEvent;
    private boolean loaded = false;

    public OrganizationView(Context context, ArrayList<PersonData> selectedPersonList, boolean isDisplaySelectedOnly, ViewGroup viewGroup) {
        this.mContext = context;
        if (selectedPersonList != null) {
            this.mSelectedPersonList = selectedPersonList;
        } else {
            this.mSelectedPersonList = new ArrayList<>();
        }
        if (isDisplaySelectedOnly) {
            initSelectedList(selectedPersonList, viewGroup);
        } else {
            initWholeOrganization(viewGroup);
        }

    }

    /**
     * this function automatically set all to selected
     *
     * @param selectedPersonList
     */
    private void initSelectedList(ArrayList<PersonData> selectedPersonList, ViewGroup viewGroup) {
        mPersonList = new ArrayList<>();
        for (PersonData selectedPerson : selectedPersonList) {
            selectedPerson.setIsCheck(true);
            if (selectedPerson.getType() == 2) {
                mPersonList.add(selectedPerson);
            }
        }
        createRecursiveList(mPersonList, mPersonList);
        displayAsFolder(viewGroup);
    }

    private void initWholeOrganization(final ViewGroup viewGroup) {
        PersonData.getDepartmentAndUser(new OnGetAllOfUser() {
            @Override
            public void onGetAllOfUserSuccess(ArrayList<PersonData> list) {

                if (!loaded) {
                    loaded = true;
                    mPersonList = new ArrayList<>(list);
                    // set selected for list before create recursive list
                    for (PersonData selectedPerson : mSelectedPersonList) {
                        /*int indexOf = mPersonList.indexOf(selectedPerson);
                        if (indexOf != -1) {
                            mPersonList.get(indexOf).setIsCheck(true);
                        }*/

                        for (PersonData personData : mPersonList) {
                            if (selectedPerson.getType() == 1 && personData.getType() == 1) {
                                if (personData.getDepartNo() == selectedPerson.getDepartNo()) {
                                    personData.setIsCheck(true);
                                }
                            }
                            if (selectedPerson.getType() == 2 && personData.getType() == 2) {
                                if (personData.getUserNo() == selectedPerson.getUserNo()) {
                                    personData.setIsCheck(true);
                                }
                            }
                        }
                    }
                    createRecursiveList(list, mPersonList);

                    displayAsFolder(viewGroup);
                }
            }

            @Override
            public void onGetAllOfUserFail(ErrorData errorData) {

            }
        }, 0);
    }

    public void setOnSelectedEvent(OnOrganizationSelectedEvent selectedEvent) {
        this.mSelectedEvent = selectedEvent;
    }

    private void createRecursiveList(ArrayList<PersonData> list, ArrayList<PersonData> parentList) {

        //create recursive list
        Iterator<PersonData> iter = list.iterator();
        while (iter.hasNext()) {
            PersonData tempPerson = iter.next();
            for (PersonData person : parentList) {
                if (person.getType() == 1) {
                    if (tempPerson.getType() == 1 && person.getDepartNo() == tempPerson.getDepartmentParentNo()) {
                        // department compare by departNo and parentNo
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    } else if (tempPerson.getType() == 2 && person.getDepartNo() == tempPerson.getDepartNo()) {
                        // member , compare by departNo and departNo
                        person.addChild(tempPerson);
                        iter.remove();
                        parentList.remove(tempPerson);
                        break;
                    }
                    if (person.getPersonList() != null && person.getPersonList().size() > 0) {
                        // not in root list , search in child list
                        ArrayList<PersonData> test = new ArrayList<>();
                        test.add(tempPerson);
                        createRecursiveList(test, person.getPersonList());
                    }
                }
            }
        }
    }


    public void setDisplayType(int type) {
        this.displayType = type;
    }

    public void displayAsFolder(ViewGroup viewGroup) {
        this.displayType = 0;
        for (PersonData personData : mPersonList) {
            if (personData.getType() == 2) {
                for (PersonData department : OrganizationUserDBHelper.getDepartments(HttpRequest.sRootLink)) {
                    if (department.getDepartNo() == personData.getDepartNo()) {
                        draw(personData, viewGroup, false, 0);
                        break;
                    }
                }
            } else {
                if (personData.getDepartmentParentNo() == 0) {
                    draw(personData, viewGroup, false, 0);
                }
            }
        }
    }

    public void displayMatchQuery(ViewGroup viewGroup, String query) {
        ArrayList<PersonData> resultList = new ArrayList<>();
        getPersonDataWithQuery(query, resultList, mPersonList);
        for (PersonData personData : resultList) {
            draw(personData, viewGroup, false, 0);
        }
    }

    private void getPersonDataWithQuery(String query, ArrayList<PersonData> searchResultList, ArrayList<PersonData> searchList) {
        for (PersonData personData : searchList) {
            if (personData.getType() == 2) {
                if ((personData.getFullName() != null && personData.getFullName().toLowerCase().contains(query))
                        || (personData.getEmail() != null && personData.getEmail().toLowerCase().contains(query))) {
                    boolean isAdd = true;
                    for (PersonData userAdded : searchResultList) {
                        if (userAdded.getUserNo() == personData.getUserNo()) {
                            isAdd = false;
                        }
                    }
                    if (isAdd) {
                        searchResultList.add(personData);
                    }
                }
            }
            if (personData.getPersonList() != null && personData.getPersonList().size() > 0) {
                getPersonDataWithQuery(query, searchResultList, personData.getPersonList());
            }
        }
    }

    private void draw(final PersonData personData, final ViewGroup layout, final boolean checked, final int iconMargin) {
        final LinearLayout child_list;
        final LinearLayout iconWrapper;
        final ImageView avatar;
        final ImageView folderIcon;
        final TextView name;
        final CheckBox row_check;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_organization, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        layout.addView(view);
        child_list = (LinearLayout) view.findViewById(R.id.child_list);
        avatar = (ImageView) view.findViewById(R.id.avatar);
        folderIcon = (ImageView) view.findViewById(R.id.icon);
        iconWrapper = (LinearLayout) view.findViewById(R.id.icon_wrapper);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconWrapper.getLayoutParams();
        if (displayType == 0) // set margin for icon if it's company type
        {
            params.leftMargin = iconMargin;
        }
        iconWrapper.setLayoutParams(params);
        name = (TextView) view.findViewById(R.id.name);
        row_check = (CheckBox) view.findViewById(R.id.row_check);
        row_check.setChecked(personData.isCheck());
       /* if(personData.getUserNo()==UserData.getUserInformation().getId())
        {
            row_check.setChecked(false);
            row_check.setEnabled(false);
        }*/
        String nameString = personData.getFullName();
        if (personData.getType() == 2) {
            String url = new Prefs().getServerSite() + personData.getUrlAvatar();
            //avatar.setImageURI(uri);
            ImageLoader.getInstance().displayImage(url, avatar, Statics.options2);
            /*GenericDraweeHierarchy hierarchy = avatar.getHierarchy();
            hierarchy.setPlaceholderImage(R.drawable.avatar_l);*/
            nameString += !TextUtils.isEmpty(personData.getPositionName()) ? " (" + personData.getPositionName() + ")" : "";
            folderIcon.setVisibility(View.GONE);
        } else {
            avatar.setVisibility(View.GONE);
            folderIcon.setVisibility(View.VISIBLE);
        }
        name.setText(nameString);

        final int tempMargin = iconMargin + Util.getDimenInPx(R.dimen.activity_login_user_margin);
        row_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && personData.getType() == 2) {
//                    unCheckFather(dto);
                    ViewGroup parent = ((ViewGroup) layout.getParent());
                    unCheckBoxParent(parent);
                } else {
                    if (buttonView.getTag() != null && !(Boolean) buttonView.getTag()) {
                        buttonView.setTag(true);
                    } else {
                        personData.setIsCheck(isChecked);
                        if (personData.getPersonList() != null && personData.getPersonList().size() != 0) {
                            int index = 0;
                            for (PersonData dto1 : personData.getPersonList()) {

                                dto1.setIsCheck(isChecked);
                                View childView = child_list.getChildAt(index);
                                CheckBox childCheckBox = (CheckBox) childView.findViewById(R.id.row_check);
                                if (childCheckBox != null) {
                                    if (childCheckBox.isEnabled()) {
                                        childCheckBox.setChecked(dto1.isCheck());
                                    }

                                } else {
                                    break;
                                }
                                index++;
                            }
                        }
                    }
                }
                if (mSelectedEvent != null) {
                    mSelectedEvent.onOrganizationCheck(isChecked, personData);
                }
            }
        });

        String temp = personData.getDepartNo() + personData.getFullName();
        if (!TextUtils.isEmpty(temp)) {
            if (new Prefs().getBooleanValue(temp, true)) {
                child_list.setVisibility(View.VISIBLE);
            } else {
                child_list.setVisibility(View.GONE);
            }
        }

        if (personData.getPersonList() != null && personData.getPersonList().size() != 0) {
            folderIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHideSubMenuView(child_list, folderIcon, personData);
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showHideSubMenuView(child_list, folderIcon, personData);
                }
            });

            for (PersonData dto1 : personData.getPersonList()) {
                draw(dto1, child_list, false, tempMargin);
            }
        }
    }


//    public ArrayList<PersonData> getSelectedData(){
//
//    }

    private void unCheckBoxParent(ViewGroup view) {
        if (view.getId() == R.id.item_org_main_wrapper || view.getId() == R.id.item_org_wrapper) {
            CheckBox parentCheckBox = (CheckBox) view.findViewById(R.id.row_check);
            if (parentCheckBox.isChecked()) {
                parentCheckBox.setTag(false);
                parentCheckBox.setChecked(false);
            }
            if ((view.getParent()).getParent() instanceof ViewGroup) {
                try {
                    ViewGroup parent = (ViewGroup) (view.getParent()).getParent();
                    unCheckBoxParent(parent);
                } catch (Exception e) {
                }
            }
        }
    }

    private void showHideSubMenuView(LinearLayout child_list, ImageView icon, PersonData personData) {
        String temp = personData.getDepartNo() + personData.getFullName();
        if (child_list.getVisibility() == View.VISIBLE) {
            child_list.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.ic_folder_close);
            new Prefs().putBooleanValue(temp, false);

        } else {
            child_list.setVisibility(View.VISIBLE);
            icon.setImageResource(R.drawable.ic_folder_open);
            new Prefs().putBooleanValue(temp, true);

        }
    }
}
