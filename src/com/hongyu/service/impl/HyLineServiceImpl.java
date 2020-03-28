package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HyLineDao;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.util.Constants.AuditStatus;
@Service(value = "hyLineServiceImpl")
public class HyLineServiceImpl extends BaseServiceImpl<HyLine, Long> implements HyLineService {
	@Resource(name = "hyLineDaoImpl")
	HyLineDao dao;
	
	@Resource(name = "hyLineDaoImpl")
	public void setBaseDao(HyLineDao dao){
		super.setBaseDao(dao);		
	}
	
	@Resource(name="hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	
	@Override
	public BigDecimal getLineLowestPrice(HyLine hyLine) {
		BigDecimal lowestPrice=BigDecimal.ZERO;
		List<Filter> filters=new ArrayList<Filter>();
		filters.add(Filter.eq("groupState", GroupStateEnum.daichutuan)); //筛选出团状态为待出团
		filters.add(Filter.eq("auditStatus", AuditStatus.pass)); //审核已通过的团
		filters.add(Filter.eq("line", hyLine));
		List<HyGroup> hyGroups=hyGroupService.findList(null,filters,null);
		if(!hyGroups.isEmpty()) {
			lowestPrice=hyGroups.get(0).getLowestPrice();
			for(HyGroup hyGroup:hyGroups) {
				lowestPrice=hyGroup.getLowestPrice().compareTo(lowestPrice)<0?hyGroup.getLowestPrice():lowestPrice;
			}
		}
		return lowestPrice;
	}

	@Override
	public Object[] getMemoDetail(HyLine hyLine){
		Object[] objs = new Object[15];
		if(hyLine.getMemo()!=null) {
			String memo = hyLine.getMemo();
			String[] values = memo.split("%%");
			if(values == null || values.length < 15){
				return objs;
			}



			objs[0] = (values[0]=="1"?true:false);
			objs[1] = (values[1]=="1"?true:false);
			objs[2] = Integer.valueOf(values[2]);
			objs[3] = Integer.valueOf(values[3]);
			objs[4] = Integer.valueOf(values[4]);
			objs[5] = (values[5]=="1"?true:false);
			objs[6] = (values[6]=="1"?true:false);
			objs[7] = (values[7]=="1"?true:false);
			objs[8] = (values[8]=="1"?true:false);
			objs[9] = (values[9]=="1"?true:false);
			objs[10] = (values[10]=="1"?true:false);
			objs[11] = (values[11]=="1"?true:false);
			objs[12] = values[12];
			objs[13] = values[13];
			objs[14] = values[14];

			return objs;
		}
		else {
			return objs;
		}

		
	}
}
