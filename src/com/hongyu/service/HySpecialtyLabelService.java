package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyLabel;
import com.hongyu.entity.HySpecialtyLabel;
import com.hongyu.entity.Specialty;

public interface HySpecialtyLabelService extends BaseService<HySpecialtyLabel, Long>{
	Boolean isMarked(HyLabel hyLabel,Specialty specialty);

}
