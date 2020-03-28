package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.ManageFeeDao;
import com.hongyu.entity.ManageFee;

@Repository("manageFeeDaoImpl")
public class ManageFeeDaoImpl extends BaseDaoImpl<ManageFee, Long> implements ManageFeeDao {
}