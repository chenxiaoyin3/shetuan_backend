package com.hongyu.dao;

import com.grain.dao.BaseDao;
import com.hongyu.entity.Language;

public interface LanguageDao extends BaseDao<Language, Long> {
	public Language findByName(String name);
}
