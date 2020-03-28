
package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyLine.LineType;

import com.hongyu.util.Constants;
import com.hongyu.util.Constants.DeductLine;
import com.hongyu.util.Constants.DeductPiaowu;
import com.hongyu.util.DateUtil;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

@Service("hyOrderServiceImpl")
public class HyOrderServiceImpl extends BaseServiceImpl<HyOrder, Long> implements HyOrderService {
	@Resource(name = "couponLineServiceImpl")
	CouponLineService couponLineService;

	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;

	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailsService;

	@Resource(name = "receiptOtherServiceImpl")
	ReceiptOtherService receiptOtherService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "guideServiceImpl")
	GuideService guideService;

	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;

	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;

	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "hySubscribeTicketServiceImpl")
	HySubscribeTicketService hySubscribeTicketService;

	@Resource(name = "hySubscribeTicketPriceItemServiceImpl")
	HySubscribeTicketPriceItemService hySubscribeTicketPriceItemService;

	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;

	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;

	@Resource(name = "GroupPlaceholderServiceImpl")
	GroupPlaceholderService groupPlaceholderService;

	@Resource(name = "linePromotionServiceImpl")
	LinePromotionService linePromotionService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name="payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name="hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name="hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;

	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;

	@Resource(name = "hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name = "hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	HyPromotionActivityService hyPromotionActivityService;

	@Resource(name = "branchBalanceServiceImpl")
    private BranchBalanceService branchBalanceService;

	@Resource(name = "branchPreSaveServiceImpl")
    private BranchPreSaveService branchPreSaveService;
	
	@Override
	@Resource(name = "hyOrderDaoImpl")
	public void setBaseDao(BaseDao<HyOrder, Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Override
	public Json addGuideOrder(HyOrder hyOrder, HttpSession session) throws Exception {
		Json json = new Json();
		if (hyOrder == null) {
			json.setSuccess(false);
			json.setMsg("订单为空，请检查");
		} else {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store != null) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0L);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				String code = nowaday + String.format("%05d", value);
				hyOrder.setOrderNumber(code);
				hyOrder.setName("导游租赁");
				hyOrder.setStatus(0);
				hyOrder.setPaystatus(0);
				hyOrder.setCheckstatus(0);
				hyOrder.setRefundstatus(0);
				hyOrder.setType(0);// 导游租赁
				hyOrder.setSource(0);
				hyOrder.setPeople(0);

				Integer xianlu = hyOrder.getXianlutype();
				LineType lineType;
				Boolean groupType;
				if (xianlu == 0 || xianlu == 1) {
					lineType = LineType.qiche;
				} else if (xianlu == 2 || xianlu == 3) {
					lineType = LineType.guonei;
				} else {
					lineType = LineType.chujing;
				}
				if ((xianlu & 1) == 1) {
					groupType = true;
				} else {
					groupType = false;
				}
				Integer serviceType = hyOrder.getFuwutype();

				hyOrder.setStoreId(store.getId());
				hyOrder.setOperator(hyAdmin);
				hyOrder.setCreatorId(username);

				GuideAssignment guideAssignment = new GuideAssignment();
				BigDecimal totalMoney = new BigDecimal(0);
				if (hyOrder.getOrderItems() != null && hyOrder.getOrderItems().size() > 0) {
					for (HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
						Long guideId = hyOrderItem.getProductId();
						Guide guide = guideService.find(guideId);
						synchronized (guide) {
							Integer days = hyOrderItem.getNumber();
							Date startDate = DateUtil.getStartOfDay(hyOrder.getFatuandate());
							Date endDate = DateUtil.getDateAfterSpecifiedDays(startDate, days);
							if (!guideService.isAvailable(guideId, startDate, endDate)) {
								json.setSuccess(false);
								json.setMsg("下单失败，当前时间不可选");
								return json;
							}
							hyOrderItem.setOrder(hyOrder);
							Integer star = guide.getZongheLevel();
							hyOrderItem.setStatus(0);
							hyOrderItem.setName(guide.getName() + "租赁");
							hyOrder.setName(guide.getName() + "导游租赁");	//重新修改订单名称
							hyOrderItem.setType(0);
							hyOrderItem.setStartDate(hyOrder.getFatuandate());
							hyOrderItem.setNumber(hyOrder.getTianshu());
							hyOrderItem.setNumberOfReturn(0);
							Json json2 = guideService.caculate(lineType, serviceType, groupType, star, days);
							BigDecimal serviceFee;
							if (!json2.isSuccess()) {
								json = json2;
								return json;
							} else {
								serviceFee = (BigDecimal) json2.getObj();
							}
							hyOrderItem.setJiesuanPrice(serviceFee);
							totalMoney = totalMoney.add(serviceFee);

							guideAssignment.setGuideId(hyOrderItem.getProductId());
							guideAssignment.setAssignmentType(1);
							guideAssignment.setStartDate(hyOrderItem.getStartDate());
							guideAssignment.setEndDate(DateUtil.getDateAfterSpecifiedDays(hyOrderItem.getStartDate(),
									hyOrderItem.getNumber()));
							guideAssignment.setDays(hyOrderItem.getNumber());
							guideAssignment.setServiceFee(serviceFee);
							hyOrder.setAdjustMoney(new BigDecimal(0));
							hyOrder.setDiscountedPrice(new BigDecimal(0));

							hyOrder.setJiusuanMoney(totalMoney);
							hyOrder.setJiesuanMoney1(totalMoney);
							hyOrder.setWaimaiMoney(totalMoney);
							hyOrder.setJiesuanTuikuan(new BigDecimal(0));
							this.save(hyOrder);
							

							//导游派遣信息
							guideAssignment.setOrderId(hyOrder.getId());
							guideAssignment.setServiceType(hyOrder.getFuwutype());
							guideAssignment.setLineName(hyOrder.getXianlumingcheng());
							guideAssignment.setTravelProfile(hyOrder.getXingchenggaiyao());
							guideAssignment.setTip(hyOrder.getTip());
							guideAssignment.setTotalFee(guideAssignment.getTip().add(guideAssignment.getServiceFee()));
							guideAssignment.setOperator(username);
							guideAssignment.setOperatorPhone(hyAdmin.getMobile());
							guideAssignment.setPaiqianDate(new Date());
							guideAssignment.setStatus(0);
							guideAssignmentService.save(guideAssignment);
							
							/**
							 * 以下给导游推送消息的代码，支付成功后再调用，这里下单时不需要
							 */
//							//发推送消息
//							 TemplateMsgResult templateMsgResult = null;
//								TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
//								// 根据具体模板参数组装
//								params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
//								String lineName=hyOrder.getXianlumingcheng();
//								params.put("keyword1", WechatTemplateMsg.item(lineName, "#000000"));
//								
//								String teamType="";
//								if(groupType==true){
//									teamType="团客";
//								}else{
//									teamType="散客";
//								}
//								params.put("keyword2", WechatTemplateMsg.item(teamType, "#000000"));
//								
//								SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd");
//								String serviceTime="";
//								serviceTime+= simpleDateFormat.format(hyOrder.getFatuandate())+"-";
//								serviceTime+=simpleDateFormat.format(DateUtil.getDateAfterSpecifiedDays(hyOrder.getFatuandate(),hyOrder.getTianshu()));
//								params.put("keyword3", WechatTemplateMsg.item(serviceTime, "#000000"));
//								
//								String serviceTypeStr="";
//								if(serviceType==0){
//									serviceTypeStr="全陪服务";
//								}else{
//									serviceTypeStr="其他服务";
//								}
//								params.put("keyword4", WechatTemplateMsg.item(serviceTypeStr, "#000000"));
//								
//								String serviceFeeStr="";
//								serviceFeeStr=serviceFee.add(hyOrder.getTip())+" 元";
//								params.put("keyword5", WechatTemplateMsg.item(serviceFeeStr, "#000000"));
//								params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
//								WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
//								wechatTemplateMsg.setTemplate_id(Constants.Store_RentGuide_Template);
//								wechatTemplateMsg.setTouser(guide.getOpenId());
//								String url=Constants.guideAssignmentSite+"id="+guideAssignment.getId();
//								wechatTemplateMsg.setUrl(url);
//								wechatTemplateMsg.setData(params);
//								String data = JsonUtils.toJson(wechatTemplateMsg);
//								//templateMsgResult = WechatUtil.storeRent(data);
//								System.out.println(JsonUtils.toJson(templateMsgResult));
						}
					}
				}
				json.setSuccess(true);
				json.setMsg("添加成功");
			} else {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		}
		return json;
	}

	@Override
	public Json addLineOrder(Long placeHolder, HyOrder hyOrder, HttpSession session) throws Exception {
		Json json = new Json();
		if (hyOrder == null) {
			json.setSuccess(false);
			json.setMsg("订单为空，请检查");
		} else {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			} else {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0L);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				String code = nowaday + String.format("%05d", value);
				hyOrder.setOrderNumber(code);
				hyOrder.setStatus(0);// 0门店待支付
				hyOrder.setPaystatus(0);// 0门店待支付
				hyOrder.setCheckstatus(0);// 门店已确认
				hyOrder.setRefundstatus(0);// 门店未退款
				hyOrder.setType(1);// 线路订单
				hyOrder.setSource(0);// 门店
				Integer people = 0;

				hyOrder.setStoreType(0);// 虹宇门店
				hyOrder.setStoreId(store.getId());
				hyOrder.setOperator(hyAdmin);
				hyOrder.setCreatorId(username);
				/***** 写获取电子券，并且送操作人一张 **********/
				// 通过团获取获取线路电子券id
				Long groupId = hyOrder.getGroupId();
				HyGroup hyGroup = hyGroupService.find(groupId);
				
				HyLine hyLine = hyGroup.getLine();
				hyOrder.setFatuandate(hyGroup.getStartDay());	//发团日期
				hyOrder.setTianshu(DateUtil.getDaysBetweenTwoDates(hyGroup.getStartDay(), hyGroup.getEndDay()).intValue()+1);	//天数
				hyOrder.setXianlumingcheng(hyLine.getName());
				Long componMoneyId = hyGroup.getCouponId();
				CouponMoney couponMoney = couponMoneyService.find(componMoneyId);

				boolean isPromotion = hyGroup.getIsPromotion();
				LinePromotion linePromotion = null;
				if (isPromotion) {
					linePromotion = linePromotionService.findByGroupId(groupId);
				}
				if (linePromotion == null) {
					hyOrder.setDiscountedType(3);// 无促销
				} else {
					hyOrder.setDiscountedType(linePromotion.getPromotionType());
					hyOrder.setDiscountedId(linePromotion.getId());
				}

				BigDecimal adjustMoney = new BigDecimal(0);// 调整金额
				BigDecimal discountedPrice = new BigDecimal(0);// 优惠金额

				BigDecimal jiesuanMoney1 = new BigDecimal(0);// 除保险之外的订单条目的总结算价
				BigDecimal jiusuanMoney = new BigDecimal(0);// 订单结算价
				BigDecimal waimaiMoney = new BigDecimal(0);// 订单外卖价
				// BigDecimal jiesuanTuikuan = new BigDecimal(0);// 结算退款价

				BigDecimal koudianMoney = new BigDecimal(0);// 扣点金额
				
				BigDecimal storeFanli = new BigDecimal(0);
				BigDecimal fanliMoney = hyGroup.getFanliMoney();
				
				Integer placeHolderNumber = 0;
				if (placeHolder != null) {
					GroupPlaceholder groupPlaceholder= groupPlaceholderService.find(placeHolder);
					placeHolderNumber = groupPlaceholder.getNumber();
				}
				hyOrder.setIfjiesuan(false);// 未结算
				List<HyOrderItem> orderItems = new ArrayList<>();
				if(hyOrder!=null && hyOrder.getOrderItems()!=null && hyOrder.getOrderItems().size() > 0){
					for(HyOrderItem hyOrderItem : hyOrder.getOrderItems()){
						if (hyOrderItem.getNumber()!=null && hyOrderItem.getNumber() > 0) {
							orderItems.add(hyOrderItem);
						}
					}
					hyOrder.setOrderItems(orderItems);
				}
				synchronized (hyGroup) {
					if (hyOrder.getOrderItems() != null && hyOrder.getOrderItems().size() > 0) {

						//库存判断
						Integer people1 = 0;
						for (HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
							Integer curPeople=hyOrderItem.getHyOrderCustomers()==null?0:hyOrderItem.getHyOrderCustomers().size();
							people1 += curPeople;
						}

						if(hyGroup.getStock() + placeHolderNumber < people1){
							throw new Exception("库存不足");
						}



						for (HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
							hyOrderItem.setStatus(0);// 有效
							BigDecimal settlePrice = hyOrderItem.getJiesuanPrice();
							BigDecimal salePrice = hyOrderItem.getWaimaiPrice();
							BigDecimal number = new BigDecimal(hyOrderItem.getNumber());
							jiesuanMoney1 = jiesuanMoney1.add(settlePrice.multiply(number));
							jiusuanMoney = jiusuanMoney.add(settlePrice.multiply(number));
							waimaiMoney = waimaiMoney.add(salePrice.multiply(number));
							int priceType = hyOrderItem.getPriceType();
							if (hyOrderItem.getType().equals(8)) {
								String name = "";
								if (priceType == 0) {
									name = "单房差";
								} else if (priceType == 1) {
									name = "补卧铺";
								} else if (priceType == 2) {
									name = "补门票";
								} else if (priceType == 3) {
									name = "儿童占床";
								} else if (priceType == 4) {
									name = "补床位";
								}
								hyOrderItem.setName("其他价格-" + name);
							} else if (hyOrderItem.getType().equals(1)) {
								hyOrderItem.setStartDate(hyGroup.getStartDay());
								hyOrderItem.setEndDate(hyGroup.getEndDay());
								//库存由产品变为人数
								Integer curPeople=hyOrderItem.getHyOrderCustomers()==null?0:hyOrderItem.getHyOrderCustomers().size();
								if(hyGroup.getStock() + placeHolderNumber >= curPeople) {
									hyGroup.setStock(hyGroup.getStock() - curPeople);
									hyGroup.setSignupNumber(hyGroup.getSignupNumber() + curPeople);
									/*
									 * write by lbc
									 * 将hyRegulate表中的visitornum的值set为group中的signupnum
									 */
									List<Filter> hyRegulateFilter = new ArrayList<>();
									hyRegulateFilter.add(Filter.eq("hyGroup", hyGroup.getId()));
									List<HyRegulate> hyRegulates = hyRegulateService.findList(null, hyRegulateFilter, null);
									if(hyRegulates.size() != 0) {
										HyRegulate hyRegulate = hyRegulates.get(0);
										hyRegulate.setVisitorNum(hyGroup.getSignupNumber());
										hyRegulateService.update(hyRegulate);
									}
									
									hyGroupService.update(hyGroup);
									synchronized (hyLine) {
//										hyLine.setSaleCount(hyLine.getSaleCount() + hyOrderItem.getNumber());
										hyLine.setSaleCount(hyLine.getSaleCount() + curPeople);
										hyLine.setIsEdit(false);
										hyLineService.update(hyLine);
									}
								} else {
									throw new Exception("库存不足");
								}

								if (hyOrderItem.getHyOrderCustomers() != null
										&& hyOrderItem.getHyOrderCustomers().size() > 0) {
									for (HyOrderCustomer hyOrderCustomer : hyOrderItem.getHyOrderCustomers()) {
										hyOrderCustomer.setOrderItem(hyOrderItem);

										/*计算storeFanLi*/
										storeFanli = storeFanli.add(fanliMoney);
										/*写电子券*/
										if (hyOrderCustomer.getIsCoupon()) {
											String receiver = hyOrderCustomer.getName();
											String receiverPhone = hyOrderCustomer.getPhone();

											CouponLine couponLine = new CouponLine();
											couponLine.setIssueTime(new Date()); // 发放日期
											couponLine.setThawingTime(hyGroup.getEndDay()); // 回团日期
											couponLine.setSum(couponMoney.getMoney() + 0F);
											couponLine.setState(2); // //0:未绑定
																	// 1:已绑定
																	// 2:冻结
																	// 3:已过期
											couponLine.setReceiver(receiver);
											couponLine.setReceiverPhone(receiverPhone);
											couponLine.setBindPhone(receiverPhone);
											couponLine.setBindPhoneTime(new Date());
											couponLine.setExpireTime(couponMoney.getEndTime());
											couponLine.setLineId(hyGroup.getLine().getId()); // 线路产品id
											couponLine.setLineName(hyGroup.getGroupLineName()); // 线路产品名称
											couponLine.setStartDate(hyGroup.getStartDay()); // 发团日期
											couponLine.setGroupId(groupId);

											couponLineService.save(couponLine);

											CouponLine couponLine2 = new CouponLine();
											couponLine2.setIssueTime(new Date()); // 发放日期
											couponLine2.setThawingTime(hyGroup.getEndDay()); // 回团日期
											couponLine2.setSum(couponMoney.getMoney() + 0F);
											couponLine2.setState(2); // //0:未绑定
																		// 1:已绑定
																		// 2:冻结
																		// 3:已过期
											couponLine2.setReceiver(hyAdmin.getName());
											couponLine2.setReceiverPhone(hyAdmin.getMobile());
											couponLine2.setBindPhone(hyAdmin.getMobile());
											couponLine2.setBindPhoneTime(new Date());
											couponLine2.setExpireTime(couponMoney.getEndTime());
											couponLine2.setLineId(hyGroup.getLine().getId()); // 线路产品id
											couponLine2.setLineName(hyGroup.getGroupLineName()); // 线路产品名称
											couponLine2.setStartDate(hyGroup.getStartDay()); // 发团日期

											couponLineService.save(couponLine2);

										}

										if (hyOrderCustomer.getIsInsurance() == true) {
//											jiusuanMoney = jiusuanMoney.add(hyOrderCustomer.getSettlementPrice());
											jiusuanMoney = jiusuanMoney.add(hyOrderCustomer.getSalePrice());
											waimaiMoney = waimaiMoney.add(hyOrderCustomer.getSalePrice());
										}
										people++;
									}
								}
								String name = hyGroup.getLine().getName();
								hyOrder.setName(name);
								String priceName = "";
								if (priceType == 0) {
									priceName = "普通成人价";
								} else if (priceType == 1) {
									priceName = "普通儿童价";
								} else if (priceType == 2) {
									priceName = "普通学生价";
								} else if (priceType == 3) {
									priceName = "普通老人价";
								} else if (priceType == 4) {
									priceName = "特殊价格";
								}
								hyOrderItem.setName(name + "-" + priceName);
								hyOrder.setKoudianMethod(hyGroup.getKoudianType().ordinal());
								// 扣点方式————人头扣点
								if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.rentou.ordinal())) {
									BigDecimal headProportion = hyGroup.getPersonKoudian();
									hyOrder.setHeadProportion(headProportion);
									BigDecimal every = headProportion;
									BigDecimal head = new BigDecimal(hyOrderItem.getHyOrderCustomers().size());
									koudianMoney = koudianMoney.add(every.multiply(head));
								}
							}
							hyOrderItem.setOrder(hyOrder);
							hyOrderItem.setNumberOfReturn(0);
						}
					}
				}
				if(placeHolder!=null){
					synchronized (hyGroup) {
						hyGroup.setStock(hyGroup.getStock()+placeHolderNumber);
						hyGroup.setOccupyNumber(hyGroup.getOccupyNumber()-placeHolderNumber);
						hyGroupService.update(hyGroup);
					}
					GroupPlaceholder groupPlaceholder=groupPlaceholderService.find(placeHolder);
					groupPlaceholder.setStatus(true);
					groupPlaceholderService.save(groupPlaceholder);
				}
				hyOrder.setPeople(people);
				hyOrder.setAdjustMoney(adjustMoney);

				hyOrder.setJiesuanMoney1(jiesuanMoney1);
				hyOrder.setJiusuanMoney(jiusuanMoney);
				hyOrder.setWaimaiMoney(waimaiMoney);

				hyOrder.setStoreFanLi(storeFanli);
				
				if (hyOrder.getDiscountedType().equals(0)) {// 满减
					BigDecimal btimes = jiesuanMoney1.divide(linePromotion.getManjianPrice1());
					int times = btimes.intValue();
					discountedPrice = linePromotion.getManjianPrice2().multiply(new BigDecimal(times));
				} else if (hyOrder.getDiscountedType().equals(1)) {// 打折
					BigDecimal dazhe = (new BigDecimal(1)).subtract(linePromotion.getDazhe());
					discountedPrice = jiesuanMoney1.multiply(dazhe);
				} else if (hyOrder.getDiscountedType().equals(2)) {// 每人减
					discountedPrice = linePromotion.getMeirenjian().multiply(new BigDecimal(people));
				}
				hyOrder.setDiscountedPrice(discountedPrice);
				// hyOrder.setJiesuanTuikuan(jiesuanTuikuan);
				hyOrder.setKoudianMoney(koudianMoney);
				//按团客散客扣点
				DeductLine deductLine=hyGroup.getKoudianType();
				if(deductLine.equals(Constants.DeductLine.tuanke)){
					BigDecimal percentagekoudian = hyGroup.getPercentageKoudian();
					hyOrder.setProportion(percentagekoudian);
					BigDecimal orderMoney = hyOrder.getJiesuanMoney1().add(hyOrder.getTip()).subtract(hyOrder.getDiscountedPrice()).subtract(hyOrder.getStoreFanLi());
					hyOrder.setKoudianMoney(percentagekoudian.divide(new BigDecimal(100)).multiply(orderMoney));
				}
				HyAdmin supplier=hyGroup.getLine().getOperator();
				hyOrder.setSupplier(supplier);
				/*add by liyang. add lineType column*/
				hyOrder.setXianlutype(hyGroup.getLine().getLineType().ordinal());
				//判断合同类型
				if(hyOrder.getType()==1){
					//当前订单类型为线路订单的时候才需要签署合同
					HyGroup group = hyGroupService.find(hyOrder.getGroupId());
					HyLine line = group.getLine();
					if(line.getDays()==1){
						//一日游
						hyOrder.setContractType(0);
					}else{
						if(hyOrder.getXianlutype()<2){
							//境内游
							hyOrder.setContractType(1);
						}else{
							//出境游
							hyOrder.setContractType(2);
						}
					}			
				}
				
				
				//添加出发地
//				if(hyOrder.getOrderItems()!=null && !hyOrder.getOrderItems().isEmpty()) {
//					for(HyOrderItem item:hyOrder.getOrderItems()) {
//						if(item.getType().equals(1) && !item.getPriceType().equals(4)) {
//							Long priceId = item.getPriceId();
//							HyGroupPrice hyGroupPrice = hyGroupPriceService.find(priceId);
//							if(hyGroupPrice!=null && hyGroupPrice.getStartplace()!=null){
//								hyOrder.setDeparture(hyGroupPrice.getStartplace());
//								break;
//							}
//						}
//					}
//				}
				this.save(hyOrder);

				//订单日志,插一条记录到hy_order_application
				HyOrderApplication hyOrderApplication=new HyOrderApplication();
				hyOrderApplication.setOperator(hyAdmin);
				hyOrderApplication.setCreatetime(new Date());
				hyOrderApplication.setStatus(1); //通过
				hyOrderApplication.setContent("门店下订单");
				hyOrderApplication.setOrderId(hyOrder.getId());
				hyOrderApplication.setOrderNumber(hyOrder.getOrderNumber());
				hyOrderApplication.setType(8); //8-门店下订单
				hyOrderApplicationService.save(hyOrderApplication);

				/** 不在此时生成保险订单，而是改为在投保的时候生成保险订单*/
				//insuranceOrderService.generate(hyOrder, session);
				json.setSuccess(true);
				json.setMsg("下单成功");
				json.setObj(hyOrder);
			}
		}
		return json;
	}
	
	@Override
	public Json addInsuranceOrder(HyOrder hyOrder, HttpSession session) throws Exception {
		Json json = new Json();
		if (hyOrder == null) {
			json.setSuccess(false);
			json.setMsg("订单为空，请检查");
		} else {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			} else {
				//建立数据表关系
//				for(HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
//					for(HyOrderCustomer hyOrderCustomer : hyOrderItem.getHyOrderCustomers()) {
//						hyOrderCustomer.setOrderItem(hyOrderItem);
//					}
//					hyOrderItem.setOrder(hyOrder);
//				}

				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0L);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				String code = nowaday + String.format("%05d", value);
				//得到订单号
				hyOrder.setOrderNumber(code);
				hyOrder.setStatus(0);// 0门店待支付
				hyOrder.setPaystatus(0);// 0门店待支付
				//hyOrder.setCheckstatus(0);// 门店已确认
				hyOrder.setRefundstatus(0);// 门店未退款
				hyOrder.setType(6);// 保险订单
				hyOrder.setSource(0);// 0门店1官网2微商
				hyOrder.setName(hyOrder.getInsurance().getRemark());
				Integer people = 0;

				//直营门店
				if(store.getStoreType() == 2) {
					hyOrder.setStoreType(1);// 直营门店
				}
				else {
					hyOrder.setStoreType(0);// 虹宇门店
				}
				
				hyOrder.setStoreId(store.getId());
				hyOrder.setOperator(hyAdmin);
				hyOrder.setCreatorId(username);
				hyOrder.setCreatetime(new Date());
				
				

				BigDecimal adjustMoney = new BigDecimal(0);// 调整金额

				BigDecimal jiusuanMoney = new BigDecimal(0);// 订单结算价
				BigDecimal waimaiMoney = new BigDecimal(0);// 订单外卖价
				// BigDecimal jiesuanTuikuan = new BigDecimal(0);// 结算退款价

				
				hyOrder.setIfjiesuan(false);// 未结算
//				synchronized (hyGroup) {
				//因为都是修改订单部分 所以不需要同步锁
				if (hyOrder.getOrderItems() != null && hyOrder.getOrderItems().size() > 0) {
					for (HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
						hyOrderItem.setStatus(0);// 有效
						BigDecimal settlePrice = hyOrderItem.getJiesuanPrice();
						BigDecimal salePrice = hyOrderItem.getWaimaiPrice();
						BigDecimal number = new BigDecimal(hyOrderItem.getNumber());
						jiusuanMoney = jiusuanMoney.add(settlePrice.multiply(number));
						waimaiMoney = waimaiMoney.add(salePrice.multiply(number));
						// HyGroup hyGroup =
						// hyGroupService.find(hyOrderItem.getProductId());
						//保险生效日期
						Calendar cal1 = Calendar.getInstance();
						Calendar cal2 = Calendar.getInstance();
						//保险开始时间
						cal1.setTime(hyOrder.getFatuandate());
						//当前时间
						cal2.setTime(new Date());
						cal1.set(Calendar.HOUR_OF_DAY, 0);
						cal1.set(Calendar.MINUTE, 0);
						cal1.set(Calendar.SECOND, 0);
						cal1.set(Calendar.MILLISECOND, 0);
						
						cal2.set(Calendar.HOUR_OF_DAY, 0);
						cal2.set(Calendar.MINUTE, 0);
						cal2.set(Calendar.SECOND, 0);
						cal2.set(Calendar.MILLISECOND, 0);
						//hyOrderItem.setStartDate(hyOrder.getFatuandate());
//						Calendar calendar = Calendar.getInstance();
//						calendar.setTime(hyOrder.getFatuandate());
//						
//						
//						
//						
//						//
//						calendar.add(Calendar.DAY_OF_YEAR, hyOrder.getTianshu());
//						hyOrderItem.setEndDate(calendar.getTime());
						if(cal1.getTime().equals(cal2.getTime())) {
							//下保险的起始日期是当天
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(new Date());
							//加上两小时
							calendar.add(Calendar.HOUR_OF_DAY, 2);
							//set保险生效时间
							hyOrderItem.setStartDate(calendar.getTime());
							//set保险结束时间 23:59:59
							calendar.add(Calendar.DAY_OF_YEAR, hyOrder.getTianshu() - 1);
							calendar.set(Calendar.HOUR_OF_DAY, 23);
							calendar.set(Calendar.MINUTE, 59);
							calendar.set(Calendar.SECOND, 59);
							hyOrderItem.setEndDate(calendar.getTime());
						}
						else {
							//下保险的起始日期不是当天,那开始时间就设置为开始那天的1点0分
							cal1.set(Calendar.HOUR_OF_DAY, 1);
							hyOrderItem.setStartDate(cal1.getTime());
							cal1.add(Calendar.DAY_OF_YEAR, hyOrder.getTianshu() - 1);
							cal1.set(Calendar.HOUR_OF_DAY, 23);
							cal1.set(Calendar.MINUTE, 59);
							cal1.set(Calendar.SECOND, 59);
							hyOrderItem.setEndDate(cal1.getTime());
						}
						
						
						
						
						hyOrderItem.setNumberOfReturn(0);
						hyOrderItem.setProductId(hyOrder.getInsurance().getId());
						hyOrderItem.setType(6);
						hyOrderItem.setName(hyOrder.getInsurance().getRemark());

						if (hyOrderItem.getHyOrderCustomers() != null
								&& hyOrderItem.getHyOrderCustomers().size() > 0) {
							for (HyOrderCustomer hyOrderCustomer : hyOrderItem.getHyOrderCustomers()) {
								hyOrderCustomer.setOrderItem(hyOrderItem);
								hyOrderCustomer.setInsuranceId(hyOrder.getInsurance().getId());
							}
						}


						hyOrderItem.setOrder(hyOrder);
						hyOrderItem.setNumberOfReturn(0);
						//人数加1
						people ++;
					}
				}
//				}
				
				hyOrder.setPeople(people);
				hyOrder.setAdjustMoney(adjustMoney);

				hyOrder.setJiusuanMoney(jiusuanMoney);
				hyOrder.setWaimaiMoney(waimaiMoney);
				hyOrder.setBaoxianJiesuanTuikuan(new BigDecimal(0));
				hyOrder.setBaoxianWaimaiTuikuan(new BigDecimal(0));
				

				
				this.save(hyOrder);
				
				
				
				//生成保险订单
				insuranceOrderService.generateStoreInsuranceOrder(hyOrder, session);
				json.setSuccess(true);
				json.setMsg("下单成功");
				json.setObj(hyOrder);
			}
		}
		return json;
	}

	/** 门店下单 - 预存款支付 */
    @Override
    public Json addStoreOrderPayment(Long id, HttpSession session) throws Exception {
        Json json = new Json();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin hyAdmin = hyAdminService.find(username);
        HyOrder hyOrder = this.find(id);
        // 订单类型
        Integer type = hyOrder.getType();
        // 订单金额
        BigDecimal orderMoney = hyOrder.getJiusuanMoney().add(hyOrder.getTip()).subtract(hyOrder.getDiscountedPrice()).subtract(hyOrder.getStoreFanLi());
        Store store = storeService.find(hyOrder.getStoreId());
        // 判断门店类型 0虹宇门店，1挂靠门店，2直营门店，3非虹宇门店
        /*直营门店*/
        if (2 == store.getStoreType()) {
            // 直营门店store-->分公司连锁发展 getSuoshuDepartment()-->分公司getHyDepartment()
           Department department = store.getSuoshuDepartment().getHyDepartment();
			if(department == null){
               throw new Exception("直营门店对应的分公司不存在!");
           }

           // 修改BranchBalance
           List<Filter> filters = new ArrayList<>();
           filters.add(Filter.eq("branchId", department.getId()));
           List<BranchBalance> list = branchBalanceService.findList(null, filters, null);
           if(list==null || list.isEmpty()){
			   json.setMsg("直营门店对应的分公司预存款不足,请充值");
			   json.setSuccess(true);
			   return json;
		   }
           BranchBalance branchBalance = list.get(0);
           synchronized (BranchBalance.class){
               // 判断分公司预存款余额
               if(branchBalance.getBranchBalance().compareTo(orderMoney) < 0){
                   json.setMsg("直营门店对应的分公司预存款不足,请充值");
                   json.setSuccess(true);
                   return json;
               }

               // 修改BranchBalance
               branchBalance.setBranchBalance(branchBalance.getBranchBalance().subtract(orderMoney));
               branchBalanceService.update(branchBalance);
           }

           // 增加BranchPreSave
            BranchPreSave branchPreSave = new BranchPreSave();
            branchPreSave.setBranchId(department.getId());
            branchPreSave.setDepartmentName(department.getName());
            branchPreSave.setType(6);
            branchPreSave.setDate(new Date());
            branchPreSave.setAmount(orderMoney);
            branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance());
            branchPreSave.setRemark(hyOrder.getRemark());
            branchPreSave.setOrderId(hyOrder.getId());
            branchPreSaveService.save(branchPreSave);
        } else {
            /*非直营的其他门店*/
            // 修改StoreAccount
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("store", store));
            List<StoreAccount> list = storeAccountService.findList(null, filters, null);
			if(list==null || list.isEmpty()){
				json.setMsg("门店预存款不足，您还需线上支付"+orderMoney.setScale(2, BigDecimal.ROUND_HALF_UP)+"元");
				json.setSuccess(true);
				return json;
			}
            StoreAccount storeAccount = list.get(0);
            synchronized (StoreAccount.class) {
                // 判断门店预存款余额
                if (storeAccount.getBalance().compareTo(orderMoney) < 0) {
                    json.setMsg("门店预存款不足，您还需线上支付"+orderMoney.subtract(storeAccount.getBalance()).setScale(2, BigDecimal.ROUND_HALF_UP)+"元");
                    json.setSuccess(false);
                    return json;
                }
                // 修改StoreAccount
                storeAccount.setBalance(storeAccount.getBalance().subtract(orderMoney));
                storeAccountService.update(storeAccount);
            }

            // 增加StoreAccountLog
            StoreAccountLog storeAccountLog = new StoreAccountLog();
            storeAccountLog.setStore(store);
            // 类型,0充值，1订单抵扣，2分成，3退团，4消团
            storeAccountLog.setType(1);
            storeAccountLog.setStatus(1);
            storeAccountLog.setMoney(orderMoney);
            storeAccountLog.setCreateDate(new Date());
            storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
            storeAccountLogService.save(storeAccountLog);

            // 增加StorePreSave
            StorePreSave storePreSave = new StorePreSave();
            storePreSave.setStoreName(store.getStoreName());
            if (type.equals(0)) {// 导游订单
                storePreSave.setType(14);
            } else if (type.equals(1)) {// 线路订单
                storePreSave.setType(3);
            } else if (type.equals(2)) {// 订购门票订单
                storePreSave.setType(4);
            } else if (type.equals(3)) {// 酒店订单
                storePreSave.setType(10);
            } else if (type.equals(4)) {// 门票订单
                storePreSave.setType(5);
            } else if (type.equals(5)) {// 酒+景订单
                storePreSave.setType(15);
            } else if (type.equals(6)) {// 保险订单
                storePreSave.setType(6);
            } else if (type.equals(7)) {// 签证订单
                storePreSave.setType(8);
            }
            storePreSave.setDate(new Date());
            storePreSave.setAmount(orderMoney);
            storePreSave.setPreSaveBalance(storeAccount.getBalance());
            storePreSave.setOrderCode(hyOrder.getOrderNumber());
            storePreSave.setOrderId(hyOrder.getId());
            storePreSaveService.save(storePreSave);
        }

        // 添加付款记录PayandrefundRecord
        PayandrefundRecord record = new PayandrefundRecord();
        record.setOrderId(id);
        record.setMoney(orderMoney);
        // 5预存款
        record.setPayMethod(5);
        // 0付款
        record.setType(0);
        // 1已付款
        record.setStatus(1);
        record.setCreatetime(new Date());
        payandrefundRecordService.save(record);

        // 订单日志 - 门店支付   增加HyOrderApplication
        HyOrderApplication hyorderApplication = new HyOrderApplication();
        hyorderApplication.setType(HyOrderApplication.STORE_PAY_ORDER);
        hyorderApplication.setContent("门店订单支付");
        // 1：支付成功
        hyorderApplication.setStatus(1);
        // 1:通过
        hyorderApplication.setOutcome(1);
        hyorderApplication.setOperator(hyAdmin);
        hyorderApplication.setOrderId(hyOrder.getId());
        hyorderApplication.setCreatetime(new Date());
        hyOrderApplicationService.save(hyorderApplication);

        // 总公司 - 收支记录 - 已收款记录   增加ReceiptOther
        ReceiptOther receiptOther = new ReceiptOther();
        // 1:电子门票-门店 2:电子门票-微商 3:电子门票-官网 4:签证-门店 5:签证-微商 6:签证-官网 7:报名-门店 8:报名-微商 9:报名-官网 10:酒店-门店 11:酒店-官网 12:酒店-微商 13:门店认购门票 14:门店保险 15:酒店-门店 16:酒店-官网 17:酒店-微商 18:门店租导游 19:门店综合服务
        if (type.equals(0)) {// 导游订单
            receiptOther.setType(18);
        } else if (type.equals(1)) {// 线路订单
            receiptOther.setType(7);
        } else if (type.equals(2)) {// 订购门票订单
            receiptOther.setType(13);
        } else if (type.equals(3)) {// 酒店订单
            receiptOther.setType(10);
        } else if (type.equals(4)) {// 门票订单
            receiptOther.setType(1);
        } else if (type.equals(5)) {// 酒+景订单
            receiptOther.setType(15);
        } else if (type.equals(6)) {// 保险订单
            receiptOther.setType(14);
        } else if (type.equals(7)) {// 签证订单
            receiptOther.setType(4);
        }
        receiptOther.setOrderCode(hyOrder.getOrderNumber());
        receiptOther.setInstitution(store.getStoreName());
        receiptOther.setAmount(orderMoney);
        receiptOther.setDate(new Date());
        receiptOtherService.save(receiptOther);

        // 总公司 - 收支记录 - 已收款详情
        ReceiptDetail receiptDetail = new ReceiptDetail();
        // 1:ReceiptDepositStore 2:ReceiptDepositServicer 3:ReceiptStoreRecharge 4:ReceiptBranchRecharge 5:ReceiptDistributorRecharge 6:ReceiptBilliCycle 7:ReceiptOther
        receiptDetail.setReceiptType(7);
        receiptDetail.setReceiptId(receiptOther.getId());
        receiptDetail.setAmount(orderMoney);
        // 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
        receiptDetail.setPayMethod(5L);
        receiptDetail.setDate(new Date());
        receiptDetail.setRemark(hyOrder.getRemark());
        receiptDetailsService.save(receiptDetail);

		// 更新hyOrder状态
		hyOrder.setStatus(2);
		hyOrder.setPaystatus(1);
		hyOrder.setCheckstatus(1);
		hyOrder.setPayTime(new Date());
		this.update(hyOrder);


		if(hyOrder.getType()==1){
			HyGroup group = hyGroupService.find(hyOrder.getGroupId());
			HyLine hyLine = group.getLine();
			if(hyOrder.getXianlutype()==0 || hyLine.getIsAutoconfirm()){
				this.providerConfirm(hyOrder.getId(),"自动通过",1,null);
			}else{
				//给供应商发确认短信
				if(hyOrder.getSupplier()!=null) {
					SendMessageEMY.sendMessage(hyOrder.getSupplier().getMobile(), "", 8);
					List<String> userIds = new ArrayList<>();
					userIds.add(hyOrder.getSupplier().getUsername());
					String content = "您有新订单，确认时限为2小时，请尽快确认。";
					SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
				}
			}

		}else{
			//给供应商发确认短信
			if(hyOrder.getSupplier()!=null) {
				SendMessageEMY.sendMessage(hyOrder.getSupplier().getMobile(), "", 8);
				List<String> userIds = new ArrayList<>();
				userIds.add(hyOrder.getSupplier().getUsername());
				String content = "您有新订单，确认时限为2小时，请尽快确认。";
				SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
			}
		}

        json.setMsg("操作成功");
        json.setSuccess(true);
		return json;
	}

	@Autowired
	GroupMemberService groupMemberService;
	@Autowired
	GroupDivideService groupDivideService;
	@Autowired
	PayablesLineService payablesLineService;

	@Autowired
	PaymentSupplierService paymentSupplierService;

	@Override
	public Json providerConfirm(Long id, String view, Integer status, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		HyAdmin admin;
		if(session == null){
			admin = null;
		}else{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			admin = hyAdminService.find(username);
		}
		Json json = new Json();
		try {
			HyOrder order = this.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)) {
				throw new Exception("订单状态不对");
			}

			if (status.equals(0)) { // 如果供应商驳回
				if (view == null || view.equals("")) {
					throw new Exception("驳回意见必填");
				}
				supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(id, view, session);
				/* add by liyang,change the insurance order status */
				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", id));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(!insuranceOrders.isEmpty()){
					for(InsuranceOrder tmp:insuranceOrders){
						//将保险状态设置为已取消状态
						tmp.setStatus(2);
						insuranceOrderService.update(tmp);
					}
				}
				//add by wj 2019-07-07  添加短信提示  供应商驳回订单
//				String phone = null;
//				HyGroup group = hyGroupService.find(order.getGroupId());
//				phone = group.getCreator().getMobile();
//				if (phone != null) {
//					phone = order.getPhone();
//				} else {
//					phone = order.getPhone();
//				}
				String phone = null;
				Long storeId = order.getStoreId();
				if(storeId!=null){
					phone = storeService.find(storeId).getHyAdmin().getMobile();
				}
				SendMessageEMY.sendMessage(phone,"",20);
			} else {
				/* add by liyang,change the insurance order status */
				List<Filter> insurancefilters = new ArrayList<>();
				insurancefilters.add(Filter.eq("orderId", id));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
				if(!insuranceOrders.isEmpty()){
					for(InsuranceOrder tmp:insuranceOrders){
						//将保险状态设置为未投保状态（已确认）
						tmp.setStatus(1);
						insuranceOrderService.update(tmp);
					}
				}

				order.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
				// 如果供应商通过

				/*
				 * write by lbc
				 */

				// 向groupmember表中添加n条数据
				// 分团中相应团的subGroupNo+n
				// 根据订单找到相应的order_item_id 再根据order_item_id找到order_customer_id
				// 每个ordercustomer_id n + 1






				HyGroup group = hyGroupService.find(order.getGroupId());
				//不是内部的团也加人
				//if (group.getIsInner()) {
				// 计算一共有n个顾客
				int n = 0;

				for (HyOrderItem orderItem : order.getOrderItems()) {
					//如果这个订单条目已经被退掉了，则不再添加此游客的信息
					if(orderItem.getNumberOfReturn() == 1) {
						continue;
					}
					for (HyOrderCustomer orderCustomer : orderItem.getHyOrderCustomers()) {
						GroupMember groupMember = new GroupMember();
						groupMember.setHyOrder(order);
						groupMember.setHyOrderItem(orderItem);
						groupMember.setHyOrderCustomer(orderCustomer);
						groupMember.setHyGroup(group);
						groupMember.setSubGroupsn("A");
						groupMemberService.save(groupMember);
						n++;
					}
				}


				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("group", group));
				filters1.add(Filter.eq("subGroupsn", "A"));

				List<GroupDivide> groupDivides = groupDivideService.findList(null, filters1, null);
				if (groupDivides.size() > 0) {
					// 取第一个更新subGroupNo
					GroupDivide groupDivide = groupDivides.get(0);
					// 原数量加上n
					groupDivide.setSubGroupNo(groupDivide.getSubGroupNo() + n);
					groupDivideService.update(groupDivide);
				}
				else {
					//没分过团
					GroupDivide groupDivide = new GroupDivide();
					groupDivide.setGroup(group);
					groupDivide.setSubGroupsn("A");
					groupDivide.setSubGroupNo(n);
					groupDivideService.save(groupDivide);
				}
				//}

				// 供应商确认订单后  生成打款记录 xyy
				BigDecimal money = order.getJiesuanMoney1().add(order.getTip()).subtract(order.getDiscountedPrice()).subtract(order.getStoreFanLi());
				HyOrderItem hyOrderItem = order.getOrderItems().get(0);
				// prodectId实际上为groupId
				Long groupId = order.getGroupId();
				HyGroup hyGroup = hyGroupService.find(groupId);

				// 获取合同
				HyLine hyLine = hyGroup.getLine();
				HySupplierContract contract = hyLine.getContract();

				if(contract!=null && contract.getHySupplier()!=null && !contract.getHySupplier().getIsInner()){
					// 获取结算方式
					HySupplierContract.Settle settle = contract.getSettle();
					Date date = new Date();
					Calendar calendar = Calendar.getInstance();

					// 如果是实时结  或T+N的N为负但下单日期过了应结算日期   直接开启打款审批
					// 同一个合同 同一结算日期 在hy_payables_line中有多条,  条目分散,需要在定时器扫描时进行合并
					Date tnDate = null;
					if (HySupplierContract.Settle.tjiaN.equals(settle)) {
						tnDate = DateUtil.getDateAfterSpecifiedDays(hyGroup.getStartDay(), contract.getDateN());
					}
					if (HySupplierContract.Settle.shishijie.equals(settle) || (HySupplierContract.Settle.tjiaN.equals(settle) && order.getCreatetime().after(tnDate))) {
						// 在payableLine中增加数据
						PayablesLine payablesLine = new PayablesLine();
						payablesLine.setServicerName(contract.getHySupplier().getSupplierName());
						//合同(1)-帐号(n) 帐号(1)-产品(n)   产品(1)-团(n)   团(1)-订单(n)   使用线路产品的创建人(operator)
						payablesLine.setOperator(hyLine.getOperator());
						payablesLine.setSupplier(hyLine.getHySupplier());
						payablesLine.setSupplierContract(contract);
						// 0票务 1线路
						payablesLine.setSupplierType(1);
						payablesLine.setDate(date);
						// jiesuanMoney1 - youhuiMoney + tiaozhengMoney
						payablesLine.setMoney(money.subtract(order.getKoudianMoney()));
						payablesLineService.save(payablesLine);

						// 在payableLineItem中增加数据
						savePayablesLineItem(payablesLine, order, contract, hyGroup, hyLine, date, money);
						// 立即提交打款申请
						paymentSupplierService.addPaymentSuppierInstant(payablesLine);
					}
					// 非实时的方式
					else {
						// 月结
						if (HySupplierContract.Settle.yuejie.equals(settle)) {
							calendar.add(Calendar.MONTH, 1);
							calendar.set(Calendar.DATE, 1);
							date = calendar.getTime();
						}
						// 半月结
						else if (HySupplierContract.Settle.banyuejie.equals(settle)) {
							int d = calendar.get(Calendar.DATE);
							if (d >= 15) {
								calendar.add(Calendar.MONTH, 1);
								calendar.set(Calendar.DATE, 1);
								date = calendar.getTime();
							} else {
								calendar.set(Calendar.DATE, 15);
								date = calendar.getTime();
							}
						}
						// 周结
						else if (HySupplierContract.Settle.zhoujie.equals(settle)) {
							// c为下周周三
							Calendar c = Calendar.getInstance();
							int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
							if (day_of_week == 0) {
								day_of_week = 7;
							}
							c.add(Calendar.DATE, -day_of_week + 10);
							date = c.getTime();
						}
						// T+N结算
						else if (HySupplierContract.Settle.tjiaN.equals(settle)) {
							date = tnDate;
						}
						// 对于非实时  (contract, 结算日期, 该产品的创建人) 应当唯一确定一条 payablesLine
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("supplierContract", contract));
						filters.add(Filter.eq("operator", hyLine.getOperator()));
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String dateString = sdf.format(date);
						filters.add(new Filter("date", Operator.ge, sdf.parse(dateString.substring(0, 10) + " " + "00:00:00")));
						filters.add(new Filter("date", Operator.le, sdf.parse(dateString.substring(0, 10) + " " + "23:59:59")));
						List<PayablesLine> payablesLines = payablesLineService.findList(null, filters, null);

						if (CollectionUtils.isNotEmpty(payablesLines)) {
							// (contract, 结算日期, 该产品的创建人) 确定一条 payablesLine
							PayablesLine payablesLine = payablesLines.get(0);
							payablesLine.setMoney(payablesLine.getMoney().add(money).subtract(order.getKoudianMoney()));
							payablesLineService.update(payablesLine);
							// 在payableLineItem中增加数据
							savePayablesLineItem(payablesLine, order, contract, hyGroup, hyLine, date, money);
						} else {
							// 在payableLine中增加数据
							PayablesLine payablesLine = new PayablesLine();
							payablesLine.setServicerName(contract.getHySupplier().getSupplierName());
							payablesLine.setOperator(hyLine.getOperator());
							payablesLine.setSupplier(hyLine.getHySupplier());
							payablesLine.setSupplierContract(contract);
							// 0票务 1线路
							payablesLine.setSupplierType(1);
							payablesLine.setDate(date);
							// jiesuanMoney1 - youhuiMoney + tiaozhengMoney
							payablesLine.setMoney(money.subtract(order.getKoudianMoney()));
							payablesLineService.save(payablesLine);
							// 在payableLineItem中增加数据
							savePayablesLineItem(payablesLine, order, contract, hyGroup, hyLine, date, money);
						}
					}
				}
			}

			HyOrderApplication application = new HyOrderApplication();
			application.setContent("供应商确认订单");
			application.setView(view);
			application.setStatus(status);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setOperator(admin);
			// 供应商确认订单
			application.setType(HyOrderApplication.PROVIDER_CONFIRM_ORDER);
			hyOrderApplicationService.save(application);

			this.update(order);

			json.setSuccess(true);
			json.setMsg("供应商确认成功");
			json.setObj(null);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("供应商确认失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;

	}

	/**
	 *  在payableLineItem中增加数据
	 * */
	private void savePayablesLineItem(PayablesLine payablesLine, HyOrder order, HySupplierContract contract, HyGroup hyGroup, HyLine hyLine, Date date, BigDecimal money) {
		PayablesLineItem item = new PayablesLineItem();
		item.setPayablesLineId(payablesLine.getId());
		item.setHyOrder(order);
		item.setSupplierContract(contract);
		item.setOperator(hyLine.getOperator());
		// 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
		item.setProductType(1);
		item.setHyGroup(hyGroup);
		item.setSn(hyLine.getPn());
		item.setProductName(hyLine.getName());
		item.settDate(hyGroup.getStartDay());
		item.setSettleDate(date);
		item.setOrderMoney(money);
		item.setKoudian(order.getKoudianMoney());
		item.setMoney(money.subtract(order.getKoudianMoney()));
		// 0:未提交 1:已提交
		item.setState(0);
		payablesLineItemService.save(item);
	}

	@Autowired
	PayablesLineItemService payablesLineItemService;


	/** 门店支付门票 - 网银支付 */
	@Override
	public void addStoreOrderBankPayment(String orderNum,String transAmt) throws Exception {
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("orderNumber",orderNum));
		List<HyOrder> orderList=this.findList(null,filters,null);
		filters.clear();
		HyOrder hyOrder = orderList.get(0);  //找到对应的订单
		Store store = storeService.find(hyOrder.getStoreId());
		filters.add(Filter.eq("store", store));
		List<StoreAccount> storeAccounts = storeAccountService.findList(null, filters, null);
		filters.clear();
		StoreAccount storeAccount=storeAccounts.get(0);
		BigDecimal storeBalance=storeAccount.getBalance(); //找出门店余额
		BigDecimal transMoney=new BigDecimal(transAmt); //网银交易金额
		BigDecimal orderMoney=BigDecimal.ZERO; //实际订单金额
		//6-保险,如果是保险订单,则计算外卖价
		if(hyOrder.getType()==6) {
			orderMoney=orderMoney.add(hyOrder.getWaimaiMoney());
		}
		else {
			//订单金额要减去门店返利值
			orderMoney = orderMoney.add(hyOrder.getJiusuanMoney());
			if(hyOrder.getTip()!=null) {
				orderMoney=orderMoney.add(hyOrder.getTip());
			}
			if(hyOrder.getDiscountedPrice()!=null) {
				orderMoney=orderMoney.subtract(hyOrder.getDiscountedPrice());
			}
			if(hyOrder.getStoreFanLi()!=null) {
				orderMoney=orderMoney.subtract(hyOrder.getStoreFanLi());
			}
		}
		
		//判断同一个订单，同一个门店是否有相同的记录，如果有，就不再插入。
		filters.add(Filter.eq("store", store));
		filters.add(Filter.eq("type", 0));
		filters.add(Filter.eq("status", 5));
		filters.add(Filter.eq("orderSn", orderNum));
		List<StoreAccountLog> preAccountLogList=storeAccountLogService.findList(null,filters,null);
		if(!preAccountLogList.isEmpty()) 
			return;
		
		// 修改StoreAccountLog
		StoreAccountLog storeAccountLog = new StoreAccountLog();
		storeAccountLog.setStore(store);
		storeAccountLog.setType(0); // 类型,0充值，1订单抵扣，2分成，3退团，4消团
		storeAccountLog.setStatus(5); //网银充值已成功支付
		storeAccountLog.setMoney(transMoney); //门店充值的记录应该是交易金额
		storeAccountLog.setCreateDate(new Date());
		storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
		storeAccountLog.setProfile("网银支付订单,充值");
		storeAccountLogService.save(storeAccountLog);
		
		/**
		 * 如果门店预存款金额+网银交易金额=实际订单金额,说明在这期间没有其他账号修改门店预存款,
		 * 这个时候可以抵扣订单;
		 * 如果上述条件不相等,说明期间有其他员工账号修改预存款余额,这个时候不抵扣,
		 * 并且不修改订单状态
		 */
		if(storeBalance.add(transMoney).compareTo(orderMoney)==0) {
			//再生成一条门店预存款抵扣记录
			StoreAccountLog storeAccountLog2 = new StoreAccountLog();
			storeAccountLog2.setStore(store);
			storeAccountLog2.setType(1); // 类型,0充值，1订单抵扣，2分成，3退团，4消团
			storeAccountLog2.setStatus(1); //订单抵扣成功
			storeAccountLog2.setMoney(orderMoney);
			storeAccountLog2.setCreateDate(new Date());
			storeAccountLog2.setOrderSn(hyOrder.getOrderNumber());
			storeAccountLogService.save(storeAccountLog2);	
			
			storeAccount.setBalance(storeBalance.subtract(orderMoney.subtract(transMoney)));
			//修改预存款的值
			storeAccountService.update(storeAccount);
			
			// 订单日志 - 门店支付
			HyOrderApplication hyorderApplication = new HyOrderApplication();
			hyorderApplication.setType(HyOrderApplication.STORE_PAY_ORDER);
			hyorderApplication.setContent("门店订单支付");
			hyorderApplication.setStatus(1);	//1：支付成功
			hyorderApplication.setOutcome(1); // 1:通过
			hyorderApplication.setOrderId(hyOrder.getId());
			hyorderApplication.setCreatetime(new Date());
			hyOrderApplicationService.save(hyorderApplication);

			// 修改StorePreSave
			StorePreSave storePreSave = new StorePreSave();
			storePreSave.setStoreName(store.getStoreName());
            
			// 1:门店充值 2:报名退款 3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵
			// 9:签证退款 10:酒店销售 11:酒店退款 12:门店后返 13:供应商驳回订单 14:门店租导游 15:酒加景销售 16:酒加景退款
			Integer type = hyOrder.getType();
			if (type.equals(0)) {// 导游订单
				storePreSave.setType(14);
			} else if (type.equals(1)) {// 线路订单
				storePreSave.setType(3);
			} else if (type.equals(2)) {// 订购门票订单
				storePreSave.setType(4);
			} else if (type.equals(3)) {// 酒店订单
				storePreSave.setType(10);
			} else if (type.equals(4)) {// 门票订单
				storePreSave.setType(5);
			} else if (type.equals(5)) {// 酒+景订单
				storePreSave.setType(15);
			} else if (type.equals(6)) {// 保险订单
				storePreSave.setType(6);
			} else if (type.equals(7)) {// 签证订单
				storePreSave.setType(8);
			}
			storePreSave.setDate(new Date());
			storePreSave.setAmount(orderMoney);
			storePreSave.setPreSaveBalance(storeAccount.getBalance().subtract(orderMoney));
			storePreSave.setOrderCode(hyOrder.getOrderNumber());
			storePreSave.setOrderId(hyOrder.getId());
			storePreSaveService.save(storePreSave);

			// 总公司 - 收支记录 - 已收款记录
			ReceiptOther receiptOther = new ReceiptOther();
			/**
			 * 1:电子门票-门店 2:电子门票-微商 3:电子门票-官网 4:签证-门店 5:签证-微商 6:签证-官网 7:报名-门店 8:报名-微商
			 * 9:报名-官网 10:酒店-门店 11:酒店-官网 12:酒店-微商 13:门店认购门票 14:门店保险
			 * 
			 * 15:酒店-门店 16:酒店-官网 17:酒店-微商 18:门店租导游
			 */
			if (type.equals(0)) {// 导游订单
				receiptOther.setType(18);
			} else if (type.equals(1)) {// 线路订单
				receiptOther.setType(7);
			} else if (type.equals(2)) {// 订购门票订单
				receiptOther.setType(13);
			} else if (type.equals(3)) {// 酒店订单
				receiptOther.setType(10);
			} else if (type.equals(4)) {// 门票订单
				receiptOther.setType(1);
			} else if (type.equals(5)) {// 酒+景订单
				receiptOther.setType(15);
			} else if (type.equals(6)) {// 保险订单
				receiptOther.setType(14);
			} else if (type.equals(7)) {// 签证订单
				receiptOther.setType(4);
			}
			receiptOther.setOrderCode(hyOrder.getOrderNumber());
			receiptOther.setInstitution(store.getStoreName());
			receiptOther.setAmount(orderMoney);
			receiptOther.setDate(new Date());
			receiptOtherService.save(receiptOther);

			// 总公司 - 收支记录 - 已收款详情
			ReceiptDetail receiptDetail = new ReceiptDetail();
			/**
			 * 1:ReceiptDepositStore 2:ReceiptDepositServicer 3:ReceiptStoreRecharge
			 * 4:ReceiptBranchRecharge 5:ReceiptDistributorRecharge
			 * 6:ReceiptBilliCycle 7:ReceiptOther
			 */
			receiptDetail.setReceiptType(7);
			receiptDetail.setReceiptId(receiptOther.getId());
			receiptDetail.setAmount(orderMoney);
			// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
			receiptDetail.setPayMethod(5L); // 5 预存款
			receiptDetail.setDate(new Date());
			receiptDetail.setRemark(hyOrder.getRemark());
			receiptDetailsService.save(receiptDetail);
			
			// 添加付款记录
			PayandrefundRecord record = new PayandrefundRecord();
			record.setOrderId(hyOrder.getId());
			record.setMoney(orderMoney);
			record.setPayMethod(5);	//5预存款
			record.setType(0);	//0付款
			record.setStatus(1);	//1已付款
			record.setCreatetime(new Date());
			payandrefundRecordService.save(record);
			//保险订单
			if(type.equals(6)) {
				hyOrder.setStatus(3);
			}
			else {
				hyOrder.setStatus(2);
			}
			
			hyOrder.setPaystatus(1);
			hyOrder.setCheckstatus(1);
			hyOrder.setPayTime(new Date());
			this.update(hyOrder);

			if(hyOrder.getType()==1){
				HyGroup group = hyGroupService.find(hyOrder.getGroupId());
				HyLine hyLine = group.getLine();
				if(hyLine.getIsAutoconfirm()){
					this.providerConfirm(hyOrder.getId(),"自动通过",1,null);
				}
				else {
					//给供应商发确认短信
					if(hyOrder.getSupplier()!=null) {
						SendMessageEMY.sendMessage(hyOrder.getSupplier().getMobile(), "", 8);
						List<String> userIds = new ArrayList<>();
						userIds.add(hyOrder.getSupplier().getUsername());
						String content = "您有新订单，确认时限为1小时，请尽快确认。";
						SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
					}
				}
			}
			else{
				//给供应商发确认短信
				if(hyOrder.getSupplier()!=null) {
					SendMessageEMY.sendMessage(hyOrder.getSupplier().getMobile(), "", 8);
					List<String> userIds = new ArrayList<>();
					userIds.add(hyOrder.getSupplier().getUsername());
					String content = "您有新订单，确认时限为1小时，请尽快确认。";
					SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
				}
			}
			//保险订单
			if(hyOrder.getType() == 6) {
				Long[] orderIds = new Long[1];
				orderIds[0] = hyOrder.getId();
				Json json = insuranceOrderService.postOrderToJT(orderIds);
				if(json.getObj() == null) {
					System.out.println("下单到江泰失败,请到保单管理手动支付 ");
				}
				Map<String,Object> map = (Map<String,Object>)json.getObj();
				List<Map<String,Object>> list = (List<Map<String,Object>>) (map.get("successIds"));
				//下单到江泰失败
				if(list.size() < 1) {
					System.out.println("下单到江泰失败,请到保单管理手动支付 ");
				}
			}
		}
		
		//相当于充值,要加门店预存款余额,更新门店预存款余额
		else {
			storeAccount.setBalance(storeBalance.add(transMoney));
			storeAccountService.update(storeAccount);
		}
	}

	@Override
	public Json addInsuranceOrderPayment(Long id, HttpSession session) throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);

		HyOrder hyOrder = this.find(id);
		
		Store store = storeService.find(hyOrder.getStoreId());
		Integer type = hyOrder.getType();
//		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("store", store));
//		List<StoreAccount> list = storeAccountService.findList(null, filters, null);
//		StoreAccount storeAccount = list.get(0);
		BigDecimal orderMoney = hyOrder.getWaimaiMoney();
//		synchronized (storeAccount) {
//			//因为是保险 要用外卖价
//			orderMoney = hyOrder.getWaimaiMoney();
//			// 判断门店预存款余额
//			if (storeAccount.getBalance().compareTo(orderMoney) < 0) {
//				json.setMsg("预存款不足，请充值");
//				json.setSuccess(false);
//				return json;
//			}
//
//			// 修改StoreAccount
//			storeAccount.setBalance(storeAccount.getBalance().subtract(orderMoney));
//			storeAccountService.update(storeAccount);
//		}

//		// 修改StoreAccountLog
//		StoreAccountLog storeAccountLog = new StoreAccountLog();
//		storeAccountLog.setStore(store);
//		storeAccountLog.setType(1); // 类型,0充值，1订单抵扣，2分成，3退团，4消团
//		storeAccountLog.setStatus(1);
//		storeAccountLog.setMoney(orderMoney);
//		storeAccountLog.setCreateDate(new Date());
//		storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
//		storeAccountLogService.save(storeAccountLog);
//
//		
//
//		// 修改StorePreSave
//		StorePreSave storePreSave = new StorePreSave();
//		storePreSave.setStoreName(store.getStoreName());
//		storePreSave.setStoreId(store.getId());
//
//		// 1:门店充值 2:报名退款 3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵
//		// 9:签证退款 10:酒店销售 11:酒店退款 12:门店后返 13:供应商驳回订单 14:门店租导游 15:酒加景销售 16:酒加景退款
//		Integer type = hyOrder.getType();
//		if (type.equals(0)) {// 导游订单
//			storePreSave.setType(14);
//		} else if (type.equals(1)) {// 线路订单
//			storePreSave.setType(3);
//		} else if (type.equals(2)) {// 订购门票订单
//			storePreSave.setType(4);
//		} else if (type.equals(3)) {// 酒店订单
//			storePreSave.setType(10);
//		} else if (type.equals(4)) {// 门票订单
//			storePreSave.setType(5);
//		} else if (type.equals(5)) {// 酒+景订单
//			storePreSave.setType(15);
//		} else if (type.equals(6)) {// 保险订单
//			storePreSave.setType(6);
//		} else if (type.equals(7)) {// 签证订单
//			storePreSave.setType(8);
//		}
//		storePreSave.setDate(new Date());
//		storePreSave.setAmount(orderMoney);
//		storePreSave.setPreSaveBalance(storeAccount.getBalance().subtract(orderMoney));
//		storePreSave.setOrderCode(hyOrder.getOrderNumber());
//		storePreSave.setOrderId(hyOrder.getId());
//		storePreSaveService.save(storePreSave);

		
		
		
		
		
		
		/*直营门店*/
        if (2 == store.getStoreType()) {
            // 直营门店store-->分公司连锁发展 getSuoshuDepartment()-->分公司getHyDepartment()
           Department department = store.getSuoshuDepartment().getHyDepartment();
			if(department == null){
               throw new Exception("直营门店对应的分公司不存在!");
           }

           // 修改BranchBalance
           List<Filter> filters = new ArrayList<>();
           filters.add(Filter.eq("branchId", department.getId()));
           List<BranchBalance> list = branchBalanceService.findList(null, filters, null);
           if(list==null || list.isEmpty()){
			   json.setMsg("直营门店对应的分公司预存款不足,请充值");
			   json.setSuccess(true);
			   return json;
		   }
           BranchBalance branchBalance = list.get(0);
           synchronized (BranchBalance.class){
               // 判断分公司预存款余额
               if(branchBalance.getBranchBalance().compareTo(orderMoney) < 0){
                   json.setMsg("直营门店对应的分公司预存款不足,请充值");
                   json.setSuccess(true);
                   return json;
               }

               // 修改BranchBalance
               branchBalance.setBranchBalance(branchBalance.getBranchBalance().subtract(orderMoney));
               branchBalanceService.update(branchBalance);
           }

           // 增加BranchPreSave
            BranchPreSave branchPreSave = new BranchPreSave();
            branchPreSave.setBranchId(department.getId());
            branchPreSave.setDepartmentName(department.getName());
            branchPreSave.setType(6);
            branchPreSave.setDate(new Date());
            branchPreSave.setAmount(orderMoney);
            branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance());
            branchPreSave.setRemark(hyOrder.getRemark());
            branchPreSave.setOrderId(hyOrder.getId());
            branchPreSaveService.save(branchPreSave);
        } else {
            /*非直营的其他门店*/
            // 修改StoreAccount
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("store", store));
            List<StoreAccount> list = storeAccountService.findList(null, filters, null);
			if(list==null || list.isEmpty()){
				json.setMsg("门店预存款不足，您还需线上支付"+orderMoney.setScale(2, BigDecimal.ROUND_HALF_UP)+"元");
				json.setSuccess(true);
				return json;
			}
            StoreAccount storeAccount = list.get(0);
            synchronized (StoreAccount.class) {
                // 判断门店预存款余额
                if (storeAccount.getBalance().compareTo(orderMoney) < 0) {
                    json.setMsg("门店预存款不足，您还需线上支付"+orderMoney.subtract(storeAccount.getBalance()).setScale(2, BigDecimal.ROUND_HALF_UP)+"元");
                    json.setSuccess(false);
                    return json;
                }
                // 修改StoreAccount
                storeAccount.setBalance(storeAccount.getBalance().subtract(orderMoney));
                storeAccountService.update(storeAccount);
            }

            // 增加StoreAccountLog
            StoreAccountLog storeAccountLog = new StoreAccountLog();
            storeAccountLog.setStore(store);
            // 类型,0充值，1订单抵扣，2分成，3退团，4消团
            storeAccountLog.setType(1);
            storeAccountLog.setStatus(1);
            storeAccountLog.setMoney(orderMoney);
            storeAccountLog.setCreateDate(new Date());
            storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
            storeAccountLogService.save(storeAccountLog);

            // 增加StorePreSave
            StorePreSave storePreSave = new StorePreSave();
            storePreSave.setStoreName(store.getStoreName());
            if (type.equals(0)) {// 导游订单
                storePreSave.setType(14);
            } else if (type.equals(1)) {// 线路订单
                storePreSave.setType(3);
            } else if (type.equals(2)) {// 订购门票订单
                storePreSave.setType(4);
            } else if (type.equals(3)) {// 酒店订单
                storePreSave.setType(10);
            } else if (type.equals(4)) {// 门票订单
                storePreSave.setType(5);
            } else if (type.equals(5)) {// 酒+景订单
                storePreSave.setType(15);
            } else if (type.equals(6)) {// 保险订单
                storePreSave.setType(6);
            } else if (type.equals(7)) {// 签证订单
                storePreSave.setType(8);
            }
            storePreSave.setDate(new Date());
            storePreSave.setAmount(orderMoney);
            storePreSave.setPreSaveBalance(storeAccount.getBalance());
            storePreSave.setOrderCode(hyOrder.getOrderNumber());
            storePreSave.setOrderId(hyOrder.getId());
            storePreSaveService.save(storePreSave);
        } 
		 
		
     // 订单日志 - 门店支付
 		HyOrderApplication hyorderApplication = new HyOrderApplication();
 		hyorderApplication.setType(HyOrderApplication.STORE_PAY_ORDER);
 		hyorderApplication.setContent("门店订单支付");
 		hyorderApplication.setStatus(1);	//1：支付成功
 		hyorderApplication.setOutcome(1); // 1:通过
 		hyorderApplication.setOperator(hyAdmin);
 		hyorderApplication.setOrderId(hyOrder.getId());
 		hyorderApplication.setCreatetime(new Date());
 		hyOrderApplicationService.save(hyorderApplication);
		
        // 总公司 - 收支记录 - 已收款记录
 		ReceiptOther receiptOther = new ReceiptOther();
 		/**
 		 * 1:电子门票-门店 2:电子门票-微商 3:电子门票-官网 4:签证-门店 5:签证-微商 6:签证-官网 7:报名-门店 8:报名-微商
 		 * 9:报名-官网 10:酒店-门店 11:酒店-官网 12:酒店-微商 13:门店认购门票 14:门店保险
 		 * 
 		 * 15:酒店-门店 16:酒店-官网 17:酒店-微商 18:门店租导游
 		 */
 		if (type.equals(0)) {// 导游订单
 			receiptOther.setType(18);
 		} else if (type.equals(1)) {// 线路订单
 			receiptOther.setType(7);
 		} else if (type.equals(2)) {// 订购门票订单
 			receiptOther.setType(13);
 		} else if (type.equals(3)) {// 酒店订单
 			receiptOther.setType(10);
 		} else if (type.equals(4)) {// 门票订单
 			receiptOther.setType(1);
 		} else if (type.equals(5)) {// 酒+景订单
 			receiptOther.setType(15);
 		} else if (type.equals(6)) {// 保险订单
 			receiptOther.setType(14);
 		} else if (type.equals(7)) {// 签证订单
 			receiptOther.setType(4);
 		}
 		receiptOther.setOrderCode(hyOrder.getOrderNumber());
 		receiptOther.setInstitution(store.getStoreName());
 		receiptOther.setAmount(orderMoney);
 		receiptOther.setDate(new Date());
 		receiptOtherService.save(receiptOther);

 		// 总公司 - 收支记录 - 已收款详情
 		ReceiptDetail receiptDetail = new ReceiptDetail();
 		/**
 		 * 1:ReceiptDepositStore 2:ReceiptDepositServicer 3:ReceiptStoreRecharge
 		 * 4:ReceiptBranchRecharge 5:ReceiptDistributorRecharge
 		 * 6:ReceiptBilliCycle 7:ReceiptOther
 		 */
 		receiptDetail.setReceiptType(7);
 		receiptDetail.setReceiptId(receiptOther.getId());
 		receiptDetail.setAmount(orderMoney);
 		// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
 		receiptDetail.setPayMethod(5L); // 5 预存款
 		receiptDetail.setDate(new Date());
 		receiptDetail.setRemark(hyOrder.getRemark());
 		receiptDetailsService.save(receiptDetail);
		
		
		
		
		// 添加付款记录
		PayandrefundRecord record = new PayandrefundRecord();
		record.setOrderId(id);
		record.setMoney(orderMoney);
		record.setPayMethod(5);	//5预存款
		record.setType(0);	//0付款
		record.setStatus(1);	//1已付款
		record.setCreatetime(new Date());
		payandrefundRecordService.save(record);

		json.setMsg("操作成功");
		json.setSuccess(true);
		
		hyOrder.setStatus(3);
		hyOrder.setPaystatus(1);
		//添加付款时间
		hyOrder.setPayTime(new Date());
		//hyOrder.setCheckstatus(1);
		this.update(hyOrder);
		
		return json;
	}

	@Override
	public BigDecimal getLineRefundPercentage(HyOrder order) {
		HyGroup group = hyGroupService.find(order.getGroupId());
		HyLine line = group.getLine();
		if (line.getRefundType().equals(HyLine.RefundTypeEnum.quane)) {
			return BigDecimal.valueOf(100);	//如果是全额类型，不退款
		}
		// 如果是阶梯退款
		List<HyLineRefund> refunds = line.getLineRefunds();
		Date startDay = group.getStartDay();
		Date nowaDay = new Date();
		Long diffHours = TimeUnit.HOURS.convert(startDay.getTime() - nowaDay.getTime(), TimeUnit.MILLISECONDS);

		for (HyLineRefund refund : refunds) {
			Long startHours = (refund.getStartDay() - 1) * 24L + (24 - refund.getStartTime());
			Long endHours = (refund.getEndDay() - 1) * 24L + (24 - refund.getEndTime());
			if (diffHours > startHours && diffHours <= endHours) {
				return refund.getPercentage();
			}
		}
		return BigDecimal.valueOf(100);
	}

	@Override
	public List<HyOrder> getOrdersByProviderName(String providerName,Integer orderType) {
		//首先根据供应商名称获得HySupplier
		List<Filter> providerFilters = new ArrayList<>();
		providerFilters.add(Filter.like("supplierName", providerName));
		List<HySupplier> suppliers = hySupplierService.findList(null,providerFilters,null);
		if(suppliers==null || suppliers.isEmpty()){
			return null;
		}
		
		Set<HyOrder> orders = new HashSet();
		
		if(orderType==1) {	//如果是求线路订单
			//获取该供应商建的线路
			List<Filter> lineFilters = new ArrayList<>();
			lineFilters.add(Filter.in("hySupplier", suppliers));
			List<HyLine> lines = hyLineService.findList(null,lineFilters,null);
			
			//获取线路对应的团
			List<Filter> groupFilters = new ArrayList<>();
			groupFilters.add(Filter.in("line", lines));
			List<HyGroup> groups = hyGroupService.findList(null,groupFilters,null);
			if(groups == null || groups.isEmpty()){
				return null;
			}
			//获得groupId列表
			
			for(HyGroup group:groups){
				//获取订单列表
				List<Filter> orderFilters = new ArrayList<>();
				orderFilters.add(Filter.eq("groupId", group.getId()));
				orderFilters.add(Filter.eq("type", 1));
				List<HyOrder> tmps = this.findList(null,orderFilters,null);
				orders.addAll(tmps);
			}
		}else if(orderType==3) {	//如果是酒店订单
			//获取该供应商所创建的酒店
			List<Filter> hotelFilters = new ArrayList<>();
			hotelFilters.add(Filter.in("ticketSupplier", suppliers));
			List<HyTicketHotel> hyTicketHotels = hyTicketHotelService.findList(null,hotelFilters,null);
			//根据酒店获取所对应的订单
			for(HyTicketHotel hyTicketHotel:hyTicketHotels) {
				List<Filter> orderItemFilters = new ArrayList<>();
				orderItemFilters.add(Filter.eq("productId", hyTicketHotel.getId()));
				orderItemFilters.add(Filter.eq("type", 3));
				List<HyOrderItem> tmps = hyOrderItemService.findList(null,orderItemFilters,null);
				for(HyOrderItem item:tmps) {
					orders.add(item.getOrder());
				}
			}
			
		}
        
		else if(orderType==5) {	//如果是酒加景订单
			//获取该供应商所创建的酒加景产品
			List<Filter> hotelFilters = new ArrayList<>();
			hotelFilters.add(Filter.in("ticketSupplier", suppliers));
			List<HyTicketHotelandscene> hyTicketHotelandscenes = hyTicketHotelandsceneService.findList(null,hotelFilters,null);
			//根据酒加景获取所对应的订单
			for(HyTicketHotelandscene hyTicketHotelandscene:hyTicketHotelandscenes) {
				List<Filter> orderItemFilters = new ArrayList<>();
				orderItemFilters.add(Filter.eq("productId", hyTicketHotelandscene.getId()));
				orderItemFilters.add(Filter.eq("type", 5));
				List<HyOrderItem> tmps = hyOrderItemService.findList(null,orderItemFilters,null);
				for(HyOrderItem item:tmps) {
					orders.add(item.getOrder());
				}
			}
			
		}
		else if(orderType==7) {	//如果是签证订单
			List<Filter> visaFilters = new ArrayList<>();
			visaFilters.add(Filter.in("ticketSupplier", suppliers));
			List<HyVisa> hyVisas = hyVisaService.findList(null,visaFilters,null);
			//根据酒加景获取所对应的订单
			for(HyVisa hyVisa:hyVisas) {
				List<Filter> orderItemFilters = new ArrayList<>();
				orderItemFilters.add(Filter.eq("productId", hyVisa.getId()));
				orderItemFilters.add(Filter.eq("type", 7));
				List<HyOrderItem> tmps = hyOrderItemService.findList(null,orderItemFilters,null);
				for(HyOrderItem item:tmps) {
					orders.add(item.getOrder());
				}
			}
			
		}
		List<HyOrder> result = new ArrayList<HyOrder>(orders);
		return result;

	}

	@Override
	public List<Long> getOrderIdsByProviderName(String providerName,Integer orderType) {
		// TODO Auto-generated method stub
		List<HyOrder> orders = this.getOrdersByProviderName(providerName,orderType);
		if(orders==null || orders.isEmpty()){
			return null;
		}
		List<Long> orderIds = new ArrayList<>();
		for(HyOrder order:orders){
			orderIds.add(order.getId());
		}
				
		return orderIds;
	}

	@Override
	public Boolean cancelOrder(Long id) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = find(id);
		if (order == null) {
			throw new Exception("订单不存在");
		}
		if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
			// 如果订单已支付
			throw new Exception("订单状态已支付，无法取消");
		}
		if (order.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
			throw new Exception("订单已经取消，不能重复取消");
		}
		
		// 设置订单状态为已取消
		order.setStatus(Constants.HY_ORDER_STATUS_CANCELED);

		if(order.getType()==1){	//线路订单
			//修改团的余位
			//求总人数
			int people = 0;
			for(HyOrderItem item:order.getOrderItems()) {
				people+=item.getHyOrderCustomers().size();
			}
			//恢复团的总库存
			HyGroup hyGroup = hyGroupService.find(order.getGroupId());
			if(hyGroup!=null) {
				synchronized(hyGroup){
					hyGroup.setStock(hyGroup.getStock()+people);
					hyGroup.setSignupNumber(hyGroup.getSignupNumber()-people);
					hyGroupService.update(hyGroup);
				}
				//added by GSbing,20190301,修改计调报账人数
				List<Filter> hyRegulateFilter = new ArrayList<>();
				hyRegulateFilter.add(Filter.eq("hyGroup", hyGroup.getId()));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null, hyRegulateFilter, null);
				if(hyRegulates.size() != 0) {
					HyRegulate hyRegulate = hyRegulates.get(0);
					synchronized(hyRegulate) {
						hyRegulate.setVisitorNum(hyGroup.getSignupNumber());
						hyRegulateService.update(hyRegulate);
					}
				}
			}
		}
		update(order);
		/* add by liyang,change the insurance order status */
		List<Filter> insurancefilters = new ArrayList<>();
		insurancefilters.add(Filter.eq("orderId", id));
		List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
		if(!insuranceOrders.isEmpty()){
			for(InsuranceOrder tmp:insuranceOrders){
				//将保险状态设置为已取消状态
				tmp.setStatus(2);
				insuranceOrderService.update(tmp);
			}
		}
		return true;
	}
	
	@Override
	public Boolean cancelInsuranceOrder(Long id) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = find(id);
		if (order == null) {
			throw new Exception("订单不存在");
		}
		if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
			// 如果订单已支付
			throw new Exception("订单状态已支付，无法取消");
		}
		if (order.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
			throw new Exception("订单已经取消，不能重复取消");
		}
		
		// 设置订单状态为已取消
		order.setStatus(Constants.HY_ORDER_STATUS_CANCELED);
		
		update(order);
		/* add by liyang,change the insurance order status */
		List<Filter> insurancefilters = new ArrayList<>();
		insurancefilters.add(Filter.eq("orderId", id));
		List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
		if(!insuranceOrders.isEmpty()){
			for(InsuranceOrder tmp:insuranceOrders){
				//将保险状态设置为已取消状态
				tmp.setStatus(2);
				insuranceOrderService.update(tmp);
			}
		}
		return true;
	}

	@Override
	public Json addVisaOrder(HyOrder hyOrder, HttpSession session) throws Exception {
		
		Json json = new Json();
		if (hyOrder == null) {
			json.setSuccess(false);
			json.setMsg("订单为空，请检查");
		} else {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			} else {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0L);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				String code = nowaday + String.format("%05d", value);
				//得到订单号
				hyOrder.setOrderNumber(code);
				hyOrder.setStatus(0);// 0门店待支付
				hyOrder.setPaystatus(0);// 0门店待支付
				hyOrder.setCheckstatus(0);// 门店已确认
				hyOrder.setRefundstatus(0);// 门店未退款
				hyOrder.setType(7);// 签证订单
				hyOrder.setSource(0);// 0门店1官网2微商
				
				Integer people = 0;

				//直营门店
				if(store.getStoreType() == 2) {
					hyOrder.setStoreType(1);// 直营门店
				}
				else {
					hyOrder.setStoreType(0);// 虹宇门店
				}
				
				hyOrder.setStoreId(store.getId());
				hyOrder.setOperator(hyAdmin);
				hyOrder.setCreatorId(username);
				hyOrder.setCreatetime(new Date());

				BigDecimal adjustMoney = new BigDecimal(0);// 调整金额
				BigDecimal jiusuanMoney = new BigDecimal(0);// 订单结算价
				BigDecimal waimaiMoney = new BigDecimal(0);// 订单外卖价
				Long visaProductId = null;
				if (hyOrder.getOrderItems() != null && hyOrder.getOrderItems().size() > 0) {
					for (HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
						hyOrderItem.setStatus(0);// 有效
						if (hyOrderItem.getHyOrderCustomers() != null
								&& hyOrderItem.getHyOrderCustomers().size() > 0) {
							for (HyOrderCustomer hyOrderCustomer : hyOrderItem.getHyOrderCustomers()) {
								hyOrderCustomer.setIsCoupon(false);
								hyOrderCustomer.setIsInsurance(false);
								hyOrderCustomer.setOrderItem(hyOrderItem);
								hyOrderCustomer.setAge(DateUtil.getAgeByBirthday(hyOrderCustomer.getBirthday()));
								hyOrderCustomer.setSettlementPrice(BigDecimal.ZERO);
								hyOrderCustomer.setSalePrice(BigDecimal.ZERO);
							}
						}
						if(visaProductId==null){
							visaProductId = hyOrderItem.getProductId();
						}
						BigDecimal settlePrice = hyOrderItem.getJiesuanPrice();
						BigDecimal salePrice = hyOrderItem.getWaimaiPrice();
						BigDecimal number = new BigDecimal(hyOrderItem.getNumber());
						jiusuanMoney = jiusuanMoney.add(settlePrice.multiply(number));
						waimaiMoney = waimaiMoney.add(salePrice.multiply(number));
						hyOrderItem.setStartDate(hyOrder.getFatuandate());
						hyOrderItem.setNumberOfReturn(0);
						hyOrderItem.setType(7);					
						hyOrderItem.setOrder(hyOrder);
						hyOrderItem.setNumberOfReturn(0);
						//人数加1
						people ++;
					}
				}
				hyOrder.setPeople(people);
				hyOrder.setRemark("无");
				hyOrder.setAdjustMoney(adjustMoney);
				hyOrder.setJiesuanMoney1(jiusuanMoney);
				hyOrder.setJiusuanMoney(jiusuanMoney);
				hyOrder.setWaimaiMoney(waimaiMoney);
				if(visaProductId!=null){
					HyVisa visa = hyVisaService.find(visaProductId);
					if(visa!=null){
						//计算扣点
						//找供应商合同
						hyOrder.setSupplier(visa.getCreator());
						HySupplierContract hySupplierContract = hySupplierContractService.getByLiable(visa.getCreator());
						if(hySupplierContract==null)
							throw new Exception("找不到供应商合同！");
						HySupplierDeductQianzheng hySupplierDeductQianzheng = hySupplierContract.getHySupplierDeductQianzheng();
						if(hySupplierDeductQianzheng==null)
							throw new Exception("供应商合同中签证折扣为空");
						hyOrder.setKoudianMethod(hySupplierDeductQianzheng.getDeductQianzheng().ordinal());	//扣点方式
						if(hyOrder.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
							//流水扣点
							hyOrder.setProportion(hySupplierDeductQianzheng.getLiushuiQianzheng());
							hyOrder.setKoudianMoney(hyOrder.getJiusuanMoney().multiply(
									hyOrder.getProportion().multiply(BigDecimal.valueOf(0.01))));
						}else{
							//人头扣点
							hyOrder.setHeadProportion(hySupplierDeductQianzheng.getRentouQianzheng());
							hyOrder.setKoudianMoney(hyOrder.getHeadProportion().multiply(
									BigDecimal.valueOf(hyOrder.getPeople())));
						}
						
						//优惠待处理
						//当前优惠判断是，如果有优惠的话，当前产品的promotionId不为空，直接通过这个计算，优惠金额
						//如果没有优惠或者优惠过期，则promotionId为null
						if(visa.getHyPromotionActivity()!=null){
							HyPromotionActivity promotionActivity = visa.getHyPromotionActivity();
							if(promotionActivity.getState()==1 && promotionActivity.getActivityType()==4){
								//当优惠状态为通过（正常）且优惠类型为签证时
								hyOrder.setDiscountedId(promotionActivity.getId());
								int promotionType = promotionActivity.getPromotionType();
								hyOrder.setDiscountedType(promotionType);
								//0每单满减，1每单打折，2每人减,3无促销
								BigDecimal discountPrice = BigDecimal.ZERO;
								if(promotionType==0){
									if(jiusuanMoney.compareTo(promotionActivity.getManjianPrice1())>0){
										//满足满减条件
										discountPrice = promotionActivity.getManjianPrice2();
									}
								}
								if(promotionType==1){
									discountPrice = promotionActivity.getDazhe().multiply(jiusuanMoney);
								}
								if(promotionType==2){
									discountPrice = promotionActivity.getMeirenjian().multiply(new BigDecimal(people));
								}
								hyOrder.setDiscountedPrice(discountPrice);	//优惠金额为0
							}
							
						}else{
							hyOrder.setDiscountedType(3);	//无优惠
							hyOrder.setDiscountedId(null);	//无优惠
							hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
						}		
					}			
					
				}			
				hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价
				hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价
				hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价
				hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价
				hyOrder.setIfjiesuan(false);// 未结算
				hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险
				hyOrder.setJiesuantime(null);	//没有结算
				this.save(hyOrder);	
				
				
				json.setSuccess(true);
				json.setMsg("下单成功");
				json.setObj(hyOrder);
				//订单日志,插一条记录到hy_order_application
				HyOrderApplication hyOrderApplication=new HyOrderApplication();
				hyOrderApplication.setOperator(hyAdmin);
				hyOrderApplication.setCreatetime(new Date());
				hyOrderApplication.setStatus(1); //通过
				hyOrderApplication.setContent("门店下订单");
				hyOrderApplication.setOrderId(hyOrder.getId());
				hyOrderApplication.setOrderNumber(hyOrder.getOrderNumber());
				hyOrderApplication.setType(8); //8-门店下订单
				hyOrderApplicationService.save(hyOrderApplication);
			}                                                                                                                                    
		}
		return json;
	}

	@Override
	public Boolean cancelVisaOrder(Long id) throws Exception {
		HyOrder order = this.find(id);
		if (order == null) {
			throw new Exception("订单不存在");
		}
		if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
			// 如果订单已支付
			throw new Exception("订单状态已支付，无法取消");
		}
		if (order.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
			throw new Exception("订单已经取消，不能重复取消");
		}
		
		// 设置订单状态为已取消
		order.setStatus(Constants.HY_ORDER_STATUS_CANCELED);
		
		update(order);
		return true;
	}

	@Resource(name="hyTicketRefundServiceImpl")
	HyTicketRefundService hyTicketRefundService;
	@Override
	public BigDecimal getTicketRefundPercentage(HyOrder order) throws Exception {
		// TODO Auto-generated method stub
		BigDecimal ans = BigDecimal.valueOf(100);
		if(order.getType()==3) {	//如果是酒店订单
			List<HyOrderItem> items = order.getOrderItems();
			if(items==null || items.isEmpty()) {
				throw new Exception("没有有效订单条目");
			}
			HyOrderItem item = items.get(0);
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(item.getProductId());
			if(hyTicketHotel==null) {
				throw new Exception("没有有效酒店");
			}
			
			if(hyTicketHotel.getRefundType().equals(HyTicketHotel.RefundTypeEnum.quane)) {
				ans = BigDecimal.valueOf(100);
			}else if(hyTicketHotel.getRefundType().equals(HyTicketHotel.RefundTypeEnum.jieti)){
				
				List<Filter> ticketRefundFilters = new ArrayList<>();
				ticketRefundFilters.add(Filter.eq("productId", hyTicketHotel.getId()));
				ticketRefundFilters.add(Filter.eq("type", 1));
				List<Order> ticketRefundOrders = new ArrayList<>();
				ticketRefundOrders.add(Order.asc("startDay"));
				List<HyTicketRefund> hyTicketRefunds = hyTicketRefundService.findList(null,ticketRefundFilters,ticketRefundOrders);
				
				Date startDay = item.getStartDate();
				Date nowaDay = new Date();
				Long diffHours = TimeUnit.HOURS.convert(startDay.getTime() - nowaDay.getTime(), TimeUnit.MILLISECONDS);

				for (HyTicketRefund refund : hyTicketRefunds) {
					Long startHours = (refund.getStartDay() - 1) * 24L + (24 - refund.getStartTime());
					Long endHours = (refund.getEndDay() - 1) * 24L + (24 - refund.getEndTime());
					if (diffHours > startHours && diffHours <= endHours) {
						return refund.getPercentage();
					}
				}
				
			}
			
		}
		
		//如果是酒加景订单
		else if(order.getType()==5) {
			//如果是酒加景订单
			List<HyOrderItem> items = order.getOrderItems();
			if(items==null || items.isEmpty()) {
				throw new Exception("没有有效订单条目");
			}
			HyOrderItem item = items.get(0);
			HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(item.getProductId());
			if(hyTicketHotelandscene==null) {
				throw new Exception("没有有效的酒加景产品");
			}
			
			if(hyTicketHotelandscene.getRefundType().equals(HyTicketHotelandscene.RefundTypeEnum.quane)) {
				ans = BigDecimal.valueOf(100);
			}else if(hyTicketHotelandscene.getRefundType().equals(HyTicketHotelandscene.RefundTypeEnum.jieti)){
				
				List<Filter> ticketRefundFilters = new ArrayList<>();
				ticketRefundFilters.add(Filter.eq("productId", hyTicketHotelandscene.getId()));
				ticketRefundFilters.add(Filter.eq("type", 2)); //2-酒加景
				List<Order> ticketRefundOrders = new ArrayList<>();
				ticketRefundOrders.add(Order.asc("startDay"));
				List<HyTicketRefund> hyTicketRefunds = hyTicketRefundService.findList(null,ticketRefundFilters,ticketRefundOrders);
				
				Date startDay = item.getStartDate();
				Date nowaDay = new Date();
				Long diffHours = TimeUnit.HOURS.convert(startDay.getTime() - nowaDay.getTime(), TimeUnit.MILLISECONDS);

				for (HyTicketRefund refund : hyTicketRefunds) {
					Long startHours = (refund.getStartDay() - 1) * 24L + (24 - refund.getStartTime());
					Long endHours = (refund.getEndDay() - 1) * 24L + (24 - refund.getEndTime());
					if (diffHours > startHours && diffHours <= endHours) {
						return refund.getPercentage();
					}
				}
				
			}
		}
		return ans;
	}
	
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;

	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;

	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	HyTicketInboundService hyTicketInboundService;
	
	@Override
	public Boolean cancelOrderAfterPay(Long id,BigDecimal orderMoney) throws Exception {
		
		HyOrder hyOrder = find(id);
		if (hyOrder == null) {
			throw new Exception("订单不存在");
		}

		if (hyOrder.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
			throw new Exception("订单已经取消，不能重复取消");
		}
		

		// TODO Auto-generated method stub
		// 1.直接生成退款记录无需出纳操作
		// 退款信息表
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setState(1); // 直接生成已付款
		refundInfo.setType(3); // 3:供应商驳回订单
		refundInfo.setApplyDate(new Date());
		refundInfo.setAppliName(null);
		refundInfo.setAmount(orderMoney);
		// refundInfo.setRemark(remark);
		refundInfo.setPayDate(new Date());
		refundInfo.setOrderId(hyOrder.getId());
		refundInfoService.save(refundInfo);

		// 退款记录表
		RefundRecords refundRecords = new RefundRecords();
		refundRecords.setRefundInfoId(refundInfo.getId());
		refundRecords.setOrderCode(hyOrder.getOrderNumber());
		refundRecords.setSignUpMethod(1); // 报名方式:门店
		refundRecords.setAmount(orderMoney);
		refundRecords.setRefundMethod(1L); // 1:预存款 2:支付宝 3:微信支付
		refundRecords.setPayDate(new Date());
		refundRecords.setPayer(null);

		Store store = storeService.find(hyOrder.getStoreId());
		refundRecords.setStoreName(store.getStoreName());
		refundRecords.setStoreId(hyOrder.getStoreId());
		refundRecordsService.save(refundRecords);

		// 2.修改门店的预存款余额
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("store", store));
		List<StoreAccount> list = storeAccountService.findList(null, filters, null);
		StoreAccount storeAccount = list.get(0);
		storeAccount.setBalance(storeAccount.getBalance().add(orderMoney));

		// 3.门店 - 预存款修改记录
		StoreAccountLog storeAccountLog = new StoreAccountLog();
		storeAccountLog.setStore(store);
		storeAccountLog.setType(5); // 类型,0充值，1订单抵扣，2分成，3退团，4消团， 5供应商驳回订单
		storeAccountLog.setStatus(1); // 1 通过
		storeAccountLog.setMoney(orderMoney);
		storeAccountLog.setOrderSn(hyOrder.getOrderNumber()); // 订单编号
		storeAccountLog.setCreateDate(new Date());
		storeAccountLogService.save(storeAccountLog);

		// 4.财务中心-门店预存款记录修改
		StorePreSave storePreSave = new StorePreSave();
		storePreSave.setStoreId(store.getId());
		storePreSave.setStoreName(store.getStoreName());
		storePreSave.setType(13); //// 1:门店充值 2:报名退款 3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款
									//// 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款
									//// 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返
									//// 13:供应商驳回订单 14:
		storePreSave.setDate(new Date());
		storePreSave.setAmount(orderMoney);
		storePreSave.setPreSaveBalance(storeAccount.getBalance().add(orderMoney));
		storePreSave.setOrderId(hyOrder.getId());
		storePreSave.setOrderCode(hyOrder.getOrderNumber());
		storePreSaveService.save(storePreSave);

		// 5.修改订单状态
		hyOrder.setStatus(5);//订单状态0待门店支付，1待门店确认，2待供应商确认，3供应商通过，4驳回待财务确认,5已驳回,6已取消
		this.update(hyOrder);


		// 6. 写退款记录表
		PayandrefundRecord record = new PayandrefundRecord();
		record.setOrderId(hyOrder.getId());
		record.setMoney(orderMoney);
		record.setPayMethod(5);	//5预存款
		record.setType(1);	//1退款
		record.setStatus(1);	//1已退款
		record.setCreatetime(new Date());
		payandrefundRecordService.save(record);

		
		// 7.修改库存和报名人数
		Integer type=hyOrder.getType();
		//如果是线路订单
		if(type==1) {
			Long groupId = hyOrder.getGroupId();
			HyGroup hyGroup = hyGroupService.find(groupId);
			synchronized(hyGroup){
				hyGroup.setStock(hyGroup.getStock() + hyOrder.getPeople());
				hyGroup.setSignupNumber(hyGroup.getSignupNumber() - hyOrder.getPeople());
				hyGroupService.update(hyGroup);
			}
			
			//added by GSbing,20190301,修改计调报账人数
			List<Filter> hyRegulateFilter = new ArrayList<>();
			hyRegulateFilter.add(Filter.eq("hyGroup", hyGroup.getId()));
			List<HyRegulate> hyRegulates = hyRegulateService.findList(null, hyRegulateFilter, null);
			if(hyRegulates.size() != 0) {
				HyRegulate hyRegulate = hyRegulates.get(0);
				synchronized(hyRegulate){
					hyRegulate.setVisitorNum(hyGroup.getSignupNumber());
					hyRegulateService.update(hyRegulate);
				}			
			}
		}		
		if(hyOrder.getType()==3) {	//如果是票务的酒店订单
			//恢复库存
			hyTicketInboundService.recoverTicketInboundByTicketOrder(hyOrder);
		}	
		//如果是票务酒加景订单
		else if(type==5) {
			//修改库存
			HyOrderItem orderItem=hyOrder.getOrderItems().get(0);
			Long priceId=orderItem.getPriceId();
			List<Filter> inboundfilter=new ArrayList<>();
			inboundfilter.add(Filter.eq("type", 1)); 
			inboundfilter.add(Filter.eq("priceInboundId", priceId));
			inboundfilter.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundfilter,null);
		    HyTicketInbound hyTicketInbound=ticketInbounds.get(0);
			hyTicketInbound.setInventory(hyTicketInbound.getInventory()+orderItem.getNumber());
			hyTicketInboundService.update(hyTicketInbound);
		}
		//如果是门票的订单
		else if(type == 4) {
			//也是修改库存
			HyOrderItem orderItem=hyOrder.getOrderItems().get(0);
			Long priceId=orderItem.getPriceId();
			List<Filter> inboundfilter=new ArrayList<>();
			inboundfilter.add(Filter.eq("type", 1)); 
			inboundfilter.add(Filter.eq("priceInboundId", priceId));
			inboundfilter.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundfilter,null);
		    HyTicketInbound hyTicketInbound=ticketInbounds.get(0);
			hyTicketInbound.setInventory(hyTicketInbound.getInventory()+orderItem.getNumber());
			hyTicketInboundService.update(hyTicketInbound);
		}
		
		hyOrder.setStatus(Constants.HY_ORDER_STATUS_REJECTED);
		hyOrder.setRefundstatus(2);	//全部已退款
		this.update(hyOrder);
		
		return true;
	}
	
}
