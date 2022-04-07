package com.hongyu.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.chainsaw.Main;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.junit.Test;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hongyu.Filter;
import com.hongyu.entity.CouponSale;
import com.hongyu.service.CouponSaleService;

import sun.security.pkcs11.wrapper.CK_AES_CTR_PARAMS;

//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;

/**
 * 用于发短信
 * */
public class SendMessageEMY {

	
	/**
	 * @param phone 手机号
	 * @param content 短信内容
	 * @return true 成功，false 失败
	 * */
	
	 //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "xxxx";
    static final String accessKeySecret = "xxxx";

	public static boolean sendMessage(String phone, String templateParam,int type) {
//		try {
//			
//			String httpUrl = "http://hprpt2.eucp.b2m.cn:8080/sdkproxy/sendsms.action";
//			content = URLEncoder.encode("【虹宇国际旅行社】" + content, "utf-8");
//			String httpArg = "cdkey=8SDK-EMY-6699-RJSPN&password=131489&phone="
//					+ phone + "&message=" + content + "&smspriority=5";
//			String result = sendGet(httpUrl, httpArg);
//			if(result.contains("<error>0</error>")) {
//				return true;
//			} else {
//				return false;
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		
		try {
			String model = "";
			if(type==1){   //验证码
				model = "SMS_165412476";
			}else if(type==2){//电子券赠送
				model = "SMS_141606334";
			}else if(type==3){//电子券购买1  单条
				model = "SMS_141581539";
			}else if(type==4){//电子券购买2  四条以上
				model = "SMS_141606507" ;
			}else if(type == 5){  //产品订购成功
				model ="SMS_141616369";
			}else if(type==6){//会员生日
				model = "SMS_141616234";
			}else if(type == 7){  //运营人员短信
				model = "SMS_141606515";
			}else if(type == 8){ //新订单
				model = "SMS_151175796";
			}else if(type == 9){  //退团提醒
				model = "SMS_151175820";
			}else if(type == 10){  //消团提醒
				model = "SMS_151175802";
			}else if(type == 11){  //清理占位
				model = "SMS_151175813";
			}else if(type == 12){   //审批提醒
				model = "SMS_151230681";
			}else if(type == 13){  //占位超时取消
				model = "SMS_151230793";
			}else if(type == 14) { //线路驳回提醒
				model = "SMS_169897823";
			}else if(type == 15) { //分成驳回提醒
				model = "SMS_169897822";
			}else if(type == 16) { //团结算驳回提醒
				model = "SMS_169897821";
			}else if(type == 17) { //提现驳回提醒
				model = "SMS_169902803";
			}else if(type == 18) { //打款单驳回提示
				model = "SMS_169897820";
			}else if(type == 19) { //退款驳回提示
				model = "SMS_169897819";
			}else if(type == 20) { //订单驳回提示
				model = "SMS_169902799";
			}else if(type == 21){
				model = "SMS_169898182";  //售后驳回提示
			}
			
			
			
			
			String signName = "森诺英语";
			//String signName = "虹宇国际旅行社";
			SendSmsResponse response = sendSms(phone,templateParam,model,signName);
//			System.out.println("短信接口返回的数据----------------");
//	        System.out.println("Code=" + response.getCode());
//	        System.out.println("Message=" + response.getMessage());
//	        System.out.println("RequestId=" + response.getRequestId());
//	        System.out.println("BizId=" + response.getBizId());
			if(response.getCode().equals("OK")){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
		
		
	}

	
	/**
	 * 商贸用发短信接口,前缀改为"游买有卖商城"
	 * @param phone 手机号
	 * @param content 短信内容
	 * @return true 成功，false 失败
	 * */
	public static boolean businessSendMessage(String phone, String templateParam,int type) {
//		try {
			
//			String httpUrl = "http://hprpt2.eucp.b2m.cn:8080/sdkproxy/sendsms.action";
//			content = URLEncoder.encode("【游买有卖商城】" + content, "utf-8");
//			String httpArg = "cdkey=8SDK-EMY-6699-RJSPN&password=131489&phone="
//					+ phone + "&message=" + content + "&smspriority=5";
//			String result = sendGet(httpUrl, httpArg);
//			if(result.contains("<error>0</error>")) {
//				return true;
//			} else {
//				return false;
//			}		
			
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
			String model = "";
			try {	
				if(type==1){   //验证码
					model = "SMS_141606332";
				}else if(type==2){//电子券赠送
					model = "SMS_141606334";
				}else if(type==3){//电子券购买1  单条
					model = "SMS_141581539";
				}else if(type==4){//电子券狗该2  四条以上
					model = "SMS_141606507" ;
				}else if(type == 5){  //产品订购成功
					model ="SMS_141616369";
				}else if(type==6){//会员生日
					model = "SMS_141616234";
				}else if(type == 7){  //运营人员短信
					model = "SMS_141606515";
				}
				
				
				
				//String signName = "游买有卖商城";
				String signName = "游买有卖商城";
				SendSmsResponse response = sendSms(phone,templateParam,model,signName);
//				System.out.println("短信接口返回的数据----------------");
//		        System.out.println("Code=" + response.getCode());
//		        System.out.println("Message=" + response.getMessage());
//		        System.out.println("RequestId=" + response.getRequestId());
//		        System.out.println("BizId=" + response.getBizId());
				if(response.getCode().equals("OK")){
					return true;
				}else{
					return false;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}			
	}
	private static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

	public static SendSmsResponse sendSms(String phone,String templateParam,String model,String signName) throws ClientException {

        //可自助调整超时时间
//        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
//        request.setSignName("阿里云短信测试专用");
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(model);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(templateParam);
        SendSmsResponse sendSmsResponse = new SendSmsResponse();

        try {
        	System.out.print(sendSmsResponse.getMessage());
        	sendSmsResponse = acsClient.getAcsResponse(request);	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        return sendSmsResponse;
    }
	
}







