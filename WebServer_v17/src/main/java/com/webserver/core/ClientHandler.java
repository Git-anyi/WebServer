package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

/**
 * 该线程用于处理指定客户端的交互
 * @author uid
 *
 */

public class ClientHandler implements Runnable {
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			/*
			 * 处理客户端交互就三步:
			 * 1:准备工作(解析请求)
			 * 2:处理请求
			 * 3:响应客户端
			 */
			//1 准备工作
			//1.1 解析请求
			HttpRequest request = new HttpRequest(socket);

			//1.2创建响应对象
			//将客户端请求的该资源响应给客户端
			HttpResponse response = new HttpResponse(socket);

			//2处理请求
			//通过request获取请求的资源的抽象路径
			String path = request.getRequestURI();
			
			HttpServlet servlet = ServerContext.getServlet(path);
			//首先判断该请求是否为请求业务
			if(servlet != null) {
				//请求注册操作
				
				servlet.service(request,response);
			
			}else {
				
				//从webapps目录下根据抽象路径去表示该资源文件
				File file = new File("./webapps"+path);
				//判断用户请求的资源是否存在
				if(file.exists()) {
					//将该资源响应客户端
					System.out.println("该资源已找到");

					//将用户请求的资源设置到response中
					response.setEntity(file);

					System.out.println("响应发送完毕!");

				}else {
					/*
					 * HTTP/1.1 404 NOT FOUND(CRLF)
					 * Content-Type: text/html(CRLF)
					 * Content-Length: xxxx(CRLF)(CRLF)
					 * 10101010101001(404页面内容)
					 */
					//响应404
					System.out.println("该资源不存在!");

					File notFoundPage = new File("./webapps/root/404.html");

					//将404页面设置到response中准备响应
					response.setEntity(notFoundPage);

					//设置状态代码和描述为404的
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");

				}
			}

			//3响应客户端
			response.flush();

		}catch(EmptyRequestException e) {
			//捕获到空请求忽略所有后续操作,直接与客户端断开
			System.out.println("空请求...");
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally {
			//当与客户端交互完毕后与其断开连接
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
