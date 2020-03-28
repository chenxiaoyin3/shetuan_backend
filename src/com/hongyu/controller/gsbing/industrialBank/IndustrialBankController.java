package com.hongyu.controller.gsbing.industrialBank;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreService;
import com.hongyu.util.industrialBankUtil.Configure;
import com.hongyu.util.industrialBankUtil.DateTimeUtil;
import com.hongyu.util.industrialBankUtil.EPay;
import com.hongyu.util.industrialBankUtil.IPUtil;
import com.hongyu.util.industrialBankUtil.Signature;

@Controller
@RequestMapping("industrialBank")
public class IndustrialBankController {
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="storeAccountLogServiceImpl")
	private StoreAccountLogService storeAccountLogService;
	
	
//	@RequestMapping(value="recharge",method = RequestMethod.POST)
//	@ResponseBody
//	public Json recharge(BigDecimal money,HttpSession session)
//	{
//		Json json=new Json();
//		try {
//			/**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			Department department=hyAdmin.getDepartment();
//			List<Filter> filters=new ArrayList<Filter>();
//			filters.add(Filter.eq("department", department));
//			List<Store> stores=storeService.findList(null,filters,null);
//			filters.clear();
//			if(stores.size()>0) {
//				Store store=stores.get(0);
//				StoreAccountLog storeAccountLog=new StoreAccountLog();
//				String produc="";
//				Date cur = new Date();
//				DateFormat format = new SimpleDateFormat("yyyyMMdd");
//				String dateStr = format.format(cur);
//				filters.add(Filter.eq("type", SequenceTypeEnum.mendianRecharge));
//				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
//				CommonSequence c = ss.get(0);
//				Long value = c.getValue() + 1;
//				c.setValue(value);
//				commonSequenceService.update(c);
//				produc="MDCZ" + dateStr + String.format("%04d", value);		
//				
//				/**增加门店充值记录*/
//				storeAccountLog.setChargeOrderSn(produc);
//				storeAccountLog.setMoney(money);
//				storeAccountLog.setStore(store);
//				storeAccountLog.setType(0); //充值
//				storeAccountLog.setProfile("兴业银行充值");
//				storeAccountLog.setStatus(4); //设置状态 "未成功支付"
//				storeAccountLogService.save(storeAccountLog);
//				
//				String order_amount=money.toString();
//				String order_title=store.getStoreName()+"充值";
//				String order_desc="门店充值";
//				String remote_ip=IPUtil.getLocalIp();
//				String response=EPay.gpPay(produc, order_amount, order_title, order_desc, remote_ip);
//				Map<String,Object> map=new HashMap<>();
//				map.put("bankHtml",response);
//				json.setSuccess(true);
//				json.setMsg("传输参数成功");
//				json.setObj(map);			
//			}	
//			else {
//				json.setSuccess(false);
//				json.setMsg("没有对应门店");
//			}			
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg("传输参数失败");
//		}
//		return json;
//	}
	
	
	@RequestMapping(value="recharge",method = RequestMethod.POST)
	@ResponseBody
	public Json recharge(BigDecimal money,HttpSession session)
	{
		Json json=new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			filters.clear();
			if(stores.size()>0) {
				Store store=stores.get(0);
				StoreAccountLog storeAccountLog=new StoreAccountLog();
				String produc="";
				Date cur = new Date();
				DateFormat format = new SimpleDateFormat("yyyyMMdd");
				String dateStr = format.format(cur);
				filters.add(Filter.eq("type", SequenceTypeEnum.mendianRecharge));
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MDCZ" + dateStr + String.format("%04d", value);		
				
				/**增加门店充值记录*/
				storeAccountLog.setChargeOrderSn(produc);
				storeAccountLog.setMoney(money);
				storeAccountLog.setStore(store);
				storeAccountLog.setType(0); //充值
				storeAccountLog.setProfile("兴业银行充值");
				storeAccountLog.setStatus(4); //设置状态 "未成功支付"
				storeAccountLogService.save(storeAccountLog);
				
				String order_amount=money.toString();
				String order_title=store.getStoreName()+"充值";
				String order_desc="门店充值";
				String remote_ip=IPUtil.getLocalIp();
//				String response=EPay.gpPay(produc, order_amount, order_title, order_desc, remote_ip);
				Map<String,String> params=new HashMap<>();
				params.put("order_no", produc);
				params.put("sub_mrch", "收付直通车测试");
				params.put("order_title", order_title);
				params.put("cur","CNY");
				params.put("ver", "01");
				params.put("order_ip", remote_ip);
				params.put("order_time", DateTimeUtil.getDateTime());
				params.put("order_desc", order_desc);
				params.put("sign_type", "RSA");
				params.put("service", "cib.epay.acquire.cashier.netPay");
				params.put("order_amount", order_amount);
				params.put("appid", "Q0001522");
				params.put("timestamp", DateTimeUtil.getDateTime());
				String mac=Signature.generateMAC(params);
				params.put("mac", mac);
				json.setSuccess(true);
				json.setMsg("传输参数成功");
				json.setObj(params);			
			}	
			else {
				json.setSuccess(false);
				json.setMsg("没有对应门店");
			}			
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("传输参数失败");
		}
		return json;
	}
	
//	@RequestMapping(value="recharge")
//    @ResponseBody
//	public void recharge(BigDecimal money,HttpSession session,HttpServletResponse response) 
//	{
//		try {
//			/**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			Department department=hyAdmin.getDepartment();
//			List<Filter> filters=new ArrayList<Filter>();
//			filters.add(Filter.eq("department", department));
//			List<Store> stores=storeService.findList(null,filters,null);
//			filters.clear();
//			if(stores.size()>0) {
//				Store store=stores.get(0);
//				StoreAccountLog storeAccountLog=new StoreAccountLog();
//				String produc="";
//				Date cur = new Date();
//				DateFormat format = new SimpleDateFormat("yyyyMMdd");
//				String dateStr = format.format(cur);
//				filters.add(Filter.eq("type", SequenceTypeEnum.mendianRecharge));
//				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
//				CommonSequence c = ss.get(0);
//				Long value = c.getValue() + 1;
//				c.setValue(value);
//				commonSequenceService.update(c);
//				produc="MDCZ" + dateStr + String.format("%04d", value);		
//				
//				/**增加门店充值记录*/
//				storeAccountLog.setChargeOrderSn(produc);
//				storeAccountLog.setMoney(money);
//				storeAccountLog.setStore(store);
//				storeAccountLog.setType(0); //充值
//				storeAccountLog.setProfile("兴业银行充值");
//				storeAccountLog.setStatus(4); //设置状态 "未成功支付"
//				storeAccountLogService.save(storeAccountLog);
//				
//				OutputStream outputStream = response.getOutputStream();
//				String order_amount=money.toString();
//				String order_title=store.getStoreName()+"充值";
//				String order_desc="门店充值";
//				String remote_ip=IPUtil.getLocalIp();
//				String s=EPay.gpPay(produc, order_amount, order_title, order_desc, remote_ip);
//				outputStream.write(s.getBytes("utf-8"));
//			}		
//		}
//		catch(IOException e) {
//			e.printStackTrace();
//		}
//	}
    
	@RequestMapping(value="macout")
	@ResponseBody
	public String macout()
	{
		String mac=null;
		try {
			Map<String,String> params=new HashMap<>();
			params.put("appid", "Q0001522");
			params.put("service", "cib.epay.acquire.cashier.netPay");
			params.put("order_no", "MDCZ201809080001");
			params.put("sub_mrch", "zhitongcheshili");
			params.put("order_title", "邓晓旭门店充值");
			params.put("cur", "CNY");
			params.put("ver","01");
			params.put("order_ip", "10.108.164.11");
			params.put("order_time","20180908170001");
			params.put("order_amount", "10");
			params.put("sign_type", "RSA");
			params.put("order_desc", "门店充值");
			params.put("timestamp", "20180908170001");
			mac=Signature.generateMAC(params);
			System.out.println(mac);
//			System.out.println(Configure.getMrch_cert());
			
	
	    	
//	    	String s_xmlpath="com/hongyu/source/appsvr_client.pfx";
//	    	ClassLoader classLoader=IndustrialBankController.class.getClassLoader();
//			InputStream in = classLoader.getResourceAsStream(s_xmlpath);
//			byte[] ch = new byte[1024];
//			int len = 0;
//			System.out.println("InputStream start:");
//			while ((len = in.read(ch)) != -1) {
//				System.out.println(new String(ch, 0, len));
//			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return mac;
	}
	
}
