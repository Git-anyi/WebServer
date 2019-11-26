package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ����Servlet�ĳ���,�涨��ServletӦ�����еĹ����Լ��ɸ��õĴ���.
 * @author uid
 *
 */

public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	
	/**
	 * ������Ӧ��ת��ָ����ҳ��.
	 * @param path	��·���Ǵ�webapps�￪ʼ��
	 * @param request
	 * @param response
	 */
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File fail = new File("./webapps"+path);
		response.setEntity(fail);
	}
}
