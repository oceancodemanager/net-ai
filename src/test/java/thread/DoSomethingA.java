package thread;

public class DoSomethingA {
	private static DoSomethingA doSomethingA = new DoSomethingA();

	private DoSomethingA() {
	}

	static final DoSomethingA getInstance() {
		return doSomethingA;
	}

	static int i = 0;

	void doSomething(String corpCode) {
		System.out.println("进入AAAAAA");
		synchronized (corpCode) {
			System.out.println("执行AAAAAA" + i++);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
