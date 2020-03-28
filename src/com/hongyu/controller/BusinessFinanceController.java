package com.hongyu.controller;

import com.hongyu.*;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("admin/business/finance")
public class BusinessFinanceController {


	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;

	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;

	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;

	@Autowired
	WeDivideModelService weDivideModelService;

	@Autowired
	SpecialtyPriceService specialtyPriceServiceImpl;

	@Autowired
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;


	//查询供应商采购单
	@RequestMapping(value = "/nowaday_duizhang_page/view")
	@ResponseBody
	public Json providerOrder(@DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date startdate, @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) Date enddate, String providername, String specialtyname,
	                          String receivername, String receiverphone, Pageable pageable) {
		Json json = new Json();

		try {
			List<Filter> filters = new ArrayList<Filter>();
			List<Filter> orderFilters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("createTime", yesterDayStart));
				filters.add(Filter.le("createTime", yesterDayEnd));
				orderFilters.add(Filter.ge("orderTime", yesterDayStart));
				orderFilters.add(Filter.le("orderTime", yesterDayEnd));

			}
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
				orderFilters.add(Filter.ge("orderTime", start));
			}
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
				orderFilters.add(Filter.le("orderTime", end));
			}

			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//商品
			//filters.add(Filter.eq("type", 0));
			//当日对账单也应该显示组合优惠

			//订单处于待发货状态
			orderFilters.add(Filter.gt("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW));
			orderFilters.add(Filter.ne("orderState",Constants.BUSINESS_ORDER_STATUS_CANCELED));

			//供货商发货
			//orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}
			orderFilters.add(Filter.ne("id", 4L));//脏数据？2019-08-05加。
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				Page<Map<String, Object>> page = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
				json.setObj(page);
				json.setMsg("查询成功");
				json.setSuccess(true);
				return json;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}
			//2019-08-05改查page又过滤的问题，来不及考虑性能。
			List<Order> paixuOrders = new ArrayList<>();//paixu拼音排序
			paixuOrders.add(Order.desc("id"));
			List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null, filters, paixuOrders);
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			for (BusinessOrderItem item :businessOrderItems) {
				if(item.getQuantity()==item.getReturnQuantity() 
						|| (Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND.equals(item.getBusinessOrder().getOrderState()) && item.getBusinessOrder().getShip()==null)){
					//全退款还有订单取消到了已退款状态的情况，取消订单的也不显示。用order.getShip()==null没有物流信息判断是用户或者管理系统取消订单，然后订单状态转移到已退款的。
					continue;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("providerName", item.getDeliverName());
				SpecialtySpecification specification = null;
				//如果是普通产品
				if(item.getType()==0){
					Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
					if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
						continue;
					}
					map.put("specialtyName", specialty.getName());
					specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
					map.put("specification", specification.getSpecification());
				}else {//是组合优惠
					HyGroupitemPromotion groupitemPromotion =
							hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					map.put("specialtyName", groupitemPromotion.getPromotionId().getPromotionName());
					map.put("specification", "");
				}
				map.put("quantity", item.getQuantity()-item.getReturnQuantity());
				map.put("orderCode", item.getBusinessOrder().getOrderCode());
				map.put("receiverName", item.getBusinessOrder().getReceiverName());
				map.put("receiverPhone", item.getBusinessOrder().getReceiverPhone());
				map.put("receiverAddress", item.getBusinessOrder().getReceiverAddress());
//				map.put("costPrice", businessOrderItemService.getCostPriceOfOrderitem(item));
				if(item.getType()==0){//普通产品调用getCostPriceOfOrderitem才正常，后边有计算组合优惠的costPrice
					map.put("costPrice", businessOrderItemService.getCostPriceOfOrderitem(item).multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()))); //2019-08-05改成订单条目总的成本
				}

				/** wayne 180813*/
				map.put("salePrice", item.getSalePrice().setScale(2, BigDecimal.ROUND_HALF_UP));	//单价
				map.put("totalSalePrice", item.getSalePrice().multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));//应支付金额

				BusinessOrder bOrder = item.getBusinessOrder();
				//bOrder.getShouldPayMoney()==0时会报/ by zero的异常，正常不会，不作判断了。
				BigDecimal payMoney =  item.getSalePrice().multiply(
					bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.put("payMoney",payMoney);	//现金支付
				map.put("payMoney",payMoney.multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));//2019-08-05改成订单条目总的现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
					bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.put("balanceMoney", balanceMoney);	//余额支付
				map.put("balanceMoney", balanceMoney.multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));//2019-08-05改成订单条目总的余额支付
//				map.put("couponMoney", item.getSalePrice().subtract(payMoney).subtract(balanceMoney));	//一次电子券
				map.put("couponMoney", item.getSalePrice().subtract(payMoney).subtract(balanceMoney).multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP));//2019-08-05改成订单条目总的一次性电子券
				map.put("payTime", bOrder.getPayTime());	//支付时间
				map.put("weBusinessName", bOrder.getWeBusiness().getName());	//微商姓名
				map.put("storeName", bOrder.getWeBusiness().getNameOfStore());	//所属门店

				map.put("remark", item.getBusinessOrder().getReceiverRemark());

				//求微商返利
				if(item.getType()==0){	//如果是普通商品
					SpecialtySpecification s = specification;//普通产品specification不会是null
					//找价格
					//先去价格变化表里面查
					List<Filter> priceFilters=new ArrayList<Filter>();
					priceFilters.add(Filter.eq("specification", s));
					priceFilters.add(Filter.eq("isActive", true));
					List<SpecialtyPrice> specialtyPrices=specialtyPriceServiceImpl.findList(null,priceFilters,null);

					if(specialtyPrices==null || specialtyPrices.isEmpty()){
						continue;
					}
					SpecialtyPrice price=specialtyPrices.get(0);
//					map.put("costPrice",price.getCostPrice());
					map.put("costPrice",price.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()))); //2019-08-05改成订单条目总的成本
					map.put("totalCostPrice",price.getCostPrice().multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));

					s.setMarketPrice(price.getMarketPrice());
					s.setPlatformPrice(price.getPlatformPrice());
					//运费
					s.setDeliverPrice(price.getDeliverPrice());

					//找提成比例
					WechatAccount wechatAccount = bOrder.getWechatAccount();
					if(true) {

						WeBusiness weBusiness = bOrder.getWeBusiness();

						if(weBusiness!=null){
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									map.put("divideRatio", price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									map.put("divideRatio", price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									map.put("divideRatio", price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							map.put("divideRatio",null);
						}
					}else {
						map.put("divideRatio",null);
					}

					//提成金额
					if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){

						if(map.get("divideRatio")!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal)map.get("divideRatio")));
							map.put("divideMoney",divideMoney.setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
							map.put("totalDivideMoney",divideMoney.multiply(
								BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));
						}

					}else{
						map.put("divideMoney",BigDecimal.ZERO);
						map.put("totalDivideMoney",BigDecimal.ZERO);
					}

				}else{	//如果是组合产品
					List<String> depotList = new ArrayList<>();
					//获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion =
						hyGroupitemPromotionServiceImpl.find(item.getSpecialty());

					if (groupitemPromotion.getBusinessPersonDivide() != null) {
						groupitemPromotion.getBusinessPersonDivide().setOperator(null);
					}
					if (groupitemPromotion.getStoreDivide() != null) {
						groupitemPromotion.getStoreDivide().setOperator(null);
					}
					if (groupitemPromotion.getExterStoreDivide() != null) {
						groupitemPromotion.getExterStoreDivide().setOperator(null);
					}

					//找提成比例
					WechatAccount wechatAccount = bOrder.getWechatAccount();


					BigDecimal costPrice = BigDecimal.ZERO; //总成本价
					BigDecimal deliverPrice = BigDecimal.ZERO;  //总运费


					for(HyGroupitemPromotionDetail detail : groupitemPromotion.getHyGroupitemPromotionDetails()) {
						SpecialtySpecification s = detail.getItemSpecificationId();
						//找价格
						//先去价格变化表里面查
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", s));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);

						if (specialtyPrices == null || specialtyPrices.isEmpty()) {
							continue;
						}
						SpecialtyPrice price = specialtyPrices.get(0);
						s.setMarketPrice(price.getMarketPrice());
						s.setPlatformPrice(price.getPlatformPrice());
						//运费
						s.setDeliverPrice(price.getDeliverPrice());

						costPrice = costPrice.add(price.getCostPrice());
						deliverPrice = deliverPrice.add(price.getDeliverPrice());

						//找提成比例
						if (true) {

							WeBusiness weBusiness = bOrder.getWeBusiness();

							if (weBusiness != null) {
								switch (weBusiness.getType()) {
									case 0:
										//找提成模型
										List<Filter> filters4 = new ArrayList<>();
										filters4.add(Filter.eq("modelType", "虹宇门店"));
										filters4.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels = weDivideModelService.findList(null, filters4, null);
										map.put("divideRatio", price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
										break;
									case 1:
										//找提成模型
										List<Filter> filters5 = new ArrayList<>();
										filters5.add(Filter.eq("modelType", "非虹宇门店"));
										filters5.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels1 = weDivideModelService.findList(null, filters5, null);
										map.put("divideRatio", price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
										break;
									case 2:
										//找提成模型
										List<Filter> filters6 = new ArrayList<>();
										filters6.add(Filter.eq("modelType", "个人商贸"));
										filters6.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels2 = weDivideModelService.findList(null, filters6, null);
										map.put("divideRatio", price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
										break;
									default:
										break;
								}
							} else {
								map.put("divideRatio", null);
							}
						} else {
							map.put("divideRatio", null);
						}
						//提成金额
						if (map.get("divideRatio") != null) {
							s.setDividMoney(item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal) map.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
						}

					}

					if(true) {
						WeBusiness weBusiness = bOrder.getWeBusiness();

						if (weBusiness != null) {
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									map.put("divideRatio", groupitemPromotion.getStoreDivide().getProportion().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									map.put("divideRatio", groupitemPromotion.getExterStoreDivide().getProportion().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									map.put("divideRatio", groupitemPromotion.getBusinessPersonDivide().getProportion().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							map.put("divideRatio",null);
						}
					}else {
						map.put("divideRatio",null);
					}

//					map.put("costPrice",costPrice);
					map.put("costPrice",costPrice.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()))); //2019-08-05改成订单条目总的成本
					map.put("totalCostPrice",costPrice.multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));

					//提成金额
					if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){

						if(map.get("divideRatio")!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(costPrice).subtract(
								deliverPrice).multiply((BigDecimal)map.get("divideRatio")));
							map.put("divideMoney",divideMoney.setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
							map.put("totalDivideMoney",divideMoney.multiply(
								BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));
						}

					}else{
						map.put("divideMoney",BigDecimal.ZERO);//如果写0，而不是BigDecimal.ZERO，之后强制类型转换成BigDecimal会报异常，Integer不能转成BigDecimal
						map.put("totalDivideMoney",BigDecimal.ZERO);
					}

				}

				//没用余额才算分成值，用余额了没有分成
				if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){
					if(item.getPromotionId()!=null && item.getPromotionId().getDivideMoney()!=BigDecimal.ZERO){
						map.put("divideMoney",item.getPromotionId().getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
						map.put("totalDivideMoney",item.getPromotionId().getDivideMoney().multiply(
							BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()
							)));
					}
				}else{
					map.put("divideMoney",BigDecimal.ZERO);
					map.put("totalDivideMoney",BigDecimal.ZERO);
				}

				//前台返利用的divideMoney，改成总返利，上边的totalDivideMoney都没改，先用divideMoney乘数量。
				if(map.get("divideMoney")!=null){
					map.put("divideMoney", ((BigDecimal)map.get("divideMoney")).multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));
				}
				
				maps.add(map);
			}
			Page<Map<String, Object>> mapPage = null;
			if (maps.size() == 0) {
				mapPage = new Page<>(maps, 0, pageable);
			} else {
				Integer pageIndex = pageable.getPage();
				Integer pageSize = pageable.getRows();
				int endIndex = maps.size()<pageIndex*pageSize?maps.size():pageIndex*pageSize;
				mapPage = new Page<Map<String, Object>>(maps.subList((pageIndex-1)*pageSize, endIndex), maps.size(), pageable);
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(mapPage);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}

	//生成供应商采购单excel表格
	@RequestMapping(value = "/nowaday_duizhang_excel/view")
	public String providerOrderExcel(@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)Date startdate, @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)Date enddate,
	                                 String providername, String specialtyname, String receivername, String receiverphone,
	                                 HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();

		try {
			List<Filter> filters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				startdate = yesterDayStart;
				enddate = yesterDayEnd;
			}
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
			}
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
			}

			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//供货商发货
			//filters.add(Filter.le("deliverType", 1));
			//商品
			//filters.add(Filter.eq("type", 0));
			//当日对账单也应该显示组合优惠
			
			List<Filter> orderFilters = new ArrayList<Filter>();

			//订单处于待发货状态
			orderFilters.add(Filter.gt("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW));
			orderFilters.add(Filter.ne("orderState",Constants.BUSINESS_ORDER_STATUS_CANCELED));

			//orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}

			orderFilters.add(Filter.ne("id", 4L));//脏数据？2019-08-05加。先跑通再说。
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				List<ProviderOrder> results = new ArrayList<ProviderOrder>();
				// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				sb2.append("当日对账单_"+format.format(startdate)+"-"+format.format(enddate));
				String fileName = "当日对账单_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "nowadayDuizhang.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
				return null;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(Order.desc("id"));
			List<BusinessOrderItem> list = businessOrderItemService.findList(null, filters, orderList);
			List<ProviderOrder> results = new ArrayList<ProviderOrder>();
			for (BusinessOrderItem item : list) {

				if(item.getQuantity()==item.getReturnQuantity() 
						|| (Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND.equals(item.getBusinessOrder().getOrderState()) && item.getBusinessOrder().getShip()==null)){
					//全退款还有订单取消到了已退款状态的情况，取消订单的也不显示。用order.getShip()==null没有物流信息判断是用户或者管理系统取消订单，然后订单状态转移到已退款的。
					continue;
				}

				ProviderOrder map = new ProviderOrder();
				map.setProviderName(item.getDeliverName());
				SpecialtySpecification specification = null;
				//如果是普通产品
				if(item.getType()==0){
					Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
					if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
						continue;
					}
					map.setSpecialtyName(specialty.getName());
					specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
					map.setSpecification(specification.getSpecification());
				}else {
					 HyGroupitemPromotion groupitemPromotion =
			                 hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
					 map.setSpecialtyName(groupitemPromotion.getPromotionId().getPromotionName());
					 map.setSpecification("");
				}
				map.setQuantity(item.getQuantity()-item.getReturnQuantity());
				map.setOrderCode(item.getBusinessOrder().getOrderCode());
				map.setReceiverName(item.getBusinessOrder().getReceiverName());
				map.setReceiverPhone(item.getBusinessOrder().getReceiverPhone());
				map.setReceiverAddress(item.getBusinessOrder().getReceiverAddress());
//				map.setCostPrice(businessOrderItemService.getCostPriceOfOrderitem(item).setScale(2,RoundingMode.HALF_UP));
				if(item.getType()==0){//普通产品调用getCostPriceOfOrderitem才正常，后边有计算组合优惠的costPrice
					map.setCostPrice(businessOrderItemService.getCostPriceOfOrderitem(item).multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP)); //2019-08-05改成订单条目总的成本
				}
				map.setRemark(item.getBusinessOrder().getReceiverRemark());

				/** wayne 180813*/
				map.setSalePrice(item.getSalePrice().setScale(2,RoundingMode.HALF_UP));	//单价
				map.setTotalSalePrice(item.getSalePrice().multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP));	//订单条目总的应支付金额

				BusinessOrder bOrder = item.getBusinessOrder();
				//bOrder.getShouldPayMoney()==0时会报/ by zero的异常，正常不会，不作判断了。
				BigDecimal payMoney =  item.getSalePrice().multiply(
					bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.setPayMoney(payMoney.setScale(2,RoundingMode.HALF_UP));	//现金支付
				map.setPayMoney(payMoney.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP));	//2019-08-05改成订单条目总的现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
					bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),6,BigDecimal.ROUND_HALF_UP));
//				map.setBalanceMoney(balanceMoney.setScale(2,RoundingMode.HALF_UP));	//余额支付
				map.setBalanceMoney(balanceMoney.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP));	//2019-08-05改成订单条目总的余额支付
//				map.setCouponMoney((item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).setScale(2,RoundingMode.HALF_UP));	//一次电子券
				map.setCouponMoney((item.getSalePrice().subtract(payMoney).subtract(balanceMoney)).multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP));	//2019-08-05改成订单条目总的一次电子券
				map.setPayTime(bOrder.getPayTime());	//支付时间
				map.setWeBusinessName(bOrder.getWeBusiness().getName());	//微商姓名
				map.setStoreName(bOrder.getWeBusiness().getNameOfStore());	//所属门店


				//求微商返利
				if(item.getType()==0){	//如果是普通商品
					SpecialtySpecification s = specification;//普通产品specification不会是null
					//找价格
					//先去价格变化表里面查
					List<Filter> priceFilters=new ArrayList<Filter>();
					priceFilters.add(Filter.eq("specification", s));
					priceFilters.add(Filter.eq("isActive", true));
					List<SpecialtyPrice> specialtyPrices=specialtyPriceServiceImpl.findList(null,priceFilters,null);

					if(specialtyPrices==null || specialtyPrices.isEmpty()){
						continue;
					}
					SpecialtyPrice price=specialtyPrices.get(0);
//					map.setCostPrice(price.getCostPrice());
					map.setCostPrice(price.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP)); //2019-08-05改成订单条目总的成本

					s.setMarketPrice(price.getMarketPrice());
					s.setPlatformPrice(price.getPlatformPrice());
					//运费
					s.setDeliverPrice(price.getDeliverPrice());

					//找提成比例
					WechatAccount wechatAccount = bOrder.getWechatAccount();
					if(true) {

						WeBusiness weBusiness = bOrder.getWeBusiness();

						if(weBusiness!=null){
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									map.setDivideRatio(price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									map.setDivideRatio( price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									map.setDivideRatio(price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							map.setDivideRatio(null);
						}
					}else {
						map.setDivideRatio(null);
					}

					//提成金额
					if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){

						if(map.getDivideRatio()!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal)map.getDivideRatio()));
							map.setDivideMoney(divideMoney.setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
						}

					}else{
						map.setDivideMoney(BigDecimal.ZERO);
					}

				}else{	//如果是组合产品
					List<String> depotList = new ArrayList<>();
					//获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion =
						hyGroupitemPromotionServiceImpl.find(item.getSpecialty());

					if (groupitemPromotion.getBusinessPersonDivide() != null) {
						groupitemPromotion.getBusinessPersonDivide().setOperator(null);
					}
					if (groupitemPromotion.getStoreDivide() != null) {
						groupitemPromotion.getStoreDivide().setOperator(null);
					}
					if (groupitemPromotion.getExterStoreDivide() != null) {
						groupitemPromotion.getExterStoreDivide().setOperator(null);
					}

					//找提成比例
					WechatAccount wechatAccount = bOrder.getWechatAccount();


					BigDecimal costPrice = BigDecimal.ZERO; //总成本价
					BigDecimal deliverPrice = BigDecimal.ZERO;  //总运费


					for(HyGroupitemPromotionDetail detail : groupitemPromotion.getHyGroupitemPromotionDetails()) {
						SpecialtySpecification s = detail.getItemSpecificationId();
						//找价格
						//先去价格变化表里面查
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", s));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);

						if (specialtyPrices == null || specialtyPrices.isEmpty()) {
							continue;
						}
						SpecialtyPrice price = specialtyPrices.get(0);
						s.setMarketPrice(price.getMarketPrice());
						s.setPlatformPrice(price.getPlatformPrice());
						//运费
						s.setDeliverPrice(price.getDeliverPrice());

						costPrice = costPrice.add(price.getCostPrice());
						deliverPrice = deliverPrice.add(price.getDeliverPrice());

						//找提成比例
						if (true) {

							WeBusiness weBusiness = bOrder.getWeBusiness();

							if (weBusiness != null) {
								switch (weBusiness.getType()) {
									case 0:
										//找提成模型
										List<Filter> filters4 = new ArrayList<>();
										filters4.add(Filter.eq("modelType", "虹宇门店"));
										filters4.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels = weDivideModelService.findList(null, filters4, null);
										map.setDivideRatio(price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
										break;
									case 1:
										//找提成模型
										List<Filter> filters5 = new ArrayList<>();
										filters5.add(Filter.eq("modelType", "非虹宇门店"));
										filters5.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels1 = weDivideModelService.findList(null, filters5, null);
										map.setDivideRatio( price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
										break;
									case 2:
										//找提成模型
										List<Filter> filters6 = new ArrayList<>();
										filters6.add(Filter.eq("modelType", "个人商贸"));
										filters6.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels2 = weDivideModelService.findList(null, filters6, null);
										map.setDivideRatio( price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
										break;
									default:
										break;
								}
							} else {
								map.setDivideRatio(null);
							}
						} else {
							map.setDivideRatio(null);
						}
						//提成金额
						if (map.getDivideRatio() != null) {
							s.setDividMoney(item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal) map.getDivideRatio()).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
						}

					}

					if(true) {
						WeBusiness weBusiness = bOrder.getWeBusiness();

						if (weBusiness != null) {
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									map.setDivideRatio( groupitemPromotion.getStoreDivide().getProportion().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									map.setDivideRatio( groupitemPromotion.getExterStoreDivide().getProportion().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									map.setDivideRatio( groupitemPromotion.getBusinessPersonDivide().getProportion().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							map.setDivideRatio(null);
						}
					}else {
						map.setDivideRatio(null);
					}

//					map.setCostPrice(costPrice);
					map.setCostPrice(costPrice.multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(2,RoundingMode.HALF_UP)); //2019-08-05改成订单条目总的成本

					//提成金额
					if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){

						if(map.getDivideRatio()!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(costPrice).subtract(
								deliverPrice).multiply((BigDecimal)map.getDivideRatio()));
							map.setDivideMoney(divideMoney.setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
						}

					}else{
						map.setDivideMoney(BigDecimal.ZERO);
					}

				}

				//没用余额才算分成值，用余额了没有分成
				if(bOrder.getBalanceMoney()==null || bOrder.getBalanceMoney().doubleValue() == 0){
					if(item.getPromotionId()!=null && item.getPromotionId().getDivideMoney()!=BigDecimal.ZERO){
						map.setDivideMoney(item.getPromotionId().getDivideMoney().setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。totalDivideMoney好像没用上，就不改了。
					}
				}else{
					map.setDivideMoney(BigDecimal.ZERO);
				}
				
				//前台返利用的divideMoney，改成总返利，导出excel接口里好像没有totalDivideMoney，先用divideMoney乘数量。
				if(map.getDivideMoney()!=null){
					map.setDivideMoney(map.getDivideMoney().multiply(BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));
				}
				
				results.add(map);
			}
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			sb2.append("当日对账单_"+format.format(startdate)+"-"+format.format(enddate));
			String fileName = "当日对账单_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "nowadayDuizhang.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			return null;
		}

		return null;
	}


	public static class ProviderOrder {
		private String providerName;
		private String specialtyName;
		private String specification;
		private Integer quantity;
		private String orderCode;
		private String receiverName;
		private String receiverPhone;
		private String receiverAddress;
		private BigDecimal costPrice;
		private String remark;
		private BigDecimal salePrice;//单价
		private BigDecimal totalSalePrice;//应支付金额
		private BigDecimal payMoney;
		private BigDecimal balanceMoney;
		private BigDecimal couponMoney;

		public BigDecimal getDivideRatio() {
			return divideRatio;
		}

		public void setDivideRatio(BigDecimal divideRatio) {
			this.divideRatio = divideRatio;
		}

		private BigDecimal divideRatio;
		

		public BigDecimal getDivideMoney() {
			return divideMoney;
		}

		public void setDivideMoney(BigDecimal divideMoney) {
			this.divideMoney = divideMoney;
		}

		private BigDecimal divideMoney;
		private Date payTime;
		private String weBusinessName;
		private String storeName;

		public String getProviderName() {
			return providerName;
		}
		public void setProviderName(String providerName) {
			this.providerName = providerName;
		}
		public String getSpecialtyName() {
			return specialtyName;
		}
		public void setSpecialtyName(String specialtyName) {
			this.specialtyName = specialtyName;
		}
		public String getSpecification() {
			return specification;
		}
		public void setSpecification(String specification) {
			this.specification = specification;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getReceiverName() {
			return receiverName;
		}
		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
		public String getReceiverPhone() {
			return receiverPhone;
		}
		public void setReceiverPhone(String receiverPhone) {
			this.receiverPhone = receiverPhone;
		}
		public String getReceiverAddress() {
			return receiverAddress;
		}
		public void setReceiverAddress(String receiverAddress) {
			this.receiverAddress = receiverAddress;
		}
		public BigDecimal getCostPrice() {
			return costPrice;
		}
		public void setCostPrice(BigDecimal costPrice) {
			this.costPrice = costPrice;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public BigDecimal getSalePrice() {
			return salePrice;
		}
		public void setSalePrice(BigDecimal salePrice) {
			this.salePrice = salePrice;
		}
		public BigDecimal getTotalSalePrice() {
			return totalSalePrice;
		}
		public void setTotalSalePrice(BigDecimal totalSalePrice) {
			this.totalSalePrice = totalSalePrice;
		}
		public BigDecimal getPayMoney() {
			return payMoney;
		}
		public void setPayMoney(BigDecimal payMoney) {
			this.payMoney = payMoney;
		}
		public BigDecimal getBalanceMoney() {
			return balanceMoney;
		}
		public void setBalanceMoney(BigDecimal balanceMoney) {
			this.balanceMoney = balanceMoney;
		}
		public BigDecimal getCouponMoney() {
			return couponMoney;
		}
		public void setCouponMoney(BigDecimal couponMoney) {
			this.couponMoney = couponMoney;
		}
		public Date getPayTime() {
			return payTime;
		}
		public void setPayTime(Date payTime) {
			this.payTime = payTime;
		}
		public String getWeBusinessName() {
			return weBusinessName;
		}
		public void setWeBusinessName(String weBusinessName) {
			this.weBusinessName = weBusinessName;
		}
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}

	}

}
