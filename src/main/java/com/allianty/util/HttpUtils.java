package com.allianty.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HttpUtils {
	static Logger logger = LoggerFactory.getLogger("ocr_logger");

	
	public static String httpRequest(String requestUrl, String requestMethod, String outputStr, String type) {
		StringBuffer buffer = null;
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(requestMethod);
			conn.connect();
			// 往服务器端写内容 也就是发起http请求需要带的参数
			if (null != outputStr) {
				OutputStream os = conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}

			// 读取服务器端返回的内容
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			buffer = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
	
	public static String doPostJson(String url, JSONObject json, Map<String,String> headerMap) {
		String result = "";
		CloseableHttpClient client = HttpClientBuilder.create().build(); // 1.创建httpclient对象
		HttpPost post = new HttpPost(url); // 2.通过url创建post方法
		try {
			StringEntity entity = new StringEntity(json.toString()); // 3.将传入的json封装成实体，并压入post方法
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");// 发送json数据需要设置contentType
			post.setEntity(entity);
			if(headerMap!=null){
				for(Map.Entry<String, String> entry:headerMap.entrySet()){
					post.setHeader(entry.getKey(),entry.getValue());//将头部对象信息设置到请求头部中
				}
			}
			logger.info(entity.toString());
			CloseableHttpResponse response = client.execute(post); // 4.执行post方法，返回HttpResponse的对象
			logger.debug(response.toString());
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8"); // 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
			}
			
			logger.debug(String.valueOf(response.getStatusLine().getStatusCode()));

			try {				
				HttpEntity e = response.getEntity(); // 6.关闭资源
				if (e != null) {
					InputStream instream = e.getContent();
					instream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static String doPost(String url,  String key, String secret, String trueName,String idenNo,String img,String typeId, String format) {
			String result = "";
		try {

			CloseableHttpClient client = HttpClients.createDefault(); // 1.创建httpclient对象
			HttpPost post = new HttpPost(url); // 2.通过url创建post方法

			if ("json".equalsIgnoreCase(format)) {
				post.setHeader("accept", "application/json");
			} else if ("xml".equalsIgnoreCase(format)
					|| "".equalsIgnoreCase(format)) {
				post.setHeader("accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			}


			MultipartEntityBuilder builder = MultipartEntityBuilder.create(); // 实例化实体构造器
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE); // 设置浏览器兼容模式

			builder.addPart("key",new StringBody(key, ContentType.create("text/plain",Consts.UTF_8))); // 添加"key"字段及其值
			builder.addPart("secret",new StringBody(secret, ContentType.create("text/plain",Consts.UTF_8))); // 添加"secret"字段及其值
			builder.addPart("trueName",new StringBody(trueName, ContentType.create("text/plain",Consts.UTF_8))); // 添加"secret"字段及其值
			builder.addPart("idenNo",new StringBody(idenNo, ContentType.create("text/plain",Consts.UTF_8))); // 添加"secret"字段及其值
			builder.addPart("img",new StringBody(img, ContentType.create("text/plain",Consts.UTF_8))); // 添加"img"字段及其值
			builder.addPart("typeId",new StringBody(typeId, ContentType.create("text/plain",Consts.UTF_8))); // 添加"typeId"字段及其值
			builder.addPart("format",new StringBody(format, ContentType.create("text/plain",Consts.UTF_8))); // 添加"format"字段及其值

			HttpEntity reqEntity = builder
					.setCharset(CharsetUtils.get("UTF-8")).build(); // 设置请求的编码格式，并构造实体

			post.setEntity(reqEntity);
			// **************************************</向post方法中封装实体>************************************

			CloseableHttpResponse response = client.execute(post); // 4.执行post方法，返回HttpResponse的对象
			if (response.getStatusLine().getStatusCode() == 200) { // 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				System.out.println("服务器返回异常");
			}

			try {
				HttpEntity e = response.getEntity(); // 6.关闭资源
				if (e != null) {
					InputStream instream = e.getContent();
					instream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				response.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return result; // 7.返回识别结果
	}

}
