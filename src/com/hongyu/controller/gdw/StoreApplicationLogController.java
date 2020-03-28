package com.hongyu.controller.gdw;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.WeBusiness;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Controller
@RequestMapping("/admin/storeApplicationLog/")
public class StoreApplicationLogController {
	@Resource(name="storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	@Resource
	private HistoryService historyService;
		
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@Resource(name="weBusinessServiceImpl")
	WeBusinessService weBusinessService;
//	static class WrapStoreApplication{
//		private Pageable pageable;
//		private StoreApplication storeApplication;
//		public Pageable getPageable() {
//			return pageable;
//		}
//		public void setPageable(Pageable pageable) {
//			this.pageable = pageable;
//		}
//		public StoreApplication getStoreApplication() {
//			return storeApplication;
//		}
//		public void setStoreApplication(StoreApplication storeApplication) {
//			this.storeApplication = storeApplication;
//		}
//	}
//	@RequestMapping("addStoreApplication")
//	@ResponseBody
//	public Json addStoreApplication(StoreApplication storeApplication){
//		Json json=new Json();
//		try {
//			storeApplicationService.save(storeApplication);
//			json.setSuccess(true);
//			json.setMsg("添加成功");
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("添加失败");
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			StoreApplication storeApplication=storeApplicationService.find(id);
			json.setSuccess(true);
			json.setMsg("获取失败");
			json.setObj(storeApplication);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("list/view")
	@ResponseBody
	public  Json list(Integer page,Integer rows,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			if(stores!=null&&stores.size()>0){
				Store store=stores.get(0);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				List<Order> orders = new ArrayList<Order>();
				Order order = Order.desc("createtime");
				orders.add(order);
				List<StoreApplication> ans=storeApplicationService.findList(null, filters2, orders);
				Map<String, Object> answer=new HashMap<>();
				answer.put("total", ans.size());
				answer.put("pageNumber",page);
				answer.put("pageSize", rows);
				answer.put("rows", ans.subList((page-1)*rows, page*rows>ans.size()?ans.size():page*rows));
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(answer);
			}else{
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("getHistoryComments/view")
	@ResponseBody
	public Json getHistoryComments(Long id){
		Json json=new Json();
		try {
			StoreApplication storeApplication=storeApplicationService.find(id);
			String processInstanceId=storeApplication.getProcessInstanceId();
			List<Comment> commentList=taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> result=new LinkedList<>();
			for(Comment comment:commentList){
				Map<String, Object> map=new HashMap<>();
				String taskId=comment.getTaskId();
				HistoricTaskInstance task=historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
				String step="";
				if(task!=null){
					step=task.getName();
				}
				map.put("step", step);
				String username=comment.getUserId();
				HyAdmin hyAdmin=hyAdminService.find(username);
				String name="";
				if(hyAdmin!=null){
					name=hyAdmin.getName();
				}
				map.put("name", name);
				String str=comment.getFullMessage();
				int index=str.lastIndexOf(":");
				map.put("comment", str.substring(0, index+1));
				map.put("time", comment.getTime());
				map.put("result", Integer.parseInt(str.substring(index+1)));
				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
		
	}
}
