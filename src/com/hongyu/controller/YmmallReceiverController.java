package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.BusinessStore;
import com.hongyu.entity.Receiver;
import com.hongyu.entity.Store;
import com.hongyu.entity.Vip;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.ReceiverService;
import com.hongyu.service.StoreService;
import com.hongyu.service.VipService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WechatAccountService;

import oracle.net.aso.r;

@Controller
@RequestMapping("/ymmall/receiver/")
public class YmmallReceiverController {
	@Resource(name = "receiverServiceImpl")
	ReceiverService receiverService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "businessStoreServiceImpl")
	BusinessStoreService businessStoreService;

	@Resource(name="vipServiceImpl")
	VipService vipService;
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@RequestMapping("we_business_address")
	@ResponseBody
	public Json weBusinessAddress(Long webusiness_id) {
		Json json = new Json();
		try {
			System.out.println("webusiness_id" + webusiness_id);
			// Long webusiness_id=(Long)session.getAttribute("webusiness_id");
			WeBusiness weBusiness = weBusinessService.find(webusiness_id);
			if (weBusiness != null) {
				String receiverName = weBusiness.getName();
				String receiverMobile = weBusiness.getMobile();
				String receiverAddress = null;
				int type = weBusiness.getType();
				if (type == 0) {
					Store store = storeService.find(weBusiness.getStoreId());
					if (store != null) {
						receiverAddress = store.getAddress();
					} else {
						receiverAddress = "";
					}
				} else if (type == 1) {
					BusinessStore businessStore = businessStoreService.find(weBusiness.getStoreId());
					if (businessStore != null) {
						receiverAddress = businessStore.getAddress();
					} else {
						receiverAddress = "";
					}
				} else if (type == 2) {
					receiverAddress = weBusiness.getAddress();
				}
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("receiverName", receiverName);
				hm.put("receiverMobile", receiverMobile);
				hm.put("receiverAddress", receiverAddress);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(hm);
			} else {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			e.printStackTrace();
			// TODO Auto-generated catch block
		}
		return json;
	}

	@RequestMapping("default")
	@ResponseBody
	public Json defaultAddress(Long wechat_id) {
		Json json = new Json();
		try {
			// Long wechat_id=(Long)session.getAttribute("wechat_id");
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("wechat_id", wechat_id));
			filters.add(Filter.eq("isDefaultReceiverAddress", true));
			List<Receiver> receivers = receiverService.findList(null, filters, null);
			if (receivers.size() > 0) {
				Receiver receiver = receivers.get(0);
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(receiver);
			} else {
				json.setSuccess(false);
				json.setMsg("查找失败，无默认地址");

			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;

	}

	@RequestMapping("list")
	@ResponseBody
	public Json list(Long wechat_id) {
		Json json = new Json();
		try {
			// Long wechat_id=(Long)session.getAttribute("wechat_id");
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("wechat_id", wechat_id));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("isDefaultReceiverAddress"));
			List<Receiver> receivers = receiverService.findList(null, filters, orders);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(receivers);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;

	}

	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody Receiver receiver) {
		Json json = new Json();
		try {
			// Long wechat_id=(Long)session.getAttribute("wechat_id");
			Long wechat_id = receiver.getWechat_id();
			if (wechat_id == null) {
				json.setSuccess(false);
				json.setMsg("请输入wechat_id");
			} else {
				if (receiver.getIsDefaultReceiverAddress() != null && receiver.getIsDefaultReceiverAddress() == true) {
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("wechat_id", receiver.getWechat_id()));
					List<Receiver> receivers = receiverService.findList(null, filters, null);
					for (Receiver tmp : receivers) {
						tmp.setIsDefaultReceiverAddress(false);
						receiverService.update(tmp);
					}
				}
				receiverService.save(receiver);
				json.setSuccess(true);
				json.setMsg("添加成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("edit")
	@ResponseBody
	public Json edit(@RequestBody Receiver receiver) {
		Json json = new Json();
		try {
			Long wechat_id = receiver.getWechat_id();
			if (receiver.getIsDefaultReceiverAddress()) {
				List<Filter> filters = new ArrayList<>();
				if (wechat_id != null) {
					filters.add(Filter.eq("wechat_id", wechat_id));
					List<Receiver> receivers = receiverService.findList(null, filters, null);
					for (Receiver tmp : receivers) {
						tmp.setIsDefaultReceiverAddress(false);
						receiverService.update(tmp);
					}
				}
			}
			receiverService.update(receiver, "wechat_id");
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
	@RequestMapping("delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			receiverService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value = "vipAddress/view")
	@ResponseBody
	public Json vipAddressview(Long wechat_id)
	{
		Json json=new Json();
		try {
			WechatAccount wechatAccount=wechatAccountService.find(wechat_id);
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("wechatAccount", wechatAccount));
			List<Vip> vipList=vipService.findList(null,filters,null);
			filters.clear();
			Vip vip=vipList.get(0);
			map.put("birthday", vip.getBirthday());
			filters.add(Filter.eq("wechat_id", wechat_id));
			filters.add(Filter.eq("isVipAddress", true));
			List<Receiver> receiverList=receiverService.findList(null,filters,null);
			if(receiverList.isEmpty()) {
				map.put("id", null);
				map.put("receiverName", null);
				map.put("receiverAddress", null);
				map.put("receiverMobile", null);
			}
			else {
				map.put("id", receiverList.get(0).getId());
				map.put("receiverName", receiverList.get(0).getReceiverName());
				map.put("receiverAddress", receiverList.get(0).getReceiverAddress());
				map.put("receiverMobile", receiverList.get(0).getReceiverMobile());
			}
			json.setMsg("查询成功");
            json.setSuccess(true);
            json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value = "birthday/add")
	@ResponseBody
	public Json birthdayAdd(Long wechat_id,@DateTimeFormat(pattern="yyyy-MM-dd") Date birthday)
	{
		Json json=new Json();
		try {
			WechatAccount wechatAccount=wechatAccountService.find(wechat_id);
			if(wechatAccount.getIsVip()==true) {
				List<Filter> filters=new ArrayList<Filter>();
				filters.add(Filter.eq("wechatAccount", wechatAccount));
				List<Vip> vipList=vipService.findList(null,filters,null);
				Vip vip=vipList.get(0);
				vip.setBirthday(birthday);
				vipService.update(vip);
				json.setMsg("设置成功");
	            json.setSuccess(true);
			}	
			else {
				json.setSuccess(false);
				json.setMsg("您还不是会员，不能进行设置！");
			}
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}

	@RequestMapping(value = "vipAddress/add")
	@ResponseBody
	public Json addSetting(Long wechat_id,String receiverName,String receiverAddress,String receiverMobile)
	{
		Json json=new Json();
		try {
			WechatAccount wechatAccount=wechatAccountService.find(wechat_id);
			if(wechatAccount.getIsVip()==true) {
				Receiver receiver=new Receiver();
				receiver.setReceiverAddress(receiverAddress);
				receiver.setReceiverMobile(receiverMobile);
				receiver.setReceiverName(receiverName);
				receiver.setIsDefaultReceiverAddress(false);
				receiver.setIsVipAddress(true);
				receiver.setWechat_id(wechat_id);
				receiverService.save(receiver);
				json.setMsg("设置成功");
	            json.setSuccess(true);
			}
			else {
				json.setSuccess(false);
				json.setMsg("您还不是会员，不能进行设置！");
			}
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value = "vipAddress/edit")
	@ResponseBody
	public Json addSetting(Long wechat_id,Long id,String receiverName,String receiverAddress,String receiverMobile)
	{
		Json json=new Json();
		try {
			Receiver receiver=new Receiver();
			receiver.setId(id);
			receiver.setReceiverAddress(receiverAddress);
			receiver.setReceiverMobile(receiverMobile);
			receiver.setReceiverName(receiverName);
			receiver.setIsDefaultReceiverAddress(false);
			receiver.setIsVipAddress(true);
			receiver.setWechat_id(wechat_id);
			receiverService.update(receiver);
			json.setMsg("修改成功");
            json.setSuccess(true);								
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
