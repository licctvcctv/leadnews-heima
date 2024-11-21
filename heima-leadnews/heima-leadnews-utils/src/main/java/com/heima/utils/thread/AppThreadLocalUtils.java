package com.heima.utils.thread;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

public class AppThreadLocalUtils {

    private static final ThreadLocal<ApUser>  APP_USER_THREAD_LOCAL = new ThreadLocal<>();


    public static ApUser getUser(){
        return APP_USER_THREAD_LOCAL .get();
    }

    public static void setUser(ApUser apUser){
        APP_USER_THREAD_LOCAL .set(apUser);
    }
    public static void clear(){
        APP_USER_THREAD_LOCAL .remove();
    }
}
