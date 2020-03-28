package com.hongyu.controller;

import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPaymentSupplier;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.PayDetails;
import com.hongyu.entity.PayServicer;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyPaymentSupplierService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.PayDetailsService;
import com.hongyu.service.PayServicerService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/ticket/payment/")
public class HyPaymentSupplierController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "payDetailsServiceImpl")
	PayDetailsService payDetailsService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	@Resource(name="hyPaymentSupplierServiceImpl")
	private HyPaymentSupplierService hyPaymentSupplierService;
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable,Integer checkStatus,String supplierName,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			HyPaymentSupplier hyPaymentSupplier=new HyPaymentSupplier();
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			List<Filter> payFilter=new ArrayList<Filter>();
			if(checkStatus!=null){
				payFilter.add(Filter.eq("checkStatus", checkStatus));
			}
			List<Filter> filter=new ArrayList<Filter>();
			if(supplierName!=null&&!supplierName.equals("")){
				filter.add(Filter.like("supplierName",supplierName));
				List<HySupplier> supplierList=hySupplierService.findList(null,filter,null);
				if(supplierList.size()==0){
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyPaymentSupplier>());
				}
				else{
					payFilter.add(Filter.in("piaowubuGongyingshang", supplierList));
					payFilter.add(Filter.in("operator", hyAdmins));
					pageable.setFilters(payFilter);
					List<Order> orders = new ArrayList<Order>();
					orders.add(Order.desc("createDate"));
					pageable.setOrders(orders);
					Page<HyPaymentSupplier> page=hyPaymentSupplierService.findPage(pageable,hyPaymentSupplier);
					if(page.getTotal()>0){
						for(HyPaymentSupplier paymentSupplier:page.getRows()){
							HashMap<String,Object> payMap=new HashMap<String,Object>();
						    HyAdmin operator=paymentSupplier.getOperator();
						    payMap.put("id", paymentSupplier.getId());
						    payMap.put("supplierName",paymentSupplier.getPiaowubuGongyingshang().getSupplierName());
						    payMap.put("startTime",paymentSupplier.getStartTime());
						    payMap.put("endTime",paymentSupplier.getEndTime());
						    payMap.put("money", paymentSupplier.getMoney());
						    payMap.put("creatorDate", paymentSupplier.getCreateDate());
						    payMap.put("checkStatus",paymentSupplier.getCheckStatus());
						    payMap.put("payStatus", paymentSupplier.getPayStatus());
						    if(paymentSupplier.getOperator()!=null){
						    	payMap.put("operator", operator.getName());
						    }
						    /** 当前用户对本条数据的操作权限 */
						    if(operator.equals(admin)){
						    	if(co==CheckedOperation.view){
						    		payMap.put("privilege", "view");
						    	}
						    	else{
						    		payMap.put("privilege", "edit");
						    	}
						    }
						    else{
						    	if(co==CheckedOperation.edit){
						    		payMap.put("privilege", "edit");
						    	}
						    	else{
						    		payMap.put("privilege", "view");
						    	}
						    }
						    list.add(payMap);
						}
					}
					map.put("rows", list);
				    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
				    map.put("pageSize", Integer.valueOf(pageable.getRows()));
				    map.put("total",Long.valueOf(page.getTotal()));
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(map);
			    }
			
			}
			else{
				payFilter.add(Filter.in("operator", hyAdmins));
				pageable.setFilters(payFilter);
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc("createDate"));
				pageable.setOrders(orders);
				Page<HyPaymentSupplier> page=hyPaymentSupplierService.findPage(pageable,hyPaymentSupplier);
				if(page.getTotal()>0){
					for(HyPaymentSupplier paymentSupplier:page.getRows()){
						HashMap<String,Object> payMap=new HashMap<String,Object>();
					    HyAdmin operator=paymentSupplier.getOperator();
					    payMap.put("id", paymentSupplier.getId());
					    payMap.put("supplierName",paymentSupplier.getPiaowubuGongyingshang().getSupplierName());
					    payMap.put("startTime",paymentSupplier.getStartTime());
					    payMap.put("endTime",paymentSupplier.getEndTime());
					    payMap.put("money", paymentSupplier.getMoney());
					    payMap.put("creatorDate", paymentSupplier.getCreateDate());
					    payMap.put("checkStatus",paymentSupplier.getCheckStatus());
					    payMap.put("payStatus", paymentSupplier.getPayStatus());
					    if(paymentSupplier.getOperator()!=null){
					    	payMap.put("operator", operator.getName());
					    }
					    /** 当前用户对本条数据的操作权限 */
					    if(operator.equals(admin)){
					    	if(co==CheckedOperation.view){
					    		payMap.put("privilege", "view");
					    	}
					    	else{
					    		payMap.put("privilege", "edit");
					    	}
					    }
					    else{
					    	if(co==CheckedOperation.edit){
					    		payMap.put("privilege", "edit");
					    	}
					    	else{
					    		payMap.put("privilege", "view");
					    	}
					    }
					    list.add(payMap);
					}
				}
				map.put("rows", list);
			    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
			    map.put("pageSize", Integer.valueOf(pageable.getRows()));
			    map.put("total",Long.valueOf(page.getTotal()));
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(map);
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{		
			HyPaymentSupplier hyPaymentSupplier = hyPaymentSupplierService.find(id);
			String processInstanceId = hyPaymentSupplier.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
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
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);		
					
			// 银行信息
			obj.put("accountName", hyPaymentSupplier.getPiaowubuGongyingshang().getBankId().getAccountName()); // 账户名称
			obj.put("bankName", hyPaymentSupplier.getPiaowubuGongyingshang().getBankId().getBankName()); // 银行名称
			obj.put("bankCode", hyPaymentSupplier.getPiaowubuGongyingshang().getBankId().getBankCode()); // 银行联行号
			obj.put("bankType", hyPaymentSupplier.getPiaowubuGongyingshang().getBankId().getBankType()); // 对公对私
			obj.put("bankAccount", hyPaymentSupplier.getPiaowubuGongyingshang().getBankId().getBankAccount()); // 帐号

			// 申请信息
			obj.put("supplierName", hyPaymentSupplier.getPiaowubuGongyingshang().getSupplierName());
			obj.put("startTime", hyPaymentSupplier.getStartTime());
			obj.put("endTime", hyPaymentSupplier.getEndTime());
			obj.put("money", hyPaymentSupplier.getMoney());
			obj.put("remark", hyPaymentSupplier.getRemark());
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyPaymentSupplier hyPaymentSupplier,Long supplierId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("ticketPay");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 分销商充值申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			HySupplier hySupplier=hySupplierService.find(supplierId);
			hyPaymentSupplier.setPiaowubuGongyingshang(hySupplier);
			hyPaymentSupplier.setOperator(admin);
			hyPaymentSupplier.setCheckStatus(0);
			hyPaymentSupplier.setPayStatus(false);
			hyPaymentSupplier.setCreateDate(new Date());
			hyPaymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
			hyPaymentSupplierService.save(hyPaymentSupplier);
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/** 付款详情  */
	@RequestMapping(value="payment/detail")
	@ResponseBody
	public Json add(Long id,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", 5));  //1:预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值
			filters.add(Filter.eq("reviewId", id));
			List<PayServicer> list2 = payServicerService.findList(null, filters, null);
			
			
			filters.clear();
			filters.add(Filter.eq("sort", 1));
			filters.add(Filter.eq("payId", list2.get(0).getId()));
			List<PayDetails> list = payDetailsService.findList(null, filters, null);
			
			json.setObj(list);
			json.setSuccess(true);
			
 		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping(value="supplierList")
	@ResponseBody
	public Json supplierList()
	{
		Json json=new Json();
		try{
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<HySupplier> supplierList=hySupplierService.findAll();
			for(HySupplier supplier:supplierList){
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("supplierId", supplier.getId());
				map.put("supplierName", supplier.getSupplierName());
				map.put("bankAccount", supplier.getBankId().getBankAccount());
				map.put("accountName", supplier.getBankId().getAccountName());
				map.put("bankName", supplier.getBankId().getBankName());
				map.put("bankCode", supplier.getBankId().getBankCode());
				map.put("bankType", supplier.getBankId().getBankType());
				list.add(map);
			}
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(list);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
