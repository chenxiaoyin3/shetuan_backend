package com.sn.service;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.sn.entity.PictureBook;

public interface PictureBookService extends BaseService<PictureBook,Long> {

	Json listView(Pageable pageable, String name, Integer type);

	Json getDetail(Long id);

	Json add(@RequestBody PictureBook pictureBook, HttpSession session);
	
	Json edit(@RequestBody PictureBook pictureBook, HttpSession session);

	Json Delete(Long id);

}
