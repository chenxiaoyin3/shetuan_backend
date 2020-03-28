package com.hongyu.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.util.Constants;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.OrderSNGenerator;
import com.hongyu.util.OrderTransactionSNGenerator;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.wechatpay.util.XMLUtil;
import com.sun.mail.imap.protocol.Item;

@RequestMapping("/ymmall/order")
@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class YmmallOrderController {
	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;

	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;

	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;

	@Resource(name = "purchaseItemServiceImpl")
	PurchaseItemService purchaseItemServiceImpl;

	@Resource(name = "inboundServiceImpl")
	InboundService inboundService;

	@Resource(name = "hySingleitemPromotionServiceImpl")
	HySingleitemPromotionService hySingleitemPromotionServiceImpl;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "hyPromotionServiceImpl")
	HyPromotionService hyPromotionService;

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;

	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@Resource(name = "specialtyAppraiseServiceImpl")
	SpecialtyAppraiseService specialtyAppraiseService;

	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;

	@Resource(name = "couponServiceImpl")
	CouponService couponService;

	@Resource(name = "shipServiceImpl")
	ShipService shipService;

	@Resource(name = "businessOrderRefundServiceImpl")
	BusinessOrderRefundService businessOrderRefundService;

	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;

	@Autowired
	BusinessSystemSettingService businessSystemSettingService;

	@Resource(name = "wechatAccountBalanceServiceImpl")
	private WechatAccountBalanceService wechatAccountBalanceService;

	@RequestMapping(value = { "/create" }, method = RequestMethod.POST)
	@ResponseBody
	// public Json createOrder(BusinessOrder businessOrder, @RequestParam(value
	// = "orderWechatId") Long wechatId,
	// @RequestParam(value = "webusinessId") Long webusinessId,
	// @RequestParam(value = "orderItems") List<Map<String, Object>> orderItems,
	// @RequestParam(value = "coupons") List<Long> coupons, HttpSession session)
	// {
	public Json createOrder(HttpSession session, @RequestParam(required = false) HashMap<String, Object> params,
			@RequestBody HashMap<String, Object> bodys) {
		Json json = new Json();
		try {
			HashMap<String, Object> customOrder = null;
			if (params != null) {
				customOrder = params;
			}
			if (bodys != null) {
				customOrder = bodys;
			}

			if (customOrder == null) {
				json.setSuccess(false);
				json.setMsg("没有请求参数");
				json.setObj(null);
				return json;
			}
			BusinessOrder businessOrder = new BusinessOrder();
			businessOrder.setOrderPhone((String) customOrder.get("orderPhone"));
			businessOrder.setTotalMoney(BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("totalMoney"))));
			businessOrder.setPromotionAmount(
					BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("promotionAmount"))));
			businessOrder.setShipFee(BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("shipFee"))));
			businessOrder.setShouldPayMoney(
					BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("shouldPayMoney"))));
			businessOrder
					.setCouponMoney(BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("couponMoney"))));
			businessOrder
					.setBalanceMoney(BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("balanceMoney"))));
			businessOrder.setPayMoney(BigDecimal.valueOf(Double.parseDouble((String) customOrder.get("payMoney"))));
			businessOrder.setReceiverRemark((String) customOrder.get("receiverRmark"));
			businessOrder.setReceiverAddress((String) customOrder.get("receiverAddress"));
			businessOrder.setReceiverName((String) customOrder.get("receiverName"));
			businessOrder.setReceiverPhone((String) customOrder.get("receiverPhone"));
			businessOrder.setReceiveType((Integer) customOrder.get("receiverType"));
			Long wechatId = customOrder.get("orderWechatId") == null ? null
					: ((Integer) customOrder.get("orderWechatId")).longValue();
			Long webusinessId = customOrder.get("webusinessId") == null ? null
					: ((Integer) customOrder.get("webusinessId")).longValue();

			List<Map<String, Object>> orderItems = (List<Map<String, Object>>) customOrder.get("orderItems");
			List<Integer> coupons = (List<Integer>) customOrder.get("coupons");

			/* 判断余额支付比例 */
			List<Filter> settingFilters = new ArrayList<>();
			settingFilters.add(Filter.like("settingName","余额最大支付比例"));

			List<BusinessSystemSetting> settings = businessSystemSettingService.findList(null,settingFilters,null);
			if(!settings.isEmpty()){
				BusinessSystemSetting setting = settings.get(0);
				BigDecimal ratio = BigDecimal.valueOf(Double.valueOf(setting.getSettingValue()));
				if(businessOrder.getBalanceMoney().divide(businessOrder.getShouldPayMoney(),2, RoundingMode.HALF_UP)
					.compareTo(ratio) > 0){
					throw new Exception("支付余额比例不能大于0.7");
				}

			}


			/* 判断库存 */
			if(!specialtySpecificationService.isBaseInboundEnough(orderItems)){
				throw new Exception("库存不足，无法下单");
			}
			
			/* 判断优惠活动数量是否满足 */
			for (Map<String, Object> orderItem : orderItems) {
				Boolean isGroupPromotion = (Boolean) orderItem.get("isGroupPromotion");
				// 如果是组合产品
				if (isGroupPromotion) {
					// 获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion = hyGroupitemPromotionServiceImpl
							.find(((Integer) orderItem.get("specialtyId")).longValue());
					// 获取购买数量
					Integer quantity = (Integer) orderItem.get("quantity");
					//判断优惠数量
					if(groupitemPromotion.getPromoteNum()<quantity) {
						throw new Exception("组合优惠活动数量不足，无法下单");
					}
					if(groupitemPromotion.getLimitedNum()<quantity) {
						throw new Exception("超出组合优惠限购数量");
					}
				} else {// 如果是普通产品
					
					if(orderItem.get("promotionId") != null) {
						Long promotionId = ((Integer) orderItem.get("promotionId")).longValue();
						Integer quantity = (Integer) orderItem.get("quantity");
						// 获取优惠明细
						HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
								((Integer) orderItem.get("specialtySpecificationId")).longValue(),
								promotionId);
						// 如果参加了优惠活动
						if (singleitemPromotion != null) {
							//判断优惠活动数量
							if(singleitemPromotion.getPromoteNum()<quantity) {
								throw new Exception("普通优惠活动数量不足，无法下单");
							}
							if(singleitemPromotion.getLimitedNum()<quantity) {
								throw new Exception("超出普通优惠限购数量");
							}
						}
					}
				}
			}
			/* 判断余额 */
			if (wechatId == null) {
				throw new Exception("微信账号不存在，无法下单");
			}
			WechatAccount wechatAccount = wechatAccountService.find(wechatId);
			// 获取使用余额
			BigDecimal balanceMoney = businessOrder.getBalanceMoney();
			
			if(wechatAccount.getTotalbalance().compareTo(balanceMoney)<0) {
				throw new Exception("账户余额不足，无法下单");
				
			}
			
	

			/*
			 * 购买数量没问题，进行下列步骤 2-建立订单记录
			 */
			// 得到下单微信账户

			businessOrder.setWechatAccount(wechatAccount);


			// 得到订单所属微商
			if (webusinessId == null) {
				businessOrder.setWeBusiness(null);
			} else {
				WeBusiness weBusiness = weBusinessService.find(webusinessId);
				businessOrder.setWeBusiness(weBusiness);
			}

			// 设置订单状态
			businessOrder.setOrderState(0/* 待付款 */);
			// 设置下单时间
			businessOrder.setOrderTime(new Date());
			// 设置电子券id字符串
			String couponsStr = "";
			for (Integer coupon : coupons) {
				couponsStr += String.format("%d", coupon) + ";";
			}
			businessOrder.setCouponId(couponsStr);
			// 设置订单条目
			Set<BusinessOrderItem> businessOrderItems = new HashSet<>();
			for (Map<String, Object> orderItem : orderItems) {
				BusinessOrderItem businessOrderItem = new BusinessOrderItem();
				businessOrderItem.setBusinessOrder(businessOrder); // 设置订单
				businessOrderItem.setSpecialty(((Integer) orderItem.get("specialtyId")).longValue()); // 设置产品
				businessOrderItem.setSpecialtySpecification(((Boolean) orderItem.get("isGroupPromotion")) ? null
						: ((Integer) orderItem.get("specialtySpecificationId")).longValue()); // 设置规格
				
				// 不设置采购明细
				// if (orderItem.get("purchaseItemId") != null) { // 设置采购明细
				// businessOrderItem
				// .setPurchaseItem(purchaseItemServiceImpl.find(((Integer)orderItem.get("purchaseItemId")).longValue()));
				// } else {
				// businessOrderItem.setPurchaseItem(null);
				// }
				businessOrderItem.setQuantity((Integer) orderItem.get("quantity"));
				BigDecimal curPrice = (BigDecimal.valueOf(Double.parseDouble((String) orderItem.get("curPrice"))));
				BigDecimal promotionPrice = curPrice.multiply(businessOrder.getPromotionAmount()
						.divide(businessOrder.getTotalMoney(), 6, BigDecimal.ROUND_HALF_DOWN));
				businessOrderItem.setOriginalPrice(curPrice); // 设置购买原价
				businessOrderItem.setSalePrice(curPrice.subtract(promotionPrice)); // 设置优惠后价格
				businessOrderItem.setIsappraised(false);

				if (orderItem.get("promotionId") != null) { // 设置优惠活动
					businessOrderItem.setPromotionId(
							hyPromotionService.find(((Integer) orderItem.get("promotionId")).longValue()));
				} else {
					businessOrderItem.setPromotionId(null);
				}

				businessOrderItem.setType(((Boolean) orderItem.get("isGroupPromotion")) ? 1 : 0); // 0:普通产品；1：组合产品

				businessOrderItem.setIsGift((Boolean)orderItem.get("isGift"));
				// 设置供应商
				if (businessOrderItem.getType() == 1) { // 如果是组合产品
					businessOrderItem.setDeliverType(0);
				} else { // 如果是普通产品
					Specialty specialty = specialtyService.find(businessOrderItem.getSpecialty());
					businessOrderItem.setDeliverName(specialty.getProvider().getProviderName());
				}
				businessOrderItems.add(businessOrderItem);
				
				//更新规格的基础库存和销量
				specialtySpecificationService.updateBaseInboundAndHasSold(businessOrderItem, true);
				
				
				
			}
			businessOrder.setBusinessOrderItems(businessOrderItems);


			String code = businessOrderService.getOrderCode();
			businessOrder.setOrderCode(code);
			businessOrder.setIsValid(true);
			businessOrder.setIsShow(false);
			businessOrder.setIsAppraised(false);
			businessOrder.setIsDivided(false);


			/*
			 * 3-修改优惠数量
			 */
			for (Map<String, Object> orderItem : orderItems) {
				Boolean isGroupPromotion = (Boolean) orderItem.get("isGroupPromotion");
				//2019-04-28改：组合优惠相关数量的修改在specialtySpecificationService.updateBaseInboundAndHasSold中进行。
				if (!isGroupPromotion) {// 如果是普通产品
					if(orderItem.get("promotionId") != null) {
						Long promotionId = ((Integer) orderItem.get("promotionId")).longValue();
						// 获取购买数量
						Integer quantity = (Integer) orderItem.get("quantity");
						// 获取优惠明细
						HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
								((Integer) orderItem.get("specialtySpecificationId")).longValue(),
								promotionId);
						// 如果参加了优惠活动
						if (singleitemPromotion != null) {

							// 修改优惠数量
							singleitemPromotion.setPromoteNum(singleitemPromotion.getPromoteNum() - quantity);
							singleitemPromotion.setHavePromoted(singleitemPromotion.getHavePromoted() + quantity);
							
							hySingleitemPromotionServiceImpl.update(singleitemPromotion);
						}
					}
				}
			}

			/*
			 * 4-修改余额电子券
			 */

			// 修改账户余额
			if (balanceMoney.compareTo(BigDecimal.ZERO) > 0) {
				wechatAccount.setTotalbalance(wechatAccount.getTotalbalance().subtract(balanceMoney));
				wechatAccountService.update(wechatAccount);
			}

			// 下单成功, 若使用的余额不为0, 在hy_wechat_account_balance中增加数据
			if(businessOrder.getBalanceMoney() != null && businessOrder.getBalanceMoney().compareTo(BigDecimal.ZERO) > 0){
				WechatAccountBalance wechatAccountBalance = new WechatAccountBalance();
				wechatAccountBalance.setWechatAccountId(businessOrder.getWechatAccount().getId());
				wechatAccountBalance.setType(WechatAccountBalance.WechatAccountBalanceType.use);
				wechatAccountBalance.setCreateTime(new Date());
				wechatAccountBalance.setAmount(businessOrder.getBalanceMoney());
				wechatAccountBalance.setSurplus(wechatAccount.getTotalbalance());
				wechatAccountBalanceService.save(wechatAccountBalance);
			}


			// 修改电子券
			// for (Integer couponId : coupons) {
			// Coupon coupon = couponService.find(couponId.longValue());
			// if (coupon != null) {
			// // 设置为已使用
			// coupon.setState(1/* 已使用 */);
			// // 设置使用时间
			// coupon.setUseTime(new Date());
			// couponService.update(coupon);
			// }
			// }

			for (Integer couponId : coupons) {
				CouponGift coupon = couponGiftService.find(couponId.longValue());
				if (coupon != null) {
					// 设置为已使用
					coupon.setState(1/* 已使用 */);
					// 设置使用时间
					coupon.setUseTime(new Date());
					couponGiftService.update(coupon);
				}
			}


			
			Map<String, Object> ret = new HashMap<>();
			ret.put("orderId", businessOrder.getId());
			ret.put("orderCode", businessOrder.getOrderCode());
			// 返回订单编号和id
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(ret);
			
			// 将订单信息保存至数据库中
			businessOrderService.save(businessOrder);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}

	/* 获取某一规格有效采购批次 */
	private PurchaseItem getValidPurchaseItem(Long specialtySpecificationId) {
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		if (specialtySpecification == null) {
			return null;
		}
		List<Filter> purchaseFilters = new ArrayList<Filter>();
		purchaseFilters.add(new Filter("specification", Filter.Operator.eq, specialtySpecification));
		purchaseFilters.add(new Filter("isValid", Filter.Operator.eq, true));
		purchaseFilters.add(new Filter("state", Filter.Operator.eq, true));
		List<Order> purchaseOrders = new ArrayList<Order>();
		purchaseOrders.add(Order.asc("id"));
		List<PurchaseItem> purchaseItems = purchaseItemServiceImpl.findList(null, purchaseFilters, purchaseOrders);
		if (purchaseItems == null || purchaseItems.isEmpty()) {
			return null;
		}
		// 获取当前有效批次
		PurchaseItem validPurchaseItem = purchaseItems.get(0);

		return validPurchaseItem;
	}

	/* 获取有效普通优惠明细 */
	private HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId,Long promotionId) {
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		if (specialtySpecification == null) {
			return null;
		}
		HyPromotion promotion = hyPromotionService.find(promotionId);
		if(promotion == null) {
			return null;
		}
		
		List<Filter> singleFilters = new ArrayList<>();
		singleFilters.add(new Filter("specificationId", Filter.Operator.eq, specialtySpecification));
		singleFilters.add(Filter.eq("hyPromotion", promotion));
		List<Order> singleOrders = new ArrayList<>();
		singleOrders.add(Order.desc("id"));
		List<HySingleitemPromotion> singleitemPromotions = hySingleitemPromotionServiceImpl.findList(null,
				singleFilters, singleOrders);
		if (singleitemPromotions == null || singleitemPromotions.isEmpty()) {
			return null;
		}

		return singleitemPromotions.get(0);
	}

	/* 获取商品规格有效库存 */
	public List<Inbound> getValidInbounds(Long specialtySpecificationId) {
		// 获取明细对应有效采购批次
		PurchaseItem purchaseItem = this.getValidPurchaseItem(specialtySpecificationId);
		if (purchaseItem == null) {
			return null;
		}
		// 获取有效采购批次对应库存
		List<Filter> inboundFilters = new ArrayList<>();
		inboundFilters.add(Filter.eq("purchaseItem", purchaseItem));
		inboundFilters.add(Filter.gt("inboundNumber", 0));
		List<Order> inboundOrders = new ArrayList<>();
		inboundOrders.add(Order.asc("productDate"));
		List<Inbound> inbounds = inboundService.findList(null, inboundFilters, inboundOrders);
		return inbounds;
	}

	static class WrapAppraises {
		private Long wechat_id;

		public Long getWechat_id() {
			return wechat_id;
		}

		public void setWechat_id(Long wechat_id) {
			this.wechat_id = wechat_id;
		}

		private List<WrapAppraise> wrapAppraises;

		public List<WrapAppraise> getWrapAppraises() {
			return wrapAppraises;
		}

		public void setWrapAppraises(List<WrapAppraise> wrapAppraises) {
			this.wrapAppraises = wrapAppraises;
		}
	}

	static class WrapAppraise {
		// private Long wechat_id;
		private Long orderItemId;
		private SpecialtyAppraise specialtyAppraise;

		// public Long getWechat_id() {
		// return wechat_id;
		// }
		//
		// public void setWechat_id(Long wechat_id) {
		// this.wechat_id = wechat_id;
		// }

		public Long getOrderItemId() {
			return orderItemId;
		}

		public void setOrderItemId(Long orderItemId) {
			this.orderItemId = orderItemId;
		}

		public SpecialtyAppraise getSpecialtyAppraise() {
			return specialtyAppraise;
		}

		public void setSpecialtyAppraise(SpecialtyAppraise specialtyAppraise) {
			this.specialtyAppraise = specialtyAppraise;
		}
	}

	@RequestMapping("/appraises/create")
	@ResponseBody
	public Json addAppraises(@RequestBody WrapAppraises wrapAppraises) {
		Json json = new Json();
		try {
			Long wechat_id = wrapAppraises.getWechat_id();
			List<WrapAppraise> wList = wrapAppraises.getWrapAppraises();
			WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
			if (wechatAccount == null) {
				json.setSuccess(false);
				json.setMsg("账号不存在");
			} else if (wList != null && wList.size() > 0) {
				for (WrapAppraise wrapAppraise : wList) {
					if (wrapAppraise != null) {

						Long orderItemId = wrapAppraise.getOrderItemId();
						SpecialtyAppraise specialtyAppraise = wrapAppraise.getSpecialtyAppraise();
						if (specialtyAppraise != null) {
							if(specialtyAppraise.getImages()!=null&&specialtyAppraise.getImages().size()>0){
								for(SpecialtyAppraiseImage specialtyAppraiseImage:specialtyAppraise.getImages()){
									specialtyAppraiseImage.setAppraise(specialtyAppraise);
								}
							}
							BusinessOrderItem businessOrderItem = businessOrderItemService.find(orderItemId);
							specialtyAppraise.setOrderItem(businessOrderItem);

							Specialty specialty = specialtyService.find(businessOrderItem.getSpecialty());
							specialtyAppraise.setSpecialty(specialty);
							specialtyAppraise.setBusinessOrder(businessOrderItem.getBusinessOrder());

							SpecialtySpecification specialtySpecification = specialtySpecificationService
									.find(businessOrderItem.getSpecialtySpecification());
							specialtyAppraise.setSpecification(specialtySpecification);
							specialtyAppraise.setAccount(wechatAccount);
							specialtyAppraiseService.save(specialtyAppraise);
							businessOrderItem.setIsappraised(true);
							businessOrderItemService.update(businessOrderItem);
							BusinessOrder businessOrder = businessOrderItem.getBusinessOrder();
							businessOrder.setIsAppraised(true);
							businessOrderService.update(businessOrder);
							
							//用户评价一个条目，增加用户10积分
							pointrecordService.changeUserPoint(wechat_id, 10, "评价");
						}
					}
				}
				json.setSuccess(true);
				json.setMsg("添加成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	// @RequestMapping("/appraise/create")
	// @ResponseBody
	// public Json addAppraise(@RequestBody WrapAppraise wrapAppraise) {
	// Json json = new Json();
	// try {
	// Long wechat_id = wrapAppraise.getWechat_id();
	// Long orderItemId = wrapAppraise.getOrderItemId();
	// SpecialtyAppraise specialtyAppraise =
	// wrapAppraise.getSpecialtyAppraise();
	// BusinessOrderItem businessOrderItem =
	// businessOrderItemService.find(orderItemId);
	// specialtyAppraise.setOrderItem(businessOrderItem);
	//
	// Specialty specialty =
	// specialtyService.find(businessOrderItem.getSpecialty());
	// specialtyAppraise.setSpecialty(specialty);
	// specialtyAppraise.setBusinessOrder(businessOrderItem.getBusinessOrder());
	//
	// SpecialtySpecification specialtySpecification =
	// specialtySpecificationService
	// .find(businessOrderItem.getSpecialtySpecification());
	// specialtyAppraise.setSpecification(specialtySpecification);
	// // Long wechat_id = (Long) session.getAttribute("wechat_id");
	// WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
	// if (wechatAccount == null) {
	// json.setSuccess(false);
	// json.setMsg("账号不存在");
	// } else {
	// specialtyAppraise.setAccount(wechatAccount);
	// specialtyAppraiseService.save(specialtyAppraise);
	// businessOrderItem.setIsappraised(true);
	// businessOrderItemService.update(businessOrderItem);
	// json.setSuccess(true);
	// json.setMsg("添加成功");
	// }
	// } catch (Exception e) {
	// json.setSuccess(false);
	// json.setMsg("添加失败: " + e.getMessage());
	// e.printStackTrace();
	// // TODO: handle exception
	// }
	// return json;
	// }

	@RequestMapping("/appraise/get")
	@ResponseBody
	public Json getAppraise(Long orderItemId) {
		Json json = new Json();
		try {
			BusinessOrderItem businessOrderItem = businessOrderItemService.find(orderItemId);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderItem", businessOrderItem));
			List<SpecialtyAppraise> lists = specialtyAppraiseService.findList(null, filters, null);
			if (lists == null || lists.size() == 0) {
				json.setSuccess(false);
				json.setMsg("无评论");
			} else {
				SpecialtyAppraise specialtyAppraise = lists.get(0);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(specialtyAppraise);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@Resource(name = "orderTransactionServiceImpl")
	OrderTransactionService orderTransactionServiceImpl;

	@Resource(name = "pointrecordServiceImpl")
	PointrecordService pointrecordService;
	
	@Resource(name = "viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Resource(name = "vipServiceImpl")
	VipService vipService;
	
	@Resource(name = "couponBalanceUseServiceImpl")
	CouponBalanceUseService couponBalanceUseService;
	
	// 订单支付成功回调
	@RequestMapping(value = { "/pay/wechat/notify/{orderId}" }, method = RequestMethod.POST)
	public void notify(@PathVariable String orderId, @RequestParam Map<String, Object> params,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 读取参数
		InputStream inputStream;
		StringBuffer sb = new StringBuffer();
		inputStream = request.getInputStream();
		String s;
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		while ((s = in.readLine()) != null) {
			sb.append(s);
		}
		in.close();
		inputStream.close();

		// 解析xml成map
		Map<String, String> m = new HashMap<String, String>();
		m = XMLUtil.doXMLParse(sb.toString());
		System.out.println("m=\n" + m);

		/*
		 * 支付成功，处理回调,修改订单状态
		 */
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("orderCode", orderId)); // 查找订单编号
		List<BusinessOrder> businessOrders = businessOrderService.findList(null, filters, null);
		// 修改订单状态从“待付款”到“待出库”
		if (businessOrders != null && !businessOrders.isEmpty()) {
			BusinessOrder businessOrder = businessOrders.get(0);
			businessOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW/* 待审核 */);
			businessOrder.setPayTime(new Date());
			businessOrderService.update(businessOrder);

			// 添加交易记录表
			// 生成交易记录
			OrderTransaction transaction = new OrderTransaction();
			transaction.setBusinessOrder(businessOrder);
			transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
			transaction.setWechatBalance(businessOrder.getBalanceMoney());
			transaction.setOrderCoupon(businessOrder.getCouponMoney());
			transaction.setPayAccount(businessOrder.getWechatAccount().getWechatOpenid());
			// 微信支付
			transaction.setPayType(1);
			transaction.setPayment(businessOrder.getPayMoney());
			transaction.setPayFlow(1);
			transaction.setPayTime(businessOrder.getPayTime());
			orderTransactionServiceImpl.save(transaction);

			// 购买成功，发送短信
//			String str = "您已成功支付了" + businessOrder.getPayMoney().setScale(2, BigDecimal.ROUND_HALF_UP) + "元的订单，订单编号为："
//					+ businessOrder.getOrderCode() + "。";
			StringBuilder sb1 = new StringBuilder();
//			sb1.append(str);
//			sb1.append("您购买的商品有：");
			for (BusinessOrderItem item : businessOrder.getBusinessOrderItems()) {
				//判断如果是组合优惠就不显示规格名，防止短信中出现"null" 2019-04-28改
				String specificationName = businessOrderItemService.getSpecificationName(item);
				sb1.append(businessOrderItemService.getSpecialtyName(item) + "("
						+ (specificationName!=null?specificationName:"") + "*" + item.getQuantity() + ")；");
			}
			String phone = null;
			if (businessOrder.getOrderPhone() != null) {
				phone = businessOrder.getOrderPhone();
			} else {
				phone = businessOrder.getReceiverPhone();
			}
			if (phone != null) {
				//write by wj
				String amount = businessOrder.getPayMoney().setScale(2, BigDecimal.ROUND_HALF_UP) + "元";
				String code = businessOrder.getOrderCode();
				String product = sb1.toString();
				String message = "{\"amount\":\""+amount+"\",\"code\":\""+code+"\",\"product\":\""+product+"\"}";
//				SendMessageEMY.sendMessage(phone, sb1.toString());
				SendMessageEMY.businessSendMessage(phone,message,5);
			}


			
			//支付成功，修改用户积分
			//首先判断订单是否参加优惠活动,
			if(!businessOrderService.havePromotions(businessOrder)) {
				//没有参加过优惠活动
				if(businessOrder.getShouldPayMoney().equals(businessOrder.getPayMoney())) {
					//如果本订单的全部用现金支付
					BigDecimal money = businessOrder.getPayMoney();
					Integer changevalue = money.intValue()/10;
					
					//判断是否318会员
					vipService.setVip318(businessOrder.getWechatAccount(), money);
					//满足条件，增加用户积分，每10元兑换 1 积分
					if(changevalue!=0) {
						pointrecordService.changeUserPoint(businessOrder.getWechatAccount().getId(),changevalue , "购物");
					}
					
					//新用户首单奖励
					WechatAccount wechatAccount = businessOrder.getWechatAccount();	
					if(wechatAccount.getIsNew()) {
						//2019-04-28 首单奖励改成系统参数
						List<Filter> settingFilters = new ArrayList<>();
						settingFilters.add(Filter.like("settingName","首单奖励限额"));
						List<BusinessSystemSetting> firstBusinessOrderAwardCeilingSettings = businessSystemSettingService.findList(null,settingFilters,null);
						BigDecimal firstBusinessOrderAwardCeiling = BigDecimal.valueOf(50);//如果系统配置中没查到，就按照更之前定好的首单最多奖励50算
						if(firstBusinessOrderAwardCeilingSettings!=null && !firstBusinessOrderAwardCeilingSettings.isEmpty()){
							BusinessSystemSetting firstBusinessOrderAwardCeilingSetting = firstBusinessOrderAwardCeilingSettings.get(0);
							firstBusinessOrderAwardCeiling = BigDecimal.valueOf(Double.valueOf(firstBusinessOrderAwardCeilingSetting.getSettingValue()));
						}
						
						settingFilters.clear();
						settingFilters.add(Filter.like("settingName","首单奖励比例"));
						List<BusinessSystemSetting> firstBusinessOrderAwardRatioSettings = businessSystemSettingService.findList(null,settingFilters,null);
						BigDecimal firstBusinessOrderAwardRatio = BigDecimal.valueOf(0.2);//如果系统配置中没查到，就按照更之前定好的比例0.2算
						if(firstBusinessOrderAwardCeilingSettings!=null && !firstBusinessOrderAwardRatioSettings.isEmpty()){
							BusinessSystemSetting firstBusinessOrderAwardRatioSetting = firstBusinessOrderAwardRatioSettings.get(0);
							firstBusinessOrderAwardRatio = BigDecimal.valueOf(Double.valueOf(firstBusinessOrderAwardRatioSetting.getSettingValue()));
						}
						BigDecimal awardMoney = money.multiply(firstBusinessOrderAwardRatio);
						if(awardMoney.compareTo(firstBusinessOrderAwardCeiling)==1) {//如果大于首单奖励限额
							awardMoney = firstBusinessOrderAwardCeiling;//按照限额来设置首单奖励
						}
						
						//修改用户余额
						if(wechatAccount.getTotalbalance()==null) {
							wechatAccount.setTotalbalance(BigDecimal.ZERO);
						}
						wechatAccount.setTotalbalance(wechatAccount.getTotalbalance().add(awardMoney));
						wechatAccount.setIsNew(false);
						wechatAccountService.update(wechatAccount);
						//添加余额兑换记录
						CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
						couponBalanceUse.setPhone(wechatAccount.getPhone());
						couponBalanceUse.setType(6);	//6首单奖励
						couponBalanceUse.setState(1);
						couponBalanceUse.setUseAmount(awardMoney.floatValue());
						couponBalanceUse.setUseTime(new Date());
						couponBalanceUse.setWechatId(wechatAccount.getId());
						couponBalanceUseService.save(couponBalanceUse);
						
					}
					
					
				}
			}
			
			//下单成功，修改用户是否为新用户
			WechatAccount wechatAccount = businessOrder.getWechatAccount();	
			if(wechatAccount.getIsNew()) {
				wechatAccount.setIsNew(false);
				wechatAccountService.update(wechatAccount);
			}




		}

		String resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
				+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(resXml.getBytes());
		out.flush();
		out.close();
		return;
	}

	/**
	 * 订单列表
	 * 
	 */
	@RequestMapping("/list_by_account")
	@ResponseBody
	public Json listByAccount(Pageable pageable, @RequestParam("wechat_id") Long wechatId, Integer status) {
		Json json = new Json();
		try {
			Integer oldPage = pageable.getPage();
			WechatAccount wAccount = wechatAccountService.find(wechatId);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("wechatAccount", wAccount));
			filters.add(Filter.eq("orderState", status));
			filters.add(Filter.eq("isValid", true));
			filters.add(Filter.eq("isShow", false));
			List<Order> orders1 = new ArrayList<>();
			orders1.add(Order.desc("orderTime"));
			pageable.setFilters(filters);
			pageable.setOrders(orders1);
			Page<BusinessOrder> orderPage = businessOrderService.findPage(pageable);
			if(pageable.getPage()<oldPage) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				pageable.setPage(oldPage);
				List<Map<String, Object>> tmps = new ArrayList<>();
				Page<Map<String, Object>> pages = new Page(tmps, 0, pageable);
				json.setObj(pages);
				return json;
			}
			List<BusinessOrder> orderList = orderPage.getRows();
			List<Map<String, Object>> orders = new ArrayList<>();
			for (BusinessOrder bOrder : orderList) {
				Map<String, Object> map = businessOrderService.getOrderListItemMap(bOrder);
				orders.add(map);
			}
			
			Page<Map<String, Object>> pages = new Page(orders, orderPage.getTotal(), pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pages);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);

		}

		return json;
	}

	@RequestMapping("/detail")
	@ResponseBody
	public Json detail(@RequestParam("orderid") Long orderId) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderService.find(orderId);
			Map<String, Object> map = new HashMap<>();
			map.put("baseInfo", order);
			// 订单条目
			Set<BusinessOrderItem> itemList = order.getBusinessOrderItems();
			List<Map<String, Object>> items = new ArrayList<>();
			for (BusinessOrderItem bItem : itemList) {
				Map<String, Object> itemMap = new HashMap<>();
				itemMap.put("id", bItem.getId());
				itemMap.put("specialtyId", bItem.getSpecialty());
				itemMap.put("specialtySpecificationId", bItem.getSpecialtySpecification());
				itemMap.put("purchaseItemId", bItem.getPurchaseItem());
				itemMap.put("quantity", bItem.getQuantity());
				itemMap.put("originalPrice", bItem.getOriginalPrice());
				itemMap.put("salePrice", bItem.getSalePrice());
				itemMap.put("isGift", bItem.getIsGift());
				int itemType = bItem.getType();
				if (itemType == 0) { // 普通商品
					Specialty specialty = specialtyService.find(bItem.getSpecialty());
					itemMap.put("name", specialty.getName());
					List<SpecialtyImage> images = specialty.getImages();
					for (SpecialtyImage image : images) {
						if (image.getIsLogo()) {
							itemMap.put("iconURL", image);
							break;
						}
					}

					SpecialtySpecification specialtySpecification = specialtySpecificationService
							.find(bItem.getSpecialtySpecification());
					itemMap.put("specification", specialtySpecification.getSpecification());
				} else {
					HyGroupitemPromotion hyGroupitemPromotion = hyGroupitemPromotionServiceImpl
							.find(bItem.getSpecialty());
					HyPromotion hyPromotion = hyGroupitemPromotion.getPromotionId();
					itemMap.put("name", hyPromotion.getPromotionName());
					Set<HyPromotionPic> images = hyPromotion.getHyPromotionPics();
					for (HyPromotionPic image : images) {
						if (image.getIsTag()) {
							itemMap.put("iconURL", image);
						}
					}

					itemMap.put("specification", null);
				}
				itemMap.put("type", itemType);

				itemMap.put("deliverName", bItem.getDeliverName());
				itemMap.put("deliverType", bItem.getDeliverType());
				itemMap.put("depotName", bItem.getDepotName());

				items.add(itemMap);
			}
			map.put("orderItems", items); // 订单条目

			// 物流信息
			List<Filter> shipFilters = new ArrayList<>();
			shipFilters.add(Filter.eq("orderId", order));
			List<Ship> ships = shipService.findList(null, null, shipFilters, null);
			map.put("ships", ships);

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}

	/**
	 * 退款订单详情
	 * 
	 * @param orderId
	 * @return
	 */
	@RequestMapping("/refund_detail")
	@ResponseBody
	public Json refundDetail(@RequestParam("orderid") Long orderId) {
		Json json = new Json();
		try {
			// 根据订单id获取退款订单
			BusinessOrder order = businessOrderService.find(orderId);
			List<Filter> refundFilters = new ArrayList<>();
			refundFilters.add(Filter.eq("businessOrder", order));
			List<BusinessOrderRefund> orderRefunds = businessOrderRefundService.findList(null, null, refundFilters,
					null);
			if (orderRefunds == null || orderRefunds.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("查询失败，不存在");
				json.setObj(null);
				return json;
			}
			List<Map<String, Object>> orderRefundsList = new ArrayList<>(); // 退款订单列表map
			BusinessOrderRefund orderRefund = orderRefunds.get(0);

			Map<String, Object> orderRefundMap = new HashMap<>(); // 退款订单map
			orderRefundMap.put("id", orderRefund.getId()); // id
			// 获取订单map
			Map<String, Object> orderMap = businessOrderService.getOrderListItemMap(order);
			orderRefundMap.put("order", orderMap);
			orderRefundMap.put("state", orderRefund.getState()); // 状态
			orderRefundMap.put("isDelivered", orderRefund.getIsDelivered()); // 是否退货
			orderRefundMap.put("deliverType", orderRefund.getDeliverType()); // 发货类型
			orderRefundMap.put("responsibleParty", orderRefund.getResponsibleParty()); // 责任方
			orderRefundMap.put("refundTotalAmount", orderRefund.getRefundTotalamount()); // 应退款总额
			orderRefundMap.put("refundShiper", orderRefund.getRefundShiper()); // 退货物流公司
			orderRefundMap.put("refundShipCode", orderRefund.getRefundShipCode()); // 退货物流单号
			orderRefundMap.put("refundReson", orderRefund.getRefundReason()); // 退款理由
			// 获取退货明细map
			List<Map<String, Object>> refundItems = businessOrderItemService.getRefundItemMapList(order);
			orderRefundMap.put("refundItems", refundItems);

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(orderRefundMap);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}

		return json;

	}

	@RequestMapping("/apply_refund")
	@ResponseBody
	// public Json applyRefund(@RequestParam("refund_info") Map<String, Object>
	// refundInfo) {
	public Json applyRefund(HttpSession session, @RequestParam(required = false) HashMap<String, Object> params,
			@RequestBody HashMap<String, Object> bodys) {
		HashMap<String, Object> refundInfo = null;
		if (params != null) {
			refundInfo = params;
		}
		if (bodys != null) {
			refundInfo = bodys;
		}
		Json json = new Json();

		try {
			Long orderId = ((Integer) refundInfo.get("orderId")).longValue(); // 订单ID
			// Boolean isDelivered = (Boolean) refundInfo.get("isDelivered"); //
			// 是否退货
			Integer deliverType = (Integer) refundInfo.get("deliverType"); // 发货类型
			String refundReason = (String) refundInfo.get("refundReason"); // 退款理由
			List<Map<String, Object>> refundItems = (List<Map<String, Object>>) refundInfo.get("refundItems"); // 退货条目

			// BusinessOrderRefund Entity
			BusinessOrderRefund bRefund = new BusinessOrderRefund();
			BusinessOrder bOrder = businessOrderService.find(orderId);
			if (bOrder == null || bOrder.getOrderState() != Constants.BUSINESS_ORDER_STATUS_HAS_RECEIVED) {
				json.setSuccess(true);
				json.setMsg("订单不存在或状态不可申请退款退货");
				Map<String, Object> obj = new HashMap<>();
				obj.put("orderId", orderId);
				obj.put("orderState", bOrder.getOrderState());
				json.setObj(obj);
				return json;
			}
			bRefund.setBusinessOrder(bOrder); // 订单
			// bRefund.setIsDelivered(isDelivered); // 是否退货
			bRefund.setDeliverType(deliverType); // 发货类型

			bRefund.setRefundReason(refundReason); // 退款理由
			bRefund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_CONFIRM); // 状态为售后人员待确认
			bRefund.setRefundApplyTime(new Date()); // 退款申请时间
			bRefund.setWechat(bOrder.getWechatAccount()); // 下单微信账户

			/* 退款金额以及设置退货条目 */
			BigDecimal orderTotal = bOrder.getTotalMoney().add(bOrder.getShipFee()); // 订单优惠前总额
			BigDecimal orderBalance = bOrder.getBalanceMoney(); // 余额支付金额
			BigDecimal orderPay = bOrder.getPayMoney(); // 其他支付金额

			BigDecimal refundOriginal = BigDecimal.ZERO; // 应退货商品总原价
			// 遍历退货明细
			for (Map<String, Object> item : refundItems) {
				Long itemId = ((Integer) item.get("id")).longValue(); // 订单明细id
				Integer refundQuantity = (Integer) item.get("refundQuantity"); // 退货数量

				BusinessOrderItem orderItem = businessOrderItemService.find(itemId); // 订单明细
				orderItem.setReturnQuantity(refundQuantity); // 设置退货数量
				// orderItem.setIsDelivered(isDelivered ? 1 : 0); // 设置是否退货

				refundOriginal = refundOriginal
						.add(orderItem.getOriginalPrice().multiply(BigDecimal.valueOf(refundQuantity)));

				businessOrderItemService.save(orderItem);

			}
			BigDecimal ratio = refundOriginal.divide(orderTotal, 8, BigDecimal.ROUND_HALF_DOWN); // 退款货物价格占比
			BigDecimal rRefundAmount = orderBalance.multiply(ratio); // 余额支付退款金额
			BigDecimal qRefundAmount = orderPay.multiply(ratio); // 其他支付退款金额
			BigDecimal refundAmount = rRefundAmount.add(qRefundAmount); // 货物退款金额

			bRefund.setrRefundAmount(rRefundAmount); // 设置余额支付退款金额
			bRefund.setqRefundAmount(qRefundAmount); // 设置其他支付退款金额
			bRefund.setRefundAmount(refundAmount); // 设置货物退款金额
			bRefund.setRefundTotalamount(refundAmount); // 设置应退款总额

			businessOrderRefundService.save(bRefund); // 保存退款订单信息

			bOrder.setRefoundMoney(refundAmount); // 订单设置退款金额
			bOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_APPLY_RETURN_GOODS_TO_CONFIRM); // 设置订单为退款待确认状态

			businessOrderService.save(bOrder); // 保存订单信息

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(null);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("失败");
			json.setObj(e);
		}

		return json;

	}

	// 确认收货
	@RequestMapping(value = { "/confirm_receive" })
	@ResponseBody
	public Json confirmReceive(@RequestParam("order_id") Long id) {
		Json json = new Json();
		try {
			BusinessOrder businessOrder = businessOrderService.find(id);
			if (businessOrder == null) {
				json.setSuccess(true);
				json.setMsg("订单不存在");
				json.setObj(null);
			}

			int state = businessOrder.getOrderState();
			if (state == Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE) {
				// 如果是等待收货状态，则执行确认收货动作
				businessOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_HAS_RECEIVED);
				businessOrder.setReceiveTime(new Date());
				businessOrderService.save(businessOrder);

				json.setSuccess(true);
				json.setMsg("确认收货成功");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			} else {
				json.setSuccess(true);
				json.setMsg("订单状态错误，无法确认收货");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("发生异常");
			json.setObj(e);
		}
		return json;
	}

	@Autowired
	BusinessOrderOutboundService orderOutboundService;

	@RequestMapping(value = { "/cancel" })
	@ResponseBody
	Json cancel(@RequestParam("order_id") Long id) {
		Json json = new Json();
		try {
			BusinessOrder businessOrder = businessOrderService.find(id);
			if (businessOrder == null) {
				json.setSuccess(true);
				json.setMsg("订单不存在");
				json.setObj(null);
			}

			int state = businessOrder.getOrderState();
			if (state <= Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY) {
				
				//恢复规格商品的库存和销量
				for(BusinessOrderItem orderItem:businessOrder.getBusinessOrderItems()){
					specialtySpecificationService.updateBaseInboundAndHasSold(orderItem, false);
					
					//修改优惠数量
					Boolean isGroupPromotion = orderItem.getType()==0?false:true;
					//2019-04-28改：组合优惠相关数量的修改在specialtySpecificationService.updateBaseInboundAndHasSold中进行。
					if (!isGroupPromotion) {// 如果是普通产品
						if(orderItem.getPromotionId()!=null) {
							// 获取购买数量
							Integer quantity = (Integer) orderItem.getQuantity();
							// 获取优惠明细
							HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
									orderItem.getSpecialtySpecification(),orderItem.getPromotionId().getId());
							// 如果参加了优惠活动
							if (singleitemPromotion != null) {

								// 修改优惠数量
								singleitemPromotion.setPromoteNum(singleitemPromotion.getPromoteNum() + quantity);
								singleitemPromotion.setHavePromoted(singleitemPromotion.getHavePromoted() - quantity);
								
								hySingleitemPromotionServiceImpl.update(singleitemPromotion);
							}
						}
					}
				}

				//发货类型是平台，状态是待发货的时候可以取消订单，需要将库存加回去
				if(state == Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY &&
					businessOrder.getIsDivided() == false){

					Set<BusinessOrderItem> orderItems = businessOrder.getBusinessOrderItems();

					List<Filter> outboundFilters = new ArrayList<>();
					outboundFilters.add(Filter.in("businessOrderItem",orderItems));

					List<BusinessOrderOutbound> orderOutbounds =
						orderOutboundService.findList(null,outboundFilters,null);

					for(BusinessOrderOutbound orderOutbound:orderOutbounds){

						Inbound inbound = orderOutbound.getInbound();

						inbound.setInboundNumber(inbound.getInboundNumber()+orderOutbound.getOutboundQuantity());
						inbound.setSaleNumber(inbound.getSaleNumber()-orderOutbound.getOutboundQuantity());

						inboundService.update(inbound);
						orderOutbound.setIsValid(false);
						orderOutboundService.update(orderOutbound);

					}

				}


				
				// 如果是未支付或未出库，则可以取消订单
				// 如果使用了余额支付，将余额退回用户
				if(state>Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY) {
					//如果已支付，要退款
					
					// BusinessOrderRefund Entity
					BusinessOrderRefund bRefund = new BusinessOrderRefund();
					BusinessOrder bOrder = businessOrder;

					bRefund.setBusinessOrder(bOrder); // 订单
					// bRefund.setIsDelivered(isDelivered); // 是否退货
					bRefund.setDeliverType(0); // 发货类型

					bRefund.setRefundReason("取消订单"); // 退款理由
					
					Date refundDate = new Date();
					bRefund.setRefundApplyTime(refundDate); // 退款申请时间
					bRefund.setRefundAcceptTime(refundDate);	//退款确认时间
					bRefund.setInboundTime(refundDate);	//设置入库时间
					bRefund.setShipTime(refundDate);	//设置物流时间
					bRefund.setReturnCompleteTime(refundDate);	//设置退货完成世界
					
					bRefund.setWechat(bOrder.getWechatAccount()); // 下单微信账户

					/* 退款金额以及设置退货条目 */
					BigDecimal orderTotal = bOrder.getTotalMoney().add(bOrder.getShipFee()); // 订单优惠前总额
					BigDecimal orderBalance = bOrder.getBalanceMoney(); // 余额支付金额
					BigDecimal orderPay = bOrder.getPayMoney(); // 其他支付金额
					
					BigDecimal rRefundAmount = orderBalance; // 余额支付退款金额
					BigDecimal qRefundAmount = orderPay; // 其他支付退款金额
					BigDecimal refundAmount = rRefundAmount.add(qRefundAmount); // 货物退款金额

					bRefund.setrRefundAmount(rRefundAmount); // 设置余额支付退款金额
					bRefund.setqRefundAmount(qRefundAmount); // 设置其他支付退款金额
					bRefund.setRefundAmount(refundAmount); // 设置货物退款金额
					bRefund.seteRefundAmount(BigDecimal.ZERO);	//设置少退金额
					bRefund.setRefundShipFee(BigDecimal.ZERO);	//设置退货物流费
					bRefund.setRefundTotalamount(refundAmount); // 设置应退款总额
					

					bRefund.setIsDelivered(false);	//设置是否退货
					bRefund.setResponsibleParty(1);	//设置责任方
					bRefund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY); // 状态为待退款
					businessOrderRefundService.save(bRefund); // 保存退款订单信息

					bOrder.setRefoundMoney(refundAmount); // 订单设置退款金额
					bOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND); // 设置订单为待退款状态
					businessOrderService.save(bOrder); // 保存订单信息

					
					
//					if(!businessOrder.getBalanceMoney().equals(BigDecimal.ZERO)){
//						WechatAccount account = businessOrder.getWechatAccount();
//						account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
//						wechatAccountService.update(account);
//					}
//					//生成交易记录
//					OrderTransaction transaction = new OrderTransaction();
//					transaction.setBusinessOrder(businessOrder);
//					transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
//					transaction.setWechatBalance(businessOrder.getBalanceMoney());
//					transaction.setPayAccount(businessOrder.getWechatAccount().getWechatOpenid());
//					transaction.setPayment(businessOrder.getPayMoney());
//					//微信支付
//					transaction.setPayType(1);
//					transaction.setPayment(businessOrder.getPayMoney());
//					//退款
//					transaction.setPayFlow(2);
//					transaction.setPayTime(businessOrder.getPayTime());
//					orderTransactionServiceImpl.save(transaction);
				}else {
					WechatAccount account = businessOrder.getWechatAccount();
					//恢复账户余额并保存
					account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
					wechatAccountService.update(account);
					if(businessOrder.getBalanceMoney() != null && businessOrder.getBalanceMoney().compareTo(BigDecimal.ZERO) > 0){
						WechatAccountBalance wechatAccountBalance = new WechatAccountBalance();
						wechatAccountBalance.setWechatAccountId(businessOrder.getWechatAccount().getId());
						wechatAccountBalance.setType(WechatAccountBalance.WechatAccountBalanceType.refund);
						wechatAccountBalance.setCreateTime(new Date());
						wechatAccountBalance.setAmount(businessOrder.getBalanceMoney());
						wechatAccountBalance.setSurplus(account.getTotalbalance());
						wechatAccountBalanceService.save(wechatAccountBalance);
					}
					//取消订单
					businessOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_CANCELED);
					businessOrder.setOrderCancelTime(new Date());
					businessOrderService.save(businessOrder);
				}

				json.setSuccess(true);
				json.setMsg("取消订单成功");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			} else {
				json.setSuccess(true);
				json.setMsg("订单状态错误，无法取消订单");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("发生异常");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			BusinessOrder order = businessOrderService.find(id);
			if(order==null){
				throw new Exception("没有有效订单");
			}
			order.setIsValid(false);
			businessOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("删除成功");
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
			// TODO: handle exception
		}
		return json;
	}
}
