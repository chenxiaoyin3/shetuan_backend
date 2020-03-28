package com.sn.dao.impl;

import org.springframework.stereotype.Repository;
import com.grain.dao.impl.BaseDaoImpl;
import com.sn.dao.StoryChildSongDao;
import com.sn.entity.StoryChildSong;

@Repository("StoryChildSongDaoImpl")
public class StoryChildSongDaoImpl extends BaseDaoImpl<StoryChildSong, Long> implements StoryChildSongDao {

}
