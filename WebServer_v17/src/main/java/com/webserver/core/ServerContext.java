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
 * ����洢���з������ص�������Ϣ
 * @author uid
 *
 */

import com.webserver.servlet.HttpServlet;

public class ServerContext {
	/*
	 * �洢����Servlet
	 * key:��Ӧ������·��
	 * value:����������servlet
	 */
	private static Map<String,HttpServlet> servletMapping = new HashMap<>();
	
	static {
		//��ɳ�ʼ��
		initServletMapping();
	}
	
	private static void initServletMapping() {
//		servletMapping.put("/myweb/reg", new RegServlet());
//		servletMapping.put("/myweb/login", new LoginServlet());
		
		/*
		 * ͨ������conf/servlets.xml�ļ�����ʼ��
		 * ���Ƚ�����ǩ�µ�����<servlet>��ǩ��ȡ����,
		 * �������е�path���Ե�ֵ��Ϊkey
		 * ��className���Ե�ֵ�õ������÷�������ڸ���,
		 * ������ʵ����,��ʵ������Servlet��Ϊvalue.
		 * ���浽servletMapping����ɳ�ʼ��.
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
	 * ���ݸ���������·����ȡ��Ӧ�����ҵ���Servlet
	 * @param path
	 * @return
	 */
	public static HttpServlet getServlet(String path) {
		return servletMapping.get(path);
	}
}
