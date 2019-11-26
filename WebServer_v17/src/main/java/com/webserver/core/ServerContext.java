package com.webserver.core;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 该类存储所有服务端相关的配置信息
 * @author uid
 *
 */

import com.webserver.servlet.HttpServlet;

public class ServerContext {
	/*
	 * 存储所有Servlet
	 * key:对应的请求路径
	 * value:处理该请求的servlet
	 */
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();
	
	static {
		//完成初始化
		initServletMapping();
	}
	
	private static void initServletMapping() {
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
		
		/*
		 * 通过解析conf/servlets.xml文件来初始化
		 * 首先将根标签下的所有<servlet>标签获取出来,
		 * 并将其中的path属性的值作为key
		 * 将className属性的值得到后利用反射加载在该类,
		 * 并进行实例化,将实例化的Servlet作为value.
		 * 保存到servletMapping中完成初始化.
		 */
		
		try {
			SAXReader reader = new SAXReader();
			File file = new File("./conf/servlets.xml");
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			
			List<Element> emp = root.elements("servlet");
			
			for (Element servletEmp : emp) {
				String path = servletEmp.attributeValue("path");
				String className = servletEmp.attributeValue("className");
				
				Class cls = Class.forName(className);
				HttpServlet servlet = (HttpServlet) cls.newInstance();
				servletMapping.put(path, servlet);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据给定的请求路径获取对应处理该业务的Servlet
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
}
