package com.hongyu.util.liyang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.hongyu.CommonAttributes;

import excel.exp.List2Excel;

public class ExcelHelper {
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
			HttpServletResponse response, String fileName
			) throws IOException,
			UnsupportedEncodingException, FileNotFoundException {
		String userdir = getUserDir(request);
		String filefullname = userdir + fileName + ".xls";
	
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
//		 删除缓存目录
		deleteDir(userdir);
	}
	
	public static String getUserDir(HttpServletRequest request) {
		String path = getPath(getDir(request) + getXgr(request));
		createDir(path); //如果不存在，则自动创建新目录
		return path;
	}
	
	//获取系统的实际根路径
	public static String getDir(HttpServletRequest request) {
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
	public static void export2Txt(HttpServletResponse response, String fileName, String content) throws Exception {
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
	public static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		short borderColor = IndexedColors.GREY_50_PERCENT.getIndex();

		// 列表名格式
		CellStyle style;
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 20);
		titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(titleFont);
		styles.put("title", style);

		// 列表头格式
		Font headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		styles.put("header", style);

		// 列表数据格式:
		Font listdataFont = wb.createFont();
		listdataFont.setFontHeightInPoints((short) 10);
		listdataFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		style = wb.createCellStyle();
			// 居中显示：
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			// 设置边框：
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(borderColor);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(borderColor);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(borderColor);
		style.setFont(listdataFont);
		styles.put("listdata", style);

		return styles;
	}
	
	/**
	 * 测试方法
	 * @param args
	 */
	public static void main(String[] args) {
		Workbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = (HSSFSheet) workbook.createSheet("当日接团统计");
		Map<String,CellStyle> styles = ExcelHelper.createStyles(workbook);
		/*初始化全局变量*/
		int cellNum = 15;
		List<String> list = new ArrayList<>();
		list.add("产品编号");
		list.add("产品名称");
		list.add("发团日期");
		list.add("回团日期");
		list.add("接团计调");
		
		list.add("门店名称");
		list.add("接牌人");
		list.add("游客人数");
		list.add("报名计调");
		list.add("报名收入");
		
		list.add("单团收入");
		list.add("单团成本");
		list.add("单团利润");
		list.add("人均利润");
		list.add("利润率");
		
		List<String> keyList = new ArrayList<>();
		keyList.add("linePn");
		keyList.add("lineName");
		keyList.add("fatuandate");
		keyList.add("huituandate");
		keyList.add("jietuanjidiao");
		
		keyList.add("storeName");
		keyList.add("jiepairen");
		keyList.add("people");
		keyList.add("baomingjidiao");
		keyList.add("baomingshouru");
		
		keyList.add("dantuanshouru");
		keyList.add("dantuanchengben");
		keyList.add("dantuanlirun");
		keyList.add("renjunlirun");
		keyList.add("lirunlv");
		/*初始化标题*/
		HSSFRow titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(24);
		HSSFCell titleCell = titleRow.createCell(0);
		titleCell.setCellValue("当日接团统计表");
		titleCell.setCellStyle(styles.get("title"));
		CellRangeAddress region=new CellRangeAddress(0,0,0,cellNum);
		sheet.addMergedRegion(region);
		
		/*初始化列表头*/
		HSSFRow headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(18);
		//header.setRowStyle(styles.get("header"));
		sheet.setColumnWidth(0, 10 * 256);
		/*初始化序列号*/
		HSSFCell numHeaderCell = headerRow.createCell(0);
		numHeaderCell.setCellValue("序号");
		numHeaderCell.setCellStyle(styles.get("header"));
		for(int i=0;i<list.size();i++){
			HSSFCell headerCell = headerRow.createCell(i+1);
			sheet.setColumnWidth(i + 1, 15 * 256);
			headerCell.setCellValue(list.get(i));
			headerCell.setCellStyle(styles.get("header"));
		}
		
		/*将数据行写入到当前表中*/
		List<Map<String, Object>> dataList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("linePn", "pn");
		map.put("lineName", "lineName");
		map.put("fatuandate", "fatuandate");
		map.put("huituandate", "huituandate");
		map.put("jietuanjidiao", "jietuanjidiao");
		map.put("storeName", "storeName");
		map.put("jiepairen", "jiepairen");
		map.put("people", "2大1小");
		map.put("baomingjidiao", "计调1");
		map.put("baomingshouru", "500");
		map.put("dantuanshouru", "800");
		map.put("dantuanchengben", "500");
		map.put("dantuanlirun", "300");
		map.put("renjunlirun", "50");
		map.put("lirunlv", "10%");
		dataList.add(map);
		Map<String, Object> map1 = new HashMap<>();
		map1.put("linePn", "pn");
		map1.put("lineName", "lineName");
		map1.put("fatuandate", "fatuandate");
		map1.put("huituandate", "huituandate");
		map1.put("jietuanjidiao", "jietuanjidiao");
		map1.put("storeName", "storeName");
		map1.put("jiepairen", "jiepairen");
		map1.put("people", "1大2小");
		map1.put("baomingjidiao", "计调2");
		map1.put("baomingshouru", "300");
		map1.put("dantuanshouru", "800");
		map1.put("dantuanchengben", "500");
		map1.put("dantuanlirun", "300");
		map1.put("renjunlirun", "50");
		map1.put("lirunlv", "10%");
		dataList.add(map1);
		int currRowNum = 2;
		for (int i = 0; i < dataList.size(); i++) {
			Row row = sheet.createRow(currRowNum++);
			row.setHeightInPoints(15);

			// 为每一行添加序号:
			Cell numCell = row.createCell(0);
			numCell.setCellStyle(styles.get("listdata"));
			numCell.setCellValue(i+1);
			
			HashMap<String, Object> tMap = (HashMap<String, Object>) dataList.get(i);
			// 对于每一行的每一列进行POJO的赋值：
			for (int j = 0; j < cellNum; j++) {
				String colname = (String) keyList.get(j);
				Cell dataCell = row.createCell(j + 1);
				dataCell.setCellStyle(styles.get("listdata"));
				Object obj =  tMap.get(colname);
				dataCell.setCellValue(obj==null?"":obj.toString());
			}
			
		}
		//合并行
		/*依次合并前五列*/
		CellRangeAddress regionColumn1=new CellRangeAddress(2,3,1,1);
		CellRangeAddress regionColumn2=new CellRangeAddress(2,3,2,2);
		CellRangeAddress regionColumn3=new CellRangeAddress(2,3,3,3);
		CellRangeAddress regionColumn4=new CellRangeAddress(2,3,4,4);
		CellRangeAddress regionColumn5=new CellRangeAddress(2,3,5,5);
		/*依次合并最后五列*/
		CellRangeAddress regionColumn12=new CellRangeAddress(2,3,11,11);
		CellRangeAddress regionColumn13=new CellRangeAddress(2,3,12,12);
		CellRangeAddress regionColumn14=new CellRangeAddress(2,3,13,13);
		CellRangeAddress regionColumn15=new CellRangeAddress(2,3,14,14);
		CellRangeAddress regionColumn16=new CellRangeAddress(2,3,15,15);
		
		sheet.addMergedRegion(regionColumn1);
		sheet.addMergedRegion(regionColumn2);
		sheet.addMergedRegion(regionColumn3);
		sheet.addMergedRegion(regionColumn4);
		sheet.addMergedRegion(regionColumn5);
		sheet.addMergedRegion(regionColumn12);
		sheet.addMergedRegion(regionColumn13);
		sheet.addMergedRegion(regionColumn14);
		sheet.addMergedRegion(regionColumn15);
		sheet.addMergedRegion(regionColumn16);
		String filePath="D:\\java\\当日接团统计.xls";//文件路径
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(filePath);
			workbook.write(out);//保存Excel文件
			out.close();
			System.out.println("OK!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			
		}
		 
	}
}
