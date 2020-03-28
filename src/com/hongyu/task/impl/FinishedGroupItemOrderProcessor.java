package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.FinishedGroupItemOrder;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.Store;
import com.hongyu.service.FinishedGroupItemOrderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;
@Component("finishedGroupItemOrderProcessor")
public class FinishedGroupItemOrderProcessor implements Processor{
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
	
	@Override
	public void process() {
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
				
				/*只统计今天回团的团*/
				Date startToday = DateUtil.getStartOfDay(new Date());
				if(group.getEndDay().before(startToday)){
					continue;
				}
				Date endToday = DateUtil.getEndOfDay(new Date());
				if(group.getEndDay().after(endToday)){
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
			List<FinishedGroupItemOrder> fgioList = new ArrayList<>();
			for(Map.Entry<Long,List<HyOrder>> entry:groupMap.entrySet()){
				/*每一个团，对当日的订单数据进行统计*/
				Long groupId = entry.getKey();
				List<HyOrder> groupOrders = entry.getValue();
				List<FinishedGroupItemOrder> flList = new ArrayList<>();
				HyGroup group = hyGroupService.find(groupId);
				if(group==null)
					throw new Exception("找不到指定团主键id为："+groupId+" 为空！");
				for(HyOrder order:groupOrders){
					FinishedGroupItemOrder fgio = new FinishedGroupItemOrder();
					Store store = storeService.find(order.getStoreId());
					if(store==null)
						throw new Exception("找不到指定团主键id为："+order.getStoreId()+" 为空！");					
					fgio.setOrderId(order.getId());
					fgio.setGroupId(order.getGroupId());
					fgio.setLineName(group.getGroupLineName());
					fgio.setLineId(group.getLine().getId());
					fgio.setLinePn(group.getGroupLinePn());
					fgio.setFatuantime(group.getStartDay());
					fgio.setHuituantime(group.getEndDay());
					fgio.setStoreId(store.getId());
					fgio.setStoreName(store.getStoreName());
					fgio.setIsInner(group.getIsInner());
					fgio.setHySupplier(group.getLine().getHySupplier().getSupplierName());
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
					fgio.setAdultNumber(adult);
					fgio.setChildNumber(child);
					
					if(order.getOperator()==null)
						throw new Exception("该订单报名计调为空！");
					fgio.setStoreOperator(order.getOperator().getUsername());
					fgio.setStoreOperatorName(order.getOperator().getName());
					if(order.getSupplier()==null)
						throw new Exception("该订单对应的接团计调为空！");
					fgio.setSupplier(order.getSupplier().getUsername());
					fgio.setSupplierName(order.getSupplier().getName());
					/*单团收入，内部供应商 结算价减去退款价再减去返利，外部供应商就是结算价*/
					BigDecimal baomingshouru = BigDecimal.ZERO;
					baomingshouru = order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())
								.subtract(order.getStoreFanLi()==null?BigDecimal.ZERO:order.getStoreFanLi());
					
					fgio.setOrderIncome(baomingshouru);
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
							//如果还没有计调报账，就按照单纯下单收入计算还是直接默认为0
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
								if(!dantuanshouru.equals(BigDecimal.ZERO)){
									lirunlv = dantuanlirun.divide(dantuanchengben,8,RoundingMode.CEILING);
								}
							}
						}
					}
					
				}else{
					//如果是外部供应商，成本和收入相同
					dantuanchengben = dantuanchengben.add(dantuanshouru);
				}
				
				/*给fgiolist中添加共有字段*/
				for(FinishedGroupItemOrder fgio:flList){
					fgio.setGroupIncome(dantuanshouru);
					fgio.setGroupExpend(dantuanchengben);
					fgio.setGroupProfit(dantuanlirun);
					fgio.setGroupAverageProfit(renjunlirun);
					fgio.setGroupProfitMargin(lirunlv);
				}
				/*将该团的所有条目加入到结果集中*/
				fgioList.addAll(flList);
			}
			
			for(FinishedGroupItemOrder finishedGroupItemOrder:fgioList){
				finishedGroupItemOrderService.save(finishedGroupItemOrder);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
