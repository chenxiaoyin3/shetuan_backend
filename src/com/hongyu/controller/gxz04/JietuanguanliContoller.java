package com.hongyu.controller.gxz04;

import com.grain.util.JsonUtils;
import com.hongyu.*;
import com.hongyu.entity.*;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.GroupSendGuide.GuideStatusEnum;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.*;
import com.hongyu.util.*;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;
import com.hongyu.util.wechatUtilEntity.TemplateMsgResult;
import com.hongyu.util.wechatUtilEntity.WechatTemplateMsg;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/linegroup")
public class JietuanguanliContoller {
	
	
	
	public static class Wrap{
		//定义内部类，生成get set方法
		private Long groupId;
		private List<HyGroupPriceSwd> hyGroupPriceSwds = new ArrayList<>();
		private List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds = new ArrayList<>();
		private List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds = new ArrayList<>();
		private List<HyGroupPriceSwd> getHyGroupPriceSwds() {
			return hyGroupPriceSwds;
		}
		public void setHyGroupPriceSwds(List<HyGroupPriceSwd> hyGroupPriceSwds) {
			this.hyGroupPriceSwds = hyGroupPriceSwds;
		}
		public List<HyGroupOtherpriceSwd> getHyGroupOtherpriceSwds() {
			return hyGroupOtherpriceSwds;
		}
		public void setHyGroupOtherpriceSwds(List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds) {
			this.hyGroupOtherpriceSwds = hyGroupOtherpriceSwds;
		}
		public List<HyGroupSpecialPriceSwd> getHyGroupSpecialPriceSwds() {
			return hyGroupSpecialPriceSwds;
		}
		public void setHyGroupSpecialPriceSwds(List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds) {
			this.hyGroupSpecialPriceSwds = hyGroupSpecialPriceSwds;
		}
		public Long getGroupId() {
			return groupId;
		}
		public void setGroupId(Long groupId) {
			this.groupId = groupId;
		}
		
	}

	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "languageServiceImpl")
	LanguageService languageService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
    @Resource(name = "guideLanguageServiceImpl")
    GuideLanguageService guideLanguageService;
	
	@Resource(name = "hyServiceFeeCarServiceImpl")
	HyServiceFeeCarService hyServiceFeeCarService;
	
	@Resource(name = "hyServiceFeeNoncarServiceImpl")
	HyServiceFeeNoncarService hyServiceFeeNoncarService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	@Resource(name = "hyGroupSpecialpriceServiceImpl")
	HyGroupSpecialpriceService hyGroupSpecialpriceService;
	@Resource(name = "hyGroupOtherpriceServiceImpl")
	HyGroupOtherpriceService hyGroupOtherpriceService;
	@Resource(name = "hyGroupCancelAuditServiceImpl")
	HyGroupCancelAuditService hyGroupCancelAuditService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "groupSendGuideServiceImp")
	private GroupSendGuideService groupSendGuideService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "groupBiankoudianServiceImpl")
	GroupBiankoudianService groupBiankoudianService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "groupXiaotuanServiceImpl")
	GroupXiaotuanService groupXiaotuanService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyGroupPriceSwdServiceImpl")
	HyGroupPriceSwdService hyGroupPriceSwdService;
	
	@Resource(name = "hyGroupOtherpriceSwdServiceImpl")
	HyGroupOtherpriceSwdService hyGroupOtherpriceSwdService;
	
	@Resource(name = "hyGroupSpecialPriceSwdServiceImpl")
	HyGroupSpecialPriceSwdService hyGroupSpecialPriceSwdService;
	
	@Resource(name = "hyGroupShenheSwdServiceImpl")
	HyGroupShenheSwdService hyGroupShenheSwdService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;

	
	@Resource(name = "hySpecialtyLineLabelServiceImpl")
	HySpecialtyLineLabelService hySpecialtyLineLabelService;
	
	@Resource(name = "hyLineLabelServiceImpl")
	HyLineLabelService hyLineLabelService;

	@Value("${guide.guideRobbing}")
	private String guideRobbing;

	@Value("${guide.guideAssignmentSite}")
    private String guideAssignmentSite;
	
	/**
	 * 接团管理列表页
	 * @param pageable
	 * @param hyLine
	 * @param hyGroup
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HyGroup hyGroup,
					 HttpSession session, String start, String end, HttpServletRequest request) {
		Json j = new Json();
		try {
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = simpleFormat.parse(start);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(fromDate);
			calendar.add(Calendar.DATE, -1);
			fromDate = calendar.getTime();
			Date toDate = simpleFormat.parse(end);
			calendar.setTime(toDate);
			calendar.add(Calendar.DATE, 1);
			toDate = calendar.getTime();
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);

			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("creator", hyAdmins));
			filters.add(Filter.gt("startDay", fromDate));
			filters.add(Filter.lt("startDay", toDate));
			filters.add(Filter.eq("auditStatus", AuditStatus.pass));
			
			Set<HySupplierContract> cs = new HashSet<HySupplierContract>();
			if(admin.getHyAdmin() == null) {//该账号是供应商父账号
				cs = admin.getLiableContracts();
			}
			else {//该账号是供应商子账号
				cs = admin.getHyAdmin().getLiableContracts();
			}
			List<Filter> lineFilters = new ArrayList<Filter>();
			if(!cs.isEmpty()) {
				lineFilters.add(Filter.in("contract", cs));
			}
						
			List<HyLine> lines = hyLineService.findList(null, lineFilters, null);
			if(!lines.isEmpty()) {
				filters.add(Filter.in("line", lines));
			}
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<HyGroup> groups = hyGroupService.findPage(pageable, hyGroup);
			
			if(groups.getRows().size() > 0) {
				for(HyGroup group : groups.getRows()) {
					HyAdmin creator = group.getCreator();
					HashMap<String, Object> hm = new HashMap<String, Object>();
					HyLine line = group.getLine();
			        hm.put("id", group.getId());
			        hm.put("pn", line.getPn());
			        hm.put("endDay", group.getEndDay());
			        hm.put("startDay", group.getStartDay());
			        hm.put("name", line.getName());
			        hm.put("teamType", group.getTeamType());
			        hm.put("publishRange", group.getPublishRange());
			        hm.put("stock", group.getStock());
			        hm.put("remainNumber", group.getRemainNumber());
			        hm.put("signupNumber", group.getSignupNumber());
			        hm.put("occupyNumber", group.getOccupyNumber());
			        hm.put("supplierName", line.getContract().getHySupplier().getSupplierName());
			        hm.put("operator", line.getOperator().getName());
			        hm.put("groupState", group.getGroupState());
			        hm.put("name", line.getName());
			        hm.put("lineType", line.getLineType());
			        hm.put("koudianType", group.getKoudianType());
			        hm.put("percentageKoudian", group.getPercentageKoudian());
			        hm.put("personKoudian", group.getPersonKoudian());
			        hm.put("isDisplay", group.getIsDisplay());
			        hm.put("isInner", line.getIsInner());
			        
			        //add by gxz 20190521计算该团团款
			        BigDecimal totalMoney = BigDecimal.ZERO;
			        List<Filter> filters1 = new ArrayList<>();
					filters1.add(Filter.eq("groupId", group.getId()));
					
					//只筛选订单状态为供应商通过的,added by GSbing,20190227
					filters1.add(Filter.eq("status", 3));
					
					List<HyOrder> hyOrders = hyOrderService.findList(null, filters1, null);
					for(HyOrder temp : hyOrders) {
						//added by GSbing,报账订单考虑门店退团的情况
						List<HyOrderItem> orderItems=temp.getOrderItems();
						Integer returnQuantity=0;
						for(HyOrderItem item:orderItems) {
							returnQuantity=returnQuantity+item.getNumberOfReturn();
						}
						
						//只显示未完全退团的订单
						if(temp.getPeople() > returnQuantity) {
//							System.out.println("order id is "+ temp.getId());
							totalMoney = totalMoney.add(temp.getJiesuanMoney1().subtract(temp.getJiesuanTuikuan())
										.subtract(temp.getStoreFanLi()).subtract(temp.getDiscountedPrice()));	
						}		
					}
					hm.put("totalMoney", totalMoney);
					//end of add
					
			        if(group.getRegulateId() != null) {
			        	HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
			        	hm.put("jidiaoStatus", regulate.getStatus());
			        }
			        		        
			      	/** 当前用户对本条数据的操作权限 */
					if(creator.equals(admin)){
						if(co == CheckedOperation.view) {
								hm.put("privilege", "view");
						} else {
								hm.put("privilege", "edit");
						}
					} else{
						if(co == CheckedOperation.edit) {
							hm.put("privilege", "edit");
						} else {
							hm.put("privilege", "view");
						}
					}
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(groups.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(groups.getPageNumber()));
			obj.put("total", Long.valueOf(groups.getTotal()));
     		obj.put("rows", lhm);
			
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 抢单发布内容和导游列表
	 * @param id 团ID
	 * @return
	 */
	@RequestMapping(value = "publishDetail/view")
	public Json publishDetail(Long groupId) {
		Json j = new Json(); 
		try {
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			List<Map<String, Object>> llhm = new ArrayList<>();
			HyGroup group = hyGroupService.find(groupId);
			
			List<Filter> filters = new ArrayList<>();//查找状态为正常的
			filters.add(Filter.eq("status", GuideStatusEnum.zhengchang));
			filters.add(Filter.eq("group", group));
			List<GroupSendGuide> a = groupSendGuideService.findList(null, filters, null);
			
			for(GroupSendGuide temp : a) {
				HashMap<String, Object> hm = new HashMap<>();
			
				hm.put("id", temp.getId());
				hm.put("serviceType", temp.getServiceType());
				hm.put("serviceFee", temp.getServiceFee());
				hm.put("guideNo", temp.getGuideNo());
				hm.put("manReceive", temp.getManReceive());
				hm.put("womanReceive", temp.getWomanReceive());
				hm.put("allReceive", temp.getAllReceive());
				hm.put("isRestrictSex", temp.getIsRestrictSex());
				hm.put("manNo", temp.getManNo());
				hm.put("womanNo", temp.getWomanNo());
				hm.put("lowestLevel", temp.getLowestLevel());
				hm.put("lowestStar", temp.getLowestStar());
				hm.put("days", temp.getDays());
				hm.put("fuwuneirong", temp.getFuwuneirong());
				hm.put("status", temp.getStatus());
				if(temp.getIsRestrictLanguage() == false) {
					hm.put("languages", "不限");
				} else if (temp.getIsRestrictLanguage() == true) {
					String langauges = temp.getLanguages();
					String[] ss = langauges.split(",");
					String s = "";
					for(String t : ss) {
						s = s + languageService.find(Long.valueOf(t)).getName() + ",";
					}
					s = s.substring(0, s.length() - 1);
					hm.put("languages", s);
				}
				
				hm.put("startTime", temp.getStartTime());
				hm.put("endTime", temp.getEndTime());
				hm.put("xiaofei", temp.getXiaofei());
				hm.put("expireTime", temp.getExpireTime());
				
				lhm.add(hm);
			}
			
			obj.put("groupSendGuides", lhm); //抢单发布内容
			
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));
			List<GuideAssignment> b = guideAssignmentService.findList(null, filters, null);
			for(GuideAssignment temp : b) {
				HashMap<String, Object> hm = new HashMap<>();
				Guide guide = guideService.find(temp.getGuideId());
				hm.put("id", temp.getId());
				hm.put("gId",guide.getId());
				hm.put("guideId", guide.getGuideSn());
				hm.put("name", guide.getName());
				hm.put("serviceType", temp.getServiceType());
				hm.put("startDate", temp.getStartDate());
				hm.put("endDate", temp.getEndDate());
				hm.put("sex", guide.getSex());
				hm.put("serviceFee", temp.getServiceFee());
				hm.put("tip", temp.getTip());
				hm.put("assignmentType", temp.getAssignmentType());
				hm.put("status", temp.getStatus());
				hm.put("confirmDate", temp.getConfirmDate());
				hm.put("quxiaoDate", temp.getQuxiaoDate());
				hm.put("reason", temp.getReason());
				llhm.add(hm);
			}
			obj.put("guideAssignments", llhm); //导游派遣列表
			
			j.setSuccess(true);
			j.setMsg("查看成功");		
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 取消抢单发布
	 * @param id 发布抢单id
	 * @return
	 */
	@RequestMapping(value = "cancelPublish")
	public Json cancelPublish(Long id) {
		Json j = new Json(); 
		try {
			GroupSendGuide a = groupSendGuideService.find(id);
			a.setStatus(GuideStatusEnum.quxiao);
			a.setCancelTime(new Date());
			
			List<Filter> assignmentFilters = new ArrayList<>();
			assignmentFilters.add(Filter.eq("qiangdanId", a.getId()));
			assignmentFilters.add(Filter.eq("status", 0));	//待确认
			List<GuideAssignment> assignments = guideAssignmentService.findList(null,assignmentFilters,null);
			for(GuideAssignment assignment:assignments) {
				assignment.setStatus(3);	//已取消
				guideAssignmentService.update(assignment);
			}
			groupSendGuideService.update(a);
			
			j.setSuccess(true);
			j.setMsg("取消成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 发布抢单的所有语言
	 * @return
	 */
	@RequestMapping(value = "languages/view")
	public Json languages() {
		Json j = new Json(); 
		try {
			
			List<Language> languages = languageService.findAll();
			j.setSuccess(true);
			j.setMsg("查看成功");	
			j.setObj(languages);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value = "testPublish")
	public Json testPublish() {
		Json j = new Json(); 
		String url=guideRobbing+"id="+11;
		PublishWeixintuisong.tuisong(url, "ox5AHj6iJnCcO6XF4dtLIodhgaB8", null);
		j.setSuccess(true);
		j.setMsg("推送测试");
		return j;
	}
	/**
	 * 发布导游抢单
	 * @param groupId
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "publish")
	public Json publish(GroupSendGuide groupSendGuide, Long groupId, String start, String end, HttpSession session) {
		Json j = new Json(); 
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyGroup group = hyGroupService.find(groupId);
			HyLine line = group.getLine();
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = simpleFormat.parse(start);
			Date toDate = simpleFormat.parse(end);
			groupSendGuide.setStartTime(fromDate);
			groupSendGuide.setEndTime(toDate);
			
			groupSendGuide.setGroup(group);
			groupSendGuide.setOperator(group.getCreator());
			groupSendGuide.setCreator(admin);
			groupSendGuide.setStatus(GuideStatusEnum.zhengchang);
			groupSendGuide.setLineType(line.getLineType());
			groupSendGuide.setTeamType(group.getTeamType());
			groupSendGuide.setName(line.getName());
			//设置服务内容
			if(groupSendGuide.getServiceType() == 0) { //全陪设置为lineTravels
				String s = "";
				int index = 1;
				for(HyLineTravels temp : line.getLineTravels()) {
					s = s + "第" + index + "天:" + temp.getTransport().getName() + " " + temp.getRoute() + "," + "\n";
					index ++;
				}
				groupSendGuide.setFuwuneirong(s);					
				
			} 
			
			//计算服务天数
			SimpleDateFormat simpleFormat1 = new SimpleDateFormat("yyyy-MM-dd");
			String fromDate1 = simpleFormat.format(groupSendGuide.getStartTime());  
			String toDate1 = simpleFormat.format(groupSendGuide.getEndTime());  
			long from = simpleFormat1.parse(fromDate1).getTime();  
			long to = simpleFormat1.parse(toDate1).getTime();  
			int days = (int) ((to - from)/(1000 * 60 * 60 * 24)) + 1; //开始结束日期相同是一天
			groupSendGuide.setDays(days);
			
			
			
			String serviceFee = "";
			
			if(groupSendGuide.getServiceType() == 0) { //全陪服务费
				for(int i = 0; i <= 5; i ++) {
					ArrayList<Filter> filters = new ArrayList<>();
					if(line.getLineType() == LineType.qiche) { //汽车游			
						filters.add(Filter.eq("groupType", group.getTeamType()));
						filters.add(Filter.eq("star", i)); //基础星级导游
						if(group.getTeamType() == false) { //汽车游散客
							filters.add(Filter.eq("days", days));
							List<HyServiceFeeCar> list = hyServiceFeeCarService.findList(null, filters, null);
							if(!list.isEmpty()) {
								serviceFee = serviceFee + i + "星:" + list.get(0).getPrice().multiply(BigDecimal.valueOf(days)) + ",";							
							}
						} else if (group.getTeamType() == true) { //汽车游团队
							filters.add(Filter.eq("days", days));
							List<HyServiceFeeCar> list = hyServiceFeeCarService.findList(null, filters, null);
							if(!list.isEmpty()) {
								if(days >= 5) {
									serviceFee = serviceFee + i + "星:" + list.get(0).getPrice().multiply(BigDecimal.valueOf(days)) + ",";//汽车团队五天以上服务费								
								} else if (days >= 1 && days <= 4){
									serviceFee = serviceFee + i + "星:" + list.get(0).getPrice() + ","; //汽车游团队五天以下服务费
								} else {
									throw new IllegalArgumentException();
								}
							
							}
						}
						
						
					} else if (line.getLineType() == LineType.chujing || line.getLineType() == LineType.guonei) { //国内和出境游
						filters.add(Filter.eq("lineType", line.getLineType()));
						filters.add(Filter.eq("groupType", group.getTeamType()));
						filters.add(Filter.eq("star", i));
						List<HyServiceFeeNoncar> list = hyServiceFeeNoncarService.findList(null, filters, null);
						if(!list.isEmpty()) {
							serviceFee = serviceFee + i + "星:" + list.get(0).getPrice().multiply(BigDecimal.valueOf(days)) + ","; //国内和出境游服务费					
						}
					} else {
						throw new IllegalArgumentException("线路类型错误");
					}
					
				}
				
			} else if (groupSendGuide.getServiceType() == 1) { //其他服务服务费
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("eduleixing", Eduleixing.elseFee));
				List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
				CommonShenheedu xiane = edu.get(0);
//				Xiane xiane = xianeService.find("elseFee");
				serviceFee = String.valueOf(xiane.getMoney().multiply(BigDecimal.valueOf(days))) + ",";

			}
			serviceFee = serviceFee.substring(0, serviceFee.length() - 1);
			
			groupSendGuide.setServiceFee(serviceFee);
			groupSendGuideService.save(groupSendGuide);
			
			//发布抢单成功以后微信推送消息
			TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
			params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
			params.put("keyword1", WechatTemplateMsg.item(line.getName(), "#000000"));
			params.put("keyword2", WechatTemplateMsg.item(group.getTeamType() == false ? "散客" : "团客", "#000000"));
			params.put("keyword3", WechatTemplateMsg.item(group.getStartDay() + "至" + group.getEndDay(), "#000000"));
			params.put("keyword4", WechatTemplateMsg.item(groupSendGuide.getServiceType() == 0 ? "全陪服务" : "其他服务", "#000000"));
			params.put("keyword5", WechatTemplateMsg.item(serviceFee + "元", "#000000"));
			params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
			

			List<Filter> guideFilters = new ArrayList<>();
			guideFilters.add(Filter.ge("zongheLevel", groupSendGuide.getLowestStar()));
			guideFilters.add(Filter.ge("rank", groupSendGuide.getLowestLevel()));
			guideFilters.add(Filter.eq("status", 1));

			List<Guide> paiguides = guideService.findList(null,guideFilters,null);
			for(Guide guide : paiguides) {
				
//				List<Filter> notAssignFilters = new ArrayList<>();
//				notAssignFilters.add(Filter.eq("guideId", guide.getId()));
//				notAssignFilters.add(Filter.le("status", 1));
//				notAssignFilters.add(Filter.le("startDate", groupSendGuide.getEndTime()));
//				notAssignFilters.add(Filter.ge("endDate", groupSendGuide.getStartTime()));
//				List<GuideAssignment> notAssignmentGuides = guideAssignmentService.findList(null,notAssignFilters,null);
//				if(notAssignmentGuides!=null && !notAssignmentGuides.isEmpty()) {
//					continue;
//				}
//				//生成导游派遣信息
//				GuideAssignment guideAssignment = new GuideAssignment();
//				guideAssignment.setLineType(groupSendGuide.getLineType());
//				guideAssignment.setTeamType(groupSendGuide.getTeamType()?1:0);
//				guideAssignment.setGroupId(group.getId());
//				guideAssignment.setGuideId(guide.getId());
//				guideAssignment.setAssignmentType(2);	//这是哪个？
//				guideAssignment.setStartDate(groupSendGuide.getStartTime());
//				guideAssignment.setEndDate(groupSendGuide.getEndTime());
//				guideAssignment.setDays(groupSendGuide.getDays());
//				
//				Json json2 = guideService.caculate(groupSendGuide.getLineType(), 
//						groupSendGuide.getServiceType(),groupSendGuide.getTeamType() , guide.getZongheLevel(), days);
//				BigDecimal serviceFee1;
//				if (!json2.isSuccess()) {
//					j = json2;
//					return j;
//				} else {
//					serviceFee1 = (BigDecimal) json2.getObj();
//				}
//				
//				guideAssignment.setServiceFee(serviceFee1);
//				
//				//导游派遣信息
//				guideAssignment.setOrderId(null);
//				guideAssignment.setServiceType(groupSendGuide.getServiceType());
//				guideAssignment.setLineName(groupSendGuide.getName());
//				guideAssignment.setTravelProfile(groupSendGuide.getFuwuneirong());
//				guideAssignment.setTip(groupSendGuide.getXiaofei());
//				guideAssignment.setTotalFee(guideAssignment.getTip().add(guideAssignment.getServiceFee()));
//				guideAssignment.setOperator(username);
//				guideAssignment.setOperatorPhone(admin.getMobile());
//				guideAssignment.setPaiqianDate(new Date());
//				guideAssignment.setStatus(0);
//				
//				guideAssignment.setQiangdanId(groupSendGuide.getId());
//				guideAssignmentService.save(guideAssignment);
				
				String url=guideRobbing+"id="+groupSendGuide.getId();
				PublishWeixintuisong.tuisong(url, guide.getOpenId(), params);			
				
			}
			
			System.out.println("推送成功");
			j.setSuccess(true);
			j.setMsg("发布抢单成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 派遣导游列表
	 * @param pageable
	 * @param session
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "guidelist/view")
	public Json guidelist(Pageable pageable, String start, String end, 
						  HttpSession session, Guide param) {
		Json j = new Json(); 
		try {
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = simpleFormat.parse(start);
			Date toDate = simpleFormat.parse(end);
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			//根据导游派遣表找出不符合条件的导游
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.gt("startDate", fromDate));
			filters.add(Filter.lt("endDate", toDate));
			filters.add(Filter.eq("status", 1));
			filters.add(Filter.eq("status", 0));
			List<GuideAssignment> list = guideAssignmentService.findList(null, filters, null);
			Set<String> gs = new HashSet<>();
			
			for(GuideAssignment temp : list) {
				gs.add(guideService.find(temp.getGuideId()).getGuideSn());
			}
			
			List<String> listss = new ArrayList<>();
			List<Guide> a = guideService.findAll();			
	
			for(Guide tmp : a) {
				if(!gs.contains(tmp.getGuideSn())) {
					listss.add(tmp.getGuideSn());
				}
			}
			filters.clear();
			filters.add(Filter.in("guideSn", listss));	
			pageable.setFilters(filters);
			
			Page<Guide> guides = guideService.findPage(pageable, param);
			if(guides.getRows().size() > 0) {
				for(Guide guide : guides.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("id", guide.getId());
					hm.put("guideSn", guide.getGuideSn());
					hm.put("name", guide.getName());
					hm.put("guideLanguages", guide.getGuideLanguages());
					hm.put("sex", guide.getSex());
					hm.put("rank", guide.getRank());
					hm.put("zongheLevel", guide.getZongheLevel());
					if(guide.getIdNumber() != null && guide.getIdNumber().length() == 18) {
						hm.put("age", getAge(guide.getIdNumber()));
					}
					hm.put("phone", guide.getPhone());
					
					
					lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(guides.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(guides.getPageNumber()));
			obj.put("total", Long.valueOf(guides.getTotal()));
     		obj.put("rows", lhm);
			
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
			
			j.setSuccess(true);
			j.setMsg("保存成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 指派导游获取导游服务费
	 * @param guideId 导游id
	 * @return
	 */
	@RequestMapping(value = "guideServiceFee/view")
	public Json guideServiceFee(Long guideId, Long groupId, String start, String end, Integer serviceType) {
		Json j = new Json(); 
		try {
			//计算服务 
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
			long from = simpleFormat.parse(start).getTime();  
			long to = simpleFormat.parse(end).getTime();  
			int days = (int) ((to - from)/(1000 * 60 * 60 * 24)) + 1; //开始结束日期相同是一天
			
			
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("days", days);
			Guide guide = guideService.find(guideId);
			HyGroup group = hyGroupService.find(groupId);
			HyLine line = group.getLine();

			//计算导游服务费:1、服务天数 2、团队类型 3、线路类型 4、导游星级 5、服务类型：全陪or其他
			if(serviceType == 1) { //如果是其他服务，则服务费是固定的，根据天数计算总的服务费  
//				Xiane xiane = xianeService.find("elseFee");
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("eduleixing", Eduleixing.elseFee));
				List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
				if(!edu.isEmpty()) {
					CommonShenheedu xiane = edu.get(0);
					obj.put("serviceFee", xiane.getMoney().multiply(BigDecimal.valueOf(days)));
				}
							
			} else if (serviceType == 0) { //如果是全陪情况
				ArrayList<Filter> filters = new ArrayList<>();
				
				if(line.getLineType() == LineType.qiche) { //汽车游			
					filters.add(Filter.eq("groupType", group.getTeamType()));
					filters.add(Filter.eq("star", guide.getZongheLevel()));
					if(group.getTeamType() == false) { //汽车游散客
						switch(days) {
							case 1 :
								filters.add(Filter.eq("days", 1));
								break;
							case 2 :
								filters.add(Filter.eq("days", 2));
								break;
							default :
								filters.add(Filter.eq("days", 3));
								break;
						}
						List<HyServiceFeeCar> list = hyServiceFeeCarService.findList(null, filters, null);
						if(!list.isEmpty()) {
							obj.put("serviceFee", list.get(0).getPrice().multiply(BigDecimal.valueOf(days))); //汽车散客服务费
						}
					} else if (group.getTeamType() == true) { //汽车游团队
						switch(days) {
						case 1 :
							filters.add(Filter.eq("days", 1));
							break;
						case 2 :
							filters.add(Filter.eq("days", 2));
							break;
						case 3 :
							filters.add(Filter.eq("days", 3));
							break;
						case 4 :
							filters.add(Filter.eq("days", 4));
							break;
						default :
							filters.add(Filter.eq("days", 5));
							break;
						}
						List<HyServiceFeeCar> list = hyServiceFeeCarService.findList(null, filters, null);
						if(!list.isEmpty()) {
							if(days >= 5) {
								obj.put("serviceFee", list.get(0).getPrice().multiply(BigDecimal.valueOf(days))); //汽车团队五天以上服务费
							} else if (days >= 1 && days <= 4){
								obj.put("serviceFee", list.get(0).getPrice()); //汽车游团队五天以下服务费
							} else {
								throw new IllegalArgumentException();
							}
						
						}
					}
					
					
				} else if (line.getLineType() == LineType.chujing || line.getLineType() == LineType.guonei) { //国内和出境游
					filters.add(Filter.eq("lineType", line.getLineType()));
					filters.add(Filter.eq("groupType", group.getTeamType()));
					filters.add(Filter.eq("star", guide.getZongheLevel()));
					List<HyServiceFeeNoncar> list = hyServiceFeeNoncarService.findList(null, filters, null);
					if(!list.isEmpty()) {
						obj.put("serviceFee",list.get(0).getPrice().multiply(BigDecimal.valueOf(days))); //国内和出境游服务费
					}
				} else {
					throw new IllegalArgumentException("线路类型错误");
				}
			}
			
		
			j.setSuccess(true);
			j.setObj(obj);
			j.setMsg("保存成功");	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 确认指派
	 * @param guideId
	 * @param groupId
	 * @param startDate
	 * @param endDate
	 * @param tip 小费
	 * @return
	 */
	@RequestMapping(value = "querenzhipai")
	public Json querenzhipai(Long guideId, Long groupId, String start, String end, BigDecimal serviceFee,
							 Integer days, BigDecimal tip, Integer serviceType, String fuwuneirong,
							 HttpSession session) {
		Json j = new Json(); 
		try {
			SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate = simpleFormat.parse(start);
			Date toDate = simpleFormat.parse(end);
			
			List<Filter> notAssignFilters = new ArrayList<>();
			notAssignFilters.add(Filter.eq("guideId", guideId));
			notAssignFilters.add(Filter.le("status", 1));
			notAssignFilters.add(Filter.le("startDate", toDate));
			notAssignFilters.add(Filter.ge("endDate", fromDate));
			List<GuideAssignment> notAssignmentGuides = guideAssignmentService.findList(null,notAssignFilters,null);
			if(notAssignmentGuides!=null && !notAssignmentGuides.isEmpty()) {
				j.setSuccess(false);
				j.setMsg("该导游已被派遣该团");
				j.setObj(2);
				return j;
			}
			
			Guide guide = guideService.find(guideId);
			if(guide.getStatus() != 1) {
				j.setSuccess(false);
				j.setMsg("该导游状态错误");
				return j;
			}
			

			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			HyGroup group = hyGroupService.find(groupId);
			HyLine line = group.getLine();
			GuideAssignment guideAssignment = new GuideAssignment();
			guideAssignment.setGuideId(guideId);
			guideAssignment.setAssignmentType(0); //计调指派
			guideAssignment.setGroupId(groupId);
			//added by Gsbing 20180801
			HyGroup hyGroup=hyGroupService.find(groupId);
			if(hyGroup.getTeamType()==true) {
				guideAssignment.setTeamType(1);
			}
			else {
				guideAssignment.setTeamType(0);
			}			
			guideAssignment.setLineType(hyGroup.getGroupLineType());
			
			guideAssignment.setServiceType(serviceType);
			guideAssignment.setServiceFee(serviceFee);
			guideAssignment.setTotalFee(tip.add(serviceFee)); //总费用
			
			guideAssignment.setTip(tip);
			guideAssignment.setStartDate(fromDate);
			guideAssignment.setEndDate(toDate);
			guideAssignment.setLineName(line.getName());
			guideAssignment.setDays(days);
			if(serviceType == 0) {
				String s = "";
				int index = 1;
				for(HyLineTravels temp : line.getLineTravels()) {
					s = s + "第" + index + "天:" + temp.getTransport().getName() + " " + temp.getRoute() + "," + "\n";
					index ++;
				}
				guideAssignment.setTravelProfile(s);
			} else if (serviceType == 1) {
				guideAssignment.setElseService(fuwuneirong);
			}
			guideAssignment.setOperator(username);
			guideAssignment.setOperatorPhone(hyAdminService.find(username).getMobile());
			guideAssignment.setPaiqianDate(new Date());
			guideAssignment.setStatus(0);
			guideAssignmentService.save(guideAssignment);
			
			j.setSuccess(true);
			j.setMsg("指派成功，请等待导游确认");	
			
			//发布抢单成功以后微信推送消息
			TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
			params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
			params.put("keyword1", WechatTemplateMsg.item(line.getName(), "#000000"));
			params.put("keyword2", WechatTemplateMsg.item(group.getTeamType() == false ? "散客" : "团客", "#000000"));
			params.put("keyword3", WechatTemplateMsg.item(group.getStartDay() + "至" + group.getEndDay(), "#000000"));
			params.put("keyword4", WechatTemplateMsg.item(serviceType == 0 ? "全陪服务" : "其他服务", "#000000"));
			params.put("keyword5", WechatTemplateMsg.item(serviceFee + "元", "#000000"));
			params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
			
			String url=guideAssignmentSite+"id="+guideAssignment.getId();
			
			PublishWeixintuisong.tuisong(url, guideService.find(guideId).getOpenId(), params);
			

			System.out.println("推送成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			j.setObj(1);
		}
		return j;
	}
	
	/**
	 * 查看导游详情
	 * @param guideId
	 * @return
	 */
	@RequestMapping(value = "guideDetail/view")
	public Json guideDetail(Long guideId) {
		Json j = new Json(); 
		try {
			Guide guide = guideService.find(guideId);
			
			j.setSuccess(true);
			j.setMsg("查看成功");	
			j.setObj(guide);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 查看派遣详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "paiqianDetail")
	public Json paiqianDetail(Long id) {
		Json j = new Json(); 
		try {
			GuideAssignment guideAssignment = guideAssignmentService.find(id);
			
			j.setSuccess(true);
			j.setMsg("查看成功");	
			j.setObj(guideAssignment);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 取消指派
	 * @param id 指派id
	 * @return
	 */
	@RequestMapping(value = "cancelAssignment")
	public Json cancelAssginment(Long id) {
		Json j = new Json(); 
		try {
			GuideAssignment guideAssignment = guideAssignmentService.find(id);
			HyGroup group = hyGroupService.find(guideAssignment.getGroupId());
			HyLine line = group.getLine();
			if(guideAssignment.getStatus() != 1) {
				j.setSuccess(false);
				j.setMsg("状态错误");
				return j;
			}
			guideAssignment.setQuxiaoDate(new Date());
			guideAssignment.setStatus(3);
			if(guideAssignment.getAssignmentType() == 2) { //计调派单还需要减去已抢单人数
				GroupSendGuide groupSendGuide = groupSendGuideService.find(guideAssignment.getQiangdanId());
				if(groupSendGuide != null) {
					groupSendGuide.setAllReceive(groupSendGuide.getAllReceive() - 1);
					if(groupSendGuide.getIsRestrictSex()) {
						Guide guide = guideService.find(guideAssignment.getGuideId());
						if(guide.getSex() == 0) { //如果是女的减去女的
							groupSendGuide.setManReceive(groupSendGuide.getManReceive() - 1);
						} else if (guide.getSex() == 1) { //如果是男的
							groupSendGuide.setWomanReceive(groupSendGuide.getWomanReceive() - 1);
						}
					}
					
				}
				groupSendGuideService.update(groupSendGuide);
			}
			guideAssignmentService.update(guideAssignment);
					
			j.setSuccess(true);
			j.setMsg("取消派遣成功");	
			j.setObj(guideAssignment);	
			//取消派遣成功以后微信推送消息
			TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
			params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社取消了给您的派团！", "#000000"));
			params.put("keyword1", WechatTemplateMsg.item(guideAssignment.getLineName(), "#000000"));
			params.put("keyword2", WechatTemplateMsg.item(group.getTeamType() == false ? "散客" : "团客", "#000000"));
			params.put("keyword3", WechatTemplateMsg.item(group.getStartDay() + "至" + group.getEndDay(), "#000000"));
			params.put("keyword4", WechatTemplateMsg.item(guideAssignment.getServiceType() == 0 ? "全陪服务" : "其他服务", "#000000"));
			params.put("keyword5", WechatTemplateMsg.item(guideAssignment.getServiceFee() + "元", "#000000"));
			params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
			
			
			WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
			wechatTemplateMsg.setTemplate_id(Constants.Quxiao_Paiqian);
			wechatTemplateMsg.setTouser(guideService.find(guideAssignment.getGuideId()).getOpenId());
			String url=guideAssignmentSite+"id="+guideAssignment.getId();
			wechatTemplateMsg.setUrl(url);
			wechatTemplateMsg.setData(params);
			String data = JsonUtils.toJson(wechatTemplateMsg);
			TemplateMsgResult templateMsgResult = WechatUtil.storeRent(data);
			System.out.println(JsonUtils.toJson(templateMsgResult));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 团和线路的详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {

			HyGroup hyGroup = hyGroupService.find(id);
			HyLine line = hyGroup.getLine();
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("group", hyGroup); //团详情
			map.put("line", line); //线路详情
				
			j.setMsg("查看成功");
			j.setSuccess(true);
			j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 变更团的扣点
	 * @param groupBiankoudian
	 * @param groupId
	 * @return
	 */
	@RequestMapping(value="changeDeduct")
	public Json changeDeduct(GroupBiankoudian groupBiankoudian, Long gId, HttpSession session) {
		Json j = new Json();
		try {
			HashMap<String, Object> map = new HashMap<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			HyGroup group = hyGroupService.find(gId);
			
			//更改团状态
			group.setIsDisplay(false);
			
			
			HyLine line = group.getLine();
			String admin = line.getHySupplier().getOperator().getUsername();
			groupBiankoudian.setGroupId(group);
			map.put("admin", admin);
			
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("groupbiankoudianprocess");
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 
			
			groupBiankoudian.setAuditStatus(AuditStatus.auditing);
			groupBiankoudian.setProcessInstanceId(pi.getProcessInstanceId());
			groupBiankoudian.setApplyTime(new Date());	
			groupBiankoudian.setApplyName(username);
			
			
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), groupBiankoudian.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(), map);	
			hyGroupService.update(group);
			groupBiankoudianService.save(groupBiankoudian);
			
			j.setMsg("提交审核成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 根据身份证号获取年龄
	 * @param shenfenzhenghao
	 * @return
	 */
	public static int getAge(String shenfenzhenghao) {
		Calendar date = Calendar.getInstance();
		
	    int curyear = date.get(Calendar.YEAR);
	    int curMonth = date.get(Calendar.MONTH);
	    int curDay = date.get(Calendar.DAY_OF_MONTH);
	    
		int birthYear = Integer.valueOf(shenfenzhenghao.substring(6, 10));
		int birthMonth = Integer.valueOf(shenfenzhenghao.substring(10, 12));
		int birthDay = Integer.valueOf(shenfenzhenghao.substring(12, 14));
		
		int age = curyear - birthYear;
		if(curMonth <= birthMonth) {
			if(curMonth == birthMonth) {
				if(curDay < birthDay) age --;
			} else {
				age --;
			}
		}
		
		return age;
	}
	
	/**
	 * 变更库存
	 * @author LBC
	 * @param groupId
	 * @param stock
	 * @return
	 */
	@RequestMapping(value="/changeInventory")
	@ResponseBody
	public Json restore(Long groupId,Integer stock) {
		Json j = new Json();
		try{
			HyGroup hyGroup = hyGroupService.find(groupId);
			hyGroup.setStock(stock);;
			hyGroupService.update(hyGroup);
			j.setSuccess(true);
			j.setMsg("变更库存成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/** 无报名消团 */
	@RequestMapping(value="cancelGroup/nonePerson")
	@ResponseBody
	public Json cancelGroupNone(Long groupId)
	{
		Json json=new Json();
		try{
			HyGroup hyGroup=hyGroupService.find(groupId);
			HyLine hyLine=hyGroup.getLine();
			BigDecimal lineLowestPrice=hyLine.getLowestPrice();
			hyGroup.setGroupState(GroupStateEnum.yiquxiao);
			hyGroup.setIsCancel(true);
			hyGroupService.update(hyGroup);			
			//如果正在消的团正好是最低价格,重新设置线路最低价格
			if(hyGroup.getLowestPrice().compareTo(lineLowestPrice)<=0) {
				BigDecimal lowestPrice=hyLineService.getLineLowestPrice(hyLine);
				hyLine.setLowestPrice(lowestPrice);
				hyLineService.update(hyLine);
			}
			
					
			json.setSuccess(true);
			json.setMsg("消团成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	/*新建一个包装类，传递消团退款参数*/
	static class WrapHyGroupPrice{
		private List<HyGroupPrice> hyGroupPrices;
		private List<HyGroupSpecialprice> hyGroupSpecialprices;
		private List<HyGroupOtherprice> hyGroupOtherprices;
		public List<HyGroupPrice> getHyGroupPrices() {
			return hyGroupPrices;
		}
		public void setHyGroupPrices(List<HyGroupPrice> hyGroupPrices) {
			this.hyGroupPrices = hyGroupPrices;
		}
		public List<HyGroupSpecialprice> getHyGroupSpecialprices() {
			return hyGroupSpecialprices;
		}
		public void setHyGroupSpecialprices(List<HyGroupSpecialprice> hyGroupSpecialprices) {
			this.hyGroupSpecialprices = hyGroupSpecialprices;
		}
		public List<HyGroupOtherprice> getHyGroupOtherprices() {
			return hyGroupOtherprices;
		}
		public void setHyGroupOtherprices(List<HyGroupOtherprice> hyGroupOtherprices) {
			this.hyGroupOtherprices = hyGroupOtherprices;
		}
		
	}
	
	/**
	 * 取消有人报名的团
	 * @param wrapHyGroupPrice
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(value="cancelGroup/person")
	@ResponseBody
	public Json cancelGroupPerson(@RequestBody WrapHyGroupPrice wrapHyGroupPrice,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HashMap<String, Object> hm = new HashMap<>();
			Long groupId = null;
			HyGroup group = null;
			List<Filter> filters = new ArrayList<>();
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin=hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("xiaotuanProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			List<HyGroupPrice> groupPriceList=wrapHyGroupPrice.getHyGroupPrices();
			if(groupPriceList.size()>0){
				for(HyGroupPrice groupPrice:groupPriceList){
					Long groupPriceId=groupPrice.getId();
					HyGroupPrice hyGroupPrice=hyGroupPriceService.find(groupPriceId);
					hyGroupPrice.setAdultPrice4(groupPrice.getAdultPrice4());
					hyGroupPrice.setChildrenPrice4(groupPrice.getChildrenPrice4());
					hyGroupPrice.setOldPrice4(groupPrice.getOldPrice4());
					hyGroupPrice.setStudentPrice4(groupPrice.getStudentPrice4());
					hyGroupPriceService.update(hyGroupPrice);
				}
				HyGroupPrice hyGroupPrice=hyGroupPriceService.find(groupPriceList.get(0).getId());
				HyGroup hyGroup=hyGroupPrice.getHyGroup();
				group = hyGroup;
				groupId = hyGroup.getId();
				hyGroup.setGroupState(GroupStateEnum.xiaotuanzhong);
				hyGroup.setIsDisplay(false);
				hyGroupService.update(hyGroup);				
			}
			List<HyGroupSpecialprice> groupSpecialpriceList=wrapHyGroupPrice.getHyGroupSpecialprices();
			if(groupSpecialpriceList.size()>0){
				for(HyGroupSpecialprice specialPrice:groupSpecialpriceList){
					Long specialPriceId=specialPrice.getId();
					HyGroupSpecialprice hyGroupSpecialprice=hyGroupSpecialpriceService.find(specialPriceId);
					hyGroupSpecialprice.setSpecialPrice4(specialPrice.getSpecialPrice4());
					hyGroupSpecialpriceService.update(hyGroupSpecialprice);
				}
			}
			List<HyGroupOtherprice> groupOtherpriceList=wrapHyGroupPrice.getHyGroupOtherprices();
			if(groupOtherpriceList.size()>0){
				for(HyGroupOtherprice otherPrice:groupOtherpriceList){
					Long otherPriceId=otherPrice.getId();
					HyGroupOtherprice hyGroupOtherprice=hyGroupOtherpriceService.find(otherPriceId);
					hyGroupOtherprice.setDanfangchaPrice4(otherPrice.getDanfangchaPrice4());
					hyGroupOtherprice.setBuchuangweiPrice4(otherPrice.getBuchuangweiPrice4());
					hyGroupOtherprice.setBumenpiaoPrice4(otherPrice.getBumenpiaoPrice4());
					hyGroupOtherprice.setBuwopuPrice4(otherPrice.getBuwopuPrice4());
					hyGroupOtherprice.setErtongzhanchaungPrice4(otherPrice.getErtongzhanchaungPrice4());
					hyGroupOtherpriceService.update(hyGroupOtherprice);
				}			
			}				
			
			
			//改变订单的状态，计算退款金额
			//待支付的改为已取消  待确认的改为驳回
//			filters.clear();
//			filters.add(Filter.eq("groupId", groupId));
//			List<HyOrder> orderAll = hyOrderService.findList(null, filters, null); //所有订单数量
//
//			filters.clear();
//			filters.add(Filter.eq("status", 0)); //待支付的订单变为已取消
//			filters.add(Filter.eq("groupId", groupId));
//			List<HyOrder> orders = hyOrderService.findList(null, filters, null);
			List<String> temps = new ArrayList<>();
//			for(HyOrder temp : orders) {
//				//消团发送短信
//				HyAdmin operator = temp.getOperator();
//				if(null != operator && null != operator.getMobile()) {
//					SendMessageEMY.sendMessage(operator.getMobile(), "", 10);
//				}
//				if(null != operator) {
//					temps.add(operator.getUsername());
//				}
//
//				temp.setStatus(6);
//				hyOrderService.update(temp);
//			}

			
			
			
//			filters.clear();
//			filters.add(Filter.eq("status", 2)); //待供应商确认的订单变为已驳回，走已驳回的流程
//			filters.add(Filter.eq("groupId", groupId));
//			List<HyOrder> orders1 = hyOrderService.findList(null, filters, null);
//			for(HyOrder temp : orders1) {
//				HyAdmin operator = temp.getOperator();
//				if(null != operator && null != operator.getMobile()) {
//					SendMessageEMY.sendMessage(operator.getMobile(), "", 10);
//				}
//
//				temp.setStatus(5);
//				hyOrderService.update(temp);
//				supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(temp.getId(), "供应商消团", httpSession);
//			}


			
			filters.clear();
			filters.add(Filter.ne("refundstatus", 1)); 
			filters.add(Filter.ne("refundstatus", 2)); 
//			filters.add(Filter.ne("refundstatus", 4));
//			filters.add(Filter.eq("checkstatus", 1));
			filters.add(Filter.eq("paystatus",1));
//			filters.add(Filter.eq("status", 3));
			filters.add(Filter.ne("status", 0));
			filters.add(Filter.ne("status", 6));
			filters.add(Filter.eq("groupId", groupId));
			List<HyOrder> hyOrders = hyOrderService.findList(null, filters, null);
//			int orderNumber = hyOrders.size(); //订单的数量
			BigDecimal zongjine = BigDecimal.ZERO; //订单的总金额
			
			//将订单的数量写入数据库 --- 用于门店确认消团的时候判断是否所有门店都已经同意
			filters.clear();
			filters.add(Filter.eq("groupId", groupId));
			List<GroupXiaotuan> xiaotuans = groupXiaotuanService.findList(null, filters, null);
			for (GroupXiaotuan temp : xiaotuans) {
				groupXiaotuanService.delete(temp);
			} //因为消团以后还会重新发起消团申请，所以需要先删除掉原来的
			
			GroupXiaotuan groupXiaotuan = new GroupXiaotuan();
			groupXiaotuan.setGroupId(groupId);
			
			Set<Department> storesNo = new HashSet<>(); //所有报名的e门店					
			for (HyOrder temp : hyOrders) { 		
				if (temp.getStoreId() != null) {
					Store store = storeService.find(temp.getStoreId());
					if(store.getDepartment() != null) {
						storesNo.add(store.getDepartment()); //将部门作为流程变量
					}
				}
			}
			
			groupXiaotuan.setNumber(storesNo.size());
			groupXiaotuanService.save(groupXiaotuan);
			
			//add 20190517 by gxz 如果不需要门店审核 直接消团成功
			if(storesNo.size() == 0) {
				group.setIsCancel(true);
				group.setGroupState(GroupStateEnum.yiquxiao);
				hyGroupService.update(group);
				
				/**
				 * 更新线路的最低价格,added by GSbing,20181016 
				 */
				HyLine hyLine=group.getLine();
				BigDecimal lineLowestPrice=hyLine.getLowestPrice();
				//如果正在消的团正好是最低价格,重新设置线路最低价格
				if(group.getLowestPrice().compareTo(lineLowestPrice)<=0) {
					BigDecimal lowestPrice=hyLineService.getLineLowestPrice(hyLine);
					hyLine.setLowestPrice(lowestPrice);
					hyLineService.update(hyLine);
				}
				json.setSuccess(true);
				json.setMsg("该团没有有效订单，消团成功！");
				return json;
			}
			
			
	
			
			//end of add	
			for (HyOrder temp : hyOrders) { 
				HyAdmin operator = temp.getOperator();
				if(null != operator && null != operator.getMobile()) {
					SendMessageEMY.sendMessage(operator.getMobile(), "", 10);
				}
				if(null != operator) {
					temps.add(operator.getUsername());
				}
//				temp.setRefundstatus(1); //将订单状态设置为退款中
//				hyOrderService.update(temp); //注：消团申请成功才改变订单状态
				zongjine = zongjine.add(temp.getJiesuanMoney1()); //计算订单总金额
				

								 
				//在订单退款OrderApplication中增加记录
				BigDecimal jiesuanMoney = BigDecimal.ZERO;
				BigDecimal waimaiMoney = BigDecimal.ZERO;
				BigDecimal baoxianJiesuanMoney = BigDecimal.ZERO;
				BigDecimal baoxianWaimaiMoney = BigDecimal.ZERO;
				
				HyOrderApplication orderApplication = new HyOrderApplication();
				orderApplication.setType(2); //设置为供应商消团类型
				orderApplication.setCancleGroupId(groupId);
				orderApplication.setStatus(0); //缺一个状态，待门店确认
				orderApplication.setContent("供应商消团");
				orderApplication.setOrderId(temp.getId());
				orderApplication.setOperator(admin);
				List<HyOrderApplicationItem> hyOrderApplicationItems = new ArrayList<>();
			
				
				List<HyOrderItem> items = temp.getOrderItems(); //获取该订单的所有订单项
				
				for(HyOrderItem item : items) {
					if(item.getNumber() != null && item.getNumberOfReturn() != null && 
					   item.getNumber() > item.getNumberOfReturn()) { //如果该条目没有全退 才进行下一步
						HyOrderApplicationItem hyOrderApplicationItem = new HyOrderApplicationItem();
						hyOrderApplicationItem.setItemId(item.getId());
						hyOrderApplicationItem.setReturnQuantity(item.getNumber() - item.getNumberOfReturn());
						hyOrderApplicationItem.setHyOrderApplication(orderApplication);						
						
						//设置订单退款项的结算退还价和外卖退还价
						if(item.getType() == 1 && item.getPriceType() != 4) { //线路的普通价格
							Long priceId = item.getPriceId();
							if(priceId != null) {
								HyGroupPrice price = hyGroupPriceService.find(priceId);
								switch(item.getPriceType()) 
								{
								case 0: //普通成人价退还价
									hyOrderApplicationItem.setJiesuanRefund(price.getAdultPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getAdultPrice4());
									break;
								case 1:
									hyOrderApplicationItem.setJiesuanRefund(price.getChildrenPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getChildrenPrice4());
									break;
								case 2:
									hyOrderApplicationItem.setJiesuanRefund(price.getStudentPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getStudentPrice4());
									break;
								case 3:
									hyOrderApplicationItem.setJiesuanRefund(price.getOldPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getOldPrice4());
									break;
								default : throw new RuntimeException("传入的参数类型错误");										
								}
							}
						} else if (item.getType() == 1 && item.getPriceType() == 4) {
							Long priceId = item.getPriceId();
							if(priceId != null) {
								HyGroupSpecialprice price = hyGroupSpecialpriceService.find(priceId);
								hyOrderApplicationItem.setJiesuanRefund(price.getSpecialPrice4());
								hyOrderApplicationItem.setWaimaiRefund(price.getSpecialPrice4());
							}
						} else if (item.getType() == 8) {
							Long priceId = item.getPriceId();
							if(priceId != null) {
								HyGroupOtherprice price = hyGroupOtherpriceService.find(priceId);
								switch(item.getPriceType())
								{
								case 0:
									hyOrderApplicationItem.setJiesuanRefund(price.getDanfangchaPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getDanfangchaPrice4());
									break;
								case 1:
									hyOrderApplicationItem.setJiesuanRefund(price.getBuwopuPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getBuwopuPrice4());
									break;
								case 2:
									hyOrderApplicationItem.setJiesuanRefund(price.getBumenpiaoPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getBumenpiaoPrice4());
									break;
								case 3:
									hyOrderApplicationItem.setJiesuanRefund(price.getErtongzhanchaungPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getErtongzhanchaungPrice4());
									break;
								case 4:
									hyOrderApplicationItem.setJiesuanRefund(price.getBuchuangweiPrice4());
									hyOrderApplicationItem.setWaimaiRefund(price.getBuchuangweiPrice4());
									break;
								default : throw new RuntimeException("传入的参数类型错误");	
								}
							}
						}
						
						//计算保险的退还价和外卖价
						List<HyOrderCustomer> hyOrderCustomers = item.getHyOrderCustomers();
						BigDecimal baoxianJiesuanRefund = BigDecimal.ZERO;
						BigDecimal baoxianWaimaiRefund = BigDecimal.ZERO;
						for(HyOrderCustomer customer : hyOrderCustomers) {
							if(customer.getSettlementPrice() != null) {
								baoxianJiesuanRefund = baoxianJiesuanRefund.add(customer.getSettlementPrice());
							}
							if(customer.getSalePrice() != null) {
								baoxianWaimaiRefund = baoxianWaimaiRefund.add(customer.getSalePrice());
							}
						}
						
						hyOrderApplicationItem.setBaoxianJiesuanRefund(baoxianJiesuanRefund);
						hyOrderApplicationItem.setBaoxianWaimaiRefund(baoxianWaimaiRefund); //设置保险结算和外卖退还价
						
						if(hyOrderApplicationItem.getJiesuanRefund()!=null) {
							jiesuanMoney = jiesuanMoney.add(hyOrderApplicationItem.getJiesuanRefund());
						}
						if(hyOrderApplicationItem.getWaimaiRefund()!=null) {
							waimaiMoney = waimaiMoney.add(hyOrderApplicationItem.getWaimaiRefund());
						}						
						if(hyOrderApplicationItem.getBaoxianJiesuanRefund()!=null) {
							baoxianJiesuanMoney = baoxianJiesuanMoney.add(hyOrderApplicationItem.getBaoxianJiesuanRefund());
						}
						if(hyOrderApplicationItem.getBaoxianWaimaiRefund()!=null) {
							baoxianWaimaiMoney = baoxianWaimaiMoney.add(hyOrderApplicationItem.getBaoxianWaimaiRefund()); //计算订单退款的四种价格
						}					
						
						hyOrderApplicationItems.add(hyOrderApplicationItem);
					}									
				}
				orderApplication.setHyOrderApplicationItems(hyOrderApplicationItems);
				orderApplication.setJiesuanMoney(jiesuanMoney); //修改，修复了消团的退换钱不对问题
				orderApplication.setWaimaiMoney(waimaiMoney);
				orderApplication.setBaoxianJiesuanMoney(baoxianJiesuanMoney);
				orderApplication.setBaoxianWaimaiMoney(baoxianWaimaiMoney); //新增设置外卖 结算的退还价
				hyOrderApplicationService.save(orderApplication);				
			}
			//消团推送微信
			/**发送微信提醒**/
			String message = "您有新的消团!";
			SendMessageQyWx.sendWxMessage(QyWxConstants.HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID, temps, null, message);
			
			HyGroupCancelAudit hyGroupCancelAudit=new HyGroupCancelAudit();
			hyGroupCancelAudit.setApplyName(admin);
			hyGroupCancelAudit.setHyGroup(group);
			hyGroupCancelAudit.setApplyTime(new Date());
			hyGroupCancelAudit.setAuditStatus(AuditStatus.auditing);
			hyGroupCancelAudit.setProcessInstanceId(pi.getProcessInstanceId());
			hyGroupCancelAudit.setMoney(zongjine);
			hyGroupCancelAuditService.save(hyGroupCancelAudit);
				
			// 完成消团退款提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
//			hm.put("groupId", groupId);
			hm.put("storesNo", storesNo);
			taskService.complete(task.getId(), hm);	
			json.setSuccess(true);
			json.setMsg("消团申请提交成功！");
		}	
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="groupSale")
	public Json groupSale(@RequestBody Wrap wrap, HttpSession session) {
		Json j = new Json();//接收的都是前端传来的json数据
		try {
			List<Filter> filters = new ArrayList<>();
			List<Order> orders = new ArrayList<>();
			//这个是后来加的，为了根据ID取到HyGroup的数据
			HyGroup hyGroup = hyGroupService.find(wrap.getGroupId());
			//三个List 取到json封装的数据
			List<HyGroupPriceSwd> hyGroupPriceSwds = wrap.getHyGroupPriceSwds();
			List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds = wrap.getHyGroupOtherpriceSwds();
			List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds = wrap.getHyGroupSpecialPriceSwds();
			
		
			
			//在这之前画了一个流程图：diagram里面的SwdProcess，修改了整体的ID为swdprocess
			//里面画了两个task，都修改了描述 task里面选择usertask表示数据
			//下面是用代码运用画的流程图
			
			//开启流程实例 复制过来的 改名为swdprocess
			//session保存了用户的账号
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//通过key开启了流程实例
			//甩尾单的 所有数据（上面三个entity）是要走这个swdprocess流程的 要审核entity的数据
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("swdprocess");
			// 根据流程实例Id查询任务，查到的就是流程图里的第一个任务要干啥：采购部提交甩尾单申请
			// 找到当前运行的task
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			//审核数据库表的创建 由于是纯后台 不接收前端的类 所以直接用new
			//注意实体类的构造器可能要删除下
			HyGroupShenheSwd swd = new HyGroupShenheSwd();
			//把shenhe这个实体类里面的值依次赋上 上面带出来的hyGroup在这里用
			swd.setHygroup(hyGroup);
			swd.setApplyName(username);
			//申请时间是当前时间
			swd.setApplyTime(new Date());
			//这个是“正在审核中”
//			swd.setAuditStatus(AuditStatus.auditing);
			//得到流程实例的ID 方法是封装好的 保存到审核的数据库中
			swd.setProcessInstanceId(pi.getProcessInstanceId());
					
			//设置甩尾单次数 ----------------------gxz add on 2018.6.22
			filters.add(Filter.eq("hygroup", hyGroup));
			orders.add(Order.asc("id"));
			List<HyGroupShenheSwd> swds = hyGroupShenheSwdService.findList(null, filters, orders);
			int times = 0; //甩尾单次数
			HyGroupShenheSwd last = null; //上一次甩尾单的实体，默认为null
			for (HyGroupShenheSwd temp : swds) {
				times = temp.getTimes();
				if(temp.getAuditStatus() == AuditStatus.pass) {
					last = temp;
				} //寻找上一次审核的次数和最近一次通过的甩尾单实体
			}
			swd.setTimes(++times); //将审核次数加一
			swd.setHyGroupShenheSwd(last); //设置最近一次甩尾单
						
			
			//end of add
			
			// 这之间写了一个监听器类（Listener这个包里面的） 即SwdListener，这个甩尾单由谁来审核
			// 用DeployListener把监听器和流程图连接在一起  Listener来决定谁来审核
			// 提交的时候不用Listener 审核的时候才用这个 
			
			// 完成任务，根据监听器设置下一步审核人 复制的
			//gxz add 2018.06.22
			//符合价格规律就不需要审核的逻辑
			HashMap<String, Object> hm = new HashMap<>(); //流程变量，用于控制流程流转
			
			//如果符合价格规律就不用审核
			filters.clear();//这个反复用filter
			if(hyGroup != null && hyGroup.getLine() != null) {
				LineType lineType = hyGroup.getLine().getLineType();//拿到线路类型
				if(lineType == LineType.guonei) {
					//filters是一个数组 有很多条件
					filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
				} else if (lineType == LineType.chujing) {
					filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
				} else if (lineType == LineType.qiche) {
					filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
				} //找到三种线路类型的价格比例
				
				//保存了价格比例
				List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
				BigDecimal money = edu.get(0).getMoney(); //money为价格比例的值
				Boolean flag = checkPrice(hyGroupPriceSwds,hyGroupOtherpriceSwds, hyGroupSpecialPriceSwds, money); //计算是否符合价格规律
				
				if (flag) {
					hm.put("result", "fuhe");
					swd.setAuditStatus(AuditStatus.pass); //如果满足价格规律不需要审核，直接通过
					
					//审核通过以后甩尾单的价格需要覆盖原价格
					for(HyGroupPriceSwd temp : hyGroupPriceSwds) {
						HyGroupPrice price = temp.getHyGroupPriceID();
						//temp是前端传过来的  price是通过前端传过来的ID 找到原价格的实体类
						//用前端传过来的数据覆盖price
						price.setHyGroup(hyGroup);
						price.setStartplace(temp.getStartplace());
						price.setAdultPrice(temp.getAdultPrice());
						price.setAdultPrice1(temp.getAdultPrice1());
						price.setAdultPrice4(temp.getAdultPrice4());
						price.setAdultPrice6(temp.getAdultPrice6());
						
						price.setChildrenPrice(temp.getChildrenPrice());
						price.setChildrenPrice1(temp.getChildrenPrice1());
						price.setChildrenPrice4(temp.getChildrenPrice4());
						price.setChildrenPrice6(temp.getChildrenPrice6());
						
						price.setStudentPrice(temp.getStudentPrice());
						price.setStudentPrice1(temp.getStudentPrice1());
						price.setStudentPrice4(temp.getStudentPrice4());
						price.setStudentPrice6(temp.getStudentPrice6());
						
						price.setOldPrice(temp.getOldPrice());
						price.setOldPrice1(temp.getOldPrice1());
						price.setOldPrice4(temp.getOldPrice4());
						price.setOldPrice6(temp.getOldPrice6());
						hyGroupPriceService.update(price);
					}
					
					for(HyGroupOtherpriceSwd temp : hyGroupOtherpriceSwds) {
						HyGroupOtherprice price = temp.getHyGroupOtherprice();
						
						price.setHyGroup(hyGroup);
						price.setDanfangchaPrice(temp.getDanfangchaPrice());
						price.setDanfangchaPrice1(temp.getDanfangchaPrice1());
						price.setDanfangchaPrice4(temp.getDanfangchaPrice4());
						price.setDanfangchaPrice6(temp.getDanfangchaPrice6());
						
						price.setBuchuangweiPrice(temp.getBuchuangweiPrice());
						price.setBuchuangweiPrice1(temp.getBuchuangweiPrice1());
						price.setBuchuangweiPrice4(temp.getBuchuangweiPrice4());
						price.setBuchuangweiPrice6(temp.getBuchuangweiPrice6());
						
						price.setErtongzhanchaungPrice(temp.getErtongzhanchaungPrice());
						price.setErtongzhanchaungPrice1(temp.getErtongzhanchaungPrice1());
						price.setErtongzhanchaungPrice4(temp.getErtongzhanchaungPrice4());
						price.setErtongzhanchaungPrice6(temp.getErtongzhanchaungPrice6());
						
						price.setBumenpiaoPrice(temp.getBumenpiaoPrice());
						price.setBumenpiaoPrice1(temp.getBumenpiaoPrice1());
						price.setBumenpiaoPrice4(temp.getBumenpiaoPrice4());
						price.setBumenpiaoPrice6(temp.getBumenpiaoPrice6());
						
						price.setBuwopuPrice(temp.getBuwopuPrice());
						price.setBuwopuPrice1(temp.getBuwopuPrice1());
						price.setBuwopuPrice4(temp.getBuwopuPrice4());
						price.setBuwopuPrice6(temp.getBuwopuPrice6());
						
						hyGroupOtherpriceService.update(price);
					}
					
					for(HyGroupSpecialPriceSwd temp : hyGroupSpecialPriceSwds) {
						HyGroupSpecialprice price = temp.getHyGroupSpecialprice();
						
						price.setHyGroup(hyGroup);
						price.setSpecialPrice(temp.getSpecialPrice());
						price.setSpecialPrice1(temp.getSpecialPrice1());
						price.setSpecialPrice4(temp.getSpecialPrice4());
						
						hyGroupSpecialpriceService.update(price);					
					}				
					
				//符合价值规律，直接拿过去打标签	
				//甩尾单通过了之后，需要把数据存到hy_specialty_linelable
				//hySpecialtyLineLabelService
				HySpecialtyLineLabel hySpecialtyLineLabel = new HySpecialtyLineLabel();
				hySpecialtyLineLabel.setCreateTime(swd.getApplyTime());//甩尾单的审核时间，还是new一个
				//查询前端建的尾单
				List<Filter> labelFilters = new ArrayList<>();
				labelFilters.add(Filter.eq("productName", "尾单"));//用尾单去筛选
				List<HyLineLabel> hyLineLabels = hyLineLabelService.findList(null, labelFilters, null);
				if(hyLineLabels != null && hyLineLabels.size() > 0)				
					hySpecialtyLineLabel.setHyLabel(hyLineLabels.get(0));//标签表从哪里来的？用前端新建的，查出来
				hySpecialtyLineLabel.setHyLine(hyGroup.getLine());
				hySpecialtyLineLabel.setIsMarked(true);//甩尾单完了是否有效啊 这里是有效
				hySpecialtyLineLabel.setOperator(username);//没问题
				hySpecialtyLineLabelService.save(hySpecialtyLineLabel);	
					
					
				} else {
					hm.put("result", "bufuhe");
					swd.setAuditStatus(AuditStatus.auditing);
				}
			}
			//end of gxz add
		
			hyGroupShenheSwdService.save(swd);
			
			//遍历第一个，存到数据库，专注于外键，普通的自动存
			for(HyGroupPriceSwd temp : hyGroupPriceSwds) {

					//根据ID找外键实体类
					//HyGroup group = hyGroupService.find(temp.getHygroup().getId());
					//只要是外键，就一定要从id到数据库中通过service找  
					//HyGroup group = hyGroupService.find(groupId);

					//设置到实体类中
					temp.setHygroup(hyGroup);					

				//另外一个外键，一样的意思
				if(temp.getHyGroupPriceID().getId() != null) {
					//我这里不会：前端传来一个属性（hyGroupPriceID），根据属性找到想要的实体（HyGroupPrice，实体包含很多属性）
					HyGroupPrice hyGroupPrice = hyGroupPriceService.find(temp.getHyGroupPriceID().getId()); //通过原价格的id找到原价格
					temp.setHyGroupPriceID(hyGroupPrice); //这里通过前端传过来原价格的id设置了原价格的实体再获取就获取到的价格实体了
				}
				temp.setShenheSwd(swd);
				hyGroupPriceSwdService.save(temp);
			}
			
			//我写的 另一个实体类里面的外键 
			for(HyGroupOtherpriceSwd temp : hyGroupOtherpriceSwds) {
			
					temp.setHygroup(hyGroup);

				if(temp.getHyGroupOtherprice().getId() != null) {
					//find用的都不是自己的service，保存都用的是自己的service
					HyGroupOtherprice hyGroupOtherprice = hyGroupOtherpriceService.find(temp.getHyGroupOtherprice().getId());//通过原价格的id找到原价格
					temp.setHyGroupOtherprice(hyGroupOtherprice);
				}
				temp.setShenheSwd(swd);
				hyGroupOtherpriceSwdService.save(temp);
			}
			
			//我写的 第三个
			for(HyGroupSpecialPriceSwd temp : hyGroupSpecialPriceSwds) {
				//不是空才接收
					//根据ID找外键实体类
					// 只要是外键，就一定要从id到数据库中通过service找    HyGroup group = hyGroupService.find(groupId);
					//设置到实体类中 纯后台的前端不会传过来 想办法拿出来
					//不能new 如果new就是一个空表
					temp.setHygroup(hyGroup);

				//另外一个外键，一样的意思
				if(temp.getHyGroupSpecialprice().getId() != null) {
					HyGroupSpecialprice hyGroupSpecialprice = hyGroupSpecialpriceService.find(temp.getHyGroupSpecialprice().getId());//通过原价格的id找到原价格
					temp.setHyGroupSpecialprice(hyGroupSpecialprice);
				}
				temp.setShenheSwd(swd); //设置一个外键，为了以后审核详情页查找使用
				hyGroupSpecialPriceSwdService.save(temp);
			}
			
			
			
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(), hm);	//gxz修改
					
			//这个是打上去的 这个接口是提交审核 还没有被审核
			j.setMsg("提交审核成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
	/**
	 * 检查团是否符合价格规律
	 * @param hyGroup
	 * @return
	 */
	private boolean checkPrice(List<HyGroupPriceSwd> hyGroupPriceSwds, 
							   List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds,
							   List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds, BigDecimal percent) {
	
			
			for(HyGroupPriceSwd price : hyGroupPriceSwds) {
				if(price.getAdultPrice1() != null && price.getAdultPrice() != null && price.getAdultPrice1().compareTo(price.getAdultPrice().multiply(percent)) > 0) {
					return false;
				}
				
				if(price.getChildrenPrice1() != null && price.getChildrenPrice() != null && price.getChildrenPrice1().compareTo(price.getChildrenPrice().multiply(percent)) > 0) {
					return false;
				}
				if(price.getOldPrice1() != null && price.getOldPrice() != null && price.getOldPrice1().compareTo(price.getOldPrice().multiply(percent)) > 0) {
					return false;
				}
				if(price.getStudentPrice1() != null && price.getStudentPrice() != null && price.getStudentPrice1().compareTo(price.getStudentPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			
			for(HyGroupSpecialPriceSwd specialprice : hyGroupSpecialPriceSwds) {
				if(specialprice.getSpecialPrice1() != null && specialprice.getSpecialPrice() != null && specialprice.getSpecialPrice1().compareTo(specialprice.getSpecialPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			
			
			for(HyGroupOtherpriceSwd otherprice : hyGroupOtherpriceSwds) {
				if(otherprice.getBuchuangweiPrice1() != null && otherprice.getBuchuangweiPrice() != null && otherprice.getBuchuangweiPrice1().compareTo(otherprice.getBuchuangweiPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getBuwopuPrice1() != null && otherprice.getBuwopuPrice() != null && otherprice.getBuwopuPrice1().compareTo(otherprice.getBuwopuPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getDanfangchaPrice1() != null && otherprice.getDanfangchaPrice() != null && otherprice.getDanfangchaPrice1().compareTo(otherprice.getDanfangchaPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getBumenpiaoPrice1() != null && otherprice.getBumenpiaoPrice() != null && otherprice.getBumenpiaoPrice1().compareTo(otherprice.getBumenpiaoPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getErtongzhanchaungPrice1() != null && otherprice.getErtongzhanchaungPrice() != null && otherprice.getErtongzhanchaungPrice1().compareTo(otherprice.getErtongzhanchaungPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			return true;
		
	}
	
	
	
}
