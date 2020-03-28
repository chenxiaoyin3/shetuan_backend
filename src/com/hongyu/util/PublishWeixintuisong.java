package com.hongyu.util;

import java.util.TreeMap;

import com.grain.util.JsonUtils;
import com.hongyu.util.wechatUtilEntity.TemplateMsgResult;
import com.hongyu.util.wechatUtilEntity.WechatTemplateMsg;

public class PublishWeixintuisong {
	public static void tuisong(String url,String openId, TreeMap<String, TreeMap<String, String>> params) {

		if(url==null){
			url="www.baidu.com";
		}
		if(openId==null){
			openId=Constants.TEST_OPENID;
		}
        TemplateMsgResult templateMsgResult = null;
//		TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
//		// 根据具体模板参数组装
//		params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
//		params.put("keyword1", WechatTemplateMsg.item("泰国豪华三日游", "#000000"));
//		params.put("keyword2", WechatTemplateMsg.item("团客", "#000000"));
//		params.put("keyword3", WechatTemplateMsg.item("2018.6.8-2018.6.11", "#000000"));
//		params.put("keyword4", WechatTemplateMsg.item("全陪服务", "#000000"));
//		params.put("keyword5", WechatTemplateMsg.item("3000元", "#000000"));
//		params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
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
