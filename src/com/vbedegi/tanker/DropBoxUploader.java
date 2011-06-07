package com.vbedegi.tanker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.dropbox.client.DropboxAPI;

import java.io.File;
import java.security.Key;

public class DropBoxUploader implements LoginListener {

    private Context context;
    private DropboxAPI api;

    public DropBoxUploader(Context context) {
        this.context = context;
        api = new DropboxAPI();
    }

    public void upload(File file) {
        String[] keys = getKeys();
        if (keys == null) {
            login();
        }

//        api.putFile("/tanker","/tanker", file);
    }

    private void login() {
        String email = "vbedegi@gmail.com";
        String password = "dcd123";

        LoginAsyncTask login = new LoginAsyncTask(this, api, email, password, getConfig());
        login.execute();
    }

    private DropboxAPI.Config config;

    private String CONSUMER_KEY = "nhtkjrw5gz8q8k1";
    private String CONSUMER_SECRET = "a03k3ahj9gpc7o1";

    protected DropboxAPI.Config getConfig() {
        if (config == null) {
            config = api.getConfig(null, false);

            config.consumerKey = CONSUMER_KEY;
            config.consumerSecret = CONSUMER_SECRET;
            config.server = "api.dropbox.com";
            config.contentServer = "api-content.dropbox.com";
            config.port = 80;
        }
        return config;
    }

    final static public String ACCOUNT_PREFS_NAME = "prefs";
    final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    public String[] getKeys() {
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

    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = context.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    public void configAvailable(DropboxAPI.Config config) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loggedIn(String key, String secret) {
        storeKeys(key,secret);
    }

    public void loginFailed(String reason) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

interface LoginListener {
    void configAvailable(DropboxAPI.Config config);
    void loggedIn(String accessTokenKey, String accessTokenSecret);
    void loginFailed(String reason);
}

class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "LoginAsyncTask";

    private LoginListener listener;
    private DropboxAPI api;

    private String email;
    private String password;

    private DropboxAPI.Config config;
    private DropboxAPI.Account account;

    // Will just log in
    public LoginAsyncTask(LoginListener listener, DropboxAPI api, String user, String password, DropboxAPI.Config config) {
        super();
        this.listener = listener;
        this.email = user;
        this.password = password;
        this.config = config;
        this.api = api;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            int success = DropboxAPI.STATUS_NONE;
            if (!api.isAuthenticated()) {
                config = api.authenticate(config, email, password);

                listener.configAvailable(config);
                // mDropboxSample.setConfig(config);
                success = config.authStatus;
                if (success != DropboxAPI.STATUS_SUCCESS) {
                    return success;
                }
            }

            account = api.accountInfo();
            if (!account.isError()) {
                return DropboxAPI.STATUS_SUCCESS;
            } else {
                return DropboxAPI.STATUS_FAILURE;
            }
        } catch (Exception e) {
            return DropboxAPI.STATUS_NETWORK_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == DropboxAPI.STATUS_SUCCESS) {
            if (config != null && config.authStatus == DropboxAPI.STATUS_SUCCESS) {
                listener.loggedIn(config.accessTokenKey, config.accessTokenSecret);
                //mDropboxSample.storeKeys(config.accessTokenKey, config.accessTokenSecret);
                //mDropboxSample.setLoggedIn(true);
                //mDropboxSample.showToast("Logged into Dropbox");
            }
            if (account != null) {
                //mDropboxSample.displayAccountInfo(account);
            }
        } else {
            if (result == DropboxAPI.STATUS_NETWORK_ERROR) {
                //mDropboxSample.showToast("Network error: " + config.authDetail);
                listener.loginFailed("Network error: " + config.authDetail);
            } else {
                //mDropboxSample.showToast("Unsuccessful login.");
                listener.loginFailed("Unsuccessful login.");
            }
        }
    }
}