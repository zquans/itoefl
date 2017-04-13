package com.iyuce.itoefl.Utils;

import com.iyuce.itoefl.Common.Constants;
import com.iyuce.itoefl.Model.Vocabulary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/4/5
 */
public class ParseJsonUtil {

    public static ArrayList<Vocabulary> parseVocabulary(String result) {
        LogUtil.e("result = " + result);
        ArrayList<Vocabulary> mList = new ArrayList<>();
        try {
            Vocabulary mVocabulary;
            JSONObject obj = new JSONObject(result);
            if (obj.getString(Constants.CODE_HTTP).equals(Constants.CODE_HTTP_SUCCESS)) {
                JSONArray arr = obj.getJSONArray(Constants.DATA_HTTP);
                for (int i = 0; i < arr.length(); i++) {
                    mVocabulary = new Vocabulary();
                    obj = arr.getJSONObject(i);
                    mVocabulary.title = obj.getString("title");
                    mVocabulary.description = obj.getString("description");
                    mVocabulary.img = obj.getString("img");
                    mVocabulary.modify_at = obj.getString("modify_at");
                    mVocabulary.path = obj.getString("path");
                    mVocabulary.size = obj.getLong("size");
                    mVocabulary.key = obj.getInt("key");
                    mVocabulary.book_mark = "0";
                    mList.add(mVocabulary);
                }
                return mList;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mList;
    }
}