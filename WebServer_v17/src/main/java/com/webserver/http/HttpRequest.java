package com.webserver.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 请求对象
 * 该类的每一个实例用于表示客户端发送过来的一个具体的
 * http请求的内容.
 * 每个请求由三部分构成:请求行,消息头,消息正文.
 * @author uid
 *
 */

public class HttpRequest {
	/*
	 * 请求行相关信息
	 */
	//请求方式
	private String method;
	//请求资源的抽象路径
	private String uri;
	//请求使用的协议版本
	private String protocol;

	//用来保存uri中的请求部分("?"左侧的内容,如果uri没有"?"那么与uri值一样)
	private String requestURI;
	//用来保存uri中的参数部分("?"右侧内容)
	private String queryString;;

	//用来保存客户端提交上来的数据
	private Map<String,String> parameters = new HashMap<>();

	/*
	 * 消息头相关信息
	 */
	private Map<String,String> headers = new HashMap<>();

	/*
	 * 消息正文相关信息
	 */

	/*
	 * 和连接先关的属性
	 */
	private Socket socket;
	//通过socket获取的输入流,用于读取客户端发送的消息
	private InputStream in;


	/*
	 * 构造方法,用来初始化请求对象
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		System.out.println("HttpRequest:开始解析请求...");
		try {
			this.socket = socket;
			this.in = socket.getInputStream();

			/*
			 * 解析一个请求分为三步:
			 * 1:解析请求行
			 * 2:解析消息头
			 * 3:解析消息正文
			 */
			parseRequestLine();
			parseHeaders();
			parseContent();
		}catch(EmptyRequestException e){
			//如果解析请求行出现了空请求,将异常抛出给clientHandler
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("HttpRequest:解析请求完毕!");
	}
	/**
	 * 解析过程中的第一步:解析请求行
	 * @throws EmptyRequestException 
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:开始解析请求行...");

		try {
			//读取客户端发送过来的第一行字符串(请求行内容)
			String line = readLine();

			/*
			 * 如果返回的line是一个空字符串,说明此次请求是
			 * 一个空请求,应当主动抛出一个空请求异常
			 */
			if("".equals(line)) {
				throw new EmptyRequestException();
			}


			System.out.println(line);
			/*
			 * 将请求行按照空格拆分为三部分,并分别赋值到
			 * 对应的三个属性:method,uri,protocol上
			 * 
			 * 正则表达式中空白字符是:"\s"
			 */
			String[] data = line.split("\\s");
			method = data[0];
			uri = data[1];

			//获取到请求行中的抽象路径部分后进一步解析
			parseURI();

			protocol = data[2];
			System.out.println("method:"+method);
			System.out.println("uri:"+uri);
			System.out.println("protocol:"+protocol);

		}catch(EmptyRequestException e) {
			//单独捕获空请求异常,出现时直接抛出给构造方法
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("HttpRequest:请求行解析完毕!");
	}

	/**
	 * 进一步解析uri
	 * 由于客户端form表单使用ge方式提交时,会将所有
	 * 表单信息拼接在uri中("?"分隔请求部分和参数部分)
	 * 因此uri在这种情况下是担忧参数,不再是纯粹的请求
	 * 所以我们要做进一步解析拆分.
	 */
	private void parseURI() {
		System.out.println("HttpRequest:进一步解析URI...");
		/*
		 * uri会有两种情况:含有参数和不含有参数
		 * 所以首先我们要判断uri是否含有参数,而包含参数
		 * 的标志就是uri是否有"?",有就说明是含有参数的.
		 * 
		 * 如果不含有参数,那么久直接将uri的值赋给requestURI
		 * 即可,queryString和parameters就无需赋值了.
		 * 
		 * 如果有参数,则需要以下操作:
		 * 1:首先按照"?"将uri拆分为两部分(请求和参数)
		 *   然后将"?"左侧的请求部分赋给requestURI
		 *   "?"右侧的参数部分赋值给queryString
		 *   
		 * 2:在将queryString(参数部分)进一步拆分
		 *   首先将queryString按照"&"拆分出若干个参数
		 *   每个参数再按照"="拆分为参数名和参数值,
		 *   然后将参数名作为key,参数值作为value,保存
		 *   到parameters这个Map中完成解析.
		 * 
		 */

		//判断是否有参数
		if(uri.indexOf("?") == -1) {
			requestURI = uri;
		}else {
			//按照"?"拆分uri
			String[] data = uri.split("\\?");
			requestURI = data[0];
			//判断是否真实含有参数部分
			if(data.length > 1) {
				queryString = data[1];
				//进一步拆分每一个参数
				data = queryString.split("&");
				//遍历每一组参数并拆分参数名与参数值
				for (String para : data) {
					//按照"="拆分
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

		System.out.println("HttpRequest:解析URI完毕!");
	}



	/**
	 * 解析第二步:解析消息头
	 */
	private void parseHeaders() {
		System.out.println("HttpRequest开始解析消息头...");
		try {
			while(true) {
				String line = readLine();
				/*
				 * 如果readLine方法返回值为空字符串,说明
				 * 单独读取到了CRLF,那么就表示所有消息头
				 * 均读取完毕了.此时应当停止循环和解析消息头的工作.
				 */
				//单独读取到了CRLF
				if("".equals(line)) {
					break;
				}
				String[] data = line.split(":\\s");
				headers.put(data[0], data[1]);
				System.out.println("消息头:"+line);
			}
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}


		System.out.println("HttpRequest消息头解析完毕!");
	}
	/**
	 * 解析第三步:解析消息正文
	 */
	private void parseContent() {
		System.out.println("HttpRequest:开始解析消息正文...");

		System.out.println("HttpRequest:消息正文解析完毕!");
	}

	/**
	 * 通过输入流读取客户端发送过来的一行字符串(以CRLF结尾的).
	 * 返回的字符串中不含有左后的CRLF
	 * @return
	 * @throws IOException 
	 */
	private String readLine() throws IOException {
		StringBuilder builder = new StringBuilder();
		int d = -1;
		//c1表示上次读取到的字符,c2表示本次读取到的字符
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
	 * 根据给定的消息头的名字获取对应消息头的值
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
	 * 根据给定的参数名获取对应的参数值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	
	
}
