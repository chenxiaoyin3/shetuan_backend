package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtyLost;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.InboundService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.PurchaseService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyLostService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;

@Controller
@RequestMapping({"/admin/business/inbound/"})
public class InboundController {
    @Resource(name="inboundServiceImpl")
    InboundService inboundService;

    @Resource(name="specialtySpecificationServiceImpl")
    SpecialtySpecificationService specialtySpecificationService;

    @Resource(name="specialtyServiceImpl")
    SpecialtyService specialtyService;

    @Resource(name="purchaseItemServiceImpl")
    PurchaseItemService purchaseItemService;

    @Resource(name="purchaseServiceImpl")
    PurchaseService purchaseService;

    @Resource(name="specialtyCategoryServiceImpl")
    SpecialtyCategoryService specialtyCategoryService;
    private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent)
    {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        if (parent.getChildSpecialtyCategory().size() > 0) {
            for (SpecialtyCategory child : parent.getChildSpecialtyCategory())
            {
                if(child.getIsActive()) {
                    HashMap<String, Object> hm = new HashMap<String, Object>();
                    hm.put("value", child.getId());
                    hm.put("label", child.getName());
                    hm.put("children", fieldFilter(child));
                    list.add(hm);
                }
            }
        }
        return list;
    }

    @RequestMapping({"/treelist/view"})
    @ResponseBody
    public Json specialtyCategoryTreeList()
    {
        Json json = new Json();
        List<Filter> filters = new ArrayList<Filter>();
        Filter filter = new Filter("parent", Filter.Operator.isNull, null);
        filters.add(filter);
        List<Order> orders = new ArrayList<Order>();
        orders.add(Order.asc("id"));
        List<SpecialtyCategory> list = this.specialtyCategoryService.findList(null, filters, orders);
        List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
        for (SpecialtyCategory parent : list)
        {
            HashMap<String, Object> hm = new HashMap<String, Object>();
            hm.put("value", parent.getId());
            hm.put("label", parent.getName());
            hm.put("children", fieldFilter(parent));
            obj.add(hm);
        }
        json.setSuccess(true);
        json.setMsg("查询成功");
        json.setObj(obj);
        return json;
    }

    @Resource(name="specialtyLostServiceImpl")
    SpecialtyLostService specialtyLostService;

    @RequestMapping(value="listview")
    @ResponseBody
    public Json listview(Pageable pageable,Long speciltyCategoryId,String specialtyName,HttpSession session)
    {
        Json json=new Json();
        Inbound inbound=new Inbound();
        try{
            if(speciltyCategoryId==null&&specialtyName==null)
            {
                List<Order> orders = new ArrayList<Order>();
                orders.add(Order.desc("productDate"));
                pageable.setOrders(orders);
                List<Filter> filter=new ArrayList<Filter>();
                filter.add(Filter.ne("inboundNumber", 0));
                pageable.setFilters(filter);
                Page<Inbound> page=inboundService.findPage(pageable,inbound);
                List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
                for(Inbound inbd:page.getRows())
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", inbd.getId());
                    map.put("specialtyName", inbd.getSpecification().getSpecialty().getName());
                    map.put("specification",inbd.getSpecification().getSpecification());
                    map.put("productDate", inbd.getProductDate());
                    map.put("durabilityPeriod", inbd.getDurabilityPeriod());
                    map.put("inboundNumber",inbd.getInboundNumber());
                    map.put("depotCode", inbd.getDepotCode());
                    map.put("purchaseCode", inbd.getPurchaseItem().getPurchase().getPurchaseCode());
                    map.put("specialtyCatrgoryName", inbd.getSpecification().getSpecialty().getCategory().getName());

                    List<Filter> lostFilters = new ArrayList<>();
                    lostFilters.add(Filter.eq("inbound", inbd));
                    lostFilters.add(Filter.ne("status", 1));
                    List<SpecialtyLost> losts = specialtyLostService.findList(null,lostFilters,null);
                    if(losts!=null && !losts.isEmpty()) {
                        map.put("canLost", false);
                    }else {
                        map.put("canLost", true);
                    }

                    lists.add(map);
                }
                Page<Map<String,Object>> inboundPage=new Page<Map<String,Object>>(lists,page.getTotal(),pageable);
                json.setSuccess(true);
                json.setMsg("查询成功");
                json.setObj(inboundPage);
            }
            else{
                List<Filter> filters = new ArrayList<Filter>();
                if(speciltyCategoryId!=null)
                {
                    SpecialtyCategory category=specialtyCategoryService.find(speciltyCategoryId);
                    Filter filter=new Filter("category",Operator.eq,category);
                    filters.add(filter);
                }
                if(specialtyName!=null&&!specialtyName.equals(""))
                {
                    Filter filter=new Filter("name",Operator.like,specialtyName);
                    filters.add(filter);
                }
                List<Specialty> specialtylist = specialtyService.findList(null, filters, null);
                List<Filter> filteres=new ArrayList<Filter>();
                if (specialtylist.size() == 0) {
                    json.setSuccess(true);
                    json.setMsg("查询成功");
                    json.setObj(new Page<Inbound>());
                    return json;
                }
                else {
                    filteres.add(Filter.in("specialty", specialtylist));
                    List<SpecialtySpecification> specialtySpecificationList=specialtySpecificationService.findList(null,filteres,null);
                    List<Filter> finalfilter=new ArrayList<Filter>();
                    finalfilter.add(Filter.in("specification", specialtySpecificationList));
                    finalfilter.add(Filter.ne("inboundNumber", 0));
                    pageable.setFilters(finalfilter);
                    List<Order> orders = new ArrayList<Order>();
                    orders.add(Order.desc("productDate"));
                    pageable.setOrders(orders);
                    Page<Inbound> page=inboundService.findPage(pageable,inbound);
                    List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
                    for(Inbound inbd:page.getRows())
                    {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("id", inbd.getId());
                        map.put("specialtyName", inbd.getSpecification().getSpecialty().getName());
                        map.put("specification",inbd.getSpecification().getSpecification());
                        map.put("productDate", inbd.getProductDate());
                        map.put("durabilityPeriod", inbd.getDurabilityPeriod());
                        map.put("inboundNumber",inbd.getInboundNumber());
                        map.put("depotCode", inbd.getDepotCode());
                        map.put("purchaseCode", inbd.getPurchaseItem().getPurchase().getPurchaseCode());
                        map.put("specialtyCatrgoryName", inbd.getSpecification().getSpecialty().getCategory().getName());

                        List<Filter> lostFilters = new ArrayList<>();
                        lostFilters.add(Filter.eq("inbound", inbd));
                        lostFilters.add(Filter.ne("status", 1));
                        List<SpecialtyLost> losts = specialtyLostService.findList(null,lostFilters,null);
                        if(losts!=null && !losts.isEmpty()) {
                            map.put("canLost", false);
                        }else {
                            map.put("canLost", true);
                        }

                        lists.add(map);
                    }
                    Page<Map<String,Object>> inboundPage=new Page<Map<String,Object>>(lists,page.getTotal(),pageable);
                    json.setSuccess(true);
                    json.setMsg("查询成功");
                    json.setObj(inboundPage);
                }
            }
        }
        catch(Exception e){
            json.setSuccess(false);
            json.setMsg("查询失败");
            json.setObj(e);
            e.printStackTrace();
        }
        return json;
    }

    @RequestMapping(value="/page/view")
    @ResponseBody
    public Json inboundPage(Pageable pageable,Long speciltyCategoryId,String specialtyName,HttpSession session)
    {
        Json j = new Json();

        try {
            Integer total = inboundService.findInboundUniqueSpecificationTotal(speciltyCategoryId, specialtyName);
            List<Map<String, Object>> res = inboundService.getMergedInboundByPage((pageable.getPage()-1)*pageable.getRows(), pageable.getRows(), speciltyCategoryId, specialtyName);
            Page<Map<String, Object>> page = new Page<>(res, total, pageable);
            j.setSuccess(true);
            j.setMsg("查询成功");
            j.setObj(page);
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("查询失败");
            j.setObj(e);
        }

        return j;

    }
}
