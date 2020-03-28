package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BankList.Yinhangleixing;
import com.hongyu.entity.CommonUploadFileEntity;
import com.hongyu.entity.CommonUploadFileEntity.UploadTypeEnum;
import com.hongyu.entity.DepositServicer;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.service.BankListService;
import com.hongyu.service.CommonUploadFileService;
import com.hongyu.service.DepositServicerService;
import com.hongyu.service.GystuiyajinService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.impl.CommonUploadFileServiceImpl;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 供应商负责人合同信息Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/contract/info/")
public class HyContractInfoController {

	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="bankListServiceImpl")
	private BankListService bankListService;
	
	@Resource(name="depositServicerServiceImpl")
	private DepositServicerService depositServicerService;
	
	@Resource(name = "gysfzrtuichuServiceImpl")
	private GystuiyajinService gystuiyajinService;
	
	@Resource(name = "commonUploadFileServiceImpl")
	private CommonUploadFileService commonUploadFileService;
	
	/**
	 * 合同负责人合同详情
	 * @param session
	 * @return
	 */
	@RequestMapping(value="page/view")
	public Json pageView(HttpSession session) {
		Json j = new Json();
		try {
			/** 得到当前用户 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminService.find(username);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("liable", liable));
			
			List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
			HySupplierContract contract = supplierContracts.get(0);
			HySupplier supplier = contract.getHySupplier();
			
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> areaMap = new HashMap<String, Object>();
			Map<String, Object> operatorMap = new HashMap<String, Object>();
			
				
				map.put("id",supplier.getId());
				map.put("supplierName", supplier.getSupplierName());
				map.put("isVip", supplier.getIsVip());
				map.put("pinpaiName", supplier.getPinpaiName());
				map.put("isLine", supplier.getIsLine());
				map.put("isInner", supplier.getIsInner());
				map.put("isDijie", supplier.getIsDijie());
				map.put("isCaigouqian", supplier.getIsCaigouqian());
				map.put("intro", supplier.getIntro());
				
				HyArea area = supplier.getArea();
				List<Long> areaIds = area.getTreePaths();
				areaMap.put("id", area.getId());
				areaMap.put("ids", areaIds);
				areaMap.put("fullName", area.getFullName());
				map.put("area", areaMap);
				
				map.put("address", supplier.getAddress());
				map.put("yycode", supplier.getYycode());
				map.put("yy", supplier.getYy());
				map.put("jycode", supplier.getJycode());
				map.put("jy", supplier.getJy());
				map.put("supplierStatus", supplier.getSupplierStatus());
				//新增供应商退押金文件模板
				filters.clear();
				filters.add(Filter.eq("type", UploadTypeEnum.gystuiyajin));
				List<CommonUploadFileEntity> files = commonUploadFileService.findList(null, filters, null);
				if(files.get(0) != null) {
					map.put("quitFileUrl", files.get(0).getFileUrl());
				}			
				operatorMap.put("username", supplier.getOperator().getUsername());
				operatorMap.put("name", supplier.getOperator().getName());
				map.put("operator", operatorMap);
				
				map.put("hySupplierContracts", supplierContracts);
				j.setMsg("查看详情成功");
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
	 * 查询是否有交押金记录
	 * added by Gsbing 20180714
	 * @param contractId
	 * @return
	 */
	@RequestMapping(value="search/view")
	public Json search(Long contractId)
	{
		Json json=new Json();
		try {
			Pageable pageable=new Pageable();
			HySupplierContract hySupplierContract=hySupplierContractService.find(contractId);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("contractId", hySupplierContract));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("applyTime"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<DepositServicer> page=depositServicerService.findPage(pageable);
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(page.getRows().isEmpty()) {
				map.put("status", 0);
			}
			else {
				if(page.getRows().get(0).getAuditStatus().equals(AuditStatus.auditing)) {
					map.put("status",1);
				}
				else if(page.getRows().get(0).getAuditStatus().equals(AuditStatus.pass)){
					map.put("status",2);
				}
				else if(page.getRows().get(0).getAuditStatus().equals(AuditStatus.notpass)){
					map.put("status",3);
				}
			}
			json.setSuccess(true);
			json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 获取虹宇总部财务的银行账号信息
	 * @param contractID
	 * @return
	 */
	@RequestMapping(value="hybanklist/view")
	public Json bankListView() {
		Json j = new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("yhlx",Yinhangleixing.zbcw));
			List<BankList> bankLists = bankListService.findList(null, filters, null);
			for(BankList bl : bankLists) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", bl.getId());
				hm.put("bankAccount", bl.getBankAccount());
				hm.put("bankName", bl.getBankName());
				hm.put("accountName", bl.getAccountName());
				hm.put("bankCode", bl.getBankCode());
				hm.put("bankType", bl.getBankType());
				if(StringUtils.isNotBlank(bl.getAlias())) {
					hm.put("alias",bl.getAlias());
				} else {
					int length = bl.getBankAccount().length() - 1;
					hm.put("alias",bl.getBankName() + "(尾号" + bl.getBankAccount().substring(length - 4, length) + ")");
				}
				list.add(hm);
			}
			j.setSuccess(true);
			j.setMsg("查看详情成功");	
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 供应商交押金-开启交押金审核流程
	 * @param session
	 * @param bank 供应商银行信息
	 * @param servicer
	 * @param contract
	 * @return
	 */
	@RequestMapping(value="pay")
	public Json pay(HttpSession session, DepositServicer servicer, Long contract, Long hyBankId, @DateTimeFormat(iso=ISO.DATE_TIME)Date payDate) {
		//已修改-付款时间接收不到
		Json j = new Json();
		try {	
			//开启付押金的流程
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("jiaoyajinprocess");
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			
			//增加提交押金申请的记录
			HySupplierContract c = hySupplierContractService.find(contract);
			BankList bl = bankListService.find(hyBankId);
			HyAdmin admin = c.getLiable();
			servicer.setServicerName(admin);
			servicer.setContractId(c);
			servicer.setApplyTime(new Date());
			servicer.setPayTime(payDate);
			if(bl != null) {
				servicer.setBankList(bl);
			}
			servicer.setAuditStatus(AuditStatus.auditing);
			servicer.setProcessInstanceId(pi.getProcessInstanceId());
			depositServicerService.save(servicer);
			// 完成任务
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());	
			j.setMsg("提交缴纳押金申请成功");
			j.setSuccess(true);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	/**
	 * 合同负责人申请退出 - 退押金和退出是同一个流程
	 * 启动供应商退押金流程实例
	 * @param contractId
	 * @return
	 */
	@RequestMapping(value="quit")
	public Json quit(Long contractId, Gysfzrtuichu tc, HttpSession session) {
		Json j = new Json();
		try {
			HashMap<String, Object> map = new HashMap<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			//开启退押金的流程
			
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("cgbgystuiyajinprocess");
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			//新增退押金申请的记录
			
			HySupplierContract c = hySupplierContractService.find(contractId);
			//退出的时候将供应商账号全部置为查看权限
			HyAdmin liable = c.getLiable();
//			hyAdminService.putView(liable.getUsername());
			if(c.getHySupplier().getIsCaigouqian()) {
				map.put("result", "zongbu");
			} else {
				map.put("result", "qiche");
			}
			tc.setContract(c);
			tc.setApplierName(username);
			tc.setApplyTime(new Date());
			tc.setAuditStatus(AuditStatus.auditing);
			tc.setProcessInstanceId(pi.getProcessInstanceId());
			gystuiyajinService.save(tc);
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(), map);	
			j.setMsg("提交退押金申请成功");
			j.setSuccess(true);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
