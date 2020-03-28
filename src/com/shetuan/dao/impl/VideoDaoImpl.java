package com.shetuan.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.shetuan.dao.VideoDao;
import com.shetuan.entity.Video;

@Repository("VideoDaoImpl")
public class VideoDaoImpl extends BaseDaoImpl<Video,Long> implements VideoDao{

}
