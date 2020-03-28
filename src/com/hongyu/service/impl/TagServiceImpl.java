package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.TagDao;
import com.hongyu.entity.Tag;
import com.hongyu.service.TagService;

@Service("tagServiceImpl")
public class TagServiceImpl extends BaseServiceImpl<Tag, Long> implements TagService {
	@Resource(name = "tagDaoImpl")
	TagDao dao;

	@Resource(name = "tagDaoImpl")
	public void setBaseDao(TagDao dao) {
		super.setBaseDao(dao);
	}
}