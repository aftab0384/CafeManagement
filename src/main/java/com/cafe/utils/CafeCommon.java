package com.cafe.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CafeCommon {

    public static final String  SOMETHING_WENT_WRONG = "something went wrong";
    public static final String INVALID_DATA = "Invalid data entered";
    public static Date getCurrentDate() {
        Date date = new Date();
        return date;
    }

    public static String getLoggedinUserName() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    public static String getUUID(){
    Date date = new Date();
    long time = date.getTime();
        String uuid = "BILL-"+time;
        return uuid;
    }

    public static final String STORAGE_LOCATION = "D:\\pdfstorage";
    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
                    JSONArray jsonArray = new JSONArray(data);
                    return jsonArray;
    }
    public static Map<String,Object> getMapFromJson(String data){
        if(!Strings.isNullOrEmpty(data)){
            return new Gson().fromJson(data,new TypeToken<Map<String,Object>>(){}.getType());
        }
        return new HashMap<>();

    }
    public static  String generateUuid(String name) {
        String uid;
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(currentDate);
        System.out.println(formattedDate);
        UUID uuid = UUID.randomUUID();
        uid = "CAFE" + name.toUpperCase() + formattedDate.toString().replace("-","") + uuid.toString().replaceAll("-", "").substring(0,4).toUpperCase();
        System.out.println(uid);
        return uid;
    }
}
