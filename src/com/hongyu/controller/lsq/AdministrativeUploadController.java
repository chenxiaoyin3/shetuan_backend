package com.hongyu.controller.lsq;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.AdministrativeUpload;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.AdministrativeUploadService;
import com.hongyu.service.HyAdminService;

@Controller
@RequestMapping("/admin/administrativeUpload")
public class AdministrativeUploadController {
	@Resource(name="administrativeUploadServiceImpl")
	AdministrativeUploadService administrativeUploadSrv;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public Json administrativeUploadAdd(AdministrativeUpload administrativeFile, HttpSession session) {
		Json json = new Json();
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		try {
			administrativeFile.setCreator(admin.getName());
			administrativeUploadSrv.save(administrativeFile);
			json.setSuccess(true);
			json.setMsg("添加成功");
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("添加失败");
		}
		return json;
	}
	@RequestMapping(value = "/page/view", method = RequestMethod.GET)
	@ResponseBody
	public Json administrativeUploadPage(AdministrativeUpload administrativeFile, Pageable pageable) {
		Json json = new Json();
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<AdministrativeUpload> page = administrativeUploadSrv.findPage(pageable, administrativeFile);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		return json;
	}
	@RequestMapping("delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			administrativeUploadSrv.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
