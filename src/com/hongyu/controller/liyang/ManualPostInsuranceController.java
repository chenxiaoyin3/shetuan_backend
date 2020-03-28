package com.hongyu.controller.liyang;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
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
import com.hongyu.util.DateUtil;
import com.hongyu.util.JiangtaiUtil;
import com.hongyu.util.XStreamUtil;
/**
 * 自动投保失败之后，手动投保
 * @author liyang
 *
 */
@Controller
@RequestMapping("admin/manual/")
public class ManualPostInsuranceController {
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
	
	@RequestMapping("/post")
	@ResponseBody
	public void process(){
		try {
			List<Long> hyOrderIds = getOrderList();
			List<Filter> filters = new ArrayList<>();
	
			for(int i = 0; i<hyOrderIds.size(); i++ ){
				System.out.println("订单id为"+hyOrderIds.get(i));
				filters.clear();
				filters.add(Filter.eq("type", 0));
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("orderId", hyOrderIds.get(i)));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,null);
				//System.out.println("对应的保险单有 "+insuranceOrders.size());
				if(insuranceOrders==null || insuranceOrders.size()!=1 )
					continue;
				InsuranceOrder insuranceOrder = insuranceOrders.get(0);
				//System.out.println("保单id为"+insuranceOrder.getId());
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
				//设置为当前之前之后的两小时
				cmo.setStartDate(DateUtil.getSimpleDate(insuranceOrder.getInsuranceStarttime()));
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
//					 投保成功，更新insuranceOrder表
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
					System.out.println("投保成功:"+orderId);
				} else{
					//投保失败，将保单状态改成投保失败
					insuranceOrder.setStatus(5);
					insuranceOrderService.update(insuranceOrder);
					System.out.println("投保失败:"+orderId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
	/**
	 * 获取第二天发团的所有线路订单的id
	 * @return
	 * @throws Exception 
	 */
	private List<Long> getOrderList() throws Exception{
		List<Long> hyOrderIds = new ArrayList<>();
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.ge("id", 803));
		List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,null);
		Date date = new Date();
		for(InsuranceOrder tmp:insuranceOrders){
			hyOrderIds.add(tmp.getOrderId());
			tmp.setInsuranceStarttime(DateUtil.getDateAfterTwoHours(date));
			insuranceOrderService.update(tmp);
		}
		System.out.println("订单id为:"+hyOrderIds);
		return hyOrderIds;
	}
}
