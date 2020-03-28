package com.hongyu.controller.cwz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupOtherprice;
import com.hongyu.entity.HyGroupOtherpriceSwd;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyGroupPriceSwd;
import com.hongyu.entity.HyGroupShenheSwd;
import com.hongyu.entity.HyGroupSpecialPriceSwd;
import com.hongyu.entity.HyGroupSpecialprice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HySpecialtyLineLabel;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.RegulateGuide;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupOtherpriceService;
import com.hongyu.service.HyGroupOtherpriceSwdService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupPriceSwdService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyGroupShenheSwdService;
import com.hongyu.service.HyGroupSpecialPriceSwdService;
import com.hongyu.service.HyGroupSpecialpriceService;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySpecialtyLineLabelService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.RegulateGuideService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.service.impl.HyGroupShenheSwdServiceImpl;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 甩尾单审核Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/swd/shenhe/")
public class SwdAudit {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyGroupShenheSwdServiceImpl") //注入甩尾单审核表的service
	private HyGroupShenheSwdService hyGroupShenheSwdService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;	
	
	@Resource(name = "hyGroupPriceSwdServiceImpl")
	HyGroupPriceSwdService hyGroupPriceSwdService;
	
	@Resource(name = "hyGroupOtherpriceSwdServiceImpl")
	HyGroupOtherpriceSwdService hyGroupOtherpriceSwdService;
	
	@Resource(name = "hyGroupSpecialPriceSwdServiceImpl")
	HyGroupSpecialPriceSwdService hyGroupSpecialPriceSwdService;

	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	@Resource(name = "hyGroupOtherpriceServiceImpl")
	HyGroupOtherpriceService hyGroupOtherpriceService;
	
	@Resource(name = "hyGroupSpecialpriceServiceImpl")
	HyGroupSpecialpriceService hyGroupSpecialpriceService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hySpecialtyLineLabelServiceImpl")
	HySpecialtyLineLabelService hySpecialtyLineLabelService;
	
	@Resource(name = "hyLineLabelServiceImpl")
	HyLineLabelService hyLineLabelService;
	
	/**
	 * 审核列表页-待加入筛选条件部分
	 * @param pageable 分页信息
	 * @param shenheStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, String shenheStatus, HyGroupShenheSwd hyGroupShenheSwd, HttpSession session) {
	Json j = new Json();	
		
		try {	
			//得到登录用户
			String username = (String) session.getAttribute(CommonAttributes.Principal); //根据session得到当前登录用户
			List<Filter> filters = FilterUtil.getInstance().getFilter(hyGroupShenheSwd); //修改此处
			List<HyGroupShenheSwd> swds = hyGroupShenheSwdService.findList(null, filters, null); //修改
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (shenheStatus == null) { //如果前端不传shenheStatus这个参数，默认是null，找出所有的任务(包括已审核的 + 待审核的)
				//根据username找到这个人的待审核数据 username决定权限
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list(); //得到当前待审核任务
				for (Task task : tasks) { //遍历待审核任务，将其返回给前端
					String processInstanceId = task.getProcessInstanceId();
					for (HyGroupShenheSwd tmp : swds) { //修改，待审核的数据获取
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据	这个函数用来存放列表页的数据					
						}
					}
				}
				//找这个人的已审核数据 根据username
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list(); //得到已完成任务
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) { //遍历已完成任务将其返回给前端
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyGroupShenheSwd tmp : swds) { //修改，已审核的数据获取
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
							List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, ans, strs[1]);
						}
					}
				}
			} else if (shenheStatus.equals("daishenhe")) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyGroupShenheSwd tmp : swds) { //修改待审核
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							helpler(tmp, ans, "daishenhe");//待审核数据		
						}
					}
				}

			} else if (shenheStatus.equals("yishenhe")) {// 搜索已审核任务
				
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyGroupShenheSwd tmp : swds) { //修改已审核
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							
						    List<Comment> comment = taskService.getProcessInstanceComments(processInstanceId);
							
							String str = "";
							for(Comment c : comment){
								if(username.equals(c.getUserId()))
										{
									str = c.getFullMessage();
									break;
										}
									
							}
							
							String[] strs = str.split(":");
							if(strs[1] == null) {
								throw new RuntimeException("状态错误");
							}
							helpler(tmp, ans, strs[1]);
						}
					}

				}
			} 
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(answer);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 甩尾单申请的ID
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		
		try {
				List<Filter> filters = new ArrayList<>();
				
				HyGroupShenheSwd swd = hyGroupShenheSwdService.find(id); //通过甩尾单审核的Id 找到了审核的实体		
				
				HyGroupShenheSwd lastSwd = swd.getHyGroupShenheSwd(); //从这里得到最近一次甩尾单成功的实体

				HyGroup group = swd.getHygroup();
				
				HyLine line = group.getLine();
				
				HashMap<String, Object> map = new HashMap<>(); //结果 修改
				
				
				/** 审核详情 */
				List<HashMap<String, Object>> shenheMap = new ArrayList<>();
				
				map.put("group", group); //团的信息
				map.put("line", line); //线路的信息							
				
				//这是甩尾单价格
				filters.clear();
				filters.add(Filter.eq("hygroup", group));
				filters.add(Filter.eq("shenheSwd", swd)); //找到本次甩尾单的三类价格
				List<HyGroupOtherpriceSwd> a1 = hyGroupOtherpriceSwdService.findList(null, filters, null);
				List<HyGroupPriceSwd> a2 = hyGroupPriceSwdService.findList(null, filters, null);
				List<HyGroupSpecialPriceSwd> a3 = hyGroupSpecialPriceSwdService.findList(null, filters, null);
				
				HashMap<String, Object> map1 = new HashMap<>();
				map1.put("hyGroupOtherprices", a1);
				map1.put("hyGroupPrices", a2);
				map1.put("hyGroupSpecialprices", a3);
				map.put("groupSale", map1);
				
				//这是原价格---去HyGroupShenheSwd表里面查询
				filters.clear();
				filters.add(Filter.eq("hygroup", group));
				filters.add(Filter.eq("shenheSwd", lastSwd)); //将上面设置的最后一次甩尾单的实体作为查询条件查出来上次成功甩尾单的价格，即页面上的原价格
				List<HyGroupOtherpriceSwd> a4 = hyGroupOtherpriceSwdService.findList(null, filters, null);
				List<HyGroupPriceSwd> a5 = hyGroupPriceSwdService.findList(null, filters, null);
				List<HyGroupSpecialPriceSwd> a6 = hyGroupSpecialPriceSwdService.findList(null, filters, null);
				
//				HashMap<String, Object> map2 = new HashMap<>();
//				map2.put("hyGroupOtherprices", a4);
//				map2.put("hyGroupPrices", a5);
//				map2.put("hyGroupSpecialprices", a6);
//				map.put("groupSaleOld", map2); //测试注释掉
										
				/**
				 * 审核详情添加
				 */
				String processInstanceId = swd.getProcessInstanceId(); //修改得到甩尾单的流程实例ID
				List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
				Collections.reverse(commentList);
				for (Comment comment : commentList) {
					HashMap<String, Object> im = new HashMap<>();
					String taskId = comment.getTaskId();
					HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
							.singleResult();
					String step = "";
					if (task != null) {
						step = task.getName();
					}
					im.put("step", step);
					String username = comment.getUserId();
					HyAdmin hyAdmin = hyAdminService.find(username);
					String name = "";
					if (hyAdmin != null) {
						name = hyAdmin.getName();
					}
					im.put("auditName", name);
					String str = comment.getFullMessage();
					String[] strs = str.split(":");
					
				    im.put("comment", strs[0]);
				    if(strs[1].equals("yitongguo")) {
				    	im.put("result", "通过");
				    } else if (strs[1].equals("yibohui")) {
				    	im.put("result", "驳回");
				    } else {
				    	im.put("result", "提交审核");
				    }
					
					im.put("time", comment.getTime());

					shenheMap.add(im);
				}
				
				//下面是价格比例
				filters.clear();
				HashMap<String, Object> jiagebili = new HashMap<>();
				filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
				List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
				BigDecimal money = edu.get(0).getMoney();
				jiagebili.put("guonei", money);
			
				filters.clear();
				filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
				List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
				BigDecimal money1 = edu1.get(0).getMoney();
				jiagebili.put("chujing", money1);
			
				filters.clear();
				filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
				List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
				BigDecimal money2 = edu2.get(0).getMoney();
				jiagebili.put("qiche", money2);
				
				filters.clear();
				filters.add(Filter.eq("eduleixing", Eduleixing.piaowujiagebili));
				List<CommonShenheedu> edu3 = commonEdushenheService.findList(null, filters, null);
				BigDecimal money3 = edu3.get(0).getMoney();
				jiagebili.put("piaowu", money3);
				
				map.put("jiagebili", jiagebili);
				map.put("auditRecord", shenheMap);
				j.setMsg("查看详情成功");
				j.setSuccess(true);
				j.setObj(map); //这句话意思是将刚才放到map里面的数据返回给前端
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 审核(通过或者驳回)甩尾单申请
	 * @param id 报账申请id comment 驳回批注   shenheStatus 审核状态 1、yitongguo 2、yibohui
	 * 		
	 * @return
	 */
	@RequestMapping(value="audit")
	public Json audit(Long id, String comment, String shenheStatus, HttpSession session) {
		Json json = new Json();

		try {
			List<Filter> filters = new ArrayList<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyGroupShenheSwd swd = hyGroupShenheSwdService.find(id); //根据前端传过来的甩尾单的id得到甩尾单的实体
			HyGroup group = swd.getHygroup(); //通过甩尾单实体得到团
			String processInstanceId = swd.getProcessInstanceId(); //获取甩尾单数据库表的流程实例ID
			
			//Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();				

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				//modify by cwz 2018/6/25
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();				
				
				if(shenheStatus.equals("yitongguo")) {		
					swd.setAuditStatus(AuditStatus.pass);
					//将原价格改为甩尾单价格
					
					//下面这一段是得到本次甩尾单的价格
					filters.clear();
					filters.add(Filter.eq("hygroup", group));//修改
					filters.add(Filter.eq("shenheSwd", swd)); //找到本次甩尾单的三类价格
					List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds = hyGroupOtherpriceSwdService.findList(null, filters, null);
					List<HyGroupPriceSwd> hyGroupPriceSwds = hyGroupPriceSwdService.findList(null, filters, null);
					List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds = hyGroupSpecialPriceSwdService.findList(null, filters, null);
					
					//下面这一段是得到团的原价格
//					List<HyGroupPrice> a4 = group.getHyGroupPrices();
//					List<HyGroupSpecialprice> a5 = group.getHyGroupSpecialprices();
//					List<HyGroupOtherprice> a6 = group.getHyGroupOtherprices();
					
					//下面这一段是将现在的甩尾单价格覆盖原价格
					//审核通过以后甩尾单的价格需要覆盖原价格
					for(HyGroupPriceSwd temp : hyGroupPriceSwds) {
						HyGroupPrice price = temp.getHyGroupPriceID();
						//temp是前端传过来的  price是通过前端传过来的ID 找到原价格的实体类
						//用前端传过来的数据覆盖price
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
						
						price.setSpecialPrice(temp.getSpecialPrice());
						price.setSpecialPrice1(temp.getSpecialPrice1());
						price.setSpecialPrice4(temp.getSpecialPrice4());
						
						hyGroupSpecialpriceService.update(price);					
					}
					//保存甩尾单次数到团里面
					if(group.getSaleTimes() == 0) {
						group.setSaleTimes(1);
					} else {
						int sals = group.getSaleTimes() + 1;
						group.setSaleTimes(sals);
					}
					hyGroupService.update(group);
					
					//甩尾单通过了之后，需要把数据存到hy_specialty_linelable
					//hySpecialtyLineLabelService
					HySpecialtyLineLabel hySpecialtyLineLabel = new HySpecialtyLineLabel();
					hySpecialtyLineLabel.setCreateTime(swd.getApplyTime());//甩尾单的审核时间，还是new一个
					//查询前端建的尾单
					List<Filter> labelFilters = new ArrayList<>();
					labelFilters.add(Filter.eq("productName", "尾单"));//用尾单去筛选
					List<HyLineLabel> hyLineLabels = hyLineLabelService.findList(null, labelFilters, null);
					if(hyLineLabels != null)
						hySpecialtyLineLabel.setHyLabel(hyLineLabels.get(0));//标签表从哪里来的？用前端新建的，查出来
					hySpecialtyLineLabel.setHyLine(group.getLine());
					hySpecialtyLineLabel.setIsMarked(true);//甩尾单完了是否有效啊 这里是有效
					hySpecialtyLineLabel.setOperator(username);//没问题
					hySpecialtyLineLabelService.save(hySpecialtyLineLabel);
					
				} else if (shenheStatus.equals("yibohui")) {//驳回需要重新提交申请 						
					swd.setAuditStatus(AuditStatus.notpass);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(),username);
				//下面这句话有问题
				taskService.addComment(task.getId(), processInstanceId, comment == null ? "审核通过" + ":" + shenheStatus : comment + ":" + shenheStatus);
				//taskService.addComment(task.getId(), processInstanceId, comment + ":" + shenheStatus);
				taskService.complete(task.getId()); //审核的统一套路，都用这个或者加一个map 这三行不需要改，可能需要在这一行传入一个map，就是需要流程跳转的时候使用
				
				//保存到数据库
				hyGroupShenheSwdService.update(swd);
				
				json.setSuccess(true);
				json.setMsg("审核成功");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	//就需要改第一个参数
	private void helpler(HyGroupShenheSwd hyGroupShenheSwd, List<Map<String, Object>> ans, String status) {
		HashMap<String, Object> m = new HashMap<>();
		HyGroup group = hyGroupShenheSwd.getHygroup(); //得到团的信息
		HyLine line = group.getLine(); //得到团的线路的信息
		HySupplier supplier = line.getHySupplier(); //得到线路供应商
		HyAdmin operator = line.getOperator(); //得到线路的建团计调
		
		//涉及到的产品ID都是线路ID	
		m.put("id", hyGroupShenheSwd.getId()); //审核的甩尾单表的ID
		m.put("shenheStatus", status);	
		m.put("pn", line.getPn()); //得到产品ID
		m.put("startDate", group.getStartDay()); //得到团的发团日期
		m.put("lineName", line.getName()); //得到线路的名称
		m.put("supplierName", supplier.getSupplierName()); //得到供应商名称
		m.put("operatorName", operator.getName()); //提交计调名称
		m.put("applyTime", hyGroupShenheSwd.getApplyTime()); //得到甩尾单的提交申请日期
		
		ans.add(m); //将结果加入返回队列
	}
}
