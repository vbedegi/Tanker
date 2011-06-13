package com.vbedegi.tanker.dropbox;

import android.content.Context;
import com.dropbox.client.DropboxAPI;

import java.io.*;
import java.util.ArrayList;

public class Dropbox extends DropboxAPI {
    private Context mContext;
    private String mKey;
    private String mSecret;

    private Config mConfig;

    public Dropbox(Context context, String key, String secret) {
        super();
        mContext = context;
        mKey = key;
        mSecret = secret;
    }


    /**
     * This handles deauthentication for you.
     */
    public void deauthenticate() {
        super.deauthenticate();
        LoginAsyncTask.clearKeys(mContext);
    }

    /**
     * Downloads a file from the Dropbox.
     * Found it here: http://forums.dropbox.com/topic.php?id=23189
     *
     * @param dbPath    Path to the file
     * @param localFile File object to a local file
     * @return Currently always true
     * @throws IOException
     */
    public boolean downloadDropboxFile(String dbPath, File localFile) throws IOException {

        BufferedInputStream br = null;
        BufferedOutputStream bw = null;

        try {
            if (!localFile.exists()) {
                localFile.createNewFile(); //otherwise dropbox client will fail silently
            }

            FileDownload fd = this.getFileStream("dropbox", dbPath, null);
            br = new BufferedInputStream(fd.is);
            bw = new BufferedOutputStream(new FileOutputStream(localFile));

            byte[] buffer = new byte[4096];
            int read;
            while (true) {
                read = br.read(buffer);
                if (read <= 0) {
                    break;
                }
                bw.write(buffer, 0, read);
            }
        } finally {
            //in finally block:
            if (bw != null) {
                bw.close();
            }
            if (br != null) {
                br.close();
            }
        }

        return true;
    }

    /**
     * Lists the files in the given directory.
     *
     * @param path Absolute Path to directory
     * @return null, if path is no directory, else an ArrayList of entries.
     */
    public ArrayList<Entry> listDirectory(String path) {
        Entry entry = metadata("dropbox", path, 0, null, true);
        if (!entry.is_dir) return null;
        return entry.contents;
    }

    /**
     * This handles authentication if the user's token & secret
     * are stored locally, so we don't have to store user-name & password
     * and re-send every time.
     */

    public boolean authenticate() {
        Config config = getConfig();
        String keys[] = LoginAsyncTask.getKeys(mContext);
        if (keys != null) {
            config = authenticateToken(keys[0], keys[1], config);
            if (config != null) {
                return true;
            }
        }
        return false;
    }


    protected Config getConfig() {
        if (mConfig == null) {
            mConfig = super.getConfig(null, false);
            // TODO On a production app which you distribute, your consumer
            // key and secret should be obfuscated somehow.
            mConfig.consumerKey = mKey;
            mConfig.consumerSecret = mSecret;
            mConfig.server = "api.dropbox.com";
            mConfig.contentServer = "api-content.dropbox.com";
            mConfig.port = 80;
        }
        return mConfig;
    }

    public void login(DropboxLoginListener loginListener) {
        LoginAsyncTask login = new LoginAsyncTask(mContext, loginListener, this, null, null);
        login.execute();
    }

    public void login(DropboxLoginListener loginListener, String email, String password) {
        LoginAsyncTask login = new LoginAsyncTask(mContext, loginListener, this, email, password);
        login.execute();
    }

    public void upload(DropboxUploadListener uploadListener, File file) {
        UploadAsyncTask task = new UploadAsyncTask(uploadListener, this);
        task.execute(file);
    }
}
