package com.hongyu.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Tag;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.TagService;

/** 标签管理 - 后台 */
@Controller
@RequestMapping(value = "/admin/tag")
public class TagAdminController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "tagServiceImpl")
	TagService tagService;

	/** 一级标签 - 列表 */
	@RequestMapping(value = "/toplevel/list")
	@ResponseBody
	public Json getTopLevelTagList(Pageable pageable, Tag tag) {
		if (tag == null)
			tag = new Tag();
		Json json = new Json();
		try {
			Page<Tag> page = tagService.findPage(pageable, tag);

			// 标签需要按照tagSort进行排序!!!

			json.setMsg("查询成功！");
			json.setObj(page);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("查询失败！");
			json.setSuccess(false);
		}
		return json;
	}

	/** 一级标签 - 添加 */
	@RequestMapping(value = "/toplevel/save")
	@ResponseBody
	public Json addTopLevelTag(String tagName, Integer tagType, Integer tagSort, String startTime, String endTime,
			HttpSession session) {
		Json json = new Json();
		try {
			// 1.判断是否已经有这种一级标签 以标签名称和类型作为依据
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("tagName", Operator.eq, tagName));
			filters.add(new Filter("tagType", Operator.eq, tagType));
			List<Tag> list = tagService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				json.setMsg("已存在这种一级标签,请修改标签属性！");
				json.setSuccess(false);
				return json;
			}
			Tag tag = new Tag();
			tag.setTagName(tagName);
			tag.setTagPid(-1L); // 一级标签没有pid
			tag.setTagType(tagType);
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			tag.setOperator(admin.getName()); // 操作人
			tag.setCreateTime(new Date());
			tag.setUpdateTime(new Date()); // 创建时 更新时间默认为创建时间
			tag.setTagSort(tagSort);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
			Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
			tag.setStartTime(sTime);
			tag.setEndTime(eTime);

			tagService.save(tag);
			json.setMsg("添加成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("添加失败！");
			json.setSuccess(false);
		}

		return json;
	}

	/** 一级标签 - 删除 */
	@RequestMapping(value = "/toplevel/delete")
	@ResponseBody
	public Json deleteTopLevelTag(Long id) {
		Json json = new Json();
		try {
			// 判断一级标签下是否有二级标签
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("pid", Operator.eq, id));
			List<Tag> list = tagService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				json.setMsg("该标签下存在二级标签,无法删除！");
				json.setSuccess(false);
				return json;
			}
			tagService.delete(id);
			json.setMsg("删除成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("删除失败！");
			json.setSuccess(false);
		}
		return json;
	}

	/** 一级标签 - 编辑 */
	@RequestMapping(value = "/toplevel/edit")
	@ResponseBody
	public Json editTopLevelEdit(Long id) {
		Json json = new Json();
		try {
			Tag tag = tagService.find(id);
			json.setObj(tag);
			json.setSuccess(true);
			json.setMsg("");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败！");
		}
		return json;
	}

	/** 一级标签 - 保存编辑 */
	@RequestMapping(value = "/toplevel/edit/save")
	@ResponseBody
	public Json saveTopLevelTag(Long id, String tagName, Integer tagType, Integer tagSort, String startTime,
			String endTime, HttpSession session) {
		Json json = new Json();
		try {
//			// 判断是否存在这种标签
//			List<Filter> filters = new ArrayList<>();
//			filters.add(new Filter("tagName", Operator.eq, tagName));
//			filters.add(new Filter("tagType", Operator.eq, tagType));
//			List<Tag> list = tagService.findList(null, filters, null);
//			if (list != null && list.size() > 0) {
//				json.setMsg("已存在这种一级标签,请修改标签属性！");
//				json.setSuccess(false);
//				return json;
//			}

			Tag tag = tagService.find(id);
			tag.setTagName(tagName);
			tag.setTagType(tagType);
			tag.setTagSort(tagSort);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
			Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
			tag.setStartTime(sTime);
			tag.setEndTime(eTime);

			tag.setUpdateTime(new Date()); // 修改标签的编辑时间
			tagService.update(tag, "tagPid", "operator", "createTime");

			json.setMsg("修改成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("修改失败！");
			json.setSuccess(true);
		}

		return json;
	}

	/********************************************************************************************************************/
	/********************************************************************************************************************/
	/********************************************************************************************************************/

	/** 二级标签 - 列表 */
	@RequestMapping(value = "/sublevel/list")
	@ResponseBody
	public Json getSubLevelTagList(Long pid) {
		Json json = new Json();
		try {
			// 1.一级标签的下拉选项框的值
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("pid", Operator.eq, -1)); // 一级标签的pid为-1
			List<Tag> topLevelTagOption = tagService.findList(null, filters, null); // 一级标签需要按照tagSort进行排序!!!

			// 2.根据pid获取相应的二级标签
			filters.get(0).setValue(pid);
			List<Tag> subLevelTagList = tagService.findList(null, filters, null); // 二级标签需要安装tagSort进行排序!!!

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("topLevelTagOption", topLevelTagOption);
			obj.put("subLevelTagList", subLevelTagList);

			json.setObj(obj);
			json.setMsg("查询成功!");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("查询失败!");
			json.setSuccess(false);
		}
		return json;
	}

	/** 二级标签 - 进入添加页 获取一级标签的下拉选项框 */
	@RequestMapping(value = "/sublevel/add/option")
	@ResponseBody
	public Json getTopLevelTagOption() {
		Json json = new Json();

		try {
			// 1.一级标签的下拉选项框的值
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("pid", Operator.eq, -1)); // 一级标签的pid为-1
			List<Tag> topLevelTagOption = tagService.findList(null, filters, null); // 一级标签需要按照tagSort进行排序!!!

			json.setObj(topLevelTagOption);
			json.setSuccess(true);

		} catch (Exception e) {
			json.setSuccess(false);
		}

		return json;
	}

	/** 二级标签 - 添加 - 保存 */
	@RequestMapping(value = "/sublevel/add/save")
	@ResponseBody
	public Json addSubLevelTag(Long tagPid, String tagName, Integer tagType, Integer tagSort, String startTime,
			String endTime, HttpSession session) {
		Json json = new Json();
		try {
			// 1.判断是否已经存在这种二级标签
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("tagName", Operator.eq, tagName));
			filters.add(new Filter("tagPid", Operator.eq, tagPid));
			List<Tag> list = tagService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				json.setMsg("已存在这种二级标签,请修改标签属性！");
				json.setSuccess(false);
				return json;
			}

			Tag tag = new Tag();
			tag.setTagName(tagName);
			tag.setTagPid(tagPid);
			tag.setTagType(tagType);
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			tag.setOperator(admin.getName()); // 操作人
			tag.setCreateTime(new Date());
			tag.setUpdateTime(new Date()); // 创建时 更新时间默认为创建时间
			tag.setTagSort(tagSort);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
			Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
			tag.setStartTime(sTime);
			tag.setEndTime(eTime);

			tagService.save(tag);
			json.setMsg("添加成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("添加失败！");
			json.setSuccess(false);
		}
		return json;
	}

	/** 二级标签 - 删除 */
	@RequestMapping(value = "/sublevel/delete")
	@ResponseBody
	public Json deleteSubLevelTag(Long id) {
		Json json = new Json();
		try {
			tagService.delete(id);
			json.setMsg("删除成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("删除失败！");
			json.setSuccess(false);
		}
		return json;
	}

	/** 二级标签 - 编辑 */
	@RequestMapping(value = "/sublevel/edit")
	@ResponseBody
	public Json editSubLevelTag(Long id) {
		Json json = new Json();
		try {
			Tag tag = tagService.find(id);
			json.setObj(tag);
			json.setSuccess(true);
			json.setMsg("");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败！");
		}
		return json;
	}
	
	/**二级标签 - 编辑 - 保存编辑*/
	@RequestMapping(value = "/sublevel/edit/save")
	@ResponseBody
	public Json saveEditSubLevelTag(Long id, String tagName, Integer tagType, Integer tagSort, String startTime,
			String endTime, HttpSession session){
		Json json = new Json();
		try {
			Tag tag = tagService.find(id);
			tag.setTagName(tagName);
			tag.setTagType(tagType);
			tag.setTagSort(tagSort);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
			Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
			tag.setStartTime(sTime);
			tag.setEndTime(eTime);

			tag.setUpdateTime(new Date()); // 修改标签的编辑时间
			tagService.update(tag, "tagPid", "operator", "createTime");

			json.setMsg("修改成功！");
			json.setSuccess(true);

		} catch (Exception e) {
			json.setMsg("修改失败！");
			json.setSuccess(true);
		}

		return json;
	}
}
