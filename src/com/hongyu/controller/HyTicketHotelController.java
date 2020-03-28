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
import java.util.HashSet;
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
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketRefundService;
import com.hongyu.util.AuthorityUtils;

/**
 * 供应商上产品-酒店
 * author:GSbing
 */

@Controller
@RequestMapping("/admin/internTicket/hotel/")
public class HyTicketHotelController {
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
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
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
	
	/**
	 * 酒店列表页
	 */
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,String hotelName,String creatorName,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			HyTicketHotel hyTicketHotel=new HyTicketHotel();
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
			List<Filter> hotelFilter=new ArrayList<Filter>();
			hotelFilter.add(Filter.in("creator",hyAdmins));
			if(hotelName!=null&&!hotelName.equals(""))
			{
				hotelFilter.add(Filter.like("hotelName", hotelName));
			}			
			if(creatorName!=null&&!creatorName.equals(""))
			{
				List<Filter> filter=new ArrayList<Filter>();
				filter.add(Filter.like("name",creatorName));
				List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
				if(adminList.size()==0){
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyTicketHotel>());
				}
				else{
					hotelFilter.add(Filter.in("creator", adminList));
					List<Filter> supplierFilter=new ArrayList<Filter>();
					supplierFilter.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
					List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
					if(!hySupplierContracts.isEmpty()) {
						HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
						hotelFilter.add(Filter.eq("ticketSupplier", hySupplier));
					}
					pageable.setFilters(hotelFilter);
					List<Order> orders = new ArrayList<Order>();
					orders.add(Order.desc("createTime"));
					pageable.setOrders(orders);
					Page<HyTicketHotel> page=hyTicketHotelService.findPage(pageable,hyTicketHotel);
					if(page.getTotal()>0){
						for(HyTicketHotel hotel:page.getRows()){
							HashMap<String,Object> hotelMap=new HashMap<String,Object>();
							HyAdmin creator=hotel.getCreator();
							hotelMap.put("id", hotel.getId());
							hotelMap.put("hotelName", hotel.getHotelName());
							List<HyTicketHotelRoom> roomList =new ArrayList<>(hotel.getHyTicketHotelRooms());						
							//算出最近价格日期和最低价格
							if(roomList.size()>0){
								List<Date> dateList=new ArrayList<>();
								List<BigDecimal> priceList=new ArrayList<>();
								for(HyTicketHotelRoom room:roomList){
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
							hotelMap.put("createTime", hotel.getCreateTime());
							hotelMap.put("creatorName", hotel.getCreator().getName());
							
							//查找是否有上架产品,判断是否可编辑
							List<HyTicketHotelRoom> hotelRooms=new ArrayList<>(hotel.getHyTicketHotelRooms());
							int flag=1; //标志位
							for(HyTicketHotelRoom room:hotelRooms) {
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
			}
			//creatorName==null
			else{
				List<Filter> supplierFilter=new ArrayList<Filter>();
				supplierFilter.add(Filter.eq("liable", admin)); //帅选该登录账号负责的合同
				List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,supplierFilter,null); //根据合同找到供应商
				if(!hySupplierContracts.isEmpty()) {
					HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
					hotelFilter.add(Filter.eq("ticketSupplier", hySupplier));
				}
				pageable.setFilters(hotelFilter);
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc("createTime"));
				pageable.setOrders(orders);
				Page<HyTicketHotel> page=hyTicketHotelService.findPage(pageable,hyTicketHotel);
				if(page.getTotal()>0){
					for(HyTicketHotel hotel:page.getRows()){
						HashMap<String,Object> hotelMap=new HashMap<String,Object>();
						HyAdmin creator=hotel.getCreator();
						hotelMap.put("id", hotel.getId());
						hotelMap.put("hotelName", hotel.getHotelName());
						List<HyTicketHotelRoom> roomList =new ArrayList<>(hotel.getHyTicketHotelRooms());
						if(roomList.size()>0){
							//算出最近价格日期和最低价格
							if(roomList.size()>0){
								List<Date> dateList=new ArrayList<>();
								List<BigDecimal> priceList=new ArrayList<>();
								for(HyTicketHotelRoom room:roomList){
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
						}
						
						hotelMap.put("createTime", hotel.getCreateTime());
						hotelMap.put("creatorName", hotel.getCreator().getName());
						
						//查找是否有上架产品,判断是否可编辑
						List<HyTicketHotelRoom> hotelRooms=new ArrayList<>(hotel.getHyTicketHotelRooms());
						int flag=1; //标志位
						for(HyTicketHotelRoom room:hotelRooms) {
							//如果有已上架产品
							if(room.getSaleStatus()==1) {
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
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	static class WrapHyTicketHotel{
		private HyTicketHotel hyTicketHotel;
		private List<HyTicketRefund> hyTicketRefunds=new ArrayList<>();
		private Long areaId;
		private Long supplierId;
		public HyTicketHotel getHyTicketHotel() {
			return hyTicketHotel;
		}
		public void setHyTicketHotel(HyTicketHotel hyTicketHotel) {
			this.hyTicketHotel = hyTicketHotel;
		}
		public List<HyTicketRefund> getHyTicketRefunds() {
			return hyTicketRefunds;
		}
		public void setHyTicketRefunds(List<HyTicketRefund> hyTicketRefunds) {
			this.hyTicketRefunds = hyTicketRefunds;
		}
		public Long getAreaId() {
			return areaId;
		}
		public void setAreaId(Long areaId) {
			this.areaId = areaId;
		}
		public Long getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(Long supplierId) {
			this.supplierId = supplierId;
		}
	}
	
	/**新建酒店*/
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyTicketHotel wrapHyTicketHotel,HttpSession session)
	{
		Json json=new Json();
		try{
			HyTicketHotel hyTicketHotel=wrapHyTicketHotel.getHyTicketHotel();
			List<HyTicketRefund> hyTicketRefunds=wrapHyTicketHotel.getHyTicketRefunds();
			Long areaId=wrapHyTicketHotel.getAreaId();
			HyArea hyArea=hyAreaService.find(areaId);
			hyTicketHotel.setArea(hyArea);
			Long supplierId=wrapHyTicketHotel.getSupplierId();	
			if(supplierId!=null) {
				HySupplierElement piaowubuGongyingshang=hySupplierElementService.find(supplierId);
				hyTicketHotel.setHySupplierElement(piaowubuGongyingshang);
			}		
			hyTicketHotel.setCreateTime(new Date());
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
			hyTicketHotel.setCreator(admin);
			List<Filter> filters=new ArrayList<Filter>();
			
			filters.add(Filter.eq("liable", findPAdmin(admin))); //筛选该登录账号负责的合同
			List<HySupplierContract> hySupplierContracts=hySupplierContractService.findList(null,filters,null); //根据合同找到供应商
			filters.clear();
			if(!hySupplierContracts.isEmpty()) {
				HySupplier hySupplier=hySupplierContracts.get(0).getHySupplier(); //找出供应商
				hyTicketHotel.setTicketSupplier(hySupplier);
			}		
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			synchronized(CommonSequence.class) {
				filters.add(Filter.eq("type", SequenceTypeEnum.piaowujiudianPn));
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);	
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="JD-" + dateStr + "-" + String.format("%04d", value);
			}	

			hyTicketHotel.setPn(produc);
			hyTicketHotel.setMhState(0); //门户完善状态默认为未完善
			hyTicketHotel.setMhIntroduction(hyTicketHotel.getMhIntroduction()); //将门户的推广文件设置初始状态
			hyTicketHotelService.save(hyTicketHotel);
			for(HyTicketRefund hyTicketRefund:hyTicketRefunds) {
				hyTicketRefund.setType(1); //1-酒店
				hyTicketRefund.setProductId(hyTicketHotel.getId());
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
	
	/**编辑酒店信息*/
	@RequestMapping(value="edit")
	@ResponseBody
	public Json edit(HyTicketHotel hyTicketHotel,Long areaId,Long supplierId)
	{
		Json json=new Json();
		try{
			HyArea hyArea=hyAreaService.find(areaId);
			hyTicketHotel.setArea(hyArea);
			if(supplierId!=null) {
				HySupplierElement piaowubuGongyingshang=hySupplierElementService.find(supplierId);
				hyTicketHotel.setHySupplierElement(piaowubuGongyingshang);
			}			
			hyTicketHotel.setModifyTime(new Date());
			HyTicketHotel preHotel=hyTicketHotelService.find(hyTicketHotel.getId());
			if(preHotel.getMhState()!=null) {
				if(preHotel.getMhState()==0) {
					hyTicketHotel.setMhState(preHotel.getMhState());
					hyTicketHotel.setMhIntroduction(hyTicketHotel.getIntroduction());
				}
				else if(preHotel.getMhState()==1) {
					hyTicketHotel.setMhState(2); //将门户完善状态改为供应商修改,待完善
					hyTicketHotel.setMhIntroduction(preHotel.getMhIntroduction());
				}
				
				else {
					hyTicketHotel.setMhState(preHotel.getMhState());
					hyTicketHotel.setMhIntroduction(preHotel.getMhIntroduction());
				}
			}
			else {
				hyTicketHotel.setMhState(preHotel.getMhState());
				hyTicketHotel.setMhIntroduction(hyTicketHotel.getIntroduction());
			}
				
			hyTicketHotelService.update(hyTicketHotel,"ticketSupplier","createTime","creator","refundType","pn","mhOperator",
					"mhCreateTime","mhUpdateTime","mhHotelName","mhAddress","mhReserveKnow","mhRefundReq","mhRefundType",
					"mhBriefIntroduction","mhIsHot");
			json.setMsg("编辑成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**酒店详情页*/
	@RequestMapping(value="detail/view",method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("hotelName",hyTicketHotel.getHotelName());
			map.put("area", hyTicketHotel.getArea().getFullName());
			map.put("areaId", hyTicketHotel.getArea().getId());		
			//判断是否内部供应商
			if(hyTicketHotel.getTicketSupplier().getIsInner()==true) {
				map.put("supplierName", hyTicketHotel.getHySupplierElement().getName());
				map.put("supplierId", hyTicketHotel.getHySupplierElement().getId());
			}	
			map.put("refundType", hyTicketHotel.getRefundType()); //退款类型
			List<Map<String,Object>> list=new ArrayList<>();			
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 1));//1-酒店
			filters.add(Filter.eq("productId", id));
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
			map.put("address", hyTicketHotel.getAddress());
			map.put("star", hyTicketHotel.getStar());
			map.put("reserveKnow", hyTicketHotel.getReserveKnow());
			map.put("refundReq", hyTicketHotel.getRefundReq());
			map.put("introduction", hyTicketHotel.getIntroduction()); //产品介绍
			map.put("ticketFile",hyTicketHotel.getTicketFile()); //票务推广文件
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
	
	/**供应商下拉列表*/
	@RequestMapping("supplierList/view")
	@ResponseBody
	public Json supplierList()
	{
		Json json=new Json();
		try{
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("supplierType", SupplierType.piaowuHotel)); //筛选旅游元素供应商
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
	
	/**酒店房间列表*/
	@RequestMapping(value="roomList/view")
	@ResponseBody
	public Json roomList(HyTicketHotelRoom queryParam,Long hotelId,Pageable pageable)
	{
		Json json=new Json();
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(hotelId);
			List<Filter> filter=new ArrayList<Filter>();
			filter.add(Filter.eq("hyTicketHotel", hyTicketHotel));
			pageable.setFilters(filter);
			Page<HyTicketHotelRoom> page=hyTicketHotelRoomService.findPage(pageable,queryParam);
			if(page.getTotal()>0){
				for(HyTicketHotelRoom hotelRoom:page.getRows()){
					HashMap<String,Object> roomMap=new HashMap<String,Object>();
					roomMap.put("id", hotelRoom.getId());
					roomMap.put("productId", hotelRoom.getProductId());
					roomMap.put("productName", hotelRoom.getProductName());
					//算出最低价格
					List<BigDecimal> priceList=new ArrayList<>();	
					List<HyTicketPriceInbound> inboundPrices=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
					for(HyTicketPriceInbound inboundPrice:inboundPrices) {
						Date date=new Date();
						if(inboundPrice.getStartDate().compareTo(date)<=0
							&& inboundPrice.getEndDate().compareTo(DateUtils.truncate(date, Calendar.DATE))>0){
								priceList.add(inboundPrice.getSettlementPrice());									
						}
					}
					if(!priceList.isEmpty()) {
						BigDecimal lowestPrice=Collections.min(priceList);
						roomMap.put("lowestPrice",lowestPrice);
					}		
					roomMap.put("auditStatus", hotelRoom.getAuditStatus());
					roomMap.put("saleStatus", hotelRoom.getSaleStatus());
					roomMap.put("status", hotelRoom.getStatus());
					list.add(roomMap);
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
	
	/*新建一个包装类，传递参数*/
	static class WrapHyTicketHotelRoom{
		private Long hotelId;
		private HyTicketHotelRoom hotelRoom;		
		
		public Long getHotelId() {
			return hotelId;
		}
		public void setHotelId(Long hotelId) {
			this.hotelId = hotelId;
		}
		public HyTicketHotelRoom getHotelRoom() {
			return hotelRoom;
		}
		public void setHotelRoom(HyTicketHotelRoom hotelRoom) {
			this.hotelRoom = hotelRoom;
		}
		
	}
	
	/**新建酒店房间,并提交审核*/
	@RequestMapping(value="addRoom", method = RequestMethod.POST)
	@ResponseBody
	public Json addRoom(@RequestBody WrapHyTicketHotelRoom wrapHyTicketHotelRoom,HttpSession httpSession)
	{
		Json json=new Json();	
		try{
			HyTicketHotelRoom hotelRoom=wrapHyTicketHotelRoom.getHotelRoom();
			Long hotelId=wrapHyTicketHotelRoom.getHotelId();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
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
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(hotelId);
			hotelRoom.setHyTicketHotel(hyTicketHotel);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowubujdfj));
			synchronized(CommonSequence.class) {			
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="FJ-" + dateStr + "-" + String.format("%04d", value);
			}
			hotelRoom.setProductId(produc);
			hotelRoom.setStatus(true);
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setAuditStatus(2); //提交审核
			hotelRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelRoom.setSubmitter(admin);
			hotelRoom.setSubmitTime(new Date());
			//如果预约时间为空,则设为0
			if(hotelRoom.getReserveDays()==null) {
				hotelRoom.setReserveDays(0);
			}
			if(hotelRoom.getReserveTime()==null) {
				hotelRoom.setReserveTime(0);
			}
			hyTicketHotelRoomService.save(hotelRoom);
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	
	/**新建酒店房间,不提交审核*/
	@RequestMapping(value="saveRoom", method = RequestMethod.POST)
	@ResponseBody
	public Json saveRoom(@RequestBody WrapHyTicketHotelRoom wrapHyTicketHotelRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom hotelRoom=wrapHyTicketHotelRoom.getHotelRoom();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
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
			Long hotelId=wrapHyTicketHotelRoom.getHotelId();
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(hotelId);
			hotelRoom.setHyTicketHotel(hyTicketHotel);
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.piaowubujdfj));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="FJ-" + dateStr + "-" + String.format("%04d", value);
			}
			hotelRoom.setProductId(produc);
			hotelRoom.setStatus(true);
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setAuditStatus(1); //保存未提交审核
			//如果预约时间为空,则设为0
			if(hotelRoom.getReserveDays()==null) {
				hotelRoom.setReserveDays(0);
			}
			if(hotelRoom.getReserveTime()==null) {
				hotelRoom.setReserveTime(0);
			}
			hyTicketHotelRoomService.save(hotelRoom);
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	
	/**酒店房间价格提交审核*/
	@RequestMapping("submitPrice")
	@ResponseBody
	public Json submitPrice(Long roomId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom hyTicketHotelRoom=hyTicketHotelRoomService.find(roomId);
			hyTicketHotelRoom.setAuditStatus(2); //提交审核
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyTicketHotelRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hyTicketHotelRoom.setSubmitter(admin);
			hyTicketHotelRoom.setSubmitTime(new Date());
			hyTicketHotelRoom.setStatus(true);
			hyTicketHotelRoomService.update(hyTicketHotelRoom);
			json.setSuccess(true);
			json.setMsg("提交成功");		    
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**酒店房间编辑,并提交审核*/
	@RequestMapping("editRoom/submit")
	@ResponseBody
	public Json editsubmitRoom(@RequestBody HyTicketHotelRoom hotelRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			Long roomId=hotelRoom.getId();
			hotelRoom.setId(roomId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
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
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hotelRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelRoom.setAuditStatus(2); //提交审核
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setStatus(true);
			hotelRoom.setSubmitter(admin);
			hotelRoom.setSubmitTime(new Date());
			//如果预约时间为空,则设为0
			if(hotelRoom.getReserveDays()==null) {
				hotelRoom.setReserveDays(0);
			}
			if(hotelRoom.getReserveTime()==null) {
				hotelRoom.setReserveTime(0);
			}
			hyTicketHotelRoomService.update(hotelRoom,"productId","hyTicketHotel","mhProductName","mhIsSale");
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	
	/**酒店房间编辑,不提交审核*/
	@RequestMapping("editRoom/save")
	@ResponseBody
	public Json editsaveRoom(@RequestBody HyTicketHotelRoom hotelRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
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
			hotelRoom.setAuditStatus(1); //未提交审核
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setStatus(true);
			//如果预约时间为空,则设为0
			if(hotelRoom.getReserveDays()==null) {
				hotelRoom.setReserveDays(0);
			}
			if(hotelRoom.getReserveTime()==null) {
				hotelRoom.setReserveTime(0);
			}
			hyTicketHotelRoomService.update(hotelRoom,"productId","hyTicketHotel","mhProductName","mhIsSale");
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(hotelRoom.getId());
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	public Json editsumitPrice(@RequestBody HyTicketHotelRoom hotelRoom,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			Long roomId=hotelRoom.getId();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("hotelRoomPriceProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 酒店房间价格提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hotelRoom.setId(roomId);
			hotelRoom.setProcessInstanceId(pi.getProcessInstanceId()); //保存流程id
			hotelRoom.setAuditStatus(2); //提交审核
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setStatus(true);
			hotelRoom.setSubmitter(admin);
			hotelRoom.setSubmitTime(new Date());
			hyTicketHotelRoomService.update(hotelRoom,"productId","productName","hyTicketHotel","roomType",
					"isWifi","isWindow","isBathroom","available","breakfast","productType","mhProductName","mhIsSale");
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	public Json editsavePrice(@RequestBody HyTicketHotelRoom hotelRoom)
	{
		Json json=new Json();
		try{
			Long roomId=hotelRoom.getId();
			hotelRoom.setId(roomId);
			hotelRoom.setAuditStatus(1); //未提交
			hotelRoom.setSaleStatus(1); //未上架
			hotelRoom.setStatus(true);
			hyTicketHotelRoomService.update(hotelRoom,"productId","productName","hyTicketHotel","roomType",
					"isWifi","isWindow","isBathroom","available","breakfast","productType","mhProductName","mhIsSale");
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			List<HyTicketPriceInbound> inboundList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound priceInbound:inboundList){
				hyTicketPriceInboundService.delete(priceInbound);
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
			if(priceList.size()>0){ 
				for(HyTicketPriceInbound price:priceList){
					price.setHyTicketHotelRoom(hotelRoom);
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
	
	/**酒店房间价格详情*/
	@RequestMapping("price/detail/view")
	@ResponseBody
	public Json priceDetail(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom hotelRoom=hyTicketHotelRoomService.find(roomId);
			String processInstanceId = hotelRoom.getProcessInstanceId();
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
			map.put("productId", hotelRoom.getProductId());
			map.put("productName", hotelRoom.getProductName());
			map.put("roomType", hotelRoom.getRoomType());
			map.put("isWifi", hotelRoom.getIsWifi());
			map.put("isWindow", hotelRoom.getIsWindow());
			map.put("isBathroom", hotelRoom.getIsBathroom());
			map.put("available", hotelRoom.getAvailable());
			map.put("breakfast", hotelRoom.getBreakfast());
			map.put("reserveDays", hotelRoom.getReserveDays());
			map.put("reserveTime",hotelRoom.getReserveTime());
			List<HyTicketPriceInbound> priceList=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
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
	
	/**取消*/
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancel(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setStatus(false);
			hyTicketHotelRoomService.update(room);
			json.setMsg("取消成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**恢复*/
	@RequestMapping("restore")
	@ResponseBody
	public Json restore(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setStatus(true);
			hyTicketHotelRoomService.update(room);
			json.setMsg("恢复成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**酒店房间产品上架*/
	@RequestMapping("oncarriage")
	@ResponseBody
	public Json oncarriage(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setSaleStatus(2); //上架
			hyTicketHotelRoomService.update(room);
			json.setMsg("上架成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**酒店房间产品下架*/
	@RequestMapping("undercarriage")
	@ResponseBody
	public Json undercarriage(Long roomId)
	{
		Json json=new Json();
		try{
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setSaleStatus(3); //下架
			room.setMhIsSale(0); //同时将门户下线
			hyTicketHotelRoomService.update(room);
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
