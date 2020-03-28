package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Area;
import com.hongyu.entity.HyArea;
import com.hongyu.service.HyAreaService;
@Controller
@RequestMapping("/admin/generalsettings/area/")
public class HyAreaController {
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService  hyAreaService;

	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyArea hyArea, Long pID) {
		Json j = new Json();
		 if (pID != null) {
		    	HyArea parent = this.hyAreaService.find(pID);
		        if (parent == null)
		        {
		          j.setSuccess(false);
		          j.setMsg("上级区域不存在");
		          j.setObj(null);
		          return j;
		        } else {
		        	hyArea.setHyArea(parent);
		        }
		    }
		try{
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("name", hyArea.getName()));
			List<HyArea> areaList=hyAreaService.findList(null,filters,null);
			if(areaList.size()>0) {
				j.setSuccess(false);
	        	j.setMsg("设置失败,该地区已存在");
	        	j.setObj(null);
	        	return j;
			}
			hyAreaService.save(hyArea);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="cancel", method = RequestMethod.GET)
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			HyArea hyArea = hyAreaService.find(id);
			hyArea.setStatus(false);
			hyAreaService.update(hyArea);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="restore", method = RequestMethod.GET)
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			HyArea hyArea = hyAreaService.find(id);
			hyArea.setStatus(true);
			hyAreaService.update(hyArea);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="update", method = RequestMethod.POST)
	@ResponseBody
	public Json update(HyArea hyArea, Long pID) {
		Json j = new Json();
		try{	
			 if (pID != null) {
			    HyArea parent = this.hyAreaService.find(pID);
			    if (parent == null)
			    {
			        j.setSuccess(false);
			        j.setMsg("上级区域不存在");
			        j.setObj(null);
			        return j;
			    } 
			    else {
			        hyArea.setHyArea(parent);
			    }
			 }
			 List<Filter> filters=new ArrayList<Filter>();
			 filters.add(Filter.eq("name", hyArea.getName()));
			 List<HyArea> areaList=hyAreaService.findList(null,filters,null);
			 //判断重名,要排除自己本身
			 if(areaList.size()>0 && !areaList.get(0).getId().equals(hyArea.getId())) {
				 j.setSuccess(false);
		         j.setMsg("设置失败,该地区已存在");
		         j.setObj(null);
		         return j;
			 }
			hyAreaService.update(hyArea, "status");
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json list(Pageable pageable, Long pID, HyArea queryParam){
		Json j = new Json();
		int pg = pageable.getPage();
		int rows = pageable.getRows();
		Long total = 0L;
		List<Area> areas = new ArrayList();
		HashMap<String, Object> obj = new HashMap();
		obj.put("pageNumber", pg);
		obj.put("pageSize", rows);
		if(StringUtils.isBlank(queryParam.getName()) && queryParam.getStatus() == null) {
			HyArea parent = hyAreaService.find(pID);
			List<HyArea> temp= new ArrayList<HyArea>(parent.getHyAreas());
			List<HyArea> l = temp.subList((pg-1)*rows, pg*rows>temp.size()?temp.size():pg*rows);
			total = (long) temp.size();
			copyAreaInfo(l, areas);
		}else{
			String treepath = ","+pID+",";
			queryParam.setTreePath(treepath);
			Page<HyArea> page = hyAreaService.findPage(pageable, queryParam);
			List<HyArea> result = page.getRows();
			total = page.getTotal();
			copyAreaInfo(result, areas);
		}
		obj.put("total", total);
		obj.put("rows",areas);
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(obj);
		return j;	
	}
	@RequestMapping(value="combotextList/view", method = RequestMethod.GET)
	@ResponseBody
	public Json combotextList(Long id){
		HyArea parent = hyAreaService.find(id);
		List<HashMap<String, Object>> obj = new ArrayList();
		HashMap<String, Object> hm = new HashMap();
		hm.put("label", parent.getName());
		hm.put("value", parent.getId().toString());
		hm.put("key", parent.getId().toString());
		obj.add(hm);
		if(parent.getHyAreas().size() > 0){
			 for (HyArea child : parent.getHyAreas())
		      {
		        HashMap<String, Object> inner = new HashMap();
				inner.put("label", child.getName());
				inner.put("value", child.getId().toString());
				inner.put("key", child.getId().toString());
				if(child.getHyAreas().size()>0){
					List<HashMap<String, Object>> childrenObj = new ArrayList();
					for(HyArea innerChild : child.getHyAreas()){
						HashMap<String, Object> innerHm = new HashMap();
						innerHm.put("label", innerChild.getName());
						innerHm.put("value", innerChild.getId().toString());
						innerHm.put("key", innerChild.getId().toString());
						childrenObj.add(innerHm);
					}
					inner.put("children", childrenObj);
				}
				obj.add(inner);
		      }
		}
		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(obj);
		return j;
	}
	/**
	 * 查找所有的省、市
	 * @param id 1：省 2：市 -待完成
	 * @return 
	 */
	@RequestMapping(value="level/view", method = RequestMethod.GET)
	@ResponseBody
	public Json level(Long id){
		return null;
	}
	private static void copyAreaInfo(List<HyArea> children, List<Area> areas){
		for(HyArea child : children){
			Area area = new Area();
			BeanUtils.copyProperties(child, area);
			HyArea p = child.getHyArea();
			if(p == null){
				area.setPid(null);
			}else{
				area.setPid(p.getId());
			}
			if(child.isLeaf()){
				area.setChildren(null);
				area.setIsleaf(true);
			}else{
				Set<HyArea> hy = new HashSet();
				area.setChildren(hy);
				area.setIsleaf(false);
			}
			areas.add(area);
		}
	}
}
