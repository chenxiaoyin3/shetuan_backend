package com.hongyu.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import org.apache.commons.lang.time.DateUtils;
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
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/internTicket/scenic/")
public class HyTicketSceneController {
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
	
	@Resource(name="hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name="hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json list(Pageable pageable,String sceneName,String creatorName,HttpSession session,HttpServletRequest request)
	{		
		Json json=new Json();
		try{
			HyTicketScene hyTicketScene=new HyTicketScene();
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
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
			List<Filter> sceneFilter=new ArrayList<Filter>();
			sceneFilter.add(Filter.in("creator",hyAdmins));
			if(sceneName!=null&&!sceneName.equals(""))
			{
				sceneFilter.add(Filter.like("sceneName", sceneName));
			}
			List<Filter> filter=new ArrayList<Filter>();
			if(creatorName!=null&&!creatorName.equals(""))
			{
				filter.add(Filter.like("name",creatorName));
				List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
				if(adminList.size()==0){
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyTicketScene>());
				}
				else{
					sceneFilter.add(Filter.in("creator", adminList));
					List<Filter> supplierFilter=new ArrayList<Filter>();
					supplierFilter.add(Filter.eq("liable", findPAdmin(admin))); //帅选该登录账号负责的合同
					List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
					if(!hySupplierContracts.isEmpty()) {
						HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
						sceneFilter.add(Filter.eq("ticketSupplier", hySupplier));
					}			
					pageable.setFilters(sceneFilter);
					List<Order> orders = new ArrayList<Order>();
					orders.add(Order.desc("createTime"));
					pageable.setOrders(orders);
					Page<HyTicketScene> page=hyTicketSceneService.findPage(pageable,hyTicketScene);
					if(page.getTotal()>0){
						for(HyTicketScene scene:page.getRows()){
							HashMap<String,Object> sceneMap=new HashMap<String,Object>();
							HyAdmin creator=scene.getCreator();
							sceneMap.put("id", scene.getId());
							sceneMap.put("sceneName", scene.getSceneName());
							List<HyTicketSceneTicketManagement> ticketList =new ArrayList<>(scene.getHyTicketSceneTickets());
							//算出最近价格日期和最低价格
							if(ticketList.size()>0){
								List<Date> dateList=new ArrayList<>();
								List<BigDecimal> priceList=new ArrayList<>();
								for(HyTicketSceneTicketManagement ticket:ticketList){
									List<HyTicketPriceInbound> inboundPrices=new ArrayList<>(ticket.getHyTicketPriceInbounds());
									for(HyTicketPriceInbound inboundPrice:inboundPrices) {
										dateList.add(inboundPrice.getEndDate());
										//时间过滤时分秒
										Date date=DateUtils.truncate(new Date(), Calendar.DATE);
										if(inboundPrice.getEndDate().compareTo(date)>=0){
												priceList.add(inboundPrice.getSettlementPrice());									
										}
									}
								}
								if(!dateList.isEmpty()) {
									Date latestPriceDate=Collections.max(dateList);				
									sceneMap.put("latestPriceDate",latestPriceDate);
								}
								if(!priceList.isEmpty()) {
									BigDecimal lowestPrice=Collections.min(priceList);
									sceneMap.put("lowestPrice",lowestPrice);
								}
							}	
							sceneMap.put("createTime", scene.getCreateTime());
							sceneMap.put("creatorName", scene.getCreator().getName());
							
							//查找是否有上架产品,判断是否可编辑
							List<HyTicketSceneTicketManagement> sceneTickets=new ArrayList<>(scene.getHyTicketSceneTickets());
							int flag=1; //标志位
							for(HyTicketSceneTicketManagement sceneTicket:sceneTickets) {
								//如果有已上架产品
								if(sceneTicket.getSaleStatus()==2) {
									flag=0;
									break;
								}
							}
							sceneMap.put("isEdit",flag); //是否可编辑,1-可编辑,0-不可编辑
							
							/** 当前用户对本条数据的操作权限 */
						    if(creator.equals(admin)){
						    	if(co==CheckedOperation.view){
						    		sceneMap.put("privilege", "view");
						    	}
						    	else{
						    		sceneMap.put("privilege", "edit");
						    	}
						    }
						    else{
						    	if(co==CheckedOperation.edit){
						    		sceneMap.put("privilege", "edit");
						    	}
						    	else{
						    		sceneMap.put("privilege", "view");
						    	}
						    }
						    list.add(sceneMap);
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
			}
			//creatorName!=null
			else{
				List<Filter> supplierFilter=new ArrayList<Filter>();
				supplierFilter.add(Filter.eq("liable", admin)); //帅选该登录账号负责的合同
				List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
				if(!hySupplierContracts.isEmpty()) {
					HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
					sceneFilter.add(Filter.eq("ticketSupplier", hySupplier));
				}				
				pageable.setFilters(sceneFilter);
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc("createTime"));
				pageable.setOrders(orders);
				Page<HyTicketScene> page=hyTicketSceneService.findPage(pageable,hyTicketScene);
				if(page.getTotal()>0){
					for(HyTicketScene scene:page.getRows()){
						HashMap<String,Object> sceneMap=new HashMap<String,Object>();
						HyAdmin creator=scene.getCreator();
						sceneMap.put("id", scene.getId());
						sceneMap.put("sceneName", scene.getSceneName());
						List<HyTicketSceneTicketManagement> ticketList =new ArrayList<>(scene.getHyTicketSceneTickets());
						//算出最近价格日期和最低价格
						if(ticketList.size()>0){
							List<Date> dateList=new ArrayList<>();
							List<BigDecimal> priceList=new ArrayList<>();
							for(HyTicketSceneTicketManagement ticket:ticketList){
								List<HyTicketPriceInbound> inboundPrices=new ArrayList<>(ticket.getHyTicketPriceInbounds());
								for(HyTicketPriceInbound inboundPrice:inboundPrices) {
									dateList.add(inboundPrice.getEndDate());
									//时间过滤时分秒
									Date date=DateUtils.truncate(new Date(), Calendar.DATE);
									if(inboundPrice.getEndDate().compareTo(date)>=0){
											priceList.add(inboundPrice.getSettlementPrice());									
									}
								}
							}
							if(!dateList.isEmpty()) {
								Date latestPriceDate=Collections.max(dateList);				
								sceneMap.put("latestPriceDate",latestPriceDate);
							}
							
							if(!priceList.isEmpty()) {
								BigDecimal lowestPrice=Collections.min(priceList);
								sceneMap.put("lowestPrice",lowestPrice);
							}
						}
						sceneMap.put("createTime", scene.getCreateTime());
						sceneMap.put("creatorName", scene.getCreator().getName());
						
						//查找是否有上架产品,判断是否可编辑
						List<HyTicketSceneTicketManagement> sceneTickets=new ArrayList<>(scene.getHyTicketSceneTickets());
						int flag=1; //标志位
						for(HyTicketSceneTicketManagement sceneTicket:sceneTickets) {
							//如果有已上架产品
							if(sceneTicket.getSaleStatus()==1) {
								flag=0;
								break;
							}
						}
						sceneMap.put("isEdit",flag); //是否可编辑,1-可编辑,0-不可编辑
						
						/** 当前用户对本条数据的操作权限 */
					    if(creator.equals(admin)){
					    	if(co==CheckedOperation.view){
					    		sceneMap.put("privilege", "view");
					    	}
					    	else{
					    		sceneMap.put("privilege", "edit");
					    	}
					    }
					    else{
					    	if(co==CheckedOperation.edit){
					    		sceneMap.put("privilege", "edit");
					    	}
					    	else{
					    		sceneMap.put("privilege", "view");
					    	}
					    }
					    list.add(sceneMap);
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
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyTicketScene hyTicketScene,Long areaId,Long supplierId,HttpSession session)
	{
		Json json=new Json();
		try{
			HyArea hyArea=hyAreaService.find(areaId);
			hyTicketScene.setArea(hyArea);
			if(supplierId!=null) {
				HySupplierElement piaowubuGongyingshang=hySupplierElementService.find(supplierId);
				hyTicketScene.setHySupplierElement(piaowubuGongyingshang);
			}			
			hyTicketScene.setCreateTime(new Date());
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			Boolean flag = false;
			//新增只有供应商合同为正常才可以新建门票
			if(admin.getHyAdmin() != null) {
				HyAdmin parent = admin.getHyAdmin();
				Set<HySupplierContract> supplierContracts = parent.getLiableContracts();
				for(HySupplierContract c : supplierContracts) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						flag = true;
						break;
					}
				}
			} else {
				Set<HySupplierContract> supplierContracts1 = admin.getLiableContracts();
				for(HySupplierContract c : supplierContracts1) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						flag = true;
						break;
					}
				}
			}
			if(flag == false) {
				json.setMsg("供应商合同状态错误");
				json.setSuccess(false);
				return json;
			}
			
			
			hyTicketScene.setCreator(admin);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyTicketScene.setTicketSupplier(hySupplier);
			} 	
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowujingquPn));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="JQ-" + dateStr + "-" + String.format("%04d", value);
			}
			hyTicketScene.setPn(produc);
			hyTicketScene.setMhState(0); //将门户完善状态设为未完善
			hyTicketScene.setMhIntroduction(hyTicketScene.getIntroduction());//初始化门户推广文件
			hyTicketSceneService.save(hyTicketScene);
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="edit")
	@ResponseBody
	public Json edit(HyTicketScene hyTicketScene,Long areaId,Long supplierId)
	{
		Json json=new Json();
		try{
			HyArea hyArea=hyAreaService.find(areaId);
			hyTicketScene.setArea(hyArea);
			HyTicketScene preScene=hyTicketSceneService.find(hyTicketScene.getId());
			if(preScene.getMhState()!=null) {
				if(preScene.getMhState()==0) {
					hyTicketScene.setMhState(preScene.getMhState());
					hyTicketScene.setMhIntroduction(hyTicketScene.getIntroduction());
				}
				else if(preScene.getMhState()==1) {
					hyTicketScene.setMhState(2); //将门户完善状态改为供应商修改,待完善
					hyTicketScene.setMhIntroduction(preScene.getMhIntroduction());
				}
				
				else {
					hyTicketScene.setMhState(preScene.getMhState());
					hyTicketScene.setMhIntroduction(preScene.getMhIntroduction());
				}
			}
			else {
				hyTicketScene.setMhState(preScene.getMhState());
				hyTicketScene.setMhIntroduction(hyTicketScene.getIntroduction());
			}
			if(supplierId!=null) {
				HySupplierElement piaowubuGongyingshang=hySupplierElementService.find(supplierId);
				hyTicketScene.setHySupplierElement(piaowubuGongyingshang);
			}		
			hyTicketScene.setModifyTime(new Date());
			hyTicketSceneService.update(hyTicketScene,"ticketSupplier","createTime","creator","pn","mhReserveReq",
					"mhSceneName","mhSceneAddress","mhBriefIntroduction","mhCreateTime","mhUpdateTime",
					"mhOperator");
			json.setMsg("编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view",method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyTicketScene hyTicketScene=hyTicketSceneService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("sceneName",hyTicketScene.getSceneName());
			map.put("area", hyTicketScene.getArea().getFullName());
			map.put("areaId", hyTicketScene.getArea().getId());
			if(hyTicketScene.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", hyTicketScene.getHySupplierElement().getName());
				map.put("supplierId", hyTicketScene.getHySupplierElement().getId());
			}		
			map.put("sceneAddress", hyTicketScene.getSceneAddress());
			map.put("star", hyTicketScene.getStar());
			map.put("openTime", hyTicketScene.getOpenTime());
			map.put("closeTime", hyTicketScene.getCloseTime());
			map.put("ticketExchangeAddress", hyTicketScene.getTicketExchangeAddress());
			map.put("introduction", hyTicketScene.getIntroduction()); //产品介绍
			map.put("ticketFile",hyTicketScene.getTicketFile()); //票务推广文件
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
			filters.add(Filter.eq("supplierType", SupplierType.piaowuTicket)); //筛选旅游元素供应商
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
	
	@RequestMapping(value="ticketList/view")
	@ResponseBody
	public Json ticketList(HyTicketSceneTicketManagement queryParam,Long sceneId,Pageable pageable)
	{
		Json json=new Json();
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			HyTicketScene hyTicketScene=hyTicketSceneService.find(sceneId);
			List<Filter> filter=new ArrayList<Filter>();
			filter.add(Filter.eq("hyTicketScene", hyTicketScene));
			pageable.setFilters(filter);
			Page<HyTicketSceneTicketManagement> page=hyTicketSceneTicketManagementService.findPage(pageable,queryParam);
			if(page.getTotal()>0){
				for(HyTicketSceneTicketManagement sceneTicket:page.getRows()){
					HashMap<String,Object> ticketMap=new HashMap<String,Object>();
					ticketMap.put("id", sceneTicket.getId());
					ticketMap.put("productId", sceneTicket.getProductId());
					ticketMap.put("productName", sceneTicket.getProductName());
					ticketMap.put("auditStatus", sceneTicket.getAuditStatus());
					ticketMap.put("saleStatus", sceneTicket.getSaleStatus());
					ticketMap.put("status", sceneTicket.getStatus());
					list.add(ticketMap);
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
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/*新建一个内部类，传递参数*/
	static class WrapHyTicketScene{
		private Long sceneId;
		private HyTicketSceneTicketManagement sceneTicket;
		public Long getSceneId() {
			return sceneId;
		}
		public void setSceneId(Long sceneId) {
			this.sceneId = sceneId;
		}
		public HyTicketSceneTicketManagement getSceneTicket() {
			return sceneTicket;
		}
		public void setSceneTicket(HyTicketSceneTicketManagement sceneTicket) {
			this.sceneTicket = sceneTicket;
		}
	}
	
	@RequestMapping(value="addTicket", method = RequestMethod.POST)
	@ResponseBody
	public Json addTicket(@RequestBody WrapHyTicketScene wrapHyTicketScene,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			Long sceneId=wrapHyTicketScene.getSceneId();
			HyTicketScene hyTicketScene=hyTicketSceneService.find(sceneId);
			HyTicketSceneTicketManagement sceneTicket=wrapHyTicketScene.getSceneTicket();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
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
			for(HyTicketPriceInbound priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("sceneticketPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			sceneTicket.setHyTicketScene(hyTicketScene);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowubump));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MP-" + dateStr + "-" + String.format("%04d", value);
			}
			sceneTicket.setProductId(produc);
			sceneTicket.setStatus(true);
			sceneTicket.setSaleStatus(1); //未上架
			sceneTicket.setAuditStatus(2); //提交审核
			sceneTicket.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			sceneTicket.setOperator(admin);
			sceneTicket.setSubmitTime(new Date());
			//如果预约时间为空,则设为0
			if(sceneTicket.getDays()==null) {
				sceneTicket.setDays(0);
			}
			if(sceneTicket.getTimes()==null) {
				sceneTicket.setTimes(0);
			}
			hyTicketSceneTicketManagementService.save(sceneTicket);
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(sceneTicket);
					hyTicketPriceInboundService.save(price);		
				}
			}
			// 完成 门票价格提交申请
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
	
	@RequestMapping(value="saveTicket", method = RequestMethod.POST)
	@ResponseBody
	public Json saveTicket(@RequestBody WrapHyTicketScene wrapHyTicketScene,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketScene hyTicketScene=hyTicketSceneService.find(wrapHyTicketScene.getSceneId());
			HyTicketSceneTicketManagement sceneTicket=wrapHyTicketScene.getSceneTicket();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
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
			for(HyTicketPriceInbound priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			sceneTicket.setHyTicketScene(hyTicketScene);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowubump));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MP-" + dateStr + "-" + String.format("%04d", value);
			}
			sceneTicket.setProductId(produc);
			sceneTicket.setStatus(true);
			sceneTicket.setSaleStatus(1); //未上架
			sceneTicket.setAuditStatus(1); //保存未提交
			//如果预约时间为空,则设为0
			if(sceneTicket.getDays()==null) {
				sceneTicket.setDays(0);
			}
			if(sceneTicket.getTimes()==null) {
				sceneTicket.setTimes(0);
			}
			hyTicketSceneTicketManagementService.save(sceneTicket);

			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(sceneTicket);
					hyTicketPriceInboundService.save(price);		
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
	
	@RequestMapping("submitPrice")
	@ResponseBody
	public Json submitPrice(Long ticketId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement hyTicketSceneTicket=hyTicketSceneTicketManagementService.find(ticketId);
			hyTicketSceneTicket.setAuditStatus(2); //提交审核
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("sceneticketPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门票价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyTicketSceneTicket.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketSceneTicket.setOperator(admin);
			hyTicketSceneTicket.setSubmitTime(new Date());
			hyTicketSceneTicket.setStatus(true);
			hyTicketSceneTicketManagementService.update(hyTicketSceneTicket);
			json.setMsg("提交成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editTicket/submit")
	@ResponseBody
	public Json editsubmitTicket(@RequestBody HyTicketSceneTicketManagement ticket,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement sceneTicket=hyTicketSceneTicketManagementService.find(ticket.getId());
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
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
			for(HyTicketPriceInbound priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("sceneticketPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门票价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			ticket.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			ticket.setAuditStatus(2); //提交审核
			ticket.setSaleStatus(1); //未上架
			ticket.setStatus(true);
			ticket.setOperator(admin);
			ticket.setSubmitTime(new Date());
			//如果预约时间为空,则设为0
			if(ticket.getDays()==null) {
				ticket.setDays(0);
			}
			if(ticket.getTimes()==null) {
				ticket.setTimes(0);
			}
			hyTicketSceneTicketManagementService.update(ticket,"productId","hyTicketScene","mhProductName","mhReserveReq",
					"mhRefundReq","mhIsSale");
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(ticket);
					hyTicketPriceInboundService.save(price);		
				}
			}
			json.setMsg("编辑成功");
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
	public Json editsaveTicket(@RequestBody HyTicketSceneTicketManagement ticket,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement sceneTicket=hyTicketSceneTicketManagementService.find(ticket.getId());
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
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
			for(HyTicketPriceInbound priceInbound:priceList) {
				//如果上产品的结束日期比合同到期日大,抛异常
				if(priceInbound.getEndDate().compareTo(expiration)>0) {
					json.setSuccess(false);
					json.setMsg("产品日期超出合同期限");
					return json;
				}
			}
			ticket.setAuditStatus(1); //未提交
			ticket.setSaleStatus(1); //未上架
			ticket.setStatus(true);
			//如果预约时间为空,则设为0
			if(ticket.getDays()==null) {
				ticket.setDays(0);
			}
			if(ticket.getTimes()==null) {
				ticket.setTimes(0);
			}
			hyTicketSceneTicketManagementService.update(ticket,"productId","hyTicketScene","mhProductName","mhReserveReq",
					"mhRefundReq","mhIsSale");
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(ticket);
					hyTicketPriceInboundService.save(price);		
				}
			}
			json.setMsg("编辑成功");
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
	public Json editsumitPrice(@RequestBody HyTicketSceneTicketManagement ticket,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("sceneticketPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门票价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			ticket.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			ticket.setAuditStatus(2); //提交审核
			ticket.setSaleStatus(1); //未上架
			ticket.setStatus(true);
			ticket.setOperator(admin);
			ticket.setSubmitTime(new Date());
			hyTicketSceneTicketManagementService.update(ticket,"productId","productName","hyTicketScene","ticketType","isReserve",
					"days","times","isRealName","refundReq","realNameRemark","reserveReq","productType","mhProductName","mhReserveReq",
					"mhRefundReq","mhIsSale");
			HyTicketSceneTicketManagement sceneTicket=hyTicketSceneTicketManagementService.find(ticket.getId());
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(ticket.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(ticket);
					hyTicketPriceInboundService.save(price);		
				}
			}
			json.setMsg("价格编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editPrice/save")
	@ResponseBody
	public Json editsavePrice(@RequestBody HyTicketSceneTicketManagement ticket)
	{
		Json json=new Json();
		try{
			ticket.setAuditStatus(1); //未提交
			ticket.setSaleStatus(1); //未上架
			ticket.setStatus(true);
			hyTicketSceneTicketManagementService.update(ticket,"productId","productName","hyTicketScene","ticketType","isReserve",
					"days","times","isRealName","refundReq","realNameRemark","reserveReq","productType","mhProductName","mhReserveReq",
					"mhRefundReq","mhIsSale");
			HyTicketSceneTicketManagement sceneTicket=hyTicketSceneTicketManagementService.find(ticket.getId());
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(ticket.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketSceneTicketManagement(ticket);
					hyTicketPriceInboundService.save(price);		
				}
			}
			json.setMsg("价格编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("price/detail/view")
	@ResponseBody
	public Json priceDetail(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticketScene=hyTicketSceneTicketManagementService.find(ticketId);
			String processInstanceId = ticketScene.getProcessInstanceId();
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
			map.put("productId", ticketScene.getProductId());
			map.put("productName", ticketScene.getProductName());
			map.put("ticketType", ticketScene.getTicketType());
			map.put("isReserve", ticketScene.getIsReserve());
			if(ticketScene.getIsReserve()==true){
				map.put("days", ticketScene.getDays());
				map.put("times", ticketScene.getTimes());
			}
			map.put("isRealName", ticketScene.getIsRealName());
			if(ticketScene.getIsRealName()==true){
				map.put("realNameRemark", ticketScene.getRealNameRemark());
			}
			map.put("refundReq", ticketScene.getRefundReq());
			map.put("reserveReq", ticketScene.getReserveReq());
			List<HyTicketPriceInbound> priceList=new ArrayList<>(ticketScene.getHyTicketPriceInbounds());
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
	
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancel(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setStatus(false);
			hyTicketSceneTicketManagementService.update(ticket);
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
	public Json restore(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setStatus(true);
			hyTicketSceneTicketManagementService.update(ticket);
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
	public Json oncarriage(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setSaleStatus(2); //上架
			hyTicketSceneTicketManagementService.update(ticket);
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
	public Json undercarriage(Long ticketId)
	{
		Json json=new Json();
		try{
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setSaleStatus(3); //下架
			ticket.setMhIsSale(0); //门户同步显下架
			hyTicketSceneTicketManagementService.update(ticket);
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
				filters.add(Filter.eq("type", 1)); //1-酒店,门票,酒加景
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
					filters.add(Filter.eq("type", 1));
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
