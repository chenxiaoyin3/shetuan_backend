package com.hongyu.service.impl;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.TouristInfo;
import com.hongyu.service.TouristInfoService;

@Service("touristInfoServiceImpl")
public class TouristInfoServiceImpl extends BaseServiceImpl<TouristInfo,Long >implements TouristInfoService {

}
