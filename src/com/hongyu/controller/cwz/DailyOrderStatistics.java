package com.hongyu.controller.cwz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.Guide;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.Store;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;

@RestController
//上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/business/daily/order/")
public class DailyOrderStatistics {

	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService; 
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "storePreSaveServiceImpl")
	private StorePreSaveService storePreSaveService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyLineServiceImpl")
	private HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name = "hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name = "guideServiceImpl")
	private GuideService guideService;
	
	@Resource(name = "hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	
	@RequestMapping(value = "view") // 1 列表页 支持按照门店、报名计调、发团日期、下单日期、产品ID筛选
	public Json dailyOrderStatisticsView(String storeName, String operatorName, String dateOfDeparture, 
			String dateOfPlaceOrder, String endOfPlaceOrder, String ID, String theOrderNumber, Integer myType,
			Long areaId,Integer storeType) throws Exception {
		Date dateOfDepartureDate = DateUtil.stringToDate(dateOfDeparture, DateUtil.YYYY_MM_DD);
//		Date dateOfPlaceOrderDate = DateUtil.stringToDate(dateOfPlaceOrder, DateUtil.YYYY_MM_DD);
		//门店筛选，用户输入门店名称，字符串
		//报名计调，用户输入计调名称，字符串
		//发团日期，传入Date类型的日期
		//下单日期，传入Date类型的日期
		//2019-4-28 endOfPlaceOrder 下单日期结束
		//产品ID，先按照输入ID做，感觉对用户不友好
		//后台默认按照按照当日作为创建时间，筛选当日订单，默认倒叙排列，除非传入新的日期
		
		//用来装下面的数据，需要一个ArrayList来存放一行数据
		List<HashMap<String, Object>> dailyOrderStatisticsViewList = new ArrayList<>();
		HashMap<String, Object> dailyOrderStatisticsViewListItem;
		HashMap<String, Object> dailyOrderStatisticsViewListItemTotal = new HashMap<String, Object>();

		
		Json j = new Json();
		try {
			//之后要放的数据
			String myorderNumber;//1.订单编号
			Long myid;//2.产品ID
			String myPn = "";//数据库中没有数据就是空
			String myxianlumingcheng = null;//3.线路名称
			String myxianluType = "";//4.线路类型 把数字转化成字符串 如果是空，就是数据库没数据
			Integer myxianluTypeNumber;//为了接收线路类型
			Date myfatuanDate;//5.发团日期
			Date mycreateTime;//6.下单日期
			String mycontact;//7.联系人名字
			Integer mypeopleNumber;//8.人数
			String myoperatorName = null;//9.操作人
			String mystoreId = null;//10.其实是用store的ID去查找名字
			BigDecimal myjiusuanMoney = null;//11.订单金额
			String mypurchaseMethod;//12.支付方式
			Date myperSaveTime = null;//13.支付时间
			Long myperSaveID = null;//14.交易流水号
			String mycontractName;//15.供应商
			BigDecimal myjiesuanMoney1;//16.供应商结算价
			String myOrderNumber;//17.订单号
			BigDecimal totalMoney = new BigDecimal("0");//18.合计金额 2019-5-14
			Integer totalPeopleNumber = 0;//2019-5-17 合计人数
			BigDecimal refundMoney = null;//2019-5-23 退款金额
			BigDecimal totalMyjiesuanMoney1 = new BigDecimal("0");//2019-6-20 供应商总计金额
			BigDecimal totalRefundMoney = new BigDecimal("0");//2019-6-20 退款合计金额
			//BigDecimal totalInsuranceMoney = new BigDecimal("0");//2019-6-20 合计保险金额
			
			 
			//用来进行初始化筛选
			List<Filter> orderFilter = new ArrayList<Filter>();
			
			//！！！注意 先根据筛选条件选定order的项，再根据order去找各个表拼成一个大表
			List<Store> fddContractInfo = new ArrayList<>();
			List<HyAdmin> operatorInfo = new ArrayList<>();
			//每一次循环得到的
			List<HyOrder> orderInfo = new ArrayList<>();
			//总的，每次循环得到的放到总的里面
			List<HyOrder> orderInfoAll = new ArrayList<>();
			List<StorePreSave> storePreSaveList = new ArrayList<>();
			
			//先容错 没有填写默认就查找全部
			if(storeName != null){
				//输入的名字在另外一张表里面，根据名字找到ID
				//根据找到的ID去在order表里面找对应的ID（hy_contract去这里找）
				//？？？存疑 不清楚用的是哪张表
				//或者是HySupplierContract？
				List<Filter> contractFilter = new ArrayList<Filter>();
				contractFilter.add(Filter.eq("storeName", storeName));//id=141
				fddContractInfo = storeService.findList(null, contractFilter, null);
			}
			if(operatorName != null){
				//输入的名字在另外一张表里面，根据名字找到ID
				//根据找到的ID去在order表里面找对应的operator（HyAdmin类型）
				List<Filter> adminFilter = new ArrayList<Filter>();
				adminFilter.add(Filter.eq("name", operatorName));//很多
				operatorInfo = hyAdminService.findList(null, adminFilter, null);
			}
			
			//很无奈，这个底层没有优化好 只能把9和10再查找一遍
			if(!operatorInfo.isEmpty()){
				//上面拿到的一个条件 循环用find去找项
				for(HyAdmin myHyAdmin : operatorInfo){
					//遍历每一项 把ID拿出来筛选这里的HyOrder
					orderFilter.add(Filter.eq("operator", myHyAdmin));
					//想用“或”的方法查找有一些难
					if(!fddContractInfo.isEmpty()){
						for(Store fddContractItem : fddContractInfo){
							//简单筛选条件加在内层
							if(dateOfDepartureDate != null){
								orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
							}
							if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
//								String previousDay = DateUtil.getThePreviousDay(dateOfPlaceOrder);
								orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
								//orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(dateOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
								//从下单这天的0点一直到最后一天的23点59分就行，改的就一个变量
								orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
							}
							//***** 2019-4-17
//							if(ID != null){
//								orderFilter.add(Filter.eq("id", ID));
//							}
							//***** 2019-4-17
							
							//!!!!这里应该是storeID
							orderFilter.add(Filter.eq("storeId", fddContractItem.getId()));
							
							//***** 2019-4-16
							if(theOrderNumber != null){
								orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
							}
							if(myType != null){
								orderFilter.add(Filter.eq("type", myType));
							}
							orderFilter.add(Filter.eq("paystatus", 1));
							//***** 2019-4-16
							
							//***** 2019-4-19
							//orderFilter.add(Filter.eq("status", 3));//因为一般的订单没有9或者10，这里直接让3 9 10 都通过
							Collection<Integer> list = new ArrayList<>();
							Collections.addAll(list,3,9,10);
							orderFilter.add(Filter.in("status", list));
							//***** 2019-4-19
							
							//&&&&& 4-19 night
							orderFilter.add(Filter.eq("source", 0));
							//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
							
							//2019-6-21
							List<Order> orders=new ArrayList<>();
							orders.add(Order.desc("createtime"));
							//2019-6-21
							
							//2019-8-21
							if(storeType != null){
								orderFilter.add(Filter.eq("storeType", storeType));
							}
							//2019-8-21
							
							//先查出来符合条件的hyOrder
							orderInfo = hyOrderService.findList(null, orderFilter, orders);
							//加到总的容器中
							orderInfoAll.addAll(orderInfo);
						}
						orderFilter.clear();//清除一下，这里
					} else {
						
						if(dateOfDepartureDate != null){
							orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
						}
						if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
							orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
							orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
						}
						
						//***** 2019-4-17
//						if(ID != null){
//							orderFilter.add(Filter.eq("id", ID));
//						}
						//***** 2019-4-17
						
						//***** 2019-4-16
						if(theOrderNumber != null){
							orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
						}
						if(myType !=  null){
							orderFilter.add(Filter.eq("type", myType));
						}
						orderFilter.add(Filter.eq("paystatus", 1));
						//***** 2019-4-16
						
						//***** 2019-4-19
						//orderFilter.add(Filter.eq("status", 3));
						Collection<Integer> list = new ArrayList<>();
						Collections.addAll(list,3,9,10);
						orderFilter.add(Filter.in("status", list));
						//***** 2019-4-19
						
						//&&&&& 4-19 night
						orderFilter.add(Filter.eq("source", 0));
						//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
						
						List<Order> orders=new ArrayList<>();
						orders.add(Order.desc("createtime"));
						
						//2019-8-21
						if(storeType != null){
							orderFilter.add(Filter.eq("storeType", storeType));
						}
						//2019-8-21
						//先查出来符合条件的hyOrder
						orderInfo = hyOrderService.findList(null, orderFilter, orders);
						//加到总的容器中
						orderInfoAll.addAll(orderInfo);
						
					}
					orderFilter.clear();//很容易弄错
				}
			} else{
				
				
				//另一个是不是为空
				//2019-4-28 错误就在这里，因为清空了filter之后就一个筛选条件了，不合适
				//就把上面的筛选条件放在循环里
				if(!fddContractInfo.isEmpty()){
					for(Store fddContractItem : fddContractInfo){
						
						if(dateOfDepartureDate != null){
							orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
						}
						if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
							orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
							orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
						}
						
						//***** 2019-4-17
//						if(ID != null){
//							orderFilter.add(Filter.eq("id", ID));
//						}
						//***** 2019-4-17
						
						//2019-3-7
						if(theOrderNumber != null){
							orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
						}
						
						//2019-3-6
						if(myType != null){
							orderFilter.add(Filter.eq("type", myType));
						}
						//2019-4-16
						orderFilter.add(Filter.eq("paystatus", 1));
						
						//***** 2019-4-19
						//orderFilter.add(Filter.eq("status", 3));
						Collection<Integer> list = new ArrayList<>();
						Collections.addAll(list,3,9,10);
						orderFilter.add(Filter.in("status", list));
						//***** 2019-4-19
						
						//&&&&& 4-19 night
						orderFilter.add(Filter.eq("source", 0));
						//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
						
						//????不确定是这个意思
						orderFilter.add(Filter.eq("storeId", fddContractItem.getId()));
						
						List<Order> orders=new ArrayList<>();
						orders.add(Order.desc("createtime"));
						
						//2019-8-21
						if(storeType != null){
							orderFilter.add(Filter.eq("storeType", storeType));
						}
						//2019-8-21
						
						//先查出来符合条件的hyOrder
						orderInfo = hyOrderService.findList(null, orderFilter, orders);
						//加到总的容器中
						orderInfoAll.addAll(orderInfo);
						orderFilter.clear();//清除一下，这里
					}
				} else {
					
					if(dateOfDepartureDate != null){
						orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
					}
					if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
						orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
						orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
					}
					
					//***** 2019-4-17
//					if(ID != null){
//						orderFilter.add(Filter.eq("id", ID));
//					}
					//***** 2019-4-17
					
					//2019-3-7
					if(theOrderNumber != null){
						orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
					}
					
					//2019-3-6
					if(myType != null){
						orderFilter.add(Filter.eq("type", myType));
					}
					//2019-4-16
					orderFilter.add(Filter.eq("paystatus", 1));
					
					//***** 2019-4-19
					//orderFilter.add(Filter.eq("status", 3));
					Collection<Integer> list = new ArrayList<>();
					Collections.addAll(list,3,9,10);
					orderFilter.add(Filter.in("status", list));
					//***** 2019-4-19
					
					//&&&&& 4-19 night
					orderFilter.add(Filter.eq("source", 0));
					//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
					
					List<Order> orders=new ArrayList<>();
					orders.add(Order.desc("createtime"));
					
					//2019-8-21
					if(storeType != null){
						orderFilter.add(Filter.eq("storeType", storeType));
					}
					//2019-8-21
					
					//先查出来符合条件的hyOrder
					orderInfo = hyOrderService.findList(null, orderFilter, orders);
					//加到总的容器中
					orderInfoAll.addAll(orderInfo);
				}
				
			}
			
			
			Long specIdelete = null;
			Long prodIdelete = null;
			//如果非得按照pn码筛选
			if(ID != null){
				//4-17 按照ID也就是line表中的pn码筛选
				//!!!!是不是util包？
				Iterator<HyOrder> iter = orderInfoAll.iterator();  

				while(iter.hasNext()){
					HyOrder myHyorder = iter.next();
					if(myHyorder.getOrderItems()!= null){
						//这样做的话，可能拿到的是null
						specIdelete = myHyorder.getOrderItems().get(0).getSpecificationId();
						prodIdelete = myHyorder.getOrderItems().get(0).getProductId();
					}
				
					
					//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
					if(myHyorder.getType() != null){
						if(myHyorder.getType() == 0){//导游租赁
							
							if(prodIdelete != null){
								Guide myGuide = guideService.find(prodIdelete);
								if(myGuide != null){
									String thePn = myGuide.getGuideSn();
									if(thePn != null){
										if(!thePn.equals(ID)){
											iter.remove();
										}
									} else {
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 1){//线路
							
							
							Long thisID = null;
							thisID = myHyorder.getGroupId();//2
							if(thisID != null){
								HyGroup myHyGroupPn = hyGroupService.find(thisID);
								if(myHyGroupPn != null){
									//hyGroupService.find(id)
									HyLine myHyLinePn = myHyGroupPn.getLine();
									if(myHyLinePn != null){
										String Pn = null;
										Pn = myHyLinePn.getPn();
										//这里判断是否等于我们要筛选的pn，不等于就删去
										if(!Pn.equals(ID)){
											//这个对么？
											iter.remove();
										}
									} else {
										//2019-5-6 没有GroupID的订单都不显示
										iter.remove();
									}
								} else {
									//2019-5-6 没有GroupID的订单都不显示
									iter.remove();
								}
							} else {
								//2019-5-6 没有GroupID的订单都不显示
								iter.remove();
							}
							
							
						} else if(myHyorder.getType() == 2){//认购门票
							
							if(prodIdelete != null){
								HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodIdelete);
								if(hyTicketSubscribe != null){
									String thePn = hyTicketSubscribe.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 3){//酒店
							
							if(specIdelete != null){
								HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(specIdelete);
								if(hyTicketHotelRoom != null){
									String thePn = hyTicketHotelRoom.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 4){//门票
							
							if(specIdelete != null){
								HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(specIdelete);
								if(hyTicketSceneTicketManagement != null){
									String thePn = hyTicketSceneTicketManagement.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 5){//酒+景
							
							if(prodIdelete != null){
								//TODO
								HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodIdelete);
								if(hyTicketHotelandscene != null){
									String thePn = hyTicketHotelandscene.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 6){//保险
							
							if(prodIdelete != null){
								Insurance insurance = insuranceService.find(prodIdelete);
								if(insurance != null){
									String thePn = insurance.getInsuranceCode();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 7){//签证
							
							if(prodIdelete != null){
								HyVisa hyVisa = hyVisaService.find(prodIdelete);
								if(hyVisa != null){
									String thePn = hyVisa.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
						}
						
					}
				
				}
			}
			
			//2019-5-10 让已经消团的不显示
			Iterator<HyOrder> iterXiaotuan = orderInfoAll.iterator(); 
			while(iterXiaotuan.hasNext()){
				Long thisID = null;
				thisID = iterXiaotuan.next().getGroupId();//2
				if(thisID != null){
					HyGroup myHyGroup = hyGroupService.find(thisID);
					if(myHyGroup != null){
						GroupStateEnum wrongStatus = GroupStateEnum.yiquxiao;
						GroupStateEnum thisStatus = myHyGroup.getGroupState();
						if(wrongStatus == thisStatus){
							iterXiaotuan.remove();
						}
					}
				}
			}
			//2019-5-10 让已经消团的不显示
			
			//2019-5-14
			BigDecimal moneyThisTurn = null;
			BigDecimal moneyThisTurn2 = null;//2019-6-20
			BigDecimal moneyThisTurnRefund = null;//2019-6-20
			if(orderInfoAll != null)
				for(HyOrder myHyOrder : orderInfoAll){
					
					//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
					Store store = storeService.find(myHyOrder.getStoreId());
					HyArea myHyArea = new HyArea();
					HyArea myHyAreaP = new HyArea();
					Long NID = null;
					Long PID = null;
					
					if(store==null){
						throw new Exception("订单 "+myHyOrder.getOrderNumber()+" 的门店找不到");
					} else {
						myHyArea = store.getHyArea();
						if(myHyArea != null){
							myHyAreaP = myHyArea.getHyArea();
							NID = myHyArea.getId();
							if(myHyAreaP != null){
								PID = myHyAreaP.getId();
							}
						}
					}
					
					if (areaId != null){
						if((NID!=null && !NID.equals(areaId)) && (PID!=null && !PID.equals(areaId))){
							continue;//如果同时不相等，就进入下一轮
						}
					}
					//2019-8-18
					if(myHyOrder.getStoreType() == null){
						continue;
					}
					//这个不用改，是对的
					if(myHyOrder.getStoreType() != null && storeType != null){
						if(!myHyOrder.getStoreType().equals(storeType)){
							continue;
						}
					}
					//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
					
					
					//2019-7-16
					if(myHyOrder.getType() != null){
						
						if(myHyOrder.getType() == 0){
							if(myHyOrder.getTip() != null && myHyOrder.getJiusuanMoney() != null){
								moneyThisTurn =  myHyOrder.getJiusuanMoney().add(myHyOrder.getTip());
							} else if(myHyOrder.getTip() == null) {
								moneyThisTurn =  myHyOrder.getJiusuanMoney();
							}
						} else if(myHyOrder.getType() != 6){
							if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
								if(myHyOrder.getJiesuanTuikuan() != null){//4-19
									BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
									moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
								} else {
									moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
								}
							} else {
								moneyThisTurn = myHyOrder.getJiusuanMoney();
							}
						} else {
							//这个是保险订单，用外卖价
							//moneyThisTurn = myHyOrder.getWaimaiMoney();
							//2019-7-31
							if(myHyOrder.getBaoxianWaimaiTuikuan() == null){
								moneyThisTurn = myHyOrder.getWaimaiMoney();
							} else {
								moneyThisTurn = myHyOrder.getWaimaiMoney().subtract(myHyOrder.getBaoxianWaimaiTuikuan());
							}
							//2019-7-31
						}
					}
					//2019-7-16
					
					//老旧代码注释
//					if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
//						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//							moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//						} else {
//							moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
//						}
//					} else {
//						moneyThisTurn = myHyOrder.getJiusuanMoney();
//					}
					
					if(myHyOrder.getType() != null){
						if(myHyOrder.getType().equals(0)){
							moneyThisTurn2 = new BigDecimal(0);
						} else {
							if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
								if(myHyOrder.getJiesuanTuikuan() != null){//4-19
									BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
									moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
								} else {
									moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
								}					
							} else {
								moneyThisTurn2 = myHyOrder.getJiesuanMoney1();
								if(moneyThisTurn2 == null){
									moneyThisTurn2 = new BigDecimal(0);//2019-6-21
								}
							}
						}
					}
					
					
					//2019-6-20 老旧代码注释
//					if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
//						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//							moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//						} else {
//							moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
//						}					
//					} else {
//						moneyThisTurn2 = myHyOrder.getJiesuanMoney1();
//						if(moneyThisTurn2 == null){
//							moneyThisTurn2 = new BigDecimal(0);//2019-6-21 不一定对，不知道供应商结算价是不是空
//						}
//					}
//					//2019-6-20
					
					//2019-6-20
					if(myHyOrder.getType() != null){
						if(myHyOrder.getType() != 6){
							moneyThisTurnRefund = myHyOrder.getJiesuanTuikuan();//不是保险订单
						} else {
							moneyThisTurnRefund = myHyOrder.getBaoxianWaimaiTuikuan();//是保险订单
						}
					}
					//2019-6-20
					
					
					//把每次的钱拿出来，都加在一起
					totalMoney = totalMoney.add(moneyThisTurn);
					//2019-6-20
					totalMyjiesuanMoney1 = totalMyjiesuanMoney1.add(moneyThisTurn2);
					totalRefundMoney = totalRefundMoney.add(moneyThisTurnRefund);
					//2019-6-20
					
					//2019-5-17人数加起来
					if(myHyOrder.getPeople() != null)
						totalPeopleNumber = totalPeopleNumber + myHyOrder.getPeople();
					
				}
			//2019-5-14
			
			Long specId = null;
			Long prodId = null;
			//现在得到了所有的HyOrder类
			//遍历所有得到的HyOrder，找到所有要传给前端的信息
			if(orderInfoAll != null)
			for(HyOrder myHyOrder : orderInfoAll){
				
				//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
				Store store = storeService.find(myHyOrder.getStoreId());
				HyArea myHyArea = new HyArea();
				HyArea myHyAreaP = new HyArea();
				Long NID = null;
				Long PID = null;
				
				if(store==null){
					throw new Exception("订单 "+myHyOrder.getOrderNumber()+" 的门店找不到");
				} else {
					myHyArea = store.getHyArea();
					if(myHyArea != null){
						myHyAreaP = myHyArea.getHyArea();
						NID = myHyArea.getId();
						if(myHyAreaP != null){
							PID = myHyAreaP.getId();
						}
					}
				}
				
				if (areaId != null){
					if((NID!=null && !NID.equals(areaId)) && (PID!=null && !PID.equals(areaId))){
						continue;//如果同时不相等，就进入下一轮
					}
				}
				
				//2019-8-18
				if(myHyOrder.getStoreType() == null){
					continue;
				}
				//这个不用改，是对的
				if(myHyOrder.getStoreType() != null && storeType != null){
					if(!myHyOrder.getStoreType().equals(storeType)){
						continue;
					}
				}
				//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
				
				
				//能赋值的赋值
				myorderNumber = myHyOrder.getOrderNumber();//1
				
				myid = myHyOrder.getGroupId();//2
				
				if(myHyOrder.getOrderItems()!= null){
					//这样做的话，可能拿到的是null
					specId = myHyOrder.getOrderItems().get(0).getSpecificationId();
					prodId = myHyOrder.getOrderItems().get(0).getProductId();
				}

				
				//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				Integer types = myHyOrder.getType();
				if(types != null){
					if(types.equals(0)){//导游租赁
						//TODO
						//老师自己下了一单，也就是order_number = 2019062100007
						//其中产品编号是导游编号，产品名称是导游名称，订单金额就是刚才报名的240+10元小费
						if(prodId != null){
							Guide myGuide = guideService.find(prodId);
							if(myGuide != null){
								myPn = myGuide.getGuideSn();
							}
						}	
						
					}else if(types.equals(1)){//线路
						
						if(myid != null){
							HyGroup myHyGroup = hyGroupService.find(myid);
							if(myHyGroup != null){
								//hyGroupService.find(id)
								HyLine myHyLine = myHyGroup.getLine();
								if(myHyLine != null){
									myPn = myHyLine.getPn();
								}
							}
						}
						
					}else if(types.equals(2)){//认购门票
						if(prodId != null){
							HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodId);
							if(hyTicketSubscribe != null){
								myPn = hyTicketSubscribe.getProductId();
							}
						}
					}else if(types.equals(3)){//酒店
						if(specId != null){
							HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(specId);
							if(hyTicketHotelRoom != null){
								myPn = hyTicketHotelRoom.getProductId();
							}
						}
					}else if(types.equals(4)){//门票
						
						if(specId != null){
							HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(specId);
							if(hyTicketSceneTicketManagement != null){
								myPn = hyTicketSceneTicketManagement.getProductId();
							}
						}
						
					}else if(types.equals(5)){//酒+景
						if(prodId != null){
							//TODO
							HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodId);
							if(hyTicketHotelandscene != null){
								myPn = hyTicketHotelandscene.getProductId();
							}
						}
					}else if(types.equals(6)){//保险
						if(prodId != null){
							Insurance insurance = insuranceService.find(prodId);
							if(insurance != null){
								myPn = insurance.getInsuranceCode();
							}
						}
					}else if(types.equals(7)){//签证
						if(prodId != null){
							HyVisa hyVisa = hyVisaService.find(prodId);
							if(hyVisa != null){
								myPn = hyVisa.getProductId();
							}
						}
					}
				}

				
				//这里的myPn是产品编号，是后来找建勇问的
				//现在需要把别的产品加进去 
				//建勇写的酒店 是从setSpecificationId里面拿 剩下的是从酒店里拿 后面的图
				//酒加景是：从productId里面拿 前面的图
				//签证是：hy_order找到对应的hy_orderitem，item表中有productId，就是hyVisa的主键ID，根据这个id找到HyVisa,HyVisa里面有pn
				
//				List<Filter> myHyLineFilter = new ArrayList<Filter>();
//				myHyLineFilter.add(Filter.eq("id", myid));//id=141
//				hyLineService.findList(null, myHyLineFilter, null);
				
				if(types.equals(0)){
					if(prodId != null){
						Guide myGuide = guideService.find(prodId);
						if(myGuide != null){
							myxianlumingcheng = myGuide.getName();
						}
					}	
				} else {
					myxianlumingcheng = myHyOrder.getName();//3
				}
				
				myxianluTypeNumber = myHyOrder.getXianlutype();//4
				if(myxianluTypeNumber!=null){
					if(myxianluTypeNumber == 0){//4
						myxianluType = "汽车散客";
					}
					else if(myxianluTypeNumber == 1){
						myxianluType = "汽车团客";
					}
					else if(myxianluTypeNumber == 2){
						myxianluType = "国内散客";
					}
					else if(myxianluTypeNumber == 3){
						myxianluType = "国内团客";
					}
					else if(myxianluTypeNumber == 4){
						myxianluType = "出境散客";
					}
					else if(myxianluTypeNumber == 5){
						myxianluType = "出境团客";
					}
				} 
				
				myfatuanDate = myHyOrder.getFatuandate();//5
				mycreateTime = myHyOrder.getCreatetime();//6
				mycontact = myHyOrder.getContact();//7
				mypeopleNumber = myHyOrder.getPeople();//8
				HyAdmin myAdmin= myHyOrder.getOperator();
				if(myAdmin!=null){
					if(myAdmin.getName()!=null){
						myoperatorName = myAdmin.getName();//9
					}
				}
				//???这里有问题
				if(myHyOrder.getStoreId()!=null){
					mystoreId = storeService.find(myHyOrder.getStoreId()).getStoreName();//10
				}
				if(myHyOrder.getType() != null){
					
					if(myHyOrder.getType() == 0){
						if(myHyOrder.getTip() != null && myHyOrder.getJiusuanMoney() != null){
							myjiusuanMoney =  myHyOrder.getJiusuanMoney().add(myHyOrder.getTip());
						} else if(myHyOrder.getTip() == null) {
							myjiusuanMoney =  myHyOrder.getJiusuanMoney();
						}
					} else if(myHyOrder.getType() != 6){
						if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
							if(myHyOrder.getJiesuanTuikuan() != null){//4-19
								BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
								myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
							} else {
								myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
							}
						} else {
							myjiusuanMoney = myHyOrder.getJiusuanMoney();
						}
					} else {
						//这个是保险订单，用外卖价
						//2019-7-31
						if(myHyOrder.getBaoxianWaimaiTuikuan() == null){
							myjiusuanMoney = myHyOrder.getWaimaiMoney();
						} else {
							myjiusuanMoney = myHyOrder.getWaimaiMoney().subtract(myHyOrder.getBaoxianWaimaiTuikuan());
						}
						//2019-7-31
						
					}
				}
				
				
				mypurchaseMethod = "预充值";//12
				//下面这个不确定 业务逻辑不清晰 先这么做 13
				List<Filter> storeFilter = new ArrayList<Filter>();
				storeFilter.add(Filter.eq("orderId", myHyOrder.getId()));
				storePreSaveList = storePreSaveService.findList(null, storeFilter, null);
				if(!storePreSaveList.isEmpty()){
					myperSaveTime = storePreSaveList.get(0).getDate();//13
					myperSaveID = storePreSaveList.get(0).getId();//14
				} else{
					//myperSaveID = (long) -1;
					myperSaveID = (long)0;
					myperSaveTime = null;
				}
				
				
				
				//TODO
				//15 这个是不对的，这个是电子合同
				//应该从order -- group -- line -- supplier
				//Long contractID = myHyOrder.getContractId();
				mycontractName = null;
				//HySupplierContract hySupplierContract = null;
				HySupplier hySupplier = null;
				//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				if(types != null){
					if(types.equals(0)){//导游租赁就没有供应商
						mycontractName = "";
					}else if(types.equals(1)){//线路 就是之前代码
						Long myGroupId = myHyOrder.getGroupId();
						if(myGroupId != null){
							HyGroup thisHyGroup = hyGroupService.find(myGroupId);
							if(thisHyGroup != null){
								HyLine myHyLine = thisHyGroup.getLine();
								if(myHyLine != null){
									hySupplier = myHyLine.getHySupplier();
									if(hySupplier != null){
										mycontractName = hySupplier.getSupplierName();
									} else{
										mycontractName = "";//hySupplier没有名字
									}
								} else {
									mycontractName = "";//数据库中没有线路信息
								}
							} else {
								mycontractName = "";//数据库中没有团信息
							}
						} else {
							mycontractName = "";//订单表中没有团信息
						}
					}else if(types.equals(2)){//订购门票
						HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodId);
						if(hyTicketSubscribe != null){
							HySupplier hySuppliers = hyTicketSubscribe.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(3)){//酒店
						HyTicketHotel hyTicketHotel = hyTicketHotelRoomService.find(specId).getHyTicketHotel();
						if(hyTicketHotel != null){
							HySupplier hySuppliers = hyTicketHotel.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(4)){//门票
						HyTicketScene hyTicketScene = hyTicketSceneTicketManagementService.find(specId).getHyTicketScene();
						if(hyTicketScene != null){
							HySupplier hySuppliers = hyTicketScene.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(5)){//酒+景
						HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodId);
						if(hyTicketHotelandscene != null){
							HySupplier hySuppliers = hyTicketHotelandscene.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(6)){//保险
						mycontractName = "";
					}else if(types.equals(7)){//签证
						HyVisa hyVisa = hyVisaService.find(prodId);
						if(hyVisa != null){
							HySupplier hySuppliers = hyVisa.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}
				}
				
				
//				if(contractID != null){
//					hySupplierContract = hySupplierContractService.find(contractID);
//					if(hySupplierContract != null){
//						hySupplier = hySupplierContract.getHySupplier();
//						if(hySupplier != null){
//							mycontractName = hySupplier.getSupplierName();
//						} else{
//							mycontractName = "hySupplier没有名字";
//						}
//					} else {
//						mycontractName = "hySupplierContract不存在";
//					}
//				} else {
//					mycontractName = "数据库表中contractID不存在";
//				}
				
				
				//getHyTicketScene().getticketspplier().getsuppliername()
				if(types.equals(0)){
					myjiesuanMoney1 = new BigDecimal(0);
				} else {
					if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
							myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
						} else {
							myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
						}					
					} else {
						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1();
						if(myjiesuanMoney1 == null){
							myjiesuanMoney1 = new BigDecimal(0);//2019-6-21
						}
					}
				}
//				if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
//					if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//						BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//					} else {
//						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
//					}					
//				} else {
//					myjiesuanMoney1 = myHyOrder.getJiesuanMoney1();
//					if(myjiesuanMoney1 == null){
//						myjiesuanMoney1 = new BigDecimal(0);//2019-6-21
//					}
//				}
				
				myOrderNumber = myHyOrder.getOrderNumber();//17
				
				//2019-5-23
				if(myHyOrder.getType() != null){
					if(myHyOrder.getType() != 6){
						refundMoney = myHyOrder.getJiesuanTuikuan();//不是保险订单
					} else {
						refundMoney = myHyOrder.getBaoxianWaimaiTuikuan();//是保险订单
					}
				}
				//2019-5-23
				
				dailyOrderStatisticsViewListItem = new HashMap<String, Object>();
				//之后要把所有的放到map里面，把map打包到list里面
				dailyOrderStatisticsViewListItem.put("myorderNumber", myorderNumber);//1
				dailyOrderStatisticsViewListItem.put("myid", myPn);//2
				dailyOrderStatisticsViewListItem.put("id", myHyOrder.getId());
				dailyOrderStatisticsViewListItem.put("myxianlumingcheng", myxianlumingcheng);//3
				dailyOrderStatisticsViewListItem.put("myxianluType", myxianluType);//4
				dailyOrderStatisticsViewListItem.put("myfatuanDate", myfatuanDate);//5
				dailyOrderStatisticsViewListItem.put("mycreateTime", mycreateTime);//6
				dailyOrderStatisticsViewListItem.put("mycontact", mycontact);//7
				dailyOrderStatisticsViewListItem.put("mypeopleNumber", mypeopleNumber);//8
				dailyOrderStatisticsViewListItem.put("myoperatorName", myoperatorName);//9
				dailyOrderStatisticsViewListItem.put("mystoreId", mystoreId);//10
				dailyOrderStatisticsViewListItem.put("myjiusuanMoney", myjiusuanMoney.add(refundMoney));//11 2019-8-3
				dailyOrderStatisticsViewListItem.put("mypurchaseMethod", mypurchaseMethod);//12
				dailyOrderStatisticsViewListItem.put("myperSaveTime", myperSaveTime);//13
				dailyOrderStatisticsViewListItem.put("myperSaveID", myperSaveID);//14
				dailyOrderStatisticsViewListItem.put("mycontractName", mycontractName);//15
				dailyOrderStatisticsViewListItem.put("myjiesuanMoney1", myjiesuanMoney1);//16
				dailyOrderStatisticsViewListItem.put("myOrderNumber", myOrderNumber);//17
				dailyOrderStatisticsViewListItem.put("refundMoney", refundMoney);//18 2019-5-23
				dailyOrderStatisticsViewListItem.put("insuranceMoney", myjiusuanMoney.subtract(myjiesuanMoney1));//18 2019-6-14
				dailyOrderStatisticsViewListItem.put("area", store.getHyArea().getFullName());//18 2019-8-4
				dailyOrderStatisticsViewListItem.put("storeType", store.getStoreType()==0?"虹宇门店":"非虹宇门店");//18 2019-8-4
				//dailyOrderStatisticsViewListItem.put("totalMoney", totalMoney);//18 前端觉得改名麻烦
				dailyOrderStatisticsViewList.add(dailyOrderStatisticsViewListItem);
			}
			dailyOrderStatisticsViewListItemTotal.put("mypeopleNumber", totalPeopleNumber);//5-17
			dailyOrderStatisticsViewListItemTotal.put("mystoreId", "合计金额");
			dailyOrderStatisticsViewListItemTotal.put("myjiusuanMoney", totalMoney.add(totalRefundMoney));//18 2019-8-3
			dailyOrderStatisticsViewListItemTotal.put("myjiesuanMoney1", totalMyjiesuanMoney1);//2019-6-20
			dailyOrderStatisticsViewListItemTotal.put("insuranceMoney", totalMoney.subtract(totalMyjiesuanMoney1));//2019-6-20
			dailyOrderStatisticsViewListItemTotal.put("refundMoney", totalRefundMoney);//2019-6-20
			dailyOrderStatisticsViewList.add(dailyOrderStatisticsViewListItemTotal);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			return j;
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
		j.setObj(dailyOrderStatisticsViewList);
		//返回json数据 返回数据的名称按照my+excel上面写的名称为准
		//比如第一张表第一项：myorderNumber 第二项：myid 最后一项：myjiesuanMoney1
		//第二张表也一样： myname mycertificate
		return j;	
	}
	
	
	

	@RequestMapping(value = "detail") // 2 详情页
	public Json dailyOrderStatisticsDetail(Long ID) {
		//和第一个接口一样的
		Json j = new Json();
		
		//用来装下面的数据，需要一个ArrayList来存放一行数据
		List<HashMap<String, Object>> personalInformationList = new ArrayList<>();
		HashMap<String, Object> personalInformationListItem;
		
		try {
			
			String myname;//姓名
			String mycertificate;//身份证号
			String myphone;//电话
			Integer mytype;//游客类型
			//2019-4-13
			String myinsuranceId = "";//保险(其实要查出来保险的名字)
			
			//根据ID去找Item表，从Item表找customer表
			HyOrder myID = hyOrderService.find(ID);
			List<Filter> itemFilter = new ArrayList<Filter>();
			itemFilter.add(Filter.eq("order", myID));
			List<HyOrderItem> myHyOrderItem= hyOrderItemService.findList(null,itemFilter,null);
			//遍历去找customer
			List<HyOrderCustomer> myHyOrderCustomerAll = new ArrayList<>();
			for(HyOrderItem hyOrderItems: myHyOrderItem){
				//4-19 不退都显示 全部退不显示 部分退要显示
				//首先门票四张退两张这种是改变的orderItem里面的numberOfReturn置位为1，number本身就是1不会变
				//要么number和numberOfReturn一个有数，一个为0
				//要么这两个相等，这种情况是全退了，为了保险还是限制一下
				
				//这种思路的前提是下单时候，一张票一个orderItem，一个Item一个customer
				//不能一个Item多个customer退其中的一个，只能全退，如果不是这么做的就要大改
				
				//逻辑是 全退（number = numberOfReturn）不显示 部分退在这里就相当于全退一个意思（number = numberOfReturn）
				//就是说这两个不相等再显示呗
				if(hyOrderItems.getNumber()!=null && hyOrderItems.getNumberOfReturn()!=null && !hyOrderItems.getNumber().equals(hyOrderItems.getNumberOfReturn())){
					List<HyOrderCustomer> myHyOrderCustomer = new ArrayList<>();
					List<Filter> customerFilter = new ArrayList<Filter>();
					customerFilter.add(Filter.eq("orderItem", hyOrderItems));
					myHyOrderCustomer = hyOrderCustomerService.findList(null, customerFilter, null);
					myHyOrderCustomerAll.addAll(myHyOrderCustomer);
					customerFilter.clear();
				}
			}
			for(HyOrderCustomer hyOrderCustomers : myHyOrderCustomerAll){
				myname = hyOrderCustomers.getName();
				mycertificate = hyOrderCustomers.getCertificate();
				myphone = hyOrderCustomers.getPhone();
				mytype = hyOrderCustomers.getType();
				if(hyOrderCustomers.getInsuranceId()!=null){
					myinsuranceId = insuranceService.find(hyOrderCustomers.getInsuranceId()).getRemark();
				}
				//放到Map里面
				personalInformationListItem = new HashMap<String, Object>();
				personalInformationListItem.put("myname", myname);
				personalInformationListItem.put("mycertificate", mycertificate);
				personalInformationListItem.put("myphone", myphone);
				personalInformationListItem.put("mytype", mytype);
				personalInformationListItem.put("myinsuranceId", myinsuranceId);
				//放到List里面
				personalInformationList.add(personalInformationListItem);
			}
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());		
			return j;	
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
		j.setObj(personalInformationList);
		return j;	
	}
	
	
	@RequestMapping(value = "view/excel") // 3 列表页的excel
	public void dailyOrderStatisticsViewExcel(String storeName, String operatorName, String dateOfDeparture, 
			String dateOfPlaceOrder,String endOfPlaceOrder, String ID, String theOrderNumber, Integer myType, 
			Long areaId, Integer storeType, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Json j = new Json();
		//把results传进去
		List<Wrap> results = new ArrayList<>();
		Date dateOfDepartureDate = DateUtil.stringToDate(dateOfDeparture, DateUtil.YYYY_MM_DD);
		
		try {

			//之后要放的数据
			String myorderNumber;//1.订单编号
			Long myid;//2.产品ID
			//2019-4-16
			String myPn = "";//数据库中没有数据就是空
			//2019-4-16
			String myxianlumingcheng = null;//3.线路名称
			String myxianluType;//4.线路类型 把数字转化成字符串
			Integer myxianluTypeNumber;//为了接收线路类型
			Date myfatuanDate;//5.发团日期
			Date mycreateTime;//6.下单日期
			String mycontact;//7.联系人名字
			Integer mypeopleNumber;//8.人数
			String myoperatorName = null;//9.操作人
			String mystoreId = null;//10.其实是用store的ID去查找名字
			BigDecimal myjiusuanMoney = null;//11.订单金额
			String mypurchaseMethod;//12.支付方式
			Date myperSaveTime = null;//13.支付时间
			Long myperSaveID = null;//14.交易流水号
			String mycontractName;//15.供应商
			BigDecimal myjiesuanMoney1;//16.供应商结算价
			//2019-4-16
			String myOrderNumber;//17.订单号
			//2019-4-16
			BigDecimal totalMoney = new BigDecimal("0");//18.合计金额 2019-5-14
			Integer totalPeopleNumber = 0;//2019-5-17 合计人数
			BigDecimal refundMoney = null;//2019-5-23 退款金额
			BigDecimal totalMyjiesuanMoney1 = new BigDecimal("0");//2019-6-20 供应商总计金额
			BigDecimal totalRefundMoney = new BigDecimal("0");//2019-6-20 退款合计金额

			 
			//用来进行初始化筛选
			List<Filter> orderFilter = new ArrayList<Filter>();
			
			//！！！注意 先根据筛选条件选定order的项，再根据order去找各个表拼成一个大表
			List<Store> fddContractInfo = new ArrayList<>();
			List<HyAdmin> operatorInfo = new ArrayList<>();
			//每一次循环得到的
			List<HyOrder> orderInfo = new ArrayList<>();
			//总的，每次循环得到的放到总的里面
			List<HyOrder> orderInfoAll = new ArrayList<>();
			List<StorePreSave> storePreSaveList = new ArrayList<>();
			
			//先容错 没有填写默认就查找全部
			if(storeName != null){
				//输入的名字在另外一张表里面，根据名字找到ID
				//根据找到的ID去在order表里面找对应的ID（hy_contract去这里找）
				//？？？存疑 不清楚用的是哪张表
				//或者是HySupplierContract？
				List<Filter> contractFilter = new ArrayList<Filter>();
				contractFilter.add(Filter.eq("storeName", storeName));//id=141
				fddContractInfo = storeService.findList(null, contractFilter, null);
			}
			if(operatorName != null){
				//输入的名字在另外一张表里面，根据名字找到ID
				//根据找到的ID去在order表里面找对应的operator（HyAdmin类型）
				List<Filter> adminFilter = new ArrayList<Filter>();
				adminFilter.add(Filter.eq("name", operatorName));//很多
				operatorInfo = hyAdminService.findList(null, adminFilter, null);
			}
			
			if(!operatorInfo.isEmpty()){
				//上面拿到的一个条件 循环用find去找项
				for(HyAdmin myHyAdmin : operatorInfo){
					//遍历每一项 把ID拿出来筛选这里的HyOrder
					orderFilter.add(Filter.eq("operator", myHyAdmin));
					//想用“或”的方法查找有一些难
					if(!fddContractInfo.isEmpty()){
						for(Store fddContractItem : fddContractInfo){
							//简单筛选条件加在内层
							if(dateOfDepartureDate != null){
								orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
							}
							if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
//								String previousDay = DateUtil.getThePreviousDay(dateOfPlaceOrder);
								orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
								orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
							}
//							if(ID != null){
//								orderFilter.add(Filter.eq("id", ID));
//							}
							
							//!!!!这里应该是storeID
							orderFilter.add(Filter.eq("storeId", fddContractItem.getId()));
							
							//***** 2019-4-16
							if(theOrderNumber != null){
								orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
							}
							if(myType != null){
								orderFilter.add(Filter.eq("type", myType));
							}
							orderFilter.add(Filter.eq("paystatus", 1));
							//***** 2019-4-16
							
							//***** 2019-4-19
							Collection<Integer> list = new ArrayList<>();
							Collections.addAll(list,3,9,10);
							orderFilter.add(Filter.in("status", list));
							//orderFilter.add(Filter.eq("status", 3));
							//***** 2019-4-19
							
							//&&&&& 4-19 night
							orderFilter.add(Filter.eq("source", 0));
							//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
							
							List<Order> orders=new ArrayList<>();
							orders.add(Order.desc("createtime"));
							
							//2019-8-21
							if(storeType != null){
								orderFilter.add(Filter.eq("storeType", storeType));
							}
							//2019-8-21
							//先查出来符合条件的hyOrder
							orderInfo = hyOrderService.findList(null, orderFilter, orders);
							//加到总的容器中
							orderInfoAll.addAll(orderInfo);
						}
						orderFilter.clear();//清除一下，这里
					} else {
						
						if(dateOfDepartureDate != null){
							orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
						}
						if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
							orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
							orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
						}
//						if(ID != null){
//							orderFilter.add(Filter.eq("id", ID));
//						}
						
						//***** 2019-4-16
						if(theOrderNumber != null){
							orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
						}
						if(myType != null){
							orderFilter.add(Filter.eq("type", myType));
						}
						orderFilter.add(Filter.eq("paystatus", 1));
						//***** 2019-4-16
						
						//***** 2019-4-19
						//orderFilter.add(Filter.eq("status", 3));
						Collection<Integer> list = new ArrayList<>();
						Collections.addAll(list,3,9,10);
						orderFilter.add(Filter.in("status", list));
						//***** 2019-4-19
						
						//&&&&& 4-19 night
						orderFilter.add(Filter.eq("source", 0));
						//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
						
						List<Order> orders=new ArrayList<>();
						orders.add(Order.desc("createtime"));
						
						//2019-8-21
						if(storeType != null){
							orderFilter.add(Filter.eq("storeType", storeType));
						}
						//2019-8-21
						//先查出来符合条件的hyOrder
						orderInfo = hyOrderService.findList(null, orderFilter, orders);
						//加到总的容器中
						orderInfoAll.addAll(orderInfo);
						
					}
					orderFilter.clear();//很容易弄错
				}
			} else{
				
				
				//另一个是不是为空
				if(!fddContractInfo.isEmpty()){
					for(Store fddContractItem : fddContractInfo){
						
						
						//4-28 之前错了,这里补上
						if(dateOfDepartureDate != null){
							orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
						}
						if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
							orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
							orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
						}
//						if(ID != null){
//							orderFilter.add(Filter.eq("id", ID));
//						}
						
						//2019-4-16
						if(theOrderNumber != null){
							orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
						}
						if(myType != null){
							orderFilter.add(Filter.eq("type", myType));
						}
						orderFilter.add(Filter.eq("paystatus", 1));
						//2019-4-16
						
						//***** 2019-4-19
						//orderFilter.add(Filter.eq("status", 3));
						Collection<Integer> list = new ArrayList<>();
						Collections.addAll(list,3,9,10);
						orderFilter.add(Filter.in("status", list));
						//***** 2019-4-19
						
						//&&&&& 4-19 night
						orderFilter.add(Filter.eq("source", 0));
						//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
						//4-28 之前错了,这里补上
						
						//????不确定是这个意思
						orderFilter.add(Filter.eq("storeId", fddContractItem.getId()));
						
						List<Order> orders=new ArrayList<>();
						orders.add(Order.desc("createtime"));
						
						//2019-8-21
						if(storeType != null){
							orderFilter.add(Filter.eq("storeType", storeType));
						}
						//2019-8-21
						//先查出来符合条件的hyOrder
						orderInfo = hyOrderService.findList(null, orderFilter, orders);
						//加到总的容器中
						orderInfoAll.addAll(orderInfo);
						orderFilter.clear();//清除一下，这里
					}
				} else {
					
					//4-28 之前错了,这里补上
					if(dateOfDepartureDate != null){
						orderFilter.add(Filter.eq("fatuandate", dateOfDepartureDate));
					}
					if(dateOfPlaceOrder != null && endOfPlaceOrder != null){
						orderFilter.add(Filter.gt("createtime", DateUtil.stringToDate(dateOfPlaceOrder+" 00:00:00", DateUtil.YYYYMMDDHHMMSS)));
						orderFilter.add(Filter.lt("createtime",  DateUtil.stringToDate(endOfPlaceOrder+" 23:59:59", DateUtil.YYYYMMDDHHMMSS)));
					}
//					if(ID != null){
//						orderFilter.add(Filter.eq("id", ID));
//					}
					
					//2019-4-16
					if(theOrderNumber != null){
						orderFilter.add(Filter.eq("orderNumber", theOrderNumber));
					}
					if(myType != null){
						orderFilter.add(Filter.eq("type", myType));
					}
					orderFilter.add(Filter.eq("paystatus", 1));
					//2019-4-16
					
					//***** 2019-4-19
					//ArrayList<Integer> stausNumber = new ArrayList<>();
					//orderFilter.add(Filter.eq("status", 3));
					Collection<Integer> list = new ArrayList<>();
					Collections.addAll(list,3,9,10);
					orderFilter.add(Filter.in("status", list));
					//***** 2019-4-19
					
					//&&&&& 4-19 night
					orderFilter.add(Filter.eq("source", 0));
					//orderFilter.add(Filter.ne("refundstatus", 2));退款的也要 2019-5-24
					//4-28 之前错了,这里补上
					
					List<Order> orders=new ArrayList<>();
					orders.add(Order.desc("createtime"));
					
					//2019-8-21
					if(storeType != null){
						orderFilter.add(Filter.eq("storeType", storeType));
					}
					//2019-8-21
					//先查出来符合条件的hyOrder
					orderInfo = hyOrderService.findList(null, orderFilter, orders);
					//加到总的容器中
					orderInfoAll.addAll(orderInfo);
				}
				
			}
			
			
			Long specIdelete = null;
			Long prodIdelete = null;
			//如果非得按照pn码筛选
			if(ID != null){
				//4-17 按照ID也就是line表中的pn码筛选
				//!!!!是不是util包？
//				Iterator<HyOrder> iter = orderInfoAll.iterator();  
//				while(iter.hasNext()){
//					Long thisID = null;
//					thisID = iter.next().getGroupId();//2
//					if(thisID != null){
//						HyGroup myHyGroupPn = hyGroupService.find(thisID);
//						if(myHyGroupPn != null){
//							//hyGroupService.find(id)
//							HyLine myHyLinePn = myHyGroupPn.getLine();
//							if(myHyLinePn != null){
//								String Pn = null;
//								Pn = myHyLinePn.getPn();
//								//这里判断是否等于我们要筛选的pn，不等于就删去
//								if(!Pn.equals(ID)){
//									//这个对么？
//									iter.remove();
//								}
//								
//							} else {
//								//2019-5-6
//								iter.remove();
//							}
//						} else {
//							//2019-5-6
//							iter.remove();
//						}
//					} else {
//						//2019-5-6
//						iter.remove();
//					}
//				}
				
				//4-17 按照ID也就是line表中的pn码筛选
				//!!!!是不是util包？
				Iterator<HyOrder> iter = orderInfoAll.iterator();  

				while(iter.hasNext()){
					HyOrder myHyorder = iter.next();
					if(myHyorder.getOrderItems()!= null){
						//这样做的话，可能拿到的是null
						specIdelete = myHyorder.getOrderItems().get(0).getSpecificationId();
						prodIdelete = myHyorder.getOrderItems().get(0).getProductId();
					}
				
					
					//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
					if(myHyorder.getType() != null){
						if(myHyorder.getType() == 0){//导游租赁
							
							if(prodIdelete != null){
								Guide myGuide = guideService.find(prodIdelete);
								if(myGuide != null){
									String thePn = myGuide.getGuideSn();
									if(thePn != null){
										if(!thePn.equals(ID)){
											iter.remove();
										}
									} else {
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 1){//线路
							
							
							Long thisID = null;
							thisID = myHyorder.getGroupId();//2
							if(thisID != null){
								HyGroup myHyGroupPn = hyGroupService.find(thisID);
								if(myHyGroupPn != null){
									//hyGroupService.find(id)
									HyLine myHyLinePn = myHyGroupPn.getLine();
									if(myHyLinePn != null){
										String Pn = null;
										Pn = myHyLinePn.getPn();
										//这里判断是否等于我们要筛选的pn，不等于就删去
										if(!Pn.equals(ID)){
											//这个对么？
											iter.remove();
										}
									} else {
										//2019-5-6 没有GroupID的订单都不显示
										iter.remove();
									}
								} else {
									//2019-5-6 没有GroupID的订单都不显示
									iter.remove();
								}
							} else {
								//2019-5-6 没有GroupID的订单都不显示
								iter.remove();
							}
							
							
						} else if(myHyorder.getType() == 2){//认购门票
							
							if(prodIdelete != null){
								HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodIdelete);
								if(hyTicketSubscribe != null){
									String thePn = hyTicketSubscribe.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 3){//酒店
							
							if(specIdelete != null){
								HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(specIdelete);
								if(hyTicketHotelRoom != null){
									String thePn = hyTicketHotelRoom.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 4){//门票
							
							if(specIdelete != null){
								HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(specIdelete);
								if(hyTicketSceneTicketManagement != null){
									String thePn = hyTicketSceneTicketManagement.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 5){//酒+景
							
							if(prodIdelete != null){
								//TODO
								HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodIdelete);
								if(hyTicketHotelandscene != null){
									String thePn = hyTicketHotelandscene.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 6){//保险
							
							if(prodIdelete != null){
								Insurance insurance = insuranceService.find(prodIdelete);
								if(insurance != null){
									String thePn = insurance.getInsuranceCode();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
							
						} else if(myHyorder.getType() == 7){//签证
							
							if(prodIdelete != null){
								HyVisa hyVisa = hyVisaService.find(prodIdelete);
								if(hyVisa != null){
									String thePn = hyVisa.getProductId();
									if(!thePn.equals(ID)){
										iter.remove();
									}
								} else {
									iter.remove();
								}
							} else {
								iter.remove();
							}
						}
						
					}
				
				}
				
			}
			
			
			//2019-5-10 让已经消团的不显示
			Iterator<HyOrder> iterXiaotuan = orderInfoAll.iterator(); 
			while(iterXiaotuan.hasNext()){
				Long thisID = null;
				thisID = iterXiaotuan.next().getGroupId();//2
				if(thisID != null){
					HyGroup myHyGroup = hyGroupService.find(thisID);
					if(myHyGroup != null){
						GroupStateEnum wrongStatus = GroupStateEnum.yiquxiao;
						GroupStateEnum thisStatus = myHyGroup.getGroupState();
						if(wrongStatus == thisStatus){
							iterXiaotuan.remove();
						}
					}
				}
			}
			//2019-5-10 让已经消团的不显示
			
			//2019-5-14
			BigDecimal moneyThisTurn = null;
			BigDecimal moneyThisTurn2 = null;//2019-6-20
			BigDecimal moneyThisTurnRefund = null;//2019-6-20
			
			if(orderInfoAll != null)
				for(HyOrder myHyOrder : orderInfoAll){
					
					//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
					Store store = storeService.find(myHyOrder.getStoreId());
					HyArea myHyArea = new HyArea();
					HyArea myHyAreaP = new HyArea();
					Long NID = null;
					Long PID = null;
					
					if(store==null){
						throw new Exception("订单 "+myHyOrder.getOrderNumber()+" 的门店找不到");
					} else {
						myHyArea = store.getHyArea();
						if(myHyArea != null){
							myHyAreaP = myHyArea.getHyArea();
							NID = myHyArea.getId();
							if(myHyAreaP != null){
								PID = myHyAreaP.getId();
							}
						}
					}
					
					if (areaId != null){
						if((NID!=null && !NID.equals(areaId)) && (PID!=null && !PID.equals(areaId))){
							continue;//如果同时不相等，就进入下一轮
						}
					}
					
					//2019-8-18
					if(myHyOrder.getStoreType() == null){
						continue;
					}
					//这个不用改，是对的
					if(myHyOrder.getStoreType() != null && storeType != null){
						if(!myHyOrder.getStoreType().equals(storeType)){
							continue;
						}
					}
					//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
					
					//2019-7-16
					if(myHyOrder.getType() != null){
						
						if(myHyOrder.getType() == 0){
							if(myHyOrder.getTip() != null && myHyOrder.getJiusuanMoney() != null){
								moneyThisTurn =  myHyOrder.getJiusuanMoney().add(myHyOrder.getTip());
							} else if(myHyOrder.getTip() == null) {
								moneyThisTurn =  myHyOrder.getJiusuanMoney();
							}
						} else if(myHyOrder.getType() != 6){
							if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
								if(myHyOrder.getJiesuanTuikuan() != null){//4-19
									BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
									moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
								} else {
									moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
								}
							} else {
								moneyThisTurn = myHyOrder.getJiusuanMoney();
							}
						} else {
							//这个是保险订单，用外卖价
							//2019-7-31
							if(myHyOrder.getBaoxianWaimaiTuikuan() == null){
								moneyThisTurn = myHyOrder.getWaimaiMoney();
							} else {
								moneyThisTurn = myHyOrder.getWaimaiMoney().subtract(myHyOrder.getBaoxianWaimaiTuikuan());
							}
							//2019-7-31
							//moneyThisTurn = myHyOrder.getWaimaiMoney();
						}
					}
					//2019-7-16
					
//					if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
//						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//							moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//						} else {
//							moneyThisTurn = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
//						}
//					} else {
//						moneyThisTurn = myHyOrder.getJiusuanMoney();
//					}
					
					
					if(myHyOrder.getType() != null){
						if(myHyOrder.getType().equals(0)){
							moneyThisTurn2 = new BigDecimal(0);
						} else {
							if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
								if(myHyOrder.getJiesuanTuikuan() != null){//4-19
									BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
									moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
								} else {
									moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
								}					
							} else {
								moneyThisTurn2 = myHyOrder.getJiesuanMoney1();
								if(moneyThisTurn2 == null){
									moneyThisTurn2 = new BigDecimal(0);//2019-6-21
								}
							}
						}
					}
					
//					//2019-6-20
//					if(myHyOrder.getJiesuanMoney1()!=null && myHyOrder.getStoreFanLi()!=null){
//						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//							moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//						} else {
//							moneyThisTurn2 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
//						}					
//					} else {
//						moneyThisTurn2 = myHyOrder.getJiesuanMoney1();
//						if(moneyThisTurn2 == null){
//							moneyThisTurn2 = new BigDecimal(0);//2019-6-21
//						}
//					}
					//2019-6-20
					
					//2019-6-20
					if(myHyOrder.getType() != null){
						if(myHyOrder.getType() != 6){
							moneyThisTurnRefund = myHyOrder.getJiesuanTuikuan();//不是保险订单
						} else {
							moneyThisTurnRefund = myHyOrder.getBaoxianWaimaiTuikuan();//是保险订单
						}
					}
					//2019-6-20
				
					//2019-6-20
					totalMyjiesuanMoney1 = totalMyjiesuanMoney1.add(moneyThisTurn2);
					totalRefundMoney = totalRefundMoney.add(moneyThisTurnRefund);
					//2019-6-20
					
					//把每次的钱拿出来，都加在一起
					totalMoney = totalMoney.add(moneyThisTurn);
					
					//2019-5-17人数加起来
					if(myHyOrder.getPeople() != null)
						totalPeopleNumber = totalPeopleNumber + myHyOrder.getPeople();
				}
			//2019-5-14
			
			
			//现在得到了所有的HyOrder类
			//遍历所有得到的HyOrder，找到所有要传给前端的信息
			Long specId = null;
			Long prodId = null;
			
			if(orderInfoAll != null)
			for(HyOrder myHyOrder : orderInfoAll){
				
				//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
				Store store = storeService.find(myHyOrder.getStoreId());
				HyArea myHyArea = new HyArea();
				HyArea myHyAreaP = new HyArea();
				Long NID = null;
				Long PID = null;
				
				if(store==null){
					throw new Exception("订单 "+myHyOrder.getOrderNumber()+" 的门店找不到");
				} else {
					myHyArea = store.getHyArea();
					if(myHyArea != null){
						myHyAreaP = myHyArea.getHyArea();
						NID = myHyArea.getId();
						if(myHyAreaP != null){
							PID = myHyAreaP.getId();
						}
					}
				}
				
				if (areaId != null){
					if((NID!=null && !NID.equals(areaId)) && (PID!=null && !PID.equals(areaId))){
						continue;//如果同时不相等，就进入下一轮
					}
				}
				
				//2019-8-18
				if(myHyOrder.getStoreType() == null){
					continue;
				}
				//这个不用改，是对的
				if(myHyOrder.getStoreType() != null && storeType != null){
					if(!myHyOrder.getStoreType().equals(storeType)){
						continue;
					}
				}
				//我也不知道我写的对不对，又测试不了，但是还是要改 2019-8-3
				
				//2019-4-16
				//能赋值的赋值
				myorderNumber = myHyOrder.getOrderNumber();//1
				
				myid = myHyOrder.getGroupId();//2
//				if(myid != null){
//					HyGroup myHyGroup = hyGroupService.find(myid);
//					if(myHyGroup != null){
//						//hyGroupService.find(id)
//						HyLine myHyLine = myHyGroup.getLine();
//						if(myHyLine != null){
//							myPn = myHyLine.getPn();
//						}
//					}
//				}
				//2019-4-16
				
				if(myHyOrder.getOrderItems()!= null){
					//这样做的话，可能拿到的是null
					specId = myHyOrder.getOrderItems().get(0).getSpecificationId();
					prodId = myHyOrder.getOrderItems().get(0).getProductId();
				}

				
				//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				Integer types = myHyOrder.getType();
				if(types != null){
					if(types.equals(0)){//导游租赁
						//老师自己下了一单，也就是order_number = 2019062100007
						//其中产品编号是导游编号，产品名称是导游名称，订单金额就是刚才报名的240+10元小费
						if(prodId != null){
							Guide myGuide = guideService.find(prodId);
							if(myGuide != null){
								myPn = myGuide.getGuideSn();
							}
						}	
						
					}else if(types.equals(1)){//线路
						
						if(myid != null){
							HyGroup myHyGroup = hyGroupService.find(myid);
							if(myHyGroup != null){
								//hyGroupService.find(id)
								HyLine myHyLine = myHyGroup.getLine();
								if(myHyLine != null){
									myPn = myHyLine.getPn();
								}
							}
						}
						
					}else if(types.equals(2)){//认购门票
						if(prodId != null){
							HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodId);
							if(hyTicketSubscribe != null){
								myPn = hyTicketSubscribe.getProductId();
							}
						}
					}else if(types.equals(3)){//酒店
						if(specId != null){
							HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(specId);
							if(hyTicketHotelRoom != null){
								myPn = hyTicketHotelRoom.getProductId();
							}
						}
					}else if(types.equals(4)){//门票
						
						if(specId != null){
							HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(specId);
							if(hyTicketSceneTicketManagement != null){
								myPn = hyTicketSceneTicketManagement.getProductId();
							}
						}
						
					}else if(types.equals(5)){//酒+景
						if(prodId != null){
							//TODO
							HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodId);
							if(hyTicketHotelandscene != null){
								myPn = hyTicketHotelandscene.getProductId();
							}
						}
					}else if(types.equals(6)){//保险
						if(prodId != null){
							Insurance insurance = insuranceService.find(prodId);
							if(insurance != null){
								myPn = insurance.getInsuranceCode();
							}
						}
					}else if(types.equals(7)){//签证
						if(prodId != null){
							HyVisa hyVisa = hyVisaService.find(prodId);
							if(hyVisa != null){
								myPn = hyVisa.getProductId();
							}
						}
					}
				}

				
				//这里的myPn是产品编号，是后来找建勇问的
				//现在需要把别的产品加进去 
				//建勇写的酒店 是从setSpecificationId里面拿 剩下的是从酒店里拿 后面的图
				//酒加景是：从productId里面拿 前面的图
				//签证是：hy_order找到对应的hy_orderitem，item表中有productId，就是hyVisa的主键ID，根据这个id找到HyVisa,HyVisa里面有pn
				
//				List<Filter> myHyLineFilter = new ArrayList<Filter>();
//				myHyLineFilter.add(Filter.eq("id", myid));//id=141
//				hyLineService.findList(null, myHyLineFilter, null);
				
				if(types.equals(0)){
					if(prodId != null){
						Guide myGuide = guideService.find(prodId);
						if(myGuide != null){
							myxianlumingcheng = myGuide.getName();
						}
					}	
				} else {
					myxianlumingcheng = myHyOrder.getName();//3
				}
				
				
				
				//能赋值的赋值
				//myorderNumber = myHyOrder.getOrderNumber();//1
				//myid = myHyOrder.getId();//2
				//myxianlumingcheng = myHyOrder.getName();//3
				myxianluTypeNumber = myHyOrder.getXianlutype();//4
				if(myxianluTypeNumber!=null){
					if(myxianluTypeNumber == 0){//4
						myxianluType = "汽车散客";
					}
					else if(myxianluTypeNumber == 1){
						myxianluType = "汽车团客";
					}
					else if(myxianluTypeNumber == 2){
						myxianluType = "国内散客";
					}
					else if(myxianluTypeNumber == 3){
						myxianluType = "国内团客";
					}
					else if(myxianluTypeNumber == 4){
						myxianluType = "出境散客";
					}
					else if(myxianluTypeNumber == 5){
						myxianluType = "出境团客";
					}else{
						myxianluType = "没提过";
					}
				} else {
					myxianluType = "无";
				}
				myfatuanDate = myHyOrder.getFatuandate();//5
				mycreateTime = myHyOrder.getCreatetime();//6
				mycontact = myHyOrder.getContact();//7
				mypeopleNumber = myHyOrder.getPeople();//8
				//myoperatorName = myHyOrder.getOperator().getName();//9
				//2019-4-16
				HyAdmin myAdmin= myHyOrder.getOperator();
				if(myAdmin!=null){
					if(myAdmin.getName()!=null){
						myoperatorName = myAdmin.getName();//9
					}
				}
				//mystoreId = storeService.find(myHyOrder.getStoreId()).getStoreName();//10
				if(myHyOrder.getStoreId()!=null){
					mystoreId = storeService.find(myHyOrder.getStoreId()).getStoreName();//10
				}
				//2019-4-16
//				if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
//					if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//						BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//						myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//					} else {
//						myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
//					}
//				} else {
//					myjiusuanMoney = myHyOrder.getJiusuanMoney();
//				}
				
				if(myHyOrder.getType() != null){
					
					
					if(myHyOrder.getType() == 0){
						if(myHyOrder.getTip() != null && myHyOrder.getJiusuanMoney() != null){
							myjiusuanMoney =  myHyOrder.getJiusuanMoney().add(myHyOrder.getTip());
						} else if(myHyOrder.getTip() == null) {
							myjiusuanMoney =  myHyOrder.getJiusuanMoney();
						}
					} else if(myHyOrder.getType() != 6){//除了保险订单以外的其他订单
						if(myHyOrder.getJiusuanMoney() != null && myHyOrder.getStoreFanLi() != null){
							if(myHyOrder.getJiesuanTuikuan() != null){//4-19
								BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
								myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
							} else {
								myjiusuanMoney = myHyOrder.getJiusuanMoney().subtract(myHyOrder.getStoreFanLi());//11
							}
						} else {
							myjiusuanMoney = myHyOrder.getJiusuanMoney();
						}
					} else {
						//这个是保险订单，用外卖价
						//myjiusuanMoney = myHyOrder.getWaimaiMoney();
						//2019-7-31
						if(myHyOrder.getBaoxianWaimaiTuikuan() == null){
							myjiusuanMoney = myHyOrder.getWaimaiMoney();
						} else {
							myjiusuanMoney = myHyOrder.getWaimaiMoney().subtract(myHyOrder.getBaoxianWaimaiTuikuan());
						}
						//2019-7-31
					}
				}
				
				
				mypurchaseMethod = "预充值";//12
				//下面这个不确定 业务逻辑不清晰 先这么做 13
				List<Filter> storeFilter = new ArrayList<Filter>();
				storeFilter.add(Filter.eq("orderId", myHyOrder.getId()));
				storePreSaveList = storePreSaveService.findList(null, storeFilter, null);
				if(!storePreSaveList.isEmpty()){
					myperSaveTime = storePreSaveList.get(0).getDate();//13
					myperSaveID = storePreSaveList.get(0).getId();//14
				} else {
					myperSaveTime = null;
//					myperSaveID = (long) -1;
					myperSaveID = (long) 0;
				}
				
				
				//TODO
				//15
				//应该从order -- group -- line -- supplier
				//Long contractID = myHyOrder.getContractId();
//				mycontractName = null;
//				//HySupplierContract hySupplierContract = null;
//				HySupplier hySupplier = null;
//				
//				Long myGroupId = myHyOrder.getGroupId();
//				if(myGroupId != null){
//					HyGroup thisHyGroup = hyGroupService.find(myGroupId);
//					if(thisHyGroup != null){
//						HyLine myHyLine = thisHyGroup.getLine();
//						if(myHyLine != null){
//							hySupplier = myHyLine.getHySupplier();
//							if(hySupplier != null){
//								mycontractName = hySupplier.getSupplierName();
//							} else{
//								mycontractName = "";//hySupplier没有名字
//							}
//						} else {
//							mycontractName = "";//数据库中没有线路信息
//						}
//					} else {
//						mycontractName = "";//数据库中没有团信息
//					}
//				} else {
//					mycontractName = "";//订单表中没有团信息
//				}
				//TODO
				
				//15 这个是不对的，这个是电子合同
				//应该从order -- group -- line -- supplier
				//Long contractID = myHyOrder.getContractId();
				mycontractName = null;
				//HySupplierContract hySupplierContract = null;
				HySupplier hySupplier = null;
				//private Integer type; 订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				if(types != null){
					if(types.equals(0)){//导游租赁就没有供应商
						mycontractName = "";
					}else if(types.equals(1)){//线路 就是之前代码
						Long myGroupId = myHyOrder.getGroupId();
						if(myGroupId != null){
							HyGroup thisHyGroup = hyGroupService.find(myGroupId);
							if(thisHyGroup != null){
								HyLine myHyLine = thisHyGroup.getLine();
								if(myHyLine != null){
									hySupplier = myHyLine.getHySupplier();
									if(hySupplier != null){
										mycontractName = hySupplier.getSupplierName();
									} else{
										mycontractName = "";//hySupplier没有名字
									}
								} else {
									mycontractName = "";//数据库中没有线路信息
								}
							} else {
								mycontractName = "";//数据库中没有团信息
							}
						} else {
							mycontractName = "";//订单表中没有团信息
						}
					}else if(types.equals(2)){//订购门票
						HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(prodId);
						if(hyTicketSubscribe != null){
							HySupplier hySuppliers = hyTicketSubscribe.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(3)){//酒店
						HyTicketHotel hyTicketHotel = hyTicketHotelRoomService.find(specId).getHyTicketHotel();
						if(hyTicketHotel != null){
							HySupplier hySuppliers = hyTicketHotel.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(4)){//门票
						HyTicketScene hyTicketScene = hyTicketSceneTicketManagementService.find(specId).getHyTicketScene();
						if(hyTicketScene != null){
							HySupplier hySuppliers = hyTicketScene.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(5)){//酒+景
						HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(prodId);
						if(hyTicketHotelandscene != null){
							HySupplier hySuppliers = hyTicketHotelandscene.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}else if(types.equals(6)){//保险
						mycontractName = "";
					}else if(types.equals(7)){//签证
						HyVisa hyVisa = hyVisaService.find(prodId);
						if(hyVisa != null){
							HySupplier hySuppliers = hyVisa.getTicketSupplier();
							if(hySuppliers != null){
								mycontractName = hySuppliers.getSupplierName();
							}
						}
					}
				}
				
				
				if(types.equals(0)){
					myjiesuanMoney1 = new BigDecimal(0);
				} else {
					if(myHyOrder.getJiesuanMoney1() != null && myHyOrder.getStoreFanLi() != null){
						if(myHyOrder.getJiesuanTuikuan() != null){//4-19
							BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
							myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
						} else {
							myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
						}	
					} else {
						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1();
						if(myjiesuanMoney1 == null){
							myjiesuanMoney1 = new BigDecimal(0);//2019-6-21
						}
					}
				}
//				if(myHyOrder.getJiesuanMoney1() != null && myHyOrder.getStoreFanLi() != null){
//					if(myHyOrder.getJiesuanTuikuan() != null){//4-19
//						BigDecimal todayJiesuanTuikuan = myHyOrder.getJiesuanTuikuan();
//						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi()).subtract(todayJiesuanTuikuan);
//					} else {
//						myjiesuanMoney1 = myHyOrder.getJiesuanMoney1().subtract(myHyOrder.getStoreFanLi());//11
//					}	
//				} else {
//					myjiesuanMoney1 = myHyOrder.getJiesuanMoney1();
//					if(myjiesuanMoney1 == null){
//						myjiesuanMoney1 = new BigDecimal(0);//2019-6-21
//					}
//				}
//				
				
				myOrderNumber = myHyOrder.getOrderNumber();//17

				//2019-5-23
				if(myHyOrder.getType() != null){
					if(myHyOrder.getType() != 6){
						refundMoney = myHyOrder.getJiesuanTuikuan();//不是保险订单
					} else {
						refundMoney = myHyOrder.getBaoxianWaimaiTuikuan();//是保险订单
					}
				}
				//2019-5-23
				
				//新增加部分
				Wrap input = new Wrap();
				input.setMyorderNumber(myorderNumber);//1
				//input.setMyid(myid);//2
				//2019-4-16
				input.setMyPn(myPn);
				//2019-4-16
				input.setMyxianlumingcheng(myxianlumingcheng);//3
				input.setMyxianluType(myxianluType);//4
				input.setMyfatuanDate(myfatuanDate);//5
				input.setMycreateTime(mycreateTime);//6
				input.setMycontact(mycontact);//7
				input.setMypeopleNumber(mypeopleNumber);//8
				input.setMyoperatorName(myoperatorName);//9
				input.setMystoreId(mystoreId);//10
				input.setMyjiusuanMoney(myjiusuanMoney.add(refundMoney));//11 2019-8-3
				input.setMypurchaseMethod(mypurchaseMethod);//12
				input.setMyperSaveTime(myperSaveTime);//13
				input.setMyperSaveID(myperSaveID);//14
				input.setMycontractName(mycontractName);//15
				input.setMyjiesuanMoney1(myjiesuanMoney1);//16
				//2019-4-16
				input.setMyOrderNumber(myOrderNumber);//17
				//2019-4-16
				//2019-5-23
				input.setMyRefundMoney(refundMoney);
				//2019-5-23
				//2019-6-14
				input.setInsuranceMoney(myjiusuanMoney.subtract(myjiesuanMoney1));
				input.setAreaName(store.getHyArea().getFullName());
				input.setStoreType(store.getStoreType()==0?"虹宇门店":"非虹宇门店");
				
				results.add(input);
			}
				
			//2019-5-14 不知道是不是在这里直接给添加上就好了
			//上边循环遍历，把每一个订单显示出来，这里把合计加进去
			Wrap input = new Wrap();
			input.setMystoreId("合计金额");
			input.setMyjiusuanMoney(totalMoney.add(totalRefundMoney));//2019-8-3
			input.setMypeopleNumber(totalPeopleNumber);
			
			//2019-6-20
			input.setInsuranceMoney(totalMoney.subtract(totalMyjiesuanMoney1));
			input.setMyjiesuanMoney1(totalMyjiesuanMoney1);
			input.setMyRefundMoney(totalRefundMoney);
			//2019-6-20
			
			results.add(input);
			//2019-5-14 不知道是不是在这里直接给添加上就好了
			
			//导出Excel的固定套路
			StringBuffer sb2 = new StringBuffer("");
			StringBuffer excelTitle = new StringBuffer("当日订单统计表");
			sb2.append(excelTitle);
			String fileName = "当日订单统计表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "dailyOrderStatisticsView.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
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


	
	@RequestMapping(value = "view/list/all") // 4 得到所有门店的名称
	public Json dailyOrderStatisticsViewExcel(){
		
		Json j = new Json();
		try{
			
			boolean flag = true;
			List<Store> storeList = new ArrayList<>();
			List<HashMap<String, Object>> storeName = new ArrayList<>();
			
			HashMap<String, Object> storeNameAndId;
			
			
			storeList = storeService.findList(null,null,null);
			
			if(!storeList.isEmpty())
			for(Store storeItem : storeList){
				
				String name = storeItem.getStoreName();
				Long id = storeItem.getId();
				
				//遍历看是不是已经有的名字
				if(!storeName.isEmpty())//不为空就遍历
				for(HashMap<String, Object> nameItem : storeName){
					
					String thisName = (String) nameItem.get("name");
					if(thisName.equals(name)){
						flag = false;//不加入
					}
					
					
				}
				//如果是能够加入的
				if(flag == true){
					storeNameAndId = new HashMap<>();
					storeNameAndId.put("name", name);
					storeNameAndId.put("id", id);
					storeName.add(storeNameAndId);
				}
				
				
				flag = true;//如果是false就在这里改一下
				
			}
			
			
			j.setObj(storeName);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			return j;
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
		return j;
	}
	
	
	
	
	
	public static class Wrap{
		String myorderNumber;//1.订单编号
		//Long myid;//2.产品ID
		//2019-4-16
		String myPn = "";//数据库中没有数据就是空
		//2019-4-16
		String myxianlumingcheng;//3.线路名称
		String myxianluType;//4.线路类型 把数字转化成字符串
		Date myfatuanDate;//5.发团日期
		Date mycreateTime;//6.下单日期
		String mycontact;//7.联系人名字
		Integer mypeopleNumber;//8.人数
		String myoperatorName;//9.操作人
		String mystoreId;//10.其实是用store的ID去查找名字
		BigDecimal myjiusuanMoney;//11.订单金额
		String mypurchaseMethod;//12.支付方式
		Date myperSaveTime = null;//13.支付时间
		Long myperSaveID = null;//14.交易流水号
		String mycontractName;//15.供应商
		BigDecimal myjiesuanMoney1;//16.供应商结算价
		//2019-4-16
		String myOrderNumber;//17.订单号
		//2019-4-16
		//2019-5-23
		BigDecimal myRefundMoney = null;
		BigDecimal insuranceMoney = null;//6-14
		//2019-8-4
		String areaName = null;
		String storeType = null;
		
		
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
		public BigDecimal getInsuranceMoney() {
			return insuranceMoney;
		}
		public void setInsuranceMoney(BigDecimal insuranceMoney) {
			this.insuranceMoney = insuranceMoney;
		}
		public BigDecimal getMyRefundMoney() {
			return myRefundMoney;
		}
		public void setMyRefundMoney(BigDecimal myRefundMoney) {
			this.myRefundMoney = myRefundMoney;
		}
		
		//2019-4-16
		public String getMyPn() {
			return myPn;
		}
		public void setMyPn(String myPn) {
			this.myPn = myPn;
		}
		public String getMyOrderNumber() {
			return myOrderNumber;
		}
		public void setMyOrderNumber(String myOrderNumber) {
			this.myOrderNumber = myOrderNumber;
		}
		//2019-4-16
		
		public String getMyorderNumber() {
			return myorderNumber;
		}
		public void setMyorderNumber(String myorderNumber) {
			this.myorderNumber = myorderNumber;
		}
		/**public Long getMyid() {
			return myid;
		}
		public void setMyid(Long myid) {
			this.myid = myid;
		}**/
		public String getMyxianlumingcheng() {
			return myxianlumingcheng;
		}
		public void setMyxianlumingcheng(String myxianlumingcheng) {
			this.myxianlumingcheng = myxianlumingcheng;
		}
		public String getMyxianluType() {
			return myxianluType;
		}
		public void setMyxianluType(String myxianluType) {
			this.myxianluType = myxianluType;
		}
		public Date getMyfatuanDate() {
			return myfatuanDate;
		}
		public void setMyfatuanDate(Date myfatuanDate) {
			this.myfatuanDate = myfatuanDate;
		}
		public Date getMycreateTime() {
			return mycreateTime;
		}
		public void setMycreateTime(Date mycreateTime) {
			this.mycreateTime = mycreateTime;
		}
		public String getMycontact() {
			return mycontact;
		}
		public void setMycontact(String mycontact) {
			this.mycontact = mycontact;
		}
		public Integer getMypeopleNumber() {
			return mypeopleNumber;
		}
		public void setMypeopleNumber(Integer mypeopleNumber) {
			this.mypeopleNumber = mypeopleNumber;
		}
		public String getMyoperatorName() {
			return myoperatorName;
		}
		public void setMyoperatorName(String myoperatorName) {
			this.myoperatorName = myoperatorName;
		}
		public String getMystoreId() {
			return mystoreId;
		}
		public void setMystoreId(String mystoreId) {
			this.mystoreId = mystoreId;
		}
		public BigDecimal getMyjiusuanMoney() {
			return myjiusuanMoney;
		}
		public void setMyjiusuanMoney(BigDecimal myjiusuanMoney) {
			this.myjiusuanMoney = myjiusuanMoney;
		}
		public String getMypurchaseMethod() {
			return mypurchaseMethod;
		}
		public void setMypurchaseMethod(String mypurchaseMethod) {
			this.mypurchaseMethod = mypurchaseMethod;
		}
		public Date getMyperSaveTime() {
			return myperSaveTime;
		}
		public void setMyperSaveTime(Date myperSaveTime) {
			this.myperSaveTime = myperSaveTime;
		}
		public Long getMyperSaveID() {
			return myperSaveID;
		}
		public void setMyperSaveID(Long myperSaveID) {
			this.myperSaveID = myperSaveID;
		}
		public String getMycontractName() {
			return mycontractName;
		}
		public void setMycontractName(String mycontractName) {
			this.mycontractName = mycontractName;
		}
		public BigDecimal getMyjiesuanMoney1() {
			return myjiesuanMoney1;
		}
		public void setMyjiesuanMoney1(BigDecimal myjiesuanMoney1) {
			this.myjiesuanMoney1 = myjiesuanMoney1;
		}
		
	}

}
