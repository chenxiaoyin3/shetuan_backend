package com.hongyu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


import com.csii.payment.client.core.MerchantSignTool;
import com.csii.payment.client.entity.RequestParameterObject;
import com.csii.payment.client.entity.SignParameterObject;
import com.csii.payment.client.entity.VerifyParameterObject;
import com.csii.payment.client.http.HttpUtil;
import com.hongyu.util.bankEntity.InterBankRequest;
import com.hongyu.util.bankEntity.LocalPayRequest;
import com.hongyu.util.bankEntity.QueryRequest;
import com.hongyu.util.bankEntity.Response;
import com.hongyu.util.bankEntity.SslUtils;

public class BankUtil {

	public static Response testQuery() {// 单笔订单查询测试
		String requestURL = Constants.Query;
		String transName = "IQSR";
		String plain = "transId=IQSR~|~merchantId=370310000004~|~originalorderId=20150915151308~|~originalTransDateTime=20150915151308";
		String response = postInfo(requestURL, "370310000004", transName, plain);
		Response response2 = toResponse(response);
		return response2;
	}

	/** 单笔订单查询接口，返回Response实体 */
	public static Response query(QueryRequest queryRequest) {
		String requestURL = Constants.Query;
		String transName = queryRequest.getTransId();
		String merchantId = queryRequest.getMerchantId();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("transId=" + queryRequest.getTransId() + "~|~");
		sBuilder.append("merchantId=" + queryRequest.getMerchantId() + "~|~");
		sBuilder.append("originalorderId=" + queryRequest.getOriginalorderId() + "~|~");
		sBuilder.append("originalTransDateTime=" + queryRequest.getOriginalTransDateTime());
		if (queryRequest.getOriginalTransAmt() != null) {
			sBuilder.append("~|~" + "originalTransAmt=" + queryRequest.getOriginalTransAmt());
		}
		if (queryRequest.getMerURL() != null) {
			sBuilder.append("~|~" + "merURL=" + queryRequest.getMerURL());
		}
		String plain = sBuilder.toString();
		String response = postInfo(requestURL, merchantId, transName, plain);
		Response response2 = toResponse(response);
		return response2;

	}

	public static String testLocalPay() {// 个人支付测试，返回html代码

		String requestURL = Constants.PersonalPay;
		String transName = "IPER";
		String plain = "transId=IPER~|~merchantId=370310000004~|~orderId=20150915151308~|~transAmt=0.01~|~TransDateTime=20150915151308~|~currencyType=01~|~customerName=大枣~|~merSecName=小贩~|~productInfo=红了~|~customerEmail=dazao@daozao.com~|~merURL=http://10.200.44.112:8007/testcollect/RESULT.do~|~merURL1=http://10.200.44.112:8007/testcollect/CONG.do~|~payIp=10.196.31.162~|~msgExt=甜";
		String response = postInfo(requestURL, "370310000004", transName, plain);
		return response;
	}

	/** 本行支付，返回html代码 **/
	public static String personalPay(LocalPayRequest localPayRequest) throws Exception {
		
		String requestURL;
		if(Constants.PPayCode.equals(localPayRequest.getTransId())){
			requestURL=Constants.PersonalPay;
		}else if(Constants.EPayCode.equals(localPayRequest.getTransId())){
			requestURL=Constants.EnterPrisePay;
		}else{
			throw new Exception("交易代码错误");
		}
		String transName = localPayRequest.getTransId();
		String merchantId = localPayRequest.getMerchantId();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("transId=" + localPayRequest.getTransId() + "~|~");
		sBuilder.append("merchantId=" + localPayRequest.getMerchantId() + "~|~");
		sBuilder.append("orderId=" + localPayRequest.getOrderId() + "~|~");
		sBuilder.append("transAmt=" + localPayRequest.getTransAmt() + "~|~");
		sBuilder.append("transDateTime=" + localPayRequest.getTransDateTime() + "~|~");
		sBuilder.append("currencyType=" + localPayRequest.getCurrencyType() + "~|~");
		sBuilder.append("customerName=" + localPayRequest.getCustomerName() + "~|~");
		sBuilder.append("merSecName=" + localPayRequest.getMerSecName() + "~|~");
		sBuilder.append("productInfo=" + localPayRequest.getProductInfo() + "~|~");
		sBuilder.append("customerEmail=" + localPayRequest.getCustomerEmail() + "~|~");
		sBuilder.append("merURL=" + localPayRequest.getMerURL() + "~|~");
		sBuilder.append("merURL1=" + localPayRequest.getMerURL1() + "~|~");
		sBuilder.append("payIp=" + localPayRequest.getPayIp() + "~|~");
		sBuilder.append("msgExt=" + localPayRequest.getMsgExt());

		String plain = sBuilder.toString();
		String response = postInfo(requestURL, merchantId, transName, plain);
		return response;
	}
	
	public static String testInterBankPay(){
		String requestURL = Constants.interBankPersonal;
		String transName = "IPER";
		String plain = "transId=IPER~|~merchantId=370310000004~|~orderId=20150915180150~|~transAmt=0.01~|~TransDateTime=20150915180150~|~currencyType=01~|~customerName=大枣~|~merSecName=小贩~|~productInfo=红了~|~customerEmail=dazao@daozao.com~|~merURL=http://10.200.44.112:8007/testcollect/RESULT.do~|~merURL1=http://10.200.44.112:8007/testcollect/CONG.do~|~payIp=10.196.31.162~|~payBankNo=09~|~msgExt=甜";
		String response = postInfo(requestURL, "370310000004", transName, plain);
		return response;
	}
	public static String interBankPay(InterBankRequest interBankRequest)throws Exception{
		String requestURL;
		if(Constants.PPayCode.equals(interBankRequest.getTransId())){
			requestURL=Constants.interBankPersonal;
		}else if(Constants.EPayCode.equals(interBankRequest.getTransId())){
			requestURL=Constants.interBankEnterprise;
		}else{
			throw new Exception("交易代码错误");
		}
		String transName = interBankRequest.getTransId();
		String merchantId = interBankRequest.getMerchantId();
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("transId="+interBankRequest.getTransId()+"~|~");
		sBuilder.append("merchantId="+interBankRequest.getMerchantId()+"~|~");
		sBuilder.append("orderId="+interBankRequest.getOrderId()+"~|~");
		sBuilder.append("transAmt="+interBankRequest.getTransAmt()+"~|~");
		sBuilder.append("transDateTime="+interBankRequest.getTransDateTime()+"~|~");
		sBuilder.append("currencyType="+interBankRequest.getCurrencyType()+"~|~");
		sBuilder.append("customerName="+interBankRequest.getCustomerName()+"~|~");
		sBuilder.append("merSecName="+interBankRequest.getMerSecName()+"~|~");
		sBuilder.append("productInfo="+interBankRequest.getProductInfo()+"~|~");
		sBuilder.append("customerEmail="+interBankRequest.getCustomerEmail()+"~|~");
		sBuilder.append("merURL="+interBankRequest.getMerURL()+"~|~");
		sBuilder.append("merURL1="+interBankRequest.getMerURL1()+"~|~");
		sBuilder.append("payIp="+interBankRequest.getPayIp()+"~|~");
		sBuilder.append("payBankNo="+interBankRequest.getPayBankNo()+"~|~");
		sBuilder.append("msgExt="+interBankRequest.getMsgExt()+"~|~");
		
		String plain = sBuilder.toString();
		String response = postInfo(requestURL, merchantId, transName, plain);
		return response;
		
	}
	public static byte[] toBytes(String Plain, String merchantId) throws Exception {
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(merchantId);// 商户号
		signParam.setTransId(null);
		signParam.setPlain(Plain);// 明文
		signParam.setCharset("GBK");// 明文使用的字符集
		signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
		signParam.setAlgorithm("MD5withRSA");// 签名算法

		String sign = MerchantSignTool.sign(signParam);
		byte[] content = ("Plain="+Plain + "\r\n"+"Signature=" + sign).getBytes();
		return content;
	}

	/** 将String类型的响应，转换成Response实体 */
	private static Response toResponse(String str) {
		Response response = null;
		try {
			BufferedReader reader = new BufferedReader(new StringReader(str));
			response = new Response();

			String ResponseCode = reader.readLine();

			int idx1 = ResponseCode.indexOf("=");
			ResponseCode = ResponseCode.substring(idx1 + 1);
			System.out.println("ResponseCode: " + ResponseCode);
			response.setResponseCode(ResponseCode);

			String Plain = reader.readLine();

			int idx2 = Plain.indexOf("=");
			Plain = Plain.substring(idx2 + 1);
			System.out.println("Plain: " + Plain);
			response.setPlain(Plain);

			String Signature = reader.readLine();
			int idx3 = Signature.indexOf("=");
			Signature = Signature.substring(idx3 + 1);
			System.out.println("Signature: " + Signature);
			response.setSignature(Signature);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/** 发送post请求，返回string类型结果 */
	private static String postInfo(String url, String merchantId, String TransId, String plain) {
		SignParameterObject signParam = new SignParameterObject();
		signParam.setMerchantId(merchantId);// 商户号
		signParam.setTransId(TransId);
		signParam.setPlain(plain);// 明文
		signParam.setCharset("GBK");// 明文使用的字符集
		signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
		signParam.setAlgorithm("MD5withRSA");// 签名算法
		try {
			String sign = MerchantSignTool.sign(signParam);
			System.out.println("sign:" + sign);
			System.out.println("tranId:" + TransId);
			System.out.println("palin:" + plain);
			RequestParameterObject requestParameterObject = new RequestParameterObject();
			requestParameterObject.setRequestURL(url);
			requestParameterObject.setRequestData("TransName=" + TransId + "&Plain=" + plain + "&Signature=" + sign);
			requestParameterObject.setRequestCharset("GBK");
			requestParameterObject.addRequestProperties("Content-type",
					"application/x-www-form-urlencoded; charset=GBK");
			requestParameterObject.setVerifyFlag(false);

			SslUtils.ignoreSsl();
			byte[] result = HttpUtil.sendHost(requestParameterObject);
			String response = new String(result, "GBK");// 银行返回的响应
			System.out.println("response: " + response);
			// Response response2 = toResponse(response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Object> getRequest(String Plain, String ResponseCode, String Signature) throws Exception {
		// VerifyParameterObject verifyParam=new VerifyParameterObject();
		//
		// verifyParam.setPlain(Plain);//明文
		// verifyParam.setPlainCharset("GBK");//明文使用的字符集
		// verifyParam.setSign(Signature);//签名串
		// verifyParam.setType(0);//0-普通报文
		// verifyParam.setAlgorithm("MD5withRSA");//签名算法
		// boolean verify=MerchantSignTool.verify(verifyParam);
		// if(verify=false){
		// throw new Exception("验签失败,非有效内容或签名");
		// }
		Map<String, Object> map = new HashMap<>();
		int idx = Plain.indexOf("=");
		if (idx < 0) {
			return map;
		}
		String content = Plain.substring(idx + 1);
		String[] pairs = content.split("~|~");
		for (String tmp : pairs) {
			String[] obj = tmp.split("=");
			String key = obj[0];
			String value = null;
			if (obj.length > 1) {
				value = obj[1];
			}
			map.put(key, value);
		}
		map.put("ResponseCode", ResponseCode);
		return map;
	}
	
    /**本行支付生成明文*/
	public static String generatePlain(LocalPayRequest localPayRequest)
	{
		String plain="transId=";
		plain=plain+localPayRequest.getTransId()+"~|~merchantId=";
		plain=plain+localPayRequest.getMerchantId()+"~|~orderId=";
		plain=plain+localPayRequest.getOrderId()+"~|~transAmt=";
		plain=plain+localPayRequest.getTransAmt()+"~|~transDateTime=";
		plain=plain+localPayRequest.getTransDateTime()+"~|~currencyType=";
		plain=plain+localPayRequest.getCurrencyType()+"~|~customerName=";
		plain=plain+localPayRequest.getCustomerName()+"~|~merSecName=";
		plain=plain+localPayRequest.getMerSecName()+"~|~productInfo=";
		plain=plain+localPayRequest.getProductInfo()+"~|~customerEmail=";
		plain=plain+localPayRequest.getCustomerEmail()+"~|~merURL=";
		plain=plain+localPayRequest.getMerURL()+"~|~merURL1=";
		plain=plain+localPayRequest.getMerURL1()+"~|~payIp=";
		plain=plain+localPayRequest.getPayIp()+"~|~msgExt=";
		plain=plain+localPayRequest.getMsgExt();
		
		return plain;
	}
	
	/**跨行支付生成明文*/
	public static String generatePlain(InterBankRequest interBankRequest)
	{
		String plain="transId=";
		plain=plain+interBankRequest.getTransId()+"~|~merchantId=";
		plain=plain+interBankRequest.getMerchantId()+"~|~orderId=";
		plain=plain+interBankRequest.getOrderId()+"~|~transAmt=";
		plain=plain+interBankRequest.getTransAmt()+"~|~transDateTime=";
		plain=plain+interBankRequest.getTransDateTime()+"~|~currencyType=";
		plain=plain+interBankRequest.getCurrencyType()+"~|~payBankNo=";
		plain=plain+interBankRequest.getPayBankNo()+"~|~customerName=";
		plain=plain+interBankRequest.getCustomerName()+"~|~merSecName=";
		plain=plain+interBankRequest.getMerSecName()+"~|~productInfo=";
		plain=plain+interBankRequest.getProductInfo()+"~|~customerEmail=";
		plain=plain+interBankRequest.getCustomerEmail()+"~|~merURL=";
		plain=plain+interBankRequest.getMerURL()+"~|~merURL1=";
		plain=plain+interBankRequest.getMerURL1()+"~|~payIp=";
		plain=plain+interBankRequest.getPayIp()+"~|~msgExt=";
		plain=plain+interBankRequest.getMsgExt();
		
		return plain;
	}
//	public static String bankRequest(String Plain, String ResponseCode, String Signature) throws Exception
//	{
//		 String response=null;
//		 VerifyParameterObject verifyParam=new VerifyParameterObject();
//		
//		 verifyParam.setPlain(Plain);//明文
//		 verifyParam.setPlainCharset("GBK");//明文使用的字符集
//		 verifyParam.setSign(Signature);//签名串
//		 verifyParam.setType(0);//0-普通报文
//		 verifyParam.setAlgorithm("MD5withRSA");//签名算法
//		 boolean verify=MerchantSignTool.verify(verifyParam);
//		 if(verify=false){
//		     throw new Exception("验签失败,非有效内容或签名");
//		 }
//		 else {
//			 String[] pairs = Plain.split("~|~");
//			 response=response+"Plain=";
//			 response=response+pairs[1]+"~|~"; //merchantId
//			 response=response+pairs[2]+"~|~"; //orderId
//			 response=response+pairs[4]+"~|~"; //transDateTime
//			 response=response+"procStatus=1"+"~|~";
//			 //先用123服务器的
////			 response=response+"merURL2=http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord";
//			 response=response+"merURL2=https://123.57.72.6/hongyup/#/storeManagement/generalService/rechargeRecord";
//			 response=response+pairs[1]+"&"+pairs[2];
//			 response=response+"\r\n"+"ResponseCode=0000"+"\r\n";
//			 response=response+"Signature="+Signature;
//		 }	 
//		
//		return response;
//	}
}
