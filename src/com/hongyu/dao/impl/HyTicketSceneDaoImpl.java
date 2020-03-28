package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketSceneDao;
import com.hongyu.entity.HyTicketScene;

@Repository("hyTicketSceneDaoImpl")
public class HyTicketSceneDaoImpl extends BaseDaoImpl<HyTicketScene,Long> implements HyTicketSceneDao {

}
