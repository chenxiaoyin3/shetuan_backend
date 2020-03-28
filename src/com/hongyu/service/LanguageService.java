package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.Language;

public interface LanguageService extends BaseService<Language, Long> {
	public Language findByName(String name);
}
