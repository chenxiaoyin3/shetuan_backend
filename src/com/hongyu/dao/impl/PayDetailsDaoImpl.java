package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayDetailsDao;
import com.hongyu.entity.PayDetails;

@Repository("payDetailsDaoImpl")
public class PayDetailsDaoImpl extends BaseDaoImpl<PayDetails, Long> implements PayDetailsDao {
}