package string;

public class StringTest {
	public static void main(String[] args) {
		String aa = "aa";
		String bb = "bb";
		System.out.println((aa + bb).intern() == "aabb");

		String s1 = "Good";
		s1 = s1 + "morning";

		String s2 = "Goodmorning";
		System.out.println(s1 == s2);
		System.out.println(s1.intern() == s2.intern());
	}

	public static void main1(String[] args) {
		String a = (StringTest.class + "_coean").intern();
		String b = (StringTest.class + "_coean").intern();
		System.out.println(a.hashCode());
		System.out.println(b.hashCode());
		System.out.println(a == b);
	}
}
