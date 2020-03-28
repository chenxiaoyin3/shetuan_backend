package com.hongyu.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.PayServicerBatchDao;

import java.math.BigInteger;
import java.util.*;

/**
 * @author xyy
 */
@Service("payServicerBatchServiceImpl")
public class PayServicerBatchServiceImpl extends BaseServiceImpl<PayServicerBatch, Long> implements PayServicerBatchService {
    @Resource(name = "payServicerBatchDaoImpl")
    PayServicerBatchDao dao;

    @Resource(name = "payServicerBatchDaoImpl")
    public void setBaseDao(PayServicerBatchDao dao) {
        super.setBaseDao(dao);
    }

    @Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;

    @Resource(name = "payServicerServiceImpl")
    private PayServicerService payServicerService;

    @Resource(name = "paymentSupplierServiceImpl")
    private PaymentSupplierService paymentSupplierService;

    @Resource(name = "payDetailsServiceImpl")
    private PayDetailsService payDetailsService;

    @Resource(name = "hyOrderServiceImpl")
    private HyOrderService hyOrderService;

    @Resource(name = "payablesLineItemServiceImpl")
    private PayablesLineItemService payablesLineItemService;

    @Override
    public List<HashMap<String, Object>> getUnPaidBatchList(Integer state, String batchCode) throws Exception {
        List<HashMap<String, Object>> res = new LinkedList<>();
        // ps.type 2:T+N 3:提前打款
        StringBuilder sql = new StringBuilder("SELECT ps.id, ps.type, ps.servicer_name, ps.amount, psb.create_date, psb.batch_code FROM hy_pay_servicer AS ps LEFT JOIN hy_pay_servicer_batch AS psb ON ps.id = psb.pay_servicer_id WHERE ps.has_paid = 0 AND ps.type IN (2,3) ");
        /*
        objects[0] id
        objects[1] type
        objects[2] servicer_name
        objects[3] amount
        objects[4] create_date
        objects[5] batch_code
        */
        if (state == 0) {
            sql.append("AND psb.process_status IS null;");
        } else if (state == 1) {
            sql.append("AND psb.process_status = 0 ");
            if(StringUtils.isNotBlank(batchCode)){
            	sql.append("AND psb.batch_code LIKE '%");
            	sql.append(batchCode);
            	sql.append("%'");
            }
        }
        List<Object[]> list = super.statis(sql.toString());
        for (Object[] objects : list) {
            HashMap<String, Object> map = new HashMap<>(6);
            map.put("id", objects[0]);
            map.put("type", objects[1]);
            map.put("servicerName", objects[2]);
            map.put("amount", objects[3]);
            map.put("createDate", 1 == state ? objects[4] : "");
            map.put("batchCode", 1 == state ? objects[5] : "");
            res.add(map);
        }
        return res;
    }

    @Override
    public void batchProcess(Long[] ids, HttpSession session, HttpServletResponse response) throws Exception {
        StringBuilder sqlInCondition = new StringBuilder("(");
        for (Long id : ids) {
            sqlInCondition.append(id);
            sqlInCondition.append(",");
        }
        // 删除多余的逗号
        sqlInCondition.deleteCharAt(sqlInCondition.length() - 1);
        sqlInCondition.append(") ");
        // 对ids做校验 防止ids中包含已经被处理的id 防止重放攻击！
        StringBuilder sql = new StringBuilder("SELECT count(*) FROM hy_pay_servicer_batch WHERE pay_servicer_id IN ");
        sql.append(sqlInCondition);
        BigInteger count = (BigInteger) super.getSingleResultByNativeQuery(sql.toString());
        if(count.longValue() > 0){
            throw new Exception("ids中包含已被处理的id");
        }
        // 数据批量添加到hy_pay_servicer_batch
        sql.setLength(0);
        String batchCode = DateUtil.getfileDate(new Date());
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        for (Long id : ids) {
            sql.append("(" + id + ", " + PayServicerBatch.ProcessStatus.processing.ordinal() + ", NOW(), NOW(), " + batchCode + ", '" + username + "'),");
        }
        sql.replace(sql.length() - 1, sql.length(), ";");
        sql.insert(0, "INSERT INTO hy_pay_servicer_batch(pay_servicer_id, process_status, create_date, modify_date, batch_code, creator) VALUES");
        super.deleteBySql(sql.toString());
        /* 分组导出txt*/
        sql.setLength(0);
        sql.append("SELECT SUM(ps.amount), bl.bank_account, bl.account_name, bl.bank_name, bl.bank_type, bl.bank_code FROM hy_pay_servicer AS ps LEFT JOIN hy_bank_list AS bl ON ps.bank_list_id = bl.id WHERE ps.id IN ");
        sql.append(sqlInCondition);
        // 按供应商和bankList进行分组
        sql.append(" GROUP BY ps.servicer_id, ps.bank_list_id");
        /*
        objects[0] amount
        objects[1] bank_account
        objects[2] account_name
        objects[3] bank_name
        objects[4] bank_type
        objects[5] bank_code
        */
        List<Object[]> list = super.statis(sql.toString());
        StringBuilder content = new StringBuilder();
        for (Object[] objects : list) {
            content.append("|");
            content.append(Constants.PAY_ACCOUNT);
            content.append("|");
            content.append(Constants.CLIENT_NUM);
            content.append("|");
            content.append(Constants.PAY_ACCOUNT_NAME);
            content.append("|");
            content.append(objects[0]);
            content.append("|");
            content.append(objects[1]);
            content.append("|");
            content.append(objects[2]);
            content.append("|");
            content.append(objects[3]);
            content.append("|");
            content.append(Constants.CURRENCY_RMB);
            content.append("|");
            if (((String) objects[3]).contains(Constants.DEFAULT_BANK_KEY_WORD)) {
                content.append(Constants.INNER_BANK_TRANSFER);
                content.append("|");
                content.append("");
            } else {
                content.append(Constants.INTER_BANK_TRANSFER);
                content.append("|");
                content.append(Constants.PAY_TYPE_INTER_BANK);
            }
            content.append("|");
            content.append(Constants.URGENT);
            content.append("|");
            content.append(Constants.PAY_USAGE);
            content.append("|");
            content.append(((Boolean) objects[4]) ? 1 : 0);
            content.append("|");
            content.append(objects[5]);
            content.append("|");
            // 文本换行
            content.append("\r\n");
        }
        String fileName = Constants.PRE_FIX + DateUtil.getfileDate(new Date()).substring(0, 8);
        com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
        excelCon.export2Txt(response, fileName, content.toString());
    }

    @Override
    public void batchCancel(Long[] ids) throws Exception {
        StringBuilder sql = new StringBuilder("(");
        for (Long id : ids) {
            sql.append(id);
            sql.append(",");
        }
        sql.replace(sql.length() - 1, sql.length(), ");");
        sql.insert(0, "DELETE FROM hy_pay_servicer_batch WHERE pay_servicer_id IN");
        super.deleteBySql(sql.toString());
    }

    @Override
    public void batchFinish(Long[] ids, HttpSession session) throws Exception {
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin hyAdmin = hyAdminService.find(username);
        // 1)修改hy_pay_servicer_batch
        StringBuilder sql = new StringBuilder("(");
        for (Long id : ids) {
            sql.append(id);
            sql.append(",");
        }
        sql.replace(sql.length() - 1, sql.length(), ");");
        sql.insert(0, "UPDATE hy_pay_servicer_batch SET process_status = " + PayServicerBatch.ProcessStatus.processed.ordinal() + ", modify_date = NOW(), operator = '" + username + "' WHERE pay_servicer_id IN");
        super.deleteBySql(sql.toString());

        // 2)修改hy_pay_servicer,完成事实上的已付款
        for (Long id : ids) {
            PayServicer payServicer = payServicerService.find(id);
            PaymentSupplier paymentSupplier = paymentSupplierService.find(payServicer.getReviewId());

            /* 1.保存付款记录*/
            PayDetails p = new PayDetails();
            // 1:PayServicer 2:PayShareProfit 3:PayGuider 4:PaySettlement 5:PayDeposit
            p.setSort(1);
            p.setPayId(id);
            p.setPayMethod(1L);
            p.setAccount("批量付款");
            p.setAmount(payServicer.getAmount());
            p.setDate(new Date());
            p.setOperator(hyAdmin.getName());
            payDetailsService.save(p);

            /* 2.修改付款状态*/
            // 1:已付款
            payServicer.setHasPaid(1);
            // 付款日期
            payServicer.setPayDate(new Date());
            // 付款人
            payServicer.setPayer(hyAdmin.getName());
            payServicerService.update(payServicer);

            /* 3.更新PaymentSupplier的付款状态*/
            paymentSupplier.setPayDate(new Date());
            paymentSupplier.setStatus(3);
            paymentSupplierService.update(paymentSupplier);

            /* 4.更新结算状态*/
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("paymentLineId", paymentSupplier.getId()));
            List<PayablesLineItem> payablesLineItems = payablesLineItemService.findList(null, filters, null);
            for (PayablesLineItem payablesLineItem : payablesLineItems) {
                HyOrder hyOrder = payablesLineItem.getHyOrder();
                hyOrder.setIfjiesuan(true);
                hyOrder.setJiesuantime(payServicer.getPayDate());
                hyOrderService.update(hyOrder);
            }
        }
    }
}