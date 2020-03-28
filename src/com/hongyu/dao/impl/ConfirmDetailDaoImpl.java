package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ConfirmDetailDao;
import com.hongyu.entity.ConfirmDetail;

@Repository("confirmDetailDaoImpl")
public class ConfirmDetailDaoImpl extends BaseDaoImpl<ConfirmDetail, Long> implements ConfirmDetailDao {
}