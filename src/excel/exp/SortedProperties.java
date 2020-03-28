package excel.exp;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

/**
 * 
 * @author zyy
 * @version 1.0
 * @since 2010-4-26
 * 工具类：用于Excel各列名称的排序
 */
public class SortedProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized Enumeration<Object> keys() {
		// TODO Auto-generated method stub
		Enumeration keysEnum = super.keys();
		Vector keyList = new Vector();
		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		Collections.sort(keyList,new PropertiesCompator());
		return keyList.elements();
	}
}
