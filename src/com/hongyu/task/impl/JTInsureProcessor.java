package com.hongyu.task.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.From;

import org.hibernate.annotations.Filters;
import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.ConfirmMessage;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.InsureInfo;
import com.hongyu.entity.JtOrderResponse;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPolicyHolderInfoService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StoreService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;
import com.hongyu.util.JiangtaiUtil;
import com.hongyu.util.XStreamUtil;
/**
 * 自动将当天发团的所有订单投保到江泰
 * @author li_yang
 *
 */
@Component("jtInsureProcessor")
public class JTInsureProcessor implements Processor{
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyPolicyHolderInfoServiceImpl")
	HyPolicyHolderInfoService hyPolicyHolderInfoService;
	/**
	 * 获取当天投保的保单id
	 * @return
	 * @throws ParseException 
	 */
	private List<Long> getInsuranceOrders() throws ParseException{
		List<Long> ids = new ArrayList<>();
		List<Filter> filters = new ArrayList<>();
		//筛选出当天发团的，且状态为待投保的所有保单。
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String today = DateUtil.getBirthday(new Date());
//		Date startDate = sdf.parse(today);
//		System.out.println(startDate);
//			InsuranceOrder test = insuranceOrderService.find((long) 1);
//			System.out.println(test.getInsuranceStarttime());
		Date today = new Date();
		Date nextDay = DateUtil.getNextDay(today);
		//到发团日期
		//System.out.println("nextDay = "+nextDay);
		//System.out.println(sdf.parse(DateUtil.getSimpleStartOfDayDate(nextDay)));
		//System.out.println(sdf.parse(DateUtil.getSimpleEndOfDayDate(nextDay)));
		filters.add(Filter.ge("insuranceStarttime",sdf.parse(DateUtil.getSimpleStartOfDayDate(nextDay))));
		filters.add(Filter.le("insuranceStarttime", sdf.parse(DateUtil.getSimpleEndOfDayDate(nextDay))));
		//
		filters.add(Filter.eq("status", 1));
		//订单为线路订单类型
		filters.add(Filter.eq("type", 0));
		List<Order> orders = new ArrayList<>();
		orders.add(Order.asc("orderId"));
		List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,orders);
		for(InsuranceOrder tmp:insuranceOrders){
			ids.add(tmp.getId());
		}
		//System.out.println(ids);
		return ids;
	}
	
	@Override
	public void process() {
		try {
			List<Long> ids = getInsuranceOrders();
			System.out.println(ids);
			for(int i = 0; i<ids.size(); i++ ){
			//获取想要投保订单id
					Long insuranceOrderId = ids.get(i);
					InsuranceOrder insuranceOrder = insuranceOrderService.find(insuranceOrderId);
					Long orderId = insuranceOrder.getOrderId();
					List<InsureInfo> insuredList = new ArrayList<>();
					for(HyPolicyHolderInfo tmp:insuranceOrder.getPolicyHolders()){
						InsureInfo info = new InsureInfo();
						//在设置生日的时候，江泰需要确保该生日和身份证上的生日是一致的。但是从这获取的生日是顾客自己填写的
						//所以当顾客提交的身份证件类型是身份证的时候，直接采用身份证的生日提取。	
						info.setIdentifyNumber(tmp.getCertificateNumber());
						info.setIdentifyType(tmp.getCertificate());
						info.setInsuredName(tmp.getName());
						//此处规定"0"为女性，"1"为男性
						if(tmp.getSex() == 0){
							info.setSex("F");
						}else{
							info.setSex("M");
						}
						if(tmp.getCertificate() == 1){
							//如果是身份证，则直接提取生日
							info.setBirthDay(JiangtaiUtil.getBirthdayFromCertificate(tmp.getCertificateNumber()));
						}else{
							info.setBirthDay(DateUtil.getBirthday(tmp.getBirthday()));
						}			
						insuredList.add(info);	
					}
					//组装confirmMessage
					ConfirmMessage cmo  = new ConfirmMessage();
					//confirmMessage中需要手动填写的参数有以下几个
					HyOrder order = hyOrderService.find(orderId);
					HyGroup group = hyGroupService.find(order.getGroupId());
					//cmo.setPayType(payType);
					//必须确保我们的日期格式是yyyy-MM-dd HH:mm:ss
					//系统的所有时间格式都是yyyy-MM-dd，我们设置当天的凌晨开始保险
					cmo.setStartDate(DateUtil.getSimpleStartOfDayDate(insuranceOrder.getInsuranceStarttime()));
					cmo.setEndDate(DateUtil.getSimpleEndOfDayDate(insuranceOrder.getInsuranceEndtime()));
					cmo.setContactName(order.getContact());
					cmo.setContactPhone(order.getPhone());
					cmo.setTravelRoute(group.getGroupLineName());
					cmo.setSumQuantity(1);
					//设置旅游团编号为虹宇的订单编号
					cmo.setTravelGroupNo(order.getOrderNumber());
					//此处获取到渠道交易的时间，因为该时间取得是当前时间，所以必须保存，在投保成功之后写入数据库
					String channelTradeDate = DateUtil.getSimpleDate(new Date());
					cmo.setChannelTradeDate(channelTradeDate);
					//获取投保产品号
					Insurance  insurance = insuranceService.find(insuranceOrder.getInsuranceId());
					cmo.setProductCode(insurance.getInsuranceCode());
					//渠道交易流水号--直接设置"前缀+订单编号+当前时间MMddHHmmss"
					//cmo.setChannelTradeSerialNo(order.getOrderNumber());
					String channelTradeSerialNo = "";
					if(insuranceOrder.getType() == 0){
						//如果是团期投保，流水号前缀为"GROUP" 意为"团期"
						channelTradeSerialNo = "GROUP"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
						//cmo.setChannelTradeSerialNo("TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
					}
					if(insuranceOrder.getType() == 1){
						//如果是门店个人自主投保，流水号前缀"SELF" 意为“自主”
						channelTradeSerialNo = "SELF"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
						//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
					}
					if(insuranceOrder.getType() == 2){
						//如果是门店个人自主投保，流水号前缀"ONLINE" 意为“网上”
						channelTradeSerialNo = "ONLINE"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
						//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
					}
					cmo.setChannelTradeSerialNo(channelTradeSerialNo);
					cmo.setInsuredList(insuredList);		
				
					Json json = JiangtaiUtil.order(cmo);	
					JtOrderResponse jtOrderResponse = (JtOrderResponse) XStreamUtil.xmlToBean(json.getObj().toString());
					//System.out.println("实体获取到了返回信息："+jtOrderResponse.toString());
					if("000001".equals(jtOrderResponse.getResponseCode())){
						 //投保成功，更新insuranceOrder表
						//此设置保单的下载地址，因为保单的对应的是订单，所以保单地址设置在订单数据表中
						HyOrder hyOrder = hyOrderService.find(orderId);
						hyOrder.setInsuranceOrderDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
						insuranceOrder.setDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
						//8.9日下午四点修改，将save改成update;
						hyOrderService.update(hyOrder);
						insuranceOrder.setStatus(3);
						insuranceOrder.setInsuredTime(new Date());
						insuranceOrder.setJtChannelTradeSerialNo(jtOrderResponse.getChannelTradeSerialNo());
						insuranceOrder.setJtChannelTradeDate(channelTradeDate);
						insuranceOrder.setJtOrderNo(jtOrderResponse.getJtOrderNo());
						insuranceOrder.setJtInsureNo(jtOrderResponse.getInsureNo());
						insuranceOrder.setJtPolicyNo(jtOrderResponse.getPolicyNo());
						insuranceOrder.setJtSumPremium(jtOrderResponse.getSumPremium());
						insuranceOrder.setJtDiscount(jtOrderResponse.getDiscount());
						for(HyPolicyHolderInfo tmp : insuranceOrder.getPolicyHolders()){
							//此处设置个人保险凭证的下载地址
							String downloadUrl = JiangtaiUtil.OrderDownCertificate(insuranceOrder.getJtChannelTradeSerialNo(),tmp.getCertificateNumber()); 
							tmp.setDownloadUrl(downloadUrl);
							hyPolicyHolderInfoService.update(tmp);							
						}
						insuranceOrderService.update(insuranceOrder);
					} else{
						//投保失败，将保单状态改成投保失败
						insuranceOrder.setStatus(5);
						insuranceOrderService.update(insuranceOrder);
						//System.out.println("投保失败:"+orderId);
					}
				}
		} catch (ParseException e) {
			e.printStackTrace();
		}				
	}
	
//	@Override
//	public void process() {
//		// TODO Auto-generated method stub
//		//实现定期投保到江泰，在当天的凌晨将当天发团的订单投保到江泰
//		try {
//		List<Filter> filters = new ArrayList<>();
//		//筛选出当天发团的，且状态为待投保的所有保单。
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		String today = sdf.format(DateUtil.getDateAfterSpecifiedDaysFormat(new Date(), 3));
//		Date startDate = sdf.parse(today);
//		System.out.println(startDate);
////		InsuranceOrder test = insuranceOrderService.find((long) 1);
////		System.out.println(test.getInsuranceStarttime());
//		
//		filters.add(Filter.eq("insuranceStarttime",startDate));
//		filters.add(Filter.eq("status", 1));
//		List<Order> orders = new ArrayList<>();
//		orders.add(Order.asc("orderId"));
//		List<InsuranceOrder> insuranceOrders1 = insuranceOrderService.findList(null,filters,orders);
//		List<Long> orderIds = new ArrayList<>();
//		for (int i = 0; i < insuranceOrders1.size(); i++) {
//			if(i==0){
//				orderIds.add(insuranceOrders1.get(i).getOrderId());
//				continue;
//			}
//			if(insuranceOrders1.get(i).getOrderId() != insuranceOrders1.get(i-1).getOrderId()){
//				orderIds.add(insuranceOrders1.get(i).getOrderId());
//			}
//		} 
//		if(orderIds.isEmpty()){
//			return;
//		}
//		System.out.println(orderIds);
//		Json json = new Json();
//		
//			Map<String,Object> map = new HashMap<>();
//			List<Long> successIds = new ArrayList<>();
//			List<Long> failIds = new ArrayList<>();
//			for(int i = 0; i<orderIds.size(); i++ ){
//				//获取想要投保订单id
//				Long orderId = orderIds.get(i);
//				
//				//根据该订单id去获取所有的保险人对应的保单
//				List<Filter> filters1 = new ArrayList<>();
//				filters1.add(Filter.eq("orderId", orderId));
//				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters1,null);
//				//如果已经投保，直接跳过。
//				if(insuranceOrders.get(0).getStatus() == 3){
//					continue;
//				}
//				//System.out.println("length = "+insuranceOrders.size());
//				List<InsureInfo> insuredList = new ArrayList<>();
//				for(InsuranceOrder tmp : insuranceOrders){
//					InsureInfo info = new InsureInfo();
//					//在设置生日的时候，江泰需要确保该生日和身份证上的生日是一致的。但是从这获取的生日是顾客自己填写的
//					//所以当顾客提交的身份证件类型是身份证的时候，直接采用身份证的生日提取。	
//					info.setIdentifyNumber(tmp.getCertificateNumber());
//					info.setIdentifyType(tmp.getCertificate());
//					info.setInsuredName(tmp.getName());
//					//此处规定"0"为女性，"1"为男性
//					if(tmp.getSex() == 0){
//						info.setSex("F");
//					}else{
//						info.setSex("M");
//					}
//					if(tmp.getCertificate() == 1){
//						//如果是身份证，则直接提取生日
//						info.setBirthDay(JiangtaiUtil.getBirthdayFromCertificate(tmp.getCertificateNumber()));
//					}else{
//						info.setBirthDay(DateUtil.getBirthday(tmp.getBirthday()));
//					}			
//					insuredList.add(info);					
//				}
//				//组装confirmMessage
//				ConfirmMessage cmo  = new ConfirmMessage();
//				//confirmMessage中需要手动填写的参数有以下几个
//				HyOrder order = hyOrderService.find(orderId);
//				//这里面显示的insuranceOrders.get(0)是为了拿到符合条件的第一条数据，取得的时间和团id都是公共相同的。
//				HyGroup group = hyGroupService.find(order.getGroupId());
//				//cmo.setPayType(payType);
//				//必须确保我们的日期格式是yyyy-MM-dd HH:mm:ss
//				//系统的所有时间格式都是yyyy-MM-dd，我们设置当天的凌晨开始保险
//				cmo.setStartDate(DateUtil.getSimpleDate(insuranceOrders.get(0).getInsuranceStarttime()));
//				cmo.setEndDate(DateUtil.getSimpleDate(insuranceOrders.get(0).getInsuranceEndtime()));
//				cmo.setContactName(order.getContact());
//				cmo.setContactPhone(order.getPhone());
//				cmo.setTravelRoute(group.getGroupLineName());
//				cmo.setSumQuantity(1);
//				//设置旅游团编号为虹宇的订单编号
//				cmo.setTravelGroupNo(order.getOrderNumber());
//				//此处获取到渠道交易的时间，因为该时间取得是当前时间，所以必须保存，在投保成功之后写入数据库
//				String channelTradeDate = DateUtil.getSimpleDate(new Date());
//				cmo.setChannelTradeDate(channelTradeDate);
//				//获取投保产品号
//				Insurance  insurance = insuranceService.find(insuranceOrders.get(0).getInsuranceId());
//				cmo.setProductCode(insurance.getInsuranceCode());
//				//渠道交易流水号--直接设置"前缀+订单编号+当前时间MMddHHmmss"
//				//cmo.setChannelTradeSerialNo(order.getOrderNumber());
//				String channelTradeSerialNo = "";
//				if(insuranceOrders.get(0).getType() == 0){
//					//如果是团期投保，流水号前缀为"TQ" 意为"团期"
//					channelTradeSerialNo = "TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
//					//cmo.setChannelTradeSerialNo("TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
//				}
//				if(insuranceOrders.get(0).getType() == 1){
//					//如果是门店个人自主投保，流水号前缀"ZZ" 意为“自主”
//					channelTradeSerialNo = "ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
//					//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
//				}
//				cmo.setChannelTradeSerialNo(channelTradeSerialNo);
//				cmo.setInsuredList(insuredList);
//				json = JiangtaiUtil.order(cmo);	
//				JtOrderResponse jtOrderResponse = (JtOrderResponse) XStreamUtil.xmlToBean(json.getObj().toString());
//				//System.out.println("实体获取到了返回信息："+jtOrderResponse.toString());
//				if("000001".equals(jtOrderResponse.getResponseCode())){
//					 //投保成功，更新insuranceOrder表
//					//此设置保单的下载地址，因为保单的对应的是订单，所以保单地址设置在订单数据表中
//					HyOrder hyOrder = hyOrderService.find(orderId);
//					hyOrder.setInsuranceOrderDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
//					hyOrderService.update(hyOrder);
//					for(InsuranceOrder tmp : insuranceOrders){
//						//设置投保状态为  已投保。
//						tmp.setStatus(3);
//						tmp.setJtChannelTradeSerialNo(jtOrderResponse.getChannelTradeSerialNo());
//						tmp.setJtChannelTradeDate(channelTradeDate);
//						tmp.setJtOrderNo(jtOrderResponse.getJtOrderNo());
//						tmp.setJtInsureNo(jtOrderResponse.getInsureNo());
//						tmp.setJtSumPremium(jtOrderResponse.getSumPremium());
//						tmp.setJtDiscount(jtOrderResponse.getDiscount());
//						//此处设置个人保险凭证的下载地址
//						String downloadUrl = JiangtaiUtil.OrderDownCertificate(tmp); 
//						tmp.setDownloadUrl(downloadUrl);
//						
//						insuranceOrderService.update(tmp);	
//						
//					}
//					successIds.add(orderId);
//					//封装数据返回给前台，将投保成功的订单id放到返回列表中。
//					
//				} else{
//					failIds.add(orderId);
//				}
//			}
//			System.out.println("成功订单id："+successIds);
//			System.out.println("失败订单id："+failIds);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
