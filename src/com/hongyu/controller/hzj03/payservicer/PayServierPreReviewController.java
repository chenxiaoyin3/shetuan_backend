package com.hongyu.controller.hzj03.payservicer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hongyu.CommonAttributes;
import com.hongyu.util.DateUtil;
import com.hongyu.util.liyang.ExcelHelper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.service.PaymentSupplierService;

/**
 * @author xyy
 *
 * 审核-向供应商付款
 */
@Controller
@RequestMapping("admin/payServicer")
public class PayServierPreReviewController {
    @Resource(name = "paymentSupplierServiceImpl")
    private PaymentSupplierService paymentSupplierService;

    private final static String[] NAME_ARR = {"打款单编号", "产品编号", "发团时间", "门店", "人数", "客户姓名", "订单编号", "产品名称","供应商名称", "收入", "结算价", "扣点金额", "应付金额", "账号", "报名计调", "审核状态"};
    private final static String[] KEY_ARR = {"payCode", "sn", "tDate", "storeName", "people", "contact", "orderNumber", "productName", "supplierName","income", "orderMoney", "koudian", "moneySum", "supplierName", "op", "state"};
    /** 需要合并单元格的列————"打款单编号"  "应付金额", "供应商名称", "银行名称"，"银行帐号", "报名计调", "审核状态"*/
    private final static int[] COL_NUM = {0, 8, 12, 13, 14, 15};

    /**
     * 向供应商付款审核 - 列表
     */
    @RequestMapping(value = "/list/view")
    @ResponseBody
    public Json payServierPreReviewList(Pageable pageable, Integer state, String supplierName, String orderNumber,String payCode,String sn, HttpSession session) {
        Json json = new Json();
        try {
        	json = paymentSupplierService.payServierPreReviewList(pageable, state, supplierName, orderNumber, payCode, sn, session);
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("获取失败");
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 向供应商付款审核 - 详情
     */
    @RequestMapping(value = "detail/view")
    @ResponseBody
    public Json getHistoryComments(Long id) {
        Json json = new Json();
        try {
            HashMap<String, Object> obj = paymentSupplierService.getHistoryComments(id);
            json.setObj(obj);
            json.setSuccess(true);
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("操作失败");
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 向供应商付款审核
     */
    @RequestMapping("/audit")
    @ResponseBody
    public Json audit(Long id, String comment, Integer state, String dismissRemark, BigDecimal modifyAmount, HttpSession session) {
        Json json = new Json();
        try {
            json = paymentSupplierService.addPaymentSupplierAudit(id, comment, state, dismissRemark, modifyAmount, session);
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("审核失败");
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 自动打款 - 财务 - 驳回处理
     */
    @RequestMapping("/correct")
    @ResponseBody
    public Json modifyAndSubmit2(Long id, Integer type, String dismissRemark, BigDecimal modifyAmount, HttpSession session) {
        Json json = new Json();
        try {
            json = paymentSupplierService.updateApply2(id, type, dismissRemark, modifyAmount, session);
        } catch (Exception e) {
            json.setMsg("操作失败");
            json.setSuccess(false);
        }
        return json;
    }


    /**
     * 取消审核操作
     */
    @RequestMapping("cancel_audit")
    @ResponseBody
    public Json cancelAudit(Long id,HttpSession session){
        Json json = new Json();
        try{
            json = paymentSupplierService.cancelAudit(id, session);
        }catch (Exception e){
            json.setMsg("操作失败");
            json.setSuccess(false);
            json.setObj(e);
            e.printStackTrace();
        }
        return json;
    }


    /**
     * 审核列表的打款单-导出excel
     * */
    @RequestMapping("list/get_excel_Remittance")
    public void getAuditExcel(Integer state, String supplierName, String orderNumber, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception{
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        // 初始化Excel表格
        Workbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = (HSSFSheet) workbook.createSheet("外部供应商打款单审核表");
        Map<String,CellStyle> styles = ExcelHelper.createStyles(workbook);
        // 初始化全局变量
        int N = NAME_ARR.length;
        // 初始化标题
        HSSFRow titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(24);
        HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("外部供应商打款单审核表-" + DateUtil.getBirthday(new Date()));
        titleCell.setCellStyle(styles.get("title"));
        CellRangeAddress region=new CellRangeAddress(0,0,0,N);
        sheet.addMergedRegion(region);
        /*初始化列表头*/
        HSSFRow headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(18);
        sheet.setColumnWidth(0, 10 * 256);
        /*初始化序列号*/
        HSSFCell numHeaderCell = headerRow.createCell(0);
        numHeaderCell.setCellValue("序号");
        numHeaderCell.setCellStyle(styles.get("header"));
        for(int i = 0; i< N; i++){
            HSSFCell headerCell = headerRow.createCell(i+1);
            sheet.setColumnWidth(i + 1, 15 * 256);
            headerCell.setCellValue(NAME_ARR[i]);
            headerCell.setCellStyle(styles.get("header"));
        }

        // 需要写入的数据
        List<Map<String, Object>> list = paymentSupplierService.getAuditList(state, supplierName, orderNumber, username);
        // excel行号
        int excelRowNum = 2;
        // list的序号
        int listIndex = 0;
        if(list.size() > 0){
            /** 用lastRowPayCode作为是否合并单元格的比较依据*/
            String lastRowPayCode = (String) list.get(0).get(KEY_ARR[0]);
            while(listIndex < list.size()){
                int groupSize = 1;
                int cursor = listIndex + 1;
                while(cursor < list.size() && lastRowPayCode.equals(list.get(cursor).get(KEY_ARR[0]))){
                    cursor++;
                    groupSize++;
                }
                for(int j = 0; j < groupSize; j++){
                    // 将数据行写到excel表
                    Row row = sheet.createRow(excelRowNum++);
                    row.setHeightInPoints(15);
                    // 为一行添加序号
                    Cell numCell = row.createCell(0);
                    numCell.setCellStyle(styles.get("listdata"));
                    numCell.setCellValue(listIndex + 1);
                    // 对于一行的每一列进行赋值
                    for (int i = 0; i < N; i++) {
                        String colName = KEY_ARR[i];
                        Cell dataCell = row.createCell(i + 1);
                        dataCell.setCellStyle(styles.get("listdata"));
                        Object obj = list.get(listIndex).get(colName);
                        // 供应商名称、银行名称和银行账号改成放到一列
                        if(i==13) {
//                            String bankname = KEY_ARR[i+1];
//                            String bankcount = KEY_ARR[i+2];
                            obj = obj + "  " + list.get(listIndex).get("bankName") 
                        			+ "  " + list.get(listIndex).get("bankAccount");
                        }
                        dataCell.setCellValue(obj == null ?  "" : obj.toString());
                    }
                    listIndex++;
                }
                // 合并单元格
                if(groupSize > 1){
                    for (int i : COL_NUM) {
                        // 注意fristRow和lastRow!
                        CellRangeAddress regionColumn = new CellRangeAddress(excelRowNum - groupSize, excelRowNum - 1, i + 1, i + 1);
                        sheet.addMergedRegion(regionColumn);
                    }
                }
                // 更新lastGroupPayCode
                if(listIndex < list.size()){
                    lastRowPayCode = (String) list.get(listIndex).get(KEY_ARR[0]);
                }
            }
        }

        //把数据写入到一个临时的目录中去 在临时目录下生成Excel
        String fileName = "外部供应商打款单审核表.xls";
        String userdir = ExcelHelper.getUserDir(request);
        String filefullname = userdir + fileName + ".xls";
        FileOutputStream out;
        try {
            out = new FileOutputStream(filefullname);
            try {
                workbook.write(out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ExcelHelper excelHelper = new ExcelHelper();
        try{
            excelHelper.export2Excel(request, response, fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    /**
//     * 获取审核列表, 无须分页
//     * */
//    private List<Map<String, Object>> getAuditList(Integer state, String supplierName, String orderNumber,String username) throws Exception{
//        List<Map<String, Object>> ans = new LinkedList<>();
//        StringBuilder sql = new StringBuilder("SELECT ps.id, ps.pay_code, ps.supplier_name, ps.operator, (SELECT name  FROM hy_admin WHERE hy_admin.username = ps.operator) AS operator_name, ps.money_sum, (SELECT name FROM hy_admin WHERE hy_admin.username = ps.creator) AS creator, ps.create_time, ps.pay_date, ps.status,hy_order.order_number,pli.sn,pli.product_name,pli.t_date,pli.order_money,pli.refunds,pli.koudian,(SELECT store_name FROM hy_store WHERE hy_store.id = hy_order.store_id) AS store_name,pli.money,(SELECT name FROM hy_admin WHERE hy_admin.username = (SELECT operator_id FROM hy_store WHERE hy_store.id = hy_order.store_id)) AS op, ps.process_instance_id, hy_order.people, hy_order.contact FROM (hy_payment_supplier AS ps RIGHT JOIN hy_payables_line_item  AS pli ON ps.id = pli.payment_line_id) LEFT JOIN hy_order ON pli.order_id = hy_order.id WHERE pli.state = 1 AND ps.is_valid = 1");
//        // 筛选条件: 审批状态
//        sql.append(" AND ps.process_instance_id IN (");
//          /*搜索已完成任务*/
//        List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
//        // 没有任何已办任务, 直接返回空的List
//        if(CollectionUtils.isEmpty(hisTasks)){
//            return ans;
//        }
//        for (HistoricTaskInstance hisTask : hisTasks) {
//            sql.append(hisTask.getProcessInstanceId());
//            sql.append(",");
//        }
//        // 删除多余的逗号
//        sql.deleteCharAt(sql.length() - 1);
//        // 按hy_payment_supplier的id降序
//        sql.append(") ORDER BY ps.id DESC");
//        List<Object[]> list = paymentSupplierService.statis(sql.toString());
//        /*
//         id                     obj[0]
//         pay_code               obj[1]
//         supplier_name          obj[2]
//         operator               obj[3]
//         operator_name          obj[4]
//         money_sum              obj[5]
//         creator                obj[6]
//         create_time            obj[7]
//         pay_date               obj[8]
//         status                 obj[9]
//         process_instance_id    obj[20]
//         -------  1 ——> n -------
//         order_number           obj[10]
//         sn                     obj[11]
//         product_name           obj[12]
//         t_date                 obj[13]
//         order_money            obj[14]
//         refunds                obj[15]
//         koudian                obj[16]
//         store_name             obj[17]
//         money                  obj[18]
//         op                     obj[19]
//         people                 obj[21]
//         contact                obj[22]
//        */
//        for (Object[] obj : list) {
//            HashMap<String, Object> m = new HashMap<>();
//            m.put("payCode", obj[1]);
//            m.put("supplierName", obj[2]);
//            m.put("operator", obj[3] == null ? "" : obj[3]);
//            m.put("operatorName", obj[4]);
//            m.put("moneySum", obj[5]);
//            m.put("createTime", obj[7]);
//            m.put("payTime", obj[8]);
//            m.put("status", obj[9]);
//            m.put("orderNumber", obj[10]);
//            m.put("sn", obj[11]);
//            m.put("productName", obj[12]);
//            m.put("tDate", obj[13]);
//            m.put("orderMoney", obj[14]);
//            m.put("koudian", obj[16]);
//            m.put("storeName", obj[17]);
//            m.put("money", obj[18]);
//            m.put("op", obj[19]);
//            m.put("people", obj[21]);
//            m.put("contact", obj[22]);
//            ans.add(m);
//        }
//        return ans;
//    }
}
