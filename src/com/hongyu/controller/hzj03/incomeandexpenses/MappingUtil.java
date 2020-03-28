package com.hongyu.controller.hzj03.incomeandexpenses;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xyy on 2019/6/4.
 *
 * @author xyy
 *         <p>
 *  总公司的待付款 已付款
 *  前端使用的type(下拉列表)和后台使用的type(数据库某一列)的对应
 */
public class MappingUtil {

    /**
     * 前端                                                           <------------->    hy_pay_servicer表
     * 1:分公司预付款                                       <------------->    1
     * 2:T+N打款                                               <------------->    2
     * 3:提前打款                                               <------------->    3
     * 4:旅游元素供应商尾款                            <------------->    4
     * 5:向酒店/门票/酒加景供应商付款        <------------->    5
     * 6:江泰预充款                                           <------------->    6
     * 21:总公司预付款                                     <------------->    7
     * 23:门店提现                                            <------------->    8
     * 24:分公司提现                                            <------------->    9
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> PAY_SERVICER_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
        put(5, 5);
        put(6, 6);
        put(21, 7);
        put(23, 8);
        put(24, 9);
    }};

    /**
     * 前端                         <------------->    hy_pay_share_profit表
     * 7:分公司分成        <------------->     1
     * 25:门店分成         <------------->    2
     * 20:微商后返         <------------->    3
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> PAY_SHAREPROFIT_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(7, 1);
        put(25, 2);
        put(20, 3);
    }};

    /**
     * 前端                                <------------->    hy_pay_guider表
     * 8:导游报账应付款        <------------->    1
     * 9:导游费用                   <------------->    2
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> PAY_GUIDER_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(8, 1);
        put(9, 2);
    }};

    /**
     * 前端                                          <------------->    hy_pay_settlement
     * hy_pay_settlement表中存放的只有一种类型
     * 10:分公司产品中心结算        <------------->
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> PAY_SETTLEMENT_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(10, 10);
    }};

    /**
     * 前端                               <------------->    hy_pay_deposit表
     * 11:门店保证金            <------------->    1
     * 12:供应商保证金        <------------->    2
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> PAY_DEPOSIT_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(11, 1);
        put(12, 2);
    }};

    /**
     * 前端                                                         <------------->    hy_refund_info
     * 13:供应商消团退款                              <------------->    1
     * 14:游客退团退款                                  <------------->    2
     * 15:供应商驳回订单退款                       <------------->    3
     * 16:门店驳回订单退款                           <------------->    4
     * 17:电子门票退款-官网/微商              <------------->    5
     * 18:签证退款-官网/微商                      <------------->    6
     * 19:酒店/酒加景退款-官网/微商        <------------->    7
     */
    @SuppressWarnings("serial")
    public static final HashMap<Integer, Integer> REFUNDINFO_TYPE_SET = new HashMap<Integer, Integer>() {{
        put(13, 1);
        put(14, 2);
        put(15, 3);
        put(16, 4);
        put(17, 5);
        put(18, 6);
        put(19, 7);
    }};

    /**
     * 通过value获取key
     */
    public static Integer getKey(Integer value, HashMap<Integer, Integer> map) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
