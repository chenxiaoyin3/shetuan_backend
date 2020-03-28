//package com.grain.plugin.file;
//
//import javax.annotation.Resource;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.grain.controller.admin.BaseController;
//import com.grain.entity.PluginConfig;
//import com.grain.service.PluginConfigService;
//
///**
// * Controller - 本地文件存储
// */
//@Controller("adminPluginFileController")
//@RequestMapping("/admin/storage_plugin/file")
//public class FileController extends BaseController {
//
//	@Resource(name = "filePlugin")
//	private FilePlugin filePlugin;
////	@Resource(name = "pluginConfigServiceImpl")
////	private PluginConfigService pluginConfigService;
//
//	/**
//	 * 设置
//	 */
////	@RequestMapping(value = "/setting", method = RequestMethod.GET)
////	public String setting(ModelMap model) {
////		PluginConfig pluginConfig = filePlugin.getPluginConfig();
////		model.addAttribute("pluginConfig", pluginConfig);
////		return "/com/grain/plugin/file/setting";
////	}
//
//	/**
//	 * 更新
//	 */
////	@RequestMapping(value = "/update", method = RequestMethod.POST)
////	public String update(Integer order, RedirectAttributes redirectAttributes) {
////		PluginConfig pluginConfig = filePlugin.getPluginConfig();
////		pluginConfig.setIsEnabled(true);
////		pluginConfig.setOrder(order);
////		pluginConfigService.update(pluginConfig);
////		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
////		return "redirect:/admin/storage_plugin/list.jhtml";
////	}
//
//}