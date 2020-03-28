package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
/**
 * 合同负责人接口-子账号和负责人绑定，区域和合同绑定
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/contract/manage/")
public class SupplierLiableController {

	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name="departmentServiceImpl")
	private DepartmentService departmentService;	
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	/**
	 * 负责人添加子账号信息
	 * @param subAdmin
	 * @param roleId
	 * @param session
	 * @param enabled
	 * @param cjAreas
	 * @param gnAreas
	 * @param qcAreas
	 * @return
	 */
	//修改，子账号和子区域和负责人绑定，而不是和合同绑定
	@RequestMapping(value="add", method = RequestMethod.POST)
	public Json add(HyAdmin subAdmin, Long roleId, HttpSession session, Boolean enabled,
					Long[] cjAreas, Long[] gnAreas, Long[] qcAreas) {
		Json j = new Json();
		try{
			/** 得到当前用户和所属部门 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminService.find(username);
			Department department = liable.getDepartment();
			
			/** 设置账号角色、部门、是否启用、所属合同 */
			subAdmin.setRole(hyRoleService.find(roleId));
			subAdmin.setDepartment(department);
			subAdmin.setIsEnabled(enabled);
			subAdmin.setHyAdmin(liable);
			
			/**　设置子账号分配区域 */
			Set<HyArea> cAreas = new HashSet<HyArea>();
			if(cjAreas != null && cjAreas.length > 0) {
				for(Long id : cjAreas)
					cAreas.add(hyAreaService.find(id));
				subAdmin.setAreaChujing(cAreas);
			}
			
			Set<HyArea> gAreas = new HashSet<HyArea>();
			if(gnAreas != null && gnAreas.length > 0) {
				for(Long id : gnAreas)
					gAreas.add(hyAreaService.find(id));
				subAdmin.setAreaGuonei(gAreas);
			}
			
			Set<HyArea> qAreas = new HashSet<HyArea>();
			if(qcAreas != null && qcAreas.length > 0) {
				for(Long id : qcAreas)
					qAreas.add(hyAreaService.find(id));
				subAdmin.setAreaQiche(qAreas);
			}
			hyAdminService.save(subAdmin);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 子账号列表
	 * @param pageable 分页信息
	 * @param admin 账号信息
	 * @param session 会话信息
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HyAdmin admin, HttpSession session) {
		Json j = new Json();
		try {
			/** 得到当前负责人 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminService.find(username);
			
			List<Filter> filters = new ArrayList<>();
			Filter filter = Filter.eq("hyAdmin", liable);
			filters.add(filter);
			pageable.setFilters(filters);
			Page<HyAdmin> page = hyAdminService.findPage(pageable, admin);
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(page);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="update")
	public Json update(HyAdmin subAdmin, Long roleId, HttpSession session, Boolean isEnabled,
			Long[] cjAreas, Long[] gnAreas, Long[] qcAreas) {
		Json j = new Json();
		try{
			/** 得到当前用户和所属部门 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminService.find(username);
			Department department = liable.getDepartment();

			
			/** 设置账号角色、部门、是否启用、所属合同 */
			subAdmin.setRole(hyRoleService.find(roleId));
			subAdmin.setDepartment(department);
			subAdmin.setIsEnabled(isEnabled);
			subAdmin.setHyAdmin(liable);
			
			/**　设置子账号分配区域 */
			Set<HyArea> cAreas = new HashSet<HyArea>();
			if(cjAreas != null && cjAreas.length > 0) {
				for(Long id : cjAreas)
					cAreas.add(hyAreaService.find(id));
				subAdmin.setAreaChujing(cAreas);
			}
			
			Set<HyArea> gAreas = new HashSet<HyArea>();
			if(gnAreas != null && gnAreas.length > 0) {
				for(Long id : gnAreas)
					gAreas.add(hyAreaService.find(id));
				subAdmin.setAreaGuonei(gAreas);
			}
			
			Set<HyArea> qAreas = new HashSet<HyArea>();
			if(qcAreas != null && qcAreas.length > 0) {
				for(Long id : qcAreas)
					qAreas.add(hyAreaService.find(id));
				subAdmin.setAreaQiche(qAreas);
			}
			hyAdminService.update(subAdmin,
					"username","password","isOnjob","contract",
					"isLocked","loginFailureCount","lockedDate","loginDate",
					"loginIp","hyAdmin","hyAdmins","hySupplierContract");
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 获取子账号区域--从现在正常的合同中获取区域信息--当新合同生效的时候才修改子区域的信息
	 * @param pageable
	 * @param admin
	 * @param session
	 * @return
	 */
	@RequestMapping(value="area/view")
	public Json area(HttpSession session) {
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<>();
			/** 得到当前用户和合同 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminService.find(username);
			HySupplierContract contract = null;
			//修改，负责人可以有多个合同，但是只要一个是原合同
			Set<HySupplierContract> contracts = liable.getLiableContracts();
			for(HySupplierContract c : contracts) {
				if(c.getContractStatus() == ContractStatus.zhengchang) { //如果是正在生效的合同
					contract = c;
					break;
				}
			}

			if(contract != null) { //根据原合同找到区域信息
				hm.put("chujingAreas", contract.getChujingAreas());
				hm.put("guoneiAreas", contract.getGuoneiAreas());
				hm.put("qicheAreas", contract.getQicheAreas());
			}
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(hm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
}
