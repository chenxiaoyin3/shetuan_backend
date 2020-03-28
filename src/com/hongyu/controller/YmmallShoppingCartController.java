package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import org.apache.shiro.crypto.hash.Hash;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyFullDiscount;
import com.hongyu.entity.HyFullPresent;
import com.hongyu.entity.HyFullSubstract;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotionPic;
import com.hongyu.entity.HyPromotion.PromotionRule;

import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.entity.HySingleitemPromotion;
import com.hongyu.entity.HyUser;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.entity.ShoppingCart;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyImage;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.HyPromotionService;

import com.hongyu.service.HySingleitemPromotionService;
import com.hongyu.service.HyUserService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.ShoppingCartService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.service.impl.HyUserServiceImpl;

@Controller
@RequestMapping("/ymmall/shopping_cart/")
@Transactional(propagation = Propagation.REQUIRED)
public class YmmallShoppingCartController {
	@Resource(name = "shoppingCartServiceImpl")
	ShoppingCartService shoppingCartService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;

	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;

	@Resource(name = "purchaseItemServiceImpl")
	PurchaseItemService purchaseItemServiceImpl;

	@Resource(name = "hySingleitemPromotionServiceImpl")
	HySingleitemPromotionService hySingleitemPromotionServiceImpl;

	@Resource(name = "hyPromotionServiceImpl")
	HyPromotionService hyPromotionServiceImpl;

	@Resource(name = "specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceServiceImpl;

	@Resource(name = "hyUserServiceImpl")
	HyUserService hyUserServiceImpl;
	
	@RequestMapping("add_items")
	@ResponseBody
	public Json add(ShoppingCart shoppingCart, /* HttpSession session */Long wechat_id, Long user_id) {
		Json json = new Json();
		try {
			// Long wechat_id = (Long) session.getAttribute("wechat_id");
			WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
			HyUser hyUser = hyUserServiceImpl.find(user_id);
			if (wechatAccount == null && hyUser == null) {
				json.setSuccess(false);
				json.setMsg("用户不存在");
			} else {
				List<Filter> filters=new ArrayList<>();
				if(wechatAccount != null){
					filters.add(Filter.eq("wechatAccount", wechatAccount));
				}
				else{//userId != null
					filters.add(Filter.eq("userId", user_id));
				}
			
				Boolean isGroupPromotion=shoppingCart.getIsGroupPromotion();
				
				
				filters.add(Filter.eq("specialtyId", shoppingCart.getSpecialtyId()));
				if(!isGroupPromotion){
					filters.add(Filter.eq("specialtySpecificationId", shoppingCart.getSpecialtySpecificationId()));
				}
				Pageable pageable=new Pageable();
				pageable.setFilters(filters);
				Page<ShoppingCart> page=shoppingCartService.findPage(pageable);
				if(page==null||page.getRows().size()<1){
					shoppingCart.setWechatAccount(wechatAccount);
					shoppingCart.setUserId(user_id);					
					shoppingCartService.save(shoppingCart);
				}else{
					for(ShoppingCart tmp:page.getRows()){
						if(tmp==null){
							continue;
						}
						int old=tmp.getQuantity();
						if(shoppingCart.getQuantity()!=null){
							tmp.setQuantity(old+shoppingCart.getQuantity());
						}else{
							tmp.setQuantity(old+1);
						}
						
						shoppingCartService.update(tmp);
					}
				}
				json.setSuccess(true);
				json.setMsg("添加成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("edit_items")
	@ResponseBody
	public Json edit(ShoppingCart shoppingCart) {
		Json json = new Json();
		try {
			shoppingCartService.update(shoppingCart, "wechatAccount", "specialtyId", "specialtySpecificationId",
					"addTime", "isGroupPromotion","userId");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("delete_items")
	@ResponseBody
	public Json delete(Long id) {
		Json json = new Json();
		try {
			shoppingCartService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("get_items")
	@ResponseBody
	public Json list(/* HttpSession session */Long wechat_id, Long user_id) {
		Json json = new Json();
		try {
			// Long id = (Long) session.getAttribute("wechat_id");
			WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
			HyUser hyUser = hyUserServiceImpl.find(user_id);
			if (wechatAccount == null && hyUser == null) {
				json.setSuccess(false);
				json.setMsg("用户不存在");
			} else {
				List<Filter> filters = new ArrayList<>();
				if(wechatAccount != null)
					filters.add(Filter.eq("wechatAccount", wechatAccount));
				else//userId != null
					filters.add(Filter.eq("userId", user_id));
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("addTime"));
				List<HashMap<String, Object>> result = new ArrayList<>();
				List<ShoppingCart> shoppingCarts = shoppingCartService.findList(null, filters, orders);
				for (ShoppingCart tmp : shoppingCarts) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", tmp.getId());
					map.put("specialtyId", tmp.getSpecialtyId());
					map.put("specialtySpecificationId", tmp.getSpecialtySpecificationId());
					map.put("quantity", tmp.getQuantity());
					map.put("isGroupPromotion", tmp.getIsGroupPromotion());
					Boolean isGroupPromotion = tmp.getIsGroupPromotion();
					// 如果是组合产品
					if (isGroupPromotion) {
						// 获取价格
						HyGroupitemPromotion groupitemPromotion = hyGroupitemPromotionServiceImpl
								.find((Long) tmp.getSpecialtyId());
						map.put("curPrice", groupitemPromotion.getSellPrice());
						// 获取优惠活动id
						map.put("promotionId", groupitemPromotion.getPromotionId().getId());
						map.put("name", groupitemPromotion.getPromotionId().getPromotionName());
						map.put("specification", null);

						Set<HyPromotionPic> hyPromotionPics = groupitemPromotion.getPromotionId().getHyPromotionPics();

						HyPromotionPic hyPromotionPic = null;

						for (HyPromotionPic h : hyPromotionPics) {
							if (h.getIsTag()) {
								hyPromotionPic = h;
								break;
							}
						}
						map.put("iconURL", hyPromotionPic);
					}
					// 如果是普通产品
					else {
						// 去价格变化表里面查
						SpecialtySpecification specialtySpecification = specialtySpecificationService
								.find((Long) tmp.getSpecialtySpecificationId());
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", specialtySpecification));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters,
								null);
						if (specialtyPrices != null && !specialtyPrices.isEmpty()) {
							//map.put("purchaseItemId", null);
							map.put("curPrice", specialtyPrices.get(0).getPlatformPrice());
						} else {
//							// 获取价格和采购明细id
//							// 获取有效采购明细
//							PurchaseItem purchaseItem = this.getValidPurchaseItem(tmp.getSpecialtySpecificationId());
//							map.put("purchaseItemId", purchaseItem.getId());
//							map.put("curPrice", purchaseItem.getSalePrice());
							json.setSuccess(false);
							json.setMsg("数据异常，没有有效价格信息");
							json.setObj(null);

						}
						// 获取优惠活动id
						// 获取有效优惠明细
						HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl
								.getValidSingleitemPromotion(tmp.getSpecialtySpecificationId());
						// 如果没有优惠，则加入“0L”promotionItems中
						if (singleitemPromotion != null) {
							Long promotionId = singleitemPromotion.getHyPromotion().getId();
							map.put("promotionId", promotionId);
						}
						Specialty specialty = specialtyService.find(tmp.getSpecialtyId());
						map.put("name", specialty.getName());
						map.put("specification", specialtySpecification.getSpecification());

						SpecialtyImage si = null;
						List<SpecialtyImage> ssImages = specialty.getImages();
						for (SpecialtyImage s : ssImages) {
							if (s != null && s.getIsLogo() != null && s.getIsLogo() == true) {
								si = s;
								break;
							}	
						}
						map.put("iconURL", si);
					}
					result.add(map);
				}
				
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(result);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
		}
		return json;
	}

	/* 获取某一规格有效采购批次 */
	private PurchaseItem getValidPurchaseItem(Long specialtySpecificationId) {
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		List<Filter> purchaseFilters = new ArrayList<Filter>();
		purchaseFilters.add(new Filter("specification", Filter.Operator.eq, specialtySpecification));
		purchaseFilters.add(new Filter("isValid", Filter.Operator.eq, true));
		purchaseFilters.add(new Filter("state", Filter.Operator.eq, true));
		List<Order> purchaseOrders = new ArrayList<Order>();
		purchaseOrders.add(Order.asc("id"));
		List<PurchaseItem> purchaseItems = purchaseItemServiceImpl.findList(null, purchaseFilters, purchaseOrders);
		// 获取当前有效批次
		if (purchaseItems == null || purchaseItems.isEmpty()) {
			return null;
		}
		PurchaseItem validPurchaseItem = purchaseItems.get(0);

		return validPurchaseItem;
	}

	/* 获取有效普通优惠明细 */
	private HySingleitemPromotion getValidSingleitemPromotion(Long specialtySpecificationId) {
		SpecialtySpecification specialtySpecification = specialtySpecificationService.find(specialtySpecificationId);
		List<Filter> singleFilters = new ArrayList<>();
		singleFilters.add(new Filter("specificationId", Filter.Operator.eq, specialtySpecification));
		List<Order> singleOrders = new ArrayList<>();
		singleOrders.add(Order.desc("id"));
		List<HySingleitemPromotion> singleitemPromotions = hySingleitemPromotionServiceImpl.findList(null,
				singleFilters, singleOrders);
		if (singleitemPromotions == null || singleitemPromotions.isEmpty()) {
			return null;
		}
		for (HySingleitemPromotion singleitemPromotion : singleitemPromotions) {
			if (singleitemPromotion.getHyPromotion().getStatus() == PromotionStatus.进行中) {
				return singleitemPromotion;
			}
		}
		return null;
	}

	/* 找出每个产品所参加的优惠活动，得到key=promotionId,value=list<customItem>的map对象 */

	private Map<Long, ArrayList<HashMap<String, Object>>> getPromotionItems(List<HashMap<String, Object>> customItems) {
		Map<Long, ArrayList<HashMap<String, Object>>> promotionItems = new HashMap<>();
		for (HashMap<String, Object> customItem : customItems) {
			Boolean isGroupPromotion = (Boolean) customItem.get("isGroupPromotion");
			// 如果是组合产品
			if (isGroupPromotion) {
				// 获取价格
				if (customItem.get("curPrice") == null) {
					HyGroupitemPromotion groupitemPromotion = hyGroupitemPromotionServiceImpl
							.find(((Integer) customItem.get("specialtyId")).longValue());
					customItem.put("curPrice", groupitemPromotion.getSellPrice());
				}
				// 获取优惠活动id
				if (customItem.get("promotionId") == null) {
					HyGroupitemPromotion groupitemPromotion = hyGroupitemPromotionServiceImpl
							.find(((Integer) customItem.get("specialtyId")).longValue());
					customItem.put("promotionId", groupitemPromotion.getPromotionId().getId().intValue());
				}

				// 将该产品加入“promotionId”promotionItems中
				Long promotionId = ((Integer) customItem.get("promotionId")).longValue();
				if (!promotionItems.containsKey(promotionId)) {
					promotionItems.put(promotionId, new ArrayList<HashMap<String, Object>>());
				}
				promotionItems.get(promotionId).add(customItem);
				// 如果是普通产品
			} else {
				// 获取价格和采购明细id
				if (customItem.get("curPrice") == null) {
					// 先去价格变化表里面查
					SpecialtySpecification specialtySpecification = specialtySpecificationService
							.find(((Integer) customItem.get("specialtySpecificationId")).longValue());
					List<Filter> priceFilters = new ArrayList<Filter>();

					priceFilters.add(Filter.eq("specification", specialtySpecification));
					priceFilters.add(Filter.eq("isActive", true));
					List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);
					if (specialtyPrices != null && !specialtyPrices.isEmpty()) {
						customItem.put("purchaseItemId", null);
						customItem.put("curPrice", specialtyPrices.get(0).getPlatformPrice());

					} else {
						// 获取有效采购明细
						PurchaseItem purchaseItem = this.getValidPurchaseItem(
								((Integer) customItem.get("specialtySpecificationId")).longValue());
						customItem.put("purchaseItemId", purchaseItem.getId());
						customItem.put("curPrice", purchaseItem.getSalePrice());
					}
				}
				// 获取优惠活动id
				if (customItem.get("promotionId") == null) {
					// 获取有效优惠明细
					HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
							((Integer) customItem.get("specialtySpecificationId")).longValue());
					// 如果没有优惠，则加入“0L”promotionItems中
					if (singleitemPromotion == null) {
						if (!promotionItems.containsKey(0L)) {

							promotionItems.put(0L, new ArrayList<HashMap<String, Object>>());
						}
						promotionItems.get(0L).add(customItem);
						// 如果有优惠，则加入“promotionId”promotionItems中
					} else {
						Long promotionId = singleitemPromotion.getHyPromotion().getId();
						customItem.put("promotionId", promotionId);
						if (!promotionItems.containsKey(promotionId)) {
							promotionItems.put(promotionId, new ArrayList<HashMap<String, Object>>());
						}
						promotionItems.get(promotionId).add(customItem);
					}

				} else {
					// 将该产品加入“promotionId”promotionItems中
					Long promotionId = ((Integer) customItem.get("promotionId")).longValue();

					customItem.put("promotionId", promotionId);
					if (!promotionItems.containsKey(promotionId)) {
						promotionItems.put(promotionId, new ArrayList<HashMap<String, Object>>());
					}
					promotionItems.get(promotionId).add(customItem);
				}
			}
		}
		return promotionItems;
	}

	@Resource(name = "vipServiceImpl")
	VipService vipService;
	
	@RequestMapping(value = { "/total_price" })
	@ResponseBody
	public Json totalPrice(HttpSession session,

			@RequestParam(required = false) List<HashMap<String, Object>> params,
			@RequestBody List<HashMap<String, Object>> bodys) {

		List<HashMap<String, Object>> customItems = null;
		if (params != null) {
			customItems = params;
		}
		if (bodys != null) {
			customItems = bodys;
		}
		Json json = new Json();

		try {
			Map<Long, ArrayList<HashMap<String, Object>>> promotionItems = this.getPromotionItems(customItems);
			List<Map<String, Object>> customPromotions = new ArrayList<>();
			for (Map.Entry<Long, ArrayList<HashMap<String, Object>>> promotionItem : promotionItems.entrySet()) {
				Map<String, Object> customPromotion = new HashMap<>();
				Long promotionId = promotionItem.getKey();
				ArrayList<HashMap<String, Object>> promotionCustomItems = promotionItem.getValue();
				// 得到活动id

				customPromotion.put("promotionId", promotionId);
				// 得到活动下属所有商品
				customPromotion.put("promotionItems", promotionCustomItems);

				BigDecimal totalPrice = BigDecimal.ZERO;
				// 获取活动商品总价
				for (HashMap<String, Object> promotionCustomItem : promotionCustomItems) {
					BigDecimal curPrice =  (BigDecimal.valueOf(
							promotionCustomItem.get("curPrice").getClass().equals(Integer.class)?
									((Integer)promotionCustomItem.get("curPrice")).longValue():
										((Double)promotionCustomItem.get("curPrice"))));
					
					Integer quantity = (Integer) promotionCustomItem.get("quantity");
					totalPrice = totalPrice.add(curPrice.multiply(BigDecimal.valueOf(quantity)));

				}
				customPromotion.put("totalMoney", totalPrice);

				if (promotionId.equals(0L)) {
					//每月18日判断用户会员等级，看普通产品是否打折
					Calendar cal=Calendar.getInstance();  
					Long id = (Long) session.getAttribute("wechat_id");
					Viplevel viplevel = vipService.getViplevelByWechatAccountId(id);
					if(cal.get(Calendar.DATE)==18 && viplevel!=null) {
						BigDecimal discount = viplevel.getDiscount();
						customPromotion.put("promotion", "会员18日折扣");
						customPromotion.put("promotionMoney", totalPrice.subtract(totalPrice.multiply(discount)));
						customPromotion.put("promotionCondition", discount);
						customPromotion.put("finalMoney", totalPrice.multiply(discount));
						
					}else {
						customPromotion.put("promotion", null);
						customPromotion.put("promotionMoney", BigDecimal.ZERO);
						customPromotion.put("promotionCondition", null);
						customPromotion.put("finalMoney", totalPrice);
					}
					
				} else {
					HyPromotion promotion = hyPromotionServiceImpl.find(promotionId);
					customPromotion.put("promotion", promotion);
					PromotionRule promotionRule = promotion.getPromotionRule();
					switch (promotionRule) {
					case 满减: {
						Set<HyFullSubstract> hyFullSubstracts = promotion.getHyFullSubstracts();
						HyFullSubstract hyFullSubstract = null;
						for (HyFullSubstract h : hyFullSubstracts) {
							if (totalPrice.compareTo(h.getFullFreeRequirement()) < 0) {
								break;
							}
							hyFullSubstract = h;
						}
						if (hyFullSubstract == null) {
							customPromotion.put("promotionCondition", hyFullSubstract);
							customPromotion.put("promotionMoney", BigDecimal.ZERO);
							customPromotion.put("finalMoney", totalPrice);
						} else {
							customPromotion.put("promotionCondition", hyFullSubstract);
							//改满减规则。
							//改成当总价大于最大满减条件时，[（总价-最大满减条件）/最大满减条件 ]取整 = N，多N倍则在减免数值amount的基础上再加N*amount
							//甲方实际使用时，绝大多数情况只会建一个满减条件，这样改之后就相当于与“满折”优惠了，但是甲方不想算折扣，因为打折可能要乘0.987654...，会很麻烦，用数值比较直接。
							//因此在原有基础上加上满足甲方需求的逻辑。
							//没有考虑在“满足的最大减免条件”的基础上加倍，例如满10减5，满20减10，满100减50，目前的逻辑在花40,50,60时都是减10。
							//如果考虑的话，则花40减20，花50减20，花60减30
							BigDecimal promotionMoney = hyFullSubstract.getFullFreeAmount();
	                        HyFullSubstract maxFullFreeRequirementHyFullSubstracts = hyFullSubstract;
	                        for(HyFullSubstract h : hyFullSubstracts){//hyFullSubstracts是升序，在实体类中有注解
	                            maxFullFreeRequirementHyFullSubstracts = h;
	                        }
	                        if(totalPrice.compareTo(maxFullFreeRequirementHyFullSubstracts.getFullFreeRequirement())>0){
	                            promotionMoney = promotionMoney.add((totalPrice.subtract(maxFullFreeRequirementHyFullSubstracts.getFullFreeRequirement())).divide(maxFullFreeRequirementHyFullSubstracts.getFullFreeRequirement(), 0, BigDecimal.ROUND_DOWN).multiply(maxFullFreeRequirementHyFullSubstracts.getFullFreeAmount()));
	                        }
	                        customPromotion.put("promotionMoney", promotionMoney);
	                        customPromotion.put("finalMoney", totalPrice.subtract(promotionMoney));

						}
					}
						break;
					case 满折: {
						Set<HyFullDiscount> hyFullDiscounts = promotion.getHyFullDiscounts();
						HyFullDiscount hyFullDiscount = null;
						for (HyFullDiscount h : hyFullDiscounts) {
							if (totalPrice.compareTo(h.getDiscountRequirenment()) < 0) {
								break;
							}
							hyFullDiscount = h;
						}
						if (hyFullDiscount == null) {
							customPromotion.put("promotionCondition", hyFullDiscount);
							customPromotion.put("promotionMoney", BigDecimal.ZERO);
							customPromotion.put("finalMoney", totalPrice);
						} else {
							customPromotion.put("promotionCondition", hyFullDiscount);
							customPromotion.put("finalMoney", totalPrice.multiply(hyFullDiscount.getDiscountOff()));
							customPromotion.put("promotionMoney",
									totalPrice.subtract((BigDecimal) customPromotion.get("finalMoney")));
						}
					}
						break;
					case 满赠: {
						Set<HyFullPresent> hyFullPresents = promotion.getHyFullPresents();
						List<HyFullPresent> hyFullPresentList = new ArrayList<>();
						for (HyFullPresent h : hyFullPresents) {
							if (totalPrice.compareTo(h.getFullPresentRequirenment()) < 0) {
								break;
							}

							if (!hyFullPresentList.isEmpty() && hyFullPresentList.get(0).getFullPresentRequirenment()
									.compareTo(h.getFullPresentRequirenment()) < 0) {
								hyFullPresentList.clear();
							}
							hyFullPresentList.add(h);
						}
						customPromotion.put("promotionCondition", hyFullPresentList);
						customPromotion.put("promotionMoney", BigDecimal.ZERO);
						customPromotion.put("finalMoney", totalPrice);
					}
						break;
					default:
						break;
					}
				}
				customPromotions.add(customPromotion);
			}

			Map<String, Object> obj = new HashMap<>();
			BigDecimal totalMoney = BigDecimal.ZERO;
			BigDecimal promotionMoney = BigDecimal.ZERO;
			BigDecimal finalMoney = BigDecimal.ZERO;
			for (Map<String, Object> customPromotion : customPromotions) {
				totalMoney = totalMoney.add((BigDecimal) customPromotion.get("totalMoney"));
				promotionMoney = promotionMoney.add((BigDecimal) customPromotion.get("promotionMoney"));
				finalMoney = finalMoney.add((BigDecimal) customPromotion.get("finalMoney"));

			}
			obj.put("totalMoney", totalMoney);
			obj.put("promotionMoney", promotionMoney);
			obj.put("finalMoney", finalMoney);
			obj.put("promotions", customPromotions);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(obj);
		} catch (Exception e) {

			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}

		return json;
	}
}
