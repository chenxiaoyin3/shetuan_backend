package com.hongyu.wechatpay.bean;

import com.hongyu.wechatpay.util.MD5;

import java.util.Random;

public class PayBean {
    private String body;
    private String callbackUrl;
    private String order;
    private String amount;
    private String openId;

    public PayBean() {
        this.body = "App Pay Test";
        this.callbackUrl = "http://tobyli16.com";
        this.order = genOutTradNo();
        this.amount = "1";
        this.openId = "ocgJPv1kyOAGEJbNYlhmOry7lgBg";
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    //生成订单号,测试用，在客户端生成
    private static String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }
}
