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
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCountry;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.entity.HyVisaPrices;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCountryService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyVisaPicService;
import com.hongyu.service.HyVisaPricesService;
import com.hongyu.service.HyVisaService;
import com.hongyu.util.AuthorityUtils;


@Controller
@RequestMapping("/admin/ticket/visa/")
public class HyTicketVisaController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyCountryServiceImpl")
	private HyCountryService hyCountryService;
	
	@Resource(name="hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name="hyVisaPricesServiceImpl")
	private HyVisaPricesService hyVisaPricesService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hyVisaPicServiceImpl")
	private HyVisaPicService hyVisaPicService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyVisa queryParam,HttpSession session,HttpServletRequest request)
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
			supplierFilter.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				filters.add(Filter.eq("ticketSupplier", hySupplier));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyVisa> page=hyVisaService.findPage(pageable, queryParam);
			if(page.getTotal()>0) {
				for(HyVisa hyVisa:page.getRows()) {
					HashMap<String,Object> visaMap=new HashMap<String,Object>();
					HyAdmin creator=hyVisa.getCreator();
					visaMap.put("id",hyVisa.getId());
					visaMap.put("productId",hyVisa.getProductId()); //productId
					visaMap.put("productName", hyVisa.getProductName());
					visaMap.put("createTime",hyVisa.getCreateTime());
					visaMap.put("creator",hyVisa.getCreator().getName());
					visaMap.put("auditStatus", hyVisa.getAuditStatus());
					visaMap.put("saleStatus",hyVisa.getSaleStatus());	
					visaMap.put("status", hyVisa.getStatus());
					/** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		visaMap.put("privilege", "view");
				    	}
				    	else{
				    		visaMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		visaMap.put("privilege", "edit");
				    	}
				    	else{
				    		visaMap.put("privilege", "view");
				    	}
				    }
					list.add(visaMap);
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
	
	/*新建一个包装类,传参数*/
	static class WrapHyVisa{
		private HyVisa hyVisa;
		private Long countryId;
		public HyVisa getHyVisa() {
			return hyVisa;
		}
		public void setHyVisa(HyVisa hyVisa) {
			this.hyVisa = hyVisa;
		}
		public Long getCountryId() {
			return countryId;
		}
		public void setCountryId(Long countryId) {
			this.countryId = countryId;
		}
	}
	
	//保存并提交
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyVisa wraphyVisa,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyVisa hyVisa=wraphyVisa.getHyVisa();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyVisaPrices> priceList=hyVisa.getHyVisaPrices();
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
			for(HyVisaPrices priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			
			Long countryId=wraphyVisa.getCountryId();
			HyCountry country=hyCountryService.find(countryId);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("visaPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowuqianzheng));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="QZ-" + dateStr + "-" + String.format("%04d", value);
			}
			hyVisa.setProductId(produc);
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyVisa.setTicketSupplier(hySupplier);
			}		
			hyVisa.setStatus(true);
			hyVisa.setCreator(admin);
			hyVisa.setSubmitter(admin);
			hyVisa.setCountry(country);
			hyVisa.setSaleStatus(1); //未上架
			hyVisa.setAuditStatus(2); //提交审核
			hyVisa.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyVisa.setSubmitTime(new Date());
			hyVisa.setCreateTime(new Date());
			hyVisaService.save(hyVisa);
			
//			//签证材料上传图片提交
//			List<HyVisaPic> picList=hyVisa.getHyVisaPics();
//			if(picList.size()>0) {
//				for(HyVisaPic visaPic:picList) {
//					visaPic.setHyVisa(hyVisa);
//					hyVisaPicService.save(visaPic);
//				}
//			}
			
			//签证价格提交
			if(priceList.size()>0){ 
				for(HyVisaPrices price:priceList){
					price.setHyVisa(hyVisa);
					hyVisaPricesService.save(price);		
				}
			}
			// 完成签证价格提交申请
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
	
	//只保存不提交
	@RequestMapping(value="save", method = RequestMethod.POST)
	@ResponseBody
	public Json save(@RequestBody WrapHyVisa wraphyVisa,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyVisa hyVisa=wraphyVisa.getHyVisa();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyVisaPrices> priceList=hyVisa.getHyVisaPrices();
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
			for(HyVisaPrices priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			
			Long countryId=wraphyVisa.getCountryId();
			HyCountry country=hyCountryService.find(countryId);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowuqianzheng));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="QZ-" + dateStr + "-" + String.format("%04d", value);
			}
			hyVisa.setProductId(produc);
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyVisa.setTicketSupplier(hySupplier);
			}	
			hyVisa.setStatus(true);
			hyVisa.setCreator(admin);
			hyVisa.setCountry(country);
			hyVisa.setCreateTime(new Date());
			hyVisa.setSaleStatus(1); //未上架
			hyVisa.setAuditStatus(1); //未提交审核
			hyVisaService.save(hyVisa);
			
//			//签证材料上传图片提交
//			List<HyVisaPic> picList=hyVisa.getHyVisaPics();
//			if(picList.size()>0) {
//				for(HyVisaPic visaPic:picList) {
//					visaPic.setHyVisa(hyVisa);
//					hyVisaPicService.save(visaPic);
//				}
//			}
			
			//签证价格提交
			if(priceList.size()>0){ 
				for(HyVisaPrices price:priceList){
					price.setHyVisa(hyVisa);
					hyVisaPricesService.save(price);		
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
			HyVisa hyVisa=hyVisaService.find(id);
			hyVisa.setAuditStatus(2); //提交审核
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin=hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("visaPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			hyVisa.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyVisa.setSubmitTime(new Date());
			hyVisa.setSubmitter(admin);
			hyVisa.setStatus(true);
			hyVisaService.update(hyVisa);
			// 完成 酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
		    taskService.complete(task.getId());
			json.setMsg("提交成功");
		    json.setSuccess(true);    
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editPrice/submit")
	@ResponseBody
	public Json editPricesubmit(@RequestBody WrapHyVisa wraphyVisa,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			Long id=wraphyVisa.getHyVisa().getId();
			Long countryId=wraphyVisa.getCountryId();
			HyVisa hyVisa=wraphyVisa.getHyVisa();
			HyCountry country=hyCountryService.find(countryId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("visaPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			hyVisa.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyVisa.setStatus(true);
			hyVisa.setCountry(country);
			hyVisa.setSaleStatus(1); //未上架
			hyVisa.setAuditStatus(2); //提交审核		
			hyVisa.setSubmitTime(new Date());
			hyVisa.setSubmitter(admin);
			hyVisaService.update(hyVisa,"ticketSupplier","productId","creator","createTime","mhState","mhProductName","mhPriceContain","mhReserveRequirement",
					"mhAcceptScope","mhAttention","mhIsSale","mhCreateTime","mhUpdateTime","mhOperator","mhIsHot");
			HyVisa visa=hyVisaService.find(id);
			List<HyVisaPrices> prices=visa.getHyVisaPrices(); //修改前价格列表
//			List<HyVisaPic> pics=visa.getHyVisaPics(); //修改以前的签证材料图片
			
			//将以前的价格删除
			for(HyVisaPrices price:prices) {
				hyVisaPricesService.delete(price); //将以前的价格删除
			}
			
//			//将以前的签证材料删除
//			for(HyVisaPic pic:pics) {
//				hyVisaPicService.delete(pic);
//			}
			
			//保存编辑后的新价格
			List<HyVisaPrices> priceList=hyVisa.getHyVisaPrices();
			if(priceList.size()>0){ 
				for(HyVisaPrices price:priceList){
					price.setHyVisa(hyVisa);
					hyVisaPricesService.save(price); 		
				}
			}
			
//			//保存编辑后的新的签证材料图片
//			List<HyVisaPic> visaPics=hyVisa.getHyVisaPics();
//			for(HyVisaPic visaPic:visaPics) {
//				visaPic.setHyVisa(hyVisa);
//				hyVisaPicService.save(visaPic);
//			}		
			
			// 完成签证价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setMsg("编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="editPrice/save", method = RequestMethod.POST)
	@ResponseBody
	public Json editPricesave(@RequestBody WrapHyVisa wraphyVisa)
	{
		Json json=new Json();	
		try{
			Long id=wraphyVisa.getHyVisa().getId();
			Long countryId=wraphyVisa.getCountryId();
			HyVisa hyVisa=wraphyVisa.getHyVisa();
			HyCountry country=hyCountryService.find(countryId);
			hyVisa.setStatus(true);
			hyVisa.setCountry(country);
			hyVisa.setSaleStatus(1); //未上架
			hyVisa.setAuditStatus(1); //未提交审核
			
			hyVisa.setSubmitTime(new Date());
			hyVisaService.update(hyVisa,"ticketSupplier","productId","creator","createTime");
			HyVisa visa=hyVisaService.find(id);
			List<HyVisaPrices> prices=visa.getHyVisaPrices(); //修改前价格列表
//			List<HyVisaPic> pics=visa.getHyVisaPics(); //修改以前的签证材料图片
			
			//将以前的价格删除
			for(HyVisaPrices price:prices) {
				hyVisaPricesService.delete(price); //将以前的价格删除
			}
			
//			//将以前的签证材料删除
//			for(HyVisaPic pic:pics) {
//				hyVisaPicService.delete(pic);
//			}
			
			//保存编辑后的新价格
			List<HyVisaPrices> priceList=hyVisa.getHyVisaPrices();
			if(priceList.size()>0){ 
				for(HyVisaPrices price:priceList){
					price.setHyVisa(hyVisa);
					hyVisaPricesService.save(price); 		
				}
			}
			
//			//保存编辑后的新的签证材料图片
//			List<HyVisaPic> visaPics=hyVisa.getHyVisaPics();
//			for(HyVisaPic visaPic:visaPics) {
//				visaPic.setHyVisa(hyVisa);
//				hyVisaPicService.save(visaPic);
//			}		
			json.setMsg("编辑成功");
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
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyVisa hyVisa=hyVisaService.find(id);
			String processInstanceId = hyVisa.getProcessInstanceId();
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
			map.put("productId", hyVisa.getProductId());
			map.put("productName", hyVisa.getProductName());
			map.put("creator", hyVisa.getCreator().getName());
			map.put("countryId",hyVisa.getCountry().getId());
			String continent=hyVisa.getCountry().getContinent();
			if(continent.equals("北美洲")) {
				map.put("continentNumber",1);
			}
			else if(continent.equals("大洋洲")) {
				map.put("continentNumber",2);
			}
			else if(continent.equals("非洲")) {
				map.put("continentNumber",3);
			}
			else if(continent.equals("南美洲")) {
				map.put("continentNumber",4);
			}
			else if(continent.equals("欧洲")) {
				map.put("continentNumber",5);
			}
			else if(continent.equals("亚洲")) {
				map.put("continentNumber",6);
			}
			map.put("country", hyVisa.getCountry().getName());
			map.put("visaType", hyVisa.getVisaType());
			map.put("duration", hyVisa.getDuration());
			map.put("times", hyVisa.getTimes());
			map.put("isInterview", hyVisa.getIsInterview());
			map.put("stayDays", hyVisa.getStayDays());
			map.put("expireDays", hyVisa.getExpireDays());
			map.put("serviceContent", hyVisa.getServiceContent());
			map.put("priceContain", hyVisa.getPriceContain());
			map.put("reserveRequirement", hyVisa.getReserveRequirement());
			map.put("accessory", hyVisa.getAccessory()); //附件
			map.put("introduce",hyVisa.getIntroduce()); //签证说明文字
			map.put("introduction", hyVisa.getIntroduction()); //产品介绍
			map.put("ticketFile",hyVisa.getTicketFile()); //票务推广文件
//			List<HyVisaPic> pics= hyVisa.getHyVisaPics();
//			List<HashMap<String,Object>> picList=new ArrayList<>();
//			for(HyVisaPic visaPic:pics) {
//				HashMap<String,Object> picMap=new HashMap<String,Object>();
//				picMap.put("source", visaPic.getSource());
//				picMap.put("large",visaPic.getLarge());
//				picMap.put("medium",visaPic.getMedium());
//				picMap.put("thumbnail", visaPic.getThumbnail());
//				picList.add(picMap);
//			}
//			map.put("hyVisaPics",picList );
			List<HyVisaPrices> prices=hyVisa.getHyVisaPrices();
			List<HashMap<String,Object>> priceList=new ArrayList<>();
			for(HyVisaPrices price:prices) {
				HashMap<String,Object> priceMap=new HashMap<String,Object>();
				priceMap.put("startDate", price.getStartDate());
				priceMap.put("endDate",price.getEndDate());
				priceMap.put("displayPrice",price.getDisplayPrice());
				priceMap.put("sellPrice",price.getSellPrice());
				priceMap.put("settlementPrice",price.getSettlementPrice());
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
	
//	@RequestMapping("supplierList/view")
//	@ResponseBody
//	public Json supplierList()
//	{
//		Json json=new Json();
//		try{
//			List<HashMap<String, Object>> list = new ArrayList<>();
//			List<Filter> filters=new ArrayList<Filter>();
//			filters.add(Filter.eq("supplierType", SupplierType.piaowuVisa)); //筛选旅游元素供应商
//			List<HySupplierElement> piaowubuGongyingshangList=hySupplierElementService.findList(null,filters,null); 
//			for(HySupplierElement gongyingshang:piaowubuGongyingshangList){
//				HashMap<String,Object> map=new HashMap<String,Object>();
//				map.put("supplierId", gongyingshang.getId());
//				map.put("supplierName", gongyingshang.getName());
//				list.add(map);
//			}
//			json.setMsg("列表成功");
//		    json.setSuccess(true);
//		    json.setObj(list);
//		}
//		catch(Exception e){
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
	
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancel(Long id)
	{
		Json json=new Json();
		try{
			HyVisa hyVisa=hyVisaService.find(id);
			hyVisa.setStatus(false);
			hyVisaService.update(hyVisa);
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
			HyVisa hyVisa=hyVisaService.find(id);
			hyVisa.setStatus(true);
			hyVisaService.update(hyVisa);
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
			HyVisa hyVisa=hyVisaService.find(id);
			hyVisa.setSaleStatus(2); //上架
			hyVisaService.update(hyVisa);
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
			HyVisa hyVisa=hyVisaService.find(id);
			hyVisa.setSaleStatus(3); //下架
			hyVisa.setMhIsSale(0); //门户同步下线
			hyVisaService.update(hyVisa);
			json.setMsg("下架成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 得到国外国家信息
	 * @return
	 */
	//1-北美洲，2-大洋洲，3-非洲，4-南美洲，5-欧洲，6-亚洲

	@RequestMapping(value="country/view")
	@ResponseBody
	public Json getCountry(Integer continentNumber) {
		Json j = new Json();
		try {
			String continent=null;
			if(continentNumber==1) {
				continent="北美洲";
			}
			else if(continentNumber==2) {
				continent="大洋洲";
			}
			else if(continentNumber==3) {
				continent="非洲";
			}
			else if(continentNumber==4) {
				continent="南美洲";
			}
			else if(continentNumber==5) {
				continent="欧洲";
			}
			else if(continentNumber==6) {
				continent="亚洲";
			}
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("continent", continent));
			List<HyCountry> countrys = hyCountryService.findList(null, filters, null);
			List<HashMap<String,Object>> list=new ArrayList<>();		
			for(HyCountry country:countrys) {
				HashMap<String,Object> map=new HashMap<>();
				map.put("countryName",country.getName());
				map.put("countryId", country.getId());
				list.add(map);
			}
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
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
