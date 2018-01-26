package okhttp;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test3 {
	public static void main(String[] args) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("http://so.csdn.net/so/search/s.do?p=2&q=okhttp&t=&domain=&o=&s=&u=&l=&f=").build();
		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
	}
}
