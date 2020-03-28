package com.sn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.util.SendMessageEMY;
import com.sn.entity.SnVerificationCode;
import com.sn.entity.User;
import com.sn.service.SnVerificationCodeService;
import com.sn.service.UserService;

@Service("UserServiceImpl")
public class UserServiceImpl extends BaseServiceImpl<User,Long> implements UserService {
	@Override
	@Resource(name="UserDaoImpl")
	public void setBaseDao(BaseDao<User,Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Override
	public Json add(User user) {
		Json j = new Json();
		try {
			if(user.getVcode() != null && user.getVcode() != ""){
				boolean isSuccess = check(user.getPhone(), user.getVcode());
	        		if(!isSuccess){
					j.setSuccess(false);
		        		j.setMsg("验证码错误");
		        		return j;
				}
			}
			this.save(user);
			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json list(Pageable pageable, String username) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<Filter>();
			if (username != null) {
				filter.add(Filter.like("username", username));
			}
			pageable.setFilters(filter);
			Page<User> page = this.findPage(pageable);
			for (User tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("username", tmp.getUsername());
				m.put("phone", tmp.getPhone());
				m.put("age", tmp.getAge());
				m.put("city", tmp.getCity());
				m.put("schoolPlace", tmp.getSchoolplace());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("result", result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
		}
		return j;
	}

	@Resource(name = "SnVerificationCodeServiceImpl")
	SnVerificationCodeService verificationCodeService;
	
	@Override
	public Json send(String phone) {
		Json json = new Json();
		try {
			if (phone == null || phone.length() == 0) {
				json.setSuccess(false);
				json.setMsg("发送失败，手机号为空");
				return json;
			}
			int x;
	        String t = null;
	        Random r = new Random();
	        while (true) {
	            x = r.nextInt(999999);
	            if (x > 99999) {
	                System.out.println(x);
	                break;
	            } else continue;
	        }
	        t="{\"code\":\""+x+"\"}";
	        SnVerificationCode verificationCode = new SnVerificationCode();
			verificationCode.setPhone(phone);
			verificationCode.setVcode(x+"");
			verificationCodeService.save(verificationCode);
	        boolean isSuccess = SendMessageEMY.sendMessage(phone, t, 1);
	        if(isSuccess){
				json.setSuccess(true);
		        json.setMsg("发送成功");
			}else{
				json.setSuccess(false);
		        json.setMsg("发送失败");
			}
			
			json.setSuccess(true);
			json.setMsg("发送成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("发送失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	public Boolean check(String phone, String code) {
		try {
			Date validDate=new Date(System.currentTimeMillis() - 600000);// addtime must not earlier thancurrenttime for 10min
			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.eq("phone", phone));
			filters2.add(Filter.eq("vcode", code));
			filters2.add(Filter.ge("createTime", validDate));
			List<SnVerificationCode> verificationCodes = verificationCodeService.findList(null, filters2, null);

			if (verificationCodes != null && verificationCodes.size() > 0) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Json testoid(String openid) {
		Json j = new Json();
		try {
			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.eq("openid", openid));
			List<User> users = this.findList(null, filters2, null);

			if (users != null && users.size() > 0) {
				j.setSuccess(true);
				j.setMsg("操作成功");
				j.setObj(true);
			}
			else {
				j.setSuccess(true);
				j.setMsg("操作成功");
				j.setObj(false);
			}
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败: " + e.getMessage());
			j.setObj(false);
			e.printStackTrace();
		}
		return j;
	}
}
