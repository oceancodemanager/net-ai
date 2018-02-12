package thread;

public class Test {

	public static void main(String[] args) {
		for (int i = 0; i <= 100; i++) {
			ThreadA threadA = new ThreadA("ThreadA_ocean");
			Thread t = new Thread(threadA);
			t.start();
		}
		for (int i = 0; i <= 2; i++) {
			ThreadB threadB = new ThreadB("ThreadB_ocean");
			Thread t = new Thread(threadB);
			t.start();
		}
	}
}
