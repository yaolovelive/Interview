package com.yy.interview.model.util;

public class ValidateUtils {

    public static boolean isNULL(String str){
        if(str != null && !"".equals(str)){
            return false;
        }
        return true;
    }

}
