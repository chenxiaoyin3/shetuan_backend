package com.hongyu.service;

import java.math.BigDecimal;

import com.grain.service.BaseService;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.MhLine;

public interface MhLineService extends BaseService<MhLine, Long>{
	BigDecimal getMhLineLowestPrice(HyLine hyLine) throws Exception;
	BigDecimal getMhGroupLowestPrice(HyGroup group) throws Exception;
}
