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
 * ���ﶨ��������HTTPЭ����صĶ���
 * @author uid
 *
 */

public class HttpContext {
	/*
	 * ��Դ��׺��Content-Type�Ķ�Ӧ��ϵ
	 * key:��Դ�ĺ�׺��
	 * value:��Ӧ��Content-Typeͷ��ֵ
	 */
	private static Map<String,String> mimeMapping = new HashMap<>();
	/*
	 * ��̬��
	 * ��̬�����ڳ�ʼ����̬��Դ
	 */
	static {
		initmimeMapping();
	}

	/**
	 * ��ʼ��MIME
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
			 * ������ĿĿ¼��conf/web.xml�ļ�
			 * ������ǩ��������Ϊ<mime-mapping>���ӱ�ǩ
			 * ��ȡ����,���������ӱ�ǩ:
			 * <extension>�м���ı���Ϊkey
			 * <mime-type>�м���ı���Ϊvalue
			 * ���浽mimeMapping���Map����ɳ�ʼ��
			 */

			SAXReader reader = new SAXReader();
			File file = new File("./conf/web.xml");
			//�������ṹ
			Document doc = reader.read(file);
			//Element��ÿһ��ʵ�����ڱ�ʾXML�ĵ��е�һ��Ԫ��(һ�Ա�ǩ),���ṩ��һ���ȡ�ñ�ǩ�����Ϣ�ķ���
			Element root = doc.getRootElement();
			//�Ӹ���ǩ�л�ȡ����<mime-mapping>��ǩ
			List<Element> emps = root.elements("mime-mapping");

			//����ÿһ��<mime-mapping>��ǩkey��value
			for (Element element : emps) {
				mimeMapping.put(String.valueOf(element.elementText("extension")), element.elementText("mime-type"));
			}
			System.out.println(mimeMapping.size());
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

	/**
	 * ���ݸ�������Դ�ĺ�׺����ȡ��Ӧ��Content-Typeֵ
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
