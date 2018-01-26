package okhttp;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Testjp {
	public static void main(String[] args) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url("http://blog.csdn.net/qq_40225248/article/details/78210959")
				.build();
		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
	}
}
