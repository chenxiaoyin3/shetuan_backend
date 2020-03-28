package com.hongyu.util;

import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.grain.util.JsonUtils;
import com.hongyu.util.wechatUtilEntity.AbstractParams;
import com.hongyu.util.wechatUtilEntity.AuthAccessToken;
import com.hongyu.util.wechatUtilEntity.AuthTokenParams;
import com.hongyu.util.wechatUtilEntity.TemplateMsgResult;
import com.hongyu.util.wechatUtilEntity.WechatTemplateMsg;


public class SendTemplateTest {
	public static void test(String url,String openId) {
//		AuthTokenParams authTokenParams = new AuthTokenParams(); 
//		String app_id="wx69b4b6538f5d4fba";
//		String app_secret="14b66ee47141925c81a7782e94607789";
//        authTokenParams.setAppid(app_id);
//        authTokenParams.setSecret(app_secret);  
//        authTokenParams.setCode("SUCCESS");
//        
//        AuthAccessToken authAccessToken=WechatUtil.getAuthAccessToken(authTokenParams, null);
//        
//        System.out.println("access_token："+authAccessToken.getAccess_token());
//        System.out.println("expires_in："+authAccessToken.getExpires_in());
//        System.out.println("refresh_token："+authAccessToken.getRefresh_token());
//        System.out.println("openid："+authAccessToken.getOpenid());
//        System.out.println("scope："+authAccessToken.getScope());
		if(url==null){
			url="www.baidu.com";
		}
		if(openId==null){
			openId=Constants.TEST_OPENID;
		}
        TemplateMsgResult templateMsgResult = null;
		TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
		// 根据具体模板参数组装
		params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
		params.put("keyword1", WechatTemplateMsg.item("泰国豪华三日游", "#000000"));
		params.put("keyword2", WechatTemplateMsg.item("团客", "#000000"));
		params.put("keyword3", WechatTemplateMsg.item("2018.6.8-2018.6.11", "#000000"));
		params.put("keyword4", WechatTemplateMsg.item("全陪服务", "#000000"));
		params.put("keyword5", WechatTemplateMsg.item("3000元", "#000000"));
		params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
		WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
		wechatTemplateMsg.setTemplate_id(Constants.Store_RentGuide_Template);
		wechatTemplateMsg.setTouser(openId);
		wechatTemplateMsg.setUrl(url);
		wechatTemplateMsg.setData(params);
		String data = JsonUtils.toJson(wechatTemplateMsg);
		templateMsgResult = WechatUtil.storeRent(data);
		System.out.println(JsonUtils.toJson(templateMsgResult));
             
	}
}
