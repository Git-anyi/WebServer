package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * ����ע�����
 * @author uid
 *
 */

public class RegServlet extends HttpServlet {
	
	public void service(HttpRequest request,HttpResponse response) {
		
		System.out.println("RegServlet:��ʼ����ע��");
		/*
		 * ע������
		 * 1:��ȡ�û���ע��ҳ���������ע����Ϣ
		 * 2:��ע����Ϣд��user.dat�ļ��б���
		 * 3:��Ӧ�ͻ���ע����ҳ��
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
		 * �����û���Ϣд���ļ�user.dat��
		 * ���ļ���ÿ���û�ռ100�ֽ�,�����û���,����,�ǳ�
		 * Ϊ�ַ���,��ռ32�ֽ�,����Ϊintֵ4�ֽ�.
		 */
		try(RandomAccessFile raf = new RandomAccessFile("user.dat","rw");) {
			//�Ƚ���ָ���ƶ����ļ�ĩβ,�Ա�׷���¼�¼
			raf.seek(raf.length());
			//д�û���
			byte[] data = username.getBytes("UTF-8");
			//���������ݵ�32�ֽ�
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//����
			data = password.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//�ǳ�
			data = nickname.getBytes("UTF-8");
			data = Arrays.copyOf(data, 32);
			raf.write(data);
			
			//����
			raf.writeInt(age);
			
			//3��Ӧע��ɹ�ҳ��
			forward("/myweb/reg_success.html",request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		System.out.println("RegServlet:����ע�����");
	}
}
