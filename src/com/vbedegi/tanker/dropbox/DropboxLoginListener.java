package com.vbedegi.tanker.dropbox;

import android.content.Context;
import com.dropbox.client.DropboxAPI;

public abstract class DropboxLoginListener {
        public abstract void loginSuccessfull();
        public abstract void loginFailed(String message);
}

