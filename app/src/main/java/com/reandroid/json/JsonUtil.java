/*
 * Copyright (c) 2002 JSON.org (now "Public Domain")
 * This is NOT property of REAndroid
 * This package is renamed from org.json.* to avoid class conflict when used on anroid platforms
*/
package com.reandroid.json;


public class JsonUtil {

    public static byte[] parseBase64(String text){
        if(text == null || !text.startsWith(JSONItem.MIME_BIN_BASE64)){
            return null;
        }
        text = text.substring(JSONItem.MIME_BIN_BASE64.length());
        try{
            return java.util.Base64.getUrlDecoder().decode(text);
        }catch (Throwable throwable){
            throw new JSONException(throwable);
        }
    }
}
