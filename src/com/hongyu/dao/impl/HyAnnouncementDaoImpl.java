package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyAnnouncementDao;
import com.hongyu.entity.HyAnnouncement;

@Repository("hyAnnouncementDaoImpl")
public class HyAnnouncementDaoImpl extends BaseDaoImpl<HyAnnouncement, Long> implements HyAnnouncementDao{

}
