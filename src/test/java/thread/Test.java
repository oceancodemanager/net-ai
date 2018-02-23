package thread;

public class Test {

	public static void main(String[] args) {
		for (int i = 0; i <= 100; i++) {
			ThreadA threadA = new ThreadA("ThreadA_ocean");
			Thread t = new Thread(threadA);
			t.start();
		}
		// 如果两个线程同步对象（传入的字符串）都一样，那么只能有一个线程能执行，
		// 所以证实线程加锁是加在对象上的，每个对象只有一把锁，一个线程占用了没有释放，其他线程就不会进入到方法中
		for (int i = 0; i <= 5; i++) {
			// ThreadB threadB = new ThreadB("ThreadB_ocean");
			ThreadB threadB = new ThreadB("ThreadB_ocean");
			Thread t = new Thread(threadB);
			t.start();
		}
	}
}
