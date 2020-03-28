package com.hongyu.controller.cwz;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupOtherprice;
import com.hongyu.entity.HyGroupOtherpriceSwd;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyGroupPriceSwd;
import com.hongyu.entity.HyGroupShenheSwd;
import com.hongyu.entity.HyGroupSpecialPriceSwd;
import com.hongyu.entity.HyGroupSpecialprice;
import com.hongyu.service.HyGroupOtherpriceService;
import com.hongyu.service.HyGroupOtherpriceSwdService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupPriceSwdService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyGroupShenheSwdService;
import com.hongyu.service.HyGroupSpecialPriceSwdService;
import com.hongyu.service.HyGroupSpecialpriceService;
import com.hongyu.util.Constants.AuditStatus;

/**
 * 
 * 整个类的作用是：传进来改完价格的数据，保存到我们的数据库里
 * @author chenwenzhi
 *
 */
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/swd/line/")
public class SwdController {
	
	public static class Wrap{
		//定义内部类，生成get set方法
		public List<HyGroupPriceSwd> hyGroupPriceSwds = new ArrayList<>();
		public List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds = new ArrayList<>();
		public List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds = new ArrayList<>();
		public List<HyGroupPriceSwd> getHyGroupPriceSwds() {
			return hyGroupPriceSwds;
		}
		public void setHyGroupPriceSwds(List<HyGroupPriceSwd> hyGroupPriceSwds) {
			this.hyGroupPriceSwds = hyGroupPriceSwds;
		}
		public List<HyGroupOtherpriceSwd> getHyGroupOtherpriceSwds() {
			return hyGroupOtherpriceSwds;
		}
		public void setHyGroupOtherpriceSwds(List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds) {
			this.hyGroupOtherpriceSwds = hyGroupOtherpriceSwds;
		}
		public List<HyGroupSpecialPriceSwd> getHyGroupSpecialPriceSwds() {
			return hyGroupSpecialPriceSwds;
		}
		public void setHyGroupSpecialPriceSwds(List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds) {
			this.hyGroupSpecialPriceSwds = hyGroupSpecialPriceSwds;
		}
		
	}

	//下面四个是自己的实体类，注入下
	//自己写的Service类是为了去save前端传过来的值
	@Resource(name = "hyGroupPriceSwdServiceImpl")
	HyGroupPriceSwdService hyGroupPriceSwdService;
	
	@Resource(name = "hyGroupOtherpriceSwdServiceImpl")
	HyGroupOtherpriceSwdService hyGroupOtherpriceSwdService;
	
	@Resource(name = "hyGroupSpecialPriceSwdServiceImpl")
	HyGroupSpecialPriceSwdService hyGroupSpecialPriceSwdService;
	
	@Resource(name = "hyGroupShenheSwdServiceImpl")
	HyGroupShenheSwdService hyGroupShenheSwdService;
	
	//后面两个是保存到数据库要的实体类，保存下
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	//去Service实现类里面找标签 我的外键，对应实体类的Service
	@Resource(name = "hyGroupOtherpriceServiceImpl")
	//起的名字：hyGroupOtherpriceService最好和前面的一样
	HyGroupOtherpriceService hyGroupOtherpriceService;
	
	//用别人写的service需要加注解
	@Resource(name = "hyGroupSpecialpriceServiceImpl")
	HyGroupSpecialpriceService hyGroupSpecialpriceService;
	
	//这三个注入是固定套路  这个是activity三个重要变量
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	//师兄写的例子，其中HttpSession session是后来添加上去的
	//网页上点击“提交”，等于说调用这里的submit
	@RequestMapping(value="submit")
	public Json submit(@RequestBody Wrap wrap, HttpSession session) {
		Json j = new Json();//接收的都是前端传来的json数据
		try {
			//这个是后来加的，为了根据ID取到HyGroup的数据
			HyGroup hyGroup = null;
			//三个List 取到json封装的数据
			List<HyGroupPriceSwd> hyGroupPriceSwds = wrap.getHyGroupPriceSwds();
			List<HyGroupOtherpriceSwd> hyGroupOtherpriceSwds = wrap.getHyGroupOtherpriceSwds();
			List<HyGroupSpecialPriceSwd> hyGroupSpecialPriceSwds = wrap.getHyGroupSpecialPriceSwds();
			
			//遍历第一个，存到数据库，专注于外键，普通的自动存
			for(HyGroupPriceSwd temp : hyGroupPriceSwds) {
				//不是空才接收
				if(temp.getHygroup().getId() != null) {
					//根据ID找外键实体类
					HyGroup group = hyGroupService.find(temp.getHygroup().getId());
					//只要是外键，就一定要从id到数据库中通过service找  
					//HyGroup group = hyGroupService.find(groupId);
					hyGroup = group;
					//设置到实体类中
					temp.setHygroup(group);
				}
				//另外一个外键，一样的意思
				if(temp.getHyGroupPriceID().getId() != null) {
					HyGroupPrice hyGroupPrice = hyGroupPriceService.find(temp.getHyGroupPriceID().getId());
					temp.setHyGroupPriceID(hyGroupPrice);
				}
				hyGroupPriceSwdService.save(temp);
			}
			
			//我写的 另一个实体类里面的外键 
			for(HyGroupOtherpriceSwd temp : hyGroupOtherpriceSwds) {
				if(temp.getHygroup().getId() != null) {
					HyGroup group = hyGroupService.find(temp.getHygroup().getId());
					temp.setHygroup(group);
				}
				if(temp.getHyGroupOtherprice().getId() != null) {
					//find用的都不是自己的service，保存都用的是自己的service
					HyGroupOtherprice hyGroupOtherprice = hyGroupOtherpriceService.find(temp.getHyGroupOtherprice().getId());
					temp.setHyGroupOtherprice(hyGroupOtherprice);
				}
				hyGroupOtherpriceSwdService.save(temp);
			}
			
			//我写的 第三个
			for(HyGroupSpecialPriceSwd temp : hyGroupSpecialPriceSwds) {
				//不是空才接收
				if(temp.getHygroup().getId() != null) {
					//根据ID找外键实体类
					HyGroup group = hyGroupService.find(temp.getHygroup().getId());
					// 只要是外键，就一定要从id到数据库中通过service找    HyGroup group = hyGroupService.find(groupId);
					hyGroup = group;
					//设置到实体类中 纯后台的前端不会传过来 想办法拿出来
					//不能new 如果new就是一个空表
					temp.setHygroup(group);
				}
				//另外一个外键，一样的意思
				if(temp.getHyGroupSpecialprice().getId() != null) {
					HyGroupSpecialprice hyGroupSpecialprice = hyGroupSpecialpriceService.find(temp.getHyGroupSpecialprice().getId());
					temp.setHyGroupSpecialprice(hyGroupSpecialprice);
				}
				hyGroupSpecialPriceSwdService.save(temp);
			}
			
			
			//在这之前画了一个流程图：diagram里面的SwdProcess，修改了整体的ID为swdprocess
			//里面画了两个task，都修改了描述 task里面选择usertask表示数据
			//下面是用代码运用画的流程图
			
			//开启流程实例 复制过来的 改名为swdprocess
			//session保存了用户的账号
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//通过key开启了流程实例
			//甩尾单的 所有数据（上面三个entity）是要走这个swdprocess流程的 要审核entity的数据
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("swdprocess");
			// 根据流程实例Id查询任务，查到的就是流程图里的第一个任务要干啥：采购部提交甩尾单申请
			// 找到当前运行的task
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			//审核数据库表的创建 由于是纯后台 不接收前端的类 所以直接用new
			//注意实体类的构造器可能要删除下
			HyGroupShenheSwd swd = new HyGroupShenheSwd();
			//把shenhe这个实体类里面的值依次赋上 上面带出来的hyGroup在这里用
			swd.setHygroup(hyGroup);
			swd.setApplyName(username);
			//申请时间是当前时间
			swd.setApplyTime(new Date());
			//这个是“正在审核中”
			swd.setAuditStatus(AuditStatus.auditing);
			//得到流程实例的ID 方法是封装好的 保存到审核的数据库中
			swd.setProcessInstanceId(pi.getProcessInstanceId());
			hyGroupShenheSwdService.save(swd);
			
			// 这之间写了一个监听器类（Listener这个包里面的） 即SwdListener，这个甩尾单由谁来审核
			// 用DeployListener把监听器和流程图连接在一起  Listener来决定谁来审核
			// 提交的时候不用Listener 审核的时候才用这个 
			
			// 完成任务，根据监听器设置下一步审核人 复制的
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());	
					
			//这个是打上去的 这个接口是提交审核 还没有被审核
			j.setMsg("提交审核成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
