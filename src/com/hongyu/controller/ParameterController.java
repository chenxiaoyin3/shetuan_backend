package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.Language;
import com.hongyu.entity.Position;
import com.hongyu.entity.Weight;
import com.hongyu.service.LanguageService;
import com.hongyu.service.PositionService;
import com.hongyu.service.WeightService;

@Controller
@RequestMapping("/admin/parameter")
public class ParameterController {
	@Resource(name = "positionServiceImpl")
	PositionService positionService;

	@Resource(name = "languageServiceImpl")
	LanguageService languageService;

	@Resource(name = "weightServiceImpl")
	WeightService weightService;

	@RequestMapping("/getPositions/view")
	@ResponseBody
	public Json getPositions(@RequestBody Pageable pageable) {
		Json json = new Json();
		try {
			Order order = Order.asc("orders");
			List<Order> list = new ArrayList<>();
			list.add(order);
			pageable.setOrders(list);
			Page<Position> page = positionService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value="/getPosition")
	@ResponseBody
	public Json getPosition(Long id) {
		Json json = new Json();
		try {
			Position position = positionService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(position);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}
	@RequestMapping(value="/addPosition")
	@ResponseBody
	public Json addPosition(@RequestBody Position position) {
		Json json = new Json();
		try {
			Filter filter = new Filter();
			filter.setProperty("positionName");
			filter.setValue(position.getPositionName());
			filter.setOperator(Operator.eq);
			if (positionService.exists(filter)) {
				json.setSuccess(false);
				json.setMsg("岗位已存在");
				return json;
			}
			position.setCreateDate(new Date());
			positionService.save(position);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(position);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping(value="/deletePosition")
	@ResponseBody
	public Json deletePosition(Long id) {
		Json json = new Json();
		try {
			positionService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("删除失败");
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping(value="/editPosition")
	@ResponseBody
	public Json editPosition(@RequestBody Position position) {
		Json json = new Json();
		try {
			Filter filter = new Filter();
			filter.setProperty("positionName");
			filter.setValue(position.getPositionName());
			filter.setOperator(Operator.eq);
			if (positionService.exists(filter)) {
				Position temp=positionService.findByName(position.getPositionName());
				if(temp.getId()!=position.getId()){
				json.setSuccess(false);
				json.setMsg("岗位已存在");
				return json;
				}
			}
			position.setModifyDate(new Date());
			positionService.update(position);
			json.setSuccess(true);
			json.setMsg("编辑成功");
			json.setObj(position);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("/getLanguages/view")
	@ResponseBody
	public Json getLanguages(@RequestBody Pageable pageable) {

		Json json = new Json();
		try {
			Order order = Order.asc("orders");
			List<Order> list = new ArrayList<>();
			list.add(order);
			pageable.setOrders(list);
			Page<Language> page = languageService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
		}
		return json;
	}

	@RequestMapping(value="/getLanguage")
	@ResponseBody
	public Json getLanguage(Long id) {
		Json json = new Json();
		try {
			Language language = languageService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(language);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value="/addLanguage")
	@ResponseBody
	public Json addLanguage(@RequestBody Language language) {
		Json json = new Json();
		try {
			Filter filter = new Filter();
			filter.setProperty("name");
			filter.setValue(language.getName());
			filter.setOperator(Operator.eq);
			if (languageService.exists(filter)) {
				json.setSuccess(false);
				json.setMsg("语种已存在");
				return json;
			}
			language.setCreateDate(new Date());
			languageService.save(language);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(language);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value="/editLanguage")
	@ResponseBody
	public Json editLanguage(@RequestBody Language language) {
		Json json = new Json();
		try {
			Filter filter = new Filter();
			filter.setProperty("name");
			filter.setValue(language.getName());
			filter.setOperator(Operator.eq);
			if (languageService.exists(filter)) {
				Language temp=languageService.findByName(language.getName());
				if(temp.getId()!=language.getId()){
				json.setSuccess(false);
				json.setMsg("语种已存在");
				return json;
				}
			}
			language.setModifyDate(new Date());
			languageService.update(language);
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value="/deleteLanguage")
	@ResponseBody
	public Json deleteLanguage(Long id) {
		Json json = new Json();
		try {
			languageService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("删除失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("/getWeights/view")
	@ResponseBody
	public Json getWeights() {
		Json json = new Json();
		try {
			List<Weight> list = weightService.findAll();
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(list);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping(value="/editWeight")
	@ResponseBody
	public Json editWeight(Weight weight) {
		Json json = new Json();
		try {
			weightService.update(weight);
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
