package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern;

import com.hongyu.entitycustom.UnPaidCustom;

import java.util.List;

/**
 * Created by xyy on 2018/10/18.
 * @author xyy
 */
public class UnPaidContext {
    /**
     * 持有一个具体策略的对象
     */
    private UnPaidStrategy strategy;

    /**
     * 构造函数，传入一个具体策略对象
     * @param strategy 具体策略对象
     */
    public UnPaidContext(UnPaidStrategy strategy) {
        this.strategy = strategy;
    }

    public UnPaidStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(UnPaidStrategy strategy) {
        this.strategy = strategy;
    }

    public List<UnPaidCustom> getUnPaidExecute(String name) {
        return this.strategy.getUnPaidList(name);
    }
}
