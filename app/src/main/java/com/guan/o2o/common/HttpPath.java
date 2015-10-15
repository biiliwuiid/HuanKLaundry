package com.guan.o2o.common;

public class HttpPath {

    // 请求网路IP
    public final static String IP = "http://www.heartguard.cn:8080";

    // 请求浣客官网IP
    public final static String IP_USER_AGREE = "http://123.56.138.192:8002";

    // login
    public static String getLoginIfo(String loginPhone, String loginCode) {
        return IP + "/demo/myClive?phone=13800138000";
    }

    // getCode
    public static String getCodeIfo(String loginPhone) {
        return IP + "/demo/myClive?phone=13800138000";
    }

    // getClothData
    public static String getClothIfo() {
        return IP + "/demo/myClive?clive=wash&url=www.heartguard.cn";
    }

    // getClothImageview
    public static String getClothIvIfo(int id) {
        return IP + "/demo/image" + id +".png";
    }

    // 用户协议IP
    public static String getUserAgreeIfo() {
        return IP_USER_AGREE + "/protocol/";
    }

    // 常见问题IP
    public static String getProblemIfo() {
        return IP_USER_AGREE + "/faq/";
    }
}
