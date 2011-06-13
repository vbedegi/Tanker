package com.vbedegi.tanker.dropbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client.DropboxAPI;

import java.io.File;

public class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoginAsyncTask";

    final static public String ACCOUNT_PREFS_NAME = "dropbox_account_prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    String mUser;
    String mPassword;
    String mErrorMessage="";
    DropboxAPI.Config mConfig;
    DropboxAPI.Account mAccount;
    Dropbox mAPI;
    DropboxLoginListener mLoginListener;
    Context mContext;

    // Will just log in
    public LoginAsyncTask(Context context, DropboxLoginListener loginListener, Dropbox api, String user, String password) {
        super();
        mContext = context;
        mLoginListener = loginListener;
        mAPI  = api;
        mUser = user;
        mPassword = password;
        mConfig = mAPI.getConfig();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {

                int success = DropboxAPI.STATUS_NONE;
                if (!mAPI.isAuthenticated()) {
                    mConfig = mAPI.authenticate(mConfig, mUser, mPassword);
                    success = mConfig.authStatus;

                    if (success != DropboxAPI.STATUS_SUCCESS) {
                        return success;
                    }
                }
                mAccount = mAPI.accountInfo();

                if (!mAccount.isError()) {
                        return DropboxAPI.STATUS_SUCCESS;
                } else {
                        Log.e(TAG, "Account info error: " + mAccount.httpCode + " " + mAccount.httpReason);
                        return DropboxAPI.STATUS_FAILURE;
                }
        } catch (Exception e) {

            Log.e(TAG, "Error in logging in.", e);
            return DropboxAPI.STATUS_NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == DropboxAPI.STATUS_SUCCESS) {
                if (mConfig != null && mConfig.authStatus == DropboxAPI.STATUS_SUCCESS) {
                storeKeys(mContext, mConfig.accessTokenKey, mConfig.accessTokenSecret);
                mLoginListener.loginSuccessfull();
            }
        } else {
                if (result == DropboxAPI.STATUS_NETWORK_ERROR) {
                        mLoginListener.loginFailed("Network error: " + mConfig.authDetail);
                } else {
                        mLoginListener.loginFailed("Unsuccessful login.");
                }
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    public static String[] getKeys(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
                String[] ret = new String[2];
                ret[0] = key;
                ret[1] = secret;
                return ret;
        } else {
                return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    public static void storeKeys(Context context, String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);

        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    public static void clearKeys(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
}
