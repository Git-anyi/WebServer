package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 处理注册服务
 * @author uid
 *
 */

public class RegServlet extends HttpServlet {
	
	public void service(HttpRequest request,HttpResponse response) {
		
		System.out.println("RegServlet:开始处理注册");
		/*
		 * 注册流程
		 * 1:获取用户在注册页面上输入的注册信息
		 * 2:将注册信息写入user.dat文件中保存
		 * 3:响应客户端注册结果页面
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(request.getParameter("age"));
		
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		System.out.println("age:"+age);
		
		/*
		 * 将该用户信息写入文件user.dat中
		 * 该文件中每个用户占100字节,其中用户名,密码,昵称
		 * 为字符串,各占32字节,年龄为int值4字节.
		 */
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","rw");) {
			//先将就指针移动到文件末尾,以便追加新纪录
			raf.seek(raf.length());
			//写用户名
			byte[] data = username.getBytes("UTF-8");
			//将数组扩容到32字节
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//密码
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//昵称
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//年龄
			raf.writeInt(age);
			
			//3响应注册成功页面
			forward("/myweb/reg_success.html",request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("RegServlet:处理注册完毕");
	}
}
