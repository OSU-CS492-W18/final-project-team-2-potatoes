package com.cogwerks.potato;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;


import com.cogwerks.potato.utils.NetworkUtils;

import java.io.IOException;
import java.security.acl.LastOwnerException;

/**
 * Created by Susmita on 3/12/2018.
 */

public class PotatoLoader extends AsyncTaskLoader<String> {
    String mPotatoResultsJSON;
    String mPotatoURL;
    byte[] mPotatoBytes; // delicious

    private static final String TAG = MainActivity.class.getSimpleName();

    PotatoLoader(Context context, String url, byte[] data){
        super(context);
        mPotatoURL = url;
        mPotatoBytes = data;
    }

    @Override
    protected void onStartLoading(){
        Log.d(TAG, "Inside onStartLoading.");
        if (mPotatoBytes != null) {
            Log.d(TAG, "onStartLoading: mPotatoBytes != null");
        }
        if(mPotatoURL != null){
            Log.d(TAG, "onStartLoading: mPotatoURL != null");
            if(mPotatoResultsJSON != null){ // apparently this can sometimes be null while bytes and url are not?
                Log.d(TAG, "onStartLoading: mPotatoResultsJSON != null. Loader ret cached results");
                deliverResult(mPotatoResultsJSON);
            }
            else{
                Log.d(TAG, "onStartLoading: force loading.");
                forceLoad();
            }
        }
    }

    @Override
    public String loadInBackground() {
        if(mPotatoURL != null && mPotatoBytes != null){
            Log.d(TAG, "Loading results from API with URL: " + mPotatoURL);
            String potatoResults = null;
            try {
                potatoResults = NetworkUtils.doHTTPPost(mPotatoURL, mPotatoBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "loadInBackground: " + potatoResults);
            return potatoResults;
        }
        else{
            return  null;
        }
    }

    @Override
    public void deliverResult(@Nullable String data){
        mPotatoResultsJSON = data;
        super.deliverResult(data);
    }
}
