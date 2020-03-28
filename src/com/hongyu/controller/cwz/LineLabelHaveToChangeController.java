package com.hongyu.controller.cwz;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HySpecialtyLineLabelService;

@RestController
//上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
//修改 2018-8-15 晚
@RequestMapping("/admin/product/secondline/secondlinelabel/")
public class LineLabelHaveToChangeController {
	
	@Resource(name = "hyLineLabelServiceImpl")
	HyLineLabelService hyLineLabelService;
	
	@Resource(name = "hySpecialtyLineLabelServiceImpl")
	HySpecialtyLineLabelService hySpecialtyLineLabelService;
	
//	@Resource(name = "specialtyServiceImpl")
//	SpecialtyService specialtyService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	//状态status（没有值查出来全部的 0无效 1有效） 标签名称labelName 是筛选条件
	//全用get方法
	@RequestMapping(value = "view") // 1 没问题
	public Json labelview(Integer status, String labelName, Pageable pageable) {
		// 接收前端传过来的json数据
		Json j = new Json();

		List<HyLineLabel> resAll = new ArrayList<>();
		Page<List<Object[]>> pageResults = null;
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			//————————————————如果没传过来 就把数据库所有字段都拿出来 同时名称也是null
			if (status == null) {
				
				Map<String, Object> params = new HashMap<>();
				List<Object[]> list = new ArrayList<>();
				
				if(labelName == null){
					//查询数据库hy_label 所有字段
					String jpql = "select * from hy_linelabel";
					pageResults = hyLineLabelService.findPageBysql(jpql, pageable);
					list = pageResults.getLstObj();
					
				} else{
					
					String jpql = "select * from hy_linelabel h where h.name = :hyLabelName";
					params.put("hyLabelName",labelName);
					pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
					list = pageResults.getLstObj();
				}
				
				List<HyLineLabel> res = new ArrayList<>();
				HyLineLabel hyLabel = null;
				
				for (Object[] arr : list) {
					hyLabel = new HyLineLabel();
					
					//难点在于 把原始的timeStamp换为Date
					java.sql.Timestamp timestamp = (Timestamp) arr[3];
				
					
					hyLabel.setID(Long.valueOf(arr[0].toString()));
					hyLabel.setProductName((String)arr[1]);
					hyLabel.setOperatorName((String)arr[2]);
					hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
					hyLabel.setIsActive((Boolean)arr[4]);
					hyLabel.setIconUrl((String)arr[5]);
					
					res.add(hyLabel);
				}
				resAll=res;
				
			//——————————————————如果status是0的话	
			} else if (status == 0) {
				
				Map<String, Object> params = new HashMap<>();
				List<Object[]> list = new ArrayList<>();
				//有名字
				if(labelName!=null){
					
					String jpql = "select * from hy_linelabel h where h.name = :hyLabelName and h.is_active = :isActive";
					params.put("hyLabelName",labelName);
					params.put("isActive",0);
					pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
					list = pageResults.getLstObj();
					
				//没有名字	
				} else{
					
					String jpql = "select * from hy_linelabel h where h.is_active = :isActive";
					params.put("isActive",0);
					pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
					list = pageResults.getLstObj();
				}
				
				List<HyLineLabel> res = new ArrayList<>();
				HyLineLabel hyLabel = null;
				
				for (Object[] arr : list) {
					hyLabel = new HyLineLabel();
					
					//难点在于 把原始的timeStamp换为Date
					java.sql.Timestamp timestamp = (Timestamp) arr[3];
					
					hyLabel.setID(Long.valueOf(arr[0].toString()));
					hyLabel.setProductName((String)arr[1]);
					hyLabel.setOperatorName((String)arr[2]);
					hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
					hyLabel.setIsActive((Boolean)arr[4]);
					hyLabel.setIconUrl((String)arr[5]);
					
					res.add(hyLabel);
				}
				resAll = res;
				
			//——————————————————如果status是1的话	
			} else if (status == 1) {
				
				Map<String, Object> params = new HashMap<>();
				List<Object[]> list = new ArrayList<>();
				//有名字
				if(labelName!=null){
					
					String jpql = "select * from hy_linelabel h where h.name = :hyLabelName and h.is_active = :isActive";
					params.put("hyLabelName",labelName);
					params.put("isActive",1);
					pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
					list = pageResults.getLstObj();
					
				//没有名字	
				} else{
					
					String jpql = "select * from hy_linelabel h where h.is_active = :isActive";
					params.put("isActive",1);
					pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
					list = pageResults.getLstObj();
				}
				
				List<HyLineLabel> res = new ArrayList<>();
				HyLineLabel hyLabel = null;
				
				for (Object[] arr : list) {
					hyLabel = new HyLineLabel();
					
					//难点在于 把原始的timeStamp换为Date
					java.sql.Timestamp timestamp = (Timestamp) arr[3];
					
					hyLabel.setID(Long.valueOf(arr[0].toString()));
					hyLabel.setProductName((String)arr[1]);
					hyLabel.setOperatorName((String)arr[2]);
					hyLabel.setCreateTime(new java.sql.Date(timestamp.getTime()));
					hyLabel.setIsActive((Boolean)arr[4]);
					hyLabel.setIconUrl((String)arr[5]);
					
					res.add(hyLabel);
				}
				resAll = res;
				//resAll.add(pageable);
				
			} else {
				j.setSuccess(false);
				j.setMsg("进入非法区域");
			}

			map.put("rows", resAll);//黄色的字可以和前台约定，随便起
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));//当前是第几页
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));//每一页有多少条 默认是一页多少
		    map.put("total",Long.valueOf(pageResults.getTotal()));
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());		
		}
		j.setSuccess(true);
		j.setObj(map);
		return j;
	}

	
	@RequestMapping(value = "add") // 2
	public Json labelAdd(String labelName, Boolean status, String iconURL, HttpSession httpSession) {
		//insert into hy_label values (null,'老二','小三','2018-08-15 17:18:23',1,'www.baidu.com')
		// 接收前端传过来的json数据
			Json j = new Json();
			
			String operatorName = (String) httpSession.getAttribute(CommonAttributes.Principal);
			String username = labelName;
			
			try {
				//-------------容错
				if(operatorName == null){
					operatorName = "无名氏";
				}
				if(username == null){
					username = "也是无名氏";
				}
				if(status == null){
					status = false;
				}
				if(iconURL == null){
					iconURL = "www.null.com";
				}
				
//				Map<String, Object> params = new HashMap<>();				
//				Date date = new Date();
//				
//				//由于不知道能不能用sql插入 先放在这里
//				String jpql = "insert into hy_label h values (null,'h.name = :userName1',"
//						+ "'h.operator = :operaterName1',h.create_time = :newDate,h.is_active = :isActive,"
//						+ "'h.icon_url = :iconURL1')";
//				params.put("userName1",username);
//				params.put("operaterName1",operatorName);
//				params.put("newDate",date);
//				params.put("isActive",status);
//				params.put("iconURL1",iconURL);
//				params.put("isActive",1);
//				hyLabelService.findPageBySqlAndParam(jpql,params,new Pageable());
				
				HyLineLabel hyLabel = new HyLineLabel();
				hyLabel.setProductName(username);
				hyLabel.setOperatorName(operatorName);
				hyLabel.setCreateTime(new Date());
//				if(status == 0){
//					hyLabel.setIsActive(false);
//				}else if(status == 1) {
//					hyLabel.setIsActive(true);
//				}
				hyLabel.setIsActive(status);
				hyLabel.setIconUrl(iconURL);
			
				hyLineLabelService.save(hyLabel);
				
			} catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());		
			}
			j.setSuccess(true);
			j.setMsg("已经成功保存");	
			return j;
	}
	
	@RequestMapping(value = "update") // 3
	public Json labelUpdate(Long ID, String labelName, String operatorName, Boolean status, String iconURL, HttpSession httpSession) {
		// 接收前端传过来的json数据
		Json j = new Json();
		try {
			
			//String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			String username = labelName;
			
			//-------------容错
			if(username == null){
				username = "前端没传";
			}
			if(operatorName == null){
				operatorName = "更改无名氏";
			}
			if(status == null){
				status = false;
			}
			if(iconURL == null){
				iconURL = "www.nothingchange.com";
			}
			
			HyLineLabel hyLabel = new HyLineLabel();
			hyLabel.setID(ID);
			hyLabel.setProductName(username);
			hyLabel.setOperatorName(operatorName);
			hyLabel.setCreateTime(new Date());
//			if(status == 0){
//				hyLabel.setIsActive(false);
//			}else if(status == 1) {
//				hyLabel.setIsActive(true);
//			}
			hyLabel.setIsActive(status);
			hyLabel.setIconUrl(iconURL);
		
			hyLineLabelService.update(hyLabel,"ID");
			
			
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());		
		}
		j.setSuccess(true);
		j.setMsg("更新成功");
		return j;
	}
	
	public static class WrapFive{
		private Long lineID;
		private List<Long> LabelID;
		
		public Long getLineID() {
			return lineID;
		}
		public void setLineID(Long lineID) {
			this.lineID = lineID;
		}
		public List<Long> getLabelID() {
			return LabelID;
		}
		public void setLabelID(List<Long> labelID) {
			LabelID = labelID;
		}
		
	}
	

	@SuppressWarnings("unused")
	private Json getResults(String msg, boolean success, Page<HashMap<String, Object>> page) {
		Json json = new Json();
		json.setMsg(msg);
		json.setSuccess(success);
		json.setObj(page);
		return json;
	}
	

}
