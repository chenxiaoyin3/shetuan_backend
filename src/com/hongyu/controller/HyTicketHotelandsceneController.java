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
import org.springframework.format.annotation.DateTimeFormat;
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
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketRefundService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/internTicket/jiujiajing/")
public class HyTicketHotelandsceneController {
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
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketHotelandsceneRoomServiceImpl")
	private HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hyTicketRefundServiceImpl")
	private HyTicketRefundService hyTicketRefundService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(HyTicketHotelandscene queryParam,String startDate,
			String endDate,Pageable pageable,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
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
			List<Filter> filter=new ArrayList<Filter>();
			filter.add(Filter.in("creator",hyAdmins));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(startDate != null) {
				filter.add(Filter.ge("createTime", sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
			}
			if(endDate != null) {
				filter.add(Filter.le("createTime", sdf.parse(endDate.substring(0, 10) + " " + "23:59:59")));
			}
			List<Filter> supplierFilter=new ArrayList<Filter>();
			supplierFilter.add(Filter.eq("liable", findPAdmin(admin))); //帅选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				filter.add(Filter.eq("ticketSupplier", hySupplier));	
			}		
			pageable.setFilters(filter);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyTicketHotelandscene> page=hyTicketHotelandsceneService.findPage(pageable,queryParam);
			if(page.getTotal()>0){
				for(HyTicketHotelandscene hotelandscene:page.getRows()){
					HashMap<String,Object> hotelMap=new HashMap<String,Object>();
					HyAdmin creator=hotelandscene.getCreator();
					hotelMap.put("id", hotelandscene.getId());
					hotelMap.put("productId", hotelandscene.getProductId());
					hotelMap.put("productName",hotelandscene.getProductName());
					hotelMap.put("createTime", hotelandscene.getCreateTime());
					hotelMap.put("creator", hotelandscene.getCreator().getName());
					List<HyTicketHotelandsceneRoom> roomList =new ArrayList<>(hotelandscene.getHyTicketHotelandsceneRooms());
					//算出最近价格日期和最低价格
					if(roomList.size()>0){
						List<Date> dateList=new ArrayList<>();
						List<BigDecimal> priceList=new ArrayList<>();
						for(HyTicketHotelandsceneRoom room:roomList){
							List<HyTicketPriceInbound> inboundPrices=new ArrayList<>(room.getHyTicketPriceInbounds());
							for(HyTicketPriceInbound inboundPrice:inboundPrices) {
								dateList.add(inboundPrice.getEndDate());
								Date date=DateUtils.truncate(new Date(), Calendar.DATE);
								if(inboundPrice.getEndDate().compareTo(date)>=0){
										priceList.add(inboundPrice.getSettlementPrice());									
								}
							}
						}
						if(!dateList.isEmpty()) {
							Date latestPriceDate=Collections.max(dateList);				
							hotelMap.put("latestPriceDate",latestPriceDate);
						}
						if(!priceList.isEmpty()) {
							BigDecimal lowestPrice=Collections.min(priceList);
							hotelMap.put("lowestPrice",lowestPrice);
						}	
					}	
					
					//查找是否有上架产品,判断是否可编辑
					List<HyTicketHotelandsceneRoom> hotelandsceneRooms=new ArrayList<>(hotelandscene.getHyTicketHotelandsceneRooms());
					int flag=1; //标志位
					for(HyTicketHotelandsceneRoom room:hotelandsceneRooms) {
						//如果有已上架产品
						if(room.getSaleStatus()==2) {
							flag=0;
							break;
						}
					}
					hotelMap.put("isEdit",flag); //是否可编辑,1-可编辑,0-不可编辑
					
					/** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		hotelMap.put("privilege", "view");
				    	}
				    	else{
				    		hotelMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		hotelMap.put("privilege", "edit");
				    	}
				    	else{
				    		hotelMap.put("privilege", "view");
				    	}
				    }
				    list.add(hotelMap);
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
	
	static class WrapHyTicketHotelandscene{
		private HyTicketHotelandscene hyTicketHotelandscene;
		private Long supplierId;
		private Long sceneAreaId;
		private Long hotelAreaId;
		private String sceneOpenTime;
		private String sceneCloseTime;
		private List<HyTicketRefund> hyTicketRefunds=new ArrayList<>();
		
		public HyTicketHotelandscene getHyTicketHotelandscene() {
			return hyTicketHotelandscene;
		}
		public void setHyTicketHotelandscene(HyTicketHotelandscene hyTicketHotelandscene) {
			this.hyTicketHotelandscene = hyTicketHotelandscene;
		}
		public Long getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(Long supplierId) {
			this.supplierId = supplierId;
		}
		public Long getSceneAreaId() {
			return sceneAreaId;
		}
		public void setSceneAreaId(Long sceneAreaId) {
			this.sceneAreaId = sceneAreaId;
		}
		public Long getHotelAreaId() {
			return hotelAreaId;
		}
		public void setHotelAreaId(Long hotelAreaId) {
			this.hotelAreaId = hotelAreaId;
		}
		public String getSceneOpenTime() {
			return sceneOpenTime;
		}
		public void setSceneOpenTime(String sceneOpenTime) {
			this.sceneOpenTime = sceneOpenTime;
		}
		public String getSceneCloseTime() {
			return sceneCloseTime;
		}
		public void setSceneCloseTime(String sceneCloseTime) {
			this.sceneCloseTime = sceneCloseTime;
		}
		public List<HyTicketRefund> getHyTicketRefunds() {
			return hyTicketRefunds;
		}
		public void setHyTicketRefunds(List<HyTicketRefund> hyTicketRefunds) {
			this.hyTicketRefunds = hyTicketRefunds;
		}
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyTicketHotelandscene wrapHyTicketHotelandscene,HttpSession session)
	{
		Json json=new Json();
		try{
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			Boolean flag = false;
			//新增只有供应商合同为正常才可以新建线路
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
			
			HyTicketHotelandscene hyTicketHotelandscene=wrapHyTicketHotelandscene.getHyTicketHotelandscene();
			Long supplierId=wrapHyTicketHotelandscene.getSupplierId();
			Long sceneAreaId=wrapHyTicketHotelandscene.getSceneAreaId();
			Long hotelAreaId=wrapHyTicketHotelandscene.getHotelAreaId();
			List<HyTicketRefund>  hyTicketRefunds=wrapHyTicketHotelandscene.getHyTicketRefunds();
			if(supplierId!=null) {
				HySupplierElement piaowubugongyingshang=hySupplierElementService.find(supplierId);
				hyTicketHotelandscene.setHySupplierElement(piaowubugongyingshang);
			}		
			HyArea hySceneArea=hyAreaService.find(sceneAreaId);
			hyTicketHotelandscene.setSceneArea(hySceneArea);
			HyArea hyHotelArea=hyAreaService.find(hotelAreaId);
			hyTicketHotelandscene.setHotelArea(hyHotelArea);
			String scenenOpenTime=wrapHyTicketHotelandscene.getSceneOpenTime();
			Date openTime = new SimpleDateFormat("HH:mm:ss").parse(scenenOpenTime);
			String scenenCloseTime=wrapHyTicketHotelandscene.getSceneCloseTime();
			Date closeTime= new SimpleDateFormat("HH:mm:ss").parse(scenenCloseTime);
			hyTicketHotelandscene.setSceneOpenTime(openTime);
			hyTicketHotelandscene.setSceneCloseTime(closeTime);
			hyTicketHotelandscene.setCreator(admin);
			hyTicketHotelandscene.setCreateTime(new Date());
			//如果预约时间为空,则设为0
			if(hyTicketHotelandscene.getReserveDays()==null) {
				hyTicketHotelandscene.setReserveDays(0);
			}
			if(hyTicketHotelandscene.getReserveTime()==null) {
				hyTicketHotelandscene.setReserveTime(0);
			}
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowubujjj));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				filters.clear();
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="JJJ-" + dateStr + "-" + String.format("%04d", value);
			}
			hyTicketHotelandscene.setProductId(produc);
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyTicketHotelandscene.setTicketSupplier(hySupplier);
			} 	
			hyTicketHotelandscene.setMhState(0); //门户完善状态设为未完善
			//初始化门户用推广文件
			hyTicketHotelandscene.setMhIntroduction(hyTicketHotelandscene.getIntroduction());
			hyTicketHotelandsceneService.save(hyTicketHotelandscene);
			for(HyTicketRefund hyTicketRefund:hyTicketRefunds) {
				hyTicketRefund.setType(2); //2-酒加景
				hyTicketRefund.setProductId(hyTicketHotelandscene.getId());
				hyTicketRefundService.save(hyTicketRefund);
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
	
	@RequestMapping(value="edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(HyTicketHotelandscene hyTicketHotelandscene,Long supplierId,Long sceneAreaId,Long hotelAreaId)
	{
		Json json=new Json();
		try{
			if(supplierId!=null) {
				HySupplierElement piaowubugongyingshang=hySupplierElementService.find(supplierId);
				hyTicketHotelandscene.setHySupplierElement(piaowubugongyingshang);
			}
			HyArea hySceneArea=hyAreaService.find(sceneAreaId);
			hyTicketHotelandscene.setSceneArea(hySceneArea);
			hyTicketHotelandscene.setModifyTime(new Date());
			HyArea hyHotelArea=hyAreaService.find(hotelAreaId);
			hyTicketHotelandscene.setHotelArea(hyHotelArea);
			//如果预约时间为空,则设为0
			if(hyTicketHotelandscene.getReserveDays()==null) {
				hyTicketHotelandscene.setReserveDays(0);
			}
			if(hyTicketHotelandscene.getReserveTime()==null) {
				hyTicketHotelandscene.setReserveTime(0);
			}
			HyTicketHotelandscene preHotelandscene=hyTicketHotelandsceneService.find(hyTicketHotelandscene.getId());
			if(preHotelandscene.getMhState()!=null) {
				if(preHotelandscene.getMhState()==0) {
					preHotelandscene.setMhState(preHotelandscene.getMhState());
					preHotelandscene.setMhIntroduction(hyTicketHotelandscene.getIntroduction());
				}
				else if(preHotelandscene.getMhState()==1) {
					hyTicketHotelandscene.setMhState(2); //将门户完善状态改为供应商修改,待完善
					hyTicketHotelandscene.setMhIntroduction(preHotelandscene.getMhIntroduction());
				}
				
				else {
					hyTicketHotelandscene.setMhState(preHotelandscene.getMhState());
					hyTicketHotelandscene.setMhIntroduction(preHotelandscene.getMhIntroduction());
				}
			}
			else {
				hyTicketHotelandscene.setMhState(preHotelandscene.getMhState());
				preHotelandscene.setMhIntroduction(hyTicketHotelandscene.getIntroduction());
			}
			
			hyTicketHotelandsceneService.update(hyTicketHotelandscene,"ticketSupplier","productId","createTime","creator","refundType",
					"mhProductName","mhPriceContain","mhReserveKnow","mhRefundKnow","mhRefundType","mhSceneName",
					"mhSceneAddress","mhHotelName","mhHotelAddress","mhBriefIntroduction","mhCreateTime",
					"mhUpdateTime","mhOperator","mhIsHot");
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long hotelandsceneId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandscene hotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(hotelandscene.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", hotelandscene.getHySupplierElement().getName());
				map.put("supplierId",hotelandscene.getHySupplierElement().getId());
			}			
			map.put("productName", hotelandscene.getProductName());
			map.put("isRealName", hotelandscene.getIsRealName());
			map.put("days", hotelandscene.getDays());
			map.put("priceContain", hotelandscene.getPriceContain());
			map.put("reserveDays", hotelandscene.getReserveDays());
			map.put("reserveTime", hotelandscene.getReserveTime());
			map.put("reserveKnow", hotelandscene.getReserveKnow());
			map.put("refundKnow", hotelandscene.getRefundKnow());
			map.put("refundType", hotelandscene.getRefundType()); //退款类型
			List<Map<String,Object>> list=new ArrayList<>();			
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 2));//2-酒加景
			filters.add(Filter.eq("productId", hotelandsceneId));
			List<HyTicketRefund> ticketRefunds=hyTicketRefundService.findList(null,filters,null);
			for(HyTicketRefund hyTicketRefund:ticketRefunds) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("startDay", hyTicketRefund.getStartDay());
				obj.put("startTime", hyTicketRefund.getStartTime());
				obj.put("endDay", hyTicketRefund.getEndDay());
				obj.put("endTime", hyTicketRefund.getEndTime());
				obj.put("percentage", hyTicketRefund.getPercentage());
				list.add(obj);
			}
			map.put("hyTicketRefunds",list);
			map.put("sceneName", hotelandscene.getSceneName());
			map.put("sceneArea", hotelandscene.getSceneArea().getFullName());
			map.put("sceneAreaId", hotelandscene.getSceneArea().getId());
			map.put("sceneAddress", hotelandscene.getSceneAddress());
			map.put("sceneStar", hotelandscene.getSceneStar());
			map.put("sceneOpenTime", hotelandscene.getSceneOpenTime());
			map.put("sceneCloseTime", hotelandscene.getSceneCloseTime());
			map.put("exchangeTicketAddress", hotelandscene.getExchangeTicketAddress());
			map.put("adultsTicketNum", hotelandscene.getAdultsTicketNum());
			map.put("childrenTicketNum", hotelandscene.getChildrenTicketNum());
			map.put("studentsTicketNum", hotelandscene.getStudentsTicketNum());
			map.put("oldTicketNum", hotelandscene.getOldTicketNum());
			map.put("hotelName", hotelandscene.getHotelName());
			map.put("hotelStar", hotelandscene.getHotelStar());
			map.put("hotelArea", hotelandscene.getHotelArea().getFullName());
			map.put("hotelAreaId", hotelandscene.getHotelArea().getId());
			map.put("hotelAddress", hotelandscene.getHotelAddress());
			map.put("introduction", hotelandscene.getIntroduction()); //产品介绍
			map.put("ticketFile",hotelandscene.getTicketFile()); //票务推广文件
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
			filters.add(Filter.eq("supplierType", SupplierType.piaowuJiujiajing)); //筛选旅游元素供应商
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
	
	@RequestMapping(value="roomList/view")
	@ResponseBody
	public Json roomList(Pageable pageable,Long hotelandsceneId,Integer saleStatus,Integer auditStatus)
	{
		Json json=new Json();
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			HyTicketHotelandscene hotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("hyTicketHotelandscene", hotelandscene));
			if(saleStatus != null){
				filters.add(Filter.eq("saleStatus", saleStatus));
			}
			if(auditStatus != null){
				filters.add(Filter.eq("auditStatus", auditStatus));
			}
			pageable.setFilters(filters);
			Page<HyTicketHotelandsceneRoom> page=hyTicketHotelandsceneRoomService.findPage(pageable);
			if(page.getTotal()>0) {
				for(HyTicketHotelandsceneRoom hyHotelandscene:page.getRows()) {
					HashMap<String,Object> roomMap=new HashMap<String,Object>();
					roomMap.put("id", hyHotelandscene.getId());
					roomMap.put("roomType", hyHotelandscene.getRoomType());
					roomMap.put("isWifi",hyHotelandscene.getIsWifi());
					roomMap.put("isWindow",hyHotelandscene.getIsWindow());
					roomMap.put("isBathroom",hyHotelandscene.getIsBathroom());
					roomMap.put("available",hyHotelandscene.getAvailable());
					roomMap.put("breakfast",hyHotelandscene.getBreakfast());
					roomMap.put("auditStatus",hyHotelandscene.getAuditStatus());
					roomMap.put("saleStatus",hyHotelandscene.getSaleStatus());
					roomMap.put("status", hyHotelandscene.getStatus());
					list.add(roomMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			json.setSuccess(true);
			json.setMsg("查找成功！");
			json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/*新建一个包装类，传递参数*/
	static class WrapHyTicketHotelandsceneRoom{
		private Long hotelandsceneId;
		private HyTicketHotelandsceneRoom hotelandsceneRoom;
		public Long getHotelandsceneId() {
			return hotelandsceneId;
		}
		public void setHotelandsceneId(Long hotelandsceneId) {
			this.hotelandsceneId = hotelandsceneId;
		}
		public HyTicketHotelandsceneRoom getHotelandsceneRoom() {
			return hotelandsceneRoom;
		}
		public void setHotelandsceneRoom(HyTicketHotelandsceneRoom hotelandsceneRoom) {
			this.hotelandsceneRoom = hotelandsceneRoom;
		}
		
	}
	@RequestMapping(value="addRoom",method = RequestMethod.POST)
	@ResponseBody
	public Json addRoom(@RequestBody WrapHyTicketHotelandsceneRoom wrapHyTicketHotelandsceneRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom hotelandsceneRoom=wrapHyTicketHotelandsceneRoom.getHotelandsceneRoom();
			Long hotelandsceneId=wrapHyTicketHotelandsceneRoom.getHotelandsceneId();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
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
			/*开启流程实例*/
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelandsceneRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();		
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			hotelandsceneRoom.setHyTicketHotelandscene(hyTicketHotelandscene);	
			hotelandsceneRoom.setStatus(true);
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setAuditStatus(2); //提交审核
			hotelandsceneRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelandsceneRoom.setSubmitter(admin);
			hotelandsceneRoom.setSubmitTime(new Date());
			hyTicketHotelandsceneRoomService.save(hotelandsceneRoom);
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
					hyTicketPriceInboundService.save(price);		
				}
			}
			
			// 完成房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			json.setSuccess(true);
			json.setMsg("添加成功！");
		}
		catch(Exception e){
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="saveRoom",method = RequestMethod.POST)
	@ResponseBody
	public Json saveRoom(@RequestBody WrapHyTicketHotelandsceneRoom wrapHyTicketHotelandsceneRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom hotelandsceneRoom=wrapHyTicketHotelandsceneRoom.getHotelandsceneRoom();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
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
			Long hotelandsceneId=wrapHyTicketHotelandsceneRoom.getHotelandsceneId();
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			hotelandsceneRoom.setHyTicketHotelandscene(hyTicketHotelandscene);	
			hotelandsceneRoom.setStatus(true);
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setAuditStatus(1); //保存未提交审核
			hyTicketHotelandsceneRoomService.save(hotelandsceneRoom);
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
					hyTicketPriceInboundService.save(price);		
				}
			}
			json.setSuccess(true);
			json.setMsg("添加成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("submitRoom")
	@ResponseBody
	public Json submitPrice(Long roomId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom hyTicketHotelandsceneRoom=hyTicketHotelandsceneRoomService.find(roomId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelandsceneRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成酒加景房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyTicketHotelandsceneRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketHotelandsceneRoom.setSubmitter(admin);
			hyTicketHotelandsceneRoom.setSubmitTime(new Date());
			hyTicketHotelandsceneRoom.setStatus(true);
			hyTicketHotelandsceneRoom.setAuditStatus(2); //提交审核
			hyTicketHotelandsceneRoomService.update(hyTicketHotelandsceneRoom);
			json.setMsg("提交成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("editRoom/submit")
	@ResponseBody
	public Json editsubmitRoom(@RequestBody HyTicketHotelandsceneRoom hotelandsceneRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			Long roomId=hotelandsceneRoom.getId();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
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
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelandsceneRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hotelandsceneRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelandsceneRoom.setAuditStatus(2); //提交审核
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setStatus(true);
			hotelandsceneRoom.setSubmitter(admin);
			hotelandsceneRoom.setSubmitTime(new Date());
			hyTicketHotelandsceneRoomService.update(hotelandsceneRoom,"productId","hyTicketHotelandscene","mhIsSale");
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
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
	
	@RequestMapping("editRoom/save")
	@ResponseBody
	public Json editsaveRoom(@RequestBody HyTicketHotelandsceneRoom hotelandsceneRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
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
			Long roomId=hotelandsceneRoom.getId();
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setAuditStatus(1); //未提交审核
			hotelandsceneRoom.setStatus(true);
			hyTicketHotelandsceneRoomService.update(hotelandsceneRoom,"productId","hyTicketHotelandscene","mhIsSale");
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
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
	public Json editsumitPrice(@RequestBody HyTicketHotelandsceneRoom hotelandsceneRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			Long roomId=hotelandsceneRoom.getId();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelandsceneRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hotelandsceneRoom.setId(roomId);
			hotelandsceneRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelandsceneRoom.setAuditStatus(2); //提交审核
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setStatus(true);
			hotelandsceneRoom.setSubmitter(admin);
			hotelandsceneRoom.setSubmitTime(new Date());
			hyTicketHotelandsceneRoomService.update(hotelandsceneRoom,"productId","productName","hyTicketHotelandscene","roomType",
					"isWifi","isWindow","isBathroom","available","breakfast","productType","mhIsSale");
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
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
	public Json editsavePrice(@RequestBody HyTicketHotelandsceneRoom hotelandsceneRoom)
	{
		Json json=new Json();
		try{
			Long roomId=hotelandsceneRoom.getId();
			hotelandsceneRoom.setId(roomId);
			hotelandsceneRoom.setAuditStatus(1); //未提交
			hotelandsceneRoom.setSaleStatus(1); //未上架
			hotelandsceneRoom.setStatus(true);
			hyTicketHotelandsceneRoomService.update(hotelandsceneRoom,"productId","productName","hyTicketHotelandscene","roomType",
					"isWifi","isWindow","isBathroom","available","breakfast","productType","mhIsSale");
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelandsceneRoom(hotelandsceneRoom);
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
	public Json priceDetail(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom hotelandsceneRoom=hyTicketHotelandsceneRoomService.find(roomId);
			String processInstanceId = hotelandsceneRoom.getProcessInstanceId();
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
			map.put("roomType", hotelandsceneRoom.getRoomType());
			map.put("isWifi", hotelandsceneRoom.getIsWifi());
			map.put("isWindow", hotelandsceneRoom.getIsWindow());
			map.put("isBathroom", hotelandsceneRoom.getIsBathroom());
			map.put("available", hotelandsceneRoom.getAvailable());
			map.put("breakfast", hotelandsceneRoom.getBreakfast());
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
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
	public Json cancel(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			room.setStatus(false);
			hyTicketHotelandsceneRoomService.update(room);
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
	public Json restore(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			room.setStatus(true);
			hyTicketHotelandsceneRoomService.update(room);
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
	public Json oncarriage(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			room.setSaleStatus(2); //上架
			hyTicketHotelandsceneRoomService.update(room);
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
	public Json undercarriage(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			room.setSaleStatus(3); //下架
			hyTicketHotelandsceneRoomService.update(room);
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


