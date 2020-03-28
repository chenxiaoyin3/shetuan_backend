package com.hongyu.controller.gxz04;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.service.CommonEdushenheService;
/**
 * 额度参数Contoller
 * @author guoxinze
 *
 */
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/generalsettings/shenheedu/")
public class CommonShenheeduController {
	
	static class Wrap {
		Set<CommonShenheedu> commonShenheedus;

		public Set<CommonShenheedu> getCommonShenheedus() {
			return commonShenheedus;
		}

		public void setCommonShenheedus(Set<CommonShenheedu> commonShenheedus) {
			this.commonShenheedus = commonShenheedus;
		}		
	}
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	/**
	 * 额度的详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail() {
		Json j = new Json();
		try {
			List<CommonShenheedu> commonShenheedus = commonEdushenheService.findAll();
			j.setObj(commonShenheedus);
			j.setMsg("查看成功");		
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 更新额度参数
	 * @param id
	 * @param money
	 * @return
	 */
	@RequestMapping(value="update")
	public Json update(@RequestBody Wrap wrap) {
		Json j = new Json();
		try {
			Set<CommonShenheedu> cs = wrap.getCommonShenheedus();
			
			for(CommonShenheedu edu : cs) {
				commonEdushenheService.update(edu, "eduleixing");
			}
					
			j.setMsg("更新成功");		
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
}
