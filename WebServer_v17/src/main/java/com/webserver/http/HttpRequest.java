package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * �������
 * �����ÿһ��ʵ�����ڱ�ʾ�ͻ��˷��͹�����һ�������
 * http���������.
 * ÿ�������������ֹ���:������,��Ϣͷ,��Ϣ����.
 * @author uid
 *
 */

public class HttpRequest {
	/*
	 * �����������Ϣ
	 */
	//����ʽ
	private String method;
	//������Դ�ĳ���·��
	private String uri;
	//����ʹ�õ�Э��汾
	private String protocol;

	//��������uri�е����󲿷�("?"��������,���uriû��"?"��ô��uriֵһ��)
	private String requestURI;
	//��������uri�еĲ�������("?"�Ҳ�����)
	private String queryString;;

	//��������ͻ����ύ����������
	private Map<String,String> parameters = new HashMap<>();

	/*
	 * ��Ϣͷ�����Ϣ
	 */
	private Map<String,String> headers = new HashMap<>();

	/*
	 * ��Ϣ���������Ϣ
	 */

	/*
	 * �������ȹص�����
	 */
	private Socket socket;
	//ͨ��socket��ȡ��������,���ڶ�ȡ�ͻ��˷��͵���Ϣ
	private InputStream in;


	/*
	 * ���췽��,������ʼ���������
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		System.out.println("HttpRequest:��ʼ��������...");
		try {
			this.socket = socket;
			this.in = socket.getInputStream();

			/*
			 * ����һ�������Ϊ����:
			 * 1:����������
			 * 2:������Ϣͷ
			 * 3:������Ϣ����
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		}catch(EmptyRequestException e){
			//������������г����˿�����,���쳣�׳���clientHandler
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("HttpRequest:�����������!");
	}
	/**
	 * ���������еĵ�һ��:����������
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:��ʼ����������...");

		try {
			//��ȡ�ͻ��˷��͹����ĵ�һ���ַ���(����������)
			String line = readLine();

			/*
			 * ������ص�line��һ�����ַ���,˵���˴�������
			 * һ��������,Ӧ�������׳�һ���������쳣
			 */
			if("".equals(line)) {
				throw new EmptyRequestException();
			}


			System.out.println(line);
			/*
			 * �������а��տո���Ϊ������,���ֱ�ֵ��
			 * ��Ӧ����������:method,uri,protocol��
			 * 
			 * ������ʽ�пհ��ַ���:"\s"
			 */
			String[] data = line.split("\\s");
			method = data[0];
			uri = data[1];

			//��ȡ���������еĳ���·�����ֺ��һ������
			parseURI();

			protocol = data[2];
			System.out.println("method:"+method);
			System.out.println("uri:"+uri);
			System.out.println("protocol:"+protocol);

		}catch(EmptyRequestException e) {
			//��������������쳣,����ʱֱ���׳������췽��
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("HttpRequest:�����н������!");
	}

	/**
	 * ��һ������uri
	 * ���ڿͻ���form��ʹ��ge��ʽ�ύʱ,�Ὣ����
	 * ����Ϣƴ����uri��("?"�ָ����󲿷ֺͲ�������)
	 * ���uri������������ǵ��ǲ���,�����Ǵ��������
	 * ��������Ҫ����һ���������.
	 */
	private void parseURI() {
		System.out.println("HttpRequest:��һ������URI...");
		/*
		 * uri�����������:���в����Ͳ����в���
		 * ������������Ҫ�ж�uri�Ƿ��в���,����������
		 * �ı�־����uri�Ƿ���"?",�о�˵���Ǻ��в�����.
		 * 
		 * ��������в���,��ô��ֱ�ӽ�uri��ֵ����requestURI
		 * ����,queryString��parameters�����踳ֵ��.
		 * 
		 * ����в���,����Ҫ���²���:
		 * 1:���Ȱ���"?"��uri���Ϊ������(����Ͳ���)
		 *   Ȼ��"?"�������󲿷ָ���requestURI
		 *   "?"�Ҳ�Ĳ������ָ�ֵ��queryString
		 *   
		 * 2:�ڽ�queryString(��������)��һ�����
		 *   ���Ƚ�queryString����"&"��ֳ����ɸ�����
		 *   ÿ�������ٰ���"="���Ϊ�������Ͳ���ֵ,
		 *   Ȼ�󽫲�������Ϊkey,����ֵ��Ϊvalue,����
		 *   ��parameters���Map����ɽ���.
		 * 
		 */

		//�ж��Ƿ��в���
		if(uri.indexOf("?") == -1) {
			requestURI = uri;
		}else {
			//����"?"���uri
			String[] data = uri.split("\\?");
			requestURI = data[0];
			//�ж��Ƿ���ʵ���в�������
			if(data.length > 1) {
				queryString = data[1];
				//��һ�����ÿһ������
				data = queryString.split("&");
				//����ÿһ���������ֲ����������ֵ
				for (String para : data) {
					//����"="���
					String[] arr = para.split("=");
					if(arr.length > 1) {
						parameters.put(arr[0], arr[1]);
					}else {
						parameters.put(arr[0], null);
					}
				}
			}
		}
		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);

		System.out.println("HttpRequest:����URI���!");
	}



	/**
	 * �����ڶ���:������Ϣͷ
	 */
	private void parseHeaders() {
		System.out.println("HttpRequest��ʼ������Ϣͷ...");
		try {
			while(true) {
				String line = readLine();
				/*
				 * ���readLine��������ֵΪ���ַ���,˵��
				 * ������ȡ����CRLF,��ô�ͱ�ʾ������Ϣͷ
				 * ����ȡ�����.��ʱӦ��ֹͣѭ���ͽ�����Ϣͷ�Ĺ���.
				 */
				//������ȡ����CRLF
				if("".equals(line)) {
					break;
				}
				String[] data = line.split(":\\s");
				headers.put(data[0], data[1]);
				System.out.println("��Ϣͷ:"+line);
			}
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}


		System.out.println("HttpRequest��Ϣͷ�������!");
	}
	/**
	 * ����������:������Ϣ����
	 */
	private void parseContent() {
		System.out.println("HttpRequest:��ʼ������Ϣ����...");

		System.out.println("HttpRequest:��Ϣ���Ľ������!");
	}

	/**
	 * ͨ����������ȡ�ͻ��˷��͹�����һ���ַ���(��CRLF��β��).
	 * ���ص��ַ����в���������CRLF
	 * @return
	 * @throws IOException 
	 */
	private String readLine() throws IOException {
		StringBuilder builder = new StringBuilder();
		int d = -1;
		//c1��ʾ�ϴζ�ȡ�����ַ�,c2��ʾ���ζ�ȡ�����ַ�
		char c1 = 'a',c2 = 'a';
		while((d = in.read()) != -1) {
			c2 = (char)d;
			if(c1 == 13 && c2 == 10) {
				break;
			}
			builder.append(c2);
			c1 = c2;
		}
		return builder.toString().trim();
	}

	public String getMethod() {
		return method;
	}
	public String getUri() {
		return uri;
	}
	public String getProtocol() {
		return protocol;
	}
	/**
	 * ���ݸ�������Ϣͷ�����ֻ�ȡ��Ӧ��Ϣͷ��ֵ
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public String getRequestURI() {
		return requestURI;
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * ���ݸ����Ĳ�������ȡ��Ӧ�Ĳ���ֵ
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	
	
}
