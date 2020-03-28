package com.hongyu.wechatpay.bean;

public class ReqOfficialBean {
    public String appId;
    public String packageValue;
    public String timeStamp;
    public String nonceStr;
    public String paySign;

    @Override
    public String toString() {
        return "appId=\""+appId+"\"\n"
                + "packageValue=\""+packageValue+"\"\n"
                + "timeStamp=\""+timeStamp+"\"\n"
                + "nonceStr=\""+nonceStr+"\"\n"
                + "paySign=\""+paySign+"\"\n";
    }
}
