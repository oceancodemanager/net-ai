package thread;

public class ThreadA implements Runnable {
	String corpCode;

	public ThreadA(String corpCode) {
		this.corpCode = corpCode;
	}

	@Override
	public void run() {
		DoSomethingA.getInstance().doSomething(corpCode);
	}

}
