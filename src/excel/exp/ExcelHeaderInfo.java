package excel.exp;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * 
 * @author zyy
 * @version 1.0
 * @since 2010-4-26
 * 工具类，
 * 用于读取配置文件--关于POJO与要导出Excel文件各列的对应关系
 */
public class ExcelHeaderInfo {

	private static SortedProperties p = null;
	private static String file_path = null;
	private static ExcelHeaderInfo instance = null;

	private ExcelHeaderInfo() {
	}
	
	protected static Properties init(){
		try {
//			String file_path = "src/dao/org/project/command/state.xml";
			p = new SortedProperties();
			p.loadFromXML(new FileInputStream(file_path)) ;
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return p;
	}
	
	/**
	 * 单例
	 * 当文件名不同的时候需要重新加载.
	 * 当文件名相同且已有实例时不需要重新加载.
	 * 
	 * 改进：可以做一个Map放在这里，容量10个这样。
	 */
	public static ExcelHeaderInfo getInstance(String file_path) {
		if (!file_path.equals(ExcelHeaderInfo.file_path) || instance==null) {
			instance = new ExcelHeaderInfo();
			ExcelHeaderInfo.file_path = file_path;
			init();
		}
		
		return instance;
	}

	public static String getFile_path() {
		return file_path;
	}

	public static void setFile_path(String file_path){
		ExcelHeaderInfo.file_path = file_path;
	}

	public Properties getProperties() {
		return p;
	}

}