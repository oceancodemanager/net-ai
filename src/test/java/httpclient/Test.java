package httpclient;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Test {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 创建一个GET对象
		// http://www.sogou.com
		HttpGet get = new HttpGet("https://www.baidu.com/s?ie=UTF-8&wd=%E7%9A%87%E5%AE%B6");
		// 执行请求
		CloseableHttpResponse response = httpClient.execute(get);
		// 取响应的结果
		String body = EntityUtils.toString(response.getEntity());
		System.out.println(body);
	}
}
