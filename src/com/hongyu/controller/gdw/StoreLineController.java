package com.hongyu.controller.gdw;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.events.EndDocument;

import com.hongyu.controller.LineMemberModelExcel;
import com.hongyu.controller.gsbing.TicketMemberModelExcel;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.StringUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyProviderRebate;
import com.hongyu.entity.HySpecialtyLabel;
import com.hongyu.entity.HySpecialtyLineLabel;
import com.hongyu.entity.Store;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GroupPlaceholderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyProviderRebateService;
import com.hongyu.service.HySpecialtyLineLabelService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.LineCatagoryService;
import com.hongyu.service.LinePromotionService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants.AuditStatus;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.thoughtworks.xstream.mapper.Mapper.Null;

import com.hongyu.util.DateUtil;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/linePurchase/")
public class StoreLineController {

	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;

	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;

	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;

	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	@Resource(name="GroupPlaceholderServiceImpl")
	GroupPlaceholderService groupPlaceholderService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name = "linePromotionServiceImpl")
	LinePromotionService linePromotionService;
	
	@Resource(name = "hyLineLabelServiceImpl")
	HyLineLabelService hyLineLabelService;
	
	@Resource(name = "hySpecialtyLineLabelServiceImpl")
	HySpecialtyLineLabelService hySpecialtyLineLabelService;
	
	@Resource(name = "hyProviderRebateServiceImpl")
	HyProviderRebateService hyProviderRebateService;
	
	@Resource(name = "lineCatagoryServiceImpl")
	LineCatagoryService lineCatagoryService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, String supplierName, HyLine hyLine, Long areaId, BigDecimal startPrice,
			BigDecimal endPrice, Long labelId,HttpSession session, Integer sorts, String startCity, LineType lineType, Long lineCategoryId) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			while (department.getIsCompany() != null && department.getIsCompany() == false) {
				department = department.getHyDepartment();
			}
			List<Department> departments = new LinkedList<>();
			departments.add(department);
			String[] treePath = department.getTreePath().split(",");
			if (treePath.length > 1) {
				Department zonggongsi = departmentService.find(Long.parseLong(treePath[1]));
				departments.add(zonggongsi);
			}
			// List<Filter> filters1=new LinkedList<>();
			// filters1.add(Filter.in("hyDepartment", departments));
			// List<HyCompany> hyCompanies=hyCompanyService.findList(null,
			// filters1, null);

			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("isTop"));
			orders.add(Order.desc("topEditTime"));
			orders.add(Order.desc("latestGroup"));
			if(sorts != null) {
				if(sorts == 0) {
					orders.add(Order.desc("lowestPrice"));
				}
				else if(sorts == 1) {
					orders.add(Order.asc("lowestPrice"));
				}
				else {
					orders.add(Order.desc("saleCount"));
				}
			}
			pageable.setOrders(orders);
			//pageable.setSort(null);

			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("isSale", IsSaleEnum.yishang));// 1上线
			filters.add(Filter.in("company", departments));
			// filters.add(Filter.eq("isCancel", 0));//0不取消
			if (supplierName != null) {
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.like("supplierName", supplierName));
				List<HySupplier> hySuppliers = hySupplierService.findList(null, filters2, null);
				if (hySuppliers == null || hySuppliers.size() == 0) {
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(new Page<Map<String, Object>>());
					return json;
				} else {
					filters.add(Filter.in("hySupplier", hySuppliers));
				}
			}
			if (areaId != null) {
				HyArea hyArea = hyAreaService.find(areaId);
				if (hyArea != null) {
					Set<HyArea> hyAreas = new HashSet<>();
					hyAreas.addAll(hyArea.getHyAreas());
					hyAreas.add(hyArea);
					filters.add(Filter.in("area", hyAreas));
				}
			}
			if (startPrice != null) {
				filters.add(Filter.ge("lowestPrice", startPrice));
			}
			if (endPrice != null) {
				filters.add(Filter.le("lowestPrice", endPrice));
			}
			if (lineType != null) {
				filters.add(Filter.eq("lineType", lineType));
			}
			if (startCity != null) {
				List<Filter> filtersPrice = new LinkedList<>();
				filtersPrice.add(Filter.like("startplace", startCity));
				List<HyGroupPrice> hyGroupPrices = hyGroupPriceService.findList(null, filtersPrice, null);
				Set<Long> lineIds = new HashSet<>();
				for(HyGroupPrice hyGroupPrice : hyGroupPrices) {
					lineIds.add(hyGroupPrice.getHyGroup().getLine().getId());
				}
				filters.add(Filter.in("id", lineIds));
			}
			
			if(lineCategoryId != null) {
				filters.add(Filter.eq("lineCategory", lineCatagoryService.find(lineCategoryId)));
			}
			//不显示最低外卖价为0的
			filters.add(Filter.ne("lowestPrice", new BigDecimal(0)));
			pageable.setFilters(filters);
			Map<String, Object> obj = new HashMap<String, Object>();
			if (labelId == null) {
				Page<HyLine> page = hyLineService.findPage(pageable, hyLine);
				List<Map<String, Object>> lhm = new ArrayList<>();
				for (HyLine tmp : page.getRows()) {
					if(tmp.getLowestPrice().equals(new BigDecimal(0))) {
						continue;
					}
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("id", tmp.getId());
					hm.put("pn", tmp.getPn());
					hm.put("name", tmp.getName());
					hm.put("lineType", tmp.getLineType());
					hm.put("latestGroup", tmp.getLatestGroup());
					HySupplierContract contract = tmp.getContract();
					HySupplier hySupplier = contract.getHySupplier();
					hm.put("supplierName", hySupplier.getSupplierName());
					hm.put("area", tmp.getArea());
					hm.put("days", tmp.getDays());
					hm.put("isTop", tmp.getIsTop());
					hm.put("lowestPrice", tmp.getLowestPrice());
					hm.put("rebate", tmp.getContract().getRebate());
					hm.put("bargainRebate", tmp.getContract().getBargainRebate());
					hm.put("isPromotion", tmp.getIsPromotion());
					List<HyLineLabel> hyLineLabels = hyLineLabelService.getLabelsByLine(tmp);
					List<String> labelNames = new ArrayList<>();
					for(HyLineLabel hyLineLabel:hyLineLabels) {
						labelNames.add(hyLineLabel.getProductName());
					}
					if(!labelNames.isEmpty()) {
						String labelStr = StringUtils.join(labelNames,",");
						
						hm.put("lineLabels",labelStr);
					}else {
						hm.put("lineLabels","");
					}

					lhm.add(hm);
				}
				// Collections.sort(lhm, new Comparator<Map<String, Object>>() {
				// @Override
				// public int compare(Map<String, Object> o1, Map<String,
				// Object> o2) {
				// Date date1 = (Date) o1.get("latestGroup");
				// Date date2 = (Date) o2.get("latestGroup");
				// return date1.compareTo(date2);
				// }
				// });
				// Collections.reverse(lhm);
				
				obj.put("total", page.getTotal());
				obj.put("rows", lhm);
				
			}else{
				List<HyLine> hyLines=hyLineService.findList(null, filters, orders);
				HyLineLabel hyLineLabel = hyLineLabelService.find(labelId);
				
				List<Filter> filters2 = new LinkedList<>();
				filters2.add(Filter.eq("hyLabel", hyLineLabel));
				
				List<HySpecialtyLineLabel> lineLabels = hySpecialtyLineLabelService.findList(null,filters2,null);
				
				List<Long> labeledLineId = new LinkedList<>();
				for(HySpecialtyLineLabel hySpecialtyLineLabel:lineLabels){
					labeledLineId.add(hySpecialtyLineLabel.getHyLine().getId());
				}
				
				List<Map<String, Object>> result=new LinkedList<>();
				for(HyLine tmp:hyLines){
					if(!labeledLineId.contains(tmp.getId())){
						continue;
					}
					if(tmp.getLowestPrice().equals(new BigDecimal(0))) {
						continue;
					}
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("id", tmp.getId());
					hm.put("pn", tmp.getPn());
					hm.put("name", tmp.getName());
					hm.put("lineType", tmp.getLineType());
					hm.put("latestGroup", tmp.getLatestGroup());
					HySupplierContract contract = tmp.getContract();
					HySupplier hySupplier = contract.getHySupplier();
					hm.put("supplierName", hySupplier.getSupplierName());
					hm.put("area", tmp.getArea());
					hm.put("days", tmp.getDays());
					hm.put("lowestPrice", tmp.getLowestPrice());
					hm.put("isTop", tmp.getIsTop());
					List<HyLineLabel> hyLineLabels = hyLineLabelService.getLabelsByLine(tmp);
					List<String> labelNames = new ArrayList<>();
					for(HyLineLabel hyLineLabel1:hyLineLabels) {
						labelNames.add(hyLineLabel1.getProductName());
					}
					if(!labelNames.isEmpty()) {
						String labelStr = StringUtils.join(labelNames,",");
						
						hm.put("lineLabels",labelStr);
					}else {
						hm.put("lineLabels","");
					}
					result.add(hm);	
				}
				int page = pageable.getPage();
				int rows = pageable.getRows();
				obj.put("total", result.size());
				obj.put("rows", result.subList((page-1)*rows, page*rows>result.size()?result.size():page*rows));
			}
			obj.put("pageSize", pageable.getPage());
			obj.put("pageNumber", pageable.getRows());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyLine hyLine = hyLineService.find(id);
			if (hyLine != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", hyLine.getId());
				map.put("createDate", hyLine.getCreateDate());
				map.put("insurance", hyLine.getInsurance());
				map.put("isHeadInsurance", hyLine.getIsHeadInsurance());
				map.put("modifyDate", hyLine.getModifyDate());
				// map.put("hySupplier", hyLine.getHySupplier());
				map.put("supplierName", hyLine.getHySupplier().getSupplierName());
				map.put("supplierContact", hyLine.getOperator().getName());
				map.put("supplierPhone", hyLine.getOperator().getMobile());
				map.put("area", hyLine.getArea());
				map.put("refundType", hyLine.getRefundType());
				map.put("name", hyLine.getName());
				map.put("lineType", hyLine.getLineType());
				map.put("pn", hyLine.getPn());
				map.put("lineCategory", hyLine.getLineCategory());
				map.put("operator", hyLine.getOperator());
				map.put("days", hyLine.getDays());
				map.put("isInsurance", hyLine.getIsInsurance());
				map.put("cancelMemo", hyLine.getCancelMemo());
				map.put("outboundMemo", hyLine.getOutboundMemo());
				map.put("memoInner", hyLine.getMemoInner());
				map.put("memo", hyLine.getMemo());
				map.put("introduction", hyLine.getIntroduction());
				map.put("lineFile", hyLine.getLineFile());
				map.put("outbound", hyLine.getOutbound());
				map.put("lineTravels", hyLine.getLineTravels());
				map.put("lineRefunds", hyLine.getLineRefunds());
				map.put("lowestPrice", hyLine.getLowestPrice());
				map.put("rebate", hyLine.getContract().getRebate());
				map.put("isPromotion", hyLine.getIsPromotion());
				map.put("isAutoconfirm", hyLine.getIsAutoconfirm());
				map.put("memoDetail", hyLineService.getMemoDetail(hyLine));

				
				List<HyLineLabel> hyLineLabels = hyLineLabelService.getLabelsByLine(hyLine);
				List<String> labelNames = new ArrayList<>();
				for(HyLineLabel hyLineLabel1:hyLineLabels) {
					labelNames.add(hyLineLabel1.getProductName());
				}
				if(!labelNames.isEmpty()) {
					String labelStr = StringUtils.join(labelNames,",");
					
					map.put("lineLabels",labelStr);
				}else {
					map.put("lineLabels","");
				}
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(map);
			} else {
				json.setSuccess(false);
				json.setMsg("查询失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping(value = "areacomboxlist/view")
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if (parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if (child.getStatus()) {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return j;
	}

	@RequestMapping("getGroups")
	@ResponseBody
	public Json getGroups(Pageable pageable, Long id, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			HyLine line = hyLineService.find(id);
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("line", line));
			filters.add(Filter.ge("startDay", new Date()));
			filters.add(Filter.eq("groupState", GroupStateEnum.daichutuan));
			filters.add(Filter.eq("isDisplay", true));
			filters.add(Filter.eq("auditStatus", AuditStatus.pass));
			List<Order> orders = new LinkedList<>();
			orders.add(Order.asc("startDay"));
			List<HyGroup> lists = hyGroupService.findList(null, filters, orders);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyGroup tmp : lists) {
				if (tmp.getPublishStore() != null && tmp.getPublishStore().getId() != store.getId()) {
					continue;
				}
				if(tmp.getStock().equals(0)) {
					continue;
				}
				Map<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				map.put("startDate", tmp.getStartDay());
				map.put("stock", tmp.getStock());
				map.put("fanliMoney", tmp.getFanliMoney());
				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("total", lists.size());
			hMap.put("pageNumber", page);
			hMap.put("pageSize", rows);
			hMap.put("rows",
					result.subList((page - 1) * rows, page * rows > result.size() ? result.size() : page * rows));
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("getLabels/view")
	@ResponseBody
	public Json getLabels(){
		Json json=new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("isActive", true));
			List<HyLineLabel> labels=hyLineLabelService.findList(null,filters,null);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(labels);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("getPrices")
	@ResponseBody
	public Json getPrices(Long id) {
		Json json = new Json();
		try {
			// String
			// username=(String)session.getAttribute(CommonAttributes.Principal);
			// HyAdmin hyAdmin=hyAdminService.find(username);
			// Store store=storeService.findStore(hyAdmin);
			// HyLine line=hyLineService.find(id);
			// List<Filter> filters=new LinkedList<>();
			// filters.add(Filter.eq("line", line));
			// filters.add(Filter.eq("startDay", startDate));
			// filters.add(Filter.eq("isDisplay", true));
			// List<HyGroup> lists=hyGroupService.findList(null, filters, null);
			// if(lists==null||lists.size()==0||(lists.get(0).getPublishStore()!=null&&!lists.get(0).getPublishStore().getId().equals(store.getId()))){
			// json.setSuccess(true);
			// json.setMsg("团期不存在");
			// }else{
			HyGroup hyGroup = hyGroupService.find(id);
			if (hyGroup == null) {
				json.setSuccess(false);
				json.setMsg("团期不存在");
			} else {
				Map<String, Object> map = new HashMap<>();
				map.put("id", hyGroup.getId());
				map.put("line", hyGroup.getLine());
				map.put("startDate", hyGroup.getStartDay());
				map.put("endDate", hyGroup.getEndDay());
				map.put("teamType", hyGroup.getTeamType());
				map.put("isCoupon", hyGroup.getIsCoupon());
				map.put("isEveryone", hyGroup.getIsEveryone());
				map.put("couponMoney", hyGroup.getCouponMoney());
				map.put("stock", hyGroup.getStock());
				map.put("signupNumber", hyGroup.getSignupNumber());
				map.put("occupyNumber", hyGroup.getOccupyNumber());
				map.put("groupState", hyGroup.getGroupState());
				map.put("saleTimes", hyGroup.getSaleTimes());
				map.put("isCancel", hyGroup.getIsCancel());
				map.put("koudianType", hyGroup.getKoudianType());
				map.put("percentageKoudian", hyGroup.getPercentageKoudian());
				map.put("personKoudian", hyGroup.getPersonKoudian());
				map.put("isSpecialKoudian", hyGroup.getIsSpecialKoudian());
				map.put("lowestPrice", hyGroup.getLowestPrice());
				map.put("isAdult", hyGroup.getIsAdult());
				map.put("isChild", hyGroup.getIsChild());
				map.put("isStudent", hyGroup.getIsStudent());
				map.put("isOld", hyGroup.getIsOld());
				map.put("isDanfangcha", hyGroup.getIsDanfangcha());
				map.put("isBuwopu", hyGroup.getIsBuwopu());
				map.put("isBumenpiao", hyGroup.getIsBumenpiao());
				map.put("isErtongzhanchuang", hyGroup.getIsErtongzhanchuang());
				map.put("isBuchuangwei", hyGroup.getIsBuchuangwei());
				map.put("isErtongzhanchuang", hyGroup.getIsErtongzhanchuang());

				map.put("remainNumber", hyGroup.getRemainNumber());
				map.put("hyGroupSpecialprices", hyGroup.getHyGroupSpecialprices());
				map.put("hyGroupPrices", hyGroup.getHyGroupPrices());
				map.put("hyGroupOtherprices", hyGroup.getHyGroupOtherprices());

				map.put("groupLineType", hyGroup.getGroupLineType());
				map.put("groupLineName", hyGroup.getGroupLineName());
				map.put("groupLinePn", hyGroup.getGroupLinePn());
				map.put("operatorName", hyGroup.getOperatorName());
				
				if (hyGroup.getIsPromotion()!=null&&hyGroup.getIsPromotion()==true) {
					LinePromotion linePromotion=linePromotionService.findByGroupId(hyGroup.getId());
					//System.out.println("这个团的线路促销是linePromotionId: "+linePromotion.getId());
					if(linePromotion==null){
						map.put("isPromotion", false);
					}else{
						map.put("isPromotion", true);
					}
					map.put("linePromotion",linePromotion);
				}else{
					//System.out.println("linePromotion没有找到");
					map.put("linePromotion", null);
					map.put("isPromotion", false);
				}
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(map);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	static class WrapOrder{
		private Long placeHolder;
		private HyOrder order;
		public Long getPlaceHolder() {
			return placeHolder;
		}
		public void setPlaceHolder(Long placeHolder) {
			this.placeHolder = placeHolder;
		}
		public HyOrder getOrder() {
			return order;
		}
		public void setOrder(HyOrder order) {
			this.order = order;
		}
		
	}
	
	@RequestMapping("order")
	@ResponseBody
	public Json order(@RequestBody WrapOrder wrapOrder, HttpSession session) {
		Json json = new Json();
		try {
			Long placeHolder=wrapOrder.getPlaceHolder();
			HyOrder order=wrapOrder.getOrder();
			json = hyOrderService.addLineOrder(placeHolder,order, session);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("下单失败： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("getInsurances/view")
	@ResponseBody
	public Json getInsurances(Pageable pageable,Long groupId){
		Json json=new Json();
		try {
			HyGroup hyGroup=hyGroupService.find(groupId);
			LineType lineType=hyGroup.getLine().getLineType();
			Integer classify;
			if(lineType.equals(LineType.chujing)){
				classify=1;
			}else{
				classify=0;
			}
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("classify", classify));
			Integer days=hyGroup.getLine().getDays();
			pageable.setFilters(filters);
			Page<Insurance> page=insuranceService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(Insurance tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("createDate", tmp.getCreateDate());
				map.put("modifyDate", tmp.getModifyDate());
				map.put("classify", tmp.getClassify());
				map.put("insuranceCode", tmp.getInsuranceCode());
				map.put("remark", tmp.getRemark());
				map.put("insuranceAttachs", tmp.getInsuranceAttachs());
				List<InsurancePrice> insurancePrices=new LinkedList<>();
				for(InsurancePrice price:tmp.getInsurancePrices()){
					if(days.compareTo(price.getStartDay())>=0&&days.compareTo(price.getEndDay())<=0){
						insurancePrices.add(price);
					}
				}
				map.put("insurancePrices", insurancePrices);
				map.put("insuranceTimes", tmp.getInsuranceTimes());
				result.add(map);
			}
			Page<Map<String, Object>>page2=new Page<>(result, page.getTotal(), pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page2);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("addPlaceHolder")
	@ResponseBody
	public Json addplaceHolder(Long groupId, Integer number, HttpSession session) {
		Json json = new Json();
		try {
			json=groupPlaceholderService.addPlaceHolder(groupId, number, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("占位错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("deletePlaceHolder")
	@ResponseBody
	public Json deletePlaceHolder(Long id){
		Json json=new Json();
		try {
			json=groupPlaceholderService.deletePlaceHolder(id);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("清除错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("plactHolderList/view")
	@ResponseBody
	public Json placeHolderList(Pageable pageable,Long groupId,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			HyGroup hyGroup=hyGroupService.find(groupId);
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("creator", hyAdmin));
			filters.add(Filter.eq("group", hyGroup));
			filters.add(Filter.eq("status", false));
			pageable.setFilters(filters);
			Page<GroupPlaceholder> page=groupPlaceholderService.findPage(pageable);
			List<Map<String, Object>>result=new LinkedList<>();
			for(GroupPlaceholder tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("name", tmp.getGroup().getGroupLineName());
				map.put("startDate", tmp.getGroup().getStartDay());
				map.put("number", tmp.getNumber());
				map.put("operator", tmp.getCreator().getName());
				map.put("createTime", new Date());
				result.add(map);
			}
			int pg=page.getPageNumber();
			int rows=page.getPageSize();
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", result.size());
			hMap.put("pageNumber", pg);
			hMap.put("pageSize", rows);
			hMap.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@Resource(name="hyOrderCustomerServiceImpl")
	HyOrderCustomerService hyOrderCustomerService;
	
	@RequestMapping("order_customer/info/view")
	@ResponseBody
	public Json orderCustomerInfo(Integer certificateType,String certificate,Long productId,
			Date startDate,Date endDate) {
		Json json = new Json();
		try {
			if(certificateType==null || certificate==null) {
				json.setSuccess(false);
				json.setMsg("缺少证件类型和证件号");
				json.setObj(null);
				return json;
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder sb = new StringBuilder("select hy_order_customer.certificate_type,hy_order_customer.certificate,"
					+ "hy_order_item.product_id,hy_order_item.start_date,hy_order_item.end_date"
					+ " from hy_order_customer,hy_order_item,hy_order"
					+ " where hy_order_customer.item_id=hy_order_item.id" +
				" and hy_order.id=hy_order_item.order_id and hy_order.type=1 and hy_order.status<5");
			sb.append(" and hy_order_customer.certificate_type="+certificateType);
			sb.append(" and hy_order_customer.certificate='"+certificate+"'");
			
			if(productId!=null) {
				sb.append(" and hy_order_item.product_id="+productId);
			}
			if(startDate!=null) {
				String startStr=format.format(startDate);
				sb.append(" and DATE_FORMAT(hy_order_item.end_date,'%Y-%m-%d')>='"+startStr+"'");
				
			}
			if(endDate!=null) {
				String endStr = format.format(endDate);
				sb.append(" and DATE_FORMAT(hy_order_item.start_date,'%Y-%m-%d')<='"+endStr+"'");
			}
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderCustomerService.statis(jpql);
			if(list!=null && !list.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("同一证件号不能同时报名重叠团期！");
				json.setObj(list);
				return json;
			}
			
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("certificateType",certificateType));
			filters.add(Filter.eq("certificate", certificate));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<HyOrderCustomer> customers = hyOrderCustomerService.findList(null,filters,orders);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			if(customers==null || customers.isEmpty()) {
				json.setObj(null);
			}else {
				json.setObj(customers.get(0));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}


	/**
	 * 下载线路人员模板
	 * @author Wayne
	 */
	@RequestMapping(value = "store_line_order/get_excel")
	//@ResponseBody
	public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
//		response.setContentType("text/html;charset=utf-8");
//        response.setCharacterEncoding("utf-8");
		try {
			//C:\Users\LBC\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\hy_backend\download\投保人员信息表.xls
			String filefullname =System.getProperty("hongyu.webapp") + "download/旅游批量导入游客信息模板.xls";
			String fileName = "线路游客信息表.xls";
			File file = new File(filefullname);
			System.out.println(filefullname);
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {
				request.setAttribute("message", "下载失败");
				return;

			} else {

				// 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
				response.setHeader("content-disposition",
						"attachment;filename=" + URLEncoder.encode("线路游客信息表.xls", "utf-8"));




				response.setHeader("content-disposition",
						"attachment;" + "filename=" + URLEncoder.encode(fileName, "UTF-8"));

				response.setHeader("Connection", "close");
				response.setHeader("Content-Type", "application/vnd.ms-excel");

				//String zipfilefullname = userdir + zipFileName;
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ServletOutputStream sos = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(sos);

				byte[] bytes = new byte[1024];
				int i = 0;
				while ((i = bis.read(bytes, 0, bytes.length)) != -1) {
					bos.write(bytes);
				}
				bos.flush();
				bis.close();
				bos.close();
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("message", "出现错误");
			e.printStackTrace();
		}
		return;

	}


	/**
	 * 上传订购线路游客信息表模板
	 * @author Wayne
	 * @date 20190720
	 */
	@RequestMapping(value = "store_line_order/upload_excels")
	@ResponseBody
	public Json UploadExcel(@RequestParam MultipartFile[] files) {

		Json json = new Json();
		try {
			if(files == null || files[0] == null) {
				json.setMsg("未接收到文件");
				json.setSuccess(false);
				json.setObj(null);
			}
			MultipartFile file = files[0];

			return UploadExcel(file);

		}
		catch (Exception e) {
			// TODO: handle exception
			json.setMsg("文件读取失败");
			json.setSuccess(false);
			json.setObj(null);

		}
		return json;

	}
	@RequestMapping(value = "store_line_order/upload_excel")
	@ResponseBody
	public Json UploadExcel(@RequestParam MultipartFile file) {
		Json json = new Json();
		try {
//			if(files == null || files[0] == null) {
//				json.setMsg("未接收到文件");
//				json.setSuccess(false);
//				json.setObj(null);
//			}
//			MultipartFile file = files[0];

			List<LineMemberModelExcel.LineMember> members = LineMemberModelExcel.readMemberExcel(file.getInputStream());

			json.setObj(members);
			json.setMsg("文件读取成功");
			json.setSuccess(true);
		}
		catch (Exception e) {
			// TODO: handle exception
			json.setMsg("文件读取失败");
			json.setSuccess(false);
			json.setObj(null);

		}
		return json;

	}


}
