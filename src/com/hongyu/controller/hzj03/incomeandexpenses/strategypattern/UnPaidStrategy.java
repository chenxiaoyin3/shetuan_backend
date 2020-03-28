package com.hongyu.controller.hzj03.incomeandexpenses.strategypattern;

import com.hongyu.entitycustom.UnPaidCustom;

import java.util.List;

/**
 * Created by xyy on 2018/10/18.
 *
 * @author xyy
 */
public interface UnPaidStrategy {
    /**
     * 获取某一种待付款
     */
    List<UnPaidCustom> getUnPaidList(String name);
}
