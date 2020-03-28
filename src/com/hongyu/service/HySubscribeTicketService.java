package com.hongyu.service;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.entity.HySubscribeTicket;

public interface HySubscribeTicketService extends BaseService<HySubscribeTicket, Long> {
	public Json addSubscribeTicket(HySubscribeTicket hySubscribeTicket, HttpSession session) throws Exception;
	public Json insertSubscribeTicketAudit(Long id, HttpSession session) throws Exception;
}