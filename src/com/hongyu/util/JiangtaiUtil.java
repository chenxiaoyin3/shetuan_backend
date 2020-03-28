package com.hongyu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hongyu.Json;
import com.hongyu.entity.ConfirmMessage;
import com.hongyu.entity.ConfirmMessageOrderCancel;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.entity.InsuranceOrder;

public class JiangtaiUtil {
	/**
	 * 投保出单接口
	 * HTTP/POST （UTF-8编码）
	 * 服务地址:http://lytest.jiangtai.com:8003/lvap/BigCustomer/TravelAccident/order
	 * 接口参数:
	 * confirmMessage
	 * md5sign
	 * encoding
	 * */
	public static Json order(ConfirmMessage confirmMessageOrder){
		//江泰的投保接口
		String url = Constants.JT_ORDER_URL;
		//将对象装换成json形式
		//String confirmMessageToJson = JSONObject.toJSONString(confirmMessageOrder);
		//将对象转换成XML格式
		//System.out.println("confirm = "+confirmMessageToJson);
		String confirmMessageToXML = XStreamUtil.beanToXml(confirmMessageOrder);
		//System.out.println(confirmMessageToXML.length()+" confirmMessageToXML = "+ confirmMessageToXML);
		//将json的数据转化成HEX格式
		//String confirmMessageHex = StringToHexString(confirmMessageToJson);
		//将XML数据转化成HEX格式
		String confirmMessageHex = HexStringUtils.stringToHexString(confirmMessageToXML);
		//String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"<ChinaTourinsRequest><channel>河北虹宇国际旅行社有限公司</channel><channelComCode>20000000</channelComCode><travelAgencyCode>20000000</travelAgencyCode><travelAgencyLicenseCode>L-HEB-CJ00024</travelAgencyLicenseCode><channelTradeCode>SYN</channelTradeCode><channelBusinessCode></channelBusinessCode><channelTradeDate>2018-06-20 17:28:55</channelTradeDate><channelTradeSerialNo>TQ-2018061100001</channelTradeSerialNo><channelOperateCode></channelOperateCode><startDate>2018-06-17 00:00:00</startDate><endDate>2018-06-19 00:00:00</endDate><contactName>刘潇</contactName><contactPhone>13356567878</contactPhone><payType>1</payType><travelRoute>北京三日游1</travelRoute><travelGroupNo>2018061100001</travelGroupNo><productCode>176</productCode><sumQuantity>1</sumQuantity><isSendSms>0</isSendSms><insuredList><insureInfo><insuredName>张三</insuredName><identifyNumber>110100199303030056</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-17</birthDay><sex>M</sex></insureInfo><insureInfo><insuredName>方大学</insuredName><identifyNumber>110100199811031111</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-19</birthDay><sex>F</sex></insureInfo><insureInfo><insuredName>唐老鸭</insuredName><identifyNumber>110100198808181056</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-05</birthDay><sex>M</sex></insureInfo><insureInfo><insuredName>米老鼠</insuredName><identifyNumber>110100199303030056</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-08</birthDay><sex>M</sex></insureInfo><insureInfo><insuredName>唐小鸭</insuredName><identifyNumber>110100197805167700</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-02</birthDay><sex>F</sex></insureInfo><insureInfo><insuredName>米小鼠</insuredName><identifyNumber>110100200810159870</identifyNumber><identifyType>0</identifyType><birthDay>2018-06-11</birthDay><sex>M</sex></insureInfo></insuredList></ChinaTourinsRequest>";
		//System.out.println(test.length()+" test = "+test);
//		if(confirmMessageToXML.equals(test)){
//			System.out.println("toxml之后的字符串相等");
//		}
		
		//得到MD5编码
		String md5sign = MD5Util.EncodeByMD5(Constants.MD5KEY+confirmMessageHex);
		//System.out.println("confirmMessage = "+confirmMessageHex);
		//System.out.println("md5sign = "+md5sign);
		//设置编码格式  默认是“HEX”
		String encoding = "hex";
		//组合参数
		String param = "confirmMessage="+confirmMessageHex+"&md5sign="+md5sign+"&encoding="+encoding;
		//System.out.println("param = "+param);
		//发送post请求
		Json result = sendPost(url, param);
		//System.out.println("result = "+result);
		return result;
	}
	/**保单撤单接口
	 * 此种撤单只能按照整个团来撤，如果一个团作为一个订单来投保的话，那么撤单的时候也是这个团一起撤掉。
	 * HTTP/POST （UTF-8编码）
	 * 服务地址:http://lytest.jiangtai.com:8003/lvap/BigCustomer/travelaccident/orderCancel
	 * 接口参数:
	 * confirmMessage
	 * md5sign
	 * encoding
	 * */
	public static Json orderCancel(ConfirmMessage cmoc){
		Json json = new Json();
		//江泰的撤单接口
		String url = Constants.JT_CANCEL_ORDER_URL;
		//将实体转化成json形式
		//String confirmMessage = JSONObject.toJSONString(cmoc);
		//将实体转化成XML形式
		String confirmMessage = XStreamUtil.beanToXml(cmoc);
		//转化成hexString
		String confirmMessageHex = HexStringUtils.stringToHexString(confirmMessage);
		//得到MD5的编码
		String md5sign = MD5Util.EncodeByMD5(Constants.MD5KEY+confirmMessageHex);
		//设置编码格式
		String encoding = "HEX";
		//组合参数
		String param = "confirmMessage="+confirmMessageHex+"&md5sign="+md5sign+"&encoding="+encoding;
		//发送Post请求
		json = sendPost(url, param);
		return json;
	}
	/**
	 * 获取电子保单的下载url
	 * @param serialNo
	 * @return
	 */
	public static String OrderDown(String serialNo){
		String url = Constants.JT_ORDER_DOWN_URL;
		String param = "serialNo="+serialNo+"&travelCode="+Constants.TRAVEL_AGENCY_CODE;
		String result = url+"?"+param;
		return result;
	}
	/**
	 * 获取保单的个人凭证下载url
	 */
//	public static String OrderDownCertificate(InsuranceOrder insuranceOrder){
//		//江泰提供的电子个人凭证下载的接口
//		String url = Constants.JT_CERTIFICATE_DOWN_URL;
//		//证件号码
//		String cardNo = insuranceOrder.getCertificateNumber();
//		//渠道交易流水号
//		String serialNo = insuranceOrder.getJtChannelTradeSerialNo();
//		//旅行社代码
//		String travelCode = Constants.TRAVEL_AGENCY_CODE;
//		//需要按规格组装url
//		String suffix = serialNo+"-"+travelCode+"-"+cardNo;
//		
//		//String param = "cardNo"+cardNo+"&serialNo="+serialNo+"&travelCode="+travelCode;
//		//参数包含在了路径中，就不需要传参数了。
//		return url+suffix;
//	}
	/**
	 * 获取保单的个人凭证下载url
	 */
	public static String OrderDownCertificate(String cardNo,String serialNo){
		//江泰提供的电子个人凭证下载的接口
		String url = Constants.JT_CERTIFICATE_DOWN_URL;
		//证件号码
//		String cardNo = policyHolderInfo.getCertificateNumber();
//		//渠道交易流水号
//		String serialNo = policyHolderInfo.getJtChannelTradeSerialNo();
		//旅行社代码
		String travelCode = Constants.TRAVEL_AGENCY_CODE;
		//需要按规格组装url
		String suffix = serialNo+"-"+travelCode+"-"+cardNo;
		
		//String param = "cardNo"+cardNo+"&serialNo="+serialNo+"&travelCode="+travelCode;
		//参数包含在了路径中，就不需要传参数了。
		return url+suffix;
	}
	public static Json sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        Json json = new Json();
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-type","application/x-www-form-urlencoded; charset=UTF-8" );
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求失败" + e);
            json.setMsg("发送post请求失败" + e.getMessage());
            json.setSuccess(false);
            return json;

        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //System.out.println("result: "+result);
        json.setSuccess(true);
        json.setMsg("发送post请求成功");
        json.setObj(result);
        return json;
    }

    public static Json sendGet(String url, String param) {
        Json json = new Json();
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
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded; charset=UTF-8" );
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET下载保单请求出现异常！" + e);
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg("发送GET下载保单请求出现异常！" + e.getMessage());
            return json;
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
        json.setMsg("发送GET下载保单请求成功");
        json.setSuccess(true);
        json.setObj(result);
        return json;
    }
    /**
     * 从身份证号码中获取生日信息
     * 返回的格式是yyyy-MM-dd
     * @param certificateNum
     * @return
     */
    public static String getBirthdayFromCertificate(String certificateNum){
    	/*
    	 * 中华人民共和国公民身份证号码为18位
    	 * 前六位为地址码
    	 * 接下来8位为出生年月日YYYYMMDD
    	 * 接着三位是顺序及性别码
    	 * 最后一位是校验和，有可能是X
    	 */
    	String year = certificateNum.substring(6,10);
    	String month = certificateNum.substring(10,12);
    	String day = certificateNum.substring(12,14);
    	String birthDay = year+"-"+month+"-"+day;
    	return birthDay;
    }
    /**
     * 虹宇证件类型转换到江泰的证件类型
     * @param hyType
     * @return
     */
    public static Integer CertificateTypeSwitch(Integer hyType){
    	//（虹宇）证件类型0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证
		//（江泰）证件类型1:身份证   2:军官证  3:护照  4:其他
		/*江泰的身份证定义为1，而我们自己定义的身份证为0*/
    	Integer jtType = 0;
    	switch(hyType){
    	case 0:
    		jtType = 1;
    		break;
    	case 1:
    		jtType = 3;
    		break;
    	case 3:
    		jtType = 2;
    		break;
    	default:
    		jtType = 4;
    		break;
    	}
    	return jtType;
    }

}
