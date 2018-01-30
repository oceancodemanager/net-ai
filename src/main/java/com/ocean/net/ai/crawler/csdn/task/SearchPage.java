package com.ocean.net.ai.crawler.csdn.task;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchPage {
	private void search(String keyword) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("http://so.csdn.net/so/search/s.do?p=2&q=" + keyword + "&t=&domain=&o=&s=&u=&l=&f=").build();
		Response response;
		try {
			response = client.newCall(request).execute();
			System.out.println(response.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
