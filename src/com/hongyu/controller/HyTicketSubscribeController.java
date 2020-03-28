package com.hongyu.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.HyTicketHotelandsceneController.WrapInbound;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketSubscribePriceService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/internTicket/subscribe")
public class HyTicketSubscribeController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name="hyTicketSubscribePriceServiceImpl")
	private HyTicketSubscribePriceService hyTicketSubscribePriceService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyTicketSubscribe queryParam,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.in("creator",hyAdmins));
			List<Filter> supplierFilter=new ArrayList<Filter>();
			supplierFilter.add(Filter.eq("liable", findPAdmin(admin))); //帅选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				filters.add(Filter.eq("ticketSupplier", hySupplier));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyTicketSubscribe> page=hyTicketSubscribeService.findPage(pageable, queryParam);
			if(page.getTotal()>0) {
				for(HyTicketSubscribe ticketSubscribe:page.getRows()) {
					HashMap<String,Object> subscribeMap=new HashMap<String,Object>();
					HyAdmin creator=ticketSubscribe.getCreator();
					subscribeMap.put("id",ticketSubscribe.getId());
					subscribeMap.put("productId",ticketSubscribe.getProductId()); //productId
					subscribeMap.put("sceneName", ticketSubscribe.getSceneName());
					subscribeMap.put("createTime",ticketSubscribe.getCreateTime());
					subscribeMap.put("creator",ticketSubscribe.getCreator().getName());
					subscribeMap.put("auditStatus", ticketSubscribe.getAuditStatus());
					subscribeMap.put("saleStatus",ticketSubscribe.getSaleStatus());			
					subscribeMap.put("status",ticketSubscribe.getStatus());
					/** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		subscribeMap.put("privilege", "view");
				    	}
				    	else{
				    		subscribeMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		subscribeMap.put("privilege", "edit");
				    	}
				    	else{
				    		subscribeMap.put("privilege", "view");
				    	}
				    }
					list.add(subscribeMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 由父区域的ID得到全部的子区域
	 * @param id
	 * @return
	 */
	@RequestMapping(value="areacomboxlist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if(parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if(child.getStatus()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("value", child.getId());
						hm.put("label", child.getName());
						hm.put("isLeaf", child.getHyAreas().size() == 0);
						obj.add(hm);
					}
				}
			}
			hashMap.put("total", parent.getHyAreas().size());
			hashMap.put("data", obj);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(obj);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/*新建一个包装类,传递参数*/
	static class WrapHyTicketSubscribe{
		private HyTicketSubscribe hyTicketSubscribe;
		private String openTime;
		private String closeTime;
		private Long areaId; //景区位置id
		private Long restrictId; //限购区域id
		private Long supplierId; //旅游元素供应商id
		
		public String getOpenTime() {
			return openTime;
		}
		public void setOpenTime(String openTime) {
			this.openTime = openTime;
		}
		public String getCloseTime() {
			return closeTime;
		}
		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}
		public HyTicketSubscribe getHyTicketSubscribe() {
			return hyTicketSubscribe;
		}
		public void setHyTicketSubscribe(HyTicketSubscribe hyTicketSubscribe) {
			this.hyTicketSubscribe = hyTicketSubscribe;
		}
		public Long getAreaId() {
			return areaId;
		}
		public void setAreaId(Long areaId) {
			this.areaId = areaId;
		}
		public Long getRestrictId() {
			return restrictId;
		}
		public void setRestrictId(Long restrictId) {
			this.restrictId = restrictId;
		}
		public Long getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(Long supplierId) {
			this.supplierId = supplierId;
		}
	}
	
	//保存并提交审核
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyTicketSubscribe wrapHyTicketSubscribe,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyTicketSubscribe hyTicketSubscribe=wrapHyTicketSubscribe.getHyTicketSubscribe();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketSubscribePrice> priceList=hyTicketSubscribe.getHyTicketSubscribePrices();
			//增加判断合同期限与上产品日期的比较，产品日期不能超出合同期限
			HyAdmin contractLiable=new HyAdmin();//合同负责人
			if(admin.getHyAdmin() != null) {
				contractLiable = admin.getHyAdmin();
				
			} else {
				contractLiable = admin;
			}
			List<Filter> contractList=new ArrayList<>();
			contractList.add(Filter.eq("liable", contractLiable)); //该负责人的合同
			contractList.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //状态正常
			List<HySupplierContract> supplierContractList=hySupplierContractService.findList(null,contractList,null);
			//如果该供应商没有正常状态的合同
			if(supplierContractList.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("没有正常状态的合同");
				return json;
			}
			//原则上一个合同负责人只有个正常状态的合同
			HySupplierContract supplierContract=supplierContractList.get(0);
			Date expiration=supplierContract.getDeadDate(); //找过合同到期日期
			for(HyTicketSubscribePrice priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			
			Long areaId=wrapHyTicketSubscribe.getAreaId();
			Long restrictId=wrapHyTicketSubscribe.getRestrictId();
			Long supplierId=wrapHyTicketSubscribe.getSupplierId();
			String openTime=wrapHyTicketSubscribe.getOpenTime();
			Date open = new SimpleDateFormat("HH:mm:ss").parse(openTime);
			String closeTime=wrapHyTicketSubscribe.getCloseTime();
			Date close= new SimpleDateFormat("HH:mm:ss").parse(closeTime);
			hyTicketSubscribe.setOpenTime(open);
			hyTicketSubscribe.setCloseTime(close);
			HyArea area=hyAreaService.find(areaId);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("subscribeTicket");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.SubscribeTicket));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MPRG-" + dateStr + "-" + String.format("%04d", value);
			}
			hyTicketSubscribe.setProductId(produc);
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyTicketSubscribe.setTicketSupplier(hySupplier);
			}	
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribe.setCreator(admin);
			hyTicketSubscribe.setCreateTime(new Date());
			hyTicketSubscribe.setArea(area);
			if(supplierId!=null) {
				HySupplierElement hySupplierElement=hySupplierElementService.find(supplierId);
				hyTicketSubscribe.setHySupplierElement(hySupplierElement);
			}	
			if(restrictId!=null) {
				HyArea restrictArea=hyAreaService.find(restrictId);
				hyTicketSubscribe.setRestrictArea(restrictArea);
			}			
			hyTicketSubscribe.setSaleStatus(1); //未上架
			hyTicketSubscribe.setAuditStatus(2); //提交审核
			hyTicketSubscribe.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketSubscribe.setSubmitTime(new Date());
			hyTicketSubscribe.setSubmitter(admin);
			//如果预约天数和预约时间为空,则设为0
			if(hyTicketSubscribe.getDays()==null) {
				hyTicketSubscribe.setDays(0);
			}
			if(hyTicketSubscribe.getReserveTime()==null) {
				hyTicketSubscribe.setReserveTime(0);
			}
			hyTicketSubscribeService.save(hyTicketSubscribe);
			
			if(priceList.size()>0){ 
				for(HyTicketSubscribePrice price:priceList){
					price.setTicketSubscribe(hyTicketSubscribe);
					hyTicketSubscribePriceService.save(price);		
				}
			}
			// 完成 房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//只保存,不提交审核
	@RequestMapping(value="save", method = RequestMethod.POST)
	@ResponseBody
	public Json save(@RequestBody WrapHyTicketSubscribe wrapHyTicketSubscribe,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyTicketSubscribe hyTicketSubscribe=wrapHyTicketSubscribe.getHyTicketSubscribe();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketSubscribePrice> priceList=hyTicketSubscribe.getHyTicketSubscribePrices();
			//增加判断合同期限与上产品日期的比较，产品日期不能超出合同期限
			HyAdmin contractLiable=new HyAdmin();//合同负责人
			if(admin.getHyAdmin() != null) {
				contractLiable = admin.getHyAdmin();
				
			} else {
				contractLiable = admin;
			}
			List<Filter> contractList=new ArrayList<>();
			contractList.add(Filter.eq("liable", contractLiable)); //该负责人的合同
			contractList.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //状态正常
			List<HySupplierContract> supplierContractList=hySupplierContractService.findList(null,contractList,null);
			//如果该供应商没有正常状态的合同
			if(supplierContractList.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("没有正常状态的合同");
				return json;
			}
			//原则上一个合同负责人只有个正常状态的合同
			HySupplierContract supplierContract=supplierContractList.get(0);
			Date expiration=supplierContract.getDeadDate(); //找过合同到期日期
			for(HyTicketSubscribePrice priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			
			Long areaId=wrapHyTicketSubscribe.getAreaId();
			Long restrictId=wrapHyTicketSubscribe.getRestrictId();
			Long supplierId=wrapHyTicketSubscribe.getSupplierId();
			String openTime=wrapHyTicketSubscribe.getOpenTime();
			Date open = new SimpleDateFormat("HH:mm:ss").parse(openTime);
			String closeTime=wrapHyTicketSubscribe.getCloseTime();
			Date close= new SimpleDateFormat("HH:mm:ss").parse(closeTime);
			hyTicketSubscribe.setOpenTime(open);
			hyTicketSubscribe.setCloseTime(close);
			HyArea area=hyAreaService.find(areaId);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.SubscribeTicket));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MPRG-" + dateStr + "-" + String.format("%04d", value);
			}
			hyTicketSubscribe.setProductId(produc);
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyTicketSubscribe.setTicketSupplier(hySupplier);
			}	
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribe.setCreator(admin);
			hyTicketSubscribe.setCreateTime(new Date());
			hyTicketSubscribe.setArea(area);
			if(supplierId!=null) {
				HySupplierElement hySupplierElement=hySupplierElementService.find(supplierId);
				hyTicketSubscribe.setHySupplierElement(hySupplierElement);
			}
			if(restrictId!=null) {
				HyArea restrictArea=hyAreaService.find(restrictId);
				hyTicketSubscribe.setRestrictArea(restrictArea);
			}			
			hyTicketSubscribe.setSaleStatus(1); //未上架
			hyTicketSubscribe.setAuditStatus(1); //未提交审核
			//如果预约天数和预约时间为空,则设为0
			if(hyTicketSubscribe.getDays()==null) {
				hyTicketSubscribe.setDays(0);
			}
			if(hyTicketSubscribe.getReserveTime()==null) {
				hyTicketSubscribe.setReserveTime(0);
			}
			hyTicketSubscribeService.save(hyTicketSubscribe);
			
			if(priceList.size()>0){ 
				for(HyTicketSubscribePrice price:priceList){
					price.setTicketSubscribe(hyTicketSubscribe);
					hyTicketSubscribePriceService.save(price);		
				}
			}
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("submit")
	@ResponseBody
	public Json submit(Long id,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			hyTicketSubscribe.setAuditStatus(2); //提交审核
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin=hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("subscribeTicket");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			hyTicketSubscribe.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketSubscribe.setSubmitTime(new Date());
			hyTicketSubscribe.setSubmitter(admin);
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribeService.update(hyTicketSubscribe);
			
			// 完成门票认购价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			
			json.setMsg("提交成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editTicket/submit")
	@ResponseBody
	public Json editsubmitTicket(@RequestBody WrapHyTicketSubscribe wrapHyTicketSubscribe,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyTicketSubscribe hyTicketSubscribe=wrapHyTicketSubscribe.getHyTicketSubscribe();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketSubscribePrice> priceList=hyTicketSubscribe.getHyTicketSubscribePrices();
			//增加判断合同期限与上产品日期的比较，产品日期不能超出合同期限
			HyAdmin contractLiable=new HyAdmin();//合同负责人
			if(admin.getHyAdmin() != null) {
				contractLiable = admin.getHyAdmin();
				
			} else {
				contractLiable = admin;
			}
			List<Filter> contractList=new ArrayList<>();
			contractList.add(Filter.eq("liable", contractLiable)); //该负责人的合同
			contractList.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //状态正常
			List<HySupplierContract> supplierContractList=hySupplierContractService.findList(null,contractList,null);
			//如果该供应商没有正常状态的合同
			if(supplierContractList.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("没有正常状态的合同");
				return json;
			}
			//原则上一个合同负责人只有个正常状态的合同
			HySupplierContract supplierContract=supplierContractList.get(0);
			Date expiration=supplierContract.getDeadDate(); //找过合同到期日期
			for(HyTicketSubscribePrice priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			Long areaId=wrapHyTicketSubscribe.getAreaId();
			Long restrictId=wrapHyTicketSubscribe.getRestrictId();
			Long supplierId=wrapHyTicketSubscribe.getSupplierId();
			Long id=wrapHyTicketSubscribe.getHyTicketSubscribe().getId();	
			String openTime=wrapHyTicketSubscribe.getOpenTime();
			Date open = new SimpleDateFormat("HH:mm:ss").parse(openTime);
			String closeTime=wrapHyTicketSubscribe.getCloseTime();
			Date close= new SimpleDateFormat("HH:mm:ss").parse(closeTime);
			hyTicketSubscribe.setOpenTime(open);
			hyTicketSubscribe.setCloseTime(close);
			HyArea area=hyAreaService.find(areaId);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("subscribeTicket");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribe.setModifyTime(new Date());
			hyTicketSubscribe.setArea(area);
			if(supplierId!=null) {
				HySupplierElement hySupplierElement=hySupplierElementService.find(supplierId);
				hyTicketSubscribe.setHySupplierElement(hySupplierElement);
			}
			if(restrictId!=null) {			
				HyArea restrictArea=hyAreaService.find(restrictId);
				hyTicketSubscribe.setRestrictArea(restrictArea);
			}		
			hyTicketSubscribe.setSaleStatus(1); //未上架
			hyTicketSubscribe.setAuditStatus(2); //提交审核
			hyTicketSubscribe.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketSubscribe.setSubmitTime(new Date());
			hyTicketSubscribe.setSubmitter(admin);
			//如果预约天数和预约时间为空,则设为0
			if(hyTicketSubscribe.getDays()==null) {
				hyTicketSubscribe.setDays(0);
			}
			if(hyTicketSubscribe.getReserveTime()==null) {
				hyTicketSubscribe.setReserveTime(0);
			}
			hyTicketSubscribeService.update(hyTicketSubscribe,"ticketSupplier","productId","creator","createTime");
			
			//删除编辑前的价格
			HyTicketSubscribe ticketSubscribe=hyTicketSubscribeService.find(id);
			List<HyTicketSubscribePrice> hyTicketSubscribePrices=ticketSubscribe.getHyTicketSubscribePrices();
			for(HyTicketSubscribePrice subscribePrice:hyTicketSubscribePrices) {
				hyTicketSubscribePriceService.delete(subscribePrice);
			}
			
			//添加编辑后的价格
			if(priceList.size()>0){ 
				for(HyTicketSubscribePrice price:priceList){
					price.setTicketSubscribe(hyTicketSubscribe);
					hyTicketSubscribePriceService.save(price);		
				}
			}
			// 完成 房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editTicket/save")
	@ResponseBody
	public Json editsaveTicket(@RequestBody WrapHyTicketSubscribe wrapHyTicketSubscribe,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyTicketSubscribe hyTicketSubscribe=wrapHyTicketSubscribe.getHyTicketSubscribe();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketSubscribePrice> priceList=hyTicketSubscribe.getHyTicketSubscribePrices();
			//增加判断合同期限与上产品日期的比较，产品日期不能超出合同期限
			HyAdmin contractLiable=new HyAdmin();//合同负责人
			if(admin.getHyAdmin() != null) {
				contractLiable = admin.getHyAdmin();
				
			} else {
				contractLiable = admin;
			}
			List<Filter> contractList=new ArrayList<>();
			contractList.add(Filter.eq("liable", contractLiable)); //该负责人的合同
			contractList.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //状态正常
			List<HySupplierContract> supplierContractList=hySupplierContractService.findList(null,contractList,null);
			//如果该供应商没有正常状态的合同
			if(supplierContractList.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("没有正常状态的合同");
				return json;
			}
			//原则上一个合同负责人只有个正常状态的合同
			HySupplierContract supplierContract=supplierContractList.get(0);
			Date expiration=supplierContract.getDeadDate(); //找过合同到期日期
			for(HyTicketSubscribePrice priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			Long areaId=wrapHyTicketSubscribe.getAreaId();
			Long restrictId=wrapHyTicketSubscribe.getRestrictId();
			Long supplierId=wrapHyTicketSubscribe.getSupplierId();
			Long id=wrapHyTicketSubscribe.getHyTicketSubscribe().getId();
			String openTime=wrapHyTicketSubscribe.getOpenTime();
			Date open = new SimpleDateFormat("HH:mm:ss").parse(openTime);
			String closeTime=wrapHyTicketSubscribe.getCloseTime();
			Date close= new SimpleDateFormat("HH:mm:ss").parse(closeTime);
			hyTicketSubscribe.setOpenTime(open);
			hyTicketSubscribe.setCloseTime(close);
			HyArea area=hyAreaService.find(areaId);
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribe.setModifyTime(new Date());
			hyTicketSubscribe.setArea(area);
			if(supplierId!=null) {
				HySupplierElement hySupplierElement=hySupplierElementService.find(supplierId);
				hyTicketSubscribe.setHySupplierElement(hySupplierElement);
			}		
			if(restrictId!=null) {			
				HyArea restrictArea=hyAreaService.find(restrictId);
				hyTicketSubscribe.setRestrictArea(restrictArea);
			}	
			hyTicketSubscribe.setSaleStatus(1); //未上架
			hyTicketSubscribe.setAuditStatus(1); //未提交审核
			
			//如果预约天数和预约时间为空,则设为0
			if(hyTicketSubscribe.getDays()==null) {
				hyTicketSubscribe.setDays(0);
			}
			if(hyTicketSubscribe.getReserveTime()==null) {
				hyTicketSubscribe.setReserveTime(0);
			}
			hyTicketSubscribeService.update(hyTicketSubscribe,"ticketSupplier","productId","creator","createTime");
			
			//删除编辑前的价格
			HyTicketSubscribe ticketSubscribe=hyTicketSubscribeService.find(id);
			List<HyTicketSubscribePrice> hyTicketSubscribePrices=ticketSubscribe.getHyTicketSubscribePrices();
			for(HyTicketSubscribePrice subscribePrice:hyTicketSubscribePrices) {
				hyTicketSubscribePriceService.delete(subscribePrice);
			}
			
			//添加编辑后的价格
			if(priceList.size()>0){ 
				for(HyTicketSubscribePrice price:priceList){
					price.setTicketSubscribe(hyTicketSubscribe);
					hyTicketSubscribePriceService.save(price);		
				}
			}
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json priceDetail(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			String processInstanceId = hyTicketSubscribe.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> obj = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				obj.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				obj.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					obj.put("comment", " ");
					obj.put("result", 1);
				} else {
					obj.put("comment", str.substring(0, index));
					obj.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				obj.put("time", comment.getTime());

				list.add(obj);
			}
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("auditList", list);	
			map.put("productId", hyTicketSubscribe.getProductId());
			map.put("sceneName", hyTicketSubscribe.getSceneName());
			if(hyTicketSubscribe.getTicketSupplier().getIsInner()==true) {
				map.put("supplierId", hyTicketSubscribe.getHySupplierElement().getId());
				map.put("supplierName", hyTicketSubscribe.getHySupplierElement().getName());
			}		
			map.put("areaId", hyTicketSubscribe.getArea().getId());
			map.put("areaName", hyTicketSubscribe.getArea().getFullName());
			map.put("sceneAddress", hyTicketSubscribe.getSceneAddress());
			map.put("star", hyTicketSubscribe.getStar());
			map.put("openTime", hyTicketSubscribe.getOpenTime());
			map.put("closeTime", hyTicketSubscribe.getCloseTime());
			map.put("ticketExchangeAddress", hyTicketSubscribe.getTicketExchangeAddress());
			if(hyTicketSubscribe.getRestrictArea()!=null) {
				map.put("restrictId", hyTicketSubscribe.getRestrictArea().getId());
				map.put("restrictArea", hyTicketSubscribe.getRestrictArea().getFullName());
			}
			else {
				map.put("restrictId", null);
				map.put("restrictArea", null);
			}
			map.put("minPurchaseQuantity", hyTicketSubscribe.getMinPurchaseQuantity());
			map.put("days", hyTicketSubscribe.getDays()); //预约天数
			map.put("reserveTime", hyTicketSubscribe.getReserveTime()); //预约时间
			map.put("refundReq", hyTicketSubscribe.getRefundReq()); //退款说明
			map.put("reserveKnow", hyTicketSubscribe.getReserveKnow()); //预定须知
			map.put("introduction", hyTicketSubscribe.getIntroduction()); //产品介绍
			map.put("ticketFile",hyTicketSubscribe.getTicketFile()); //票务推广文件
			List<HyTicketSubscribePrice> prices=new ArrayList<>(hyTicketSubscribe.getHyTicketSubscribePrices());
			List<HashMap<String,Object>> priceList=new ArrayList<>();
			for(HyTicketSubscribePrice price:prices) {
				HashMap<String,Object> priceMap=new HashMap<>();
				priceMap.put("priceId",price.getId());
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate",price.getEndDate());
				priceMap.put("adultListPrice",price.getAdultListPrice());
				priceMap.put("adultOutsalePrice",price.getAdultOutsalePrice());
				priceMap.put("adultSettlePrice",price.getAdultSettlePrice());
				priceMap.put("childListPrice",price.getChildListPrice());
				priceMap.put("childOutPrice",price.getChildOutPrice());
				priceMap.put("childSettlePrice",price.getChildSettlePrice());
				priceMap.put("studentListPrice",price.getStudentListPrice());
				priceMap.put("studentOutsalePrice",price.getStudentOutsalePrice());
				priceMap.put("studentSettlePrice",price.getStudentSettlePrice());
				priceMap.put("oldListPrice",price.getOldListPrice());
				priceMap.put("oldOutsalePrice",price.getOldOutsalePrice());
				priceMap.put("oldSettlePrice",price.getOldSettlePrice());
				priceMap.put("inventory",price.getInventory());
				priceList.add(priceMap);
			}
			map.put("priceList", priceList);
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("supplierList/view")
	@ResponseBody
	public Json supplierList()
	{
		Json json=new Json();
		try{
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("supplierType", SupplierType.piaowuSubscribe)); //筛选旅游元素供应商
			List<HySupplierElement> piaowubuGongyingshangList=hySupplierElementService.findList(null,filters,null); 
			for(HySupplierElement gongyingshang:piaowubuGongyingshangList){
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("supplierId", gongyingshang.getId());
				map.put("supplierName", gongyingshang.getName());
				list.add(map);
			}
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(list);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancel(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			hyTicketSubscribe.setStatus(false);
			hyTicketSubscribeService.update(hyTicketSubscribe);
			json.setMsg("取消成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("restore")
	@ResponseBody
	public Json restore(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			hyTicketSubscribe.setStatus(true);
			hyTicketSubscribeService.update(hyTicketSubscribe);
			json.setMsg("恢复成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("oncarriage")
	@ResponseBody
	public Json oncarriage(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			hyTicketSubscribe.setSaleStatus(2); //上架
			hyTicketSubscribeService.update(hyTicketSubscribe);
			json.setMsg("上架成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("undercarriage")
	@ResponseBody
	public Json undercarriage(Long id)
	{
		Json json=new Json();
		try{
			HyTicketSubscribe hyTicketSubscribe=hyTicketSubscribeService.find(id);
			hyTicketSubscribe.setSaleStatus(3); //下架
			hyTicketSubscribeService.update(hyTicketSubscribe);
			json.setMsg("下架成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//查看实时库存
		@RequestMapping("inbound/view")
		@ResponseBody
		public Json inboundView(Long priceId)
		{
			Json json=new Json();
			try {
				List<Filter> filters=new ArrayList<>();
				if(priceId==null) {
					json.setSuccess(false);
					json.setMsg("传出参数有误");
					return json;
				}
				filters.add(Filter.eq("priceInboundId", priceId));
				filters.add(Filter.eq("type", 2)); //2-认购门票
				List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,filters,null);
				List<Map<String,Object>> list=new ArrayList<>();
				for(HyTicketInbound inbound:ticketInbounds) {
					Map<String,Object> map=new HashMap<>();
					map.put("day", inbound.getDay());
					map.put("inventory", inbound.getInventory());
					list.add(map);
				}
				json.setObj(list);
				json.setSuccess(true);
				json.setMsg("查询成功");
			}
			catch(Exception e) {
				json.setMsg(e.getMessage());
				json.setSuccess(false);
			}
			return json;
		}
		
		//内部类,用于修改库存传递参数
		static class WrapInbound{
		    Long priceId;
		    List<HyTicketInbound> hyTicketInbounds;
			public Long getPriceId() {
				return priceId;
			}
			public void setPriceId(Long priceId) {
				this.priceId = priceId;
			}
			public List<HyTicketInbound> getHyTicketInbounds() {
				return hyTicketInbounds;
			}
			public void setHyTicketInbounds(List<HyTicketInbound> hyTicketInbounds) {
				this.hyTicketInbounds = hyTicketInbounds;
			}
		}
		
		//只修改库存
		@RequestMapping("editInbound")
		@ResponseBody
		public Json editInbound(@RequestBody WrapInbound wrapInbound)
		{
			Json json=new Json();
			try {
				Long priceId=wrapInbound.getPriceId();
				List<HyTicketInbound> hyTicketInbounds=wrapInbound.getHyTicketInbounds();
				//将库存表针对每天的库存都修改
				for(HyTicketInbound inbound:hyTicketInbounds) {
					List<Filter> filters=new ArrayList<>();
					filters.add(Filter.eq("type", 2)); //2-认购门票
					filters.add(Filter.eq("priceInboundId", priceId));
					SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
					String dateString=formatter.format(inbound.getDay());
					Date inboundDay=formatter.parse(dateString);
					filters.add(Filter.eq("day", inboundDay));
					List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,filters,null);
					HyTicketInbound ticketInbound=ticketInbounds.get(0);
					ticketInbound.setInventory(inbound.getInventory());
					hyTicketInboundService.update(ticketInbound);
				}
				json.setSuccess(true);
				json.setMsg("修改成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
			return json;
		}
	
	public HyAdmin findPAdmin(HyAdmin admin) {
		HyAdmin hyAdmin=new HyAdmin();
		try {
			//如果是父帐号,即合同负责人
			if(admin.getHyAdmin()==null) {
				hyAdmin=admin;
			}
			//如果是子账号,查找其父帐号
			else {
				while(admin.getHyAdmin()!=null) {
					admin=admin.getHyAdmin();
				}
				hyAdmin=admin;
			}
		}
		catch(Exception e) {		
		    e.printStackTrace();
		}
		return hyAdmin;
	}
}
