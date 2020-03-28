package com.hongyu.service.impl;

import javax.annotation.Resource;

import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.*;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierElementService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.HyPayablesElementDao;
import com.hongyu.service.HyPayablesElementService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xyy
 */
@Service("hyPayablesElementServiceImpl")
public class HyPayablesElementServiceImpl extends BaseServiceImpl<HyPayablesElement, Long> implements HyPayablesElementService {
    @Resource(name = "hyPayablesElementDaoImpl")
    HyPayablesElementDao dao;

    @Resource(name = "hyPayablesElementDaoImpl")
    public void setBaseDao(HyPayablesElementDao dao) {
        super.setBaseDao(dao);
    }

    @Resource(name = "hySupplierElementServiceImpl")
    private HySupplierElementService hySupplierElementService;

    @Resource(name = "hyRegulateServiceImpl")
    private HyRegulateService hyRegulateService;

    @Resource(name = "hyGroupServiceImpl")
    private HyGroupService hyGroupService;

    @Override
    public HashMap<String, Object> getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn, Integer supplierType, HyAdmin hyAdmin) throws Exception {
        HashMap<String, Object> obj = new HashMap<>();
        int page = pageable.getPage();
        int rows = pageable.getRows();
        /* 未付款 - 按单位(供应商)付款*/
        if (status == 1) {
            // 按供应商名称排序
            List<Order> orders = new ArrayList<>();
            orders.add(Order.desc("hySupplierElement"));
            pageable.setOrders(orders);

            List<Filter> filters = new ArrayList<>();

            if (hyAdmin != null) {
                // 只能查看自己的团
                filters.add(Filter.eq("operator", hyAdmin));
            }

            // 旅游元素供应商类型
            if (supplierType != null) {
                HySupplierElement.SupplierType type = null;

                switch (supplierType) {
                    case 0:
                        type = HySupplierElement.SupplierType.hotel;
                        break;
                    case 1:
                        type = HySupplierElement.SupplierType.ticket;
                        break;
                    case 2:
                        type = HySupplierElement.SupplierType.catering;
                        break;
                    case 3:
                        type = HySupplierElement.SupplierType.car;
                        break;
                    case 4:
                        type = HySupplierElement.SupplierType.traffic;
                        break;
                    case 5:
                        type = HySupplierElement.SupplierType.insurance;
                        break;
                    case 6:
                        type = HySupplierElement.SupplierType.shopping;
                        break;
                    case 7:
                        type = HySupplierElement.SupplierType.selfpay;
                        break;
                    case 8:
                        type = HySupplierElement.SupplierType.otherincome;
                        break;
                    case 9:
                        type = HySupplierElement.SupplierType.otherexpend;
                        break;
                    case 10:
                        type = HySupplierElement.SupplierType.elementlocal;
                        break;
                    case 11:
                        type = HySupplierElement.SupplierType.linelocal;
                        break;
                    case 12:
                        type = HySupplierElement.SupplierType.coupon;
                        break;
                    default:
                        break;
                }
                filters.add(Filter.eq("type", type));
            }

            // 根据名称筛选供应商
            if (StringUtils.isNotEmpty(name)) {
                List<Filter> filterList = new LinkedList<>();
                filterList.add(Filter.like("name", name));
                List<HySupplierElement> elements = hySupplierElementService.findList(null, filterList, null);
                if (elements != null && !elements.isEmpty()) {
                    filters.add(Filter.in("hySupplierElement", elements));
                }
            }

            // 欠付不为0 需要付款
            filters.add(Filter.gt("debt", 0));

            // 筛选计调报账审核已经通过的
            List<Filter> filtersHyRegulate = new ArrayList<>();
            filtersHyRegulate.add(Filter.eq("status", 2));
            List<HyRegulate> hyRegulates = hyRegulateService.findList(null, filtersHyRegulate, null);
            if (CollectionUtils.isNotEmpty(hyRegulates)) {
                filters.add(Filter.in("hyRegulate", hyRegulates));
            }

            pageable.setFilters(filters);
            Page<HyPayablesElement> hyPayablesElementPage = this.findPage(pageable);
            List<HashMap<String, Object>> list = getHyPayablesElementList(hyPayablesElementPage);
            obj.put("list", list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
            obj.put("total", hyPayablesElementPage.getTotal());
        }


        /* 未付款 - 按团付款*/
        else if (status == 2) {
            Page<HyPayablesElement> hyPayablesElementPage = getHyPayablesElementPage(false, pageable, startDate, endDate, sn, hyAdmin);
            List<HashMap<String, Object>> list = getHyPayablesElementList(hyPayablesElementPage);
            obj.put("list", list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
            obj.put("total", hyPayablesElementPage.getTotal());
        }

        /* 已付款 - 按团付款*/
        else if (status == 3) {
            Page<HyPayablesElement> hyPayablesElementPage = getHyPayablesElementPage(true, pageable, startDate, endDate, sn, hyAdmin);
            List<HashMap<String, Object>> list = getHyPayablesElementList(hyPayablesElementPage);
            obj.put("list", list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
            obj.put("total", hyPayablesElementPage.getTotal());
        }
        obj.put("pageNumber", page);
        obj.put("pageSize", rows);
        return obj;
    }

    @Override
    public HashMap<String, Object> getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn, Integer supplierType) throws Exception {
        return getList(pageable, status, name, startDate, endDate, sn, supplierType, null);
    }

    /**
     * 将Page<HyPayablesElement>封装为List
     */
    private List<HashMap<String, Object>> getHyPayablesElementList(Page<HyPayablesElement> hyPayablesElementPage) {
        List<HashMap<String, Object>> list = new LinkedList<>();
        for (HyPayablesElement tmp : hyPayablesElementPage.getRows()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", tmp.getId());
            map.put("groupId", tmp.getHyGroup().getId());
            map.put("supplierElementId", tmp.getHySupplierElement().getId());
            map.put("supplierName", tmp.getHySupplierElement().getName());
            map.put("supplierType", tmp.getHySupplierElement().getSupplierType());
            map.put("sn", tmp.getHyGroup().getLine().getPn());
            map.put("launchDate", tmp.getHyGroup().getStartDay());
            map.put("operator", tmp.getHyGroup().getCreator().getName());
            map.put("shouldPay", tmp.getPay());
            map.put("hasPaid", tmp.getPaid());
            map.put("own", tmp.getDebt());
            list.add(map);
        }
        return list;
    }

    /**
     * 按团付款的未付和已付  获取Page
     *
     * @param hasPaid 是否为已付
     */
    private Page<HyPayablesElement> getHyPayablesElementPage(Boolean hasPaid, Pageable pageable, String startDate, String endDate, String sn, HyAdmin hyAdmin) throws Exception {
        // 按团倒序
        List<Order> orders = new ArrayList<>();
        orders.add(Order.desc("hyGroup"));
        pageable.setOrders(orders);

        List<Filter> filters1 = new ArrayList<>();

        if (hyAdmin != null) {
            // 只能查看自己的团
            filters1.add(Filter.eq("operator", hyAdmin));
        }

        // 符合发团日期筛选的团
        List<Filter> filterList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(startDate)) {
            filterList.add(new Filter("startDay", Filter.Operator.ge, sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
        }
        if (StringUtils.isNotBlank(endDate)) {
            filterList.add(new Filter("startDay", Filter.Operator.le, sdf.parse(endDate.substring(0, 10) + " " + "23:59:59")));
        }
        List<HyGroup> groups = hyGroupService.findList(null, filterList, null);
        if (groups != null && !groups.isEmpty()) {
            filters1.add(Filter.in("hyGroup", groups));
        }

        // 符合产品编号的团
        if (StringUtils.isNotBlank(sn)) {
            filterList.clear();
            filterList.add(Filter.like("groupLinePn", sn));
            List<HyGroup> groups2 = hyGroupService.findList(null, filterList, null);
            if (groups2 != null && !groups2.isEmpty()) {
                filters1.add(Filter.in("hyGroup", groups2));
            }
        }

        // 已付和欠付的区别条件
        if (hasPaid) {
            // 已付大于0
            filters1.add(Filter.gt("paid", 0));
        } else {
            // 欠付大于0
            filters1.add(Filter.gt("debt", 0));
        }

        // 筛选计调报账审核已经通过的
        List<Filter> filtersHyRegulate = new ArrayList<>();
        filtersHyRegulate.add(Filter.eq("status", 2));
        List<HyRegulate> hyRegulates = hyRegulateService.findList(null, filtersHyRegulate, null);
        if (hyRegulates != null && !hyRegulates.isEmpty()) {
            filters1.add(Filter.in("hyRegulate", hyRegulates));
        }

        pageable.setFilters(filters1);
        Page<HyPayablesElement> hyPayablesElementPage = this.findPage(pageable);
        return hyPayablesElementPage;
    }
}
