package com.webserver.servlet;

import java.io.File;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 所有Servlet的超类,规定了Servlet应当具有的功能以及可复用的代码.
 * @author uid
 *
 */

public abstract class HttpServlet {
	public abstract void service(HttpRequest request,HttpResponse response);
	
	/**
	 * 设置响应跳转到指定的页面.
	 * @param path	该路径是从webapps里开始的
	 * @param request
	 * @param response
	 */
	public void forward(String path,HttpRequest request,HttpResponse response) {
		File fail = new File("./webapps"+path);
		response.setEntity(fail);
	}
}
