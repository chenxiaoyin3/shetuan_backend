package com.hongyu.service; import com.grain.service.BaseService;import com.hongyu.entity. PayablesLineItem;

import java.util.HashMap;
import java.util.List;

public interface PayablesLineItemService extends BaseService<PayablesLineItem,Long>{
    List<HashMap<String,Object>> getSupplierReconciliationDetail(String str) throws Exception;
}