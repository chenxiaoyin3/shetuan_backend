package com.hongyu.controller.cwz;

import java.math.BigInteger;
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
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.entity.HySpecialtyLineLabel;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HySpecialtyLineLabelService;

@RestController
//上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
//修改 2018-8-15 晚
@RequestMapping("/admin/product/line/linelabel/")
public class LineLabelController {
	
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
	
	
	
	@RequestMapping(value = "check") // 4 没问题
	public Json labelCheck(Long lineID, Pageable pageable) {
		// 接收前端传过来的json数据
		Json j = new Json();
		List<Wrap> resAll = new ArrayList<>();
		Map<String,Object> map = new HashMap<String,Object>();
		pageable.setPage(1);
		pageable.setRows(10000);
		
		try {
			
			//根据spetialtyID去找labelID，去label表里读出所有数据
			//select b.name,b.is_active from hy_label b,hy_specialty_label s where b.ID = s.label_id and s.specialty_id = 77
			Map<String, Object> params = new HashMap<>();
			List<Object[]> list = new ArrayList<>();
			
//			String jpql = "select b.ID,b.name,s.is_marked from hy_label b,hy_specialty_label s where b.ID = s.label_id "
//					+ "and s.specialty_id = :specialtyId and b.is_active = 1";
			
			//select hy_label.*,hy_specialty_label.is_marked is_marked from hy_label left join hy_specialty_label on hy_label.ID=hy_specialty_label.label_id and hy_specialty_label.specialty_id=123 and hy_specialty_label.is_marked=1;
//			String jpql = "select b.ID,b.name from hy_label b where b.is_active = :isActiveNow";
			
//			String jpql = "select hy_label.ID,hy_label.name,hy_specialty_label.is_marked is_marked "
//					+ "from hy_label left join hy_specialty_label on hy_label.ID=hy_specialty_label.label_id "
//					+ "where hy_specialty_label.specialty_id=:specialtyId and hy_label.is_active=1";
			
			String jpql = "select a.ID,a.name,b.is_marked is_marked "
			+"from (select * from hy_linelabel where is_active=1) as a left join" 
			+"(select * from hy_specialty_linelabel where line_id=:lineId) as b on a.ID = b.label_id";
			
			params.put("lineId",lineID);
//			params.put("isActiveNow",1);
			Page<List<Object[]>> pageResults = hyLineLabelService.findPageBySqlAndParam(jpql,params,pageable);
			list = pageResults.getLstObj();
			
			
			List<Wrap> res = new ArrayList<>();
			Wrap wrap = null;
			
			for (Object[] arr : list) {
				wrap = new Wrap();
				
				BigInteger a = (BigInteger)arr[0];
				wrap.setID(a.longValue());
				wrap.setProductName((String)arr[1]);
				if(arr[2] == null){
					wrap.setIsMarked(false);
				} else{
					wrap.setIsMarked((Boolean)arr[2]);
				}
				
				res.add(wrap);
			}
			resAll = res;
			
			map.put("rows", resAll);//黄色的字可以和前台约定，随便起
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));//当前是第几页
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));//每一页有多少条 默认是一页多少
		    map.put("total",Long.valueOf(pageResults.getTotal()));
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());		
		}
		j.setSuccess(true);
		//不要分页
		j.setObj(resAll);
		j.setMsg("成功");
		return j;
	}
	
	public static class Wrap{
		private Long labelID;//ID 也一定是Long
		private String productName;//name
		private Boolean isMarked;//is_marked
		public Long getID() {
			return labelID;
		}
		public void setID(Long iD) {
			labelID = iD;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public Boolean getIsMarked() {
			return isMarked;
		}
		public void setIsMarked(Boolean isMarked) {
			this.isMarked = isMarked;
		}
		
		
	}
	
	
	@RequestMapping(value = "check/update") // 5
	public Json labelCheckUpdate(WrapFive wrapFive, Pageable pageable, HttpSession httpSession) {
		// 接收前端传过来的json数据
		Json j = new Json();
		
		try {
			
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			
			Long lineID = wrapFive.getLineID();
			//发过来的是选中了的ID
			List<Long> LabelIDs = wrapFive.getLabelID();
			
			//用specialtyId找值如果List 找得到就更新，找不到就存进去 
			//有问题 还是用eq
			List<Filter> adminFilter = new ArrayList<Filter>();
			HyLine hyLine = hyLineService.find(lineID);
			adminFilter.add(Filter.eq("hyLine", hyLine));//specialty是specialty_id
			
			//找出来是所有的
			List<HySpecialtyLineLabel> specialtyLabel = hySpecialtyLineLabelService.findList(null, adminFilter, null);
			
			//如果之前有值 是非空的
			if(!specialtyLabel.isEmpty()){
				
				//问题在于，第四个接口展示了很多数据，第五个接口点击更改的时候，hySpecialtyLabel里的数据不够
				//要更改，就要在这个接口点击提交的时候，把新数据加进去 原来的数据也不用删，因为挨个判断了
				//select * from hy_specialty_label where specialtyID = specialtyID 拿出labelID看看是否和传来的一样
				//先遍历LabelIDs 因为这个多
				for(Long tempID1 : LabelIDs){ 
					boolean flag = false;//默认是数据库中没有
					
					for(HySpecialtyLineLabel tempData : specialtyLabel){
						//如果有一个ID相等
						if(tempData.getHyLabel().getID() == tempID1){
							flag = true;//找到匹配的 不需要做操作
						} 
					}
					
					if(flag == false){//如果判断半天还是没有 就加进去
						HySpecialtyLineLabel hySpecialtyLabel = new HySpecialtyLineLabel();
						hySpecialtyLabel.setCreateTime(new Date());
						//~~~~~~~~做一件事，把数据库里的实体拿出来便于更新
						//find
						HyLineLabel hyLabel = hyLineLabelService.find(tempID1);
						hySpecialtyLabel.setHyLabel(hyLabel);
						hySpecialtyLabel.setHyLine(hyLine);
						hySpecialtyLabel.setIsMarked(true);
						hySpecialtyLabel.setOperator(username);
						
						hySpecialtyLineLabelService.save(hySpecialtyLabel);
					}
					
				}
				
				
				//更新回去 更新关系表就行 不用更新Label表
				for(HySpecialtyLineLabel temp : specialtyLabel){
					
					//进来先设置为未标记 如果找到再设置已标记
					temp.setIsMarked(false);
					for(Long tempID : LabelIDs){
						//如果找到匹配的ID
						if(temp.getHyLabel().getID().equals(tempID)){
							temp.setIsMarked(true);
						} 
					}
					
					//更新回数据库
					hySpecialtyLineLabelService.update(temp);
				}
				
				
			} else {
				//向数据库里面添加 那个List是空值 这个接口想了一下 没问题
				//有多条label 就要一个循环
				for(Long temp: LabelIDs){
					HySpecialtyLineLabel hySpecialtyLabel = new HySpecialtyLineLabel();
					hySpecialtyLabel.setCreateTime(new Date());
					//~~~~~~~~做一件事，把数据库里的实体拿出来便于更新
					//find
					HyLineLabel hyLabel = hyLineLabelService.find(temp);
					hySpecialtyLabel.setHyLabel(hyLabel);
					hySpecialtyLabel.setHyLine(hyLine);
					hySpecialtyLabel.setIsMarked(true);
					hySpecialtyLabel.setOperator(username);
					
					hySpecialtyLineLabelService.save(hySpecialtyLabel);
				}
			}
			
			
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
