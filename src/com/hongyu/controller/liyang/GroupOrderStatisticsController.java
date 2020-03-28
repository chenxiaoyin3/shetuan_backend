package com.hongyu.controller.liyang;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.controller.BaseController;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.FinishedGroupItemOrder;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.Store;
import com.hongyu.service.FinishedGroupItemOrderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.DateUtil;
import com.hongyu.util.liyang.ExcelHelper;

import oracle.net.aso.p;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 接团统计表
 * @author liyang
 * @version 2019年5月6日 下午5:31:09
 */
@Controller
@RequestMapping("/admin/group/statistics")
public class GroupOrderStatisticsController {

	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "finishedGroupItemOrderServiceImpl")
	FinishedGroupItemOrderService finishedGroupItemOrderService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
	/**
	 * 当日接团统计
	 * @param request
	 * @param response
	 * @param session
	 * @param storeName 门店名称
	 * @param leftFtDate 发团日期下界
	 * @param rightFtDate 发团日期上界
	 * @param lineName 线路名称
	 * @param linePn 线路产品编号
	 * @param storeOperator 报名计调
	 * @param providerOperator 接团计调
	 * @return
	 */
	@RequestMapping("/reception/today")
	@ResponseBody
	public Json todayGroupReceptionStatistics(HttpServletRequest request,
			HttpServletResponse response,HttpSession session,
			String storeName,@DateTimeFormat(iso=ISO.DATE) Date leftFtDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightFtDate,@DateTimeFormat(iso=ISO.DATE) Date leftCreateDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightCreateDate,
			String lineName,String linePn,String storeOperator,
			String providerOperator,Long areaId,Integer storeType){
		Json json = new Json();
		// TODO 判断当前用户是否有访问权限(暂时由前端赋权控制，这里不判断)
		/**
		 * 三步走战略
		 * 1、找到所有今天的线路订单以及对应的团id
		 * 2、根据每个团id去筛选所有的订单，来计算结果
		 * 3、返回结果
		 */
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.eq("status", 3));
			/*只筛选来自门店的订单*/
			filters.add(Filter.eq("source", 0));
			filters.add(Filter.eq("paystatus", 1));
			if(leftFtDate != null){
				Date ltoday = DateUtil.getStartOfDay(leftFtDate);
				filters.add(Filter.ge("fatuandate", ltoday));
			}
			if(rightFtDate != null){
				Date rtoday = DateUtil.getEndOfDay(rightFtDate);
				filters.add(Filter.le("fatuandate", rtoday));
			}
			
			if(leftCreateDate != null){
				Date ltoday = DateUtil.getStartOfDay(leftCreateDate);
				filters.add(Filter.ge("createtime", ltoday));
			}else{
				Date ltoday = DateUtil.getStartOfDay(new Date());
				filters.add(Filter.ge("createtime", ltoday));
			}
			if(rightCreateDate != null){
				Date rtoday = DateUtil.getEndOfDay((rightCreateDate));
				filters.add(Filter.le("createtime", rtoday));
			}else{
				Date rtoday = DateUtil.getEndOfDay(new Date());
				filters.add(Filter.le("createtime", rtoday));
			}
			
			
			if(lineName != null){
				filters.add(Filter.like("xianlumingcheng", lineName));
			}
			
			if(storeType != null){
				filters.add(Filter.eq("storeType", storeType));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("fatuandate"));
			List<HyOrder> hyOrders = hyOrderService.findList(null,filters,orders);
			/**筛选订单，以groupId为key存储到Map中*/
			TreeMap<Long, List<HyOrder>> groupMap = new TreeMap<>();
			for(HyOrder order:hyOrders){
				HyGroup group = hyGroupService.find(order.getGroupId());
				if(group==null)
					throw new Exception("找不到指定团主键id为："+order.getGroupId()+" 为空！");
				Store store = storeService.find(order.getStoreId());
				if(store==null)
					throw new Exception("订单 "+order.getOrderNumber()+" 的门店找不到");
				/*筛选符合条件的数据**/
				if (areaId != null && !store.getHyArea().getId().equals(areaId)) {
					continue;
				}
				if(storeName!=null && !store.getStoreName().contains(storeName)){
					continue;
				}
				if(linePn!=null && !group.getGroupLinePn().contains(linePn)){
					continue;
				}
				if(storeOperator!=null && !order.getOperator().getName().contains(storeOperator)){
					continue;
				}
				if(providerOperator!=null && !group.getLine().getOperator().getName().contains(providerOperator)){
					continue;
				}
				/*放入map**/
				if(groupMap.containsKey(order.getGroupId())){
					groupMap.get(order.getGroupId()).add(order);
				}else{
					List<HyOrder> groupOrders = new ArrayList<>();
					groupOrders.add(order);
					groupMap.put(order.getGroupId(), groupOrders);
				}
			}
			
			/*遍历map，对每个group进行统计**/
			int allAdultNum = 0;
			int allChildNum = 0;
			BigDecimal allBaomingshouru = BigDecimal.ZERO;
			BigDecimal allDantuanshouru = BigDecimal.ZERO;
			BigDecimal allDantuanchengben = BigDecimal.ZERO;
			BigDecimal allDantuanlirun = BigDecimal.ZERO;
			BigDecimal allRenjunlirun = BigDecimal.ZERO;
			BigDecimal allLirunlv = BigDecimal.ZERO;
			List<Map<String, Object>> result = new ArrayList<>();
			for(Map.Entry<Long,List<HyOrder>> entry:groupMap.entrySet()){
				/*每一个团，对当日的订单数据进行统计*/
				Long groupId = entry.getKey();
				List<HyOrder> groupOrders = entry.getValue();
				List<HashMap<String,Object>> list = new ArrayList<>();
				HyGroup group = hyGroupService.find(groupId);
				if(group==null)
					throw new Exception("找不到指定团主键id为："+groupId+" 为空！");
				String supplierName =  group.getLine().getHySupplier().getSupplierName();
				for(HyOrder order:groupOrders){
					HashMap<String, Object> tmp = new HashMap<>();
					Store store = storeService.find(order.getStoreId());
					if(store==null)
						throw new Exception("订单 "+order.getOrderNumber()+" 的门店找不到");
					tmp.put("groupId", order.getGroupId());
					tmp.put("lineName", group.getGroupLineName());
					tmp.put("linePn", group.getGroupLinePn());
					tmp.put("supplierName",supplierName);
					tmp.put("fatuandate", group.getStartDay());
					tmp.put("huituandate", group.getEndDay());
					tmp.put("storeName", store.getStoreName());
					tmp.put("area", store.getHyArea().getFullName());
					tmp.put("storeType", store.getStoreType()==0?"虹宇门店":"非虹宇门店");
					List<HyOrderItem> orderItems = order.getOrderItems();
					int adult = 0;
					int child = 0;
					for(HyOrderItem item:orderItems){
						if(item.getType()==1 && item.getPriceType()==1 ){
							child += (item.getNumber()-item.getNumberOfReturn());
						}
						if(item.getType()==1 && item.getPriceType()!=1){
							//System.out.println("item id = "+item.getId()+" type = "+item.getType()+" priceType = "+item.getPriceType());
							adult += (item.getNumber()-item.getNumberOfReturn());
						}			
					}
					StringBuilder sb = new StringBuilder();
					if(adult>0){
						sb.append(adult+"大");
					}
					if(child>0){
						sb.append(child+"小");
					}
					tmp.put("people", sb.toString());
					allAdultNum += adult;
					allChildNum += child;
					tmp.put("jiepairen", order.getContact());
					if(order.getOperator()==null)
						throw new Exception("该订单报名计调为空！");
					tmp.put("baomingjidiao", order.getOperator().getName());
					if(order.getSupplier()==null)
						throw new Exception("该订单对应的接团计调为空！");
					tmp.put("jietuanjidiao", order.getSupplier().getName());
					/*单团收入，内部供应商 结算价减去退款价再减去返利，外部供应商就是结算价*/
					BigDecimal baomingshouru = null;

					baomingshouru = order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())
								.subtract(order.getStoreFanLi()==null?BigDecimal.ZERO:order.getStoreFanLi());
					
					tmp.put("baomingshouru", baomingshouru);
					allBaomingshouru = allBaomingshouru.add(baomingshouru);
					list.add(tmp);
					
				}
				
				/*算团的当前报名订单总和*/
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("groupId", groupId));
				filters2.add(Filter.eq("type", 1));
				filters2.add(Filter.eq("status", 3));
				filters2.add(Filter.eq("paystatus", 1));
				List<HyOrder> historyOrders = hyOrderService.findList(null,filters2,null);
				/*计算单团收入*/
				BigDecimal dantuanshouru = BigDecimal.ZERO;
				
				boolean isInner = group.getLine().getIsInner();
				if(isInner){
					//如果是内部供应商,计算单团收入时需要加上计调报账的其他各种收入
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							//如果已经计调报账了，直接获取当前团的所有收入
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanshouru = dantuanshouru.add(regulategroupAccount.getAllIncome());
						}else{
							//如果还没有计调报账，就按照单纯下单收入计算
							for(HyOrder hOrder:historyOrders){
								dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
										.subtract(hOrder.getJiesuanTuikuan())
										.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
							}
						}
					}
				}else{
					for(HyOrder hOrder:historyOrders){
						dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
								.subtract(hOrder.getJiesuanTuikuan())
								.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
					}
				}
				/*计算单团成本，分为内外部供应商*/		
				BigDecimal dantuanchengben = BigDecimal.ZERO;
				BigDecimal dantuanlirun = BigDecimal.ZERO;
				BigDecimal renjunlirun = BigDecimal.ZERO;
				BigDecimal lirunlv = BigDecimal.ZERO;
				if(isInner){
					//如果该团是内部供应商，从计调报账的单团核算表中获取成本
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanchengben = dantuanchengben.add(regulategroupAccount.getAllExpense());
							dantuanlirun = dantuanlirun.add(regulategroupAccount.getProfit());
							if(regulategroupAccount.getAverageProfit()!=null){
								renjunlirun = renjunlirun.add(regulategroupAccount.getAverageProfit());
								if(dantuanchengben.compareTo(BigDecimal.ZERO)!=0){
									lirunlv = dantuanlirun.divide(dantuanchengben,8,RoundingMode.CEILING);
								}
							}
						}
					}
					
				}else{
					//如果是外部供应商，成本和收入相同
					dantuanchengben = dantuanchengben.add(dantuanshouru);
				}
				
				/*给list中的行添加共有字段*/
				for(HashMap<String, Object> map:list){
					map.put("dantuanshouru", dantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanchengben", dantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanlirun", dantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("renjunlirun", renjunlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("lirunlv", lirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
				}
				allDantuanshouru = allDantuanshouru.add(dantuanshouru);
				allDantuanchengben = allDantuanchengben.add(dantuanchengben);
				allDantuanlirun = allDantuanlirun.add(dantuanlirun);
				/*将该团的所有条目加入到结果集中*/
				result.addAll(list);
			}
			HashMap<String,Object> ans = new HashMap<>();
			ans.put("result", result);
			StringBuilder sb = new StringBuilder();
			if(allAdultNum>0){
				sb.append(allAdultNum+"大");
			}
			if(allChildNum>0){
				sb.append(allChildNum+"小");
			}
			ans.put("allPeopleNum", sb.toString());
			ans.put("total", result.size());
			ans.put("allBaomingshouru", allBaomingshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanshouru", allDantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanchengben", allDantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanlirun", allDantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
			if(allAdultNum+allChildNum>0){
				ans.put("allRenjunlirun", allDantuanlirun.divide(new BigDecimal(allAdultNum+allChildNum),8,RoundingMode.CEILING).setScale(3, BigDecimal.ROUND_HALF_UP));
			}else{
				ans.put("allRenjunlirun", allRenjunlirun);
			}
			if(!allDantuanchengben.equals(BigDecimal.ZERO)){
				allLirunlv = allDantuanlirun.divide(allDantuanchengben,8,RoundingMode.CEILING).setScale(3, BigDecimal.ROUND_HALF_UP);
			}
			ans.put("allLirunlv", allLirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
			json.setMsg("查询成功");
			json.setObj(ans);
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
			json.setSuccess(false);
		}
		
		return json;
	}
	/**
	 * 已回团的单团利润统计
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping("/profit")
	@ResponseBody
	public Json groupProfitStatistics(Pageable pageable,HttpServletRequest request,
			HttpServletResponse response,HttpSession session,
			String storeName,@DateTimeFormat(iso=ISO.DATE) Date leftFtDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightFtDate,
			String lineName,String linePn,String storeOperator,
			String providerOperator){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if(leftFtDate!=null){
				Date ltoday = DateUtil.getStartOfDay(leftFtDate);
				filters.add(Filter.ge("fatuantime", ltoday));
			}
			if(rightFtDate!=null){
				Date rtoday = DateUtil.getEndOfDay(rightFtDate);
				filters.add(Filter.le("fatuantime", rtoday));
			}
			if(lineName != null){
				filters.add(Filter.like("lineName", lineName));
			}
			if(linePn != null){
				filters.add(Filter.like("linePn", linePn));
			}
			if(providerOperator != null){
				filters.add(Filter.like("supplierName", providerOperator));
			}
			if(storeName!=null){
				filters.add(Filter.like("storeName", storeName));
			}
			if(storeOperator!=null){
				filters.add(Filter.like("storeOperatorName", storeOperator));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("fatuantime"));
			List<FinishedGroupItemOrder> fgios = finishedGroupItemOrderService.findList(null,filters,orders);
			List<HashMap<String, Object>> list = new ArrayList<>();
			for(FinishedGroupItemOrder fgio:fgios){
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", fgio.getId());
				map.put("groupId", fgio.getGroupId());
				map.put("lineName", fgio.getLineName());
				map.put("linePn", fgio.getLinePn());
				map.put("hySupplier", fgio.getHySupplier());
				map.put("orderNumber",fgio.getOrderNumber());
				map.put("fatuantime", fgio.getFatuantime());
				map.put("huituantime", fgio.getHuituantime());
				map.put("storeName", fgio.getStoreName());
				map.put("storeOperatorName", fgio.getStoreOperatorName());
				map.put("supplierName", fgio.getSupplierName());
				StringBuilder sBuilder =new StringBuilder();
				if(fgio.getAdultNumber()>0){
					sBuilder.append(fgio.getAdultNumber()).append("大");
				}
				if(fgio.getChildNumber()>0){
					sBuilder.append(fgio.getChildNumber()).append("小");
				}
				map.put("people", sBuilder.toString());
				map.put("orderIncome", fgio.getOrderIncome().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupIncome", fgio.getGroupIncome().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupExpend", fgio.getGroupExpend().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupProfit", fgio.getGroupProfit().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupAverageProfit", fgio.getGroupAverageProfit().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupProfitMargin", fgio.getGroupProfitMargin().multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP));
				list.add(map);
			}
			HashMap<String , Object> result = new HashMap<>();
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			result.put("total", list.size());
			result.put("pageNumber", pg);
			result.put("pageSize", rows);
			result.put("rows", list.subList((pg-1)*rows, pg*rows>list.size()?list.size():pg*rows));
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setMsg("查询失败："+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	/**
	 * 将所有的order表中的xianlumingcheng字段设置正确的值
	 * @return
	 */
	@RequestMapping("/init")
	@ResponseBody
	public Json initXianlumingcheng(){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", 1));
			List<HyOrder> orders = hyOrderService.findList(null,filters,null);
			for(HyOrder order:orders){
				HyGroup group = hyGroupService.find(order.getGroupId());
				order.setXianlumingcheng(group.getGroupLineName());
				hyOrderService.save(order);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}
	/**
	 * 将所有的order表中的xianlumingcheng字段设置正确的值
	 * @return
	 */
	@RequestMapping("/init/orderNumber")
	@ResponseBody
	public Json initOrderNumber(){
		Json json = new Json();
		List<FinishedGroupItemOrder> fgios = finishedGroupItemOrderService.findAll();
		for(FinishedGroupItemOrder fgio:fgios){
			HyOrder order = hyOrderService.find(fgio.getOrderId());
			fgio.setOrderNumber(order.getOrderNumber());
			finishedGroupItemOrderService.update(fgio);
		}
		return json;
	}
	/**
	 * 将所有的order表中的xianlumingcheng字段设置正确的值
	 * @return
	 */
	@RequestMapping("/init/lirunlv")
	@ResponseBody
	public Json initLirunlv(){
		Json json = new Json();
		
		try {
			List<FinishedGroupItemOrder> fgios = finishedGroupItemOrderService.findAll();
			for(FinishedGroupItemOrder fgio:fgios){
				BigDecimal tmp = BigDecimal.ZERO;
				if(fgio.getGroupExpend().compareTo(BigDecimal.ZERO)!=0){
					tmp = fgio.getGroupProfit().divide(fgio.getGroupExpend(),8, BigDecimal.ROUND_HALF_UP);
				}
				fgio.setGroupProfitMargin(tmp);
				finishedGroupItemOrderService.update(fgio);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			json.setMsg(e.getMessage());
			
		}
		return json;
	}
	/**
	 * 将所有的orderitem表中的退货数量设置为初始值。
	 * @return
	 */
	@RequestMapping("/init/numberOfreturn")
	@ResponseBody
	public Json initNumberOfReturn(){
		Json json = new Json();
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("type", 1));
		List<HyOrder> orders = hyOrderService.findList(null,filters,null);
		for(HyOrder order:orders){
			List<HyOrderItem> orderItems = order.getOrderItems();
			for(HyOrderItem item:orderItems){
				if(item.getNumberOfReturn()==null){
					item.setNumberOfReturn(0);
					hyOrderItemService.update(item);
				}
			}
		}
		return json;
	}
	/**
	 * 将所有的已回的团的所有订单都查出来进行总结
	 * @return
	 */
	@RequestMapping("/init/groupProfit")
	@ResponseBody
	public Json initGroupProfit(){
		Json json = new Json();
		/**
		 * 三步走战略
		 * 1、找到所有线路回团日期在今天之前的订单以及对应的团id
		 * 2、根据每个团id去筛选所有的订单，来计算结果
		 * 3、返回结果
		 */
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.eq("status", 3));
			filters.add(Filter.eq("paystatus", 1));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("groupId"));
			List<HyOrder> hyOrders = hyOrderService.findList(null,filters,orders);
			/**筛选订单，以groupId为key存储到Map中*/
			HashMap<Long, List<HyOrder>> groupMap = new HashMap<>();
			for(HyOrder order:hyOrders){
				HyGroup group = hyGroupService.find(order.getGroupId());
				if(group==null)
					throw new Exception("找不到指定团主键id为："+order.getGroupId()+" 为空！");

				Date today = DateUtil.getStartOfDay(new Date());
				if(!group.getEndDay().before(today)){
					continue;
				}
				/*放入map**/
				if(groupMap.containsKey(order.getGroupId())){
					groupMap.get(order.getGroupId()).add(order);
				}else{
					List<HyOrder> groupOrders = new ArrayList<>();
					groupOrders.add(order);
					groupMap.put(order.getGroupId(), groupOrders);
				}
			}
			
			/*遍历map，对每个group进行统计**/
			int allAdultNum = 0;
			int allChildNum = 0;
			BigDecimal allBaomingshouru = BigDecimal.ZERO;
			BigDecimal allDantuanshouru = BigDecimal.ZERO;
			BigDecimal allDantuanchengben = BigDecimal.ZERO;
			BigDecimal allDantuanlirun = BigDecimal.ZERO;
			BigDecimal allRenjunlirun = BigDecimal.ZERO;
			BigDecimal allLirunlv = BigDecimal.ZERO;
			List<Map<String, Object>> result = new ArrayList<>();
			List<FinishedGroupItemOrder> fgioList = new ArrayList<>();
			for(Map.Entry<Long,List<HyOrder>> entry:groupMap.entrySet()){
				/*每一个团，对当日的订单数据进行统计*/
				Long groupId = entry.getKey();
				List<HyOrder> groupOrders = entry.getValue();
				List<HashMap<String,Object>> list = new ArrayList<>();
				List<FinishedGroupItemOrder> flList = new ArrayList<>();
				HyGroup group = hyGroupService.find(groupId);
				if(group==null)
					throw new Exception("找不到指定团主键id为："+groupId+" 为空！");
				for(HyOrder order:groupOrders){
					HashMap<String, Object> tmp = new HashMap<>();
					FinishedGroupItemOrder fgio = new FinishedGroupItemOrder();
					Store store = storeService.find(order.getStoreId());
					if(store==null)
						throw new Exception("找不到指定团主键id为："+order.getStoreId()+" 为空！");
					tmp.put("groupId", order.getGroupId());
					tmp.put("lineName", group.getGroupLineName());
					tmp.put("linePn", group.getGroupLinePn());
					tmp.put("fatuandate", group.getStartDay());
					tmp.put("huituandate", group.getEndDay());
					tmp.put("storeName", store.getStoreName());
					
					fgio.setOrderId(order.getId());
					fgio.setOrderNumber(order.getOrderNumber());
					fgio.setGroupId(order.getGroupId());
					fgio.setLineName(group.getGroupLineName());
					fgio.setLineId(group.getLine().getId());
					fgio.setLinePn(group.getGroupLinePn());
					fgio.setFatuantime(group.getStartDay());
					fgio.setHuituantime(group.getEndDay());
					fgio.setStoreId(store.getId());
					fgio.setStoreName(store.getStoreName());
					fgio.setIsInner(group.getIsInner());
					
					List<HyOrderItem> orderItems = order.getOrderItems();
					int adult = 0;
					int child = 0;
					for(HyOrderItem item:orderItems){
						if(item.getType()==1 && item.getPriceType()==1 ){
							child += (item.getNumber()-item.getNumberOfReturn());
						}
						if(item.getType()==1 && item.getPriceType()!=1){
							//System.out.println("item id = "+item.getId()+" type = "+item.getType()+" priceType = "+item.getPriceType());
							adult += (item.getNumber()-item.getNumberOfReturn());
						}			
					}
					StringBuilder sb = new StringBuilder();
					if(adult>0){
						sb.append(adult+"大");
					}
					if(child>0){
						sb.append(child+"小");
					}
					fgio.setAdultNumber(adult);
					fgio.setChildNumber(child);
					
					tmp.put("people", sb.toString());
					allAdultNum += adult;
					allChildNum += child;
					tmp.put("jiepairen", order.getContact());
					if(order.getOperator()==null)
						throw new Exception("该订单报名计调为空！");
					tmp.put("baomingjidiao", order.getOperator().getName());
					fgio.setStoreOperator(order.getOperator().getUsername());
					fgio.setStoreOperatorName(order.getOperator().getName());
					if(order.getSupplier()==null)
						throw new Exception("该订单对应的接团计调为空！");
					tmp.put("jietuanjidiao", order.getSupplier().getName());
					fgio.setSupplier(order.getSupplier().getUsername());
					fgio.setSupplierName(order.getSupplier().getName());
					/*单团收入，内部供应商 结算价减去退款价再减去返利，外部供应商就是结算价*/
					BigDecimal baomingshouru = null;

					baomingshouru = order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())
								.subtract(order.getStoreFanLi()==null?BigDecimal.ZERO:order.getStoreFanLi());
					
					tmp.put("baomingshouru", baomingshouru);
					fgio.setOrderIncome(baomingshouru);
					allBaomingshouru = allBaomingshouru.add(baomingshouru);
					list.add(tmp);
					flList.add(fgio);
					
				}
				
				/*算团的当前报名订单总和*/
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("groupId", groupId));
				filters2.add(Filter.eq("type", 1));
				filters2.add(Filter.eq("status", 3));
				filters2.add(Filter.eq("paystatus", 1));
				List<HyOrder> historyOrders = hyOrderService.findList(null,filters2,null);
				/*计算单团收入*/
				BigDecimal dantuanshouru = BigDecimal.ZERO;
				
				boolean isInner = group.getLine().getIsInner();
				if(isInner){
					//如果是内部供应商,计算单团收入时需要加上计调报账的其他各种收入
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							//如果已经计调报账了，直接获取当前团的所有收入
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanshouru = dantuanshouru.add(regulategroupAccount.getAllIncome());
						}else{
							//如果还没有计调报账，就按照单纯下单收入计算
							for(HyOrder hOrder:historyOrders){
								dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
										.subtract(hOrder.getJiesuanTuikuan())
										.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
							}
						}
					}
				}else{
					for(HyOrder hOrder:historyOrders){
						dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
								.subtract(hOrder.getJiesuanTuikuan())
								.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
					}
				}
				/*计算单团成本，分为内外部供应商*/		
				BigDecimal dantuanchengben = BigDecimal.ZERO;
				BigDecimal dantuanlirun = BigDecimal.ZERO;
				BigDecimal renjunlirun = BigDecimal.ZERO;
				BigDecimal lirunlv = BigDecimal.ZERO;
				if(isInner){
					//如果该团是内部供应商，从计调报账的单团核算表中获取成本
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanchengben = dantuanchengben.add(regulategroupAccount.getAllExpense());
							dantuanlirun = dantuanlirun.add(regulategroupAccount.getProfit());
							if(regulategroupAccount.getAverageProfit()!=null){
								renjunlirun = renjunlirun.add(regulategroupAccount.getAverageProfit());
								if(dantuanchengben.compareTo(BigDecimal.ZERO)!=0){
									lirunlv = dantuanlirun.divide(dantuanchengben,8,RoundingMode.CEILING);
								}
							}
						}
					}
					
				}else{
					//如果是外部供应商，成本和收入相同
					dantuanchengben = dantuanchengben.add(dantuanshouru);
				}
				
				/*给list中的行添加共有字段*/
				for(HashMap<String, Object> map:list){
					map.put("dantuanshouru", dantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanchengben", dantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanlirun", dantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("renjunlirun", renjunlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("lirunlv", lirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
				}
				/*给fgiolist中添加共有字段*/
				for(FinishedGroupItemOrder fgio:flList){
					fgio.setGroupIncome(dantuanshouru);
					fgio.setGroupExpend(dantuanchengben);
					fgio.setGroupProfit(dantuanlirun);
					fgio.setGroupAverageProfit(renjunlirun);
					fgio.setGroupProfitMargin(lirunlv);
				}
				allDantuanshouru = allDantuanshouru.add(dantuanshouru);
				allDantuanchengben = allDantuanchengben.add(dantuanchengben);
				allDantuanlirun = allDantuanlirun.add(dantuanlirun);
				allLirunlv = allLirunlv.add(lirunlv);
				/*将该团的所有条目加入到结果集中*/
				result.addAll(list);
				fgioList.addAll(flList);
			}
			
			for(FinishedGroupItemOrder finishedGroupItemOrder:fgioList){
				finishedGroupItemOrderService.save(finishedGroupItemOrder);
			}
			HashMap<String,Object> ans = new HashMap<>();
			ans.put("result", result);
			StringBuilder sb = new StringBuilder();
			if(allAdultNum>0){
				sb.append(allAdultNum+"大");
			}
			if(allChildNum>0){
				sb.append(allChildNum+"小");
			}
			ans.put("fgoi", fgioList);
			ans.put("allPeopleNum", sb.toString());
			ans.put("total", result.size());
			ans.put("allBaomingshouru", allBaomingshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanshouru", allDantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanchengben", allDantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanlirun", allDantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
			if(allAdultNum+allChildNum>0){
				ans.put("allRenjunlirun", allDantuanlirun.divide(new BigDecimal(allAdultNum+allChildNum),8,RoundingMode.CEILING).setScale(3, BigDecimal.ROUND_HALF_UP));
			}else{
				ans.put("allRenjunlirun", BigDecimal.ZERO.setScale(3, BigDecimal.ROUND_HALF_UP));
			}
			ans.put("allLirunlv", allLirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
			json.setMsg("查询成功");
			json.setObj(ans);
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
			json.setSuccess(false);
		}
		
		return json;
	}
	
	@RequestMapping("/reception/todayToExcel")
	public void todayGroupReceptionStatisticsToExcel(HttpServletRequest request,
			HttpServletResponse response,HttpSession session,
			String storeName,@DateTimeFormat(iso=ISO.DATE) Date leftFtDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightFtDate,@DateTimeFormat(iso=ISO.DATE) Date leftCreateDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightCreateDate,
			String lineName,String linePn,String storeOperator,
			String providerOperator,Long areaId,Integer storeType){
		// TODO 判断当前用户是否有访问权限(暂时由前端赋权控制，这里不判断)
		/**
		 * 三步走战略
		 * 1、找到所有今天的线路订单以及对应的团id
		 * 2、根据每个团id去筛选所有的订单，来计算结果
		 * 3、返回结果
		 */
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.eq("status", 3));
			/*只筛选来自门店的订单*/
			filters.add(Filter.eq("source", 0));
			filters.add(Filter.eq("paystatus", 1));
			if(leftFtDate != null){
				Date ltoday = DateUtil.getStartOfDay(leftFtDate);
				filters.add(Filter.ge("fatuandate", ltoday));
			}
			if(rightFtDate != null){
				Date rtoday = DateUtil.getEndOfDay(rightFtDate);
				filters.add(Filter.le("fatuandate", rtoday));
			}
			
			Date ltoday = null;
			if(leftCreateDate != null){
				ltoday = DateUtil.getStartOfDay(leftCreateDate);
				filters.add(Filter.ge("createtime", ltoday));
			}else{
				ltoday = DateUtil.getStartOfDay(new Date());
				filters.add(Filter.ge("createtime", ltoday));
			}
			Date rtoday = null;
			if(rightCreateDate != null){
				rtoday = DateUtil.getEndOfDay((rightCreateDate));
				filters.add(Filter.le("createtime", rtoday));
			}else{
				rtoday = DateUtil.getEndOfDay(new Date());
				filters.add(Filter.le("createtime", rtoday));
			}
			if(lineName != null){
				filters.add(Filter.like("xianlumingcheng", lineName));
			}
			if(storeType != null){
				filters.add(Filter.eq("storeType", storeType));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("fatuandate"));
			List<HyOrder> hyOrders = hyOrderService.findList(null,filters,orders);
			/**筛选订单，以groupId为key存储到Map中*/
			TreeMap<Long, List<HyOrder>> groupMap = new TreeMap<>();
			for(HyOrder order:hyOrders){
				HyGroup group = hyGroupService.find(order.getGroupId());
				if(group==null)
					throw new Exception("找不到指定团主键id为："+order.getGroupId()+" 为空！");
				Store store = storeService.find(order.getStoreId());
				if(store==null)
					throw new Exception("找不到指定团主键id为："+order.getStoreId()+" 为空！");
				/*筛选符合条件的数据**/
				if (areaId != null && !store.getHyArea().getId().equals(areaId)) {
					continue;
				}
				if(storeName!=null && !store.getStoreName().contains(storeName)){
					continue;
				}
				if(linePn!=null && !group.getGroupLinePn().contains(linePn)){
					continue;
				}
				if(storeOperator!=null && !order.getOperator().getName().contains(storeOperator)){
					continue;
				}
				if(providerOperator!=null && !group.getLine().getOperator().getName().contains(providerOperator)){
					continue;
				}
				/*放入map**/
				if(groupMap.containsKey(order.getGroupId())){
					groupMap.get(order.getGroupId()).add(order);
				}else{
					List<HyOrder> groupOrders = new ArrayList<>();
					groupOrders.add(order);
					groupMap.put(order.getGroupId(), groupOrders);
				}
			}
			
			/*遍历map，对每个group进行统计**/
			int allAdultNum = 0;
			int allChildNum = 0;
			BigDecimal allBaomingshouru = BigDecimal.ZERO;
			BigDecimal allDantuanshouru = BigDecimal.ZERO;
			BigDecimal allDantuanchengben = BigDecimal.ZERO;
			BigDecimal allDantuanlirun = BigDecimal.ZERO;
			BigDecimal allRenjunlirun = BigDecimal.ZERO;
			BigDecimal allLirunlv = BigDecimal.ZERO;
			List<Map<String, Object>> result = new ArrayList<>();
			
			
			/*初始化Excel表格*/
			Workbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = (HSSFSheet) workbook.createSheet("当日接团统计");
			Map<String,CellStyle> styles = ExcelHelper.createStyles(workbook);
			/*初始化全局变量*/
			List<String> nameList = getTodayCellNameList();
			List<String> keyList = getTodayKeyList();
			int cellNum = nameList.size();
			/*初始化标题*/
			HSSFRow titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(24);
			HSSFCell titleCell = titleRow.createCell(0);
			String time = DateUtil.getSimpleDate(ltoday)+"~"+DateUtil.getSimpleDate(rtoday);
			titleCell.setCellValue("当日接团统计表("+time+")");
			titleCell.setCellStyle(styles.get("title"));
			CellRangeAddress region=new CellRangeAddress(0,0,0,cellNum);
			sheet.addMergedRegion(region);	
			/*初始化列表头*/
			HSSFRow headerRow = sheet.createRow(1);
			headerRow.setHeightInPoints(18);
			sheet.setColumnWidth(0, 10 * 256);
			/*初始化序列号*/
			HSSFCell numHeaderCell = headerRow.createCell(0);
			numHeaderCell.setCellValue("序号");
			numHeaderCell.setCellStyle(styles.get("header"));
			for(int i=0;i<nameList.size();i++){
				HSSFCell headerCell = headerRow.createCell(i+1);
				sheet.setColumnWidth(i + 1, 15 * 256);
				headerCell.setCellValue(nameList.get(i));
				headerCell.setCellStyle(styles.get("header"));
			}
			/*初始化一些Excel需要用到的变量*/
			int currRowNum = 2; //从第二行开始写数据
			int index = 1;
			for(Map.Entry<Long,List<HyOrder>> entry:groupMap.entrySet()){
				/*每一个团，对当日的订单数据进行统计*/
				Long groupId = entry.getKey();
				List<HyOrder> groupOrders = entry.getValue();
				List<HashMap<String,Object>> list = new ArrayList<>();
				HyGroup group = hyGroupService.find(groupId);
				if(group==null)
					throw new Exception("找不到指定团主键id为："+groupId+" 为空！");
				String supplierName = group.getLine().getHySupplier().getSupplierName();
				for(HyOrder order:groupOrders){
					HashMap<String, Object> tmp = new HashMap<>();
					Store store = storeService.find(order.getStoreId());
					if(store==null)
						throw new Exception("订单 "+order.getOrderNumber()+" 的门店找不到");
					tmp.put("groupId", order.getGroupId());
					tmp.put("lineName", group.getGroupLineName());
					tmp.put("linePn", group.getGroupLinePn());
					tmp.put("supplierName", supplierName);
					tmp.put("fatuandate", group.getStartDay());
					tmp.put("huituandate", group.getEndDay());
					tmp.put("storeName", store.getStoreName());
					tmp.put("area", store.getHyArea().getFullName());
					tmp.put("storeType", store.getStoreType()==0?"虹宇门店":"非虹宇门店");
					List<HyOrderItem> orderItems = order.getOrderItems();
					int adult = 0;
					int child = 0;
					for(HyOrderItem item:orderItems){
						if(item.getType()==1 && item.getPriceType()==1 ){
							child += (item.getNumber()-item.getNumberOfReturn());
						}
						if(item.getType()==1 && item.getPriceType()!=1){
							//System.out.println("item id = "+item.getId()+" type = "+item.getType()+" priceType = "+item.getPriceType());
							adult += (item.getNumber()-item.getNumberOfReturn());
						}			
					}
					StringBuilder sb = new StringBuilder();
					if(adult>0){
						sb.append(adult+"大");
					}
					if(child>0){
						sb.append(child+"小");
					}
					tmp.put("people", sb.toString());
					allAdultNum += adult;
					allChildNum += child;
					tmp.put("jiepairen", order.getContact());
					if(order.getOperator()==null)
						throw new Exception("该订单报名计调为空！");
					tmp.put("baomingjidiao", order.getOperator().getName());
					if(order.getSupplier()==null)
						throw new Exception("该订单对应的接团计调为空！");
					tmp.put("jietuanjidiao", order.getSupplier().getName());
					/*单团收入，内部供应商 结算价减去退款价再减去返利，外部供应商就是结算价*/
					BigDecimal baomingshouru = null;

					baomingshouru = order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())
								.subtract(order.getStoreFanLi()==null?BigDecimal.ZERO:order.getStoreFanLi());
					
					tmp.put("baomingshouru", baomingshouru);
					allBaomingshouru = allBaomingshouru.add(baomingshouru);
					list.add(tmp);
					
				}
				
				/*算团的当前报名订单总和*/
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("groupId", groupId));
				filters2.add(Filter.eq("type", 1));
				filters2.add(Filter.eq("status", 3));
				filters2.add(Filter.eq("paystatus", 1));
				List<HyOrder> historyOrders = hyOrderService.findList(null,filters2,null);
				/*计算单团收入*/
				BigDecimal dantuanshouru = BigDecimal.ZERO;
				
				boolean isInner = group.getLine().getIsInner();
				if(isInner){
					//如果是内部供应商,计算单团收入时需要加上计调报账的其他各种收入
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							//如果已经计调报账了，直接获取当前团的所有收入
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanshouru = dantuanshouru.add(regulategroupAccount.getAllIncome());
						}else{
							//如果还没有计调报账，就按照单纯下单收入计算
							for(HyOrder hOrder:historyOrders){
								dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
										.subtract(hOrder.getJiesuanTuikuan())
										.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
							}
						}
					}
				}else{
					for(HyOrder hOrder:historyOrders){
						dantuanshouru = dantuanshouru.add(hOrder.getJiusuanMoney())
								.subtract(hOrder.getJiesuanTuikuan())
								.subtract(hOrder.getStoreFanLi()==null?BigDecimal.ZERO:hOrder.getStoreFanLi());
					}
				}
				/*计算单团成本，分为内外部供应商*/		
				BigDecimal dantuanchengben = BigDecimal.ZERO;
				BigDecimal dantuanlirun = BigDecimal.ZERO;
				BigDecimal renjunlirun = BigDecimal.ZERO;
				BigDecimal lirunlv = BigDecimal.ZERO;
				if(isInner){
					//如果该团是内部供应商，从计调报账的单团核算表中获取成本
					if(group.getRegulateId()!=null){
						HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
						if(regulate.getDantuanhesuanbiaoId()!=null){
							RegulategroupAccount regulategroupAccount = regulategroupAccountService.find(regulate.getDantuanhesuanbiaoId());
							dantuanchengben = dantuanchengben.add(regulategroupAccount.getAllExpense());
							dantuanlirun = dantuanlirun.add(regulategroupAccount.getProfit());
							if(regulategroupAccount.getAverageProfit()!=null){
								renjunlirun = renjunlirun.add(regulategroupAccount.getAverageProfit());
								if(dantuanchengben.compareTo(BigDecimal.ZERO)!=0){
									lirunlv = dantuanlirun.divide(dantuanchengben,8,RoundingMode.CEILING);
								}
							}
						}
					}
					
				}else{
					//如果是外部供应商，成本和收入相同
					dantuanchengben = dantuanchengben.add(dantuanshouru);
				}
				
				/*给list中的行添加共有字段*/
				for(HashMap<String, Object> map:list){
					map.put("dantuanshouru", dantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanchengben", dantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("dantuanlirun", dantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("renjunlirun", renjunlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
					map.put("lirunlv", lirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
				}
				allDantuanshouru = allDantuanshouru.add(dantuanshouru);
				allDantuanchengben = allDantuanchengben.add(dantuanchengben);
				allDantuanlirun = allDantuanlirun.add(dantuanlirun);
				/*将该团的所有条目加入到结果集中*/
				result.addAll(list);
				/*将该团的数据写入到excel表中去*/
				for (int i = 0; i < list.size(); i++) {
					Row row = sheet.createRow(currRowNum++);
					row.setHeightInPoints(15);

					// 为每一行添加序号:
					Cell numCell = row.createCell(0);
					numCell.setCellStyle(styles.get("listdata"));
					numCell.setCellValue(index++);
					
					HashMap<String, Object> tMap = (HashMap<String, Object>) list.get(i);
					// 对于每一行的每一列进行POJO的赋值：
					for (int j = 0; j < cellNum; j++) {
						String colname = (String) keyList.get(j);
						Cell dataCell = row.createCell(j + 1);
						dataCell.setCellStyle(styles.get("listdata"));
						Object obj =  tMap.get(colname);
						dataCell.setCellValue(obj==null?"":obj.toString());
					}		
				}
				
				int mergeStartRow = currRowNum-list.size();
				int mergeEndRow = currRowNum-1;
				
				/*合并前6列*/
				for(int k = 1;k<=6;k++){
					CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
					sheet.addMergedRegion(regionColumn);
				}
				/*合并后5列*/
				for(int k = 14;k<=18;k++){
					CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
					sheet.addMergedRegion(regionColumn);
				}
			
			}
			HashMap<String,Object> ans = new HashMap<>();
			ans.put("result", result);
			StringBuilder sb = new StringBuilder();
			if(allAdultNum>0){
				sb.append(allAdultNum+"大");
			}
			if(allChildNum>0){
				sb.append(allChildNum+"小");
			}
			ans.put("allPeopleNum", sb.toString());
			ans.put("total", result.size());
			ans.put("allBaomingshouru", allBaomingshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanshouru", allDantuanshouru.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanchengben", allDantuanchengben.setScale(3, BigDecimal.ROUND_HALF_UP));
			ans.put("allDantuanlirun", allDantuanlirun.setScale(3, BigDecimal.ROUND_HALF_UP));
			if(allAdultNum+allChildNum>0){
				ans.put("allRenjunlirun", allDantuanlirun.divide(new BigDecimal(allAdultNum+allChildNum),8,RoundingMode.CEILING).setScale(3, BigDecimal.ROUND_HALF_UP));
			}else{
				ans.put("allRenjunlirun", allRenjunlirun);
			}
			if(!allDantuanchengben.equals(BigDecimal.ZERO)){
				allLirunlv = allDantuanlirun.divide(allDantuanchengben,8,RoundingMode.CEILING).setScale(3, BigDecimal.ROUND_HALF_UP);
			}
			ans.put("allLirunlv", allLirunlv.multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP).toString()+"%");
			/*添加最后一条总计数据*/
			Row row = sheet.createRow(currRowNum++);
			row.setHeightInPoints(15);

			Cell numCell = row.createCell(0);
			numCell.setCellStyle(styles.get("listdata"));
			numCell.setCellValue("总计");
			
			Cell cell9 = row.createCell(12);
			cell9.setCellStyle(styles.get("listdata"));
			cell9.setCellValue((String)ans.get("allPeopleNum"));
			Cell cell10 = row.createCell(13);
			cell10.setCellStyle(styles.get("listdata"));
			cell10.setCellValue(((BigDecimal)ans.get("allBaomingshouru")).toString());
			Cell cell11= row.createCell(14);
			cell11.setCellStyle(styles.get("listdata"));
			cell11.setCellValue(((BigDecimal)ans.get("allDantuanshouru")).toString());
			Cell cell12 = row.createCell(15);
			cell12.setCellStyle(styles.get("listdata"));
			cell12.setCellValue(((BigDecimal)ans.get("allDantuanchengben")).toString());
			Cell cell13 = row.createCell(16);
			cell13.setCellStyle(styles.get("listdata"));
			cell13.setCellValue(((BigDecimal)ans.get("allDantuanlirun")).toString());
			Cell cell14 = row.createCell(17);
			cell14.setCellStyle(styles.get("listdata"));
			cell14.setCellValue(((BigDecimal)ans.get("allRenjunlirun")).toString());
			Cell cell15 = row.createCell(18);
			cell15.setCellStyle(styles.get("listdata"));
			cell15.setCellValue((String)ans.get("allLirunlv"));
			
			
			String fileName = "当日接团统计表.xls";
			//然后把数据写入到一个临时的目录中去
			// 在临时目录下生成Excel：
			String userdir = ExcelHelper.getUserDir(request);
			String filefullname = userdir + fileName + ".xls";
			System.out.println("filefullname = "+filefullname);
			
			FileOutputStream out;
			try {
				out = new FileOutputStream(filefullname);
				try {
					workbook.write(out);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			ExcelHelper excelHelper = new ExcelHelper();
			excelHelper.export2Excel(request, response, fileName);
		
		} catch (Exception e) {
			e.printStackTrace();
			
		}

	}
	
	/**
	 * 已回团的单团利润统计
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping("/profitToExcel")
	public void groupProfitStatisticsToExcel(Pageable pageable,HttpServletRequest request,
			HttpServletResponse response,HttpSession session,
			String storeName,@DateTimeFormat(iso=ISO.DATE) Date leftFtDate,
			@DateTimeFormat(iso=ISO.DATE) Date rightFtDate,
			String lineName,String linePn,String storeOperator,
			String providerOperator){
		try {
			List<Filter> filters = new ArrayList<>();
			Date ltoday = null;
			
			if(leftFtDate!=null){
				ltoday = DateUtil.getStartOfDay(leftFtDate);
				filters.add(Filter.ge("fatuantime", ltoday));
			}
			Date rtoday = null;
			if(rightFtDate!=null){
				rtoday = DateUtil.getEndOfDay(rightFtDate);
				filters.add(Filter.le("fatuantime", rtoday));
			}
			if(lineName != null){
				filters.add(Filter.like("lineName", lineName));
			}
			if(linePn != null){
				filters.add(Filter.like("linePn", linePn));
			}
			if(providerOperator != null){
				filters.add(Filter.like("supplierName", providerOperator));
			}
			if(storeName!=null){
				filters.add(Filter.like("storeName", storeName));
			}
			if(storeOperator!=null){
				filters.add(Filter.like("storeOperatorName", storeOperator));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("fatuantime"));
			List<FinishedGroupItemOrder> fgios = finishedGroupItemOrderService.findList(null,filters,orders);
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			/*初始化Excel表格*/
			Workbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = (HSSFSheet) workbook.createSheet("单团利润统计");
			Map<String,CellStyle> styles = ExcelHelper.createStyles(workbook);
			/*初始化全局变量*/
			List<String> nameList = getProfitCellNameList();
			List<String> keyList = getProfitKeyList();
			int cellNum = nameList.size();
			/*初始化标题*/
			HSSFRow titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(24);
			HSSFCell titleCell = titleRow.createCell(0);
			StringBuilder time = new StringBuilder();
			if(ltoday!=null){
				time.append(DateUtil.getSimpleDate(ltoday));
			}
			if(rtoday!=null){
				time.append("~").append(DateUtil.getSimpleDate(rtoday));
			}
			if(time.toString().equals("")){
				titleCell.setCellValue("单团利润统计表");
			}else{
				titleCell.setCellValue("单团利润统计表("+time.toString()+")");
			}
			titleCell.setCellStyle(styles.get("title"));
			CellRangeAddress region=new CellRangeAddress(0,0,0,cellNum);
			sheet.addMergedRegion(region);	
			/*初始化列表头*/
			HSSFRow headerRow = sheet.createRow(1);
			headerRow.setHeightInPoints(18);
			sheet.setColumnWidth(0, 10 * 256);
			/*初始化序列号*/
			HSSFCell numHeaderCell = headerRow.createCell(0);
			numHeaderCell.setCellValue("序号");
			numHeaderCell.setCellStyle(styles.get("header"));
			for(int i=0;i<nameList.size();i++){
				HSSFCell headerCell = headerRow.createCell(i+1);
				sheet.setColumnWidth(i + 1, 15 * 256);
				headerCell.setCellValue(nameList.get(i));
				headerCell.setCellStyle(styles.get("header"));
			}
			/*初始化一些Excel需要用到的变量*/
			int currRowNum = 2; //从第二行开始写数据
			int index = 1;
			
			Long beforeGroupId  = null;
			Long currGroupId  = null;
			int count = 0;
			if(fgios.size()>0){
				beforeGroupId = fgios.get(0).getGroupId();
				currGroupId = fgios.get(0).getGroupId();
			}
			for(int i = 0;i<fgios.size();i++){
				FinishedGroupItemOrder fgio = fgios.get(i);
				currGroupId = fgio.getGroupId();
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", fgio.getId());
				map.put("groupId", fgio.getGroupId());
				map.put("lineName", fgio.getLineName());
				map.put("linePn", fgio.getLinePn());
				map.put("hySupplier", fgio.getHySupplier());
				map.put("orderNumber",fgio.getOrderNumber());
				map.put("fatuantime", fgio.getFatuantime());
				map.put("huituantime", fgio.getHuituantime());
				map.put("storeName", fgio.getStoreName());
				map.put("storeOperatorName", fgio.getStoreOperatorName());
				map.put("supplierName", fgio.getSupplierName());
				StringBuilder sBuilder =new StringBuilder();
				if(fgio.getAdultNumber()>0){
					sBuilder.append(fgio.getAdultNumber()).append("大");
				}
				if(fgio.getChildNumber()>0){
					sBuilder.append(fgio.getChildNumber()).append("小");
				}
				map.put("people", sBuilder.toString());
				map.put("orderIncome", fgio.getOrderIncome().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupIncome", fgio.getGroupIncome().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupExpend", fgio.getGroupExpend().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupProfit", fgio.getGroupProfit().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupAverageProfit", fgio.getGroupAverageProfit().setScale(3, BigDecimal.ROUND_HALF_UP));
				map.put("groupProfitMargin", fgio.getGroupProfitMargin().multiply(new BigDecimal("100")).setScale(3, BigDecimal.ROUND_HALF_UP));
				list.add(map);
				//将数据行写到excel表
				Row row = sheet.createRow(currRowNum++);
				row.setHeightInPoints(15);

				// 为每一行添加序号:
				Cell numCell = row.createCell(0);
				numCell.setCellStyle(styles.get("listdata"));
				numCell.setCellValue(index++);
				
				// 对于每一行的每一列进行POJO的赋值：
				for (int j = 0; j < cellNum; j++) {
					String colname = (String) keyList.get(j);
					Cell dataCell = row.createCell(j + 1);
					dataCell.setCellStyle(styles.get("listdata"));
					Object obj =  map.get(colname);
					dataCell.setCellValue(obj==null?"":obj.toString());
				}		
				
				//判断当前团是否结束了，也就是当前的groupid和上一个groupId是否一样，
				//如果一致，则继续，如果不一致，则需要对上一个group进行合并单元格
				//如果是最后一个也要进行处理。
				if(!currGroupId.equals(beforeGroupId)){
					int mergeStartRow = currRowNum-count-1;
					int mergeEndRow = currRowNum-2;
					/*合并前6列*/
					for(int k = 1;k<=6;k++){
						CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
						sheet.addMergedRegion(regionColumn);
					}
					/*合并后5列*/
					for(int k = 12;k<=16;k++){
						CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
						sheet.addMergedRegion(regionColumn);
					}
					
					beforeGroupId = currGroupId;
					count = 1;
				}else if(i == (fgios.size()-1)){
					count++;
					int mergeStartRow = currRowNum-count;
					int mergeEndRow = currRowNum-1;
					/*合并前6列*/
					for(int k = 1;k<=6;k++){
						CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
						sheet.addMergedRegion(regionColumn);
					}
					/*合并后5列*/
					for(int k = 12;k<=16;k++){
						CellRangeAddress regionColumn = new CellRangeAddress(mergeStartRow,mergeEndRow,k,k);
						sheet.addMergedRegion(regionColumn);
					}
					
				}else{
					count++;
				}
				
			}
			
			String fileName = "单团利润统计表.xls";
			//然后把数据写入到一个临时的目录中去
			// 在临时目录下生成Excel：
			String userdir = ExcelHelper.getUserDir(request);
			String filefullname = userdir + fileName + ".xls";
			System.out.println("filefullname = "+filefullname);
			
			FileOutputStream out;
			try {
				out = new FileOutputStream(filefullname);
				try {
					workbook.write(out);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			ExcelHelper excelHelper = new ExcelHelper();
			excelHelper.export2Excel(request, response, fileName);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private List<String> getTodayCellNameList() {
		List<String> list = new ArrayList<>();
		list.add("产品编号");
		list.add("产品名称");
		list.add("发团日期");
		list.add("回团日期");
		list.add("供应商名称");
		list.add("接团计调");
		
		list.add("门店名称");
		list.add("门店所属区域");
		list.add("门店类型");
		list.add("接牌人");
		list.add("报名计调");
		list.add("游客人数");
		list.add("报名收入");
		
		list.add("单团收入");
		list.add("单团成本");
		list.add("单团利润");
		list.add("人均利润");
		list.add("利润率");	
		return list;
	}
	private List<String> getTodayKeyList() {
		List<String> list = new ArrayList<>();
		list.add("linePn");
		list.add("lineName");
		list.add("fatuandate");
		list.add("huituandate");
		list.add("supplierName");
		list.add("jietuanjidiao");
		
		list.add("storeName");
		list.add("area");
		list.add("storeType");
		list.add("jiepairen");	
		list.add("baomingjidiao");
		list.add("people");
		list.add("baomingshouru");
		
		list.add("dantuanshouru");
		list.add("dantuanchengben");
		list.add("dantuanlirun");
		list.add("renjunlirun");
		list.add("lirunlv");
		return list;
	}
	private List<String> getProfitCellNameList() {
		List<String> list = new ArrayList<>();
		list.add("产品编号");
		list.add("产品名称");
		list.add("发团日期");
		list.add("回团日期");
		list.add("供应商名称");
		list.add("接团计调");
		
		list.add("订单编号");
		list.add("门店名称");
		list.add("报名计调");
		list.add("游客人数");
		list.add("报名收入");
		
		list.add("单团收入");
		list.add("单团成本");
		list.add("单团利润");
		list.add("人均利润");
		list.add("利润率");	
		return list;
	}
	private List<String> getProfitKeyList() {
		List<String> list = new ArrayList<>();
		list.add("linePn");
		list.add("lineName");
		list.add("fatuantime");
		list.add("huituantime");
		list.add("hySupplier");
		list.add("supplierName");
		
		list.add("orderNumber");	
		list.add("storeName");
		list.add("storeOperatorName");
		list.add("people");
		list.add("orderIncome");
		
		list.add("groupIncome");
		list.add("groupExpend");
		list.add("groupProfit");
		list.add("groupAverageProfit");
		list.add("groupProfitMargin");
		return list;
	}
	/**
	 * 获取区域  级联框 （第一级 id=0查询）
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/areacomboxlist/view")
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
}
