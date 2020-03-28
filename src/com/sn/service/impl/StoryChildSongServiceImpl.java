package com.sn.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.sn.entity.StoryChildSong;
import com.sn.service.StoryChildSongService;

@Service("StoryChildSongServiceImpl")
public class StoryChildSongServiceImpl extends BaseServiceImpl<StoryChildSong,Long> implements StoryChildSongService{
	@Override
	@Resource(name="StoryChildSongDaoImpl")
	public void setBaseDao(BaseDao<StoryChildSong,Long> baseDao) {
		super.setBaseDao(baseDao);
	}
}
