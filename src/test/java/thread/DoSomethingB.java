package thread;

public class DoSomethingB {
	private static DoSomethingB doSomethingB = new DoSomethingB();

	private DoSomethingB() {
	}

	static final DoSomethingB getInstance() {
		return doSomethingB;
	}

	static int i = 0;

	void doSomething(String corpCode) {
		System.out.println("进入BBBBBB");
		synchronized (corpCode) {
			System.out.println("执行BBBBBB" + i++);
			try {
				Thread.sleep(20000000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
