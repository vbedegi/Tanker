package com.vbedegi.tanker.dropbox;

import android.content.Context;

public class DropboxAPIFactory {
    public static Dropbox create(Context context) {
        return new Dropbox(context, "fmzn0usem733aiv", "4bp18v8k2e8awbn");
    }
}
