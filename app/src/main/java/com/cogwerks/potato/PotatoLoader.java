package com.cogwerks.potato;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;


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
            Log.d(TAG, "Loading reults from API");
            String potatoResults = null;
            /*try{
                /*potatoResults = get the results from the api;*
            }
            catch(IOException e){
                e.printStackTrace();
            }*/
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
