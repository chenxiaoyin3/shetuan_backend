package com.hongyu.wechatpay;

import com.hongyu.wechatpay.bean.PayBean;
import com.hongyu.wechatpay.bean.ReqOfficialBean;
import com.hongyu.wechatpay.util.HttpUtil;
import com.hongyu.wechatpay.util.MD5;
import com.hongyu.wechatpay.util.XMLUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.*;

public class WechatPayMainOffcialAccount {

    private static StringBuffer sb = new StringBuffer();
    private static Map<String,String> resultunifiedorder;
    private static ReqOfficialBean req = new ReqOfficialBean();
    private static String url= "https://api.mch.weixin.qq.com/pay/unifiedorder";
    

    public static void main(String[] args) throws Exception {
        PayBean payBean = new PayBean();
        payBean.setAmount("2");
        payBean.setBody("JSAPI Test");
        payBean.setOrder("201801191545");
        payBean.setCallbackUrl("http://tobyli16.com");
        payBean.setOpenId("ocgJPv1kyOAGEJbNYlhmOry7lgBg");
        getReqOfficial(payBean,1);
    }
    //type==1 wechat type==2 xiaochengxu
    public static ReqOfficialBean getReqOfficial(PayBean payBean,int type) throws Exception{
        String entity=getProductArgs(payBean,type);
        String content = HttpUtil.postData(url, entity);
        System.out.println(content);
        resultunifiedorder = XMLUtil.doXMLParse(content);
        genPayReq(type);
        return req;
    }

    private static void genPayReq(int type) {
    	if(type==1) {
    		req.appId = ConstantsOfficialAccount.getInstance().getAPP_ID();
    	}else if(type==2) {
    		req.appId = ConstantsOfficialAccount.getInstance().getXCX_APP_ID();
    	}
        
        req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
        req.nonceStr = getNonceStr();
        req.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appId", req.appId));
        signParams.add(new BasicNameValuePair("nonceStr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("signType", "MD5"));
        signParams.add(new BasicNameValuePair("timeStamp", String.valueOf(System.currentTimeMillis() / 1000)));
        req.paySign = genAppSign(signParams);
        return;
    }

    private static String getProductArgs(PayBean payBean,int type) {
        StringBuffer xml=new StringBuffer();
        try {
            String nonceStr=getNonceStr();
            xml.append("<xml>");
            List<NameValuePair> packageParams=new LinkedList<NameValuePair>();
            if(type == 1) {

                packageParams.add(new BasicNameValuePair("appid",  ConstantsOfficialAccount.getInstance().getAPP_ID()));
            }else if(type == 2){
                packageParams.add(new BasicNameValuePair("appid",  ConstantsOfficialAccount.getInstance().getXCX_APP_ID()));
            	
            }
            packageParams.add(new BasicNameValuePair("body", payBean.getBody()));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("mch_id",  ConstantsOfficialAccount.getInstance().getMCH_ID()));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", payBean.getCallbackUrl()));//写你们的回调地址
            packageParams.add(new BasicNameValuePair("openid", payBean.getOpenId()));
            packageParams.add(new BasicNameValuePair("out_trade_no", payBean.getOrder()));
            packageParams.add(new BasicNameValuePair("total_fee", payBean.getAmount()));
            packageParams.add(new BasicNameValuePair("trade_type", "JSAPI"));

            String sign=getPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            String xmlString=toXml(packageParams);
            return xmlString;
        } catch (Exception e) {
            return null;
        }
    }

    //生成随机号，防重发
    private static String getNonceStr() {
        Random random=new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }
    /**
     生成签名
     */
    private static String getPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(ConstantsOfficialAccount.getInstance().getAPI_KEY());
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }
    /*
     * 转换成xml
     */
    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");


            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            tmp.append(params.get(i).getName());
            tmp.append('=');
            tmp.append(params.get(i).getValue());
            tmp.append('&');
        }
        tmp.append("key=");
        tmp.append(ConstantsOfficialAccount.getInstance().getAPI_KEY());
        sb.append("sign str\n"+tmp.toString()+"\n\n");
        String appSign = MD5.getMessageDigest(tmp.toString().getBytes());
        return appSign;
    }
}
