package com.hongyu.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Receiver;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.ReceiverService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;

@Controller
@RequestMapping("/admin/business/vip")
public class VipManagementController {
	@Resource(name="receiverServiceImpl")
	ReceiverService receiverService;
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name="vipServiceImpl")
	VipService vipService;
	
	@Resource(name="viplevelServiceImpl")
	ViplevelService viplevelService;
	
	
	@RequestMapping(value = "list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Long viplevelId,String receiverName,String receiverMobile,@DateTimeFormat(pattern="yyyy-MM-dd") Date birthday)
	{
		Json json=new Json();
		try {
			List<Vip> vipList=new ArrayList<>();
			if(receiverName==null&&receiverMobile==null) {
				if(viplevelId==null) {
					vipList=vipService.findAll();	
				}
				else {
					List<Filter> filters=new ArrayList<Filter>();
					filters.add(Filter.eq("viplevelId", viplevelId));
					vipList=vipService.findList(null,filters,null);
					filters.clear();
				}
			}
			else {
				List<Receiver> receiverList=new ArrayList<>();
				List<Filter> receiverFilter=new ArrayList<Filter>();
				if(receiverName!=null) {
					receiverFilter.add(Filter.like("receiverName", receiverName));
				}
				if(receiverMobile!=null) {
					receiverFilter.add(Filter.like("receiverMobile", receiverMobile));
				}
				receiverList=receiverService.findList(null,receiverFilter,null);
				if(receiverList.isEmpty()) {
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<Receiver>());
				    return json;
				}
				else {
					Set<WechatAccount> wechatAccounts=new HashSet<>();
					for(Receiver receiver:receiverList) {
						WechatAccount wechatAccount=wechatAccountService.find(receiver.getWechat_id());
						wechatAccounts.add(wechatAccount);
					}
					List<Filter> filters=new ArrayList<Filter>();
					filters.add(Filter.in("wechatAccount", wechatAccounts));
					if(viplevelId!=null) {		
						filters.add(Filter.eq("viplevelId", viplevelId));
					}				
					vipList=vipService.findList(null,filters,null);
					filters.clear();
				}
			}
			if(birthday==null) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for(Vip vip:vipList) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("viplevelId", vip.getViplevelId());
					map.put("wechatName", vip.getWechatAccount().getWechatName());
					map.put("wechatPhone", vip.getWechatAccount().getPhone()); //绑定手机号
					map.put("birthday", vip.getBirthday());
					Long wechat_id=vip.getWechatAccount().getId();
					List<Filter> receFilter=new ArrayList<Filter>();
					receFilter.add(Filter.eq("wechat_id", wechat_id));
					receFilter.add(Filter.eq("isVipAddress", true));
					List<Receiver> receList=receiverService.findList(null,receFilter,null);
					if(receList.isEmpty()) {
						map.put("receiverName", null);
						map.put("receiverMobile", null);
						map.put("receiverAddress", null);
					}
					else {
						map.put("receiverName", receList.get(0).getReceiverName());
						map.put("receiverMobile", receList.get(0).getReceiverMobile());
						map.put("receiverAddress", receList.get(0).getReceiverAddress());
					}
					list.add(map);
				}
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Long id1 = (Long) o1.get("viplevelId");
						Long id2 = (Long) o2.get("viplevelId");
						return id1.compareTo(id2); 
					}
				});
				Collections.reverse(list);
				Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);	
			    json.setMsg("查询成功");
	            json.setSuccess(true);
	            json.setObj(page);
			}
			//birthday!=null
			else {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for(Vip vip:vipList) {
					if(vip.getBirthday()!=null) {
						if(getMonthOfDate(vip.getBirthday())==getMonthOfDate(birthday)&&getDayOfDate(vip.getBirthday())==getDayOfDate(birthday)) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("viplevelId", vip.getViplevelId());
							map.put("wechatName", vip.getWechatAccount().getWechatName());
							map.put("wechatPhone", vip.getWechatAccount().getPhone()); //绑定手机号
							map.put("birthday", vip.getBirthday());
							Long wechat_id=vip.getWechatAccount().getId();
							List<Filter> receFilter=new ArrayList<Filter>();
							receFilter.add(Filter.eq("wechat_id", wechat_id));
							receFilter.add(Filter.eq("isVipAddress", true));
							List<Receiver> receList=receiverService.findList(null,receFilter,null);
							if(receList.isEmpty()) {
								map.put("receiverName", null);
								map.put("receiverMobile", null);
								map.put("receiverAddress", null);
							}
							else {
								map.put("receiverName", receList.get(0).getReceiverName());
								map.put("receiverMobile", receList.get(0).getReceiverMobile());
								map.put("receiverAddress", receList.get(0).getReceiverAddress());
							}
							list.add(map);
						}
					}		
				}
				Collections.sort(list, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						Long id1 = (Long) o1.get("viplevelId");
						Long id2 = (Long) o2.get("viplevelId");
						return id1.compareTo(id2); 
					}
				});
				Collections.reverse(list);
				Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);	
			    json.setMsg("查询成功");
	            json.setSuccess(true);
	            json.setObj(page);
			}																
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//获取月份
	public static int getMonthOfDate(Date date) {
	    int m = 0;
	    Calendar cd = Calendar.getInstance();
	    cd.setTime(date);
	    m = cd.get(Calendar.MONTH) + 1;
	    return m;
	}
	//获取日
	public static int getDayOfDate(Date date) {
	    int d = 0;
	    Calendar cd = Calendar.getInstance();
	    cd.setTime(date);
	    d = cd.get(Calendar.DAY_OF_MONTH);
	    return d;
    }
	public static class VipManagement{
		private String viplevelName;
		private Long viplevelId;
		private String wechatName;
		private String wechatPhone;
		private String birthday;
		private String receiverName;
		private String receiverMobile;
		private String receiverAddress;
		public Long getViplevelId() {
			return viplevelId;
		}
		public void setViplevelId(Long viplevelId) {
			this.viplevelId = viplevelId;
		}
		public String getViplevelName() {
			return viplevelName;
		}
		public void setViplevelName(String viplevelName) {
			this.viplevelName = viplevelName;
		}
		public String getWechatName() {
			return wechatName;
		}
		public void setWechatName(String wechatName) {
			this.wechatName = wechatName;
		}
		public String getWechatPhone() {
			return wechatPhone;
		}
		public void setWechatPhone(String wechatPhone) {
			this.wechatPhone = wechatPhone;
		}
		public String getBirthday() {
			return birthday;
		}
		public void setBirthday(String birthday) {
			this.birthday = birthday;
		}
		public String getReceiverName() {
			return receiverName;
		}
		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
		public String getReceiverMobile() {
			return receiverMobile;
		}
		public void setReceiverMobile(String receiverMobile) {
			this.receiverMobile = receiverMobile;
		}
		public String getReceiverAddress() {
			return receiverAddress;
		}
		public void setReceiverAddress(String receiverAddress) {
			this.receiverAddress = receiverAddress;
		}
	}
	/*导出会员列表excel*/
	@RequestMapping(value="list/excel")
	public String vipExcel(Long viplevelId,String receiverName,String receiverMobile,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date birthday,HttpServletRequest request, HttpServletResponse response)
	{
		try {
			List<Vip> vipList=new ArrayList<>();
			if(receiverName==null&&receiverMobile==null) {
				if(viplevelId==null) {
					vipList=vipService.findAll();	
				}
				else {
					List<Filter> filters=new ArrayList<Filter>();
					filters.add(Filter.eq("viplevelId", viplevelId));
					vipList=vipService.findList(null,filters,null);
					filters.clear();
				}
			}
			else {
				List<Receiver> receiverList=new ArrayList<>();
				List<Filter> receiverFilter=new ArrayList<Filter>();
				if(receiverName!=null) {
					receiverFilter.add(Filter.like("receiverName", receiverName));
				}
				if(receiverMobile!=null) {
					receiverFilter.add(Filter.like("receiverMobile", receiverMobile));
				}
				receiverList=receiverService.findList(null,receiverFilter,null);
				if(receiverList.isEmpty()) {
					List<VipManagement> results = new ArrayList<VipManagement>();
					// 生成Excel表标题
					StringBuffer sb2 = new StringBuffer();
					sb2.append("会员管理");
					String fileName = "会员管理报表.xls";  // Excel文件名
					String tableTitle = sb2.toString();   // Excel表标题
					String configFile = "vipManagement.xml"; // 配置文件
					com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
					excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
					return null;
				}
				else {
					Set<WechatAccount> wechatAccounts=new HashSet<>();
					for(Receiver receiver:receiverList) {
						WechatAccount wechatAccount=wechatAccountService.find(receiver.getWechat_id());
						wechatAccounts.add(wechatAccount);
					}
					List<Filter> filters=new ArrayList<Filter>();
					filters.add(Filter.in("wechatAccount", wechatAccounts));
					if(viplevelId!=null) {		
						filters.add(Filter.eq("viplevelId", viplevelId));
					}				
					vipList=vipService.findList(null,filters,null);
					filters.clear();
				}
			}
			
			if(birthday==null) {
				List<VipManagement> results = new ArrayList<VipManagement>();
				for(Vip vip:vipList) {
					VipManagement map = new VipManagement();
					map.setViplevelId(vip.getViplevelId());
					Viplevel viplevel=viplevelService.find(vip.getViplevelId());
					map.setViplevelName(viplevel.getLevelname());
					map.setWechatName(vip.getWechatAccount().getWechatName());
					map.setWechatPhone(vip.getWechatAccount().getPhone());
					if(vip.getBirthday()!=null) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");						
						map.setBirthday(sdf.format(vip.getBirthday()));
					}				
					Long wechat_id=vip.getWechatAccount().getId();
					List<Filter> receFilter=new ArrayList<Filter>();
					receFilter.add(Filter.eq("wechat_id", wechat_id));
					receFilter.add(Filter.eq("isVipAddress", true));
					List<Receiver> receList=receiverService.findList(null,receFilter,null);
					if(receList.size()>0) {
						map.setReceiverName(receList.get(0).getReceiverName());
						map.setReceiverMobile(receList.get(0).getReceiverMobile());
						map.setReceiverAddress(receList.get(0).getReceiverAddress());
					}
					results.add(map);
				}
				Collections.sort(results, new Comparator<VipManagement>() {
					@Override
					public int compare(VipManagement o1,VipManagement o2) {
						Long id1 = (Long) o1.getViplevelId();
						Long id2 = (Long) o2.getViplevelId();
						return id1.compareTo(id2); 
					}
				});
				Collections.reverse(results);
				// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				sb2.append("会员管理");
				String fileName = "会员管理报表.xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "vipManagement.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			}
			//birthday!=null
			else {
				List<VipManagement> results = new ArrayList<VipManagement>();
				for(Vip vip:vipList) {
					if(vip.getBirthday()!=null) {
						if(getMonthOfDate(vip.getBirthday())==getMonthOfDate(birthday)&&getDayOfDate(vip.getBirthday())==getDayOfDate(birthday)) {
							VipManagement map = new VipManagement();
							map.setViplevelId(vip.getViplevelId());
							Viplevel viplevel=viplevelService.find(vip.getViplevelId());
							map.setViplevelName(viplevel.getLevelname());
							map.setWechatName(vip.getWechatAccount().getWechatName());
							map.setWechatPhone(vip.getWechatAccount().getPhone());
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");						
							map.setBirthday(sdf.format(vip.getBirthday()));
							Long wechat_id=vip.getWechatAccount().getId();
							List<Filter> receFilter=new ArrayList<Filter>();
							receFilter.add(Filter.eq("wechat_id", wechat_id));
							receFilter.add(Filter.eq("isVipAddress", true));
							List<Receiver> receList=receiverService.findList(null,receFilter,null);
							if(receList.size()>0) {
								map.setReceiverName(receList.get(0).getReceiverName());
								map.setReceiverMobile(receList.get(0).getReceiverMobile());
								map.setReceiverAddress(receList.get(0).getReceiverAddress());
							}
							results.add(map);
						}
					}											
				}
				Collections.sort(results, new Comparator<VipManagement>() {
					@Override
					public int compare(VipManagement o1,VipManagement o2) {
						Long id1 = (Long) o1.getViplevelId();
						Long id2 = (Long) o2.getViplevelId();
						return id1.compareTo(id2); 
					}
				});
				Collections.reverse(results);
				// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				sb2.append("会员管理");
				String fileName = "会员管理报表.xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "vipManagement.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);						
			}
	
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
