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
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.AdministrativeUploadService;
import com.hongyu.service.HyAdminService;

@Controller
@RequestMapping("/admin/administrativeDownload")
public class AdministrativeDownloadController {

	@Resource(name="administrativeUploadServiceImpl")
	AdministrativeUploadService administrativeUploadSrv;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
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
}
