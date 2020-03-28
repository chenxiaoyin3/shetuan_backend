package com.hongyu.util.contract;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fadada.sdk.client.FddClientBase;
import com.fadada.sdk.client.request.SignResultQueryRequest;
import com.fadada.sdk.util.crypt.FddEncryptTool;
import com.fadada.sdk.util.http.HttpsUtil;
import com.hongyu.util.Constants;
import com.hongyu.util.HttpReqUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;


public class FadadaContract {
	private static final String APP_ID = Constants.FDD_APP_ID;
	private static final String APP_SECRET = Constants.FDD_APP_SECRET;
	private static final String VERSION = Constants.FDD_VERSION;
	private static final String URL = Constants.FDD_URL;
	public static void testCA(){
		/*
		 * 测试环境相关参数	　	　
		 * 地址①：	http://test.api.fabigbig.com:8888/api/
		 * 地址②：	https://testapi.fadada.com:8443/api/
		 * app_id：	401364
		 * app_secret：	gb5LUvQ24ylaBUXSw9nS6LMt
		 * 公司名称：	河北虹宇国际旅行社有限公司
		 * 客户编号：	28A5FF14BDE80CB145E61E0DA9E01444
		 * */
		String CAURL = FadadaContract.URL;
		FddClientBase clientBase = new FddClientBase(FadadaContract.APP_ID, FadadaContract.APP_SECRET,
										FadadaContract.VERSION, CAURL);
		String response = clientBase.invokeSyncPersonAuto("李哈哈洋","", "622827199512234938" , "0", "13366021228");
		JSONObject jsonObject = JSONObject.parseObject(response);
		System.out.println(jsonObject.get("customer_id"));
		System.out.println(response);
		//String liyangId = "F6F6AA011B50EFEDDD097E37B3E8458E";
	}
	public static void uploadContract(){
		String uploadUrl = FadadaContract.URL;
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, uploadUrl);
		File file = new File("D:\\JavaWeb\\workspace\\hy_backend\\src\\com\\hongyu\\util\\contract\\测试合同2.pdf");
		String response = clientBase.invokeUploadDocs("2", "二号测试合同",file, null, ".pdf");
		System.out.println(response);		
	}
	public static String view_template(String templateId){
		String baseUrl = Constants.FDD_URL+"view_template.api";
		
		String timeStamp = HttpsUtil.getTimeStamp();
        StringBuilder sb = new StringBuilder(baseUrl);
        try {
            String msgDigest;
            // Base64(SHA1(app_id+md5(timestamp)+SHA1(app_secret+ contract_id+transaction_id+push_type+customer_id+sign_keyword)))
            String sha1 = FddEncryptTool.sha1(APP_ID + FddEncryptTool.md5Digest(timeStamp) + FddEncryptTool.sha1(APP_SECRET +templateId
            		));
            msgDigest = new String(FddEncryptTool.Base64Encode(sha1.getBytes()));

            sb.append("?app_id=").append(APP_ID);
            sb.append("&v=").append(VERSION);
            sb.append("&timestamp=").append(timeStamp);
            sb.append("&template_id=").append(templateId);
            sb.append("&msg_digest=").append(msgDigest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sb.toString();
	}
	public static void extSign(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		String return_url = "";//签章完成之后，法大大将自动跳转到该地址，这个地址跳转到前端，一般是下载页面，
		String notify_url = "";//签署完成之后，法大大向此接口发送签署结果
		String sign_url = clientBase.invokeExtSign("order28", "F6F6AA011B50EFEDDD097E37B3E8458E", "26", "国内合同模板-3", null,return_url, notify_url);
		System.out.println(sign_url);
	}
	public static void querySignResult(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		SignResultQueryRequest request = new SignResultQueryRequest();
		request.setContractId("3");
		request.setCustomerId("28A5FF14BDE80CB145E61E0DA9E01444");
		request.setTransactionId("order4");
		String response = clientBase.invokeQuerySignResult(request);
		System.out.println(response);
	}
	public static void JiafangextSign(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		String return_url = "";//签章完成之后，法大大将自动跳转到该地址，这个地址跳转到前端，一般是下载页面，
		String notify_url = "";//签署完成之后，法大大向此接口发送签署结果
		String sign_url = clientBase.invokeExtSign("order3", "28A5FF14BDE80CB145E61E0DA9E01444", "3", "国内合同模板-1", null, return_url, notify_url);
		System.out.println(sign_url);
		//HttpServletResponse().sendRedirect(sign_url);
	}
	public static void uploadTemplate(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		File file = new File("D:\\JavaWeb\\workspace\\hy_backend\\src\\com\\hongyu\\util\\contract\\一日游合同模板-添加透明签章位置版本.pdf");
		String response = clientBase.invokeUploadTemplate("template_dx_20190411", file, null);
		System.out.println(response);
	}
	public static void generateContract(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		JSONObject paramter = new JSONObject();
		paramter.put("contractId", "order26");
		paramter.put("customerName", "李上述阳");
		paramter.put("customerNum", "3");
		paramter.put("hyName", "虹宇国际旅行社");
		paramter.put("hyId", "123456");
		JSONObject table = new JSONObject();
		table.put("pageBegin", 1);
		table.put("cellHeight", 30);
		table.put("theFirstHeader", "附一");
		table.put("headers", new String[]{"序号","借款人","贷款人","金额"});
		String row1[] = new String[]{"1","小网","小易","1000"};
		String row2[] = new String[]{"2","小溪","小当","1020"};
		String row3[] = new String[]{"3","小海","小都是","1030"};
		//String row4[] = new String[]{"4","小好","小范德萨","1040"};
		table.put("datas", new String[][]{row1,row2,row3});
		table.put("cellHorizontalAlignment", 0);
		table.put("cellVerticalAlignment", 5);
		table.put("colWidthPercent", new int[]{1,4,4,4});
		JSONArray jsonArray = new JSONArray();
		jsonArray.add(table);
		String response = clientBase.invokeGenerateContract("CT_Inland", "28", "李阳的国内游合同-2", null, null, paramter.toJSONString(), jsonArray.toJSONString());
		
		System.out.println(response);
	}
	public static void extsign_auto(){
		FddClientBase clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		String return_url = "";//签章完成之后，法大大将自动跳转到该地址，这个地址跳转到前端，一般是下载页面，
		String notify_url = "";//签署完成之后，法大大向此接口发送签署结果
		String sign_url = clientBase.invokeExtSignAuto("CT_Inland2", "28A5FF14BDE80CB145E61E0DA9E01444", "1","26", "国内合同模板-3", "旅行社经办人签名：", "0", notify_url);
		System.out.println(sign_url);
		JSONObject jsonObject = JSONObject.parseObject(sign_url);
		System.out.println(jsonObject.get("viewpdf_url"));
	}
	public static String pushdoc_extsign(String transaction_id,String customer_id,String contract_id,String doc_title,String push_type
			,String return_url,String notify_url){
		String baseUrl = "https://testapi.fadada.com:8443/api/pushdoc_extsign.api";
		 String timeStamp = HttpsUtil.getTimeStamp();
	        StringBuilder sb = new StringBuilder(baseUrl);
	        try {
	            String msgDigest;
	            // Base64(SHA1(app_id+md5(transaction_id+timestamp)+SHA1(app_secret+ customer_id)))
	            String sha1 = FddEncryptTool.sha1(APP_ID + FddEncryptTool.md5Digest(timeStamp) + FddEncryptTool.sha1(APP_SECRET +contract_id+transaction_id+push_type+ customer_id));
	            msgDigest = new String(FddEncryptTool.Base64Encode(sha1.getBytes()));

	            sb.append("?app_id=").append(APP_ID);
	            sb.append("&v=").append(VERSION);
	            sb.append("&timestamp=").append(timeStamp);
	            sb.append("&push_type=").append(push_type);
	            sb.append("&transaction_id=").append(transaction_id);
	            sb.append("&customer_id=").append(customer_id);
	            sb.append("&contract_id=").append(contract_id);
	            sb.append("&sign_keyword").append("");
	            sb.append("&doc_title=").append(URLEncoder.encode(doc_title, HttpsUtil.charset));
//	            sb.append("&return_url=").append(URLEncoder.encode(return_url, HttpsUtil.charset));
//	            sb.append("&notify_url=").append(URLEncoder.encode(notify_url, HttpsUtil.charset));
	            sb.append("&return_url=").append(return_url);
	            sb.append("&notify_url=").append(notify_url);
	            sb.append("&msg_digest=").append(msgDigest);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException(e);
	        }
	        return sb.toString();
	}
//	public static void main(String[] args){
//		String templateId = "template_cj_20190411";//template_dx_20190411  template_gn_20190411  template_cj_20190411
//		System.out.println(view_template(templateId));
//		testCA();
//		uploadContract();
//		JiafangextSign();
//		querySignResult();
//		uploadTemplate();
//	generateContract();
//		extSign();
//		extsign_auto();
//		String s = pushdoc_extsign("order29", "F6F6AA011B50EFEDDD097E37B3E8458E", "26", "测试合同-短信", "1", "", "");
//		System.out.println(s);
//		String url = "https://testapi.fadada.com:8443/api/pushdoc_extsign.api?app_id=401364&v=2.0&timestamp=20180808212131&push_type=1&transaction_id=order29&customer_id=F6F6AA011B50EFEDDD097E37B3E8458E&contract_id=26&sign_keyword&doc_title=%E6%B5%8B%E8%AF%95%E5%90%88%E5%90%8C-%E7%9F%AD%E4%BF%A1&return_url=https://testapi.fadada.com:8443/api/pushdoc_extsign.api&notify_url=&msg_digest=Q0E5M0E4NjM2QTkyOTY3Qjg1NDg2Q0RFQTNDQkFCNUIzODFDRjdCQw==";
//		String result = HttpReqUtil.HttpsDefaultExecute("POST", url, null, null, "UTF-8");
//		System.out.println(result);
//		Date date = new Date();
//		String string = date.toString();
//		System.out.println(string);
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String string2 = df.format(date);
//		System.out.println(string2);
//		String old = "HYHT201808140001";
//		String[] ss = old.split("[R]");
//		for(int i=0;i<ss.length;i++){
//			System.out.println(ss[i]);
//		}
//		if(ss.length>1){
//			//说明这是在重新签署之后的再次重签,次数加一
//			Integer rNum = Integer.valueOf(ss[1])+1;
//			System.out.println("rnum = "+rNum);
//			System.out.println(ss[0]+"R"+rNum);
//		}else{
//			//第一次重签
//			System.out.println(ss[0]+"R"+1);
//		}
//	}
}
