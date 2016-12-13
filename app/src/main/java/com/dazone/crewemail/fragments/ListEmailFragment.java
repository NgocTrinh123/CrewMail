package com.dazone.crewemail.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dazone.crewemail.BuildConfig;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ListEmailActivity;
import com.dazone.crewemail.activities.MoveMailBoxActivity;
import com.dazone.crewemail.activities.ToolBarActivity;
import com.dazone.crewemail.adapter.ListEmailRecyclerAdapter;
import com.dazone.crewemail.adapter.SortAdapter;
import com.dazone.crewemail.customviews.EndlessRecyclerOnScrollListener;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.MenuSortData;
import com.dazone.crewemail.helper.MailHelper;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnMailListCallBack;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Sherry on 12/17/15.
 */
public class ListEmailFragment extends BaseFragment implements BaseHTTPCallBack {

    private View mView;
    private ListEmailRecyclerAdapter mAdapter;
    private ArrayList<MailData> mMailDataList;
    private ArrayList<MailData> mSelectedMailList;
    private ArrayList<MenuSortData> listSort;
    private boolean mIsEditModeActivated;
    private RecyclerView recyclerView;
    private boolean mAllowLoadOld;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtNoData;

    // for searching
    private int mSearchType;
    private String mSearchQuery;
    private int mMailBoxNo;
    private int mMailType; // 0 normal mail box , 1 : tag mail box
    private String mMailBoxName;
    private String mMailBoxClassName;
    private Menu mMenu;
    private boolean mIsTrashBox;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    MenuItem mListCheckAll;
    private boolean isCheck = true;
    private long quickSearch = 1;
    private int sortColum = 4;
    private boolean isAscend = false;
    private SearchView searchView;
    private PopupWindow popupWindow;
    private Menu menuTemp;

    /**
     * @param mailBoxNo         mail box id
     * @param mailBoxName       mail box name
     * @param emailBoxClassName class name {@link EmailBoxStatics}
     * @param mailType          0 normal mail box , 1 : tag mail box
     * @return Fragment
     */
    public static ListEmailFragment newInstance(int mailBoxNo, String mailBoxName, String emailBoxClassName, int mailType) {
        Bundle args = new Bundle();
        args.putInt(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO, mailBoxNo);
        args.putInt("mailType", mailType);
        args.putString("mailBoxName", mailBoxName);
        args.putBoolean("isTrashBox", (EmailBoxStatics.MAIL_CLASS_TRASH_BOX.equals(emailBoxClassName)));
        args.putString("emailBoxClassName", TextUtils.isEmpty(emailBoxClassName) ? "" : emailBoxClassName);
        ListEmailFragment fragment = new ListEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMailDataList = new ArrayList<>();
        mSelectedMailList = new ArrayList<>();
        mIsEditModeActivated = false;
        setHasOptionsMenu(true);
        mAllowLoadOld = true;
        mSearchType = 0;
        mSearchQuery = "";
        mMailBoxNo = getArguments().getInt(StaticsBundle.BUNDLE_MAIL_FROM_NOTIFICATION_MAILBOX_NO);
        mMailType = getArguments().getInt("mailType");
        mMailBoxClassName = getArguments().getString("emailBoxClassName");
        mIsTrashBox = getArguments().getBoolean("isTrashBox");
        genDataSort();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.common_recycler_view, container, false);

        initRecyclerView(true);
        if (mMailBoxNo != 0) {
            initSwipeRefreshLayout();
        }
        return mView;
    }

    //isTask = 0 if load data default
    //=1 if refesh data
    private void getListEmailFromServer(long anchorMailNo) {
        mAdapter.setProgressOn(true);
        HttpRequest.getInstance().getEmailList(mMailBoxNo, anchorMailNo, Statics.DEFAULT_GET_NEW_DISPLAY_LIST_COUNT, true, mMailType, mSearchType, mSearchQuery, quickSearch, sortColum, isAscend, new OnMailListCallBack() {
            @Override
            public void onMailListSuccess(List<MailData> mailDataList, int totalEmailCount) {
                if (isVisible()) {
                    int lastPosition = 0;
                    mAdapter.setProgressOn(false);
                    try {
                        if (mMailDataList.size() > 0) {
                            lastPosition = mMailDataList.size();
                        } else {
                            lastPosition = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mMailDataList.addAll(mailDataList);
                    if (sortColum == 4) {
                        List<MailData> listTemp = TimeUtils.CheckDateTime(mMailDataList);
                        mMailDataList.clear();
                        mMailDataList.addAll(listTemp);
                    }

                    if (mMailDataList.size() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (lastPosition == 0)
                        mAdapter.notifyDataSetChanged();
                    else {
                        mAdapter.notifyItemRangeInserted(lastPosition, mailDataList.size());
                        //setToolBarTitle();
                    }
                    if (mMailDataList.size() >= totalEmailCount) {
                        mAllowLoadOld = false;
                    }
                    if (!isCheck) {
                        if (!mIsEditModeActivated) {
                            changeToolBarStatusToEditMode(true);
                        }
                        selectAllMailInList();
                    }
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                }
            }
        });
    }

    private void initRecyclerView(boolean check) {
        txtNoData = (TextView) mView.findViewById(R.id.txt_no_data);
        recyclerView = (RecyclerView) mView.findViewById(R.id.common_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new ListEmailRecyclerAdapter(this, mMailDataList, mMailBoxClassName);
        recyclerView.setAdapter(mAdapter);
        // remove default animation
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        // listener when check/un-check item
        mAdapter.setOnMailItemSelectListener(new ListEmailRecyclerAdapter.OnMailItemSelectListener() {
            @Override
            public void onMailItemSelect(MailData mailData) {
                if (!mIsEditModeActivated)
                    changeToolBarStatusToEditMode(true);
                if (mSelectedMailList.contains(mailData)) {
                    // remove if already add selected list
                    mSelectedMailList.remove(mailData);
                } else {
                    mSelectedMailList.add(mailData);
                }
                setToolBarTitle();

            }
        });
        // load more when hit bottom
        recyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (mAllowLoadOld) {
                    long lastEmailNo = 0;
                    if (mMailDataList != null && mMailDataList.size() > 0) {
                        lastEmailNo = mMailDataList.get(mMailDataList.size() - 1).getMailNo();
                    }
                    getListEmailFromServer(lastEmailNo);
                }
            }
        });
        // load data from server
        if (mMailBoxNo != 0 && check) {
            sortColum = 4;
            isAscend = false;
            getListEmailFromServer(0);
        }
    }

    public void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.common_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefeshData(true);
            }
        });
    }

    public void RefeshData(final boolean isLoad) {
        int reloadEmailCount;
        MailData anchorEmail = null;
        long anchorMailNo = 0;
        if (mMailDataList == null || mMailDataList.size() == 0) {
            if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
            mAdapter.setProgressOn(true);
            getListEmailFromServer(0);
            return;
        }

        reloadEmailCount = Statics.DEFAULT_GET_NEW_DISPLAY_LIST_COUNT;
        mMailDataList = new ArrayList<>();

        final MailData finalAnchorEmail = anchorEmail;
        HttpRequest.getInstance().getEmailList(mMailBoxNo, anchorMailNo, reloadEmailCount, true, mMailType, mSearchType, mSearchQuery, quickSearch, sortColum, isAscend, new OnMailListCallBack() {
            @Override
            public void onMailListSuccess(List<MailData> mailDataList, int totalEmailCount) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    swipeRefreshLayout.setRefreshing(false);
                    if (sortColum == 4) {
                        List<MailData> listTemp = TimeUtils.CheckDateTime(mailDataList);
                        mMailDataList.clear();
                        mMailDataList.addAll(listTemp);
                    } else {
                        mMailDataList.clear();
                        mMailDataList.addAll(mailDataList);
                    }
                    if (finalAnchorEmail != null)
                        mMailDataList.add(finalAnchorEmail);

                    if (mMailDataList.size() == 0) {
                        txtNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        txtNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    initRecyclerView(false);
                    if (isLoad == true) {
                        if (getActivity() instanceof ListEmailActivity) {
                            ((ListEmailActivity) getActivity()).reloadMenu(1);
                        }
                    }
                }
            }

            @Override
            public void onMailListFail(ErrorData errorData) {
                if (isVisible()) {
                    mAdapter.setProgressOn(false);
                    //Snackbar.make(mView, errorData.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void changeToolBarStatusToEditMode(boolean isActive) {
        if (isActive) {
            // hide drawer layout
            ((ToolBarActivity) getActivity()).setDrawerState(false);
            // displayAsFolder back button
            ((ToolBarActivity) getActivity()).displayToolBarBackButton(true, true);
            ((ToolBarActivity) getActivity()).getDrawerToggle().setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // release all selected email
                    releaseAllSelectedItem();
                }
            });
        } else {
            // show drawer layout again
            ((ToolBarActivity) getActivity()).setDrawerState(true);
            ((ToolBarActivity) getActivity()).getDrawerToggle().setToolbarNavigationClickListener(null);
        }
        mIsEditModeActivated = isActive;
        ActionBar actionbar = ((ToolBarActivity) getActivity()).getSupportActionBar();
        if (actionbar != null)
            actionbar.invalidateOptionsMenu();
    }

    /**
     * Set all checked item to un-check
     *
     * @return true if releasable else return false
     */
    public boolean releaseAllSelectedItem() {
        if (mIsEditModeActivated) {
            for (MailData mailData : mSelectedMailList) {
                mailData.setSelected(false);
                int indexOf = mMailDataList.indexOf(mailData);
                // run flip animation
                checkUnCheckItemWithAnimation(indexOf, false);
            }
            mSelectedMailList.clear();
            if (mAdapter != null) {
                mAdapter.ClearListSelect();
            }
//            ((ToolBarActivity) getActivity()).setToolBarTitle(getString(R.string.app_name));
            //changeToolBarStatusToEditMode(false);
            setToolBarTitle();

            return true;
        } else {
            return false;
        }
    }

    private void selectAllMailInList() {
        int index = 0;
        for (MailData mailData : mMailDataList) {
            if (!mailData.isSelected() && !mailData.isDeleted()) {
                mailData.setSelected(true);
                checkUnCheckItemWithAnimation(index, true);
            }
            index++;
        }
        mSelectedMailList.clear();
        mSelectedMailList.addAll(mMailDataList);
        setToolBarTitle();
    }

    private void checkUnCheckItemWithAnimation(int index, boolean isSelected) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        if (holder != null && holder instanceof ListEmailRecyclerAdapter.ItemViewHolder)
            mAdapter.showHideCheckAnimation((ListEmailRecyclerAdapter.ItemViewHolder) holder, isSelected);
    }

    private void setToolBarTitle() {
        int mSelectedCount = mSelectedMailList.size();
        if (mSelectedCount < 1) {
            changeToolBarStatusToEditMode(false);
            ((ToolBarActivity) getActivity()).setToolBarTitle(mMailBoxName);
        } else {
            if (mMenu != null) {
                MenuItem menuItem = mMenu.findItem(R.id.menu_list_set_filter);
                if (menuItem != null && mSelectedCount == 1) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }
            ((ToolBarActivity) getActivity()).setToolBarTitle(mSelectedCount + "/" + mMailDataList.size());
        }
    }

    private void setReadUnreadForSelectedList(MenuItem item) {
        if (mSelectedMailList != null && mSelectedMailList.size() > 0) {
            boolean isFirstOneRead = mSelectedMailList.get(0).isRead();
            for (MailData selectedMail : mSelectedMailList) {
                // set back to unread if first one already read , else set to read
                selectedMail.setRead(!isFirstOneRead);
            }
            mAdapter.notifyDataSetChanged();
            // set to open email
            if (item != null) {
                if (isFirstOneRead) {
                    item.setIcon(R.drawable.ic_drafts_white_24dp);
                } else {
                    item.setIcon(R.drawable.navbar_mail_ic);
                }
            }
            // call api
            HttpRequest.getInstance().updateEmailReadUnRead(!isFirstOneRead, mSelectedMailList, this);
        }
    }

    public void setEmailAsImportant(MailData mailData) {
        HttpRequest.getInstance().updateEmailImportant(!mailData.isImportant(), mailData.getMailNo(), this);
        mailData.setImportant(!mailData.isImportant());
        int adapterPosition = mMailDataList.indexOf(mailData);
        mAdapter.notifyItemChanged(adapterPosition);
    }

    public void setListOfMailAsImportant(boolean isSetImportant) {
        HttpRequest.getInstance().updateEmailImportant(isSetImportant, mSelectedMailList, this);
        for (MailData selectedMail : mSelectedMailList) {
            selectedMail.setImportant(isSetImportant);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void deleteEmailFromSelectedList() {
        final ArrayList<MailData> tempDeleteEmailList = new ArrayList<>();
        for (MailData selectedEmail : mSelectedMailList) {
            selectedEmail.setDeleted(true, getString(R.string.deleting));
            tempDeleteEmailList.add(selectedEmail);
        }
        mAdapter.ClearListSelect();
        mAdapter.notifyDataSetChanged();
        mSelectedMailList.clear();
        HttpRequest.getInstance().moveEmailToTrash(tempDeleteEmailList, mIsTrashBox, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {

                handleDeleteEmailInListSuccess(tempDeleteEmailList);
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
            }
        });
    }

    public void moveEmailFromSelectedList(int mailBoxNo) {
        final ArrayList<MailData> tempDeleteEmailList = new ArrayList<>();
        for (MailData selectedEmail : mSelectedMailList) {
            selectedEmail.setDeleted(true, getString(R.string.moving));
            tempDeleteEmailList.add(selectedEmail);
        }
        mAdapter.notifyDataSetChanged();
        mSelectedMailList.clear();
        HttpRequest.getInstance().moveEmailToBox(tempDeleteEmailList, mailBoxNo, new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                handleDeleteEmailInListSuccess(tempDeleteEmailList);
            }

            @Override
            public void onHTTPFail(ErrorData errorDto) {
                handleDeleteEmailListFail(tempDeleteEmailList);
            }
        });
    }

    private void handleDeleteEmailInListSuccess(ArrayList<MailData> tempDeleteEmailList) {
        try {
            for (MailData removeEmail : tempDeleteEmailList) {
                int index = mMailDataList.indexOf(removeEmail);
                if (index == mMailDataList.size() - 1 && index > 0 && mMailDataList.get(index - 1).getMailNo() < 0) {
                    mMailDataList.remove(index);
                    if (mMailDataList.size() > 1)
                        mMailDataList.remove(index - 1);
                } else if (index > 0 && mMailDataList.get(index - 1).getMailNo() < 0 && mMailDataList.get(index + 1).getMailNo() < 0) {
                    mMailDataList.remove(index);
                    mMailDataList.remove(index - 1);
                } else {
                    mMailDataList.remove(index);
                }
                mAdapter.notifyItemRemoved(index);
            }
            if (mMailDataList.size() > 1) {
                mEndlessRecyclerOnScrollListener.reset(0, false);
                recyclerView.setAdapter(mAdapter);
            } else {
                //getListEmailFromServer(0);
                RefeshData(false);
            }
            if (mSelectedMailList.size() == 0)
                changeToolBarStatusToEditMode(false);
            setToolBarTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteEmailListFail(ArrayList<MailData> tempDeleteEmailList) {
        for (MailData tempMail : tempDeleteEmailList) {
            tempMail.setDeleted(false, "");
            tempMail.setSelected(false);
        }
        mAdapter.notifyDataSetChanged();
        tempDeleteEmailList.clear();
        if (mSelectedMailList.size() == 0)
            changeToolBarStatusToEditMode(false);
    }

    public void GetTagMenu(boolean isSkipCache) {
        final LinkedHashMap<String, Object> menuList = null;
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        if (!isSkipCache && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            ShowDialog(MailHelper.convertJsonStringToMap(splitString[1]));
        } else {
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    if (isVisible()) {
                        ShowDialog(menuMap);
                    }
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    if (isVisible()) {
                        ShowDialog(menuList);
                    }
                }
            });
        }
    }

    public void ShowDialog(LinkedHashMap menuMap) {
        final List<MailTagMenuData> itemList = new ArrayList<>();
        if (menuMap != null) {
            Iterator<?> keySets = menuMap.keySet().iterator();
            while (keySets.hasNext()) {
                String key = (String) keySets.next();
                List<?> menuList = (List<?>) menuMap.get(key);
                for (Object menuData : menuList) {
                    if (menuData instanceof MailTagMenuData) {
                        itemList.add((MailTagMenuData) menuData);
                    }
                }
            }
        }
        itemList.add(0, new MailTagMenuData(0, 0, getString(R.string.string_title_mail_no_tag), false));
        MailHelper.displaySingleChoiceList(getActivity(), itemList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (mSelectedMailList != null && mSelectedMailList.size() > 0)
                    HttpRequest.getInstance().updateTagOfMail(itemList.get(which).getTagNo(), mSelectedMailList, new BaseHTTPCallBack() {
                        @Override
                        public void onHTTPSuccess() {
                            if (isVisible()) {
                                for (MailData selectedMail : mSelectedMailList) {
                                    selectedMail.setmImageNo(itemList.get(which).getImageNo());
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            Util.showMessage(getString(R.string.string_success));
                        }

                        @Override
                        public void onHTTPFail(ErrorData errorDto) {
                            Util.showMessage(getString(R.string.string_update_fail));
                        }
                    });
            }
        }, getString(R.string.app_name));
    }

    public PopupWindow mPopupWindow() {
        final PopupWindow popupWindow = new PopupWindow(getActivity());
        ListView listViewDogs = new ListView(getActivity());
        listViewDogs.setDividerHeight(0);
        listViewDogs.setPadding(0, 0, 0, 10);
        listViewDogs.setBackgroundResource(R.color.background_holo_light);
        SortAdapter tagAdapter = new SortAdapter(getActivity(), R.layout.tag, listSort);
        listViewDogs.setAdapter(tagAdapter);

        listViewDogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuSortData menuSortData = listSort.get(position);
                sortColum = menuSortData.getSort();
                int index = MailHelper.getSelectPosition(listSort);
                if (index == position) {
                    if (menuSortData.getIsCheck() == 1) {
                        menuSortData.setIsCheck(2);
                        isAscend = true;
                    } else {
                        menuSortData.setIsCheck(1);
                        isAscend = false;
                    }
                } else {
                    menuSortData.setIsCheck(1);
                    listSort.get(index).setIsCheck(0);
                    isAscend = false;
                }
                RefeshData(false);
                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        //popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.background_menu_tag));
        popupWindow.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        popupWindow.setWidth((int) getResources().getDimension(R.dimen.dimen_180_360));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(listViewDogs);
        return popupWindow;
    }

    public void genDataSort() {
        listSort = new ArrayList<>();
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_name), 0, 1));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_to), 0, 2));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_title), 0, 3));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_reg_date), 1, 4));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_size), 0, 5));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_important), 0, 6));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_read), 0, 7));
        listSort.add(new MenuSortData(getString(R.string.string_title_menu_file), 0, 8));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (mIsEditModeActivated) {
            mView.findViewById(R.id.common_swipe_refresh).setEnabled(false);
            inflater.inflate(R.menu.list_mail_menu, menu);
            // set read/unread icon based on first item select on list
            try {
                if (mSelectedMailList.get(0).isRead()) {
                    menu.findItem(R.id.menu_list_set_unread).setIcon(R.drawable.navbar_mail_ic);
                } else {
                    menu.findItem(R.id.menu_list_set_unread).setIcon(R.drawable.ic_drafts_white_24dp);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG)
                    e.printStackTrace();
            }
            mMenu = menu;
        } else {
            mMenu = null;

            if (mView == null) {
                getActivity().finish();
            } else {
                mView.findViewById(R.id.common_swipe_refresh).setEnabled(true);
            }
            inflater.inflate(R.menu.search_menu, menu);
            menuTemp = menu;

            mListCheckAll = menu.findItem(R.id.menu_list_check_all);
            mListCheckAll.setVisible(false);
            /*MenuItem menuItem = menu.findItem(R.id.menu_more_search);
            img = menuItem.getActionView();*/
            try {
                //Search
                SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView = (SearchView) menu.findItem(R.id.search).getActionView();
//            ComponentName cn = new ComponentName(getActivity(), SearchEmailResultActivity.class);
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(true);
                SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }

                    public boolean onQueryTextSubmit(String query) {
                        // handle search action here
                        mSearchType = 1;
                        mSearchQuery = query;
                        RefeshData(false);
                        return true;
                    }
                };
                searchView.setOnQueryTextListener(queryTextListener);
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        mSearchQuery = "";
                        RefeshData(false);
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_list_check_all:
                if (isCheck) {
                    if (!mIsEditModeActivated) {
                        changeToolBarStatusToEditMode(true);
                    }
                    selectAllMailInList();
                    isCheck = false;
                } else {
                    releaseAllSelectedItem();
                    if (mAdapter != null) {
                        mAdapter.ClearListSelect();
                    }
                    isCheck = true;
                }
                break;
            case R.id.menu_list_delete:
                deleteEmailFromSelectedList();
                break;
            case R.id.menu_list_set_unread:
                setReadUnreadForSelectedList(item);
                break;
            case R.id.menu_mark_important:
                MenuItem importantMenuItem = mMenu.findItem(R.id.menu_mark_important);
                if (importantMenuItem.getTitle().equals(getString(R.string.mark_not_important))) {
                    setListOfMailAsImportant(false);
                } else {
                    setListOfMailAsImportant(true);
                }
                break;
            case R.id.menu_more:
                MenuItem menuItem = mMenu.findItem(R.id.menu_mark_important);
                boolean isAllImportant = true;
                for (MailData selectedMail : mSelectedMailList) {
                    if (!selectedMail.isImportant()) {
                        isAllImportant = false;
                        break;
                    }
                }
                if (isAllImportant) {
                    menuItem.setTitle(R.string.mark_not_important);
                } else {
                    menuItem.setTitle(R.string.mark_as_important);
                }
                break;

            case R.id.menu_move_to:
                Intent intent = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent.putExtra(StaticsBundle.BUNDLE_TASK, 1);
                startActivityForResult(intent, Statics.EMAIL_MOVE_ACTIVITY);
                break;

            case R.id.menu_tag:
                GetTagMenu(false);
                break;

            case R.id.menu_filter_address:
                Intent intent1 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent1.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent1, Statics.EMAIL_FILTER_ADDRESS_ACTIVITY);
                break;

            case R.id.menu_filter_sender:
                Intent intent2 = new Intent(getActivity(), MoveMailBoxActivity.class);
                intent2.putExtra(StaticsBundle.BUNDLE_TASK, 2);
                startActivityForResult(intent2, Statics.EMAIL_FILTER_SENDER_ACTIVITY);
                break;
           /* case R.id.menu_more_search:
                Util.printLogs(img+"");
                GetTagMenu(false, swipeRefreshLayout);
                break;*/
            case R.id.search:
                if (searchView != null) {
                    if (menuTemp != null) {
                        menuTemp.findItem(R.id.menu_sort).setVisible(false);
                    }
                    ((ListEmailActivity) getActivity()).setFab(false);
                    item.expandActionView();
                    searchView.setQueryHint(getString(R.string.string_title_menu_title));
                    ((ListEmailActivity) getActivity()).setDrawerState(false);
                    MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            ((ListEmailActivity) getActivity()).setFab(true);
                            searchView.setQuery("", false);
                            mSearchQuery = "";
                            RefeshData(false);
                            if (menuTemp != null) {
                                menuTemp.findItem(R.id.menu_sort).setVisible(true);
                            }
                            ((ListEmailActivity) getActivity()).setDrawerState(true);
                            return true;
                        }
                    });
                }
                break;
            case R.id.menu_sort:
                popupWindow = mPopupWindow();
                popupWindow.showAtLocation(swipeRefreshLayout, Gravity.RIGHT | Gravity.TOP, 0, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int boxNoAfter, boxNoBefore = 0;
        String address = null, sender = null;

        if (resultCode == Activity.RESULT_OK) {
            if (mSelectedMailList != null && mSelectedMailList.size() == 1) {
                boxNoBefore = mSelectedMailList.get(0).getmBoxNo();
                address = mSelectedMailList.get(0).getFromEmail();
                sender = mSelectedMailList.get(0).getFromName();
            }
            switch (requestCode) {
                case Statics.EMAIL_MOVE_ACTIVITY:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        moveEmailFromSelectedList(b.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO));
                    }
                    break;
                case Statics.EMAIL_FILTER_ADDRESS_ACTIVITY:
                    Bundle bc = data.getExtras();
                    if (bc != null) {
                        boxNoAfter = bc.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(address)) {
                            final ArrayList<MailData> temp = new ArrayList<>();
                            temp.addAll(mSelectedMailList);
                            mSelectedMailList.clear();
                            HttpRequest.getInstance().filterAddressMail(boxNoBefore, boxNoAfter, address, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    handleDeleteEmailInListSuccess(temp);
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
                case Statics.EMAIL_FILTER_SENDER_ACTIVITY:
                    Bundle bd = data.getExtras();
                    if (bd != null) {
                        boxNoAfter = bd.getInt(StaticsBundle.BUNDLE_MAIL_BOX_NO);
                        if (!TextUtils.isEmpty(sender)) {
                            final ArrayList<MailData> temp = new ArrayList<>();
                            temp.addAll(mSelectedMailList);
                            mSelectedMailList.clear();
                            HttpRequest.getInstance().filterSenderMail(boxNoBefore, boxNoAfter, sender, new BaseHTTPCallBack() {
                                @Override
                                public void onHTTPSuccess() {
                                    handleDeleteEmailInListSuccess(temp);
                                    Util.showMessage(getString(R.string.string_title_filter_success));
                                }

                                @Override
                                public void onHTTPFail(ErrorData errorDto) {
                                    Util.showMessage(getString(R.string.string_title_filter_fail));
                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onHTTPSuccess() {
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
    }

}
