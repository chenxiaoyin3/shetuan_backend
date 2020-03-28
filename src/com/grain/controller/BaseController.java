package com.grain.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hongyu.CommonAttributes;

import excel.exp.List2Excel;

/**
 * @author 束欢
 * @date 2010-05-06
 * */
public class BaseController {

	public static String getPath(String Path) {
		StringBuffer sf = new StringBuffer(Path); //
	
		if(isWin()){
		if (!Path.endsWith("\\")) {
			sf.append("\\");
		}
		}
		else if
			(Path.endsWith("\\")){
			int startIndex = sf.indexOf("\\");
			sf.replace(startIndex,startIndex+1,"");
		}
		else if(!Path.endsWith("/")){
		sf.append("/");	
		}
		createDir(Path); //删除目录

		return sf.toString();

	}
	
	  private static boolean isWin() {
    	  String OS = System.getProperty("os.name").toLowerCase();
    	  if(OS.indexOf("windows")>-1)
    		  return true;
    	  else return false;
    }
	  
	public static boolean createDir(String DirName) {
		File f = new File(DirName);
		if (!f.exists()) {
			return f.mkdirs();
		}
		return true;
	}
	
	public static void deleteDir(String path){
		try{
		File file = new File(path);
		if (!file.exists())	createDir(path);
		if(file!=null){
			String [] fileNames = file.list();
			for (int i=0; i<fileNames.length;i++){
				File delFile = new File(path + fileNames[i]);
				delFile.delete();
			}
		}

			
		}catch (Exception e){
			return;
		}
	}

	/**
	 * 导出Excel
	 * 
	 * @author 束欢
	 * @date 2010-05-06
	 * @param request
	 * @param response
	 * @param datas //
	 *            数据list
	 * @param fileName //
	 *            Excel文件名
	 * @param tableTitle //
	 *            Excel表标题
	 * @param configFile //
	 *            配置文件
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public void export2Excel(HttpServletRequest request,
			HttpServletResponse response, List datas, String fileName,
			String tableTitle, String configFile) throws IOException,
			UnsupportedEncodingException, FileNotFoundException {
		// 在临时目录下生成Excel：
		String userdir = getUserDir(request);
		String filefullname = userdir + fileName + ".xls";
		String cfgfiledir = getPath(getDir(request) + "pojo2excel");

		List2Excel l2e = List2Excel.getInstance(cfgfiledir + configFile,
				tableTitle, filefullname, datas);
		filefullname = l2e.generateExcel();
		/*int index = filefullname.lastIndexOf(".");
		String suffix=filefullname.substring(index+1);*/
		
		// 将压缩文件写出到OutStream中去：
//		fileName = new String(fileName.getBytes("GBK"),"iso8859-1"); 
//		response.addHeader("Content-Disposition", "attachment;filename="
//				+ fileName );

		
//		fileName = new String(fileName.getBytes("GBK"),"iso8859-1");   // ´ó¿Í»§¹ºÂòµç×ÓÈ¯Í³¼Æ.xls
//		fileName = new String(fileName.getBytes("iso8859-1"),"GBK");   // __________.xls
//		fileName = new String(fileName.getBytes("GBK"),"UTF-8");       // _              .xls
//		fileName = new String(fileName.getBytes("UTF-8"),"GBK"); 	   // _              .xls
//		fileName = new String(fileName.getBytes("UTF-8"),"iso8859-1"); // å¤§å®¢æ_·è´_ä¹°ç_µå__å_¸ç»_è®¡.xls
//		fileName = new String(fileName.getBytes("iso8859-1"),"UTF-8"); // __________.xls
//		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		
		
		response.setHeader("content-disposition",
				"attachment;" + "filename=" + URLEncoder.encode(fileName, "UTF-8"));	
		
		response.setHeader("Connection", "close");
		response.setHeader("Content-Type", "application/vnd.ms-excel");

		//String zipfilefullname = userdir + zipFileName;
		FileInputStream fis = new FileInputStream(new File(filefullname));
		BufferedInputStream bis = new BufferedInputStream(fis);
		ServletOutputStream sos = response.getOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(sos);

		byte[] bytes = new byte[1024];
		int i = 0;
		while ((i = bis.read(bytes, 0, bytes.length)) != -1) {
			bos.write(bytes);
		}
		bos.flush();
		bis.close();
		bos.close();

		// 删除缓存目录
		deleteDir(userdir);
	}
	
	public String getUserDir(HttpServletRequest request) {
		String path = getPath(getDir(request) + getXgr(request));
		createDir(path); //如果不存在，则自动创建新目录
		return path;
	}
	
	//获取系统的实际根路径
	public String getDir(HttpServletRequest request) {
		return getPath(request.getSession().getServletContext()
				.getRealPath("/"));
	}
	
	protected static String getXgr(javax.servlet.http.HttpServletRequest req) {
//		Principal prin = (Principal) req.getSession().getAttribute(CommonAttributes.Principal);
		String username = (String)req.getSession().getAttribute(CommonAttributes.Principal);
		return username;
	}

	/**
	 * 导出txt
	 * 以流的开式直接response,适用于数据量不是很大的情况
	 * */
	public void export2Txt(HttpServletResponse response, String fileName, String content) throws Exception {
		response.reset();
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".txt");
		ServletOutputStream outSTr = response.getOutputStream();
		BufferedOutputStream buff = new BufferedOutputStream(outSTr);
//		buff.write(content.getBytes("UTF-8"));
		buff.write(content.getBytes("GB2312"));
		buff.flush();
		buff.close();
		outSTr.close();
	}
}
