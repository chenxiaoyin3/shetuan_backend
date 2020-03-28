package excel.exp;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 
 * @author zyy
 * @version 1.0
 * @since 2010-4-26
 * 工具类,将List对象导出为Excel
 */
public class List2Excel {
	private Workbook wb = null;
	private Map<String, CellStyle> styles = null; // Excel文件中的各种样式
	private static String headerCfgPath = null; // Excel文件的列名与POJO的属性的对应关系--配置文件名
	private String titileName = null; // Excel文件的表头名称
	private List<Object> datas = null; // 列表数据
	private String excelFilename = null; // 导出的Excel文件名
	private static List2Excel instance = null;

	private List2Excel(String headerCfgPath, String titileName,
			String excelFilename, List<Object> datas) {
		List2Excel.headerCfgPath = headerCfgPath;
		wb = new HSSFWorkbook();
		styles = createStyles(wb);
		this.titileName = titileName;
		this.excelFilename = excelFilename;
		this.datas = datas;
		ExcelHeaderInfo.getInstance(headerCfgPath);
	}

	/**
	 * 单例 当文件名不同的时候需要重新加载. 当文件名相同且已有实例时不需要重新加载.
	 * 
	 * @param headerCfgPath	 POJO与Excel列表各列的对应关系--配置文件
	 * @param titileName	 Excel表头名称
	 * @param excelFilename	 Excel文件名
	 * @param datas			 数据
	 * @return
	 */
	public static List2Excel getInstance(String headerCfgPath,
			String titileName, String excelFilename, List<Object> datas) {
//		if ((!headerCfgPath.equals(List2Excel.headerCfgPath))
//				|| instance == null) {
			instance = new List2Excel(headerCfgPath, titileName, excelFilename,
					datas);
//		}
		return instance;
	}

	/**
	 * 将数据生成到指定路径的Excel文件中:
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String generateExcel() {
		// Excel表中的数据:
		// Excel表中的列头：
		ExcelHeaderInfo sf = ExcelHeaderInfo.getInstance(headerCfgPath);
		Properties pro = sf.getProperties();
		Enumeration eunma = pro.keys();
		List columns = new ArrayList();

		while (eunma.hasMoreElements()) {
			String keyname = (String) eunma.nextElement();
			// keyname = keyname.substring(keyname.indexOf(" ")+1,
			// keyname.length());
			columns.add(keyname);
		}
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH mm ss");
		Sheet sheet = wb.createSheet(titileName + format.format(date));

		// turn off gridlines
		sheet.setDisplayGridlines(false);
		sheet.setPrintGridlines(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);

		// the following three statements are required only for HSSF
		sheet.setAutobreaks(true);
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);

		Row headerRow = null;
		int colnum = pro.size();
		int rownum = 0;
		Character c = new Character((char) ('A'+colnum));
		// the header row: centered text in 48pt font
		if(titileName != null) {
			Row titleRow = sheet.createRow(0);
			titleRow.setHeightInPoints(24);
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(titileName);
			titleCell.setCellStyle(styles.get("title"));
			sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$" + c.charValue()
					+ "$1"));
			headerRow = sheet.createRow(1);
			rownum = 2;
		} else {
			headerRow = sheet.createRow(0);
			rownum = 1;
		}
		
		// header with list titles
		headerRow.setHeightInPoints(18);

		// 添加序号列:
		sheet.setColumnWidth(0, 10 * 256); // the column is 5 characters wide
		Cell numHeaderCell = headerRow.createCell(0);
		numHeaderCell.setCellValue("序号");
		numHeaderCell.setCellStyle(styles.get("header"));
		// 添加列名:
		for (int i = 0; i < colnum; i++) {
			String keyname = (String) columns.get(i);
			// set column widths, the width is measured in units of 1/256th of a
			// character width
			sheet.setColumnWidth(i + 1, 15 * 256); // the column is 5
													// characters wide
			Cell headerCell = headerRow.createCell(i + 1);
			headerCell.setCellValue(pro.getProperty(keyname));
			headerCell.setCellStyle(styles.get("header"));
		}

		// list datas rows:
		
		int size = this.datas.size();
		for (int i = 0; i < size; i++) {
			Row row = sheet.createRow(rownum++);
			row.setHeightInPoints(15);

			// 为每一行添加序号:
			Cell numCell = row.createCell(0);
			numCell.setCellStyle(styles.get("listdata"));
			numCell.setCellValue(i+1);

			// 对于每一行的每一列进行POJO的赋值：
			for (int j = 0; j < colnum; j++) {
				String keyname = (String) columns.get(j);
				String colname = keyname.substring(keyname.indexOf(" ") + 1,
						keyname.length());

				Cell dataCell = row.createCell(j + 1);
				dataCell.setCellStyle(styles.get("listdata"));
				Object obj = getGetMethodInvoke(this.datas.get(i),colname);
				dataCell.setCellValue(obj==null?"":obj.toString());
			}
		}

		// 将信息写出excel:
		if (wb instanceof HSSFWorkbook)
			excelFilename += "x";

		FileOutputStream out;
		try {
			out = new FileOutputStream(excelFilename);
			try {
				wb.write(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return excelFilename;
	}

	/**
	 * cell styles used for formatting calendar sheets 
	 * 获得Excel文件的格式
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
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
	 * 获得POJO的get方法： param obj 作用的POJO param colenname 需要获得的成员变量名称
	 * @modify 束欢
	 * @date 2010-05-06
	 * 扩展：能够多级子对象
	 */
	private Object getGetMethodInvoke(Object obj, String colenname) {
		Method method = null;
		Object returnObj = null;
		try {
			int index = 0;
			if ((index = colenname.lastIndexOf(".")) != -1) {
				String objname = colenname.substring(0, index);
				String objcolname = upperCase(colenname.substring(index + 1));

				Object childobj = getGetMethodInvoke(obj, objname);
				method = childobj.getClass().getMethod("get" + objcolname);

				if (method != null) {
					returnObj = method.invoke(childobj);
				}
			} else {
				method = obj.getClass().getMethod("get" + upperCase(colenname));
				if (method != null) {
					returnObj = method.invoke(obj);
				}
			}

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// do nothing
		}
		return returnObj;
	}
	
	/**
	 * 首字符大写
	 * @author 束欢
	 * @date 2010-05-06
	 * @param attribute
	 * @return
	 */
	private static String upperCase(String attribute) {

	   String upStr = attribute.toUpperCase();   
       StringBuffer buf = new StringBuffer(attribute.length());   
       if(attribute.charAt(0) == upStr.charAt(0))
       {  // 首字符大写 
           return attribute;   
       }
       else
       {
    	   buf.append(upStr.charAt(0));
       }
       for(int i = 1; i < attribute.length(); i++)
       {   
           buf.append(attribute.charAt(i));   
       }   
       return buf.toString();   
	}

//	public static void main(String[] args) {
//		List<Object> datas = new ArrayList<Object>();
//		// 相关的数据
//		for (int i = 0; i < 10; i++) {
//			EDirectory edir = new EDirectory();
//			edir.setCdate("2010-10-09"); // 创建日期
//			edir.setCperson("朱元友"); // 创建人
//			edir.setId(100); // id
//			edir.setMdate("2010-09-09"); // 修改日期
//			edir.setMperson("朱元友"); // 修改人
//			edir.setPcoding("1098877"); // 商品编号
//			edir.setPdepict("这个是朱元友创建的哦"); // 商品描述
//			edir.setPmodel("1098"); // 商品型号
//			edir.setPname("模型名称" + i); // 商品名称
//			edir.setSap_encoding("13211234324"); // SAP编码
//			SCatalogs scat = new SCatalogs();
//			scat.setScatalogname("XXOO");
//			edir.setScatalog(scat);
//
//			datas.add(edir);
//		}
//		List2Excel l2e = List2Excel.getInstance(
//				"src/web/edirectoryExcelTitle.xml", "电子目录列表", "电子目录列表3.xls", datas);
//		l2e.generateExcel();
//		System.out.println(upperCase("hahahah"));
//	}
}
