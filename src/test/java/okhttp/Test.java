package okhttp;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test {

	OkHttpClient client = new OkHttpClient();

	String run(String url) throws IOException {
		Request request = new Request.Builder().url(url)
				.header("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3322.4 Safari/537.36")
				.addHeader("Cookie",
						"BAIDUID=D7EC0E69CAEB84FC129B777D3C30E275:FG=1; PSTM=1515129157; BD_UPN=12314753; BIDUPSID=D7EC0E69CAEB84FC129B777D3C30E275; ispeed_lsm=2; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; MCITY=-131%3A; H_PS_PSSID=1465_21090_20929; H_PS_645EC=a0cbN6Hgid9tID11caoBDonhM2chpHSdcZAtASOG6hCIUowiXFKJBAoGv%2F4; BDSVRTM=119")
				.addHeader("Upgrade-Insecure-Requests:", "1").build();

		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}

	public static void main(String[] args) throws IOException {
		Test example = new Test();
		// https://raw.github.com/square/okhttp/master/README.md
		String response = example.run(
				"https://www.baidu.com/s?wd=%E7%9A%87%E5%AE%B6&pn=50&oq=%E7%9A%87%E5%AE%B6&ie=utf-8&usm=3&rsv_pq=d04d1cf10003ab15&rsv_t=4f3dVMkL34sQ9yasr3gbtbontydMhOBMbiRkbtdNyZSUq53nqGomWotzBSA");
		System.out.println(response);
	}
}
