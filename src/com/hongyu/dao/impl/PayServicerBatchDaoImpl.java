package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;
import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayServicerBatchDao;
import com.hongyu.entity.PayServicerBatch;

@Repository("payServicerBatchDaoImpl")
public class PayServicerBatchDaoImpl extends BaseDaoImpl<PayServicerBatch, Long> implements PayServicerBatchDao {
}