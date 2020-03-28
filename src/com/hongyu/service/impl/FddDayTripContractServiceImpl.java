package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddContractTemplate;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.FddStoreCA;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineTravels;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.MhGroupPrice;
import com.hongyu.entity.MhLine;
import com.hongyu.entity.MhLineTravels;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.TransportEntity;
import com.hongyu.service.FddContractService;
import com.hongyu.service.FddContractTemplateService;
import com.hongyu.service.FddDayTripContractService;
import com.hongyu.service.FddStoreCAService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.MhGroupPriceService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.util.DateUtil;
import com.hongyu.util.HttpReqUtil;
import com.hongyu.util.contract.AutoSignRequest;
import com.hongyu.util.contract.CARequestEntity;
import com.hongyu.util.contract.ContractInfoAboutHy;
import com.hongyu.util.contract.FddContractUtil;
import com.hongyu.util.contract.SignRequest;
import com.hongyu.util.liyang.CopyFromNotNullBeanUtilsBean;
@Service("fddDayTripContractServiceImpl")
@Transactional
public class FddDayTripContractServiceImpl extends BaseServiceImpl<FddDayTripContract, Long> implements FddDayTripContractService{	

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name = "fddContractTemplateServiceImpl")
	FddContractTemplateService fddContractTemplateService;
	
	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	@Resource(name = "fddStoreCAServiceImpl")
	FddStoreCAService fddStoreCAService;
	
	@Resource(name = "mhGroupPriceServiceImpl")
	MhGroupPriceService mhGroupPriceService;
	
	@Override
	@Resource(name="fddDayTripContractDaoImpl")
	public void setBaseDao(BaseDao<FddDayTripContract, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	@Override
	public HashMap<String, Object> getDynamicTables(HyOrder order) throws Exception {
		// TODO Auto-generated method stub
				//总共四个附件，也就是四个动态表格
				HashMap<String,Object> result = new HashMap<>();
				/****游客名单表****/
				List<HashMap<String, Object>> orderCustomers = new ArrayList<>();
				List<HyOrderItem> orderItems = order.getOrderItems();
				for(HyOrderItem item : orderItems){
					//订单条目类型是线路
					if(item.getNumberOfReturn()!=null && item.getNumber().equals(item.getNumberOfReturn()))
						continue;
					if(item.getType()==1){
						List<HyOrderCustomer> customers = item.getHyOrderCustomers();
						for(HyOrderCustomer customer:customers){
							HashMap<String, Object> hm = new HashMap<>();
							hm.put("name", customer.getName());
							hm.put("certificate", customer.getCertificate());
							hm.put("gender", customer.getGender()==0?"女":"男");
							hm.put("national", "");
							hm.put("ifChildren", customer.getType()==1?"是":"否");
							hm.put("phone", customer.getPhone());
							hm.put("health","");
							orderCustomers.add(hm);
						}
					}
				}
				result.put("orderCustomers", orderCustomers);
				/****线路行程单****/
				List<HashMap<String, Object>> lineTravels = new ArrayList<>();
				HyGroup hyGroup = hyGroupService.find(order.getGroupId());
				HyLine hyLine = hyGroup.getLine();
				List<HyLineTravels> travels = hyLine.getLineTravels();
				for(HyLineTravels tmp:travels){
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("linePn", hyLine.getPn());
					hm.put("lineName", hyLine.getName());
					hm.put("startTime", hyGroup.getStartDay());
					hm.put("traffic", tmp.getTransport().getName());
					hm.put("route", tmp.getRoute());
					hm.put("restaurant", tmp.getRestaurant());
					hm.put("isBreakfast", tmp.getIsBreakfast()?"是":"否");		
					hm.put("isLunch", tmp.getIsLunch()?"是":"否");
					hm.put("isDinner", tmp.getIsDinner()?"是":"否");
					lineTravels.add(hm);
				}
				result.put("lineTravels", lineTravels);
				/****自愿购物协议****/
				List<HashMap<String, Object>> shoppings = new ArrayList<>();
				result.put("shoppings",shoppings);
				/****自愿付费旅游项目协议****/
				List<HashMap<String, Object>> payItems = new ArrayList<>();
				result.put("payItems",payItems);
				return result;
	}
	/**
	 * 获取官网的下单线路动态表单
	 */
	@Override
	public HashMap<String, Object> getMhDynamicTables(HyOrder order) throws Exception {
		// TODO Auto-generated method stub
				//总共四个附件，也就是四个动态表格
				HashMap<String,Object> result = new HashMap<>();
				/****游客名单表****/
				List<HashMap<String, Object>> orderCustomers = new ArrayList<>();
				List<HyOrderItem> orderItems = order.getOrderItems();
				for(HyOrderItem item : orderItems){
					//订单条目类型是线路
					if(item.getNumberOfReturn()!=null && item.getNumber().equals(item.getNumberOfReturn()))
						continue;
					if(item.getType()==1){
						List<HyOrderCustomer> customers = item.getHyOrderCustomers();
						for(HyOrderCustomer customer:customers){
							HashMap<String, Object> hm = new HashMap<>();
							hm.put("name", customer.getName());
							hm.put("certificate", customer.getCertificate());
							hm.put("gender", customer.getGender()==0?"女":"男");
							hm.put("national", "");
							hm.put("ifChildren", customer.getType()==1?"是":"否");
							hm.put("phone", customer.getPhone());
							hm.put("health","");
							orderCustomers.add(hm);
						}
					}
				}
				result.put("orderCustomers", orderCustomers);
				/****线路行程单****/
				List<HashMap<String, Object>> lineTravels = new ArrayList<>();
				HyGroup hyGroup = hyGroupService.find(order.getGroupId());
				HyLine hyLine = hyGroup.getLine();
				MhLine mhLine = hyLine.getMhLine();
				if(mhLine==null)
					throw new Exception("该线路没有被完善，无法在官网展示");
				List<MhLineTravels> travels = mhLine.getMhLineTravels();
				for(MhLineTravels tmp:travels){
					HashMap<String, Object> hm = new HashMap<>();
					hm.put("linePn", hyLine.getPn());
					hm.put("lineName", hyLine.getName());
					hm.put("startTime", hyGroup.getStartDay());
					hm.put("traffic", tmp.getTransport().getName());
					hm.put("route", tmp.getRoute());
					hm.put("restaurant", tmp.getRestaurant());
					hm.put("isBreakfast", tmp.getIsBreakfast()?"是":"否");		
					hm.put("isLunch", tmp.getIsLunch()?"是":"否");
					hm.put("isDinner", tmp.getIsDinner()?"是":"否");
					lineTravels.add(hm);
				}
				result.put("lineTravels", lineTravels);
				/****自愿购物协议****/
				List<HashMap<String, Object>> shoppings = new ArrayList<>();
				result.put("shoppings",shoppings);
				/****自愿付费旅游项目协议****/
				List<HashMap<String, Object>> payItems = new ArrayList<>();
				result.put("payItems",payItems);
				return result;
	}
	
	@Override
	public HashMap<String, Object> fillIn(Long orderId,HttpSession session) throws Exception {
		/**判断当前的联系人有没有退款，退款的话就把订单的联系人改成剩下的第一个游客*/
		updateContact(orderId);
		//返回fddContract实体和表单内容
		HashMap<String,Object> result = new HashMap<>();
		HyOrder hyOrder = hyOrderService.find(orderId);
		result.put("tables", this.getDynamicTables(hyOrder));
		System.out.println(hyOrder.getOrderNumber());
		if(hyOrder.getContractId() == null){
			//如果该订单没有之前没有创建过合同，就创建一个新的合同。
			FddDayTripContract fddDayTripContract = new FddDayTripContract();	
			//生成合同号，前缀“DT”+orderId
			fddDayTripContract.setContractId("DX"+hyOrder.getOrderNumber());
			//保存订单id
			fddDayTripContract.setOrderId(hyOrder.getId());
			//签署人
			fddDayTripContract.setCustomer(hyOrder.getContact());		
			//签署人号码
			fddDayTripContract.setCustomerPhone(hyOrder.getPhone());
			//签署人证件号
			fddDayTripContract.setCustomerIDNum(hyOrder.getContactIdNumber());
			//团里人数
			fddDayTripContract.setCustomerNum(hyOrder.getPeople());
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			fddDayTripContract.setHyOperator(admin.getName());
//			Department department = admin.getDepartment();
//			if(department.getStore()!=null){
//				Store store = department.getStore();
//				fddDayTripContract.setHyAddress(store.getAddress());
//				fddDayTripContract.setHyId(store.getUniqueCode());
//				fddDayTripContract.setHyName(store.getStoreName());
//				fddDayTripContract.setHyPhone(store.getHyAdmin().getMobile());
//			}
			//除保险外的订单结算价
			fddDayTripContract.setTotalPrice(hyOrder.getJiesuanMoney1());
			
			HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
			//获取线路信息
			HyLine line = hyGroup.getLine();
			HyLineTravels travels = line.getLineTravels().get(0);
			
			fddDayTripContract.setLineInfo(travels.getRoute());
			TransportEntity transportEntity = travels.getTransport();
			/**设置交通信息*/
			fddDayTripContract.setTrafficType(0);
			fddDayTripContract.setTrafficStandard(transportEntity.getName());	
			/**设置团费包含内容*/
			fddDayTripContract.setFeeNote(line.getMemo());
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", hyOrder.getId()));
			List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
			for(PayandrefundRecord tmp : payandrefundRecords){
				if(tmp.getType() == 0 && tmp.getStatus()== 1){
					//支付方式
					fddDayTripContract.setPaymentType(tmp.getPayMethod());
					//支付时间
					fddDayTripContract.setPaymentTime(tmp.getCreatetime());
					break;
				}
			}
			//此处填写票价，现在这个里面可以拿到priceId和productId，但是不知道去哪个表查
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			int adultNumber = 0;
			int childrenNumber = 0;
			Integer source = hyOrder.getSource();
			for(HyOrderItem tmp : hyOrderItems){
				//筛选出成人和儿童，然后获取票价
				/**当订单条目类型type为1线路
				 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
				 */
				
				if(tmp.getType() == 1 && tmp.getPriceType()==0){
					if(source == 0){
						//获取成人价
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						fddDayTripContract.setAdultTicketPrice(hyGroupPrice.getAdultPrice1());
					}else{
						MhGroupPrice mhGroupPrice = mhGroupPriceService.find(tmp.getPriceId());
						fddDayTripContract.setAdultTicketPrice(mhGroupPrice.getMhAdultWaimaiPrice());
					}
					int re = tmp.getNumberOfReturn()==null?0:tmp.getNumberOfReturn();
					adultNumber += (tmp.getNumber()-re);
					continue;
				}
				if(tmp.getType() == 1 && tmp.getPriceType()==1){
					if(source == 0){
						//获取儿童价
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						fddDayTripContract.setChildrenTicketPrice(hyGroupPrice.getChildrenPrice1());
					}else{
						MhGroupPrice mhGroupPrice = mhGroupPriceService.find(tmp.getPriceId());
						fddDayTripContract.setChildrenTicketPrice(mhGroupPrice.getMhChildrenWaimaiPrice());
					}
					int re = tmp.getNumberOfReturn()==null?0:tmp.getNumberOfReturn();
					childrenNumber += (tmp.getNumber()-re);
					continue;
				}
			}
			/*设置官网订单总价格是jiusuanmoney*/
			fddDayTripContract.setAdultNum(adultNumber);
			fddDayTripContract.setChildrenNum(childrenNumber);
			fddDayTripContract.setTotalPrice(hyOrder.getWaimaiMoney());
			fddDayTripContract.setHyOperator(ContractInfoAboutHy.hyWLXSBOperator);
			fddDayTripContract.setHyAddress(ContractInfoAboutHy.hyWLXSBAddress);
			fddDayTripContract.setHyName(ContractInfoAboutHy.hyName);
			fddDayTripContract.setHyPhone(ContractInfoAboutHy.hyPhone);
			fddDayTripContract.setHyId(ContractInfoAboutHy.hyId);
			result.put("fddDayTripContract", fddDayTripContract);
			result.put("msg", "获取成功(第一次创建合同)");
		}else{
			//如果已有合同号，那就属于重新签署，需要重新设置合同号
			FddDayTripContract fddDayTripContract = this.find(hyOrder.getContractId());
			//将原有合同设置为已取消状态
			fddDayTripContract.setCancelDate(new Date());
			fddDayTripContract.setStatus(5);
			this.update(fddDayTripContract);
			FddDayTripContract tmp = new FddDayTripContract();
			new CopyFromNotNullBeanUtilsBean().copyProperties(tmp, fddDayTripContract);
			tmp.setId(null);
			//设置新的合同号为之前的合同号后边添加字母“R+重签的次数”
			String oldContractId = tmp.getContractId();
			String[] ss = oldContractId.split("[R]");
			if(ss.length>1){
				//说明这是在重新签署之后的再次重签,次数加一
				Integer rNum = Integer.valueOf(ss[1])+1;
				tmp.setContractId(ss[0]+"R"+rNum);
			}else{
				//第一次重签
				tmp.setContractId(ss[0]+"R"+1);
			}
			result.put("fddDayTripContract", tmp);
			result.put("msg", "获取成功(已有合同，从数据库获取)");		
		}
		return result;
	}

	@Override
	public HashMap<String, Object> submit(FddDayTripContract fddDayTripContract) throws Exception {
		HashMap<String, Object> hm = new HashMap<>();
		System.out.println("进入submit");
		fddDayTripContract.setId(null);
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		//在这为签署人申请CA证书号码
		CARequestEntity caRequestEntity = new CARequestEntity();
		caRequestEntity.setIdCard(fddDayTripContract.getCustomerIDNum());
		caRequestEntity.setName(fddDayTripContract.getCustomer());
		caRequestEntity.setPhone(fddDayTripContract.getCustomerPhone());
		String caNum = fddContractUtil.getCustomerCAId(caRequestEntity);
		if(caNum!=null){
			System.out.println("客户CA申请成功："+caNum);
		}else{
			throw new Exception("客户CA申请失败，身份证号码不正确");
		}
		fddDayTripContract.setCustomerCANum(caNum);
		//设置合同的签署时间
		fddDayTripContract.setHySignTime(new Date());
		fddDayTripContract.setCustomerSignTime(new Date());
		//将合同信息存入数据库
		this.save(fddDayTripContract);
		//System.out.println("合同存储成功");
		//请求法大大生成该合同
		List<Filter> filters11 = new ArrayList<>();
		filters11.add(Filter.eq("type", 0));
		FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
		//System.out.println("模板id是"+template.getTemplateId());
		HyOrder dingdan = hyOrderService.find(fddDayTripContract.getOrderId());
		HashMap<String, Object> tables = this.getDynamicTables(dingdan);
		Json gcResult = fddContractUtil.generateDayTripContract(fddDayTripContract,template,tables);
		if(gcResult.isSuccess()){
			//生成合同成功
			//System.out.println("生成合同成功");
			//将合同id存入订单中,必要时还要添加合同编号，但是合同id应该好用一些。
			HyOrder order = hyOrderService.find(fddDayTripContract.getOrderId());
			order.setContractId(fddDayTripContract.getId());
			order.setContractNumber(fddDayTripContract.getContractId());
			hyOrderService.update(order);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("contractId", fddDayTripContract.getContractId()));
			List<FddDayTripContract> fddDayTripContracts = this.findList(null,filters,null);
			//设置合同状态为生成合同成功以及设置相应的下载和查看地址
			FddDayTripContract fc = fddDayTripContracts.get(0);
			Long currId = fc.getId();
			fc.setStatus(2);
			HashMap<String, Object> hashMap = (HashMap<String, Object>)gcResult.getObj();
			
			fc.setDownloadUrl((String)hashMap.get("download_url"));
			fc.setViewpdfUrl((String)hashMap.get("viewpdf_url"));
			//初始化虹宇自动签章的交易号
			fc.setHyTransactionId("HYSIGN"+DateUtil.getfileDate(new Date()));
			//初始化客户手动签章的交易号
			fc.setCustomerTransactionId("CTSIGN"+DateUtil.getfileDate(new Date()));
			
			this.update(fc);
			FddDayTripContract newfdtc = this.find(currId);
			//虹宇自动签章
			AutoSignRequest autoSignRequest = new AutoSignRequest();
			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.eq("storeId",order.getStoreId()));
			List<FddStoreCA> fddStoreCAs = fddStoreCAService.findList(null,filters2,null);
			/*如果查到了门店CA，就使用门店的章，否则使用默认的虹宇的章 2019.05.27   liyang*/
			if(!fddStoreCAs.isEmpty()){
				FddStoreCA ca = fddStoreCAs.get(0);
				if(ca.getStoreCA()!=null && !ca.getStoreCA().equals("")){
					autoSignRequest.setCustomerId(ca.getStoreCA());
				}
			}
			autoSignRequest.setPositionType(1);
			autoSignRequest.setTransactionId(newfdtc.getHyTransactionId());
			autoSignRequest.setContractId(newfdtc.getContractId());
			JSONArray signaturePositions = new JSONArray();
			JSONObject position = new JSONObject();
			position.put("pagenum", 1);
			position.put("x", 577.7);
			position.put("y", 656.1);
			signaturePositions.add(position);
			autoSignRequest.setSignaturePositions(signaturePositions.toJSONString());
			autoSignRequest.setDocTitle("DX"+newfdtc.getContractId());
			Json asc = fddContractUtil.autoSignDayTripContract(autoSignRequest);
			if(asc.isSuccess()){
				hm.put("orderId", newfdtc.getOrderId());
				hm.put("id", newfdtc.getId());
				hm.put("contractId", newfdtc.getContractId());
				hm.put("msg", "虹宇自动签章成功");
				
			}else{
				hm.put("msg", "虹宇自动签章失败"+asc.getMsg());
			}
		}else{
			hm.put("msg", "法大大生成合同失败");	
		}	
		return hm;
	}

	@Override
	public String extCustomerSign(Long id) throws Exception {
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		FddDayTripContract fddDayTripContract = this.find(id);
		//System.out.println("虹宇自动签章成功,现在进入个人签章");
		SignRequest signRequest = new SignRequest();
		//System.out.println(fddDayTripContract.getContractId());
		signRequest.setContractId(fddDayTripContract.getContractId());
		signRequest.setCustomerId(fddDayTripContract.getCustomerCANum());

		signRequest.setDocTitle("DX"+fddDayTripContract.getContractId());
		signRequest.setTransactionId(fddDayTripContract.getCustomerTransactionId());
		
		/*指定客户签章坐标位置*/
//		JSONArray signaturePositions = new JSONArray();
//		JSONObject position = new JSONObject();
//		position.put("pagenum", 1);
//		position.put("x", 225.882);
//		position.put("y", 661.427);
//		signaturePositions.add(position);
//		
//		signRequest.setPositionType(1);
//		signRequest.setSignaturePositions(signaturePositions.toJSONString());
		
		String sign_url = fddContractUtil.pushDoc_extSign(signRequest);
		//将客户签约的地址,拿到返回结果
		String result = HttpReqUtil.HttpsDefaultExecute("POST", sign_url, null, null, "UTF-8");
		//System.out.println(result);
	
		return result;
	}

	@Override
	public HashMap<String, Object> fillInForMh(Long orderId) throws Exception {
		//返回fddContract实体和表单内容
				HashMap<String,Object> result = new HashMap<>();
				HyOrder hyOrder = hyOrderService.find(orderId);
				result.put("tables", this.getMhDynamicTables(hyOrder));
				System.out.println(hyOrder.getOrderNumber());
				if(hyOrder.getContractId() == null){
					//如果该订单没有之前没有创建过合同，就创建一个新的合同。
					FddDayTripContract fddDayTripContract = new FddDayTripContract();	
					//生成合同号，前缀“DT”+orderId
					fddDayTripContract.setContractId("DX"+hyOrder.getOrderNumber());
					//保存订单id
					fddDayTripContract.setOrderId(hyOrder.getId());
					//签署人
					fddDayTripContract.setCustomer(hyOrder.getContact());		
					//签署人号码
					fddDayTripContract.setCustomerPhone(hyOrder.getPhone());
					//签署人证件号
					fddDayTripContract.setCustomerIDNum(hyOrder.getContactIdNumber());
					//团里人数
					fddDayTripContract.setCustomerNum(hyOrder.getPeople());
					
					//除保险外的订单结算价
					fddDayTripContract.setTotalPrice(hyOrder.getJiesuanMoney1());
					
					
					HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
					//获取线路信息
					MhLine line = hyGroup.getLine().getMhLine();
					fddDayTripContract.setLineInfo(line.getBriefDescription());
					MhLineTravels travels = line.getMhLineTravels().get(0);
					
					fddDayTripContract.setLineInfo(travels.getRoute());
					TransportEntity transportEntity = travels.getTransport();
					/**设置交通信息*/
					fddDayTripContract.setTrafficType(0);
					fddDayTripContract.setTrafficStandard(transportEntity.getName());	
					/**设置团费包含内容*/
					fddDayTripContract.setFeeNote(line.getFeeDescription());
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("orderId", hyOrder.getId()));
					List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
					for(PayandrefundRecord tmp : payandrefundRecords){
						if(tmp.getType() == 0 && tmp.getStatus()== 1){
							//支付方式
							fddDayTripContract.setPaymentType(tmp.getPayMethod());
							//支付时间
							fddDayTripContract.setPaymentTime(tmp.getCreatetime());
							break;
						}
					}
					List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
					int adultNumber = 0;
					int childrenNumber = 0;
					for(HyOrderItem tmp : hyOrderItems){
						//筛选出成人和儿童，然后获取票价
						/**当订单条目类型type为1线路
						 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
						 */
						if(tmp.getType() == 1 && tmp.getPriceType()==0){
							//获取成人价
							HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
							MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
							if(mhGroupPrice==null)
								throw new Exception("没有官网成人价");
							fddDayTripContract.setAdultTicketPrice(mhGroupPrice.getMhAdultSalePrice());
							int re = tmp.getNumberOfReturn()==null?0:tmp.getNumberOfReturn();
							adultNumber += (tmp.getNumber()-re);
							continue;
						}
						if(tmp.getType() == 1 && tmp.getPriceType()==1){
							//获取儿童价
							HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
							MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
							if(mhGroupPrice==null)
								throw new Exception("没有官网儿童价");
							fddDayTripContract.setChildrenTicketPrice(mhGroupPrice.getMhChildrenSalePrice());
							int re = tmp.getNumberOfReturn()==null?0:tmp.getNumberOfReturn();
							childrenNumber += (tmp.getNumber()-re);
							continue;
						}
					}
					fddDayTripContract.setAdultNum(adultNumber);
					fddDayTripContract.setChildrenNum(childrenNumber);
					fddDayTripContract.setHyOperator(ContractInfoAboutHy.hyWLXSBOperator);
					fddDayTripContract.setHyAddress(ContractInfoAboutHy.hyWLXSBAddress);
					fddDayTripContract.setHyName(ContractInfoAboutHy.hyName);
					fddDayTripContract.setHyId(ContractInfoAboutHy.hyId);
					fddDayTripContract.setHyPhone(ContractInfoAboutHy.hyPhone);
					
					result.put("fddDayTripContract", fddDayTripContract);
					result.put("msg", "获取成功(第一次创建合同)");
				}else{
					//如果已有合同号，那就属于重新签署，需要重新设置合同号
					FddDayTripContract fddDayTripContract = this.find(hyOrder.getContractId());
					//将原有合同设置为已取消状态
					fddDayTripContract.setCancelDate(new Date());
					fddDayTripContract.setStatus(5);
					this.update(fddDayTripContract);
					FddDayTripContract tmp = new FddDayTripContract();
					new CopyFromNotNullBeanUtilsBean().copyProperties(tmp, fddDayTripContract);
					tmp.setId(null);
					//设置新的合同号为之前的合同号后边添加字母“R+重签的次数”
					String oldContractId = tmp.getContractId();
					String[] ss = oldContractId.split("[R]");
					if(ss.length>1){
						//说明这是在重新签署之后的再次重签,次数加一
						Integer rNum = Integer.valueOf(ss[1])+1;
						tmp.setContractId(ss[0]+"R"+rNum);
					}else{
						//第一次重签
						tmp.setContractId(ss[0]+"R"+1);
					}
					result.put("fddDayTripContract", tmp);
					result.put("msg", "获取成功(已有合同，从数据库获取)");		
				}
				return result;
	}

	@Override
	public HashMap<String, Object> submitForMh(FddDayTripContract fddDayTripContract) throws Exception {
		HashMap<String, Object> hm = new HashMap<>();
		System.out.println("进入submit");
		fddDayTripContract.setId(null);
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		//在这为签署人申请CA证书号码
		CARequestEntity caRequestEntity = new CARequestEntity();
		caRequestEntity.setIdCard(fddDayTripContract.getCustomerIDNum());
		caRequestEntity.setName(fddDayTripContract.getCustomer());
		caRequestEntity.setPhone(fddDayTripContract.getCustomerPhone());
		String caNum = fddContractUtil.getCustomerCAId(caRequestEntity);
		if(!caNum.equals("")){
			//System.out.println("客户CA申请成功："+caNum);
		}else{
			//System.out.println("客户CA申请失败");
			throw new Exception("客户CA申请失败，检查客户信息是否正确");
		}
		fddDayTripContract.setCustomerCANum(caNum);
		//设置合同的签署时间
		fddDayTripContract.setHySignTime(new Date());
		fddDayTripContract.setCustomerSignTime(new Date());
		//将合同信息存入数据库
		this.save(fddDayTripContract);
		//System.out.println("合同存储成功");
		//请求法大大生成该合同
		List<Filter> filters11 = new ArrayList<>();
		filters11.add(Filter.eq("type", 0));
		FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
		System.out.println("模板id是"+template.getTemplateId());
		HyOrder dingdan = hyOrderService.find(fddDayTripContract.getOrderId());
		HashMap<String, Object> tables = this.getMhDynamicTables(dingdan);
		Json gcResult = fddContractUtil.generateDayTripContract(fddDayTripContract,template,tables);
		if(gcResult.isSuccess()){
			//生成合同成功
			//System.out.println("生成合同成功");
			//将合同id存入订单中,必要时还要添加合同编号，但是合同id应该好用一些。
			HyOrder order = hyOrderService.find(fddDayTripContract.getOrderId());
			order.setContractId(fddDayTripContract.getId());
			order.setContractNumber(fddDayTripContract.getContractId());
			hyOrderService.update(order);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("contractId", fddDayTripContract.getContractId()));
			List<FddDayTripContract> fddDayTripContracts = this.findList(null,filters,null);
			//设置合同状态为生成合同成功以及设置相应的下载和查看地址
			FddDayTripContract fc = fddDayTripContracts.get(0);
			Long currId = fc.getId();
			fc.setStatus(2);
			HashMap<String, Object> hashMap = (HashMap<String, Object>)gcResult.getObj();
			
			fc.setDownloadUrl((String)hashMap.get("download_url"));
			fc.setViewpdfUrl((String)hashMap.get("viewpdf_url"));
			//初始化虹宇自动签章的交易号
			fc.setHyTransactionId("HYSIGN"+DateUtil.getfileDate(new Date()));
			//初始化客户手动签章的交易号
			fc.setCustomerTransactionId("CTSIGN"+DateUtil.getfileDate(new Date()));
			
			this.update(fc);
			FddDayTripContract newfdtc = this.find(currId);
			//虹宇自动签章
			AutoSignRequest autoSignRequest = new AutoSignRequest();
			autoSignRequest.setPositionType(1);
			autoSignRequest.setTransactionId(newfdtc.getHyTransactionId());
			autoSignRequest.setContractId(newfdtc.getContractId());
			JSONArray signaturePositions = new JSONArray();
			JSONObject position = new JSONObject();
			position.put("pagenum", 1);
			position.put("x", 577.7);
			position.put("y", 656.1);
			signaturePositions.add(position);
			autoSignRequest.setSignaturePositions(signaturePositions.toJSONString());
			autoSignRequest.setDocTitle("DX"+newfdtc.getContractId());
			Json asc = fddContractUtil.autoSignDayTripContract(autoSignRequest);
			if(asc.isSuccess()){
				hm.put("orderId", newfdtc.getOrderId());
				hm.put("id", newfdtc.getId());
				hm.put("contractId", newfdtc.getContractId());
				hm.put("msg", "虹宇自动签章成功");
				
			}else{
				hm.put("msg", "虹宇自动签章失败:"+asc.getMsg());
			}
		}else{
			hm.put("msg", "法大大生成合同失败");	
		}	
		return hm;
	}

	@Override
	public Json autoSignContractForMh(HyOrder order) throws Exception {
		Json json  = new Json();
		/*先填充合同数据*/
		HashMap<String, Object> fillInResult = fillInForMh(order.getId());
		FddDayTripContract fddDayTripContract = (FddDayTripContract)fillInResult.get("fddDayTripContract");
		/*生成合同并执行虹宇自动签章*/
		HashMap<String, Object> submitResult = submitForMh(fddDayTripContract);
		Long id = (Long) submitResult.get("id");
		/*给客户发送短信*/
		String ans = extCustomerSign(id);
		JSONObject jsonObject = JSONObject.parseObject(ans);
		String code = jsonObject.getString("code");
		if(code.equals("3000")){
			json.setMsg("给客户发送信息成功，等待客户签章");
			json.setSuccess(true);
		}else{
			throw new Exception("给客户发送信息失败:"+jsonObject.getString("msg"));
		}		
		return json;
	}
	
	/**
	 * 在部分退款的时候，退掉的是联系人的话
	 * 更新当前订单联系人
	 * @return
	 */
	private void updateContact (Long orderId) throws Exception{
		HyOrder order = hyOrderService.find(orderId);
		if(order.getRefundstatus()==3){
			List<HyOrderItem> items = order.getOrderItems();
			/*直接找到第一个没有退的人即可*/
			for(HyOrderItem item:items){
				if(item.getStatus()==0 && item.getNumberOfReturn()==0){
					//判断当前的item里面的联系人
					List<HyOrderCustomer> customers = item.getHyOrderCustomers();
					if(customers!=null && customers.size()>0){
						order.setContact(customers.get(0).getName());
						order.setContactIdNumber(customers.get(0).getCertificate());
						order.setPhone(customers.get(0).getPhone());
						hyOrderService.update(order);
					}
					
				}
			}
			
		}
	}
}
