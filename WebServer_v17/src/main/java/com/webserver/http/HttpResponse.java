package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 响应对象
 * 响应对象的每一个实例用于表示给客户端发送的内容
 * 一个响应包含三部分:状态行,响应头,响应正文.
 * @author uid
 *
 */

public class HttpResponse {
	//状态行相关信息定义
	//状态代码,默认值为:200
	private int statusCode = 200;
	//状态描述,默认值为:OK
	private String statusReason = "OK";

	//响应头相关信息定义
	private Map<String,String> headers = new HashMap<>();


	//响应正文相关信息
	//正文的实体文件
	private File entity;

	//和连接相关的信息
	private Socket socket;
	private OutputStream out;

	public HttpResponse(Socket socket) {
		try {

			this.socket = socket;
			this.out = socket.getOutputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将当前响应对象的内容以一个标准的HTTP响应格式发送给客户端
	 */
	public void flush() {
		try {
			/*
			 * 发送一个响应分为三步:
			 * 1:发送状态行
			 * 2:发送响应头
			 * 3:发送响应正文
			 */
			sendStatusLine();
			sendHeaders();
			sendContent();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送状态行
	 */
	private void sendStatusLine() {
		try {
			//1发送状态行
			String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);//written CR
			out.write(10);//Written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送响应头
	 */
	private void sendHeaders() {
		try {
			//2发送响应头
			//遍历headers这个Map将所有响应头发送
			Set<Entry<String,String>> set = headers.entrySet();
			for (Entry<String, String> e : set) {
				String name = e.getKey();
				String value = e.getValue();
				String line = name + ": " + value;
				out.write(line.getBytes("ISO8859-1"));
				out.write(13);
				out.write(10);
			}


			//单独发送CRLF表示响应头发送完毕
			out.write(13);//written CR
			out.write(10);//Written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送响应正文
	 */
	private void sendContent() {
		//判断传送过来的文本不等于空
		if(entity != null) {
			try(FileInputStream  fis = new FileInputStream(entity);) {
				//3响应正文
				int len = -1;
				byte[] data = new byte[1024*10];
				while((len = fis.read(data)) != -1) {
					out.write(data,0,len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public File getEntity() {
		return entity;
	}

	/**
	 * 设置响应正文的实体文件
	 * 设置的同时会自动包含两个响应头
	 * Content-Type和Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		//根据正文文件设置对应的Content-Type,Content-Length
		String fileName = entity.getName();
		int index = entity.getName().lastIndexOf(".")+1;
		String ext = fileName.substring(index);
		String type = HttpContext.getMimeType(ext);

		/*
		 * 设置了正文的同时,就要设置两个响应头
		 * Content-Type与Content-Length来说明
		 * 正文的类型以及长度
		 */
		putHeaders("Content-Type", type);
		putHeaders("Content-Length", entity.length()+"");

	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	/**
	 * 设置一个要发送的响应头信息
	 * @param name 响应头的名字
	 * @param value 响应头对应的值
	 */
	public void putHeaders(String name,String value) {
		this.headers.put(name, value);
	}

}
