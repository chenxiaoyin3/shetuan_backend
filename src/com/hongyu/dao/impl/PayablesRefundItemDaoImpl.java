package com.hongyu.dao.impl;

import org.springframework.stereotype.Repository;
import com.grain.dao.impl.BaseDaoImpl;
import com.hongyu.dao.PayablesRefundItemDao;
import com.hongyu.entity.PayablesRefundItem;

@Repository("payablesRefundItemDaoImpl")
public class PayablesRefundItemDaoImpl extends BaseDaoImpl<PayablesRefundItem, Long> implements PayablesRefundItemDao {
}