package com.hongyu.controller.gsbing.industrialBank;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.industrialBankUtil.Signature;

@Controller
@RequestMapping("/bankCallback")
public class BankCallback {
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="storeAccountLogServiceImpl")
	private StoreAccountLogService storeAccountLogService;
	
	@Resource(name="storeAccountServiceImpl")
	private StoreAccountService storeAccountService;
	
//	static class WapParams{
//		private String appid; //商户号
//		private String event; //时间类型
//		private String mac; //消息校验码
//		private String order_amount; //订单金额
//		private String order_no; //订单号
//		private String order_time; //订单生成时间
//		private String pay_time; //付款时间
//		private String sign_type; //数字签名类型
//		private String sno; //支付网关流水号
//		private String timestamp; //时间戳
//		
//		//交易失败的字段
//		private String errcode; //错误代码
//		private String errmsg; //错误信息
//		
//		public String getErrmsg() {
//			return errmsg;
//		}
//		public void setErrmsg(String errmsg) {
//			this.errmsg = errmsg;
//		}
//		public String getAppid() {
//			return appid;
//		}
//		public void setAppid(String appid) {
//			this.appid = appid; 
//		}
//		public String getEvent() {
//			return event;
//		}
//		public void setEvent(String event) {
//			this.event = event;
//		}
//		public String getMac() {
//			return mac;
//		}
//		public void setMac(String mac) {
//			this.mac = mac;
//		}
//		public String getOrder_amount() {
//			return order_amount;
//		}
//		public void setOrder_amount(String order_amount) {
//			this.order_amount = order_amount;
//		}
//		public String getOrder_no() {
//			return order_no;
//		}
//		public void setOrder_no(String order_no) {
//			this.order_no = order_no;
//		}
//		public String getOrder_time() {
//			return order_time;
//		}
//		public void setOrder_time(String order_time) {
//			this.order_time = order_time;
//		}
//		public String getPay_time() {
//			return pay_time;
//		}
//		public void setPay_time(String pay_time) {
//			this.pay_time = pay_time;
//		}
//		public String getSign_type() {
//			return sign_type;
//		}
//		public void setSign_type(String sign_type) {
//			this.sign_type = sign_type;
//		}
//		public String getSno() {
//			return sno;
//		}
//		public void setSno(String sno) {
//			this.sno = sno;
//		}
//		public String getTimestamp() {
//			return timestamp;
//		}
//		public void setTimestamp(String timestamp) {
//			this.timestamp = timestamp;
//		}
//		public String getErrcode() {
//			return errcode;
//		}
//		public void setErrcode(String errcode) {
//			this.errcode = errcode;
//		}
//	}
//	@RequestMapping(value="industrialBack",method = RequestMethod.POST)
//	@ResponseBody
//    public void industrialBack(String jsonStr,HttpServletRequest request,HttpServletResponse response)
//    {
//    	try {
//    		Map<String,String> params=Signature.jsonToMap(jsonStr);
//    		if(Signature.verifyMAC(params)==true) { //验签成功
//    			String event=params.get("event");                                                                                                           
//    			if(event.equals("NOTIFY_ACQUIRE_SUCCESS")) { //支付成功
//        			String order_no=params.get("order_no");
//        			String order_amount=params.get("order_amount");
//        			List<Filter> filters=new ArrayList<Filter>();
//        			filters.add(Filter.eq("chargeOrderSn", order_no));
//        			List<StoreAccountLog> storeAccountLogs=storeAccountLogService.findList(null,filters,null);
//        			filters.clear();
//        			StoreAccountLog accountLog=storeAccountLogs.get(0);
//        			accountLog.setStatus(5); //设置状态为"已完成支付"
//        			storeAccountLogService.update(accountLog);
//        			Store store=accountLog.getStore();
//        			filters.add(Filter.eq("store", store));
//        			List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters,null);
//        			StoreAccount storeAccount=storeAccounts.get(0);
//        			BigDecimal balance=new BigDecimal(order_amount);
//        			balance=balance.add(storeAccount.getBalance()); //加上充值金额
//        			storeAccount.setBalance(balance);
//        			storeAccountService.update(storeAccount);
//        		}
//    		}
//    		//验签未成功
//    		else {
//    			response.setStatus(500);  //设置状态码为500,只要是非200就行
//    		}
//    	}
//    	catch(Exception e) {
//    		e.printStackTrace();
//    	}  	
//    }
	
	@RequestMapping(value="/industrialBack")
//	@ResponseBody
    public void industrialBack(HttpServletRequest request,HttpServletResponse response)
    {
//		String method = request.getMethod();
//		Map<String,String> params = new HashMap<String,String>();
//		Map<?, ?> reqParams = request.getParameterMap();
//		Iterator<?> iter = reqParams.keySet().iterator();		
//    	try {	
//    		while (iter.hasNext()) {
//    			String name = (String) iter.next();
//    			String[] values = (String[]) reqParams.get(name);
//    			if("get".equalsIgnoreCase(method)) 
//    				params.put(name, new String(values[0].getBytes("ISO-8859-1"), "UTF-8"));
//    			else
//    				params.put(name, values[0]);
//    		}
//    		if(Signature.verifyMAC(params)==true) { //验签成功     
//    			if("post".equalsIgnoreCase(method)){
//    				if("NOTIFY_ACQUIRE_SUCCESS".equalsIgnoreCase(params.get("event"))) { //支付成功
//            			String order_no=params.get("order_no");
//            			String order_amount=params.get("order_amount");
		                String order_no="MDCZ201809100004";
            			List<Filter> filters=new ArrayList<Filter>();
            			filters.add(Filter.eq("chargeOrderSn", order_no));
            			List<StoreAccountLog> storeAccountLogs=storeAccountLogService.findList(null,filters,null);
            			filters.clear();
            			StoreAccountLog accountLog=storeAccountLogs.get(0);
            			accountLog.setStatus(5); //设置状态为"已完成支付"
            			storeAccountLogService.update(accountLog);
            			Store store=accountLog.getStore();
            			filters.add(Filter.eq("store", store));
            			List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters,null);
            			StoreAccount storeAccount=storeAccounts.get(0);
            			BigDecimal balance=new BigDecimal(15);
            			balance=balance.add(storeAccount.getBalance()); //加上充值金额
            			storeAccount.setBalance(balance);
            			storeAccountService.update(storeAccount);
//            		}
//    			}		
//    		}
    		//验签未成功
//    		else {
//    			response.setStatus(500);  //设置状态码为500,只要是非200就行
//    		}
//    	}
//    	catch(Exception e) {
//    		e.printStackTrace();
//    	}  	
    }
}
