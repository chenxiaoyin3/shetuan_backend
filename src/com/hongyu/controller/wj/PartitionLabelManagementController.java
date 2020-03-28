package com.hongyu.controller.wj;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

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
import com.hongyu.entity.Area;
import com.hongyu.entity.FenquArea;
import com.hongyu.entity.FenquLabel;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyCountry;
import com.hongyu.service.FenquAreaService;
import com.hongyu.service.FenquLabelService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCountryService;
import com.hongyu.service.impl.FenquLabelServiceImpl;

import javafx.print.JobSettings;

@Controller
@RequestMapping("/admin/partitionlabel/management")
public class PartitionLabelManagementController {
	
	@Resource(name = "fenquLabelServiceImpl")
	FenquLabelService fenquLabelService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
	@Resource(name = "fenquAreaServiceImpl")
	FenquAreaService fenquAreaService;
	
	@Resource(name = "hyCountryServiceImpl")
	HyCountryService hyCountryService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	
	/**
	 ** 分区标签列表页
	 **/
	@RequestMapping("/list/view") 
	@ResponseBody
	public Json list(Integer type,String name,Integer isActive,Pageable page,HttpSession session){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", type));
			filters.add(Filter.eq("name",name));
			filters.add(Filter.eq("isActive", isActive));
			page.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("sort"));
			page.setOrders(orders);
			Page<FenquLabel> labels = fenquLabelService.findPage(page);
			HashMap<String, Object> answer = new HashMap<>();
			List<HashMap<String, Object>> ans = new ArrayList<>();
			
			for(FenquLabel fenquLabel:labels.getRows()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("name", fenquLabel.getName());
				map.put("type", fenquLabel.getType());
				map.put("sort", fenquLabel.getSort());
				map.put("operator", hyAdminService.find(fenquLabel.getOperator()).getName());
				map.put("createTime", fenquLabel.getCreateTime());
				map.put("isActive", fenquLabel.getIsActive());
				map.put("id", fenquLabel.getId());
				ans.add(map);
			}

			answer.put("total",labels.getTotal());
			answer.put("pageNumber", page.getPage());
			answer.put("pageSize", page.getRows());
			answer.put("rows", ans);
			
			json.setObj(answer);
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
		
	}
	
	/**
	 * 查找中国下属所有省的信息
	 * @return
	 */
	@RequestMapping(value="/province/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getProvince() {
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<>();
			HyArea pArea = hyAreaService.find(0L); //找到全国
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("hyArea", pArea);
			Filter filter1 = Filter.eq("status", true);
			filters.add(filter1);
			filters.add(filter);
			List<HyArea> list = hyAreaService.findList(null, filters, null);
			hm.put("total", list.size());
			hm.put("data", list);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 得到国外国家信息(出境)
	 * @return
	 */
	@RequestMapping(value="/country/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getCountry() {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("treePath", ",1,");
			Filter filter1 = Filter.eq("status", true);
			filters.add(filter1);
			filters.add(filter);
			List<HyArea> list = hyAreaService.findList(null, filters, null);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 得到所有国家（签证）
	 */
	@RequestMapping(value="/qianzhengcountry/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getQianzhengCountry() {
		Json j = new Json();
		try {
//			List<Filter> filters = new ArrayList<Filter>();
//			Filter filter = Filter.eq("treePath", ",1,");
//			Filter filter1 = Filter.eq("status", true);
//			filters.add(filter1);
//			filters.add(filter);
//			List<HyArea> list = hyAreaService.findList(null, filters, null);
			List<HyCountry> countries = hyCountryService.findAll();
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(countries);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 获取分区标签详情页
	 * @param id
	 * @return
	 */
	@RequestMapping("/detail") 
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			FenquLabel fenquLabel = fenquLabelService.find(id);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("fenquLabelId", id));
			List<FenquArea> areas = fenquAreaService.findList(null,filters,null);
//			HashMap<String, Object> map = new HashMap<>();
//			map.put("area", areas);
//			map.put("label", fenquLabel);
//			List<HashMap<String,Object>> ans = new ArrayList<>();
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", fenquLabel.getId());
			map.put("name", fenquLabel.getName());
			map.put("type", fenquLabel.getType());
			map.put("operator", hyAdminService.find(fenquLabel.getOperator()).getName());
			map.put("sort", fenquLabel.getSort());
			map.put("createTime", fenquLabel.getCreateTime());
			map.put("isActive", fenquLabel.getIsActive());
			map.put("iconUrl", fenquLabel.getIconUrl());
			
			Set<Long> area = new HashSet<>();
			for(FenquArea a : areas){
				area.add(a.getAreaId());
			}
			map.put("areas", area);
			json.setMsg("获取成功");
			json.setObj(map);
			json.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
		
	}
	
	/**
	 * 分区标签编辑页
	 * @return
	 */
	@RequestMapping("/edit") 
	@ResponseBody
	public Json edit(Long id,Long[] areas,Integer type,Integer sort,String name,Boolean isActive){
		Json json = new Json();
		try {
			FenquLabel fenquLabel = fenquLabelService.find(id);
			
			if(type!=null){
				fenquLabel.setType(type);
			}
			if(sort != null){
				fenquLabel.setSort(sort);
			}
			if(isActive!=null){
				fenquLabel.setIsActive(isActive);
			}
			if(name != null){
				fenquLabel.setName(name);
			}
			fenquLabelService.update(fenquLabel);
			
			if(areas != null && areas.length > 0) {
				
				String sql = "delete  from fenqu_area where fenqu_label_id = "+id;
				fenquLabelService.deleteBySql(sql);
				
				for(Long areaId : areas){
					FenquArea fenquArea = new FenquArea();
					fenquArea.setAreaId(areaId);
					fenquArea.setFenquLabelId(fenquLabel.getId());
					fenquAreaService.save(fenquArea);
				}

			}
			
			json.setMsg("编辑成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("编辑失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	/**
	 * 新建分区标签
	 * @return
	 */
	@RequestMapping("/create") 
	@ResponseBody
	public Json create(Long[] areas,String name,Integer type,Integer sort,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			FenquLabel fenquLabel = new FenquLabel();
			fenquLabel.setType(type);
			fenquLabel.setCreateTime(new Date());
			fenquLabel.setIsActive(true);
			fenquLabel.setName(name);
			fenquLabel.setSort(sort);
			fenquLabel.setOperator(username);
//			Set<HyArea> area = new HashSet<HyArea>();
			
			fenquLabelService.save(fenquLabel);
			
			if(areas != null && areas.length > 0) {
				for(Long id : areas){
					FenquArea fenquArea = new FenquArea();
					fenquArea.setFenquLabelId(fenquLabel.getId());
					fenquArea.setAreaId(id);
					fenquAreaService.save(fenquArea);
				}
			}
		
			json.setMsg("新建成功");
			json.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("新建失败");
			json.setSuccess(false);
		}
		return json;
	}

}
