package com.shetuan.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.grain.service.BaseService;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.shetuan.entity.People;
import com.shetuan.service.PeopleManagementService;
import com.shetuan.service.PeopleService;

@Service("PeopleManagementServiceImpl")
public class PeopleManagementServiceImpl implements PeopleManagementService {
	@Resource(name = "PeopleServiceImpl")
	PeopleService peopleService;

	@Override
	public Json list(Pageable pageable, String peopleName, boolean state) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<Filter>();
			if (peopleName != null) {
				filter.add(Filter.like("peopleName", peopleName));
			}
			filter.add(Filter.eq("state", state));
			pageable.setFilters(filter);
			Page<People> page = peopleService.findPage(pageable);
			for (People tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("peopleName", tmp.getPeopleName());
				m.put("logoUrl", tmp.getLogoUrl());
				m.put("description", tmp.getDescription());
				m.put("state", tmp.getState());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("result", result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}

		return j;
	}

	@Override
	public Json detailById(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("获取成功");
		j.setObj(peopleService.find(id));
		return j;
	}

	@Override
	public Json add(@RequestBody People people) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			if (people.getPeopleName() != null) {
				people.setState(true);
				peopleService.save(people);
				j.setSuccess(true);
				j.setMsg("新建成功");
				j.setObj(null);
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("创建失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json editById(@RequestBody People people) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			People old = peopleService.find(people.getId());
			if (old != null) {
				old.setPeopleName(people.getPeopleName());
				old.setState(old.getState());
				old.setLogoUrl(people.getLogoUrl());
				old.setDescription(people.getDescription());
			}
			peopleService.update(old);
			j.setSuccess(true);
			j.setMsg("修改成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("修改失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json invalidById(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			People people = peopleService.find(id);
			people.setState(false);
			peopleService.update(people);
			j.setSuccess(true);
			j.setMsg("取消成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("取消失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
	
	@Override
	public Json validById(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			People people = peopleService.find(id);
			people.setState(true);
			peopleService.update(people);
			j.setSuccess(true);
			j.setMsg("恢复成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("恢复失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json getAllPeople() {
		// TODO Auto-generated method stub
		Json j=new Json();
		try {
			List<HashMap<String,Object>> responseList = new ArrayList<>();
			List<People> allList = new ArrayList<People>();
			List<Filter> filter=new ArrayList<>();
			filter.add(Filter.eq("state", true));
			allList = peopleService.findList(null, filter, null);
			for (People p : allList) {
				HashMap<String,Object> m=new HashMap<>();
				m.put("id", p.getId());
				m.put("peopleName", p.getPeopleName());
				m.put("description", p.getDescription());
				responseList.add(m);
			}
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(responseList);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

}
