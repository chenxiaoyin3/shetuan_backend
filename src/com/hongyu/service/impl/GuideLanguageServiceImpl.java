package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.GuideLanguage;
import com.hongyu.service.GuideLanguageService;

@Service("guideLanguageServiceImpl")
public class GuideLanguageServiceImpl extends BaseServiceImpl<GuideLanguage, Long> implements GuideLanguageService {

	@Override
	@Resource(name="guideLanguageDaoImpl")
	public void setBaseDao(BaseDao<GuideLanguage, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
