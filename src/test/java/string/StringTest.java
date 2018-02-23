package string;

public class StringTest {
	public static void main(String[] args) {
		String a = (StringTest.class + "_coean").intern();
		String b = (StringTest.class + "_coean").intern();
		System.out.println(a.hashCode());
		System.out.println(b.hashCode());
		System.out.println(a == b);
	}
}
