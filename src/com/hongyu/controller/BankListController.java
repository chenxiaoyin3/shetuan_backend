package com.hongyu.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.Pageable;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.CommonAttributes;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.entity.BankList;
import com.hongyu.service.BankListService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("admin/bankList")
public class BankListController {
    @Resource(name = "hyAdminServiceImpl")
    HyAdminService hyAdminService;

    @Resource(name = "bankListServiceImpl")
    BankListService bankListService;

    @Resource(name = "hyCompanyServiceImpl")
    HyCompanyService hyCompanyService;

    @RequestMapping(value = "/list/view")
    @ResponseBody
    public Json list(Pageable pageable, BankList bankList, HttpSession session) {
        if (bankList == null) {
            bankList = new BankList();
        }
        HyCompany hyCompany = getCompanyBySession(session);
        Json json = new Json();
        try {
            List<Filter> filters = new LinkedList<>();
            filters.add(Filter.eq("hyCompany", hyCompany.getID()));
            // 筛选状态为未删除的帐号
            filters.add(Filter.eq("bankListStatus", 1));
            pageable.setFilters(filters);
            Page<BankList> page = bankListService.findPage(pageable, bankList);
            json.setObj(page);
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setMsg("操作失败");
            json.setSuccess(false);
        }
        return json;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Json add(BankList bankList, HttpSession session) {
        Json json = new Json();
        if(bankList == null){
            bankList = new BankList();
        }
        try {
            // TODO 避免重复添加
            HyCompany company = getCompanyBySession(session);
            if(company.getIsHead()){
                // 如果是总公司
                bankList.setYhlx(BankList.Yinhangleixing.zbcw);
            }
            else{
                // 如果为分公司
                bankList.setYhlx(BankList.Yinhangleixing.fengongsi);
            }
            bankList.setHyCompany(company);
            bankListService.save(bankList);
            json.setMsg("操作成功");
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setMsg("操作失败");
            json.setSuccess(false);
        }
        return json;
    }


    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public Json edit(BankList bankList) {
        Json json = new Json();
        try {
            // TODO 避免修改之后造成重复
            String alias = bankList.getAlias();
            String bankName = bankList.getBankName();
            String bankAccount = bankList.getBankAccount();
            if(bankList.getType() == BankList.BankType.bank){
                alias = bankName+"(尾号"+bankAccount.substring(bankAccount.length()-4, bankAccount.length())+")";
            }else if(bankList.getType() == BankList.BankType.alipay){
                alias = "支付宝-" + bankAccount;
            }else if(bankList.getType() == BankList.BankType.wechatpay){
                alias = "微信支付-" + bankAccount;
            }
            bankList.setAlias(alias);

            bankList.setBankListStatus(1);
            bankListService.update(bankList,"hyCompany","bankListStatus", "yhlx");
            json.setMsg("操作成功");
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setMsg("操作失败");
            json.setSuccess(false);
        }
        return json;
    }

    // TODO 增加批量删除
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Json delete(Long id) {
        Json json = new Json();
        try {
            BankList bankList = bankListService.find(id);
            synchronized (bankList) {
                // 置为删除状态
                bankList.setBankListStatus(0);
                bankListService.update(bankList);
            }
            json.setMsg("操作成功");
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setMsg("操作失败");
            json.setSuccess(false);
        }
        return json;
    }

    /**
     * 根据session获取Company
     */
    private HyCompany getCompanyBySession(HttpSession session) {
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin admin = hyAdminService.find(username);
        Department pDepartment = admin.getDepartment().getHyDepartment();
        List<Filter> filters = new ArrayList<>(1);
        filters.add(Filter.eq("hyDepartment",pDepartment.getId()));
        List<HyCompany> list = hyCompanyService.findList(null, filters, null);
        HyCompany hyCompany = list.get(0);
        return hyCompany;
    }

}
