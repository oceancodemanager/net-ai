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

	/**
	 * 
	 * (?!pattern) <br>
	 * 执行反向预测先行搜索的子表达式，该表达式匹配不处于匹配 pattern 的字符串的起始点的搜索字符串。它是一个
	 * 非捕获匹配，即不能捕获供以后使用的匹配。例如，'Windows (?!95|98|NT|2000)' 匹配"Windows 3.1"中的
	 * "Windows"，但不匹配"Windows 2000"中的"Windows"。预测先行不占用字符，即发生匹配后，下一匹配的搜索
	 * 紧随上一匹配之后，而不是在组成预测先行的字符后。
	 */
	static void test5() {
		// String MOBILE_AGENT = "iPod|Android|iPhone|Windows Phone|MQQBrowser";
		// Pattern MOBILE_AGENT_PATTERN = Pattern.compile(MOBILE_AGENT,
		// Pattern.CASE_INSENSITIVE);
		// String agent = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 10
		// Build/MOB31T) AppleWebKit/537.36 (KHTML, like Gecko)
		// Chrome/65.0.3325.181 Safari/537.36";

		Pattern p = Pattern.compile("Windows (?!95|98|NT|2000)");
		Matcher m = p.matcher("Windows 2000"); // 获取 matcher 对象
		while (m.find()) {
			System.out.println("start(): " + m.start());
			System.out.println("end(): " + m.end());
		}
	}

	static void test6() {
		String MOBILE_AGENT = "(iPod|Android|iPhone|Windows Phone|MQQBrowser).*(MOBILE)";
		Pattern p = Pattern.compile(MOBILE_AGENT, Pattern.CASE_INSENSITIVE);
		// String andriodPad = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 10
		// Build/MOB31T) AppleWebKit/537.36 mobile (KHTML, like
		// Gecko)Chrome/65.0.3325.181 Safari/537.36";
		String andriodPad = "IPOD  MOBILE";
		Matcher m = p.matcher(andriodPad); // 获取 matcher 对象
		System.out.println("result:" + m.find());
		// while (m.find()) {
		// System.out.println("start(): " + m.start());
		// System.out.println("end(): " + m.end());
		// }

	}

	public static void main(String[] args) {
		// String s = "ssssss";
		// String a = s.substring(0, 0);
		// System.out.println("a：" + a);
		test6();
	}
}
