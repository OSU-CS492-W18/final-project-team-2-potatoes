package com.cogwerks.potato.utils;

import android.net.Uri;

/**
 * Created by annie on 3/17/18.
 */

public class MSAzureComputerVisionUtils {
    private final static String CV_ANALYZE_BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";

    private final static String CV_ANALYZE_FEATURES_PARAM = "visualFeatures";
    private final static String CV_ANALYZE_FEATURES_VALUE = "Description,Tags,Color,Faces,Adult";

    private final static String CV_ANALYZE_DETAILS_PARAM = "details";
    private final static String CV_ANALYZE_DETAILS_VALUE = "Celebrities,Landmarks";

    private final static String CV_ANALYZE_API_KEY_PARAM = "subscription-key";
    private final static String CV_ANALYZE_API_KEY_VALUE = "placeholder";

    public static String buildAnalyzeURL() {
        return Uri.parse(CV_ANALYZE_BASE_URL).buildUpon()
                .appendQueryParameter(CV_ANALYZE_FEATURES_PARAM, CV_ANALYZE_FEATURES_VALUE)
                .appendQueryParameter(CV_ANALYZE_DETAILS_PARAM, CV_ANALYZE_DETAILS_VALUE)
                .appendQueryParameter(CV_ANALYZE_API_KEY_PARAM, CV_ANALYZE_API_KEY_VALUE)
                .build()
                .toString();
    }
}
