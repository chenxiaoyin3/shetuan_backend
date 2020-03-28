package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.strategies;

import java.util.ArrayList;
import java.util.List;

import com.hongyu.controller.hzj03.incomeandexpenses.MappingUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ContextLoader;

import com.hongyu.Filter;
import com.hongyu.controller.hzj03.incomeandexpenses.strategypattern.UnPaidStrategy;
import com.hongyu.entity.PaySettlement;
import com.hongyu.entitycustom.UnPaidCustom;
import com.hongyu.service.PaySettlementService;

/**
 * Created by lenovo on 2018/10/18.
 * @author xyy
 */
public class PaySettlementStrategy implements UnPaidStrategy{
    @Override
    public List<UnPaidCustom> getUnPaidList(String name) {
        List<UnPaidCustom> res = new ArrayList<>();
        List<Filter> filters = new ArrayList<>();
        filters.clear();
        filters.add(Filter.eq("hasPaid", 0));
		if (StringUtils.isNotEmpty(name)) {
			filters.add(Filter.like("branchName", name));
		}
        PaySettlementService paySettlementService = ContextLoader.getCurrentWebApplicationContext().getBean(PaySettlementService.class);
        List<PaySettlement> paySettlements = paySettlementService.findList(null,filters,null);
        for (PaySettlement p : paySettlements) {
            UnPaidCustom u = new UnPaidCustom();
            u.setAmount(p.getAmount());
            u.setId(p.getId());
            u.setName(p.getBranchName());
            // 分公司产品中心结算的付款表没有细分的类型
            u.setType(10);
            res.add(u);
        }
        return res;
    }
}
