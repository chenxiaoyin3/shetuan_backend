package com.grain.plugin.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.grain.plugin.StoragePlugin;
import com.grain.util.SettingUtils;
import com.hongyu.FileInfo;
import com.hongyu.Setting;

/**
 * Plugin - 本地文件存储
 * 
 */
@Component("filePlugin")
public class FilePlugin extends StoragePlugin implements ServletContextAware {

	/** servletContext */
	private ServletContext servletContext;
	
	static private String SITE_URL = System.getProperty( "catalina.base" )+"/webapps/";

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public String getName() {
		return "本地文件存储";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "Szy Team";
	}

	@Override
	public String getSiteUrl() {
		return "";  //http://www.xxxx.net
	}

	@Override
	public String getInstallUrl() {
		return null;
	}

	@Override
	public String getUninstallUrl() {
		return null;
	}

	@Override
	public String getSettingUrl() {
		return "file/setting.jhtml";
	}

	@Override
	public void upload(String path, File file, String contentType) {
//		File destFile = new File(servletContext.getRealPath(path));
		File destFile = new File(SITE_URL + path);
		try {
			FileUtils.moveFile(file, destFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print(SITE_URL + path);
	}

	@Override
	public String getUrl(String path) {
//		Setting setting = SettingUtils.get();
		return path;
	}

	@Override
	public List<FileInfo> browser(String path) {
		Setting setting = SettingUtils.get();
		List<FileInfo> fileInfos = new ArrayList<FileInfo>();
		File directory = new File(servletContext.getRealPath(path));
		if (directory.exists() && directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setName(file.getName());
				fileInfo.setUrl(setting.getSiteUrl() + path + file.getName());
				fileInfo.setIsDirectory(file.isDirectory());
				fileInfo.setSize(file.length());
				fileInfo.setLastModified(new Date(file.lastModified()));
				fileInfos.add(fileInfo);
			}
		}
		return fileInfos;
	}

	@Override
	public int compareTo(StoragePlugin paramT) {
		// TODO Auto-generated method stub
		return 0;
	}

}