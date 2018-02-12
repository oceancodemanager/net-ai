package thread;

public class Test {

	public static void main(String[] args) {
		for (int i = 0; i <= 10; i++) {
			ThreadA threadA = new ThreadA("ocean");
			Thread t = new Thread(threadA);
			t.start();
		}
		for (int i = 0; i <= 10; i++) {
			ThreadB threadB = new ThreadB("ocean");
			Thread t = new Thread(threadB);
			t.start();
		}
	}
}
