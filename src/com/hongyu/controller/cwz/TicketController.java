package com.hongyu.controller.cwz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.gsbing.TicketMemberModelExcel;
import com.hongyu.controller.gsbing.TicketMemberModelExcel.TicketMember;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.Constants.DeductPiaowu;

//HyTicketScene  景区信息
//HyTicketSceneTicketManagement  门票信息
//HyTicketPriceInbound  价格库存

//这一系列接口是第一套流程 就是门票订购付款什么的


//从FileRecv里面的连锁发展原型图，之后门店管理-产品订购来找
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/ticket/test/")
public class TicketController {

	@Resource(name = "hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name = "hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name = "commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="supplierDismissOrderApplyServiceImpl")
	private SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name="hyReceiptRefundServiceImpl")
	private HyReceiptRefundService hyReceiptRefundService;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource (name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	private HyPromotionActivityService hyPromotionActivityService;
	
//	@Resource(name = "hyOrderCustomerServiceImpl")
//	private HyOrderCustomerService hyOrderCustomerService;
//	
//	@Resource(name = "hyOrderItemServiceImpl")
//	private HyOrderItemService hyOrderItemService;
	
	//列表页 根据区域/景点星级/景区名称来筛选
	@RequestMapping(value = "list/first")
	@ResponseBody
	public Json firstPage(Long areaId, Integer star, String sceneName, Pageable pageable) {//三个筛选条件
		Json j = new Json();
		Page<HashMap<String, Object>> page = null;//分页
		try{
			
			List<HashMap<String, Object>> ticketSceneTable = new ArrayList<>();//存储MAP用
			//设定一个filter来做
			List<Filter> ticketSceneFilter = new ArrayList<Filter>();
			//不知道对不对，再说
			if(areaId != null){
				HyArea hyArea = hyAreaService.find(areaId);
				if(hyArea != null)
				 ticketSceneFilter.add(Filter.eq("area", hyArea));
			}
			if(star != null){
				ticketSceneFilter.add(Filter.eq("star", star));
			}
			if(sceneName != null){
				ticketSceneFilter.add(Filter.eq("sceneName", sceneName));
			}
			//2018-11-19
			List<Order> myOrders = new ArrayList<>();
			myOrders.add(Order.desc("createTime"));
			
			List<HyTicketScene> hyTicketSceneList = hyTicketSceneService.findList(null, ticketSceneFilter, myOrders);
			
			if(!hyTicketSceneList.isEmpty())
				for(HyTicketScene hyTicketSceneItem : hyTicketSceneList){
					BigDecimal minimumPrice = new BigDecimal(0);
					Map<String, Object> hyTicketSceneMapItem = new HashMap<String, Object>();
					hyTicketSceneMapItem.put("sceneID", hyTicketSceneItem.getId());
					hyTicketSceneMapItem.put("sceneName", hyTicketSceneItem.getSceneName());
					hyTicketSceneMapItem.put("area", hyTicketSceneItem.getArea().getName());
					hyTicketSceneMapItem.put("star", hyTicketSceneItem.getStar());
					//最低价格需要比较一下再拿出来 结算价是HyTicketPriceInbound中的settlementPrice最低的
					//这里拿到management
					Set<HyTicketSceneTicketManagement> HyTicketSceneTicketManagements = hyTicketSceneItem.getHyTicketSceneTickets();
					//遍历management，找到最低的票价
					if(!HyTicketSceneTicketManagements.isEmpty())
					for(HyTicketSceneTicketManagement HyTicketSceneTicketManagementItems : HyTicketSceneTicketManagements){
						Set<HyTicketPriceInbound> hyTicketPriceInbounds = HyTicketSceneTicketManagementItems.getHyTicketPriceInbounds();
						//容错
						if(!hyTicketPriceInbounds.isEmpty())
							for(HyTicketPriceInbound hyTicketPriceInboundItems : hyTicketPriceInbounds){
								BigDecimal thisPrice = hyTicketPriceInboundItems.getSettlementPrice();
								int number = thisPrice.compareTo(minimumPrice);//相等是0 左边大是1 右边大是-1
								
								if(number == 1){//证明左边大于右边
									//直接替换
									minimumPrice = thisPrice;
								}
							}
					}
					//把最后的结果放到Map里
					hyTicketSceneMapItem.put("theLowestPrice", minimumPrice);
					ticketSceneTable.add((HashMap<String, Object>) hyTicketSceneMapItem);
				}
			page = new Page<>(ticketSceneTable, ticketSceneTable.size(), pageable);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(page);
		j.setMsg("更新成功");
		return j;
		
	}
	
	
	//点击详情进入详情页 景区信息/供应商/门票信息
	@RequestMapping(value = "list/second")
	@ResponseBody
	public Json secondPage(Long id, String useDate, Boolean isReserve, Boolean isRealName, String productName, Pageable pageable) {//三个筛选条件
		Json j = new Json();
		List<Object> allList = new ArrayList<>();
		//Page<HashMap<String, Object>> page = null;//分页
		Map<String, Object> touristsAttractionInfoFirst = new HashMap<String, Object>();//加入景区信息
		Map<String, Object> touristsAttractionInfoSecond = new HashMap<String, Object>();//加入供应商信息
		try{
			//先返回最上方的景区信息
			//是第一张表的内容 唯一
			HyTicketScene hyTicketScene = hyTicketSceneService.find(id);
			if(hyTicketScene != null){
				touristsAttractionInfoFirst.put("sceneName",hyTicketScene.getSceneName());//景区名称
				touristsAttractionInfoFirst.put("star", hyTicketScene.getStar());//景区星级
				touristsAttractionInfoFirst.put("area", hyTicketScene.getArea());//景区区域
				touristsAttractionInfoFirst.put("sceneAddress", hyTicketScene.getSceneAddress());//景区地址
				touristsAttractionInfoFirst.put("openTime", hyTicketScene.getOpenTime());//营业时间
				touristsAttractionInfoFirst.put("closeTime", hyTicketScene.getCloseTime());//营业时间
				touristsAttractionInfoFirst.put("ticketExchangeAddress",hyTicketScene.getTicketExchangeAddress());//换票地址
			    //增加门票推广文件,富文本信息
				touristsAttractionInfoFirst.put("introduction", hyTicketScene.getIntroduction());
				//第一张表连接供应商表查询
				HySupplier myTicketSupplier = hyTicketScene.getTicketSupplier();
				if(myTicketSupplier != null){
					//供应商 创建人 联系电话
					touristsAttractionInfoSecond.put("supplierName", myTicketSupplier.getSupplierName());
					HyAdmin myHyAdmin = hyTicketScene.getCreator();
					if(myHyAdmin != null){
						String theName = myHyAdmin.getName();
						String thePhone = myHyAdmin.getMobile();
						touristsAttractionInfoSecond.put("adminName", theName);
						touristsAttractionInfoSecond.put("adminPhone", thePhone);
					}
					
					
//					touristsAttractionInfoSecond.put("adminName", myTicketSupplier.getAdminName());
//					touristsAttractionInfoSecond.put("adminPhone", myTicketSupplier.getAdminPhone());
				}
				
				//下面开始做带分页信息的
				//第一张表连到第二张 通过List
				List<HashMap<String, Object>> ticketSceneTable = new ArrayList<>();//存储MAP用
				List<Filter> ticketSceneFilter = new ArrayList<Filter>();
				
				//首先是必须得是这个景区
				ticketSceneFilter.add(Filter.eq("hyTicketScene", hyTicketScene));
				
				//其次 这三个条件是 关于第二个表的 先这么筛选出来
				if(isReserve != null){
					ticketSceneFilter.add(Filter.eq("isReserve", isReserve));
				}
				if(isRealName != null){
					ticketSceneFilter.add(Filter.eq("isRealName", isRealName));
				}
				if(productName != null){
					ticketSceneFilter.add(Filter.eq("productName", productName));
				}
				
				//查找
				List<HyTicketSceneTicketManagement> managementList = hyTicketSceneTicketManagementService.findList(null, ticketSceneFilter, null);
				
				if(!managementList.isEmpty()){
					//如果不是空 就向下筛选 选择日期合适的 遍历所有的Management
					for(HyTicketSceneTicketManagement HyTicketSceneTicketManagementItems : managementList){
						//根据自己筛选inbound
						List<Filter> ticketInboundFilter = new ArrayList<Filter>();
						ticketInboundFilter.add(Filter.eq("hyTicketSceneTicketManagement", HyTicketSceneTicketManagementItems));
						//剩余一个筛选条件
						if(useDate != null){
							//转化成年月日的
							Date theUseDate = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);
							ticketInboundFilter.add(Filter.le("startDate", theUseDate));
							ticketInboundFilter.add(Filter.ge("endDate", theUseDate));
						}
						//开始筛选
						List<HyTicketPriceInbound> boundList = hyTicketPriceInboundService.findList(null, ticketInboundFilter, null);
						
						//开始向Map里面放列表页的值,直接放即可，如果没有Date出来的就是所有Inbound
						if(!boundList.isEmpty()){
							for(HyTicketPriceInbound hyTicketPriceInboundItems : boundList){
								//每次在这里新建Map
								Map<String, Object> touristsAttractionInfoThird = new HashMap<String, Object>();//加入详情页列表
								touristsAttractionInfoThird.put("id", HyTicketSceneTicketManagementItems.getId());
								touristsAttractionInfoThird.put("productId", HyTicketSceneTicketManagementItems.getProductId());
								touristsAttractionInfoThird.put("productName", HyTicketSceneTicketManagementItems.getProductName());
								touristsAttractionInfoThird.put("isReserve", HyTicketSceneTicketManagementItems.getIsReserve());
								touristsAttractionInfoThird.put("isRealName", HyTicketSceneTicketManagementItems.getIsRealName());
								touristsAttractionInfoThird.put("boundId", hyTicketPriceInboundItems.getId());
								touristsAttractionInfoThird.put("inventory", hyTicketPriceInboundItems.getInventory());
								touristsAttractionInfoThird.put("displayPrice", hyTicketPriceInboundItems.getDisplayPrice());
								touristsAttractionInfoThird.put("sellPrice", hyTicketPriceInboundItems.getSellPrice());
								touristsAttractionInfoThird.put("settlementPrice", hyTicketPriceInboundItems.getSettlementPrice());
								ticketSceneTable.add((HashMap<String, Object>) touristsAttractionInfoThird);
							}
						}	
					}
				}
				
				//page = new Page<>(ticketSceneTable, ticketSceneTable.size(), pageable);
			}
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//由于前端说分开传 这里只传基本信息
		allList.add(touristsAttractionInfoFirst);//加入景区信息
		allList.add(touristsAttractionInfoSecond);//加入供应商信息
		//allList.add(page);//加入最下方的表格信息
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(allList);
		j.setMsg("更新成功");
		return j;
		
	}
	
	
	//点击详情进入详情页 景区信息/供应商/门票信息
	@RequestMapping(value = "list/second2")
	@ResponseBody
	public Json secondPage2(Long id, String useDate, Boolean isReserve, Boolean isRealName, String productName, Pageable pageable) {//三个筛选条件
		Json j = new Json();
		List<Object> allList = new ArrayList<>();
		Page<HashMap<String, Object>> page = null;//分页
		Map<String, Object> touristsAttractionInfoFirst = new HashMap<String, Object>();//加入景区信息
		Map<String, Object> touristsAttractionInfoSecond = new HashMap<String, Object>();//加入供应商信息
		try{
			//先返回最上方的景区信息
			//是第一张表的内容 唯一
			HyTicketScene hyTicketScene = hyTicketSceneService.find(id);
			if(hyTicketScene != null){
				touristsAttractionInfoFirst.put("sceneName",hyTicketScene.getSceneName());//景区名称
				touristsAttractionInfoFirst.put("star", hyTicketScene.getStar());//景区星级
				touristsAttractionInfoFirst.put("area", hyTicketScene.getArea());//景区区域
				touristsAttractionInfoFirst.put("sceneAddress", hyTicketScene.getSceneAddress());//景区地址
				touristsAttractionInfoFirst.put("openTime", hyTicketScene.getOpenTime());//营业时间
				touristsAttractionInfoFirst.put("ticketExchangeAddress",hyTicketScene.getTicketExchangeAddress());//换票地址
			
				//第一张表连接供应商表查询
				HySupplier myTicketSupplier = hyTicketScene.getTicketSupplier();
				if(myTicketSupplier != null){
					//供应商 创建人 联系电话
					touristsAttractionInfoSecond.put("supplierName", myTicketSupplier.getSupplierName());
					touristsAttractionInfoSecond.put("adminName", myTicketSupplier.getAdminName());
					touristsAttractionInfoSecond.put("adminPhone", myTicketSupplier.getAdminPhone());
				}
				
				//下面开始做带分页信息的
				//第一张表连到第二张 通过List
				List<HashMap<String, Object>> ticketSceneTable = new ArrayList<>();//存储MAP用
				List<Filter> ticketSceneFilter = new ArrayList<Filter>();
				
				//首先是必须得是这个景区
				ticketSceneFilter.add(Filter.eq("hyTicketScene", hyTicketScene));
				
				//其次 这三个条件是 关于第二个表的 先这么筛选出来
				if(isReserve != null){
					ticketSceneFilter.add(Filter.eq("isReserve", isReserve));
				}
				if(isRealName != null){
					ticketSceneFilter.add(Filter.eq("isRealName", isRealName));
				}
				if(productName != null){
					ticketSceneFilter.add(Filter.eq("productName", productName));
				}
				
				//查找
				List<HyTicketSceneTicketManagement> managementList = hyTicketSceneTicketManagementService.findList(null, ticketSceneFilter, null);
				
				if(!managementList.isEmpty()){
					//如果不是空 就向下筛选 选择日期合适的 遍历所有的Management
					for(HyTicketSceneTicketManagement HyTicketSceneTicketManagementItems : managementList){
						
						Integer beforeDays = HyTicketSceneTicketManagementItems.getDays();//提前几天
						Integer beforeHours = HyTicketSceneTicketManagementItems.getTimes();//那一天几点之前
						
						
						if(useDate != null && beforeDays != null && beforeHours != null){
							//2018-12-6 
							//想用这一天的票 这一天的票减去提前几天几小时和今天的日子作比较 今天的日子靠后就最了
							//之后再用 这一天和库存里面有效时间相比较
							Date today = new Date();
							
							Date theUseDate1 = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);//哪天要用
							  
							long day = (theUseDate1.getTime()-today.getTime())/(24*60*60*1000); 
							//如果实际日期间隔大于要求的间隔，合理
							if((day+1) == beforeDays){
								
								SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//只有时分秒
								String time = sdf.format(today);
								//转化为String类型 时分秒
								String hours = beforeHours + ":00:00";
								//比较两个小时的大小 要求的时分秒-今天的时分秒 > 0 就是说还没到最后期限 可以定
								Boolean gap = compTime(hours,time);
								if(gap.equals(true)){
									//已经达到要求 可以向下取数据了 把代码复制过来
									
									
									//根据自己筛选inbound
									List<Filter> ticketInboundFilter = new ArrayList<Filter>();
									ticketInboundFilter.add(Filter.eq("hyTicketSceneTicketManagement", HyTicketSceneTicketManagementItems));
									//剩余一个筛选条件
									if(useDate != null){
										//转化成年月日的
										Date theUseDate = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);
										ticketInboundFilter.add(Filter.le("startDate", theUseDate));
										ticketInboundFilter.add(Filter.ge("endDate", theUseDate));
									}
									//开始筛选
									List<HyTicketPriceInbound> boundList = hyTicketPriceInboundService.findList(null, ticketInboundFilter, null);
									
									//开始向Map里面放列表页的值,直接放即可，如果没有Date出来的就是所有Inbound
									if(!boundList.isEmpty()){
										for(HyTicketPriceInbound hyTicketPriceInboundItems : boundList){
											
											//每次在这里新建Map
											Map<String, Object> touristsAttractionInfoThird = new HashMap<String, Object>();//加入详情页列表
											touristsAttractionInfoThird.put("id", HyTicketSceneTicketManagementItems.getId());
											touristsAttractionInfoThird.put("productId", HyTicketSceneTicketManagementItems.getProductId());
											touristsAttractionInfoThird.put("productName", HyTicketSceneTicketManagementItems.getProductName());
											touristsAttractionInfoThird.put("isReserve", HyTicketSceneTicketManagementItems.getIsReserve());
											touristsAttractionInfoThird.put("isRealName", HyTicketSceneTicketManagementItems.getIsRealName());
											touristsAttractionInfoThird.put("boundId", hyTicketPriceInboundItems.getId());
											touristsAttractionInfoThird.put("inventory", hyTicketPriceInboundItems.getInventory());
											touristsAttractionInfoThird.put("displayPrice", hyTicketPriceInboundItems.getDisplayPrice());
											touristsAttractionInfoThird.put("sellPrice", hyTicketPriceInboundItems.getSellPrice());
											touristsAttractionInfoThird.put("settlementPrice", hyTicketPriceInboundItems.getSettlementPrice());
											ticketSceneTable.add((HashMap<String, Object>) touristsAttractionInfoThird);
										}
									}
									
								}
							} else if((day+1) > beforeDays){

								//已经达到要求 可以向下取数据了 把代码复制过来
								
								
								//根据自己筛选inbound
								List<Filter> ticketInboundFilter = new ArrayList<Filter>();
								ticketInboundFilter.add(Filter.eq("hyTicketSceneTicketManagement", HyTicketSceneTicketManagementItems));
								//剩余一个筛选条件
								if(useDate != null){
									//转化成年月日的
									Date theUseDate = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);
									ticketInboundFilter.add(Filter.le("startDate", theUseDate));
									ticketInboundFilter.add(Filter.ge("endDate", theUseDate));
								}
								//开始筛选
								List<HyTicketPriceInbound> boundList = hyTicketPriceInboundService.findList(null, ticketInboundFilter, null);
								
								//开始向Map里面放列表页的值,直接放即可，如果没有Date出来的就是所有Inbound
								if(!boundList.isEmpty()){
									for(HyTicketPriceInbound hyTicketPriceInboundItems : boundList){
										
										//每次在这里新建Map
										Map<String, Object> touristsAttractionInfoThird = new HashMap<String, Object>();//加入详情页列表
										touristsAttractionInfoThird.put("id", HyTicketSceneTicketManagementItems.getId());
										touristsAttractionInfoThird.put("productId", HyTicketSceneTicketManagementItems.getProductId());
										touristsAttractionInfoThird.put("productName", HyTicketSceneTicketManagementItems.getProductName());
										touristsAttractionInfoThird.put("isReserve", HyTicketSceneTicketManagementItems.getIsReserve());
										touristsAttractionInfoThird.put("isRealName", HyTicketSceneTicketManagementItems.getIsRealName());
										touristsAttractionInfoThird.put("boundId", hyTicketPriceInboundItems.getId());
										touristsAttractionInfoThird.put("inventory", hyTicketPriceInboundItems.getInventory());
										touristsAttractionInfoThird.put("displayPrice", hyTicketPriceInboundItems.getDisplayPrice());
										touristsAttractionInfoThird.put("sellPrice", hyTicketPriceInboundItems.getSellPrice());
										touristsAttractionInfoThird.put("settlementPrice", hyTicketPriceInboundItems.getSettlementPrice());
										ticketSceneTable.add((HashMap<String, Object>) touristsAttractionInfoThird);
									}
								}
								
							

							}
						} else {
							//没有传参数的时候这个是null，返回所有的
							//已经达到要求 可以向下取数据了 把代码复制过来
							//根据自己筛选inbound
							List<Filter> ticketInboundFilter = new ArrayList<Filter>();
							ticketInboundFilter.add(Filter.eq("hyTicketSceneTicketManagement", HyTicketSceneTicketManagementItems));
							//剩余一个筛选条件
							if(useDate != null){
								//转化成年月日的
								Date theUseDate = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);
								ticketInboundFilter.add(Filter.le("startDate", theUseDate));
								ticketInboundFilter.add(Filter.ge("endDate", theUseDate));
							}
							//开始筛选
							List<HyTicketPriceInbound> boundList = hyTicketPriceInboundService.findList(null, ticketInboundFilter, null);
							
							//开始向Map里面放列表页的值,直接放即可，如果没有Date出来的就是所有Inbound
							if(!boundList.isEmpty()){
								for(HyTicketPriceInbound hyTicketPriceInboundItems : boundList){
									
									//每次在这里新建Map
									Map<String, Object> touristsAttractionInfoThird = new HashMap<String, Object>();//加入详情页列表
									touristsAttractionInfoThird.put("id", HyTicketSceneTicketManagementItems.getId());
									touristsAttractionInfoThird.put("productId", HyTicketSceneTicketManagementItems.getProductId());
									touristsAttractionInfoThird.put("productName", HyTicketSceneTicketManagementItems.getProductName());
									touristsAttractionInfoThird.put("isReserve", HyTicketSceneTicketManagementItems.getIsReserve());
									touristsAttractionInfoThird.put("isRealName", HyTicketSceneTicketManagementItems.getIsRealName());
									touristsAttractionInfoThird.put("boundId", hyTicketPriceInboundItems.getId());
									touristsAttractionInfoThird.put("inventory", hyTicketPriceInboundItems.getInventory());
									touristsAttractionInfoThird.put("displayPrice", hyTicketPriceInboundItems.getDisplayPrice());
									touristsAttractionInfoThird.put("sellPrice", hyTicketPriceInboundItems.getSellPrice());
									touristsAttractionInfoThird.put("settlementPrice", hyTicketPriceInboundItems.getSettlementPrice());
									ticketSceneTable.add((HashMap<String, Object>) touristsAttractionInfoThird);
								}
							}
							
	
						}
						
					}
				}
				
				page = new Page<>(ticketSceneTable, ticketSceneTable.size(), pageable);
			}
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//由于前端说要分开传，这里只传回带分页信息的
		//allList.add(touristsAttractionInfoFirst);//加入景区信息
		//allList.add(touristsAttractionInfoSecond);//加入供应商信息
		allList.add(page);//加入最下方的表格信息
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(allList);
		j.setMsg("更新成功");
		return j;
		
	}
				
	
	//点击详情进入详情页 景区信息/供应商/门票信息
		@RequestMapping(value = "list/second3")
		@ResponseBody
		public Json secondPage3(Long id, String useDate, Boolean isReserve, Boolean isRealName, String productName, Pageable pageable) {//三个筛选条件
			Json j = new Json();
			List<Object> allList = new ArrayList<>();
			Page<HashMap<String, Object>> page = null;//分页
			Map<String, Object> touristsAttractionInfoFirst = new HashMap<String, Object>();//加入景区信息
			Map<String, Object> touristsAttractionInfoSecond = new HashMap<String, Object>();//加入供应商信息
			try{
				//先返回最上方的景区信息
				//是第一张表的内容 唯一
				HyTicketScene hyTicketScene = hyTicketSceneService.find(id);
				if(hyTicketScene != null){
					touristsAttractionInfoFirst.put("sceneName",hyTicketScene.getSceneName());//景区名称
					touristsAttractionInfoFirst.put("star", hyTicketScene.getStar());//景区星级
					touristsAttractionInfoFirst.put("area", hyTicketScene.getArea());//景区区域
					touristsAttractionInfoFirst.put("sceneAddress", hyTicketScene.getSceneAddress());//景区地址
					touristsAttractionInfoFirst.put("openTime", hyTicketScene.getOpenTime());//营业时间
					touristsAttractionInfoFirst.put("ticketExchangeAddress",hyTicketScene.getTicketExchangeAddress());//换票地址
				
					//第一张表连接供应商表查询
					HySupplier myTicketSupplier = hyTicketScene.getTicketSupplier();
					if(myTicketSupplier != null){
						//供应商 创建人 联系电话
						touristsAttractionInfoSecond.put("supplierName", myTicketSupplier.getSupplierName());
						touristsAttractionInfoSecond.put("adminName", myTicketSupplier.getAdminName());
						touristsAttractionInfoSecond.put("adminPhone", myTicketSupplier.getAdminPhone());
					}
					
					//下面开始做带分页信息的
					//第一张表连到第二张 通过List
					List<HashMap<String, Object>> ticketSceneTable = new ArrayList<>();//存储MAP用
					List<Filter> ticketSceneFilter = new ArrayList<Filter>();
					
					//首先是必须得是这个景区
					ticketSceneFilter.add(Filter.eq("hyTicketScene", hyTicketScene));
					
					//其次 这三个条件是 关于第二个表的 先这么筛选出来
					if(isReserve != null){
						ticketSceneFilter.add(Filter.eq("isReserve", isReserve));
					}
					if(isRealName != null){
						ticketSceneFilter.add(Filter.eq("isRealName", isRealName));
					}
					if(productName != null){
						ticketSceneFilter.add(Filter.eq("productName", productName));
					}
					
					//查找
					List<HyTicketSceneTicketManagement> managementList = hyTicketSceneTicketManagementService.findList(null, ticketSceneFilter, null);
					
					if(!managementList.isEmpty()){
						//如果不是空 就向下筛选 选择日期合适的 遍历所有的Management
						for(HyTicketSceneTicketManagement HyTicketSceneTicketManagementItems : managementList){
							//根据自己筛选inbound
							List<Filter> ticketInboundFilter = new ArrayList<Filter>();
							ticketInboundFilter.add(Filter.eq("hyTicketSceneTicketManagement", HyTicketSceneTicketManagementItems));
							//剩余一个筛选条件
							if(useDate != null){
								//转化成年月日的
								Date theUseDate = DateUtil.stringToDate(useDate, DateUtil.YYYY_MM_DD);
								ticketInboundFilter.add(Filter.le("startDate", theUseDate));
								ticketInboundFilter.add(Filter.ge("endDate", theUseDate));
							}
							//开始筛选
							List<HyTicketPriceInbound> boundList = hyTicketPriceInboundService.findList(null, ticketInboundFilter, null);
							
							//开始向Map里面放列表页的值,直接放即可，如果没有Date出来的就是所有Inbound
							if(!boundList.isEmpty()){
								for(HyTicketPriceInbound hyTicketPriceInboundItems : boundList){
									//每次在这里新建Map
									Map<String, Object> touristsAttractionInfoThird = new HashMap<String, Object>();//加入详情页列表
									touristsAttractionInfoThird.put("id", HyTicketSceneTicketManagementItems.getId());
									touristsAttractionInfoThird.put("productId", HyTicketSceneTicketManagementItems.getProductId());
									touristsAttractionInfoThird.put("productName", HyTicketSceneTicketManagementItems.getProductName());
									touristsAttractionInfoThird.put("isReserve", HyTicketSceneTicketManagementItems.getIsReserve());
									touristsAttractionInfoThird.put("isRealName", HyTicketSceneTicketManagementItems.getIsRealName());
									touristsAttractionInfoThird.put("boundId", hyTicketPriceInboundItems.getId());
									touristsAttractionInfoThird.put("inventory", hyTicketPriceInboundItems.getInventory());
									touristsAttractionInfoThird.put("displayPrice", hyTicketPriceInboundItems.getDisplayPrice());
									touristsAttractionInfoThird.put("sellPrice", hyTicketPriceInboundItems.getSellPrice());
									touristsAttractionInfoThird.put("settlementPrice", hyTicketPriceInboundItems.getSettlementPrice());
									ticketSceneTable.add((HashMap<String, Object>) touristsAttractionInfoThird);
								}
							}	
						}
					}
					
					page = new Page<>(ticketSceneTable, ticketSceneTable.size(), pageable);
				}
				
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//这个返回全部的值
			allList.add(touristsAttractionInfoFirst);//加入景区信息
			allList.add(touristsAttractionInfoSecond);//加入供应商信息
			allList.add(page);//加入最下方的表格信息
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(allList);
			j.setMsg("更新成功");
			return j;
			
		}
				
	
	//门票信息/预定信息/联系人
	@RequestMapping(value = "list/third")
	@ResponseBody
	public Json thirdPage(Long id, Long boundId, @DateTimeFormat(pattern="yyyy-MM-dd") Date useDate,Pageable pageable) {//三个筛选条件
		Json j = new Json();
		List<HashMap<String, Object>> informationTable = new ArrayList<>();//存储MAP用
		try{
			//提供门票信息还有三种价格
			//首先写门票信息 需要提供
			HyTicketPriceInbound hyTicketPriceInbound = hyTicketPriceInboundService.find(boundId);
			HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(id);
			
			Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入景区信息
			
			//这个id一定要找到对应的，否则无法返回
			if(hyTicketSceneTicketManagement != null){
				//2018-12-6 晚上添加的促销
				HyPromotionActivity promotion = hyTicketSceneTicketManagement.getHyPromotionActivity();
				//一会弄 TODO
				if(promotion != null){
					Long promId = promotion.getId();
					Date endDate = promotion.getEndDate();//结束日期
					Date today = new Date();//今天
					
					if(promId != null && today.before(endDate)){
						touristsAttractionInfo.put("isPromotion", 1);//0是没有 1是有
					} else {
						touristsAttractionInfo.put("isPromotion", 0);//0是没有 1是有
					}
					
					touristsAttractionInfo.put("promotion_id", promotion.getId());
					/** 计调 **/
					touristsAttractionInfo.put("promotion_jidiao", promotion.getJidiao());
					/** 促销名称 **/
					touristsAttractionInfo.put("promotion_name", promotion.getName());
					touristsAttractionInfo.put("promotion_startDate", promotion.getStartDate());
					touristsAttractionInfo.put("promotion_endDate", promotion.getEndDate());
					/** 优惠方式0每单满减，1每单打折，2每人减,3无促销**/
					touristsAttractionInfo.put("promotion_promotionType", promotion.getPromotionType());
					/** 满减促销满足的金额 **/
					touristsAttractionInfo.put("promotion_manjianPrice1", promotion.getManjianPrice1());
					/** 满减促销减免的金额 **/
					touristsAttractionInfo.put("promotion_manjianPrice2", promotion.getManjianPrice2());
					/** 每人减/按数量减金额 **/
					touristsAttractionInfo.put("promotion_meirenjian", promotion.getMeirenjian());
					/** 打折折扣 **/
					touristsAttractionInfo.put("promotion_dazhe", promotion.getDazhe());
					/** 审核状态 0:待审核 1:通过 2:驳回  3:已过期 4:已取消**/
					touristsAttractionInfo.put("promotion_state", promotion.getState());
					/** 备注 **/
					touristsAttractionInfo.put("promotion_remark", promotion.getRemark());
					/** 活动类型 0:门票,1:酒店,2:酒+景,3:认购门票,4:签证 **/
					touristsAttractionInfo.put("promotion_activityType", promotion.getActivityType());
				} else {
					touristsAttractionInfo.put("isPromotion", 0);//0是没有 1是有
				}
				
				//————————————————————————————这以下是原来的内容————————————————————————————————
				touristsAttractionInfo.put("productName", hyTicketSceneTicketManagement.getProductName());
				touristsAttractionInfo.put("ticketType", hyTicketSceneTicketManagement.getTicketType());
				touristsAttractionInfo.put("isReserve", hyTicketSceneTicketManagement.getIsReserve());
				touristsAttractionInfo.put("days", hyTicketSceneTicketManagement.getDays());// 预约时间 1
				touristsAttractionInfo.put("time", hyTicketSceneTicketManagement.getTimes());// 预约时间 2
				touristsAttractionInfo.put("isRealName", hyTicketSceneTicketManagement.getIsRealName());
				touristsAttractionInfo.put("realNameRemark", hyTicketSceneTicketManagement.getRealNameRemark());
				touristsAttractionInfo.put("refundReq", hyTicketSceneTicketManagement.getRefundReq());
				touristsAttractionInfo.put("reserveReq", hyTicketSceneTicketManagement.getReserveReq());	
				//预定是显示当天库存,added by GSbing,20190720
				List<Filter> inboundFilters=new ArrayList<>();
				inboundFilters.add(Filter.eq("type", 1)); //1-酒店,门票,酒加景
				inboundFilters.add(Filter.eq("priceInboundId", boundId));
				inboundFilters.add(Filter.eq("day", useDate));
				List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
				if(!ticketInbounds.isEmpty()) {
					HyTicketInbound ticketInbound=ticketInbounds.get(0);
					touristsAttractionInfo.put("inboundNumber", ticketInbound.getInventory());
				}
				else {
					touristsAttractionInfo.put("inboundNumber", 0);
				}		
			}
			if(hyTicketPriceInbound != null){
				touristsAttractionInfo.put("displayPrice", hyTicketPriceInbound.getDisplayPrice());
				touristsAttractionInfo.put("sellPrice", hyTicketPriceInbound.getSellPrice());
				touristsAttractionInfo.put("settlementPrice", hyTicketPriceInbound.getSettlementPrice());
			}
			informationTable.add((HashMap<String, Object>) touristsAttractionInfo);
			
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(informationTable);
		j.setMsg("更新成功");
		return j;
		
	}
	
	
	//这个是前端传来的数据存到数据库里 首先有一个库存表叫做hy_ticket_inbound 我的type永远是1 要在里面减少库存
	//上面的预约日期什么的应该是存到order_customer里面
	//下面的联系人说是存到hy_order里面
	//参照建勇写的类HyTicketHotelReserveController里面的最后一个接口 存HyOrder
	@RequestMapping(value = "list/forth")
	@ResponseBody
	public Json forthPage(@RequestBody Wrap info, HttpSession session,HttpServletRequest request) {//三个筛选条件
		//如果后台复杂 前端需要传json，这时候必须包装成一个大类 但是简单的时候，前端传formdata，这样就可以随便写
		//String appointDate, Integer number, String Contector, String ContectNumber, 
		//String information, Integer turnover, Long id, Long boundId, Boolean isRealName,
		Json j = new Json();
		Long myId = null;
		try{
			String appointDate = info.getAppointDate();
			Integer number = info.getNumber();
			String Contector = info.getContector();
			String ContectNumber = info.getContectNumber();
			String information = info.getInformation();
			Double turnover = info.getTurnover();
			Long id = info.getId();
			Long boundId = info.getBoundId();
			Boolean isRealName = info.getIsRealName();
			
			//这个是多个顾客的信息
			List<HashMap<String, Object>> informationTable = new ArrayList<>();
			if(info != null){
				//接收
				List<InformationTable> myInformationTable =  info.getInformationTable();
				if(myInformationTable != null)
				for(InformationTable informationTableItem : myInformationTable){
					Map<String, Object> Infos = new HashMap<String, Object>();//加入信息
					Infos.put("name", informationTableItem.getName());
					Infos.put("certificate", informationTableItem.getCertificate());
					Infos.put("phone", informationTableItem.getPhone());
					informationTable.add((HashMap<String, Object>) Infos);
				}
				
			}
			
			//对传过来的数据进行处理
			Date theAppointDate = DateUtil.stringToDate(appointDate, DateUtil.YYYY_MM_DD);
			
			//找到对应的Management表条目
			HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(id);
			//找到对应的scene表（根据Management的ID）
			//！！！！
			HyTicketScene hyTicketScene = hyTicketSceneTicketManagement.getHyTicketScene();
			//找到对应的Inbound表条目
			HyTicketPriceInbound hyTicketPriceInbound = hyTicketPriceInboundService.find(boundId);
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			
			//首先是在订单数据库里面减少库存，如果库存不够 就提示错误
			//2018-11-7添加
			List<Filter> ticketInboundFilter = new ArrayList<Filter>();
			ticketInboundFilter.add(Filter.in("priceInboundId", boundId));
			ticketInboundFilter.add(Filter.in("day", theAppointDate));
			ticketInboundFilter.add(Filter.in("type", 1));//酒店这里是1
			List<HyTicketInbound> hyTicketInbounds = hyTicketInboundService.findList(null, ticketInboundFilter, null);
			HyTicketInbound myHyTicketInbound = null;
			if(!hyTicketInbounds.isEmpty()){
				myHyTicketInbound = hyTicketInbounds.get(0);
				//减去库存
				if(myHyTicketInbound.getInventory() - number >= 0)
					myHyTicketInbound.setInventory(myHyTicketInbound.getInventory() - number);
				else
					throw new Exception("数据库表中库存不足");
				//更新回去
				hyTicketInboundService.update(myHyTicketInbound);
			}
			
			//其次是设置hy_order一系列
			//首先是hy_order
			//新建订单
			HyOrder hyOrder = new HyOrder();
			String code = null;
			
			
			//在这里判断是否是实名制来决定是否有表格，进而是否有Customer
			if(isRealName.equals(true)){
				//上一级的list里面加入下一级
				List<HyOrderCustomer> orderCustomers = new ArrayList<>();
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0l);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				code = nowaday + String.format("%05d", value);
				hyOrder.setOrderNumber(code);//准
				
				if(hyTicketScene != null)
				hyOrder.setName(hyTicketScene.getSceneName());	//订单名称 ticketScene表里面的名称 准
				hyOrder.setStatus(0);// 0门店待支付  准
				hyOrder.setPaystatus(0);// 0门店待支付 准
				hyOrder.setCheckstatus(0);// 门店待确认 准
				hyOrder.setGuideCheckStatus(0);	//供应商待确认状态 准
				hyOrder.setRefundstatus(0);// 门店未退款 准
				hyOrder.setType(4);	//酒店订单 准
				hyOrder.setSource(0);	//门店来源 准
				hyOrder.setPeople(number);	//不确定 应该没用 不管了
				hyOrder.setStoreType(0);	//虹宇门店 准
				
				Store myStore = storeService.findStore(admin);
				
				if(myStore != null)
				hyOrder.setStoreId(myStore.getId());	//门店id 通过登录找 准
				hyOrder.setOperator(admin);	//门店处理人id 准
				hyOrder.setCreatorId(null);	//创建者id 不确定
				
				//调整金额待处理
				hyOrder.setAdjustMoney(BigDecimal.ZERO);	//调整金额为0

				//优惠待处理
//				hyOrder.setDiscountedType(3);	//无优惠 
//				hyOrder.setDiscountedId(null);	//无优惠
//				hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
				
				//除保险之外的订单条目的总结算价,没有保险
				BigDecimal totalNumber = new BigDecimal(number);
				hyOrder.setJiesuanMoney1(hyTicketPriceInbound.getSettlementPrice().multiply(totalNumber));//准
				//订单结算价,没有保险，
				hyOrder.setJiusuanMoney(new BigDecimal(turnover));//准 前端传给我的
				//外卖价 用单的乘以票数
				hyOrder.setWaimaiMoney(hyTicketPriceInbound.getSellPrice().multiply(totalNumber));//准
				hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价 准
				hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价 准
				hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价 准
				hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价 准
				hyOrder.setIfjiesuan(false);// 未结算 准
				hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险 准
				hyOrder.setJiesuantime(new Date());	//
				
				//获取优惠活动
				HyPromotionActivity promotionActivity = hyTicketSceneTicketManagement.getHyPromotionActivity();
				if(promotionActivity==null) {
					hyOrder.setDiscountedType(3);
				}else {
					hyOrder.setDiscountedType(promotionActivity.getPromotionType());
					hyOrder.setDiscountedId(promotionActivity.getId());
				}
				//获取优惠金额
				hyOrder.setDiscountedPrice(hyPromotionActivityService.getDiscountedPriceByHyOrder(hyOrder, promotionActivity));
				
				
				//计算扣点
				
				//2019-5-8 扣点逻辑错误了 修正
				List<Filter> koudianfilters2=new ArrayList<>();
				koudianfilters2.add(Filter.eq("liable", hyTicketScene.getCreator()));
				koudianfilters2.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
				List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,koudianfilters2,null);
				if(supplierContracts.isEmpty()) {
					j.setSuccess(false);
					j.setMsg("该供应商没有正常状态的合同");
					return j;
				}
				filters.clear();
				//2019-5-8
				
				//找供应商合同
//				HySupplier myHySupplier = hyTicketScene.getTicketSupplier();
				HySupplierContract hySupplierContract = null;
//				if(myHySupplier != null)
//				hySupplierContract = hySupplierContractService.getByHySupplier(myHySupplier);
				hySupplierContract = supplierContracts.get(0);
				//有错
				HySupplierDeductPiaowu hySupplierDeductPiaowu = null;
				if(hySupplierContract != null)
				hySupplierDeductPiaowu = hySupplierContract.getHySupplierDeductPiaowu();
				if(hySupplierDeductPiaowu != null){
					hyOrder.setKoudianMethod(hySupplierDeductPiaowu.getDeductPiaowu().ordinal());	//扣点方式
				} else {
					hyOrder.setKoudianMethod(0);
				}
				if(hyOrder.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
					//流水扣点
					hyOrder.setProportion(hySupplierDeductPiaowu.getLiushuiPiaowu());
					hyOrder.setKoudianMoney(hyOrder.getJiusuanMoney().multiply(
							hyOrder.getProportion().multiply(BigDecimal.valueOf(0.01))));
				}else{
					//人头扣点
					hyOrder.setHeadProportion(hySupplierDeductPiaowu.getRentouPiaowu());
					BigDecimal a = hyOrder.getHeadProportion();
					Integer b = hyOrder.getPeople();
					BigDecimal c = new BigDecimal(b);
					hyOrder.setKoudianMoney(a.multiply(c));
				}
				
				hyOrder.setContact(Contector);	//联系人姓名
				hyOrder.setContactIdNumber(ContectNumber);	//联系人身份证为null
				hyOrder.setPhone(ContectNumber);	//联系人电话
				hyOrder.setRemark(information);	//备注
				
				//合同相关设为null 这个就是 			//在第二套流程的列表页里面拿出来筛选
				hyOrder.setCreatetime(new Date());	//创建时间 ！！！！！！！！这个就是下单日期！！！！！！！！
				hyOrder.setModifytime(null);	//修改时间设为null
				
				//-------------------------到这里hy_order添加完毕-----------------------------
				//上一级的list里面加入下一级
				List<HyOrderItem> orderItems = new ArrayList<>();
				
				if(informationTable != null)
					//有几条存几条
					for(HashMap<String, Object> myHashMap : informationTable){
					
						//订单条目
						HyOrderItem hyOrderItem = new HyOrderItem();
						//这个还需要存储一下数据
						HyOrderCustomer hyOrderCustomer = new HyOrderCustomer();
						
						//下面开始设置Item表以及剩下的order表				
						hyOrderItem.setStatus(0);	//0为有效
						hyOrderItem.setStartDate(theAppointDate);	//开始时间 应该是Inbound的 不确定
						hyOrderItem.setEndDate(theAppointDate);	//结束时间 不确定
						hyOrderItem.setName(hyTicketSceneTicketManagement.getProductName());	//订单条目名称 不确定
						hyOrderItem.setType(4);	//酒店
						hyOrderItem.setPriceType(null);	//价格类型为null
						hyOrderItem.setJiesuanPrice(hyTicketPriceInbound.getSettlementPrice());	//结算价
						hyOrderItem.setWaimaiPrice(hyTicketPriceInbound.getSellPrice());	//外卖价
						hyOrderItem.setNumber(1);	//购买数量
						hyOrderItem.setNumberOfReturn(0);	//退货数量
						hyOrderItem.setOrder(hyOrder);	//所属订单
						hyOrderItem.setProductId(hyTicketScene.getId());	//酒店id
						hyOrderItem.setSpecificationId(hyTicketSceneTicketManagement.getId());	//房型id
						hyOrderItem.setPriceId(hyTicketPriceInbound.getId());	//价格id
						
						
						orderItems.add(hyOrderItem);
						
						hyOrderCustomer.setOrderItem(hyOrderItem);
						hyOrderCustomer.setName((String) myHashMap.get("name"));
						hyOrderCustomer.setType(null);//不区分
						hyOrderCustomer.setGender(getGenderByIdCard((String) myHashMap.get("certificate")));//不分男女
						hyOrderCustomer.setAge(getAgeByIdCard((String) myHashMap.get("certificate")));
						hyOrderCustomer.setPhone((String) myHashMap.get("phone"));
						hyOrderCustomer.setIsCoupon(null);
						hyOrderCustomer.setIsInsurance(false);
						hyOrderCustomer.setInsuranceId(null);
						hyOrderCustomer.setSalePrice(hyTicketPriceInbound.getSellPrice());
						hyOrderCustomer.setSettlementPrice(hyTicketPriceInbound.getSettlementPrice());
						hyOrderCustomer.setCertificateType(0);
						hyOrderCustomer.setCertificate((String) myHashMap.get("certificate"));
						//身份证有效期不知道怎么做
						hyOrderCustomer.setCertificateTime(null);
						hyOrderCustomer.setPassportTime(null);
						hyOrderCustomer.setCountry(null);//如果想的话 改成中国
						hyOrderCustomer.setFirstname(null);//没有输入拼音
						hyOrderCustomer.setSurname(null);//没有输入拼音
						//上一级的list里面加入下一级
						orderCustomers.add(hyOrderCustomer);
						//上一级的list里面加入下一级
						hyOrderItem.setHyOrderCustomers(orderCustomers);	//顾客为null
						
					}
				hyOrder.setOrderItems(orderItems);	//订单条目
				
				hyOrder.setGroupId(null);	//没有团
				hyOrder.setSupplier(hyTicketScene.getCreator());	//没有团所属计调
				hyOrder.setIsDivideStatistic(false);	//没有分成统计
				
				hyOrder.setTip(new BigDecimal(0));
				

				hyOrderService.save(hyOrder);
				
			} else {
				//这里就不是实名制 只有order以及Item

				//上一级的list里面加入下一级
				List<HyOrderCustomer> orderCustomers = new ArrayList<>();
				
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 99999) {
						c.setValue(0l);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				code = nowaday + String.format("%05d", value);
				hyOrder.setOrderNumber(code);//准
				
				if(hyTicketScene != null)
				hyOrder.setName(hyTicketScene.getSceneName());	//订单名称 ticketScene表里面的名称 准
				hyOrder.setStatus(0);// 0门店待支付  准
				hyOrder.setPaystatus(0);// 0门店待支付 准
				hyOrder.setCheckstatus(0);// 门店待确认 准
				hyOrder.setGuideCheckStatus(0);	//供应商待确认状态 准
				hyOrder.setRefundstatus(0);// 门店未退款 准
				hyOrder.setType(4);	//酒店订单 准
				hyOrder.setSource(0);	//门店来源 准
				hyOrder.setPeople(number);	//不确定 应该没用 不管了
				hyOrder.setStoreType(0);	//虹宇门店 准
				
				Store myStore = storeService.findStore(admin);
				
				if(myStore != null)
				hyOrder.setStoreId(myStore.getId());	//门店id 通过登录找 准
				hyOrder.setOperator(admin);	//门店处理人id 准
				hyOrder.setCreatorId(null);	//创建者id 不确定
				
				//调整金额待处理
				hyOrder.setAdjustMoney(BigDecimal.ZERO);	//调整金额为0
				
				//获取优惠活动
				HyPromotionActivity promotionActivity = hyTicketSceneTicketManagement.getHyPromotionActivity();
				if(promotionActivity==null) {
					hyOrder.setDiscountedType(3);
				}else {
					hyOrder.setDiscountedType(promotionActivity.getPromotionType());
					hyOrder.setDiscountedId(promotionActivity.getId());
				}
				//获取优惠金额
				hyOrder.setDiscountedPrice(hyPromotionActivityService.getDiscountedPriceByHyOrder(hyOrder, promotionActivity));
				
				
//				//优惠待处理
//				hyOrder.setDiscountedType(3);	//无优惠 
//				hyOrder.setDiscountedId(null);	//无优惠
//				hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
				
				//除保险之外的订单条目的总结算价,没有保险
				BigDecimal totalNumber = new BigDecimal(number);
				//2018-12-5
				hyOrder.setJiesuanMoney1(hyTicketPriceInbound.getSettlementPrice().multiply(totalNumber).subtract(hyOrder.getDiscountedPrice()));//准
				//订单结算价,没有保险
				hyOrder.setJiusuanMoney(new BigDecimal(turnover));//准 前端传给我的
				//外卖价 用单的乘以票数
				hyOrder.setWaimaiMoney(hyTicketPriceInbound.getSellPrice().multiply(totalNumber));//准
				hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价 准
				hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价 准
				hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价 准
				hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价 准
				hyOrder.setIfjiesuan(false);// 未结算 准
				hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险 准
				hyOrder.setJiesuantime(new Date());	//
				
				//2019-5-8 扣点逻辑错误了
				List<Filter> koudianfilters=new ArrayList<>();
				koudianfilters.add(Filter.eq("liable", hyTicketScene.getCreator()));
				koudianfilters.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
				List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,koudianfilters,null);
				if(supplierContracts.isEmpty()) {
					j.setSuccess(false);
					j.setMsg("该供应商没有正常状态的合同");
					return j;
				}
				filters.clear();
				//2019-5-8
				
				
				//计算扣点
				//找供应商合同
				//封闭 2019-5-8
//				HySupplier myHySupplier = hyTicketScene.getTicketSupplier();
				HySupplierContract hySupplierContract = null;
//				if(myHySupplier != null)
//				hySupplierContract = hySupplierContractService.getByHySupplier(myHySupplier);
				hySupplierContract = supplierContracts.get(0);
				//有错
				HySupplierDeductPiaowu hySupplierDeductPiaowu = null;
				if(hySupplierContract != null)
				hySupplierDeductPiaowu = hySupplierContract.getHySupplierDeductPiaowu();
				hyOrder.setKoudianMethod(hySupplierDeductPiaowu.getDeductPiaowu().ordinal());	//扣点方式
				if(hyOrder.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
					//流水扣点
					hyOrder.setProportion(hySupplierDeductPiaowu.getLiushuiPiaowu());
					hyOrder.setKoudianMoney(hyOrder.getJiusuanMoney().multiply(
							hyOrder.getProportion().multiply(BigDecimal.valueOf(0.01))));
				}else{
					//人头扣点
					hyOrder.setHeadProportion(hySupplierDeductPiaowu.getRentouPiaowu());
					BigDecimal a = hyOrder.getHeadProportion();
					Integer b = hyOrder.getPeople();
					BigDecimal c = new BigDecimal(b);
					hyOrder.setKoudianMoney(a.multiply(c));
				}
				
				hyOrder.setContact(Contector);	//联系人姓名
				hyOrder.setContactIdNumber(ContectNumber);	//联系人身份证为null
				hyOrder.setPhone(ContectNumber);	//联系人电话
				hyOrder.setRemark(information);	//备注
				
				//合同相关设为null 这个就是 			//在第二套流程的列表页里面拿出来筛选
				hyOrder.setCreatetime(new Date());	//创建时间 ！！！！！！！！这个就是下单日期！！！！！！！！
				hyOrder.setModifytime(null);	//修改时间设为null
				
				//-------------------------到这里hy_order添加完毕-----------------------------
				//上一级的list里面加入下一级
				List<HyOrderItem> orderItems = new ArrayList<>();
				
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				//这个还需要存储一下数据
				HyOrderCustomer hyOrderCustomer = new HyOrderCustomer();
				
				//下面开始设置Item表以及剩下的order表				
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(theAppointDate);	//开始时间 应该是Inbound的 不确定
				hyOrderItem.setEndDate(theAppointDate);	//结束时间 不确定
				hyOrderItem.setName(hyTicketSceneTicketManagement.getProductName());	//订单条目名称 不确定
				hyOrderItem.setType(4);	//酒店
				hyOrderItem.setPriceType(null);	//价格类型为null
				hyOrderItem.setJiesuanPrice(hyTicketPriceInbound.getSettlementPrice());	//结算价
				hyOrderItem.setWaimaiPrice(hyTicketPriceInbound.getSellPrice());	//外卖价
				hyOrderItem.setNumber(number);	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketScene.getId());	//酒店id
				hyOrderItem.setSpecificationId(hyTicketSceneTicketManagement.getId());	//房型id
				hyOrderItem.setPriceId(hyTicketPriceInbound.getId());	//价格id
				
				
				orderItems.add(hyOrderItem);
				orderCustomers.add(hyOrderCustomer);
				//上一级的list里面加入下一级
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null
						
					
				hyOrder.setOrderItems(orderItems);	//订单条目
				
				hyOrder.setGroupId(null);	//没有团
				hyOrder.setSupplier(hyTicketScene.getCreator());	//没有团所属计调
				hyOrder.setIsDivideStatistic(false);	//没有分成统计
				
				hyOrder.setTip(new BigDecimal(0));
				

				hyOrderService.save(hyOrder);
			}
			

			
			//订单日志,插一条记录到hy_order_application
			HyOrderApplication hyOrderApplication=new HyOrderApplication();
			hyOrderApplication.setOperator(admin);
			hyOrderApplication.setCreatetime(new Date());
			hyOrderApplication.setStatus(1); //通过
			hyOrderApplication.setContent("门店下订单");
			hyOrderApplication.setOrderId(hyOrder.getId());
			hyOrderApplication.setOrderNumber(code);
			//订单申请类型。 0：门店退团，1：售后退款，2：供应商消团,3:门店确认订单，
			//4：门店取消订单,5:门店订单支付,6供应商确认,7供应商驳回,8门店下订单
			hyOrderApplication.setType(8); //8-门店下订单
			hyOrderApplicationService.save(hyOrderApplication);
			
			//不知道行不行
			myId = hyOrder.getId();
			//返个字段是不是实名制
			//515 1026 703
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
			return j;
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(myId);
		j.setMsg("更新成功");
		return j;
		
	}
	
		/**
	     * 根据身份编号获取性别
	     *
	     * @param idCard 身份编号
	     * @return 性别(M-男，F-女，N-未知)
	     */
	 public static Integer getGenderByIdCard(String idCard) {
        Integer sGender = null;

        String sCardNum = idCard.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = 1;//男
        } else {
            sGender = 0;//女
        }
        return sGender;
	 }

	 
	 /**
	     * 根据身份编号获取年龄
	     *
	     * @param idCard
	     *            身份编号
	     * @return 年龄
	     */
	  public static int getAgeByIdCard(String idCard) {
	        int iAge = 0;
	        Calendar cal = Calendar.getInstance();
	        String year = idCard.substring(6, 10);
	        int iCurrYear = cal.get(Calendar.YEAR);
	        iAge = iCurrYear - Integer.valueOf(year);
	        return iAge;
	  }

	  
	  
	   /**
	     * 根据身份编号获取生日
	     *
	     * @param idCard 身份编号
	     * @return 生日(yyyyMMdd)
	     */
	 public static String getBirthByIdCard(String idCard) {
	        return idCard.substring(6, 14);
	 }

	 public static class InformationTable implements Serializable{
		 /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String name;
		 private String certificate;
		 private String phone;
		 
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getCertificate() {
			return certificate;
		}
		public void setCertificate(String certificate) {
			this.certificate = certificate;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		 
	 }
	    
	 public static class Wrap implements Serializable{
		//后台接受一个表格怎么接受的？是一个List里面包着Map，根据Key把值取出来
		 //问了建勇：
			//如果后台复杂 前端需要传json，这时候必须包装成一个大类 但是简单的时候，前端传formdata，这样就可以随便写
			//String appointDate, Integer number, String Contector, String ContectNumber, 
			//String information, Integer turnover, Long id, Long boundId, Boolean isRealName,
		 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		//注意这个是小写
		private List<InformationTable> informationTable;//存储MAP用
		private String appointDate;
		private Integer number;
		private String contector;
		private String contectNumber;
		private String information;
		//private Integer turnover;
		private Double turnover;
		private Long id;
		private Long boundId;
		private Boolean isRealName;
		public List<InformationTable> getInformationTable() {
			return informationTable;
		}
		public void setInformationTable(List<InformationTable> informationTable) {
			this.informationTable = informationTable;
		}
		public String getAppointDate() {
			return appointDate;
		}
		public void setAppointDate(String appointDate) {
			this.appointDate = appointDate;
		}
		public Integer getNumber() {
			return number;
		}
		public void setNumber(Integer number) {
			this.number = number;
		}

		public String getContector() {
			return contector;
		}
		public void setContector(String contector) {
			this.contector = contector;
		}
		public String getContectNumber() {
			return contectNumber;
		}
		public void setContectNumber(String contectNumber) {
			this.contectNumber = contectNumber;
		}
		public String getInformation() {
			return information;
		}
		public void setInformation(String information) {
			this.information = information;
		}
		public Double getTurnover() {
			return turnover;
		}
		public void setTurnover(Double turnover) {
			this.turnover = turnover;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getBoundId() {
			return boundId;
		}
		public void setBoundId(Long boundId) {
			this.boundId = boundId;
		}
		public Boolean getIsRealName() {
			return isRealName;
		}
		public void setIsRealName(Boolean isRealName) {
			this.isRealName = isRealName;
		}
		

	}
	 
		
		//TODO 修改接口名称：三个流程各自有各自的前缀名，如果是列表页以list结尾 这个明天再改
		//增加一个接口列出各个的详细信息，洪神给过我接口位置 这个接口跟第一个流程 就在这个文件不需要动
		/**
		 * 由父区域的ID得到全部的子区域
		 * @param id
		 * @return
		 */
		@RequestMapping(value="areacomboxlist/view", method = RequestMethod.GET)
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
				j.setSuccess(false);
				j.setMsg(e.getMessage());
			}
			return j;
		}
		//不知道前面第一个工作流要不要复制过来 不需要
		
		//shouhouPiaowuRefund 这个接口不需要一点修改
		//另外HyOrderApplicationServiceImpl中增加了两个接口 搜索cwz即可 在Service接口进行注册
		//建勇说要更改扣点方式 这个拉下来代码再看  这个感觉不需要修改 我的是流水 
		
		public static boolean compTime(String s1,String s2){
			try {
				if (s1.indexOf(":")<0||s1.indexOf(":")<0) {
					System.out.println("格式不正确");
				}else{
					String[]array1 = s1.split(":");
					int total1 = Integer.valueOf(array1[0])*3600+Integer.valueOf(array1[1])*60+Integer.valueOf(array1[2]);
					String[]array2 = s2.split(":");
					int total2 = Integer.valueOf(array2[0])*3600+Integer.valueOf(array2[1])*60+Integer.valueOf(array2[2]);
					return total1-total2>0?true:false;
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return true;
		}

		/**
		 * 购买门票批量导入顾客信息模板
		 * @author GSbing
		 * @date 20190716
		 */
		@RequestMapping(value = "ticket_order/get_excel")
		public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
	        try {
	        	String filefullname =System.getProperty("hongyu.webapp") + "download/票务产品游客信息表.xls";
	            String fileName = "购买门票游客信息表.xls";
				File file = new File(filefullname);
				System.out.println(filefullname);
				System.out.println(file.getAbsolutePath());
				if (!file.exists()) {
				    request.setAttribute("message", "下载失败");
				    return;
	                
	            } else {

	                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
	                response.setHeader("content-disposition",
	                        "attachment;filename=" + URLEncoder.encode("门票产品游客信息表.xls", "utf-8"));         
	        		
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
		 * 上传订购门票游客信息表模板
		 * @author GSbing
		 * @date 20190716
		 */
		@RequestMapping(value = "ticket_order/upload_excel")
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
				
	        	List<TicketMember> members = TicketMemberModelExcel.readMemberExcel(file.getInputStream());
	        	List<Map<String,Object> > list = new ArrayList<>();
	            for(TicketMember member : members) {
	            	Map<String, Object> map = new HashMap<>();
	            	map.put("name", member.getName());
	            	map.put("certificateNumber", member.getCertificateNumber());
	            	map.put("telephone", member.getTelephone());
	            	list.add(map);
	            }
	        	
				json.setObj(list);
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
