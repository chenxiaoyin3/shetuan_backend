package com.hongyu.controller.liyang;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.poi.hssf.record.chart.DatRecord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddContractTemplate;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.FddContractService;
import com.hongyu.service.FddContractTemplateService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.util.DateUtil;
import com.hongyu.util.HttpReqUtil;
import com.hongyu.util.contract.AutoSignRequest;
import com.hongyu.util.contract.CARequestEntity;
import com.hongyu.util.contract.ContractInfoAboutHy;
import com.hongyu.util.contract.FddContractUtil;
import com.hongyu.util.contract.SelfPayAgreement;
import com.hongyu.util.contract.ShoppingAgreement;
import com.hongyu.util.contract.SignRequest;
import com.hongyu.util.liyang.CopyFromNotNullBeanUtilsBean;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * 负责境内和境外合同的生成和修改以及上传。
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/storeLineOrderStore/fddContract/")
public class FddContractController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name = "fddContractTemplateServiceImpl")
	FddContractTemplateService fddContractTemplateService;
	
	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	/**
	 * 合同列表接口
	 * 暂时未完善，根据前端的检索项增加参数，添加到filter中
	 * @param pageable
	 * @param orderId
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Long orderId){
		Json json = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			List<Filter> filters = new ArrayList<>();
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<FddContract> page = fddContractService.findPage(pageable);
			List<Map<String, Object>>result=new LinkedList<>();
			for(FddContract tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				//封装数据
				result.add(map);
			}			
			hashMap.put("pageNumber", page.getPageNumber());
			hashMap.put("pageSize", page.getPageSize());
			hashMap.put("total", page.getTotal());
			hashMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败:"+e.getMessage());
		}
		
		return json;
	}
	/**
	 * 合同详情页
	 * @param id
	 * @return
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			FddContract fddContract = fddContractService.find(id);
			HashMap< String, Object> hashMap = new HashMap<>();
			if(fddContract!=null){
				hashMap.put("contractId", fddContract.getContractId());
				hashMap.put("status", fddContract.getStatus());
				hashMap.put("downloadUrl", fddContract.getDownloadUrl());
				hashMap.put("viewpdfUrl", fddContract.getViewpdfUrl());
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败:"+e.getMessage());
		}
		
		return json;
	}
	//上传合同模板
	@RequestMapping("upload")
	@ResponseBody
	public Json uploadTemplate(File file,FddContractTemplate fddContractTemplate){
		Json json = new Json();
		try {
			FddContractUtil fddContractUtil = FddContractUtil.getInstance();
			Boolean success = fddContractUtil.uploadTemplate(file, fddContractTemplate.getTemplateId());
			if(success){
				fddContractTemplateService.save(fddContractTemplate);
				json.setMsg("上传法大大成功");
				json.setSuccess(true);
			}else{
				json.setMsg("上传法大大失败");
				json.setSuccess(false);
			}
			
		} catch (Exception e) {
			json.setMsg("写入模板表失败"+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * 返回fddDayTripContract实体，里面已经有填充了可以从数据库获取的数据
	 * 1.已经有合同信息则从合同表中提取
	 * 2.如果没有，则新建一个合同
	 * @param orderId
	 * @param session
	 * @return
	 */
	@RequestMapping("fillIn")
	@ResponseBody
	public Json fillIn(Long orderId,HttpSession session){
		Json json = new Json();
		try {
				HashMap<String, Object> hm = fddContractService.fillIn(orderId, session);
				json.setMsg((String)hm.get("msg"));
				json.setObj(hm);
				json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	public static class Wrap{
		FddContract fddContract;
		List<ShoppingAgreement> shoppingAgreements;
		List<SelfPayAgreement> selfPayAgreements;
		public FddContract getFddContract() {
			return fddContract;
		}
		public void setFddContract(FddContract fddContract) {
			this.fddContract = fddContract;
		}
		public List<ShoppingAgreement> getShoppingAgreements() {
			return shoppingAgreements;
		}
		public void setShoppingAgreements(List<ShoppingAgreement> shoppingAgreements) {
			this.shoppingAgreements = shoppingAgreements;
		}
		public List<SelfPayAgreement> getSelfPayAgreements() {
			return selfPayAgreements;
		}
		public void setSelfPayAgreements(List<SelfPayAgreement> selfPayAgreements) {
			this.selfPayAgreements = selfPayAgreements;
		}
		
	}
	
	
	/**
	 * 接收前端传回的合同填充信息。
	 * 1、如果是新合同，则将合同save到合同表中
	 * 如果是重签的合同，则更新该合同
	 * 2、调用虹宇自动签章
	 * @param fddDayTripContract
	 * @return
	 */
	@RequestMapping("submit")
	@ResponseBody
	public Json generateContract(@RequestBody Wrap wrap){
		Json json = new Json();
		try {
			FddContract fddContract = wrap.getFddContract();
			List<ShoppingAgreement> shoppingAgreements = wrap.getShoppingAgreements();
			List<SelfPayAgreement> selfPayAgreements = wrap.getSelfPayAgreements();
			HashMap<String, Object> hm = fddContractService.submit(fddContract,shoppingAgreements,selfPayAgreements);
			json.setMsg((String) hm.get("msg"));
			json.setObj(hm);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("生成合同失败："+e.getMessage());
		}
		return json;
	}
	/**
	 * 
	 * @param id 合同表的主键id
	 * @return
	 */
	@RequestMapping("customerSign")
	@ResponseBody
	public Json extCustomerSign(Long id){
		Json json = new Json();
		try {
			String result = fddContractService.extCustomerSign(id);
			JSONObject jsonObject = JSONObject.parseObject(result);
			String code = jsonObject.getString("code");
			if(code.equals("3000")){
				json.setMsg("给客户发送信息成功，等待客户签章");
				json.setSuccess(true);
			}else{
				json.setMsg("给客户发送信息失败:"+jsonObject.getString("msg"));
				json.setSuccess(false);
			}			
		} catch (Exception e) {
			json.setMsg("给客户发送信息失败"+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
}
