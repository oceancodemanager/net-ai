package thread;

public class DoSomethingB {
	private static DoSomethingB doSomethingB = new DoSomethingB();

	private DoSomethingB() {
	}

	static final DoSomethingB getInstance() {
		return doSomethingB;
	}

	void doSomething(String corpCode) {
		System.out.println("进入BBBBBB");
		synchronized (corpCode) {
			System.out.println("执行BBBBBB");
		}

	}
}
