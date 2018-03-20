package com.cogwerks.potato.utils;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Struct;
import java.util.ArrayList;

/**
 * Created by annie on 3/17/18.
 */

public class MSAzureComputerVisionUtils {
    private final static String CV_ANALYZE_BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";

    private final static String CV_ANALYZE_FEATURES_PARAM = "visualFeatures";
    private final static String CV_ANALYZE_FEATURES_VALUE = "Tags,Color,Adult,ImageType";

    private final static String CV_ANALYZE_DETAILS_PARAM = "details";
    private final static String CV_ANALYZE_DETAILS_VALUE = "Celebrities,Landmarks";

    private final static String CV_ANALYZE_API_KEY_PARAM = "subscription-key";
    private final static String CV_ANALYZE_API_KEY_VALUE = "a97aab14e3264b74a601e0c38cfcd9bc";

    public static String[] azureClipartType = {"Non Clip-Art", "Ambiguous", "Normal Clip-Art", "Good Clip-Art"};

    public static class FullApiResult implements Serializable {
        public ArrayList<AnalyzeResult> tags;
        public boolean isAdult;
        public boolean isRacy;
        public String adultScore;
        public String racyScore;
        public String clipArtType;
        public boolean blackAndWhite;

    }

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

    public static FullApiResult parseAnalyzeResultsJSON(String resultsJSON) {
        try {

            FullApiResult completeResult = new FullApiResult();

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
            completeResult.tags = analyzeResultsList;

            JSONObject adultElement = resultsObj.getJSONObject("adult");
            completeResult.adultScore = adultElement.getString("adultScore");
            completeResult.racyScore  = adultElement.getString("racyScore");
            completeResult.isAdult    = adultElement.getBoolean("isAdultContent");
            completeResult.isRacy    = adultElement.getBoolean("isRacyContent");

            JSONObject colorElement = resultsObj.getJSONObject("color");
            completeResult.blackAndWhite = colorElement.getBoolean("isBwImg");

            JSONObject imageElement = resultsObj.getJSONObject("imageType");
            int clipArtIndex = imageElement.getInt("clipArtType");
            completeResult.clipArtType = azureClipartType[clipArtIndex];

            return completeResult;

        } catch (JSONException e) {
            return new FullApiResult();
        }
    }


}
