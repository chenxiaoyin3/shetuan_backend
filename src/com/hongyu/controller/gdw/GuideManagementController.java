package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.xml.soap.Detail;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;

import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/guideManagement/")
public class GuideManagementController {
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Guide guide){
		Json json=new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.ne("status", 0));
			filters.add(Filter.ne("status", 2));
			filters.add(Filter.ne("status", 4));
			filters.add(Filter.ne("status", 5));
			pageable.setFilters(filters);
			Page<Guide> page=guideService.findPage(pageable,guide);
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Guide tmp:page.getRows()){
				HashMap<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("name", tmp.getName());
				map.put("sex", tmp.getSex());
				map.put("IDNumber", tmp.getIdNumber());
				map.put("rank", tmp.getRank());
				map.put("touristCertificateNumber", tmp.getTouristCertificateNumber());
				map.put("phone", tmp.getPhone());
				map.put("guideSn", tmp.getGuideSn());
				map.put("applicationTime", tmp.getApplicationTime());
				map.put("status", tmp.getStatus());
				map.put("zongheLevel", tmp.getZongheLevel());
				result.add(map);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			Guide guide=guideService.find(id);
			if(guide!=null){
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guide);
			}else{
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}
	
	
	@RequestMapping("changeStatus")
	@ResponseBody
	public Json changeStatus(Long id){
		Json json=new Json();
		try {
			Guide guide=guideService.find(id);
			if(guide!=null){
				guide.setStatus(guide.getStatus()==1?3:1);
				guideService.update(guide);
				json.setSuccess(true);
				json.setMsg("更改成功");
			}else{
				json.setSuccess(false);
				json.setMsg("更改失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更改错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
