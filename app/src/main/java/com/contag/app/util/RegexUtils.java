package com.contag.app.util;

import com.contag.app.config.Constants;

import java.util.regex.Pattern;

/**
 * Created by tanay on 20/8/15.
 */
public class RegexUtils {

    public static boolean isPhoneNumber(String phNum) {
        Pattern pattern = Pattern.compile(Constants.Regex.PHONE_NUM);
        return pattern.matcher(phNum).matches();
    }

    public static boolean isContagId(String contagID){
        Pattern pattern = Pattern.compile(Constants.Regex.CONTAG_ID) ;
        return pattern.matcher(contagID).matches() ;
    }
}
