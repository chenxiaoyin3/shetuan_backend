package excel.exp;
import java.util.Comparator;

/**
 * 
 * @author zyy
 * @version 1.0
 * @since 2010-4-26
 * 工具类：用于Excel各列名称的排序
 */
@SuppressWarnings("rawtypes")
public class PropertiesCompator implements Comparator {

	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		String str1 = (String)o1;
		String str2 = (String)o2;
		
		int index = str1.indexOf(" ");
		String num1 = str1.substring(0, index);
		Integer intnum1 = Integer.parseInt(num1);
		
		index = str2.indexOf(" ");
		String num2 = str2.substring(0, index);
		Integer intnum2 = Integer.parseInt(num2);
		
		return (intnum1.equals(intnum2))?0:((intnum1>intnum2)?1:-1);
	}

}
