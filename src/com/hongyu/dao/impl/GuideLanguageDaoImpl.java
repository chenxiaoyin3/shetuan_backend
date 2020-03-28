package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.GuideLanguageDao;
import com.hongyu.entity.GuideLanguage;

@Repository("guideLanguageDaoImpl")
public class GuideLanguageDaoImpl extends BaseDaoImpl<GuideLanguage, Long> implements GuideLanguageDao {

}
