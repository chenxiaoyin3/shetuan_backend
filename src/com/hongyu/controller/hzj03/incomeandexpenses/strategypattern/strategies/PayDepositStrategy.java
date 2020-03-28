package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.PayDepositService;

/**
 * Created by lenovo on 2018/10/18.
 * @author xyy
 */
public class PayDepositStrategy implements UnPaidStrategy {
    @Override
    public List<UnPaidCustom> getUnPaidList(String name) {
        List<UnPaidCustom> res = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        filters.add(Filter.eq("hasPaid", 0));
		if (StringUtils.isNotEmpty(name)) {
			filters.add(Filter.like("institution", name));
		}
        // 1:门店保证金退还 2:供应商保证金退还
        orders.add(Order.asc("depositType"));
        PayDepositService payDepositService = ContextLoader.getCurrentWebApplicationContext().getBean(PayDepositService.class);
        List<PayDeposit> payDeposits = payDepositService.findList(null, filters, null);
        for (PayDeposit p : payDeposits) {
            UnPaidCustom u = new UnPaidCustom();
            u.setAmount(p.getAmount());
            u.setId(p.getId());
            u.setName(p.getInstitution());
            u.setType(MappingUtil.getKey(p.getDepositType(), MappingUtil.PAY_DEPOSIT_TYPE_SET));
            res.add(u);
        }
        return res;
    }
}
