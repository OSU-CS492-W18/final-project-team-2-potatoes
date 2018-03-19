package com.cogwerks.potato.utils;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by annie on 3/17/18.
 */

public class MSAzureComputerVisionUtils {
    private final static String CV_ANALYZE_BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";

    private final static String CV_ANALYZE_FEATURES_PARAM = "visualFeatures";
    private final static String CV_ANALYZE_FEATURES_VALUE = "Tags,Description,Color,Faces,Adult";

    private final static String CV_ANALYZE_DETAILS_PARAM = "details";
    private final static String CV_ANALYZE_DETAILS_VALUE = "Celebrities,Landmarks";

    private final static String CV_ANALYZE_API_KEY_PARAM = "subscription-key";
    private final static String CV_ANALYZE_API_KEY_VALUE = "a97aab14e3264b74a601e0c38cfcd9bc";

    public static class AnalyzeResult implements Serializable {
        public String tag;
        public String confidence;

        /* Converts confidence from string to double, then rounds to two decimal places and
        multiples by 100 to return a percentage
        */
        public int roundConfidence() {
            double confidenceDec = Double.parseDouble(this.confidence);
            BigDecimal bd = new BigDecimal(confidenceDec);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            double percent = bd.doubleValue() * 100;
            return (int)percent;
        }
    }

    public static String buildAnalyzeURL() {
        return Uri.parse(CV_ANALYZE_BASE_URL).buildUpon()
                .appendQueryParameter(CV_ANALYZE_FEATURES_PARAM, CV_ANALYZE_FEATURES_VALUE)
                .appendQueryParameter(CV_ANALYZE_DETAILS_PARAM, CV_ANALYZE_DETAILS_VALUE)
                .appendQueryParameter(CV_ANALYZE_API_KEY_PARAM, CV_ANALYZE_API_KEY_VALUE)
                .build()
                .toString();
    }

    public static ArrayList<AnalyzeResult> parseAnalyzeResultsJSON(String resultsJSON) {
        try {
            JSONObject resultsObj = new JSONObject(resultsJSON); // grab entire string as JSON object
            JSONArray resultsItems  = resultsObj.getJSONArray("tags"); // grab tags array

            ArrayList<AnalyzeResult> analyzeResultsList = new ArrayList<AnalyzeResult>();
            for (int i = 0; i < resultsItems.length(); i++) { // for each key/val object in tags,
                AnalyzeResult result = new AnalyzeResult();
                JSONObject resultItem = resultsItems.getJSONObject(i); // grab {tag_name: confidence} object
                result.tag = resultItem.getString("name");
                result.confidence = resultItem.getString("confidence");
                analyzeResultsList.add(result);
            }
            return analyzeResultsList;
        } catch (JSONException e) {
            return  null;
        }
    }
}
