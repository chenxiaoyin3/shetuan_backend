package com.hongyu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CardType;
import com.hongyu.service.CardTypeService;

@Controller
@RequestMapping("/admin/generalsettings/cardtype/")
public class CardTypeController {
	@Resource(name = "cardTypeServiceImpl")
	CardTypeService  cardTypeService;
	static class WrapCardType {
		public Pageable pageable;
		public CardType cardtype;
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody CardType cardType) {
		Json j = new Json();
		
		/**
		 * 检查名字是否重复
		 */
		if(cardTypeService.checkName(cardType)){
			j.setSuccess(false);
			j.setMsg("名字重复!");
			return j;
		}	
		
		try{
			cardType.setStatus(true);
			cardTypeService.save(cardType);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			CardType cardType = cardTypeService.find(id);
			cardType.setStatus(false);
			cardTypeService.update(cardType);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="update")
	@ResponseBody
	public Json update(@RequestBody CardType cardType) {
		Json j = new Json();
		/**
		 * 检查名字是否重复
		 */
		if(cardTypeService.checkName(cardType)){
			j.setSuccess(false);
			j.setMsg("名字重复!");
			return j;
		}
		
		try{
			cardTypeService.update(cardType);
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json list(@RequestBody WrapCardType queryParam){
		Pageable pageable = queryParam.pageable;
		CardType cardtype = queryParam.cardtype;
		Json j = new Json();
		Page<CardType> page=cardTypeService.findPage(pageable,cardtype);
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(page);
		return j;
	}	

	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			CardType cardType = cardTypeService.find(id);
			cardType.setStatus(true);
			cardTypeService.update(cardType);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
