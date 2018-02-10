package com.ocean.net.ai.crawler.csdn.task;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchPage {
	private void searchOkhttp(String keyword) throws Exception {

		// http://so.csdn.net/so/search/s.do?q=%E7%BD%91%E9%A1%B5%E7%88%AC%E8%99%AB+%E6%95%B0%E6%8D%AE%E5%BA%93&t=&o=&s=&l=
		// http://so.csdn.net/so/search/s.do?q=%E7%BD%91%E9%A1%B5%E7%88%AC%E8%99%AB%20%E6%95%B0%E6%8D%AE%E5%BA%93&t=
		// 网页爬虫 数据库

		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("http://so.csdn.net/so/search/s.do?q=" + URLEncoder.encode(keyword, "UTF-8") + "&t=").build();
		Response response;
		try {
			response = client.newCall(request).execute();
			System.out.println(response.body().string());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void searchHtmlUnit() {

	}
}
