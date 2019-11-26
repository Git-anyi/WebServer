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
 * ��Ӧ����
 * ��Ӧ�����ÿһ��ʵ�����ڱ�ʾ���ͻ��˷��͵�����
 * һ����Ӧ����������:״̬��,��Ӧͷ,��Ӧ����.
 * @author uid
 *
 */

public class HttpResponse {
	//״̬�������Ϣ����
	//״̬����,Ĭ��ֵΪ:200
	private int statusCode = 200;
	//״̬����,Ĭ��ֵΪ:OK
	private String statusReason = "OK";

	//��Ӧͷ�����Ϣ����
	private Map<String,String> headers = new HashMap<>();


	//��Ӧ���������Ϣ
	//���ĵ�ʵ���ļ�
	private File entity;

	//��������ص���Ϣ
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
	 * ����ǰ��Ӧ�����������һ����׼��HTTP��Ӧ��ʽ���͸��ͻ���
	 */
	public void flush() {
		try {
			/*
			 * ����һ����Ӧ��Ϊ����:
			 * 1:����״̬��
			 * 2:������Ӧͷ
			 * 3:������Ӧ����
			 */
			sendStatusLine();
			sendHeaders();
			sendContent();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����״̬��
	 */
	private void sendStatusLine() {
		try {
			//1����״̬��
			String line = "HTTP/1.1"+" "+statusCode+" "+statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);//written CR
			out.write(10);//Written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������Ӧͷ
	 */
	private void sendHeaders() {
		try {
			//2������Ӧͷ
			//����headers���Map��������Ӧͷ����
			Set<Entry<String,String>> set = headers.entrySet();
			for (Entry<String, String> e : set) {
				String name = e.getKey();
				String value = e.getValue();
				String line = name + ": " + value;
				out.write(line.getBytes("ISO8859-1"));
				out.write(13);
				out.write(10);
			}


			//��������CRLF��ʾ��Ӧͷ�������
			out.write(13);//written CR
			out.write(10);//Written LF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������Ӧ����
	 */
	private void sendContent() {
		//�жϴ��͹������ı������ڿ�
		if(entity != null) {
			try(FileInputStream  fis = new FileInputStream(entity);) {
				//3��Ӧ����
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
	 * ������Ӧ���ĵ�ʵ���ļ�
	 * ���õ�ͬʱ���Զ�����������Ӧͷ
	 * Content-Type��Content-Length
	 * @param entity
	 */
	public void setEntity(File entity) {
		this.entity = entity;
		//���������ļ����ö�Ӧ��Content-Type,Content-Length
		String fileName = entity.getName();
		int index = entity.getName().lastIndexOf(".")+1;
		String ext = fileName.substring(index);
		String type = HttpContext.getMimeType(ext);

		/*
		 * ���������ĵ�ͬʱ,��Ҫ����������Ӧͷ
		 * Content-Type��Content-Length��˵��
		 * ���ĵ������Լ�����
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
	 * ����һ��Ҫ���͵���Ӧͷ��Ϣ
	 * @param name ��Ӧͷ������
	 * @param value ��Ӧͷ��Ӧ��ֵ
	 */
	public void putHeaders(String name,String value) {
		this.headers.put(name, value);
	}

}
