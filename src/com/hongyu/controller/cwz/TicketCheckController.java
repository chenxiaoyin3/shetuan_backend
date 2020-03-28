package com.hongyu.controller.cwz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
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
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.DeductPiaowu;

@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/ticket/check/")
public class TicketCheckController {

	//这里不用了 做备份
	
	//供应商订单管理 门店管理 

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
			List<HyTicketScene> hyTicketSceneList = hyTicketSceneService.findList(null, ticketSceneFilter, null);
			
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
	public Json thirdPage(Long id, Long boundId, Pageable pageable) {//三个筛选条件
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
				touristsAttractionInfo.put("productName", hyTicketSceneTicketManagement.getProductName());
				touristsAttractionInfo.put("ticketType", hyTicketSceneTicketManagement.getTicketType());
				touristsAttractionInfo.put("isReserve", hyTicketSceneTicketManagement.getIsReserve());
				touristsAttractionInfo.put("days", hyTicketSceneTicketManagement.getDays());// 预约时间 1
				touristsAttractionInfo.put("time", hyTicketSceneTicketManagement.getTimes());// 预约时间 2
				touristsAttractionInfo.put("isRealName", hyTicketSceneTicketManagement.getIsRealName());
				touristsAttractionInfo.put("realNameRemark", hyTicketSceneTicketManagement.getRealNameRemark());
				touristsAttractionInfo.put("refundReq", hyTicketSceneTicketManagement.getRefundReq());
				touristsAttractionInfo.put("reserveReq", hyTicketSceneTicketManagement.getReserveReq());	
			}
			if(hyTicketPriceInbound != null){
				touristsAttractionInfo.put("displayPrice", hyTicketPriceInbound.getDisplayPrice());
				touristsAttractionInfo.put("sellPrice", hyTicketPriceInbound.getSellPrice());
				touristsAttractionInfo.put("settlementPrice", hyTicketPriceInbound.getSettlementPrice());
			}
			
			//疑似需要修改
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
			Integer turnover = info.getTurnover();
			Long id = info.getId();
			Long boundId = info.getBoundId();
			Boolean isRealName = info.getIsRealName();
			
			//对传过来的数据进行处理
			Date theAppointDate = DateUtil.stringToDate(appointDate, DateUtil.YYYY_MM_DD);
			
			//找到对应的Management表条目
			HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(id);
			//找到对应的scene表（根据Management的ID）
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
			String code = nowaday + String.format("%05d", value);
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
			hyOrder.setDiscountedType(3);	//无优惠 
			hyOrder.setDiscountedId(null);	//无优惠
			hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
			
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
			
			//计算扣点
			//找供应商合同
			HySupplier myHySupplier = hyTicketScene.getTicketSupplier();
			HySupplierContract hySupplierContract = null;
			if(myHySupplier != null)
			hySupplierContract = hySupplierContractService.getByHySupplier(myHySupplier);
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
			
			//下面开始设置Item表以及剩下的order表
			//订单条目
			HyOrderItem hyOrderItem = new HyOrderItem();
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
			
			//上一级的list里面加入下一级
			List<HyOrderItem> orderItems = new ArrayList<>();
			orderItems.add(hyOrderItem);
			hyOrder.setOrderItems(orderItems);	//订单条目
			
			hyOrder.setGroupId(null);	//没有团
			hyOrder.setSupplier(null);	//没有团所属计调
			hyOrder.setIsDivideStatistic(false);	//没有分成统计
			
			hyOrder.setTip(new BigDecimal(0));
			hyOrder.setDiscountedPrice(new BigDecimal(0));
			
			//这个是多个顾客的信息
			List<HashMap<String, Object>> informationTable = new ArrayList<>();
			if(info != null){
				//接收
				List<InformationTable> myInformationTable =  info.getInformationTable();
				for(InformationTable informationTableItem : myInformationTable){
					Map<String, Object> Infos = new HashMap<String, Object>();//加入信息
					Infos.put("name", informationTableItem.getName());
					Infos.put("certificate", informationTableItem.getCertificate());
					Infos.put("phone", informationTableItem.getPhone());
					informationTable.add((HashMap<String, Object>) Infos);
				}
				
			}
			
			//在这里判断是否是实名制来决定是否有表格，进而是否有Customer
			if(isRealName.equals(true)){
				//上一级的list里面加入下一级
				List<HyOrderCustomer> orderCustomers = new ArrayList<>();
				if(informationTable != null)
					//有几条存几条
					for(HashMap<String, Object> myHashMap : informationTable){
						//这个还需要存储一下数据
						HyOrderCustomer hyOrderCustomer = new HyOrderCustomer();
						hyOrderCustomer.setOrderItem(hyOrderItem);
						hyOrderCustomer.setName((String) myHashMap.get("name"));
						hyOrderCustomer.setType(null);//不区分
						hyOrderCustomer.setGender(getGenderByIdCard((String) myHashMap.get("certificate")));//不分男女
						hyOrderCustomer.setAge(getAgeByIdCard((String) myHashMap.get("certificate")));
						hyOrderCustomer.setPhone((String) myHashMap.get("phone"));
						hyOrderCustomer.setIsCoupon(null);
						hyOrderCustomer.setIsInsurance(null);
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
					}
				
				//上一级的list里面加入下一级
				hyOrderItem.setHyOrderCustomers(orderCustomers);	//顾客为null
			}
			
			hyOrderService.save(hyOrder);
			
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
			//515 1026 703
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
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
		private Integer turnover;
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
		public Integer getTurnover() {
			return turnover;
		}
		public void setTurnover(Integer turnover) {
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
	 
	 
	 //接下来就是跳到网络销售--订单中心--门票订单中心--门票订单详情  有必要限制权限 需要参照线路的（这个已经问了郭哥）
	 //先写个列表页 就是最开始的
	 //筛选条件：支付状态，确认状态，退款状态，星级(通过Item表来找)，来源，订单号 其他的都在order表
	 
	 //供应商用的是：     admin.swczyc.com/hyapi/admin/storeLineOrder/gys/list/view
	 //门店下单用的是 ：admin.swczyc.com/hyapi/admin/storeLineOrder/list/view 不一样
	 //供应商还需要重新做一个 这里的createTime是订单创建时间
	 @RequestMapping(value = "list/fifth")
		@ResponseBody
		public Json fifthPage(Integer paystatus, Integer checkstatus, Integer refundstatus,
				Integer star, Integer source, String createtime, String orderNumber, String ticketName, HttpSession session, 
				HttpServletRequest request, Pageable pageable) {//三个筛选条件
			Json j = new Json();
			Page<HashMap<String, Object>> page = null;//分页
			try{
				
				Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
				
				List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
				//设定一个filter来做
				List<Filter> orderAndSceneFilter = new ArrayList<Filter>();
				//首先这个必须要是门票
				orderAndSceneFilter.add(Filter.eq("type", 4));
				//然后必须得是合适的人看到这个
				orderAndSceneFilter.add(Filter.in("operator", hyAdmins));
				if(paystatus != null){
					orderAndSceneFilter.add(Filter.eq("paystatus", paystatus));
				}
				if(checkstatus != null){
					orderAndSceneFilter.add(Filter.eq("checkstatus", checkstatus));
				}
				if(refundstatus != null){
					orderAndSceneFilter.add(Filter.eq("refundstatus", refundstatus));
				}
				//只有星级是连表查询的
//				if(star != null){
//					orderAndSceneFilter.add(Filter.eq("star", star));
//				}
				if(source != null){
					orderAndSceneFilter.add(Filter.eq("source", source));
				}
				
				if(orderNumber != null){
					orderAndSceneFilter.add(Filter.eq("orderNumber", orderNumber));
				}
				
				//订单号 商品名称
				//这个日期需要转换一下 年月日时分秒
//				Date theCreatetime = DateUtil.stringToDate(createtime, DateUtil.YYYYMMDDHHMMSS);
//				if(createtime != null){
//					orderAndSceneFilter.add(Filter.eq("createtime", theCreatetime));
//				}
				//之后开始筛选
				List<HyOrder> myHyOrder = hyOrderService.findList(null, orderAndSceneFilter, null);
				if(!myHyOrder.isEmpty()){
					//遍历一下 准备筛选星级
					for(HyOrder HyOrderItem : myHyOrder){
						//得到所有的Items
						List<HyOrderItem> myHyOrderItem = HyOrderItem.getOrderItems();
						//遍历Items看看哪个星级可以
						if(!myHyOrderItem.isEmpty()){
							for(HyOrderItem HyOrderItems : myHyOrderItem){
								//找到scene的ID，之后查找看看星级对不对
								Long sceneId = HyOrderItems.getProductId();
								List<Filter> SceneFilter = new ArrayList<Filter>();
								if(sceneId != null){
									SceneFilter.add(Filter.eq("id", sceneId));
								}
								if(star != null){
									SceneFilter.add(Filter.eq("star", star));
								}
								if(ticketName != null){
									SceneFilter.add(Filter.eq("sceneName", ticketName));
								}
								//筛选
								List<HyTicketScene> myTicketScene = hyTicketSceneService.findList(null, SceneFilter, null);
								//遍历看star
								for(HyTicketScene myHyTicketScene : myTicketScene){
									//筛选出来所有的变量都是符合条件的
									Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
									touristsAttractionInfo.put("id", HyOrderItem.getId());
									touristsAttractionInfo.put("status", HyOrderItem.getStatus());
									touristsAttractionInfo.put("orderNumber", HyOrderItem.getOrderNumber());
									touristsAttractionInfo.put("name", HyOrderItem.getName());
									touristsAttractionInfo.put("star", myHyTicketScene.getStar());
									touristsAttractionInfo.put("source", HyOrderItem.getSource());
									touristsAttractionInfo.put("createtime", HyOrderItem.getCreatetime());
									//加入List中
									orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
								}
							}
						}
					}
				}
				page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
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
	 
	 
	 	 //供应商重新做的做在这里
		 @RequestMapping(value = "list/fifth/gys")
			@ResponseBody
			//筛选条件：三个状态，订单创建时间（createTime），订单名称（name），订单来源（source）
			public Json fifthPageGys(Integer paystatus, Integer checkstatus, Integer refundstatus,
					Integer source, String createtime, String name, HttpSession session, String orderNumber, String ticketName,
					HttpServletRequest request, Pageable pageable) {//三个筛选条件
				Json j = new Json();
				Page<HashMap<String, Object>> page = null;//分页
				try{
					

					Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
					
					List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
					//设定一个filter来做
					List<Filter> orderAndSceneFilter = new ArrayList<Filter>();
					//首先这个必须要是门票
					orderAndSceneFilter.add(Filter.eq("type", 4));
					//然后必须得是合适的人看到这个
					orderAndSceneFilter.add(Filter.in("operator", hyAdmins));
					if(name != null){
						orderAndSceneFilter.add(Filter.eq("name", name));
					}
					if(paystatus != null){
						orderAndSceneFilter.add(Filter.eq("paystatus", paystatus));
					}
					if(checkstatus != null){
						orderAndSceneFilter.add(Filter.eq("checkstatus", checkstatus));
					}
					if(refundstatus != null){
						orderAndSceneFilter.add(Filter.eq("refundstatus", refundstatus));
					}
					if(source != null){
						orderAndSceneFilter.add(Filter.eq("source", source));
					}
					if(orderNumber != null){
						orderAndSceneFilter.add(Filter.eq("orderNumber", orderNumber));
					}
					//这个日期需要转换一下 年月日时分秒
					Date theCreatetime = DateUtil.stringToDate(createtime, DateUtil.YYYYMMDDHHMMSS);
					if(createtime != null){
						orderAndSceneFilter.add(Filter.eq("createtime", theCreatetime));
					}
					//之后开始筛选
					List<HyOrder> myHyOrder = hyOrderService.findList(null, orderAndSceneFilter, null);
					if(!myHyOrder.isEmpty()){
						//遍历一下 准备筛选星级
						for(HyOrder HyOrderItem : myHyOrder){
							//得到所有的Items
							List<HyOrderItem> myHyOrderItem = HyOrderItem.getOrderItems();
							//遍历Items看看哪个星级可以
							if(!myHyOrderItem.isEmpty()){
								for(HyOrderItem HyOrderItems : myHyOrderItem){
									//找到scene的ID，之后查找看看星级对不对
									Long sceneId = HyOrderItems.getProductId();
									List<Filter> SceneFilter = new ArrayList<Filter>();
									//没有别的筛选条件 这里只用sceneId筛选相同景区
									if(sceneId != null){
										SceneFilter.add(Filter.eq("id", sceneId));
									}
									if(ticketName != null){
										SceneFilter.add(Filter.eq("sceneName", ticketName));
									}
									//筛选
									List<HyTicketScene> myTicketScene = hyTicketSceneService.findList(null, SceneFilter, null);
									//遍历看star
									for(HyTicketScene myHyTicketScene : myTicketScene){
										//筛选出来所有的变量都是符合条件的
										Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
										touristsAttractionInfo.put("id", HyOrderItem.getId());
										touristsAttractionInfo.put("status", HyOrderItem.getStatus());
										touristsAttractionInfo.put("orderNumber", HyOrderItem.getOrderNumber());
										touristsAttractionInfo.put("name", HyOrderItem.getName());
										touristsAttractionInfo.put("star", myHyTicketScene.getStar());
										touristsAttractionInfo.put("source", HyOrderItem.getSource());
										touristsAttractionInfo.put("createtime", HyOrderItem.getCreatetime());
										//加入List中
										orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
									}
								}
							}
						}
					}
					page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
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
	 
		 
	 //预付款界面的
	 @RequestMapping(value = "list/sixth/advance/payment")
		@ResponseBody
		public Json sixthPageAdvancePaymentDetail(Long id) {
			Json j = new Json();
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
					//订单号
					touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
					
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					HyOrderItem myHyOrderItem = null;
					if(!HyOrderItems.isEmpty())
						myHyOrderItem = HyOrderItems.get(0);
					if(myHyOrderItem != null){
						//产品ID
						touristsAttractionInfo.put("id", myHyOrderItem.getId());
						//产品数量
						touristsAttractionInfo.put("number", myHyOrderItem.getNumber());
						//产品信息(名称2)
						touristsAttractionInfo.put("name2", myHyOrderItem.getName());
					}
						
					//下单日期
					touristsAttractionInfo.put("createtime", myHyOrder.getCreatetime());
					//产品信息(名称1)
					touristsAttractionInfo.put("name1", myHyOrder.getName());
					//订单总额
					touristsAttractionInfo.put("jiusuanMoney", myHyOrder.getJiusuanMoney());
					orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
				}
				
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 //这个写详情页 这个不需要获取权限
	 @RequestMapping(value = "list/sixth/orderDetail")
		@ResponseBody
		public Json sixthPageOrder(Long id) {
			Json j = new Json();
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
					//订单号
					touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
					//产品名称
					touristsAttractionInfo.put("name", myHyOrder.getName());
					//支付状态
					touristsAttractionInfo.put("paystatus", myHyOrder.getPaystatus());
					//退款状态
					touristsAttractionInfo.put("refundstatus", myHyOrder.getRefundstatus());
					//订单状态
					touristsAttractionInfo.put("status", myHyOrder.getStatus());
					//订单金额
					touristsAttractionInfo.put("jiusuanMoney", myHyOrder.getJiusuanMoney());
					//扣点方式
					touristsAttractionInfo.put("koudianMethod", myHyOrder.getKoudianMethod());
					//扣点金额
					touristsAttractionInfo.put("koudianMoney", myHyOrder.getKoudianMoney());
					//联系电话
					touristsAttractionInfo.put("phone", myHyOrder.getPhone());
					//下单时间
					touristsAttractionInfo.put("createtime", myHyOrder.getCreatetime());
					//合同号
					touristsAttractionInfo.put("contractNumber", myHyOrder.getContractNumber());
					//确认状态
					touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
//					//产品状态 这个不用管
//					touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
					//订单来源
					touristsAttractionInfo.put("source", myHyOrder.getSource());
					//成交数量 
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					HyOrderItem myHyOrderItem = null;
					if(!HyOrderItems.isEmpty())
						myHyOrderItem = HyOrderItems.get(0);
					if(myHyOrderItem != null)
						touristsAttractionInfo.put("number", myHyOrderItem.getNumber());
					//扣点比例
					touristsAttractionInfo.put("proportion", myHyOrder.getProportion());
					//联系人
					touristsAttractionInfo.put("contact", myHyOrder.getContact());
					//备注
					touristsAttractionInfo.put("remark", myHyOrder.getRemark());
					//还需要返回 优惠价格 原价（外卖价格感觉是） 调整金额
					touristsAttractionInfo.put("discountedPrice", myHyOrder.getDiscountedPrice());
					touristsAttractionInfo.put("waimaiMoney", myHyOrder.getWaimaiMoney());
					touristsAttractionInfo.put("adjustMoney", myHyOrder.getAdjustMoney());
					
					orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
				}

			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 
	 
	 //这个写详情页 这个不需要获取权限
	 @RequestMapping(value = "list/sixth/customerDetail")
		@ResponseBody
		public Json sixthPageCustomer(Long id, Pageable pageable) {
			Json j = new Json();
			Page<HashMap<String, Object>> page = null;//分页
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//order--items--customers
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
				if(!HyOrderItems.isEmpty()){
					for(HyOrderItem myHyOrderItems : HyOrderItems){
						List<HyOrderCustomer> HyOrderCustomers = myHyOrderItems.getHyOrderCustomers();
						if(!HyOrderCustomers.isEmpty()){
							for(HyOrderCustomer myHyOrderCustomer : HyOrderCustomers){
								//开始用Map赋值
								Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
								touristsAttractionInfo.put("name", myHyOrderCustomer.getName());
								touristsAttractionInfo.put("number", myHyOrderCustomer.getCertificate());
								touristsAttractionInfo.put("phone", myHyOrderCustomer.getPhone());
								orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
							}
						}
					}
				}
				page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
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
	 
	 
	 @RequestMapping(value = "list/sixth/commodityDetail")
		@ResponseBody
		public Json sixthPageCommodity(Long id) {
			Json j = new Json();
			//这个信息就只有一条
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//根据传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					if(!HyOrderItems.isEmpty()){
						//Items最多就只有一条
						HyOrderItem myHyOrderItem = HyOrderItems.get(0);
						Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
						//状态
						touristsAttractionInfo.put("status", myHyOrder.getStatus());
						//商品编号
						touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
						//商品名称
						touristsAttractionInfo.put("name", myHyOrder.getName());
						//类型
						touristsAttractionInfo.put("type", myHyOrder.getType());
						//数量
						touristsAttractionInfo.put("number", myHyOrderItem.getNumber());
						//商品价格
						Long inboundId = myHyOrderItem.getPriceId();
						//用这个ID去找inbound表得到价格
						HyTicketPriceInbound hyTicketPriceInbound = hyTicketPriceInboundService.find(inboundId);
						if(hyTicketPriceInbound != null){
							//不确定 ！！！！！价格是不是结算价
							touristsAttractionInfo.put("price", myHyOrderItem.getJiesuanPrice());
							orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);	
						}	
					}	
				}
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 
	 //复用 这个接口感觉没问题 建勇那里能这么简略的写我也行
	 @RequestMapping(value = "list/sixth/refundAndPayDetail")
		@ResponseBody
		public Json sixthPageRefundAndPay(Long id, Integer type) {
		 Json json = new Json();
			try {

				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", id));
				filters.add(Filter.eq("type", type));
				//2018-11-13 我看建勇加了
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createTime"));
				//从收-退款表里面找到记录，之后直接把记录返回给前端
				//type=0是付款  type=1是退款 
				//前端是否能够拿到相应的数据？能 建勇前端自己找数据的
				List<PayandrefundRecord> records = payandrefundRecordService.findList(null, filters, null);
				

				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(records);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e.getMessage());
			}
			return json;
			
		}
	 
	 
	 
	 
	 //复用 前段找不到我再找  建勇的applicationList/view直接复用
	 //我觉得这个也没问题 前端要的精确的返回了
	 @RequestMapping(value = "list/sixth/dataDetail")
		@ResponseBody
		public Json sixthPageData(Long id) {
		 Json json = new Json();
			try {
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("orderId", id));
				List<Order> orders = new LinkedList<>();
				orders.add(Order.desc("id"));
				List<HyOrderApplication> hyOrderApplications=hyOrderApplicationService.findList(null,filters,orders);
	            List<Map<String, Object>> result = new LinkedList<>();
				
				for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
					Map<String, Object> map = new HashMap<>();
					map.put("type", hyOrderApplication.getType());//1
					map.put("createtime", hyOrderApplication.getCreatetime());//4
					map.put("id", hyOrderApplication.getId());//0
					HyAdmin myHyAdmin = hyOrderApplication.getOperator();
					String operatorName = null;
					if(myHyAdmin != null){
						operatorName = myHyAdmin.getName();
					}
					map.put("operator", operatorName);//2
//					map.put("outcome", hyOrderApplication.getOutcome());//5
					map.put("status", hyOrderApplication.getStatus());//5
					map.put("view", hyOrderApplication.getView());//3
//					map.put("hyOrderApplicationItems", hyOrderApplication.getHyOrderApplicationItems());//6
					result.add(map);
				}	
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(result);
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询错误： " + e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			return json;
		}
	 
	 
		//订单日志
		@RequestMapping("list/sixth/orderLogs/view")
		@ResponseBody
		public Json orderLogs(Long id)
		{
			Json json=new Json();
			try {
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("orderId", id));
				List<Order> orders = new LinkedList<>();
				orders.add(Order.desc("id"));
				List<HyOrderApplication> hyOrderApplications=hyOrderApplicationService.findList(null,filters,orders);
	            List<Map<String, Object>> result = new LinkedList<>();
				for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
					Map<String, Object> map = new HashMap<>();
					map.put("type", hyOrderApplication.getType());
					map.put("operator", hyOrderApplication.getOperator().getName());
					map.put("view", hyOrderApplication.getView()); //意见
					map.put("createTime", hyOrderApplication.getCreatetime()); //操作时间
					map.put("status", hyOrderApplication.getStatus()); //0驳回,1通过
					result.add(map);
				}
				json.setSuccess(true);
				json.setObj(result);
				json.setMsg("查询成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
			return json;
		}
	 
	 
	 //************************上面的接口是详情页列表页一类的*****************************
	 
	 //************************下面的接口是门店下单一类的*****************************
	 	//取消线路订单 这里变成取消门票订单 直接复用
		@RequestMapping(value = "list/seventh/store_cancel")
		@ResponseBody
		public Json cancel(Long id, HttpSession session) {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			Json json = new Json();
			try {
				//这个不能复用 注释掉建勇写的
				//hyOrderService.cancelOrder(id);
				//这里要做的是 改变状态  ---- 支付状态：待支付  订单状态：待门店支付 
				HyOrder order = hyOrderService.find(id);
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
				hyOrderService.update(order);//更新回去
				
				//下面这个每次完成一个流程都需要写入一次 这里取消只有也需要
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("门店取消订单");
				application.setOperator(admin);
				application.setOrderId(id);
				application.setCreatetime(new Date());
				application.setStatus(HyOrderApplication.STATUS_ACCEPT);
				application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
				hyOrderApplicationService.save(application);

				//2018-11-7 加上库存
				List<HyOrderItem> myOrderItems = order.getOrderItems();
				HyOrderItem myOrderItem = myOrderItems.get(0);
				Integer myNumber = myOrderItem.getNumber();//数量
				
				Integer type = 1;//1.类型
				Date appointDate = myOrderItem.getStartDate();//2.预约日期
				Long inboundId = myOrderItem.getPriceId();//3.inboundId
				
				List<Filter> ticketInboundFilter = new ArrayList<Filter>();
				ticketInboundFilter.add(Filter.in("priceInboundId", inboundId));
				ticketInboundFilter.add(Filter.in("day", appointDate));
				ticketInboundFilter.add(Filter.in("type", type));//酒店这里是1
				List<HyTicketInbound> hyTicketInbounds = hyTicketInboundService.findList(null, ticketInboundFilter, null);
				HyTicketInbound myHyTicketInbound = null;
				if(!hyTicketInbounds.isEmpty()){
					myHyTicketInbound = hyTicketInbounds.get(0);
					//加上库存
					myHyTicketInbound.setInventory(myHyTicketInbound.getInventory() + myNumber);
					//更新回去
					hyTicketInboundService.update(myHyTicketInbound);
				}
				
				
				json.setSuccess(true);
				json.setMsg("取消成功");
				json.setObj(null);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("取消失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}

		//这里不需要再写接口了
		//是不是点返回之后，郭哥说不管 -- 那我就不写接口了
		//并且可以选择取消订单，取消后订单状态为已取消 (这个感觉才是需要向application表里面存的)
		//若不取消订单，则供应商可以进行调整金额，调整金额之后重新进行支付
	 
		
		
		//这个也是复用  这个是在支付页面点击--“查看详情”，之后又跳转回去 跳转的接口
		//问一问这个接口是不是一样的 这个不一定一样
		//!!!!!不是建勇写的 前端要的话 就给 不要就算了
		@RequestMapping("list/seventh/getStoreType/view")
		@ResponseBody
		public Json getStoreType(HttpSession session){
			Json json = new Json();
			try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin hyAdmin = hyAdminService.find(username);
				//我这里跟门店有关系么？用了前端会来找我
				Store store = storeService.findStore(hyAdmin);
				if(store==null){
					json.setSuccess(false);
					json.setMsg("门店不存在");
				}else{
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(store.getStoreType());
				}
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("获取失败");
				e.printStackTrace();
			}
			return json;
		}
		
	 
		//复用支付的接口 这个和郭哥确认过可以用 准了
		//这个支付在addStoreOrderPayment的实现类里面改变了状态
		@RequestMapping(value = "list/seventh/pay")
		@ResponseBody
		public Json pay(Long id, HttpSession session) {
			Json json = new Json();
			try {
				//支付状态变为已支付，订单状态变为待供应商确认
				//状态要不要自己存？ 这个已经改了
				//里面的操作是必要的么？StoreAccountLog HyOrderApplication StorePreSave receiptOther receiptDetail的改变 行
				//另外 需不需要改变库存？ 不需要改库存
				
				//门店状态变为已确认 这个郭哥说没法做
				json = hyOrderService.addStoreOrderPayment(id, session);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("支付错误： " + e.getMessage());
				e.printStackTrace();
			}
			return json;

		}
		//************************上面的接口是门店下单一类的*****************************
		
		//************************下面的接口是供应商确认一类的*****************************
		//进页面 如果是查看、编辑 都是对应detail/view接口 这个在门店下单已经复用过
		//之后查询留六类信息也都一样 都是复用过的接口
		//就剩下供应商确认 驳回什么的 审核信息
		
		//如果是确认 那么对应的接口是：provider_confirm 郭哥说这个一点都不用动
		// 供应商确认订单 
		@RequestMapping(value = "list/eighth/supplier_confirm")
		@ResponseBody
		public Json supplierConfirm(Long id, String view, Integer status, HttpSession session)
		{
			Json json=new Json();
			try {
				/**
				 * 获取当前用户
				 */
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin admin = hyAdminService.find(username);
				HyOrder hyOrder = hyOrderService.find(id);
				if (hyOrder == null) {
					throw new Exception("订单不存在");
				}
				if (!hyOrder.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)) {
					throw new Exception("订单状态不对");
				}
				
				//供应商驳回
				if(status==0) {
					if (view == null || view.equals("")) {
						throw new Exception("请输入驳回意见");
					}
					supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(id, view, session);
				}
				
				//供应商确认通过
				else {
					hyOrder.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
					
					//下面生成打款记录
					
					
				}
				
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("供应商确认订单");
				application.setView(view);
				application.setStatus(status);
				application.setOrderId(id);
				application.setCreatetime(new Date());
				application.setOperator(admin);
				application.setType(HyOrderApplication.PROVIDER_CONFIRM_ORDER); // 供应商确认订单
				hyOrderApplicationService.save(application);

				hyOrderService.update(hyOrder);
				json.setSuccess(true);
				json.setMsg("确认成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg("确认失败:"+e.getMessage());
			}
			return json;
		}
		
		
		//供应商调整金额
		@RequestMapping(value = "list/eighth/adjust_money")
		@ResponseBody
		public Json adjustMoney(Long id, BigDecimal adjustMoney, HttpSession session) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单无效");
				}
				if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)) {
					throw new Exception("订单状态不对");
				}
				BigDecimal oldAjustMoney = order.getAdjustMoney();
				if(oldAjustMoney==null){
					oldAjustMoney=BigDecimal.valueOf(0);
				}
				//修改订单金额
				order.setWaimaiMoney(order.getWaimaiMoney().subtract(oldAjustMoney).add(adjustMoney));
				order.setJiusuanMoney(order.getJiusuanMoney().subtract(oldAjustMoney).add(adjustMoney));
				order.setJiesuanMoney1(order.getJiesuanMoney1().subtract(oldAjustMoney).add(adjustMoney));
				order.setAdjustMoney(adjustMoney);
				
				//修改扣点金额
				if(order.getIfjiesuan()==false){	//如果没有结算
					if(order.getKoudianMethod().equals(Constants.DeductPiaowu.liushui)){
						order.setKoudianMoney(
								order.getProportion().multiply(
										order.getJiesuanMoney1()).multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP));
					}
				}
				
				
				hyOrderService.update(order);
				json.setSuccess(true);
				json.setMsg("调整金额成功");

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("调整金额失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		//供应商确认订单，如果驳回的话。我看了逻辑，驳回订单的审核流程应该可以共用。各种类型的订单不一样的地方就是在财务审核通过之后，按照订单类型作不同的操作
		//修改一个controller：在hzj03---audit里面的AccountantReview_SupplierDismissOrder_Controller
		//顺便修改一个Service：找最下面的supplierDismissOrderApplyService实现类修改完毕（修改了addSupplierDismissOrderAudit）
		//url：admin/accountant/dissmissOrder/list/view  /detail/view  /audit
		//以上的都不需要复制到自己的controller
		
		//这里把上述文件改变之后，完成了财务的审核流程
		
		
		
		
		
		//******************************下面是退款还有售后的*********************************
	 
		//有一个向application表里存的还没有写，写在下订单的地方
		
		// 添加实收付款记录 这个应该是前端说的那个
		static class ReceiptRefund {
			public Long orderId;
			public BigDecimal money;
			public Integer type;
			public String method;
			public Date collectionTime;
			public String remark;
			public String bankNum;
			public String cusName;
			public String cusBank;
			public String cusUninum;
			public String reason;
			public BigDecimal adjustMoney;
		}
		
		@Resource(name = "departmentServiceImpl")
		DepartmentService departmentService;
			
		@RequestMapping(value = "receipt_refund/add", method = RequestMethod.POST)
		@ResponseBody
		public Json addReceiptRefund(@RequestBody ReceiptRefund body, HttpSession session) {
			/**
			* 获取当前用户
			*/
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			Json json = new Json();
			try {
				Long orderId = body.orderId; // 订单id
				if (orderId == null) {
					throw new Exception("没有订单参数");
				}
				HyOrder order = hyOrderService.find(orderId);
				if (order == null) {
					throw new Exception("没有有效订单");
				}
				BigDecimal money = body.money; // 收退款钱数

				if (money == null) {
					throw new Exception("传入的钱数有误");
				}

				Integer type = body.type; // 类型

				String method = body.method; // 收退款方式

				Date collectionTime = body.collectionTime; // 收退款时间
				String remark = body.remark; // 备注
					
				String bankNum = body.bankNum;	//银行卡号
					
				String cusName = body.cusName;	//游客姓名
					
				String cusBank = body.cusBank;	//游客银行
					
				String cusUninum = body.cusUninum;	//游客联行号
					
				String reason = body.reason;	//原因
					
				BigDecimal adjustMoney = body.adjustMoney;	//调整金额

				HyReceiptRefund receiptRefund = new HyReceiptRefund();

				receiptRefund.setCollectionTime(collectionTime);
				receiptRefund.setCreateTime(new Date());
				receiptRefund.setMethod(method);
					
				receiptRefund.setOperator(admin);
				receiptRefund.setOrder(order);
				receiptRefund.setRemark(remark);
				receiptRefund.setStore(storeService.findStore(admin));
				receiptRefund.setType(type);
				receiptRefund.setBankNum(bankNum);
				receiptRefund.setStatus(0);	//待分公司财务确认
				receiptRefund.setBranch(departmentService.findCompanyOfDepartment(admin.getDepartment()));
				receiptRefund.setCusName(cusName);
				receiptRefund.setCusBank(cusBank);
				receiptRefund.setCusUninum(cusUninum);
				receiptRefund.setReason(reason);
				receiptRefund.setAdjustMoney(adjustMoney==null?BigDecimal.ZERO:adjustMoney);
				receiptRefund.setMoney(money);
				hyReceiptRefundService.save(receiptRefund);
				json.setSuccess(true);
				json.setMsg("添加成功");
				json.setObj(receiptRefund.getId());

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("添加失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		
		static class MyOrderItem implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Long itemId;
			private String name;
			private Integer type;
			private Integer priceType;
			private Integer number;
			private BigDecimal jiesuanPrice;
			private BigDecimal jiesuanRefund;
			private BigDecimal waimaiPrice;
			private BigDecimal waimaiRefund;
			private BigDecimal baoxianJiesuanPrice;
			private BigDecimal baoxianJiesuanRefund;
			private BigDecimal baoxianWaimaiPrice;
			private BigDecimal baoxianWaimaiRefund;
			private String customerName;

			public Long getItemId() {
				return itemId;
			}

			public void setItemId(Long itemId) {
				this.itemId = itemId;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Integer getType() {
				return type;
			}

			public void setType(Integer type) {
				this.type = type;
			}

			public Integer getPriceType() {
				return priceType;
			}

			public void setPriceType(Integer priceType) {
				this.priceType = priceType;
			}

			public Integer getNumber() {
				return number;
			}

			public void setNumber(Integer number) {
				this.number = number;
			}

			public BigDecimal getJiesuanPrice() {
				return jiesuanPrice;
			}

			public void setJiesuanPrice(BigDecimal jiesuanPrice) {
				this.jiesuanPrice = jiesuanPrice;
			}

			public BigDecimal getJiesuanRefund() {
				return jiesuanRefund;
			}

			public void setJiesuanRefund(BigDecimal jiesuanRefund) {
				this.jiesuanRefund = jiesuanRefund;
			}

			public BigDecimal getWaimaiPrice() {
				return waimaiPrice;
			}

			public void setWaimaiPrice(BigDecimal waimaiPrice) {
				this.waimaiPrice = waimaiPrice;
			}

			public BigDecimal getWaimaiRefund() {
				return waimaiRefund;
			}

			public void setWaimaiRefund(BigDecimal waimaiRefund) {
				this.waimaiRefund = waimaiRefund;
			}

			public BigDecimal getBaoxianJiesuanPrice() {
				return baoxianJiesuanPrice;
			}

			public void setBaoxianJiesuanPrice(BigDecimal baoxianJiesuanPrice) {
				this.baoxianJiesuanPrice = baoxianJiesuanPrice;
			}

			public BigDecimal getBaoxianJiesuanRefund() {
				return baoxianJiesuanRefund;
			}

			public void setBaoxianJiesuanRefund(BigDecimal baoxianJiesuanRefund) {
				this.baoxianJiesuanRefund = baoxianJiesuanRefund;
			}

			public BigDecimal getBaoxianWaimaiPrice() {
				return baoxianWaimaiPrice;
			}

			public void setBaoxianWaimaiPrice(BigDecimal baoxianWaimaiPrice) {
				this.baoxianWaimaiPrice = baoxianWaimaiPrice;
			}

			public BigDecimal getBaoxianWaimaiRefund() {
				return baoxianWaimaiRefund;
			}

			public void setBaoxianWaimaiRefund(BigDecimal baoxianWaimaiRefund) {
				this.baoxianWaimaiRefund = baoxianWaimaiRefund;
			}

			public String getCustomerName() {
				return customerName;
			}

			public void setCustomerName(String customerName) {
				this.customerName = customerName;
			}

		}
		
		
		/**
		 * 门店退款（售前）订单条目列表
		 */
		@RequestMapping(value = "store_refund_list")
		@ResponseBody
		public Json storeRefundList(Long id) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				/** 找退款规则需要注意 这里修改为1，100*0.01 */
				BigDecimal ticketRefundPercentage = new BigDecimal(1);
				
				List<MyOrderItem> lists = new ArrayList<>();
				for (HyOrderItem item : order.getOrderItems()) {
					MyOrderItem myOrderItem = new MyOrderItem();
					myOrderItem.setItemId(item.getId());
					myOrderItem.setType(item.getType());
					myOrderItem.setPriceType(item.getPriceType());
					myOrderItem.setName(item.getName());
					myOrderItem.setNumber(item.getNumber());
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(ticketRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(ticketRefundPercentage));
					myOrderItem.setBaoxianJiesuanPrice(BigDecimal.ZERO);
					myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
					myOrderItem.setBaoxianWaimaiPrice(BigDecimal.ZERO);
					myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());
					lists.add(myOrderItem);
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(lists);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}	
		
		//门店提交退款申请
		@RequestMapping(value = "store_refund/apply", method = RequestMethod.POST)
		@ResponseBody
		public Json storeRefundApply(@RequestBody HyOrderApplication application, HttpSession session) {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if (order == null) {
					throw new Exception("订单无效");
				}
				if(order.getIfjiesuan()==true) {
					json.setSuccess(false);
					json.setMsg("结算后不能售前退款");
					json.setObj(2);
					return json;
				}
				
				List<HyOrderItem> orderItems = order.getOrderItems();
				if(orderItems==null || orderItems.isEmpty()) {
					throw new Exception("没有有效订单条目");
				}
				HyOrderItem orderItem = orderItems.get(0);
				HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(orderItem.getProductId());
				if(hyTicketHotelandscene==null) {
					throw new Exception("没有有效门票产品");//进行修改
				}



				Map<String, Object> variables = new HashMap<>();
				/**找供应商需要注意 这个对于门票来说，不需要改*/
				//找出供应商
				HyAdmin provider = hyTicketHotelandscene.getCreator();
				// 指定审核供应商
				variables.put("provider", provider.getUsername());

				// 启动流程
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeTuiTuan", variables);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
				taskService.complete(task.getId(), variables);

				application.setContent("门店售前退款");
				application.setOperator(admin);
				application.setStatus(0); // 待供应商审核
				application.setCreatetime(new Date());
				application.setProcessInstanceId(task.getProcessInstanceId());
				application.setType(HyOrderApplication.STORE_CANCEL_GROUP);
				order.setRefundstatus(1); // 订单退款状态为退款中
				//
				hyOrderService.update(order);

				for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
					item.setHyOrderApplication(application);
				}

				hyOrderApplicationService.save(application);

				json.setSuccess(true);
				json.setMsg("门店售前退款申请成功");
				json.setObj(null);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("门店售前退款申请失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		//门店退款列表页
		@RequestMapping(value = "store_refund/list/view")
		@ResponseBody
		public Json storeCancelGroupList(Pageable pageable, Integer status, String providerName, HttpSession session) {
			return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session, 
					HyOrderApplication.STORE_CANCEL_GROUP,4);	//门票订单类型为4
		}
		
		//门店售后
		@RequestMapping(value = "store_customer_service/list/view")
		@ResponseBody
		public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
			return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
					HyOrderApplication.STORE_CUSTOMER_SERVICE,4);	//门票订单类型为4
		}
		
		
		//门店退款审核详情页
		@RequestMapping(value = { "store_refund/detail/view", "store_customer_service/detail/view" })
		@ResponseBody
		public Json storeRefundDetail(Long id) {
			Json json = new Json();

			try {
				HyOrderApplication application = hyOrderApplicationService.find(id);
				if (application == null) {
					throw new Exception("没有有效的审核申请记录");
				}
				@SuppressWarnings("unused")
				HyOrder order = hyOrderService.find(application.getOrderId());

				Map<String, Object> ans = new HashMap<>();

				/** 审核详情需要注意 这个修改完了*/
				ans.put("application", hyOrderApplicationService.auditDetailHelper(application, application.getStatus()));
				/** 审核条目详情需要注意 这个就走其他无保险类 不需要修改*/
				ans.put("applicationItems", hyOrderApplicationService.auditItemsHelper(application));

				/**
				 * 审核详情添加
				 */
				String processInstanceId = application.getProcessInstanceId();
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);

				List<Map<String, Object>> auditList = new ArrayList<>();
				for (Comment comment : commentList) {
					Map<String, Object> map = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					map.put("step", step);
					String username = comment.getUserId();

					HyAdmin admin = hyAdminService.find(username);
					String name = "";
					if (admin != null) {
						name = admin.getName();
					}
					map.put("auditName", name);
					
					String fullMsg = comment.getFullMessage();
					
					String[] msgs = fullMsg.split(":");
					map.put("comment", msgs[0]);
					if (msgs[1].equals("0")) {
						map.put("result", "驳回");
					} else if (msgs[1].equals("1")) {
						map.put("result", "通过");
					}

					map.put("time", comment.getTime());

					auditList.add(map);
				}

				ans.put("auditRecords", auditList);

				json.setSuccess(true);
				json.setMsg("查看详情成功");
				json.setObj(ans);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查看详情失败");
				json.setObj(e.getMessage());
			}
			return json;
		}	
		
		
		
		@RequestMapping(value = "store_refund/audit", method = RequestMethod.POST)
		@ResponseBody
		public Json storeRefundAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
			Json json = new Json();
			try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyOrderApplication application = hyOrderApplicationService.find(id);
				String processInstanceId = application.getProcessInstanceId();

				if (processInstanceId == null || processInstanceId.equals("")) {
					throw new Exception("审核出错，信息不完整，请重新申请");
				}

				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
																// 下一阶段审核的部门

				if (auditStatus.equals(1)) { // 如果审核通过
					map.put("msg", "true");
					if (task.getTaskDefinitionKey().equals("usertask2")) { // 如果供应商
						// 设置下一阶段审核的部门 ---
						List<Filter> filters = new ArrayList<>();
						/**审核限额需要注意 不用管*/
						filters.add(Filter.eq("eduleixing", Eduleixing.storeTuiTuanLimit));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						BigDecimal money = edu.get(0).getMoney();
						BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
						if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额，
							map.put("money", "more"); // 设置需要品控中心限额审核
							application.setStatus(1); // 待品控限额审核
						} else { // 如果退款总额不大于限额
							map.put("money", "less"); // 设置财务审核
							application.setStatus(2); // 待财务审核
						}
					} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
						application.setStatus(2); // 待财务审核
					} else if (task.getTaskDefinitionKey().equals("usertask4")) {
		
						/**财务审核通过需要注意 ！！！！这个还没开始修改！！！！*/
						// 售前退款财务审核通过，进行订单处理
						hyOrderApplicationService.handleTicketHotelandsceneScg(application);
						
						application.setStatus(4);//已退款	

						//售前退款财务审核通过，添加相关操作
						//!!!!不确定需不需要改 这里看着不需要修改
						piaowuConfirmService.piaowuRefund(application, username, 3, "门店门票售前退款");
						
					}

				} else {
					map.put("msg", "false");
					application.setStatus(5); // 已驳回
					HyOrder order = hyOrderService.find(application.getOrderId());
					order.setRefundstatus(4); // 退款已驳回
					hyOrderService.update(order);

				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						(comment == null ? "审核通过" : comment) + ":" + auditStatus);
				taskService.complete(task.getId(), map);
				hyOrderApplicationService.update(application);
				json.setSuccess(true);
				json.setMsg("审核成功");
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("审核失败");
				e.printStackTrace();
			}
			return json;
		}	
		
		
		
		@RequestMapping(value = "scs_list")
		@ResponseBody
		public Json scsList(Long id) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				List<MyOrderItem> lists = new ArrayList<>();
				for (HyOrderItem item : order.getOrderItems()) {
					MyOrderItem myOrderItem = new MyOrderItem();
					myOrderItem.setItemId(item.getId());
					myOrderItem.setType(item.getType());
					myOrderItem.setPriceType(item.getPriceType());
					myOrderItem.setName(item.getName());
					myOrderItem.setNumber(item.getNumber());
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(BigDecimal.ZERO);
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(BigDecimal.ZERO);
					myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
					myOrderItem.setBaoxianJiesuanRefund(BigDecimal.ZERO);
					myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
					myOrderItem.setBaoxianWaimaiRefund(BigDecimal.ZERO);

					lists.add(myOrderItem);
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(lists);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		//这下面的接口可能改动比较大
		@Transactional
		@RequestMapping(value = "store_customer_service/apply", method = RequestMethod.POST)
		@ResponseBody
		public Json storeCustomerServiceApply(@RequestBody HyOrderApplication application, HttpSession session) {	
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if (order == null) {
					throw new Exception("订单无效");
				}
				if(order.getIfjiesuan()==false) {
					json.setSuccess(false);
					json.setMsg("结算前不能售后退款");
					json.setObj(2);
					return json;
				}
				
				List<HyOrderItem> orderItems = order.getOrderItems();
				if(orderItems==null || orderItems.isEmpty()) {
					throw new Exception("没有有效订单条目");
				}
				HyOrderItem orderItem = orderItems.get(0);
				//！！！！这个明显需要修改
				HyTicketScene hyTicketScene = hyTicketSceneService.find(orderItem.getProductId());
				if(hyTicketScene==null) {
					throw new Exception("没有有效的酒加景产品");
				}



				Map<String, Object> variables = new HashMap<>();
				/**找供应商需要注意 这里修改完毕*/
				//找出供应商
				HyAdmin provider = hyTicketScene.getCreator();
				// 指定审核供应商
				variables.put("provider", provider.getUsername());

				// 启动流程
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeShouHou", variables);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
				taskService.complete(task.getId(), variables);

				application.setContent("门店售后退款");
				application.setOperator(admin);
				application.setStatus(0); // 待供应商审核
				application.setCreatetime(new Date());
				application.setProcessInstanceId(task.getProcessInstanceId());
				application.setType(HyOrderApplication.STORE_CUSTOMER_SERVICE);
				order.setRefundstatus(1); // 订单退款状态为退款中
				//
				hyOrderService.update(order);

				for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
					item.setHyOrderApplication(application);
				}

				hyOrderApplicationService.save(application);

				json.setSuccess(true);
				json.setMsg("门店售后申请成功");
				json.setObj(null);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("门店售后申请失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}

		@RequestMapping(value = "store_customer_service/audit", method = RequestMethod.POST)
		@ResponseBody
		public Json storeCustomerServiceAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
			Json json = new Json();
			try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyOrderApplication application = hyOrderApplicationService.find(id);
				@SuppressWarnings("unused")
				String applyName = application.getOperator().getUsername(); // 找到提交申请的人
				String processInstanceId = application.getProcessInstanceId();

				if (processInstanceId == null || processInstanceId.equals("")) {
					throw new Exception("审核出错，信息不完整，请重新申请");
				}

				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
																// 下一阶段审核的部门

				if (auditStatus.equals(1)) { // 如果审核通过
					map.put("msg", "true");
					if (task.getTaskDefinitionKey().equals("usertask2")) { // 如果供应商
						// 设置下一阶段审核的部门 ---
						List<Filter> filters = new ArrayList<>();
						/**审核额度需要注意 */
						filters.add(Filter.eq("eduleixing", Eduleixing.storeShouHouLimit));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						BigDecimal money = edu.get(0).getMoney();
						BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
						if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额，
							map.put("money", "more"); // 设置需要品控中心限额审核
							application.setStatus(1); // 待品控限额审核
						} else { // 如果退款总额不大于限额
							map.put("money", "less"); // 设置财务审核
							application.setStatus(2); // 待财务审核
						}
					} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
						application.setStatus(2); // 待财务审核
					} else if (task.getTaskDefinitionKey().equals("usertask4")) {
		
						/**财务审核通过需要注意*/
						// 售前退款财务审核通过，进行订单处理
						hyOrderApplicationService.handleTicketHotelandsceneScs(application);
						
						application.setStatus(4);//已退款
						
						//售前退款财务审核通过，请王劼同学添加相关操作
						piaowuConfirmService.shouhouPiaowuRefund(application, username, 3, "门店门票售后退款");
						
					}

				} else {
					map.put("msg", "false");
					application.setStatus(5); // 已驳回
					HyOrder order = hyOrderService.find(application.getOrderId());
					order.setRefundstatus(4); // 退款已驳回
					hyOrderService.update(order);

				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						(comment == null ? "审核通过" : comment) + ":" + auditStatus);
				taskService.complete(task.getId(), map);
				hyOrderApplicationService.update(application);
				json.setSuccess(true);
				json.setMsg("审核成功");
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("审核失败");
				e.printStackTrace();
			}
			return json;
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
		

}
