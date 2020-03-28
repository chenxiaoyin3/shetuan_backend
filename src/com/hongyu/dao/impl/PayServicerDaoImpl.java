package com.hongyu.dao.impl;import org.springframework.stereotype.Repository;

import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayServicerDao;
import com.hongyu.entity.PayServicer;@Repository("payServicerDaoImpl")public class PayServicerDaoImpl extends BaseDaoImpl<PayServicer, Long> implements PayServicerDao {}