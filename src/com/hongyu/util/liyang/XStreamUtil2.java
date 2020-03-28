package com.hongyu.util.liyang;

import com.hongyu.entity.ConfirmMessage;
import com.hongyu.entity.ConfirmMessageOrderCancel;
import com.hongyu.entity.InsureInfo;
import com.hongyu.entity.JtCancelOrderResponse;
import com.hongyu.entity.JtOrderResponse;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XStreamUtil2 {
	private static XStream xStream; 
	 public static final XmlFriendlyNameCoder nameCoder = new XmlFriendlyNameCoder(); 
	// 编码格式  
	 private static final String ENCODING = "UTF-8";  
    
	    //JVM加载类时会执行这些静态的代码块，如果static代码块有多个，JVM将按照它们在类中出现的先后顺序依次执行它们，每个代码块只会被执行一次。  
	    static{  
	        xStream = new XStream(new StaxDriver()); 
	        xStream.setupDefaultSecurity(xStream);
	        xStream.allowTypes(new Class[]{ConfirmMessage.class,ConfirmMessageOrderCancel.class,
	        		JtOrderResponse.class,JtCancelOrderResponse.class});
	        
	        /* 
	         * 使用xStream.alias(String name, Class Type)为任何一个自定义类创建到类到元素的别名 
	         * 如果不使用别名，则生成的标签名为类全名 
	         */  
	        xStream.alias("ChinaTourinsRequest", ConfirmMessage.class);
	        //xStream.alias("ChinaTourinsRequest", ConfirmMessageOrderCancel.class);
	        xStream.alias("insureInfo", InsureInfo.class);
	        xStream.alias("ChinaTourinsResponse", JtCancelOrderResponse.class);
	        //xStream.alias("ChinaTourinsResponse", JtCancelOrderResponse.class);
	        //将某一个类的属性，作为xml头信息的属性，而不是子节点  
//	        xStream.useAttributeFor(Address.class, "country");  
//	        //对属性取别名  
//	        xStream.aliasField("省", Address.class,"province");  
	    }  
	      
	    //xml转java对象  
	    public static Object xmlToBean(String xml){  
	        return xStream.fromXML(xml);  
	    }  
	      
	    //java对象转xml  
	    public static String beanToXml(Object obj){  
	        return  xStream.toXML(obj);
	    }  
}
