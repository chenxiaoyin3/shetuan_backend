package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.omg.PortableServer.ThreadPolicyOperations;
import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.ConfirmMessage;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.InsuranceTime;
import com.hongyu.entity.InsureInfo;
import com.hongyu.entity.JtCancelOrderResponse;
import com.hongyu.entity.JtOrderResponse;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPolicyHolderInfoService;
import com.hongyu.service.HyVisaPicService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsurancePriceService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.InsuranceTimeService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.JiangtaiUtil;
import com.hongyu.util.XStreamUtil;
import com.hongyu.util.liyang.XStreamUtil2;


@Service("insuranceOrderServiceImpl")
public class InsuranceOrderServiceImpl extends BaseServiceImpl<InsuranceOrder, Long> implements InsuranceOrderService {
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name = "hyPolicyHolderInfoServiceImpl")
	HyPolicyHolderInfoService hyPolicyHolderInfoService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "hyVisaPicServiceImpl")
	HyVisaPicService hyVisaPicService;
	
	@Resource(name = "insurancePriceServiceImpl")
	InsurancePriceService insurancePriceService;
	
	@Resource(name = "insuranceTimeServiceImpl")
	InsuranceTimeService insuranceTimeService;

	//	@Override
//	public void generate(HyOrder hyOrder, HttpSession session) throws Exception {
//		// TODO Auto-generated method stub
//		if(hyOrder.getOrderItems()!=null&&hyOrder.getOrderItems().size()>0){
//			for(HyOrderItem hyOrderItem:hyOrder.getOrderItems()){
//				if(hyOrderItem.getHyOrderCustomers()!=null&&hyOrderItem.getHyOrderCustomers().size()>0){
//					HyGroup hyGroup=hyGroupService.find(hyOrderItem.getProductId());
//					Insurance insurance=hyGroup.getLine().getInsurance();
//					for(HyOrderCustomer hyOrderCustomer:hyOrderItem.getHyOrderCustomers()){
//						InsuranceOrder insuranceOrder=new InsuranceOrder();
//						
//						insuranceOrder.setOrderId(hyOrder.getId());
//						insuranceOrder.setGroupId(hyOrderItem.getProductId());
//						insuranceOrder.setName(hyOrderCustomer.getName());
//						insuranceOrder.setSex(hyOrderCustomer.getGender());
//						//（虹宇）证件类型0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证
//						//（江泰）证件类型1:身份证   2:军官证  3:护照  4:其他
//						/*江泰的身份证定义为1，而我们自己定义的身份证为0*/
//						insuranceOrder.setCertificate(JiangtaiUtil.CertificateTypeSwitch(hyOrderCustomer.getCertificateType()));
//						insuranceOrder.setCertificateNumber(hyOrderCustomer.getCertificate());
//						insuranceOrder.setAge(hyOrderCustomer.getAge());
//						insuranceOrder.setBirthday(hyOrderCustomer.getBirthday());
//						insuranceOrder.setInsuranceStarttime(hyOrderItem.getStartDate());
//						insuranceOrder.setInsuranceEndtime(hyOrderItem.getEndDate());
//						insuranceOrder.setType(0);
//						insuranceOrder.setStatus(0);
//						if(hyOrderCustomer.getIsInsurance()!=null&&hyOrderCustomer.getIsInsurance()==false){
//							if(insurance==null){
//								continue;
//							}
//							insuranceOrder.setInsuranceId(insurance.getId());
//							Integer days=hyGroup.getLine().getDays();
//							for(InsurancePrice insurancePrice:insurance.getInsurancePrices()){
//								if(days>=insurancePrice.getStartDay()&&days<=insurancePrice.getEndDay()){
//									insuranceOrder.setReceivedMoney(insurancePrice.getSalePrice());
//									insuranceOrder.setShifuMoney(insurancePrice.getSettlementPrice());
//								}
//							}
//							if(insuranceOrder.getReceivedMoney()==null||insuranceOrder.getShifuMoney()==null){
//								insuranceOrder.setReceivedMoney(new BigDecimal(0));
//								insuranceOrder.setShifuMoney(new BigDecimal(0));
//							}
//						}else{
//							insuranceOrder.setInsuranceId(hyOrderCustomer.getInsuranceId());
//							insuranceOrder.setReceivedMoney(hyOrderCustomer.getSalePrice());
//							insuranceOrder.setShifuMoney(hyOrderCustomer.getSettlementPrice());
//							
//						}
//						insuranceOrder.setProfit(insuranceOrder.getReceivedMoney().subtract(insuranceOrder.getShifuMoney()));
//						this.save(insuranceOrder);
//					}
//				}
//			}
//		}
//	}
	@Deprecated
	@Override
	public void generate(HyOrder hyOrder, HttpSession session) throws Exception {
		// TODO Auto-generated method stub
		InsuranceOrder insuranceOrder=new InsuranceOrder();
		insuranceOrder.setOrderId(hyOrder.getId());
		insuranceOrder.setGroupId(hyOrder.getGroupId());
		//设置保单类型为团期投保
		insuranceOrder.setType(0);
		//设置状态为未确认
		insuranceOrder.setStatus(0);
		List<HyPolicyHolderInfo> policyHolders = new ArrayList<>();
		if(hyOrder.getOrderItems()!=null&&hyOrder.getOrderItems().size()>0){
			for(HyOrderItem hyOrderItem:hyOrder.getOrderItems()){
				if(hyOrderItem.getHyOrderCustomers()!=null&&hyOrderItem.getHyOrderCustomers().size()>0){
					HyGroup hyGroup=hyGroupService.find(hyOrderItem.getProductId());
					Insurance insurance=hyGroup.getLine().getInsurance();
					
					for(HyOrderCustomer hyOrderCustomer:hyOrderItem.getHyOrderCustomers()){	
						HyPolicyHolderInfo policyHolderInfo = new HyPolicyHolderInfo();
						policyHolderInfo.setName(hyOrderCustomer.getName());
						policyHolderInfo.setSex(hyOrderCustomer.getGender());
						//（虹宇）证件类型0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证
						//（江泰）证件类型1:身份证   2:军官证  3:护照  4:其他
						/*江泰的身份证定义为1，而我们自己定义的身份证为0*/
						int certificateType = JiangtaiUtil.CertificateTypeSwitch(hyOrderCustomer.getCertificateType());
						policyHolderInfo.setCertificate(certificateType);
						if(certificateType==4){
							//如果当前证件类型是其他的话，默认将生日作为证件号码（主要针对儿童）
							policyHolderInfo.setCertificateNumber(DateUtil.getBirthday(hyOrderCustomer.getBirthday()));
						}else{
							policyHolderInfo.setCertificateNumber(hyOrderCustomer.getCertificate());
						}
						policyHolderInfo.setAge(hyOrderCustomer.getAge());
						policyHolderInfo.setBirthday(hyOrderCustomer.getBirthday());
						policyHolderInfo.setInsuranceOrder(insuranceOrder);
						policyHolders.add(policyHolderInfo);
						if(insuranceOrder.getInsuranceStarttime()==null || insuranceOrder.getInsuranceEndtime()==null){
							insuranceOrder.setInsuranceStarttime(DateUtil.getStartOfDay(hyOrderItem.getStartDate()));
							insuranceOrder.setInsuranceEndtime(DateUtil.getEndOfDay(hyOrderItem.getEndDate()));
							
						}							
						if(hyOrderCustomer.getIsInsurance()!=null&&hyOrderCustomer.getIsInsurance()==false){
							//如果游客没有单独选择保险，就按线路自带保险购买
							if(insurance==null){
								continue;
							}
							if(insuranceOrder.getInsuranceId()==null){
								insuranceOrder.setInsuranceId(insurance.getId());
								Integer days=hyGroup.getLine().getDays();
								for(InsurancePrice insurancePrice:insurance.getInsurancePrices()){
									if(days>=insurancePrice.getStartDay()&&days<=insurancePrice.getEndDay()){
										insuranceOrder.setReceivedMoney(insurancePrice.getSalePrice());
										insuranceOrder.setShifuMoney(insurancePrice.getSettlementPrice());
									}
								}
								if(insuranceOrder.getReceivedMoney()==null||insuranceOrder.getShifuMoney()==null){
									insuranceOrder.setReceivedMoney(new BigDecimal(0));
									insuranceOrder.setShifuMoney(new BigDecimal(0));
								}
							}	
						}else{
							//游客自选保险就按自选保险走
							if(insuranceOrder.getInsuranceId()==null){
								insuranceOrder.setInsuranceId(hyOrderCustomer.getInsuranceId());
								insuranceOrder.setReceivedMoney(hyOrderCustomer.getSalePrice());
								insuranceOrder.setShifuMoney(hyOrderCustomer.getSettlementPrice());
							}
							
						}
					}
				}
			}
		}
		if(insuranceOrder.getInsuranceId()==null){
			return;
		}
		//设置本单的实收款总额
		BigDecimal shishouMoney = insuranceOrder.getReceivedMoney().multiply(new BigDecimal(policyHolders.size()));
		insuranceOrder.setReceivedMoney(shishouMoney);
		//设置实付款总额
		BigDecimal shifuMoney = insuranceOrder.getShifuMoney().multiply(new BigDecimal(policyHolders.size()));
		insuranceOrder.setShifuMoney(shifuMoney);
		//设置本单利润
		insuranceOrder.setProfit(insuranceOrder.getReceivedMoney().subtract(insuranceOrder.getShifuMoney()));
		insuranceOrder.setPolicyHolders(policyHolders);
		this.save(insuranceOrder);
	}
	/**
	 * 根据当前订单生成保险订单（切换为投保时才生成保险订单）
	 * 是为了避免从订单下单到发团期间订单发生变化从而要维护保险订单的状态
	 * 2019年3月1日
	 * liyang
	 */
	@Override
	public void generate(Long orderId) throws Exception {
		HyOrder hyOrder = hyOrderService.find(orderId);
		InsuranceOrder insuranceOrder=new InsuranceOrder();
		insuranceOrder.setOrderId(hyOrder.getId());
		insuranceOrder.setGroupId(hyOrder.getGroupId());
		//设置保单类型为团期投保
		insuranceOrder.setType(0);
		//设置状态为未投保
		insuranceOrder.setStatus(1);
		List<HyPolicyHolderInfo> policyHolders = new ArrayList<>();
		
		/**首先确定保险id*/
		if(hyOrder.getOrderItems()==null || hyOrder.getOrderItems().isEmpty()){
			return;
		}
		/**
		 * 判断游客是否自选保险
		 * 1. 游客自选保险---按游客自选算
		 * 2. 游客没有自选保险，团自带保险---按团保险算
		 * 3. 游客没有自选保险，团也没有保险--不计算投保 
		 */
		Insurance customerInsurance = null;
		Insurance lineInsurance = null;
		Insurance finalInsurance = null;
		//找到第一个包含游客的item
		for(HyOrderItem item:hyOrder.getOrderItems()){
			if(item!=null && item.getHyOrderCustomers()!=null && !item.getHyOrderCustomers().isEmpty()){
				//判断当前这个订单是否游客自选保险，因为自选保险每个游客都一样，所以取第一个即可
				HyOrderCustomer customer = item.getHyOrderCustomers().get(0);
				if(customer.getInsuranceId()!=null){
					customerInsurance = insuranceService.find(customer.getInsuranceId());
					break;
				}
			}
		}
		
		//游客没有自选保险，再判断团是否自带保险
		HyGroup hyGroup=hyGroupService.find(hyOrder.getGroupId());
		Insurance insurance=hyGroup.getLine().getInsurance();
		if(insurance!=null){
			lineInsurance = insurance;
		}
		
		//逻辑计算finalInsurance
		if(customerInsurance!=null){
			finalInsurance = customerInsurance;
		}else{
			if(lineInsurance!=null){
				finalInsurance = lineInsurance;
			}else{
				//此订单不需要投保
				return;
			}
		}
		
		insuranceOrder.setInsuranceId(finalInsurance.getId());
		Integer days = hyGroup.getLine().getDays();
		List<InsurancePrice> prices = finalInsurance.getInsurancePrices();
		List<InsuranceTime> times = finalInsurance.getInsuranceTimes();
		
		BigDecimal receivedMoney = BigDecimal.ZERO;
		BigDecimal shifuMoney = BigDecimal.ZERO;
		
		if(hyOrder.getOrderItems()!=null&&hyOrder.getOrderItems().size()>0){
			for(HyOrderItem hyOrderItem:hyOrder.getOrderItems()){
				if(hyOrderItem.getStatus().equals(1))
					continue;
				if(hyOrderItem.getHyOrderCustomers()!=null&&hyOrderItem.getHyOrderCustomers().size()>0){
						
					/*针对不同的游客年龄计算不同的价格*/
					for(HyOrderCustomer hyOrderCustomer:hyOrderItem.getHyOrderCustomers()){	
						HyPolicyHolderInfo policyHolderInfo = new HyPolicyHolderInfo();
						policyHolderInfo.setName(hyOrderCustomer.getName());
						policyHolderInfo.setSex(hyOrderCustomer.getGender());
						//（虹宇）证件类型0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证
						//（江泰）证件类型1:身份证   2:军官证  3:护照  4:其他
						/*江泰的身份证定义为1，而我们自己定义的身份证为0*/
						int certificateType = JiangtaiUtil.CertificateTypeSwitch(hyOrderCustomer.getCertificateType());
						policyHolderInfo.setCertificate(certificateType);
						if(certificateType==4){
							//如果当前证件类型是其他的话，默认将生日作为证件号码（主要针对儿童）
							policyHolderInfo.setCertificateNumber(DateUtil.getBirthday(hyOrderCustomer.getBirthday()));
						}else{
							policyHolderInfo.setCertificateNumber(hyOrderCustomer.getCertificate());
						}
						policyHolderInfo.setAge(hyOrderCustomer.getAge());
						policyHolderInfo.setBirthday(hyOrderCustomer.getBirthday());
						policyHolderInfo.setInsuranceOrder(insuranceOrder);
						policyHolders.add(policyHolderInfo);
						if(insuranceOrder.getInsuranceStarttime()==null || insuranceOrder.getInsuranceEndtime()==null){
							insuranceOrder.setInsuranceStarttime(DateUtil.getStartOfDay(hyOrderItem.getStartDate()));
							insuranceOrder.setInsuranceEndtime(DateUtil.getEndOfDay(hyOrderItem.getEndDate()));		
						}
						
						/**计算单人保险总价*/
						
						BigDecimal shishou = BigDecimal.ZERO;
						BigDecimal shifu = BigDecimal.ZERO;
						
						BigDecimal maxSalePrice = BigDecimal.ZERO;
						BigDecimal maxSettlePrice = BigDecimal.ZERO;
								
						boolean ifBeyond = true;
						for(InsurancePrice insurancePrice:prices){
							if(days>=insurancePrice.getStartDay()&&days<=insurancePrice.getEndDay()){
								shishou = insurancePrice.getSalePrice();
								shifu = insurancePrice.getSettlementPrice();
								ifBeyond = false;
								break;
							}
							if(maxSalePrice.compareTo(insurancePrice.getSalePrice())<0){
								maxSalePrice = insurancePrice.getSalePrice();
								maxSettlePrice = insurancePrice.getSettlementPrice();
							}
						}
						if(ifBeyond){
							//如果超出界限,按最低价格算
							shishou = shishou.add(maxSalePrice);
							shifu = shifu.add(maxSettlePrice);
						}
							
						Integer age = hyOrderCustomer.getAge();
						for(InsuranceTime time:times){
							if(age>=time.getAgeStart() && age<=time.getAgeEnd()){
								shishou = shishou.multiply(time.getTimes());
								shifu = shifu.multiply(time.getTimes());
							}
						}
						
						receivedMoney = receivedMoney.add(shishou);
						shifuMoney = shifuMoney.add(shifu);
					}
				}
			}
		}
			
		if(insuranceOrder.getInsuranceId()==null){
			return;
		}
		//设置本单的实收款总额
		insuranceOrder.setReceivedMoney(receivedMoney);
		//设置实付款总额
		insuranceOrder.setShifuMoney(shifuMoney);
		//设置本单利润
		insuranceOrder.setProfit(insuranceOrder.getReceivedMoney().subtract(insuranceOrder.getShifuMoney()));
		insuranceOrder.setPolicyHolders(policyHolders);
		this.save(insuranceOrder);
		
	}

	@Override
	public void generateStoreInsuranceOrder(HyOrder hyOrder, HttpSession session) throws Exception {
		// TODO Auto-generated method stub
		InsuranceOrder insuranceOrder=new InsuranceOrder();
		insuranceOrder.setOrderId(hyOrder.getId());
		//设置保单类型为自主投保
		insuranceOrder.setType(1);
		//设置状态为未确认
		
		//System.out.println(insuranceOrder.getStatus());
		Insurance insurance=hyOrder.getInsurance();
		insuranceOrder.setInsuranceId(insurance.getId());
		insuranceOrder.setReceivedMoney(hyOrder.getWaimaiMoney());
		insuranceOrder.setShifuMoney(hyOrder.getJiusuanMoney());
		
		List<HyPolicyHolderInfo> policyHolders = new ArrayList<>();
		if(hyOrder.getOrderItems()!=null&&hyOrder.getOrderItems().size()>0){
			for(HyOrderItem hyOrderItem:hyOrder.getOrderItems()){
				//只有有效的，才下单
				if(hyOrderItem.getStatus().equals(0)) {
					if(hyOrderItem.getHyOrderCustomers()!=null&&hyOrderItem.getHyOrderCustomers().size()>0){
						for(HyOrderCustomer hyOrderCustomer:hyOrderItem.getHyOrderCustomers()){	
							HyPolicyHolderInfo policyHolderInfo = new HyPolicyHolderInfo();
							policyHolderInfo.setName(hyOrderCustomer.getName());
							policyHolderInfo.setSex(hyOrderCustomer.getGender());
							//（虹宇）证件类型0：身份证          1：护照        2：港澳台通行证 3：士兵证       4：回乡证
							//（江泰）证件类型1:身份证   2:军官证  3:护照  4:其他
							/*江泰的身份证定义为1，而我们自己定义的身份证为0*/
							int certificateType = JiangtaiUtil.CertificateTypeSwitch(hyOrderCustomer.getCertificateType());
							policyHolderInfo.setCertificate(certificateType);
							if(certificateType==4){
								//如果当前证件类型是其他的话，默认将生日作为证件号码（主要针对儿童）
								policyHolderInfo.setCertificateNumber(DateUtil.getBirthday(hyOrderCustomer.getBirthday()));
							}else{
								policyHolderInfo.setCertificateNumber(hyOrderCustomer.getCertificate());
							}
							policyHolderInfo.setAge(hyOrderCustomer.getAge());
							policyHolderInfo.setBirthday(hyOrderCustomer.getBirthday());
							policyHolderInfo.setInsuranceOrder(insuranceOrder);
							policyHolders.add(policyHolderInfo);
							if(insuranceOrder.getInsuranceStarttime()==null || insuranceOrder.getInsuranceEndtime()==null){
								Calendar cal1 = Calendar.getInstance();
								Calendar cal2 = Calendar.getInstance();
								//保险开始时间
								cal1.setTime(hyOrderItem.getStartDate());
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
								
								if(cal1.getTime().equals(cal2.getTime())) {
									//下保险的起始日期是当天
									Calendar calendar = Calendar.getInstance();
									calendar.setTime(new Date());
									//加上两小时
									calendar.add(Calendar.HOUR_OF_DAY, 2);
									insuranceOrder.setInsuranceStarttime(calendar.getTime());
								}
								else {
									//下保险的起始日期不是当天,那就设置为开始那天的1点0分
									cal1.set(Calendar.HOUR_OF_DAY, 1);
									insuranceOrder.setInsuranceStarttime(cal1.getTime());
								}
								
								
								insuranceOrder.setInsuranceEndtime(hyOrderItem.getEndDate());
							}							
							
								//游客自选保险就按自选保险走
							//insuranceOrder.setInsuranceId(hyOrderCustomer.getInsuranceId());
							
	
								
						}
					}
				}
			}
		}
		if(insuranceOrder.getInsuranceId()==null){
			return;
		}
		//设置本单利润
		insuranceOrder.setProfit(insuranceOrder.getReceivedMoney().subtract(insuranceOrder.getShifuMoney()));
		insuranceOrder.setPolicyHolders(policyHolders);
		insuranceOrder.setStatus(1);
		this.save(insuranceOrder);
		//
		//this.update(insuranceOrder);
		//System.out.println(insuranceOrder.getStatus());
	}
	
	@Resource(name="insuranceOrderDaoImpl")
	@Override
	public void setBaseDao(BaseDao<InsuranceOrder, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

//	@Override
//	public Json postOrderToJT(Long[] orderIds) throws Exception {
//			//将那些投保失败的订单再依次投保一遍，每个订单应该中包含有好几个被保人
//			//1、如果投保类型是团投，就是加入了旅游团，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
//			//2、如果投保类型是自主投保，那就是他去门店自己买保险。
//			Json json = new Json();
//			long startTime = System.currentTimeMillis();
//			Map<String,Object> map = new HashMap<>();
//			List<Map<String,Object>> successIds = new ArrayList<>();
//			List<Map<String,Object>> failIds = new ArrayList<>();
//			for(int i = 0; i<orderIds.length; i++ ){
//				//获取想要投保订单id
//				Long orderId = orderIds[i];
//				//根据该订单id去获取所有的保险人对应的保单
//				List<Filter> filters = new ArrayList<>();
//				filters.add(Filter.eq("orderId", orderId));
//				List<InsuranceOrder> insuranceOrders = this.findList(null,filters,null);
//				//如果已经投保，直接跳过。
//				if(insuranceOrders.get(0).getStatus() == 3){
//					continue;
//				}
//				List<InsureInfo> insuredList = new ArrayList<>();
//				for(InsuranceOrder tmp : insuranceOrders){
//					InsureInfo info = new InsureInfo();
//					//在设置生日的时候，江泰需要确保该生日和身份证上的生日是一致的。但是从这获取的生日是顾客自己填写的
//					//所以当顾客提交的身份证件类型是身份证的时候，直接采用身份证的生日提取。	
//					info.setIdentifyNumber(tmp.getCertificateNumber());
//					info.setIdentifyType(tmp.getCertificate());
//					info.setInsuredName(tmp.getName());
//					//此处规定"0"为女性，"1"为男性
//					if(tmp.getSex() == 0){
//						info.setSex("F");
//					}else{
//						info.setSex("M");
//					}
//					if(tmp.getCertificate() == 1){
//						//如果是身份证，则直接提取生日
//						info.setBirthDay(JiangtaiUtil.getBirthdayFromCertificate(tmp.getCertificateNumber()));
//					}else{
//						info.setBirthDay(DateUtil.getBirthday(tmp.getBirthday()));
//					}			
//					insuredList.add(info);					
//				}
//				//组装confirmMessage
//				ConfirmMessage cmo  = new ConfirmMessage();
//				//confirmMessage中需要手动填写的参数有以下几个
//				HyOrder order = hyOrderService.find(orderId);
//				//这里面显示的insuranceOrders.get(0)是为了拿到符合条件的第一条数据，取得的时间和团id都是公共相同的。
//				HyGroup group = hyGroupService.find(order.getGroupId());
//				//cmo.setPayType(payType);
//				//必须确保我们的日期格式是yyyy-MM-dd HH:mm:ss
//				//系统的所有时间格式都是yyyy-MM-dd，我们设置当天的凌晨开始保险
//				cmo.setStartDate(DateUtil.getSimpleDate(insuranceOrders.get(0).getInsuranceStarttime()));
//				cmo.setEndDate(DateUtil.getSimpleDate(insuranceOrders.get(0).getInsuranceEndtime()));
//				cmo.setContactName(order.getContact());
//				cmo.setContactPhone(order.getPhone());
//				cmo.setTravelRoute(group.getGroupLineName());
//				cmo.setSumQuantity(1);
//				//设置旅游团编号为虹宇的订单编号
//				cmo.setTravelGroupNo(order.getOrderNumber());
//				//此处获取到渠道交易的时间，因为该时间取得是当前时间，所以必须保存，在投保成功之后写入数据库
//				String channelTradeDate = DateUtil.getSimpleDate(new Date());
//				cmo.setChannelTradeDate(channelTradeDate);
//				//获取投保产品号
//				Insurance  insurance = insuranceService.find(insuranceOrders.get(0).getInsuranceId());
//				cmo.setProductCode(insurance.getInsuranceCode());
//				//渠道交易流水号--直接设置"前缀+订单编号+当前时间MMddHHmmss"
//				//cmo.setChannelTradeSerialNo(order.getOrderNumber());
//				String channelTradeSerialNo = "";
//				if(insuranceOrders.get(0).getType() == 0){
//					//如果是团期投保，流水号前缀为"TQ" 意为"团期"
//					channelTradeSerialNo = "TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
//					//cmo.setChannelTradeSerialNo("TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
//				}
//				if(insuranceOrders.get(0).getType() == 1){
//					//如果是门店个人自主投保，流水号前缀"ZZ" 意为“自主”
//					channelTradeSerialNo = "ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
//					//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
//				}
//				cmo.setChannelTradeSerialNo(channelTradeSerialNo);
//				cmo.setInsuredList(insuredList);		
//				json = JiangtaiUtil.order(cmo);	
//				JtOrderResponse jtOrderResponse = (JtOrderResponse) XStreamUtil.xmlToBean(json.getObj().toString());
//				//System.out.println("实体获取到了返回信息："+jtOrderResponse.toString());
//				if("000001".equals(jtOrderResponse.getResponseCode())){
//					 //投保成功，更新insuranceOrder表
//					//此设置保单的下载地址，因为保单的对应的是订单，所以保单地址设置在订单数据表中
//					HyOrder hyOrder = hyOrderService.find(orderId);
//					hyOrder.setInsuranceOrderDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
//					//8.9日下午四点修改，将save改成update;
//					hyOrderService.update(hyOrder);
//					for(InsuranceOrder tmp : insuranceOrders){
//						//设置投保状态为  已投保。
//						tmp.setStatus(3);
//						tmp.setJtChannelTradeSerialNo(jtOrderResponse.getChannelTradeSerialNo());
//						tmp.setJtChannelTradeDate(channelTradeDate);
//						tmp.setJtOrderNo(jtOrderResponse.getJtOrderNo());
//						tmp.setJtInsureNo(jtOrderResponse.getInsureNo());
//						tmp.setJtSumPremium(jtOrderResponse.getSumPremium());
//						tmp.setJtDiscount(jtOrderResponse.getDiscount());
//						//此处设置个人保险凭证的下载地址
//						String downloadUrl = JiangtaiUtil.OrderDownCertificate(tmp); 
//						tmp.setDownloadUrl(downloadUrl);
//						//8.9日下午四点修改，将save改成update;
//						this.update(tmp);						
//					}
//					//封装数据返回给前台，将投保成功的订单id放到返回列表中。
//					Map<String, Object> tmp = new HashMap<>();
//					tmp.put("orderId",orderId);
//					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
//					successIds.add(tmp);
//				} else{
//					//如果投保失败就将失败的id返回
//					Map<String, Object> tmp = new HashMap<>();
//					tmp.put("orderId",orderId);
//					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
//					failIds.add(tmp);
//				}
//			}
//			map.put("successIds", successIds);
//			map.put("failIds", failIds);
//			long endTime = System.currentTimeMillis();
//			json.setMsg("以下订单投保成功,总耗时 "+(endTime-startTime)+"ms");
//			json.setObj(map);;
//			
//			return json;		
//	}

	@Override
	public Json postOrderToJT(Long[] orderIds) throws Exception {
			//将那些投保失败的订单再依次投保一遍，每个订单应该中包含有好几个被保人
			//1、如果投保类型是团投，就是加入了旅游团，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
			//2、如果投保类型是自主投保，那就是他去门店自己买保险。
			Json json = new Json();
			long startTime = System.currentTimeMillis();
			Map<String,Object> map = new HashMap<>();
			List<Map<String,Object>> successIds = new ArrayList<>();
			List<Map<String,Object>> failIds = new ArrayList<>();
			for(int i = 0; i<orderIds.length; i++ ){
				//获取想要投保订单id
				Long orderId = orderIds[i];
				HyOrder order = hyOrderService.find(orderId);
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", orderId));
				if(order == null) {
					continue;
				}
				if(order.getRefundstatus() != null && order.getRefundstatus() == 3) {
					//过滤掉已撤保的
					filters.add(Filter.ne("status", 4));
				}
				List<InsuranceOrder> insuranceOrders = this.findList(null,filters,null);
				//如果已经投保，直接跳过。
				if(insuranceOrders.get(0).getStatus() == 3){
					continue;
				}
				InsuranceOrder insuranceOrder = insuranceOrders.get(0);
				List<InsureInfo> insuredList = new ArrayList<>();
				for(HyPolicyHolderInfo tmp:insuranceOrder.getPolicyHolders()){
					InsureInfo info = new InsureInfo();
					//在设置生日的时候，江泰需要确保该生日和身份证上的生日是一致的。但是从这获取的生日是顾客自己填写的
					//所以当顾客提交的身份证件类型是身份证的时候，直接采用身份证的生日提取。	
					info.setIdentifyNumber(tmp.getCertificateNumber());
					info.setIdentifyType(tmp.getCertificate());
					info.setInsuredName(tmp.getName());
					//此处规定"0"为女性，"1"为男性
					if(tmp.getSex() == 0){
						info.setSex("F");
					}else{
						info.setSex("M");
					}
					if(tmp.getCertificate() == 1){
						//如果是身份证，则直接提取生日
						info.setBirthDay(JiangtaiUtil.getBirthdayFromCertificate(tmp.getCertificateNumber()));
					}else{
						info.setBirthDay(DateUtil.getBirthday(tmp.getBirthday()));
					}			
					insuredList.add(info);	
				}
				//组装confirmMessage
				ConfirmMessage cmo  = new ConfirmMessage();
				//confirmMessage中需要手动填写的参数有以下几个
				
				if(order.getType()==1){
					HyGroup group = hyGroupService.find(order.getGroupId());
					cmo.setTravelRoute(group.getGroupLineName());
				}
				//cmo.setPayType(payType);
				//必须确保我们的日期格式是yyyy-MM-dd HH:mm:ss
				//系统的所有时间格式都是yyyy-MM-dd，我们设置当天的凌晨开始保险
				DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //HH表示24小时制；  
		        String formatDate = dFormat.format(insuranceOrder.getInsuranceStarttime()); 
				cmo.setStartDate(formatDate);
				cmo.setEndDate(DateUtil.getSimpleEndOfDayDate(insuranceOrder.getInsuranceEndtime()));
				cmo.setContactName(order.getContact());
				cmo.setContactPhone(order.getPhone());

				if(order.getType() == 6) {
					if(order.getXianlumingcheng() == null) {
						cmo.setTravelRoute("默认线路");
					}
					else {
						cmo.setTravelRoute(order.getXianlumingcheng());
					}
					
				}		
				cmo.setSumQuantity(1);
				//设置旅游团编号为虹宇的订单编号
				cmo.setTravelGroupNo(order.getOrderNumber());
				//此处获取到渠道交易的时间，因为该时间取得是当前时间，所以必须保存，在投保成功之后写入数据库
				String channelTradeDate = DateUtil.getSimpleDate(new Date());
				cmo.setChannelTradeDate(channelTradeDate);
				//获取投保产品号
				Insurance  insurance = insuranceService.find(insuranceOrder.getInsuranceId());
				cmo.setProductCode(insurance.getInsuranceCode());
				//渠道交易流水号--直接设置"前缀+订单编号+当前时间MMddHHmmss"
				//cmo.setChannelTradeSerialNo(order.getOrderNumber());
				String channelTradeSerialNo = "";
				if(insuranceOrder.getType() == 0){
					//如果是团期投保，流水号前缀为"GROUP" 意为"团期"
					channelTradeSerialNo = "GROUP"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
					//cmo.setChannelTradeSerialNo("TQ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
				}
				if(insuranceOrder.getType() == 1){
					//如果是门店个人自主投保，流水号前缀"SELF" 意为“自主”
					channelTradeSerialNo = "SELF"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
					//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
				}
				if(insuranceOrder.getType() == 2){
					//如果是门店个人自主投保，流水号前缀"ONLINE" 意为“网上”
					channelTradeSerialNo = "ONLINE"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date());
					//cmo.setChannelTradeSerialNo("ZZ"+order.getOrderNumber()+DateUtil.getSuffixDate(new Date()));
				}
				cmo.setChannelTradeSerialNo(channelTradeSerialNo);
				cmo.setInsuredList(insuredList);		
				json = JiangtaiUtil.order(cmo);
				//System.out.println("投保原始数据："+json.getObj().toString());
				
				JtOrderResponse jtOrderResponse = (JtOrderResponse) XStreamUtil.xmlToBean(json.getObj().toString());
				//System.out.println("投保实体获取到了返回信息："+jtOrderResponse.toString());
				if("000001".equals(jtOrderResponse.getResponseCode())){
					 //投保成功，更新insuranceOrder表
					//此设置保单的下载地址，因为保单的对应的是订单，所以保单地址设置在订单数据表中
					HyOrder hyOrder = hyOrderService.find(orderId);
					hyOrder.setInsuranceOrderDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
					insuranceOrder.setDownloadUrl(JiangtaiUtil.OrderDown(channelTradeSerialNo));
					//8.9日下午四点修改，将save改成update;
					hyOrderService.update(hyOrder);
					insuranceOrder.setStatus(3);
					insuranceOrder.setInsuredTime(new Date());
					insuranceOrder.setJtChannelTradeSerialNo(jtOrderResponse.getChannelTradeSerialNo());
					insuranceOrder.setJtChannelTradeDate(channelTradeDate);
					insuranceOrder.setJtOrderNo(jtOrderResponse.getJtOrderNo());
					insuranceOrder.setJtPolicyNo(jtOrderResponse.getPolicyNo());
					insuranceOrder.setJtSumPremium(jtOrderResponse.getSumPremium());
					insuranceOrder.setJtDiscount(jtOrderResponse.getDiscount());
					for(HyPolicyHolderInfo tmp : insuranceOrder.getPolicyHolders()){
						//此处设置个人保险凭证的下载地址
						String downloadUrl = JiangtaiUtil.OrderDownCertificate(insuranceOrder.getJtChannelTradeSerialNo(),tmp.getCertificateNumber()); 
						tmp.setDownloadUrl(downloadUrl);
						hyPolicyHolderInfoService.update(tmp);							
					}
					this.update(insuranceOrder);
					//封装数据返回给前台，将投保成功的订单id放到返回列表中。
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
					successIds.add(tmp);
				} else{
					//如果投保失败就将失败的id返回
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
					failIds.add(tmp);
				}
			}
			map.put("successIds", successIds);
			map.put("failIds", failIds);
			long endTime = System.currentTimeMillis();
			
			json.setMsg("以下订单投保操作完成,总耗时 "+(endTime-startTime)+"ms");
			json.setObj(map);
			return json;		
	}
	
	@Override
	public Json cancelOrder(Long[] orderIds){
		Json json = new Json();
		//将那些投保失败的订单再依次投保一遍，每个订单应该有好几个投保人
		//1、如果投保类型是团投，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
		//2、如果投保类型是们门店自主投保
		try {
			long startTime = System.currentTimeMillis();
			//将传入的所有的单子全部撤保
			Map<String, Object> map = new HashMap<>();
			List<Map<String,Object>> cancelSuccessIds = new ArrayList<>();
			List<Map<String,Object>> cancelFailIds = new ArrayList<>();
			for(int i=0;i<orderIds.length;i++){
				Long orderId = orderIds[i];
				//根据该订单id去获取所有的保险人对应的保单
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", orderId));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,null);
				//因为撤单只需要提供这个订单的投保流水号就行了，而这个订单对应的那些人的投保流水号是一样的，所以取第一个就好了
				InsuranceOrder insuranceOrder = insuranceOrders.get(0);
				//如果当前订单已经撤保，直接跳过
				if(insuranceOrder.getStatus() == 4){
					continue;
				}
				//封装撤保参数
				ConfirmMessage cmoc  = new ConfirmMessage();
				cmoc.setChannelTradeSerialNo(insuranceOrder.getJtChannelTradeSerialNo());
				cmoc.setChannelTradeDate(insuranceOrder.getJtChannelTradeDate());
				cmoc.setChannelTradeCode(Constants.CHANNEL_TRADE_CODE_CANCEL_ORDER);
				//调用撤保接口来撤保
				json = JiangtaiUtil.orderCancel(cmoc);
				//System.out.println("cancel json = "+json.getMsg() + " result = "+(String)json.getObj());
				//System.out.println("xml = "+ json.getObj().toString());
				JtCancelOrderResponse jtCancelOrderResponse = (JtCancelOrderResponse) XStreamUtil2.xmlToBean(json.getObj().toString());
				//System.out.println("jtCancelOrderResponse = "+jtCancelOrderResponse);
				if("000001".equals(jtCancelOrderResponse.getResponseCode())){
					//设置为4，就是已撤保状态	
					insuranceOrder.setStatus(4);
					insuranceOrderService.update(insuranceOrder); 					
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtCancelOrderResponse.getResponseMessage());
					cancelSuccessIds.add(tmp);
				}else{
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtCancelOrderResponse.getResponseMessage());
					cancelFailIds.add(tmp);
				}
			}
			map.put("cancelSuccessIds", cancelSuccessIds);
			map.put("cancelFailIds", cancelFailIds);
			long endTime = System.currentTimeMillis();
			json.setMsg("以下订单退保成功,共耗时 "+ (endTime-startTime) + "ms");
			json.setSuccess(true);
			json.setObj(map);
		} catch (Exception e) {
			json.setMsg("撤销保单出错 "+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}

	@Override
	public Json cancelInsuranceOrder(Long id) throws Exception {
		Json json = new Json();
		//将那些投保失败的订单再依次投保一遍，每个订单应该有好几个投保人
		//1、如果投保类型是团投，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
		//2、如果投保类型是们门店自主投保
		try {
			long startTime = System.currentTimeMillis();
			InsuranceOrder insuranceOrder = insuranceOrderService.find(id);
			if(insuranceOrder==null)
				throw new Exception("该保单不存在，无法撤保");
			//如果当前订单已经撤保，直接跳过
			if(insuranceOrder.getStatus() == 4){
				throw new Exception("该保单已经撤保，无需重复撤保！");
			}
			if(insuranceOrder.getStatus() == 3){
				
				//封装撤保参数
				ConfirmMessage cmoc  = new ConfirmMessage();
				cmoc.setChannelTradeSerialNo(insuranceOrder.getJtChannelTradeSerialNo());
				cmoc.setChannelTradeDate(insuranceOrder.getJtChannelTradeDate());
				cmoc.setChannelTradeCode(Constants.CHANNEL_TRADE_CODE_CANCEL_ORDER);
				//调用撤保接口来撤保
				json = JiangtaiUtil.orderCancel(cmoc);
				JtCancelOrderResponse jtCancelOrderResponse = (JtCancelOrderResponse) XStreamUtil2.xmlToBean(json.getObj().toString());
				if("000001".equals(jtCancelOrderResponse.getResponseCode())){
					//设置为4，就是已撤保状态	
					insuranceOrder.setStatus(4);
					insuranceOrderService.update(insuranceOrder); 					
				}else{
					throw new Exception("江泰方撤保失败！");
				}
			}
			long endTime = System.currentTimeMillis();
			json.setMsg("撤保成功,共耗时 "+ (endTime-startTime) + "ms");
			json.setSuccess(true);
			json.setObj(null);
		} catch (Exception e) {
			json.setMsg("撤销保单出错 "+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	@Override
	public void updateOldDataMoney() throws Exception {
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("type", 0));
		filters.add(Filter.eq("status", 3));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = dateFormat.parse("2019-06-19 00:00:00");
		filters.add(Filter.le("createDate", date));
		List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,null);
		for(InsuranceOrder tmp:insuranceOrders){
			//对于每个保险订单，在这里不能考率保险id的问题，因为之前线路自带保险和游客自选保险不一致，已经投完了，只能按照实际投的算了
			Insurance insurance = insuranceService.find(tmp.getInsuranceId());
			
			List<InsurancePrice> prices = insurance.getInsurancePrices();
			List<InsuranceTime> times = insurance.getInsuranceTimes();
			List<HyPolicyHolderInfo> policyHolderInfos = tmp.getPolicyHolders();
			HyOrder order = hyOrderService.find(tmp.getOrderId());
			Integer days = order.getTianshu();
			
			BigDecimal receivedMoney = BigDecimal.ZERO;
			BigDecimal shifuMoney = BigDecimal.ZERO;
			
			for(HyPolicyHolderInfo policyHolderInfo:policyHolderInfos){
				//对于每一个游客计算价格
				/**计算单人保险总价*/
				
				BigDecimal shishou = BigDecimal.ZERO;
				BigDecimal shifu = BigDecimal.ZERO;
				
				BigDecimal maxSalePrice = BigDecimal.ZERO;
				BigDecimal maxSettlePrice = BigDecimal.ZERO;
						
				boolean ifBeyond = true;
				for(InsurancePrice insurancePrice:prices){
					if(days>=insurancePrice.getStartDay()&&days<=insurancePrice.getEndDay()){
						shishou = insurancePrice.getSalePrice();
						shifu = insurancePrice.getSettlementPrice();
						ifBeyond = false;
						break;
					}
					if(maxSalePrice.compareTo(insurancePrice.getSalePrice())<0){
						maxSalePrice = insurancePrice.getSalePrice();
						maxSettlePrice = insurancePrice.getSettlementPrice();
					}
				}
				if(ifBeyond){
					//如果超出界限,按最低价格算
					shishou = shishou.add(maxSalePrice);
					shifu = shifu.add(maxSettlePrice);
				}
					
				Integer age = policyHolderInfo.getAge();
				for(InsuranceTime time:times){
					if(age>=time.getAgeStart() && age<=time.getAgeEnd()){
						shishou = shishou.multiply(time.getTimes());
						shifu = shifu.multiply(time.getTimes());
					}
				}
				
				receivedMoney = receivedMoney.add(shishou);
				shifuMoney = shifuMoney.add(shifu);
			}
			
			tmp.setReceivedMoney(receivedMoney);
			tmp.setShifuMoney(shifuMoney);
			tmp.setProfit(receivedMoney.subtract(shifuMoney));
		}
		
	}
	
	
}
