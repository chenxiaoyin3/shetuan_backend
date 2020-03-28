package com.hongyu.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.bouncycastle.jce.provider.BrokenJCEBlockCipher.BrokePBEWithMD5AndDES;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.controller.InnerSupplierProfit.InnerSupplierProfitManager;
import com.hongyu.dao.FddContractDao;
import com.hongyu.dao.GroupDivideDao;
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
import com.hongyu.service.FddContractService;
import com.hongyu.service.FddContractTemplateService;
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
import com.hongyu.util.contract.SelfPayAgreement;
import com.hongyu.util.contract.ShoppingAgreement;
import com.hongyu.util.contract.SignRequest;
import com.hongyu.util.liyang.CopyFromNotNullBeanUtilsBean;
@Service("fddContractServiceImpl")
@Transactional
public class FddContractServiceImpl extends BaseServiceImpl<FddContract, Long> implements FddContractService{
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name = "fddContractTemplateServiceImpl")
	FddContractTemplateService fddContractTemplateService;
	
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
	@Resource(name="fddContractDaoImpl")
	public void setBaseDao(BaseDao<FddContract, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	@Override
	public HashMap<String,Object> getDynamicTables(HyOrder order,List<ShoppingAgreement> shoppingAgreements,List<SelfPayAgreement> selfPayAgreements) throws Exception {
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
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH");
		for(ShoppingAgreement sa:shoppingAgreements){
			HashMap<String, Object> hm = new HashMap<>();
			if(sa.getShoppingTime()!=null)
				hm.put("time", dFormat.format(sa.getShoppingTime()));
			else
				hm.put("time", "");
			hm.put("address", sa.getAddress());
			hm.put("storeName", sa.getStoreName());
			hm.put("goodInfo", sa.getGoodInfo());
			if(sa.getDuration()!=null)
				hm.put("duration",String.valueOf(sa.getDuration()));
			else
				hm.put("duration", "");
			
			hm.put("otherInfo", sa.getOtherInfo());
			hm.put("customerName", order.getContact());		
			shoppings.add(hm);
		}
		result.put("shoppings",shoppings);
		/****自愿付费旅游项目协议****/
		List<HashMap<String, Object>> payItems = new ArrayList<>();
		for(SelfPayAgreement spa:selfPayAgreements){
			HashMap<String, Object> hm = new HashMap<>();
			if(spa.getTime()!=null)
				hm.put("time", dFormat.format(spa.getTime()));
			else
				hm.put("time", "");
			hm.put("address", spa.getAddress());
			hm.put("itemDescription", spa.getItemDescription());
			if(spa.getMoney()!=null)
				hm.put("money", spa.getMoney().toString());
			else
				hm.put("money", "");
			if(spa.getDuration()!=null)
				hm.put("duration",String.valueOf(spa.getDuration()));
			else
				hm.put("duration", "");
			
			hm.put("otherInfo", spa.getOtherInfo());
			hm.put("customerName", order.getContact());		
			payItems.add(hm);
		}
		result.put("payItems",payItems);
		return result;
	}
	/**
	 * 官网线路的动态表单获取
	 * @param order
	 * @return
	 * @throws Exception
	 */
	@Override
	public HashMap<String,Object> getMhDynamicTables(HyOrder order) throws Exception {
		// TODO Auto-generated method stub
		//总共四个附件，也就是四个动态表格
		HashMap<String,Object> result = new HashMap<>();
		/****游客名单表****/
		List<HashMap<String, Object>> orderCustomers = new ArrayList<>();
		List<HyOrderItem> orderItems = order.getOrderItems();
		for(HyOrderItem item : orderItems){
			//订单条目类型是线路,且该条目没有被退款
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
	/**
	 * 填充fddContract数据，给前台展示
	 */
	@Override
	public HashMap<String, Object> fillIn(Long orderId,HttpSession session) throws Exception {
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		/**判断当前的联系人有没有退款，退款的话就把订单的联系人改成剩下的第一个游客*/
		updateContact(orderId);
		//返回fddContract实体和表单内容
		HashMap<String,Object> result = new HashMap<>();
		HyOrder hyOrder = hyOrderService.find(orderId);
		List<ShoppingAgreement> shoppingAgreements = new ArrayList<>();
		List<SelfPayAgreement> selfPayAgreements = new ArrayList<>();
		result.put("tables", this.getDynamicTables(hyOrder,shoppingAgreements,selfPayAgreements));
		//System.out.println(hyOrder.getOrderNumber());
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		if(hyOrder.getContractId() == null){
			//如果该订单没有之前没有创建过合同，就创建一个新的合同。
			FddContract fddContract = fddContractUtil.hyOrder2fddContract(hyOrder);	
			fddContract.setHyOperator(admin.getName());
			//设置虹宇的门店信息,因为现在全部以虹宇的名义签署，不需要具体门店了
//			Department department = admin.getDepartment();
//			if(department.getStore()!=null){
//				Store store = department.getStore();
//				fddContract.setHyAddress(store.getAddress());
//				fddContract.setHyId(store.getUniqueCode());
//				fddContract.setHyName(store.getStoreName());
//				fddContract.setHyPhone(store.getHyAdmin().getMobile());
//			}			
//			System.out.println("hyGroup id是："+hyOrder.getGroupId());
			HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
			if(hyGroup!=null){
//				System.out.println("结束日期是："+hyGroup.getEndDay());
				fddContract.setEndTime(hyGroup.getEndDay());
			}
			List<Filter> filters = new ArrayList<>();
			//签署人
			fddContract.setCustomer(hyOrder.getContact());			
			//签署人号码
			fddContract.setCustomerPhone(hyOrder.getPhone());
			//签署人身份证号码
			fddContract.setCustomerIDNum(hyOrder.getContactIdNumber());
			//重用filter
			filters.clear();
			filters.add(Filter.eq("orderId", hyOrder.getId()));
			List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
			for(PayandrefundRecord tmp : payandrefundRecords){
				if(tmp.getType() == 0 && tmp.getStatus()== 1){
					//支付方式
					fddContract.setPaymentMethod(tmp.getPayMethod());
					//支付时间
					fddContract.setPaymentTime(tmp.getCreatetime());
					break;
				}
			}
			//此处填写票价，现在这个里面可以拿到priceId和productId，但是不知道去哪个表查
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			for(HyOrderItem tmp : hyOrderItems){
				//筛选出成人和儿童，然后获取票价
				/**当订单条目类型type为1线路
				 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
				 */
				if(tmp.getType() == 1 && tmp.getPriceType()==0){
					//获取成人价
					HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
					fddContract.setAdultTicketPrice(hyGroupPrice.getAdultPrice1());
					continue;
				}
				if(tmp.getType() == 1 && tmp.getPriceType()==1){
					//获取儿童价
					HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
					fddContract.setChildrenTicketPrice(hyGroupPrice.getChildrenPrice1());
					continue;
				}
			}
			HyLine line = hyGroup.getLine();
			if(line==null)
				throw new Exception("没有找到该团对应的线路，线路为空！");
			List<HyLineTravels> lineTravels = line.getLineTravels();
			/*住宿天数暂时获取不到*/
			fddContract.setStayAtHotelDays(line.getDays()-1);
			fddContract.setTotalPrice(hyOrder.getWaimaiMoney());
			/*将线路退团说明和备注添加到其他说明里面去   2019.4.11*/
			String lineSeparate = System.getProperty("line.separator");
			StringBuilder otherNote = new StringBuilder();
			if(line.getCancelMemo()!=null && !line.getCancelMemo().equals(""))
				otherNote.append("消团说明："+line.getCancelMemo());
			if(line.getMemo()!=null && !line.getMemo().equals(""))
				otherNote.append(lineSeparate+"备注："+line.getMemo());
			fddContract.setOtherNote(otherNote.toString());
			
			result.put("fddContract", fddContract);
			result.put("msg", "获取成功(第一次创建合同)");
		}else{
			//如果已有合同号，那就属于重新签署，需要重新设置合同号
			FddContract fddContract = this.find(hyOrder.getContractId());
			//将原有合同设置为已取消状态
			fddContract.setCancelDate(new Date());
			fddContract.setStatus(5);
			this.update(fddContract);
			FddContract tmp = new FddContract();
			new CopyFromNotNullBeanUtilsBean().copyProperties(tmp, fddContract);
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
			result.put("fddContract", tmp);
			result.put("msg", "获取成功(已有合同，从数据库获取)");		
		}
		return result;
	}
	@Override
	public HashMap<String, Object> submit(FddContract fddContract,List<ShoppingAgreement> shoppingAgreements,List<SelfPayAgreement> selfPayAgreements) throws Exception {
//		System.out.println("从前台接受的fddContract为："+fddContract.getEndTime());
		HashMap<String, Object> hm = new HashMap<>();
		//System.out.println("进入submit");
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		fddContract.setId(null);
		//在这为签署人申请CA证书号码
		CARequestEntity caRequestEntity = new CARequestEntity();
		caRequestEntity.setIdCard(fddContract.getCustomerIDNum());
		caRequestEntity.setName(fddContract.getCustomer());
		caRequestEntity.setPhone(fddContract.getCustomerPhone());
		String caNum = fddContractUtil.getCustomerCAId(caRequestEntity);
		if(caNum!=null){
			System.out.println("客户CA申请成功："+caNum);
		}else{
			throw new Exception("客户CA申请失败，身份证号码不正确");
		}
		
		fddContract.setCustomerCANum(caNum);
		//设置合同的签署时间
		fddContract.setHySignTime(new Date());
		fddContract.setCustomerSignTime(new Date());
		//将合同信息存入数据库
		this.save(fddContract);
		//System.out.println("合同存储成功");
		//请求法大大生成该合同
		List<Filter> filters11 = new ArrayList<>();
		filters11.add(Filter.eq("type", fddContract.getType()));
		FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
		//System.out.println("模板id是"+template.getTemplateId());
		HyOrder dingdan = hyOrderService.find(fddContract.getOrderId());
		HashMap<String, Object> tables = this.getDynamicTables(dingdan,shoppingAgreements,selfPayAgreements);
		Json gcResult = fddContractUtil.generateContract(fddContract,template,tables);
		if(gcResult.isSuccess()){
			//生成合同成功
			//System.out.println("生成合同成功");
			//将合同id存入订单中,必要时还要添加合同编号，但是合同id应该好用一些。
			HyOrder order = hyOrderService.find(fddContract.getOrderId());
			order.setContractId(fddContract.getId());
			order.setContractNumber(fddContract.getContractId());
			hyOrderService.update(order);
		
			fddContract.setStatus(2);
			HashMap<String, Object> hashMap = (HashMap<String, Object>)gcResult.getObj();
			
			fddContract.setDownloadUrl((String)hashMap.get("download_url"));
			fddContract.setViewpdfUrl((String)hashMap.get("viewpdf_url"));
			//初始化虹宇自动签章的交易号
			fddContract.setHyTransactionId("HYSIGN"+DateUtil.getfileDate(new Date()));
			//初始化客户手动签章的交易号
			fddContract.setCustomerTransactionId("CTSIGN"+DateUtil.getfileDate(new Date()));
			
			this.update(fddContract);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("storeId",order.getStoreId()));
			List<FddStoreCA> fddStoreCAs = fddStoreCAService.findList(null,filters,null);
			//虹宇自动签章
			AutoSignRequest autoSignRequest = new AutoSignRequest();
			/*如果查到了门店CA，就使用门店的章，否则使用默认的虹宇的章 2019.05.27   liyang*/
			if(!fddStoreCAs.isEmpty()){
				FddStoreCA ca = fddStoreCAs.get(0);
				if(ca.getStoreCA()!=null && !ca.getStoreCA().equals("")){
					autoSignRequest.setCustomerId(ca.getStoreCA());
				}
			}
			autoSignRequest.setKeyword(template.getSignKeyWord());
			autoSignRequest.setTransactionId(fddContract.getHyTransactionId());
			autoSignRequest.setContractId(fddContract.getContractId());
			
			String suffix = "";
			if(fddContract.getType()==0){
				suffix = "DX";
			}else{
				suffix = fddContract.getType()==1?"GN":"CJ";
			}
			autoSignRequest.setDocTitle(suffix+fddContract.getContractId());
			Json asc = fddContractUtil.autoSignContract(autoSignRequest);
			if(asc.isSuccess()){
				hm.put("orderId", fddContract.getOrderId());
				hm.put("id", fddContract.getId());
				hm.put("contractId", fddContract.getContractId());
				hm.put("msg", "虹宇自动签章成功");
			}else{
				hm.put("msg", "虹宇自动签章失败"+asc.getMsg());
			}
		}else{
			hm.put("msg", "法大大生成合同失败:"+gcResult.getMsg());	
		}	
		return hm;
	}
	@Override
	public String extCustomerSign(Long id) throws Exception {
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		FddContract fddContract = this.find(id);
		//System.out.println("虹宇自动签章成功,现在进入个人签章");
		SignRequest signRequest = new SignRequest();
		//System.out.println(fddDayTripContract.getContractId());
		signRequest.setContractId(fddContract.getContractId());
		signRequest.setCustomerId(fddContract.getCustomerCANum());
		String suffix = "";
		if(fddContract.getType()==0){
			suffix = "DX";
		}else{
			suffix = fddContract.getType()==1?"GN":"CJ";
		}
		signRequest.setDocTitle(suffix+fddContract.getContractId());
		signRequest.setTransactionId(fddContract.getCustomerTransactionId());
		
		/*指定客户签章坐标位置*/
//		JSONArray signaturePositions = new JSONArray();
//		JSONObject position = new JSONObject();
//		position.put("pagenum", 14);
//		position.put("x", 354.0489);
//		position.put("y", 728.9001);
//		signaturePositions.add(position);
//		
//		signRequest.setPositionType(1);
//		signRequest.setSignaturePositions(signaturePositions.toJSONString());
		
		
		String sign_url = fddContractUtil.pushDoc_extSign(signRequest);
		//System.out.println("sign_url = "+sign_url);
		//将客户签约的地址,拿到返回结果
		String result = HttpReqUtil.HttpsDefaultExecute("POST", sign_url, null, null, "UTF-8");
		//System.out.println("result = "+result==null?"null":result);
	
		return result;
	}
	/**
	 * 一键填充并生成合同然后虹宇签章最终发短信给客户签署
	 */
	@Override
	public Json autoSignContractForMh(HyOrder order) throws Exception {
		Json json  = new Json();
		/*先填充合同数据*/
		HashMap<String, Object> fillInResult = fillInForMh(order.getId());
		FddContract fddContract = (FddContract)fillInResult.get("fddContract");
		/*生成合同并执行虹宇自动签章*/
		HashMap<String, Object> submitResult = submitForMh(fddContract);
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
	 * 填充官网订单
	 */
	@Override
	public HashMap<String, Object> fillInForMh(Long orderId) throws Exception {
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		//返回fddContract实体和表单内容
		HashMap<String,Object> result = new HashMap<>();
		HyOrder hyOrder = hyOrderService.find(orderId);
		result.put("tables", this.getMhDynamicTables(hyOrder));
		if(hyOrder.getContractId() == null){
			//如果该订单没有之前没有创建过合同，就创建一个新的合同。
			FddContract fddContract = fddContractUtil.hyOrder2fddContract(hyOrder);			
			HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
			if(hyGroup!=null){
				fddContract.setEndTime(hyGroup.getEndDay());
			}
			List<Filter> filters = new ArrayList<>();
			//签署人身份证号码
			fddContract.setCustomerIDNum(hyOrder.getContactIdNumber());
			//重用filter
			filters.clear();
			filters.add(Filter.eq("orderId", hyOrder.getId()));
			List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
			for(PayandrefundRecord tmp : payandrefundRecords){
				if(tmp.getType() == 0 && tmp.getStatus()== 1){
					//支付方式
					fddContract.setPaymentMethod(tmp.getPayMethod());
					//支付时间
					fddContract.setPaymentTime(tmp.getCreatetime());
					break;
				}
			}
			//此处填写票价，现在这个里面可以拿到priceId和productId，但是不知道去哪个表查
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			Integer source = hyOrder.getSource();
			for(HyOrderItem tmp : hyOrderItems){
				//筛选出成人和儿童，然后获取票价
				/**当订单条目类型type为1线路
				 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
				 */
				if(tmp.getType() == 1 && tmp.getPriceType()==0){
					//获取官网成人销售价。。。成人价
					if(source == 0){
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						fddContract.setAdultTicketPrice(hyGroupPrice.getAdultPrice1());
					}else{
						MhGroupPrice mhGroupPrice = mhGroupPriceService.find(tmp.getPriceId());
						fddContract.setAdultTicketPrice(mhGroupPrice.getMhAdultWaimaiPrice());
					}
					continue;
				}
				if(tmp.getType() == 1 && tmp.getPriceType()==1){
					//获取儿童价
					if(source == 0){
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						fddContract.setChildrenTicketPrice(hyGroupPrice.getAdultPrice1());
					}else{
						MhGroupPrice mhGroupPrice = mhGroupPriceService.find(tmp.getPriceId());
						fddContract.setChildrenTicketPrice(mhGroupPrice.getMhChildrenWaimaiPrice());
					}	
					continue;
				}
			}
			HyLine line = hyGroup.getLine();
			MhLine mhLine = line.getMhLine();
			List<MhLineTravels> lineTravels = mhLine.getMhLineTravels();
			int zhusutianshu = 0;
			for(MhLineTravels ml:lineTravels){
				if(ml.getIfAccommodation()==1){
					zhusutianshu++;
				}
			}
			fddContract.setOtherNote(mhLine.getMattersNeedAttention());
			fddContract.setStayAtHotelDays(zhusutianshu);
			fddContract.setTotalPrice(hyOrder.getWaimaiMoney());
			fddContract.setHyOperator(ContractInfoAboutHy.hyWLXSBOperator);
			fddContract.setHyAddress(ContractInfoAboutHy.hyWLXSBAddress);
			fddContract.setHyName(ContractInfoAboutHy.hyName);
			fddContract.setHyPhone(ContractInfoAboutHy.hyPhone);
			result.put("fddContract", fddContract);
			result.put("msg", "获取成功(第一次创建合同)");
		}else{
			//如果已有合同号，那就属于重新签署，需要重新设置合同号
			FddContract fddContract = this.find(hyOrder.getContractId());
			//将原有合同设置为已取消状态
			fddContract.setCancelDate(new Date());
			fddContract.setStatus(5);
			this.update(fddContract);
			FddContract tmp = new FddContract();
			new CopyFromNotNullBeanUtilsBean().copyProperties(tmp, fddContract);
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
			result.put("fddContract", tmp);
			result.put("msg", "获取成功(已有合同，从数据库获取)");		
		}
		return result;
	}
	/**
	 * 生成官网订单合同，并执行虹宇签章
	 */
	@Override
	public HashMap<String, Object> submitForMh(FddContract fddContract) throws Exception {
		HashMap<String, Object> hm = new HashMap<>();
		System.out.println("进入submit");
		//System.out.println("进入submit");
		FddContractUtil fddContractUtil = FddContractUtil.getInstance();
		fddContract.setId(null);
		//在这为签署人申请CA证书号码
		CARequestEntity caRequestEntity = new CARequestEntity();
		caRequestEntity.setIdCard(fddContract.getCustomerIDNum());
		caRequestEntity.setName(fddContract.getCustomer());
		caRequestEntity.setPhone(fddContract.getCustomerPhone());
		String caNum = fddContractUtil.getCustomerCAId(caRequestEntity);
		if(caNum==null || caNum.equals(""))
			throw new Exception("为客户申请CA号失败，请检查证件号码！");
		
		fddContract.setCustomerCANum(caNum);
		//设置合同的签署时间
		fddContract.setHySignTime(new Date());
		fddContract.setCustomerSignTime(new Date());
		//将合同信息存入数据库
		this.save(fddContract);
		//System.out.println("合同存储成功");
		//请求法大大生成该合同
		List<Filter> filters11 = new ArrayList<>();
		filters11.add(Filter.eq("type", fddContract.getType()));
		FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
		//System.out.println("模板id是"+template.getTemplateId());
		HyOrder dingdan = hyOrderService.find(fddContract.getOrderId());
		HashMap<String, Object> tables = this.getMhDynamicTables(dingdan);
		Json gcResult = fddContractUtil.generateContract(fddContract,template,tables);
		if(gcResult.isSuccess()){
			//生成合同成功
			//System.out.println("生成合同成功");
			//将合同id存入订单中,必要时还要添加合同编号，但是合同id应该好用一些。
			HyOrder order = hyOrderService.find(fddContract.getOrderId());
			order.setContractId(fddContract.getId());
			order.setContractNumber(fddContract.getContractId());
			hyOrderService.update(order);
		
			fddContract.setStatus(2);
			HashMap<String, Object> hashMap = (HashMap<String, Object>)gcResult.getObj();
			
			fddContract.setDownloadUrl((String)hashMap.get("download_url"));
			fddContract.setViewpdfUrl((String)hashMap.get("viewpdf_url"));
			//初始化虹宇自动签章的交易号
			fddContract.setHyTransactionId("HYSIGN"+DateUtil.getfileDate(new Date()));
			//初始化客户手动签章的交易号
			fddContract.setCustomerTransactionId("CTSIGN"+DateUtil.getfileDate(new Date()));
			
			this.update(fddContract);
					
			//虹宇自动签章
			AutoSignRequest autoSignRequest = new AutoSignRequest();
			autoSignRequest.setKeyword(template.getSignKeyWord());
			autoSignRequest.setTransactionId(fddContract.getHyTransactionId());
			autoSignRequest.setContractId(fddContract.getContractId());
			
			String suffix = "";
			if(fddContract.getType()==0){
				suffix = "DX";
			}else{
				suffix = fddContract.getType()==1?"GN":"CJ";
			}
			autoSignRequest.setDocTitle(suffix+fddContract.getContractId());
			Json asc = fddContractUtil.autoSignContract(autoSignRequest);
			if(asc.isSuccess()){
				hm.put("orderId", fddContract.getOrderId());
				hm.put("id", fddContract.getId());
				hm.put("contractId", fddContract.getContractId());
				hm.put("msg", "虹宇自动签章成功");
			}else{
				//hm.put("msg", "虹宇自动签章失败");
				throw new Exception("虹宇自动签章失败"+asc.getMsg());
			}
		}else{
			//hm.put("msg", "法大大生成合同失败");	
			throw new Exception("法大大生成合同失败");
		}	
		return hm;
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
