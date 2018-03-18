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

    private static final String TAG = MainActivity.class.getSimpleName();

    PotatoLoader(Context context, String url){
        super(context);
        mPotatoURL = url;
    }

    @Override
    protected void onStartLoading(){
        if(mPotatoURL != null){
            if(mPotatoResultsJSON != null){
                Log.d(TAG, "Loader returns cached results");
                deliverResult(mPotatoResultsJSON);
            }
            else{
                forceLoad();
            }
        }
    }

    @Override
    public String loadInBackground() {
        if(mPotatoURL != null){
            Log.d(TAG, "Loading results from API with URL: " + mPotatoURL);
            String potatoResults = null;
            try {
                potatoResults = NetworkUtils.doHTTPPost(mPotatoURL); // doHTTPPost should perform the analyze call on a static web image.
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
