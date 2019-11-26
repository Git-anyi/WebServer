package com.webserver.http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * 这里定义所有与HTTP协议相关的定义
 * @author uid
 *
 */

public class HttpContext {
	/*
	 * 资源后缀与Content-Type的对应关系
	 * key:资源的后缀名
	 * value:对应的Content-Type头的值
	 */
	private static Map<String,String> mimeMapping = new HashMap<>();
	/*
	 * 静态块
	 * 静态块用于初始化静态资源
	 */
	static {
		initmimeMapping();
	}

	/**
	 * 初始化MIME
	 */
	private static void initmimeMapping() {
		try {
			//		mimeMapping.put("html", "text/html");
			//		mimeMapping.put("css", "text/css");
			//		mimeMapping.put("js", "application/javascript");
			//		mimeMapping.put("png", "image/png");
			//		mimeMapping.put("gif", "image/gif");
			//		mimeMapping.put("jpg", "image/jpeg");

			/*
			 * 解析项目目录下conf/web.xml文件
			 * 将根标签下所有名为<mime-mapping>的子标签
			 * 读取出来,并将它的子标签:
			 * <extension>中间的文本作为key
			 * <mime-type>中间的文本作为value
			 * 保存到mimeMapping这个Map中完成初始化
			 */

			SAXReader reader = new SAXReader();
			File file = new File("./conf/web.xml");
			//生成树结构
			Document doc = reader.read(file);
			//Element的每一个实例用于表示XML文档中的一个元素(一对标签),它提供了一组获取该标签相关信息的方法
			Element root = doc.getRootElement();
			//从根标签中获取所有<mime-mapping>标签
			List<Element> emps = root.elements("mime-mapping");

			//遍历每一个<mime-mapping>标签key和value
			for (Element element : emps) {
				mimeMapping.put(String.valueOf(element.elementText("extension")), element.elementText("mime-type"));
			}
			System.out.println(mimeMapping.size());
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	/**
	 * 根据给定的资源的后缀名获取对应的Content-Type值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mimeMapping.get(ext);

	}

	public static void main(String[] args) {
		String type = getMimeType("3dml");
		System.out.println(type);
	}


}
