package com.hongyu.controller.gsbing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.Store;
import com.hongyu.Filter.Operator;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;

/**按团号统计收款报表*/
@RestController
@RequestMapping("/admin/groupcollect/statistics/")
public class GroupCollectReportController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name="hyLineServiceImpl")
	private HyLineService hyLineService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	//内部类,用于sql查询的分页帮助
	public static class WrapCollectMoney implements Serializable{
		private Long orderId;
		private String orderNumber;
		private String linePn;
		private String lineName;
		private Date groupStartDay;
		private Integer storeType;
		private String storeName;
		private String areaName;
		private String contact;
		private String creator;
		private String groupOperator;
		private BigDecimal collectMoney;
		private Date collectDay;
		private Boolean isCheckAccount;
		public Long getOrderId() {
			return orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getLinePn() {
			return linePn;
		}
		public void setLinePn(String linePn) {
			this.linePn = linePn;
		}
		public String getLineName() {
			return lineName;
		}
		public void setLineName(String lineName) {
			this.lineName = lineName;
		}
		public Date getGroupStartDay() {
			return groupStartDay;
		}
		public void setGroupStartDay(Date groupStartDay) {
			this.groupStartDay = groupStartDay;
		}
		public Integer getStoreType() {
			return storeType;
		}
		public void setStoreType(Integer storeType) {
			this.storeType = storeType;
		}
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		public String getAreaName() {
			return areaName;
		}
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public String getGroupOperator() {
			return groupOperator;
		}
		public void setGroupOperator(String groupOperator) {
			this.groupOperator = groupOperator;
		}
		public BigDecimal getCollectMoney() {
			return collectMoney;
		}
		public void setCollectMoney(BigDecimal collectMoney) {
			this.collectMoney = collectMoney;
		}
		public Date getCollectDay() {
			return collectDay;
		}
		public void setCollectDay(Date collectDay) {
			this.collectDay = collectDay;
		}
		public Boolean getIsCheckAccount() {
			return isCheckAccount;
		}
		public void setIsCheckAccount(Boolean isCheckAccount) {
			this.isCheckAccount = isCheckAccount;
		}
	}
	
	public static class WrapCollectMoneyPage{
		private List<WrapCollectMoney> list;
		private Long total;
		public List<WrapCollectMoney> getList() {
			return list;
		}
		public void setList(List<WrapCollectMoney> list) {
			this.list = list;
		}
		public Long getTotal() {
			return total;
		}
		public void setTotal(Long total) {
			this.total = total;
		}
	}
	
	/**按团号收款统计报表*/
	@RequestMapping(value = "list/view")
	@ResponseBody
	public Json listview(Pageable pageable,String collectStart,String collectEnd,String groupStart,String groupEnd,
			String orderNumber,String linePn,String lineName,Integer isCheckAccount,Long areaId,Integer storeType) {
		Json json=new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			StringBuilder jpql = new StringBuilder("select o1.id,o1.order_number,l1.pn,o1.fatuandate,st1.store_name,o1.store_type,");
			jpql.append("ar1.full_name,o1.contact,a1.name,concat(su1.supplier_name,':',a2.name),o1.jiusuan_money + o1.tip - o1.discounted_price - o1.store_fan_li,");
			jpql.append("o1.createtime,l1.name as linename,o1.is_check_account");
			jpql.append(" from (select * from hy_order where hy_order.type=1 and hy_order.source=0) as o1");
			jpql.append(" join hy_admin a1 ON a1.username=o1.creator_id");
			jpql.append(" join hy_group g1 ON o1.group_id=g1.id");
			jpql.append(" join hy_line l1 ON g1.line=l1.id");
			jpql.append(" join hy_supplier su1 ON su1.id=l1.supplier");
			jpql.append(" join hy_admin a2 ON a2.username=o1.supplier");
			jpql.append(" join hy_store st1 ON st1.id=o1.store_id");
		    jpql.append(" join hy_area ar1 ON ar1.ID=st1.area_id");
		    jpql.append(" where o1.status=3");
		    if(collectStart!=null && !collectStart.equals("")) {
		    	Date start = sdf.parse(collectStart.substring(0, 10) + " " + "00:00:00");
		    	jpql.append("  and o1.createtime >=  " + "'" + sdf.format(start) + "'");
		    }
		    if(collectEnd!=null && !collectEnd.equals("")) {
		    	Date end = sdf.parse(collectEnd.substring(0, 10) + " " + "23:59:59");
		    	jpql.append("  and o1.createtime <=  " + "'" + sdf.format(end) + "'");
		    }
		    if(groupStart!=null && !groupStart.equals("")) {
		    	Date start = sdf.parse(groupStart.substring(0, 10) + " " + "00:00:00");
		    	jpql.append("  and o1.fatuandate >=  " + "'" + sdf.format(start) + "'");
		    }
		    if(groupEnd!=null && !groupEnd.equals("")) {
		    	Date end = sdf.parse(groupEnd.substring(0, 10) + " " + "23:59:59");
		    	jpql.append("  and o1.fatuandate <=  " + "'" + sdf.format(end) + "'");
		    }
		    if(orderNumber!=null) {
		    	jpql.append(" and o1.order_number= '"+orderNumber+"'");
		    }
		    if(linePn!=null) {
		    	jpql.append(" and l1.pn= '"+linePn+"'");
		    }
		    if(lineName!=null) {
		    	jpql.append(" and l1.name like '"+lineName+"'");
		    }
		    if(isCheckAccount!=null) {
		    	jpql.append(" and o1.is_check_account="+isCheckAccount);
		    }
		    if(storeType!=null) {
		    	jpql.append(" and st1.type="+storeType);
		    }
		    if(areaId!=null) {
		    	jpql.append(" and (ar1.ID="+areaId+" or ar1.pID="+areaId);
		    	jpql.append(" or ar1.pID in (select ID from hy_area where pID="+areaId+"))");
		    }
		    jpql.append(" order by o1.createtime desc");
		    Page<List<Object[]>> p=hyOrderService.findPageBysql(jpql.toString(),pageable);
		    List<Object[]> page=p.getLstObj();
		    List<WrapCollectMoney> list=new ArrayList<>();
		    for(Object[] objects:page) {
		    	WrapCollectMoney wrap=new WrapCollectMoney();
		    	wrap.setOrderId(((BigInteger)objects[0]).longValue()); //订单id
		    	wrap.setOrderNumber((String)objects[1]); //订单编号
		    	wrap.setLinePn((String)objects[2]); //线路编号
		    	wrap.setGroupStartDay((Date)objects[3]); //发团日期
		    	wrap.setStoreName((String)objects[4]); //门店名称
		    	wrap.setStoreType((Integer)objects[5]); //门店类型
		    	wrap.setAreaName((String)objects[6]); //门店所属区域
		    	wrap.setContact((String)objects[7]); //联系人
		    	wrap.setCreator((String)objects[8]); //报名计调
		    	wrap.setGroupOperator((String)objects[9]); //接团计调
		    	wrap.setCollectMoney((BigDecimal)objects[10]); //收款金额
		    	wrap.setCollectDay((Date)objects[11]); //收款时间
		    	wrap.setLineName((String)objects[12]); //线路名称
		    	wrap.setIsCheckAccount((Boolean)objects[13]);
		    	list.add(wrap);
		    }	  
		    Page<WrapCollectMoney> result=new Page<>(list,p.getTotal(),pageable);
//			List<Filter> filters=new ArrayList<>();
//			filters.add(Filter.eq("type", 1)); //只统计线路订单
//			filters.add(Filter.eq("status", 3)); //统计订单状态为供应商通过
//			filters.add(Filter.eq("source", 0)); //只统计门店
//			if(collectStart!=null && !collectStart.equals("")) {
//				filters.add(new Filter("createtime", Operator.ge,
//						sdf.parse(collectStart.substring(0, 10) + " " + "00:00:00")));
//			}
//			if(collectEnd!=null && !collectEnd.equals("")) {
//				filters.add(new Filter("createtime", Operator.le,
//						sdf.parse(collectEnd.substring(0, 10) + " " + "23:59:59")));
//			}
//			if(groupStart!=null && !groupStart.equals("")) {
//				filters.add(new Filter("fatuandate", Operator.ge,
//						sdf.parse(groupStart.substring(0, 10) + " " + "00:00:00")));
//			}
//			if(groupEnd!=null && !groupEnd.equals("")) {
//				filters.add(new Filter("fatuandate", Operator.le,
//						sdf.parse(groupEnd.substring(0, 10) + " " + "23:59:59")));
//			}
//			if(orderNumber!=null) {
//				filters.add(Filter.eq("orderNumber",orderNumber));
//			}
//			if(lineName!=null) {
//				filters.add(Filter.like("xianlumingcheng", lineName));
//			}
//			if(isCheckAccount!=null) {
//				if(isCheckAccount==1) {
//					filters.add(Filter.eq("isCheckAccount", true));
//				}
//				else if(isCheckAccount==0){
//					filters.add(Filter.eq("isCheckAccount", false));
//				}
//			}
//			if(storeType!=null) {
//				filters.add(Filter.eq("storeType", storeType));
//			}
//			List<Order> orders=new ArrayList<>();
//			orders.add(Order.desc("createtime"));
//			List<HyOrder> orderList=hyOrderService.findList(null,filters,orders);
//			Map<String,Object> obj=new HashMap<String,Object>();
//			List<HashMap<String, Object>> list = new ArrayList<>();
//			for(HyOrder hyOrder:orderList) {
//				HashMap<String,Object> map=new HashMap<String,Object>();
//				HyGroup hyGroup=hyGroupService.find(hyOrder.getGroupId()); //找到团
//				HyLine hyLine=hyGroup.getLine(); //找到线路
//				Store store=storeService.find(hyOrder.getStoreId()); //找到所属门店
//				HySupplier hySupplier=hyLine.getHySupplier(); //找出线路供应商
//				if(linePn!=null) {
//					if(hyLine.getPn().equals(linePn)) {
//						if(areaId!=null) {
//							HyArea fArea=hyAreaService.find(areaId);
//							Boolean flag=false; //判断是否属于所属区域
//							Set<HyArea> areaList=new HashSet<>();
//							if(fArea!=null) {
//								//如果选中的是省级找出该省级下所有的市级区域
//								areaList.add(fArea);
//								Set<HyArea> seAreaList=fArea.getHyAreas();
//								if(!seAreaList.isEmpty()) {
//									areaList.addAll(seAreaList);
//									for(HyArea area:seAreaList) {
//										areaList.add(area);
//										Set<HyArea> thirdAreaList=area.getHyAreas();
//										if(!thirdAreaList.isEmpty()) {
//											areaList.addAll(thirdAreaList);
//										}
//									}
//								}
//							}
//							
//							for(HyArea area:areaList) {
//								//门店区域在这个范围内
//								if(area.equals(store.getHyArea())) {
//									flag=true;
//									break;
//								}
//							}
//							if(flag==true) {
//								map.put("orderId", hyOrder.getId());
//								map.put("orderNumber",hyOrder.getOrderNumber());
//								map.put("linePn",hyLine.getPn()); //线路产品编号
//								map.put("lineName",hyLine.getName()); //线路名称
//								map.put("groupStartDay",hyOrder.getFatuandate());
//								map.put("storeType", hyOrder.getStoreType()); //门店类型
//								if(store.getHyArea()!=null) {
//									map.put("areaName", store.getHyArea().getFullName()); //所属地区
//								}						
//								map.put("storeName",store.getStoreName());								
//								map.put("contact",hyOrder.getContact()); //联系人
//								HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
//								map.put("creator",creator.getName()); //报名计调
//								String groupOperator=hySupplier.getSupplierName();
//								groupOperator=groupOperator+":";		
//								groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
//								map.put("groupOperator",groupOperator); //接团计调(供应商:计调)	
//								BigDecimal money=hyOrder.getJiusuanMoney();
//								if(hyOrder.getTip()!=null) {
//									money=money.add(hyOrder.getTip());
//								}
//								if(hyOrder.getDiscountedPrice()!=null) {
//									money=money.subtract(hyOrder.getDiscountedPrice());
//								}
//								if(hyOrder.getStoreFanLi()!=null) {
//									money=money.subtract(hyOrder.getStoreFanLi());
//								}
//							    map.put("collectMoney",money);
//							    map.put("collectDay",hyOrder.getCreatetime());
//							    map.put("isCheckAccount",hyOrder.getIsCheckAccount());
//							    list.add(map);
//							}
//						}
//						//areaId==null
//						else {
//							map.put("orderId", hyOrder.getId());
//							map.put("orderNumber",hyOrder.getOrderNumber());
//							map.put("linePn",hyLine.getPn()); //线路产品编号
//							map.put("lineName",hyLine.getName()); //线路名称
//							map.put("groupStartDay",hyOrder.getFatuandate());
//							map.put("storeType", hyOrder.getStoreType()); //门店类型
//							if(store.getHyArea()!=null) {
//								map.put("areaName", store.getHyArea().getFullName()); //所属地区
//							}
//							map.put("storeName",store.getStoreName());
//							
//							map.put("contact",hyOrder.getContact()); //联系人
//							HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
//							map.put("creator",creator.getName()); //报名计调
//							String groupOperator=hySupplier.getSupplierName();
//							groupOperator=groupOperator+":";		
//							groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
//							map.put("groupOperator",groupOperator); //接团计调(供应商:计调)	
//							BigDecimal money=hyOrder.getJiusuanMoney();
//							if(hyOrder.getTip()!=null) {
//								money=money.add(hyOrder.getTip());
//							}
//							if(hyOrder.getDiscountedPrice()!=null) {
//								money=money.subtract(hyOrder.getDiscountedPrice());
//							}
//							if(hyOrder.getStoreFanLi()!=null) {
//								money=money.subtract(hyOrder.getStoreFanLi());
//							}
//						    map.put("collectMoney",money);
//						    map.put("collectDay",hyOrder.getCreatetime());
//						    map.put("isCheckAccount",hyOrder.getIsCheckAccount());
//						    list.add(map);
//						}
//					}		
//				}	
//				//linePn==null
//				else {
//					if(areaId!=null) {
//						HyArea fArea=hyAreaService.find(areaId);
//						Boolean flag=false; //判断是否属于所属区域
//						Set<HyArea> areaList=new HashSet<>();
//						if(fArea!=null) {
//							//如果选中的是省级找出该省级下所有的市级区域
//							areaList.add(fArea);
//							Set<HyArea> seAreaList=fArea.getHyAreas();
//							if(!seAreaList.isEmpty()) {
//								areaList.addAll(seAreaList);
//								for(HyArea area:seAreaList) {
//									areaList.add(area);
//									Set<HyArea> thirdAreaList=area.getHyAreas();
//									if(!thirdAreaList.isEmpty()) {
//										areaList.addAll(thirdAreaList);
//									}
//								}
//							}
//						}
//						for(HyArea area:areaList) {
//							//门店区域在这个范围内
//							if(area.equals(store.getHyArea())) {
//								flag=true;
//								break;
//							}
//						}
//						if(flag==true) {
//							map.put("orderId", hyOrder.getId());
//							map.put("orderNumber",hyOrder.getOrderNumber());
//							map.put("linePn",hyLine.getPn()); //线路产品编号
//							map.put("lineName",hyLine.getName()); //线路名称
//							map.put("groupStartDay",hyOrder.getFatuandate());
//							map.put("storeType", hyOrder.getStoreType()); //门店类型
//							if(store.getHyArea()!=null) {
//								map.put("areaName", store.getHyArea().getFullName()); //所属地区
//							}						
//							map.put("storeName",store.getStoreName());								
//							map.put("contact",hyOrder.getContact()); //联系人
//							HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
//							map.put("creator",creator.getName()); //报名计调
//							String groupOperator=hySupplier.getSupplierName();
//							groupOperator=groupOperator+":";		
//							groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
//							map.put("groupOperator",groupOperator); //接团计调(供应商:计调)	
//							BigDecimal money=hyOrder.getJiusuanMoney();
//							if(hyOrder.getTip()!=null) {
//								money=money.add(hyOrder.getTip());
//							}
//							if(hyOrder.getDiscountedPrice()!=null) {
//								money=money.subtract(hyOrder.getDiscountedPrice());
//							}
//							if(hyOrder.getStoreFanLi()!=null) {
//								money=money.subtract(hyOrder.getStoreFanLi());
//							}
//						    map.put("collectMoney",money);
//						    map.put("collectDay",hyOrder.getCreatetime());
//						    map.put("isCheckAccount",hyOrder.getIsCheckAccount());
//						    list.add(map);
//						}
//					}
//					//areaId==null
//					else {
//						map.put("orderId", hyOrder.getId());
//						map.put("orderNumber",hyOrder.getOrderNumber());
//						map.put("linePn",hyLine.getPn()); //线路产品编号
//						map.put("lineName",hyLine.getName()); //线路名称
//						map.put("groupStartDay",hyOrder.getFatuandate());
//						map.put("storeType", hyOrder.getStoreType()); //门店类型
//						if(store.getHyArea()!=null) {
//							map.put("areaName", store.getHyArea().getFullName()); //所属地区
//						}						
//						map.put("storeName",store.getStoreName());								
//						map.put("contact",hyOrder.getContact()); //联系人
//						HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
//						map.put("creator",creator.getName()); //报名计调
//						String groupOperator=hySupplier.getSupplierName();
//						groupOperator=groupOperator+":";		
//						groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
//						map.put("groupOperator",groupOperator); //接团计调(供应商:计调)	
//						BigDecimal money=hyOrder.getJiusuanMoney();
//						if(hyOrder.getTip()!=null) {
//							money=money.add(hyOrder.getTip());
//						}
//						if(hyOrder.getDiscountedPrice()!=null) {
//							money=money.subtract(hyOrder.getDiscountedPrice());
//						}
//						if(hyOrder.getStoreFanLi()!=null) {
//							money=money.subtract(hyOrder.getStoreFanLi());
//						}
//					    map.put("collectMoney",money);
//					    map.put("collectDay",hyOrder.getCreatetime());
//					    map.put("isCheckAccount",hyOrder.getIsCheckAccount());
//					    list.add(map);
//					}
//				}
//			}
//			int page = pageable.getPage();
//			int rows = pageable.getRows();
//			obj.put("pageNumber", page);
//			obj.put("pageSize", rows);
//			obj.put("total", list.size());
//			obj.put("rows", list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
		    
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(result);		
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败"+e.getMessage());
		}
		return json;
	}
	
	/**确认对账*/
	@RequestMapping(value = "check")
	@ResponseBody
	public Json checkAccount(Long orderId) {
		Json json=new Json();
		try {
			HyOrder hyOrder=hyOrderService.find(orderId);
			hyOrder.setIsCheckAccount(true); //设为已对账
			hyOrderService.update(hyOrder);
			json.setSuccess(true);
			json.setMsg("对账成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("对账失败"+e.getMessage());
		}
		return json;
	}
	
	public static class GroupCollect{
		private String orderNumber;
		private String linePn;
		private String lineName;
		private String groupStartDay;
		private String storeName;
		private String contact;
		private String creator;
		private String groupOperator;
		private String collectMoney;
		private String collectDay;
		private String isCheckAccount;
		private String areaName;
		private String storeType;
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getLinePn() {
			return linePn;
		}
		public void setLinePn(String linePn) {
			this.linePn = linePn;
		}
		public String getLineName() {
			return lineName;
		}
		public void setLineName(String lineName) {
			this.lineName = lineName;
		}
		public String getGroupStartDay() {
			return groupStartDay;
		}
		public void setGroupStartDay(String groupStartDay) {
			this.groupStartDay = groupStartDay;
		}
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}
		public String getCreator() {
			return creator;
		}
		public void setCreator(String creator) {
			this.creator = creator;
		}
		public String getGroupOperator() {
			return groupOperator;
		}
		public void setGroupOperator(String groupOperator) {
			this.groupOperator = groupOperator;
		}
		public String getCollectMoney() {
			return collectMoney;
		}
		public void setCollectMoney(String collectMoney) {
			this.collectMoney = collectMoney;
		}
		public String getCollectDay() {
			return collectDay;
		}
		public void setCollectDay(String collectDay) {
			this.collectDay = collectDay;
		}
		public String getIsCheckAccount() {
			return isCheckAccount;
		}
		public void setIsCheckAccount(String isCheckAccount) {
			this.isCheckAccount = isCheckAccount;
		}
		public String getAreaName() {
			return areaName;
		}
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		public String getStoreType() {
			return storeType;
		}
		public void setStoreType(String storeType) {
			this.storeType = storeType;
		}
	}
	
	/**按团统计收款导出excel*/
	@RequestMapping(value = "excel")
	public String groupExcel(String collectStart,String collectEnd,String groupStart,String groupEnd,String orderNumber,
			String linePn,String lineName,HttpServletRequest request, HttpServletResponse response,Integer isCheckAccount,
			Long areaId,Integer storeType) {
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		List<Filter> filters=new ArrayList<>();
    		filters.add(Filter.eq("type", 1)); //只统计线路订单
    		filters.add(Filter.eq("status", 3)); //统计订单状态为供应商通过
    		filters.add(Filter.eq("source", 0)); //只统计门店
    		if(collectStart!=null && !collectStart.equals("")) {
    			filters.add(new Filter("createtime", Operator.ge,
    					sdf.parse(collectStart.substring(0, 10) + " " + "00:00:00")));
    		}
    		if(collectEnd!=null && !collectEnd.equals("")) {
    			filters.add(new Filter("createtime", Operator.le,
    					sdf.parse(collectEnd.substring(0, 10) + " " + "23:59:59")));
    		}
    		if(groupStart!=null && !groupStart.equals("")) {
    			filters.add(new Filter("fatuandate", Operator.ge,
    					sdf.parse(groupStart.substring(0, 10) + " " + "00:00:00")));
    		}
    		if(groupEnd!=null && !groupEnd.equals("")) {
    			filters.add(new Filter("fatuandate", Operator.le,
    					sdf.parse(groupEnd.substring(0, 10) + " " + "23:59:59")));
    		}
    		if(orderNumber!=null) {
    			filters.add(Filter.eq("orderNumber", orderNumber));
    		}
    		if(lineName!=null) {
    			filters.add(Filter.like("xianlumingcheng", lineName));
    		}
    		if(isCheckAccount!=null) {
				if(isCheckAccount==1) {
					filters.add(Filter.eq("isCheckAccount", true));
				}
				else if(isCheckAccount==0){
					filters.add(Filter.eq("isCheckAccount", false));
				}
			}
    		if(storeType!=null) {
				filters.add(Filter.eq("storeType", storeType));
			}
    		List<Order> orders=new ArrayList<>();
    		orders.add(Order.desc("createtime"));
    		List<HyOrder> hyOrderList=hyOrderService.findList(null,filters,orders);
    		List<GroupCollect> results = new ArrayList<GroupCollect>();
    		for(HyOrder hyOrder:hyOrderList) {
    			GroupCollect tmp=new GroupCollect();
    			HyGroup hyGroup=hyGroupService.find(hyOrder.getGroupId()); //找到团
    			HyLine hyLine=hyGroup.getLine(); //找到线路Pn
    			Store store=storeService.find(hyOrder.getStoreId()); //找到所属门店
    			HySupplier hySupplier=hyLine.getHySupplier(); //找出线路供应商
    			if(linePn!=null && !linePn.equals("")) {
    				if(hyLine.getPn().equals(linePn)) {
    					if(areaId!=null) {
    						HyArea fArea=hyAreaService.find(areaId);
							Boolean flag=false; //判断是否属于所属区域
							Set<HyArea> areaList=new HashSet<>();
							if(fArea!=null) {
								//如果选中的是省级找出该省级下所有的市级区域
								areaList.add(fArea);
								Set<HyArea> seAreaList=fArea.getHyAreas();
								if(!seAreaList.isEmpty()) {
									areaList.addAll(seAreaList);
									for(HyArea area:seAreaList) {
										areaList.add(area);
										Set<HyArea> thirdAreaList=area.getHyAreas();
										if(!thirdAreaList.isEmpty()) {
											areaList.addAll(thirdAreaList);
										}
									}
								}
							}
							
							for(HyArea area:areaList) {
								//门店区域在这个范围内
								if(area.equals(store.getHyArea())) {
									flag=true;
									break;
								}
							}
							if(flag==true) {
								tmp.setOrderNumber(hyOrder.getOrderNumber());
	        					tmp.setLinePn(hyLine.getPn());
	                			tmp.setLineName(hyLine.getName());
	                			if(hyOrder.getFatuandate()==null) {
	                				tmp.setGroupStartDay("");
	                			}
	                			else {
	                				tmp.setGroupStartDay(hyOrder.getFatuandate().toString());
	                			}   			
	                			Long storeId=hyOrder.getStoreId(); //获取门店id
	                			if(storeId!=null) {
	                				
	                				tmp.setStoreName(store.getStoreName());
	                				
	                			}
	                			else {
	                				tmp.setStoreName("");
	                			}
	                			tmp.setContact(hyOrder.getContact()); //联系人
	                			HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
	                			tmp.setCreator(creator.getName()); //报名计调		
	                			String groupOperator=hySupplier.getSupplierName();
	                			groupOperator=groupOperator+":";		
	                			groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
	                			tmp.setGroupOperator(groupOperator); //接团计调(供应商:计调)	
	                			BigDecimal money=hyOrder.getJiusuanMoney();
	                			if(hyOrder.getTip()!=null) {
	                				money=money.add(hyOrder.getTip());
	                			}
	                			if(hyOrder.getDiscountedPrice()!=null) {
	                				money=money.subtract(hyOrder.getDiscountedPrice());
	                			}
	                			if(hyOrder.getStoreFanLi()!=null) {
	                				money=money.subtract(hyOrder.getStoreFanLi());
	                			}
	                			tmp.setCollectMoney(money.toString());
	                			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                			String collectDay=format.format(hyOrder.getCreatetime());
	                		    tmp.setCollectDay(collectDay);
	                		    tmp.setAreaName(store.getHyArea().getFullName());
	                		    if(hyOrder.getStoreType()==0) {
	                		    	tmp.setStoreType("虹宇门店");
	                		    }
	                		    else if(hyOrder.getStoreType()==1) {
	                		    	tmp.setStoreType("挂靠门店");
	                		    }
	                		    else if(hyOrder.getStoreType()==2) {
	                		    	tmp.setStoreType("直营门店");
	                		    }
	                		    else if(hyOrder.getStoreType()==3) {
	                		    	tmp.setStoreType("非虹宇门店");
	                		    }
	                		    else {
	                		    	
	                		    }
	                		    if(true==hyOrder.getIsCheckAccount()) {
	                		    	tmp.setIsCheckAccount("是");
	                		    }
	                		    else {
	                		    	tmp.setIsCheckAccount("否");
	                		    }
	             
	                		    results.add(tmp);
							}
    					}
    					//areaId==null
    					else {
    						tmp.setOrderNumber(hyOrder.getOrderNumber());
        					tmp.setLinePn(hyLine.getPn());
                			tmp.setLineName(hyLine.getName());
                			if(hyOrder.getFatuandate()==null) {
                				tmp.setGroupStartDay("");
                			}
                			else {
                				tmp.setGroupStartDay(hyOrder.getFatuandate().toString());
                			}   			
                			Long storeId=hyOrder.getStoreId(); //获取门店id
                			if(storeId!=null) {
                				
                				tmp.setStoreName(store.getStoreName());
                				
                			}
                			else {
                				tmp.setStoreName("");
                			}
                			tmp.setContact(hyOrder.getContact()); //联系人
                			HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
                			tmp.setCreator(creator.getName()); //报名计调		
                			String groupOperator=hySupplier.getSupplierName();
                			groupOperator=groupOperator+":";		
                			groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
                			tmp.setGroupOperator(groupOperator); //接团计调(供应商:计调)	
                			BigDecimal money=hyOrder.getJiusuanMoney();
                			if(hyOrder.getTip()!=null) {
                				money=money.add(hyOrder.getTip());
                			}
                			if(hyOrder.getDiscountedPrice()!=null) {
                				money=money.subtract(hyOrder.getDiscountedPrice());
                			}
                			if(hyOrder.getStoreFanLi()!=null) {
                				money=money.subtract(hyOrder.getStoreFanLi());
                			}
                			tmp.setCollectMoney(money.toString());
                			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                			String collectDay=format.format(hyOrder.getCreatetime());
                		    tmp.setCollectDay(collectDay);
                		    tmp.setAreaName(store.getHyArea().getFullName());
                		    if(hyOrder.getStoreType()==0) {
                		    	tmp.setStoreType("虹宇门店");
                		    }
                		    else if(hyOrder.getStoreType()==1) {
                		    	tmp.setStoreType("挂靠门店");
                		    }
                		    else if(hyOrder.getStoreType()==2) {
                		    	tmp.setStoreType("直营门店");
                		    }
                		    else if(hyOrder.getStoreType()==3) {
                		    	tmp.setStoreType("非虹宇门店");
                		    }
                		    else {
                		    	
                		    }
                		    if(true==hyOrder.getIsCheckAccount()) {
                		    	tmp.setIsCheckAccount("是");
                		    }
                		    else {
                		    	tmp.setIsCheckAccount("否");
                		    }
             
                		    results.add(tmp);
    					}
    				}		
    			}
    			//linePn==null
    			else {
    				if(areaId!=null) {
    					HyArea fArea=hyAreaService.find(areaId);
						Boolean flag=false; //判断是否属于所属区域
						Set<HyArea> areaList=new HashSet<>();
						if(fArea!=null) {
							//如果选中的是省级找出该省级下所有的市级区域
							areaList.add(fArea);
							Set<HyArea> seAreaList=fArea.getHyAreas();
							if(!seAreaList.isEmpty()) {
								areaList.addAll(seAreaList);
								for(HyArea area:seAreaList) {
									areaList.add(area);
									Set<HyArea> thirdAreaList=area.getHyAreas();
									if(!thirdAreaList.isEmpty()) {
										areaList.addAll(thirdAreaList);
									}
								}
							}
						}
						
						for(HyArea area:areaList) {
							//门店区域在这个范围内
							if(area.equals(store.getHyArea())) {
								flag=true;
								break;
							}
						}
						if(flag==true) {
							tmp.setOrderNumber(hyOrder.getOrderNumber());
	    					tmp.setLinePn(hyLine.getPn());
	            			tmp.setLineName(hyLine.getName());
	            			if(hyOrder.getFatuandate()==null) {
	            				tmp.setGroupStartDay("");
	            			}
	            			else {
	            				tmp.setGroupStartDay(hyOrder.getFatuandate().toString());
	            			}   			
	            			Long storeId=hyOrder.getStoreId(); //获取门店id
	            			if(storeId!=null) {
	            				
	            				tmp.setStoreName(store.getStoreName());
	            				
	            			}
	            			else {
	            				tmp.setStoreName("");
	            			}
	            			tmp.setContact(hyOrder.getContact()); //联系人
	            			HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
	            			tmp.setCreator(creator.getName()); //报名计调		
	            			String groupOperator=hySupplier.getSupplierName();
	            			groupOperator=groupOperator+":";		
	            			groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
	            			tmp.setGroupOperator(groupOperator); //接团计调(供应商:计调)	
	            			BigDecimal money=hyOrder.getJiusuanMoney();
	            			if(hyOrder.getTip()!=null) {
	            				money=money.add(hyOrder.getTip());
	            			}
	            			if(hyOrder.getDiscountedPrice()!=null) {
	            				money=money.subtract(hyOrder.getDiscountedPrice());
	            			}
	            			if(hyOrder.getStoreFanLi()!=null) {
	            				money=money.subtract(hyOrder.getStoreFanLi());
	            			}
	            			tmp.setCollectMoney(money.toString());
	            			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            			String collectDay=format.format(hyOrder.getCreatetime());
	            		    tmp.setCollectDay(collectDay);
	            		    tmp.setAreaName(store.getHyArea().getFullName());
	            		    if(hyOrder.getStoreType()==0) {
	            		    	tmp.setStoreType("虹宇门店");
	            		    }
	            		    else if(hyOrder.getStoreType()==1) {
	            		    	tmp.setStoreType("挂靠门店");
	            		    }
	            		    else if(hyOrder.getStoreType()==2) {
	            		    	tmp.setStoreType("直营门店");
	            		    }
	            		    else if(hyOrder.getStoreType()==3) {
	            		    	tmp.setStoreType("非虹宇门店");
	            		    }
	            		    else {
	            		    	
	            		    }
	            		    if(true==hyOrder.getIsCheckAccount()) {
	            		    	tmp.setIsCheckAccount("是");
	            		    }
	            		    else {
	            		    	tmp.setIsCheckAccount("否");
	            		    }
	         
	            		    results.add(tmp);
						}
					}
    				//areaId==null
    				else {
    					tmp.setOrderNumber(hyOrder.getOrderNumber());
    					tmp.setLinePn(hyLine.getPn());
            			tmp.setLineName(hyLine.getName());
            			if(hyOrder.getFatuandate()==null) {
            				tmp.setGroupStartDay("");
            			}
            			else {
            				tmp.setGroupStartDay(hyOrder.getFatuandate().toString());
            			}   			
            			Long storeId=hyOrder.getStoreId(); //获取门店id
            			if(storeId!=null) {
            				
            				tmp.setStoreName(store.getStoreName());
            				
            			}
            			else {
            				tmp.setStoreName("");
            			}
            			tmp.setContact(hyOrder.getContact()); //联系人
            			HyAdmin creator=hyAdminService.find(hyOrder.getCreatorId());
            			tmp.setCreator(creator.getName()); //报名计调		
            			String groupOperator=hySupplier.getSupplierName();
            			groupOperator=groupOperator+":";		
            			groupOperator=groupOperator+hyOrder.getSupplier().getName(); 
            			tmp.setGroupOperator(groupOperator); //接团计调(供应商:计调)	
            			BigDecimal money=hyOrder.getJiusuanMoney();
            			if(hyOrder.getTip()!=null) {
            				money=money.add(hyOrder.getTip());
            			}
            			if(hyOrder.getDiscountedPrice()!=null) {
            				money=money.subtract(hyOrder.getDiscountedPrice());
            			}
            			if(hyOrder.getStoreFanLi()!=null) {
            				money=money.subtract(hyOrder.getStoreFanLi());
            			}
            			tmp.setCollectMoney(money.toString());
            			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            			String collectDay=format.format(hyOrder.getCreatetime());
            		    tmp.setCollectDay(collectDay);
            		    tmp.setAreaName(store.getHyArea().getFullName());
            		    if(hyOrder.getStoreType()==0) {
            		    	tmp.setStoreType("虹宇门店");
            		    }
            		    else if(hyOrder.getStoreType()==1) {
            		    	tmp.setStoreType("挂靠门店");
            		    }
            		    else if(hyOrder.getStoreType()==2) {
            		    	tmp.setStoreType("直营门店");
            		    }
            		    else if(hyOrder.getStoreType()==3) {
            		    	tmp.setStoreType("非虹宇门店");
            		    }
            		    else {
            		    	
            		    }
            		    if(true==hyOrder.getIsCheckAccount()) {
            		    	tmp.setIsCheckAccount("是");
            		    }
            		    else {
            		    	tmp.setIsCheckAccount("否");
            		    }
         
            		    results.add(tmp);
    				}
    			}		
    		}
    		// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			sb2.append("按团号收款统计报表");
			String fileName = "按团号收款统计报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "groupCollectStatis.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);			
        }
        catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
        return null;
	}
	
	/**
	 * 由父区域的ID得到全部的子区域
	 * @param id
	 * @return
	 */
	@RequestMapping(value="areacomboxlist/view")
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
}
