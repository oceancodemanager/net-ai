package com.ocean.net.test.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {

	static void test1() {

		String str = "11<script type=\"text/javascript\">alert(1);<\\/script>22<script type=\"text/javascript\">alert(1);<\\/script>";
		Pattern p = Pattern.compile("<script.+>");
		Matcher m = p.matcher(str); // 获取 matcher 对象
		// str = m.replaceAll("");
		str = m.replaceFirst("");
		System.out.println(str);
		// while (m.find()) {
		// System.out.println("start(): " + m.start());
		// System.out.println("end(): " + m.end());
		// }
	}

	/**
	 * <出现一次
	 */
	static void test3() {

		String str = "11<script type=\"text/javascript\">alert(1);<\\/script>22<script type=\"text/javascript\">alert(1);<\\/script>";
		Pattern p = Pattern.compile("<+");
		Matcher m = p.matcher(str); // 获取 matcher 对象
		while (m.find()) {
			System.out.println("start(): " + m.start());
			System.out.println("end(): " + m.end());
			System.out.println("=========================");
		}
	}

	static void test2() {

		String str = "11<script type=\"text/javascript\">alert(1);<\\/script>22<script type=\"text/javascript\">alert(1);<\\/script>";
		Pattern p = Pattern.compile("<script.+>");
		Matcher m = p.matcher(str); // 获取 matcher 对象
		while (m.find()) {
			System.out.println("start(): " + m.start());
			System.out.println("end(): " + m.end());
		}
	}

	static void test4() {

		String str = "11<script type=\"text/javascript\">alert(1);<\\/script>22<script type=\"text/javascript\">alert(1);<\\/script>";
		Pattern p = Pattern.compile("<script.+>+?");
		Matcher m = p.matcher(str); // 获取 matcher 对象
		while (m.find()) {
			System.out.println("start(): " + m.start());
			System.out.println("end(): " + m.end());
		}
	}

	static void test5() {
		String MOBILE_AGENT = "iPod|Android|iPhone|Windows Phone|MQQBrowser";

		Pattern MOBILE_AGENT_PATTERN = Pattern.compile(MOBILE_AGENT, Pattern.CASE_INSENSITIVE);
	}

	public static void main(String[] args) {
		String s = "ssssss";
		String a = s.substring(0, 0);
		System.out.println("a：" + a);
	}
}
