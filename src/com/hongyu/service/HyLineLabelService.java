package com.hongyu.service;

import java.util.List;

import com.grain.service.BaseService;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineLabel;

public interface HyLineLabelService extends BaseService<HyLineLabel, Long>{
	public List<HyLineLabel> getLabelsByLine(HyLine hyLine);

}
