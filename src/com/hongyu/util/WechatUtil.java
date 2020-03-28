package com.hongyu.util;

import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.grain.util.JsonUtils;
import com.hongyu.util.wechatUtilEntity.AbstractParams;
import com.hongyu.util.wechatUtilEntity.AuthAccessToken;
import com.hongyu.util.wechatUtilEntity.AuthTokenParams;
import com.hongyu.util.wechatUtilEntity.TemplateMsgResult;

public class WechatUtil {
	public static AuthAccessToken getAuthAccessToken(AbstractParams basic, String url,Integer type) {
		AuthAccessToken authAccessToken = null;
		// 获取网页授权凭证
		try {
			if (StringUtils.isEmpty(url)) {
				url = Constants.GET_OAUTH_TOKEN_URL;
			}
			String result;
			if(type==null){
				result=HttpReqUtil.HttpsDefaultExecute(Constants.GET_METHOD, url, basic.getParams(), null, null);
			}else{
				result=HttpReqUtil.HttpsDefaultExecute(Constants.GET_METHOD, url, basic.getParams1(), null, null);
			}
			System.out.println("result: "+result);
			authAccessToken = JsonUtils.toObject(result, AuthAccessToken.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authAccessToken;
	}
	public static TemplateMsgResult sendTemplate(String accessToken, String data) {
		TemplateMsgResult templateMsgResult = null;
		TreeMap<String, String> params = new TreeMap<>();
		params.put("access_token", accessToken);
		String result = HttpReqUtil.HttpsDefaultExecute(Constants.GET_METHOD, Constants.SEND_TEMPLATE_MESSAGE,
				params, data, null);
		templateMsgResult = JsonUtils.toObject(result, TemplateMsgResult.class);
		//log.....
		return templateMsgResult;
	}
	public static TemplateMsgResult storeRent(String data){
		AuthTokenParams authTokenParams = new AuthTokenParams(); 
        authTokenParams.setAppid(Constants.APP_ID);
        authTokenParams.setSecret(Constants.APP_SECRET);  
        authTokenParams.setCode("SUCCESS");
        authTokenParams.setGrant_type("client_credential");
        AuthAccessToken authAccessToken=getAuthAccessToken(authTokenParams, null,null);
        String accessToken=authAccessToken.getAccess_token();
        
        return sendTemplate(accessToken, data);
        
	}
}
