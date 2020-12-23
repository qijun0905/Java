package com.dfundata.fund.schedule.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 2020/11/22 8:01 下午
 *
 * @author Seldom
 */
public class NullUtis {

    public static Boolean isNotNull(String str){

        if(StringUtils.isNotBlank(str) && !"null".equals(str)){
            return true;
        }
        return false;
    }

}
