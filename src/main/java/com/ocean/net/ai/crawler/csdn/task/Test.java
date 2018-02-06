package com.ocean.net.ai.crawler.csdn.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Test {
	public static void main(String[] args) {
		// System.out.println("11111111111111");
		// try {
		// Thread.sleep(10000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println("22222222222222");
		// System.out.println(Base64.getEncoder().encodeToString("网页爬虫
		// 数据库".getBytes()));
		try {
			System.out.println(URLEncoder.encode("网页爬虫 数据库", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
