package com.hongyu.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.dao.GroupPlaceholderDao;
import com.hongyu.dao.GroupSendGuideDao;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.Store;
import com.hongyu.service.GroupPlaceholderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.StoreService;

@Service("GroupPlaceholderServiceImpl")
public class GroupPlaceholderServiceImpl extends BaseServiceImpl< GroupPlaceholder, Long> implements GroupPlaceholderService {

	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	@Override
	public Json addPlaceHolder(Long groupId, Integer number, HttpSession session) throws Exception{
		// TODO Auto-generated method stub
		Json json=new Json();
		HyGroup hyGroup = hyGroupService.find(groupId);
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		Store store = storeService.findStore(hyAdmin);
		if (store == null) {
			json.setSuccess(false);
			json.setMsg("门店不存在");
		} else {
			synchronized (hyGroup) {
				if (hyGroup.getStock() < number) {
					json.setSuccess(false);
					json.setMsg("余位不足");
				} else {
					hyGroup.setStock(hyGroup.getStock() - number);
					hyGroup.setOccupyNumber(hyGroup.getOccupyNumber() + number);
					hyGroupService.update(hyGroup);
					GroupPlaceholder groupPlaceholder = new GroupPlaceholder();
					groupPlaceholder.setGroup(hyGroup);
					groupPlaceholder.setCreator(hyAdmin);
					groupPlaceholder.setStore_id(store.getId());
					groupPlaceholder.setNumber(number);
					groupPlaceholder.setSignup_phone(hyAdmin.getMobile());
					groupPlaceholder.setStatus(false);
					groupPlaceholder.setStore_type(false);
					this.save(groupPlaceholder);
					json.setSuccess(true);
					json.setMsg("占位成功");
				}
			}
		}
		return json;
		
	}

	@Override
	public Json deletePlaceHolder(Long id) throws Exception {
		// TODO Auto-generated method stub
		Json json=new Json();
		GroupPlaceholder groupPlaceholder=this.find(id);
		HyGroup hyGroup=hyGroupService.find(groupPlaceholder.getGroup().getId());
		synchronized (hyGroup) {
			hyGroup.setStock(hyGroup.getStock()+groupPlaceholder.getNumber());
			hyGroup.setOccupyNumber(hyGroup.getOccupyNumber()+groupPlaceholder.getNumber());
			hyGroupService.update(hyGroup);
			groupPlaceholder.setStatus(true);
			this.update(groupPlaceholder);
		}
		json.setSuccess(true);
		json.setMsg("清除成功");
		return json;
	}

	@Resource(name = "GroupPlaceholderDaoImpl")
	public void setBaseDao(GroupPlaceholderDao dao) {
		super.setBaseDao(dao);
	}

}
