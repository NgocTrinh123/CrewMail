package com.dazone.crewemail.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.dazone.crewemail.BuildConfig;


/**
 * Created by maidinh on 8/13/2015.
 */
public class AppContentProvider extends ContentProvider {

    /* database helper */
    AppDatabaseHelper mDatabaseHelper;

    /* key for uri matches */
    private static final int GET_USER_KEY = 1;
    private static final int GET_USER_ROW_KEY = 2;

//    private static final int GET_COMPANY_INFO_KEY = 3;
//    private static final int GET_COMPANY_INFO_ROW_KEY = 4;

    private static final int GET_SERVER_SITE_KEY = 5;
    private static final int GET_SERVER_SITE_ROW_KEY = 6;

//    private static final int GET_GROUP_KEY = 7;
//    private static final int GET_GROUP_ROW_KEY = 8;

//    private static final int GET_NOTE_LIST_KEY = 9;
//    private static final int GET_NOTE_LIST_ROW_KEY = 10;

//    private static final int GET_ATTACHMENT_KEY = 11;
//    private static final int GET_ATTACHMENT_ROW_KEY = 12;

    private static final int GET_ORGANIZATION_USER_KEY = 13;
    private static final int GET_ORGANIZATION_USER_ROW_KEY = 14;

    private static final int GET_ACCOUNT_USER_KEY = 15;
    private static final int GET_ACCOUNT_USER_ROW_KEY = 16;

    /* authority */
    private static final String AUTHORITY = BuildConfig.APPLICATION_ID+".provider";

    /* Uri Matches */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /* path */
    private static final String GET_USER_PATH = "request";
//    private static final String GET_COMPANY_INFO_PATH = "request_company_info";
    private static final String GET_SERVER_SITE_PATH = "request_server_site";
    private static final String GET_ORGANIZATION_USER_PATH = "request_organization_user";
    private static final String GET_ACCOUNT_PATH = "request_acount";
//    private static final String GET_NOTE_LIST_PATH = "request_note_list";
//    private static final String GET_LIST_GRID_ITEM_PATH = "request_grid_item_list";
//    private static final String GET_ATTACHMENT_PATH = "request_attachment_list";


    /* content uri */
    public static final Uri GET_USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_USER_PATH);
//    public static final Uri GET_COMPANY_INFO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_COMPANY_INFO_PATH);
    public static final Uri GET_SERVER_SITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_SERVER_SITE_PATH);
    public static final Uri GET_SERVER_ORGANIZATION_USER_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ORGANIZATION_USER_PATH);
    public static final Uri GET_ACCOUNT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ACCOUNT_PATH);
//    public static final Uri GET_NOTE_LIST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_NOTE_LIST_PATH);
//    public static final Uri GET_GRID_ITEM_LIST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_LIST_GRID_ITEM_PATH);
//    public static final Uri GET_ATTACHMENT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + GET_ATTACHMENT_PATH);

    static {
        sUriMatcher.addURI(AUTHORITY, GET_USER_PATH, GET_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_USER_PATH + "/#", GET_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_ORGANIZATION_USER_PATH, GET_ORGANIZATION_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_ORGANIZATION_USER_PATH + "/#", GET_ORGANIZATION_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_ACCOUNT_PATH, GET_ACCOUNT_USER_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_ACCOUNT_PATH + "/#", GET_ACCOUNT_USER_ROW_KEY);

        sUriMatcher.addURI(AUTHORITY, GET_SERVER_SITE_PATH, GET_SERVER_SITE_KEY);
        sUriMatcher.addURI(AUTHORITY, GET_SERVER_SITE_PATH + "/#", GET_SERVER_SITE_ROW_KEY);

//        sUriMatcher.addURI(AUTHORITY, GET_GROUP_PATH, GET_GROUP_KEY);
//        sUriMatcher.addURI(AUTHORITY, GET_GROUP_PATH + "/#", GET_GROUP_ROW_KEY);

//        sUriMatcher.addURI(AUTHORITY, GET_NOTE_LIST_PATH, GET_NOTE_LIST_KEY);
//        sUriMatcher.addURI(AUTHORITY, GET_NOTE_LIST_PATH + "/#", GET_NOTE_LIST_ROW_KEY);

//        sUriMatcher.addURI(AUTHORITY, GET_LIST_GRID_ITEM_PATH, GET_GRID_ITEM_LIST_KEY);
//        sUriMatcher.addURI(AUTHORITY, GET_LIST_GRID_ITEM_PATH + "/#", GET_GRID_ITEM_LIST_ROW_KEY);

//        sUriMatcher.addURI(AUTHORITY, GET_ATTACHMENT_PATH, GET_ATTACHMENT_KEY);
//        sUriMatcher.addURI(AUTHORITY, GET_ATTACHMENT_PATH + "/#", GET_ATTACHMENT_ROW_KEY);

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_deleted = 0;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                row_deleted = db.delete(UserDBHelper.TABLE_NAME, UserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_USER_KEY:
                row_deleted = db.delete(UserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_ROW_KEY:
                row_deleted = db.delete(OrganizationUserDBHelper.TABLE_NAME, OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_KEY:
                row_deleted = db.delete(OrganizationUserDBHelper.TABLE_NAME, selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_ROW_KEY:
                row_deleted = db.delete(AccountUserDBHelper.TABLE_NAME, AccountUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
//            case GET_COMPANY_INFO_KEY:
//                row_deleted = db.delete(CompanyDBHelper.TABLE_NAME, selection, selectionArgs);
//                break;

            case GET_SERVER_SITE_ROW_KEY:
                row_deleted = db.delete(ServerSiteDBHelper.TABLE_NAME, ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_deleted = db.delete(ServerSiteDBHelper.TABLE_NAME, selection, selectionArgs);
                break;

//            case GET_GROUP_ROW_KEY:
//                row_deleted = db.delete(GroupDBHelper.TABLE_NAME, GroupDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_GROUP_KEY:
//                row_deleted = db.delete(GroupDBHelper.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case GET_NOTE_LIST_ROW_KEY:
//                row_deleted = db.delete(NoteListDBHelper.TABLE_NAME, NoteListDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_NOTE_LIST_KEY:
//                row_deleted = db.delete(NoteListDBHelper.TABLE_NAME, selection, selectionArgs);
//                break;



//            case GET_GRID_ITEM_LIST_ROW_KEY:
//                row_deleted = db.delete(CrewMainGridDbHelper.TABLE_NAME, CrewMainGridDbHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_GRID_ITEM_LIST_KEY:
//                row_deleted = db.delete(CrewMainGridDbHelper.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case GET_ATTACHMENT_ROW_KEY:
//                row_deleted = db.delete(AttachmentDBHelper.TABLE_NAME, AttachmentDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_ATTACHMENT_KEY:
//                row_deleted = db.delete(AttachmentDBHelper.TABLE_NAME, selection, selectionArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_deleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        Uri result_uri = null;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        long id = 0;
        switch (uriKey) {
            case GET_USER_KEY:
                id = db.insertWithOnConflict(UserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_USER_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_ORGANIZATION_USER_KEY:
                id = db.insertWithOnConflict(OrganizationUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_USER_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_ACCOUNT_USER_KEY:
                id = db.insertWithOnConflict(AccountUserDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_ACCOUNT_CONTENT_URI + "/" + Long.toString(id));
                break;
            case GET_SERVER_SITE_KEY:
                id = db.insertWithOnConflict(ServerSiteDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                result_uri = Uri.parse(GET_SERVER_SITE_CONTENT_URI + "/" + Long.toString(id));
                break;
//            case GET_GROUP_KEY:
//                id = db.insertWithOnConflict(GroupDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                result_uri = Uri.parse(GET_GROUP_CONTENT_URI + "/" + Long.toString(id));
//                break;
//            case GET_NOTE_LIST_KEY:
//                id = db.insertWithOnConflict(NoteListDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                result_uri = Uri.parse(GET_NOTE_LIST_CONTENT_URI + "/" + Long.toString(id));
//                break;
//            case GET_GRID_ITEM_LIST_KEY:
//                id = db.insertWithOnConflict(CrewMainGridDbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                result_uri = Uri.parse(GET_LIST_GRID_ITEM_PATH + "/" + Long.toString(id));
//                break;
//            case GET_ATTACHMENT_KEY:
//                id = db.insertWithOnConflict(AttachmentDBHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                result_uri = Uri.parse(GET_ATTACHMENT_CONTENT_URI + "/" + Long.toString(id));
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return result_uri;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        mDatabaseHelper = new AppDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        Cursor cursor = null;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        SQLiteQueryBuilder querybuilder = new SQLiteQueryBuilder();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                querybuilder.appendWhere(UserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_USER_KEY:
                querybuilder.setTables(UserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_ORGANIZATION_USER_ROW_KEY:
                querybuilder.appendWhere(OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_ORGANIZATION_USER_KEY:
                querybuilder.setTables(OrganizationUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_ACCOUNT_USER_ROW_KEY:
                querybuilder.appendWhere(AccountUserDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_ACCOUNT_USER_KEY:
                querybuilder.setTables(AccountUserDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

            case GET_SERVER_SITE_ROW_KEY:
                querybuilder.appendWhere(ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment());
            case GET_SERVER_SITE_KEY:
                querybuilder.setTables(ServerSiteDBHelper.TABLE_NAME);
                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;

//            case GET_GROUP_ROW_KEY:
//                querybuilder.appendWhere(GroupDBHelper.ID + " = " + uri.getLastPathSegment());
//            case GET_GROUP_KEY:
//                querybuilder.setTables(GroupDBHelper.TABLE_NAME);
//                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
//                cursor.setNotificationUri(getContext().getContentResolver(), uri);
//                break;
//
//            case GET_NOTE_LIST_ROW_KEY:
//                querybuilder.appendWhere(NoteListDBHelper.ID + " = " + uri.getLastPathSegment());
//            case GET_NOTE_LIST_KEY:
//                querybuilder.setTables(NoteListDBHelper.TABLE_NAME);
//                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
//                cursor.setNotificationUri(getContext().getContentResolver(), uri);
//                break;



//            case GET_GRID_ITEM_LIST_ROW_KEY:
//                querybuilder.appendWhere(CrewMainGridDbHelper.ID + " = " + uri.getLastPathSegment());
//            case GET_GRID_ITEM_LIST_KEY:
//                querybuilder.setTables(CrewMainGridDbHelper.TABLE_NAME);
//                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
//                cursor.setNotificationUri(getContext().getContentResolver(), uri);
//                break;


//            case GET_ATTACHMENT_ROW_KEY:
//                querybuilder.appendWhere(AttachmentDBHelper.ID + " = " + uri.getLastPathSegment());
//            case GET_ATTACHMENT_KEY:
//                querybuilder.setTables(AttachmentDBHelper.TABLE_NAME);
//                cursor = querybuilder.query(db, null, selection, selectionArgs, null, null, sortOrder);
//                cursor.setNotificationUri(getContext().getContentResolver(), uri);
//                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        int row_update = 0;
        SQLiteDatabase db = null;
        db = mDatabaseHelper.getWritableDatabase();
        int uriKey = sUriMatcher.match(uri);
        switch (uriKey) {
            case GET_USER_ROW_KEY:
                row_update = db.update(UserDBHelper.TABLE_NAME, values, UserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_USER_KEY:
                row_update = db.update(UserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_ROW_KEY:
                row_update = db.update(OrganizationUserDBHelper.TABLE_NAME, values, OrganizationUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ORGANIZATION_USER_KEY:
                row_update = db.update(OrganizationUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_ROW_KEY:
                row_update = db.update(AccountUserDBHelper.TABLE_NAME, values, AccountUserDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_ACCOUNT_USER_KEY:
                row_update = db.update(AccountUserDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

            case GET_SERVER_SITE_ROW_KEY:
                row_update = db.update(ServerSiteDBHelper.TABLE_NAME, values, ServerSiteDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
                        + selection, selectionArgs);
                break;
            case GET_SERVER_SITE_KEY:
                row_update = db.update(ServerSiteDBHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

//            case GET_GROUP_ROW_KEY:
//                row_update = db.update(GroupDBHelper.TABLE_NAME, values, GroupDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_GROUP_KEY:
//                row_update = db.update(GroupDBHelper.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case GET_NOTE_LIST_ROW_KEY:
//                row_update = db.update(NoteListDBHelper.TABLE_NAME, values, NoteListDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_NOTE_LIST_KEY:
//                row_update = db.update(NoteListDBHelper.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//
//            case GET_GRID_ITEM_LIST_ROW_KEY:
//                row_update = db.update(CrewMainGridDbHelper.TABLE_NAME, values, CrewMainGridDbHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//            case GET_GRID_ITEM_LIST_KEY:
//                row_update = db.update(CrewMainGridDbHelper.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case GET_ATTACHMENT_ROW_KEY:
//                row_update = db.update(AttachmentDBHelper.TABLE_NAME, values, AttachmentDBHelper.ID + " = " + uri.getLastPathSegment() + " and "
//                        + selection, selectionArgs);
//                break;
//            case GET_ATTACHMENT_KEY:
//                row_update = db.update(AttachmentDBHelper.TABLE_NAME, values, selection, selectionArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return row_update;
    }


}
