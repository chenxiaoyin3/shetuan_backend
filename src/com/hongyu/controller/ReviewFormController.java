package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.ReviewForm;
import com.hongyu.entity.ReviewFormItem;
import com.hongyu.service.ReviewFormItemService;
import com.hongyu.service.ReviewFormService;

@Controller
@RequestMapping("/admin/reviewForm/")
public class ReviewFormController {
	@Resource(name = "reviewFormServiceImpl")
	ReviewFormService reviewFormService;

	@Resource(name = "reviewFormItemServiceImpl")
	ReviewFormItemService reviewFormItemService;
	
	/**
	 * 获取评价单模板列表
	 * @param pageable	
	 * @param reviewForm
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, ReviewForm reviewForm) {
		Json json = new Json();
		try {
			Page<ReviewForm> page = reviewFormService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			ReviewForm reviewForm = reviewFormService.find(id);
			if (reviewForm != null) {
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(reviewForm);
			} else {
				json.setSuccess(false);
				json.setMsg("获取失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("modify")
	@ResponseBody
	public Json modify(@RequestBody ReviewForm reviewForm) {
		Json json = new Json();
		try {
			ReviewForm oldReviewForm = reviewFormService.find(reviewForm.getId());
			if (oldReviewForm != null) {
				oldReviewForm.setTitle(reviewForm.getTitle());
				oldReviewForm.setContent(reviewForm.getContent());
				
				if (reviewForm.getReviewFormItems() != null && reviewForm.getReviewFormItems().size() > 0) {
					oldReviewForm.getReviewFormItems().clear();
					
					for (ReviewFormItem reviewFormItem : reviewForm.getReviewFormItems()) {
						if(reviewFormItem.getId() == null){
							//如果是新增item，就先存放到数据表中
							reviewFormItemService.save(reviewFormItem);
							List<Filter> filters = new ArrayList<>();
							filters.add(Filter.eq("item", reviewFormItem.getItem()));
							filters.add(Filter.eq("serviceType", reviewFormItem.getServiceType()));
							List<ReviewFormItem> reviewFormItems = reviewFormItemService.findList(null,filters,null);
							reviewFormItem = reviewFormItems.get(0);
						}
						reviewFormItem.setReviewForm(oldReviewForm);
					}
					oldReviewForm.getReviewFormItems().addAll(reviewForm.getReviewFormItems());
					
				}
				reviewFormService.update(oldReviewForm);
				json.setSuccess(true);
				json.setMsg("编辑成功");
			}else {
				json.setSuccess(false);
				json.setMsg("编辑失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
