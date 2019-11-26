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
 * ���߳����ڴ���ָ���ͻ��˵Ľ���
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
			 * ����ͻ��˽���������:
			 * 1:׼������(��������)
			 * 2:��������
			 * 3:��Ӧ�ͻ���
			 */
			//1 ׼������
			//1.1 ��������
			HttpRequest request = new HttpRequest(socket);

			//1.2������Ӧ����
			//���ͻ�������ĸ���Դ��Ӧ���ͻ���
			HttpResponse response = new HttpResponse(socket);

			//2��������
			//ͨ��request��ȡ�������Դ�ĳ���·��
			String path = request.getRequestURI();
			
			HttpServlet servlet = ServerContext.getServlet(path);
			//�����жϸ������Ƿ�Ϊ����ҵ��
			if(servlet != null) {
				//����ע�����
				
				servlet.service(request,response);
			
			}else {
				
				//��webappsĿ¼�¸��ݳ���·��ȥ��ʾ����Դ�ļ�
				File file = new File("./webapps"+path);
				//�ж��û��������Դ�Ƿ����
				if(file.exists()) {
					//������Դ��Ӧ�ͻ���
					System.out.println("����Դ���ҵ�");

					//���û��������Դ���õ�response��
					response.setEntity(file);

					System.out.println("��Ӧ�������!");

				}else {
					/*
					 * HTTP/1.1 404 NOT FOUND(CRLF)
					 * Content-Type: text/html(CRLF)
					 * Content-Length: xxxx(CRLF)(CRLF)
					 * 10101010101001(404ҳ������)
					 */
					//��Ӧ404
					System.out.println("����Դ������!");

					File notFoundPage = new File("./webapps/root/404.html");

					//��404ҳ�����õ�response��׼����Ӧ
					response.setEntity(notFoundPage);

					//����״̬���������Ϊ404��
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");

				}
			}

			//3��Ӧ�ͻ���
			response.flush();

		}catch(EmptyRequestException e) {
			//���񵽿�����������к�������,ֱ����ͻ��˶Ͽ�
			System.out.println("������...");
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally {
			//����ͻ��˽�����Ϻ�����Ͽ�����
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
