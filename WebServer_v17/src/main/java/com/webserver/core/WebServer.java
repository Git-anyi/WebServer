package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer 一个模拟Tomcat的轻量级Web容器
 * 
 * Web容器用于管理所有部署在这里的webapp(网络应用)
 * 每个webapp包含:页面,素材,业务代码等.
 * 
 * 我们俗称的一个网站实际上就是运行在Web容器中的一个网络应用.
 * 
 * Web容器的另一个作用是维持与客户端(浏览器)的连接
 * 与基于HTTP协议的交互操作,可以根据用户请求来调用
 * 各webapp的内容并处理后予以响应.
 * 
 * @author uid
 *
 */

public class WebServer {

	private ServerSocket server;
	
	//管理处理客户端交互线程的线程池
	private ExecutorService threadPool;

	/**
	 * 构造方法,用于初始化服务端
	 */
	public WebServer() {
		try {
			System.out.println("正在启动服务端...");
			server = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("服务端启动完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			//没写好整个处理请求的流程前,仅接收一次连接
			while(true) {
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了!");

				//启动一个线程来处理该客户端交互
				ClientHandler handler = new ClientHandler(socket);
				threadPool.execute(handler);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}

}
