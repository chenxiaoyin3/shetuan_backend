package com.hongyu.task.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.AdministrativeUpload;
import com.hongyu.entity.HyDimissionAudit;
import com.hongyu.service.AdministrativeUploadService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;
/**
 * 将行政上传的失效文件删除
 * @author lsq
 *
 */
@Component("adminitratorUploadFileProcessor")
public class AdminitratorUploadFileProcessor implements Processor{
	
	
	@Resource(name="administrativeUploadServiceImpl")
	AdministrativeUploadService administrativeUploadSrv;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		List<Filter> administratorFilters = new ArrayList<Filter>();
		administratorFilters.add(Filter.ne("effectDate", 100));
		List<AdministrativeUpload> uploadFiles = administrativeUploadSrv.findList(null, administratorFilters, null);

		for (AdministrativeUpload uploadFile : uploadFiles) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String nowaStr = format.format(new Date());
			String effectDateStr = format.format(DateUtil.getDateAfterSpecifiedDaysFormat(uploadFile.getCreateTime(),uploadFile.getEffectDate()));
			if(nowaStr.equals(effectDateStr)) {
				administrativeUploadSrv.delete(uploadFile.getId());
			}
		}
	}

}
