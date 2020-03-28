package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.LanguageDao;
import com.hongyu.entity.Language;
import com.hongyu.service.LanguageService;

@Service("languageServiceImpl")
public class LanguageServiceImpl extends BaseServiceImpl<Language,Long> implements LanguageService{

	@Resource(name="languageDaoImpl")
	LanguageDao languageDao;
	@Override
	public Language findByName(String name) {
		// TODO Auto-generated method stub
		return languageDao.findByName(name);
	}

	

	@Override
	@Resource(name="languageDaoImpl")
	public void setBaseDao(BaseDao<Language, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
	
}
