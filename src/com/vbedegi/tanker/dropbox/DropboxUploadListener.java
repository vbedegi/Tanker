package com.vbedegi.tanker.dropbox;

public abstract class DropboxUploadListener {
    public abstract void uploadSuccessful();
    public abstract void uploadFailed(String message);
}
