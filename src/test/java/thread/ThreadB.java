package thread;

public class ThreadB implements Runnable {
	String corpCode;

	public ThreadB(String corpCode) {
		this.corpCode = corpCode;
	}

	@Override
	public void run() {
		DoSomethingB.getInstance().doSomething(corpCode);
	}

}
