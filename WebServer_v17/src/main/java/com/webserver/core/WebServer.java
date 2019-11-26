package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer һ��ģ��Tomcat��������Web����
 * 
 * Web�������ڹ������в����������webapp(����Ӧ��)
 * ÿ��webapp����:ҳ��,�ز�,ҵ������.
 * 
 * �����׳Ƶ�һ����վʵ���Ͼ���������Web�����е�һ������Ӧ��.
 * 
 * Web��������һ��������ά����ͻ���(�����)������
 * �����HTTPЭ��Ľ�������,���Ը����û�����������
 * ��webapp�����ݲ������������Ӧ.
 * 
 * @author uid
 *
 */

public class WebServer {

	private ServerSocket server;
	
	//������ͻ��˽����̵߳��̳߳�
	private ExecutorService threadPool;

	/**
	 * ���췽��,���ڳ�ʼ�������
	 */
	public WebServer() {
		try {
			System.out.println("�������������...");
			server = new ServerSocket(8088);
			threadPool = Executors.newFixedThreadPool(50);
			System.out.println("������������!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start() {
		try {
			//ûд�������������������ǰ,������һ������
			while(true) {
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept();
				System.out.println("һ���ͻ���������!");

				//����һ���߳�������ÿͻ��˽���
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
