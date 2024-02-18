package com.cafe.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

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

}
