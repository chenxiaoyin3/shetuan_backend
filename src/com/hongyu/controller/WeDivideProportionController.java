package com.hongyu.controller;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeDivideProportion;
import com.hongyu.entity.WeDivideProportionHistory;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WeDivideProportionHistoryService;
import com.hongyu.service.WeDivideProportionService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/admin/business/divideproportion"})
public class WeDivideProportionController
{
  @Resource(name="weDivideProportionServiceImpl")
  WeDivideProportionService proportionSrv;
  @Resource(name="weDivideProportionHistoryServiceImpl")
  WeDivideProportionHistoryService historySrv;
  @Resource(name="hyAdminServiceImpl")
  private HyAdminService hyAdminService;
  
  @RequestMapping(value={"/page/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Json WeDivideModelPage(WeDivideProportion proportion, Pageable pageable)
  {
    Json json = new Json();
    try
    {
      List<Order> orders = new ArrayList<Order>();
	  orders.add(Order.desc("id"));
	  pageable.setOrders(orders);
      Page<WeDivideProportion> page = this.proportionSrv.findPage(pageable, proportion);
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(page);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Json WeDivideModelAdd(WeDivideProportion proportion, HttpSession session)
  {
    Json json = new Json();
    try
    {
      String username = (String)session.getAttribute("principal");
      HyAdmin admin = (HyAdmin)this.hyAdminService.find(username);
      proportion.setOperator(admin);
      this.proportionSrv.save(proportion);
      json.setSuccess(true);
      json.setMsg("添加成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("添加失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/modify"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Json WeDivideModelModify(WeDivideProportion proportion)
  {
    Json json = new Json();
    try
    {
      this.proportionSrv.update(proportion, new String[] { "createTime", "endTime", "operator", "isValid", "proportionType" });
      
      json.setSuccess(true);
      json.setMsg("修改成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("修改失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/historylist/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Json WeDivideModelHistory(Long id)
  {
    Json json = new Json();
    try
    {
      List<Filter> filters = new ArrayList();
      filters.add(new Filter("proportionId", Filter.Operator.eq, id));
      List<WeDivideProportionHistory> list = this.historySrv.findList(null, filters, new ArrayList());
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(list);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/delete"})
  @ResponseBody
  public Json WeDivideDelete(Long id)
  {
    Json json = new Json();
    try
    {
      WeDivideProportion proportion = (WeDivideProportion)this.proportionSrv.find(id);
      proportion.setIsValid(Boolean.valueOf(false));
      this.proportionSrv.update(proportion);
      json.setSuccess(true);
      json.setMsg("删除成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("删除失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping({"/restore"})
  @ResponseBody
  public Json WeDivideRestore(Long id)
  {
    Json json = new Json();
    try
    {
      WeDivideProportion proportion = (WeDivideProportion)this.proportionSrv.find(id);
      proportion.setIsValid(Boolean.valueOf(true));
      this.proportionSrv.update(proportion);
      json.setSuccess(true);
      json.setMsg("恢复成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("恢复失败");
      json.setObj(null);
    }
    return json;
  }
}
