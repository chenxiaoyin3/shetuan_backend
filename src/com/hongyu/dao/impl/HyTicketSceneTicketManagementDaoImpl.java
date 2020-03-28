package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.HyTicketSceneTicketManagementDao;
import com.hongyu.entity.HyTicketSceneTicketManagement;

@Repository("hyTicketSceneTicketManagementDaoImpl")
public class HyTicketSceneTicketManagementDaoImpl extends BaseDaoImpl<HyTicketSceneTicketManagement,Long>
    implements HyTicketSceneTicketManagementDao{

}
