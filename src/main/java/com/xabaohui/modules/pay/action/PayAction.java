/**
 * 
 */
package com.xabaohui.modules.pay.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xabaohui.modules.pay.service.PayService;

/**
 * @author YRee
 * 
 */
@Controller
@RequestMapping("/pay")
public class PayAction {
	@Autowired
	private PayService payService;

}
